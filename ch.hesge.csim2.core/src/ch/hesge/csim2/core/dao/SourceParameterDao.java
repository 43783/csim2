package ch.hesge.csim2.core.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.SourceMethod;
import ch.hesge.csim2.core.model.SourceParameter;
import ch.hesge.csim2.core.persistence.ConnectionUtils;
import ch.hesge.csim2.core.persistence.IDataRow;
import ch.hesge.csim2.core.persistence.IParamMapper;
import ch.hesge.csim2.core.persistence.IRowMapper;
import ch.hesge.csim2.core.persistence.QueryBuilder;
import ch.hesge.csim2.core.persistence.QueryEngine;
import ch.hesge.csim2.core.utils.Console;
import ch.hesge.csim2.core.utils.StringUtils;

/**
 * Class responsible to manage DAO access for SourceParameter.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class SourceParameterDao {

	// Private static SQL queries
	private static String INSERT = "INSERT INTO source_parameters SET method_id=?methodId, name='?name', type='?type'";
	private static String UPDATE = "UPDATE source_parameters SET method_id=?methodId, name='?name', type='?type' WHERE key_id=?keyId";
	private static String DELETE = "DELETE FROM source_parameters WHERE method_id in (SELECT key_id FROM source_methods WHERE class_id in (SELECT key_id FROM source_classes WHERE project_id=?projectId))";

	private static String FIND_METHOD = "SELECT key_id, method_id, name, type FROM source_parameters WHERE method_id=?methodId ORDER BY name";
	private static String FIND_BY_PROJECT = "SELECT p.key_id, p.method_id, p.name, p.type FROM source_parameters p INNER JOIN source_methods m ON p.method_id = m.key_id INNER JOIN source_classes c ON m.class_id = c.key_id WHERE c.project_id=?projectId";
	
	/**
	 * Retrieves all source parameters owned by a method.
	 * 
	 * @param method
	 *        the method owning all parameters
	 * @return a list of parameters associated to the method or null
	 */
	public static List<SourceParameter> findByMethod(SourceMethod sourceMethod) {

		List<SourceParameter> referenceList = null;

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build query string
			String queryString = QueryBuilder.create(SourceParameterDao.FIND_METHOD, "methodId", sourceMethod.getKeyId());

			// Build the query to execute
			referenceList = QueryEngine.queryForList(connection, queryString, getRowMapper());
		}
		catch (SQLException e) {
			Console.writeError(SourceParameterDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}

		return referenceList;
	}

	/**
	 * Retrieves all source parameters owned by a project.
	 * 
	 * @param project
	 *        the project owning all parameters
	 * @return a list of parameters associated to the method or null
	 */
	public static List<SourceParameter> findByProject(Project project) {

		List<SourceParameter> referenceList = null;

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build query string
			String queryString = QueryBuilder.create(SourceParameterDao.FIND_BY_PROJECT, "projectId", project.getKeyId());

			// Build the query to execute
			referenceList = QueryEngine.queryForList(connection, queryString, getRowMapper());
		}
		catch (SQLException e) {
			Console.writeError(SourceParameterDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}

		return referenceList;
	}

	/**
	 * Add a new parameter into the database.
	 * 
	 * @param sourceParameter
	 *        the source parameter to add into the database
	 */
	public static void add(SourceParameter sourceParameter) {

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(SourceParameterDao.INSERT, getParamMapper(), sourceParameter);

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);

			// Update keyId defined by database
			sourceParameter.setKeyId(QueryEngine.queryForLastInsertedIdentifier(connection));
		}
		catch (SQLException e) {
			Console.writeError(SourceParameterDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Update a parameter to the database.
	 * 
	 * @param parameter
	 *        the source parameter that should be updated on the database
	 */
	public static void update(SourceParameter sourceParameter) {

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(SourceParameterDao.UPDATE, getParamMapper(), sourceParameter);

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);
		}
		catch (SQLException e) {
			Console.writeError(SourceParameterDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Remove all source parameters owned by a project.
	 * 
	 * @param project
	 */
	public static void deleteByProject(Project project) {

		Connection connection = ConnectionUtils.createConnection();

		try {
			// Build the query to execute
			String queryString = QueryBuilder.create(SourceParameterDao.DELETE, "projectId", project.getKeyId());

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);
		}
		catch (SQLException e) {
			Console.writeError(SourceParameterDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Declare a generic parameter mapper for model object.
	 * 
	 * This class is responsible to extract all attributes of an object and put
	 * them into a <property-name, property-value> map. This map can then be
	 * used to replace all parameters of an sql query.
	 */
	private static IParamMapper<SourceParameter> getParamMapper() {
		return new IParamMapper<SourceParameter>() {

			@Override
			public Map<String, Object> mapParameters(SourceParameter sourceParameter) {

				Map<String, Object> map = new HashMap<>();

				map.put("keyId", sourceParameter.getKeyId());
				map.put("methodId", sourceParameter.getMethodId());
				map.put("name", sourceParameter.getName());
				map.put("type", sourceParameter.getType());

				return map;
			}
		};
	}

	/**
	 * Declare a generic row mapper for model object.
	 * 
	 * This class is responsible to extract all fields from a database row
	 * (IDataRow) and to create a plain java object, with all its attributes
	 * initialized with these values. That is in one sentence, to map all row
	 * values into one single object.
	 * 
	 */
	public static IRowMapper<SourceParameter> getRowMapper() {
		return new IRowMapper<SourceParameter>() {

			@Override
			public SourceParameter mapRow(IDataRow row) {

				SourceParameter sourceParameter = new SourceParameter();

				sourceParameter.setKeyId(row.getInteger("key_id"));
				sourceParameter.setMethodId(row.getInteger("method_id"));
				sourceParameter.setName(row.getString("name"));
				sourceParameter.setType(row.getString("type"));

				return sourceParameter;
			}
		};
	}
}
