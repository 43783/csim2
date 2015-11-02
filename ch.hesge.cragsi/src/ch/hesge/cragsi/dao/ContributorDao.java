package ch.hesge.cragsi.dao;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import ch.hesge.cragsi.loader.UserSettings;
import ch.hesge.cragsi.model.Contributor;
import ch.hesge.cragsi.utils.CsvReader;

/**
 * Class responsible to manage DAO access for Account.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class ContributorDao {

	/**
	 * Retrieve all contributors contained in file
	 * @return
	 * @throws IOException
	 */
	public static List<Contributor> findAll() throws IOException {

		List<Contributor> contributorList = new ArrayList<>();
		String contributorPath = UserSettings.getInstance().getProperty("contributorPath");

		CsvReader reader = new CsvReader(contributorPath, ';', Charset.forName("UTF8"));
		reader.setSkipEmptyRecords(true);
		reader.readHeaders();

		while (reader.readRecord()) {
			
			String date = reader.get(0);
			String hessoId = reader.get(1);
			String keyId = reader.get(2);
			String gestpacId = reader.get(3);
			String lastname = reader.get(4);
			String firstname = reader.get(5);
			String classe = reader.get(6);
			String rate = reader.get(7);

			Contributor contributor = new Contributor();
			
			contributor.setKeyId(keyId);
			contributor.setDate(date);
			contributor.setHessoId(hessoId);
			contributor.setGestpacId(gestpacId);
			contributor.setLastname(lastname);
			contributor.setFirstname(firstname);
			contributor.setClasse(classe);
			contributor.setRate(rate);

			contributorList.add(contributor);
		}

		reader.close();
		
		return contributorList;
	}
}
