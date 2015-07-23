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
import java.io.*;
import java.util.*;

public class RoomSelectWindow extends Frame implements Runnable {

	private javax.swing.JPanel jPanel = null;
	private ReservationManagement rm;
	private int roomtype;
	private String room;
	private ArrayList allrooms = new ArrayList();
	private String[] settings;
	private int floors;
	private javax.swing.JRadioButton jRadioButton = null;
	private javax.swing.JRadioButton jRadioButton1 = null;
	private javax.swing.JRadioButton jRadioButton2 = null;
	private javax.swing.JRadioButton jRadioButton3 = null;
	private javax.swing.JRadioButton jRadioButton4 = null;
	private javax.swing.JRadioButton jRadioButton5 = null;
	private javax.swing.JRadioButton jRadioButton6 = null;
	private javax.swing.JRadioButton jRadioButton7 = null;
	Language lang = new Language();
	Reservation res;
	String[] language;
	int startday;
	int endday;
	String selectedRoom;
	String filename;

	private javax.swing.JRadioButton jRadioButton8 = null;
	private javax.swing.JRadioButton jRadioButton9 = null;
	private javax.swing.JRadioButton jRadioButton10 = null;
	private javax.swing.JRadioButton jRadioButton11 = null;
	private javax.swing.JRadioButton jRadioButton12 = null;
	private javax.swing.JRadioButton jRadioButton13 = null;
	private javax.swing.JRadioButton jRadioButton14 = null;
	private javax.swing.JRadioButton jRadioButton15 = null;
	private javax.swing.JRadioButton jRadioButton16 = null;
	private javax.swing.JRadioButton jRadioButton17 = null;
	private javax.swing.JRadioButton jRadioButton18 = null;
	private javax.swing.JRadioButton jRadioButton19 = null;
	private javax.swing.JRadioButton jRadioButton20 = null;
	private javax.swing.JRadioButton jRadioButton21 = null;
	private javax.swing.JRadioButton jRadioButton22 = null;
	private javax.swing.JRadioButton jRadioButton23 = null;
	private javax.swing.JRadioButton jRadioButton24 = null;
	private javax.swing.JRadioButton jRadioButton25 = null;
	private javax.swing.JRadioButton jRadioButton26 = null;
	private javax.swing.JRadioButton jRadioButton27 = null;
	private javax.swing.JRadioButton jRadioButton28 = null;
	private javax.swing.JTextField jTextField = null;
	private javax.swing.JLabel jLabel = null;
	private javax.swing.JButton jButton = null;
	private javax.swing.JButton jButton1 = null;
	ArrayList reservations = new ArrayList();
	String[] availableRooms = new String[168];

	private javax.swing.JProgressBar jProgressBar = null;
	/**
	 * This is the default constructor
	 */
	public RoomSelectWindow(ReservationManagement rm, int roomtype, int startday, int endday, ArrayList reservations) {
		this.rm = rm;
		this.startday = startday;
		this.endday = endday;
		this.roomtype = roomtype;
		this.allrooms = getRooms(roomtype);
		this.reservations = reservations;
		Options options = new Options();
		settings = options.getSettings();
		Language lang = new Language();
		language = lang.getLanguage();
		
		this.floors = Integer.parseInt(settings[2]);
		
		initialize();
	}
	
