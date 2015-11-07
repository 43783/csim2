package ch.hesge.cragsi.loader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ch.hesge.cragsi.dao.AGFLineDao;
import ch.hesge.cragsi.dao.AccountDao;
import ch.hesge.cragsi.dao.AccountingDao;
import ch.hesge.cragsi.dao.ActivityDao;
import ch.hesge.cragsi.dao.FundingDao;
import ch.hesge.cragsi.dao.PartnerDao;
import ch.hesge.cragsi.dao.PriceDao;
import ch.hesge.cragsi.dao.ProjectDao;
import ch.hesge.cragsi.model.AGFLine;
import ch.hesge.cragsi.model.Account;
import ch.hesge.cragsi.model.Accounting;
import ch.hesge.cragsi.model.Activity;
import ch.hesge.cragsi.model.Funding;
import ch.hesge.cragsi.model.Partner;
import ch.hesge.cragsi.model.Price;
import ch.hesge.cragsi.model.Project;
import ch.hesge.cragsi.utils.AccountingFactory;
import ch.hesge.cragsi.utils.DateFactory;
import ch.hesge.cragsi.utils.StringUtils;

public class CragsiLoader {

	// Private attributes
	private int accountingSequence;

	private String journalId_S1;
	private String journalId_S2;
	private String periodId_S1;
	private String periodId_S2;

	private String accountSuffix;
	private Account socleAccount;
	private Account salaryAccount;	
	private Account allProjectsAccount;
	
	private List<Account> accounts;
	private List<Project> projects;
	private Map<String, Price> priceMap;
	private List<Accounting> accountings;

	/**
	 * Default constructor
	 */
	public CragsiLoader() {
	}

	public void init() {

		accountings.clear();
		accountingSequence = 1;
		accountings = new ArrayList<>();

		journalId_S1 = UserSettings.getInstance().getProperty("journalId_S1");
		journalId_S2 = UserSettings.getInstance().getProperty("journalId_S2");
		periodId_S1  = UserSettings.getInstance().getProperty("periodId_S1");
		periodId_S2  = UserSettings.getInstance().getProperty("periodId_S2");

		try {

			accounts = AccountDao.findAll();
			projects = ProjectDao.findAll();
			priceMap = PriceDao.findMapByLibelle();

			// Retrieve the all-project-code property
			String allProjectsCode = UserSettings.getInstance().getProperty("projectsAccount");

			if (allProjectsCode == null) {
				throw new IllegalArgumentException("==> missing property 'projectsAccount' in configuration file !");
			}

			// Retrieve the projects account
			allProjectsAccount = AccountDao.findByCode(allProjectsCode, accounts);

			if (allProjectsAccount == null) {
				throw new IllegalArgumentException("==> missing projects account with code '" + allProjectsCode + "' !");
			}

			// Retrieve suffix of all projects in accounting list
			accountSuffix = UserSettings.getInstance().getProperty("projectAccountSuffix");

			if (accountSuffix == null) {
				throw new IllegalArgumentException("==> missing property 'projectAccountSuffix' in configuration file !");
			}

			// Retrieve socle account code
			String socleCode = UserSettings.getInstance().getProperty("socleAccount");

			if (socleCode == null) {
				throw new IllegalArgumentException("==> missing property 'socleAccount' in configuration file !");
			}

			// Retrieve the socle account
			socleAccount = AccountDao.findByCode(socleCode, accounts);

			if (socleAccount == null) {
				throw new IllegalArgumentException("==> missing socle account with code '" + socleCode + "' !");
			}
			
			// Retrieve salaries account code
			String salaryCode = UserSettings.getInstance().getProperty("salaryAccount");

			if (salaryCode == null) {
				throw new IllegalArgumentException("==> missing property 'salaryAccount' in configuration file !");
			}

			// Retrieve the salaries account
			salaryAccount = AccountDao.findByCode(salaryCode, accounts);

			if (salaryAccount == null) {
				throw new IllegalArgumentException("==> missing salaries account with code '" + salaryCode + "' !");
			}
		}
		catch (FileNotFoundException e) {
			System.out.println("CragsiLoader: an unexpected error has occured: " + e.toString());
		}
		catch (IOException e) {
			System.out.println("CragsiLoader: an unexpected error has occured: " + e.toString());
		}

	}

