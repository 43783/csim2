package ch.hesge.csim2.core.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.SourceMethod;
import ch.hesge.csim2.core.model.SourceReferenceOrigin;
import ch.hesge.csim2.core.model.StemMethod;
import ch.hesge.csim2.core.model.StemMethodType;
import ch.hesge.csim2.core.persistence.ConnectionUtils;
import ch.hesge.csim2.core.persistence.IDataRow;
import ch.hesge.csim2.core.persistence.IParamMapper;
import ch.hesge.csim2.core.persistence.IRowMapper;
import ch.hesge.csim2.core.persistence.QueryBuilder;
import ch.hesge.csim2.core.persistence.QueryEngine;
import ch.hesge.csim2.core.utils.Console;
import ch.hesge.csim2.core.utils.StringUtils;

/**
 * Class responsible to manage DAO access for StemMethod.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class StemMethodDao {

	// Private static SQL queries
	private static String INSERT = "INSERT INTO stem_methods SET project_id=?projectId, parent_id=?parentId, method_id=?methodId, term='?term', stem_type=?stemType, ref_origin=?refOrigin";
	private static String UPDATE = "UPDATE stem_methods SET project_id=?projectId, parent_id=?parentId, method_id=?methodId, term='?term', stem_type=?stemType, ref_origin=?refOrigin WHERE key_id=?keyId";
	private static String DELETE = "DELETE FROM stem_methods WHERE method_id in (SELECT key_id FROM source_methods WHERE class_id in (SELECT key_id FROM source_classes WHERE project_id=?projectId))";

	private static String FIND_BY_METHOD  = "SELECT key_id, project_id, parent_id, method_id, term, stem_type, ref_origin FROM stem_methods WHERE method_id=?methodId";
	private static String FIND_BY_PROJECT = "SELECT key_id, project_id, parent_id, method_id, term, stem_type, ref_origin FROM stem_methods WHERE project_id=?projectId";

	/**
	 * Retrieves all stem owned by a method.
	 * 
	 * @param method
	 *        the method owning stems
	 * @return a list of stem or null
	 */
	public static List<StemMethod> findByMethod(SourceMethod method) {

		List<StemMethod> stemList = null;
		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(StemMethodDao.FIND_BY_METHOD, "methodId", method.getKeyId());

			// Execute the query
			stemList = QueryEngine.queryForList(connection, queryString, getRowMapper());
		}
		catch (SQLException e) {
			Console.writeError(StemMethodDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}

		return stemList;
	}

	/**
	 * Retrieves all stem owned by a project.
	 * 
	 * @param project
	 *        the project owning stems
	 * @return a list of stem or null
	 */
	public static List<StemMethod> findByProject(Project project) {

		List<StemMethod> stemList = null;
		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(StemMethodDao.FIND_BY_PROJECT, "projectId", project.getKeyId());

			// Execute the query
			stemList = QueryEngine.queryForList(connection, queryString, getRowMapper());
		}
		catch (SQLException e) {
			Console.writeError(StemMethodDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}

		return stemList;
	}

	/**
	 * Add a new stem method into the database.
	 * 
	 * @param stemMethod
	 *        the stem method to add into the database
	 */
	public static void add(StemMethod stemMethod) {

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(StemMethodDao.INSERT, getParamMapper(), stemMethod);

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);

			// Update keyId defined by database
			stemMethod.setKeyId(QueryEngine.queryForLastInsertedIdentifier(connection));
		}
		catch (SQLException e) {
			Console.writeError(StemMethodDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Update a stem method attributes to the database.
	 * 
	 * @param stemMethod
	 *        the stem method whose attributes should be updated on the database
	 */
	public static void update(StemMethod stemMethod) {

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(StemMethodDao.UPDATE, getParamMapper(), stemMethod);

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);
		}
		catch (SQLException e) {
			Console.writeError(StemMethodDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Remove all stem methods associated to a project.
	 * 
	 * @param project
	 *        the project owning all stem methods
	 */
	public static void deleteByProject(Project project) {

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(StemMethodDao.DELETE, "projectId", project.getKeyId());

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);
		}
		catch (SQLException e) {
			Console.writeError(StemMethodDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Declare a generic parameter mapper for model object.
	 * 
	 * This class is responsible to extract all attributes of an object and put
	 * them into a <property-name, property-value> map. This map can then be
	 * used to replace all parameters of an sql query.
	 */
	private static IParamMapper<StemMethod> getParamMapper() {
		return new IParamMapper<StemMethod>() {

			@Override
			public Map<String, Object> mapParameters(StemMethod stemMethod) {

				Map<String, Object> map = new HashMap<>();

				map.put("keyId", stemMethod.getKeyId());
				map.put("projectId", stemMethod.getProjectId());
				map.put("parentId", stemMethod.getParentId());
				map.put("methodId", stemMethod.getSourceMethodId());
				map.put("term", stemMethod.getTerm());
				map.put("stemType", stemMethod.getStemType().getValue());
				map.put("refOrigin", stemMethod.getRefOrigin() == null ? -1 : stemMethod.getRefOrigin().getValue());
				map.put("weight", stemMethod.getWeight());

				return map;
			}
		};
	}

	/**
	 * Declare a generic row mapper for model object.
	 * 
	 * This class is responsible to extract all fields from a database row
	 * (IDataRow) and to create a plain java object, with all its attributes
	 * initialized with these values. That is in one sentence,
	 * to map all row values into one single object.
	 * 
	 */
	public static IRowMapper<StemMethod> getRowMapper() {
		return new IRowMapper<StemMethod>() {

			@Override
			public StemMethod mapRow(IDataRow row) {

				StemMethod stemMethod = new StemMethod();

				stemMethod.setKeyId(row.getInteger("key_id"));
				stemMethod.setProjectId(row.getInteger("project_id"));
				stemMethod.setParentId(row.getInteger("parent_id"));
				stemMethod.setSourceMethodId(row.getInteger("method_id"));
				stemMethod.setTerm(row.getString("term"));

				int stemType = row.getInteger("stem_type");
				stemMethod.setStemType(stemType == -1 ? null : StemMethodType.valueOf(stemType));

				int refOrigin = row.getInteger("ref_origin");
				stemMethod.setRefOrigin(refOrigin == -1 ? null : SourceReferenceOrigin.valueOf(refOrigin));

				stemMethod.setWeight(row.getDouble("weight"));

				return stemMethod;
			}
		};
	}
}
