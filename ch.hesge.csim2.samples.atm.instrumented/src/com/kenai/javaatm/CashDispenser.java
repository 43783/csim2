package com.kenai.javaatm;

// CashDispenser.java
// Represents the cash dispenser of the ATM

public class CashDispenser {
    // the default initial number of bills in the cash dispenser

    private final static int INITIAL_COUNT = 500;
    private int count; // number of $20 bills remaining

    // no-argument CashDispenser constructor initializes count to default
    public CashDispenser() {
        
try {
			ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
					"CashDispenser#" + this.hashCode(), "com.kenai.javaatm",
					"CashDispenser#" + this.hashCode(), "CashDispenser",
					"void", "void");
			count = INITIAL_COUNT; // set count attribute to default
		} finally {
			ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
					"CashDispenser#" + this.hashCode(), "com.kenai.javaatm",
					"CashDispenser#" + this.hashCode(), "CashDispenser",
					"void", "void");
		}
    } // end CashDispenser constructor

    // simulates dispensing of specified amount of cash
    public void dispenseCash(int amount) {
        
try {
			ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
					"CashDispenser#" + this.hashCode(), "com.kenai.javaatm",
					"CashDispenser#" + this.hashCode(), "dispenseCash",
					"int amount", "void");
			int billsRequired = amount / 20; // number of $20 bills required
			count -= billsRequired; // update the count of bills
		} finally {
			ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
					"CashDispenser#" + this.hashCode(), "com.kenai.javaatm",
					"CashDispenser#" + this.hashCode(), "dispenseCash",
					"int amount", "void");
		}
    } // end method dispenseCash

    // indicates whether cash dispenser can dispense desired amount
    public boolean isSufficientCashAvailable(int amount) {
        try {
			ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
					"CashDispenser#" + this.hashCode(), "com.kenai.javaatm",
					"CashDispenser#" + this.hashCode(),
					"isSufficientCashAvailable", "int amount", "boolean");
			int billsRequired = amount / 20; // number of $20 bills required
			if (count >= billsRequired) {
			    return true; // enough bills available
			} else {
			    return false; // not enough bills available
			}
		} finally {
			ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
					"CashDispenser#" + this.hashCode(), "com.kenai.javaatm",
					"CashDispenser#" + this.hashCode(),
					"isSufficientCashAvailable", "int amount", "boolean");
		}
    } // end method isSufficientCashAvailable
} // end class CashDispenser



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
