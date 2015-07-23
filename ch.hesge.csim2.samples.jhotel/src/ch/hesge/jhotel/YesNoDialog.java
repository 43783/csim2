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

public class YesNoDialog extends Frame {

	private javax.swing.JButton jButton = null;
	private javax.swing.JButton jButton1 = null;
	MainWindow mw;
	SearchWindow sw;
	ReservationManagement rm;
	ShowReservationWindow srw;
	String name, firstlabel, thirdlabel, action, firstname;
	String[] guest, oldguest, newguest;
	private javax.swing.JTextArea jTextArea = null;
	String text;
	String[] language;
	
	/**
	 * This is the default constructor
	 */
	public YesNoDialog(MainWindow mw, String[] guest, String text, String action) {
		Language lang = new Language();
		language = lang.getLanguage();
		this.mw = mw;
		this.guest = guest;
		this.action = action;
		this.text = text;
		
		initialize();
	}
	
	public YesNoDialog(ReservationManagement rm, ShowReservationWindow srw, String[] guest, String text, String action) {
		Language lang = new Language();
		language = lang.getLanguage();
		this.rm = rm;
		this.srw = srw;
		this.guest = guest;
		this.action = action;
		this.text = text;
		
		initialize();
	}
	
	public YesNoDialog(ReservationManagement rm, ShowReservationWindow srw, String[] newguest, String[] oldguest, String text, String action) {
		Language lang = new Language();
		language = lang.getLanguage();
		this.rm = rm;
		this.srw = srw;
		this.newguest = newguest;
		this.oldguest = oldguest;
		this.action = action;
		this.text = text;
		
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setLayout(null);
		this.add(getJButton(), null);
		this.add(getJButton1(), null);
		this.add(getJTextArea(), null);
		this.setBounds(366, 304, 295, 160);
		this.setTitle(language[48]);
		this.setResizable(false);
	}
	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButton() {
		if(jButton == null) {
			jButton = new javax.swing.JButton();
			jButton.setBounds(158, 118, 116, 26);
			jButton.setText(language[15]);
			jButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					if (action.equals("deleteEntry")) {
						mw.deleteEntry(guest);
					}
					else if (action.equals("undoEntry")) {
						mw.clearFields();
					}
					else if (action.equals("undoAddEntry")) {
						mw.addDataWindowReset(guest);
					}
					else if (action.equals("deleteRes")) {
						srw.dispose();
						Reservation res = new Reservation(rm);
						res.deleteReservation(guest);	
					}
					else if (action.equals("changeRes")) {
						srw.dispose();
						Reservation res = new Reservation(rm);
						res.changeReservation(oldguest, newguest);
					}
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
			jButton1.setBounds(22, 118, 116, 26);
			jButton1.setText(language[16]);
			jButton1.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					//mw.setGuest(guest);
					if (action.equals("undoEntry")) {
						mw.setGuest(guest);
					}
					
					
					dispose();

						
						



				}
			});
		}
		return jButton1;
	}
	
	
	/**
	 * This method initializes jTextArea
	 * 
	 * @return javax.swing.JTextArea
	 */
	private javax.swing.JTextArea getJTextArea() {
		if(jTextArea == null) {
			jTextArea = new javax.swing.JTextArea();
			jTextArea.setBounds(23, 28, 250, 84);
			jTextArea.setBackground(java.awt.SystemColor.window);
			jTextArea.setEditable(false);
			jTextArea.setText(text);
			jTextArea.setLineWrap(true);
			jTextArea.setWrapStyleWord(true);
		}
		return jTextArea;
	}
}  //  @jve:visual-info  decl-index=0 visual-constraint="10,10"
