package ch.hesge.csim2.core.utils;

/**
 * This interface allow mapping of a database row to a JavaBean object filled
 * with attributes available within the row.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public interface IRowMapper<T> {

	T mapRow(IDataRow row);
}
