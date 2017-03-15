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
        cardPlugged = false;
        this.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cardInsertactionPerformed(evt);
            }
        });
    }

    public boolean checkCard() {
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
    }

    private void cardInsertactionPerformed(java.awt.event.ActionEvent evt) {
        cardPlugged = true;
    }
} // end class DepositSlot



