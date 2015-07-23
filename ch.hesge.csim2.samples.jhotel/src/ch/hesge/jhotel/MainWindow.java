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

public class MainWindow extends Frame implements ActionListener {

	private javax.swing.JLabel jLabel = null;
	private javax.swing.JButton jButton = null;
	private javax.swing.JButton jButton1 = null;
	private javax.swing.JButton jButton2 = null;
	private javax.swing.JButton jButton3 = null;
	private javax.swing.JLabel jLabel1 = null;
	private javax.swing.JLabel jLabel2 = null;
	private javax.swing.JLabel jLabel4 = null;
	private javax.swing.JLabel jLabel5 = null;
	private javax.swing.JButton jButton4 = null;
	private javax.swing.JTextField jTextField = null;
	private javax.swing.JTextField jTextField1 = null;
	private javax.swing.JTextArea jTextArea = null;
	private javax.swing.JTextField jTextField2 = null;
	private javax.swing.JTextField jTextField3 = null;
	private javax.swing.JButton jButton5 = null;
	private javax.swing.JButton jButton6 = null;
	private javax.swing.JButton jButton7 = null;
	private javax.swing.JLabel jLabel6 = null;
	private javax.swing.JLabel jLabel7 = null;
	private javax.swing.JTextField jTextField4 = null;
	public static MainWindow thisWindow;
	public static SearchWindow searchWindow;
	public static AdditionalDataWindow addDataWindow;
	public static boolean decision = false;
	private static int entries;
	static Guest g = new Guest();
	public String[] currentGuest = new String[entries];
	public boolean first = true;
	public boolean smoker = false;
	public String[] oldGuest = new String[entries];
	
	private static String[] language;
	String file;
	
	
	private javax.swing.JTextArea jTextArea1 = null;
	private javax.swing.JRadioButton jRadioButton = null;
	private javax.swing.JRadioButton jRadioButton1 = null;
	private javax.swing.JLabel jLabel3 = null;
	private javax.swing.JLabel jLabel8 = null;
	private javax.swing.JTextField jTextField5 = null;
	private javax.swing.JButton jButton8 = null;

	private javax.swing.JButton jButton9 = null;
	private javax.swing.JButton jButton10 = null;
	/**
	 * @throws java.awt.HeadlessException
	 */
	public MainWindow() throws HeadlessException {
		super();
		Language lang = new Language();
		language = lang.getLanguage();
			initialize();
	// TODO Auto-generated constructor stub
	}

	/**
	 * @param gc
	 */
	public MainWindow(GraphicsConfiguration gc) {
		super(gc);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param title
	 * @throws java.awt.HeadlessException
	 */
	public MainWindow(String title) throws HeadlessException {
		super(title);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param title
	 * @param gc
	 */
	public MainWindow(String title, GraphicsConfiguration gc) {
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
		this.add(getJLabel(), null);
		this.add(getJButton(), null);
		this.add(getJButton1(), null);
		this.add(getJButton2(), null);
		this.add(getJButton3(), null);
		this.add(getJLabel1(), null);
		this.add(getJLabel2(), null);
		this.add(getJLabel4(), null);
		this.add(getJLabel5(), null);
		this.add(getJButton4(), null);
		this.add(getJTextField(), null);
		this.add(getJTextField1(), null);
		this.add(getJTextArea(), null);
		this.add(getJTextField2(), null);
		this.add(getJTextField3(), null);
		this.add(getJButton5(), null);
		this.add(getJButton6(), null);
		this.add(getJButton7(), null);
		this.add(getJLabel6(), null);
		this.add(getJLabel7(), null);
		this.add(getJTextField4(), null);
		this.add(getJTextArea1(), null);
		this.add(getJRadioButton(), null);
		this.add(getJRadioButton1(), null);
		this.add(getJLabel3(), null);
		this.add(getJLabel8(), null);
		this.add(getJTextField5(), null);
		this.add(getJButton8(), null);
		this.add(getJButton9(), null);
		this.add(getJButton10(), null);
		this.setSize(807, 583);
		this.setTitle("JHotel 0.62");
		this.setVisible(true);
		this.addWindowListener(new java.awt.event.WindowAdapter() { 
			public void windowClosing(java.awt.event.WindowEvent e) {    
				System.exit(0);
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
			jLabel.setBounds(312, 69, 192, 48);
			jLabel.setText("JHotel 0.6");
			jLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			jLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		}
		return jLabel;
	}
	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	
	public void newGuest() {
		boolean modified = true;
		setGuest(0);
		first = false;
					
		//setGuestStatus(true);
		jTextArea1.setVisible(false);
					
					
		String[] tst = new String[entries];
		tst = getTempGuest();
					
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
			YesNoDialog zn = new YesNoDialog(thisWindow, tst, language[52], "undoEntry");
			zn.setVisible(true);
		}
		else {
			clearFields();  
			addDataWindow.clearFields();
			setGuestStatus(true);
		}
	}
	
	private javax.swing.JButton getJButton() {
		if(jButton == null) {
			jButton = new javax.swing.JButton();
			jButton.setBounds(37, 125, 198, 33);
			jButton.setText(language[50]);
			jButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) { 
					newGuest();
				}
			});
		}
		return jButton;
	}
	
