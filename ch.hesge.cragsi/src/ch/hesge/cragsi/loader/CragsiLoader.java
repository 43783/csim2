package ch.hesge.cragsi.loader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ch.hesge.cragsi.dao.AccountDao;
import ch.hesge.cragsi.dao.AccountingDao;
import ch.hesge.cragsi.dao.ActivityDao;
import ch.hesge.cragsi.dao.PriceDao;
import ch.hesge.cragsi.dao.ProjectDao;
import ch.hesge.cragsi.model.Account;
import ch.hesge.cragsi.model.Accounting;
import ch.hesge.cragsi.model.Activity;
import ch.hesge.cragsi.model.Price;
import ch.hesge.cragsi.model.Project;
import ch.hesge.cragsi.utils.StringUtils;

public class CragsiLoader {

	// Private attributes
	private Calendar calendar;
	private Date firstSemesterStart;
	private Date firstSemesterEnd;
	private Date secondSemesterStart;
	private Date secondSemesterEnd;

	private String academicPeriodS1;
	private String academicPeriodS2;
	private String journalId_S1;
	private String journalId_S2;
	private String periodId_S1;
	private String periodId_S2;

	private List<Account> accounts;
	private List<Project> projects;
	private Map<String, Price> priceMap;
	private List<Accounting> accountings;

	/**
	 * Default constructor
	 */
	public CragsiLoader() {

		accountings = new ArrayList<>();
		
		academicPeriodS1 = UserSettings.getInstance().getProperty("academicPeriod_S1");
		academicPeriodS2 = UserSettings.getInstance().getProperty("academicPeriod_S2");
		journalId_S1     = UserSettings.getInstance().getProperty("journalId_S1");
		journalId_S2     = UserSettings.getInstance().getProperty("journalId_S2");
		periodId_S1      = UserSettings.getInstance().getProperty("periodId_S1");
		periodId_S2      = UserSettings.getInstance().getProperty("periodId_S2");

		calendar = Calendar.getInstance();

		// Calculate start of first semester
		calendar.set(Calendar.YEAR, StringUtils.toInteger(academicPeriodS1));
		calendar.set(Calendar.MONTH, 8);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		firstSemesterStart = new Date(calendar.getTime().getTime());
		
		// Calculate start of second semester
		calendar.set(Calendar.YEAR, StringUtils.toInteger(academicPeriodS1));
		calendar.set(Calendar.MONTH, 11);
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		firstSemesterEnd = new Date(calendar.getTime().getTime());

		// Calculate start of second semester
		calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, StringUtils.toInteger(academicPeriodS2));
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		secondSemesterStart = new Date(calendar.getTime().getTime());

