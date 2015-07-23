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
import javax.swing.table.*;
import java.util.*;
import java.io.*;
import java.awt.Color;

public class ReservationManagement extends Frame implements Runnable {

	private javax.swing.JPanel jPanel = null;
	private javax.swing.JPanel jPanel1 = null;
	private javax.swing.JTextField jTextField = null;
	private javax.swing.JTextField jTextField1 = null;
	private javax.swing.JLabel jLabel = null;
	private javax.swing.JLabel jLabel1 = null;
	private javax.swing.JRadioButton jRadioButton = null;
	private javax.swing.JRadioButton jRadioButton1 = null;
	private javax.swing.JRadioButton jRadioButton2 = null;
	private javax.swing.JRadioButton jRadioButton3 = null;
	private javax.swing.JRadioButton jRadioButton4 = null;
	private javax.swing.JButton jButton = null;
	private javax.swing.JButton jButton1 = null;
	private javax.swing.JButton jButton2 = null;
	private javax.swing.JLabel jLabel2 = null;
	private javax.swing.JTextField jTextField3 = null;
	private javax.swing.JButton jButton3 = null;
	private javax.swing.JButton jButton4 = null;
	private MainWindow mw;
	private Reservation res;
	public static ReservationManagement thisWindow;
	private ReservationManagement rm;
	private int roomtype;
	private String room;
	String selectedRoom;
	String[] oldguest;
	String[] currentGuest;
	String[] language;
	boolean changedRoom = false;
	ArrayList restable;
	
	private javax.swing.JLabel jLabel3 = null;	
	private javax.swing.JScrollPane jScrollPane = null;
	private javax.swing.JTable jTable = null;
	private javax.swing.JComboBox jComboBox = null;
	private javax.swing.JComboBox jComboBox1 = null;
	int roomsInHotel;
	private int firstday;
	private int lastday;
	String[] settings;
	boolean mr;
	boolean correctDate = false;
	
