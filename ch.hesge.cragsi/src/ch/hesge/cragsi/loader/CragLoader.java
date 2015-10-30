package ch.hesge.cragsi.loader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import ch.hesge.cragsi.dao.AccountDao;
import ch.hesge.cragsi.dao.ActivityDao;
import ch.hesge.cragsi.model.Account;
import ch.hesge.cragsi.model.Activity;

public class CragLoader {

	/**
	 * Default constructor
	 */
	public CragLoader() {
	}

	public void start() {
		
		try {
			
			List<Account> accounts = AccountDao.findAll();

			for (Account account : accounts) {
				
				System.out.println("----------------------------------");
				System.out.println("Account list:");
				System.out.println("----------------------------------");

				System.out.println("name: " + account.getName());
				System.out.println("  keyId: " + account.getKeyId());
				System.out.println("  code:  " + account.getCode());
				System.out.println("  type:  " + account.getType());
			}

			List<Activity> activities = ActivityDao.findAll();

			for (Activity activity : activities) {
				
				System.out.println("----------------------------------");
				System.out.println("Account list:");
				System.out.println("----------------------------------");

				System.out.println("name: " + account.getName());
				System.out.println("  keyId: " + account.getKeyId());
				System.out.println("  code:  " + account.getCode());
				System.out.println("  type:  " + account.getType());
			}
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
