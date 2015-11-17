package ch.hesge.cragsi.loader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import ch.hesge.cragsi.utils.StringUtils;

public class TestAccessConnection {

	public TestAccessConnection() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {

		try {
			
			// Load UCanAccess driver
			Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
			
			// Connect to access file
			//Connection connection = DriverManager.getConnection("jdbc:ucanaccess://res\\CRAG-SI_Demo.accdb"); 
			Connection connection = DriverManager.getConnection("jdbc:ucanaccess://C:/DATA/Projects/cragsi/sagex/4_20151117_400_expOLAP.mdb"); 

			System.out.println("Connection succesfull");

			String query = "SELECT * FROM MV2_COLLABORATOR";
			ResultSet result = connection.createStatement().executeQuery(query);
			
			while (result.next()) {
				System.out.println(result.getString(1));
				System.out.println(result.getString(2));
				System.out.println(result.getString(3));
				System.out.println();
			}
			
			System.out.println("SELECT completed succesfull");
		}
		catch (Exception e) {
			System.err.println(StringUtils.toString(e));
		}
	}

}
