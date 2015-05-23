package ch.hesge.csim2.ui;

import ch.hesge.csim2.ui.views.MainView;

/**
 * This is the main entry point of the Csim2 UI environment.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */
public class AppStartup {

	/**
	 * Main application entry point.
	 */
	public static void main(String[] args) {
		new MainView().setVisible(true);
	}
}
