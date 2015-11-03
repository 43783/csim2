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

public class CragsiLoader {

	/**
	 * Default constructor
	 */
	public CragsiLoader() {
	}

	public void start() {

		try {

			int accountingSequence = 1;
			List<Accounting> accountings = new ArrayList<>();

			Date accountingDate = Calendar.getInstance().getTime();
			String journalId_S1 = UserSettings.getInstance().getProperty("journalId_S1");
			String periodId_S1 = UserSettings.getInstance().getProperty("periodId_S1");

			List<Account> accounts = AccountDao.findAll();
			Map<String, Price> priceMap = PriceDao.findMapByLibelle();

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
							debitAccounting.setKeyId(accountingSequence++);
							debitAccounting.setDate(accountingDate);
							debitAccounting.setJournalId(journalId_S1);
							debitAccounting.setName("1");
							debitAccounting.setPeriodId(periodId_S1);
							debitAccounting.setAccountId(resourceAccount.getId());
							debitAccounting.setLineDate(accountingDate);
							debitAccounting.setLineName(activity.getDetail());
							debitAccounting.setLineDebit(activity.getCostS1());
							debitAccounting.setLineJournalId(journalId_S1);
							debitAccounting.setLinePeriodId(periodId_S1);

							Accounting creditAccounting = new Accounting();
							creditAccounting.setAccountId(salaryAccount.getId());
							creditAccounting.setLineDate(accountingDate);
							creditAccounting.setLineName(activity.getDetail());
							creditAccounting.setLineCredit(activity.getCostS1());
							debitAccounting.setLineJournalId(journalId_S1);
							debitAccounting.setLinePeriodId(periodId_S1);

							accountings.add(debitAccounting);
							accountings.add(creditAccounting);
						}
					}
				}
			}

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