	private javax.swing.JProgressBar jProgressBar = null;
	private javax.swing.JLabel jLabel4 = null;
	private javax.swing.JButton jButton5 = null;
	private javax.swing.JLabel jLabel5 = null;
	private javax.swing.JTextField jTextField2 = null;
	private javax.swing.JLabel jLabel6 = null;
	private javax.swing.JLabel jLabel7 = null;
	private javax.swing.JLabel jLabel8 = null;
	/**
	 * This is the default constructor
	 */
	public ReservationManagement(MainWindow mw) {
		Options options = new Options();
		settings = options.getSettings();
		Reservation r = new Reservation(thisWindow);
		this.res = r;
		thisWindow = this;
		this.mw = mw;
		mw.setVisible(false);
		Language lang = new Language();
		language = lang.getLanguage();
		int[] days = res.calcDate();
		this.firstday = days[0];
		this.lastday = days[1];
		
		initialize();
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */		
	public void setDays(int fd, int ld) {
		this.firstday = fd;
		this.lastday = ld;
	}
	
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */		
	
	
	
	private void initialize() {
		this.setLayout(null);
		this.add(getJPanel(), null);
		this.add(getJPanel1(), null);
		this.add(getJComboBox(), null);
		this.add(getJComboBox1(), null);
		this.add(getJProgressBar(), null);
		this.add(getJLabel4(), null);
		this.add(getJButton5(), null);
		this.setBounds(0, 0, 795, 570);
		this.setBounds(0, 0, 795, Integer.parseInt(settings[3])-30);
		this.setTitle("Reservation Management");
		this.setVisible(true);
		this.addWindowListener(new java.awt.event.WindowAdapter() { 
			public void windowClosing(java.awt.event.WindowEvent e) {    
				mw.setVisible(true);
				mw.setEnabled(true);
				dispose();
			}
		});
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
			jPanel.add(getJTextField(), null);
			jPanel.add(getJTextField1(), null);
			jPanel.add(getJLabel(), null);
			jPanel.add(getJLabel1(), null);
			jPanel.add(getJRadioButton(), null);
			jPanel.add(getJRadioButton1(), null);
			jPanel.add(getJRadioButton2(), null);
			jPanel.add(getJRadioButton3(), null);
			jPanel.add(getJRadioButton4(), null);
			jPanel.add(getJButton(), null);
			jPanel.add(getJButton1(), null);
			jPanel.add(getJButton2(), null);
			jPanel.add(getJLabel2(), null);
			jPanel.add(getJTextField3(), null);
			jPanel.add(getJButton3(), null);
			jPanel.add(getJButton4(), null);
			jPanel.add(getJLabel3(), null);
			jPanel.add(getJLabel5(), null);
			jPanel.add(getJTextField2(), null);
			jPanel.add(getJLabel6(), null);
			jPanel.add(getJLabel7(), null);
			jPanel.add(getJLabel8(), null);
			jPanel.setBounds(7, 24, 781, 203);
			jPanel.setBackground(java.awt.SystemColor.window);
		}
		return jPanel;
	}
	/**
	 * This method initializes jPanel1
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJPanel1() {
		if(jPanel1 == null) {
			jPanel1 = new javax.swing.JPanel();
			jPanel1.setLayout(new java.awt.BorderLayout());
			jPanel1.add(getJScrollPane(), java.awt.BorderLayout.CENTER);
			jPanel1.setBounds(7, 247, 782, Integer.parseInt(settings[3])-300);
			jPanel1.setBackground(java.awt.SystemColor.window);
			jPanel1.setPreferredSize(new java.awt.Dimension(34,32));
			jPanel1.setVisible(false);
		}
		return jPanel1;
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
			jTextField.setBounds(101, 33, 123, 19);
			jTextField.setText("");
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
			jTextField1.setNextFocusableComponent(getJButton1());
			jTextField1.setBounds(325, 33, 123, 19);
			jTextField1.setText("");
			jTextField1.addFocusListener(new java.awt.event.FocusAdapter() { 
				public void focusLost(java.awt.event.FocusEvent e) {    
					Reservation res = new Reservation(rm);

					int days = 0;
					int startday;
					int endday;
			
					startday = res.createCal(jTextField.getText());
					endday = res.createCal(jTextField1.getText());
					days = endday - startday;
					
					jLabel3.setText(Integer.toString(days));


				}
			});
		}
		return jTextField1;
	}
	/**
	 * This method initializes jLabel
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel() {
		if(jLabel == null) {
			jLabel = new javax.swing.JLabel();
			jLabel.setBounds(25, 33, 75, 19);
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
			jLabel1.setBounds(253, 33, 71, 19);
			jLabel1.setText(language[61]);
		}
		return jLabel1;
	}
	/**
	 * This method initializes jRadioButton
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton() {
		if(jRadioButton == null) {
			jRadioButton = new javax.swing.JRadioButton();
			jRadioButton.setBounds(25, 78, 138, 18);
			jRadioButton.setText(language[55]);
			jRadioButton.setBackground(java.awt.SystemColor.window);
			jRadioButton.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton1.setSelected(false);
					jRadioButton2.setSelected(false);
					jRadioButton3.setSelected(false);
					jRadioButton4.setSelected(false);
					
				}
			});
			jRadioButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Reservation res = new Reservation(thisWindow);
					if (jTextField.getText().equals("") || jTextField1.getText().equals("")  || (res.correctDate(jTextField.getText())==false) || (res.correctDate(jTextField1.getText())==false)) {
						// nada
					}
					else {
						RoomSelectWindow rsm = new RoomSelectWindow(thisWindow, 1, res.createCal(jTextField.getText()), res.createCal(jTextField1.getText()), restable);
						new Thread(rsm).start();
						rsm.setVisible(true);
					}
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
			jRadioButton1.setBounds(25, 102, 138, 18);
			jRadioButton1.setText(language[56]);
			jRadioButton1.setBackground(java.awt.SystemColor.window);
			jRadioButton1.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton.setSelected(false);
					jRadioButton2.setSelected(false);
					jRadioButton3.setSelected(false);
					jRadioButton4.setSelected(false);
					
				}
			});
			jRadioButton1.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {   
					Reservation res = new Reservation(thisWindow); 
					if (jTextField.getText().equals("") || jTextField1.getText().equals("")  || (res.correctDate(jTextField.getText())==false) || (res.correctDate(jTextField1.getText())==false)) {
						// nada
					}
					else {
						
						RoomSelectWindow rsm = new RoomSelectWindow(thisWindow, 2, res.createCal(jTextField.getText()), res.createCal(jTextField1.getText()), restable);
						new Thread(rsm).start();
						rsm.setVisible(true);
					}
				}
			});
		}
		return jRadioButton1;
	}
	/**
	 * This method initializes jRadioButton2
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton2() {
		if(jRadioButton2 == null) {
			jRadioButton2 = new javax.swing.JRadioButton();
			jRadioButton2.setBounds(25, 126, 138, 18);
			jRadioButton2.setText(language[57]);
			jRadioButton2.setBackground(java.awt.SystemColor.window);
			jRadioButton2.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton1.setSelected(false);
					jRadioButton.setSelected(false);
					jRadioButton3.setSelected(false);
					jRadioButton4.setSelected(false);
					
					
				}
			});
			jRadioButton2.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Reservation res = new Reservation(thisWindow);
					if (jTextField.getText().equals("") || jTextField1.getText().equals("") || (res.correctDate(jTextField.getText())==false) || (res.correctDate(jTextField1.getText())==false)) {
						// nada
					}
					else {    
						
						RoomSelectWindow rsm = new RoomSelectWindow(thisWindow, 3, res.createCal(jTextField.getText()), res.createCal(jTextField1.getText()), restable);
						new Thread(rsm).start();
						rsm.setVisible(true);
					}
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
			jRadioButton3.setBounds(25, 150, 138, 18);
			jRadioButton3.setText(language[58]);
			jRadioButton3.setBackground(java.awt.SystemColor.window);
			jRadioButton3.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton1.setSelected(false);
					jRadioButton2.setSelected(false);
					jRadioButton.setSelected(false);
					jRadioButton4.setSelected(false);
					
					
				}
			});
			jRadioButton3.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Reservation res = new Reservation(thisWindow);
					if (jTextField.getText().equals("") || jTextField1.getText().equals("") || (res.correctDate(jTextField.getText())==false) || (res.correctDate(jTextField1.getText())==false)) {
						// nada
					}
					else {
						
						RoomSelectWindow rsm = new RoomSelectWindow(thisWindow, 4, res.createCal(jTextField.getText()), res.createCal(jTextField1.getText()), restable);
						new Thread(rsm).start();
						rsm.setVisible(true);
					}
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
			jRadioButton4.setBounds(25, 174, 138, 18);
			jRadioButton4.setText(language[59]);
			jRadioButton4.setBackground(java.awt.SystemColor.window);
			jRadioButton4.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton1.setSelected(false);
					jRadioButton2.setSelected(false);
					jRadioButton3.setSelected(false);
					jRadioButton.setSelected(false);
					
					
				}
			});
			jRadioButton4.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Reservation res = new Reservation(thisWindow);
					if (jTextField.getText().equals("") || jTextField1.getText().equals("") || (res.correctDate(jTextField.getText())==false) || (res.correctDate(jTextField1.getText())==false)) {
						// nada
					}
					else {    
						
						RoomSelectWindow rsm = new RoomSelectWindow(thisWindow, 5, res.createCal(jTextField.getText()), res.createCal(jTextField1.getText()), restable);
						new Thread(rsm).start();
						rsm.setVisible(true);
					}
				}
			});
		}
		return jRadioButton4;
	}
	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButton() {
		if(jButton == null) {
			jButton = new javax.swing.JButton();
			jButton.setBounds(267, 97, 181, 19);
			jButton.setText(language[62]);
			jButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					ResGuestList rgl = new ResGuestList(thisWindow);
					rgl.setVisible(true);
					rgl.getGuestDB();
				}
			});
		}
		return jButton;
	}

	public void setOldGuest(String[] og) {
		oldguest = og;
	}

	private javax.swing.JButton getJButton1() {
		if(jButton1 == null) {
			jButton1 = new javax.swing.JButton();
			jButton1.setBounds(648, 174, 103, 18);
			jButton1.setText(language[21]);
			jButton1.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					if (jTextField.getText().equals("") || jTextField1.getText().equals("") || jTextField3.getText().equals("") || jTextField2.getText().equals("") || jLabel8.getText().equals("")) {
						// nada
					}
					else {
						Reservation res2 = new Reservation(thisWindow, jTextField.getText(), jTextField1.getText(), jTextField3.getText(), selectedRoom, jTextField2.getText());
						res2.start();
						clearFields();
						
					}
				}
			});
		}
		return jButton1;
	}
	
	public void setThreadRunning(String jLabelText) {
		jPanel1.setVisible(false);
		jLabel4.setText(jLabelText);
		jLabel4.setVisible(true);
		jProgressBar.setVisible(true);
		jProgressBar.setIndeterminate(true);
	}
	
	public void setThreadEnded() {
		jPanel1.setVisible(true);
		jLabel4.setVisible(false);
		jProgressBar.setIndeterminate(false);
		jProgressBar.setVisible(false);
	}
	
	public String getSelectedRoom() {
		return selectedRoom;
	}
	
	public void setSelectedRoom(String val) {
		this.selectedRoom = val;
		jLabel8.setText(val);
	}
	/**
	 * This method initializes jButton2
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButton2() {
		if(jButton2 == null) {
			jButton2 = new javax.swing.JButton();
			jButton2.setBounds(502, 174, 140, 18);
			jButton2.setText(language[34]);
			jButton2.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					clearFields();
					//setTable();
				}
			});
		}
		return jButton2;
	}
	/**
	 * This method initializes jLabel2
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel2() {
		if(jLabel2 == null) {
			jLabel2 = new javax.swing.JLabel();
			jLabel2.setBounds(490, 32, 92, 19);
			jLabel2.setText(language[64]);
		}
		return jLabel2;
	}
	/**
	 * This method initializes jTextField3
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField3() {
		if(jTextField3 == null) {
			jTextField3 = new javax.swing.JTextField();
			jTextField3.setBounds(452, 97, 299, 19);
			jTextField3.setEditable(false);
		}
		return jTextField3;
	}
	/**
	 * This method initializes jButton3
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButton3() {
		if(jButton3 == null) {
			jButton3 = new javax.swing.JButton();
			jButton3.setBounds(101, 52, 123, 16);
			jButton3.setText(language[63]);
			jButton3.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 10));
			jButton3.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
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
			jButton4.setBounds(325, 52, 123, 16);
			jButton4.setText(language[63]);
			jButton4.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 10));
			jButton4.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
				}
			});
		}
		return jButton4;
	}
	
	public void clearFields() {
		jTextField.setText("");
		jTextField1.setText("");
		jLabel3.setText("");
		jTextField3.setText("");
		jTextField2.setText("");
		jLabel8.setText("");
		
		jRadioButton.setSelected(false);
		jRadioButton1.setSelected(false);
		jRadioButton2.setSelected(false);
		jRadioButton3.setSelected(false);
		jRadioButton4.setSelected(false);
	}
	
	public void setGuest(String[] guest) {
		String tmpguest = (guest[0] + " - " + guest[1] + ", " + guest[2]);
		jTextField3.setText(tmpguest);
	}
	
	public void setGuest(String company, String name, String firstname) {
		String tmpguest = (company + " - " + name + ", " + firstname);
		jTextField3.setText(tmpguest);
	}

	
	/**
	 * This method initializes jLabel3
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel3() {
		if(jLabel3 == null) {
			jLabel3 = new javax.swing.JLabel();
			jLabel3.setBounds(585, 32, 63, 19);
			jLabel3.setText("");
		}
		return jLabel3;
	}

	/**
	 * This method initializes jTable
	 * 
	 * @return javax.swing.JTable
	 */
	public int getDaysOfMonth() {
		int daysInMonth = 0;
		Reservation res = new Reservation(thisWindow);
		
		if ((jComboBox.getSelectedIndex()==0) || (jComboBox.getSelectedIndex()==2) || (jComboBox.getSelectedIndex()==4) || (jComboBox.getSelectedIndex()==6) || (jComboBox.getSelectedIndex()==7) || (jComboBox.getSelectedIndex()==9) || (jComboBox.getSelectedIndex()==11)) {
			daysInMonth = 31;
		}
		else if (jComboBox.getSelectedIndex()==1) {
			if (res.isLeapYear(Integer.parseInt((String) jComboBox1.getSelectedItem()))) {
				daysInMonth = 29;
			}
			else {
				daysInMonth = 28;
			}
		}
		else {
			daysInMonth = 30;
		}

		
		
		return daysInMonth;
	}


