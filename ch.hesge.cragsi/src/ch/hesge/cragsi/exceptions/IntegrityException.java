package ch.hesge.cragsi.exceptions;

/**
 * Exception raised when a integrity problem appears while
 * processing information (like an account is referenced, but doesn't exist).
 * 
 * Copyright HEG Geneva 2015, Switzerland
 * @author Eric Harth
 */
@SuppressWarnings("serial")
public class IntegrityException extends Exception {

	public IntegrityException() {
	}

	public IntegrityException(String message) {
		super(message);
	}

	public IntegrityException(Throwable throwable) {
		super(throwable);
	}

	public IntegrityException(String message, Throwable throwable) {
		super(message, throwable);
		// TODO Auto-generated constructor stub
	}

	public IntegrityException(String message, Throwable throwable, boolean arg1, boolean arg2) {
		super(message, throwable, arg1, arg2);
	}

}
