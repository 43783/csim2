package com.kenai.javaatm;

// Fig. 25.28: ResultSetTableModel.java
    // A TableModel that supplies ResultSet data to a JTable.
    import java.sql.Connection;
    import java.sql.Statement;
    import java.sql.DriverManager;
    import java.sql.ResultSet;
    import java.sql.ResultSetMetaData;
    import java.sql.SQLException;
    import javax.swing.table.AbstractTableModel;
   // ResultSet rows and columns are counted from 1 and JTable
   // rows and columns are counted from 0. When processing
   // ResultSet rows or columns for use in a JTable, it is
   // necessary to add 1 to the row or column number to manipulate
   // the appropriate ResultSet column (i.e., JTable column 0 is
   // ResultSet column 1 and JTable row 0 is ResultSet row 1).
   public class ResultSetTableModel extends AbstractTableModel
   {
      /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Connection connection;
      private Statement statement;
      private ResultSet resultSet;
      private ResultSetMetaData metaData;
      private int numberOfRows;
      
 
      // keep track of database connection status 
      private boolean connectedToDatabase = false;
 
      // constructor initializes resultSet and obtains its meta data object;
      // determines number of rows
      public ResultSetTableModel( String driver, String url,
         String username, String password, String query )
         throws SQLException, ClassNotFoundException
      {
         try {
			ch.hesge.csim2.engine.TraceLogger
					.entering(
							"com.kenai.javaatm",
							"ResultSetTableModel#" + this.hashCode(),
							"com.kenai.javaatm",
							"ResultSetTableModel#" + this.hashCode(),
							"ResultSetTableModel",
							"String driver,String url,String username,String password,String query",
							"void");
			// load database driver class
			 Class.forName( driver );
			// connect to database
			 connection = DriverManager.getConnection( url, username, password );
			// create Statement to query database   
			 statement = connection.createStatement( 
			    ResultSet.TYPE_SCROLL_INSENSITIVE,   
			    ResultSet.CONCUR_READ_ONLY );
			// update database connection status
			 connectedToDatabase = true;
			// set query and execute it
			 setQuery( query );
		} finally {
			ch.hesge.csim2.engine.TraceLogger
					.exiting(
							"com.kenai.javaatm",
							"ResultSetTableModel#" + this.hashCode(),
							"com.kenai.javaatm",
							"ResultSetTableModel#" + this.hashCode(),
							"ResultSetTableModel",
							"String driver,String url,String username,String password,String query",
							"void");
		}
      } // end constructor ResultSetTableModel
 
      // get class that represents column type
    @Override
      public Class getColumnClass( int column ) throws IllegalStateException
      {
         
try {
			ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
					"ResultSetTableModel#" + this.hashCode(),
					"com.kenai.javaatm",
					"ResultSetTableModel#" + this.hashCode(), "getColumnClass",
					"int column", "Class");
			// ensure database connection is available                        
			 if ( !connectedToDatabase )                                       
			    throw new IllegalStateException( "Not Connected to Database" );
			// determine Java class of column
			 try
			 {
			    String className = metaData.getColumnClassName( column + 1 );
			    // return Class object that represents className
			    return Class.forName( className );              
			 } // end try
			 catch ( Exception exception )
			 {
			    exception.printStackTrace();
			 } // end catch
			return Object. class; // if problems occur above, assume type Object
		} finally {
			ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
					"ResultSetTableModel#" + this.hashCode(),
					"com.kenai.javaatm",
					"ResultSetTableModel#" + this.hashCode(), "getColumnClass",
					"int column", "Class");
		}
      } // end method getColumnClass
 
      // get number of columns in ResultSet
      public int getColumnCount() throws IllegalStateException
      {
         
try {
			ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
					"ResultSetTableModel#" + this.hashCode(),
					"com.kenai.javaatm",
					"ResultSetTableModel#" + this.hashCode(), "getColumnCount",
					"void", "int");
			// ensure database connection is available
			 if ( !connectedToDatabase )
			    throw new IllegalStateException( "Not Connected to Database" );
			// determine number of columns
			 try
			 {
			    return metaData.getColumnCount();
			} // end try
			 catch ( SQLException sqlException )
			 {
			   sqlException.printStackTrace();
			 } // end catch
			return 0; // if problems occur above, return 0 for number of columns
		} finally {
			ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
					"ResultSetTableModel#" + this.hashCode(),
					"com.kenai.javaatm",
					"ResultSetTableModel#" + this.hashCode(), "getColumnCount",
					"void", "int");
		}
      } // end method getColumnCount

     // get name of a particular column in ResultSet
     public String getColumnName( int column ) throws IllegalStateException
     {
       
try {
			ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
					"ResultSetTableModel#" + this.hashCode(),
					"com.kenai.javaatm",
					"ResultSetTableModel#" + this.hashCode(), "getColumnName",
					"int column", "String");
			// ensure database connection is available
			  if ( !connectedToDatabase )
			       throw new IllegalStateException( "Not Connected to Database" );
			// determine column name
			try
			{
			   return metaData.getColumnName( column + 1 );
			} // end try
			catch ( SQLException sqlException )
			{
			   sqlException.printStackTrace();
			} // end catch
			return ""; // if problems, return empty string for column name
		} finally {
			ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
					"ResultSetTableModel#" + this.hashCode(),
					"com.kenai.javaatm",
					"ResultSetTableModel#" + this.hashCode(), "getColumnName",
					"int column", "String");
		}
     } // end method getColumnName
 
     // return number of rows in ResultSet
     public int getRowCount() throws IllegalStateException
     {
        try {
			ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
					"ResultSetTableModel#" + this.hashCode(),
					"com.kenai.javaatm",
					"ResultSetTableModel#" + this.hashCode(), "getRowCount",
					"void", "int");
			// ensure database connection is available
			if ( !connectedToDatabase )
			   throw new IllegalStateException( "Not Connected to Database" );
			return numberOfRows;
		} finally {
			ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
					"ResultSetTableModel#" + this.hashCode(),
					"com.kenai.javaatm",
					"ResultSetTableModel#" + this.hashCode(), "getRowCount",
					"void", "int");
		}
     } // end method getRowCount
 
     // obtain value in particular row and column
     public Object getValueAt( int row, int column )
        throws IllegalStateException
     {
        
try {
			ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
					"ResultSetTableModel#" + this.hashCode(),
					"com.kenai.javaatm",
					"ResultSetTableModel#" + this.hashCode(), "getValueAt",
					"int row,int column", "Object");
			// ensure database connection is available
			if ( !connectedToDatabase )
			   throw new IllegalStateException( "Not Connected to Database" );
			// obtain a value at specified ResultSet row and column
			try
			{
			   resultSet.absolute( row + 1 );
			   return resultSet.getObject( column + 1 );
			} // end try
			catch ( SQLException sqlException )
			{
			   sqlException.printStackTrace();
			} // end catch
			return ""; // if problems, return empty string object
		} finally {
			ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
					"ResultSetTableModel#" + this.hashCode(),
					"com.kenai.javaatm",
					"ResultSetTableModel#" + this.hashCode(), "getValueAt",
					"int row,int column", "Object");
		}
     } // end method getValueAt
 
     // set new database query string
     public void setQuery( String query )
        throws SQLException, IllegalStateException
     {
        try {
			ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
					"ResultSetTableModel#" + this.hashCode(),
					"com.kenai.javaatm",
					"ResultSetTableModel#" + this.hashCode(), "setQuery",
					"String query", "void");
			// ensure database connection is available
			if ( !connectedToDatabase )
			   throw new IllegalStateException( "Not Connected to Database" );
			// specify query and execute it
			resultSet = statement.executeQuery(query);
			// obtain meta data for ResultSet
			metaData = resultSet.getMetaData();
			// determine number of rows in ResultSet
			resultSet.last();                   // move to last row
			numberOfRows = resultSet.getRow();  // get row number
			// notify JTable that model has changed
			fireTableStructureChanged();
		} finally {
			ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
					"ResultSetTableModel#" + this.hashCode(),
					"com.kenai.javaatm",
					"ResultSetTableModel#" + this.hashCode(), "setQuery",
					"String query", "void");
		}           
     } // end method setQuery
  
     // close Statement and Connection               
     public void disconnectFromDatabase()            
     {                                               
        
try {
		ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
				"ResultSetTableModel#" + this.hashCode(), "com.kenai.javaatm",
				"ResultSetTableModel#" + this.hashCode(),
				"disconnectFromDatabase", "void", "void");
		if (!connectedToDatabase)                    
           return;
		// close Statement and Connection            
		   try                                          
		    {                                            
		       statement.close();                        
		       connection.close();                       
		    } // end try                                 
		    catch ( SQLException sqlException )          
		    {                                            
		       sqlException.printStackTrace();           
		    } // end catch                               
		    finally  // update database connection status
		    {                                            
		       connectedToDatabase = false;              
		    } // end finally                             
	} finally {
		ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
				"ResultSetTableModel#" + this.hashCode(), "com.kenai.javaatm",
				"ResultSetTableModel#" + this.hashCode(),
				"disconnectFromDatabase", "void", "void");
	}
     } // end method disconnectFromDatabase          
  } // end class ResultSetTableModel
