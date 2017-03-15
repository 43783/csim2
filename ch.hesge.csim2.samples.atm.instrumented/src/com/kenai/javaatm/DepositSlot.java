package com.kenai.javaatm;


import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;

// DepositSlot.java
// Represents the deposit slot of the ATM
public class DepositSlot extends JButton {

    boolean envlopReceived;
    int timeout;

    public DepositSlot() {
        try {
			ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
					"DepositSlot#" + this.hashCode(), "com.kenai.javaatm",
					"DepositSlot#" + this.hashCode(), "DepositSlot", "void",
					"void");
			envlopReceived = false;
			timeout = 10;
			this.addActionListener(new java.awt.event.ActionListener() {

			    public void actionPerformed(java.awt.event.ActionEvent evt) {
			        try {
						ch.hesge.csim2.engine.TraceLogger.entering(
								"com.kenai.javaatm",
								"DepositSlot$java.awt.event.ActionListener#"
										+ this.hashCode(),
								"com.kenai.javaatm",
								"DepositSlot$java.awt.event.ActionListener#"
										+ this.hashCode(), "actionPerformed",
								"java.awt.event.ActionEvent evt", "void");
						depositSlotactionPerformed(evt);
					} finally {
						ch.hesge.csim2.engine.TraceLogger.exiting(
								"com.kenai.javaatm",
								"DepositSlot$java.awt.event.ActionListener#"
										+ this.hashCode(),
								"com.kenai.javaatm",
								"DepositSlot$java.awt.event.ActionListener#"
										+ this.hashCode(), "actionPerformed",
								"java.awt.event.ActionEvent evt", "void");
					}
			    }
			});
		} finally {
			ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
					"DepositSlot#" + this.hashCode(), "com.kenai.javaatm",
					"DepositSlot#" + this.hashCode(), "DepositSlot", "void",
					"void");
		}
    }

    // indicates whether envelope was received (always returns true,
    // because this is only a software simulation of a real deposit slot)
    public boolean isEnvelopeReceived() {
        
try {
			ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
					"DepositSlot#" + this.hashCode(), "com.kenai.javaatm",
					"DepositSlot#" + this.hashCode(), "isEnvelopeReceived",
					"void", "boolean");
			this.setEnabled(true);
			return checkEnvelope(); // deposit envelope was received
		} finally {
			ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
					"DepositSlot#" + this.hashCode(), "com.kenai.javaatm",
					"DepositSlot#" + this.hashCode(), "isEnvelopeReceived",
					"void", "boolean");
		}
    } // end method isEnvelopeReceived

    private boolean checkEnvelope() {
        try {
			ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
					"DepositSlot#" + this.hashCode(), "com.kenai.javaatm",
					"DepositSlot#" + this.hashCode(), "checkEnvelope", "void",
					"boolean");
			int i = 0;
			while (!envlopReceived && i < timeout) {
			    i++;

			    try {
			        Thread.sleep(1000);
			    } catch (InterruptedException ex) {
			        Logger.getLogger(DepositSlot.class.getName()).log(Level.SEVERE, null, ex);
			    }

			}
			this.setEnabled(false);
			if (envlopReceived) {
			    envlopReceived = false;
			    return true;
			} else {
			    return false;
			}
		} finally {
			ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
					"DepositSlot#" + this.hashCode(), "com.kenai.javaatm",
					"DepositSlot#" + this.hashCode(), "checkEnvelope", "void",
					"boolean");
		}


    }

    public void depositSlotactionPerformed(java.awt.event.ActionEvent evt) {
        try {
			ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
					"DepositSlot#" + this.hashCode(), "com.kenai.javaatm",
					"DepositSlot#" + this.hashCode(),
					"depositSlotactionPerformed",
					"java.awt.event.ActionEvent evt", "void");
			envlopReceived = true;
		} finally {
			ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
					"DepositSlot#" + this.hashCode(), "com.kenai.javaatm",
					"DepositSlot#" + this.hashCode(),
					"depositSlotactionPerformed",
					"java.awt.event.ActionEvent evt", "void");
		}
    }
} // end class DepositSlot



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
