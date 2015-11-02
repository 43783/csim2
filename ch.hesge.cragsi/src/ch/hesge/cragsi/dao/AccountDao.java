package ch.hesge.cragsi.dao;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import ch.hesge.cragsi.loader.UserSettings;
import ch.hesge.cragsi.model.Account;
import ch.hesge.cragsi.utils.CsvReader;

public class AccountDao {

	/**
	 * Retrieve all accounts contained in file
	 * @return
	 * @throws IOException
	 */
	public static List<Account> findAll() throws IOException {

		List<Account> accountList = new ArrayList<>();
		String accountPath = UserSettings.getInstance().getProperty("accountPath");

		CsvReader reader = new CsvReader(accountPath, ';', Charset.forName("UTF8"));
		reader.setSkipEmptyRecords(true);
		reader.readHeaders();

		while (reader.readRecord()) {

			String id = reader.get(0);
			String code = reader.get(1);
			String name = reader.get(2);
			String type = reader.get(3);

			Account account = new Account();

			account.setId(id);
			account.setCode(code);
			account.setName(name);
			account.setType(type);

			accountList.add(account);
		}

		reader.close();

		return accountList;
	}
}
