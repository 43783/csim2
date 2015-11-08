package ch.hesge.cragsi.dao;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import ch.hesge.cragsi.exceptions.ConfigurationException;
import ch.hesge.cragsi.model.Account;
import ch.hesge.cragsi.utils.CsvReader;
import ch.hesge.cragsi.utils.PropertyUtils;

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
	 * Retrieve all accounts from file.
	 * 
	 * @return a list of Account
	 * @throws IOException
	 * @throws ConfigurationException 
	 */
	public static List<Account> findAll() throws IOException, ConfigurationException {

		CsvReader reader = null;
		List<Account> accountList = new ArrayList<>();
		String accountPath = PropertyUtils.getProperty("accountPath");

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
	
}
