package ch.hesge.cragsi.loader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ch.hesge.cragsi.dao.AccountDao;
import ch.hesge.cragsi.dao.ActivityDao;
import ch.hesge.cragsi.dao.PriceDao;
import ch.hesge.cragsi.model.Account;
import ch.hesge.cragsi.model.Accounting;
import ch.hesge.cragsi.model.Activity;
import ch.hesge.cragsi.model.Contributor;
import ch.hesge.cragsi.model.Funding;
import ch.hesge.cragsi.model.Price;
import ch.hesge.cragsi.model.Project;

public class CragsiLoader {

	/**
	 * Default constructor
	 */
	public CragsiLoader() {
	}

	public void start() {

		try {

			List<Account> accounts = AccountDao.findAll();
			List<Price> prices = PriceDao.findAll();

			List<Activity> activities = ActivityDao.findAll();

			/*
			List<Project> projects = ProjectDao.findAll();
			dumpProjects(projects);

			List<Activity> activities = ActivityDao.findAll();
			dumpActivities(activities);

			List<Contributor> contributors = ContributorDao.findAll();
			dumpContributors(contributors);

			List<Funding> fundings = FundingDao.findAll();		
			dumpFundings(fundings);

			List<Accounting> accountings= createAccountings();			
			AccountingDao.saveAll(accountings);
			dumpAccountings(accountings);
			*/

		}
		catch (FileNotFoundException e) {
			System.out.println("CragsiLoader: an unexpected error has occured: " + e.toString());
		}
		catch (IOException e) {
			System.out.println("CragsiLoader: an unexpected error has occured: " + e.toString());
		}
	}

	public List<Accounting> createAccountings() {

		List<Accounting> accountings = new ArrayList<>();

		//id;date;journal_id/id;name;period_id/id;line_id/account_id/id;line_id/date;line_id/name;line_id/credit;line_id/debit;line_id/journal_id/id;line_id/period_id/id
		//7;2015-9-1;__export__.account_journal_5;1;__export__.account_period_10;__export__.account_account_172;2015-9-1;Career Women;;6000;__export__.account_journal_5;__export__.account_period_10

		Accounting accounting = new Accounting();
		accounting.setKeyId("7");
		accounting.setDate("2015-9-1");
		accounting.setJournalId("__export__.account_journal_5");
		accounting.setName("1");
		accounting.setPeriodId("__export__.account_period_10");
		accounting.setAccountId("__export__.account_account_172");
		accounting.setLineDate("2015-9-1");
		accounting.setLineName("Career Women");
		accounting.setLineDebit("6000");
		accounting.setLineJournalId("__export__.account_journal_5");
		accounting.setLinePeriodId("__export__.account_period_10");
		accountings.add(accounting);

		//id;date;journal_id/id;name;period_id/id;line_id/account_id/id;line_id/date;line_id/name;line_id/credit;line_id/debit;line_id/journal_id/id;line_id/period_id/id
		//;;;;;__export__.account_account_134;2015-9-1;Career Women;6000;;__export__.account_journal_5;__export__.account_period_10

		accounting = new Accounting();
		accounting.setAccountId("__export__.account_account_134");
		accounting.setLineDate("2015-9-1");
		accounting.setLineName("Career Women");
		accounting.setLineCredit("6000");
		accounting.setLineJournalId("__export__.account_journal_5");
		accounting.setLinePeriodId("__export__.account_period_10");
		accountings.add(accounting);

		//id;date;journal_id/id;name;period_id/id;line_id/account_id/id;line_id/date;line_id/name;line_id/credit;line_id/debit;line_id/journal_id/id;line_id/period_id/id
		//121;2015-9-1;__export__.account_journal_5;1;__export__.account_period_10;__export__.account_account_173;2015-9-1;Dépôt - RCSO;;1600;__export__.account_journal_5;__export__.account_period_10

		accounting = new Accounting();
		accounting.setKeyId("121");
		accounting.setDate("2015-9-1");
		accounting.setJournalId("__export__.account_journal_5");
		accounting.setName("1");
		accounting.setPeriodId("__export__.account_period_10");
		accounting.setAccountId("__export__.account_account_173");
		accounting.setLineDate("2015-9-1");
		accounting.setLineName("Dépôt - RCSO");
		accounting.setLineDebit("1600");
		accounting.setLineJournalId("__export__.account_journal_5");
		accounting.setLinePeriodId("__export__.account_period_10");
		accountings.add(accounting);

		//id;date;journal_id/id;name;period_id/id;line_id/account_id/id;line_id/date;line_id/name;line_id/credit;line_id/debit;line_id/journal_id/id;line_id/period_id/id
		//;;;;;__export__.account_account_134;2015-9-1;Dépôt - RCSO;1600;;__export__.account_journal_5;__export__.account_period_10

		accounting = new Accounting();
		accounting.setAccountId("__export__.account_account_134");
		accounting.setLineDate("2015-9-1");
		accounting.setLineName("Dépôt - RCSO");
		accounting.setLineCredit("1600");
		accounting.setLineJournalId("__export__.account_journal_5");
		accounting.setLinePeriodId("__export__.account_period_10");
		accountings.add(accounting);

		return accountings;
	}

	public void dumpAccounts(List<Account> accounts) {

		System.out.println("----------------------------------");
		System.out.println(" Account list:");
		System.out.println("----------------------------------");

		for (Account account : accounts) {
			System.out.println(account.toString());
		}
	}

	public void dumpProjects(List<Project> projects) {

		System.out.println("----------------------------------");
		System.out.println(" Project list:");
		System.out.println("----------------------------------");

		for (Project project : projects) {
			System.out.println(project.toString());
		}
	}

	public void dumpActivities(List<DetailedActivity> activities) {

		System.out.println("----------------------------------");
		System.out.println(" Activity list:");
		System.out.println("----------------------------------");

		for (DetailedActivity activity : activities) {
			System.out.println(activity.toString());
		}
	}

	public void dumpContributors(List<Contributor> contributors) {

		System.out.println("----------------------------------");
		System.out.println(" Contributor list:");
		System.out.println("----------------------------------");

		for (Contributor contributor : contributors) {
			System.out.println(contributor.toString());
		}
	}

	public void dumpFundings(List<Funding> fundings) {

		System.out.println("----------------------------------");
		System.out.println(" Funding list:");
		System.out.println("----------------------------------");

		for (Funding funding : fundings) {
			System.out.println(funding.toString());
		}
	}

	public void dumpAccountings(List<Accounting> accountings) {

		System.out.println("----------------------------------");
		System.out.println(" Accounting list:");
		System.out.println("----------------------------------");

		for (Accounting acc : accountings) {
			System.out.println(acc.toString());
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
