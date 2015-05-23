package ch.hesge.csim2.core.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import ch.hesge.csim2.core.persistence.ConnectionUtils;
import ch.hesge.csim2.core.persistence.IDataRow;
import ch.hesge.csim2.core.persistence.QueryBuilder;
import ch.hesge.csim2.core.persistence.QueryEngine;
import ch.hesge.csim2.core.utils.Console;
import ch.hesge.csim2.core.utils.StringUtils;

/**
 * Class responsible to get/update application settings
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class SettingsDao {

	// Private static SQL queries
	private static String UPDATE = "UPDATE settings SET value='?value'  WHERE name='?value'";
	private static String INSERT = "INSERT INTO settings (name, value) VALUES ('?name', '?value')";

	private static String FIND_BY_NAME = "SELECT value FROM settings WHERE name='?name'";
	private static String FIND_ALL = "SELECT name, value FROM settings";

	/**
	 * Retrieves all steps owned by a usecase.
	 * 
	 * @param usecase
	 *            the usecase owning steps
	 * @return a list of UsecaseStep or null
	 */
	public static Properties findAll() {

		Properties properties = new Properties();
		Connection connection = ConnectionUtils.createConnection();

		try {

			// Execute the query
			List<IDataRow> dataRows = QueryEngine.queryForRows(connection, SettingsDao.FIND_ALL);

			for (IDataRow row : dataRows) {

				String name = row.getString("name");
				String value = row.getString("value");
				properties.setProperty(name, value);
			}
		}
		catch (SQLException e) {
			Console.writeLine("an unexpected error has occured: " + StringUtils.toString(e));
		}

		return properties;
	}

	/**
	 * Retrieve a setting's value.
	 * 
	 * @return value the value associated to the setting's name as a string
	 * @throws SQLException
	 */
	public static String findValue(String name) {

		String settingValue = null;
		Connection connection = ConnectionUtils.createConnection();

		try {

			// Build the query to execute
			String queryString = QueryBuilder.create(SettingsDao.FIND_BY_NAME, "name", name);

			// Execute the query
			settingValue = QueryEngine.queryForScalar(connection, queryString);

		}
		catch (SQLException e) {
			Console.writeLine("an unexpected error has occured: " + StringUtils.toString(e));
		}

		return settingValue;
	}

	/**
	 * Update a setting's value.
	 * 
	 * @param name
	 *            the name of the setting
	 * @param value
	 *            the new value of the setting
	 * @throws SQLException
	 */
	public static void update(String name, String value) {

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Create a parameter's map
			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("name", name);
			paramMap.put("value", value);

			// Build the query to execute
			String queryString = QueryBuilder.create(UPDATE, paramMap);

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);

		}
		catch (SQLException e) {
			Console.writeLine("an unexpected error has occured: " + StringUtils.toString(e));
		}
	}

	/**
	 * Add a new object into the database.
	 * 
	 * @param datarow
	 *            the object's attributes
	 * @throws SQLException
	 *             if an exception is thrown
	 */
	public static void createFromRow(IDataRow datarow) {

		Connection connection = ConnectionUtils.createConnection();

		try {

			// Create a parameter's map
			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("name", datarow.getString("name"));
			paramMap.put("value", datarow.getString("value"));

			// Build the query to execute
			String queryString = QueryBuilder.create(INSERT, paramMap);

			// Execute the query
			QueryEngine.executeQuery(connection, queryString);

		}
		catch (SQLException e) {
			Console.writeLine("an unexpected error has occured: " + StringUtils.toString(e));
		}
	}
}
