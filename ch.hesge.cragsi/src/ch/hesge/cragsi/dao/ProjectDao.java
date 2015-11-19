package ch.hesge.cragsi.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.hesge.cragsi.exceptions.ConfigurationException;
import ch.hesge.cragsi.model.Project;
import ch.hesge.cragsi.utils.ConnectionUtils;
import ch.hesge.cragsi.utils.PropertyUtils;

/**
 * Class responsible to manage physical access to underlying
 * files and to load them in memory.
 * 
 * Copyright HEG Geneva 2015, Switzerland
 * @author Eric Harth
 */
public class ProjectDao {

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
		String query = PropertyUtils.getProperty("PROJECT_QUERY");
		ResultSet result = ConnectionUtils.getConnection().createStatement().executeQuery(query);
		
		while (result.next()) {

			// Retrieve field values
			String code    = result.getString(1); // IDPROJECTHESSO
			Date startDate = result.getDate(2); // PROJECTENVSTARTDATE
			Date endDate   = result.getDate(3); // PROJECTENVENDDATE
			String descr   = result.getString(4); // PROJECTTITLE
			
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
}
