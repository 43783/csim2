/*
 * Java CSV is a stream based library for reading and writing
 * CSV and other delimited data.
 * 
 * Copyright (C) Bruce Dunwiddie bruce@csvreader.com
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package ch.hesge.cragsi.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import ch.hesge.cragsi.exceptions.ConfigurationException;

/**
 * Class responsible to manage string conversion.
 * 
 * Copyright HEG Geneva 2015, Switzerland
 * @author Eric Harth
 */
public class ConnectionUtils {

	// Singleton access
	private static Connection connection;
	
	/**
	 * Retrieve a connection to mdb file.
	 * 
	 * @return a Connection
	 * @throws ConfigurationException
	 */
	public static synchronized Connection getConnection() throws ConfigurationException {
		
		if (connection == null) {

			try {

				// Load UCanAccess driver
				Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
			
				// Retrieve path to mdb file
				String mdbPath = PropertyUtils.getProperty("mdbPath");
				
				// Create a connection
				connection = DriverManager.getConnection("jdbc:ucanaccess://" + mdbPath);
			}
			catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return connection;
	}
	
	/**
	 * Close a connection to mdb file.
	 * 
	 * @param connection
	 */
	public static synchronized void close(Connection connection) {
		
		if (connection != null) {
			try {
				connection.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	
}