	public void start() {

		try {

			// Generate FDC accountings
			generateFDCAccountings();

			// Generate AGF accountings
			//generateAFGAccountings();

			// Generate funding accountings
			generateFundingAccountings();

			// Generate partner accountings
			generatePartnerAccountings();

			// Finally save accountings
			AccountingDao.saveAll(accountings);

		}
		catch (FileNotFoundException e) {
			System.out.println("CragsiLoader: an unexpected error has occured: " + e.toString());
		}
		catch (IOException e) {
			System.out.println("CragsiLoader: an unexpected error has occured: " + e.toString());
		}
	}

	/**
	 * 
	 */
	private void generateFDCAccountings() throws IOException {

		System.out.println("Generating FDC accounting...");

		for (Activity activity : ActivityDao.findAll()) {

			if (!priceMap.containsKey(activity.getContractType())) {
				System.out.println("==> missing contract '" + activity.getContractType() + "' !");
			}
			else {

				// Retrieve the activity price
				Price activityPrice = priceMap.get(activity.getContractType());

				if (activityPrice == null) {
					System.out.println("==> missing price for code '" + activity.getContractType() + "' !");
					continue;
				}

				// Retrieve the collaborator account
				Account collaboratorAccount = AccountDao.findByName(activity.getLastname(), accounts);

				if (collaboratorAccount == null) {
					System.out.println("==> missing collaborator account for '" + activity.getFirstname() + " " + activity.getLastname() + "' with contract '" + activity.getContractType() + "' !");
					continue;
				}

				// Retrieve FDC project account
				Account projectAccount = null;
				String projectNumber = StringUtils.toNumber(activity.getProjectNumber());
				if (!StringUtils.isEmtpy(projectNumber)) {

					// Retrieve project from its number
					Project project = ProjectDao.findByCode(projectNumber, projects);

					// Check if project exists
					if (project == null) {
						System.out.println("==> missing project with code '" + projectNumber + "' !");
						continue;
					}
					else {

						Date currentDate = Calendar.getInstance().getTime();

						// Check if project is not closed
						if (currentDate.equals(project.getStartDate()) || (currentDate.after(project.getStartDate()) && currentDate.before(project.getEndDate())) || currentDate.equals(project.getEndDate())) {

							String accountSuffix = UserSettings.getInstance().getProperty("projectAccountSuffix");
							projectAccount = AccountDao.findByCode(accountSuffix + projectNumber, accounts);

							// Check if an account for the project exists
							if (projectAccount == null) {
								System.out.println("==> missing project account with code '" + projectNumber + "' !");
								continue;
							}
						}
						else {
							System.out.println("==> project with code '" + projectNumber + "' is already closed !");
							continue;
						}
					}
				}

				// Calculate accounting date for first semester
				Date firstSemesterStartDate = DateFactory.getFirstSemesterStartDate();
				Date firstSemesterEndDate = DateFactory.getFirstSemesterEndDate();
				Date accountingDateS1 = firstSemesterStartDate;
				if (activity.getStartContract().before(firstSemesterStartDate)) {
					System.out.println("==> invalid contract start date for collaborator '" + activity.getLastname() + " !");
					continue;
				}
				else if (activity.getStartContract().after(firstSemesterStartDate)) {
					accountingDateS1 = activity.getStartContract();
				}

				// Calculate accounting date for second semester
				Date secondSemesterStartDate = DateFactory.getSecondSemesterStartDate();
				Date secondSemesterEndDate = DateFactory.getSecondSemesterEndDate();
				Date accountingDateS2 = secondSemesterStartDate;
				if (activity.getEndContract().after(secondSemesterEndDate)) {
					System.out.println("==> invalid contract end date for collaborator '" + activity.getLastname() + " !");
					continue;
				}
				else if (activity.getStartContract().after(secondSemesterStartDate)) {
					accountingDateS2 = activity.getStartContract();
				}

				// Calculate activity costs
				activity.setCostS1(activity.getTotalS1() * activityPrice.getPrice());
				activity.setCostS2(activity.getTotalS2() * activityPrice.getPrice());

				// Adjust activityLabel with project number
				String activityLabel = activity.getDetail();
				if (!StringUtils.isEmtpy(projectNumber) && !activityLabel.contains(projectNumber)) {
					activityLabel = projectNumber + "-" + activityLabel;
				}

				// Calculate accounting labels
				String accountingLabel = (collaboratorAccount.getCode() + "-" + collaboratorAccount.getName() + "-" + activityLabel).replace("-", " - ");
				String academicPeriodYearS1 = UserSettings.getInstance().getProperty("academicPeriod_S1");
				String academicPeriodYearS2 = UserSettings.getInstance().getProperty("academicPeriod_S2");
				String accountingLabelS1 = accountingLabel + " (" + academicPeriodYearS1 + ")";
				String accountingLabelS2 = accountingLabel + " (" + academicPeriodYearS2 + ")";

				// ==> Generate accounting for first semester, if accounting date is within range
				if (activity.getCostS1() != 0 && !accountingDateS1.before(firstSemesterStartDate) && !accountingDateS1.after(firstSemesterEndDate)) {

					// Collaborator accountings
					accountings.add(AccountingFactory.createDebitEntry(accountingSequence, accountingDateS1, journalId_S1, periodId_S1, collaboratorAccount, accountingLabelS1, activity.getCostS1()));
					accountings.add(AccountingFactory.createCreditEntry(accountingSequence, accountingDateS1, journalId_S1, periodId_S1, salaryAccount, accountingLabelS1, activity.getCostS1()));
					accountingSequence++;

					// Check if activity is associated to a project
					if (projectAccount != null) {

						// Project accountings
						accountings.add(AccountingFactory.createDebitEntry(accountingSequence, accountingDateS1, journalId_S1, periodId_S1, projectAccount, accountingLabelS1, activity.getCostS1()));
						accountings.add(AccountingFactory.createCreditEntry(accountingSequence, accountingDateS1, journalId_S1, periodId_S1, allProjectsAccount, accountingLabelS1, activity.getCostS1()));
						accountingSequence++;
					}
					else {

						// Socle accountings
						accountings.add(AccountingFactory.createDebitEntry(accountingSequence, accountingDateS1, journalId_S1, periodId_S1, socleAccount, accountingLabelS1, activity.getCostS1()));
						accountings.add(AccountingFactory.createCreditEntry(accountingSequence, accountingDateS1, journalId_S1, periodId_S1, allProjectsAccount, accountingLabelS1, activity.getCostS1()));
						accountingSequence++;
					}
				}

				// ==> Generate accounting for second semester (S2)
				if (activity.getCostS2() != 0 && !accountingDateS2.before(secondSemesterStartDate) && !accountingDateS2.after(secondSemesterEndDate)) {

					// Collaborator accountings
					accountings.add(AccountingFactory.createDebitEntry(accountingSequence, accountingDateS2, journalId_S2, periodId_S2, collaboratorAccount, accountingLabelS2, activity.getCostS2()));
					accountings.add(AccountingFactory.createCreditEntry(accountingSequence, accountingDateS2, journalId_S2, periodId_S2, salaryAccount, accountingLabelS2, activity.getCostS2()));
					accountingSequence++;

					// Check if activity is associated to a project
					if (projectAccount != null) {

						// Project accountings
						accountings.add(AccountingFactory.createDebitEntry(accountingSequence, accountingDateS2, journalId_S2, periodId_S2, projectAccount, accountingLabelS2, activity.getCostS2()));
						accountings.add(AccountingFactory.createCreditEntry(accountingSequence, accountingDateS2, journalId_S2, periodId_S2, allProjectsAccount, accountingLabelS2, activity.getCostS2()));
						accountingSequence++;
					}
					else {

						// Socle accountings
						accountings.add(AccountingFactory.createDebitEntry(accountingSequence, accountingDateS2, journalId_S2, periodId_S2, socleAccount, accountingLabelS2, activity.getCostS2()));
						accountings.add(AccountingFactory.createCreditEntry(accountingSequence, accountingDateS2, journalId_S2, periodId_S2, allProjectsAccount, accountingLabelS2, activity.getCostS2()));
						accountingSequence++;
					}
				}
			}
		}

		System.out.println("FDC generation done.");

	}

