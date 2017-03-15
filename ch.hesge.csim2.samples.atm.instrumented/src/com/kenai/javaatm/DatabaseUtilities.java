package com.kenai.javaatm;


import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author senouci hadj
 */
public class DatabaseUtilities {

    Connection connetion;
    PreparedStatement ps;
    String pilote = "com.mysql.jdbc.Driver";
    ResultSet result;
    String dbName = "credit_card_db";
    String userName = "root"; // put your mysql user name
    String password = "root";// put your mysql password
    Account account;
    double total_Balance, availableBalance;

    // check if data base exist or not
    // if not then create it
    public boolean ensureDatabaseExistence() {
        try {
			ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
					"DatabaseUtilities#" + this.hashCode(),
					"com.kenai.javaatm",
					"DatabaseUtilities#" + this.hashCode(),
					"ensureDatabaseExistence", "void", "boolean");
			connectToDatabase(dbName);
			try {
			    ps = (PreparedStatement) connetion.prepareStatement("CREATE DATABASE IF NOT EXISTS credit_card_db;"); // prepare the request
			    ps.executeUpdate();
			    ps = (PreparedStatement) connetion.prepareStatement("CREATE TABLE  IF NOT EXISTS credit_card_db.client ("
			            + "account_number smallint(6) NOT NULL default '0',"
			            + "pin smallint(6) NOT NULL,"
			            + "available_balance double NOT NULL,"
			            + "total_Balance double NOT NULL,"
			            + "PRIMARY KEY  (account_number)"
			            + ") ENGINE=MyISAM DEFAULT CHARSET=latin1;"); // prepare the request
			    ps.executeUpdate();
			    return true;
			} catch (SQLException ex) {
			    JOptionPane.showMessageDialog(null,
			            ex.getMessage(), "Note",
			            JOptionPane.ERROR_MESSAGE);
			    Logger.getLogger(DatabaseUtilities.class.getName()).log(Level.SEVERE, null, ex);
			    return false;
			}
		} finally {
			ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
					"DatabaseUtilities#" + this.hashCode(),
					"com.kenai.javaatm",
					"DatabaseUtilities#" + this.hashCode(),
					"ensureDatabaseExistence", "void", "boolean");
		}

    }

    public void updateDataBase() {

        try {
			ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
					"DatabaseUtilities#" + this.hashCode(),
					"com.kenai.javaatm",
					"DatabaseUtilities#" + this.hashCode(), "updateDataBase",
					"void", "void");
			try {
			    connectToDatabase(dbName); // connect to db
			    ps = (PreparedStatement) connetion.prepareStatement("UPDATE client set"
			            + " available_balance = total_Balance"); // prepare the request
			    ps.executeUpdate();
			} catch (SQLException ex) {
			    Logger.getLogger(DatabaseUtilities.class.getName()).log(Level.SEVERE, null, ex);
			}
		} finally {
			ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
					"DatabaseUtilities#" + this.hashCode(),
					"com.kenai.javaatm",
					"DatabaseUtilities#" + this.hashCode(), "updateDataBase",
					"void", "void");
		}

    }
    // return the specific account if exist

    public Account getAccount(int accountNumber) {
        try {
			ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
					"DatabaseUtilities#" + this.hashCode(),
					"com.kenai.javaatm",
					"DatabaseUtilities#" + this.hashCode(), "getAccount",
					"int accountNumber", "Account");
			connectToDatabase(dbName);// connect to db
			try {
			    ps = (PreparedStatement) connetion.prepareStatement("SELECT * "
			            + "FROM client WHERE account_number = ?"); // prepare the request
			    ps.setInt(1, accountNumber); //assign accountNumber to ? in ps
			    result = ps.executeQuery(); // execute the query
			    if (result.next()) { // check if there's a result
			        account = new Account(result.getInt(1), result.getInt(2),
			                result.getDouble(3), result.getDouble(4)); // return the account
			    } else { // no result
			        account = null;
			    }
			    disconnect();
			    return account;
			} catch (SQLException ex) {
			    Logger.getLogger(DatabaseUtilities.class.getName()).log(Level.SEVERE, null, ex);
			    return null;
			}
		} finally {
			ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
					"DatabaseUtilities#" + this.hashCode(),
					"com.kenai.javaatm",
					"DatabaseUtilities#" + this.hashCode(), "getAccount",
					"int accountNumber", "Account");
		}
    }

    public int getNextAccountNumber() {
        try {
			ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
					"DatabaseUtilities#" + this.hashCode(),
					"com.kenai.javaatm",
					"DatabaseUtilities#" + this.hashCode(),
					"getNextAccountNumber", "void", "int");
			int nextAccount = 1;
			connectToDatabase(dbName);// connect to db
			try {
			    ps = (PreparedStatement) connetion.prepareStatement("SELECT account_number "
			            + "FROM client ORDER BY account_number DESC"); // prepare the request
			    result = ps.executeQuery(); // execute the query
			    if (result.next()) { // check if there's a result
			        nextAccount = result.getInt(1) + 1;
			    }
			    disconnect();
			} catch (SQLException ex) {
			    Logger.getLogger(DatabaseUtilities.class.getName()).log(Level.SEVERE, null, ex);
			}
			return nextAccount;
		} finally {
			ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
					"DatabaseUtilities#" + this.hashCode(),
					"com.kenai.javaatm",
					"DatabaseUtilities#" + this.hashCode(),
					"getNextAccountNumber", "void", "int");
		}
    }

    //update account balance
    public boolean updateAccountBalance(int accountNumber,
            double availableBalance, double totalBalance) {
        try {
			ch.hesge.csim2.engine.TraceLogger
					.entering(
							"com.kenai.javaatm",
							"DatabaseUtilities#" + this.hashCode(),
							"com.kenai.javaatm",
							"DatabaseUtilities#" + this.hashCode(),
							"updateAccountBalance",
							"int accountNumber,double availableBalance,double totalBalance",
							"boolean");
			connectToDatabase(dbName);// connect to db
			try {
			    ps = (PreparedStatement) connetion.prepareStatement("UPDATE client SET " +
			            "available_balance = ?, total_Balance = ? " +
			            "WHERE account_number = ?;"); // prepare the request
			    ps.setDouble(1, availableBalance);
			    ps.setDouble(2, totalBalance);
			    ps.setInt(3, accountNumber);
			    ps.executeUpdate(); // execute the query
			    disconnect();
			    return true;
			} catch (SQLException ex) {
			                JOptionPane.showMessageDialog(null,
			            ex.getMessage(), "Notice",
			            JOptionPane.ERROR_MESSAGE);
			    Logger.getLogger(DatabaseUtilities.class.getName()).log(Level.SEVERE, null, ex);
			    return false;
			}
		} finally {
			ch.hesge.csim2.engine.TraceLogger
					.exiting(
							"com.kenai.javaatm",
							"DatabaseUtilities#" + this.hashCode(),
							"com.kenai.javaatm",
							"DatabaseUtilities#" + this.hashCode(),
							"updateAccountBalance",
							"int accountNumber,double availableBalance,double totalBalance",
							"boolean");
		}

    }

    /*if the account exist then updated it
     * else create a new one
     */
    public boolean updateAccount(int accountNumber, int PIN,
            double availableBalance, double totalBalance) {
        try {
			ch.hesge.csim2.engine.TraceLogger
					.entering(
							"com.kenai.javaatm",
							"DatabaseUtilities#" + this.hashCode(),
							"com.kenai.javaatm",
							"DatabaseUtilities#" + this.hashCode(),
							"updateAccount",
							"int accountNumber,int PIN,double availableBalance,double totalBalance",
							"boolean");
			connectToDatabase(dbName);// connect to db
			try {
			    ps = (PreparedStatement) connetion.prepareStatement("REPLACE INTO client"
			            + "(account_number,pin,available_balance,total_Balance)"
			            + "VALUES (?,?,?,?);"); // prepare the request
			    ps.setInt(1, accountNumber);
			    ps.setInt(2, PIN);
			    ps.setDouble(3, availableBalance);
			    ps.setDouble(4, totalBalance);
			    ps.executeUpdate(); // execute the query
			    disconnect();
			    JOptionPane.showMessageDialog(null,
			            "Account Updated", "Notice",
			            JOptionPane.INFORMATION_MESSAGE);
			    return true;
			} catch (SQLException ex) {
			    JOptionPane.showMessageDialog(null,
			            ex.getMessage(), "Notice",
			            JOptionPane.ERROR_MESSAGE);
			    Logger.getLogger(DatabaseUtilities.class.getName()).log(Level.SEVERE, null, ex);
			    return false;
			}
		} finally {
			ch.hesge.csim2.engine.TraceLogger
					.exiting(
							"com.kenai.javaatm",
							"DatabaseUtilities#" + this.hashCode(),
							"com.kenai.javaatm",
							"DatabaseUtilities#" + this.hashCode(),
							"updateAccount",
							"int accountNumber,int PIN,double availableBalance,double totalBalance",
							"boolean");
		}

    }



    // delete account
    public boolean deleteAccount(int accountNumber) {
        try {
			ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
					"DatabaseUtilities#" + this.hashCode(),
					"com.kenai.javaatm",
					"DatabaseUtilities#" + this.hashCode(), "deleteAccount",
					"int accountNumber", "boolean");
			connectToDatabase(dbName);// connect to db
			try {
			    ps = (PreparedStatement) connetion.prepareStatement("DELETE FROM client "
			            + "WHERE account_number = ?"); // prepare the request
			    ps.setInt(1, accountNumber);
			    ps.executeUpdate(); // execute the query
			    disconnect();
			    JOptionPane.showMessageDialog(null,
			            "Account Deleted", "Note",
			            JOptionPane.INFORMATION_MESSAGE);
			    return true;
			} catch (SQLException ex) {
			    JOptionPane.showMessageDialog(null,
			            ex.getMessage(), "Note",
			            JOptionPane.ERROR_MESSAGE);
			    Logger.getLogger(DatabaseUtilities.class.getName()).log(Level.SEVERE, null, ex);
			    return false;
			}
		} finally {
			ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
					"DatabaseUtilities#" + this.hashCode(),
					"com.kenai.javaatm",
					"DatabaseUtilities#" + this.hashCode(), "deleteAccount",
					"int accountNumber", "boolean");
		}

    }

    // connect to the data base
    public boolean connectToDatabase(String dbName) {
        try {
			ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
					"DatabaseUtilities#" + this.hashCode(),
					"com.kenai.javaatm",
					"DatabaseUtilities#" + this.hashCode(),
					"connectToDatabase", "String dbName", "boolean");
			try {
			    // create a connetion with the db
			    Class.forName(pilote);
			    connetion = (Connection) DriverManager.getConnection("jdbc:mysql://localhost/" + dbName,
			            userName, password);
			    return true;
			} catch (SQLException ex) {
			    Logger.getLogger(DatabaseUtilities.class.getName()).log(Level.SEVERE, null, ex);
			    return false;
			} catch (ClassNotFoundException ex) {
			    Logger.getLogger(DatabaseUtilities.class.getName()).log(Level.SEVERE, null, ex);
			    return false;
			}
		} finally {
			ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
					"DatabaseUtilities#" + this.hashCode(),
					"com.kenai.javaatm",
					"DatabaseUtilities#" + this.hashCode(),
					"connectToDatabase", "String dbName", "boolean");
		}


    }

    // close the connection with the data base
    public boolean disconnect() {
        try {
			ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
					"DatabaseUtilities#" + this.hashCode(),
					"com.kenai.javaatm",
					"DatabaseUtilities#" + this.hashCode(), "disconnect",
					"void", "boolean");
			try {
			    connetion.close();
			    return true;
			} catch (SQLException ex) {
			    Logger.getLogger(DatabaseUtilities.class.getName()).log(Level.SEVERE, null, ex);
			}
			return false;
		} finally {
			ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
					"DatabaseUtilities#" + this.hashCode(),
					"com.kenai.javaatm",
					"DatabaseUtilities#" + this.hashCode(), "disconnect",
					"void", "boolean");
		}
    }

    // credits an amount to the account
    public void credit(int userAccountNumber, double amount) {
        try {
			ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
					"DatabaseUtilities#" + this.hashCode(),
					"com.kenai.javaatm",
					"DatabaseUtilities#" + this.hashCode(), "credit",
					"int userAccountNumber,double amount", "void");
			account = getAccount(userAccountNumber);
			account.credit(amount);
			total_Balance = account.getTotalBalance();
			availableBalance = account.getAvailableBalance();
			updateAccountBalance(userAccountNumber, availableBalance, total_Balance);
		} finally {
			ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
					"DatabaseUtilities#" + this.hashCode(),
					"com.kenai.javaatm",
					"DatabaseUtilities#" + this.hashCode(), "credit",
					"int userAccountNumber,double amount", "void");
		}
    } // end method credit

    // debits an amount from the account
    public void debit(int userAccountNumber, double amount) {
        try {
			ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
					"DatabaseUtilities#" + this.hashCode(),
					"com.kenai.javaatm",
					"DatabaseUtilities#" + this.hashCode(), "debit",
					"int userAccountNumber,double amount", "void");
			account = getAccount(userAccountNumber);
			account.debit(amount);
			total_Balance = account.getTotalBalance();
			availableBalance = account.getAvailableBalance();
			System.out.println("account: "+ account +
			        " total_Balance: "+ total_Balance +
			        "availableBalance"+availableBalance);
			updateAccountBalance(userAccountNumber, availableBalance, total_Balance);
		} finally {
			ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
					"DatabaseUtilities#" + this.hashCode(),
					"com.kenai.javaatm",
					"DatabaseUtilities#" + this.hashCode(), "debit",
					"int userAccountNumber,double amount", "void");
		}
    } // end method debit

    public String getDataBaseUserName() {
        try {
			ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
					"DatabaseUtilities#" + this.hashCode(),
					"com.kenai.javaatm",
					"DatabaseUtilities#" + this.hashCode(),
					"getDataBaseUserName", "void", "String");
			return userName;
		} finally {
			ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
					"DatabaseUtilities#" + this.hashCode(),
					"com.kenai.javaatm",
					"DatabaseUtilities#" + this.hashCode(),
					"getDataBaseUserName", "void", "String");
		}
    }

    public String getDataBasePassword() {
        try {
			ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
					"DatabaseUtilities#" + this.hashCode(),
					"com.kenai.javaatm",
					"DatabaseUtilities#" + this.hashCode(),
					"getDataBasePassword", "void", "String");
			return password;
		} finally {
			ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
					"DatabaseUtilities#" + this.hashCode(),
					"com.kenai.javaatm",
					"DatabaseUtilities#" + this.hashCode(),
					"getDataBasePassword", "void", "String");
		}
    }
    // main method creates and runs the class

    public static void main(String[] args) {
        try {
			ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
					"DatabaseUtilities#0", "com.kenai.javaatm",
					"DatabaseUtilities#0", "main", "String[] args", "void");
			DatabaseUtilities d = new DatabaseUtilities();
			d.ensureDatabaseExistence();
			d.updateDataBase();
		} finally {
			ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
					"DatabaseUtilities#0", "com.kenai.javaatm",
					"DatabaseUtilities#0", "main", "String[] args", "void");
		}
    } // end main
}

/**************************************************************************
 *  2009-2010 by SENOUCI hadj.
 *  Email: senoucihs@gmail.com
 *  this code is free which mean
 *  you can re-utilize it as you want
 *************************************************************************/