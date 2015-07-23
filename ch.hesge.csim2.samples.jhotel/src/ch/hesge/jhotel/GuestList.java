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

public class GuestList extends Frame {
	
	private MainWindow mw;
	private javax.swing.JScrollPane jScrollPane = null;
	private javax.swing.JList jList = null;
	private javax.swing.JButton jButton = null;
	private javax.swing.JButton jButton1 = null;
	String[] language;
	int entries;
	ArrayList sr = new ArrayList();
	ArrayList db = new ArrayList();
	private javax.swing.JButton jButton2 = null;
	private javax.swing.JTextField jTextField = null;
	/**
	 * This is the default constructor
	 */
	public GuestList(MainWindow mw) {
		this.mw = mw;
		Language lang = new Language();
		language = lang.getLanguage();
		Guest guest = new Guest();
		this.entries = guest.getEntries();
		
		initialize();
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
		
	
	private void initialize() {
		this.setLayout(null);
		this.add(getJScrollPane(), null);
		this.add(getJButton(), null);
		this.add(getJButton1(), null);
		this.add(getJButton2(), null);
		this.add(getJTextField(), null);
		this.setBounds(200, 100, 370, 596);
		this.setTitle(language[49]);
		this.addWindowListener(new java.awt.event.WindowAdapter() { 
			public void windowClosing(java.awt.event.WindowEvent e) {    
				jList.setModel(new DefaultListModel());
			}
		});
		this.addWindowListener(new java.awt.event.WindowAdapter() { 
			public void windowClosing(java.awt.event.WindowEvent e) {    
				dispose();
			}
		});
	}
	
	public void getGuestDB() {
		jList.setModel(new DefaultListModel());
		DefaultListModel dlm = (DefaultListModel) jList.getModel();
		Guest guest = new Guest();
		int entries = guest.getEntries();
		ArrayList db = guest.getDB();
		String[] currentGuest;
		String tmp;
		int count = 0;
		
		for (int i=0; i<db.size(); ++i)  {
			currentGuest = (String[]) db.get(i);
			tmp = (currentGuest[0] + " - " + currentGuest[1] + ", " + currentGuest[2] + " ");
			sr.add(count, currentGuest);
			++count;
			dlm.addElement((Object) tmp);
		}
		
		jList.setModel(dlm);
	}

	
	/**
	 * 
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private javax.swing.JScrollPane getJScrollPane() {
		if(jScrollPane == null) {
			jScrollPane = new javax.swing.JScrollPane();
			jScrollPane.setViewportView(getJList());
			jScrollPane.setBounds(12, 55, 346, 488);
		}
		return jScrollPane;
	}
	/**
	 * This method initializes jList
	 * 
	 * @return javax.swing.JList
	 */
	private javax.swing.JList getJList() {
		if(jList == null) {
			jList = new javax.swing.JList();
			jList.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			MouseListener mouseListener = new MouseAdapter() {
				 public void mouseClicked(MouseEvent e) {
					 if (e.getClickCount() == 2) {
						int index = jList.locationToIndex(e.getPoint());
						loadGuest(index);
					  }
				 }
			 };
			 
			jList.addMouseListener(mouseListener);

		}
		return jList;
	}
	
	public void loadGuest(int index) {
		Guest guest = new Guest();
		String[] gst = new String[entries];
		String[] entry = new String[entries];
		gst = (String[]) sr.get(index);
		
		entry = guest.getGuest((String[]) sr.get(index));
					
		mw.setGuestStatus(true);
		mw.setVisible(true);
		mw.setEnabled(true);
		mw.setGuest(entry);
		mw.setCurrentGuest();
		mw.setDeleted(true);
		dispose();

		jList.setModel(new DefaultListModel());
	}

	
	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButton() {
		if(jButton == null) {
			jButton = new javax.swing.JButton();
			jButton.setBounds(243, 553, 113, 24);
			jButton.setText(language[20]);
			jButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					int index = jList.getSelectedIndex();
					if (index!=(-1)) {
						loadGuest(index);
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
			jButton1.setBounds(13, 553, 113, 24);
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
	 * This method initializes jButton2
	 * 
	 * @return javax.swing.JButton
	 */
	
	 
	public void findGuest(String val) {
		Guest guest = new Guest();
		db = guest.getDB();
		entries = guest.getEntries();
	
		boolean isGuest = false;
		jList.setModel(new DefaultListModel());
		DefaultListModel dlm = (DefaultListModel) jList.getModel();
		String[] currentGuest;
		int count = 0;
	
		loop1:
		for (int i=0; i<db.size(); ++i) {
		currentGuest = (String[]) db.get(i);
		
				
			loop2:
			for (int index=0; index<entries; ++index) {
				if (currentGuest[index].equalsIgnoreCase(val)) {
					String tmp = (currentGuest[0] + " - " + currentGuest[1] + ", " + currentGuest[2] + " ");
					sr.add(count, currentGuest);
					++count;
					dlm.addElement((Object) tmp);
				}
			}
		}
	}

	
	private javax.swing.JButton getJButton2() {
		if(jButton2 == null) {
			jButton2 = new javax.swing.JButton();
			jButton2.setText(language[1]);
			jButton2.setBounds(243, 28, 113, 20);
			jButton2.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {   
					findGuest(jTextField.getText());
				}
			});
		}
		return jButton2;
	}
	/**
	 * This method initializes jTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField() {
		if(jTextField == null) {
			jTextField = new javax.swing.JTextField();
			jTextField.setBounds(13, 28, 224, 20);
		}
		return jTextField;
	}
}  //  @jve:visual-info  decl-index=0 visual-constraint="10,10"
