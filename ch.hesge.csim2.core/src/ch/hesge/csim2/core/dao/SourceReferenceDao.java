package ch.hesge.csim2.core.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.SourceMethod;
import ch.hesge.csim2.core.model.SourceReference;
import ch.hesge.csim2.core.model.SourceReferenceOrigin;
import ch.hesge.csim2.core.persistence.ConnectionUtils;
import ch.hesge.csim2.core.persistence.IDataRow;
import ch.hesge.csim2.core.persistence.IParamMapper;
import ch.hesge.csim2.core.persistence.IRowMapper;
import ch.hesge.csim2.core.persistence.QueryBuilder;
import ch.hesge.csim2.core.persistence.QueryEngine;
import ch.hesge.csim2.core.utils.Console;
import ch.hesge.csim2.core.utils.StringUtils;

/**
 * Class responsible to manage DAO access for SourceReference.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class SourceReferenceDao {

	// Private static SQL queries
	private static String INSERT = "INSERT INTO source_references SET method_id=?methodId, name='?name', type='?type', origin=?origin";
	private static String UPDATE = "UPDATE source_references SET method_id=?methodId, name='?name', type='?type', origin=?origin WHERE key_id=?keyId";
	private static String DELETE = "DELETE FROM source_references WHERE method_id in (SELECT key_id FROM source_methods WHERE class_id in (SELECT key_id FROM source_classes WHERE project_id=?projectId))";

	private static String FIND_BY_METHOD = "SELECT key_id, method_id, name, type, origin FROM source_references WHERE method_id=?methodId ORDER BY name";
	private static String FIND_BY_PROJECT = "SELECT r.key_id, r.method_id, r.name, r.type, r.origin FROM source_references r INNER JOIN source_methods m ON r.method_id = m.key_id INNER JOIN source_classes c ON m.class_id = c.key_id WHERE c.project_id=?projectId";
	
	/**
	 * Retrieves all source references owned by a method.
	 * 
	 * @param method
	 *        the method owning all references
	 * @return a list of references associated to the method or null
	 */
	public static List<SourceReference> findByMethod(SourceMethod sourceMethod) {

		List<SourceReference> referenceList = null;

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build query string
			String queryString = QueryBuilder.create(SourceReferenceDao.FIND_BY_METHOD, "methodId", sourceMethod.getKeyId());

			// Build the query to execute
			referenceList = QueryEngine.queryForList(connection, queryString, getRowMapper());
		}
		catch (SQLException e) {
			Console.writeLine("an unexpected error has occured: " + StringUtils.toString(e));
		}

		return referenceList;
	}

	/**
	 * Retrieves all source references owned by a project.
	 * 
	 * @param project
	 *        the project owning all references
	 * @return a list of references
	 */
	public static List<SourceReference> findByProject(Project project) {

		List<SourceReference> referenceList = null;

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build query string
			String queryString = QueryBuilder.create(SourceReferenceDao.FIND_BY_PROJECT, "projectId", project.getKeyId());

			// Build the query to execute
			referenceList = QueryEngine.queryForList(connection, queryString, getRowMapper());
		}
		catch (SQLException e) {
			Console.writeLine("an unexpected error has occured: " + StringUtils.toString(e));
		}

		return referenceList;
	}

	/**
	 * Add a new reference into the database.
	 * 
	 * @param sourceReference
	 *        the source reference to add into the database
	 */
	public static void add(SourceReference sourceReference) {

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(SourceReferenceDao.INSERT, getParamMapper(), sourceReference);

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);

			// Update keyId defined by database
			sourceReference.setKeyId(QueryEngine.queryForLastInsertedIdentifier(connection));
		}
		catch (SQLException e) {
			Console.writeLine("an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Update a reference to the database.
	 * 
	 * @param reference
	 *        the source reference that should be updated on the database
	 */
	public static void update(SourceReference sourceReference) {

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(SourceReferenceDao.UPDATE, getParamMapper(), sourceReference);

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);
		}
		catch (SQLException e) {
			Console.writeLine("an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Remove all source references owned by a project.
	 * 
	 * @param project
	 */
	public static void deleteByProject(Project project) {

		Connection connection = ConnectionUtils.createConnection();

		try {
			// Build the query to execute
			String queryString = QueryBuilder.create(SourceReferenceDao.DELETE, "projectId", project.getKeyId());

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);
		}
		catch (SQLException e) {
			Console.writeLine("an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Declare a generic parameter mapper for model object.
	 * 
	 * This class is responsible to extract all attributes of an object and put
	 * them into a <property-name, property-value> map. This map can then be
	 * used to replace all parameters of an sql query.
	 */
	private static IParamMapper<SourceReference> getParamMapper() {
		return new IParamMapper<SourceReference>() {

			@Override
			public Map<String, Object> mapParameters(SourceReference sourceReference) {

				Map<String, Object> map = new HashMap<>();

				map.put("keyId", sourceReference.getKeyId());
				map.put("methodId", sourceReference.getMethodId());
				map.put("name", sourceReference.getName());
				map.put("type", sourceReference.getType());
				map.put("origin", sourceReference.getOrigin().getValue());

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
	public static IRowMapper<SourceReference> getRowMapper() {
		return new IRowMapper<SourceReference>() {

			@Override
			public SourceReference mapRow(IDataRow row) {

				SourceReference sourceReference = new SourceReference();

				sourceReference.setKeyId(row.getInteger("key_id"));
				sourceReference.setMethodId(row.getInteger("method_id"));
				sourceReference.setName(row.getString("name"));
				sourceReference.setType(row.getString("type"));
				sourceReference.setOrigin(SourceReferenceOrigin.valueOf(row.getInteger("origin")));

				return sourceReference;
			}
		};
	}
}
