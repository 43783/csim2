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


import java.util.*;
import java.io.*;

public class Guest {
	
	String dbname;
	String EOE = "EOE";
	int entries = 19;

	public Guest() {
		Options options = new Options();
		dbname = options.getFileName();
		
	}
	
	public int getEntries() {
		return entries;
	}
	
	public void writeDB(Object file) {
		try {
			FileOutputStream fos = new FileOutputStream(dbname);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			oos.writeObject(file);
			oos.flush();
			oos.close();		
			
		}
		catch (IOException io) {
			// nada
		}
	}

	public ArrayList getDB() {
		ArrayList al = new ArrayList();		
	
		try {
			FileInputStream fis = new FileInputStream(dbname);
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			al = (ArrayList) ois.readObject();
			
		}
		catch (IOException io1) {
			// nada
		}
		catch (ClassNotFoundException cnf) {
			// nada
		}
		
		return al;
	}

	public boolean findGuest(String guest) {
		boolean isGuest = false;
		ArrayList db = new ArrayList();
		String[] currentGuest;
		
		try {
			FileInputStream fis = new FileInputStream(dbname);
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			db = (ArrayList) ois.readObject();
			ois.close();
			// File closed, guestDB in db
			
			loop1:
			for (int i=0; i<db.size(); ++i) {
			currentGuest = (String[]) db.get(i);
				
				loop2:
				for (int index=0; index<entries; ++index) {
					if (currentGuest[index].equalsIgnoreCase(guest)) {
						isGuest = true;
						break loop1;
					}
				}
			}
		}
		catch (IOException io) {
			System.out.println(io + " findGuest()");
		}
		catch (ClassNotFoundException cnf) {
			System.out.println(cnf + " findGuest()");
		}
		
		
		return isGuest;
	}
	
	public String[] getGuest(String[] guest) {
		String[] foundguest = new String[entries];
		ArrayList db = new ArrayList();
		String[] currentGuest;
		boolean[] foundarray = new boolean[entries];
		boolean found = false;
		
		try {
			FileInputStream fis = new FileInputStream(dbname);
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			db = (ArrayList) ois.readObject();
			ois.close();
			// File closed, guestDB in db
			
			loop1:
			for (int i=0; i<db.size(); ++i) {
			currentGuest = (String[]) db.get(i);
				
				loop2:
				for (int index=0; index<entries; ++index) { 
					if (currentGuest[index].equalsIgnoreCase(guest[index])) {
						foundarray[index] = true;
					}
					else {
						foundarray[index] = false;
						break loop2;
					}
				}
					
					loop3:
					for (int fi = 0; fi<entries; ++fi) {
						if (foundarray[fi]==true) {
							found = true;
						}
						else {
							found = false;
							break loop3;
						}
					}
					
					if (found) {
						foundguest = currentGuest;
						break loop1;
					}
					else {
						continue loop1;
					}
				}
		}
		catch (IOException io) {
			System.out.println(io + " findGuest()");
		}
		catch (ClassNotFoundException cnf) {
			System.out.println(cnf + " findGuest()");
		}
		
		return foundguest;
	}
	
	
	public String[] getSWGuest(String[] guest) {
		String[] foundguest = new String[entries];
		ArrayList db = new ArrayList();
		String[] currentGuest;
		boolean[] foundarray = new boolean[entries];
		boolean found = false;
		
		try {
			FileInputStream fis = new FileInputStream(dbname);
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			db = (ArrayList) ois.readObject();
			ois.close();
			// File closed, guestDB in db
			
			loop1:
			for (int i=0; i<db.size(); ++i) {
			currentGuest = (String[]) db.get(i);
				
				loop2:
				for (int index=0; index<3; ++index) { 
					if (currentGuest[index].equalsIgnoreCase(guest[index])) {
						foundarray[index] = true;
					}
					else {
						foundarray[index] = false;
						break loop2;
					}
				}
					
					loop3:
					for (int fi = 0; fi<3; ++fi) {
						if (foundarray[fi]==true) {
							found = true;
						}
						else {
							found = false;
							break loop3;
						}
					}
					
					if (found) {
						foundguest = currentGuest;
						break loop1;
					}
					else {
						continue loop1;
					}
				}
		}
		catch (IOException io) {
			System.out.println(io + " findGuest()");
		}
		catch (ClassNotFoundException cnf) {
			System.out.println(cnf + " findGuest()");
		}
		
		return foundguest;
	}

	
	
	public String[] getGuest(String guest) {
		String[] foundguest = new String[entries];
		ArrayList db = new ArrayList();
		String[] currentGuest;
		boolean[] foundarray = new boolean[entries];
		boolean found = false;
		
		try {
			FileInputStream fis = new FileInputStream(dbname);
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			db = (ArrayList) ois.readObject();
			ois.close();
			// File closed, guestDB in db
			
			loop1:
			for (int i=0; i<db.size(); ++i) {
			currentGuest = (String[]) db.get(i);
				
				loop2:
				for (int index=0; index<entries; ++index) {
					if (currentGuest[index].equalsIgnoreCase(guest)) {
						foundguest = currentGuest;
						break loop1;
					}
				}
			}


		}
		catch (IOException io) {
			System.out.println(io + " findGuest()");
		}
		catch (ClassNotFoundException cnf) {
			System.out.println(cnf + " findGuest()");
		}
		
		return foundguest;
	}

	
	
	
	
	
	
	
	
