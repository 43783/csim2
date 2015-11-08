package ch.hesge.cragsi.dao;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import ch.hesge.cragsi.exceptions.ConfigurationException;
import ch.hesge.cragsi.model.Collaborator;
import ch.hesge.cragsi.utils.CsvReader;
import ch.hesge.cragsi.utils.PropertyUtils;
import ch.hesge.cragsi.utils.StringUtils;

/**
 * Class responsible to manage physical access to underlying
 * files and to load them in memory.
 * 
 * Copyright HEG Geneva 2015, Switzerland
 * @author Eric Harth
 */
public class CollaboratorDao {

	/**
	 * Retrieve all contributors contained in file.
	 * 
	 * @return a list of Collaborator
	 * @throws IOException
	 * @throws ConfigurationException 
	 */
	public static List<Collaborator> findAll() throws IOException, ConfigurationException {

		CsvReader reader = null;
		List<Collaborator> contributorList = new ArrayList<>();
		String contributorPath = PropertyUtils.getProperty("collaboratorPath");

		try {
			
			// Open file to load
			reader = new CsvReader(contributorPath, ';', Charset.forName("UTF8"));
			reader.setSkipEmptyRecords(true);
			reader.readHeaders();

			// Start parsing collaborators
			while (reader.readRecord()) {
				
				// Retrieve field values
				String date      = reader.get(0);
				String hessoId   = reader.get(1);
				String id        = reader.get(2);
				String gestpacId = reader.get(3);
				String lastname  = reader.get(4);
				String firstname = reader.get(5);
				String classe    = reader.get(6);
				String rate      = reader.get(7);

				// Create and initialize an new instance
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
		}
		finally {
			if (reader != null) 
				reader.close();
		}
		
		return contributorList;
	}
}
