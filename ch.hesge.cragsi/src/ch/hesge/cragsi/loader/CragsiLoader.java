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
import ch.hesge.cragsi.model.Account;
import ch.hesge.cragsi.model.Accounting;
import ch.hesge.cragsi.model.Activity;
import ch.hesge.cragsi.model.Price;
import ch.hesge.cragsi.utils.StringUtils;

public class CragsiLoader {

	// Private attributes
	private int accountingSequence;
	private Date accountingDate;
	private String journalId_S1;
	private String periodId_S1;

	private List<Account> accounts;
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

				if (resourceAccount == null) {
					System.out.println("==> missing account for collaborator '" + activity.getFirstname() + " " + activity.getLastname() + "' with contract '" + activity.getContractType() + "' !");
				}
				else {

					// Retrieve salary account
					String salaryAccountCode = UserSettings.getInstance().getProperty("salaryAccount");
					Account salaryAccount = AccountDao.findByCode("5000", accounts);

					if (salaryAccount == null) {
						System.out.println("==> missing account for salaries with code '" + salaryAccountCode + "' !");
					}
					else {

						Accounting debitAccounting = new Accounting();
						
						// Automatically add project number in libelle
						String libelle = activity.getDetail();
						String projectNumber = StringUtils.toNumber(activity.getProjectNumber());
						if (projectNumber.length() > 0 && !libelle.contains(projectNumber)) {
							libelle = projectNumber + "-" + libelle;
						}
						
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

						Accounting creditAccounting = new Accounting();
						
						creditAccounting.setAccountId(salaryAccount.getId());
						creditAccounting.setLineDate(accountingDate);
						creditAccounting.setLineName(libelle);
						creditAccounting.setLineCredit(activity.getCostS1());
						debitAccounting.setLineJournalId(journalId_S1);
						debitAccounting.setLinePeriodId(periodId_S1);

						accountings.add(creditAccounting);

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
