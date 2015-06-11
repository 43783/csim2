package ch.hesge.csim2.core.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.hesge.csim2.core.model.Ontology;
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
 * Class responsible to manage DAO access for Ontology.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class OntologyDao {

	// Private static SQL queries
	private static String INSERT = "INSERT INTO ontologies SET project_id=?projectId, name='?name'";
	private static String UPDATE = "UPDATE ontologies SET project_id=?projectId, name='?name' WHERE key_id=?keyId";
	private static String DELETE = "DELETE FROM ontologies WHERE key_id=?keyId";

	private static String FIND_ALL   = "SELECT key_id, project_id, name FROM ontologies";
	private static String FIND_BY_PROJECT = "SELECT key_id, project_id, name FROM ontologies WHERE project_id=?projectId ORDER BY name";
	private static String FIND_BY_NAME = "SELECT key_id, project_id, name FROM ontologies WHERE name='?name'";

	/**
	 * Retrieves all ontologies stored in the database.
	 * 
	 * @return a list of ontology or null
	 */
	public static List<Ontology> findAll() {

		List<Ontology> ontologyList = null;
		Connection connection = ConnectionUtils.createConnection();

		try {
			// Build the query to execute
			String queryString = OntologyDao.FIND_ALL;

			// Execute the query
			ontologyList = QueryEngine.queryForList(connection, queryString, getRowMapper());
		}
		catch (SQLException e) {
			Console.writeError(OntologyDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}

		return ontologyList;
	}

	/**
	 * Retrieves an ontology by its name.
	 * 
	 * @param name
	 *        the name of the ontology
	 * @return the ontology associated or null
	 */
	public static Ontology findByName(String name) {

		Ontology ontology = null;
		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build query string
			String queryString = QueryBuilder.create(OntologyDao.FIND_BY_NAME, "name", name);

			// Execute the query
			ontology = QueryEngine.queryForObject(connection, queryString, getRowMapper());
		}
		catch (SQLException e) {
			Console.writeError(OntologyDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}

		return ontology;
	}

	/**
	 * Retrieves all ontologies owned by a project.
	 * 
	 * @param project
	 *        the project owning ontologies
	 * @return a list of ontologies or null
	 */
	public static List<Ontology> findByProject(Project project) {

		List<Ontology> ontologyList = null;
		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(OntologyDao.FIND_BY_PROJECT, "projectId", project.getKeyId());

			// Execute the query
			ontologyList = QueryEngine.queryForList(connection, queryString, getRowMapper());
		}
		catch (SQLException e) {
			Console.writeError(OntologyDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}

		return ontologyList;
	}

	/**
	 * Add a new ontology into the database.
	 * 
	 * @param ontology
	 *        the ontology to add into the database
	 */
	public static void add(Ontology ontology) {

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(OntologyDao.INSERT, getParamMapper(), ontology);

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);

			// Update keyId defined by database
			ontology.setKeyId(QueryEngine.queryForLastInsertedIdentifier(connection));
		}
		catch (SQLException e) {
			Console.writeError(OntologyDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Update a ontology's attributes to the database.
	 * 
	 * @param ontology
	 *        the ontology whose attributes should be updated on the
	 *        database
	 */
	public static void update(Ontology ontology) {

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(OntologyDao.UPDATE, getParamMapper(), ontology);

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);
		}
		catch (SQLException e) {
			Console.writeError(OntologyDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Remove an ontology from the database.
	 * 
	 * @param keyId
	 *        the ontology to remove from database
	 */
	public static void delete(Ontology ontology) {

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(OntologyDao.DELETE, "keyId", ontology.getKeyId());

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);
		}
		catch (SQLException e) {
			Console.writeError(OntologyDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Declare a generic parameter mapper for model object.
	 * 
	 * This class is responsible to extract all attributes of an object and put
	 * them into a <property-name, property-value> map. This map can then be
	 * used to replace all parameters of an sql query.
	 */
	private static IParamMapper<Ontology> getParamMapper() {
		return new IParamMapper<Ontology>() {

			@Override
			public Map<String, Object> mapParameters(Ontology ontology) {

				Map<String, Object> map = new HashMap<>();

				map.put("keyId", ontology.getKeyId());
				map.put("projectId", ontology.getProjectId());
				map.put("name", ontology.getName());

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
	public static IRowMapper<Ontology> getRowMapper() {
		return new IRowMapper<Ontology>() {

			@Override
			public Ontology mapRow(IDataRow row) {

				Ontology ontology = new Ontology();

				ontology.setKeyId(row.getInteger("key_id"));
				ontology.setProjectId(row.getInteger("project_id"));
				ontology.setName(row.getString("name"));

				return ontology;
			}
		};
	}
}
