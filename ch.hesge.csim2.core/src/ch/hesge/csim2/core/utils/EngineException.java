/**
 * 
 */
package ch.hesge.csim2.core.utils;

/**
 * Csim2 engine exception. Represents an exception while initialize or running
 * an Csim2 engine.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class EngineException extends RuntimeException {

	// Private attributes
	private String				message;
	private static final long	serialVersionUID	= -2468728461814072244L;

	/**
	 * Default constructor
	 */
	public EngineException(String message) {
		this.message = message;
	}

	/**
	 * Retrieve exception message
	 */
	public String getMessage() {
		return message;
	}

}
