package ch.hesge.cragsi.dao;

import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.hesge.cragsi.exceptions.ConfigurationException;
import ch.hesge.cragsi.model.AGFLine;
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
public class AGFLineDao {

	// Private attributes
	private static String SQLQUERY = "SELECT AGFDATE, IDPROJECTHESSO, AGF, AGFAMOUNT FROM MV2_AGF INNER JOIN MV2_PROJECT ON MV2_AGF.IDPROJECT = MV2_PROJECT.IDPROJECT WHERE IDSCHOOL=7";
	
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
		ResultSet result = ConnectionUtils.getConnection().createStatement().executeQuery(SQLQUERY);
		
		while (result.next()) {
			
			// Retrieve field values
			Date date          = result.getDate(1);
			String projectCode = result.getString(2);
			String libelle     = result.getString(3);
			double amount      = result.getDouble(4);
			
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
	
	/**
	 * Retrieve all AGF lines contained in file.
	 * 
	 * @return a list of AGFLine
	 * @throws IOException
	 * @throws ConfigurationException 
	 */
	public static List<AGFLine> findAllFromFile() throws IOException, ConfigurationException {

		CsvReader reader = null;
		List<AGFLine> agfLines = new ArrayList<>();
		String agfPath = PropertyUtils.getProperty("agfPath");

		try {

			// Open file to load
			reader = new CsvReader(agfPath, ';', Charset.forName("UTF8"));
			reader.setSkipEmptyRecords(true);
			reader.readHeaders();

			// Start parsing lines
			while (reader.readRecord()) {

				// Retrieve field values
				String date        = reader.get(0);
				String projectCode = reader.get(1);
				String libelle     = reader.get(2);
				String amount      = reader.get(3);

				// Create and initialize an new instance
				AGFLine line = new AGFLine();

				line.setDate(StringUtils.toDate(date, "yyyy-MM-dd"));
				line.setProjectCode(projectCode);
				line.setLibelle(libelle);
				line.setAmount(StringUtils.toDouble(amount));

				agfLines.add(line);
			}
		}
		finally {
			if (reader != null)
				reader.close();
		}

		return agfLines;
	}

}
