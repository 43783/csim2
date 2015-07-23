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

public class AboutWindow extends Frame {
	
	private String[] language;

	private javax.swing.JScrollPane jScrollPane = null;
	private javax.swing.JTextArea jTextArea = null;
	public AboutWindow() {
		Language lang = new Language();
		language = lang.getLanguage();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setLayout(null);
		this.add(getJScrollPane(), null);
		this.setBounds(200, 150, 436, 288);
		this.setTitle(language[87]);
		this.addWindowListener(new java.awt.event.WindowAdapter() { 
			public void windowClosing(java.awt.event.WindowEvent e) {    
				dispose();
			}
		});
	}
	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private javax.swing.JScrollPane getJScrollPane() {
		if(jScrollPane == null) {
			jScrollPane = new javax.swing.JScrollPane();
			jScrollPane.setViewportView(getJTextArea());
			jScrollPane.setBounds(6, 25, 425, 256);
		}
		return jScrollPane;
	}
	/**
	 * This method initializes jTextArea
	 * 
	 * @return javax.swing.JTextArea
	 */
	private javax.swing.JTextArea getJTextArea() {
		if(jTextArea == null) {
			jTextArea = new javax.swing.JTextArea();
			jTextArea.setText("JHotel 0.6 " + "\n\n" + 
			"JHotel is a software for small and medium-sized hotels. It helps you manage your guests, " +
			"your reservations and everything else that small and medium-sized hotels require. " + "\n\n" + 
			"JHotel is multi-lingual, additional language files are available on the project website. " +
			"You can easily create your own language files using JHotel_Translator, which is part of the " +
			"JHotel package. JHotel is written in Java and platform independent. It will run on any platform with a Java-VM." + "\n\n" +
			"JHotel is available under the GPL license and is can be found on Sourceforge: " + "\n\n" + 
			"http://sourceforge.net/projects/jhotel" + "\n\n" + 
			"If you have any suggestions and/or questions, feel free to contact me:" + "\n\n" + 
			"JSoft" + "\n" + 
			"Martin Jungowski" + "\n" + 
			"E-Mail: jungowsk@in.tum.de" + "\n" + 
			"Phone: 0049-89-45 23 78 00" + "\n" + 
			"Munich, Germany");
			jTextArea.setLineWrap(true);

			jTextArea.setWrapStyleWord(true);
			jTextArea.setEditable(false);
			jTextArea.setComponentOrientation(java.awt.ComponentOrientation.LEFT_TO_RIGHT);
		}
		return jTextArea;
	}
}  //  @jve:visual-info  decl-index=0 visual-constraint="10,10"