		// Calculate end of second semester
		calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, StringUtils.toInteger(academicPeriodS2));
		calendar.set(Calendar.MONTH, 7);
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		secondSemesterEnd = new Date(calendar.getTime().getTime());

		try {
			accounts = AccountDao.findAll();
			projects = ProjectDao.findAll();
			priceMap = PriceDao.findMapByLibelle();
		}
		catch (FileNotFoundException e) {
			System.out.println("CragsiLoader: an unexpected error has occured: " + e.toString());
		}
		catch (IOException e) {
			System.out.println("CragsiLoader: an unexpected error has occured: " + e.toString());
		}
	}

	public void start() {

		accountings.clear();

		try {

			// Generate all accountings
			generateFDCAccountings();

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

		int accountingSequence = 1;
		
		for (Activity activity : ActivityDao.findAll()) {

			if (!priceMap.containsKey(activity.getContractType())) {
				System.out.println("==> missing contract '" + activity.getContractType() + "' !");
			}
			else {
				
				// Retrieve activity price
				Price activityPrice = priceMap.get(activity.getContractType());

				if (activityPrice == null) {
					System.out.println("==> missing price for code '" + activity.getContractType() + "' !");
					continue;
				}

				// Retrieve salary account
				String salaryCode = UserSettings.getInstance().getProperty("salaryAccount");
				Account salaryAccount = AccountDao.findByCode(salaryCode, accounts);

				if (salaryAccount == null) {
					System.out.println("==> missing salaries account with code '" + salaryCode + "' !");
					continue;
				}

				// Retrieve socle account
				String socleCode = UserSettings.getInstance().getProperty("socleAccount");
				Account socleAccount = AccountDao.findByCode(socleCode, accounts);

				if (socleAccount == null) {
					System.out.println("==> missing socle account with code '" + socleCode + "' !");
					continue;
				}

				// Retrieve projects account
				String projectsCode = UserSettings.getInstance().getProperty("projectsAccount");
				Account projectsAccount = AccountDao.findByCode(projectsCode, accounts);

				// Retrieve collaborator account
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

							// Check if an account is associated to project
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
				Date accountingDateS1 = firstSemesterStart;
				if (activity.getStartContract().before(firstSemesterStart)) {
					System.out.println("==> invalid contract start date for collaborator '" + activity.getLastname() + " !");
					continue;
				}
				else if (activity.getStartContract().after(firstSemesterStart)) {
					accountingDateS1 = activity.getStartContract();
				}
				
				// Calculate accounting date for second semester
				Date accountingDateS2 = secondSemesterStart;				
				if (activity.getEndContract().after(secondSemesterEnd)) {
					System.out.println("==> invalid contract end date for collaborator '" + activity.getLastname() + " !");
					continue;
				}
				else if (activity.getStartContract().after(secondSemesterStart)) {
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
				String accountingLabelS1 = accountingLabel + " (" + academicPeriodS1 + ")";
				String accountingLabelS2 = accountingLabel + " (" + academicPeriodS2 + ")";

				// ==> Generate accounting for first semester, if accounting date is within range
				if (activity.getCostS1() != 0 && !accountingDateS1.before(firstSemesterStart) && !accountingDateS1.after(firstSemesterEnd)) {
					
					// Collaborator accountings
					accountings.add(AccountingFactory.createDebitEntry(accountingSequence, accountingDateS1, journalId_S1, periodId_S1, collaboratorAccount, accountingLabelS1, activity.getCostS1()));
					accountings.add(AccountingFactory.createCreditEntry(accountingSequence, accountingDateS1, journalId_S1, periodId_S1, salaryAccount, accountingLabelS1, activity.getCostS1()));
					accountingSequence++;

					// Check if activity is associated to a project
					if (projectAccount != null) {

						// Project accountings
						accountings.add(AccountingFactory.createDebitEntry(accountingSequence, accountingDateS1, journalId_S1, periodId_S1, projectAccount, accountingLabelS1, activity.getCostS1()));
						accountings.add(AccountingFactory.createCreditEntry(accountingSequence, accountingDateS1, journalId_S1, periodId_S1, projectsAccount, accountingLabelS1, activity.getCostS1()));
						accountingSequence++;
					}
					else {

						// Socle accountings
						accountings.add(AccountingFactory.createDebitEntry(accountingSequence, accountingDateS1, journalId_S1, periodId_S1, socleAccount, accountingLabelS1, activity.getCostS1()));
						accountings.add(AccountingFactory.createCreditEntry(accountingSequence, accountingDateS1, journalId_S1, periodId_S1, projectsAccount, accountingLabelS1, activity.getCostS1()));
						accountingSequence++;
					}
				}
				

				// ==> Generate accounting for second semester (S2)
				if (activity.getCostS2() != 0 && !accountingDateS2.before(secondSemesterStart) && !accountingDateS2.after(secondSemesterEnd)) {
					
					// Collaborator accountings
					accountings.add(AccountingFactory.createDebitEntry(accountingSequence, accountingDateS2, journalId_S2, periodId_S2, collaboratorAccount, accountingLabelS2, activity.getCostS2()));
					accountings.add(AccountingFactory.createCreditEntry(accountingSequence, accountingDateS2, journalId_S2, periodId_S2, salaryAccount, accountingLabelS2, activity.getCostS2()));
					accountingSequence++;

					// Check if activity is associated to a project
					if (projectAccount != null) {

						// Project accountings
						accountings.add(AccountingFactory.createDebitEntry(accountingSequence, accountingDateS2, journalId_S2, periodId_S2, projectAccount, accountingLabelS2, activity.getCostS2()));
						accountings.add(AccountingFactory.createCreditEntry(accountingSequence, accountingDateS2, journalId_S2, periodId_S2, projectsAccount, accountingLabelS2, activity.getCostS2()));
						accountingSequence++;
					}
					else {

						// Socle accountings
						accountings.add(AccountingFactory.createDebitEntry(accountingSequence, accountingDateS2, journalId_S2, periodId_S2, socleAccount, accountingLabelS2, activity.getCostS2()));
						accountings.add(AccountingFactory.createCreditEntry(accountingSequence, accountingDateS2, journalId_S2, periodId_S2, projectsAccount, accountingLabelS2, activity.getCostS2()));
						accountingSequence++;
					}
				}
			}
		}
	}

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
