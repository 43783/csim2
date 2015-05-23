package ch.hesge.csim2.core.persistence;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * This class implements various helper allowing query execution and mapping to
 * JavaBean objects.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class QueryEngine {

	/**
	 * Execute a standard SQL query (for instance UPDATE), without the need to
	 * map the resulting rows to be mapped to object.
	 * 
	 * @param connection
	 *            the connection to the database
	 * @param queryString
	 *            the SQL query to execute
	 * @return the row count modified by the query or -1
	 * @throws SQLException
	 */
	public static int executeQuery(Connection connection, String queryString) throws SQLException {
		int result = -1;
		Statement statement = null;

		try {
			statement = connection.createStatement();
			result = statement.executeUpdate(queryString);
		}
		finally {
			ConnectionUtils.closeQuietly(statement);
		}

		return result;
	}

	/**
	 * Execute a SELECT query and retrieve the result as a scalar (number).
	 * 
	 * @param connection
	 *            the connection to the database
	 * @param queryString
	 *            the SQL query to execute
	 * @return a scalar (int, double or float)
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T queryForScalar(Connection connection, String queryString) throws SQLException {

		T result = null;

		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(queryString)) {

			if (resultSet.next()) {
				result = (T) resultSet.getObject(1);
			}
		}

		return result;
	}

	/**
	 * Execute a SELECT query and use the rowMapper passed in argument, to map
	 * row values into a single object.
	 * 
	 * @param connection
	 *            the connection to the database
	 * @param queryString
	 *            the SQL query to execute
	 * @param rowMapper
	 *            the mapper used to extract values from row
	 * @return the object with field extracted from row values
	 * @throws SQLException
	 */
	public static <T> T queryForObject(Connection connection, String queryString, IRowMapper<T> rowMapper) throws SQLException {

		T result = null;
		Statement statement = null;
		ResultSet resultSet = null;

		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(queryString);

			if (resultSet.next()) {

				// Extract all row information from result-set
				IDataRow row = convertToDataRow(resultSet);

				// Convert the row into proper object
				result = rowMapper.mapRow(row);
			}

		}
		finally {
			ConnectionUtils.closeQuietly(resultSet, statement);
		}

		return result;
	}

	/**
	 * Execute a query to retrieve last inserted object identifier defined by
	 * the database.
	 * 
	 * @param connection
	 *            the connection to the database
	 * @return the identifier for the object just inserted in database
	 * @throws SQLException
	 */
	public static int queryForLastInsertedIdentifier(Connection connection) throws SQLException {

		int lastInsertedId = -1;

		BigInteger lastIdentifier = queryForScalar(connection, "SELECT LAST_INSERT_ID()");

		if (lastIdentifier != null) {
			lastInsertedId = lastIdentifier.intValue();
		}

		return lastInsertedId;
	}

	/**
	 * Execute a SELECT query and use the rowMapper passed in argument, to map
	 * all row values into a list of objects.
	 * 
	 * @param connection
	 *            the connection to the database
	 * @param queryString
	 *            the SQL query to execute
	 * @param rowMapper
	 *            the mapper used to extract values from row
	 * @return a list of object with field values extracted from row values
	 * @throws SQLException
	 */
	public static <T> List<T> queryForList(Connection connection, String queryString, IRowMapper<T> rowMapper) throws SQLException {

		List<T> result = new ArrayList<>();

		Statement statement = null;
		ResultSet resultSet = null;

		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(queryString);

			while (resultSet.next()) {

				// Extract all row information from result-set
				IDataRow row = convertToDataRow(resultSet);

				// Convert each row into proper object
				result.add(rowMapper.mapRow(row));
			}

		}
		finally {
			ConnectionUtils.closeQuietly(resultSet, statement);
		}

		return result;
	}

	/**
	 * Execute a SELECT query and return rough values coming from database
	 * result set.
	 * 
	 * @param connection
	 *            the connection to the database
	 * @param queryString
	 *            the SQL query to execute
	 * @return an list of IDataRow containing all values of all rows returned
	 * @throws SQLException
	 */
	public static List<IDataRow> queryForRows(Connection connection, String queryString) throws SQLException {

		List<IDataRow> dataRows = new ArrayList<>();

		Statement statement = null;
		ResultSet resultSet = null;

		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(queryString);

			while (resultSet.next()) {

				// Extract all row information from result-set
				IDataRow row = convertToDataRow(resultSet);

				// Convert each row into proper object
				dataRows.add(row);
			}

		}
		finally {
			ConnectionUtils.closeQuietly(resultSet, statement);
		}

		return dataRows;
	}

	/**
	 * Convert a database result-set into and DataRow (kind of map of
	 * field/value).
	 * 
	 * @param resultSet
	 *            the result-set to convert
	 * @return the DataRow built
	 * @throws SQLException
	 */
	private static IDataRow convertToDataRow(ResultSet resultSet) throws SQLException {

		DataRow dataRow = new DataRow();

		// Extract all row information from resultset
		for (int i = 1; i < resultSet.getMetaData().getColumnCount() + 1; i++) {
			String fieldName = resultSet.getMetaData().getColumnLabel(i);
			Object fieldValue = resultSet.getObject(i);
			dataRow.put(i, fieldName, fieldValue);
		}

		return dataRow;
	}
}
