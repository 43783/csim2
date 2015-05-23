/**
 * 
 */
package ch.hesge.csim2.core.utils;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Custom java.util.logging formatter used to log event to a file.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class LogFormatter extends Formatter {

	/**
	 * Default constructor
	 */
	public LogFormatter() {
	}

	/**
	 * Format a record and return a string
	 * 
	 * @see java.util.logging.Formatter#format(java.util.logging.LogRecord)
	 */
	@Override
	public String format(LogRecord record) {

		// Create a StringBuffer to contain the formatted
		// record start with the date.
		StringBuffer sb = new StringBuffer();

		// Get the time from the LogRecord
		Date timestamp = new Date(record.getMillis());
		sb.append(String.format("%1$td.%1$tm.%1$tY-%1$tH:%1$tM:%1$tS ", timestamp));

		// Get the level
		sb.append(String.format("%s: ", record.getLevel().getName()));

		// Get the formatted message (includes localization
		// and substitution of parameters) and add it to the buffer
		sb.append(formatMessage(record));
		sb.append("\n");

		return sb.toString();
	}
}
