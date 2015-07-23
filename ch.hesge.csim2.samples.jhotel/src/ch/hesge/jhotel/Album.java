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

import java.io.*;
import java.util.*;


public class Album extends Thread {
	
	private AlbumWindow aw;
	private ArrayList album = new ArrayList();
	private long entries = 0;
	
	public Album(AlbumWindow aw) {
		this.aw = aw;
	}

	public void run() {
		aw.setThreadRunning();
		
		try {
			FileInputStream fis = new FileInputStream("./db/album.jh");
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			album = (ArrayList) ois.readObject();
			ois.close();
			
			entries = album.size();
		}
		catch (IOException io) {
			System.out.println(io);
		}
		catch (ClassNotFoundException cnf) {
			System.out.println(cnf);
		}
		
		aw.setThreadEnded(entries);
	}
	
	public void saveAlbum(ArrayList newEntries) {
		
		aw.setThreadRunning();
		
		
		for (int i=0; i<newEntries.size(); ++i) {
			album.add(newEntries.get(i));
		}
		try {
			FileOutputStream fos = new FileOutputStream("./db/album.jh");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			oos.writeObject(album);
			oos.flush();
			oos.close();
		}
		catch (IOException io) {
			System.out.println(io);
		}
		
		aw.dispose();
		
	}
}
