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

public class InitializeLists extends Thread {

	private CheckinWindow cw;
	private DefaultListModel dlm_checkin, dlm_checkout;
	private String[] language;
	private String company, name, firstname, arrival, departure, room, checkedin, price;
	private String lcompany, lname, lfirstname, larrival, ldeparture, lroom, lcheckedin, lprice;
	private ArrayList checkins = new ArrayList();
	private ArrayList checkouts = new ArrayList();
	private boolean other = false;
	private String otherdate;
	private ArrayList al;

	public InitializeLists(CheckinWindow cw, DefaultListModel dlm_checkin, DefaultListModel dlm_checkout) {
		this.cw = cw;
		this.dlm_checkin = dlm_checkin;
		this.dlm_checkout = dlm_checkout;
		
		Language lang = new Language();
		language = lang.getLanguage();
	}
	
	public InitializeLists(CheckinWindow cw, DefaultListModel dlm_checkin, DefaultListModel dlm_checkout, String date) {
		this.cw = cw;
		this.dlm_checkin = dlm_checkin;
		this.dlm_checkout = dlm_checkout;
		this.other = true;
		this.otherdate = date;
		
		Language lang = new Language();
		language = lang.getLanguage();
	}
	
	public InitializeLists() {
	}
	
	public String[] getCheckin(int index) {
		return (String[]) checkins.get(index);
	}
	
	public String[] getCheckout(int index) {
		return (String[]) checkouts.get(index);
	}
	
	
	public void run() {
		try {
			
			dlm_checkin.clear();
			dlm_checkout.clear();
			cw.setThreadRunning(language[65]);
			
			ArrayList reservations = new ArrayList();
			String[] availableRooms;
			String[] currentRoom;
			Calendar cal = Calendar.getInstance();
			String day, month, year;
			int tmpday, tmpmonth;
			tmpday =cal.get(Calendar.DATE);
			tmpmonth = cal.get(Calendar.MONTH)+1;
			int index = 0;
			int checkoutindex = 0;
			
			
			
			if (tmpday<10) {
				day = "0" + tmpday;
			}
			else {
				day = Integer.toString(tmpday);
			}
			if (tmpmonth<10) {
				month = "0" + tmpmonth;
			}
			else {
				month = Integer.toString(tmpmonth);
			}
			
			year = Integer.toString(cal.get(Calendar.YEAR));
			Reservation res = new Reservation(cw);
			int date;
			if (other) {
				date = res.createCal(otherdate);
			}
			else {
				date = res.createCal((day + "." + month + "." + year));
			}
			
			
			
			
			
			FileInputStream fis = new FileInputStream("./db/restable.jh");
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			reservations = (ArrayList) ois.readObject();
			this.al = reservations;
			ois.close();
			
			availableRooms = (String[]) reservations.get(reservations.size()-1);
			int cnt = 0;
			
			for (int i=0; i<availableRooms.length; ++i) {
				if (availableRooms[i]!=null) {
					cnt++;
				}
			}
			
			
			for (int i=0; i<cnt; ++i) {
				currentRoom = (String[]) reservations.get(i);
				String guest = currentRoom[date];
				String leavingguest = currentRoom[date-1];
				String[] gst = new String[8];
				String[] lgst = new String[8];
				
				if (guest!=null) {
					company = guest.substring(0, guest.indexOf('-')-1);
					name = guest.substring(guest.indexOf('-')+2, guest.indexOf(','));
					firstname = guest.substring(guest.indexOf(',')+2, guest.indexOf(':'));
					arrival = guest.substring(guest.indexOf(':')+2, guest.indexOf(';'));
					departure = guest.substring(guest.indexOf(';')+2, guest.lastIndexOf(';'));
					room = guest.substring(guest.lastIndexOf(';')+2, guest.lastIndexOf(':'));
					checkedin = guest.substring(guest.lastIndexOf(':') + 2, guest.indexOf('#'));
					price = guest.substring(guest.indexOf('#')+2);
										
					gst[0] = company;
					gst[1] = name;
					gst[2] = firstname;
					gst[3] = arrival;
					gst[4] = departure;
					gst[5] = room;
					gst[6] = checkedin;
					gst[7] = price;
					

	
					if (res.createCal(gst[3])==date && gst[6].equals("false")) {
						dlm_checkin.add(index, (gst[5] + ": " + gst[0] + " - " + gst[1] + ", " + gst[2]));
						checkins.add(gst);
						index++;
					}
				}
				
				if (leavingguest!=null) {
					lcompany = leavingguest.substring(0, leavingguest.indexOf('-')-1);
					lname = leavingguest.substring(leavingguest.indexOf('-')+2, leavingguest.indexOf(','));
					lfirstname = leavingguest.substring(leavingguest.indexOf(',')+2, leavingguest.indexOf(':'));
					larrival = leavingguest.substring(leavingguest.indexOf(':')+2, leavingguest.indexOf(';'));
					ldeparture = leavingguest.substring(leavingguest.indexOf(';')+2, leavingguest.lastIndexOf(';'));
					lroom = leavingguest.substring(leavingguest.lastIndexOf(';')+2, leavingguest.lastIndexOf(':'));
					lcheckedin = leavingguest.substring(leavingguest.lastIndexOf(':') + 2, leavingguest.indexOf('#'));
					lprice = leavingguest.substring(leavingguest.indexOf('#')+2);

					lgst[0] = lcompany;
					lgst[1] = lname;
					lgst[2] = lfirstname;
					lgst[3] = larrival;
					lgst[4] = ldeparture;
					lgst[5] = lroom;
					lgst[6] = lcheckedin;
					lgst[7] = lprice;

					if (res.createCal(lgst[4])==(date+1)) {
						dlm_checkout.add(checkoutindex, (lgst[5] + ": " + lgst[0] + " - " + lgst[1] + ", " + lgst[2]));
						checkouts.add(lgst);
						checkoutindex++;
					}
				}
			}	
			
			cw.setInitialized();		
		}
		catch (IOException io) {
			System.out.println(io);
		}
		catch (ClassNotFoundException cnf) {
			System.out.println(cnf);
		}
	}
	
