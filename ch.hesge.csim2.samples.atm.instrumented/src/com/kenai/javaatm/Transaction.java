package com.kenai.javaatm;


import java.util.logging.Level;
import java.util.logging.Logger;

// Transaction.java
// Abstract superclass Transaction represents an ATM transaction

public abstract class Transaction
{
   private int accountNumber; // indicates account involved
   private Screen screen; // ATM's screen
   private BankDatabase bankDatabase; // account info database

   // Transaction constructor invoked by subclasses using super()
   public Transaction( int userAccountNumber, Screen atmScreen, 
      BankDatabase atmBankDatabase )
   {
      try {
		ch.hesge.csim2.engine.TraceLogger
				.entering(
						"com.kenai.javaatm",
						"Transaction#" + this.hashCode(),
						"com.kenai.javaatm",
						"Transaction#" + this.hashCode(),
						"Transaction",
						"int userAccountNumber,Screen atmScreen,BankDatabase atmBankDatabase",
						"void");
		accountNumber = userAccountNumber;
		screen = atmScreen;
		bankDatabase = atmBankDatabase;
	} finally {
		ch.hesge.csim2.engine.TraceLogger
				.exiting(
						"com.kenai.javaatm",
						"Transaction#" + this.hashCode(),
						"com.kenai.javaatm",
						"Transaction#" + this.hashCode(),
						"Transaction",
						"int userAccountNumber,Screen atmScreen,BankDatabase atmBankDatabase",
						"void");
	}
   } // end Transaction constructor

   // return account number 
   public int getAccountNumber()
   {
      try {
		ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
				"Transaction#" + this.hashCode(), "com.kenai.javaatm",
				"Transaction#" + this.hashCode(), "getAccountNumber", "void",
				"int");
		return accountNumber;
	} finally {
		ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
				"Transaction#" + this.hashCode(), "com.kenai.javaatm",
				"Transaction#" + this.hashCode(), "getAccountNumber", "void",
				"int");
	} 
   } // end method getAccountNumber

   // return reference to screen
   public Screen getScreen()
   {
      try {
		ch.hesge.csim2.engine.TraceLogger
				.entering("com.kenai.javaatm",
						"Transaction#" + this.hashCode(), "com.kenai.javaatm",
						"Transaction#" + this.hashCode(), "getScreen", "void",
						"Screen");
		return screen;
	} finally {
		ch.hesge.csim2.engine.TraceLogger
				.exiting("com.kenai.javaatm", "Transaction#" + this.hashCode(),
						"com.kenai.javaatm", "Transaction#" + this.hashCode(),
						"getScreen", "void", "Screen");
	}
   } // end method getScreen

   // return reference to bank database
   public BankDatabase getBankDatabase()
   {
      try {
		ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
				"Transaction#" + this.hashCode(), "com.kenai.javaatm",
				"Transaction#" + this.hashCode(), "getBankDatabase", "void",
				"BankDatabase");
		return bankDatabase;
	} finally {
		ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
				"Transaction#" + this.hashCode(), "com.kenai.javaatm",
				"Transaction#" + this.hashCode(), "getBankDatabase", "void",
				"BankDatabase");
	}
   } // end method getBankDatabase

   // perform the transaction (overridden by each subclass)
   abstract public void execute();


   // simple delay method
   public static void delay(long ms)
   {
        try {
			ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
					"Transaction#0", "com.kenai.javaatm", "Transaction#0",
					"delay", "long ms", "void");
			try {
			    Thread.sleep(ms);
			} catch (InterruptedException ex) {
			    Logger.getLogger(Transaction.class.getName()).log(Level.SEVERE, null, ex);
			}
		} finally {
			ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
					"Transaction#0", "com.kenai.javaatm", "Transaction#0",
					"delay", "long ms", "void");
		}

   }

} // end class Transaction



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