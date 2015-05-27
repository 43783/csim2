package ch.hesge.csim2.core.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.hesge.csim2.core.model.Scenario;
import ch.hesge.csim2.core.model.Trace;
import ch.hesge.csim2.core.persistence.ConnectionUtils;
import ch.hesge.csim2.core.persistence.IDataRow;
import ch.hesge.csim2.core.persistence.IParamMapper;
import ch.hesge.csim2.core.persistence.IRowMapper;
import ch.hesge.csim2.core.persistence.QueryBuilder;
import ch.hesge.csim2.core.persistence.QueryEngine;
import ch.hesge.csim2.core.utils.Console;
import ch.hesge.csim2.core.utils.StringUtils;

/**
 * Class responsible to manage DAO access for Trace.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class TraceDao {

	// Private static SQL queries
	private static String INSERT = "INSERT INTO traces SET key_id=?keyId, scenario_id=?scenarioId, sequence_number=?sequenceNumber, is_entering=?isEntering, dynamic_package='?dynamicPackage', dynamic_class='?dynamicClass', static_package='?staticPackage', static_class='?staticClass', thread_id=?threadId, signature='?signature', parameters='?parameters', return_type='?returnType', timestamp=?timestamp, duration=?duration";
	private static String UPDATE = "UPDATE traces SET key_id=?keyId, scenario_id=?scenarioId, sequence_number=?sequenceNumber, is_entering=?isEntering, dynamic_package='?dynamicPackage', dynamic_class='?dynamicClass', static_package='?staticPackage', static_class='?staticClass', thread_id=?threadId, signature='?signature', parameters='?parameters', return_type='?returnType', timestamp=?timestamp, duration=?duration WHERE key_id=?keyId";
	private static String DELETE = "DELETE FROM traces WHERE scenario_id=?scenarioId";

	private static String FIND_MIN_SEQUENCE_NUMBER = "SELECT min(t.sequence_number) FROM traces t WHERE scenario_id = ?scenarioId";
	private static String FIND_MAX_SEQUENCE_NUMBER = "SELECT max(t.sequence_number) FROM traces t WHERE scenario_id = ?scenarioId";

	private static String FIND_BY_ID = "SELECT key_id, scenario_id, sequence_number, is_entering, dynamic_package, dynamic_class, static_package, static_class, thread_id, signature, parameters, return_type, timestamp, duration FROM traces WHERE key_id=?keyId";
	private static String FIND_BY_SCENARIO = "SELECT t.key_id, t.scenario_id, t.sequence_number, t.is_entering, t.dynamic_package, t.dynamic_class, t.static_package, t.static_class, t.thread_id, t.signature, t.parameters, t.return_type, t.timestamp, t.duration, c.key_id as class_id, m.key_id as method_id FROM traces t INNER JOIN source_classes c ON t.static_class = c.name INNER JOIN source_methods m ON t.signature = m.signature WHERE scenario_id = ?scenarioId AND t.is_entering = 1";
	private static String FIND_DISTINCT_METHOD_IDS = "SELECT distinct(m.key_id) FROM traces t INNER JOIN source_classes c ON t.static_class = c.name INNER JOIN source_methods m ON t.signature = m.signature WHERE scenario_id = ?scenarioId";
	private static String FIND_METHOD_BETWEEN_SEQUENCE_NUMBER = "SELECT distinct(m.key_id) FROM traces t INNER JOIN source_classes c ON t.static_class = c.name INNER JOIN source_methods m ON t.signature = m.signature WHERE scenario_id = ?scenarioId AND sequence_number between ?startSequence AND ?endSequence";

	/**
	 * Retrieves a trace entry by its id.
	 * 
	 * @param keyId
	 *        the unique id of the Trace
	 * @return the trace associated to the id passed in parameter or null
	 */
	public static Trace findById(int keyId) {

		Trace trace = null;
		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build query string
			String queryString = QueryBuilder.create(TraceDao.FIND_BY_ID, "keyId", keyId);

			// Build the query to execute
			trace = QueryEngine.queryForObject(connection, queryString, getRowMapper());
		}
		catch (SQLException e) {
			Console.writeLine("an unexpected error has occured: " + StringUtils.toString(e));
		}

		return trace;
	}

	/**
	 * Retrieves all traces for a scenario.
	 * 
	 * @param scenario
	 *        the scenario owning traces
	 * @return a list of trace or null
	 */
	public static List<Trace> findByScenario(Scenario scenario) {

		List<Trace> traceList = null;
		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(TraceDao.FIND_BY_SCENARIO, "scenarioId", scenario.getKeyId());

			// Execute the query
			traceList = QueryEngine.queryForList(connection, queryString, getRowMapper());
		}
		catch (SQLException e) {
			Console.writeLine("an unexpected error has occured: " + StringUtils.toString(e));
		}

		return traceList;
	}

	/**
	 * Retrieves the first sequence number of all traces owned by a scenario.
	 * 
	 * @param scenario
	 *        the scenario owning traces
	 * @return the smallest sequence number
	 */
	public static int findFirstSequenceNumber(Scenario scenario) {

		int smallestSequenceNumber = -1;
		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(TraceDao.FIND_MIN_SEQUENCE_NUMBER, "scenarioId", scenario.getKeyId());

			// Execute the query
			smallestSequenceNumber = QueryEngine.queryForScalar(connection, queryString);
		}
		catch (SQLException e) {
			Console.writeLine("an unexpected error has occured: " + StringUtils.toString(e));
		}

		return smallestSequenceNumber;
	}
	
	/**
	 * Retrieves the last sequence number of all traces owned by a scenario.
	 * 
	 * @param scenario
	 *        the scenario owning traces
	 * @return the smallest sequence number
	 */
	public static int findLastSequenceNumber(Scenario scenario) {

		int greatestSequenceNumber = -1;
		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(TraceDao.FIND_MAX_SEQUENCE_NUMBER, "scenarioId", scenario.getKeyId());

			// Execute the query
			greatestSequenceNumber = QueryEngine.queryForScalar(connection, queryString);
		}
		catch (SQLException e) {
			Console.writeLine("an unexpected error has occured: " + StringUtils.toString(e));
		}

		return greatestSequenceNumber;
	}
	
	/**
	 * Find distinct method ids located in all scenario traces.
	 * 
	 * @param scenario
	 *        the scenario owning traces
	 * @return a list of method-id
	 */
	public static List<Integer> findDistinctMethodIds(Scenario scenario) {

		List<Integer> uniqueMethodIds = null;
		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(TraceDao.FIND_DISTINCT_METHOD_IDS, "scenarioId", scenario.getKeyId());

			// Execute the query
			uniqueMethodIds = QueryEngine.queryForList(connection, queryString, new IRowMapper<Integer>() {
				@Override
				public Integer mapRow(IDataRow row) {
					return (Integer) row.getFieldValue(1);
					
				}
			});
		}
		catch (SQLException e) {
			Console.writeLine("an unexpected error has occured: " + StringUtils.toString(e));
		}

		return uniqueMethodIds;
	}

		
	/**
	 * Find all mthode ids in trace between two sequence numbers.
	 * 
	 * @param scenario
	 *        the scenario owning traces
	 * @param startSequence
	 *        the starting sequence_number
	 * @param endSequence
	 *        the ending sequence_number
	 * @return a list of method-id
	 */
	public static List<Integer> findMethodIdsBetweenSequences(Scenario scenario, int startSequenceNumber, int endSequenceNumber) {

		List<Integer> uniqueMethodIds = null;
		Connection connection = ConnectionUtils.createConnection();

		try {

			// Create the query parameter map
			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("scenarioId", scenario.getKeyId());
			paramMap.put("startSequence", startSequenceNumber);
			paramMap.put("endSequence", endSequenceNumber);

			// Build the query to execute
			String queryString = QueryBuilder.create(TraceDao.FIND_METHOD_BETWEEN_SEQUENCE_NUMBER, paramMap);

			// Execute the query
			uniqueMethodIds = QueryEngine.queryForList(connection, queryString, new IRowMapper<Integer>() {
				@Override
				public Integer mapRow(IDataRow row) {
					return (Integer) row.getFieldValue(1);
					
				}
			});
		}
		catch (SQLException e) {
			Console.writeLine("an unexpected error has occured: " + StringUtils.toString(e));
		}

		return uniqueMethodIds;
	}

	
	/**
	 * Add a new trace into the database.
	 * 
	 * @param trace
	 *        the trace to add into the database
	 */
	public static void add(Trace trace) {

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(TraceDao.INSERT, getParamMapper(), trace);

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);

			// Update keyId defined by database
			trace.setKeyId(QueryEngine.queryForLastInsertedIdentifier(connection));
		}
		catch (SQLException e) {
			Console.writeLine("an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Update a trace attributes to the database.
	 * 
	 * @param trace
	 *        the trace whose attributes should be updated on the database
	 */
	public static void update(Trace trace) {

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(TraceDao.UPDATE, getParamMapper(), trace);

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);
		}
		catch (SQLException e) {
			Console.writeLine("an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Remove all trace owned by a scenario.
	 * 
	 * @param scenario
	 *        the scenario whose trace should be removed from database
	 */

	public static void deleteByScenario(Scenario scenario) {

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(TraceDao.DELETE, "scenarioId", scenario.getKeyId());

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);
		}
		catch (SQLException e) {
			Console.writeLine("an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Declare a generic parameter mapper for Trace object.
	 * 
	 * This class is responsible to extract all attributes of an object and put
	 * them into a <property-name, property-value> map. This map can then be
	 * used to replace all parameters of an sql query.
	 */
	private static IParamMapper<Trace> getParamMapper() {
		return new IParamMapper<Trace>() {

			@Override
			public Map<String, Object> mapParameters(Trace trace) {

				Map<String, Object> map = new HashMap<>();

				map.put("keyId", trace.getKeyId());
				map.put("scenarioId", trace.getScenarioId());
				map.put("sequenceNumber", trace.getSequenceNumber());
				map.put("isEntering", trace.isEnteringTrace());
				map.put("dynamicPackage", trace.getDynamicPackage());
				map.put("dynamicClass", trace.getDynamicClass());
				map.put("staticPackage", trace.getStaticPackage());
				map.put("staticClass", trace.getStaticClass());
				map.put("threadId", trace.getThreadId());
				map.put("signature", trace.getSignature());
				map.put("parameters", trace.getParameters());
				map.put("returnType", trace.getReturnType());
				map.put("timestamp", trace.getTimestamp());
				map.put("duration", trace.getDuration());

				return map;
			}
		};
	}

	/**
	 * Declare a generic row mapper for Trace object.
	 * 
	 * This class is responsible to extract all fields from a database row
	 * (IDataRow) and to create a plain java object, with all its attributes
	 * initialized with these values. That is in one sentence,
	 * to map all row values into one single object.
	 * 
	 */
	public static IRowMapper<Trace> getRowMapper() {
		return new IRowMapper<Trace>() {

			@Override
			public Trace mapRow(IDataRow row) {

				Trace trace = new Trace();

				trace.setKeyId(row.getInteger("key_id"));
				trace.setScenarioId(row.getInteger("scenario_id"));
				trace.setSequenceNumber(row.getInteger("sequence_number"));
				trace.setEnteringTrace(row.getBoolean("is_entering"));
				trace.setDynamicPackage(row.getString("dynamic_package"));
				trace.setDynamicClass(row.getString("dynamic_class"));
				trace.setStaticPackage(row.getString("static_package"));
				trace.setStaticClass(row.getString("static_class"));
				trace.setThreadId(row.getLong("threadId"));
				trace.setSignature(row.getString("signature"));
				trace.setParameters(row.getString("parameters"));
				trace.setReturnType(row.getString("return_type"));
				trace.setTimestamp(row.getLong("timestamp"));
				trace.setDuration(row.getInteger("duration"));

				// By default, MySql JDBC driver doesn't support alias defined 
				// in a standard query. So we used direct position in result to retrieve desired values:
				//    class_id  = 15
				//   method_id = 16
				//
				// However, a MYSQL workaround exists, but is really nasty:
				//
				//   jdbc-url-connection = jdbc:mysql://server:port/database?useOldAliasMetadataBehavior=true
				
				trace.setClassId((Integer)row.getFieldValue(15));
				trace.setMethodId((Integer)row.getFieldValue(16));

				return trace;
			}
		};
	}
}
