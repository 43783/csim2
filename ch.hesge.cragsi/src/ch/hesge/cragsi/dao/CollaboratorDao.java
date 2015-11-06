package ch.hesge.cragsi.dao;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import ch.hesge.cragsi.loader.UserSettings;
import ch.hesge.cragsi.model.Collaborator;
import ch.hesge.cragsi.utils.CsvReader;
import ch.hesge.cragsi.utils.StringUtils;

/**
 * Class responsible to manage DAO access for Account.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class CollaboratorDao {

	/**
	 * Retrieve all contributors contained in file
	 * @return
	 * @throws IOException
	 */
	public static List<Collaborator> findAll() throws IOException {

		List<Collaborator> contributorList = new ArrayList<>();
		String contributorPath = UserSettings.getInstance().getProperty("collaboratorPath");

		CsvReader reader = new CsvReader(contributorPath, ';', Charset.forName("UTF8"));
		reader.setSkipEmptyRecords(true);
		reader.readHeaders();

		while (reader.readRecord()) {
			
			String date = reader.get(0);
			String hessoId = reader.get(1);
			String id = reader.get(2);
			String gestpacId = reader.get(3);
			String lastname = reader.get(4);
			String firstname = reader.get(5);
			String classe = reader.get(6);
			String rate = reader.get(7);

			Collaborator contributor = new Collaborator();
			
			contributor.setId(id);
			contributor.setDate(StringUtils.toDate(date, "yyyy-MM-dd"));
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
