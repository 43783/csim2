package com.kenai.javaatm;


import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;

// CardSlot.java
// Represents the Card slot of the ATM
public class CardSlot extends JButton {

    boolean cardPlugged;
    int timeout;

    public CardSlot() {
        try {
			ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
					"CardSlot#" + this.hashCode(), "com.kenai.javaatm",
					"CardSlot#" + this.hashCode(), "CardSlot", "void", "void");
			cardPlugged = false;
			this.addActionListener(new java.awt.event.ActionListener() {

			    public void actionPerformed(java.awt.event.ActionEvent evt) {
			        try {
						ch.hesge.csim2.engine.TraceLogger.entering(
								"com.kenai.javaatm",
								"CardSlot$java.awt.event.ActionListener#"
										+ this.hashCode(),
								"com.kenai.javaatm",
								"CardSlot$java.awt.event.ActionListener#"
										+ this.hashCode(), "actionPerformed",
								"java.awt.event.ActionEvent evt", "void");
						cardInsertactionPerformed(evt);
					} finally {
						ch.hesge.csim2.engine.TraceLogger.exiting(
								"com.kenai.javaatm",
								"CardSlot$java.awt.event.ActionListener#"
										+ this.hashCode(),
								"com.kenai.javaatm",
								"CardSlot$java.awt.event.ActionListener#"
										+ this.hashCode(), "actionPerformed",
								"java.awt.event.ActionEvent evt", "void");
					}
			    }
			});
		} finally {
			ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
					"CardSlot#" + this.hashCode(), "com.kenai.javaatm",
					"CardSlot#" + this.hashCode(), "CardSlot", "void", "void");
		}
    }

    public boolean checkCard() {
        try {
			ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
					"CardSlot#" + this.hashCode(), "com.kenai.javaatm",
					"CardSlot#" + this.hashCode(), "checkCard", "void",
					"boolean");
			this.setEnabled(true);
			while (!cardPlugged) {

			    try {
			        Thread.sleep(1000);
			    } catch (InterruptedException ex) {
			        Logger.getLogger(DepositSlot.class.getName()).log(Level.SEVERE, null, ex);
			    }

			}
			this.setEnabled(false);
			cardPlugged = false;
			return true;
		} finally {
			ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
					"CardSlot#" + this.hashCode(), "com.kenai.javaatm",
					"CardSlot#" + this.hashCode(), "checkCard", "void",
					"boolean");
		}
    }

    private void cardInsertactionPerformed(java.awt.event.ActionEvent evt) {
        try {
			ch.hesge.csim2.engine.TraceLogger.entering("com.kenai.javaatm",
					"CardSlot#" + this.hashCode(), "com.kenai.javaatm",
					"CardSlot#" + this.hashCode(), "cardInsertactionPerformed",
					"java.awt.event.ActionEvent evt", "void");
			cardPlugged = true;
		} finally {
			ch.hesge.csim2.engine.TraceLogger.exiting("com.kenai.javaatm",
					"CardSlot#" + this.hashCode(), "com.kenai.javaatm",
					"CardSlot#" + this.hashCode(), "cardInsertactionPerformed",
					"java.awt.event.ActionEvent evt", "void");
		}
    }
} // end class DepositSlot



