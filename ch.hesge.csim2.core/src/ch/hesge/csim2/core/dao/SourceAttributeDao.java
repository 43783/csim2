package ch.hesge.csim2.core.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.SourceAttribute;
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
 * Class responsible to manage DAO access for SourceAttribute.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class SourceAttributeDao {

	// Private static SQL queries
	private static String INSERT = "INSERT INTO source_attributes SET class_id=?classId, name='?name', value='?value', type='?type'";
	private static String UPDATE = "UPDATE source_attributes SET class_id=?classId, name='?name', value='?value', type='?type' WHERE key_id=?keyId";
	private static String DELETE = "DELETE FROM source_attributes WHERE class_id in (SELECT key_id FROM source_classes WHERE project_id=?projectId)";

	private static String FIND_BY_CLASS = "SELECT key_id, class_id, name, value, type FROM source_attributes WHERE class_id=?classId ORDER BY name";

	/**
	 * Retrieves all source attributes owned by an source class.
	 * 
	 * @param class the class owning attributes
	 * @return a list of attributes or null
	 */
	public static List<SourceAttribute> findByClass(SourceClass sourceClass) {

		List<SourceAttribute> attributeList = null;
		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(SourceAttributeDao.FIND_BY_CLASS, "classId", sourceClass.getKeyId());

			// Execute the query
			attributeList = QueryEngine.queryForList(connection, queryString, getRowMapper());
		}
		catch (SQLException e) {
			Console.writeError(SourceAttributeDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}

		return attributeList;
	}

	/**
	 * Add a new source attribute into the database.
	 * 
	 * @param sourceAttribute
	 *            the attribute to add into the database
	 */
	public static void add(SourceAttribute sourceAttribute) {

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(SourceAttributeDao.INSERT, getParamMapper(), sourceAttribute);

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);

			// Update keyId defined by database
			sourceAttribute.setKeyId(QueryEngine.queryForLastInsertedIdentifier(connection));
		}
		catch (SQLException e) {
			Console.writeError(SourceAttributeDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Update a source attribute to the database.
	 * 
	 * @param attribute
	 *            the attribute that should be updated on the database
	 */
	public static void update(SourceAttribute attribute) {

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(SourceAttributeDao.UPDATE, getParamMapper(), attribute);

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);
		}
		catch (SQLException e) {
			Console.writeError(SourceAttributeDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Remove all source attributes owned by a project.
	 * 
	 * @param project
	 */
	public static void deleteByProject(Project project) {

		Connection connection = ConnectionUtils.createConnection();

		try {
			// Build the query to execute
			String queryString = QueryBuilder.create(SourceAttributeDao.DELETE, "projectId", project.getKeyId());

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);
		}
		catch (SQLException e) {
			Console.writeError(SourceAttributeDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Declare a generic parameter mapper for mode object.
	 * 
	 * This class is responsible to extract all attributes of an object and put them into a <property-name, property-value> map. This map can then be used to replace all parameters of an sql query.
	 */
	private static IParamMapper<SourceAttribute> getParamMapper() {
		return new IParamMapper<SourceAttribute>() {

			@Override
			public Map<String, Object> mapParameters(SourceAttribute sourceAttribute) {

				Map<String, Object> map = new HashMap<>();

				map.put("keyId", sourceAttribute.getKeyId());
				map.put("classId", sourceAttribute.getClassId());
				map.put("name", sourceAttribute.getName());
				map.put("value", sourceAttribute.getValue());
				map.put("type", sourceAttribute.getType());

				return map;
			}
		};
	}

	/**
	 * Declare a generic row mapper for model object.
	 * 
	 * This class is responsible to extract all fields from a database row (IDataRow) and to create a plain java object, with all its attributes initialized with these values. That is in one sentence,
	 * to map all row values into one single object.
	 * 
	 */
	public static IRowMapper<SourceAttribute> getRowMapper() {
		return new IRowMapper<SourceAttribute>() {

			@Override
			public SourceAttribute mapRow(IDataRow row) {

				SourceAttribute sourceAttribute = new SourceAttribute();

				sourceAttribute.setKeyId(row.getInteger("key_id"));
				sourceAttribute.setClassId(row.getInteger("class_id"));
				sourceAttribute.setName(row.getString("name"));
				sourceAttribute.setValue(row.getString("value"));
				sourceAttribute.setType(row.getString("type"));

				return sourceAttribute;
			}
		};
	}
}
