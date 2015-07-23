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


import java.awt.*;
import java.awt.event.*;

public class MainMenu extends MenuBar {

	private MenuItem miRueck;
	private CheckboxMenuItem miFarbe;
	String[] language;

	private void addNewMenuItem(Menu menu, String name, ActionListener listener) {
		int seek = name.indexOf('&');
		MenuItem mi;
		
		mi = new MenuItem(name);
		mi.setActionCommand(name);
		mi.addActionListener(listener);
		menu.add(mi);
	}


	public MainMenu(ActionListener listener) 
	{
		
		Language lang = new Language();
		language = lang.getLanguage();

	
		
	  Menu m;

	  //Datei
	  m = new Menu(language[42]);
	  addNewMenuItem(m, language[0], listener);
	  addNewMenuItem(m, language[1], listener);
	  addNewMenuItem(m, language[2], listener);
	  m.addSeparator();
	  addNewMenuItem(m, language[3], listener);
	  addNewMenuItem(m, language[4], listener);
	  m.addSeparator();
	  addNewMenuItem(m, language[24], listener);
	  add(m);
	  m = new Menu(language[5]);
	  addNewMenuItem(m, language[6], listener);
	  add(m);
	  m = new Menu(language[88]);
	  addNewMenuItem(m, language[87], listener);
	  add(m);
	}
  }

