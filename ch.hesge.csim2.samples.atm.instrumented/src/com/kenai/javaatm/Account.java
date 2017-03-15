package com.kenai.javaatm;

// Account.java
// Represents a bank account

public class Account 
{
   private int accountNumber; // account number
   private int pin; // PIN for authentication
   private double availableBalance; // funds available for withdrawal
   private double totalBalance; // funds available + pending deposits

   // Account constructor initializes attributes
   public Account( int theAccountNumber, int thePIN, 
      double theAvailableBalance, double theTotalBalance )
   {
      try {
		ch.hesge.csim2.engine.TraceLogger
				.entering(
						"com.kenai.javaatm",
						"Account#" + this.hashCode(),
						"com.kenai.javaatm",
						"Account#" + this.hashCode(),
						"Account",
						"int theAccountNumber,int thePIN,double theAvailableBalance,double theTotalBalance",
						"void");
		accountNumber = theAccountNumber;
		pin = thePIN;
		availableBalance = theAvailableBalance;
		totalBalance = theTotalBalance;
	} finally {
		ch.hesge.csim2.engine.TraceLogger
				.exiting(
						"com.kenai.javaatm",
						"Account#" + this.hashCode(),
						"com.kenai.javaatm",
						"Account#" + this.hashCode(),
						"Account",
						"int theAccountNumber,int thePIN,double theAvailableBalance,double theTotalBalance",
						"void");
	}
   } // end Account constructor

   // determines whether a user-specified PIN matches PIN in Account
   public boolean validatePIN( int userPIN )
   {
      try {
		ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
				"Account#" + this.hashCode(), "com.kenai.javaatm", "Account#"
						+ this.hashCode(), "validatePIN", "int userPIN",
				"boolean");
		if ( userPIN == pin )
		     return true;
		  else
		     return false;
	} finally {
		ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
				"Account#" + this.hashCode(), "com.kenai.javaatm", "Account#"
						+ this.hashCode(), "validatePIN", "int userPIN",
				"boolean");
	}
   } // end method validatePIN
   
   // returns available balance
   public double getAvailableBalance()
   {
      try {
		ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
				"Account#" + this.hashCode(), "com.kenai.javaatm", "Account#"
						+ this.hashCode(), "getAvailableBalance", "void",
				"double");
		return availableBalance;
	} finally {
		ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
				"Account#" + this.hashCode(), "com.kenai.javaatm", "Account#"
						+ this.hashCode(), "getAvailableBalance", "void",
				"double");
	}
   } // end getAvailableBalance

   // returns the total balance
   public double getTotalBalance()
   {
      try {
		ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
				"Account#" + this.hashCode(), "com.kenai.javaatm", "Account#"
						+ this.hashCode(), "getTotalBalance", "void", "double");
		return totalBalance;
	} finally {
		ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
				"Account#" + this.hashCode(), "com.kenai.javaatm", "Account#"
						+ this.hashCode(), "getTotalBalance", "void", "double");
	}
   } // end method getTotalBalance

   // credits an amount to the account
   public void credit( double amount )
   {
      
try {
		ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
				"Account#" + this.hashCode(), "com.kenai.javaatm", "Account#"
						+ this.hashCode(), "credit", "double amount", "void");
		totalBalance += amount; // add to total balance
	} finally {
		ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
				"Account#" + this.hashCode(), "com.kenai.javaatm", "Account#"
						+ this.hashCode(), "credit", "double amount", "void");
	}
   } // end method credit

   // debits an amount from the account
   public void debit( double amount )
   {
      
try {
		ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
				"Account#" + this.hashCode(), "com.kenai.javaatm", "Account#"
						+ this.hashCode(), "debit", "double amount", "void");
		availableBalance -= amount; // subtract from available balance
		totalBalance -= amount; // subtract from total balance
	} finally {
		ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
				"Account#" + this.hashCode(), "com.kenai.javaatm", "Account#"
						+ this.hashCode(), "debit", "double amount", "void");
	}
   } // end method debit

   // returns account number
   public int getAccountNumber()
   {
      try {
		ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
				"Account#" + this.hashCode(), "com.kenai.javaatm", "Account#"
						+ this.hashCode(), "getAccountNumber", "void", "int");
		return accountNumber;
	} finally {
		ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
				"Account#" + this.hashCode(), "com.kenai.javaatm", "Account#"
						+ this.hashCode(), "getAccountNumber", "void", "int");
	}  
   } // end method getAccountNumber

      // returns account pin
   public int getPin()
   {
      try {
		ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
				"Account#" + this.hashCode(), "com.kenai.javaatm", "Account#"
						+ this.hashCode(), "getPin", "void", "int");
		return pin;
	} finally {
		ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
				"Account#" + this.hashCode(), "com.kenai.javaatm", "Account#"
						+ this.hashCode(), "getPin", "void", "int");
	}
   } // end method getPin
} // end class Account


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