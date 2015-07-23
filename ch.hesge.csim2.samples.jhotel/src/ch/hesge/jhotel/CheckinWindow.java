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
import javax.swing.*;
import java.io.*;
import java.util.*;
import java.awt.event.*;

public class CheckinWindow extends Frame {

	private javax.swing.JTabbedPane jTabbedPane = null;
	private javax.swing.JScrollPane jScrollPane = null;
	private javax.swing.JScrollPane jScrollPane1 = null;
	private javax.swing.JList jList = null;
	private javax.swing.JList jList1 = null;
	private javax.swing.JButton jButton = null;
	private javax.swing.JButton jButton1 = null;
	private InitializeLists initializeLists;
	private String[] language;
	
	DefaultListModel dlm_checkin = new DefaultListModel();
	DefaultListModel dlm_checkout = new DefaultListModel();
	CheckinWindow thisWindow;
	MainWindow mw;
	ReservationManagement rm;
	ArrayList al;

	private javax.swing.JProgressBar jProgressBar = null;
	private javax.swing.JLabel jLabel = null;
	private javax.swing.JLabel jLabel1 = null;
	private javax.swing.JTextField jTextField = null;
	private javax.swing.JButton jButton2 = null;
	/**
	 * This is the default constructor
	 */
	public CheckinWindow(MainWindow mw) { //, ReservationManagement rm) {
		this.thisWindow = this;
		this.mw = mw;
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
		this.add(getJTabbedPane(), null);
		this.add(getJButton(), null);
		this.add(getJButton1(), null);
		this.add(getJProgressBar(), null);
		this.add(getJLabel(), null);
		this.add(getJLabel1(), null);
		this.add(getJTextField(), null);
		this.add(getJButton2(), null);
		this.setSize(383, 569);
		this.setBounds(200,10,383,569);
		this.setTitle(language[89]);
		this.addWindowListener(new java.awt.event.WindowAdapter() { 
			public void windowClosing(java.awt.event.WindowEvent e) {    
				dispose();
			}
		});
		
		InitializeLists initializeLists = new InitializeLists(thisWindow, dlm_checkin, dlm_checkout);
		this.initializeLists = initializeLists;
		initializeLists.start();
	}
	/**
	 * This method initializes jTabbedPane
	 * 
	 * @return javax.swing.JTabbedPane
	 */
	private javax.swing.JTabbedPane getJTabbedPane() {
		if(jTabbedPane == null) {
			jTabbedPane = new javax.swing.JTabbedPane();
			jTabbedPane.addTab(language[60], null, getJScrollPane(), null);
			jTabbedPane.addTab(language[61], null, getJScrollPane1(), null);
			jTabbedPane.setBounds(6, 27, 371, 477);
			jTabbedPane.setVisible(true);
		}
		return jTabbedPane;
	}
	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private javax.swing.JScrollPane getJScrollPane() {
		if(jScrollPane == null) {
			jScrollPane = new javax.swing.JScrollPane();
			jScrollPane.setViewportView(getJList());
		}
		return jScrollPane;
	}
	/**
	 * This method initializes jScrollPane1
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private javax.swing.JScrollPane getJScrollPane1() {
		if(jScrollPane1 == null) {
			jScrollPane1 = new javax.swing.JScrollPane();
			jScrollPane1.setViewportView(getJList1());
		}
		return jScrollPane1;
	}

	/**
	 * This method initializes jList
	 * 
	 * @return javax.swing.JList
	 */
	private javax.swing.JList getJList() {
		if(jList == null) {
			jList = new javax.swing.JList();
			jList.setModel(new DefaultListModel());
			dlm_checkin = (DefaultListModel) jList.getModel();
			MouseListener mouseListener = new MouseAdapter() {
				 public void mouseClicked(MouseEvent e) {
					 if (e.getClickCount() == 2) {
						int index = jList.locationToIndex(e.getPoint());
							String[] tmp = initializeLists.getCheckin(index);
							String[] tmp2 = initializeLists.getCheckin(index);
							tmp[6] = "true";
							
							Reservation res = new Reservation(thisWindow);
							res.checkinGuest(tmp2, tmp);
					  }
				 }
			 };
			 
			jList.addMouseListener(mouseListener);
		}
		return jList;
	}
	/**
	 * This method initializes jList1
	 * 
	 * @return javax.swing.JList
	 */
	private javax.swing.JList getJList1() {
		if(jList1 == null) {
			jList1 = new javax.swing.JList();
			jList1.setModel(new DefaultListModel());
			dlm_checkout = (DefaultListModel) jList1.getModel();
			MouseListener mouseListener = new MouseAdapter() {
				 public void mouseClicked(MouseEvent e) {
					 if (e.getClickCount() == 2) {
						int index = jList1.locationToIndex(e.getPoint());
							
							ShowReservationWindow srw = new ShowReservationWindow(initializeLists.getCheckout(index));
							srw.setVisible(true);
						
					  }
				 }
			 };
			 
			jList1.addMouseListener(mouseListener);
		}
		return jList1;
	}
	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButton() {
		if(jButton == null) {
			jButton = new javax.swing.JButton();
			jButton.setBounds(262, 536, 110, 23);
			jButton.setVisible(true);
			jButton.setText(language[21]);
			jButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					int index = jList.getSelectedIndex();
					if (index!=(-1)) {
						String[] tmp = initializeLists.getCheckin(index);
						String[] tmp2 = initializeLists.getCheckin(index);
						tmp[6] = "true";
							
						Reservation res = new Reservation(thisWindow);
						res.checkinGuest(tmp2, tmp);
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
			jButton1.setBounds(9, 536, 110, 23);
			jButton1.setVisible(true);
			jButton1.setText(language[22]);
			jButton1.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					dispose();
				}
			});
		}
		return jButton1;
	}
	/**
	 * This method initializes jProgressBar
	 * 
	 * @return javax.swing.JProgressBar
	 */
	private javax.swing.JProgressBar getJProgressBar() {
		if(jProgressBar == null) {
			jProgressBar = new javax.swing.JProgressBar();
			jProgressBar.setBounds(113, 536, 259, 23);
			jProgressBar.setVisible(false);
		}
		return jProgressBar;
	}
	/**
	 * This method initializes jLabel
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel() {
		if(jLabel == null) {
			jLabel = new javax.swing.JLabel();
			jLabel.setBounds(11, 536, 94, 23);
			jLabel.setText("JLabel");
			jLabel.setVisible(false);
		}
		return jLabel;
	}
	
	public void setThreadRunning(String label) {
		jProgressBar.setVisible(true);
		jProgressBar.setIndeterminate(true);
		jLabel.setVisible(true);
		jLabel.setText(label);
		jButton.setVisible(false);
		jButton1.setVisible(false);
		jLabel1.setVisible(false);
		jTextField.setVisible(false);
		jButton2.setVisible(false);
		
	}
	public void setThreadEnded() {
		jProgressBar.setVisible(false);
		jLabel.setVisible(false);
		jTabbedPane.setVisible(true);
		jButton.setVisible(true);
		jButton1.setVisible(true);
		jLabel1.setVisible(true);
		jTextField.setVisible(true);
		jButton2.setVisible(true);

		updateListsAfterCheckin();
	}
	public void setInitialized() {
		jProgressBar.setVisible(false);
		jLabel.setVisible(false);
		jTabbedPane.setVisible(true);
		jButton.setVisible(true);
		jButton1.setVisible(true);
		
		jLabel1.setVisible(true);
		jTextField.setVisible(true);
		jButton2.setVisible(true);
	}

	/**
	 * This method initializes jLabel1
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel1() {
		if(jLabel1 == null) {
			jLabel1 = new javax.swing.JLabel();
			jLabel1.setBounds(10, 509, 106, 23);
			jLabel1.setText(language[93]);
		}
		return jLabel1;
	}
	/**
	 * This method initializes jTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField() {
		if(jTextField == null) {
			jTextField = new javax.swing.JTextField();
			jTextField.setBounds(119, 509, 139, 23);
			jTextField.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					updateLists();
				}
			});
		}
		return jTextField;
	}
	/**
	 * This method initializes jButton2
	 * 
	 * @return javax.swing.JButton
	 */
	
	private void updateLists() {
		if ((!(jTextField.getText().equals("")))) {
			InitializeLists iL = new InitializeLists(thisWindow, dlm_checkin, dlm_checkout, jTextField.getText());
			this.initializeLists = iL;
			iL.start();
		}
	}
	
	private void updateListsAfterCheckin() {
		InitializeLists iL = new InitializeLists(thisWindow, dlm_checkin, dlm_checkout);
		this.initializeLists = iL;
		iL.updateLists(al);
	}
	
	public void setCheckinList(ArrayList al) {
		this.al = al;
	}
	
	private javax.swing.JButton getJButton2() {
		if(jButton2 == null) {
			jButton2 = new javax.swing.JButton();
			jButton2.setBounds(262, 509, 110, 23);
			jButton2.setText(language[25]);
			jButton2.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					updateLists();
				}
			});
		}
		return jButton2;
	}
}  //  @jve:visual-info  decl-index=0 visual-constraint="10,10"
