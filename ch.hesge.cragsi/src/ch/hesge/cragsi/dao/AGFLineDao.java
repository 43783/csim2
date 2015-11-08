package ch.hesge.cragsi.dao;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import ch.hesge.cragsi.exceptions.ConfigurationException;
import ch.hesge.cragsi.model.AGFLine;
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
public class AGFLineDao {

	/**
	 * Retrieve all AGF lines contained in file.
	 * 
	 * @return a list of AGFLine
	 * @throws IOException
	 * @throws ConfigurationException 
	 */
	public static List<AGFLine> findAll() throws IOException, ConfigurationException {

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
				String date    = reader.get(0);
				String project = reader.get(1);
				String name    = reader.get(2);
				String amount  = reader.get(3);

				// Create and initialize an new instance
				AGFLine line = new AGFLine();

				line.setDate(StringUtils.toDate(date, "yyyy-MM-dd"));
				line.setProjectNumber(project);
				line.setName(name);
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
