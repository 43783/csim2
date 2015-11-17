package ch.hesge.cragsi.dao;

import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.hesge.cragsi.exceptions.ConfigurationException;
import ch.hesge.cragsi.model.Financial;
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
public class FinancialDao {

	// Private attributes
	private static String SQLQUERY = "SELECT PRESTATIONSTARTDATE, IDPROJECTHESSO, PARTNERNAME, PROJECTFINANCIALAMOUNT FROM (MV2_PROJECT INNER JOIN MV2_FINANCIALPRESTATION ON MV2_PROJECT.IDPROJECT = MV2_FINANCIALPRESTATION.IDPROJECT) INNER JOIN MV2_PROJECTPARTNER ON MV2_FINANCIALPRESTATION.IDPROJECTPARTNERSOURCE = MV2_PROJECTPARTNER.IDPROJECTPARTNER WHERE MV2_PROJECT.IDSCHOOL=7";
	
	/**
	 * Retrieve all fundings (or financials) from database.
	 * 
	 * @return a list of Funding
	 * @throws SQLException
	 * @throws ConfigurationException
	 */
	public static List<Financial> findAll() throws SQLException, ConfigurationException {
		
		List<Financial> fundingList = new ArrayList<>();

		// Execute the query
		ResultSet result = ConnectionUtils.getConnection().createStatement().executeQuery(SQLQUERY);
		
		while (result.next()) {
			
			// Retrieve field values
			Date date          = result.getDate(1);
			String projectCode = result.getString(2);
			String libelle     = result.getString(3);
			double amount      = result.getDouble(4);

			// Create and initialize an new instance
			Financial funding = new Financial();
			
			funding.setDate(date);
			funding.setProjectCode(projectCode);
			funding.setLibelle(libelle);
			funding.setAmount(amount);

			fundingList.add(funding);
		}
		
		return fundingList;
	}
	
	/**
	 * Retrieve all fundings (or financials) contained in file.
	 * 
	 * @return a list of Funding
	 * @throws IOException
	 * @throws ConfigurationException 
	 */
	public static List<Financial> findAllFromFile() throws IOException, ConfigurationException {

		CsvReader reader = null;
		List<Financial> fundingList = new ArrayList<>();
		String fundingPath = PropertyUtils.getProperty("financialPath");

		try {

			// Open file to load
			reader = new CsvReader(fundingPath, ';', Charset.forName("UTF8"));
			reader.setSkipEmptyRecords(true);
			reader.readHeaders();

			// Start parsing fundings
			while (reader.readRecord()) {
				
				// Retrieve field values
				String date        = reader.get(0);
				String projectCode = reader.get(1);
				String libelle     = reader.get(2);
				String amount      = reader.get(3);

				// Create and initialize an new instance
				Financial funding = new Financial();
				
				funding.setDate(StringUtils.toDate(date, "yyyy-MM-dd"));
				funding.setProjectCode(projectCode);
				funding.setLibelle(libelle);
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