	public void setCheckinList(ArrayList al) {
		this.al = al;
	}
	
	public void updateLists(ArrayList altmp) {
		
			dlm_checkin.clear();
			dlm_checkout.clear();
			cw.setThreadRunning(language[65]);
			ArrayList reservations = altmp;
			String[] availableRooms;
			String[] currentRoom;
			Calendar cal = Calendar.getInstance();
			String day, month, year;
			int tmpday, tmpmonth;
			tmpday =cal.get(Calendar.DATE);
			tmpmonth = cal.get(Calendar.MONTH)+1;
			int index = 0;
			int checkoutindex = 0;
			
			
			
			if (tmpday<10) {
				day = "0" + tmpday;
			}
			else {
				day = Integer.toString(tmpday);
			}
			if (tmpmonth<10) {
				month = "0" + tmpmonth;
			}
			else {
				month = Integer.toString(tmpmonth);
			}
			
			year = Integer.toString(cal.get(Calendar.YEAR));
			Reservation res = new Reservation(cw);
			int date;
			if (other) {
				date = res.createCal(otherdate);
			}
			else {
				date = res.createCal((day + "." + month + "." + year));
			}
			
			availableRooms = (String[]) reservations.get(reservations.size()-1);
			int cnt = 0;
			
			for (int i=0; i<availableRooms.length; ++i) {
				if (availableRooms[i]!=null) {
					cnt++;
				}
			}
			
			
			for (int i=0; i<cnt; ++i) {
				currentRoom = (String[]) reservations.get(i);
				String guest = currentRoom[date];
				String leavingguest = currentRoom[date-1];
				String[] gst = new String[8];
				String[] lgst = new String[8];
				
				if (guest!=null) {
					company = guest.substring(0, guest.indexOf('-')-1);
					name = guest.substring(guest.indexOf('-')+2, guest.indexOf(','));
					firstname = guest.substring(guest.indexOf(',')+2, guest.indexOf(':'));
					arrival = guest.substring(guest.indexOf(':')+2, guest.indexOf(';'));
					departure = guest.substring(guest.indexOf(';')+2, guest.lastIndexOf(';'));
					room = guest.substring(guest.lastIndexOf(';')+2, guest.lastIndexOf(':'));
					checkedin = guest.substring(guest.lastIndexOf(':') + 2, guest.indexOf('#'));
					price = guest.substring(guest.indexOf('#')+2);
										
					gst[0] = company;
					gst[1] = name;
					gst[2] = firstname;
					gst[3] = arrival;
					gst[4] = departure;
					gst[5] = room;
					gst[6] = checkedin;
					gst[7] = price;
					

	
					if (res.createCal(gst[3])==date && gst[6].equals("false")) {
						dlm_checkin.add(index, (gst[5] + ": " + gst[0] + " - " + gst[1] + ", " + gst[2]));
						checkins.add(gst);
						index++;
					}
				}
				
				if (leavingguest!=null) {
					lcompany = leavingguest.substring(0, leavingguest.indexOf('-')-1);
					lname = leavingguest.substring(leavingguest.indexOf('-')+2, leavingguest.indexOf(','));
					lfirstname = leavingguest.substring(leavingguest.indexOf(',')+2, leavingguest.indexOf(':'));
					larrival = leavingguest.substring(leavingguest.indexOf(':')+2, leavingguest.indexOf(';'));
					ldeparture = leavingguest.substring(leavingguest.indexOf(';')+2, leavingguest.lastIndexOf(';'));
					lroom = leavingguest.substring(leavingguest.lastIndexOf(';')+2, leavingguest.lastIndexOf(':'));
					lcheckedin = leavingguest.substring(leavingguest.lastIndexOf(':') + 2, leavingguest.indexOf('#'));
					lprice = leavingguest.substring(leavingguest.indexOf('#')+2);

					lgst[0] = lcompany;
					lgst[1] = lname;
					lgst[2] = lfirstname;
					lgst[3] = larrival;
					lgst[4] = ldeparture;
					lgst[5] = lroom;
					lgst[6] = lcheckedin;
					lgst[7] = lprice;

					if (res.createCal(lgst[4])==(date+1)) {
						dlm_checkout.add(checkoutindex, (lgst[5] + ": " + lgst[0] + " - " + lgst[1] + ", " + lgst[2]));
						checkouts.add(lgst);
						checkoutindex++;
					}
				}
			}	
			
			cw.setInitialized();		
	}


}
