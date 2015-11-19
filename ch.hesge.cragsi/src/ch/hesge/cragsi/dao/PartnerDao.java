package ch.hesge.cragsi.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.hesge.cragsi.exceptions.ConfigurationException;
import ch.hesge.cragsi.model.Partner;
import ch.hesge.cragsi.utils.ConnectionUtils;
import ch.hesge.cragsi.utils.PropertyUtils;

/**
 * Class responsible to manage physical access to underlying
 * files and to load them in memory.
 * 
 * Copyright HEG Geneva 2015, Switzerland
 * @author Eric Harth
 */
public class PartnerDao {

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
		String query = PropertyUtils.getProperty("FINANCIAL_QUERY");
		ResultSet result = ConnectionUtils.getConnection().createStatement().executeQuery(query);
		
		while (result.next()) {

			// Retrieve field values
			Date date          = result.getDate(1); // OUTSOURCINGSTARTDATE
			String projectCode = result.getString(2); // IDPROJECTHESSO
			String name        = result.getString(3); // PARTNERNAME
			String libelle     = result.getString(4); // OUTSOURCINGREMARK
			double amount      = result.getDouble(5); // OUTSOURCINGAMOUNT

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
}
