package ch.hesge.csim2.core.utils;

import java.util.Date;

/**
 * This interface declare all methods provided to access information from a row
 * of data. Field values can be access through their names or their position.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public interface IDataRow {

	int getSize();

	boolean isNull(int fieldIndex);

	boolean isNotNull(int fieldIndex);

	Object getFieldValue(int fieldIndex);

	boolean isNull(String fieldName);

	boolean isNotNull(String fieldName);

	Object getFieldValue(String fieldName);

	boolean getBoolean(String fieldName);

	String getString(String fieldName);

	char getChar(String fieldName);

	Date getDate(String fieldName);

	float getFloat(String fieldName);

	double getDouble(String fieldName);

	byte getByte(String fieldName);

	int getInteger(String fieldName);

	long getLong(String fieldName);
}
