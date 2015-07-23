package ch.hesge.jhotel;
/**
 * Copyright 2004, Martin Jungowski
 *
 *  This file is part of JHotel.
 *
 *  JHotel is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  JHotel is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *   along with JHotel; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *
 *  */
import java.util.*;

import java.awt.Frame;

public class AlbumWindow extends Frame {

	private javax.swing.JLabel jLabel = null;
	private javax.swing.JLabel jLabel1 = null;
	private javax.swing.JLabel jLabel2 = null;
	private javax.swing.JLabel jLabel3 = null;
	private javax.swing.JLabel jLabel4 = null;
	private javax.swing.JLabel jLabel5 = null;
	private javax.swing.JLabel jLabel6 = null;
	private javax.swing.JLabel jLabel7 = null;
	private javax.swing.JLabel jLabel8 = null;
	private javax.swing.JLabel jLabel9 = null;
	private javax.swing.JLabel jLabel10 = null;
	private javax.swing.JLabel jLabel11 = null;
	private javax.swing.JLabel jLabel12 = null;
	private javax.swing.JLabel jLabel13 = null;
	private javax.swing.JTextField jTextField = null;
	private javax.swing.JTextField jTextField1 = null;
	private javax.swing.JTextField jTextField2 = null;
	private javax.swing.JTextArea jTextArea = null;
	private javax.swing.JTextField jTextField4 = null;
	private javax.swing.JTextField jTextField5 = null;
	private javax.swing.JTextField jTextField6 = null;
	private javax.swing.JTextField jTextField7 = null;
	private javax.swing.JTextField jTextField8 = null;
	private javax.swing.JTextField jTextField9 = null;
	private javax.swing.JTextField jTextField10 = null;
	private javax.swing.JTextField jTextField11 = null;
	private javax.swing.JTextField jTextField12 = null;
	private javax.swing.JTextField jTextField13 = null;
	private javax.swing.JButton jButton = null;
	private javax.swing.JButton jButton1 = null;
	private javax.swing.JProgressBar jProgressBar = null;
	private javax.swing.JLabel jLabel14 = null;
	private AlbumWindow thisWindow;
	private Album album;
	private ArrayList newEntries = new ArrayList();
	
	
	
