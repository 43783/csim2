package ch.hesge.csim2.core.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.hesge.csim2.core.model.Scenario;
import ch.hesge.csim2.core.model.ScenarioStep;
import ch.hesge.csim2.core.persistence.ConnectionUtils;
import ch.hesge.csim2.core.persistence.IDataRow;
import ch.hesge.csim2.core.persistence.IParamMapper;
import ch.hesge.csim2.core.persistence.IRowMapper;
import ch.hesge.csim2.core.persistence.QueryBuilder;
import ch.hesge.csim2.core.persistence.QueryEngine;
import ch.hesge.csim2.core.utils.Console;
import ch.hesge.csim2.core.utils.StringUtils;

/**
 * Class responsible to manage DAO access for ScenarioStep.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class ScenarioStepDao {

	// Private static SQL queries
	private static String INSERT = "INSERT INTO scenario_steps SET scenario_id=?scenarioId, execution_time=?executionTime, name='?name', description='?description'";
	private static String UPDATE = "UPDATE scenario_steps SET scenario_id=?scenarioId, execution_time=?executionTime, name='?name', description='?description' WHERE key_id=?keyId";
	private static String DELETE = "DELETE FROM scenario_steps WHERE key_id=?keyId";

	private static String FIND_BY_NAME = "SELECT key_id, scenario_id, execution_time, name, description FROM scenario_steps WHERE scenario_id=?scenarioId AND name='?name'";
	private static String FIND_BY_SCENARIO = "SELECT key_id, scenario_id, execution_time, name, description FROM scenario_steps WHERE scenario_id=?scenarioId ORDER BY scenario_id";

	/**
	 * Retrieves all steps owned by a scenario.
	 * 
	 * @param scenario
	 *            the scenario owning steps
	 * @return a list of ScenarioStep or null
	 */
	public static List<ScenarioStep> findByScenario(Scenario scenario) {

		List<ScenarioStep> scenarioSteps = null;
		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(ScenarioStepDao.FIND_BY_SCENARIO, "scenarioId", scenario.getKeyId());

			// Execute the query
			scenarioSteps = QueryEngine.queryForList(connection, queryString, getRowMapper());
		}
		catch (SQLException e) {
			Console.writeError(ScenarioStepDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}

		return scenarioSteps;
	}

	/**
	 * Retrieves all steps owned by a scenario.
	 * 
	 * @param scenario
	 *            the scenario owning steps
	 * @return a ScenarioStep or null
	 */
	public static ScenarioStep findByName(Scenario scenario, String name) {

		ScenarioStep scenarioStep = null;
		Connection connection = ConnectionUtils.createConnection();

		try {

			// Create query parameters
			HashMap<String, Object> paramMap = new HashMap<>();
			paramMap.put("scenarioId", scenario.getKeyId());
			paramMap.put("name", name);

			// Build the query to execute
			String queryString = QueryBuilder.create(ScenarioStepDao.FIND_BY_NAME, paramMap);

			// Execute the query
			scenarioStep = QueryEngine.queryForObject(connection, queryString, getRowMapper());
		}
		catch (SQLException e) {
			Console.writeError(ScenarioStepDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}

		return scenarioStep;
	}

	/**
	 * Add a new scenario-step into the database.
	 * 
	 * @param step
	 *            the ScenarioStep to add into the database
	 */
	public static void add(ScenarioStep step) {

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(ScenarioStepDao.INSERT, getParamMapper(), step);

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);

			// Update keyId defined by database
			step.setKeyId(QueryEngine.queryForLastInsertedIdentifier(connection));
		}
		catch (SQLException e) {
			Console.writeError(ScenarioStepDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Update a step's attributes to the database.
	 * 
	 * @param step
	 *            the ScenarioStep whose attributes should be updated on the database
	 */
	public static void update(ScenarioStep step) {

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(ScenarioStepDao.UPDATE, getParamMapper(), step);

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);
		}
		catch (SQLException e) {
			Console.writeError(ScenarioStepDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Remove an scenario step from the database.
	 * 
	 * @param scenario
	 *            the scenario owning all step to remove from database
	 */
	public static void deleteByScenario(Scenario scenario) {

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(ScenarioStepDao.DELETE, "keyId", scenario.getKeyId());

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);
		}
		catch (SQLException e) {
			Console.writeError(ScenarioStepDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Declare a generic parameter mapper for ScenarioStep object.
	 * 
	 * This class is responsible to extract all attributes of an object and put them into a <property-name, property-value> map. This map can then be used to replace all parameters of an sql query.
	 */
	private static IParamMapper<ScenarioStep> getParamMapper() {
		return new IParamMapper<ScenarioStep>() {

			@Override
			public Map<String, Object> mapParameters(ScenarioStep step) {

				Map<String, Object> map = new HashMap<>();

				map.put("keyId", step.getKeyId());
				map.put("scenarioId", step.getScenarioId());
				map.put("executionTime", step.getExecutionTime());
				map.put("name", step.getName());
				map.put("description", step.getDescription());

				return map;
			}
		};
	}

	/**
	 * Declare a generic row mapper for ScenarioStep object.
	 * 
	 * This class is responsible to extract all fields from a database row (IDataRow) and to create a plain java object, with all its attributes initialized with these values. That is in one sentence,
	 * to map all row values into one single object.
	 * 
	 */
	public static IRowMapper<ScenarioStep> getRowMapper() {
		return new IRowMapper<ScenarioStep>() {

			@Override
			public ScenarioStep mapRow(IDataRow row) {

				ScenarioStep step = new ScenarioStep();

				step.setKeyId(row.getInteger("key_id"));
				step.setScenarioId(row.getInteger("scenario_id"));
				step.setExecutionTime(row.getLong("execution_time"));
				step.setName(row.getString("name"));
				step.setDescription(row.getString("description"));

				return step;
			}
		};
	}
}
