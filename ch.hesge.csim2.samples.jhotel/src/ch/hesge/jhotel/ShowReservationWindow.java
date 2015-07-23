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
import java.util.*;

public class ShowReservationWindow extends Frame {

	private javax.swing.JLabel jLabel = null;
	private javax.swing.JLabel jLabel1 = null;
	private javax.swing.JLabel jLabel2 = null;
	private javax.swing.JLabel jLabel3 = null;
	private javax.swing.JLabel jLabel4 = null;
	private javax.swing.JButton jButton = null;
	private javax.swing.JButton jButton1 = null;
	private javax.swing.JTextField jTextField = null;
	private javax.swing.JTextField jTextField1 = null;
	private javax.swing.JTextField jTextField2 = null;
	private javax.swing.JTextField jTextField3 = null;
	private javax.swing.JTextField jTextField4 = null;
	
	private ReservationManagement rm;
	private String[] guest;
	String[] language;
	private String company, name, firstname, arrival, departure, room, checkedin, price;
	private String[] gst = new String[8];
	private ShowReservationWindow thisWindow;
	private boolean edited = false;
	private ArrayList reservations;
	private javax.swing.JLabel jLabel5 = null;
	private javax.swing.JTextField jTextField5 = null;
	private javax.swing.JButton jButton2 = null;
	private javax.swing.JButton jButton3 = null;
	private javax.swing.JLabel jLabel6 = null;
	private javax.swing.JRadioButton jRadioButton = null;
	private javax.swing.JRadioButton jRadioButton1 = null;
	private javax.swing.JLabel jLabel7 = null;
	private javax.swing.JTextField jTextField6 = null;
	private javax.swing.JLabel jLabel8 = null;
	boolean checkout = false;
	/**
	 * This is the default constructor
	 */
	
	public ShowReservationWindow(ReservationManagement rm, String[] guest, ArrayList reservations) {
		Language lang = new Language();
		language = lang.getLanguage();
		this.thisWindow = this;
		this.reservations = reservations;
		
		setGuest(guest);
		initialize();
	}
	
	public ShowReservationWindow(String[] guest) {
		Language lang = new Language();
		language = lang.getLanguage();
		setGuest(guest);
		this.checkout = true;
		initialize();
	}
	
	public ShowReservationWindow(ReservationManagement rm, Object guest, ArrayList reservations) {
		this.rm = rm;
		this.thisWindow = this;
		this.reservations = reservations;
		
		Language lang = new Language();
		language = lang.getLanguage();
		
		setGuest((String) guest);
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
		this.add(getJLabel3(), null);
		this.add(getJLabel4(), null);
		this.add(getJButton(), null);
		this.add(getJButton1(), null);
		this.add(getJTextField(), null);
		this.add(getJTextField1(), null);
		this.add(getJTextField2(), null);
		this.add(getJTextField3(), null);
		this.add(getJTextField4(), null);
		this.add(getJLabel5(), null);
		this.add(getJTextField5(), null);
		this.add(getJButton2(), null);
		this.add(getJButton3(), null);
		this.add(getJLabel6(), null);
		this.add(getJRadioButton(), null);
		this.add(getJRadioButton1(), null);
		this.add(getJLabel7(), null);
		this.add(getJTextField6(), null);
		this.add(getJLabel8(), null);
		this.setSize(314, 397);
		this.setTitle(language[82]);
		this.addWindowListener(new java.awt.event.WindowAdapter() { 
			public void windowClosing(java.awt.event.WindowEvent e) {    
				dispose();
			}
		});
		if (checkedin.equals("true")) {
			jRadioButton.setSelected(true);
			jRadioButton1.setSelected(false);
		}
		else {
			jRadioButton.setSelected(false);
			jRadioButton1.setSelected(true);
		}
	}
	/**
	 * This method initializes jLabel
	 * 
	 * @return javax.swing.JLabel
	 */
	
	public void setGuest(String[] guest) {
		company = guest[0];
		name = guest[1];
		firstname = guest[2];
		arrival = guest[3];
		departure = guest[4];
		room = guest[5];
		checkedin = guest[6];
		price = guest[7];
		
		gst = guest;
	}
	