	private javax.swing.JButton jButton2 = null;
	private javax.swing.JButton jButton3 = null;
	public AlbumWindow() {
		this.thisWindow = this;
		
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
		this.add(getJLabel5(), null);
		this.add(getJLabel6(), null);
		this.add(getJLabel7(), null);
		this.add(getJLabel8(), null);
		this.add(getJLabel9(), null);
		this.add(getJLabel10(), null);
		this.add(getJLabel11(), null);
		this.add(getJLabel12(), null);
		this.add(getJLabel13(), null);
		this.add(getJTextField(), null);
		this.add(getJTextField1(), null);
		this.add(getJTextField2(), null);
		this.add(getJTextArea(), null);
		this.add(getJTextField4(), null);
		this.add(getJTextField5(), null);
		this.add(getJTextField6(), null);
		this.add(getJTextField7(), null);
		this.add(getJTextField8(), null);
		this.add(getJTextField9(), null);
		this.add(getJTextField10(), null);
		this.add(getJTextField11(), null);
		this.add(getJTextField12(), null);
		this.add(getJTextField13(), null);
		this.add(getJButton(), null);
		this.add(getJButton1(), null);
		this.add(getJProgressBar(), null);
		this.add(getJLabel14(), null);
		this.add(getJButton2(), null);
		this.add(getJButton3(), null);
		this.setSize(713, 556);
		this.addWindowListener(new java.awt.event.WindowAdapter() { 
			public void windowClosing(java.awt.event.WindowEvent e) {    
				dispose();
			}
		});
		Album album = new Album(thisWindow);
		album.start();
		this.album = album;
		
	}
	/**
	 * This method initializes jLabel
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel() {
		if(jLabel == null) {
			jLabel = new javax.swing.JLabel();
			jLabel.setBounds(220, 33, 244, 21);
			jLabel.setText("Lfd. Nummer");
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
			jLabel1.setBounds(220, 67, 244, 21);
			jLabel1.setText("Zimmer");
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
			jLabel2.setBounds(220, 101, 244, 21);
			jLabel2.setText("Name, Vorname");
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
			jLabel3.setBounds(220, 135, 244, 21);
			jLabel3.setText("Adresse");
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
			jLabel4.setBounds(220, 179, 244, 21);
			jLabel4.setText("Geburtsdatum");
		}
		return jLabel4;
	}
	/**
	 * This method initializes jLabel5
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel5() {
		if(jLabel5 == null) {
			jLabel5 = new javax.swing.JLabel();
			jLabel5.setBounds(220, 203, 244, 21);
			jLabel5.setText("Geburtsort");
		}
		return jLabel5;
	}
	/**
	 * This method initializes jLabel6
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel6() {
		if(jLabel6 == null) {
			jLabel6 = new javax.swing.JLabel();
			jLabel6.setBounds(220, 237, 244, 21);
			jLabel6.setText("Staatsangehörigkeit");
		}
		return jLabel6;
	}
	/**
	 * This method initializes jLabel7
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel7() {
		if(jLabel7 == null) {
			jLabel7 = new javax.swing.JLabel();
			jLabel7.setBounds(220, 271, 244, 21);
			jLabel7.setText("Beruf, Firma");
		}
		return jLabel7;
	}
	/**
	 * This method initializes jLabel8
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel8() {
		if(jLabel8 == null) {
			jLabel8 = new javax.swing.JLabel();
			jLabel8.setBounds(220, 305, 244, 21);
			jLabel8.setText("Kinder unter 18 Jahren");
		}
		return jLabel8;
	}
	/**
	 * This method initializes jLabel9
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel9() {
		if(jLabel9 == null) {
			jLabel9 = new javax.swing.JLabel();
			jLabel9.setBounds(220, 339, 244, 21);
			jLabel9.setText("Teilnehmer (Gruppe)");
		}
		return jLabel9;
	}
	/**
	 * This method initializes jLabel10
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel10() {
		if(jLabel10 == null) {
			jLabel10 = new javax.swing.JLabel();
			jLabel10.setBounds(220, 373, 244, 21);
			jLabel10.setText("Personalausweis oder Reisepass");
		}
		return jLabel10;
	}
	/**
	 * This method initializes jLabel11
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel11() {
		if(jLabel11 == null) {
			jLabel11 = new javax.swing.JLabel();
			jLabel11.setBounds(220, 407, 244, 21);
			jLabel11.setText("Kraftfahrzeug Kennzeichen");
		}
		return jLabel11;
	}
	/**
	 * This method initializes jLabel12
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel12() {
		if(jLabel12 == null) {
			jLabel12 = new javax.swing.JLabel();
			jLabel12.setBounds(220, 441, 244, 21);
			jLabel12.setText("Ankunft");
		}
		return jLabel12;
	}
	/**
	 * This method initializes jLabel13
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel13() {
		if(jLabel13 == null) {
			jLabel13 = new javax.swing.JLabel();
			jLabel13.setBounds(220, 475, 244, 21);
			jLabel13.setText("Abreise");
		}
		return jLabel13;
	}
	/**
	 * This method initializes jTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField() {
		if(jTextField == null) {
			jTextField = new javax.swing.JTextField();
			jTextField.setBounds(463, 34, 225, 21);
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
			jTextField1.setBounds(463, 67, 225, 21);
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
			jTextField2.setBounds(463, 101, 225, 21);
		}
		return jTextField2;
	}
	/**
	 * This method initializes jTextArea
	 * 
	 * @return javax.swing.JTextArea
	 */
	private javax.swing.JTextArea getJTextArea() {
		if(jTextArea == null) {
			jTextArea = new javax.swing.JTextArea();
			jTextArea.setBounds(463, 135, 225, 42);
			jTextArea.setWrapStyleWord(true);
			jTextArea.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.SystemColor.textText,1));
		}
		return jTextArea;
	}
	/**
	 * This method initializes jTextField4
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField4() {
		if(jTextField4 == null) {
			jTextField4 = new javax.swing.JTextField();
			jTextField4.setBounds(463, 179, 225, 21);
		}
		return jTextField4;
	}
	/**
	 * This method initializes jTextField5
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField5() {
		if(jTextField5 == null) {
			jTextField5 = new javax.swing.JTextField();
			jTextField5.setBounds(463, 203, 225, 21);
		}
		return jTextField5;
	}
	/**
	 * This method initializes jTextField6
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField6() {
		if(jTextField6 == null) {
			jTextField6 = new javax.swing.JTextField();
			jTextField6.setBounds(463, 237, 225, 21);
		}
		return jTextField6;
	}
	/**
	 * This method initializes jTextField7
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField7() {
		if(jTextField7 == null) {
			jTextField7 = new javax.swing.JTextField();
			jTextField7.setBounds(463, 271, 225, 21);
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
			jTextField8.setBounds(463, 305, 225, 21);
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
			jTextField9.setBounds(463, 339, 225, 21);
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
			jTextField10.setBounds(463, 373, 225, 21);
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
			jTextField11.setBounds(463, 407, 225, 21);
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
			jTextField12.setBounds(463, 441, 225, 21);
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
			jTextField13.setBounds(463, 475, 225, 21);
		}
		return jTextField13;
	}
	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButton() {
		if(jButton == null) {
			jButton = new javax.swing.JButton();
			jButton.setBounds(572, 520, 119, 24);
			jButton.setText("Next");
			jButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					String[] newEntry = new String[13];
					newEntry[0] = jTextField1.getText();
					newEntry[1] = jTextField2.getText();
					newEntry[2] = jTextArea.getText();
					newEntry[3] = jTextField4.getText();
					newEntry[4] = jTextField5.getText();
					newEntry[5] = jTextField6.getText();
					newEntry[6] = jTextField7.getText();
					newEntry[7] = jTextField8.getText();
					newEntry[8] = jTextField9.getText();
					newEntry[9] = jTextField10.getText();
					newEntry[10] = jTextField11.getText();
					newEntry[11] = jTextField12.getText();
					newEntry[12] = jTextField13.getText();
					
					newEntries.add(newEntry);
					clearFields();
				}
			});
		}
		return jButton;
	}
	
	public void clearFields() {
		jTextField.setText(Integer.toString(Integer.parseInt(jTextField.getText())+1));
		jTextField1.setText("");
		jTextField2.setText("");
		jTextArea.setText("");
		jTextField4.setText("");
		jTextField5.setText("");
		jTextField6.setText("");
		jTextField7.setText("");
		jTextField8.setText("");
		jTextField9.setText("");
		jTextField10.setText("");
		jTextField11.setText("");
		jTextField12.setText("");
		jTextField13.setText("");
		
	}
	/**
	 * This method initializes jButton1
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButton1() {
		if(jButton1 == null) {
			jButton1 = new javax.swing.JButton();
			jButton1.setBounds(26, 520, 119, 24);
			jButton1.setText("Save");
			jButton1.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					album.saveAlbum(newEntries);
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
			jProgressBar.setBounds(270, 520, 280, 24);
		}
		return jProgressBar;
	}
	/**
	 * This method initializes jLabel14
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel14() {
		if(jLabel14 == null) {
			jLabel14 = new javax.swing.JLabel();
			jLabel14.setBounds(168, 520, 95, 24);
			jLabel14.setText("JLabel");
		}
		return jLabel14;
	}
	
	public void setThreadRunning() {
		jProgressBar.setVisible(true);
		jProgressBar.setIndeterminate(true);
		jLabel14.setVisible(true);
		jLabel14.setText("Loading");	
	}
	
	public void setThreadEnded(long entries) {
		jProgressBar.setVisible(false);
		jProgressBar.setIndeterminate(false);
		jLabel14.setVisible(false);
		jTextField.setText(Long.toString(entries+1));
	}
	
	public static void main(String[] args) {
		AlbumWindow aw = new AlbumWindow();
		aw.setVisible(true);
	}
	
	/**
	 * This method initializes jButton2
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButton2() {
		if(jButton2 == null) {
			jButton2 = new javax.swing.JButton();
			jButton2.setBounds(11, 67, 176, 21);
			jButton2.setText("Stammgast");
			jButton2.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					AlbumSearchWindow asw = new AlbumSearchWindow(thisWindow);
					asw.setVisible(true);
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
			jButton3.setBounds(11, 135, 176, 21);
			jButton3.setText("Im Buch suchen");
			jButton3.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					//AlbumSearch as = new AlbumSearch(thisWindow);
					//as.setVisible(true);
				}
			});
		}
		return jButton3;
	}
	
	public void setGuest(String[] guest) {
		
		jTextField2.setText(guest[1] + ", " + guest[2]);
		jTextField4.setText(guest[4]);
		jTextField6.setText(guest[5]);
		jTextArea.setText(guest[3]);
		jTextField7.setText(guest[0]); //set company
		
	}
}  //  @jve:visual-info  decl-index=0 visual-constraint="1,10"
