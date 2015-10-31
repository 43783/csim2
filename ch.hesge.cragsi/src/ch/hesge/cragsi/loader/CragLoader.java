package ch.hesge.cragsi.loader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ch.hesge.cragsi.dao.AccountDao;
import ch.hesge.cragsi.dao.ActivityDao;
import ch.hesge.cragsi.dao.ProjectDao;
import ch.hesge.cragsi.model.Account;
import ch.hesge.cragsi.model.AccountingEntry;
import ch.hesge.cragsi.model.Activity;
import ch.hesge.cragsi.model.Project;

public class CragLoader {

	/**
	 * Default constructor
	 */
	public CragLoader() {
	}

	public void start() {
		
		try {
			
			System.out.println("----------------------------------");
			System.out.println(" Account list:");
			System.out.println("----------------------------------");

			List<Account> accounts = AccountDao.findAll();

			for (Account account : accounts) {
				System.out.println(account.toString());
			}

			System.out.println("----------------------------------");
			System.out.println(" Project list:");
			System.out.println("----------------------------------");

			List<Project> projects = ProjectDao.findAll();

			for (Project project : projects) {
				System.out.println(project.toString());
			}
			
			System.out.println("----------------------------------");
			System.out.println(" Activity list:");
			System.out.println("----------------------------------");

			List<Activity> activities = ActivityDao.findAll();

			for (Activity activity : activities) {
				System.out.println(activity.toString());
			}
			
			List<AccountingEntry> newEntries = new ArrayList<>();
			
			AccountingEntry entry = new AccountingEntry();
			newEntries.add(entry);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Main entry point
	 * @param args
	 */
	public static void main(String[] args) {
		new CragLoader().start();
	}

}
