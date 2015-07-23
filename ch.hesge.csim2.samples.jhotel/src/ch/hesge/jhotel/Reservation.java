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


import java.io.*;
import java.util.*;

public class Reservation extends Thread {
	private ReservationManagement rm;
	private int arrival;
	private int departure;
	private String name;
	private String room;
	private String price;
	private String[] language;
	private String[] oldguest;
	private String[] newguest;
	Guest guest = new Guest();
	private String[] emptyGuest = new String[guest.getEntries()];
	private String arrivalS, departureS;
	private CheckinWindow cw;
	
	public Reservation(ReservationManagement rm) {
		Language lang = new Language();
		language = lang.getLanguage();
		this.rm = rm;
	}
	
	public Reservation(CheckinWindow cw) {
		this.cw = cw;
		Language lang = new Language();
		language = lang.getLanguage();
	}
	
	public Reservation(ReservationManagement rm, String arr, String dep, String name, String room, String price) {
		this.arrival = createCal(arr);
		this.departure = createCal(dep);
		this.name = name;
		this.room = room;
		this.rm = rm;
		this.arrivalS = arr;
		this.departureS = dep;
		if (price.length()<3) {
			this.price = price + ",00";
		}
		else {
			this.price = price;
		}
		
		Language lang = new Language();
		language = lang.getLanguage();
	}
	
	public void run() {
		rm.setThreadRunning(language[66]);
		makeReservation(arrival, departure, name, room);
		rm.run();
		rm.setThreadEnded();
	}
	
	
	public boolean correctDate(String date) {
		boolean correctDate = false;
		int day, month, year;
		
		day = Integer.parseInt(date.substring(0, 2));
		month = Integer.parseInt(date.substring(3, 5));
		
		try {
			year = Integer.parseInt(date.substring(6, 10));
		}
		catch (StringIndexOutOfBoundsException sioob) {
			year = Integer.parseInt(("20") + date.substring(6, 8));
		}
	
		
		if ((month==1) || (month==3) || (month==5) || (month==7) || (month==8) || (month==10) || (month==12)) {
			if (day>31) {
				correctDate = false;
			}
			else {
				correctDate = true;
			}
		}
		else if (month==4 || month==6 || month==9 || month==11) {
			if (day>30) {
				correctDate = false;
			}
			else {
				correctDate = true;
			}
		}
		else if (month==2) {
			if (isLeapYear(year)) {
				if (day>29) {
					correctDate = false;
				}
				else {
					correctDate = true;
				}
			}
			else {
				if (day>28) {
					correctDate = false;
				}
				else {
					correctDate = true;
				}
			}
		}
		return correctDate;
	}
		
	public void deleteReservation(String[] guest) {
		deleteResThread drt = new deleteResThread(rm, guest);
		drt.start();
	}	
	public void changeReservation(String[] oldguest, String[] newguest) {
		changeResThread crt = new changeResThread(rm, oldguest, newguest);
		crt.start();
	}
	
	public void checkinGuest(String[] oldguest, String[] newguest) {
		checkinGuestThread cgt = new checkinGuestThread(cw, oldguest, newguest);
		cgt.start();
	}
	
