package ch.hesge.cragsi.dao;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import ch.hesge.cragsi.exceptions.ConfigurationException;
import ch.hesge.cragsi.model.Partner;
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
public class PartnerDao {

	/**
	 * Retrieve all partners (or sub-contractor) contained in file.
	 * 
	 * @return a list of Partner
	 * @throws IOException
	 * @throws ConfigurationException 
	 */
	public static List<Partner> findAll() throws IOException, ConfigurationException {

		CsvReader reader = null;
		List<Partner> partnerList = new ArrayList<>();
		String fundingPath = PropertyUtils.getProperty("partnerPath");

		try {

			// Open file to load
			reader = new CsvReader(fundingPath, ';', Charset.forName("UTF8"));
			reader.setSkipEmptyRecords(true);
			reader.readHeaders();

			// Start parsing partners
			while (reader.readRecord()) {

				// Retrieve field values
				String date          = reader.get(0);
				String projectNumber = reader.get(1);
				String name          = reader.get(2);
				String title         = reader.get(3);
				String amount        = reader.get(4);

				// Create and initialize an new instance
				Partner partner = new Partner();

				partner.setDate(StringUtils.toDate(date, "yyyy-MM-dd"));
				partner.setProjectNumber(projectNumber);
				partner.setName(name);
				partner.setTitle(title);
				partner.setAmount(StringUtils.toDouble(amount));

				partnerList.add(partner);
			}
		}
		finally {
			if (reader != null)
				reader.close();
		}

		return partnerList;
	}
}
