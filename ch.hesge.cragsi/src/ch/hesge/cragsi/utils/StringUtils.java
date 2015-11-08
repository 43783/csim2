/*
 * Java CSV is a stream based library for reading and writing
 * CSV and other delimited data.
 * 
 * Copyright (C) Bruce Dunwiddie bruce@csvreader.com
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package ch.hesge.cragsi.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class responsible to manage string conversion.
 * 
 * Copyright HEG Geneva 2015, Switzerland
 * @author Eric Harth
 */
public class StringUtils {

	/**
	 * Convert a Throwable into its string representation. Basically, the
	 * throwable is converted with its stack-trace into a single string
	 * representation.
	 * 
	 * @param aThrowable
	 *            the Throwable to convert
	 * @return a String
	 */
	public static String toString(Throwable aThrowable) {

		Writer result = new StringWriter();
		PrintWriter printWriter = new PrintWriter(result);
		aThrowable.printStackTrace(printWriter);

		return result.toString();
	}

	public static boolean isEmtpy(String value) {
		return value == null || value.trim().length() == 0;
	}

	public static int toInteger(String value) {
		if (value == null || value.trim().length() == 0)
			return 0;
		return Integer.valueOf(value);
	}

	public static double toDouble(String value) {
		if (value == null || value.trim().length() == 0)
			return 0d;
		return Double.valueOf(value);
	}

	public static String toString(int value) {
		if (value == 0)
			return null;
		return String.valueOf(value);
	}

	public static String toString(double value) {
		if (value == 0)
			return null;
		return new DecimalFormat("#0.00").format(value);
	}

	public static String toString(Date date) {
		if (date == null)
			return null;
		return new SimpleDateFormat("yyyy-MM-dd").format(date);
	}

	public static Date toDate(String value, String format) {
		try {
			// date format: "yyyy.MM.dd"
			return new SimpleDateFormat(format).parse(value);
		}
		catch (ParseException e) {
			// Ignore exception
		}
		return null;
	}

	public static String toNumber(String value) {

		if (value == null)
			return "";

		Matcher regexMatcher = Pattern.compile("([0-9]+)").matcher(value);

		if (regexMatcher.matches()) {
			return regexMatcher.group(1);
		}

		return "";
	}
}
