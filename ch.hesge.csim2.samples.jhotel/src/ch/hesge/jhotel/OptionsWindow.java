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
import java.awt.event.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.io.*;
import java.util.*;

public class OptionsWindow extends Frame{

	private javax.swing.JLabel jLabel = null;
	private javax.swing.JTextField jTextField = null;
	private javax.swing.JRadioButton jRadioButton = null;
	private javax.swing.JRadioButton jRadioButton1 = null;
	private javax.swing.JScrollPane jScrollPane = null;
	private javax.swing.JButton jButton = null;
	private javax.swing.JPanel jPanel = null;
	private javax.swing.JButton jButton1 = null;
	private javax.swing.JButton jButton2 = null;
	boolean standard;
	MainWindow mw;
	Options options = new Options();
	String[] language;
	String language2;
	private int flrs;
	private int resolution;
	
	private ArrayList sr = new ArrayList();
	private ArrayList dr = new ArrayList();
	private ArrayList tr = new ArrayList();
	private ArrayList qr = new ArrayList();
	private ArrayList ap = new ArrayList();
	private String sr1 = "";
	private String sr2 = "";
	private String sr3 = "";
	private String sr4 = "";
	private String sr5 = "";
	private String sr6 = "";
	private String sr7 = "";
	private String sr8 = "";
	private String dr1 = "";
	private String dr2 = "";
	private String dr3 = "";
	private String dr4 = "";
	private String dr5 = "";
	private String dr6 = "";
	private String dr7 = "";
	private String dr8 = "";
	private String tr1 = "";
	private String tr2 = "";
	private String tr3 = "";
	private String tr4 = "";
	private String tr5 = "";
	private String tr6 = "";
	private String tr7 = "";
	private String tr8 = "";
	private String qr1 = "";
	private String qr2 = "";
	private String qr3 = "";
	private String qr4 = "";
	private String qr5 = "";
	private String qr6 = "";
	private String qr7 = "";
	private String qr8 = "";
	private String ap1 = "";
	private String ap2 = "";
	private String ap3 = "";
	private String ap4 = "";
	private String ap5 = "";
	private String ap6 = "";
	private String ap7 = "";
	private String ap8 = "";
	
	
	private javax.swing.JTree jTree = null;
	private javax.swing.JButton jButton3 = null;
	private javax.swing.JButton jButton4 = null;
	private javax.swing.JPanel jPanel1 = null;
	private javax.swing.JButton jButton5 = null;
	private javax.swing.JButton jButton6 = null;
	private javax.swing.JLabel jLabel1 = null;
	private javax.swing.JRadioButton jRadioButton2 = null;
	private javax.swing.JRadioButton jRadioButton3 = null;
	private javax.swing.JRadioButton jRadioButton4 = null;
	private javax.swing.JRadioButton jRadioButton5 = null;
	private javax.swing.JRadioButton jRadioButton6 = null;
	String[] settings;
	String[] newSettings;
	private String floors;
	private javax.swing.JPanel jPanel2 = null;  //  @jve:visual-info  decl-index=0 visual-constraint="151,67"
	private javax.swing.JButton jButton7 = null;
	private javax.swing.JButton jButton8 = null;
	private javax.swing.JRadioButton jRadioButton7 = null;
	private javax.swing.JRadioButton jRadioButton8 = null;
	private javax.swing.JRadioButton jRadioButton9 = null;
	private javax.swing.JRadioButton jRadioButton10 = null;
	private javax.swing.JRadioButton jRadioButton11 = null;
	private javax.swing.JRadioButton jRadioButton12 = null;
	private javax.swing.JRadioButton jRadioButton13 = null;
	private javax.swing.JRadioButton jRadioButton14 = null;
	private javax.swing.JRadioButton jRadioButton15 = null;
	private javax.swing.JTextField jTextField1 = null;
	private javax.swing.JPanel jPanel3 = null;
	private javax.swing.JRadioButton jRadioButton16 = null;
	private javax.swing.JRadioButton jRadioButton17 = null;
	private javax.swing.JRadioButton jRadioButton18 = null;
	private javax.swing.JRadioButton jRadioButton19 = null;
	private javax.swing.JRadioButton jRadioButton20 = null;
	private javax.swing.JRadioButton jRadioButton21 = null;
	private javax.swing.JRadioButton jRadioButton22 = null;
	private javax.swing.JRadioButton jRadioButton23 = null;
	private javax.swing.JLabel jLabel2 = null;
	private javax.swing.JTextField jTextField2 = null;
	private javax.swing.JLabel jLabel3 = null;
	private javax.swing.JTextField jTextField3 = null;
	private javax.swing.JLabel jLabel4 = null;
	private javax.swing.JTextField jTextField4 = null;
	private javax.swing.JLabel jLabel5 = null;
	private javax.swing.JTextField jTextField5 = null;
	private javax.swing.JLabel jLabel6 = null;
	private javax.swing.JTextField jTextField6 = null;
	private javax.swing.JButton jButton9 = null;
	private javax.swing.JButton jButton10 = null;
	private javax.swing.JTextField jTextField7 = null;
	private javax.swing.JTextField jTextField8 = null;
	private javax.swing.JTextField jTextField9 = null;
	private javax.swing.JTextField jTextField10 = null;
	private javax.swing.JTextField jTextField11 = null;
	private javax.swing.JTextField jTextField12 = null;
	private javax.swing.JTextField jTextField13 = null;
	private javax.swing.JTextField jTextField14 = null;
	private javax.swing.JTextField jTextField15 = null;
	private javax.swing.JTextField jTextField16 = null;
	private javax.swing.JTextField jTextField17 = null;
	private javax.swing.JTextField jTextField18 = null;
	private javax.swing.JTextField jTextField19 = null;
	private javax.swing.JTextField jTextField20 = null;
	private javax.swing.JTextField jTextField21 = null;
	private javax.swing.JTextField jTextField22 = null;
	private javax.swing.JTextField jTextField23 = null;
	private javax.swing.JTextField jTextField24 = null;
	private javax.swing.JTextField jTextField25 = null;
	private javax.swing.JTextField jTextField26 = null;
	private javax.swing.JTextField jTextField27 = null;
	private javax.swing.JTextField jTextField28 = null;
	private javax.swing.JTextField jTextField29 = null;
	private javax.swing.JTextField jTextField30 = null;
	private javax.swing.JTextField jTextField31 = null;
	private javax.swing.JTextField jTextField32 = null;
	private javax.swing.JTextField jTextField33 = null;
	private javax.swing.JTextField jTextField34 = null;
	private javax.swing.JTextField jTextField35 = null;
	private javax.swing.JTextField jTextField36 = null;
	private javax.swing.JTextField jTextField37 = null;
	private javax.swing.JTextField jTextField38 = null;
	private javax.swing.JTextField jTextField39 = null;
	private javax.swing.JTextField jTextField40 = null;
	private javax.swing.JTextField jTextField41 = null;
	private javax.swing.JPanel jPanel4 = null;
	private javax.swing.JRadioButton jRadioButton24 = null;
	private javax.swing.JRadioButton jRadioButton25 = null;
	private javax.swing.JRadioButton jRadioButton26 = null;
	private javax.swing.JButton jButton11 = null;
	private javax.swing.JButton jButton12 = null;
	/**
	 * This is the default constructor
	 */
	public OptionsWindow(MainWindow mw) {
		Language lang = new Language();
		language = lang.getLanguage();
		getRooms();
		
		this.settings = options.getSettings();
		this.floors = settings[2];
		this.mw = mw;
		initialize();
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setLayout(null);
		this.setLayout(null);
		this.add(getJLabel(), null);
		this.add(getJScrollPane(), null);
		this.add(getJPanel(), null);
		this.add(getJButton2(), null);
		this.add(getJButton1(), null);
		this.add(getJPanel1(), null);
		this.add(getJPanel2(), null);
		this.add(getJPanel3(), null);
		this.add(getJPanel4(), null);
		this.setBounds(150, 150, 537, 275);
		this.setTitle(language[6]);
		this.setVisible(true);
		
		
		if (settings[0].equals("./db/guestDB")) {
			jRadioButton.setSelected(true);
			jRadioButton1.setSelected(false);
		}
		else {
			jRadioButton1.setSelected(true);
			jRadioButton.setSelected(false);
			jTextField.setText(settings[0]);
		}
		if (settings[1].equals("Deutsch")) {
			jRadioButton2.setSelected(true);
		}
		else if (settings[1].equals("English")) {
			jRadioButton3.setSelected(true);
		}
		else if (settings[1].equals("Francais")) {
			jRadioButton4.setSelected(true);
		}
		else if (settings[1].equals("Espagnol")) {
			jRadioButton5.setSelected(true);
		}
		else if (settings[1].equals("Polski")) {
			jRadioButton6.setSelected(true);
		}
		if (settings[2].equals("1")) {
			jRadioButton7.setSelected(true);
		}
		else if (settings[2].equals("2")) {
			jRadioButton8.setSelected(true);
		}
		else if (settings[2].equals("3")) {
			jRadioButton9.setSelected(true);
		}
		else if (settings[2].equals("4")) {
			jRadioButton10.setSelected(true);
		}
		else if (settings[2].equals("5")) {
			jRadioButton11.setSelected(true);
		}
		else if (settings[2].equals("6")) {
			jRadioButton12.setSelected(true);
		}
		else if (settings[2].equals("7")) {
			jRadioButton13.setSelected(true);
		}
		else if (settings[2].equals("8")) {
			jRadioButton14.setSelected(true);
		}
		else {
			jRadioButton15.setSelected(true);
			jTextField1.setText(settings[2]);
		}
		
		if (settings[3].equals("600")) {
			jRadioButton24.setSelected(true);
		}
		else if (settings[3].equals("768")) {
			jRadioButton25.setSelected(true);
		}
		else if (settings[3].equals("960")) {
			jRadioButton26.setSelected(true);
		}
		
		this.addWindowListener(new java.awt.event.WindowAdapter() { 
			public void windowClosing(java.awt.event.WindowEvent e) {    
				dispose();
			}
		});
		
		
	}
	/**
	 * This method initializes jLabel
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel() {
		if(jLabel == null) {
			jLabel = new javax.swing.JLabel();
			jLabel.setBounds(140, 33, 382, 20);
			jLabel.setText("");
			jLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		}
		return jLabel;
	}
	/**
	 * This method initializes jTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField() {
		if(jTextField == null) {
			jTextField = new javax.swing.JTextField();
			jTextField.setBounds(97, 62, 275, 23);
			jTextField.setEnabled(false);
		}
		return jTextField;
	}
	/**
	 * This method initializes jRadioButton
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton() {
		if(jRadioButton == null) {
			jRadioButton = new javax.swing.JRadioButton();
			jRadioButton.setBounds(10, 37, 80, 23);
			jRadioButton.setText(language[37]);
			jRadioButton.setBackground(java.awt.SystemColor.window);
			jRadioButton.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jTextField.setEnabled(false);
					jButton.setEnabled(false);
					jRadioButton1.setSelected(false);
					standard = true;


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
			jRadioButton1.setBounds(10, 62, 82, 23);
			jRadioButton1.setText(language[38]);
			jRadioButton1.setBackground(java.awt.SystemColor.window);
			jRadioButton1.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					
					jTextField.setEnabled(true);
					jButton.setEnabled(true);
					jRadioButton.setSelected(false);
					standard = false;

				}
			});
		}
		return jRadioButton1;
	}
	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private javax.swing.JScrollPane getJScrollPane() {
		if(jScrollPane == null) {
			jScrollPane = new javax.swing.JScrollPane();
			jScrollPane.setViewportView(getJTree());
			jScrollPane.setBounds(9, 32, 126, 230);
		}
		return jScrollPane;
	}
	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButton() {
		if(jButton == null) {
			jButton = new javax.swing.JButton();
			jButton.setBounds(256, 35, 115, 25);
			jButton.setText(language[23]);
			jButton.setEnabled(false);
			jButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					JFileChooser chooser = new JFileChooser("./db");
					int returnVal = chooser.showOpenDialog(OptionsWindow.this);
					if(returnVal == JFileChooser.APPROVE_OPTION) {
					jTextField.setText(chooser.getSelectedFile().getPath());
}

				}
			});
		}
		return jButton;
	}
	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJPanel() {
		if(jPanel == null) {
			jPanel = new javax.swing.JPanel();
			jPanel.setLayout(null);
			jPanel.add(getJRadioButton(), null);
			jPanel.add(getJRadioButton1(), null);
			jPanel.add(getJTextField(), null);
			jPanel.add(getJButton(), null);
			jPanel.add(getJButton3(), null);
			jPanel.add(getJButton4(), null);
			jPanel.setBounds(141, 57, 390, 212);
			jPanel.setBackground(java.awt.SystemColor.window);
			jPanel.setVisible(false);
		}
		return jPanel;
	}
	/**
	 * This method initializes jButton1
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButton1() {
		if(jButton1 == null) {
			jButton1 = new javax.swing.JButton();
			jButton1.setBounds(424, 233, 98, 25);
			jButton1.setText(language[2]);
			jButton1.setVisible(true);
			jButton1.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					options.setSettings(settings);
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
	
	public String getFileName() {
		if (standard) {
			return "./db/guestDB";
		}
		else {
			return jTextField.getText();
		}
	}
	public void setFileName() {
		
	}
	
	
	private javax.swing.JButton getJButton2() {
		if(jButton2 == null) {
			jButton2 = new javax.swing.JButton();
			jButton2.setBounds(310, 233, 108, 25);
			jButton2.setText(language[22]);
			jButton2.setVisible(true);
			jButton2.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					dispose();
				}
			});
		}
		return jButton2;
	}
	/**
	 * This method initializes jTree
	 * 
	 * @return javax.swing.JTree
	 */
	private javax.swing.JTree getJTree() {
		if(jTree == null) {
			
			DefaultMutableTreeNode top = new DefaultMutableTreeNode("Root");
			DefaultMutableTreeNode next = new DefaultMutableTreeNode(language[35]);
			top.add(next);
			next = new DefaultMutableTreeNode(language[36]);
			top.add(next);
			next = new DefaultMutableTreeNode(language[51]);
			top.add(next);
			next = new DefaultMutableTreeNode(language[54]);
			top.add(next);
			next = new DefaultMutableTreeNode(language[67]);
			top.add(next);

			jTree = new javax.swing.JTree(top);
			
			MouseListener ml = new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					int selRow = jTree.getRowForLocation(e.getX(), e.getY());
					TreePath selPath = jTree.getPathForLocation(e.getX(), e.getY());
					if(selRow != -1) {
						int index = jTree.getClosestRowForLocation(e.getX(), e.getY());
						
						if (index == 0) {
							jPanel4.setVisible(false);
							jPanel.setVisible(true);
							jPanel1.setVisible(false);
							jPanel2.setVisible(false);
							jPanel3.setVisible(false);
							jButton1.setVisible(false);
							jButton2.setVisible(false);
							jLabel.setText(language[35]);
						}
						else if (index==1) {
							jPanel4.setVisible(false);
							jPanel1.setVisible(true);
							jPanel.setVisible(false);
							jPanel2.setVisible(false);
							jPanel3.setVisible(false);
							jButton1.setVisible(false);
							jButton2.setVisible(false);
							jLabel.setText(language[36]);
						}
						else if (index==2) {
							jPanel4.setVisible(false);
							jPanel1.setVisible(false);
							jPanel.setVisible(false);
							jPanel2.setVisible(true);
							jPanel3.setVisible(false);
							jButton1.setVisible(false);
							jButton2.setVisible(false);
							jLabel.setText(language[51]);
						}
						else if (index==3)  {
							jPanel4.setVisible(false);
							jPanel3.setVisible(true);
							jPanel2.setVisible(false);
							jPanel1.setVisible(false);
							jPanel.setVisible(false);
							jButton1.setVisible(false);
							jButton2.setVisible(false);
							jLabel.setText(language[54]);
						}
						else if (index==4) {
							jPanel.setVisible(false);
							jPanel1.setVisible(false);
							jPanel2.setVisible(false);
							jPanel3.setVisible(false);
							jPanel4.setVisible(true);
							jButton1.setVisible(false);
							jButton2.setVisible(false);
							jLabel.setText(language[67]);
						}
					}
				}
			};
			jTree.addMouseListener(ml);
			
			
			jTree.setRootVisible(false);
			jTree.setToggleClickCount(1);
		}
		return jTree;
	}
	
		
		
	/**
	 * This method initializes jButton3
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButton3() {
		if(jButton3 == null) {
			jButton3 = new javax.swing.JButton();
			jButton3.setBounds(283, 176, 98, 25);
			jButton3.setText(language[21]);
			jButton3.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					jPanel.setVisible(false);
					settings[0] = getFileName();
				}
			});
		}
		return jButton3;
	}
	/**
	 * This method initializes jButton4
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButton4() {
		if(jButton4 == null) {
			jButton4 = new javax.swing.JButton();
			jButton4.setBounds(169, 176, 108, 25);
			jButton4.setText(language[22]);
			jButton4.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					jPanel.setVisible(false);
				}
			});
		}
		return jButton4;
	}
	/**
	 * This method initializes jPanel1
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJPanel1() {
		if(jPanel1 == null) {
			jPanel1 = new javax.swing.JPanel();
			jPanel1.setLayout(null);
			jPanel1.add(getJButton5(), null);
			jPanel1.add(getJButton6(), null);
			jPanel1.add(getJLabel1(), null);
			jPanel1.add(getJRadioButton2(), null);
			jPanel1.add(getJRadioButton3(), null);
			jPanel1.add(getJRadioButton4(), null);
			jPanel1.add(getJRadioButton5(), null);
			jPanel1.add(getJRadioButton6(), null);
			jPanel1.setBounds(141, 57, 390, 212);
			jPanel1.setVisible(false);
			jPanel1.setBackground(java.awt.SystemColor.window);
		}
		return jPanel1;
	}
	/**
	 * This method initializes jButton5
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButton5() {
		if(jButton5 == null) {
			jButton5 = new javax.swing.JButton();
			jButton5.setBounds(169, 176, 108, 25);
			jButton5.setText(language[22]);
			jButton5.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					jPanel1.setVisible(false);
					jButton1.setVisible(true);
					jButton2.setVisible(true);
				}
			});
		}
		return jButton5;
	}
	/**
	 * This method initializes jButton6
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButton6() {
		if(jButton6 == null) {
			jButton6 = new javax.swing.JButton();
			jButton6.setBounds(283, 176, 98, 25);
			jButton6.setText(language[21]);
			jButton6.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					settings[1] = language2;
					jPanel1.setVisible(false);
					jButton1.setVisible(true);
					jButton2.setVisible(true);
				}
			});
		}
		return jButton6;
	}
	/**
	 * This method initializes jLabel1
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel1() {
		if(jLabel1 == null) {
			jLabel1 = new javax.swing.JLabel();
			jLabel1.setBounds(10, 11, 370, 20);
			jLabel1.setText(language[39]);
		}
		return jLabel1;
	}
	/**
	 * This method initializes jRadioButton2
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton2() {
		if(jRadioButton2 == null) {
			jRadioButton2 = new javax.swing.JRadioButton();
			jRadioButton2.setBounds(10, 55, 161, 18);
			jRadioButton2.setText("Deutsch");
			jRadioButton2.setBackground(java.awt.SystemColor.window);
			jRadioButton2.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton3.setSelected(false);
					jRadioButton4.setSelected(false);
					jRadioButton5.setSelected(false);
					jRadioButton6.setSelected(false);
					language2 = "Deutsch";
					
					
				}
			});
		}
		return jRadioButton2;
	}
	/**
	 * This method initializes jRadioButton3
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton3() {
		if(jRadioButton3 == null) {
			jRadioButton3 = new javax.swing.JRadioButton();
			jRadioButton3.setBounds(10, 82, 161, 18);
			jRadioButton3.setText("English");
			jRadioButton3.setBackground(java.awt.SystemColor.window);
			jRadioButton3.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton2.setSelected(false);
					jRadioButton4.setSelected(false);
					jRadioButton5.setSelected(false);
					jRadioButton6.setSelected(false);
					
					language2 = "English";

				}
			});
		}
		return jRadioButton3;
	}
	/**
	 * This method initializes jRadioButton4
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton4() {
		if(jRadioButton4 == null) {
			jRadioButton4 = new javax.swing.JRadioButton();
			jRadioButton4.setBounds(10, 107, 161, 18);
			jRadioButton4.setText("Francais");
			jRadioButton4.setBackground(java.awt.SystemColor.window);
			jRadioButton4.setEnabled(false);
			jRadioButton4.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton2.setSelected(false);
					jRadioButton3.setSelected(false);
					jRadioButton5.setSelected(false);
					jRadioButton6.setSelected(false);

					language2 = "Francais";

				}
			});
		}
		return jRadioButton4;
	}
	/**
	 * This method initializes jRadioButton5
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton5() {
		if(jRadioButton5 == null) {
			jRadioButton5 = new javax.swing.JRadioButton();
			jRadioButton5.setBounds(10, 133, 161, 18);
			jRadioButton5.setText("Espagnol");
			jRadioButton5.setBackground(java.awt.SystemColor.window);
			jRadioButton5.setEnabled(false);
			jRadioButton5.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton2.setSelected(false);
					jRadioButton4.setSelected(false);
					jRadioButton3.setSelected(false);
					jRadioButton6.setSelected(false);
					
					language2 = "Espagnol";


				}
			});
		}
		return jRadioButton5;
	}
	/**
	 * This method initializes jRadioButton6
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton6() {
		if(jRadioButton6 == null) {
			jRadioButton6 = new javax.swing.JRadioButton();
			jRadioButton6.setBounds(10, 158, 161, 18);
			jRadioButton6.setText("Polski");
			jRadioButton6.setBackground(java.awt.SystemColor.window);
			jRadioButton6.setEnabled(false);
			jRadioButton6.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton2.setSelected(false);
					jRadioButton4.setSelected(false);
					jRadioButton5.setSelected(false);
					jRadioButton3.setSelected(false);
					
					language2 = "Polski";

				}
			});
		}
		return jRadioButton6;
	}
	/**
	 * This method initializes jPanel2
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJPanel2() {
		if(jPanel2 == null) {
			jPanel2 = new javax.swing.JPanel();
			jPanel2.setLayout(null);
			jPanel2.setVisible(false);
			jPanel2.add(getJButton7(), null);
			jPanel2.add(getJButton8(), null);
			jPanel2.add(getJRadioButton7(), null);
			jPanel2.add(getJRadioButton8(), null);
			jPanel2.add(getJRadioButton9(), null);
			jPanel2.add(getJRadioButton10(), null);
			jPanel2.add(getJRadioButton11(), null);
			jPanel2.add(getJRadioButton12(), null);
			jPanel2.add(getJRadioButton13(), null);
			jPanel2.add(getJRadioButton14(), null);
			jPanel2.add(getJRadioButton15(), null);
			jPanel2.add(getJTextField1(), null);
			jPanel2.setSize(390, 212);
			jPanel2.setBackground(java.awt.SystemColor.window);
			jPanel2.setLocation(141, 57);
		}
		return jPanel2;
	}
	/**
	 * This method initializes jButton7
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButton7() {
		if(jButton7 == null) {
			jButton7 = new javax.swing.JButton();
			jButton7.setBounds(283, 176, 98, 25);
			jButton7.setText(language[21]);
			jButton7.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					if (floors.equals("N/A")) {
						floors = jTextField1.getText();
					}
					settings[2]  = floors;
					jPanel2.setVisible(false);
					jButton1.setVisible(true);
					jButton2.setVisible(true);
				}
			});
		}
		return jButton7;
	}
	/**
	 * This method initializes jButton8
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButton8() {
		if(jButton8 == null) {
			jButton8 = new javax.swing.JButton();
			jButton8.setBounds(169, 176, 108, 25);
			jButton8.setText(language[22]);
			jButton8.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					jPanel2.setVisible(false);
				}
			});
		}
		return jButton8;
	}
	/**
	 * This method initializes jRadioButton7
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton7() {
		if(jRadioButton7 == null) {
			jRadioButton7 = new javax.swing.JRadioButton();
			jRadioButton7.setBounds(40, 33, 118, 20);
			jRadioButton7.setBackground(java.awt.SystemColor.window);
			jRadioButton7.setText("1");
			jRadioButton7.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton8.setSelected(false);
					jRadioButton9.setSelected(false);
					jRadioButton10.setSelected(false);
					jRadioButton11.setSelected(false);
					jRadioButton12.setSelected(false);
					jRadioButton13.setSelected(false);
					jRadioButton14.setSelected(false);
					jRadioButton15.setSelected(false);
					jTextField1.setEnabled(false);
					
					floors = "1";
				}
			});
		}
		return jRadioButton7;
	}
	/**
	 * This method initializes jRadioButton8
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton8() {
		if(jRadioButton8 == null) {
			jRadioButton8 = new javax.swing.JRadioButton();
			jRadioButton8.setBounds(40, 59, 118, 20);
			jRadioButton8.setBackground(java.awt.SystemColor.window);
			jRadioButton8.setText("2");
			jRadioButton8.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton7.setSelected(false);
					jRadioButton9.setSelected(false);
					jRadioButton10.setSelected(false);
					jRadioButton11.setSelected(false);
					jRadioButton12.setSelected(false);
					jRadioButton13.setSelected(false);
					jRadioButton14.setSelected(false);
					jRadioButton15.setSelected(false);
					jTextField1.setEnabled(false);
					
					floors = "2";

				}
			});
		}
		return jRadioButton8;
	}
	/**
	 * This method initializes jRadioButton9
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton9() {
		if(jRadioButton9 == null) {
			jRadioButton9 = new javax.swing.JRadioButton();
			jRadioButton9.setBounds(40, 86, 118, 20);
			jRadioButton9.setBackground(java.awt.SystemColor.window);
			jRadioButton9.setText("3");
			jRadioButton9.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton8.setSelected(false);
					jRadioButton7.setSelected(false);
					jRadioButton10.setSelected(false);
					jRadioButton11.setSelected(false);
					jRadioButton12.setSelected(false);
					jRadioButton13.setSelected(false);
					jRadioButton14.setSelected(false);
					jRadioButton15.setSelected(false);
					jTextField1.setEnabled(false);
					
					floors = "3";

				}
			});
		}
		return jRadioButton9;
	}
	/**
	 * This method initializes jRadioButton10
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton10() {
		if(jRadioButton10 == null) {
			jRadioButton10 = new javax.swing.JRadioButton();
			jRadioButton10.setBounds(40, 113, 118, 20);
			jRadioButton10.setBackground(java.awt.SystemColor.window);
			jRadioButton10.setText("4");
			jRadioButton10.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton8.setSelected(false);
					jRadioButton9.setSelected(false);
					jRadioButton7.setSelected(false);
					jRadioButton11.setSelected(false);
					jRadioButton12.setSelected(false);
					jRadioButton13.setSelected(false);
					jRadioButton14.setSelected(false);
					jRadioButton15.setSelected(false);
					jTextField1.setEnabled(false);
					
					floors = "4";

				}
			});
		}
		return jRadioButton10;
	}
	/**
	 * This method initializes jRadioButton11
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton11() {
		if(jRadioButton11 == null) {
			jRadioButton11 = new javax.swing.JRadioButton();
			jRadioButton11.setBounds(188, 33, 118, 20);
			jRadioButton11.setBackground(java.awt.SystemColor.window);
			jRadioButton11.setText("5");
			jRadioButton11.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton8.setSelected(false);
					jRadioButton9.setSelected(false);
					jRadioButton10.setSelected(false);
					jRadioButton7.setSelected(false);
					jRadioButton12.setSelected(false);
					jRadioButton13.setSelected(false);
					jRadioButton14.setSelected(false);
					jRadioButton15.setSelected(false);
					jTextField1.setEnabled(false);
					
					floors = "5";

				}
			});
		}
		return jRadioButton11;
	}
	/**
	 * This method initializes jRadioButton12
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton12() {
		if(jRadioButton12 == null) {
			jRadioButton12 = new javax.swing.JRadioButton();
			jRadioButton12.setBounds(188, 59, 118, 20);
			jRadioButton12.setBackground(java.awt.SystemColor.window);
			jRadioButton12.setText("6");
			jRadioButton12.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton8.setSelected(false);
					jRadioButton9.setSelected(false);
					jRadioButton10.setSelected(false);
					jRadioButton11.setSelected(false);
					jRadioButton7.setSelected(false);
					jRadioButton13.setSelected(false);
					jRadioButton14.setSelected(false);
					jRadioButton15.setSelected(false);
					jTextField1.setEnabled(false);
					
					floors = "6";

				}
			});
		}
		return jRadioButton12;
	}
	/**
	 * This method initializes jRadioButton13
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton13() {
		if(jRadioButton13 == null) {
			jRadioButton13 = new javax.swing.JRadioButton();
			jRadioButton13.setBounds(188, 86, 118, 20);
			jRadioButton13.setBackground(java.awt.SystemColor.window);
			jRadioButton13.setText("7");
			jRadioButton13.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton8.setSelected(false);
					jRadioButton9.setSelected(false);
					jRadioButton10.setSelected(false);
					jRadioButton11.setSelected(false);
					jRadioButton12.setSelected(false);
					jRadioButton7.setSelected(false);
					jRadioButton14.setSelected(false);
					jRadioButton15.setSelected(false);
					jTextField1.setEnabled(false);
					
					floors = "7";

				}
			});
		}
		return jRadioButton13;
	}
	/**
	 * This method initializes jRadioButton14
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton14() {
		if(jRadioButton14 == null) {
			jRadioButton14 = new javax.swing.JRadioButton();
			jRadioButton14.setBounds(188, 113, 118, 20);
			jRadioButton14.setBackground(java.awt.SystemColor.window);
			jRadioButton14.setText("8");
			jRadioButton14.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton8.setSelected(false);
					jRadioButton9.setSelected(false);
					jRadioButton10.setSelected(false);
					jRadioButton11.setSelected(false);
					jRadioButton12.setSelected(false);
					jRadioButton13.setSelected(false);
					jRadioButton7.setSelected(false);
					jRadioButton15.setSelected(false);
					jTextField1.setEnabled(false);
					
					floors = "8";

				}
			});
		}
		return jRadioButton14;
	}
	/**
	 * This method initializes jRadioButton15
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton15() {
		if(jRadioButton15 == null) {
			jRadioButton15 = new javax.swing.JRadioButton();
			jRadioButton15.setText(language[38]);
			jRadioButton15.setBounds(40, 140, 40, 20);
			jRadioButton15.setBackground(java.awt.SystemColor.window);
			jRadioButton15.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton8.setSelected(false);
					jRadioButton9.setSelected(false);
					jRadioButton10.setSelected(false);
					jRadioButton11.setSelected(false);
					jRadioButton12.setSelected(false);
					jRadioButton13.setSelected(false);
					jRadioButton14.setSelected(false);
					jRadioButton7.setSelected(false);
					jTextField1.setEnabled(true);

					floors = "N/A";
				}
			});
		}
		return jRadioButton15;
	}
	/**
	 * This method initializes jTextField1
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField1() {
		if(jTextField1 == null) {
			jTextField1 = new javax.swing.JTextField();
			jTextField1.setEnabled(false);
			jTextField1.setBounds(85, 140, 38, 20);
		}
		return jTextField1;
	}
	/**
	 * This method initializes jPanel3
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJPanel3() {
		if(jPanel3 == null) {
			jPanel3 = new javax.swing.JPanel();
			jPanel3.setLayout(null);
			jPanel3.setVisible(false);
			jPanel3.add(getJRadioButton16(), null);
			jPanel3.add(getJRadioButton17(), null);
			jPanel3.add(getJRadioButton18(), null);
			jPanel3.add(getJRadioButton19(), null);
			jPanel3.add(getJRadioButton20(), null);
			jPanel3.add(getJRadioButton21(), null);
			jPanel3.add(getJRadioButton22(), null);
			jPanel3.add(getJRadioButton23(), null);
			jPanel3.add(getJLabel2(), null);
			jPanel3.add(getJTextField2(), null);
			jPanel3.add(getJLabel3(), null);
			jPanel3.add(getJTextField3(), null);
			jPanel3.add(getJLabel4(), null);
			jPanel3.add(getJTextField4(), null);
			jPanel3.add(getJLabel5(), null);
			jPanel3.add(getJTextField5(), null);
			jPanel3.add(getJLabel6(), null);
			jPanel3.add(getJTextField6(), null);
			jPanel3.add(getJButton9(), null);
			jPanel3.add(getJButton10(), null);
			jPanel3.add(getJTextField7(), null);
			jPanel3.add(getJTextField8(), null);
			jPanel3.add(getJTextField9(), null);
			jPanel3.add(getJTextField10(), null);
			jPanel3.add(getJTextField11(), null);
			jPanel3.add(getJTextField12(), null);
			jPanel3.add(getJTextField13(), null);
			jPanel3.add(getJTextField14(), null);
			jPanel3.add(getJTextField15(), null);
			jPanel3.add(getJTextField16(), null);
			jPanel3.add(getJTextField17(), null);
			jPanel3.add(getJTextField18(), null);
			jPanel3.add(getJTextField19(), null);
			jPanel3.add(getJTextField20(), null);
			jPanel3.add(getJTextField21(), null);
			jPanel3.add(getJTextField22(), null);
			jPanel3.add(getJTextField23(), null);
			jPanel3.add(getJTextField24(), null);
			jPanel3.add(getJTextField25(), null);
			jPanel3.add(getJTextField26(), null);
			jPanel3.add(getJTextField27(), null);
			jPanel3.add(getJTextField28(), null);
			jPanel3.add(getJTextField29(), null);
			jPanel3.add(getJTextField30(), null);
			jPanel3.add(getJTextField31(), null);
			jPanel3.add(getJTextField32(), null);
			jPanel3.add(getJTextField33(), null);
			jPanel3.add(getJTextField34(), null);
			jPanel3.add(getJTextField35(), null);
			jPanel3.add(getJTextField36(), null);
			jPanel3.add(getJTextField37(), null);
			jPanel3.add(getJTextField38(), null);
			jPanel3.add(getJTextField39(), null);
			jPanel3.add(getJTextField40(), null);
			jPanel3.add(getJTextField41(), null);
			jPanel3.setBounds(141, 57, 390, 212);
			jPanel3.setBackground(java.awt.SystemColor.window);
		}
		return jPanel3;
	}
	/**
	 * This method initializes jRadioButton16
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton16() {
		if(jRadioButton16 == null) {
			jRadioButton16 = new javax.swing.JRadioButton();
			int c = 1;
			if (c<=Integer.parseInt(floors)) {
				jRadioButton16.setVisible(true);
			}
			else {
				jRadioButton16.setVisible(false);
			}
			jRadioButton16.setBounds(43, 8, 43, 21);
			jRadioButton16.setBackground(java.awt.SystemColor.window);
			jRadioButton16.setText("1");
			jRadioButton16.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton17.setSelected(false);
					jRadioButton18.setSelected(false);
					jRadioButton19.setSelected(false);
					jRadioButton20.setSelected(false);
					jRadioButton21.setSelected(false);
					jRadioButton22.setSelected(false);
					jRadioButton23.setSelected(false);
					showFields(false);
					jTextField2.setVisible(true);
					jTextField3.setVisible(true);
					jTextField4.setVisible(true);
					jTextField5.setVisible(true);
					jTextField6.setVisible(true);
				}
			});
		}
		return jRadioButton16;
	}
	/**
	 * This method initializes jRadioButton17
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton17() {
		if(jRadioButton17 == null) {
			jRadioButton17 = new javax.swing.JRadioButton();
			int c = 2;
			if (c<=Integer.parseInt(floors)) {
				jRadioButton17.setVisible(true);
			}
			else {
				jRadioButton17.setVisible(false);
			}

			jRadioButton17.setBounds(43, 36, 43, 21);
			jRadioButton17.setBackground(java.awt.SystemColor.window);
			jRadioButton17.setText("2");
			jRadioButton17.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton16.setSelected(false);
					jRadioButton18.setSelected(false);
					jRadioButton19.setSelected(false);
					jRadioButton20.setSelected(false);
					jRadioButton21.setSelected(false);
					jRadioButton22.setSelected(false);
					jRadioButton23.setSelected(false);
					showFields(false);
					jTextField7.setVisible(true);
					jTextField8.setVisible(true);
					jTextField9.setVisible(true);
					jTextField10.setVisible(true);
					jTextField11.setVisible(true);

				}
			});
		}
		return jRadioButton17;
	}
	/**
	 * This method initializes jRadioButton18
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton18() {
		if(jRadioButton18 == null) {
			jRadioButton18 = new javax.swing.JRadioButton();
			int c = 3;
			if (c<=Integer.parseInt(floors)) {
				jRadioButton18.setVisible(true);
			}
			else {
				jRadioButton18.setVisible(false);
			}
			jRadioButton18.setBounds(129, 8, 43, 21);
			jRadioButton18.setBackground(java.awt.SystemColor.window);
			jRadioButton18.setText("3");
			jRadioButton18.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton16.setSelected(false);
					jRadioButton17.setSelected(false);
					jRadioButton19.setSelected(false);
					jRadioButton20.setSelected(false);
					jRadioButton21.setSelected(false);
					jRadioButton22.setSelected(false);
					jRadioButton23.setSelected(false);
					showFields(false);
					jTextField12.setVisible(true);
					jTextField13.setVisible(true);
					jTextField14.setVisible(true);
					jTextField15.setVisible(true);
					jTextField16.setVisible(true);

				}
			});
		}
		return jRadioButton18;
	}
	/**
	 * This method initializes jRadioButton19
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton19() {
		if(jRadioButton19 == null) {
			jRadioButton19 = new javax.swing.JRadioButton();
			int c = 4;
			if (c<=Integer.parseInt(floors)) {
				jRadioButton19.setVisible(true);
			}
			else {
				jRadioButton19.setVisible(false);
			}
			jRadioButton19.setBounds(129, 36, 43, 21);
			jRadioButton19.setBackground(java.awt.SystemColor.window);
			jRadioButton19.setText("4");
			jRadioButton19.setBackground(java.awt.SystemColor.window);
			jRadioButton19.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton16.setSelected(false);
					jRadioButton17.setSelected(false);
					jRadioButton18.setSelected(false);
					jRadioButton20.setSelected(false);
					jRadioButton21.setSelected(false);
					jRadioButton22.setSelected(false);
					jRadioButton23.setSelected(false);
					showFields(false);
					jTextField17.setVisible(true);
					jTextField18.setVisible(true);
					jTextField19.setVisible(true);
					jTextField20.setVisible(true);
					jTextField21.setVisible(true);

				}
			});
		}
		return jRadioButton19;
	}
	/**
	 * This method initializes jRadioButton20
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton20() {
		if(jRadioButton20 == null) {
			jRadioButton20 = new javax.swing.JRadioButton();
			int c = 5;
			if (c<=Integer.parseInt(floors)) {
				jRadioButton20.setVisible(true);
			}
			else {
				jRadioButton20.setVisible(false);
			}
			jRadioButton20.setBounds(215, 8, 43, 21);
			jRadioButton20.setBackground(java.awt.SystemColor.window);
			jRadioButton20.setText("5");
			jRadioButton20.setBackground(java.awt.SystemColor.window);
			jRadioButton20.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton16.setSelected(false);
					jRadioButton17.setSelected(false);
					jRadioButton18.setSelected(false);
					jRadioButton19.setSelected(false);
					jRadioButton21.setSelected(false);
					jRadioButton22.setSelected(false);
					jRadioButton23.setSelected(false);
					showFields(false);
					jTextField22.setVisible(true);
					jTextField23.setVisible(true);
					jTextField24.setVisible(true);
					jTextField25.setVisible(true);
					jTextField26.setVisible(true);

				}
			});
		}
		return jRadioButton20;
	}
	/**
	 * This method initializes jRadioButton21
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton21() {
		if(jRadioButton21 == null) {
			jRadioButton21 = new javax.swing.JRadioButton();
			int c = 7;
			if (c<=Integer.parseInt(floors)) {
				jRadioButton21.setVisible(true);
			}
			else {
				jRadioButton21.setVisible(false);
			}
			jRadioButton21.setBounds(301, 8, 43, 21);
			jRadioButton21.setBackground(java.awt.SystemColor.window);
			jRadioButton21.setText("7");
			jRadioButton21.setBackground(java.awt.SystemColor.window);
			jRadioButton21.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton16.setSelected(false);
					jRadioButton17.setSelected(false);
					jRadioButton18.setSelected(false);
					jRadioButton19.setSelected(false);
					jRadioButton22.setSelected(false);
					jRadioButton20.setSelected(false);
					jRadioButton23.setSelected(false);
					showFields(false);
					jTextField32.setVisible(true);
					jTextField33.setVisible(true);
					jTextField34.setVisible(true);
					jTextField35.setVisible(true);
					jTextField36.setVisible(true);

				}
			});
		}
		return jRadioButton21;
	}
	/**
	 * This method initializes jRadioButton22
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton22() {
		if(jRadioButton22 == null) {
			jRadioButton22 = new javax.swing.JRadioButton();
			int c = 6;
			if (c<=Integer.parseInt(floors)) {
				jRadioButton22.setVisible(true);
			}
			else {
				jRadioButton22.setVisible(false);
			}
			jRadioButton22.setBounds(215, 36, 43, 21);
			jRadioButton22.setBackground(java.awt.SystemColor.window);
			jRadioButton22.setText("6");
			jRadioButton22.setBackground(java.awt.SystemColor.window);
			jRadioButton22.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton16.setSelected(false);
					jRadioButton17.setSelected(false);
					jRadioButton18.setSelected(false);
					jRadioButton19.setSelected(false);
					jRadioButton21.setSelected(false);
					jRadioButton20.setSelected(false);
					jRadioButton23.setSelected(false);
					showFields(false);
					jTextField27.setVisible(true);
					jTextField28.setVisible(true);
					jTextField29.setVisible(true);
					jTextField30.setVisible(true);
					jTextField31.setVisible(true);

				}
			});
		}
		return jRadioButton22;
	}
	/**
	 * This method initializes jRadioButton23
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton23() {
		if(jRadioButton23 == null) {
			jRadioButton23 = new javax.swing.JRadioButton();
			int c = 8;
			if (c<=Integer.parseInt(floors)) {
				jRadioButton23.setVisible(true);
			}
			else {
				jRadioButton23.setVisible(false);
			}
			jRadioButton23.setBounds(301, 36, 43, 21);
			jRadioButton23.setBackground(java.awt.SystemColor.window);
			jRadioButton23.setText("8");
			jRadioButton23.setBackground(java.awt.SystemColor.window);
			jRadioButton23.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton16.setSelected(false);
					jRadioButton17.setSelected(false);
					jRadioButton18.setSelected(false);
					jRadioButton19.setSelected(false);
					jRadioButton22.setSelected(false);
					jRadioButton20.setSelected(false);
					jRadioButton21.setSelected(false);
					showFields(false);
					jTextField37.setVisible(true);
					jTextField38.setVisible(true);
					jTextField39.setVisible(true);
					jTextField40.setVisible(true);
					jTextField41.setVisible(true);
				}
			});
		}
		return jRadioButton23;
	}
	/**
	 * This method initializes jLabel2
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel2() {
		if(jLabel2 == null) {
			jLabel2 = new javax.swing.JLabel();
			jLabel2.setBounds(25, 71, 80, 20);
			jLabel2.setText(language[55]);
		}
		return jLabel2;
	}
	/**
	 * This method initializes jTextField2
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField2() {
		if(jTextField2 == null) {
			jTextField2 = new javax.swing.JTextField();
			jTextField2.setText(sr1);
			jTextField2.setBounds(104, 71, 256, 20);
			jTextField2.setVisible(false);
		}
		return jTextField2;
	}
	/**
	 * This method initializes jLabel3
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel3() {
		if(jLabel3 == null) {
			jLabel3 = new javax.swing.JLabel();
			jLabel3.setBounds(25, 90, 80, 20);
			jLabel3.setText(language[56]);
		}
		return jLabel3;
	}
	/**
	 * This method initializes jTextField3
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField3() {
		if(jTextField3 == null) {
			jTextField3 = new javax.swing.JTextField();
			jTextField3.setText(dr1);
			jTextField3.setBounds(104, 90, 256, 20);
			jTextField3.setVisible(false);
		}
		return jTextField3;
	}
	/**
	 * This method initializes jLabel4
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel4() {
		if(jLabel4 == null) {
			jLabel4 = new javax.swing.JLabel();
			jLabel4.setBounds(25, 128, 80, 20);
			jLabel4.setText(language[58]);
		}
		return jLabel4;
	}
	/**
	 * This method initializes jTextField4
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField4() {
		if(jTextField4 == null) {
			jTextField4 = new javax.swing.JTextField();
			jTextField4.setText(tr1);
			jTextField4.setBounds(104, 109, 256, 20);
			jTextField4.setVisible(false);
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
			jLabel5.setBounds(25, 109, 80, 20);
			jLabel5.setText(language[57]);
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
			jTextField5.setText(qr1);
			jTextField5.setBounds(104, 128, 256, 20);
			jTextField5.setVisible(false);
		}
		return jTextField5;
	}
	/**
	 * This method initializes jLabel6
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel6() {
		if(jLabel6 == null) {
			jLabel6 = new javax.swing.JLabel();
			jLabel6.setBounds(25, 147, 80, 20);
			jLabel6.setText(language[59]);
		}
		return jLabel6;
	}
	/**
	 * This method initializes jTextField6
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField6() {
		if(jTextField6 == null) {
			jTextField6 = new javax.swing.JTextField();
			jTextField6.setText(ap1);
			jTextField6.setBounds(104, 147, 256, 20);
			jTextField6.setVisible(false);
		}
		return jTextField6;
	}
	/**
	 * This method initializes jButton9
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButton9() {
		if(jButton9 == null) {
			jButton9 = new javax.swing.JButton();
			jButton9.setText(language[21]);
			jButton9.setBounds(279, 173, 98, 25);
			jButton9.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					saveRooms();
					dispose();
					mw.setVisible(true);
				}
			});
		}
		return jButton9;
	}
	/**
	 * This method initializes jButton10
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButton10() {
		if(jButton10 == null) {
			jButton10 = new javax.swing.JButton();
			jButton10.setText(language[22]);
			jButton10.setBounds(160, 174, 108, 22);
			jButton10.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					showFields(false);
					jPanel3.setVisible(false);
				}
			});
		}
		return jButton10;
	}
	/**
	 * This method initializes jTextField7
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField7() {
		if(jTextField7 == null) {
			jTextField7 = new javax.swing.JTextField();
			jTextField7.setText(sr2);
			jTextField7.setBounds(104, 71, 256, 20);
			jTextField7.setVisible(false);
		}
		return jTextField7;
	}
	/**
	 * This method initializes jTextField8
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField8() {
		if(jTextField8 == null) {
			jTextField8 = new javax.swing.JTextField();
			jTextField8.setText(dr2);
			jTextField8.setBounds(104, 90, 256, 20);
			jTextField8.setVisible(false);
		}
		return jTextField8;
	}
	/**
	 * This method initializes jTextField9
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField9() {
		if(jTextField9 == null) {
			jTextField9 = new javax.swing.JTextField();
			jTextField9.setText(tr2);
			jTextField9.setBounds(104, 109, 256, 20);
			jTextField9.setVisible(false);
		}
		return jTextField9;
	}
	/**
	 * This method initializes jTextField10
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField10() {
		if(jTextField10 == null) {
			jTextField10 = new javax.swing.JTextField();
			jTextField10.setText(qr2);
			jTextField10.setBounds(104, 128, 256, 20);
			jTextField10.setVisible(false);
		}
		return jTextField10;
	}
	/**
	 * This method initializes jTextField11
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField11() {
		if(jTextField11 == null) {
			jTextField11 = new javax.swing.JTextField();
			jTextField11.setText(ap2);
			jTextField11.setBounds(104, 147, 256, 20);
			jTextField11.setVisible(false);
		}
		return jTextField11;
	}
	/**
	 * This method initializes jTextField12
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField12() {
		if(jTextField12 == null) {
			jTextField12 = new javax.swing.JTextField();
			jTextField12.setText(sr3);
			jTextField12.setBounds(104, 71, 256, 20);
			jTextField12.setVisible(false);
		}
		return jTextField12;
	}
	/**
	 * This method initializes jTextField13
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField13() {
		if(jTextField13 == null) {
			jTextField13 = new javax.swing.JTextField();
			jTextField13.setText(dr3);
			jTextField13.setBounds(104, 90, 256, 20);
			jTextField13.setVisible(false);
		}
		return jTextField13;
	}
	/**
	 * This method initializes jTextField14
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField14() {
		if(jTextField14 == null) {
			jTextField14 = new javax.swing.JTextField();
			jTextField14.setText(tr3);
			jTextField14.setBounds(104, 109, 256, 20);
			jTextField14.setVisible(false);
		}
		return jTextField14;
	}
	/**
	 * This method initializes jTextField15
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField15() {
		if(jTextField15 == null) {
			jTextField15 = new javax.swing.JTextField();
			jTextField15.setText(qr3);
			jTextField15.setBounds(104, 128, 256, 20);
			jTextField15.setVisible(false);
		}
		return jTextField15;
	}
	/**
	 * This method initializes jTextField16
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField16() {
		if(jTextField16 == null) {
			jTextField16 = new javax.swing.JTextField();
			jTextField16.setText(ap3);
			jTextField16.setBounds(104, 147, 256, 20);
			jTextField16.setVisible(false);
		}
		return jTextField16;
	}
	/**
	 * This method initializes jTextField17
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField17() {
		if(jTextField17 == null) {
			jTextField17 = new javax.swing.JTextField();
			jTextField17.setText(sr4);
			jTextField17.setBounds(104, 71, 256, 20);
			jTextField17.setVisible(false);
		}
		return jTextField17;
	}
	/**
	 * This method initializes jTextField18
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField18() {
		if(jTextField18 == null) {
			jTextField18 = new javax.swing.JTextField();
			jTextField18.setText(dr4);
			jTextField18.setBounds(104, 90, 256, 20);
			jTextField18.setVisible(false);
		}
		return jTextField18;
	}
	/**
	 * This method initializes jTextField19
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField19() {
		if(jTextField19 == null) {
			jTextField19 = new javax.swing.JTextField();
			jTextField19.setText(tr4);
			jTextField19.setBounds(104, 109, 256, 20);
			jTextField19.setVisible(false);
		}
		return jTextField19;
	}
	/**
	 * This method initializes jTextField20
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField20() {
		if(jTextField20 == null) {
			jTextField20 = new javax.swing.JTextField();
			jTextField20.setText(qr4);
			jTextField20.setBounds(104, 128, 256, 20);
			jTextField20.setVisible(false);
		}
		return jTextField20;
	}
	/**
	 * This method initializes jTextField21
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField21() {
		if(jTextField21 == null) {
			jTextField21 = new javax.swing.JTextField();
			jTextField21.setText(ap4);
			jTextField21.setBounds(104, 147, 256, 20);
			jTextField21.setVisible(false);
		}
		return jTextField21;
	}
	/**
	 * This method initializes jTextField22
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField22() {
		if(jTextField22 == null) {
			jTextField22 = new javax.swing.JTextField();
			jTextField22.setText(sr5);
			jTextField22.setBounds(104, 71, 256, 20);
			jTextField22.setVisible(false);
		}
		return jTextField22;
	}
	/**
	 * This method initializes jTextField23
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField23() {
		if(jTextField23 == null) {
			jTextField23 = new javax.swing.JTextField();
			jTextField23.setText(dr5);
			jTextField23.setBounds(104, 90, 256, 20);
			jTextField23.setVisible(false);
		}
		return jTextField23;
	}
	/**
	 * This method initializes jTextField24
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField24() {
		if(jTextField24 == null) {
			jTextField24 = new javax.swing.JTextField();
			jTextField24.setText(tr5);
			jTextField24.setBounds(104, 109, 256, 20);
			jTextField24.setVisible(false);
		}
		return jTextField24;
	}
	/**
	 * This method initializes jTextField25
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField25() {
		if(jTextField25 == null) {
			jTextField25 = new javax.swing.JTextField();
			jTextField25.setText(qr5);
			jTextField25.setBounds(104, 128, 256, 20);
			jTextField25.setVisible(false);
		}
		return jTextField25;
	}
	/**
	 * This method initializes jTextField26
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField26() {
		if(jTextField26 == null) {
			jTextField26 = new javax.swing.JTextField();
			jTextField26.setText(ap5);
			jTextField26.setBounds(104, 147, 256, 20);
			jTextField26.setVisible(false);
		}
		return jTextField26;
	}
	/**
	 * This method initializes jTextField27
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField27() {
		if(jTextField27 == null) {
			jTextField27 = new javax.swing.JTextField();
			jTextField27.setText(sr6);
			jTextField27.setBounds(104, 71, 256, 20);
			jTextField27.setVisible(false);
		}
		return jTextField27;
	}
	/**
	 * This method initializes jTextField28
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField28() {
		if(jTextField28 == null) {
			jTextField28 = new javax.swing.JTextField();
			jTextField28.setText(dr6);
			jTextField28.setBounds(104, 90, 256, 20);
			jTextField28.setVisible(false);
		}
		return jTextField28;
	}
	/**
	 * This method initializes jTextField29
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField29() {
		if(jTextField29 == null) {
			jTextField29 = new javax.swing.JTextField();
			jTextField29.setText(tr6);
			jTextField29.setBounds(104, 109, 256, 20);
			jTextField29.setVisible(false);
		}
		return jTextField29;
	}
	/**
	 * This method initializes jTextField30
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField30() {
		if(jTextField30 == null) {
			jTextField30 = new javax.swing.JTextField();
			jTextField30.setText(qr6);
			jTextField30.setBounds(104, 128, 256, 20);
			jTextField30.setVisible(false);
		}
		return jTextField30;
	}
	/**
	 * This method initializes jTextField31
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField31() {
		if(jTextField31 == null) {
			jTextField31 = new javax.swing.JTextField();
			jTextField31.setText(ap6);
			jTextField31.setBounds(104, 147, 256, 20);
			jTextField31.setVisible(false);
		}
		return jTextField31;
	}
	/**
	 * This method initializes jTextField32
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField32() {
		if(jTextField32 == null) {
			jTextField32 = new javax.swing.JTextField();
			jTextField32.setText(sr7);
			jTextField32.setBounds(104, 71, 256, 20);
			jTextField32.setVisible(false);
		}
		return jTextField32;
	}
	/**
	 * This method initializes jTextField33
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField33() {
		if(jTextField33 == null) {
			jTextField33 = new javax.swing.JTextField();
			jTextField33.setText(dr7);
			jTextField33.setBounds(104, 90, 256, 20);
			jTextField33.setVisible(false);
		}
		return jTextField33;
	}
	/**
	 * This method initializes jTextField34
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField34() {
		if(jTextField34 == null) {
			jTextField34 = new javax.swing.JTextField();
			jTextField34.setText(tr7);
			jTextField34.setBounds(104, 109, 256, 20);
			jTextField34.setVisible(false);
		}
		return jTextField34;
	}
	/**
	 * This method initializes jTextField35
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField35() {
		if(jTextField35 == null) {
			jTextField35 = new javax.swing.JTextField();
			jTextField35.setText(qr7);
			jTextField35.setBounds(104, 128, 256, 20);
			jTextField35.setVisible(false);
		}
		return jTextField35;
	}
	/**
	 * This method initializes jTextField36
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField36() {
		if(jTextField36 == null) {
			jTextField36 = new javax.swing.JTextField();
			jTextField36.setText(ap7);
			jTextField36.setBounds(104, 147, 256, 20);
			jTextField36.setVisible(false);
		}
		return jTextField36;
	}
	/**
	 * This method initializes jTextField37
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField37() {
		if(jTextField37 == null) {
			jTextField37 = new javax.swing.JTextField();
			jTextField37.setText(sr8);
			jTextField37.setBounds(104, 71, 256, 20);
			jTextField37.setVisible(false);
		}
		return jTextField37;
	}
	/**
	 * This method initializes jTextField38
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField38() {
		if(jTextField38 == null) {
			jTextField38 = new javax.swing.JTextField();
			jTextField38.setText(dr8);
			jTextField38.setBounds(104, 90, 256, 20);
			jTextField38.setVisible(false);
		}
		return jTextField38;
	}
	/**
	 * This method initializes jTextField39
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField39() {
		if(jTextField39 == null) {
			jTextField39 = new javax.swing.JTextField();
			jTextField39.setText(tr8);
			jTextField39.setBounds(104, 109, 256, 20);
			jTextField39.setVisible(false);
		}
		return jTextField39;
	}
	/**
	 * This method initializes jTextField40
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField40() {
		if(jTextField40 == null) {
			jTextField40 = new javax.swing.JTextField();
			jTextField40.setText(qr8);
			jTextField40.setBounds(104, 128, 256, 20);
			jTextField40.setVisible(false);
		}
		return jTextField40;
	}
	/**
	 * This method initializes jTextField41
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField41() {
		if(jTextField41 == null) {
			jTextField41 = new javax.swing.JTextField();
			jTextField41.setText(ap8);
			jTextField41.setBounds(104, 147, 256, 20);
			jTextField41.setVisible(false);
		}
		return jTextField41;
	}
	
	private void showFields(boolean status) {
		jTextField2.setVisible(status);
		jTextField3.setVisible(status);
		jTextField4.setVisible(status);
		jTextField5.setVisible(status);
		jTextField6.setVisible(status);
		jTextField7.setVisible(status);
		jTextField8.setVisible(status);
		jTextField9.setVisible(status);
		jTextField10.setVisible(status);
		jTextField11.setVisible(status);
		jTextField12.setVisible(status);
		jTextField13.setVisible(status);
		jTextField14.setVisible(status);
		jTextField15.setVisible(status);
		jTextField16.setVisible(status);
		jTextField17.setVisible(status);
		jTextField18.setVisible(status);
		jTextField19.setVisible(status);
		jTextField20.setVisible(status);
		jTextField21.setVisible(status);
		jTextField22.setVisible(status);
		jTextField23.setVisible(status);
		jTextField24.setVisible(status);
		jTextField25.setVisible(status);
		jTextField26.setVisible(status);
		jTextField27.setVisible(status);
		jTextField28.setVisible(status);
		jTextField29.setVisible(status);
		jTextField30.setVisible(status);
		jTextField31.setVisible(status);
		jTextField32.setVisible(status);
		jTextField33.setVisible(status);
		jTextField34.setVisible(status);
		jTextField35.setVisible(status);
		jTextField36.setVisible(status);
		jTextField37.setVisible(status);
		jTextField38.setVisible(status);
		jTextField39.setVisible(status);
		jTextField40.setVisible(status);
		jTextField41.setVisible(status);
	}
	
	private int getOffset(String text) {
		int cnt = 0;
		char[] tmparray = new char[text.length()];
		tmparray = text.toCharArray();
		
		loop1:
		for (int i=0; i<text.length(); ++i) {
			if (tmparray[i]==',') {
				++cnt;
				if (tmparray[i+1]==' ') {
					++cnt;
					break loop1;
				}
				else {
					break loop1;
				}
			}
			else if (tmparray[i]==';') {
				++cnt;
				if (tmparray[i+1]==' ') {
					++cnt;
					break loop1;
				}
				else {
					break loop1;
				}
			}
			else {
				++cnt;
			}
		}
		return cnt; 
	}
	
	private int getRoomLength(String text) {
		int cnt = 0;
		char[] tmparray = new char[text.length()];
		tmparray = text.toCharArray();
		
		loop1:
		for (int i=0; i<text.length(); ++i) {
			if (tmparray[i]==',') {
				break loop1;
			}
			else if (tmparray[i]==';') {
				break loop1;
			}
			else if (tmparray[i]==' ') {
				break loop1;
			}
			else {
				++cnt;
			}
		}
		
		return cnt;
	}
	
	private void saveRooms() {
		try {
			FileOutputStream sngl = new FileOutputStream("./cfg/single.rms");
			FileOutputStream dblr = new FileOutputStream("./cfg/double.rms");
			FileOutputStream trpl = new FileOutputStream("./cfg/triple.rms");
			FileOutputStream qd = new FileOutputStream("./cfg/quad.rms");
			FileOutputStream app = new FileOutputStream("./cfg/apartment.rms");
			
			ObjectOutputStream single = new ObjectOutputStream(sngl);
			ObjectOutputStream dbl = new ObjectOutputStream(dblr);
			ObjectOutputStream triple = new ObjectOutputStream(trpl);
			ObjectOutputStream quad = new ObjectOutputStream(qd);
			ObjectOutputStream apartment = new ObjectOutputStream(app);
			
			ArrayList singlerooms = new ArrayList();
			ArrayList doublerooms = new ArrayList();
			ArrayList triplerooms = new ArrayList();
			ArrayList quadrooms = new ArrayList();
			ArrayList apartments = new ArrayList();
			ArrayList tmp = new ArrayList(); 
			String tmp2;
			String[] tmpstring;
			
			int offset = getOffset(jTextField2.getText());
			int roomlength = getRoomLength(jTextField2.getText());
			
			
			// single, first floor
			tmp2 = jTextField2.getText();
			for (int i=0; i<tmp2.length()-1; i=i+offset) {
				tmp.add(tmp2.substring(i, i+roomlength));
			}
			tmpstring = new String[tmp.size()];
			for (int i=0; i<tmp.size(); ++i) {
				tmpstring[i] = (String) tmp.get(i);
			}
			tmp.clear();
			singlerooms.add(tmpstring);
			
			// single, second floor
			tmp2 = jTextField7.getText();
			for (int i=0; i<tmp2.length()-1; i=i+offset) {
				tmp.add(tmp2.substring(i, i+roomlength));
			}
			tmpstring = new String[tmp.size()];
			for (int i=0; i<tmp.size(); ++i) {
				tmpstring[i] = (String) tmp.get(i);
			}
			tmp.clear();
			singlerooms.add(tmpstring);

			// single, third floor
			tmp2 = jTextField12.getText();
			for (int i=0; i<tmp2.length()-1; i=i+offset) {
				tmp.add(tmp2.substring(i, i+roomlength));
			}
			tmpstring = new String[tmp.size()];
			for (int i=0; i<tmp.size(); ++i) {
				tmpstring[i] = (String) tmp.get(i);
			}
			tmp.clear();
			singlerooms.add(tmpstring);

			// single, fourth floor
			tmp2 = jTextField17.getText();
			for (int i=0; i<tmp2.length()-1; i=i+offset) {
				tmp.add(tmp2.substring(i, i+roomlength));
			}	
			tmpstring = new String[tmp.size()];
			for (int i=0; i<tmp.size(); ++i) {
				tmpstring[i] = (String) tmp.get(i);
			}
			tmp.clear();
			singlerooms.add(tmpstring);
			
			// single, fifth floor
			tmp2 = jTextField22.getText();
			for (int i=0; i<tmp2.length()-1; i=i+offset) {
				tmp.add(tmp2.substring(i, i+roomlength));
			}
			tmpstring = new String[tmp.size()];
			for (int i=0; i<tmp.size(); ++i) {
				tmpstring[i] = (String) tmp.get(i);
			}
			tmp.clear();
			singlerooms.add(tmpstring);
			
			// single, sixth floor
			tmp2 = jTextField27.getText();
			for (int i=0; i<tmp2.length()-1; i=i+offset) {
				tmp.add(tmp2.substring(i, i+roomlength));
			}
			tmpstring = new String[tmp.size()];
			for (int i=0; i<tmp.size(); ++i) {
				tmpstring[i] = (String) tmp.get(i);
			}
			tmp.clear();
			singlerooms.add(tmpstring);

			// single, seventh floor
			tmp2 = jTextField32.getText();
			for (int i=0; i<tmp2.length()-1; i=i+offset) {
				tmp.add(tmp2.substring(i, i+roomlength));
			}
			tmpstring = new String[tmp.size()];
			for (int i=0; i<tmp.size(); ++i) {
				tmpstring[i] = (String) tmp.get(i);
			}
			tmp.clear();
			singlerooms.add(tmpstring);
			
			// single, eigth floor
			tmp2 = jTextField37.getText();
			for (int i=0; i<tmp2.length()-1; i=i+offset) {
				tmp.add(tmp2.substring(i, i+roomlength));
			}
			tmpstring = new String[tmp.size()];
			for (int i=0; i<tmp.size(); ++i) {
				tmpstring[i] = (String) tmp.get(i);
			}
			tmp.clear();
			singlerooms.add(tmpstring);
			
			single.writeObject(singlerooms);
			single.flush();
			single.close();
			/*
			 ********************** DONE *************************
			 */
			
			
			// double, first floor
			tmp2 = jTextField3.getText();
			for (int i=0; i<tmp2.length()-1; i=i+offset) {
				tmp.add(tmp2.substring(i, i+roomlength));
			}
			tmpstring = new String[tmp.size()];
			for (int i=0; i<tmp.size(); ++i) {
				tmpstring[i] = (String) tmp.get(i);
			}
			tmp.clear();
			doublerooms.add(tmpstring);

			// double, second floor
			tmp2 = jTextField8.getText();
			for (int i=0; i<tmp2.length()-1; i=i+offset) {
				tmp.add(tmp2.substring(i, i+roomlength));
			}
			tmpstring = new String[tmp.size()];
			for (int i=0; i<tmp.size(); ++i) {
				tmpstring[i] = (String) tmp.get(i);
			}
			tmp.clear();
			doublerooms.add(tmpstring);
			
			// double, third floor
			tmp2 = jTextField13.getText();
			for (int i=0; i<tmp2.length()-1; i=i+offset) {
				tmp.add(tmp2.substring(i, i+roomlength));
			}
			tmpstring = new String[tmp.size()];
			for (int i=0; i<tmp.size(); ++i) {
				tmpstring[i] = (String) tmp.get(i);
			}
			tmp.clear();
			doublerooms.add(tmpstring);
			
			// double, fourth floor
			tmp2 = jTextField18.getText();
			for (int i=0; i<tmp2.length()-1; i=i+offset) {
				tmp.add(tmp2.substring(i, i+roomlength));
			}
			tmpstring = new String[tmp.size()];
			for (int i=0; i<tmp.size(); ++i) {
				tmpstring[i] = (String) tmp.get(i);
			}
			tmp.clear();
			doublerooms.add(tmpstring);
			
			// double, fifth floor
			tmp2 = jTextField23.getText();
			for (int i=0; i<tmp2.length()-1; i=i+offset) {
				tmp.add(tmp2.substring(i, i+roomlength));
			}
			tmpstring = new String[tmp.size()];
			for (int i=0; i<tmp.size(); ++i) {
				tmpstring[i] = (String) tmp.get(i);
			}
			tmp.clear();
			doublerooms.add(tmpstring);
			
			// double, sixth floor
			tmp2 = jTextField28.getText();
			for (int i=0; i<tmp2.length()-1; i=i+offset) {
				tmp.add(tmp2.substring(i, i+roomlength));
			}
			tmpstring = new String[tmp.size()];
			for (int i=0; i<tmp.size(); ++i) {
				tmpstring[i] = (String) tmp.get(i);
			}
			tmp.clear();
			doublerooms.add(tmpstring);
			
			// double, seventh floor
			tmp2 = jTextField33.getText();
			for (int i=0; i<tmp2.length()-1; i=i+offset) {
				tmp.add(tmp2.substring(i, i+roomlength));
			}
			tmpstring = new String[tmp.size()];
			for (int i=0; i<tmp.size(); ++i) {
				tmpstring[i] = (String) tmp.get(i);
			}
			tmp.clear();
			doublerooms.add(tmpstring);
			
			// double, eigth floor
			tmp2 = jTextField38.getText();
			for (int i=0; i<tmp2.length()-1; i=i+offset) {
				tmp.add(tmp2.substring(i, i+roomlength));
			}
			tmpstring = new String[tmp.size()];
			for (int i=0; i<tmp.size(); ++i) {
				tmpstring[i] = (String) tmp.get(i);
			}
			tmp.clear();
			doublerooms.add(tmpstring);

			dbl.writeObject(doublerooms);
			dbl.flush();
			dbl.close();
			
			/*
			 ********************** DONE *************************
			 */

			
			
			
			// triple, first floor
			tmp2 = jTextField4.getText();
			for (int i=0; i<tmp2.length()-1; i=i+offset) {
				tmp.add(tmp2.substring(i, i+roomlength));
			}
			tmpstring = new String[tmp.size()];
			for (int i=0; i<tmp.size(); ++i) {
				tmpstring[i] = (String) tmp.get(i);
			}
			tmp.clear();
			triplerooms.add(tmpstring);

			// triple, second floor
			tmp2 = jTextField9.getText();
			for (int i=0; i<tmp2.length()-1; i=i+offset) {
				tmp.add(tmp2.substring(i, i+roomlength));
			}
			tmpstring = new String[tmp.size()];
			for (int i=0; i<tmp.size(); ++i) {
				tmpstring[i] = (String) tmp.get(i);
			}
			tmp.clear();
			triplerooms.add(tmpstring);

			// triple, third floor
			tmp2 = jTextField14.getText();
			for (int i=0; i<tmp2.length()-1; i=i+offset) {
				tmp.add(tmp2.substring(i, i+roomlength));
			}
			tmpstring = new String[tmp.size()];
			for (int i=0; i<tmp.size(); ++i) {
				tmpstring[i] = (String) tmp.get(i);
			}
			tmp.clear();
			triplerooms.add(tmpstring);

			// triple, fourth floor
			tmp2 = jTextField19.getText();
			for (int i=0; i<tmp2.length()-1; i=i+offset) {
				tmp.add(tmp2.substring(i, i+roomlength));
			}
			tmpstring = new String[tmp.size()];
			for (int i=0; i<tmp.size(); ++i) {
				tmpstring[i] = (String) tmp.get(i);
			}
			tmp.clear();
			triplerooms.add(tmpstring);

			// triple, fifth floor
			tmp2 = jTextField24.getText();
			for (int i=0; i<tmp2.length()-1; i=i+offset) {
				tmp.add(tmp2.substring(i, i+roomlength));
			}
			tmpstring = new String[tmp.size()];
			for (int i=0; i<tmp.size(); ++i) {
				tmpstring[i] = (String) tmp.get(i);
			}
			tmp.clear();
			triplerooms.add(tmpstring);

			// triple, sixth floor
			tmp2 = jTextField29.getText();
			for (int i=0; i<tmp2.length()-1; i=i+offset) {
				tmp.add(tmp2.substring(i, i+roomlength));
			}
			tmpstring = new String[tmp.size()];
			for (int i=0; i<tmp.size(); ++i) {
				tmpstring[i] = (String) tmp.get(i);
			}
			tmp.clear();
			triplerooms.add(tmpstring);

			// triple, seventh floor
			tmp2 = jTextField34.getText();
			for (int i=0; i<tmp2.length()-1; i=i+offset) {
				tmp.add(tmp2.substring(i, i+roomlength));
			}
			tmpstring = new String[tmp.size()];
			for (int i=0; i<tmp.size(); ++i) {
				tmpstring[i] = (String) tmp.get(i);
			}
			tmp.clear();
			triplerooms.add(tmpstring);

			// triple, eigth floor
			tmp2 = jTextField39.getText();
			for (int i=0; i<tmp2.length()-1; i=i+offset) {
				tmp.add(tmp2.substring(i, i+roomlength));
			}
			tmpstring = new String[tmp.size()];
			for (int i=0; i<tmp.size(); ++i) {
				tmpstring[i] = (String) tmp.get(i);
			}
			tmp.clear();
			triplerooms.add(tmpstring);

			triple.writeObject(triplerooms);
			triple.flush();
			triple.close();
			
			/*
			 ********************** DONE *************************
			 */

			
			
			
			// quad, first floor
			tmp2 = jTextField5.getText();
			for (int i=0; i<tmp2.length()-1; i=i+offset) {
				tmp.add(tmp2.substring(i, i+roomlength));
			}
			tmpstring = new String[tmp.size()];
			for (int i=0; i<tmp.size(); ++i) {
				tmpstring[i] = (String) tmp.get(i);
			}
			tmp.clear();
			quadrooms.add(tmpstring);

			// quad, second floor
			tmp2 = jTextField10.getText();
			for (int i=0; i<tmp2.length()-1; i=i+offset) {
				tmp.add(tmp2.substring(i, i+roomlength));
			}
			tmpstring = new String[tmp.size()];
			for (int i=0; i<tmp.size(); ++i) {
				tmpstring[i] = (String) tmp.get(i);
			}
			tmp.clear();
			quadrooms.add(tmpstring);

			// quad, third floor
			tmp2 = jTextField15.getText();
			for (int i=0; i<tmp2.length()-1; i=i+offset) {
				tmp.add(tmp2.substring(i, i+roomlength));
			}
			tmpstring = new String[tmp.size()];
			for (int i=0; i<tmp.size(); ++i) {
				tmpstring[i] = (String) tmp.get(i);
			}
			tmp.clear();
			quadrooms.add(tmpstring);

			// quad, fourth floor
			tmp2 = jTextField20.getText();
			for (int i=0; i<tmp2.length()-1; i=i+offset) {
				tmp.add(tmp2.substring(i, i+roomlength));
			}
			tmpstring = new String[tmp.size()];
			for (int i=0; i<tmp.size(); ++i) {
				tmpstring[i] = (String) tmp.get(i);
			}
			tmp.clear();
			quadrooms.add(tmpstring);

			// quad, fifth floor
			tmp2 = jTextField25.getText();
			for (int i=0; i<tmp2.length()-1; i=i+offset) {
				tmp.add(tmp2.substring(i, i+roomlength));
			}
			tmpstring = new String[tmp.size()];
			for (int i=0; i<tmp.size(); ++i) {
				tmpstring[i] = (String) tmp.get(i);
			}
			tmp.clear();
			quadrooms.add(tmpstring);

			// quad, sixth floor
			tmp2 = jTextField30.getText();
			for (int i=0; i<tmp2.length()-1; i=i+offset) {
				tmp.add(tmp2.substring(i, i+roomlength));
			}
			tmpstring = new String[tmp.size()];
			for (int i=0; i<tmp.size(); ++i) {
				tmpstring[i] = (String) tmp.get(i);
			}
			tmp.clear();
			quadrooms.add(tmpstring);

			// quad, seventh floor
			tmp2 = jTextField35.getText();
			for (int i=0; i<tmp2.length()-1; i=i+offset) {
				tmp.add(tmp2.substring(i, i+roomlength));
			}
			tmpstring = new String[tmp.size()];
			for (int i=0; i<tmp.size(); ++i) {
				tmpstring[i] = (String) tmp.get(i);
			}
			tmp.clear();
			quadrooms.add(tmpstring);

			// quad, eigth floor
			tmp2 = jTextField40.getText();
			for (int i=0; i<tmp2.length()-1; i=i+offset) {
				tmp.add(tmp2.substring(i, i+roomlength));
			}
			tmpstring = new String[tmp.size()];
			for (int i=0; i<tmp.size(); ++i) {
				tmpstring[i] = (String) tmp.get(i);
			}
			tmp.clear();
			quadrooms.add(tmpstring);

			quad.writeObject(quadrooms);
			quad.flush();
			quad.close();
			
			/*
			 ********************** DONE *************************
			 */

			
			
			
			// ap, first floor
			tmp2 = jTextField6.getText();
			for (int i=0; i<tmp2.length()-1; i=i+offset) {
				tmp.add(tmp2.substring(i, i+roomlength));
			}
			tmpstring = new String[tmp.size()];
			for (int i=0; i<tmp.size(); ++i) {
				tmpstring[i] = (String) tmp.get(i);
			}
			tmp.clear();
			apartments.add(tmpstring);

			// ap, second floor
			tmp2 = jTextField11.getText();
			for (int i=0; i<tmp2.length()-1; i=i+offset) {
				tmp.add(tmp2.substring(i, i+roomlength));
			}
			tmpstring = new String[tmp.size()];
			for (int i=0; i<tmp.size(); ++i) {
				tmpstring[i] = (String) tmp.get(i);
			}
			tmp.clear();
			apartments.add(tmpstring);

			// ap, third floor
			tmp2 = jTextField16.getText();
			for (int i=0; i<tmp2.length()-1; i=i+offset) {
				tmp.add(tmp2.substring(i, i+roomlength));
			}
			tmpstring = new String[tmp.size()];
			for (int i=0; i<tmp.size(); ++i) {
				tmpstring[i] = (String) tmp.get(i);
			}
			tmp.clear();
			apartments.add(tmpstring);

			// ap, fourth floor
			tmp2 = jTextField21.getText();
			for (int i=0; i<tmp2.length()-1; i=i+offset) {
				tmp.add(tmp2.substring(i, i+roomlength));
			}
			tmpstring = new String[tmp.size()];
			for (int i=0; i<tmp.size(); ++i) {
				tmpstring[i] = (String) tmp.get(i);
			}
			tmp.clear();
			apartments.add(tmpstring);

			// ap, fifth floor
			tmp2 = jTextField26.getText();
			for (int i=0; i<tmp2.length()-1; i=i+offset) {
				tmp.add(tmp2.substring(i, i+roomlength));
			}
			tmpstring = new String[tmp.size()];
			for (int i=0; i<tmp.size(); ++i) {
				tmpstring[i] = (String) tmp.get(i);
			}
			tmp.clear();
			apartments.add(tmpstring);

			// ap, sixth floor
			tmp2 = jTextField31.getText();
			for (int i=0; i<tmp2.length()-1; i=i+offset) {
				tmp.add(tmp2.substring(i, i+roomlength));
			}
			tmpstring = new String[tmp.size()];
			for (int i=0; i<tmp.size(); ++i) {
				tmpstring[i] = (String) tmp.get(i);
			}
			tmp.clear();
			apartments.add(tmpstring);

			// ap, seventh floor
			tmp2 = jTextField36.getText();
			for (int i=0; i<tmp2.length()-1; i=i+offset) {
				tmp.add(tmp2.substring(i, i+roomlength));
			}
			tmpstring = new String[tmp.size()];
			for (int i=0; i<tmp.size(); ++i) {
				tmpstring[i] = (String) tmp.get(i);
			}
			tmp.clear();
			apartments.add(tmpstring);

			// ap, eigth floor
			tmp2 = jTextField41.getText();
			for (int i=0; i<tmp2.length()-1; i=i+offset) {
				tmp.add(tmp2.substring(i, i+roomlength));
			}
			tmpstring = new String[tmp.size()];
			for (int i=0; i<tmp.size(); ++i) {
				tmpstring[i] = (String) tmp.get(i);
			}
			tmp.clear();
			apartments.add(tmpstring);

			apartment.writeObject(apartments);
			apartment.flush();
			apartment.close();
			
			/*
			 ********************** DONE *************************
			 */

			
			
			
			showFields(false);
			jPanel3.setVisible(false);
			jButton1.setVisible(true);
			jButton2.setVisible(true);
			
			/*Rooms rt = new Rooms();
			for (int i=1; i<=Integer.parseInt(settings[2]); ++i) {
				rt.createRoomFiles(rt.getRooms(i));
			}*/
			ReservationManagement rm = new ReservationManagement(mw);
			rm.setVisible(false);
			Reservation res = new Reservation(rm);
			res.createResTable();
			rm.dispose();

			
		}
		catch (IOException io) {
			System.out.println(io + " saveRooms()");
		}
	}
	
	private void getRooms() {
		try {
			FileInputStream sngl = new FileInputStream("./cfg/single.rms");
			FileInputStream dblr = new FileInputStream("./cfg/double.rms");
			FileInputStream trpl = new FileInputStream("./cfg/triple.rms");
			FileInputStream qd = new FileInputStream("./cfg/quad.rms");
			FileInputStream app = new FileInputStream("./cfg/apartment.rms");
			
			ObjectInputStream single = new ObjectInputStream(sngl);
			ObjectInputStream dbl = new ObjectInputStream(dblr);
			ObjectInputStream triple = new ObjectInputStream(trpl);
			ObjectInputStream quad = new ObjectInputStream(qd);
			ObjectInputStream apartment = new ObjectInputStream(app);
			
			int l;
			
			sr = (ArrayList) single.readObject();
			single.close();
			dr = (ArrayList) dbl.readObject();
			dbl.close();
			tr = (ArrayList) triple.readObject();
			triple.close();
			qr = (ArrayList) quad.readObject();
			quad.close();
			ap = (ArrayList) apartment.readObject();
			apartment.close();
			
			String[] sr1a = (String[]) sr.get(0);
			if (sr1a.length>0) {
				for (int i=0; i<sr1a.length-1; ++i) {
					sr1 = sr1 + sr1a[i] + ", ";
				}
				sr1 = sr1 + sr1a[sr1a.length-1];
			}
			
			String [] sr2a = (String[]) sr.get(1);
			if (sr2a.length > 0) {
				for (int i=0; i<sr2a.length-1; ++i) {
					sr2 = sr2 + sr2a[i] + ", ";
				}
				sr2 = sr2 + sr2a[sr2a.length-1];
			}	
					
			String[] sr3a = (String[]) sr.get(2);
			if (sr3a.length > 0) {
				for (int i=0; i<sr3a.length-1; ++i) {
					sr3 = sr3 + sr3a[i] + ", ";
				}
				sr3 = sr3 + sr3a[sr3a.length-1];
			}
			
			String[] sr4a = (String[]) sr.get(3);
			l = sr4a.length;
			if (l > 0) {
				for (int i=0; i<l-1; ++i) {
					sr4 = sr4 + sr4a[i] + ", ";
				}
				sr4 = sr4 + sr4a[l-1];
			}
			
			String[] sr5a = (String[]) sr.get(4);
			l = sr5a.length;
			if (l > 0) {
				for (int i=0; i<l-1; ++i) {
					sr5 = sr5 + sr5a[i] + ", ";
				}
				sr5 = sr5 + sr5a[l-1];
			}
			
			String[] sr6a = (String[]) sr.get(5);
			l = sr6a.length;
			if (l > 0) {
				for (int i=0; i<l-1; ++i) {
					sr6 = sr6 + sr6a[i] + ", ";
				}
				sr6 = sr6 + sr6a[l-1];
			}

			String[] sr7a = (String[]) sr.get(6);
			l = sr7a.length;
			if (l > 0) {
				for (int i=0; i<l-1; ++i) {
					sr7 = sr7 + sr7a[i] + ", ";
				}
				sr7 = sr7 + sr7a[l-1];
			}

			String[] sr8a = (String[]) sr.get(7);
			l = sr8a.length;
			if (l > 0) {
				for (int i=0; i<l-1; ++i) {
					sr8 = sr8 + sr8a[i] + ", ";
				}
				sr8 = sr8 + sr8a[l-1];
			}

			String[] dr1a = (String[]) dr.get(0);
			l = dr1a.length;
			if (l > 0) {
				for (int i=0; i<l-1; ++i) {
					dr1 = dr1 + dr1a[i] + ", ";
				}
				dr1 = dr1 + dr1a[l-1];
			}

			String[] dr2a = (String[]) dr.get(1);
			l = dr2a.length;
			if (l > 0) {
				for (int i=0; i<l-1; ++i) {
					dr2 = dr2 + dr2a[i] + ", ";
				}
				dr2 = dr2 + dr2a[l-1];
			}

			String[] dr3a = (String[]) dr.get(2);
			l = dr3a.length;
			if (l > 0) {
				for (int i=0; i<l-1; ++i) {
					dr3 = dr3 + dr3a[i] + ", ";
				}
				dr3 = dr3 + dr3a[l-1];
			}
			
			String[] dr4a = (String[]) dr.get(3);
			l = dr4a.length;
			if (l > 0) {
				for (int i=0; i<l-1; ++i) {
					dr4 = dr4 + dr4a[i] + ", ";
				}
				dr4 = dr4 + dr4a[l-1];
			}

			String[] dr5a = (String[]) dr.get(4);
			l = dr5a.length;
			if (l > 0) {
				for (int i=0; i<l-1; ++i) {
					dr5 = dr5 + dr5a[i] + ", ";
				}
				dr5 = dr5 + dr5a[l-1];
			}

			String[] dr6a = (String[]) dr.get(5);
			l = dr6a.length;
			if (l > 0) {
				for (int i=0; i<l-1; ++i) {
					dr6 = dr6 + dr6a[i] + ", ";
				}
				dr6 = dr6 + dr6a[l-1];
			}

			String[] dr7a = (String[]) dr.get(6);
			l = dr7a.length;
			if (l > 0) {
				for (int i=0; i<l-1; ++i) {
					dr7 = dr7 + dr7a[i] + ", ";
				}
				dr7 = dr7 + dr7a[l-1];
			}
			
			String[] dr8a = (String[]) dr.get(7);
			l = dr8a.length;
			if (l > 0) {
				for (int i=0; i<l-1; ++i) {
					dr8 = dr8 + dr8a[i] + ", ";
				}
				dr8 = dr8 + dr8a[l-1];
			}

			String[] tr1a = (String[]) tr.get(0);
			l = tr1a.length;
			if (l > 0) {
				for (int i=0; i<l-1; ++i) {
					tr1 = tr1 + tr1a[i] + ", ";
				}
				tr1 = tr1 + tr1a[l-1];
			}

			String[] tr2a = (String[]) tr.get(1);
			l = tr2a.length;
			if (l > 0) {
				for (int i=0; i<l-1; ++i) {
					tr2 = tr2 + tr2a[i] + ", ";
				}
				tr2 = tr2 + tr2a[l-1];
			}

			String[] tr3a = (String[]) tr.get(2);
			l = tr3a.length;
			if (l > 0) {
				for (int i=0; i<l-1; ++i) {
					tr3 = tr3 + tr3a[i] + ", ";
				}
				tr3 = tr3 + tr3a[l-1];
			}

			String[] tr4a = (String[]) tr.get(3);
			l = tr4a.length;
			if (l > 0) {
				for (int i=0; i<l-1; ++i) {
					tr4 = tr4 + tr4a[i] + ", ";
				}
				tr4 = tr4 + tr4a[l-1];
			}
			
			String[] tr5a = (String[]) tr.get(4);
			l = tr5a.length;
			if (l > 0) {
				for (int i=0; i<l-1; ++i) {
					tr5 = tr5 + tr5a[i] + ", ";
				}
				tr5 = tr5 + tr5a[l-1];
			}

			String[] tr6a = (String[]) tr.get(5);
			l = tr6a.length;
			if (l > 0) {
				for (int i=0; i<l-1; ++i) {
					tr6 = tr6 + tr6a[i] + ", ";
				}
				tr6 = tr6 + tr6a[l-1];
			}

			String[] tr7a = (String[]) tr.get(6);
			l = tr7a.length;
			if (l > 0) {
				for (int i=0; i<l-1; ++i) {
					tr7 = tr7 + tr7a[i] + ", ";
				}
				tr7 = tr7 + tr7a[l-1];
			}

			String[] tr8a = (String[]) tr.get(7);
			l = tr8a.length;
			if (l > 0) {
				for (int i=0; i<l-1; ++i) {
					tr8 = tr8 + tr8a[i] + ", ";
				}
				tr8 = tr8 + tr8a[l-1];
			}
			
			String[] qr1a = (String[]) qr.get(0);
			l = qr1a.length;
			if (l > 0) {
				for (int i=0; i<l-1; ++i) {
					qr1 = qr1 + qr1a[i] + ", ";
				}
				qr1 = qr1 + qr1a[l-1];
			}

			String[] qr2a = (String[]) qr.get(1);
			l = qr2a.length;
			if (l > 0) {
				for (int i=0; i<l-1; ++i) {
					qr2 = qr2 + qr2a[i] + ", ";
				}
				qr2 = qr2 + qr2a[l-1];
			}

			String[] qr3a = (String[]) qr.get(2);
			l = qr3a.length;
			if (l > 0) {
				for (int i=0; i<l-1; ++i) {
					qr3 = qr3 + qr3a[i] + ", ";
				}
				qr3 = qr3 + qr3a[l-1];
			}

			String[] qr4a = (String[]) qr.get(3);
			l = qr4a.length;
			if (l > 0) {
				for (int i=0; i<l-1; ++i) {
					qr4 = qr4 + qr4a[i] + ", ";
				}
				qr4 = qr4 + qr4a[l-1];
			}

			String[] qr5a = (String[]) qr.get(4);
			l = qr5a.length;
			if (l > 0) {
				for (int i=0; i<l-1; ++i) {
					qr5 = qr5 + qr5a[i] + ", ";
				}
				qr5 = qr5 + qr5a[l-1];
			}

			String[] qr6a = (String[]) qr.get(5);
			l = qr6a.length;
			if (l > 0) {
				for (int i=0; i<l-1; ++i) {
					qr6 = qr6 + qr6a[i] + ", ";
				}
				qr6 = qr6 + qr6a[l-1];
			}

			String[] qr7a = (String[]) qr.get(6);
			l = qr7a.length;
			if (l > 0) {
				for (int i=0; i<l-1; ++i) {
					qr7 = qr7 + qr7a[i] + ", ";
				}
				qr7 = qr7 + qr7a[l-1];
			}

			String[] qr8a = (String[]) qr.get(7);
			l = qr8a.length;
			if (l > 0) {
				for (int i=0; i<l-1; ++i) {
					qr8 = qr8 + qr8a[i] + ", ";
				}
				qr8 = qr8 + qr8a[l-1];
			}

			String[] ap1a = (String[]) ap.get(0);
			l = ap1a.length;
			if (l > 0) {
				for (int i=0; i<l-1; ++i) {
					ap1 = ap1 + ap1a[i] + ", ";
				}
				ap1 = ap1 + ap1a[l-1];
			}

			String[] ap2a = (String[]) ap.get(1);
			l = ap2a.length;
			if (l > 0) {
				for (int i=0; i<l-1; ++i) {
					ap2 = ap2 + ap2a[i] + ", ";
				}
				ap2 = ap2 + ap2a[l-1];
			}

			String[] ap3a = (String[]) ap.get(2);
			l = ap3a.length;
			if (l > 0) {
				for (int i=0; i<l-1; ++i) {
					ap3 = ap3 + ap3a[i] + ", ";
				}
				ap3 = ap3 + ap3a[l-1];
			}
			
			String[] ap4a = (String[]) ap.get(3);
			l = ap4a.length;
			if (l > 0) {
				for (int i=0; i<l-1; ++i) {
					ap4 = ap4 + ap4a[i] + ", ";
				}
				ap4 = ap4 + ap4a[l-1];
			}

			String[] ap5a = (String[]) ap.get(4);
			l = ap5a.length;
			if (l > 0) {
				for (int i=0; i<l-1; ++i) {
					ap5 = ap5 + ap5a[i] + ", ";
				}
				ap5 = ap5 + ap5a[l-1];
			}

			String[] ap6a = (String[]) ap.get(5);
			l = ap6a.length;
			if (l > 0) {
				for (int i=0; i<l-1; ++i) {
					ap6 = ap6 + ap6a[i] + ", ";
				}
				ap6 = ap6 + ap6a[l-1];
			}

			String[] ap7a = (String[]) ap.get(6);
			l = ap7a.length;
			if (l > 0) {
				for (int i=0; i<l-1; ++i) {
					ap7 = ap7 + ap7a[i] + ", ";
				}
				ap7 = ap7 + ap7a[l-1];
			}

			String[] ap8a = (String[]) ap.get(7);
			l = ap8a.length;
			if (l > 0) {
				for (int i=0; i<l-1; ++i) {
					ap8 = ap8 + ap8a[i] + ", ";
				}
				ap8 = ap8 + ap8a[l-1];
			}		
		}
		catch (IOException io) {
			System.out.println(io + " in getRooms()");
		}
		catch (ClassNotFoundException cnf) {
			System.out.println(cnf + " in getRooms()");
		}
	}

	/**
	 * This method initializes jPanel4
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJPanel4() {
		if(jPanel4 == null) {
			jPanel4 = new javax.swing.JPanel();
			jPanel4.setLayout(null);
			jPanel4.add(getJRadioButton24(), null);
			jPanel4.add(getJRadioButton25(), null);
			jPanel4.add(getJRadioButton26(), null);
			jPanel4.add(getJButton11(), null);
			jPanel4.add(getJButton12(), null);
			jPanel4.setBounds(141, 57, 390, 212);
			jPanel4.setVisible(false);
			jPanel4.setBackground(java.awt.SystemColor.window);
		}
		return jPanel4;
	}
	/**
	 * This method initializes jRadioButton24
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton24() {
		if(jRadioButton24 == null) {
			jRadioButton24 = new javax.swing.JRadioButton();
			jRadioButton24.setBounds(18, 49, 127, 25);
			jRadioButton24.setText("800 x 600");
			jRadioButton24.setBackground(java.awt.SystemColor.window);
			jRadioButton24.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton25.setSelected(false);
					jRadioButton26.setSelected(false);
					
					resolution = 600;
				}
			});
		}
		return jRadioButton24;
	}
	/**
	 * This method initializes jRadioButton25
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton25() {
		if(jRadioButton25 == null) {
			jRadioButton25 = new javax.swing.JRadioButton();
			jRadioButton25.setBounds(18, 79, 127, 25);
			jRadioButton25.setText("1024 x 768");
			jRadioButton25.setBackground(java.awt.SystemColor.window);
			jRadioButton25.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton24.setSelected(false);
					jRadioButton26.setSelected(false);
					
					resolution = 768;
				}
			});
		}
		return jRadioButton25;
	}
	/**
	 * This method initializes jRadioButton26
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton26() {
		if(jRadioButton26 == null) {
			jRadioButton26 = new javax.swing.JRadioButton();
			jRadioButton26.setBounds(18, 109, 127, 25);
			jRadioButton26.setText("1280x960");
			jRadioButton26.setBackground(java.awt.SystemColor.window);
			jRadioButton26.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton25.setSelected(false);
					jRadioButton24.setSelected(false);
					
					resolution = 960;
				}
			});
		}
		return jRadioButton26;
	}
	/**
	 * This method initializes jButton11
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButton11() {
		if(jButton11 == null) {
			jButton11 = new javax.swing.JButton();
			jButton11.setText(language[21]);
			jButton11.setBounds(279, 173, 98, 25);
			jButton11.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					settings[3] = Integer.toString(resolution);
					jPanel4.setVisible(false);
					jButton1.setVisible(true);
					jButton2.setVisible(true);
				}
			});
		}
		return jButton11;
	}
	/**
	 * This method initializes jButton12
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButton12() {
		if(jButton12 == null) {
			jButton12 = new javax.swing.JButton();
			jButton12.setText(language[22]);
			jButton12.setBounds(160, 174, 108, 25);
			jButton12.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					jPanel4.setVisible(false);
					jButton1.setVisible(true);
					jButton2.setVisible(true);
				}
			});
		}
		return jButton12;
	}
}  //  @jve:visual-info  decl-index=0 visual-constraint="10,10"
