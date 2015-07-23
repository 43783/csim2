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

public class deleteResThread extends Thread{
	
	private Reservation res;
	private ReservationManagement rm;
	private String[] reservation;
	private String[] language;
	
	public deleteResThread(ReservationManagement rm, String[] reservation) {
		Reservation res = new Reservation(rm);
		Language lang = new Language();
		language = lang.getLanguage();
		this.res = res;
		this.rm = rm;
		this.reservation = reservation;
	}

	public void run() {
		deleteReservation(reservation);
	}


	public void deleteReservation(String[] reservation) {
		String toDelete = reservation[0] + " - " + reservation[1] + ", " + reservation[2] + ": " + reservation[3] + "; " + reservation[4];
		String roomtoDelete = reservation[5];
		
		int firstday = res.createCal(reservation[3]);
		int lastday = res.createCal(reservation[4]);
		
		ArrayList reservations = new ArrayList();
		String[] availableRooms = new String[168];
		String[] tmp = new String[100000];
		int index=0;
		
		rm.setThreadRunning(language[80]);
		
		try {
			FileInputStream fis = new FileInputStream("./db/restable.jh");
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			reservations = (ArrayList) ois.readObject();
			ois.close();
			
			availableRooms = (String[]) reservations.get(reservations.size()-1);
			
			// Determine room - index
			loop1:
			for (int i=0; i<availableRooms.length; ++i) {
				if (availableRooms[i].equals(roomtoDelete)) {
					index = i;
					break loop1;
				}
			}

			
			tmp = (String[]) reservations.get(index);
			for (int i=firstday; i<lastday; ++i) {
				tmp[i] = null;
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

		rm.updateTable(reservations);
		rm.setThreadEnded();

	}
}