	public void run() {	
		DefaultTableModel tm = (DefaultTableModel) jTable.getModel();
		ArrayList al = new ArrayList();
		String[] availableRooms;
		int roomcnt = 0;
		String[] columnid = new String[getDaysOfMonth()+1];
		jProgressBar.setIndeterminate(true);
		String[] currentRoom;
		
		try {
			FileInputStream fis = new FileInputStream("./db/restable.jh");
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			al = (ArrayList) ois.readObject();
			ois.close();

			availableRooms = (String[]) al.get(al.size()-1);
			this.restable = al;
			
			loop3:
			for (int i=0; i<availableRooms.length; ++i) {
				if (availableRooms[i]==null) {
					break loop3;
				}
				else {
					roomcnt++;
				}
			}
			tm.setColumnCount(getDaysOfMonth()+1);
			tm.setRowCount(roomcnt);
			
			// im tmp den Zeitbereich zw. lastday und firstday speichern
			for (int i=0; i<roomcnt; ++i) {
				int index = 1;
				currentRoom = (String[]) al.get(i);
				String[] tmp;				

				tmp = new String[lastday-firstday+2];
				
				for (int j=firstday; j<=lastday; ++j) {
					tmp[index] = currentRoom[j];
					++index;
				}
				for (int j = 1; j<tmp.length; ++j) {
					tm.setValueAt(tmp[j], i, j);
				}
			}
			// Zimmernummer in die erste Spalte
			for (int l=0; l<roomcnt; ++l) {
				tm.setValueAt(availableRooms[l], l, 0);
			}
		}
		catch (IOException io) {
			System.out.println(io);
		}
		catch (ClassNotFoundException cnf) {
			System.out.println(cnf);
		}
		for (int i=1; i<getDaysOfMonth()+1; ++i) {
			columnid[i] = Integer.toString(i);
		}		
		columnid[0] = "";
		
		jPanel1.setVisible(true);
		jProgressBar.setIndeterminate(false);
		jProgressBar.setVisible(false);
		jLabel4.setVisible(false);

		tm.setColumnIdentifiers(columnid);		
		jTable.setModel(tm);
		

	}
	