	/**
	 * 
	 * @throws IOException
	 */
	private void generateAGFAccountings() throws IOException {

		System.out.println("Generating AGF accounting...");

		for (AGFLine afgLine : AGFLineDao.findAll()) {

			Account projectAccount = AccountDao.findByCode(accountSuffix + afgLine.getProjectNumber(), accounts);

			// Check if an account for the project exists
			if (projectAccount == null) {
				System.out.println("==> missing project account with code '" + afgLine.getProjectNumber() + "' !");
				continue;
			}

			// Generate AGF accountings
			accountings.add(AccountingFactory.createDebitEntry(accountingSequence, afgLine.getDate(), journalId_S1, periodId_S1, allProjectsAccount, afgLine.getName(), afgLine.getAmount()));
			accountings.add(AccountingFactory.createCreditEntry(accountingSequence, afgLine.getDate(), journalId_S1, periodId_S1, projectAccount, afgLine.getName(), afgLine.getAmount()));
			accountingSequence++;
		}

		System.out.println("AGF generation done.");

	}

	/**
	 * 
	 * @throws IOException
	 */
	private void generateFundingAccountings() throws IOException {

		System.out.println("Generating FINANCIAL accounting...");

		for (Funding funding : FundingDao.findAll()) {

			Account projectAccount = AccountDao.findByCode(accountSuffix + funding.getProjectNumber(), accounts);

			// Check if an account for the project exists
			if (projectAccount == null) {
				System.out.println("==> missing project account with code '" + funding.getProjectNumber() + "' !");
				continue;
			}

			// Generate funding accountings
			accountings.add(AccountingFactory.createDebitEntry(accountingSequence, funding.getDate(), journalId_S1, periodId_S1, allProjectsAccount, funding.getName(), funding.getAmount()));
			accountings.add(AccountingFactory.createCreditEntry(accountingSequence, funding.getDate(), journalId_S1, periodId_S1, projectAccount, funding.getName(), funding.getAmount()));
			accountingSequence++;
		}

		System.out.println("FINANCIAL generation done.");
	}

