package ch.hesge.csim2.core.utils;

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

	/**
	 * Write a message on the console.
	 * 
	 * @param caller
	 *        the object calling console method
	 * @param message
	 *        the string to display on the console
	 */
	public static void writeInfo(Object caller, String message) {
		LoggerFactory.getLogger(caller.getClass().getName()).info(message);
	}

	/**
	 * Write a message on the console.
	 * 
	 * @param callerClass
	 *        the object calling console method
	 * @param message
	 *        the string to display on the console
	 */
	@SuppressWarnings("rawtypes")
	public static void writeInfo(Class callerClass, String message) {
		LoggerFactory.getLogger(callerClass).info(message);
	}

	/**
	 * Write an error message on the console.
	 * 
	 * @param caller
	 *        the object calling console method
	 * @param message
	 *        the string to display on the console
	 */
	public static void writeError(Object caller, String message) {
		LoggerFactory.getLogger(caller.getClass().getName()).error(message);
	}

	/**
	 * Write a message on the console.
	 * 
	 * @param callerClass
	 *        the object calling console method
	 * @param message
	 *        the string to display on the console
	 */
	@SuppressWarnings("rawtypes")
	public static void writeError(Class callerClass, String message) {
		LoggerFactory.getLogger(callerClass).error(message);
	}

	/**
	 * Write a debug message on the log file.
	 * 
	 * @param caller
	 *        the object calling console method
	 * @param message
	 *        the string to display on the console
	 */
	public static void writeDebug(Object caller, String message) {
		LoggerFactory.getLogger(caller.getClass().getName()).debug(message);
	}

	/**
	 * Write a message on the console.
	 * 
	 * @param callerClass
	 *        the object calling console method
	 * @param message
	 *        the string to display on the console
	 */
	@SuppressWarnings("rawtypes")
	public static void writeDebug(Class callerClass, String message) {
		LoggerFactory.getLogger(callerClass).debug(message);
	}

}
