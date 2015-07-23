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

public class Rooms {
	
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
	String[] settings;
	int year;

	public Rooms() {
		Options options = new Options();
		this.settings = options.getSettings();
		
		getRooms();
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
				for (int i=0; i<sr1a.length; ++i) {
					sr1 = sr1 + sr1a[i] + ", ";
				}
				//sr1 = sr1 + sr1a[sr1a.length-1];
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
	
	
	private int getLength(String str) {
		int cnt;
		String tmp;
		int offset = getOffset(str);
		int roomlength = getRoomLength(str);
		
		if (str.length()==0) {
			cnt = 0;
		}
		else if (str.length()==2) {
			cnt = 1;
		}
		else if (str.length()==3) {
			cnt = 1;
		}
		else {
			cnt = 0;
			for (int index=0; index<=(str.length()-roomlength); index = index+offset) {
				++cnt;
			}
		}
		
		return cnt;
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
		
		if (cnt <=2) {
			cnt = cnt + 2;
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

	
	public String[] getRooms(int floor) {
		String[] rooms;
		String f;
		int i=0;
		
		if (floor==1) {
			rooms = new String[getLength(sr1)+getLength(dr1)+getLength(tr1)+getLength(qr1)+getLength(ap1)];
			System.out.println(rooms.length);
			String[] sr1a = (String[]) sr.get(0);
			for (int j=0; j<sr1a.length; ++j) {
				rooms[i] = ("S" + sr1a[j]);
				++i;
			}
			
			String[] dr1a = (String[]) dr.get(0);
			for (int j=0; j<dr1a.length; ++j) {
				rooms[i] = "D" + dr1a[j];
				++i;
			}
			
			String[] tr1a = (String[]) tr.get(0);
			for (int j=0; j<tr1a.length; ++j) {
				rooms[i] = "T" + tr1a[j];
				++i;
			}
			String[] qr1a = (String[]) qr.get(0);
			for (int j=0; j<qr1a.length; ++j) {
				rooms[i] = "Q" + qr1a[j];
				++i;
			}
			String[] ap1a = (String[]) ap.get(0);
			for (int j=0; j<ap1a.length; ++j) {
				rooms[i] = "A" + ap1a[j];
				++i;
			}
		}
		else if (floor==2) {
			rooms = new String[getLength(sr2)+getLength(dr2)+getLength(tr2)+getLength(qr2)+getLength(ap2)];
			String[] sr2a = (String[]) sr.get(1);
			for (int j=0; j<sr2a.length; ++j) {
				rooms[i] = ("S" + sr2a[j]);
				++i;
			}
			String[] dr2a = (String[]) dr.get(1);
			for (int j=0; j<dr2a.length; ++j) {
				rooms[i] = "D" + dr2a[j];
				++i;
			}
			String[] tr2a = (String[]) tr.get(1);
			for (int j=0; j<tr2a.length; ++j) {
				rooms[i] = "T" + tr2a[j];
				++i;
			}
			String[] qr2a = (String[]) qr.get(1);
			for (int j=0; j<qr2a.length; ++j) {
				rooms[i] = "Q" + qr2a[j];
				++i;
			}
			String[] ap2a = (String[]) ap.get(1);
			for (int j=0; j<ap2a.length; ++j) {
				rooms[i] = "A" + ap2a[j];
				++i;
			}
		}
		else if (floor==3) {
			rooms = new String[getLength(sr3)+getLength(dr3)+getLength(tr3)+getLength(qr3)+getLength(ap3)];
			String[] sr3a = (String[]) sr.get(2);
			for (int j=0; j<sr3a.length; ++j) {
				rooms[i] = ("S" + sr3a[j]);
				++i;
			}
			String[] dr3a = (String[]) dr.get(2);
			for (int j=0; j<dr3a.length; ++j) {
				rooms[i] = "D" + dr3a[j];
				++i;
			}
			String[] tr3a = (String[]) tr.get(2);
			for (int j=0; j<tr3a.length; ++j) {
				rooms[i] = "T" + tr3a[j];
				++i;
			}
			String[] qr3a = (String[]) qr.get(2);
			for (int j=0; j<qr3a.length; ++j) {
				rooms[i] = "Q" + qr3a[j];
				++i;
			}
			String[] ap3a = (String[]) ap.get(2);
			for (int j=0; j<ap3a.length; ++j) {
				rooms[i] = "A" + ap3a[j];
				++i;
			}
		}
		else if (floor==4) {
			rooms = new String[getLength(sr4)+getLength(dr4)+getLength(tr4)+getLength(qr4)+getLength(ap4)];
			String[] sr4a = (String[]) sr.get(3);
			for (int j=0; j<sr4a.length; ++j) {
				rooms[i] = ("S" + sr4a[j]);
				++i;
			}
			String[] dr4a = (String[]) dr.get(3);
			for (int j=0; j<dr4a.length; ++j) {
				rooms[i] = "D" + dr4a[j];
				++i;
			}
			String[] tr4a = (String[]) tr.get(3);
			for (int j=0; j<tr4a.length; ++j) {
				rooms[i] = "T" + tr4a[j];
				++i;
			}
			String[] qr4a = (String[]) qr.get(3);
			for (int j=0; j<qr4a.length; ++j) {
				rooms[i] = "Q" + qr4a[j];
				++i;
			}
			String[] ap4a = (String[]) ap.get(3);
			for (int j=0; j<ap4a.length; ++j) {
				rooms[i] = "A" + ap4a[j];
				++i;
			}
		}
		else if (floor==5) {
			rooms = new String[getLength(sr5)+getLength(dr5)+getLength(tr5)+getLength(qr5)+getLength(ap5)];
			String[] sr5a = (String[]) sr.get(4);
			for (int j=0; j<sr5a.length; ++j) {
				rooms[i] = ("S" + sr5a[j]);
				++i;
			}
			String[] dr5a = (String[]) dr.get(4);
			for (int j=0; j<dr5a.length; ++j) {
				rooms[i] = "D" + dr5a[j];
				++i;
			}
			String[] tr5a = (String[]) tr.get(4);
			for (int j=0; j<tr5a.length; ++j) {
				rooms[i] = "T" + tr5a[j];
				++i;
			}
			String[] qr5a = (String[]) qr.get(4);
			for (int j=0; j<qr5a.length; ++j) {
				rooms[i] = "Q" + qr5a[j];
				++i;
			}
			String[] ap5a = (String[]) ap.get(4);
			for (int j=0; j<ap5a.length; ++j) {
				rooms[i] = "A" + ap5a[j];
				++i;
			}
		}
		else if (floor==6) {
			rooms = new String[getLength(sr6)+getLength(dr6)+getLength(tr6)+getLength(qr6)+getLength(ap6)];
			String[] sr6a = (String[]) sr.get(5);
			for (int j=0; j<sr6a.length; ++j) {
				rooms[i] = ("S" + sr6a[j]);
				++i;
			}
			String[] dr6a = (String[]) dr.get(5);
			for (int j=0; j<dr6a.length; ++j) {
				rooms[i] = "D" + dr6a[j];
				++i;
			}
			String[] tr6a = (String[]) tr.get(5);
			for (int j=0; j<tr6a.length; ++j) {
				rooms[i] = "T" + tr6a[j];
				++i;
			}
			String[] qr6a = (String[]) qr.get(5);
			for (int j=0; j<qr6a.length; ++j) {
				rooms[i] = "Q" + qr6a[j];
				++i;
			}
			String[] ap6a = (String[]) ap.get(5);
			for (int j=0; j<ap6a.length; ++j) {
				rooms[i] = "A" + ap6a[j];
				++i;
			}
		}
		else if (floor==7) {
			rooms = new String[getLength(sr7)+getLength(dr7)+getLength(tr7)+getLength(qr7)+getLength(ap7)];
			String[] sr7a = (String[]) sr.get(6);
			for (int j=0; j<sr7a.length; ++j) {
				rooms[i] = ("S" + sr7a[j]);
				++i;
			}
			String[] dr7a = (String[]) dr.get(6);
			for (int j=0; j<dr7a.length; ++j) {
				rooms[i] = "D" + dr7a[j];
				++i;
			}
			String[] tr7a = (String[]) tr.get(6);
			for (int j=0; j<tr7a.length; ++j) {
				rooms[i] = "T" + tr7a[j];
				++i;
			}
			String[] qr7a = (String[]) qr.get(6);
			for (int j=0; j<qr7a.length; ++j) {
				rooms[i] = "Q" + qr7a[j];
				++i;
			}
			String[] ap7a = (String[]) ap.get(6);
			for (int j=0; j<ap7a.length; ++j) {
				rooms[i] = "A" + ap7a[j];
				++i;
			}
		}
		else if (floor==8) {
			rooms = new String[getLength(sr8)+getLength(dr8)+getLength(tr8)+getLength(qr8)+getLength(ap8)];
			String[] sr8a = (String[]) sr.get(7);
			for (int j=0; j<sr8a.length; ++j) {
				rooms[i] = ("S" + sr8a[j]);
				++i;
			}
			String[] dr8a = (String[]) dr.get(7);
			for (int j=0; j<dr8a.length; ++j) {
				rooms[i] = "D" + dr8a[j];
				++i;
			}
			String[] tr8a = (String[]) tr.get(7);
			for (int j=0; j<tr8a.length; ++j) {
				rooms[i] = "T" + tr8a[j];
				++i;
			}
			String[] qr8a = (String[]) qr.get(7);
			for (int j=0; j<qr8a.length; ++j) {
				rooms[i] = "Q" + qr8a[j];
				++i;
			}
			String[] ap8a = (String[]) ap.get(7);
			for (int j=0; j<ap8a.length; ++j) {
				rooms[i] = "A" + ap8a[j];
				++i;
			}
		}
		else {
			rooms = new String[1];
		}
		return rooms;
	}
	
	
	public void createRoomFiles(String[] rooms) {

		for (int i=0; i<rooms.length; ++i) {
			try {
				FileOutputStream tmp = new FileOutputStream("./cfg/" + rooms[i] + ".jhr");
				ObjectOutputStream tmp2 = new ObjectOutputStream(tmp);
				boolean[] year = new boolean[1000000];
				
				for (int k=0; k<1000000; ++k) {
					year[i] = false;
				}
				
				tmp2.writeObject(year);
				tmp2.flush();
				tmp2.close();
			}
			catch (IOException io) {
				System.out.println(io + " createRoomFiles()");
			}
		}	
	}

	public int getLength(int floor) {
		if (floor==1) {
			return getLength(sr1) + getLength(dr1) + getLength(tr1) + getLength(qr1) + getLength(ap1);
		}
		else if (floor==2) {
			return getLength(sr2) + getLength(dr2) + getLength(tr2) + getLength(qr2) + getLength(ap2);
		}
		else if (floor==3) {
			return getLength(sr3) + getLength(dr3) + getLength(tr3) + getLength(qr3) + getLength(ap3);
		}
		else if (floor==4) {
			return getLength(sr4) + getLength(dr4) + getLength(tr4) + getLength(qr4) + getLength(ap4);
		}
		else if (floor==5) {
			return getLength(sr5) + getLength(dr5) + getLength(tr5) + getLength(qr5) + getLength(ap5);
		}
		else if (floor==6) {
			return getLength(sr6) + getLength(dr6) + getLength(tr6) + getLength(qr6) + getLength(ap6);
		}
		else if (floor==7) {
			return getLength(sr7) + getLength(dr7) + getLength(tr7) + getLength(qr7) + getLength(ap7);
		}
		else {
			return getLength(sr8) + getLength(dr8) + getLength(tr8) + getLength(qr8) + getLength(ap8);
		}

	}

	



	/*public static void main(String[] args) {
		length;
		String[] rooms;
		Rooms rt = new Rooms();
		rt.getRooms();
		
		for (int i=0; i<Integer.parseInt(rt.settings[2]); ++i) {
			rooms = rt.getRooms(i);
			rt.createRoomFiles(rooms);
		}
	}*/
}
