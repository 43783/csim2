package com.kenai.javaatm;

// Deposit.java
// Represents a deposit ATM transaction

public class Deposit extends Transaction {

    private double amount; // amount to deposit
    private Keypad keypad; // reference to keypad
    private DepositSlot depositSlot; // reference to deposit slot
    private final static int CANCELED = 0; // constant for cancel option

    // Deposit constructor
    public Deposit(int userAccountNumber, Screen atmScreen,
            BankDatabase atmBankDatabase, Keypad atmKeypad,
            DepositSlot atmDepositSlot) {
        // initialize superclass variables
        super(userAccountNumber, atmScreen, atmBankDatabase);
		try {
			ch.hesge.csim2.engine.TraceLogger
					.entering(
							"com.kenai.javaatm",
							"Deposit#" + this.hashCode(),
							"com.kenai.javaatm",
							"Deposit#" + this.hashCode(),
							"Deposit",
							"int userAccountNumber,Screen atmScreen,BankDatabase atmBankDatabase,Keypad atmKeypad,DepositSlot atmDepositSlot",
							"void");
			// initialize references to keypad and deposit slot
			keypad = atmKeypad;
			depositSlot = atmDepositSlot;
		} finally {
			ch.hesge.csim2.engine.TraceLogger
					.exiting(
							"com.kenai.javaatm",
							"Deposit#" + this.hashCode(),
							"com.kenai.javaatm",
							"Deposit#" + this.hashCode(),
							"Deposit",
							"int userAccountNumber,Screen atmScreen,BankDatabase atmBankDatabase,Keypad atmKeypad,DepositSlot atmDepositSlot",
							"void");
		}
    } // end Deposit constructor

    // perform transaction
    public void execute() {
        try {
			ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
					"Deposit#" + this.hashCode(), "com.kenai.javaatm",
					"Deposit#" + this.hashCode(), "execute", "void", "void");
			BankDatabase bankDatabase = getBankDatabase(); // get reference
			Screen screen = getScreen(); // get reference
			amount = promptForDepositAmount(); // get deposit amount from user
			// check whether user entered a deposit amount or canceled
			if (amount != CANCELED) {
			    // request deposit envelope containing specified amount
			    screen.displayMessage(
			            "\nPlease insert a deposit envelope\ncontaining ");
			    screen.displayDollarAmount(amount);
			    screen.displayMessageLine(".");
			    screen.diplayPutEnvelopeImage();

			    // receive deposit envelope
			    boolean envelopeReceived = depositSlot.isEnvelopeReceived();

			    // check whether deposit envelope was received
			    if (envelopeReceived) {
			        screen.displayMessageLine("\nYour envelope has been "
			                + "received.\nNOTE: The money just deposited will not\n"
			                + "be available until we verify the amount of any\n"
			                + "enclosed cash and your checks clear.");
			        delay(5000);

			        // credit account to reflect the deposit
			        bankDatabase.credit(getAccountNumber(), amount);
			    } // end if
			    else // deposit envelope not received
			    {
			        screen.displayMessageLine("\nYou did not insert an "
			                + "envelope\nso the ATM has canceled your transaction.");
			        delay(2000);
			    } // end else
			} // end if
			else // user canceled instead of entering amount
			{
			    screen.displayMessageLine("\nCanceling transaction...");
			} // end else
			screen.clearPutEnvelopeImage();
		} finally {
			ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
					"Deposit#" + this.hashCode(), "com.kenai.javaatm",
					"Deposit#" + this.hashCode(), "execute", "void", "void");
		}
    } // end method execute

    // prompt user to enter a deposit amount in cents
    private double promptForDepositAmount() {
        
try {
			ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
					"Deposit#" + this.hashCode(), "com.kenai.javaatm",
					"Deposit#" + this.hashCode(), "promptForDepositAmount",
					"void", "double");
			Screen screen = getScreen(); // get reference to screen
			// display the prompt
			screen.clearMessageScreen();
			screen.displayMessage("\nPlease enter a deposit amount in"
			        + "\nCENTS (or 0 to cancel): ");
			int input = keypad.getInput(); // receive input of deposit amount
			// check whether the user canceled or entered a valid amount
			if (input == CANCELED) {
			    return CANCELED;
			} else {
			    return (double) input / 100; // return dollar amount
			} // end else
		} finally {
			ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
					"Deposit#" + this.hashCode(), "com.kenai.javaatm",
					"Deposit#" + this.hashCode(), "promptForDepositAmount",
					"void", "double");
		}
    } // end method promptForDepositAmount
} // end class Deposit



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
