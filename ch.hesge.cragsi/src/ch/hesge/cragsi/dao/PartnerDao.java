package ch.hesge.cragsi.dao;

import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.hesge.cragsi.exceptions.ConfigurationException;
import ch.hesge.cragsi.model.Partner;
import ch.hesge.cragsi.utils.ConnectionUtils;
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

	// Private attributes
	private static String SQLQUERY = "SELECT OUTSOURCINGSTARTDATE, IDPROJECTHESSO, PARTNERNAME, OUTSOURCINGREMARK, OUTSOURCINGAMOUNT FROM (MV2_OUTSOURCING INNER JOIN MV2_PROJECT ON MV2_OUTSOURCING.IDPROJECT = MV2_PROJECT.IDPROJECT) INNER JOIN MV2_PROJECTPARTNER ON MV2_OUTSOURCING.IDPROJECTPARTNEROUTSOURCED = MV2_PROJECTPARTNER.IDPROJECTPARTNEROUTSOURCED WHERE MV2_PROJECT.IDSCHOOL=7";
	
	/**
	 * Retrieve all partners (or sub-contractor) from database.
	 * 
	 * @return a list of Partner
	 * @throws SQLException
	 * @throws ConfigurationException
	 */
	public static List<Partner> findAll() throws SQLException, ConfigurationException {
		
		List<Partner> partnerList = new ArrayList<>();
		
		// Execute the query
		ResultSet result = ConnectionUtils.getConnection().createStatement().executeQuery(SQLQUERY);
		
		while (result.next()) {

			// Retrieve field values
			Date date          = result.getDate(1);
			String projectCode = result.getString(2);
			String name        = result.getString(3);
			String libelle     = result.getString(4);
			double amount      = result.getDouble(5);

			// Create and initialize an new instance
			Partner partner = new Partner();

			partner.setDate(date);
			partner.setProjectCode(projectCode);
			partner.setName(name);
			partner.setLibelle(libelle);
			partner.setAmount(amount);

			partnerList.add(partner);
		}

		return partnerList;
	}
	
	/**
	 * Retrieve all partners (or sub-contractor) contained in file.
	 * 
	 * @return a list of Partner
	 * @throws IOException
	 * @throws ConfigurationException 
	 */
	public static List<Partner> findAllFromFile() throws IOException, ConfigurationException {

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
				String date        = reader.get(0);
				String projectCode = reader.get(1);
				String name        = reader.get(2);
				String libelle     = reader.get(3);
				String amount      = reader.get(4);

				// Create and initialize an new instance
				Partner partner = new Partner();

				partner.setDate(StringUtils.toDate(date, "yyyy-MM-dd"));
				partner.setProjectCode(projectCode);
				partner.setName(name);
				partner.setLibelle(libelle);
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
