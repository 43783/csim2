package ch.hesge.cragsi.dao;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import ch.hesge.cragsi.loader.UserSettings;
import ch.hesge.cragsi.model.Account;
import ch.hesge.cragsi.utils.CsvReader;

/**
 * Class responsible to manage physical access to underlying
 * files and to load them in memory.
 * 
 * Copyright HEG Geneva 2015, Switzerland
 * 
 * @author Eric Harth
 */
public class AccountDao {

	/**
	 * Retrieve all accounts contained in file.
	 * 
	 * @return a list of Account
	 * @throws IOException
	 */
	public static List<Account> findAll() throws IOException {

		CsvReader reader = null;
		List<Account> accountList = new ArrayList<>();
		String accountPath = UserSettings.getInstance().getProperty("accountPath");

		try {
			
			// Open file to load
			reader = new CsvReader(accountPath, ';', Charset.forName("UTF8"));
			reader.setSkipEmptyRecords(true);
			reader.readHeaders();

			// Start parsing accounts
			while (reader.readRecord()) {

				// Retrieve field values
				String id   = reader.get(0);
				String code = reader.get(1);
				String name = reader.get(2);
				String type = reader.get(3);

				// Create and initialize an new instance
				Account account = new Account();

				account.setId(id);
				account.setCode(code);
				account.setName(name);
				account.setType(type);

				accountList.add(account);
			}
		}
		finally {
			if (reader != null)
				reader.close();
		}

		return accountList;
	}
	
	/**
	 * Retrieve a single account based on its name.
	 * If an account match partially the name (that is it contains part of the name), it is returned.
	 * 
	 * @param name the account name to find
	 * @param accounts the list of account to use while scanning name
	 * @return an Account or null
	 */
	public static Account findByName(String name, List<Account> accounts) {

		for (Account account : accounts) {

			if (account.getName().toLowerCase().contains(name.toLowerCase())) {
				return account;
			}
		}

		return null;
	}
	
	/**
	 * Retrieve a single account based on its code.
	 * If an account match exactly the code, it is returned.
	 * 
	 * @param code the account code to find
	 * @param accounts the list of account to use while scanning name
	 * @return an Account or null
	 */
	public static Account findByCode(String code, List<Account> accounts) {

		for (Account account : accounts) {

			if (account.getCode().toLowerCase().equals(code.toLowerCase())) {
				return account;
			}
		}

		return null;
	}
}