	public void updateTable(ArrayList al) {	
		DefaultTableModel tm = (DefaultTableModel) jTable.getModel();
		String[] availableRooms;
		int roomcnt = 0;
		String[] columnid = new String[getDaysOfMonth()+1];
		jProgressBar.setIndeterminate(true);
		String[] currentRoom;

		availableRooms = (String[]) al.get(al.size()-1);
			
		loop3:
		for (int i=0; i<availableRooms.length; ++i) {
			if (availableRooms[i]==null) {
				break loop3;
			}
			else {
				roomcnt++;
			}
		}
		tm.setColumnCount(getDaysOfMonth()+1);
		tm.setRowCount(roomcnt);
			
		// im tmp den Zeitbereich zw. lastday und firstday speichern
		for (int i=0; i<roomcnt; ++i) {
			int index = 1;
			currentRoom = (String[]) al.get(i);
			String[] tmp;				

			tmp = new String[lastday-firstday+2];
				
			for (int j=firstday; j<=lastday; ++j) {
				tmp[index] = currentRoom[j];
				++index;
			}
			for (int j = 1; j<tmp.length; ++j) {
				tm.setValueAt(tmp[j], i, j);
			}
		}
		// Zimmernummer in die erste Spalte
		for (int l=0; l<roomcnt; ++l) {
			tm.setValueAt(availableRooms[l], l, 0);
		}
	
		for (int i=1; i<getDaysOfMonth()+1; ++i) {
			columnid[i] = Integer.toString(i);
		}		
		columnid[0] = "";
		
		jPanel1.setVisible(true);
		jProgressBar.setIndeterminate(false);
		jProgressBar.setVisible(false);
		jLabel4.setVisible(false);

		tm.setColumnIdentifiers(columnid);		
		jTable.setModel(tm);
	}

	
	