	public void setGuest(String guest) {
		company = guest.substring(0, guest.indexOf('-')-1);
		name = guest.substring(guest.indexOf('-')+2, guest.indexOf(','));
		firstname = guest.substring(guest.indexOf(',')+2, guest.indexOf(':'));
		arrival = guest.substring(guest.indexOf(':')+2, guest.indexOf(';'));
		departure = guest.substring(guest.indexOf(';')+2, guest.lastIndexOf(';'));
		room = guest.substring(guest.lastIndexOf(';')+2, guest.lastIndexOf(':'));
		checkedin = guest.substring(guest.lastIndexOf(':') + 2, guest.indexOf('#'));
		price = guest.substring(guest.lastIndexOf('#') + 2);
		
		gst[0] = company;
		gst[1] = name;
		gst[2] = firstname;
		gst[3] = arrival;
		gst[4] = departure;
		gst[5] = room;
		gst[6] = checkedin;
		gst[7] = price;
	}
	
	private javax.swing.JLabel getJLabel() {
		if(jLabel == null) {
			jLabel = new javax.swing.JLabel();
			jLabel.setBounds(12, 65, 95, 21);
			jLabel.setText(language[60]);
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
			jLabel1.setBounds(12, 92, 95, 21);
			jLabel1.setText(language[61]);
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
			jLabel2.setBounds(12, 135, 95, 21);
			jLabel2.setText(language[27]);
		}
		return jLabel2;
	}
	/**
	 * This method initializes jLabel3
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel3() {
		if(jLabel3 == null) {
			jLabel3 = new javax.swing.JLabel();
			jLabel3.setBounds(12, 164, 95, 21);
			jLabel3.setText(language[29]);
		}
		return jLabel3;
	}
	/**
	 * This method initializes jLabel4
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel4() {
		if(jLabel4 == null) {
			jLabel4 = new javax.swing.JLabel();
			jLabel4.setBounds(12, 193, 95, 21);
			jLabel4.setText(language[28]);
		}
		return jLabel4;
	}
	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButton() {
		if(jButton == null) {
			jButton = new javax.swing.JButton();
			jButton.setBounds(206, 363, 98, 24);
			jButton.setText(language[21]);
			jButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					if (edited) {				
						Reservation res = new Reservation(rm);
						String[] newgst = new String[8];
						newgst[0] = jTextField2.getText();
						newgst[1] = jTextField3.getText();
						newgst[2] = jTextField4.getText();
						newgst[3] = jTextField.getText();
						newgst[4] = jTextField1.getText();
						newgst[5] = jTextField5.getText();
						newgst[6] = checkedin;
						newgst[7] = jTextField6.getText();
						
						jTextField.setEditable(false);
						jTextField1.setEditable(false);
						jTextField2.setEditable(false);
						jTextField3.setEditable(false);
						jTextField4.setEditable(false);
						jTextField5.setEditable(false);
						jTextField6.setEditable(false);
						jRadioButton.setEnabled(false);
						jRadioButton1.setEnabled(false);
						jButton3.setEnabled(false);
						
						edited = false;

						YesNoDialog ynd = new YesNoDialog(rm, thisWindow, newgst, gst, language[83], "changeRes");
						ynd.setVisible(true);
						
					}
					else {
						dispose();
					}
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
			jButton1.setBounds(11, 363, 117, 24);
			jButton1.setText(language[33]);
			if (checkout) {
				jButton1.setVisible(false);
			}
			jButton1.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					YesNoDialog ynd = new YesNoDialog(rm, thisWindow, gst, language[83], "deleteRes");
					ynd.setVisible(true);
				}
			});
		}
		return jButton1;
	}
	/**
	 * This method initializes jTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField() {
		if(jTextField == null) {
			jTextField = new javax.swing.JTextField();
			jTextField.setText(arrival);
			jTextField.setBounds(111, 65, 192, 21);
			jTextField.setEditable(false);
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
			jTextField1.setText(departure);
			jTextField1.setBounds(111, 92, 192, 21);
			jTextField1.setEditable(false);
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
			jTextField2.setText(company);
			jTextField2.setBounds(113, 135, 192, 21);
			jTextField2.setEditable(false);
		}
		return jTextField2;
	}
	/**
	 * This method initializes jTextField3
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField3() {
		if(jTextField3 == null) {
			jTextField3 = new javax.swing.JTextField();
			jTextField3.setText(name);
			jTextField3.setBounds(114, 164, 192, 21);
			jTextField3.setEditable(false);
		}
		return jTextField3;
	}
	/**
	 * This method initializes jTextField4
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField4() {
		if(jTextField4 == null) {
			jTextField4 = new javax.swing.JTextField();
			jTextField4.setText(firstname);
			jTextField4.setBounds(113, 193, 192, 21);
			jTextField4.setEditable(false);
		}
		return jTextField4;
	}
	/**
	 * This method initializes jLabel5
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel5() {
		if(jLabel5 == null) {
			jLabel5 = new javax.swing.JLabel();
			jLabel5.setBounds(12, 232, 95, 21);
			jLabel5.setText(language[44]);
		}
		return jLabel5;
	}
	/**
	 * This method initializes jTextField5
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField5() {
		if(jTextField5 == null) {
			jTextField5 = new javax.swing.JTextField();
			jTextField5.setText(room);
			jTextField5.setBounds(113, 231, 58, 21);
			jTextField5.setEditable(false);
		}
		return jTextField5;
	}
	/**
	 * This method initializes jButton2
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButton2() {
		if(jButton2 == null) {
			jButton2 = new javax.swing.JButton();
			jButton2.setBounds(207, 258, 98, 24);
			jButton2.setText(language[81]);
			if (checkout) {
				jButton2.setVisible(false);
			}
			jButton2.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					jTextField.setEditable(true);
					jTextField1.setEditable(true);
					jTextField2.setEditable(true);
					jTextField3.setEditable(true);
					jTextField4.setEditable(true);
					jTextField5.setEditable(true);
					jTextField6.setEditable(true);
					jRadioButton.setEnabled(true);
					jRadioButton1.setEnabled(true);
					jButton3.setEnabled(true);
					
					if (checkedin.equals("true")) {
						jRadioButton.setSelected(true);
						jRadioButton1.setSelected(false);
					}
					else {
						jRadioButton.setSelected(false);
						jRadioButton1.setSelected(true);
					}

					
					edited = true;
				}
			});
		}
		return jButton2;
	}
	/**
	 * This method initializes jButton3
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButton3() {
		if(jButton3 == null) {
			jButton3 = new javax.swing.JButton();
			jButton3.setBounds(176, 231, 21, 21);
			jButton3.setText("...");
			jButton3.setEnabled(false);
			jButton3.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {   
					Reservation res = new Reservation(rm);
					RoomSelectWindow2 rsm = new RoomSelectWindow2(rm, thisWindow, 1, res.createCal(jTextField.getText()), res.createCal(jTextField1.getText()), reservations);
					new Thread(rsm).start();
					rsm.setVisible(true);
				}
			});
		}
		return jButton3;
	}
	
	public void setSelectedRoom(String selRoom) {
		jTextField5.setText(selRoom);
	}
	/**
	 * This method initializes jLabel6
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel6() {
		if(jLabel6 == null) {
			jLabel6 = new javax.swing.JLabel();
			jLabel6.setBounds(13, 258, 94, 20);
			jLabel6.setText(language[92]);
		}
		return jLabel6;
	}
	/**
	 * This method initializes jRadioButton
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton() {
		if(jRadioButton == null) {
			jRadioButton = new javax.swing.JRadioButton();
			jRadioButton.setBounds(111, 258, 85, 20);
			jRadioButton.setBackground(java.awt.SystemColor.window);
			jRadioButton.setText(language[15]);
			jRadioButton.setEnabled(false);
			jRadioButton.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					checkedin = "true";
					jRadioButton1.setSelected(false);
				}
			});
		}
		return jRadioButton;
	}
	/**
	 * This method initializes jRadioButton1
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton1() {
		if(jRadioButton1 == null) {
			jRadioButton1 = new javax.swing.JRadioButton();
			jRadioButton1.setBounds(111, 285, 85, 20);
			jRadioButton1.setBackground(java.awt.SystemColor.window);
			jRadioButton1.setText(language[16]);
			jRadioButton1.setEnabled(false);
			jRadioButton1.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					checkedin = "false";
					jRadioButton.setSelected(false);
				}
			});
		}
		return jRadioButton1;
	}
	/**
	 * This method initializes jLabel7
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel7() {
		if(jLabel7 == null) {
			jLabel7 = new javax.swing.JLabel();
			jLabel7.setBounds(13, 311, 94, 20);
			jLabel7.setText(language[90]);
		}
		return jLabel7;
	}
	/**
	 * This method initializes jTextField6
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField6() {
		if(jTextField6 == null) {
			jTextField6 = new javax.swing.JTextField();
			jTextField6.setText(price);
			jTextField6.setBounds(114, 311, 97, 20);
			jTextField6.setEditable(false);
		}
		return jTextField6;
	}
	/**
	 * This method initializes jLabel8
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel8() {
		if(jLabel8 == null) {
			jLabel8 = new javax.swing.JLabel();
			jLabel8.setBounds(214, 311, 58, 20);
			jLabel8.setText(language[91]);
		}
		return jLabel8;
	}
}  //  @jve:visual-info  decl-index=0 visual-constraint="10,10"
