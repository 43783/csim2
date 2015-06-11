package ch.hesge.csim2.core.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class centralize all request to display message on the system console.
 * At the same time, each message are also logged under the FINE level within
 * the logging system.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class Console {

	// Private attributes
	private static boolean		isEchoOn;

	// Create a local logger
	//private static final Logger	LOGGER	= Logger.getLogger(Console.class.getName());
	private static final Logger LOGGER = LoggerFactory.getLogger(Console.class);
	
	/**
	 * Static initializor
	 */
	static {
		isEchoOn = true;
	}

	/**
	 * Return true if echo is enable and if all trace are written to the
	 * console.
	 * 
	 * @return the isEchoOn
	 */
	public static boolean isEchoOn() {
		return isEchoOn;
	}

	/**
	 * Sets the echo state (true or false).
	 * 
	 * @param isEchoOn
	 *            the isEchoOn to set
	 */
	public static void setEchoOn(boolean isEchoOn) {
		Console.isEchoOn = isEchoOn;
	}

	/**
	 * Write a message on the console.
	 * 
	 * @param message
	 *            the string to display on the console
	 */
	public static void writeLine(String message) {

		if (isEchoOn) {
			System.out.println(message);
		}

		LOGGER.info(message);
		//LOGGER.log(Level.FINE, message);
	}

	/**
	 * Write an error message on the console.
	 * 
	 * @param message
	 *            the string to display on the console
	 */
	public static void writeError(String message) {
		System.out.println(message);
		LOGGER.error(message);
		//LOGGER.log(Level.SEVERE, message);
	}

	/**
	 * Write a debug message on the log file.
	 * 
	 * @param message
	 *            the string to display on the console
	 */
	public static void writeDebug(String message) {
		LOGGER.debug(message);
		//LOGGER.log(Level.FINEST, message);
	}

	/**
	 * Write a default message (if present) on the console and read user input
	 * from the console.
	 * 
	 * @param message
	 *            the string to display on the console
	 * @return the string typed by the user
	 */
	public static String readLine(String message) {

		String inputLine = null;
		BufferedReader inputReader = null;

		try {

			// Write user message if required
			if (message.length() > 0 && isEchoOn) {
				System.out.print(message);
			}

			// Read input string
			inputReader = new BufferedReader(new InputStreamReader(System.in));
			inputLine = inputReader.readLine();
		}
		catch (IOException e) {
			Console.writeError("Console: error while reading input: " + StringUtils.toString(e));
		}

		return inputLine;
	}
}
