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
				Accounting debitS1ResourceAccounting = new Accounting();
				debitS1ResourceAccounting.setId(accountingSequence);
				debitS1ResourceAccounting.setDate(accountingDate);
				debitS1ResourceAccounting.setJournalId(journalId_S1);
				debitS1ResourceAccounting.setName(StringUtils.toString(accountingSequence));
				debitS1ResourceAccounting.setPeriodId(periodId_S1);
				debitS1ResourceAccounting.setAccountId(resourceAccount.getId());
				debitS1ResourceAccounting.setLineDate(accountingDate);
				debitS1ResourceAccounting.setLineName(accountingLabel + "(" + academicPeriodS1 + ")");
				debitS1ResourceAccounting.setLineDebit(activity.getCostS1());
				debitS1ResourceAccounting.setLineJournalId(journalId_S1);
				debitS1ResourceAccounting.setLinePeriodId(periodId_S1);
				accountings.add(debitS1ResourceAccounting);

				// Create credit resource entry  (S1)
				Accounting creditS1ResourceAccounting = new Accounting();
				creditS1ResourceAccounting.setAccountId(salaryAccount.getId());
				creditS1ResourceAccounting.setLineDate(accountingDate);
				creditS1ResourceAccounting.setLineName(accountingLabel + "(" + academicPeriodS1 + ")");
				creditS1ResourceAccounting.setLineCredit(activity.getCostS1());
				debitS1ResourceAccounting.setLineJournalId(journalId_S1);
				debitS1ResourceAccounting.setLinePeriodId(periodId_S1);
				accountings.add(creditS1ResourceAccounting);

				accountingSequence++;

				// Create debit resource entry (S2)
				Accounting debitS2ResourceAccounting = new Accounting();
				debitS2ResourceAccounting.setId(accountingSequence);
				debitS2ResourceAccounting.setDate(accountingDate);
				debitS2ResourceAccounting.setJournalId(journalId_S2);
				debitS2ResourceAccounting.setName(StringUtils.toString(accountingSequence));
				debitS2ResourceAccounting.setPeriodId(periodId_S2);
				debitS2ResourceAccounting.setAccountId(resourceAccount.getId());
				debitS2ResourceAccounting.setLineDate(accountingDate);
				debitS2ResourceAccounting.setLineName(accountingLabel + "(" + academicPeriodS2 + ")");
				debitS2ResourceAccounting.setLineDebit(activity.getCostS1());
				debitS2ResourceAccounting.setLineJournalId(journalId_S2);
				debitS2ResourceAccounting.setLinePeriodId(periodId_S2);
				accountings.add(debitS2ResourceAccounting);

				// Create credit resource entry  (S2)
				Accounting creditS2ResourceAccounting = new Accounting();
				creditS2ResourceAccounting.setAccountId(salaryAccount.getId());
				creditS2ResourceAccounting.setLineDate(accountingDate);
				debitS2ResourceAccounting.setLineName(accountingLabel + "(" + academicPeriodS2 + ")");
				creditS2ResourceAccounting.setLineCredit(activity.getCostS1());
				debitS2ResourceAccounting.setLineJournalId(journalId_S2);
				debitS2ResourceAccounting.setLinePeriodId(periodId_S2);
				accountings.add(creditS2ResourceAccounting);

				accountingSequence++;

				// Activity is associated to a project
				if (projectAccount != null) {
					
					// Create debit project entry (S1)
					Accounting debitS1ProjectAccounting = new Accounting();
					debitS1ProjectAccounting.setId(accountingSequence);
					debitS1ProjectAccounting.setDate(accountingDate);
					debitS1ProjectAccounting.setJournalId(journalId_S1);
					debitS1ProjectAccounting.setName(StringUtils.toString(accountingSequence));
					debitS1ProjectAccounting.setPeriodId(periodId_S1);
					debitS1ProjectAccounting.setAccountId(projectAccount.getId());
					debitS1ProjectAccounting.setLineDate(accountingDate);
					debitS1ProjectAccounting.setLineName(accountingLabel + "(" + academicPeriodS1 + ")");
					debitS1ProjectAccounting.setLineDebit(activity.getCostS1());
					debitS1ProjectAccounting.setLineJournalId(journalId_S1);
					debitS1ProjectAccounting.setLinePeriodId(periodId_S1);
					accountings.add(debitS1ProjectAccounting);
						
					accountingSequence++;

					// Create debit project entry (S2)
					Accounting debitS2ProjectAccounting = new Accounting();
					debitS2ProjectAccounting.setId(accountingSequence);
					debitS2ProjectAccounting.setDate(accountingDate);
					debitS2ProjectAccounting.setJournalId(journalId_S2);
					debitS2ProjectAccounting.setName(StringUtils.toString(accountingSequence));
					debitS2ProjectAccounting.setPeriodId(periodId_S2);
					debitS2ProjectAccounting.setAccountId(projectAccount.getId());
					debitS2ProjectAccounting.setLineDate(accountingDate);
					debitS2ProjectAccounting.setLineName(accountingLabel + "(" + academicPeriodS2 + ")");
					debitS2ProjectAccounting.setLineDebit(activity.getCostS1());
					debitS2ProjectAccounting.setLineJournalId(journalId_S2);
					debitS2ProjectAccounting.setLinePeriodId(periodId_S2);
					accountings.add(debitS2ProjectAccounting);
						
					accountingSequence++;
				}
				// Activity is associated to the HES socle
				else {
					
					// Create debit socle project entry (S1)
					Accounting debitS1SocleAccounting = new Accounting();
					debitS1SocleAccounting.setId(accountingSequence);
					debitS1SocleAccounting.setDate(accountingDate);
					debitS1SocleAccounting.setJournalId(journalId_S1);
					debitS1SocleAccounting.setName(StringUtils.toString(accountingSequence));
					debitS1SocleAccounting.setPeriodId(periodId_S1);
					debitS1SocleAccounting.setAccountId(socleAccount.getId());
					debitS1SocleAccounting.setLineDate(accountingDate);
					debitS1SocleAccounting.setLineName(accountingLabel + "(" + academicPeriodS1 + ")");
					debitS1SocleAccounting.setLineDebit(activity.getCostS1());
					debitS1SocleAccounting.setLineJournalId(journalId_S1);
					debitS1SocleAccounting.setLinePeriodId(periodId_S1);
					accountings.add(debitS1SocleAccounting);
						
					accountingSequence++;

					// Create debit socle project entry (S2)
					Accounting debitS2SocleAccounting = new Accounting();
					debitS2SocleAccounting.setId(accountingSequence);
					debitS2SocleAccounting.setDate(accountingDate);
					debitS2SocleAccounting.setJournalId(journalId_S2);
					debitS2SocleAccounting.setName(StringUtils.toString(accountingSequence));
					debitS2SocleAccounting.setPeriodId(periodId_S2);
					debitS2SocleAccounting.setAccountId(socleAccount.getId());
					debitS2SocleAccounting.setLineDate(accountingDate);
					debitS2SocleAccounting.setLineName(accountingLabel + "(" + academicPeriodS2 + ")");
					debitS2SocleAccounting.setLineDebit(activity.getCostS1());
					debitS2SocleAccounting.setLineJournalId(journalId_S2);
					debitS2SocleAccounting.setLinePeriodId(periodId_S2);
					accountings.add(debitS2SocleAccounting);
						
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
