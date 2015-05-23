package ch.hesge.csim2.core.persistence;

import java.util.Map;
import java.util.regex.Matcher;

/**
 * This class implements various helper to build a single SQL query as a string.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class QueryBuilder {

	/**
	 * Build a query by replacing all parameter occurences, within the query
	 * string passed in argument, by the value passed in argument.
	 * 
	 * @param queryString
	 *            the original query string
	 * @param paramName
	 *            the parameters to look for, within the query
	 * @param paramValue
	 *            the value to use during the substitution
	 * @return a new query mapped with value
	 */
	public static String create(String queryString, String paramName, Object paramValue) {
		String paramTag = "\\?" + paramName;
		String cleanedParamValue = String.valueOf(paramValue).replace("'", "'' ");
		return queryString.replaceAll(paramTag, Matcher.quoteReplacement(cleanedParamValue));
	}

	/**
	 * Build a query by replacing all parameters within the query string, by the
	 * values contained in a map of parameters.
	 * 
	 * @param queryString
	 *            the original query string
	 * @param paramMap
	 *            the map of parameter of name/value
	 * @return the new query string mapped with values
	 */
	public static String create(String queryString, Map<String, Object> paramMap) {

		for (String paramName : paramMap.keySet()) {
			queryString = QueryBuilder.create(queryString, paramName, paramMap.get(paramName));
		}

		return queryString;
	}

	/**
	 * Build a query by replacing all parameters within the query string, by the
	 * values provided by the mapper passed in argument.
	 * 
	 * @param queryString
	 *            the original query string
	 * @param mapper
	 *            an object implementing the 'T mapRow(IDataRow row)' method
	 * @param paramObject
	 *            the object containing parameter values
	 * @return the new query string mapped with values
	 */
	public static <T> String create(String queryString, IParamMapper<T> mapper, T paramObject) {
		Map<String, Object> map = mapper.mapParameters(paramObject);
		return QueryBuilder.create(queryString, map);
	}
}
