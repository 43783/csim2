package ch.hesge.cragsi.utils;

import ch.hesge.cragsi.exceptions.ConfigurationException;
import ch.hesge.cragsi.loader.UserSettings;

/**
 * Class responsible to manage properties from configuration file.
 * 
 * Copyright HEG Geneva 2015, Switzerland
 * @author Eric Harth
 */
public class PropertyUtils {

	/**
	 * Retrieve a property from configuration file.
	 * 
	 * @param name
	 *        the property name
	 * @return the value found
	 * @throws ConfigurationException
	 */
	public static String getProperty(String name) throws ConfigurationException {

		String propertyValue = UserSettings.getInstance().getProperty(name);

		if (propertyValue == null) {
			throw new ConfigurationException("==> missing property '" + name + "' in configuration file !");
		}

		return propertyValue;
	}

}