	/**
	 * 
	 * @throws IOException
	 */
	private void generatePartnerAccountings() throws IOException {

		System.out.println("Generating SUBCONTRACTOR accounting...");

		for (Partner partner : PartnerDao.findAll()) {

			Account projectAccount = AccountDao.findByCode(accountSuffix + partner.getProjectNumber(), accounts);

			// Check if an account for the project exists
			if (projectAccount == null) {
				System.out.println("==> missing project account with code '" + partner.getProjectNumber() + "' !");
				continue;
			}

			// generate project accountings
			accountings.add(AccountingFactory.createDebitEntry(accountingSequence, partner.getDate(), journalId_S1, periodId_S1, allProjectsAccount, partner.getName(), partner.getAmount()));
			accountings.add(AccountingFactory.createCreditEntry(accountingSequence, partner.getDate(), journalId_S1, periodId_S1, projectAccount, partner.getName(), partner.getAmount()));
			accountingSequence++;
		}

		System.out.println("SUBCONTRACTOR generation done.");
	}

	/**
	 * 
	 */
	public void dumpActivities(List<Activity> activities) {

		System.out.println("----------------------------------");
		System.out.println(" Activity list:");
		System.out.println("----------------------------------");

		for (Activity activity : activities) {

			System.out.println("  unit:      " + activity.getUnit());
			System.out.println("  lastname:  " + activity.getLastname());
			System.out.println("  firstname: " + activity.getFirstname());
			System.out.println("  contract:  " + activity.getContractType());
			System.out.println("  function:  " + activity.getFunction());
			System.out.println("  student:   " + activity.getStudentCount());
			System.out.println("  hours:     " + activity.getHours());
			System.out.println("  coeff:     " + activity.getCoefficient());
			System.out.println("  weeks:     " + activity.getWeeks());
			System.out.println("  total:     " + activity.getTotal());
			System.out.println("  total S1:  " + activity.getTotalS1());
			System.out.println("  total S2:  " + activity.getTotalS2());
			System.out.println("  cost S1:   " + activity.getCostS1());
			System.out.println("  cost S2:   " + activity.getCostS2());
			System.out.println("  activity:  " + activity.getActivity());
			System.out.println("  pillarGE:  " + activity.getPillarGE());
			System.out.println("  pillarHES: " + activity.getPillarHES());
			System.out.println("  sector:    " + activity.getSector());
			System.out.println("  project:   " + activity.getProjectNumber());

			System.out.println();
		}
	}

	/**
	 * Main entry point
	 * @param args
	 */
	public static void main(String[] args) {
		new CragsiLoader().start();
	}

}
