package ch.hesge.cragsi.loader;

import java.io.IOException;
import java.util.ArrayList;
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

	private String firstSemesterJournalId;
	private String secondSemesterjournalId;
	private String firstSemesterPeriodId;
	private String secondSemesterPeriodId;

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

	/**
	 * Initialize the loader.
	 * 
	 * @throws IOException
	 */
	public void init() throws IOException {

		accountingSequence = 1;
		accountings = new ArrayList<>();

		// Retrieve properties from config file
		firstSemesterJournalId  = CragsiLogic.getProperty("journalId_S1");
		firstSemesterPeriodId   = CragsiLogic.getProperty("periodId_S1");
		secondSemesterjournalId = CragsiLogic.getProperty("journalId_S2");
		secondSemesterPeriodId  = CragsiLogic.getProperty("periodId_S2");

		// Load common files
		accounts = AccountDao.findAll();
		projects = ProjectDao.findAll();
		priceMap = PriceDao.findMapByLibelle();

		// Retrieve the projects account
		String allProjectsCode = CragsiLogic.getProperty("projectsAccount");
		allProjectsAccount = AccountDao.findByCode(allProjectsCode, accounts);
		if (allProjectsAccount == null) {
			throw new IllegalArgumentException("==> missing projects account with code '" + allProjectsCode + "' !");
		}

		// Retrieve the socle account
		String socleCode = CragsiLogic.getProperty("socleAccount");
		socleAccount = AccountDao.findByCode(socleCode, accounts);
		if (socleAccount == null) {
			throw new IllegalArgumentException("==> missing socle account with code '" + socleCode + "' !");
		}

		// Retrieve the salaries account
		String salaryCode = CragsiLogic.getProperty("salaryAccount");
		salaryAccount = AccountDao.findByCode(salaryCode, accounts);
		if (salaryAccount == null) {
			throw new IllegalArgumentException("==> missing salaries account with code '" + salaryCode + "' !");
		}
	}

	/**
	 * Start loading all files and genered all accountings.
	 * @throws IOException
	 */
	public void start() throws IOException {

		// Generate FDC accountings
		generateFDCAccountings();

		// Generate AGF accountings
		generateAGFAccountings();

		// Generate funding accountings
		generateFundingAccountings();

		// Generate partner accountings
		generatePartnerAccountings();

		// Finally save accountings
		AccountingDao.saveAll(accountings);
	}

	/**
	 * Generate activities accountings
	 */
	private void generateFDCAccountings() throws IOException {

		System.out.println("Generating FDC accounting...");

		for (Activity activity : ActivityDao.findAll()) {

			Price activityPrice = CragsiLogic.getActivityPrice(activity, priceMap);
			
			if (activity != null) {
				
				// Retrieve the collaborator account
				Account collaboratorAccount = CragsiLogic.getCollaboratorAccount(activity, accounts);

				// Retrieve the project account
				Account projectAccount = CragsiLogic.getProjectAccount(activity, projects, accounts);

				if (collaboratorAccount != null && projectAccount != null) {
					
					// Retrieve accounting dates
					Date firstSemesterAccountingDate = CragsiLogic.getFirstSemesterAccountingDate(activity);
					Date secondSemesterAccountingDate = CragsiLogic.getSecondSemesterAccountingDate(activity);					

					// Calculate activity costs
					activity.setCostS1(activity.getTotalS1() * activityPrice.getPrice());
					activity.setCostS2(activity.getTotalS2() * activityPrice.getPrice());

					// Adjust activityLabel with project number
					String activityLabel = activity.getDetail();
					if (!StringUtils.isEmtpy(activity.getProjectNumber()) && !activityLabel.contains(activity.getProjectNumber())) {
						activityLabel = activity.getProjectNumber() + "-" + activityLabel;
					}

					// Calculate accounting labels
					String accountingLabel = (collaboratorAccount.getCode() + "-" + collaboratorAccount.getName() + "-" + activityLabel).replace("-", " - ");
					String academicPeriodYearS1 = UserSettings.getInstance().getProperty("academicPeriod_S1");
					String academicPeriodYearS2 = UserSettings.getInstance().getProperty("academicPeriod_S2");
					String accountingLabelS1 = accountingLabel + " (" + academicPeriodYearS1 + ")";
					String accountingLabelS2 = accountingLabel + " (" + academicPeriodYearS2 + ")";
					
					// ==> Generate accounting for first semester, if accounting date is within range
					Date firstSemesterStartDate = DateFactory.getFirstSemesterStartDate();
					Date firstSemesterEndDate = DateFactory.getFirstSemesterEndDate();
					if (activity.getCostS1() != 0 && !firstSemesterAccountingDate.before(firstSemesterStartDate) && !firstSemesterAccountingDate.after(firstSemesterEndDate)) {

						// Collaborator accountings
						accountings.add(AccountingFactory.createDebitEntry(accountingSequence, firstSemesterAccountingDate, firstSemesterJournalId, firstSemesterPeriodId, collaboratorAccount, accountingLabelS1, activity.getCostS1()));
						accountings.add(AccountingFactory.createCreditEntry(accountingSequence, firstSemesterAccountingDate, firstSemesterJournalId, firstSemesterPeriodId, salaryAccount, accountingLabelS1, activity.getCostS1()));
						accountingSequence++;

						// Check if activity is associated to a project
						if (projectAccount != null) {
							accountings.add(AccountingFactory.createDebitEntry(accountingSequence, firstSemesterAccountingDate, firstSemesterJournalId, firstSemesterPeriodId, projectAccount, accountingLabelS1, activity.getCostS1()));
							accountings.add(AccountingFactory.createCreditEntry(accountingSequence, firstSemesterAccountingDate, firstSemesterJournalId, firstSemesterPeriodId, allProjectsAccount, accountingLabelS1, activity.getCostS1()));
							accountingSequence++;
						}
					}

					// ==> Generate accounting for second semester (S2)
					Date secondSemesterStartDate = DateFactory.getFirstSemesterStartDate();
					Date secondSemesterEndDate = DateFactory.getFirstSemesterEndDate();
					if (activity.getCostS2() != 0 && !secondSemesterAccountingDate.before(secondSemesterStartDate) && !secondSemesterAccountingDate.after(secondSemesterEndDate)) {

						// Collaborator accountings
						accountings.add(AccountingFactory.createDebitEntry(accountingSequence, secondSemesterAccountingDate, secondSemesterjournalId, secondSemesterPeriodId, collaboratorAccount, accountingLabelS2, activity.getCostS2()));
						accountings.add(AccountingFactory.createCreditEntry(accountingSequence, secondSemesterAccountingDate, secondSemesterjournalId, secondSemesterPeriodId, salaryAccount, accountingLabelS2, activity.getCostS2()));
						accountingSequence++;

						// Check if activity is associated to a project
						if (projectAccount != null) {
							accountings.add(AccountingFactory.createDebitEntry(accountingSequence, secondSemesterAccountingDate, secondSemesterjournalId, secondSemesterPeriodId, projectAccount, accountingLabelS2, activity.getCostS2()));
							accountings.add(AccountingFactory.createCreditEntry(accountingSequence, secondSemesterAccountingDate, secondSemesterjournalId, secondSemesterPeriodId, allProjectsAccount, accountingLabelS2, activity.getCostS2()));
							accountingSequence++;
						}
					}
				}
			}
		}

		System.out.println("FDC generation done.");

	}

	/**
	 * Generate financial accountings.
	 * 
	 * @throws IOException
	 */
	private void generateAGFAccountings() throws IOException {

		System.out.println("Generating AGF accounting...");

		for (AGFLine afgLine : AGFLineDao.findAll()) {

			Account projectAccount = CragsiLogic.getProjectAccount(afgLine, accounts);

			if (projectAccount != null) {
				accountings.add(AccountingFactory.createDebitEntry(accountingSequence, afgLine.getDate(), firstSemesterJournalId, firstSemesterPeriodId, allProjectsAccount, afgLine.getName(), afgLine.getAmount()));
				accountings.add(AccountingFactory.createCreditEntry(accountingSequence, afgLine.getDate(), firstSemesterJournalId, firstSemesterPeriodId, projectAccount, afgLine.getName(), afgLine.getAmount()));
				accountingSequence++;
			}
		}

		System.out.println("AGF generation done.");

	}

	/**
	 * Generation funding accounting.
	 * 
	 * @throws IOException
	 */
	private void generateFundingAccountings() throws IOException {

		System.out.println("Generating FINANCIAL accounting...");

		for (Funding funding : FundingDao.findAll()) {

			Account projectAccount = CragsiLogic.getProjectAccount(funding, accounts);

			if (projectAccount != null) {
				accountings.add(AccountingFactory.createDebitEntry(accountingSequence, funding.getDate(), firstSemesterJournalId, firstSemesterPeriodId, allProjectsAccount, funding.getName(), funding.getAmount()));
				accountings.add(AccountingFactory.createCreditEntry(accountingSequence, funding.getDate(), firstSemesterJournalId, firstSemesterPeriodId, projectAccount, funding.getName(), funding.getAmount()));
				accountingSequence++;
			}
		}

		System.out.println("FINANCIAL generation done.");
	}

	/**
	 * Generating partner accountings.
	 * 
	 * @throws IOException
	 */
	private void generatePartnerAccountings() throws IOException {

		System.out.println("Generating SUBCONTRACTOR accounting...");

		for (Partner partner : PartnerDao.findAll()) {

			Account projectAccount = CragsiLogic.getProjectAccount(partner, accounts);

			if (projectAccount != null) {
				accountings.add(AccountingFactory.createDebitEntry(accountingSequence, partner.getDate(), firstSemesterJournalId, firstSemesterPeriodId, allProjectsAccount, partner.getName(), partner.getAmount()));
				accountings.add(AccountingFactory.createCreditEntry(accountingSequence, partner.getDate(), firstSemesterJournalId, firstSemesterPeriodId, projectAccount, partner.getName(), partner.getAmount()));
				accountingSequence++;
			}
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
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		CragsiLoader loader = new CragsiLoader();
		loader.init();
		loader.start();
	}

}
