package ch.hesge.csim2.core.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.ConceptAttribute;
import ch.hesge.csim2.core.persistence.ConnectionUtils;
import ch.hesge.csim2.core.persistence.IDataRow;
import ch.hesge.csim2.core.persistence.IParamMapper;
import ch.hesge.csim2.core.persistence.IRowMapper;
import ch.hesge.csim2.core.persistence.QueryBuilder;
import ch.hesge.csim2.core.persistence.QueryEngine;
import ch.hesge.csim2.core.utils.Console;
import ch.hesge.csim2.core.utils.StringUtils;

/**
 * Class responsible to manage DAO access for ConceptAttribute.
 * 
 * Copyright HEG Geneva 2014, Switzerland.
 * 
 * @author Eric Harth
 */

public class ConceptAttributeDao {

	// Private static SQL queries
	private static String INSERT = "INSERT INTO concept_attributes SET concept_id=?conceptId, name='?name', identifier='?identifier'";
	private static String UPDATE = "UPDATE concept_attributes SET concept_id=?ontologyId, name='?name', identifier='?identifier' WHERE key_id=?keyId";
	private static String DELETE = "DELETE FROM concept_attributes WHERE concept_id=?conceptId";

	private static String FIND_BY_CONCEPT = "SELECT key_id, concept_id, name, identifier FROM concept_attributes WHERE concept_id=?conceptId ORDER BY name";

	/**
	 * Retrieves all concept attributes.
	 * 
	 * @param concept
	 *            the concept owning attributes
	 * @return a list of attributes or null
	 */
	public static List<ConceptAttribute> findByConcept(Concept concept) {

		List<ConceptAttribute> attributeList = null;
		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(ConceptAttributeDao.FIND_BY_CONCEPT, "conceptId", concept.getKeyId());

			// Execute the query
			attributeList = QueryEngine.queryForList(connection, queryString, getRowMapper());
		}
		catch (SQLException e) {
			Console.writeError(ConceptAttributeDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}

		return attributeList;
	}

	/**
	 * Add a new concept attribute into the database.
	 * 
	 * @param conceptAttribute
	 *            the concept attribute to add into the database
	 */
	public static void add(ConceptAttribute conceptAttribute) {

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(ConceptAttributeDao.INSERT, getParamMapper(), conceptAttribute);

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);

			// Update keyId defined by database
			conceptAttribute.setKeyId(QueryEngine.queryForLastInsertedIdentifier(connection));
		}
		catch (SQLException e) {
			Console.writeError(ConceptAttributeDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Update a concept attribute to the database.
	 * 
	 * @param conceptAttribute
	 *            the concept attribute to update
	 */
	public static void update(ConceptAttribute conceptAttribute) {

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(ConceptAttributeDao.UPDATE, getParamMapper(), conceptAttribute);

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);
		}
		catch (SQLException e) {
			Console.writeError(ConceptAttributeDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Remove all attributes owned by a concept.
	 */
	public static void deleteByConcept(Concept concept) {

		Connection connection = ConnectionUtils.createConnection();

		try {
			// Build the query to execute
			String queryString = QueryBuilder.create(ConceptAttributeDao.DELETE, "conceptId", concept.getKeyId());

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);
		}
		catch (SQLException e) {
			Console.writeError(ConceptAttributeDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Declare a generic parameter mapper for model object.
	 * 
	 * This class is responsible to extract all attributes of an object and put them into a <property-name, property-value> map. This map can then be used to replace all parameters of an sql query.
	 */
	private static IParamMapper<ConceptAttribute> getParamMapper() {
		return new IParamMapper<ConceptAttribute>() {

			@Override
			public Map<String, Object> mapParameters(ConceptAttribute attribute) {

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
	public static IRowMapper<ConceptAttribute> getRowMapper() {
		return new IRowMapper<ConceptAttribute>() {

			@Override
			public ConceptAttribute mapRow(IDataRow row) {

				ConceptAttribute attribute = new ConceptAttribute();

				attribute.setKeyId(row.getInteger("key_id"));
				attribute.setConceptId(row.getInteger("concept_id"));
				attribute.setName(row.getString("name"));
				attribute.setIdentifier(row.getString("identifier"));

				return attribute;
			}
		};
	}
}
