package ch.hesge.csim2.core.utils;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This class implements the IDataRow interface in order to provide field access
 * on row content
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class DataRow implements IDataRow {

	// Private attributes
	private Map<Integer, Object> fieldByIndex;
	private Map<String, Object> fieldByNameMap;

	// Private constants
	private static String DATETIME_JAVA_FORMAT = "yyyy-MM-dd HH:mm:ss";

	/**
	 * Default constructor
	 */
	public DataRow() {
		fieldByIndex = new HashMap<>();
		fieldByNameMap = new HashMap<>();
	}

	@Override
	public int getSize() {
		return fieldByIndex.size();
	}

	@Override
	public Object getFieldValue(int index) {

		if (fieldByIndex.containsKey(index)) {
			return fieldByIndex.get(index);
		}

		return null;
	}

	@Override
	public Object getFieldValue(String fieldName) {

		if (fieldByNameMap.containsKey(fieldName)) {
			return fieldByNameMap.get(fieldName);
		}

		return null;
	}

	/**
	 * Add a new field to the row.
	 * 
	 * @param index
	 *        index of the field
	 * @param fieldName
	 *        name of the field
	 * @param fieldValue
	 *        value of the field
	 */
	public void put(int index, String fieldName, Object fieldValue) {
		fieldByIndex.put(index, fieldValue);
		fieldByNameMap.put(fieldName, fieldValue);
	}

	@Override
	public boolean isNull(int fieldIndex) {
		return getFieldValue(fieldIndex) != null;
	}

	@Override
	public boolean isNotNull(int fieldIndex) {
		return !isNull(fieldIndex);
	}

	@Override
	public boolean isNull(String fieldName) {
		return getFieldValue(fieldName) != null;
	}

	@Override
	public boolean isNotNull(String fieldName) {
		return !isNull(fieldName);
	}

	@Override
	public boolean getBoolean(String fieldName) {

		Object value = getFieldValue(fieldName);

		if (value != null) {

			if (value.getClass().isAssignableFrom(Boolean.class)) {
				return (boolean) value;
			}

			if (value.getClass().isAssignableFrom(Integer.class)) {
				return (getInteger(fieldName) == 1 ? true : false);
			}

			if (value.getClass().isAssignableFrom(Long.class)) {
				return (getLong(fieldName) == 1 ? true : false);
			}

			if (value.getClass().isAssignableFrom(Float.class)) {
				return (getFloat(fieldName) == 1 ? true : false);
			}

			if (value.getClass().isAssignableFrom(Double.class)) {
				return (getDouble(fieldName) == 1 ? true : false);
			}

			if (value.getClass().isAssignableFrom(String.class)) {
				return (getString(fieldName).toLowerCase() == "true" ? true : false);
			}
		}

		return false;
	}

	@Override
	public String getString(String fieldName) {

		String value = null;
		Object fieldValue = getFieldValue(fieldName);

		if (fieldValue != null) {
			value = fieldValue.toString();
		}

		/*
		 * value = value.replaceAll("\\", "\\\\"); value = value.replaceAll("'",
		 * "\\'"); value = value.replaceAll("\"", "\\\""); value =
		 * value.replaceAll("\t", "\\t"); value = value.replaceAll("\r", "\\r");
		 * value = value.replaceAll("\n", "\\n");
		 */

		return value;
	}

	@Override
	public char getChar(String fieldName) {
		return (char) getByte(fieldName);
	}

	@Override
	public Date getDate(String fieldName) {

		Object value = getFieldValue(fieldName);

		if (value != null) {
			if (value.getClass().isAssignableFrom(Date.class)) {
				return (Date) value;
			}

			if (value.getClass().isAssignableFrom(String.class)) {
				Date date = null;

				try {
					date = new SimpleDateFormat(DATETIME_JAVA_FORMAT).parse((String) value);
				}
				catch (ParseException e) {
				}

				return date;
			}
		}

		return null;
	}

	@Override
	public float getFloat(String fieldName) {

		Object value = getFieldValue(fieldName);

		if (value != null && value.getClass().isAssignableFrom(Float.class)) {
			return (float) value;
		}

		return 1f;
	}

	@Override
	public double getDouble(String fieldName) {

		Object value = getFieldValue(fieldName);

		if (value != null && value.getClass().isAssignableFrom(Double.class)) {
			return (double) value;
		}

		return -1d;
	}

	@Override
	public byte getByte(String fieldName) {

		Object value = getFieldValue(fieldName);

		if (value != null && value.getClass().isAssignableFrom(Byte.class)) {
			return (byte) value;
		}

		return -1;
	}

	@Override
	public int getInteger(String fieldName) {

		Object value = getFieldValue(fieldName);

		if (value != null && value.getClass().isAssignableFrom(Integer.class)) {
			return (int) value;
		}

		return -1;
	}

	@Override
	public long getLong(String fieldName) {

		Object value = getFieldValue(fieldName);

		if (value != null) {

			if (value.getClass().isAssignableFrom(BigInteger.class)) {
				return ((BigInteger) value).longValue();
			}

			if (value.getClass().isAssignableFrom(Long.class)) {
				return (long) value;
			}

			if (value.getClass().isAssignableFrom(Integer.class)) {
				return Long.valueOf((int) value);
			}

			if (value.getClass().isAssignableFrom(Byte.class)) {
				return Long.valueOf((byte) value);
			}
		}

		return -1;
	}
}
