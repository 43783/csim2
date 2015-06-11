package ch.hesge.csim2.core.utils;

import java.util.Map;

/**
 * This interface declare all methods provided to access information from a
 * parameter mapper. This interface allow mapping of all parameters required in
 * order to build a SQL query string field with parameter values.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public interface IParamMapper<T> {

	Map<String, Object> mapParameters(T object);
}
