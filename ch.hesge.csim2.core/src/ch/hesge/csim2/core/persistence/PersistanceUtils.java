package ch.hesge.csim2.core.persistence;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This class is a generic utility class for objects.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class PersistanceUtils {

	/**
	 * Convert a String array into a single String
	 * 
	 * @param aStringArray
	 *        the array to convert
	 * @return a single String
	 */
	public static boolean isNewObject(Object o) {

		try {

			Method method = o.getClass().getMethod("getKeyId");

			if (method != null) {
				Object result = method.invoke(o);
				int keyId = Integer.valueOf(result.toString());
				return keyId == 0;
			}
		}
		catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// Do nothing
		}

		return false;
	}
}
