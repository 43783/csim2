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

public class Language {
	String[] settings = new String[3];
	String language;
	int words = 80;
	
	
	public Language() {
		
		try {
			FileInputStream fis = new FileInputStream("./cfg/options.cfg");
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			this.settings = (String[]) ois.readObject();
			ois.close();
			this.language = settings[1];
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
	}
	
	public String[] getLanguage() {
		String[] lang = new String[words];
		
		if (language.equals("Deutsch")) {
			lang = getGerman();
		}
		else if (language.equals("English")) {
			lang = getEnglish();
		}
		else if (language.equals("Francais")) {
			lang = getFrench();
		}
		else if (language.equals("Espagnol")) {
			lang = getSpanish();
		}
		else if (language.equals("Polski")) {
			lang = getPolish();
		}
		
		return lang;
	}
	
	
	public String[] getGerman() {
		String[] lang = new String[words];
		
		try {	
			FileInputStream fis = new FileInputStream("./cfg/german.lng");
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			lang = (String[]) ois.readObject();
			ois.close();
			this.language = settings[1];
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
	
		return lang;
	}
	
	public String[] getEnglish() {
		String[] lang = new String[words];
		
		try {	
			FileInputStream fis = new FileInputStream("./cfg/english.lng");
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			lang = (String[]) ois.readObject();
			ois.close();
			this.language = settings[1];
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
	
		return lang;

	}
	
	public String[] getFrench() {
		String[] lang = new String[words];
		
		try {	
			FileInputStream fis = new FileInputStream("./cfg/french.lng");
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			lang = (String[]) ois.readObject();
			ois.close();
			this.language = settings[1];
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
	
		return lang;

	}
	
	public String[] getSpanish() {
		String[] lang = new String[words];
		
		try {	
			FileInputStream fis = new FileInputStream("./cfg/spanish.lng");
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			lang = (String[]) ois.readObject();
			ois.close();
			this.language = settings[1];
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
	
		return lang;

	}

	public String[] getPolish() {
		String[] lang = new String[words];
		
		try {	
			FileInputStream fis = new FileInputStream("./cfg/polish.lng");
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			lang = (String[]) ois.readObject();
			ois.close();
			this.language = settings[1];
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
	
		return lang;

	}
	
	public int getCnt() {
		return words;
	}
	
}
