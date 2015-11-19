package ch.hesge.cragsi.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.hesge.cragsi.exceptions.ConfigurationException;
import ch.hesge.cragsi.model.Financial;
import ch.hesge.cragsi.utils.ConnectionUtils;
import ch.hesge.cragsi.utils.PropertyUtils;

/**
 * Class responsible to manage physical access to underlying
 * files and to load them in memory.
 * 
 * Copyright HEG Geneva 2015, Switzerland
 * @author Eric Harth
 */
public class FinancialDao {

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
		String query = PropertyUtils.getProperty("FINANCIAL_QUERY");
		ResultSet result = ConnectionUtils.getConnection().createStatement().executeQuery(query);
		
		while (result.next()) {
			
			// Retrieve field values
			Date date          = result.getDate(1); // PRESTATIONSTARTDATE
			String projectCode = result.getString(2); // IDPROJECTHESSO
			String libelle     = result.getString(3); // PARTNERNAME
			double amount      = result.getDouble(4); // PROJECTFINANCIALAMOUNT

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
}
