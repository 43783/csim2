package ch.hesge.cragsi.dao;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import ch.hesge.cragsi.exceptions.ConfigurationException;
import ch.hesge.cragsi.model.Project;
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
public class ProjectDao {

	/**
	 * Retrieve all projects contained in file.
	 * 
	 * @return a list of Project
	 * @throws IOException
	 * @throws ConfigurationException 
	 */
	public static List<Project> findAll() throws IOException, ConfigurationException {

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
				String date        = reader.get(0);
				String code        = reader.get(1);
				String startDate   = reader.get(2);
				String endDate     = reader.get(3);
				String description = reader.get(4);
				String status      = reader.get(5);

				// Create and initialize an new instance
				Project project = new Project();

				project.setDate(StringUtils.toDate(date, "yyyy-MM-dd"));
				project.setCode(code);
				project.setStartDate(StringUtils.toDate(startDate, "yyyy-MM-dd"));
				project.setEndDate(StringUtils.toDate(endDate, "yyyy-MM-dd"));
				project.setDescription(description);
				project.setStatus(status);

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
