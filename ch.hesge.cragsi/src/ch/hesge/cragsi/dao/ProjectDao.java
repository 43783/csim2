package ch.hesge.cragsi.dao;

import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.hesge.cragsi.exceptions.ConfigurationException;
import ch.hesge.cragsi.model.Project;
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
public class ProjectDao {

	// Private attributes
	private static String SQLQUERY = "SELECT IDPROJECTHESSO, PROJECTENVSTARTDATE, PROJECTENVENDDATE, PROJECTTITLE FROM MV2_PROJECT WHERE IDSCHOOL=7";

	/**
	 * Retrieve all projects from database.
	 * 
	 * @return a list of Project
	 * @throws SQLException
	 * @throws ConfigurationException 
	 */
	public static List<Project> findAll() throws SQLException, ConfigurationException {
		
		List<Project> projectList = new ArrayList<>();
		
		// Execute the query
		ResultSet result = ConnectionUtils.getConnection().createStatement().executeQuery(SQLQUERY);
		
		while (result.next()) {

			// Retrieve field values
			String code    = result.getString(1);
			Date startDate = result.getDate(2);
			Date endDate   = result.getDate(3);
			String descr   = result.getString(4);

			// Create and initialize an new instance
			Project project = new Project();

			project.setCode(code);
			project.setStartDate(startDate);
			project.setEndDate(endDate);
			project.setDescription(descr);

			projectList.add(project);		
		}
		
		return projectList;
	}
	
	/**
	 * Retrieve all projects contained in file.
	 * 
	 * @return a list of Project
	 * @throws IOException
	 * @throws ConfigurationException 
	 */
	public static List<Project> findAllFromFile() throws IOException, ConfigurationException {

		CsvReader reader = null;
		List<Project> projectList = new ArrayList<>();
		String projectPath = PropertyUtils.getProperty("projectPath");

		try {

			// Open file to load
			reader = new CsvReader(projectPath, ';', Charset.forName("UTF8"));
			reader.setSkipEmptyRecords(true);
			reader.readHeaders();

			// Start parsing projects
			while (reader.readRecord()) {

				// Retrieve field values
				String code        = reader.get(1);
				String startDate   = reader.get(2);
				String endDate     = reader.get(3);
				String description = reader.get(4);

				// Create and initialize an new instance
				Project project = new Project();

				project.setCode(code);
				project.setStartDate(StringUtils.toDate(startDate, "yyyy-MM-dd"));
				project.setEndDate(StringUtils.toDate(endDate, "yyyy-MM-dd"));
				project.setDescription(description);

				projectList.add(project);
			}
		}
		finally {
			if (reader != null) 
				reader.close();
		}

		return projectList;
	}

}
