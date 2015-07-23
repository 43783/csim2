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
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class SearchWindow extends Frame {

	private javax.swing.JButton jButton = null;
	private javax.swing.JTextField jTextField = null;
	private javax.swing.JLabel jLabel = null;
	private javax.swing.JLabel jLabel2 = null;
	private javax.swing.JButton jButton1 = null;
	private javax.swing.JButton jButton3 = null;
	public MainWindow mw;
	private javax.swing.JScrollPane jScrollPane = null;
	private javax.swing.JList jList = null;
	ArrayList data = new ArrayList();
	ArrayList db = new ArrayList();
	ArrayList sr = new ArrayList();
	String[] language;
	String dbname;
	int entries;
	
	public SearchWindow(MainWindow window) throws HeadlessException {
		this.mw = window;
		Language lang = new Language();
		language = lang.getLanguage();
		Options options = new Options();
		dbname = options.getFileName();
		
		initialize();
	// TODO Auto-generated constructor stub
	}

	/**
	 * @param gc
	 */
	public SearchWindow(GraphicsConfiguration gc) {
		super(gc);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param title
	 * @throws java.awt.HeadlessException
	 */
	public SearchWindow(String title) throws HeadlessException {
		super(title);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param title
	 * @param gc
	 */
	public SearchWindow(String title, GraphicsConfiguration gc) {
		super(title, gc);
		// TODO Auto-generated constructor stub
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
        this.setLayout(null);
        this.add(getJButton(), null);
        this.add(getJTextField(), null);
        this.add(getJLabel(), null);
        this.add(getJLabel2(), null);
        this.add(getJButton1(), null);
        this.add(getJButton3(), null);
        jButton3.setVisible(false);
        this.add(getJScrollPane(), null);
        this.setBounds(200, 200, 370, 439);
        this.setTitle(language[1]);
        this.setVisible(true);
        this.addWindowListener(new java.awt.event.WindowAdapter() { 
        	public void windowClosing(java.awt.event.WindowEvent e) {    
				jList.setModel(new DefaultListModel());
				clearFields();
				setVisible(false);
				mw.setEnabled(true);
        	}
        });
			
	}
	/**
	 * This method initializes jButton
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
				
		
		Guest gst = new Guest();
		if (gst.findGuest(val)) {
			jButton3.setVisible(true);
			jButton.setVisible(false);
		}
		else {
			jButton3.setVisible(false);
			jButton.setVisible(true);
		}

		
		
	}
	
	
	public void getGuest(String[] gst) {
		boolean isGuest = false;
			Guest guest = new Guest();
			String[] entry = new String[entries];


				entry = guest.getGuest(gst);
					
				mw.setGuestStatus(true);
				mw.setVisible(true);
				mw.setEnabled(true);
				mw.setGuest(entry);
				mw.setCurrentGuest();
				mw.setDeleted(true);
				clearFields();
				setVisible(false);
	
			}
	
	
	private javax.swing.JButton getJButton() {
		if(jButton == null) {
			jButton = new javax.swing.JButton();
			jButton.setBounds(218, 400, 133, 26);
			jButton.setText(language[21]);
			jButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					findGuest(jTextField.getText());
				}
			});
		}
		return jButton;
	}
	
	private javax.swing.JButton getJButton3() {
		if(jButton3 == null) {
			jButton3 = new javax.swing.JButton();
			jButton3.setBounds(250, 400, 133, 26);
			jButton3.setText(language[25]);
			jButton3.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					int index = jList.getSelectedIndex();
					loadGuest(index);					

				}
			});
		}
		return jButton3;
	}
	
	
	
	/**
	 * This method initializes jTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField() {
		if(jTextField == null) {
			jTextField = new javax.swing.JTextField();
			
			jTextField.setBounds(19, 72, 332, 21);
			jTextField.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					findGuest(jTextField.getText());
				}
			});
		}
		return jTextField;
	}
	/**
	 * This method initializes jLabel
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel() {
		if(jLabel == null) {
			jLabel = new javax.swing.JLabel();
			jLabel.setBounds(19, 48, 130, 25);
			jLabel.setText(language[40]);
		}
		return jLabel;
	}
	/**
	 * This method initializes jLabel2
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel2() {
		if(jLabel2 == null) {
			jLabel2 = new javax.swing.JLabel();
			jLabel2.setBounds(19, 96, 332, 21);
			jLabel2.setText("");
			jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		}
		return jLabel2;
	}
	/**
	 * This method initializes jButton1
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButton1() {
		if(jButton1 == null) {
			jButton1 = new javax.swing.JButton();
			jButton1.setBounds(19, 400, 133, 26);
			jButton1.setText(language[41]);
			jButton1.setVisible(true);
			jButton1.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					jList.setModel(new DefaultListModel());
					clearFields();
					setVisible(false);
					mw.setEnabled(true);
					

				}
			});
		}
		return jButton1;
	}
	
	public void clearFields() {
		jTextField.setText("");
		jLabel2.setText("");
	}
	
	public void setSearchField(boolean status) {
		jTextField.setEnabled(status);
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
			jScrollPane.setBounds(19, 127, 332, 252);
		}
		return jScrollPane;
	}
	/**
	 * This method initializes jList
	 * 
	 * @return javax.swing.JList
	 */
	
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
		clearFields();
		setVisible(false);

		jList.setModel(new DefaultListModel());
		jButton.setVisible(true);
		jButton3.setVisible(false);
	}

	
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
}  //  @jve:visual-info  decl-index=0 visual-constraint="10,10"
