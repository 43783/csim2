package ch.hesge.csim2.core.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.ConceptClass;
import ch.hesge.csim2.core.persistence.ConnectionUtils;
import ch.hesge.csim2.core.persistence.IDataRow;
import ch.hesge.csim2.core.persistence.IParamMapper;
import ch.hesge.csim2.core.persistence.IRowMapper;
import ch.hesge.csim2.core.persistence.QueryBuilder;
import ch.hesge.csim2.core.persistence.QueryEngine;
import ch.hesge.csim2.core.utils.Console;
import ch.hesge.csim2.core.utils.StringUtils;

/**
 * Class responsible to manage DAO access for ConceptClass.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class ConceptClassDao {

	// Private static SQL queries
	private static String INSERT = "INSERT INTO concept_classes SET concept_id=?conceptId, name='?name', identifier='?identifier'";
	private static String UPDATE = "UPDATE concept_classes SET concept_id=?ontologyId, name='?name', identifier='?identifier' WHERE key_id=?keyId";
	private static String DELETE = "DELETE FROM concept_classes WHERE concept_id=?conceptId";

	private static String FIND_BY_CONCEPT = "SELECT key_id, concept_id, name, identifier FROM concept_classes WHERE concept_id=?conceptId ORDER BY name";

	/**
	 * Retrieves all concept classes for a concept.
	 * 
	 * @param concept
	 *            the concept owning classes
	 * @return a list of classes or null
	 */
	public static List<ConceptClass> findByConcept(Concept concept) {

		List<ConceptClass> classList = null;
		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(ConceptClassDao.FIND_BY_CONCEPT, "conceptId", concept.getKeyId());

			// Execute the query
			classList = QueryEngine.queryForList(connection, queryString, getRowMapper());
		}
		catch (SQLException e) {
			Console.writeLine("an unexpected error has occured: " + StringUtils.toString(e));
		}

		return classList;
	}

	/**
	 * Add a new concept class into the database.
	 * 
	 * @param class the concept class to add into the database
	 */
	public static void add(ConceptClass conceptClass) {

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(ConceptClassDao.INSERT, getParamMapper(), conceptClass);

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);

			// Update keyId defined by database
			conceptClass.setKeyId(QueryEngine.queryForLastInsertedIdentifier(connection));
		}
		catch (SQLException e) {
			Console.writeLine("an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Update a concept class to the database.
	 * 
	 * @param conceptClass
	 *            the concept class to update
	 */
	public static void update(ConceptClass conceptClass) {

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(ConceptClassDao.UPDATE, getParamMapper(), conceptClass);

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);
		}
		catch (SQLException e) {
			Console.writeLine("an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Remove all classes owned by a concept.
	 */
	public static void deleteByConcept(Concept concept) {

		Connection connection = ConnectionUtils.createConnection();

		try {
			// Build the query to execute
			String queryString = QueryBuilder.create(ConceptClassDao.DELETE, "conceptId", concept.getKeyId());

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
	 * This class is responsible to extract all attributes of an object and put them into a <property-name, property-value> map. This map can then be used to replace all parameters of an sql query.
	 */
	private static IParamMapper<ConceptClass> getParamMapper() {
		return new IParamMapper<ConceptClass>() {

			@Override
			public Map<String, Object> mapParameters(ConceptClass attribute) {

				Map<String, Object> map = new HashMap<>();

				map.put("keyId", attribute.getKeyId());
				map.put("conceptId", attribute.getConceptId());
				map.put("name", attribute.getName());
				map.put("identifier", attribute.getIdentifier());

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
	public static IRowMapper<ConceptClass> getRowMapper() {
		return new IRowMapper<ConceptClass>() {

			@Override
			public ConceptClass mapRow(IDataRow row) {

				ConceptClass attribute = new ConceptClass();

				attribute.setKeyId(row.getInteger("key_id"));
				attribute.setConceptId(row.getInteger("concept_id"));
				attribute.setName(row.getString("name"));
				attribute.setIdentifier(row.getString("identifier"));

				return attribute;
			}
		};
	}
}
