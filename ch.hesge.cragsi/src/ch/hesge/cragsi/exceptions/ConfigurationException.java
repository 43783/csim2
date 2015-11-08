package ch.hesge.cragsi.exceptions;

@SuppressWarnings("serial")
public class ConfigurationException extends Exception {

	public ConfigurationException() {
	}

	public ConfigurationException(String message) {
		super(message);
	}

	public ConfigurationException(Throwable throwable) {
		super(throwable);
	}

	public ConfigurationException(String message, Throwable throwable) {
		super(message, throwable);
		// TODO Auto-generated constructor stub
	}

	public ConfigurationException(String message, Throwable throwable, boolean arg1, boolean arg2) {
		super(message, throwable, arg1, arg2);
	}

}
