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


public class ActionCommandsMW {
MainWindow mw;
String command;
String[] language;

public ActionCommandsMW(MainWindow mw, String command) {
	this.mw = mw;
	this.command = command;
	
	Language lang = new Language();
	language = lang.getLanguage();
	
		
	if (command.equals(language[0])) {
		mw.newGuest();
	}
	else if (command.equals(language[1])) {
		mw.searchGuest();
	}
	else if (command.equals(language[2])) {
		mw.saveGuest();	
	}
	else if (command.equals(language[4])) {
		ImportWindow iw = new ImportWindow();
		iw.setVisible(true);
		//mw.importDB();
	}
	else if (command.equals(language[3])) {
		ExportWindow ew = new ExportWindow();
		ew.setVisible(true);
		//mw.exportDB();
	}
	else if (command.equals(language[6])) {
		OptionsWindow ow = new OptionsWindow(mw);
		ow.setVisible(true);
	}
	else if (command.equals(language[24])) {
		System.exit(0);
	}
	else if (command.equals(language[87])) {
		AboutWindow aw = new AboutWindow();
		aw.setVisible(true);
	}
}


}
