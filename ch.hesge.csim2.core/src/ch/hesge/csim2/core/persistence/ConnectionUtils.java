/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hesge.csim2.core.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import ch.hesge.csim2.core.utils.Console;
import ch.hesge.csim2.core.utils.StringUtils;

/**
 * This class provide centralized way to create a connection to the database and
 * to reuse it internally if necessary.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class ConnectionUtils {

	// Private attributes
	private static String		dbUrl;
	private static String		dbUser;
	private static String		dbPassword;
	private static Connection	dbConnection;

	/**
	 * Retrieve the url used while connecting to database.
	 * 
	 * @return the database url
	 */
	public static String getUrl() {
		return dbUrl;
	}

	/**
	 * Sets the url passed to the database driver while creation a new
	 * connection.
	 * 
	 * @param dbUrl
	 *            the connection string
	 */
	public static void setUrl(String url) {
		dbUrl = url;
	}

	/**
	 * Retrieve the username used while connecting to database.
	 * 
	 * @return the user
	 */
	public static String getUser() {
		return dbUser;
	}

	/**
	 * Sets the username passed to the database driver while creation a new
	 * connection.
	 * 
	 * @param user
	 *            the user
	 */
	public static void setUser(String user) {
		dbUser = user;
	}

	/**
	 * Retrieve the password used while connecting to database.
	 * 
	 * @return the password
	 */
	public static String getPassword() {
		return dbPassword;
	}

	/**
	 * Sets the password passed to the database driver while creation a new
	 * connection.
	 * 
	 * @param the
	 *            password
	 */
	public static void setPassword(String passwordString) {
		dbPassword = passwordString;
	}

	/**
	 * Create a new connection to the database.
	 * 
	 * @return Connection a new JDBC connection to the database
	 */
	public static synchronized Connection createConnection() {

		if (dbConnection == null) {

			try {

				// Initialize connection properties
				Properties properties = new Properties();

				if (dbUser != null) {
					properties.put("user", dbUser);
				}

				if (dbPassword != null) {
					properties.put("password", dbPassword);
				}

				// Create a new database connection
				dbConnection = DriverManager.getConnection(dbUrl, properties);

				Console.writeDebug("connection to the database '" + dbUrl + "' created.");
			}
			catch (SQLException e) {
				Console.writeError("an unexpected error has occured: " + StringUtils.toString(e));
			}
		}

		return dbConnection;
	}

	/**
	 * Close current database connection.
	 */
	public static synchronized void closeConnection() {

		if (dbConnection != null) {
			try {
				dbConnection.close();
				dbConnection = null;

				Console.writeDebug("connection to the database closed.");
			}
			catch (SQLException e) {
				Console.writeError("an unexpected error has occured: " + StringUtils.toString(e));
			}
		}
	}

	/**
	 * Close quietly all database objects passed in argument. This utility
	 * method is applicable to: ResultSet, Statement and Connection
	 * 
	 * @param objectList
	 *            the object to close
	 */
	public static void closeQuietly(Object... objectList) {

		for (Object o : objectList) {
			try {

				if (o != null) {
					if (o.getClass().isAssignableFrom(ResultSet.class)) {
						((ResultSet) o).close();
					}

					if (o.getClass().isAssignableFrom(Statement.class)) {
						((Statement) o).close();
					}
				}
			}
			catch (SQLException e) {
				// Do nothing
			}
		}
	}
}
