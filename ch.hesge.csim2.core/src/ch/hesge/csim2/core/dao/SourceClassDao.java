package ch.hesge.csim2.core.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.SourceClass;
import ch.hesge.csim2.core.utils.ConnectionUtils;
import ch.hesge.csim2.core.utils.Console;
import ch.hesge.csim2.core.utils.IDataRow;
import ch.hesge.csim2.core.utils.IParamMapper;
import ch.hesge.csim2.core.utils.IRowMapper;
import ch.hesge.csim2.core.utils.QueryBuilder;
import ch.hesge.csim2.core.utils.QueryEngine;
import ch.hesge.csim2.core.utils.StringUtils;

/**
 * Class responsible to manage DAO access for SourceClass.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class SourceClassDao {

	// Private static SQL queries
	private static String INSERT = "INSERT INTO source_classes SET project_id=?projectId, superclass_id=?superclassId, filename='?filename', name='?name', type='?type', parent_id=?parentId";
	private static String UPDATE = "UPDATE source_classes SET project_id=?projectId, superclass_id=?superclassId, filename='?filename', name='?name', type='?type' WHERE key_id=?keyId";
	private static String DELETE = "DELETE FROM source_classes WHERE project_id=?projectId";

	private static String FIND_BY_PROJECT = "SELECT key_id, project_id, superclass_id, filename, name, type FROM source_classes WHERE project_id=?projectId ORDER BY name";
	private static String FIND_CLASS_AND_METHOD_SIGNATURES = "SELECT c.name, m.signature FROM source_methods m INNER JOIN source_classes c ON m.class_id = c.key_id WHERE project_id=?projectId";

	/**
	 * Retrieves all classes owned by a project.
	 * S
	 * @param project
	 *        the project owning classes
	 * @return a list of class or null
	 */
	public static List<SourceClass> findByProject(Project project) {

		List<SourceClass> classList = null;
		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(SourceClassDao.FIND_BY_PROJECT, "projectId", project.getKeyId());

			// Execute the query
			classList = QueryEngine.queryForList(connection, queryString, getRowMapper());
		}
		catch (SQLException e) {
			Console.writeError(SourceClassDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}

		return classList;
	}

	/**
	 * Retrieves all class names and method signatures for a specific project.
	 * The return map contains (classname, list-of-method-signature).
	 * 
	 * @param project
	 *        the project we are interested in
	 *        
	 * @return a map class/method signatures
	 */
	public static Map<String, List<String>> findClassAndMethodSignatures(Project project) {

		final Map<String, List<String>> classnamesMap = new HashMap<>();
		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(SourceClassDao.FIND_CLASS_AND_METHOD_SIGNATURES, "projectId", project.getKeyId());

			// Execute the query
			QueryEngine.queryForList(connection, queryString, new IRowMapper<Object>() {
				@Override
				public Object mapRow(IDataRow row) {

					String classname = row.getString("name");
					String methodSignature = row.getString("signature");

					// Create a new entry, if missing
					if (!classnamesMap.containsKey(classname)) {
						classnamesMap.put(classname, new ArrayList<>());
					}

					// Add the method signature to the entry
					classnamesMap.get(classname).add(methodSignature);

					return null;
				}
			});
		}
		catch (SQLException e) {
			Console.writeError(SourceClassDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}

		return classnamesMap;
	}

	/**
	 * Add a new class into the database.
	 * 
	 * @param class the source class to add into the database
	 */
	public static void add(SourceClass sourceClass) {

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(SourceClassDao.INSERT, getParamMapper(), sourceClass);

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);

			// Update keyId defined by database
			sourceClass.setKeyId(QueryEngine.queryForLastInsertedIdentifier(connection));
		}
		catch (SQLException e) {
			Console.writeError(SourceClassDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Update a source class to the database.
	 * 
	 * @param class the class whose attributes should be updated on the database
	 */
	public static void update(SourceClass sourceClass) {

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(SourceClassDao.UPDATE, getParamMapper(), sourceClass);

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);
		}
		catch (SQLException e) {
			Console.writeError(SourceClassDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Remove all source classes owned by and project.
	 * 
	 * @param project
	 */
	public static void deleteByProject(Project project) {

		Connection connection = ConnectionUtils.createConnection();

		try {
			// Build the query to execute
			String queryString = QueryBuilder.create(SourceClassDao.DELETE, "projectId", project.getKeyId());

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);
		}
		catch (SQLException e) {
			Console.writeError(SourceClassDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Declare a generic parameter mapper for model object.
	 * 
	 * This class is responsible to extract all attributes of an object and put
	 * them into a <property-name, property-value> map. This map can then be
	 * used to replace all parameters of an sql query.
	 */
	private static IParamMapper<SourceClass> getParamMapper() {
		return new IParamMapper<SourceClass>() {

			@Override
			public Map<String, Object> mapParameters(SourceClass sourceClass) {

				Map<String, Object> map = new HashMap<>();

				map.put("keyId", sourceClass.getKeyId());
				map.put("projectId", sourceClass.getProjectId());
				map.put("superclassId", sourceClass.getSuperClassId());
				map.put("filename", sourceClass.getFilename());
				map.put("name", sourceClass.getName());
				map.put("type", sourceClass.getType());

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
	public static IRowMapper<SourceClass> getRowMapper() {
		return new IRowMapper<SourceClass>() {

			@Override
			public SourceClass mapRow(IDataRow row) {

				SourceClass sourceClass = new SourceClass();

				sourceClass.setKeyId(row.getInteger("key_id"));
				sourceClass.setProjectId(row.getInteger("project_id"));
				sourceClass.setSuperClassId(row.getInteger("superclass_id"));
				sourceClass.setFilename(row.getString("filename"));
				sourceClass.setName(row.getString("name"));
				sourceClass.setType(row.getString("type"));

				return sourceClass;
			}
		};
	}
}
