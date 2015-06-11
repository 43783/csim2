package ch.hesge.csim2.core.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.Scenario;
import ch.hesge.csim2.core.persistence.ConnectionUtils;
import ch.hesge.csim2.core.persistence.IDataRow;
import ch.hesge.csim2.core.persistence.IParamMapper;
import ch.hesge.csim2.core.persistence.IRowMapper;
import ch.hesge.csim2.core.persistence.QueryBuilder;
import ch.hesge.csim2.core.persistence.QueryEngine;
import ch.hesge.csim2.core.utils.Console;
import ch.hesge.csim2.core.utils.StringUtils;

/**
 * Class responsible to manage DAO access for Scenario.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class ScenarioDao {

	// Private static SQL queries
	private static String INSERT = "INSERT INTO scenarios SET project_id=?projectId, name='?name'";
	private static String UPDATE = "UPDATE scenarios SET project_id=?projectId, name='?name' WHERE key_id=?keyId";
	private static String DELETE = "DELETE FROM scenarios WHERE key_id=?keyId";

	private static String FIND_ALL = "SELECT key_id, project_id, name FROM scenarios";
	private static String FIND_BY_NAME = "SELECT key_id, project_id, name FROM scenarios WHERE project_id=?projectId AND name='?name'";
	private static String FIND_BY_PROJECT = "SELECT key_id, project_id, name FROM scenarios WHERE project_id=?projectId ORDER BY name";

	/**
	 * Retrieves all scenarios stored in the database.
	 * 
	 * @return a list of scenario or null
	 */
	public static List<Scenario> findAll() {

		List<Scenario> scenarioList = null;
		Connection connection = ConnectionUtils.createConnection();

		try {
			// Build the query to execute
			String queryString = ScenarioDao.FIND_ALL;

			// Execute the query
			scenarioList = QueryEngine.queryForList(connection, queryString, getRowMapper());
		}
		catch (SQLException e) {
			Console.writeError(ScenarioDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}

		return scenarioList;
	}

	/**
	 * Retrieves a scenario by its name.
	 * 
	 * @param project
	 *            the project owning scenario
	 * @param name
	 *            the name of the scenario
	 * @return the scenario associated to the name or null
	 */
	public static Scenario findByName(Project project, String name) {

		Scenario scenario = null;
		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build query string
			String queryString = QueryBuilder.create(ScenarioDao.FIND_BY_NAME, "name", name);

			// Execute the querys
			scenario = QueryEngine.queryForObject(connection, queryString, getRowMapper());
		}
		catch (SQLException e) {
			Console.writeError(ScenarioDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}

		return scenario;
	}

	/**
	 * Retrieves all scenarios owned by a project.
	 * 
	 * @param project
	 *            the project owning scenarios
	 * @return a list of scenario or null
	 */
	public static List<Scenario> findByProject(Project project) {

		List<Scenario> scenarioList = null;
		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build query string
			String queryString = QueryBuilder.create(ScenarioDao.FIND_BY_PROJECT, "projectId", project.getKeyId());

			// Execute the query
			scenarioList = QueryEngine.queryForList(connection, queryString, getRowMapper());
		}
		catch (SQLException e) {
			Console.writeError(ScenarioDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}

		return scenarioList;
	}

	/**
	 * Add a new scenario into the database.
	 * 
	 * @param scenario
	 *            the scenario to add into the database
	 */
	public static void add(Scenario scenario) {

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(ScenarioDao.INSERT, getParamMapper(), scenario);

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);

			// Update keyId defined by database
			scenario.setKeyId(QueryEngine.queryForLastInsertedIdentifier(connection));
		}
		catch (SQLException e) {
			Console.writeError(ScenarioDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Update a scenario's attributes to the database.
	 * 
	 * @param scenario
	 *            the scenario whose attributes should be updated on the database
	 */
	public static void update(Scenario scenario) {

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(ScenarioDao.UPDATE, getParamMapper(), scenario);

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);
		}
		catch (SQLException e) {
			Console.writeError(ScenarioDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Remove an scenario from the database.
	 * 
	 * @param keyId
	 *            the scenario to remove from database
	 */
	public static void delete(Scenario scenario) {

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(ScenarioDao.DELETE, "keyId", scenario.getKeyId());

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);
		}
		catch (SQLException e) {
			Console.writeError(ScenarioDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Declare a generic parameter mapper for model object.
	 * 
	 * This class is responsible to extract all attributes of an object and put them into a <property-name, property-value> map. This map can then be used to replace all parameters of an sql query.
	 */
	private static IParamMapper<Scenario> getParamMapper() {
		return new IParamMapper<Scenario>() {

			@Override
			public Map<String, Object> mapParameters(Scenario scenario) {

				Map<String, Object> map = new HashMap<>();

				map.put("keyId", scenario.getKeyId());
				map.put("projectId", scenario.getProjectId());
				map.put("name", scenario.getName());

				return map;
			}
		};
	}

	/**
	 * Declare a generic row mapper for modelF object.
	 * 
	 * This class is responsible to extract all fields from a database row (IDataRow) and to create a plain java object, with all its attributes initialized with these values. That is in one sentence,
	 * to map all row values into one single object.
	 * 
	 */
	public static IRowMapper<Scenario> getRowMapper() {
		return new IRowMapper<Scenario>() {

			@Override
			public Scenario mapRow(IDataRow row) {

				Scenario scenario = new Scenario();

				scenario.setKeyId(row.getInteger("key_id"));
				scenario.setProjectId(row.getInteger("project_id"));
				scenario.setName(row.getString("name"));

				return scenario;
			}
		};
	}
}