	public void setGuest(int value) {
		if (value==0) {
			for (int i=0; i<(entries-1); ++i) {
				currentGuest[i] = ("");
			}
			currentGuest[entries-1] = ("N/A");
		}
	}
	
	/**
	 * This method initializes jButton1
	 * 
	 * @return javax.swing.JButton
	 */
	
	public void searchGuest() {
		searchWindow.setVisible(true);
		searchWindow.setEnabled(true);
		searchWindow.setSearchField(true);
		searchWindow.clearFields();
		//thisWindow.setVisible(false);
	}
	
	private javax.swing.JButton getJButton1() {
		if(jButton1 == null) {
			jButton1 = new javax.swing.JButton();
			jButton1.setBounds(380, 143, 182, 19);
			jButton1.setText(language[1]);
			jButton1.setVisible(false);
			jButton1.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 10));
			jButton1.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					searchGuest();
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
	private javax.swing.JButton getJButton2() {
		if(jButton2 == null) {
			jButton2 = new javax.swing.JButton();
			jButton2.setBounds(37, 232, 198, 33);
			jButton2.setText(language[43]);
			jButton2.setEnabled(true);
			jButton2.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					//setGuestStatus(false);
					//jButton6.setVisible(false);
					ReservationManagement rm = new ReservationManagement(thisWindow);
					rm.setVisible(true);
					new Thread(rm).start();
					rm.setEnabled(true);
					
					setVisible(false);
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
			jButton3.setBounds(37, 272, 198, 33);
			jButton3.setText(language[89]);
			jButton3.setEnabled(true);
			jButton3.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					CheckinWindow cw = new CheckinWindow(thisWindow);
					cw.setVisible(true);
				}
			});
		}
		return jButton3;
	}
	
	
	/**
	 * This method initializes jLabel1
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel1() {
		if(jLabel1 == null) {
			jLabel1 = new javax.swing.JLabel();
			jLabel1.setBounds(381, 230, 139, 21);
			jLabel1.setText(language[29]);
			jLabel1.setVisible(true);
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
			jLabel2.setBounds(381, 279, 139, 21);
			jLabel2.setText(language[28]);
			jLabel2.setVisible(true);
		}
		return jLabel2;
	}
	/**
	 * This method initializes jLabel4
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel4() {
		if(jLabel4 == null) {
			jLabel4 = new javax.swing.JLabel();
			jLabel4.setBounds(382, 431, 139, 21);
			jLabel4.setText(language[45]);
			jLabel4.setVisible(true);
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
			jLabel5.setBounds(382, 457, 139, 21);
			jLabel5.setText(language[31]);
			jLabel5.setVisible(true);
		}
		return jLabel5;
	}
	/**
	 * This method initializes jButton4
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButton4() {
		if(jButton4 == null) {
			jButton4 = new javax.swing.JButton();
			jButton4.setBounds(381, 486, 179, 31);
			jButton4.setText(language[32]);
			jButton4.setVisible(true);
			jButton4.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					String[] tmpGuest = new String[entries];
					tmpGuest = getTempGuest();
					addDataWindow.setTempGuest(tmpGuest);
					addDataWindow.setVisible(true);
				}
			});
		}
		return jButton4;
	}
	/**
	 * This method initializes jTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField() {
		if(jTextField == null) {
			jTextField = new javax.swing.JTextField();
			jTextField.setNextFocusableComponent(getJTextField1());
			jTextField.setBounds(381, 250, 392, 21);
			jTextField.setVisible(true);
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
			jTextField1.setNextFocusableComponent(getJTextArea());
			jTextField1.setBounds(381, 299, 392, 21);
			jTextField1.setVisible(true);
		}
		return jTextField1;
	}
	/**
	 * This method initializes jTextArea
	 * 
	 * @return javax.swing.JTextArea
	 */
	private javax.swing.JTextArea getJTextArea() {
		if(jTextArea == null) {
			jTextArea = new javax.swing.JTextArea();
			jTextArea.setBounds(382, 359, 392, 64);
			jTextArea.setVisible(false);
		}
		return jTextArea;
	}
	/**
	 * This method initializes jTextField2
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField2() {
		if(jTextField2 == null) {
			jTextField2 = new javax.swing.JTextField();
			jTextField2.setNextFocusableComponent(getJTextField3());
			jTextField2.setBounds(526, 431, 248, 21);
			jTextField2.setVisible(true);
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
			jTextField3.setNextFocusableComponent(getJButton5());
			jTextField3.setBounds(526, 457, 248, 21);
			jTextField3.setVisible(true);
		}
		return jTextField3;
	}
	/**
	 * This method initializes jButton5
	 * 
	 * @return javax.swing.JButton
	 */
	
	public void saveGuest() {
		Guest guest = new Guest();
		String[] tst = new String[guest.getEntries()];
		tst = getTempGuest();

		guest.addGuest(tst, currentGuest);

					
		clearFields();
	}
	
	private javax.swing.JButton getJButton5() {
		if(jButton5 == null) {
			jButton5 = new javax.swing.JButton();
			jButton5.setBounds(669, 533, 106, 31);
			jButton5.setText(language[2]);
			jButton5.setVisible(true);
			jButton5.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					saveGuest();
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
			jButton6.setBounds(389, 533, 124, 31);
			jButton6.setText(language[33]);
			jButton6.setVisible(true);
			jButton6.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					String[] tst = new String[entries];
					tst = getTempGuest();
					
					YesNoDialog yn = new YesNoDialog(thisWindow, tst, language[53], "deleteEntry");
					yn.setVisible(true);
					
					
					
			
					
				}
			});
		}
		return jButton6;
	}
	/**
	 * This method initializes jButton7
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButton7() {
		if(jButton7 == null) {
			jButton7 = new javax.swing.JButton();
			jButton7.setBounds(520, 533, 143, 31);
			jButton7.setText(language[34]);
			jButton7.setVisible(true);
			jButton7.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) { 
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
						YesNoDialog zn = new YesNoDialog(thisWindow, tst, language[52], "undoEntry");
						zn.setVisible(true);
					}
					else {
						clearFields(); 
						addDataWindow.clearFields();
					}
					
				}
			});
		}
		return jButton7;
	}
	
	public void setGuestStatus(boolean status) {
		jLabel1.setVisible(status);
		jLabel2.setVisible(status);
		jLabel3.setVisible(status);
		jLabel4.setVisible(status);
		jLabel5.setVisible(status);
		jLabel6.setVisible(!status);
		jLabel7.setVisible(status);
		jLabel8.setVisible(status);
		jButton1.setVisible(status);
		jButton4.setVisible(status);
		jButton5.setVisible(status);
		jButton6.setVisible(!status);
		jButton7.setVisible(status);
		jButton8.setVisible(status);
		jButton9.setVisible(status);
		jTextField.setVisible(status);
		jTextField1.setVisible(status);
		jTextField2.setVisible(status);
		jTextField3.setVisible(status);
		jTextField4.setVisible(status);
		jTextField5.setVisible(status);
		jTextArea.setVisible(status);
		jTextArea1.setVisible(status);
		jRadioButton.setVisible(status);
		jRadioButton1.setVisible(status);
	}
	
	public void setDeleted(boolean status) {
		jButton6.setVisible(status);
	}
	


	public void setGuest(String[] guest) {
		jTextField.setText(guest[1]);
		jTextField1.setText(guest[2]);
		jTextArea.setText(guest[3]);
		jTextArea1.setText(guest[6]);
		jTextField2.setText(guest[4]);
		jTextField3.setText(guest[5]);
		jTextField4.setText(guest[0]);
		jRadioButton.setSelected(true);
		jRadioButton1.setSelected(false);
		jTextArea1.setVisible(false);
		jTextArea.setVisible(true);
		addDataWindow.setWife(guest[7]);
		addDataWindow.setChildren(guest[8]);
		addDataWindow.setPhone(false, guest[9]);
		addDataWindow.setMobile(false, guest[10]);
		addDataWindow.setFax(false, guest[11]);
		addDataWindow.setEmail(false, guest[12]);
		addDataWindow.setPhone(true, guest[13]);
		addDataWindow.setMobile(true, guest[14]);
		addDataWindow.setFax(true, guest[15]);
		addDataWindow.setEmail(true, guest[16]);
		addDataWindow.setRoom(guest[17]);
		addDataWindow.setSmoker(guest[18]);
	}
	
	
	/**
	 * This method initializes jLabel6
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel6() {
		if(jLabel6 == null) {
			jLabel6 = new javax.swing.JLabel();
			jLabel6.setBounds(474, 253, 222, 29);
			jLabel6.setText(language[46]);
			jLabel6.setVisible(true);
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
			jLabel7.setBounds(381, 181, 139, 21);
			jLabel7.setText(language[27]);
			jLabel7.setVisible(true);
		}
		return jLabel7;
	}
	/**
	 * This method initializes jTextField4
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField4() {
		if(jTextField4 == null) {
			jTextField4 = new javax.swing.JTextField();
			jTextField4.setNextFocusableComponent(getJTextField());
			jTextField4.setBounds(381, 201, 392, 21);
			jTextField4.setVisible(true);
		}
		return jTextField4;
	}
	
	public void clearFields() {
		jTextField.setText("");
		jTextField1.setText("");
		jTextArea.setText("");
		jTextArea1.setText("");
		jTextField2.setText("");
		jTextField3.setText("");
		jTextField4.setText("");
		jTextField5.setText("");
		addDataWindow.setWife("");
		addDataWindow.setChildren("");
		addDataWindow.setPhone(false, "");
		addDataWindow.setMobile(false, "");
		addDataWindow.setFax(false, "");
		addDataWindow.setEmail(false, "");
		addDataWindow.setPhone(true, "");
		addDataWindow.setMobile(true, "");
		addDataWindow.setFax(true, "");
		addDataWindow.setEmail(true, "");
		addDataWindow.setRoom("");
		addDataWindow.setSmoker("N/A");
	}	
	
	public void addDataWindowReset(String[] guest) {
		addDataWindow.reset(guest);
	}
	
	/**
	 * This method initializes jTextArea1
	 * 
	 * @return javax.swing.JTextArea
	 */
	private javax.swing.JTextArea getJTextArea1() {
		if(jTextArea1 == null) {
			jTextArea1 = new javax.swing.JTextArea();
			jTextArea1.setBounds(382, 359, 392, 64);
			jTextArea1.setVisible(true);
		}
		return jTextArea1;
	}
	/**
	 * This method initializes jRadioButton
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton() {
		if(jRadioButton == null) {
			jRadioButton = new javax.swing.JRadioButton();
			jRadioButton.setBounds(552, 339, 101, 19);
			jRadioButton.setText(language[17]);
			jRadioButton.setVisible(true);
			jRadioButton.setSelected(true);
			jRadioButton.setBackground(java.awt.SystemColor.window);
			jRadioButton.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jTextArea.setVisible(true);
					jTextArea1.setVisible(false);
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
			jRadioButton1.setBounds(655, 338, 104, 20);
			jRadioButton1.setText(language[18]);
			jRadioButton1.setVisible(true);
			jRadioButton1.setBackground(java.awt.SystemColor.window);
			jRadioButton1.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jTextArea1.setVisible(true);
					jTextArea.setVisible(false);
					jRadioButton.setSelected(false);
				}
			});
		}
		return jRadioButton1;
	}
	/**
	 * This method initializes jLabel3
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel3() {
		if(jLabel3 == null) {
			jLabel3 = new javax.swing.JLabel();
			jLabel3.setBounds(382, 339, 139, 21);
			jLabel3.setText(language[30]);
			jLabel3.setVisible(true);
		}
		return jLabel3;
	}
	
	public String[] getTempGuest() {
		String[] tst = new String[entries];
		tst[0] = jTextField4.getText();
		tst[1] = jTextField.getText();
		tst[2] = jTextField1.getText();
		tst[3] = jTextArea.getText();
		tst[4] = jTextField2.getText();
		tst[5] = jTextField3.getText();
		tst[6] = jTextArea1.getText();
		tst[7] = addDataWindow.getWife();
		tst[8] = addDataWindow.getChildren();
		tst[9] = addDataWindow.getPhone(false);
		tst[10] = addDataWindow.getMobile(false);
		tst[11] = addDataWindow.getFax(false);
		tst[12] = addDataWindow.getEmail(false);
		tst[13] = addDataWindow.getPhone(true);
		tst[14] = addDataWindow.getMobile(true);
		tst[15] = addDataWindow.getFax(true);
		tst[16] = addDataWindow.getEmail(true);
		tst[17] = addDataWindow.getRoom();
		tst[18] = addDataWindow.getSmoker();
		return tst;
	}
	
	public void deleteEntry(String[] guest) {

		g.deleteExisting(guest);
	
		clearFields();
		setDeleted(false);
	}
	
	public void setCurrentGuest() {
		currentGuest = getTempGuest();
	}
	
	
	/**
	 * This method initializes jLabel8
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel8() {
		if(jLabel8 == null) {
			jLabel8 = new javax.swing.JLabel();
			jLabel8.setBounds(380, 120, 103, 21);
			jLabel8.setText(language[26]);
		}
		return jLabel8;
	}
	/**
	 * This method initializes jTextField5
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField5() {
		if(jTextField5 == null) {
			jTextField5 = new javax.swing.JTextField();
			jTextField5.setBounds(482, 120, 269, 21);
			jTextField5.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					findEntry(jTextField5.getText());
					jTextField5.setText("");
				}
			});
		}
		return jTextField5;
	}
	/**
	 * This method initializes jButton8
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButton8() {
		if(jButton8 == null) {
			jButton8 = new javax.swing.JButton();
			jButton8.setBounds(753, 120, 19, 21);
			jButton8.setText("...");
			jButton8.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					findEntry(jTextField5.getText());
					jTextField5.setText("");
				}
			});
		}
		return jButton8;
	}
	
	public void findEntry(String guest) {
		boolean isGuest = false;
		String[] entry = new String[entries];
					
		isGuest = g.findGuest(guest);
					
		if (isGuest) {
					

			entry = g.getGuest(guest);

					
			setGuestStatus(true);
			setVisible(true);
			setEnabled(true);
			setGuest(entry);
			setCurrentGuest();
			setDeleted(true);
		}
	}
	
	public String getCompany() {
		return jTextField4.getText();
	}
	public String getName() {
		return jTextField.getText();
	}
	public String getFirstName() {
		return jTextField1.getText();
	}
	public String getAddress (boolean business) {
		if (business == false) {
			return jTextArea.getText();
		}
		else {
			return jTextArea1.getText();
		}
	}
	public String getBirthday() {
		return jTextField2.getText();
	}
	public String getCitizenship() {
		return jTextField3.getText();
	}
	
	public void exportDB() {
		ExportWindow ew = new ExportWindow();
	}
	
	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();
		ActionCommandsMW action = new ActionCommandsMW(thisWindow, command);		
	}
	
	
	
	public static void main(String[] args) {
		Language lang = new Language();
		language = lang.getLanguage();
		
		
		entries = g.getEntries();
		MainWindow window = new MainWindow();
		thisWindow = window;
		SearchWindow sw = new SearchWindow(thisWindow);
		searchWindow = sw;
		
		sw.setVisible(false);
		thisWindow.setGuestStatus(false);
		thisWindow.jLabel6.setVisible(false);
		thisWindow.jButton6.setVisible(false);
		thisWindow.setMenuBar(new MainMenu(thisWindow));
		

		
		
		
		AdditionalDataWindow adw = new AdditionalDataWindow(g, thisWindow, entries);
		addDataWindow = adw;
		adw.setVisible(false);
		
		
		
	}
	

	/**
	 * This method initializes jButton9
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButton9() {
		if(jButton9 == null) {
			jButton9 = new javax.swing.JButton();
			jButton9.setBounds(589, 143, 182, 19);
			jButton9.setText(language[49]);
			jButton9.setVisible(false);
			jButton9.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 10));
			jButton9.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					GuestList gl = new GuestList(thisWindow);
					gl.setVisible(true);
					gl.getGuestDB();
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
			jButton10.setBounds(37, 335, 198, 33);
			jButton10.setVisible(false);
			jButton10.setText("Fremdenbuch");
		}
		return jButton10;
	}
}  //  @jve:visual-info  decl-index=0 visual-constraint="22,10"
