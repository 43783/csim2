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

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 */
public class StringUtils {

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

	public static Date fromString(String value) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd").parse(value);
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