	public void addGuest(String[] newguest, String[] oldguest) {
		boolean isGuest = false;
		ArrayList db = new ArrayList();
		String[] currentGuest;
		
		for (int i=0; i<entries; ++i) {
			if (newguest[i].equals("")) {
				newguest[i] = "N/A";
			}
		}
		
		try {
			FileInputStream fis = new FileInputStream(dbname);
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			db = (ArrayList) ois.readObject();
			
			
			ois.close();
			
			// File closed, guestDB in db
			
			currentGuest = getGuest(oldguest);
			
			if (currentGuest[0]==null) {
				addNew(newguest);
			}
			else {
				replaceExisting(newguest, oldguest);
				
			}

		}
		catch (IOException io) {
			System.out.println(io + " addGuest()");
			addNew(newguest);
		}
		catch (ClassNotFoundException cnf) {
			System.out.println(cnf + " addGuest()");
		}
	}
	
	public void addNew(String[] newguest) {
		ArrayList db = new ArrayList();
		String[] currentGuest;
		
		for (int i=0; i<entries; ++i) {
			if (newguest[i].equals("")) {
				newguest[i] = "N/A";
			}
		}
		
		try {
			FileInputStream fis = new FileInputStream(dbname);
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			db = (ArrayList) ois.readObject();
			ois.close();
			// File closed, guestDB in db
		}
		catch (IOException io) {
			System.out.println(io + " addNew()");
		}
		catch (ClassNotFoundException cnf) {
			System.out.println(cnf + " addNew()");
		}
		
		db.add(newguest);
		
		try {
			FileOutputStream fos = new FileOutputStream(dbname);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			oos.writeObject(db);
			oos.flush();
			oos.close();
		}
		catch (IOException io) {
			System.out.println(io + " addNew()");
		}
	}
	
	public void replaceExisting(String[] newguest, String[] oldguest) {
		ArrayList db = new ArrayList();
		String[] currentGuest;

		
		try {
			FileInputStream fis = new FileInputStream(dbname);
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			db = (ArrayList) ois.readObject();
			ois.close();
			// File closed, guestDB in db
		}
		catch (IOException io) {
			System.out.println(io + " replaceExisting()");
		}
		catch (ClassNotFoundException cnf) {
			System.out.println(cnf + " replaceExisting()");
		}

		db.set(getIndex(oldguest),  newguest);

		try {
			FileOutputStream fos = new FileOutputStream(dbname);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			oos.writeObject(db);
			oos.flush();
			oos.close();
			
			
			
		}
		catch (IOException io) {
			// nada
		}


	}

	public int getIndex(String[] oldguest) {
		int guestindex=0;
		
		ArrayList db = new ArrayList();
		String[] currentGuest;
		boolean[] foundarray = new boolean[oldguest.length];
		boolean found = false;
		
		try {
			FileInputStream fis = new FileInputStream(dbname);
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			db = (ArrayList) ois.readObject();
			ois.close();
			// File closed, guestDB in db
			
			loop1:
			for (int i=0; i<db.size(); ++i) {
			currentGuest = (String[]) db.get(i);
			
			for (int k=0; k<entries; ++k) {
				if (!findGuest(oldguest[k])) {
					guestindex = -1;
					break loop1;
				}
			}
				
				loop2:
				for (int index=0; index<entries; ++index) { 
					if (currentGuest[index].equalsIgnoreCase(oldguest[index])) {
						foundarray[index] = true;
					}
					else {
						foundarray[index] = false;
						break loop2;
					}
				}
					
					loop3:
					for (int fi = 0; fi<entries; ++fi) {
						if (foundarray[fi]==true) {
							found = true;
						}
						else {
							found = false;
							break loop3;
						}
					}
					
					if (found) {
						break loop1;
					}
					else {
						++guestindex;
						continue loop1;
					}
				}
		}
		catch (IOException io) {
			System.out.println(io + " getIndex()");
		}
		catch (ClassNotFoundException cnf) {
			System.out.println(cnf + " getIndex()");
		}

		return guestindex;
	}

	public void deleteExisting(String[] guest) {
		ArrayList db = new ArrayList();
		String[] currentGuest;

		
		try {
			FileInputStream fis = new FileInputStream(dbname);
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			db = (ArrayList) ois.readObject();
			ois.close();
			// File closed, guestDB in db
		}
		catch (IOException io) {
			System.out.println(io + " deleteExisting()");
		}
		catch (ClassNotFoundException cnf) {
			System.out.println(cnf + " deleteExisting()");
		}
		
		if (getIndex(guest)==(-1)) {
			//
		}
		else {
		
			db.remove(getIndex(guest));

			try {
				FileOutputStream fos = new FileOutputStream(dbname);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
			
				oos.writeObject(db);
				oos.flush();
				oos.close();
			
			
			}
			catch (IOException io) {
				// nada
			}
		}
	}



	/*public static void main(String[] args) {
		String[] guests = new String[3];
		String[] newguest = new String[3];
		String[] foundguest = new String[3];
		Guest guest = new Guest();
		ArrayList al = new ArrayList();
		
		
		guests[0] = "test1 0";
		guests[1] = "test1 1";
		guests[2] = "test1 2";
		newguest[0] = "tßst3";
		newguest[1] = "tßst3";
		newguest[2] = "tßst3";
		
		//guest.addNew(newguest);
		guest.addGuest(newguest, guests);
		//guest.deleteExisting(newguest);
		System.out.println(guest.findGuest("tßst3"));
		
		

		
	}*/
	
}
