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
	private String periodId_S1;

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
		journalId_S1 = UserSettings.getInstance().getProperty("journalId_S1");
		periodId_S1 = UserSettings.getInstance().getProperty("periodId_S1");

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
				double activityPrice = priceMap.get(activity.getContractType()).getPrice();

				// Compute costs
				activity.setCostS1(activity.getTotalS1() * activityPrice);
				activity.setCostS2(activity.getTotalS2() * activityPrice);

				// Retrieve collaborator account
				Account resourceAccount = AccountDao.findByName(activity.getLastname(), accounts);

				// Retrieve salary account
				String salaryCode = UserSettings.getInstance().getProperty("salaryAccountCode");
				Account salaryAccount = AccountDao.findByCode(salaryCode, accounts);

				// Retrieve socle account
				String socleCode = UserSettings.getInstance().getProperty("socleAccountCode");
				Account socleAccount = AccountDao.findByCode(socleCode, accounts);

				if (resourceAccount == null) {
					System.out.println("==> missing account for collaborator '" + activity.getFirstname() + " " + activity.getLastname() + "' with contract '" + activity.getContractType() + "' !");
				}
				else if (salaryAccount == null) {
					System.out.println("==> missing account for salaries with code '" + salaryCode + "' !");
				}
				else if (socleAccount == null) {
					System.out.println("==> missing account for socle with code '" + socleCode + "' !");
				}
				else {

					// Retrieve project from activity
					String projectNumber = StringUtils.toNumber(activity.getProjectNumber());

					// Automatically add project number in libelle
					String libelle = activity.getDetail();
					if (projectNumber.length() > 0 && !libelle.contains(projectNumber)) {
						libelle = projectNumber + " - " + libelle;
					}
						
					// Retrieve project associated to the activity
					Project project = ProjectDao.findByCode(projectNumber, projects);

					if (projectNumber != null) {
						
						// Create resource debit accounting
						Accounting debitAccounting = new Accounting();
						debitAccounting.setKeyId(accountingSequence);
						debitAccounting.setDate(accountingDate);
						debitAccounting.setJournalId(journalId_S1);
						debitAccounting.setName("1");
						debitAccounting.setPeriodId(periodId_S1);
						debitAccounting.setAccountId(resourceAccount.getId());
						debitAccounting.setLineDate(accountingDate);
						debitAccounting.setLineName(libelle);
						debitAccounting.setLineDebit(activity.getCostS1());
						debitAccounting.setLineJournalId(journalId_S1);
						debitAccounting.setLinePeriodId(periodId_S1);
						accountings.add(debitAccounting);

						// Create resource credit accounting
						Accounting creditAccounting = new Accounting();
						creditAccounting.setAccountId(salaryAccount.getId());
						creditAccounting.setLineDate(accountingDate);
						creditAccounting.setLineName(libelle);
						creditAccounting.setLineCredit(activity.getCostS1());
						debitAccounting.setLineJournalId(journalId_S1);
						debitAccounting.setLinePeriodId(periodId_S1);
						accountings.add(creditAccounting);

						accountingSequence++;
						
						// Retrieve account associated to the project
						String projectAccountSuffix = UserSettings.getInstance().getProperty("projectAccountSuffix");
						Account account = AccountDao.findByCode(projectAccountSuffix + projectNumber, accounts);

						if (account == null) {
							System.out.println("==> missing account for project '" + projectNumber + "' !");
						}
						else {
							
							// Create project debit accounting
							debitAccounting = new Accounting();
							debitAccounting.setKeyId(accountingSequence);
							debitAccounting.setDate(accountingDate);
							debitAccounting.setJournalId(journalId_S1);
							debitAccounting.setName("1");
							debitAccounting.setPeriodId(periodId_S1);
							debitAccounting.setAccountId(account.getId());
							debitAccounting.setLineDate(accountingDate);
							debitAccounting.setLineName(libelle);
							debitAccounting.setLineDebit(activity.getCostS1());
							debitAccounting.setLineJournalId(journalId_S1);
							debitAccounting.setLinePeriodId(periodId_S1);
							accountings.add(debitAccounting);

							accountingSequence++;
						}
						
					}
					else {
						// Activity not associated to project

						// Create resource counterpart
						Accounting debitAccounting = new Accounting();
						debitAccounting.setKeyId(accountingSequence);
						debitAccounting.setDate(accountingDate);
						debitAccounting.setJournalId(journalId_S1);
						debitAccounting.setName("1");
						debitAccounting.setPeriodId(periodId_S1);
						debitAccounting.setAccountId(resourceAccount.getId());
						debitAccounting.setLineDate(accountingDate);
						debitAccounting.setLineName(libelle);
						debitAccounting.setLineDebit(activity.getCostS1());
						debitAccounting.setLineJournalId(journalId_S1);
						debitAccounting.setLinePeriodId(periodId_S1);
						accountings.add(debitAccounting);

						// Create salary counterpart
						Accounting creditAccounting = new Accounting();
						creditAccounting.setAccountId(salaryAccount.getId());
						creditAccounting.setLineDate(accountingDate);
						creditAccounting.setLineName(libelle);
						creditAccounting.setLineCredit(activity.getCostS1());
						debitAccounting.setLineJournalId(journalId_S1);
						debitAccounting.setLinePeriodId(periodId_S1);
						accountings.add(creditAccounting);

						accountingSequence++;

						// Create project counterpart
						debitAccounting = new Accounting();
						debitAccounting.setKeyId(accountingSequence);
						debitAccounting.setDate(accountingDate);
						debitAccounting.setJournalId(journalId_S1);
						debitAccounting.setName("1");
						debitAccounting.setPeriodId(periodId_S1);
						debitAccounting.setAccountId(socleAccount.getId());
						debitAccounting.setLineDate(accountingDate);
						debitAccounting.setLineName(libelle);
						debitAccounting.setLineDebit(activity.getCostS1());
						debitAccounting.setLineJournalId(journalId_S1);
						debitAccounting.setLinePeriodId(periodId_S1);
						accountings.add(debitAccounting);

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
