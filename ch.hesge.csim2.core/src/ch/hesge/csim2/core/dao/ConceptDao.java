package ch.hesge.csim2.core.dao;

import java.awt.Rectangle;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.Ontology;
import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.utils.ConnectionUtils;
import ch.hesge.csim2.core.utils.Console;
import ch.hesge.csim2.core.utils.IDataRow;
import ch.hesge.csim2.core.utils.IParamMapper;
import ch.hesge.csim2.core.utils.IRowMapper;
import ch.hesge.csim2.core.utils.QueryBuilder;
import ch.hesge.csim2.core.utils.QueryEngine;
import ch.hesge.csim2.core.utils.StringUtils;

/**
 * Class responsible to manage DAO access for Concept.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class ConceptDao {

	// Private static SQL queries
	private static String INSERT = "INSERT INTO concepts SET ontology_id=?ontologyId, name='?name', bounds='?bounds', action=?action";
	private static String UPDATE = "UPDATE concepts SET ontology_id=?ontologyId, name='?name', bounds='?bounds', action=?action WHERE key_id=?keyId";
	private static String DELETE = "DELETE FROM concepts WHERE key_id=?keyId";

	private static String FIND_BY_PROJECT  = "SELECT c.key_id, c.ontology_id, c.name, c.bounds, c.action FROM concepts c INNER JOIN ontologies o ON c.ontology_id = o.key_id INNER JOIN projects p ON o.project_id = p.key_id WHERE p.key_id = ?projectId ORDER BY c.name";
	private static String FIND_BY_ONTOLOGY = "SELECT key_id, ontology_id, name, bounds, action FROM concepts WHERE ontology_id=?ontologyId ORDER BY name";

	/**
	 * Retrieves all concepts owned by an ontology.
	 * 
	 * @param ontology
	 *            the ontology owning concepts
	 * @return a list of concepts or null
	 */
	public static List<Concept> findByOntology(Ontology ontology) {

		List<Concept> conceptList = null;
		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(ConceptDao.FIND_BY_ONTOLOGY, "ontologyId", ontology.getKeyId());

			// Execute the query
			conceptList = QueryEngine.queryForList(connection, queryString, getRowMapper());
		}
		catch (SQLException e) {
			Console.writeError(ConceptDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}

		return conceptList;
	}

	/**
	 * Retrieves all concepts owned by an ontology.
	 * 
	 * @param ontology
	 *            the ontology owning concepts
	 * @return a list of concepts or null
	 */
	public static List<Concept> findByProject(Project project) {

		List<Concept> conceptList = null;
		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(ConceptDao.FIND_BY_PROJECT, "projectId", project.getKeyId());

			// Execute the query
			conceptList = QueryEngine.queryForList(connection, queryString, getRowMapper());
		}
		catch (SQLException e) {
			Console.writeError(ConceptDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}

		return conceptList;
	}

	/**
	 * Add a new concept into the database.
	 * 
	 * @param concept
	 *            the concept to add into the database
	 */
	public static void add(Concept concept) {

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(ConceptDao.INSERT, getParamMapper(), concept);

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);

			// Update keyId defined by database
			concept.setKeyId(QueryEngine.queryForLastInsertedIdentifier(connection));
		}
		catch (SQLException e) {
			Console.writeError(ConceptDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Update a concept's attributes to the database.
	 * 
	 * @param concept
	 *            the concept whose attributes should be updated on the database
	 */
	public static void update(Concept concept) {

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(ConceptDao.UPDATE, getParamMapper(), concept);

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);
		}
		catch (SQLException e) {
			Console.writeError(ConceptDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Remove a concept from the database.
	 * 
	 * @param concept
	 *            the concept to remove from database.
	 */
	public static void delete(Concept concept) {

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(ConceptDao.DELETE, "keyId", concept.getKeyId());

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);
		}
		catch (SQLException e) {
			Console.writeError(ConceptDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Declare a generic parameter mapper for model object.
	 * 
	 * This class is responsible to extract all attributes of an object and put them into a <property-name, property-value> map. This map can then be used to replace all parameters of an sql query.
	 */
	private static IParamMapper<Concept> getParamMapper() {
		return new IParamMapper<Concept>() {

			@Override
			public Map<String, Object> mapParameters(Concept concept) {

				Map<String, Object> map = new HashMap<>();

				map.put("keyId", concept.getKeyId());
				map.put("ontologyId", concept.getOntologyId());
				map.put("name", concept.getName());

				// Fix default bounds, if missing
				if (concept.getBounds() == null) {
					concept.setBounds(new Rectangle(10, 10, 100, 40));
				}

				String boundsAsString = "";
				boundsAsString += concept.getBounds().x + ",";
				boundsAsString += concept.getBounds().y + ",";
				boundsAsString += concept.getBounds().width + ",";
				boundsAsString += concept.getBounds().height;
				map.put("bounds", boundsAsString);

				map.put("action", concept.isAction());

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
	public static IRowMapper<Concept> getRowMapper() {
		return new IRowMapper<Concept>() {

			@Override
			public Concept mapRow(IDataRow row) {

				Concept concept = new Concept();

				concept.setKeyId(row.getInteger("key_id"));
				concept.setOntologyId(row.getInteger("ontology_id"));
				concept.setName(row.getString("name"));

				String boundsAsString = row.getString("bounds");

				if (boundsAsString != null && !boundsAsString.equals("null") && boundsAsString.trim().length() > 0) {
					String[] boundsItems = boundsAsString.split(",");
					Rectangle bounds = new Rectangle();
					bounds.x = (int) Integer.valueOf(boundsItems[0]);
					bounds.y = (int) Integer.valueOf(boundsItems[1]);
					bounds.width = (int) Integer.valueOf(boundsItems[2]);
					bounds.height = (int) Integer.valueOf(boundsItems[3]);
					concept.setBounds(bounds);
				}
				else {
					concept.setBounds(new Rectangle(10, 10, 100, 40));
				}

				concept.setAction(row.getBoolean("action"));

				return concept;
			}
		};
	}
}
