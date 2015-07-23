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

public class Options {
	
	private String filename;
	private String language;
	private String newoption;
	private String[] options = new String[4];

	public Options() {
		
		try {
			FileInputStream fis = new FileInputStream("./cfg/options.cfg");
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			options = (String[]) ois.readObject();

			ois.close();
		}
		catch (ClassNotFoundException cnf) {
			// nada
		}
		catch (FileNotFoundException fnf) {
			// nada
		}
		catch (IOException io1) {
			// nada
		}
		
		//System.out.println(options[0]);
		//System.out.println(options[1]);
		
		
	}

	public void setFileName(String new_filename) {
		
		try {
			FileOutputStream fos = new FileOutputStream("./cfg/options.cfg");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			options[0] = new_filename;
			
			oos.writeObject(options);
			oos.flush();
			oos.close();
		}
		catch (FileNotFoundException fnf) {
			// nada
		}
		catch (IOException io) {
			// nada
		}
	}
	public String getFileName() {
		return options[0];
	}
	
	
	public void setLanguage(String new_language) {
	
		try {
			FileOutputStream fos = new FileOutputStream("./cfg/options.cfg");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			options[1] = new_language;
			//oos.writeObject(filename);
			oos.writeObject(options);
			oos.flush();
			oos.close();
		}
		catch (FileNotFoundException fnf) {
			// nada
		}
		catch (IOException io) {
			// nada
		}

	}
	public String getLanguage() {
		return options[1];
	}
	
	public void setFloors(String floors) {
	
		try {
			FileOutputStream fos = new FileOutputStream("./cfg/options.cfg");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			options[2] = floors;
			//oos.writeObject(filename);
			oos.writeObject(options);
			oos.flush();
			oos.close();
		}
		catch (FileNotFoundException fnf) {
			// nada
		}
		catch (IOException io) {
			// nada
		}

	}
	public String getFloors() {
		return options[2];
	}

	
	
	public void setSettings(String[] settings) {
		try {
			FileOutputStream fos = new FileOutputStream("./cfg/options.cfg");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			//oos.writeObject(filename);
			oos.writeObject(settings);
			oos.flush();
			oos.close();
		}
		catch (FileNotFoundException fnf) {
			// nada
		}
		catch (IOException io) {
			// nada
		}

	}

	public String[] getSettings() {
		return options;
	}
	
	public void setResolution(String res) {
		try {
			FileOutputStream fos = new FileOutputStream("./cfg/options.cfg");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			options[3] = res;
			//oos.writeObject(filename);
			oos.writeObject(options);
			oos.flush();
			oos.close();
		}
		catch (FileNotFoundException fnf) {
			// nada
		}
		catch (IOException io) {
			// nada
		}
	}
	public String getResolution() {
		return options[3];
	}
}
