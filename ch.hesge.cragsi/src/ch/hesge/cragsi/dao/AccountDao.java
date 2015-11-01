package ch.hesge.cragsi.dao;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import ch.hesge.cragsi.model.Account;
import ch.hesge.cragsi.utils.CsvReader;

/**
 * Class responsible to manage DAO access for Account.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class AccountDao {

	/**
	 * Retrieve all accounts contained in file
	 * @return
	 * @throws IOException
	 */
	public static List<Account> findAll() throws IOException {

		List<Account> accountList = new ArrayList<>();
		String accountPath = UserSettings.getInstance().getAccountPath();

		CsvReader reader = new CsvReader(accountPath, ',', Charset.forName("UTF8"));
		reader.setSkipEmptyRecords(true);
		reader.readHeaders();

		while (reader.readRecord()) {

			String keyId = reader.get(1);//.replaceAll("__export__.", "");
			String code = reader.get(2);
			String name = reader.get(3);
			String type = reader.get(4);
			
			Account account = new Account();
			
			account.setKeyId(keyId);
			account.setCode(code);
			account.setName(name);
			account.setType(type);

			accountList.add(account);
		}

		reader.close();
		
		return accountList;
	}
}