	public void makeReservation(int arrival, int departure, String name, String room) {
		ArrayList reservations = new ArrayList();
		String[] availableRooms = new String[168];
		String[] tmp = new String[100000];
		int index=0;
		
		try {
			FileInputStream fis = new FileInputStream("./db/restable.jh");
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			reservations = (ArrayList) ois.readObject();
			ois.close();
			
			availableRooms = (String[]) reservations.get(reservations.size()-1);
			
			// Determine room - index
			loop1:
			for (int i=0; i<availableRooms.length; ++i) {
				if (availableRooms[i].equals(room)) {
					index = i;
					break loop1;
				}
			}

			
			tmp = (String[]) reservations.get(index);
			for (int i=arrival; i<departure; ++i) {
				tmp[i] = name + ": " + arrivalS + "; " +  departureS + "; " + room + ": " + false + "# " + price;
			}
			reservations.set(index, tmp);
		}
		catch (ClassNotFoundException cnf) {
			System.out.println(cnf + " Reservation.makeReservation()");
		}
		catch (IOException io) {
			System.out.println(io + " Reservation.makeReservation()");
		}
		
		try {
			FileOutputStream fos = new FileOutputStream("./db/restable.jh");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			oos.writeObject(reservations);
			oos.flush();
			oos.close();
		}
		catch (IOException io) {
			System.out.println(io);
		}
	}

	
	public void createResTable() {
		ArrayList al1, al2, al3, al4, al5;
		String[] f1, f2, f3, f4, f5, f6, f7, f8;
		ArrayList reservations = new ArrayList();
		String[] tmp;
		String[] availableRooms = new String[168];
		/*for (int i=0; i<availableRooms.length; ++i) {
			availableRooms[i]="";
		}*/
		int j=0;
		
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
			
			al1 = (ArrayList) single.readObject(); 			// Alle Einzelzimmer über alle Stockwerke als String[]
			al2 = (ArrayList) dbl.readObject();				// Alle Doppelzimmer über alle Stockwerke als String[]
			al3 = (ArrayList) triple.readObject();			// Alle Drei-Bett Zimmer über alle Stockwerke als String[]
			al4 = (ArrayList) quad.readObject();			// Alle Vier-Bett Zimmer über alle Stockwerke als String[]
			al5 = (ArrayList) apartment.readObject();	// Alle Apartments über alle Stockwerke als String[]
			
			single.close();
			dbl.close();
			triple.close();
			quad.close();
			apartment.close();
			
			// Einzelzimmer über alle Stockwerke
			f1 = (String[]) al1.get(0);			
			for (int i=0; i<f1.length; ++i) {
				availableRooms[j] = f1[i];
				++j;
				reservations.add(new String[100000]);
			}
			f1 = (String[]) al1.get(1);			
			for (int i=0; i<f1.length; ++i) {
				availableRooms[j] = f1[i];
				++j;
				reservations.add(new String[100000]);
			}
			f1 = (String[]) al1.get(2);			
			for (int i=0; i<f1.length; ++i) {
				availableRooms[j] = f1[i];
				++j;
				reservations.add(new String[100000]);
			}
			f1 = (String[]) al1.get(3);			
			for (int i=0; i<f1.length; ++i) {
				availableRooms[j] = f1[i];
				++j;
				reservations.add(new String[100000]);
			}
			f1 = (String[]) al1.get(4);			
			for (int i=0; i<f1.length; ++i) {
				availableRooms[j] = f1[i];
				++j;
				reservations.add(new String[100000]);
			}
			f1 = (String[]) al1.get(5);			
			for (int i=0; i<f1.length; ++i) {
				availableRooms[j] = f1[i];
				++j;
				reservations.add(new String[100000]);
			}
			f1 = (String[]) al1.get(6);			
			for (int i=0; i<f1.length; ++i) {
				availableRooms[j] = f1[i];
				++j;
				reservations.add(new String[100000]);
			}
			f1 = (String[]) al1.get(7);			
			for (int i=0; i<f1.length; ++i) {
				availableRooms[j] = f1[i];
				++j;
				reservations.add(new String[100000]);
			}
			
			// Doppelzimmer über alle Stockwerke
			f1 = (String[]) al2.get(0);			
			for (int i=0; i<f1.length; ++i) {
				availableRooms[j] = f1[i];
				++j;
				reservations.add(new String[100000]);
			}
			f1 = (String[]) al2.get(1);			
			for (int i=0; i<f1.length; ++i) {
				availableRooms[j] = f1[i];
				++j;
				reservations.add(new String[100000]);
			}
			f1 = (String[]) al2.get(2);			
			for (int i=0; i<f1.length; ++i) {
				availableRooms[j] = f1[i];
				++j;
				reservations.add(new String[100000]);
			}
			f1 = (String[]) al2.get(3);			
			for (int i=0; i<f1.length; ++i) {
				availableRooms[j] = f1[i];
				++j;
				reservations.add(new String[100000]);
			}
			f1 = (String[]) al2.get(4);			
			for (int i=0; i<f1.length; ++i) {
				availableRooms[j] = f1[i];
				++j;
				reservations.add(new String[100000]);
			}
			f1 = (String[]) al2.get(5);			
			for (int i=0; i<f1.length; ++i) {
				availableRooms[j] = f1[i];
				++j;
				reservations.add(new String[100000]);
			}
			f1 = (String[]) al2.get(6);			
			for (int i=0; i<f1.length; ++i) {
				availableRooms[j] = f1[i];
				++j;
				reservations.add(new String[100000]);
			}
			f1 = (String[]) al2.get(7);			
			for (int i=0; i<f1.length; ++i) {
				availableRooms[j] = f1[i];
				++j;
				reservations.add(new String[100000]);
			}

			// Drei-Bett Zimmer über alle Stockwerke
			f1 = (String[]) al3.get(0);			
			for (int i=0; i<f1.length; ++i) {
				availableRooms[j] = f1[i];
				++j;
				reservations.add(new String[100000]);
			}
			f1 = (String[]) al3.get(1);			
			for (int i=0; i<f1.length; ++i) {
				availableRooms[j] = f1[i];
				++j;
				reservations.add(new String[100000]);
			}
			f1 = (String[]) al3.get(2);			
			for (int i=0; i<f1.length; ++i) {
				availableRooms[j] = f1[i];
				++j;
				reservations.add(new String[100000]);
			}
			f1 = (String[]) al3.get(3);			
			for (int i=0; i<f1.length; ++i) {
				availableRooms[j] = f1[i];
				++j;
				reservations.add(new String[100000]);
			}
			f1 = (String[]) al3.get(4);			
			for (int i=0; i<f1.length; ++i) {
				availableRooms[j] = f1[i];
				++j;
				reservations.add(new String[100000]);
			}
			f1 = (String[]) al3.get(5);			
			for (int i=0; i<f1.length; ++i) {
				availableRooms[j] = f1[i];
				++j;
				reservations.add(new String[100000]);
			}
			f1 = (String[]) al3.get(6);			
			for (int i=0; i<f1.length; ++i) {
				availableRooms[j] = f1[i];
				++j;
				reservations.add(new String[100000]);
			}
			f1 = (String[]) al3.get(7);			
			for (int i=0; i<f1.length; ++i) {
				availableRooms[j] = f1[i];
				++j;
				reservations.add(new String[100000]);
			}

			// Vier-Bett Zimmer über alle Stockwerke
			f1 = (String[]) al4.get(0);			
			for (int i=0; i<f1.length; ++i) {
				availableRooms[j] = f1[i];
				++j;
				reservations.add(new String[100000]);
			}
			f1 = (String[]) al4.get(1);			
			for (int i=0; i<f1.length; ++i) {
				availableRooms[j] = f1[i];
				++j;
				reservations.add(new String[100000]);
			}
			f1 = (String[]) al4.get(2);			
			for (int i=0; i<f1.length; ++i) {
				availableRooms[j] = f1[i];
				++j;
				reservations.add(new String[100000]);
			}
			f1 = (String[]) al4.get(3);			
			for (int i=0; i<f1.length; ++i) {
				availableRooms[j] = f1[i];
				++j;
				reservations.add(new String[100000]);
			}
			f1 = (String[]) al4.get(4);			
			for (int i=0; i<f1.length; ++i) {
				availableRooms[j] = f1[i];
				++j;
				reservations.add(new String[100000]);
			}
			f1 = (String[]) al4.get(5);			
			for (int i=0; i<f1.length; ++i) {
				availableRooms[j] = f1[i];
				++j;
				reservations.add(new String[100000]);
			}
			f1 = (String[]) al4.get(6);			
			for (int i=0; i<f1.length; ++i) {
				availableRooms[j] = f1[i];
				++j;
				reservations.add(new String[100000]);
			}
			f1 = (String[]) al4.get(7);			
			for (int i=0; i<f1.length; ++i) {
				availableRooms[j] = f1[i];
				++j;
				reservations.add(new String[100000]);
			}

			// Apartments über alle Stockwerke
			f1 = (String[]) al5.get(0);			
			for (int i=0; i<f1.length; ++i) {
				availableRooms[j] = f1[i];
				++j;
				reservations.add(new String[100000]);
			}
			f1 = (String[]) al5.get(1);			
			for (int i=0; i<f1.length; ++i) {
				availableRooms[j] = f1[i];
				++j;
				reservations.add(new String[100000]);
			}
			f1 = (String[]) al5.get(2);			
			for (int i=0; i<f1.length; ++i) {
				availableRooms[j] = f1[i];
				++j;
				reservations.add(new String[100000]);
			}
			f1 = (String[]) al5.get(3);			
			for (int i=0; i<f1.length; ++i) {
				availableRooms[j] = f1[i];
				++j;
				reservations.add(new String[100000]);
			}
			f1 = (String[]) al5.get(4);			
			for (int i=0; i<f1.length; ++i) {
				availableRooms[j] = f1[i];
				++j;
				reservations.add(new String[100000]);
			}
			f1 = (String[]) al5.get(5);			
			for (int i=0; i<f1.length; ++i) {
				availableRooms[j] = f1[i];
				++j;
				reservations.add(new String[100000]);
			}
			f1 = (String[]) al5.get(6);			
			for (int i=0; i<f1.length; ++i) {
				availableRooms[j] = f1[i];
				++j;
				reservations.add(new String[100000]);
			}
			f1 = (String[]) al5.get(7);			
			for (int i=0; i<f1.length; ++i) {
				availableRooms[j] = f1[i];
				++j;
				reservations.add(new String[100000]);
			}
			reservations.add(availableRooms);
		}
		catch (ClassNotFoundException cnf) {
			System.out.println(cnf);
		}
		catch (IOException io) {
			System.out.println(io);
		}
		