	/**
	 * This method initializes jTable1
	 * 
	 * @return javax.swing.JTable
	 */
	private javax.swing.JTable getJTable() {
		if(jTable == null) {
			jTable = new javax.swing.JTable();
			//jTable.setPreferredSize(new java.awt.Dimension(0,400));
			jTable.setShowGrid(false);
			jTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
			jTable.setAutoscrolls(false);
			//jTable.setPreferredSize(new java.awt.Dimension(765,Integer.parseInt(settings[3])));
			jTable.setCellSelectionEnabled(false);
			jTable.setColumnSelectionAllowed(false);
			jTable.setRowSelectionAllowed(false);
			jTable.setVisible(true);
			jTable.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12));
			jTable.setBackground(java.awt.Color.white);
			jTable.setShowHorizontalLines(true);
			jTable.addMouseListener(new java.awt.event.MouseAdapter() { 
				public void mouseClicked(java.awt.event.MouseEvent e) {    
					try {
						int[] index = new int[2];
						index[0] = jTable.getSelectedRow();
						index[1] = jTable.getSelectedColumn();
					
						//System.out.println(jTable.getValueAt(index[0], index[1]));		
						ShowReservationWindow srw = new ShowReservationWindow(thisWindow, jTable.getValueAt(index[0], index[1]), restable);
						srw.setVisible(true);
					}
					catch (NullPointerException npe) {
						int[] reservation = new int[2];
						String sR;
						reservation[0] = jTable.getSelectedRow();
						reservation[1] = jTable.getSelectedColumn();
						String day, month;
						if (reservation[1]<10) {
							day = "0" + reservation[1];
						}
						else {
							day = Integer.toString(reservation[1]);
						}
						if (jComboBox.getSelectedIndex()<9) {
							month = "0" + (jComboBox.getSelectedIndex()+1);
						}
						else {
							month = Integer.toString(jComboBox.getSelectedIndex()+1);
						}
						
						if (jTextField.getText().equals("")) {
							jTextField.setText(day + "." + month + "." + jComboBox1.getSelectedItem());
							setSelectedRoom((String) jTable.getValueAt(reservation[0], 0));
						}
						else if (!jTextField.getText().equals("") && !jTextField1.getText().equals("")) {
							jTextField.setText(day + "." + month + "." + jComboBox1.getSelectedItem());
							jTextField1.setText("");
							jLabel3.setText("");
							setSelectedRoom((String) jTable.getValueAt(reservation[0], 0));
						}
						else {
							if (!(jTable.getValueAt(jTable.getSelectedRow(), 0).equals(jLabel8.getText()))) {
								// nada
							}
							else {
								jTextField1.setText(day + "." + month + "." + jComboBox1.getSelectedItem());
								jLabel3.setText(Integer.toString(res.createCal(jTextField1.getText()) - res.createCal(jTextField.getText())));
								setSelectedRoom((String) jTable.getValueAt(reservation[0], 0));
							}
							
						}
					}
				}
			});
		}
		return jTable;
	}
	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private javax.swing.JScrollPane getJScrollPane() {
		if(jScrollPane == null) {
			jScrollPane = new javax.swing.JScrollPane();
			jScrollPane.setViewportView(getJTable());
			//jScrollPane.setPreferredSize(new java.awt.Dimension(765,Integer.parseInt(settings[3])));
		}
		return jScrollPane;
	}
	/**
	 * This method initializes jComboBox
	 * 
	 * @return javax.swing.JComboBox
	 */
	private javax.swing.JComboBox getJComboBox() {
		if(jComboBox == null) {
			jComboBox = new javax.swing.JComboBox();
			for (int i=68; i<80; ++i) {	
				jComboBox.addItem(new String(language[i]));
			}
			Calendar cal = Calendar.getInstance();
			jComboBox.setSelectedIndex(cal.get(Calendar.MONTH));
			jComboBox.setBounds(371, 227, 137, 19);
			jComboBox.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					int[] days = res.calcDate(jComboBox.getSelectedIndex(), Integer.parseInt((String) jComboBox1.getSelectedItem()));
					firstday = days[0];
					lastday = days[1];
				}
			});
		}
		return jComboBox;
	}
	/**
	 * This method initializes jComboBox1
	 * 
	 * @return javax.swing.JComboBox
	 */
	private javax.swing.JComboBox getJComboBox1() {
		if(jComboBox1 == null) {
			jComboBox1 = new javax.swing.JComboBox();
			jComboBox1.setBounds(512, 227, 137, 19);
			Calendar cal = Calendar.getInstance();
			int year = cal.get(Calendar.YEAR);
			int year2 = year+8;
			
			for (int i=year-2; i<year2; ++i) {
				jComboBox1.addItem(Integer.toString(i));
			}
			
			jComboBox1.setSelectedItem(Integer.toString(year));
			jComboBox1.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					int[] days = res.calcDate(jComboBox.getSelectedIndex(), Integer.parseInt((String) jComboBox1.getSelectedItem()));
					firstday = days[0];
					lastday = days[1];
				}
			});
		}
		return jComboBox1;
	}
	/**
	 * This method initializes jProgressBar
	 * 
	 * @return javax.swing.JProgressBar
	 */
	private javax.swing.JProgressBar getJProgressBar() {
		if(jProgressBar == null) {
			jProgressBar = new javax.swing.JProgressBar();
			jProgressBar.setBounds(117, 227, 133, 19);
		}
		return jProgressBar;
	}
	/**
	 * This method initializes jLabel4
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel4() {
		if(jLabel4 == null) {
			jLabel4 = new javax.swing.JLabel();
			jLabel4.setBounds(32, 227, 86, 19);
			jLabel4.setText(language[65]);
		}
		return jLabel4;
	}
	/**
	 * This method initializes jButton5
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButton5() {
		if(jButton5 == null) {
			jButton5 = new javax.swing.JButton();
			jButton5.setBounds(655, 227, 103, 18);
			jButton5.setText(language[25]);
			jButton5.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					UpdateListThread ult = new UpdateListThread(thisWindow);
					ult.start();
				}
			});
		}
		return jButton5;
	}
	/**
	 * This method initializes jLabel5
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel5() {
		if(jLabel5 == null) {
			jLabel5 = new javax.swing.JLabel();
			jLabel5.setBounds(341, 120, 107, 19);
			jLabel5.setText(language[90]);
		}
		return jLabel5;
	}
	/**
	 * This method initializes jTextField2
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField2() {
		if(jTextField2 == null) {
			jTextField2 = new javax.swing.JTextField();
			jTextField2.setBounds(452, 120, 73, 19);
		}
		return jTextField2;
	}
	/**
	 * This method initializes jLabel6
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel6() {
		if(jLabel6 == null) {
			jLabel6 = new javax.swing.JLabel();
			jLabel6.setBounds(532, 120, 58, 19);
			jLabel6.setText(language[91]);
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
			jLabel7.setBounds(490, 53, 92, 19);
			jLabel7.setText(language[54]);
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
			jLabel8.setBounds(585, 53, 63, 19);
			jLabel8.setText("");
		}
		return jLabel8;
	}
}  //  @jve:visual-info  decl-index=0 visual-constraint="10,10"
