package ch.hesge.cragsi.dao;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import ch.hesge.cragsi.model.Account;
import ch.hesge.cragsi.utils.CsvReader;
import ch.hesge.cragsi.utils.StringUtils;

/**
 * Class responsible to manage DAO access for Concept.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class AccountDao {

	private static String accountPath = "res/adoo.plan-comptable.csv";

	/**
	 * Retrieve all accounts contained in file
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static List<Account> findAll() throws IOException {

		List<Account> accountList = new ArrayList<>();

		CsvReader reader = new CsvReader(accountPath, ',', Charset.forName("UTF8"));
		reader.readHeaders();

		while (reader.readRecord()) {

			String keyId = StringUtils.clean(reader.get("id"));
			String code = StringUtils.clean(reader.get("code"));
			String name = StringUtils.clean(reader.get("name"));
			String type = StringUtils.clean(reader.get("type"));

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