		try {
			FileOutputStream fos = new FileOutputStream("./db/restable.jh");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			oos.writeObject(reservations);
			oos.flush();
			oos.close();
		}
		catch (IOException io) {
			System.out.println(io);
		}
	}
	
	
	public int createCal(String date) {
		int day, month, year;
		
		day = Integer.parseInt(date.substring(0, 2));
		month = Integer.parseInt(date.substring(3, 5));
		
		try {
			year = Integer.parseInt(date.substring(6, 10));
		}
		catch (StringIndexOutOfBoundsException sioob) {
			year = Integer.parseInt(("20") + date.substring(6, 8));
		}

		int days = 0;

		for (int i=2004; i<year; ++i) {
			if (isLeapYear(i)) {
				days = days + 366;
			}
			else {
				days = days + 365;
			}
		}
		
		for (int i=1; i<month; ++i) {
			if (i==1) {
				days = days + 31;
			}
			else if (i==2) {
				if (isLeapYear(year)) {
					days = days + 29;
				}
				else {
					days = days + 28;
				}
			}
			else if (i==3) {
				days = days + 31;
			}
			else if (i==4) {
				days = days + 30;
			}
			else if (i==5) {
				days = days + 31;
			}
			else if (i==6) {
				days = days + 30;
			}
			else if (i==7) {
				days = days + 31;
			}
			else if (i==8) {
				days = days +  31;
			}
			else if (i==9) {
				days = days + 30;
			}
			else if (i==10) {
				days = days + 31;
			}
			else if (i==11) {
				days = days + 30;
			}
			else if (i==12) {
				days = days + 31;
			}
		}
		
		days = days + day;
		
		return days;
	}
	
	public boolean isLeapYear(int year) {
		boolean isLY = false;
		
		if ((year % 4)!=0) {
			isLY = false; 
		}
		else if (((year % 4) == 0) && ((year % 100) == 0) && (year % 1000) == 0) {
			isLY = true;
		}
		else if ((year % 4) == 0 && ((year % 100) == 0)) {
			isLY = false;
		}
		else if ((year % 4) == 0) {
			isLY = true;
		}
		
		return isLY;
	}
	
	public boolean checkAvailability(int arrival, int departure, String room) {
		boolean available = true;
		ArrayList reservations = new ArrayList();
		String[] availableRooms = new String[168];
		String[] tmp;
		int index=0;
		
		try {
			FileInputStream fis = new FileInputStream("./db/restable.jh");
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			reservations = (ArrayList) ois.readObject();
			ois.close();
			
			availableRooms = (String[]) reservations.get(reservations.size()-1);
			

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

		}
		catch (ClassNotFoundException cnf) {
			System.out.println(cnf + " Reservation.makeReservation()");
		}
		catch (IOException io) {
			System.out.println(io + " Reservation.makeReservation()");
		}
		
		return available;
	}
	
	public int[] calcDate() {
		int[] days = new int[2];
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int daysInMonth;
		String f;
		String l;
		
		//jComboBox.setSelectedItem(Integer.toString(year));
		
		if ((month==0) || (month==2) || (month==4) || (month==6) || (month==7) || (month==9) || (month==11)) {
			daysInMonth = 31;
		}
		else if (month==1) {
			if (isLeapYear(year)) {
				daysInMonth = 29;
			}
			else {
				daysInMonth = 28;
			}
		}
		else {
			daysInMonth = 30;
		}
		
		if (month<9) {
			f = "01.0" + (month+1) + "." + year;
			l = daysInMonth + ".0" + (month+1) + "." + year;
		}
		else {
			f = "01." + (month+1) + "." + year;
			l = daysInMonth + "." + (month+1) + "." + year;
		}
		
		days[0] = createCal(f);
		days[1] = createCal(l);
		return days;
	}
	
	public int[] calcDate(int month, int year) {
		int[] days = new int[2];
		Calendar cal = Calendar.getInstance();
		int daysInMonth;
		String f;
		String l;
		
		if ((month==0) || (month==2) || (month==4) || (month==6) || (month==7) || (month==9) || (month==11)) {
			daysInMonth = 31;
		}
		else if (month==1) {
			if (isLeapYear(year)) {
				daysInMonth = 29;
			}
			else {
				daysInMonth = 28;
			}
		}
		else {
			daysInMonth = 30;
		}
		
		if (month<9) {
			f = "01.0" + (month+1) + "." + year;
			l = daysInMonth + ".0" + (month+1) + "." + year;
		}
		else {
			f = "01." + (month+1) + "." + year;
			l = daysInMonth + "." + (month+1) + "." + year;
		}
		
		days[0] = createCal(f);
		days[1] = createCal(l);
		return days;
	}

	
	
	/*public static void main(String[] args) {
		MainWindow mw = new MainWindow();
		mw.setVisible(false);
		ReservationManagement rm = new ReservationManagement(mw);
		rm.setVisible(false);
		Reservation res = new Reservation(rm);
		
		//res.makeReservation(9, 11, "Chris Olbertz", "10");
		//res.checkAvailability(10, 11, "10");
		res.createResTable();
		System.exit(0);
	}*/




}
