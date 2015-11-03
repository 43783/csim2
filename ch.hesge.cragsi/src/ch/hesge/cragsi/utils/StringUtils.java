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

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 */
public class StringUtils {
	
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
		return String.valueOf(value);
	}

	public static String toString(double value) {
		return String.valueOf(value);
	}

	public static String toString(Date date) {
		if (date == null)
			return null;
		return new SimpleDateFormat("yyyy-mm-dd").format(date);
	}
}
