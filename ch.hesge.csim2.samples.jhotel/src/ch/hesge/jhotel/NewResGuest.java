package ch.hesge.jhotel;
/**
 * Copyright 2004, Martin Jungowski
 *
 *	This file is part of JHotel.
 *
 *	JHotel is free software; you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation; either version 2 of the License, or
 *	(at your option) any later version.
 *
 *	JHotel is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with JHotel; if not, write to the Free Software
 *	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *
**/


import java.awt.Frame;

public class NewResGuest extends Frame {
	
	private ReservationManagement rm;
	private ResGuestList rgl;
	String[] language;
	private javax.swing.JLabel jLabel = null;
	private javax.swing.JLabel jLabel1 = null;
	private javax.swing.JLabel jLabel2 = null;
	private javax.swing.JTextField jTextField = null;
	private javax.swing.JTextField jTextField1 = null;
	private javax.swing.JTextField jTextField2 = null;
	private javax.swing.JButton jButton = null;
	private javax.swing.JButton jButton1 = null;
	/**
	 * This is the default constructor
	 */
	public NewResGuest(ReservationManagement rm, ResGuestList rgl) {
		this.rm = rm;
		this.rgl = rgl;
		
		Language lang = new Language();
		language = lang.getLanguage();
		initialize();
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setLayout(null);
		this.add(getJLabel(), null);
		this.add(getJLabel1(), null);
		this.add(getJLabel2(), null);
		this.add(getJTextField(), null);
		this.add(getJTextField1(), null);
		this.add(getJTextField2(), null);
		this.add(getJButton(), null);
		this.add(getJButton1(), null);
		this.setBounds(100, 100, 300, 200);
		this.setTitle(language[0]);
	}
	/**
	 * This method initializes jLabel
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel() {
		if(jLabel == null) {
			jLabel = new javax.swing.JLabel();
			jLabel.setBounds(17, 35, 97, 21);
			jLabel.setText(language[27]);
		}
		return jLabel;
	}
	/**
	 * This method initializes jLabel1
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel1() {
		if(jLabel1 == null) {
			jLabel1 = new javax.swing.JLabel();
			jLabel1.setBounds(17, 63, 97, 21);
			jLabel1.setText(language[29]);
		}
		return jLabel1;
	}
	/**
	 * This method initializes jLabel2
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel2() {
		if(jLabel2 == null) {
			jLabel2 = new javax.swing.JLabel();
			jLabel2.setBounds(17, 94, 97, 21);
			jLabel2.setText(language[28]);
		}
		return jLabel2;
	}
	/**
	 * This method initializes jTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField() {
		if(jTextField == null) {
			jTextField = new javax.swing.JTextField();
			jTextField.setBounds(121, 35, 164, 21);
			jTextField.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					rm.setGuest(jTextField.getText(), jTextField1.getText(), jTextField2.getText());
					rgl.dispose();
					dispose();
				}
			});
		}
		return jTextField;
	}
	/**
	 * This method initializes jTextField1
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField1() {
		if(jTextField1 == null) {
			jTextField1 = new javax.swing.JTextField();
			jTextField1.setBounds(121, 63, 164, 21);
			jTextField1.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					rm.setGuest(jTextField.getText(), jTextField1.getText(), jTextField2.getText());
					rgl.dispose();
					dispose();
				}
			});
		}
		return jTextField1;
	}
	/**
	 * This method initializes jTextField2
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField2() {
		if(jTextField2 == null) {
			jTextField2 = new javax.swing.JTextField();
			jTextField2.setBounds(121, 94, 164, 21);
			jTextField2.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					rm.setGuest(jTextField.getText(), jTextField1.getText(), jTextField2.getText());
					rgl.dispose();
					dispose();
				}
			});
		}
		return jTextField2;
	}
	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButton() {
		if(jButton == null) {
			jButton = new javax.swing.JButton();
			jButton.setBounds(173, 162, 111, 25);
			jButton.setText(language[21]);
			jButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					rm.setGuest(jTextField.getText(), jTextField1.getText(), jTextField2.getText());
					rgl.dispose();
					dispose();
				}
			});
		}
		return jButton;
	}
	/**
	 * This method initializes jButton1
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButton1() {
		if(jButton1 == null) {
			jButton1 = new javax.swing.JButton();
			jButton1.setBounds(8, 162, 111, 25);
			jButton1.setText(language[22]);
			jButton1.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					dispose();
					rgl.setVisible(true);
				}
			});
		}
		return jButton1;
	}
}
