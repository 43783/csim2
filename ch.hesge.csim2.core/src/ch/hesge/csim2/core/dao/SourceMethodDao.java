package ch.hesge.csim2.core.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.SourceClass;
import ch.hesge.csim2.core.model.SourceMethod;
import ch.hesge.csim2.core.persistence.ConnectionUtils;
import ch.hesge.csim2.core.persistence.IDataRow;
import ch.hesge.csim2.core.persistence.IParamMapper;
import ch.hesge.csim2.core.persistence.IRowMapper;
import ch.hesge.csim2.core.persistence.QueryBuilder;
import ch.hesge.csim2.core.persistence.QueryEngine;
import ch.hesge.csim2.core.utils.Console;
import ch.hesge.csim2.core.utils.StringUtils;

/**
 * Class responsible to manage DAO access for SourceMethod.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class SourceMethodDao {

	// Private static SQL queries
	private static String INSERT = "INSERT INTO source_methods SET filename='?filename', name='?name', class_id=?classId, signature='?signature', return_type='?returnType'";
	private static String UPDATE = "UPDATE source_methods SET filename='?filename', name='?name', class_id=?classId, signature='?signature', return_type='?returnType' WHERE key_id=?keyId";
	private static String DELETE = "DELETE FROM source_methods WHERE class_id in (SELECT key_id FROM source_classes WHERE project_id=?projectId)";

	private static String FIND_BY_CLASS = "SELECT key_id, filename, name, class_id, signature, return_type FROM source_methods WHERE class_id=?classId ORDER BY name";
	private static String FIND_BY_PROJECT = "SELECT m.key_id, m.filename, m.name, m.class_id, m.signature, m.return_type FROM source_methods m INNER JOIN source_classes c ON m.class_id = c.key_id WHERE c.project_id=?projectId ORDER BY m.signature";
	private static String FIND_BY_CLASS_AND_SIGNATURE = "SELECT * FROM source_methods WHERE class_id = (SELECT key_id FROM source_classes WHERE project_id=?projectId AND name = '?classname') AND signature = '?signature'";
			
	/**
	 * Retrieves all source methods owned by an source class.
	 * 
	 * @param class the class owning methods
	 * @return a list of methods or null
	 */
	public static List<SourceMethod> findByClass(SourceClass sourceClass) {

		List<SourceMethod> methodList = null;
		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(SourceMethodDao.FIND_BY_CLASS, "classId", sourceClass.getKeyId());

			// Execute the query
			methodList = QueryEngine.queryForList(connection, queryString, getRowMapper());
		}
		catch (SQLException e) {
			Console.writeLine("an unexpected error has occured: " + StringUtils.toString(e));
		}

		return methodList;
	}

	/**
	 * Retrieves all methods owned by a project.
	 * 
	 * @param project the project owning methods
	 * @return a list of methods or null
	 */
	public static List<SourceMethod> findByProject(Project project) {

		List<SourceMethod> methodList = null;
		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(SourceMethodDao.FIND_BY_PROJECT, "projectId", project.getKeyId());

			// Execute the query
			methodList = QueryEngine.queryForList(connection, queryString, getRowMapper());
		}
		catch (SQLException e) {
			Console.writeLine("an unexpected error has occured: " + StringUtils.toString(e));
		}

		return methodList;
	}

	/**
	 * Retrieve a method by its signature.
	 * 
	 * @param classname
	 *        the name of the owning class
	 * @param signature
	 *        the signature of the method to find
	 * @return a source method
	 */
	public static SourceMethod findByClassAndSignature(Project project, String classname, String signature) {

		SourceMethod sourceMethod = null;
		Connection connection = ConnectionUtils.createConnection();

		try {

			Map<String, Object> parametersMap = new HashMap<>();
			parametersMap.put("projectId", project.getKeyId());
			parametersMap.put("classname", classname);
			parametersMap.put("signature", signature);

			// Build the query to execute
			String queryString = QueryBuilder.create(SourceMethodDao.FIND_BY_CLASS_AND_SIGNATURE, parametersMap);

			// Execute the query
			sourceMethod = QueryEngine.queryForObject(connection, queryString, getRowMapper());
		}
		catch (SQLException e) {
			Console.writeLine("an unexpected error has occured: " + StringUtils.toString(e));
		}

		return sourceMethod;
	}

	/**
	 * Add a new source method into the database.
	 * 
	 * @param method
	 *        the method to add into the database
	 */
	public static void add(SourceMethod sourceMethod) {

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(SourceMethodDao.INSERT, getParamMapper(), sourceMethod);

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);

			// Update keyId defined by database
			sourceMethod.setKeyId(QueryEngine.queryForLastInsertedIdentifier(connection));
		}
		catch (SQLException e) {
			Console.writeLine("an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Update a source method to the database.
	 * 
	 * @param method
	 *        the method that should be updated on the database
	 */
	public static void update(SourceMethod sourceMethod) {

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(SourceMethodDao.UPDATE, getParamMapper(), sourceMethod);

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);
		}
		catch (SQLException e) {
			Console.writeLine("an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Remove all source methods owned by a project.
	 * 
	 * @param project
	 */
	public static void deleteByProject(Project project) {

		Connection connection = ConnectionUtils.createConnection();

		try {
			// Build the query to execute
			String queryString = QueryBuilder.create(SourceMethodDao.DELETE, "projectId", project.getKeyId());

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
	private static IParamMapper<SourceMethod> getParamMapper() {
		return new IParamMapper<SourceMethod>() {

			@Override
			public Map<String, Object> mapParameters(SourceMethod sourceMethod) {

				Map<String, Object> map = new HashMap<>();

				map.put("keyId", sourceMethod.getKeyId());
				map.put("filename", sourceMethod.getFilename());
				map.put("name", sourceMethod.getName());
				map.put("classId", sourceMethod.getClassId());
				map.put("signature", sourceMethod.getSignature());
				map.put("returnType", sourceMethod.getReturnType());

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
	public static IRowMapper<SourceMethod> getRowMapper() {
		return new IRowMapper<SourceMethod>() {

			@Override
			public SourceMethod mapRow(IDataRow row) {

				SourceMethod sourceMethod = new SourceMethod();

				sourceMethod.setKeyId(row.getInteger("key_id"));
				sourceMethod.setFilename(row.getString("filename"));
				sourceMethod.setName(row.getString("name"));
				sourceMethod.setClassId(row.getInteger("class_id"));
				sourceMethod.setSignature(row.getString("signature"));
				sourceMethod.setReturnType(row.getString("return_type"));

				return sourceMethod;
			}
		};
	}
}
