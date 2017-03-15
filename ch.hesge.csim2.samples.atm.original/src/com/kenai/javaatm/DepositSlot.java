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
        envlopReceived = false;
        timeout = 10;
        this.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                depositSlotactionPerformed(evt);
            }
        });
    }

    // indicates whether envelope was received (always returns true,
    // because this is only a software simulation of a real deposit slot)
    public boolean isEnvelopeReceived() {
        this.setEnabled(true);
        return checkEnvelope(); // deposit envelope was received
    } // end method isEnvelopeReceived

    private boolean checkEnvelope() {
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


    }

    public void depositSlotactionPerformed(java.awt.event.ActionEvent evt) {
        envlopReceived = true;
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
