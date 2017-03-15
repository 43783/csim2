package com.kenai.javaatm;

// BankDatabase.java
// Represents the bank account information database 

public class BankDatabase {

    private DatabaseUtilities dbu = new DatabaseUtilities();

    // no-argument BankDatabase constructor initializes accounts
    public BankDatabase() {
        
try {
			ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
					"BankDatabase#" + this.hashCode(), "com.kenai.javaatm",
					"BankDatabase#" + this.hashCode(), "BankDatabase", "void",
					"void");
			dbu.ensureDatabaseExistence(); // to ensure the database existence
			dbu.updateDataBase(); // update the database accounts
		} finally {
			ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
					"BankDatabase#" + this.hashCode(), "com.kenai.javaatm",
					"BankDatabase#" + this.hashCode(), "BankDatabase", "void",
					"void");
		}
    } // end no-argument BankDatabase constructor

    // retrieve Account object containing specified account number
    private Account getAccount(int accountNumber) {
        try {
			ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
					"BankDatabase#" + this.hashCode(), "com.kenai.javaatm",
					"BankDatabase#" + this.hashCode(), "getAccount",
					"int accountNumber", "Account");
			return dbu.getAccount(accountNumber);
		} finally {
			ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
					"BankDatabase#" + this.hashCode(), "com.kenai.javaatm",
					"BankDatabase#" + this.hashCode(), "getAccount",
					"int accountNumber", "Account");
		}
    } // end method getAccount

    // determine whether user-specified account number and PIN match
    // those of an account in the database
    public boolean authenticateUser(int userAccountNumber, int userPIN) {
        try {
			ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
					"BankDatabase#" + this.hashCode(), "com.kenai.javaatm",
					"BankDatabase#" + this.hashCode(), "authenticateUser",
					"int userAccountNumber,int userPIN", "boolean");
			// attempt to retrieve the account with the account number
			Account userAccount = getAccount(userAccountNumber);
			// if account exists, return result of Account method validatePIN
			if (userAccount != null) {
			    return userAccount.validatePIN(userPIN);
			} else {
			    return false; // account number not found, so return false
			}
		} finally {
			ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
					"BankDatabase#" + this.hashCode(), "com.kenai.javaatm",
					"BankDatabase#" + this.hashCode(), "authenticateUser",
					"int userAccountNumber,int userPIN", "boolean");
		}
    } // end method authenticateUser

    // return available balance of Account with specified account number
    public double getAvailableBalance(int userAccountNumber) {
        try {
			ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
					"BankDatabase#" + this.hashCode(), "com.kenai.javaatm",
					"BankDatabase#" + this.hashCode(), "getAvailableBalance",
					"int userAccountNumber", "double");
			return getAccount(userAccountNumber).getAvailableBalance();
		} finally {
			ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
					"BankDatabase#" + this.hashCode(), "com.kenai.javaatm",
					"BankDatabase#" + this.hashCode(), "getAvailableBalance",
					"int userAccountNumber", "double");
		}
    } // end method getAvailableBalance

    // return total balance of Account with specified account number
    public double getTotalBalance(int userAccountNumber) {
        try {
			ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
					"BankDatabase#" + this.hashCode(), "com.kenai.javaatm",
					"BankDatabase#" + this.hashCode(), "getTotalBalance",
					"int userAccountNumber", "double");
			return getAccount(userAccountNumber).getTotalBalance();
		} finally {
			ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
					"BankDatabase#" + this.hashCode(), "com.kenai.javaatm",
					"BankDatabase#" + this.hashCode(), "getTotalBalance",
					"int userAccountNumber", "double");
		}
    } // end method getTotalBalance

    // credit an amount to Account with specified account number
    public void credit(int userAccountNumber, double amount) {
        try {
			ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
					"BankDatabase#" + this.hashCode(), "com.kenai.javaatm",
					"BankDatabase#" + this.hashCode(), "credit",
					"int userAccountNumber,double amount", "void");
			dbu.credit(userAccountNumber, amount);
		} finally {
			ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
					"BankDatabase#" + this.hashCode(), "com.kenai.javaatm",
					"BankDatabase#" + this.hashCode(), "credit",
					"int userAccountNumber,double amount", "void");
		}
    } // end method credit

    // debit an amount from of Account with specified account number
    public void debit(int userAccountNumber, double amount) {
        try {
			ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
					"BankDatabase#" + this.hashCode(), "com.kenai.javaatm",
					"BankDatabase#" + this.hashCode(), "debit",
					"int userAccountNumber,double amount", "void");
			dbu.debit(userAccountNumber, amount);
		} finally {
			ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
					"BankDatabase#" + this.hashCode(), "com.kenai.javaatm",
					"BankDatabase#" + this.hashCode(), "debit",
					"int userAccountNumber,double amount", "void");
		}
    } // end method debit
} // end class BankDatabase



/**************************************************************************
 * (C) Copyright 1992-2005 by Deitel & Associates, Inc. and               *
 * Pearson Education, Inc. All Rights Reserved.                           *
 *                                                                        *
 * DISCLAIMER: The authors and publisher of this book have used their     *
 * best efforts in preparing the book. These efforts include the          *
 * development, research, and testing of the theories and programs        *
 * to determine their effectiveness. The authors and publisher make       *
 * no warranty of any kind, expressed or implied, with regard to these    *
 * programs or to the documentation contained in these books. The authors *
 * and publisher shall not be liable in any event for incidental or       *
 * consequential damages in connection with, or arising out of, the       *
 * furnishing, performance, or use of these programs.                     *
 *************************************************************************/
