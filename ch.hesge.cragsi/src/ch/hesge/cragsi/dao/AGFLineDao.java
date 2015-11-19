package ch.hesge.cragsi.dao;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.hesge.cragsi.exceptions.ConfigurationException;
import ch.hesge.cragsi.model.AGFLine;
import ch.hesge.cragsi.utils.ConnectionUtils;
import ch.hesge.cragsi.utils.PropertyUtils;

/**
 * Class responsible to manage physical access to underlying
 * files and to load them in memory.
 * 
 * Copyright HEG Geneva 2015, Switzerland
 * @author Eric Harth
 */
public class AGFLineDao {

	/**
	 * Retrieve all AGF lines from database.
	 * 
	 * @return a list of AGFLine
	 * @throws IOException
	 * @throws ConfigurationException 
	 */
	public static List<AGFLine> findAll() throws SQLException, ConfigurationException {
		
		List<AGFLine> agfLines = new ArrayList<>();

		// Execute the query
		String query = PropertyUtils.getProperty("AGF_QUERY");
		ResultSet result = ConnectionUtils.getConnection().createStatement().executeQuery(query);
		
		while (result.next()) {
			
			// Retrieve field values
			Date date          = result.getDate(1); // AGFDATE
			String projectCode = result.getString(2); // IDPROJECTHESSO
			String libelle     = result.getString(3); // AGF
			double amount      = result.getDouble(4); // AGFAMOUNT
			
			// Create and initialize an new instance
			AGFLine line = new AGFLine();

			line.setDate(date);
			line.setProjectCode(projectCode);
			line.setLibelle(libelle);
			line.setAmount(amount);

			agfLines.add(line);
		}
		
		return agfLines;
	}
}