	/*private void loadFile() {
		try {
			FileInputStream fis = new FileInputStream("./db/restable.jh");
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			this.reservations = (ArrayList) ois.readObject();
			ois.close();
		}
		catch (ClassNotFoundException cnf) {
			System.out.println(cnf + " Reservation.makeReservation()");
		}
		catch (IOException io) {
			System.out.println(io + " Reservation.makeReservation()");
		}
	}*/

	
	private ArrayList getRooms(int roomtype) {
		ArrayList allrooms;
		String filename;
		
		if (roomtype==1) {
			filename = "./cfg/single.rms";
		}
		else if (roomtype==2) {
			filename = "./cfg/double.rms";
		}
		else if (roomtype==3) {
			filename = "./cfg/triple.rms";
		}
		else if (roomtype==4) {
			filename = "./cfg/quad.rms";
		}
		else {
			filename = "./cfg/apartment.rms";
		}
		
		
		try {
			FileInputStream fis = new FileInputStream(filename);
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			allrooms = (ArrayList) ois.readObject();
		}
		catch (IOException io1) {
			System.out.println(io1 + " in getRooms()");
			allrooms = null;
		}
		catch (ClassNotFoundException cnf) {
			System.out.println(cnf + " in getRooms()");
			allrooms = null;
		}

		return allrooms;
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setLayout(null);
		this.add(getJRadioButton(), null);
		this.add(getJRadioButton1(), null);
		this.add(getJRadioButton2(), null);
		this.add(getJRadioButton3(), null);
		this.add(getJRadioButton4(), null);
		this.add(getJRadioButton5(), null);
		this.add(getJRadioButton6(), null);
		this.add(getJRadioButton7(), null);
		this.add(getJPanel(), null);
		this.add(getJTextField(), null);
		this.add(getJLabel(), null);
		this.add(getJButton(), null);
		this.add(getJButton1(), null);
		this.add(getJProgressBar(), null);
		this.setSize(546, 374);
		this.setTitle(language[86]);
		this.addWindowListener(new java.awt.event.WindowAdapter() { 
			public void windowClosing(java.awt.event.WindowEvent e) {    
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
			jPanel.add(getJRadioButton8(), null);
			jPanel.add(getJRadioButton9(), null);
			jPanel.add(getJRadioButton10(), null);
			jPanel.add(getJRadioButton11(), null);
			jPanel.add(getJRadioButton12(), null);
			jPanel.add(getJRadioButton13(), null);
			jPanel.add(getJRadioButton14(), null);
			jPanel.add(getJRadioButton15(), null);
			jPanel.add(getJRadioButton16(), null);
			jPanel.add(getJRadioButton17(), null);
			jPanel.add(getJRadioButton18(), null);
			jPanel.add(getJRadioButton19(), null);
			jPanel.add(getJRadioButton20(), null);
			jPanel.add(getJRadioButton21(), null);
			jPanel.add(getJRadioButton22(), null);
			jPanel.add(getJRadioButton23(), null);
			jPanel.add(getJRadioButton24(), null);
			jPanel.add(getJRadioButton25(), null);
			jPanel.add(getJRadioButton26(), null);
			jPanel.add(getJRadioButton27(), null);
			jPanel.add(getJRadioButton28(), null);
			jPanel.setBounds(44, 137, 446, 196);
			jPanel.setVisible(true);
			jPanel.setBackground(java.awt.SystemColor.window);
		}
		return jPanel;
	}
	/**
	 * This method initializes jRadioButton
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton() {
		if(jRadioButton == null) {
			jRadioButton = new javax.swing.JRadioButton();
			int c = 1;
			if (c<=floors) {
				jRadioButton.setVisible(true);
			}
			else {
				jRadioButton.setVisible(false);
			}
			jRadioButton.setBounds(22, 37, 108, 18);
			jRadioButton.setBackground(java.awt.SystemColor.window);
			jRadioButton.setText("1. " + language[84]);
			jRadioButton.setEnabled(false);
			jRadioButton.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton1.setSelected(false);
					jRadioButton2.setSelected(false);
					jRadioButton3.setSelected(false);
					jRadioButton4.setSelected(false);
					jRadioButton5.setSelected(false);
					jRadioButton6.setSelected(false);
					jRadioButton7.setSelected(false);
				}
			});
			jRadioButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					jPanel.setVisible(true);
					clearPanel();
					checkRoomAvailability(roomtype, 1);
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
			int c = 3;
			if (c<=floors) {
				jRadioButton1.setVisible(true);
			}
			else {
				jRadioButton1.setVisible(false);
			}

			jRadioButton1.setBounds(152, 37, 108, 18);
			jRadioButton1.setBackground(java.awt.SystemColor.window);
			jRadioButton1.setText("3. " + language[84]);
			jRadioButton1.setEnabled(false);
			jRadioButton1.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton.setSelected(false);
					jRadioButton2.setSelected(false);
					jRadioButton3.setSelected(false);
					jRadioButton4.setSelected(false);
					jRadioButton5.setSelected(false);
					jRadioButton6.setSelected(false);
					jRadioButton7.setSelected(false);
				}
			});
			jRadioButton1.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					jPanel.setVisible(true);
					clearPanel();
					checkRoomAvailability(roomtype, 3);
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
			int c = 5;
			if (c<=floors) {
				jRadioButton2.setVisible(true);
			}
			else {
				jRadioButton2.setVisible(false);
			}

			jRadioButton2.setBounds(282, 37, 108, 18);
			jRadioButton2.setBackground(java.awt.SystemColor.window);
			jRadioButton2.setText("5. " + language[84]);
			jRadioButton2.setEnabled(false);
			jRadioButton2.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton1.setSelected(false);
					jRadioButton.setSelected(false);
					jRadioButton3.setSelected(false);
					jRadioButton4.setSelected(false);
					jRadioButton5.setSelected(false);
					jRadioButton6.setSelected(false);
					jRadioButton7.setSelected(false);
				}
			});
			jRadioButton2.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					jPanel.setVisible(true);
					clearPanel();
					checkRoomAvailability(roomtype, 5);
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
			int c = 7;
			if (c<=floors) {
				jRadioButton3.setVisible(true);
			}
			else {
				jRadioButton3.setVisible(false);
			}

			jRadioButton3.setBounds(412, 37, 108, 18);
			jRadioButton3.setBackground(java.awt.SystemColor.window);
			jRadioButton3.setText("7. " + language[84]);
			jRadioButton3.setEnabled(false);
			jRadioButton3.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton1.setSelected(false);
					jRadioButton2.setSelected(false);
					jRadioButton.setSelected(false);
					jRadioButton4.setSelected(false);
					jRadioButton5.setSelected(false);
					jRadioButton6.setSelected(false);
					jRadioButton7.setSelected(false);

				}
			});
			jRadioButton3.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					jPanel.setVisible(true);
					clearPanel();
					checkRoomAvailability(roomtype, 7);
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
			int c = 2;
			if (c<=floors) {
				jRadioButton4.setVisible(true);
			}
			else {
				jRadioButton4.setVisible(false);
			}

			jRadioButton4.setBounds(22, 60, 108, 18);
			jRadioButton4.setBackground(java.awt.SystemColor.window);
			jRadioButton4.setText("2. " + language[84]);
			jRadioButton4.setEnabled(false);
			jRadioButton4.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton1.setSelected(false);
					jRadioButton2.setSelected(false);
					jRadioButton3.setSelected(false);
					jRadioButton.setSelected(false);
					jRadioButton5.setSelected(false);
					jRadioButton6.setSelected(false);
					jRadioButton7.setSelected(false);


				}
			});
			jRadioButton4.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					jPanel.setVisible(true);
					clearPanel();
					checkRoomAvailability(roomtype, 2);
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
			int c = 4;
			if (c<=floors) {
				jRadioButton5.setVisible(true);
			}
			else {
				jRadioButton5.setVisible(false);
			}

			jRadioButton5.setBounds(152, 60, 108, 18);
			jRadioButton5.setBackground(java.awt.SystemColor.window);
			jRadioButton5.setText("4. " + language[84]);
			jRadioButton5.setEnabled(false);
			jRadioButton5.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton1.setSelected(false);
					jRadioButton2.setSelected(false);
					jRadioButton3.setSelected(false);
					jRadioButton4.setSelected(false);
					jRadioButton.setSelected(false);
					jRadioButton6.setSelected(false);
					jRadioButton7.setSelected(false);


				}
			});
			jRadioButton5.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					jPanel.setVisible(true);
					clearPanel();
					checkRoomAvailability(roomtype, 4);
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
			int c = 6;
			if (c<=floors) {
				jRadioButton6.setVisible(true);
			}
			else {
				jRadioButton6.setVisible(false);
			}

			jRadioButton6.setBounds(282, 60, 108, 18);
			jRadioButton6.setBackground(java.awt.SystemColor.window);
			jRadioButton6.setText("6. " + language[84]);
			jRadioButton6.setEnabled(false);
			jRadioButton6.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton1.setSelected(false);
					jRadioButton2.setSelected(false);
					jRadioButton3.setSelected(false);
					jRadioButton4.setSelected(false);
					jRadioButton5.setSelected(false);
					jRadioButton.setSelected(false);
					jRadioButton7.setSelected(false);


				}
			});
			jRadioButton6.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					jPanel.setVisible(true);
					clearPanel();
					checkRoomAvailability(roomtype, 6);
				}
			});
		}
		return jRadioButton6;
	}
	/**
	 * This method initializes jRadioButton7
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton7() {
		if(jRadioButton7 == null) {
			jRadioButton7 = new javax.swing.JRadioButton();
			int c = 8;
			if (c<=floors) {
				jRadioButton7.setVisible(true);
			}
			else {
				jRadioButton7.setVisible(false);
			}

			jRadioButton7.setBounds(412, 60, 108, 18);
			jRadioButton7.setBackground(java.awt.SystemColor.window);
			jRadioButton7.setText("8. " + language[84]);
			jRadioButton7.setEnabled(false);
			jRadioButton7.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton1.setSelected(false);
					jRadioButton2.setSelected(false);
					jRadioButton3.setSelected(false);
					jRadioButton4.setSelected(false);
					jRadioButton5.setSelected(false);
					jRadioButton6.setSelected(false);
					jRadioButton.setSelected(false);


				}
			});
			jRadioButton7.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					jPanel.setVisible(true);
					clearPanel();
					checkRoomAvailability(roomtype, 8);
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
	
	private String getRoomtType() {
		String type = "";
		
		if (roomtype==1) {
			type = "S";
		}
		else if (roomtype==2) {
			type = "D";
		}
		else if (roomtype==3) {
			type = "T";
		}
		else if (roomtype==4) {
			type = "Q";
		}
		else if (roomtype==5) {
			type="A";
		}
		
		return type;
	}
	
	private javax.swing.JRadioButton getJRadioButton8() {
		if(jRadioButton8 == null) {
			jRadioButton8 = new javax.swing.JRadioButton();
			jRadioButton8.setBounds(39, 9, 96, 17);
			jRadioButton8.setBackground(java.awt.SystemColor.window);
			jRadioButton8.setVisible(false);
			jRadioButton8.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton9.setSelected(false);
					jRadioButton10.setSelected(false);
					jRadioButton11.setSelected(false);
					jRadioButton12.setSelected(false);
					jRadioButton13.setSelected(false);
					jRadioButton14.setSelected(false);
					jRadioButton15.setSelected(false);
					jRadioButton16.setSelected(false);
					jRadioButton17.setSelected(false);
					jRadioButton18.setSelected(false);
					jRadioButton19.setSelected(false);
					jRadioButton20.setSelected(false);
					jRadioButton21.setSelected(false);
					jRadioButton22.setSelected(false);
					jRadioButton23.setSelected(false);
					jRadioButton24.setSelected(false);
					jRadioButton25.setSelected(false);
					jRadioButton26.setSelected(false);
					jRadioButton27.setSelected(false);
					jRadioButton28.setSelected(false);
				}
			});
			jRadioButton8.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					//filename = ("./cfg/" + getType() + jRadioButton8.getText() + ".jhr");
					selectedRoom = jRadioButton8.getText();
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
			jRadioButton9.setBounds(39, 35, 96, 17);
			jRadioButton9.setBackground(java.awt.SystemColor.window);
			jRadioButton9.setVisible(false);
			jRadioButton9.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton8.setSelected(false);
					jRadioButton10.setSelected(false);
					jRadioButton11.setSelected(false);
					jRadioButton12.setSelected(false);
					jRadioButton13.setSelected(false);
					jRadioButton14.setSelected(false);
					jRadioButton15.setSelected(false);
					jRadioButton16.setSelected(false);
					jRadioButton17.setSelected(false);
					jRadioButton18.setSelected(false);
					jRadioButton19.setSelected(false);
					jRadioButton20.setSelected(false);
					jRadioButton21.setSelected(false);
					jRadioButton22.setSelected(false);
					jRadioButton23.setSelected(false);
					jRadioButton24.setSelected(false);
					jRadioButton25.setSelected(false);
					jRadioButton26.setSelected(false);
					jRadioButton27.setSelected(false);
					jRadioButton28.setSelected(false);
				}
			});
			jRadioButton9.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					selectedRoom = jRadioButton9.getText();
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
			jRadioButton10.setBounds(39, 61, 96, 17);
			jRadioButton10.setBackground(java.awt.SystemColor.window);
			jRadioButton10.setVisible(false);
			jRadioButton10.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton9.setSelected(false);
					jRadioButton8.setSelected(false);
					jRadioButton11.setSelected(false);
					jRadioButton12.setSelected(false);
					jRadioButton13.setSelected(false);
					jRadioButton14.setSelected(false);
					jRadioButton15.setSelected(false);
					jRadioButton16.setSelected(false);
					jRadioButton17.setSelected(false);
					jRadioButton18.setSelected(false);
					jRadioButton19.setSelected(false);
					jRadioButton20.setSelected(false);
					jRadioButton21.setSelected(false);
					jRadioButton22.setSelected(false);
					jRadioButton23.setSelected(false);
					jRadioButton24.setSelected(false);
					jRadioButton25.setSelected(false);
					jRadioButton26.setSelected(false);
					jRadioButton27.setSelected(false);
					jRadioButton28.setSelected(false);
				}
			});
			jRadioButton10.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					//filename = ("./cfg/" + getType() + jRadioButton10.getText() + ".jhr");
					selectedRoom = jRadioButton10.getText();
				}
			});
		}
		return jRadioButton10;
	}
	
	public void clearPanel() {
		jRadioButton8.setVisible(false);
		jRadioButton9.setVisible(false);
		jRadioButton10.setVisible(false);
		jRadioButton11.setVisible(false);
		jRadioButton12.setVisible(false);
		jRadioButton13.setVisible(false);
		jRadioButton14.setVisible(false);
		jRadioButton15.setVisible(false);
		jRadioButton16.setVisible(false);
		jRadioButton17.setVisible(false);
		jRadioButton18.setVisible(false);
		jRadioButton19.setVisible(false);
		jRadioButton20.setVisible(false);
		jRadioButton21.setVisible(false);
		jRadioButton22.setVisible(false);
		jRadioButton23.setVisible(false);
		jRadioButton24.setVisible(false);
		jRadioButton25.setVisible(false);
		jRadioButton26.setVisible(false);
		jRadioButton27.setVisible(false);
		jRadioButton28.setVisible(false);
	}
	
	public void checkRoomAvailability(int roomtype, int floor) {
		clearPanel();
		ArrayList avR = new ArrayList();
		String filename;
		
		if (roomtype==1) {
			filename = "./cfg/single.rms"; 
		}
		else if (roomtype==2) {
			filename = "./cfg/double.rms";
		}
		else if (roomtype==3) {
			filename = "./cfg/triple.rms";
		}
		else if (roomtype==4) {
			filename = "./cfg/quad.rms";
		}
		else {
			filename = "./cfg/apartment.rms";
		}		
			try {
				
				ArrayList al = new ArrayList();
				String[] availableRooms;
				FileInputStream sngl = new FileInputStream(filename);
				ObjectInputStream single = new ObjectInputStream(sngl);
							
				al = (ArrayList) single.readObject();
				single.close();
				availableRooms = (String[]) al.get(floor-1);
				
				
				for (int i=0; i<availableRooms.length; ++i) {
					if (checkAvailability(startday, endday, availableRooms[i])) {
						avR.add(availableRooms[i]);
					}					
				}								
			}
			catch (ClassNotFoundException cnf) {
				System.out.println(cnf + ": " + roomtype + ". Floor");
			}
			catch (IOException io) {
				System.out.println(io + ": " + roomtype + ". Floor");
			}

		if (avR.size()>0) { 
		
			loop1:
			while (true) {
				int i=0;
				jRadioButton8.setVisible(true);
				jRadioButton8.setText((String) avR.get(i));
				++i;
				if (i==avR.size()) {
					break loop1;
				}
				jRadioButton9.setVisible(true);
				jRadioButton9.setText((String) avR.get(i));
				++i;
				if (i==avR.size()) {
					break loop1;
				}
				jRadioButton10.setVisible(true);
				jRadioButton10.setText((String) avR.get(i));
				++i;
				if (i==avR.size()) {
					break loop1;
				}
				jRadioButton11.setVisible(true);
				jRadioButton11.setText((String) avR.get(i));
				++i;
				if (i==avR.size()) {
					break loop1;
				}
				jRadioButton12.setVisible(true);
				jRadioButton12.setText((String) avR.get(i));
				++i;
				if (i==avR.size()) {
					break loop1;
				}
				jRadioButton13.setVisible(true);
				jRadioButton13.setText((String) avR.get(i));
				++i;
				if (i==avR.size()) {
					break loop1;
				}
				jRadioButton14.setVisible(true);
				jRadioButton14.setText((String) avR.get(i));
				++i;
				if (i==avR.size()) {
					break loop1;
				}
				jRadioButton15.setVisible(true);
				jRadioButton15.setText((String) avR.get(i));
				++i;
				if (i==avR.size()) {
					break loop1;
				}
				jRadioButton16.setVisible(true);
				jRadioButton16.setText((String) avR.get(i));
				++i;
				if (i==avR.size()) {
					break loop1;
				}
				jRadioButton17.setVisible(true);
				jRadioButton17.setText((String) avR.get(i));
				++i;
				if (i==avR.size()) {
					break loop1;
				}
				jRadioButton18.setVisible(true);
				jRadioButton18.setText((String) avR.get(i));
				++i;
				if (i==avR.size()) {
					break loop1;
				}
				jRadioButton19.setVisible(true);
				jRadioButton19.setText((String) avR.get(i));
				++i;
				if (i==avR.size()) {
					break loop1;
				}
				jRadioButton20.setVisible(true);
				jRadioButton20.setText((String) avR.get(i));
				++i;
				if (i==avR.size()) {
					break loop1;
				}
				jRadioButton21.setVisible(true);
				jRadioButton21.setText((String) avR.get(i));
				++i;
				if (i==avR.size()) {
					break loop1;
				}
				jRadioButton22.setVisible(true);
				jRadioButton22.setText((String) avR.get(i));
				++i;
				if (i==avR.size()) {
					break loop1;
				}
				jRadioButton23.setVisible(true);
				jRadioButton23.setText((String) avR.get(i));
				++i;
				if (i==avR.size()) {
					break loop1;
				}
				jRadioButton24.setVisible(true);
				jRadioButton24.setText((String) avR.get(i));
				++i;
				if (i==avR.size()) {
					break loop1;
				}
				jRadioButton25.setVisible(true);
				jRadioButton25.setText((String) avR.get(i));
				++i;
				if (i==avR.size()) {
					break loop1;
				}
				jRadioButton26.setVisible(true);
				jRadioButton26.setText((String) avR.get(i));
				++i;
				if (i==avR.size()) {
					break loop1;
				}
				jRadioButton27.setVisible(true);
				jRadioButton27.setText((String) avR.get(i));
				++i;
				if (i==avR.size()) {
					break loop1;
				}
				jRadioButton28.setVisible(true);
				jRadioButton28.setText((String) avR.get(i));
				++i;
				if (i==avR.size()) {
					break loop1;
				}
			}
		}
	}
	
	/**
	 * This method initializes jRadioButton11
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton11() {
		if(jRadioButton11 == null) {
			jRadioButton11 = new javax.swing.JRadioButton();
			jRadioButton11.setBounds(39, 87, 96, 17);
			jRadioButton11.setBackground(java.awt.SystemColor.window);
			jRadioButton11.setVisible(false);
			jRadioButton11.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton9.setSelected(false);
					jRadioButton10.setSelected(false);
					jRadioButton8.setSelected(false);
					jRadioButton12.setSelected(false);
					jRadioButton13.setSelected(false);
					jRadioButton14.setSelected(false);
					jRadioButton15.setSelected(false);
					jRadioButton16.setSelected(false);
					jRadioButton17.setSelected(false);
					jRadioButton18.setSelected(false);
					jRadioButton19.setSelected(false);
					jRadioButton20.setSelected(false);
					jRadioButton21.setSelected(false);
					jRadioButton22.setSelected(false);
					jRadioButton23.setSelected(false);
					jRadioButton24.setSelected(false);
					jRadioButton25.setSelected(false);
					jRadioButton26.setSelected(false);
					jRadioButton27.setSelected(false);
					jRadioButton28.setSelected(false);
				}
			});
			jRadioButton11.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					//filename = ("./cfg/" + getType() + jRadioButton11.getText() + ".jhr");
					selectedRoom = jRadioButton11.getText();
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
			jRadioButton12.setBounds(39, 113, 96, 17);
			jRadioButton12.setBackground(java.awt.SystemColor.window);
			jRadioButton12.setVisible(false);
			jRadioButton12.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton9.setSelected(false);
					jRadioButton10.setSelected(false);
					jRadioButton11.setSelected(false);
					jRadioButton8.setSelected(false);
					jRadioButton13.setSelected(false);
					jRadioButton14.setSelected(false);
					jRadioButton15.setSelected(false);
					jRadioButton16.setSelected(false);
					jRadioButton17.setSelected(false);
					jRadioButton18.setSelected(false);
					jRadioButton19.setSelected(false);
					jRadioButton20.setSelected(false);
					jRadioButton21.setSelected(false);
					jRadioButton22.setSelected(false);
					jRadioButton23.setSelected(false);
					jRadioButton24.setSelected(false);
					jRadioButton25.setSelected(false);
					jRadioButton26.setSelected(false);
					jRadioButton27.setSelected(false);
					jRadioButton28.setSelected(false);
				}
			});
			jRadioButton12.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					//filename = ("./cfg/" + getType() + jRadioButton12.getText() + ".jhr");
					selectedRoom = jRadioButton12.getText();
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
			jRadioButton13.setBounds(39, 139, 96, 17);
			jRadioButton13.setBackground(java.awt.SystemColor.window);
			jRadioButton13.setVisible(false);
			jRadioButton13.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton9.setSelected(false);
					jRadioButton10.setSelected(false);
					jRadioButton11.setSelected(false);
					jRadioButton12.setSelected(false);
					jRadioButton8.setSelected(false);
					jRadioButton14.setSelected(false);
					jRadioButton15.setSelected(false);
					jRadioButton16.setSelected(false);
					jRadioButton17.setSelected(false);
					jRadioButton18.setSelected(false);
					jRadioButton19.setSelected(false);
					jRadioButton20.setSelected(false);
					jRadioButton21.setSelected(false);
					jRadioButton22.setSelected(false);
					jRadioButton23.setSelected(false);
					jRadioButton24.setSelected(false);
					jRadioButton25.setSelected(false);
					jRadioButton26.setSelected(false);
					jRadioButton27.setSelected(false);
					jRadioButton28.setSelected(false);
				}
			});
			jRadioButton13.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					//filename = ("./cfg/" + getType() + jRadioButton13.getText() + ".jhr");
					selectedRoom = jRadioButton13.getText();
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
			jRadioButton14.setBounds(39, 165, 96, 17);
			jRadioButton14.setBackground(java.awt.SystemColor.window);
			jRadioButton14.setVisible(false);
			jRadioButton14.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton9.setSelected(false);
					jRadioButton10.setSelected(false);
					jRadioButton11.setSelected(false);
					jRadioButton12.setSelected(false);
					jRadioButton13.setSelected(false);
					jRadioButton8.setSelected(false);
					jRadioButton15.setSelected(false);
					jRadioButton16.setSelected(false);
					jRadioButton17.setSelected(false);
					jRadioButton18.setSelected(false);
					jRadioButton19.setSelected(false);
					jRadioButton20.setSelected(false);
					jRadioButton21.setSelected(false);
					jRadioButton22.setSelected(false);
					jRadioButton23.setSelected(false);
					jRadioButton24.setSelected(false);
					jRadioButton25.setSelected(false);
					jRadioButton26.setSelected(false);
					jRadioButton27.setSelected(false);
					jRadioButton28.setSelected(false);
				}
			});
			jRadioButton14.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					//filename = ("./cfg/" + getType() + jRadioButton14.getText() + ".jhr");
					selectedRoom = jRadioButton14.getText();
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
			jRadioButton15.setBounds(174, 9, 96, 17);
			jRadioButton15.setBackground(java.awt.SystemColor.window);
			jRadioButton15.setVisible(false);
			jRadioButton15.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton9.setSelected(false);
					jRadioButton10.setSelected(false);
					jRadioButton11.setSelected(false);
					jRadioButton12.setSelected(false);
					jRadioButton13.setSelected(false);
					jRadioButton14.setSelected(false);
					jRadioButton8.setSelected(false);
					jRadioButton16.setSelected(false);
					jRadioButton17.setSelected(false);
					jRadioButton18.setSelected(false);
					jRadioButton19.setSelected(false);
					jRadioButton20.setSelected(false);
					jRadioButton21.setSelected(false);
					jRadioButton22.setSelected(false);
					jRadioButton23.setSelected(false);
					jRadioButton24.setSelected(false);
					jRadioButton25.setSelected(false);
					jRadioButton26.setSelected(false);
					jRadioButton27.setSelected(false);
					jRadioButton28.setSelected(false);
				}
			});
			jRadioButton15.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					//filename = ("./cfg/" + getType() + jRadioButton15.getText() + ".jhr");
					selectedRoom = jRadioButton15.getText();
				}
			});
		}
		return jRadioButton15;
	}
	/**
	 * This method initializes jRadioButton16
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton16() {
		if(jRadioButton16 == null) {
			jRadioButton16 = new javax.swing.JRadioButton();
			jRadioButton16.setBounds(174, 35, 96, 17);
			jRadioButton16.setBackground(java.awt.SystemColor.window);
			jRadioButton16.setVisible(false);
			jRadioButton16.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton9.setSelected(false);
					jRadioButton10.setSelected(false);
					jRadioButton11.setSelected(false);
					jRadioButton12.setSelected(false);
					jRadioButton13.setSelected(false);
					jRadioButton14.setSelected(false);
					jRadioButton15.setSelected(false);
					jRadioButton8.setSelected(false);
					jRadioButton17.setSelected(false);
					jRadioButton18.setSelected(false);
					jRadioButton19.setSelected(false);
					jRadioButton20.setSelected(false);
					jRadioButton21.setSelected(false);
					jRadioButton22.setSelected(false);
					jRadioButton23.setSelected(false);
					jRadioButton24.setSelected(false);
					jRadioButton25.setSelected(false);
					jRadioButton26.setSelected(false);
					jRadioButton27.setSelected(false);
					jRadioButton28.setSelected(false);
				}
			});
			jRadioButton16.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					//filename = ("./cfg/" + getType() + jRadioButton16.getText() + ".jhr");
					selectedRoom = jRadioButton16.getText();
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
			jRadioButton17.setBounds(174, 61, 96, 17);
			jRadioButton17.setBackground(java.awt.SystemColor.window);
			jRadioButton17.setVisible(false);
			jRadioButton17.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton9.setSelected(false);
					jRadioButton10.setSelected(false);
					jRadioButton11.setSelected(false);
					jRadioButton12.setSelected(false);
					jRadioButton13.setSelected(false);
					jRadioButton14.setSelected(false);
					jRadioButton15.setSelected(false);
					jRadioButton16.setSelected(false);
					jRadioButton8.setSelected(false);
					jRadioButton18.setSelected(false);
					jRadioButton19.setSelected(false);
					jRadioButton20.setSelected(false);
					jRadioButton21.setSelected(false);
					jRadioButton22.setSelected(false);
					jRadioButton23.setSelected(false);
					jRadioButton24.setSelected(false);
					jRadioButton25.setSelected(false);
					jRadioButton26.setSelected(false);
					jRadioButton27.setSelected(false);
					jRadioButton28.setSelected(false);
				}
			});
			jRadioButton17.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					//filename = ("./cfg/" + getType() + jRadioButton17.getText() + ".jhr");
					selectedRoom = jRadioButton17.getText();
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
			jRadioButton18.setBounds(174, 87, 96, 17);
			jRadioButton18.setBackground(java.awt.SystemColor.window);
			jRadioButton18.setVisible(false);
			jRadioButton18.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton9.setSelected(false);
					jRadioButton10.setSelected(false);
					jRadioButton11.setSelected(false);
					jRadioButton12.setSelected(false);
					jRadioButton13.setSelected(false);
					jRadioButton14.setSelected(false);
					jRadioButton15.setSelected(false);
					jRadioButton16.setSelected(false);
					jRadioButton17.setSelected(false);
					jRadioButton8.setSelected(false);
					jRadioButton19.setSelected(false);
					jRadioButton20.setSelected(false);
					jRadioButton21.setSelected(false);
					jRadioButton22.setSelected(false);
					jRadioButton23.setSelected(false);
					jRadioButton24.setSelected(false);
					jRadioButton25.setSelected(false);
					jRadioButton26.setSelected(false);
					jRadioButton27.setSelected(false);
					jRadioButton28.setSelected(false);
				}
			});
			jRadioButton18.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					//filename = ("./cfg/" + getType() + jRadioButton18.getText() + ".jhr");
					selectedRoom = jRadioButton18.getText();
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
			jRadioButton19.setBounds(174, 113, 96, 17);
			jRadioButton19.setBackground(java.awt.SystemColor.window);
			jRadioButton19.setVisible(false);
			jRadioButton19.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton9.setSelected(false);
					jRadioButton10.setSelected(false);
					jRadioButton11.setSelected(false);
					jRadioButton12.setSelected(false);
					jRadioButton13.setSelected(false);
					jRadioButton14.setSelected(false);
					jRadioButton15.setSelected(false);
					jRadioButton16.setSelected(false);
					jRadioButton17.setSelected(false);
					jRadioButton18.setSelected(false);
					jRadioButton8.setSelected(false);
					jRadioButton20.setSelected(false);
					jRadioButton21.setSelected(false);
					jRadioButton22.setSelected(false);
					jRadioButton23.setSelected(false);
					jRadioButton24.setSelected(false);
					jRadioButton25.setSelected(false);
					jRadioButton26.setSelected(false);
					jRadioButton27.setSelected(false);
					jRadioButton28.setSelected(false);
				}
			});
			jRadioButton19.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					//filename = ("./cfg/" + getType() + jRadioButton19.getText() + ".jhr");
					selectedRoom = jRadioButton19.getText();
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
			jRadioButton20.setBounds(174, 139, 96, 17);
			jRadioButton20.setBackground(java.awt.SystemColor.window);
			jRadioButton20.setVisible(false);
			jRadioButton20.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton9.setSelected(false);
					jRadioButton10.setSelected(false);
					jRadioButton11.setSelected(false);
					jRadioButton12.setSelected(false);
					jRadioButton13.setSelected(false);
					jRadioButton14.setSelected(false);
					jRadioButton15.setSelected(false);
					jRadioButton16.setSelected(false);
					jRadioButton17.setSelected(false);
					jRadioButton18.setSelected(false);
					jRadioButton19.setSelected(false);
					jRadioButton8.setSelected(false);
					jRadioButton21.setSelected(false);
					jRadioButton22.setSelected(false);
					jRadioButton23.setSelected(false);
					jRadioButton24.setSelected(false);
					jRadioButton25.setSelected(false);
					jRadioButton26.setSelected(false);
					jRadioButton27.setSelected(false);
					jRadioButton28.setSelected(false);
				}
			});
			jRadioButton20.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					//filename = ("./cfg/" + getType() + jRadioButton20.getText() + ".jhr");
					selectedRoom = jRadioButton20.getText();
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
			jRadioButton21.setBounds(174, 165, 96, 17);
			jRadioButton21.setBackground(java.awt.SystemColor.window);
			jRadioButton21.setVisible(false);
			jRadioButton21.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton9.setSelected(false);
					jRadioButton10.setSelected(false);
					jRadioButton11.setSelected(false);
					jRadioButton12.setSelected(false);
					jRadioButton13.setSelected(false);
					jRadioButton14.setSelected(false);
					jRadioButton15.setSelected(false);
					jRadioButton16.setSelected(false);
					jRadioButton17.setSelected(false);
					jRadioButton18.setSelected(false);
					jRadioButton19.setSelected(false);
					jRadioButton20.setSelected(false);
					jRadioButton8.setSelected(false);
					jRadioButton22.setSelected(false);
					jRadioButton23.setSelected(false);
					jRadioButton24.setSelected(false);
					jRadioButton25.setSelected(false);
					jRadioButton26.setSelected(false);
					jRadioButton27.setSelected(false);
					jRadioButton28.setSelected(false);
				}
			});
			jRadioButton21.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					//filename = ("./cfg/" + getType() + jRadioButton21.getText() + ".jhr");
					selectedRoom = jRadioButton21.getText();
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
			jRadioButton22.setBounds(309, 9, 96, 17);
			jRadioButton22.setBackground(java.awt.SystemColor.window);
			jRadioButton22.setVisible(false);
			jRadioButton22.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton9.setSelected(false);
					jRadioButton10.setSelected(false);
					jRadioButton11.setSelected(false);
					jRadioButton12.setSelected(false);
					jRadioButton13.setSelected(false);
					jRadioButton14.setSelected(false);
					jRadioButton15.setSelected(false);
					jRadioButton16.setSelected(false);
					jRadioButton17.setSelected(false);
					jRadioButton18.setSelected(false);
					jRadioButton19.setSelected(false);
					jRadioButton20.setSelected(false);
					jRadioButton21.setSelected(false);
					jRadioButton8.setSelected(false);
					jRadioButton23.setSelected(false);
					jRadioButton24.setSelected(false);
					jRadioButton25.setSelected(false);
					jRadioButton26.setSelected(false);
					jRadioButton27.setSelected(false);
					jRadioButton28.setSelected(false);
				}
			});
			jRadioButton22.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					//filename = ("./cfg/" + getType() + jRadioButton22.getText() + ".jhr");
					selectedRoom = jRadioButton22.getText();
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
			jRadioButton23.setBounds(309, 35, 96, 17);
			jRadioButton23.setBackground(java.awt.SystemColor.window);
			jRadioButton23.setVisible(false);
			jRadioButton23.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton9.setSelected(false);
					jRadioButton10.setSelected(false);
					jRadioButton11.setSelected(false);
					jRadioButton12.setSelected(false);
					jRadioButton13.setSelected(false);
					jRadioButton14.setSelected(false);
					jRadioButton15.setSelected(false);
					jRadioButton16.setSelected(false);
					jRadioButton17.setSelected(false);
					jRadioButton18.setSelected(false);
					jRadioButton19.setSelected(false);
					jRadioButton20.setSelected(false);
					jRadioButton21.setSelected(false);
					jRadioButton22.setSelected(false);
					jRadioButton8.setSelected(false);
					jRadioButton24.setSelected(false);
					jRadioButton25.setSelected(false);
					jRadioButton26.setSelected(false);
					jRadioButton27.setSelected(false);
					jRadioButton28.setSelected(false);
				}
			});
			jRadioButton23.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					//filename = ("./cfg/" + getType() + jRadioButton23.getText() + ".jhr");
					selectedRoom = jRadioButton23.getText();
				}
			});
		}
		return jRadioButton23;
	}
	/**
	 * This method initializes jRadioButton24
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton24() {
		if(jRadioButton24 == null) {
			jRadioButton24 = new javax.swing.JRadioButton();
			jRadioButton24.setBounds(309, 61, 96, 17);
			jRadioButton24.setBackground(java.awt.SystemColor.window);
			jRadioButton24.setVisible(false);
			jRadioButton24.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton9.setSelected(false);
					jRadioButton10.setSelected(false);
					jRadioButton11.setSelected(false);
					jRadioButton12.setSelected(false);
					jRadioButton13.setSelected(false);
					jRadioButton14.setSelected(false);
					jRadioButton15.setSelected(false);
					jRadioButton16.setSelected(false);
					jRadioButton17.setSelected(false);
					jRadioButton18.setSelected(false);
					jRadioButton19.setSelected(false);
					jRadioButton20.setSelected(false);
					jRadioButton21.setSelected(false);
					jRadioButton22.setSelected(false);
					jRadioButton23.setSelected(false);
					jRadioButton8.setSelected(false);
					jRadioButton25.setSelected(false);
					jRadioButton26.setSelected(false);
					jRadioButton27.setSelected(false);
					jRadioButton28.setSelected(false);
				}
			});
			jRadioButton24.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					//filename = ("./cfg/" + getType() + jRadioButton24.getText() + ".jhr");
					selectedRoom = jRadioButton24.getText();
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
			jRadioButton25.setBounds(309, 87, 96, 17);
			jRadioButton25.setBackground(java.awt.SystemColor.window);
			jRadioButton25.setVisible(false);
			jRadioButton25.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton9.setSelected(false);
					jRadioButton10.setSelected(false);
					jRadioButton11.setSelected(false);
					jRadioButton12.setSelected(false);
					jRadioButton13.setSelected(false);
					jRadioButton14.setSelected(false);
					jRadioButton15.setSelected(false);
					jRadioButton16.setSelected(false);
					jRadioButton17.setSelected(false);
					jRadioButton18.setSelected(false);
					jRadioButton19.setSelected(false);
					jRadioButton20.setSelected(false);
					jRadioButton21.setSelected(false);
					jRadioButton22.setSelected(false);
					jRadioButton23.setSelected(false);
					jRadioButton24.setSelected(false);
					jRadioButton8.setSelected(false);
					jRadioButton26.setSelected(false);
					jRadioButton27.setSelected(false);
					jRadioButton28.setSelected(false);
				}
			});
			jRadioButton25.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					//filename = ("./cfg/" + getType() + jRadioButton25.getText() + ".jhr");
					selectedRoom = jRadioButton25.getText();
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
			jRadioButton26.setBounds(309, 113, 96, 17);
			jRadioButton26.setBackground(java.awt.SystemColor.window);
			jRadioButton26.setVisible(false);
			jRadioButton26.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton9.setSelected(false);
					jRadioButton10.setSelected(false);
					jRadioButton11.setSelected(false);
					jRadioButton12.setSelected(false);
					jRadioButton13.setSelected(false);
					jRadioButton14.setSelected(false);
					jRadioButton15.setSelected(false);
					jRadioButton16.setSelected(false);
					jRadioButton17.setSelected(false);
					jRadioButton18.setSelected(false);
					jRadioButton19.setSelected(false);
					jRadioButton20.setSelected(false);
					jRadioButton21.setSelected(false);
					jRadioButton22.setSelected(false);
					jRadioButton23.setSelected(false);
					jRadioButton24.setSelected(false);
					jRadioButton25.setSelected(false);
					jRadioButton8.setSelected(false);
					jRadioButton27.setSelected(false);
					jRadioButton28.setSelected(false);
				}
			});
			jRadioButton26.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					//filename = ("./cfg/" + getType() + jRadioButton26.getText() + ".jhr");
					selectedRoom = jRadioButton26.getText();
				}
			});
		}
		return jRadioButton26;
	}
	/**
	 * This method initializes jRadioButton27
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton27() {
		if(jRadioButton27 == null) {
			jRadioButton27 = new javax.swing.JRadioButton();
			jRadioButton27.setBounds(309, 139, 96, 17);
			jRadioButton27.setBackground(java.awt.SystemColor.window);
			jRadioButton27.setVisible(false);
			jRadioButton27.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton9.setSelected(false);
					jRadioButton10.setSelected(false);
					jRadioButton11.setSelected(false);
					jRadioButton12.setSelected(false);
					jRadioButton13.setSelected(false);
					jRadioButton14.setSelected(false);
					jRadioButton15.setSelected(false);
					jRadioButton16.setSelected(false);
					jRadioButton17.setSelected(false);
					jRadioButton18.setSelected(false);
					jRadioButton19.setSelected(false);
					jRadioButton20.setSelected(false);
					jRadioButton21.setSelected(false);
					jRadioButton22.setSelected(false);
					jRadioButton23.setSelected(false);
					jRadioButton24.setSelected(false);
					jRadioButton25.setSelected(false);
					jRadioButton26.setSelected(false);
					jRadioButton8.setSelected(false);
					jRadioButton28.setSelected(false);
				}
			});
			jRadioButton27.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					//filename = ("./cfg/" + getType() + jRadioButton27.getText() + ".jhr");
					selectedRoom = jRadioButton27.getText();
				}
			});
		}
		return jRadioButton27;
	}
	/**
	 * This method initializes jRadioButton28
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton28() {
		if(jRadioButton28 == null) {
			jRadioButton28 = new javax.swing.JRadioButton();
			jRadioButton28.setBounds(309, 165, 96, 17);
			jRadioButton28.setBackground(java.awt.SystemColor.window);
			jRadioButton28.setVisible(false);
			jRadioButton28.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					jRadioButton9.setSelected(false);
					jRadioButton10.setSelected(false);
					jRadioButton11.setSelected(false);
					jRadioButton12.setSelected(false);
					jRadioButton13.setSelected(false);
					jRadioButton14.setSelected(false);
					jRadioButton15.setSelected(false);
					jRadioButton16.setSelected(false);
					jRadioButton17.setSelected(false);
					jRadioButton18.setSelected(false);
					jRadioButton19.setSelected(false);
					jRadioButton20.setSelected(false);
					jRadioButton21.setSelected(false);
					jRadioButton22.setSelected(false);
					jRadioButton23.setSelected(false);
					jRadioButton24.setSelected(false);
					jRadioButton25.setSelected(false);
					jRadioButton26.setSelected(false);
					jRadioButton27.setSelected(false);
					jRadioButton8.setSelected(false);
				}
			});
			jRadioButton28.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					//filename = ("./cfg/" + getType() + jRadioButton28.getText() + ".jhr");
					selectedRoom = jRadioButton28.getText();
				}
			});
		}
		return jRadioButton28;
	}
	/**
	 * This method initializes jTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField() {
		if(jTextField == null) {
			jTextField = new javax.swing.JTextField();
			jTextField.setBounds(7, 81, 531, 2);
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
			jLabel.setBounds(7, 92, 532, 21);
			jLabel.setText(language[85]);
			jLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		}
		return jLabel;
	}
	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButton() {
		if(jButton == null) {
			jButton = new javax.swing.JButton();
			jButton.setText(language[21]);
			jButton.setBounds(416, 337, 110, 23);
			jButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					rm.setSelectedRoom(selectedRoom);
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
			jButton1.setText(language[22]);
			jButton1.setBounds(19, 337, 110, 23);
			jButton1.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					dispose();
				}
			});
		}
		return jButton1;
	}
	
	public boolean checkAvailability(int arrival, int departure, String room) {
		boolean available = true;
		String[] tmp;
		int index=0;
					
		availableRooms = (String[]) reservations.get(reservations.size()-1);
			
		long start = System.currentTimeMillis();
		loop1:
		for (int i=0; i<availableRooms.length; ++i) {
			if (availableRooms[i].equals(room)) {
				index = i;
				break loop1;
			}
		}		
			
		tmp = (String[]) reservations.get(index);
		loop1:
		for (int i=arrival; i<departure; ++i) {
			if (tmp[i]!=null) {
				available = false;
				break loop1;
			}
		}
				
	return available;
	}
	
	public void run() {
		jProgressBar.setVisible(true);
		jLabel.setText(language[65]);
		jProgressBar.setIndeterminate(true);
		jProgressBar.setIndeterminate(false);
		jProgressBar.setVisible(false);
		jRadioButton.setEnabled(true);
		jRadioButton1.setEnabled(true);
		jRadioButton2.setEnabled(true);
		jRadioButton3.setEnabled(true);
		jRadioButton4.setEnabled(true);
		jRadioButton5.setEnabled(true);
		jRadioButton6.setEnabled(true);
		jRadioButton7.setEnabled(true);
		jLabel.setText(language[85]);
	}

	/**
	 * This method initializes jProgressBar
	 * 
	 * @return javax.swing.JProgressBar
	 */
	private javax.swing.JProgressBar getJProgressBar() {
		if(jProgressBar == null) {
			jProgressBar = new javax.swing.JProgressBar();
			jProgressBar.setBounds(140, 114, 261, 21);
		}
		return jProgressBar;
	}
}  //  @jve:visual-info  decl-index=0 visual-constraint="10,10"
