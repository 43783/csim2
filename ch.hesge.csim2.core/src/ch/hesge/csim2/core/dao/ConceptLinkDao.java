package ch.hesge.csim2.core.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.ConceptLink;
import ch.hesge.csim2.core.persistence.ConnectionUtils;
import ch.hesge.csim2.core.persistence.IDataRow;
import ch.hesge.csim2.core.persistence.IParamMapper;
import ch.hesge.csim2.core.persistence.IRowMapper;
import ch.hesge.csim2.core.persistence.QueryBuilder;
import ch.hesge.csim2.core.persistence.QueryEngine;
import ch.hesge.csim2.core.utils.Console;
import ch.hesge.csim2.core.utils.StringUtils;

/**
 * Class responsible to manage DAO access for ConceptLink.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class ConceptLinkDao {

	// Private static SQL queries
	private static String INSERT = "INSERT INTO concept_links SET source_id=?sourceId, target_id=?targetId, qualifier='?qualifier'";
	private static String DELETE = "DELETE FROM concept_links WHERE source_id=?conceptId OR target_id=?conceptId";

	private static String FIND_BY_CONCEPT = "SELECT source_id, target_id, qualifier FROM concept_links WHERE source_id=?sourceId";

	/**
	 * Retrieves all links owned by a concept. In other words return all links starting from a specific concept.
	 * 
	 * @param concept
	 *            the source concept owning links
	 * @return a list of ConceptLink or null
	 */
	public static List<ConceptLink> findByConcept(Concept concept) {

		List<ConceptLink> links = null;
		Connection connection = ConnectionUtils.createConnection();

		String queryString = null;
		try {

			// Build query string
			/* String */queryString = QueryBuilder.create(ConceptLinkDao.FIND_BY_CONCEPT, "sourceId", concept.getKeyId());

			// Build the query to execute
			links = QueryEngine.queryForList(connection, queryString, getRowMapper());
		}
		catch (SQLException e) {
			Console.writeError(ConceptLinkDao.class, "an unexpected error has occured: " + StringUtils.toString(e) + " - " + queryString);
		}

		return links;
	}

	/**
	 * Add a new link bewteen two concepts into the database.
	 * 
	 * @param link
	 *            the ConceptLink to add into the database
	 */
	public static void add(ConceptLink link) {

		Connection connection = ConnectionUtils.createConnection();

		String queryString = null;

		try {

			// Build the query to execute
			/* String */queryString = QueryBuilder.create(ConceptLinkDao.INSERT, getParamMapper(), link);

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);
		}
		catch (SQLException e) {
			Console.writeError(ConceptLinkDao.class, "an unexpected error has occured: " + StringUtils.toString(e) + " - " + queryString);
		}
	}

	/**
	 * Remove all link whose source or target is the concept passed in argument.
	 * 
	 * @param concept
	 *            the concept as source or target that links should be removed
	 */
	public static void deleteByConcept(Concept concept) {

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Remove all links with the concept as the source or target
			String queryString = QueryBuilder.create(ConceptLinkDao.DELETE, "conceptId", concept.getKeyId());

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);
		}
		catch (SQLException e) {
			Console.writeError(ConceptLinkDao.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Declare a generic parameter mapper for model object.
	 * 
	 * This class is responsible to extract all attributes of an object and put them into a <property-name, property-value> map. This map can then be used to replace all parameters of an sql query.
	 */
	private static IParamMapper<ConceptLink> getParamMapper() {
		return new IParamMapper<ConceptLink>() {

			@Override
			public Map<String, Object> mapParameters(ConceptLink link) {

				Map<String, Object> map = new HashMap<>();

				map.put("sourceId", link.getSourceId());
				map.put("targetId", link.getTargetId());
				map.put("qualifier", link.getQualifier());

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
	public static IRowMapper<ConceptLink> getRowMapper() {
		return new IRowMapper<ConceptLink>() {

			@Override
			public ConceptLink mapRow(IDataRow row) {

				ConceptLink link = new ConceptLink();

				link.setSourceId(row.getInteger("source_id"));
				link.setTargetId(row.getInteger("target_id"));

				String qualifier = row.getString("qualifier");

				if (qualifier == null || qualifier.equals("null")) {
					link.setQualifier(null);
				}
				else {
					link.setQualifier(qualifier);
				}

				return link;
			}
		};
	}
}
