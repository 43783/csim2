package ch.hesge.csim2.core.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.Ontology;
import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.StemConcept;
import ch.hesge.csim2.core.model.StemConceptType;
import ch.hesge.csim2.core.persistence.ConnectionUtils;
import ch.hesge.csim2.core.persistence.IDataRow;
import ch.hesge.csim2.core.persistence.IParamMapper;
import ch.hesge.csim2.core.persistence.IRowMapper;
import ch.hesge.csim2.core.persistence.QueryBuilder;
import ch.hesge.csim2.core.persistence.QueryEngine;
import ch.hesge.csim2.core.utils.Console;
import ch.hesge.csim2.core.utils.StringUtils;

/**
 * Class responsible to manage DAO access for StemConcept.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class StemConceptDao {

	// Private static SQL queries
	private static String INSERT = "INSERT INTO stem_concepts SET project_id=?projectId, parent_id=?parentId, concept_id=?conceptId, term='?term', stem_type=?stemType";
	private static String UPDATE = "UPDATE stem_concepts SET project_id=?projectId, parent_id=?parentId, concept_id=?conceptId, term='?term', stem_type=?stemType WHERE key_id=?keyId";
	private static String DELETE = "DELETE FROM stem_concepts WHERE concept_id in (SELECT key_id FROM concepts WHERE ontology_id=?ontologyId)";

	private static String FIND_BY_CONCEPT = "SELECT key_id, project_id, parent_id, concept_id, term, stem_type FROM stem_concepts WHERE concept_id=?conceptId";
	private static String FIND_BY_PROJECT = "SELECT key_id, project_id, parent_id, concept_id, term, stem_type FROM stem_concepts WHERE project_id=?projectId";

	
	/**
	 * Retrieves all stem owned by a concept.
	 * 
	 * @return a list of stem or null
	 */
	public static List<StemConcept> findByConcept(Concept concept) {

		List<StemConcept> stemList = null;
		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(StemConceptDao.FIND_BY_CONCEPT, "conceptId", concept.getKeyId());

			// Execute the query
			stemList = QueryEngine.queryForList(connection, queryString, getRowMapper());
		}
		catch (SQLException e) {
			Console.writeLine("an unexpected error has occured: " + StringUtils.toString(e));
		}

		return stemList;
	}

	/**
	 * Retrieves all stem owned by a project.
	 * 
	 * @param project
	 *            the project owning stems
	 * @return a list of stem or null
	 */
	public static List<StemConcept> findByProject(Project project) {

		List<StemConcept> stemList = null;
		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(StemConceptDao.FIND_BY_PROJECT, "projectId", project.getKeyId());

			// Execute the query
			stemList = QueryEngine.queryForList(connection, queryString, getRowMapper());
		}
		catch (SQLException e) {
			Console.writeLine("an unexpected error has occured: " + StringUtils.toString(e));
		}

		return stemList;
	}

	/**
	 * Add a new stem concept into the database.
	 * 
	 * @param stemConcept
	 *            the stem concept to add into the database
	 */
	public static void add(StemConcept stemConcept) {

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(StemConceptDao.INSERT, getParamMapper(), stemConcept);

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);

			// Update keyId defined by database
			stemConcept.setKeyId(QueryEngine.queryForLastInsertedIdentifier(connection));
		}
		catch (SQLException e) {
			Console.writeLine("an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Update a stem concept attributes to the database.
	 * 
	 * @param stemConcept
	 *            the stem concept whose attributes should be updated on the database
	 */
	public static void update(StemConcept stemConcept) {

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(StemConceptDao.UPDATE, getParamMapper(), stemConcept);

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);
		}
		catch (SQLException e) {
			Console.writeLine("an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Remove all stem concepts associated to an ontology.
	 * 
	 * @param ontology
	 *            the ontology owning all term concepts
	 */
	public static void deleteByOntology(Ontology ontology) {

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(StemConceptDao.DELETE, "ontologyId", ontology.getKeyId());

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
	private static IParamMapper<StemConcept> getParamMapper() {
		return new IParamMapper<StemConcept>() {

			@Override
			public Map<String, Object> mapParameters(StemConcept stemConcept) {

				Map<String, Object> map = new HashMap<>();

				map.put("keyId", stemConcept.getKeyId());
				map.put("projectId", stemConcept.getProjectId());
				map.put("parentId", stemConcept.getParentId());
				map.put("conceptId", stemConcept.getConceptId());
				map.put("term", stemConcept.getTerm());
				map.put("stemType", stemConcept.getStemType().getValue());

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
	public static IRowMapper<StemConcept> getRowMapper() {
		return new IRowMapper<StemConcept>() {

			@Override
			public StemConcept mapRow(IDataRow row) {

				StemConcept stemConcept = new StemConcept();

				stemConcept.setKeyId(row.getInteger("key_id"));
				stemConcept.setProjectId(row.getInteger("project_id"));
				stemConcept.setParentId(row.getInteger("parent_id"));
				stemConcept.setConceptId(row.getInteger("concept_id"));
				stemConcept.setTerm(row.getString("term"));

				int stemType = row.getInteger("stem_type");
				stemConcept.setStemType(stemType == -1 ? null : StemConceptType.valueOf(stemType));

				return stemConcept;
			}
		};
	}
}
