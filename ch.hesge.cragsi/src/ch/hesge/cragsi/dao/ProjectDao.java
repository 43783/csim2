package ch.hesge.cragsi.dao;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import ch.hesge.cragsi.loader.UserSettings;
import ch.hesge.cragsi.model.Project;
import ch.hesge.cragsi.utils.CsvReader;

/**
 * Class responsible to manage DAO access for Project.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class ProjectDao {

	/**
	 * Retrieve all projects contained in file
	 * @return
	 * @throws IOException
	 */
	public static List<Project> findAll() throws IOException {

		List<Project> projectList = new ArrayList<>();
		String projectPath = UserSettings.getInstance().getProperty("projectPath");

		CsvReader reader = new CsvReader(projectPath, ';', Charset.forName("UTF8"));
		reader.setSkipEmptyRecords(true);
		reader.readHeaders();

		while (reader.readRecord()) {

			String date = reader.get(0);
			String code = reader.get(1);
			String startDate = reader.get(2);
			String endDate = reader.get(3);
			String description = reader.get(4);
			String status = reader.get(5);

			Project project = new Project();

			project.setDate(date);
			project.setCode(code);
			project.setStartDate(startDate);
			project.setEndDate(endDate);
			project.setDescription(description);
			project.setStatus(status);

			projectList.add(project);
		}

		reader.close();

		return projectList;
	}

	public static Project findByCode(String code, List<Project> projects) {

		for (Project project : projects) {

			if (project.getCode().toLowerCase().equals(code.toLowerCase())) {
				return project;
			}
		}

		return null;
	}

}
