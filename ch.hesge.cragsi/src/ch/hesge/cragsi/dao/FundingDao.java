package ch.hesge.cragsi.dao;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import ch.hesge.cragsi.exceptions.ConfigurationException;
import ch.hesge.cragsi.model.Funding;
import ch.hesge.cragsi.utils.CsvReader;
import ch.hesge.cragsi.utils.PropertyUtils;
import ch.hesge.cragsi.utils.StringUtils;

/**
 * Class responsible to manage physical access to underlying
 * files and to load them in memory.
 * 
 * Copyright HEG Geneva 2015, Switzerland
 * 
 * @author Eric Harth
 */
public class FundingDao {

	/**
	 * Retrieve all fundings (or financials) contained in file.
	 * 
	 * @return a list of Funding
	 * @throws IOException
	 * @throws ConfigurationException 
	 */
	public static List<Funding> findAll() throws IOException, ConfigurationException {

		CsvReader reader = null;
		List<Funding> fundingList = new ArrayList<>();
		String fundingPath = PropertyUtils.getProperty("financialPath");

		try {

			// Open file to load
			reader = new CsvReader(fundingPath, ';', Charset.forName("UTF8"));
			reader.setSkipEmptyRecords(true);
			reader.readHeaders();

			// Start parsing fundings
			while (reader.readRecord()) {
				
				// Retrieve field values
				String date          = reader.get(0);
				String projectNumber = reader.get(1);
				String name          = reader.get(2);
				String amount        = reader.get(3);

				// Create and initialize an new instance
				Funding funding = new Funding();
				
				funding.setDate(StringUtils.toDate(date, "yyyy-MM-dd"));
				funding.setProjectNumber(projectNumber);
				funding.setName(name);
				funding.setAmount(StringUtils.toDouble(amount));

				fundingList.add(funding);
			}
		}
		finally {
			if (reader != null) 
				reader.close();
		}
		
		return fundingList;
	}
}
