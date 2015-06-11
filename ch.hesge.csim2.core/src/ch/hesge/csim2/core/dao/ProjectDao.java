package ch.hesge.csim2.core.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.persistence.ConnectionUtils;
import ch.hesge.csim2.core.persistence.IDataRow;
import ch.hesge.csim2.core.persistence.IParamMapper;
import ch.hesge.csim2.core.persistence.IRowMapper;
import ch.hesge.csim2.core.persistence.QueryBuilder;
import ch.hesge.csim2.core.persistence.QueryEngine;
import ch.hesge.csim2.core.utils.Console;
import ch.hesge.csim2.core.utils.StringUtils;

/**
 * Class responsible to manage DAO access for Project.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class ProjectDao {

	// Private static SQL queries
	private static String INSERT = "INSERT INTO projects SET name='?name'";
	private static String UPDATE = "UPDATE projects SET name='?name' WHERE key_id=?keyId";
	private static String DELETE = "DELETE FROM projects WHERE key_id=?keyId";

	private static String FIND_ALL   = "SELECT key_id, name FROM projects ORDER BY name";
	private static String FIND_BY_NAME = "SELECT key_id, name FROM projects WHERE name LIKE '?name'";

	/**
	 * Retrieves all projects stored in the database.
	 * 
	 * @return a list of project or null
	 */
	public static List<Project> findAll() {

		List<Project> projectList = null;
		Connection connection = ConnectionUtils.createConnection();

		try {
			// Build the query to execute
			String queryString = ProjectDao.FIND_ALL;

			// Execute the query
			projectList = QueryEngine.queryForList(connection, queryString, getRowMapper());
		}
		catch (SQLException e) {
			Console.writeError(ProjectDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}

		return projectList;
	}

	/**
	 * Retrieves a project from its name.
	 * 
	 * @param name
	 *        the name of the project
	 * @return the project associated to the name or null
	 */
	public static Project findByName(String name) {

		Project project = null;
		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build query string
			String queryString = QueryBuilder.create(ProjectDao.FIND_BY_NAME, "name", name);

			// Execute the query
			project = QueryEngine.queryForObject(connection, queryString, getRowMapper());
		}
		catch (SQLException e) {
			Console.writeError(ProjectDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}

		return project;
	}

	/**
	 * Add a new project into the database.
	 * 
	 * @param project
	 *        the project to add into the database
	 */
	public static void add(Project project) {

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(ProjectDao.INSERT, getParamMapper(), project);

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);

			// Update keyId defined by database
			project.setKeyId(QueryEngine.queryForLastInsertedIdentifier(connection));
		}
		catch (SQLException e) {
			Console.writeError(ProjectDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Update a project's attributes to the database.
	 * 
	 * @param project
	 *        the project whose attributes should be updated on the database
	 */
	public static void update(Project project) {

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(ProjectDao.UPDATE, getParamMapper(), project);

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);
		}
		catch (SQLException e) {
			Console.writeError(ProjectDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Remove a project from the database.
	 * 
	 * @param project
	 *        the project to remove from database
	 */
	public static void delete(Project project) {

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(ProjectDao.DELETE, "keyId", project.getKeyId());

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);
		}
		catch (SQLException e) {
			Console.writeError(ProjectDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Return a generic parameter mapper for the model object.
	 * 
	 * This class is responsible to extract all attributes of an object and put
	 * them into a <property-name, property-value> map. This map can then be
	 * used to replace all parameters of an sql query.
	 */
	private static IParamMapper<Project> getParamMapper() {
		return new IParamMapper<Project>() {

			@Override
			public Map<String, Object> mapParameters(Project project) {

				Map<String, Object> map = new HashMap<>();

				map.put("keyId", project.getKeyId());
				map.put("name", project.getName());

				return map;
			}
		};
	}

	/**
	 * Return a generic row mapper for the model object.
	 * 
	 * This class is responsible to extract all fields from a database row
	 * (IDataRow) and to create a plain java object, with all its attributes
	 * initialized with these values. That is in one sentence, to map all row
	 * values into one single object.
	 * 
	 */
	public static IRowMapper<Project> getRowMapper() {
		return new IRowMapper<Project>() {

			@Override
			public Project mapRow(IDataRow row) {

				Project project = new Project();

				project.setKeyId(row.getInteger("key_id"));
				project.setName(row.getString("name"));

				return project;
			}
		};
	}
}
