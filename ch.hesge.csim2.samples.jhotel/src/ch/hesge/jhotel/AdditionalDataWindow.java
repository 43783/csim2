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

public class AdditionalDataWindow extends Frame {

	private javax.swing.JLabel jLabel = null;
	private javax.swing.JTextField jTextField = null;
	private javax.swing.JLabel jLabel1 = null;
	private javax.swing.JTextArea jTextArea = null;
	private javax.swing.JLabel jLabel2 = null;
	private javax.swing.JLabel jLabel3 = null;
	private javax.swing.JLabel jLabel4 = null;
	private javax.swing.JLabel jLabel5 = null;
	private javax.swing.JLabel jLabel6 = null;
	private javax.swing.JButton jButton = null;
	private javax.swing.JTextField jTextField1 = null;
	private javax.swing.JTextField jTextField2 = null;
	private javax.swing.JTextField jTextField3 = null;
	private javax.swing.JTextField jTextField4 = null;
	private javax.swing.JTextField jTextField5 = null;
	private javax.swing.JRadioButton jRadioButton = null;
	private javax.swing.JRadioButton jRadioButton1 = null;
	private javax.swing.JTextField jTextField6 = null;
	private javax.swing.JTextField jTextField7 = null;
	private javax.swing.JTextField jTextField8 = null;
	private javax.swing.JTextField jTextField9 = null;
	private javax.swing.JLabel jLabel8 = null;
	private javax.swing.JRadioButton jRadioButton2 = null;
	private javax.swing.JRadioButton jRadioButton3 = null;
	public static boolean smoker = false;
	MainWindow mw;
	Guest guest;
	int entries;
	public String[] currentGuest;
	private javax.swing.JButton jButton1 = null;
	private javax.swing.JButton jButton2 = null;
	public static AdditionalDataWindow thisWindow;
	String[] language;
	
	
	/**
	 * This is the default constructor
	 */
	public AdditionalDataWindow(Guest guest, MainWindow mw, int entries) {
		Language lang = new Language();
		language = lang.getLanguage();
		
		
		this.guest = guest;
		this.entries = entries;
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
		this.add(getJLabel(), null);
		this.add(getJTextField(), null);
		this.add(getJLabel1(), null);
		this.add(getJTextArea(), null);
		this.add(getJLabel2(), null);
		this.add(getJLabel3(), null);
		this.add(getJLabel4(), null);
		this.add(getJLabel5(), null);
		this.add(getJLabel6(), null);
		//this.add(getJButton(), null);
		this.add(getJTextField1(), null);
		this.add(getJTextField2(), null);
		this.add(getJTextField3(), null);
		this.add(getJTextField4(), null);
		this.add(getJTextField5(), null);
		this.add(getJRadioButton(), null);
		this.add(getJRadioButton1(), null);
		this.add(getJTextField6(), null);
		this.add(getJTextField7(), null);
		this.add(getJTextField8(), null);
		this.add(getJTextField9(), null);
		this.add(getJLabel8(), null);
		this.add(getJRadioButton2(), null);
		this.add(getJRadioButton3(), null);
		this.add(getJButton1(), null);
		this.add(getJButton2(), null);
		this.setSize(382, 472);
		this.setTitle(language[32]);
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
			jLabel.setBounds(24, 44, 104, 20);
			jLabel.setText(language[7]);
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
			jTextField.setBounds(24, 63, 338, 19);
		}
		return jTextField;
	}
	/**
	 * This method initializes jLabel1
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel1() {
		if(jLabel1 == null) {
			jLabel1 = new javax.swing.JLabel();
			jLabel1.setBounds(24, 89, 104, 20);
			jLabel1.setText(language[8]);
		}
		return jLabel1;
	}
	/**
	 * This method initializes jTextArea
	 * 
	 * @return javax.swing.JTextArea
	 */
	private javax.swing.JTextArea getJTextArea() {
		if(jTextArea == null) {
			jTextArea = new javax.swing.JTextArea();
			jTextArea.setBounds(24, 108, 338, 61);
		}
		return jTextArea;
	}
	/**
	 * This method initializes jLabel2
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel2() {
		if(jLabel2 == null) {
			jLabel2 = new javax.swing.JLabel();
			jLabel2.setBounds(24, 226, 104, 20);
			jLabel2.setText(language[9]);
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
			jLabel3.setBounds(24, 250, 104, 20);
			jLabel3.setText(language[10]);
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
			jLabel4.setBounds(24, 275, 104, 20);
			jLabel4.setText(language[11]);
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
			jLabel5.setBounds(24, 301, 104, 20);
			jLabel5.setText(language[12]);
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
			jLabel6.setBounds(24, 353, 145, 20);
			jLabel6.setText(language[13]);
		}
		return jLabel6;
	}
	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	/*private javax.swing.JButton getJButton() {
		if(jButton == null) {
			jButton = new javax.swing.JButton();
			jButton.setBounds(341, 44, 20, 20);
			jButton.setText("...");
			jButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						guest.findGuest(jTextField.getText()); 
					}
					catch (EOFException eo1) {
						// nada
					}
					
				}
			});

		}
		return jButton;
	}*/
	/**
	 * This method initializes jTextField1
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField1() {
		if(jTextField1 == null) {
			jTextField1 = new javax.swing.JTextField();
			jTextField1.setBounds(135, 226, 227, 21);
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
			jTextField2.setBounds(135, 250, 227, 21);
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
			jTextField3.setBounds(135, 275, 227, 21);
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
			jTextField4.setBounds(135, 301, 227, 21);
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
			jTextField5.setBounds(176, 353, 186, 21);
		}
		return jTextField5;
	}
	/**
	 * This method initializes jRadioButton
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton() {
		if(jRadioButton == null) {
			jRadioButton = new javax.swing.JRadioButton();
			jRadioButton.setBounds(135, 198, 83, 21);
			jRadioButton.setText(language[17]);
			jRadioButton.setSelected(true);
			jRadioButton.setBackground(java.awt.SystemColor.window);
			jRadioButton.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					setBusiness(false);
					jRadioButton1.setSelected(false);
				}
			});
		}
		return jRadioButton;
	}
	
	public void setBusiness(boolean state) {
		jTextField6.setVisible(state);
		jTextField7.setVisible(state);
		jTextField8.setVisible(state);
		jTextField9.setVisible(state);
		jTextField4.setVisible(!state);
		jTextField3.setVisible(!state);
		jTextField2.setVisible(!state);
		jTextField1.setVisible(!state);
	}
	
	/**
	 * This method initializes jRadioButton1
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton1() {
		if(jRadioButton1 == null) {
			jRadioButton1 = new javax.swing.JRadioButton();
			jRadioButton1.setBounds(234, 198, 128, 21);
			jRadioButton1.setText(language[18]);
			jRadioButton1.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
			jRadioButton1.setBackground(java.awt.SystemColor.window);
			jRadioButton1.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					setBusiness(true);
					jRadioButton.setSelected(false);
				}
			});
		}
		return jRadioButton1;
	}
	/**
	 * This method initializes jTextField6
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField6() {
		if(jTextField6 == null) {
			jTextField6 = new javax.swing.JTextField();
			jTextField6.setBounds(135, 226, 227, 21);
			jTextField6.setVisible(false);
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
			jTextField7.setBounds(135, 250, 227, 21);
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
			jTextField8.setBounds(135, 275, 227, 21);
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
			jTextField9.setBounds(135, 301, 227, 21);
			jTextField9.setVisible(false);
		}
		return jTextField9;
	}
	
	public String getWife() {
		return jTextField.getText();
	}
	public void setWife(String val) {
		jTextField.setText(val);
	}
	public String getChildren() {
		return jTextArea.getText();
	}
	public void setChildren(String val) {
		jTextArea.setText(val);
	}
	public String getPhone(boolean business) {
		if (business == false) {
			return jTextField1.getText();
		}
		else {
			return jTextField6.getText();
		}
	}
	public void setPhone(boolean business, String val) {
		if (business == false) {
			jTextField1.setText(val);
		}
		else {
			jTextField6.setText(val);
		}
	}
	public String getMobile(boolean business) {
		if (business == false) {
			return jTextField2.getText();
		}
		else {
			return jTextField7.getText();
		}
	}
	public void setMobile(boolean business, String val) {
		if (business == false) {
			jTextField2.setText(val);
		}
		else {
			jTextField7.setText(val);
		}
	}
	public String getFax(boolean business) {
		if (business == false) {
			return jTextField3.getText();
		}
		else {
			return jTextField8.getText();
		}
	}
	public void setFax(boolean business, String val) {
		if (business == false) {
			jTextField3.setText(val);
		}
		else {
			jTextField8.setText(val);
		}
	}
	public String getEmail(boolean business) {
		if (business == false) {
			return jTextField4.getText();
		}
		else {
			return jTextField9.getText();
		}
	}
	public void setEmail(boolean business, String val) {
		if (business == false) {
			jTextField4.setText(val);
		}
		else {
			jTextField9.setText(val);
		}
	}
	public String getRoom() {
		return jTextField5.getText();
	}
	public void setRoom(String val) {
		jTextField5.setText(val); 
	}
	public String getSmoker() {
		String yes = "yes";
		String no = "N/A";
		
		if (smoker==true) {
			return (yes);
		}
		/*else if (smoker==false) {
			return (no);
		}*/
		else {
			
			return (no);
		}
	}
	public void setSmoker(String val) {
		if (val.equalsIgnoreCase("yes")) {
			jRadioButton3.setSelected(false);
			jRadioButton2.setSelected(true);
			smoker = true;
		}
		else {
			jRadioButton2.setSelected(false);
			jRadioButton3.setSelected(true);
			smoker = false;
		}
	}
	public void clearFields() {
		jTextField.setText("");
		jTextField1.setText("");
		jTextField2.setText("");
		jTextField3.setText("");
		jTextField4.setText("");
		jTextField5.setText("");
		jTextField6.setText("");
		jTextField7.setText("");
		jTextField8.setText("");
		jTextField9.setText("");
		jTextArea.setText("");
		jRadioButton.setSelected(true);
		jRadioButton1.setSelected(false);
		jRadioButton2.setSelected(true);
		jRadioButton3.setSelected(false);
	}
	
	/**
	 * This method initializes jLabel8
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel8() {
		if(jLabel8 == null) {
			jLabel8 = new javax.swing.JLabel();
			jLabel8.setBounds(24, 383, 104, 20);
			jLabel8.setText(language[14]);
		}
		return jLabel8;
	}
	/**
	 * This method initializes jRadioButton2
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton2() {
		if(jRadioButton2 == null) {
			jRadioButton2 = new javax.swing.JRadioButton();
			jRadioButton2.setBounds(176, 383, 53, 20);
			jRadioButton2.setText(language[15]);
			jRadioButton2.setBackground(java.awt.SystemColor.window);
			jRadioButton2.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton3.setSelected(false);
					smoker = true;
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
			jRadioButton3.setBounds(229, 383, 64, 20);
			jRadioButton3.setText(language[16]);
			jRadioButton3.setBackground(java.awt.SystemColor.window);
			jRadioButton3.setSelected(true);
			jRadioButton3.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton2.setSelected(false);
					smoker = false;
				}
			});
		}
		return jRadioButton3;
	}
	/**
	 * This method initializes jButton1
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButton1() {
		if(jButton1 == null) {
			jButton1 = new javax.swing.JButton();
			jButton1.setBounds(161, 433, 100, 23);
			jButton1.setText(language[22]);
			jButton1.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					Guest g = new Guest();
					String[] tst = new String[entries];
					tst = getTempGuest();
					boolean modified = true;
					
					loop1:
					for (int i=0; i<entries; ++i) {
						if (tst[i].equals(currentGuest[i])) {
							modified = false;
						}
						else {
							modified = true;
							break loop1;
						}
					}
					
					if (modified) {
						YesNoDialog zn = new YesNoDialog(mw, currentGuest, language[52], "undoAddEntry");
						zn.setVisible(true);
					}
					else {
						setVisible(false);
					}
					
				}
			});
		}
		return jButton1;
	}
	
	public void setTempGuest(String[] tmpGuest) {
		currentGuest = tmpGuest;
	}
	
	public void reset(String[] tmpGuest) {
		mw.setGuest(tmpGuest);
		setVisible(false);
	}

	public String[] getTempGuest() {
		String[] tst = new String[entries];
		tst[0] = mw.getCompany();
		tst[1] = mw.getName();
		tst[2] = mw.getFirstName();
		tst[3] = mw.getAddress(false);
		tst[4] = mw.getBirthday();
		tst[5] = mw.getCitizenship();
		tst[6] = mw.getAddress(true);
		tst[7] = getWife();
		tst[8] = getChildren();
		tst[9] = getPhone(false);
		tst[10] = getMobile(false);
		tst[11] = getFax(false);
		tst[12] = getEmail(false);
		tst[13] = getPhone(true);
		tst[14] = getMobile(true);
		tst[15] = getFax(true);
		tst[16] = getEmail(true);
		tst[17] = getRoom();
		if (smoker) {
			tst[18] = ("yes");
		}
		else {
			tst[18] = ("N/A");
		}
		return tst;
	}





	/**
	 * This method initializes jButton2
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButton2() {
		if(jButton2 == null) {
			jButton2 = new javax.swing.JButton();
			jButton2.setBounds(272, 433, 90, 23);
			jButton2.setText(language[21]);
			jButton2.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					setVisible(false);
				}
			});
		}
		return jButton2;
	}
}  //  @jve:visual-info  decl-index=0 visual-constraint="10,10"
