package ch.hesge.csim2.core.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This class is a generic utility class for objects.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class DaoUtils {

	/**
	 * Check if a business object is already persisted to database.
	 * 
	 * @param o
	 *        the object to test
	 * @return true or false
	 */
	public static boolean isNewObject(Object o) {

		try {

			Method method = o.getClass().getMethod("getKeyId");

			if (method != null) {
				Object result = method.invoke(o);
				int keyId = Integer.valueOf(result.toString());
				return keyId == 0 || keyId == -1;
			}
		}
		catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// Do nothing
		}

		return false;
	}
}
