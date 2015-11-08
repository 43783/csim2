package ch.hesge.cragsi.utils;

import ch.hesge.cragsi.exceptions.ConfigurationException;
import ch.hesge.cragsi.loader.UserSettings;

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
