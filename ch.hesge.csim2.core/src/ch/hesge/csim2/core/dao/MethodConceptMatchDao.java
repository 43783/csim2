package ch.hesge.csim2.core.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.hesge.csim2.core.model.MethodConceptMatch;
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
 * Class responsible to manage DAO access for Trace.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class MethodConceptMatchDao {

	// Private static SQL queries
	private static String INSERT = "INSERT INTO matchings SET key_id=?keyId, project_id=?projectId, method_id=?methodId, concept_id=?conceptId, weight=?weightValue";
	private static String UPDATE = "UPDATE matchings SET key_id=?keyId, project_id=?projectId, method_id=?methodId, concept_id=?conceptIdg, weight=?weightValue WHERE key_id=?keyId";
	private static String DELETE = "DELETE FROM matchings WHERE project_id=?projectId";

	private static String FIND_BY_ID = "SELECT key_id, project_id, method_id, concept_id, weight FROM matchings WHERE key_id=?keyId";

	// Query to retrieve matching ordered by class name, method name, weight and concept name  (even if names doesn't appear in result)
	private static String FIND_BY_PROJECT = "SELECT ma.key_id, ma.project_id, ma.method_id, ma.concept_id, ma.weight, sc.name, me.name, co.name FROM matchings ma INNER JOIN source_methods me ON ma.method_id = me.key_id INNER JOIN source_classes sc ON me.class_id = sc.key_id INNER JOIN concepts co ON ma.concept_id = co.key_id WHERE sc.project_id=?projectId ORDER BY sc.name, me.name, ma.weight desc, co.name";

	/**
	 * Retrieves a match entry by its id.
	 * 
	 * @param keyId
	 *        the unique id of the MethodConceptMatch
	 * @return the match associated to the id passed in parameter or null
	 */
	public static MethodConceptMatch findById(int keyId) {

		MethodConceptMatch match = null;
		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build query string
			String queryString = QueryBuilder.create(MethodConceptMatchDao.FIND_BY_ID, "keyId", keyId);

			// Build the query to execute
			match = QueryEngine.queryForObject(connection, queryString, getRowMapper());
		}
		catch (SQLException e) {
			Console.writeError(MethodConceptMatchDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}

		return match;
	}

	/**
	 * Retrieves all matches for a project.
	 * 
	 * @param project
	 *        the project owning matches
	 * @return a list of matches or null
	 */
	public static List<MethodConceptMatch> findByProject(Project project) {

		List<MethodConceptMatch> matchList = null;
		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(MethodConceptMatchDao.FIND_BY_PROJECT, "projectId", project.getKeyId());

			// Execute the query
			matchList = QueryEngine.queryForList(connection, queryString, getRowMapper());
		}
		catch (SQLException e) {
			Console.writeError(MethodConceptMatchDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}

		return matchList;
	}

	/**
	 * Add a new match into the database.
	 * 
	 * @param match
	 *        the match to add into the database
	 */
	public static void add(MethodConceptMatch match) {

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(MethodConceptMatchDao.INSERT, getParamMapper(), match);

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);

			// Update keyId defined by database
			match.setKeyId(QueryEngine.queryForLastInsertedIdentifier(connection));
		}
		catch (SQLException e) {
			Console.writeError(MethodConceptMatchDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Update a match attributes to the database.
	 * 
	 * @param match
	 *        the match whose attributes should be updated on the database
	 */
	public static void update(MethodConceptMatch match) {

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(MethodConceptMatchDao.UPDATE, getParamMapper(), match);

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);
		}
		catch (SQLException e) {
			Console.writeError(MethodConceptMatchDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Remove all matches owned by a project.
	 * 
	 * @param scenario
	 *        the match whose MethodConceptMatch should be removed from database
	 */

	public static void deleteByProject(Project project) {

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(MethodConceptMatchDao.DELETE, "projectId", project.getKeyId());

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);
		}
		catch (SQLException e) {
			Console.writeError(MethodConceptMatchDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Declare a generic parameter mapper for MethodConceptMath object.
	 * 
	 * This class is responsible to extract all attributes of an object and put
	 * them into a <property-name, property-value> map. This map can then be
	 * used to replace all parameters of an sql query.
	 */
	private static IParamMapper<MethodConceptMatch> getParamMapper() {
		return new IParamMapper<MethodConceptMatch>() {

			@Override
			public Map<String, Object> mapParameters(MethodConceptMatch match) {

				Map<String, Object> map = new HashMap<>();

				map.put("keyId", match.getKeyId());
				map.put("projectId", match.getProjectId());
				map.put("methodId", match.getSourceMethodId());
				map.put("conceptId", match.getConceptId());
				map.put("weightValue", match.getWeight());

				return map;
			}
		};
	}

	/**
	 * Declare a generic row mapper for MethodConceptMatch object.
	 * 
	 * This class is responsible to extract all fields from a database row
	 * (IDataRow) and to create a plain java object, with all its attributes
	 * initialized with these values. That is in one sentence,
	 * to map all row values into one single object.
	 * 
	 */
	public static IRowMapper<MethodConceptMatch> getRowMapper() {
		return new IRowMapper<MethodConceptMatch>() {

			@Override
			public MethodConceptMatch mapRow(IDataRow row) {

				MethodConceptMatch match = new MethodConceptMatch();

				match.setKeyId(row.getInteger("key_id"));
				match.setProjectId(row.getInteger("project_id"));
				match.setSourceMethodId(row.getInteger("method_id"));
				match.setConceptId(row.getInteger("concept_id"));
				match.setWeight(row.getDouble("weight"));

				return match;
			}
		};
	}
}
