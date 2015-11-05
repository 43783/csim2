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
	private int accountingSequence;
	private Date accountingDate;
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

		accountingSequence = 1;
		accountings = new ArrayList<>();
		accountingDate = Calendar.getInstance().getTime();
		journalId_S1   = UserSettings.getInstance().getProperty("journalId_S1");
		journalId_S2   = UserSettings.getInstance().getProperty("journalId_S2");
		periodId_S1    = UserSettings.getInstance().getProperty("periodId_S1");
		periodId_S2    = UserSettings.getInstance().getProperty("periodId_S2");

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

		for (Activity activity : ActivityDao.findAll()) {

			if (!priceMap.containsKey(activity.getContractType())) {
				System.out.println("==> missing contract '" + activity.getContractType() + "' !");
			}
			else {

				// Retrieve salary account
				String salaryCode = UserSettings.getInstance().getProperty("salaryAccountCode");
				Account salaryAccount = AccountDao.findByCode(salaryCode, accounts);

				// Retrieve socle account
				String socleCode = UserSettings.getInstance().getProperty("socleAccountCode");
				Account socleAccount = AccountDao.findByCode(socleCode, accounts);

				// Retrieve collaborator account
				Account resourceAccount = AccountDao.findByName(activity.getLastname(), accounts);

				// Retrieve project account
				Account projectAccount = null;
				String projectNumber = StringUtils.toNumber(activity.getProjectNumber());
				if (!StringUtils.isEmtpy(projectNumber)) {

					// Retrieve project
					Project project = ProjectDao.findByCode(projectNumber, projects);

					// Check if project exists
					if (project == null) {
						System.out.println("==> missing project with code '" + projectNumber + "' !");
						continue;
					}
					else {
						
						// Check if project is not closed
						if (accountingDate.after(project.getStartDate()) && accountingDate.before(project.getEndDate())) {
							
							String accountSuffix = UserSettings.getInstance().getProperty("projectAccountSuffix");
							projectAccount = AccountDao.findByCode(accountSuffix + projectNumber, accounts);

							// Check if account associated to project exists
							if (projectAccount == null) {
								System.out.println("==> missing account for project with code '" + projectNumber + "' !");
								continue;
							}
						}
					}
				}
				
				// Retrieve activity price
				Price activityPrice = priceMap.get(activity.getContractType());
				
				// Catch invalid cases
				if (resourceAccount == null) {
					System.out.println("==> missing account for collaborator '" + activity.getFirstname() + " " + activity.getLastname() + "' with contract '" + activity.getContractType() + "' !");
					continue;
				}
				else if (salaryAccount == null) {
					System.out.println("==> missing account for salaries with code '" + salaryCode + "' !");
					continue;
				}
				else if (socleAccount == null) {
					System.out.println("==> missing account for socle with code '" + socleCode + "' !");
					continue;
				}
				else if (activityPrice == null) {
					System.out.println("==> missing price for code '" + activity.getContractType() + "' !");
					continue;
				}
					
				// Calculate activity costs
				activity.setCostS1(activity.getTotalS1() * activityPrice.getPrice());
				activity.setCostS2(activity.getTotalS2() * activityPrice.getPrice());
				
				// Adjust activityLabel with project number
				String activityLabel = activity.getDetail();
				if (!StringUtils.isEmtpy(projectNumber) && !activityLabel.contains(projectNumber)) {
					activityLabel = projectNumber + "-" + activityLabel;
				}
				
				// Calculate accounting label
				String academicPeriodS1 = UserSettings.getInstance().getProperty("academicPeriod_S1");
				String academicPeriodS2 = UserSettings.getInstance().getProperty("academicPeriod_S2");
				String accountingLabel = resourceAccount.getCode() + "-" + resourceAccount.getName() + "-" + activityLabel; 
				
				// Format label nicely
				accountingLabel = accountingLabel.replace("-", " - ");
				
				// Create debit resource entry (S1)
				Accounting debitResourceS1Accounting = new Accounting();
				debitResourceS1Accounting.setId(accountingSequence);
				debitResourceS1Accounting.setDate(accountingDate);
				debitResourceS1Accounting.setJournalId(journalId_S1);
				debitResourceS1Accounting.setName(StringUtils.toString(accountingSequence));
				debitResourceS1Accounting.setPeriodId(periodId_S1);
				debitResourceS1Accounting.setAccountId(resourceAccount.getId());
				debitResourceS1Accounting.setLineDate(accountingDate);
				debitResourceS1Accounting.setLineName(accountingLabel + " (" + academicPeriodS1 + ")");
				debitResourceS1Accounting.setLineDebit(activity.getCostS1());
				debitResourceS1Accounting.setLineJournalId(journalId_S1);
				debitResourceS1Accounting.setLinePeriodId(periodId_S1);
				accountings.add(debitResourceS1Accounting);

				// Create credit resource entry  (S1)
				Accounting creditResourceS1Accounting = new Accounting();
				creditResourceS1Accounting.setAccountId(salaryAccount.getId());
				creditResourceS1Accounting.setLineDate(accountingDate);
				creditResourceS1Accounting.setLineName(accountingLabel + " (" + academicPeriodS1 + ")");
				creditResourceS1Accounting.setLineCredit(activity.getCostS1());
				debitResourceS1Accounting.setLineJournalId(journalId_S1);
				debitResourceS1Accounting.setLinePeriodId(periodId_S1);
				accountings.add(creditResourceS1Accounting);

				accountingSequence++;

				// Create debit resource entry (S2)
				Accounting debitResourceS2Accounting = new Accounting();
				debitResourceS2Accounting.setId(accountingSequence);
				debitResourceS2Accounting.setDate(accountingDate);
				debitResourceS2Accounting.setJournalId(journalId_S2);
				debitResourceS2Accounting.setName(StringUtils.toString(accountingSequence));
				debitResourceS2Accounting.setPeriodId(periodId_S2);
				debitResourceS2Accounting.setAccountId(resourceAccount.getId());
				debitResourceS2Accounting.setLineDate(accountingDate);
				debitResourceS2Accounting.setLineName(accountingLabel + " (" + academicPeriodS2 + ")");
				debitResourceS2Accounting.setLineDebit(activity.getCostS2());
				debitResourceS2Accounting.setLineJournalId(journalId_S2);
				debitResourceS2Accounting.setLinePeriodId(periodId_S2);
				accountings.add(debitResourceS2Accounting);

				// Create credit resource entry  (S2)
				Accounting creditResourceS2Accounting = new Accounting();
				creditResourceS2Accounting.setAccountId(salaryAccount.getId());
				creditResourceS2Accounting.setLineDate(accountingDate);
				creditResourceS2Accounting.setLineName(accountingLabel + " (" + academicPeriodS2 + ")");
				creditResourceS2Accounting.setLineCredit(activity.getCostS2());
				creditResourceS2Accounting.setLineJournalId(journalId_S2);
				creditResourceS2Accounting.setLinePeriodId(periodId_S2);
				accountings.add(creditResourceS2Accounting);

				accountingSequence++;

				// Activity is associated to a project
				if (projectAccount != null) {
					
					// Create debit project entry (S1)
					Accounting debitProjectS1Accounting = new Accounting();
					debitProjectS1Accounting.setId(accountingSequence);
					debitProjectS1Accounting.setDate(accountingDate);
					debitProjectS1Accounting.setJournalId(journalId_S1);
					debitProjectS1Accounting.setName(StringUtils.toString(accountingSequence));
					debitProjectS1Accounting.setPeriodId(periodId_S1);
					debitProjectS1Accounting.setAccountId(projectAccount.getId());
					debitProjectS1Accounting.setLineDate(accountingDate);
					debitProjectS1Accounting.setLineName(accountingLabel + " (" + academicPeriodS1 + ")");
					debitProjectS1Accounting.setLineDebit(activity.getCostS1());
					debitProjectS1Accounting.setLineJournalId(journalId_S1);
					debitProjectS1Accounting.setLinePeriodId(periodId_S1);
					accountings.add(debitProjectS1Accounting);
						
					accountingSequence++;

					// Create debit project entry (S2)
					Accounting debitProjectS2Accounting = new Accounting();
					debitProjectS2Accounting.setId(accountingSequence);
					debitProjectS2Accounting.setDate(accountingDate);
					debitProjectS2Accounting.setJournalId(journalId_S2);
					debitProjectS2Accounting.setName(StringUtils.toString(accountingSequence));
					debitProjectS2Accounting.setPeriodId(periodId_S2);
					debitProjectS2Accounting.setAccountId(projectAccount.getId());
					debitProjectS2Accounting.setLineDate(accountingDate);
					debitProjectS2Accounting.setLineName(accountingLabel + " (" + academicPeriodS2 + ")");
					debitProjectS2Accounting.setLineDebit(activity.getCostS2());
					debitProjectS2Accounting.setLineJournalId(journalId_S2);
					debitProjectS2Accounting.setLinePeriodId(periodId_S2);
					accountings.add(debitProjectS2Accounting);
						
					accountingSequence++;
				}
				// Activity is associated to the HES socle
				else {
					
					// Create debit socle project entry (S1)
					Accounting debitSocleS1Accounting = new Accounting();
					debitSocleS1Accounting.setId(accountingSequence);
					debitSocleS1Accounting.setDate(accountingDate);
					debitSocleS1Accounting.setJournalId(journalId_S1);
					debitSocleS1Accounting.setName(StringUtils.toString(accountingSequence));
					debitSocleS1Accounting.setPeriodId(periodId_S1);
					debitSocleS1Accounting.setAccountId(socleAccount.getId());
					debitSocleS1Accounting.setLineDate(accountingDate);
					debitSocleS1Accounting.setLineName(accountingLabel + " (" + academicPeriodS1 + ")");
					debitSocleS1Accounting.setLineDebit(activity.getCostS1());
					debitSocleS1Accounting.setLineJournalId(journalId_S1);
					debitSocleS1Accounting.setLinePeriodId(periodId_S1);
					accountings.add(debitSocleS1Accounting);
						
					accountingSequence++;

					// Create debit socle project entry (S2)
					Accounting debitSocleS2Accounting = new Accounting();
					debitSocleS2Accounting.setId(accountingSequence);
					debitSocleS2Accounting.setDate(accountingDate);
					debitSocleS2Accounting.setJournalId(journalId_S2);
					debitSocleS2Accounting.setName(StringUtils.toString(accountingSequence));
					debitSocleS2Accounting.setPeriodId(periodId_S2);
					debitSocleS2Accounting.setAccountId(socleAccount.getId());
					debitSocleS2Accounting.setLineDate(accountingDate);
					debitSocleS2Accounting.setLineName(accountingLabel + " (" + academicPeriodS2 + ")");
					debitSocleS2Accounting.setLineDebit(activity.getCostS2());
					debitSocleS2Accounting.setLineJournalId(journalId_S2);
					debitSocleS2Accounting.setLinePeriodId(periodId_S2);
					accountings.add(debitSocleS2Accounting);
						
					accountingSequence++;
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
