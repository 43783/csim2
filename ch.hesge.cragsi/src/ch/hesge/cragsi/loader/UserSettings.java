package ch.hesge.cragsi.loader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import ch.hesge.cragsi.exceptions.ConfigurationException;

/**
 * Class responsible to manage DAO access for Account.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

@SuppressWarnings("serial")
public class UserSettings extends Properties {

	// Singleton access
	private static UserSettings uniqueInstance;

	public static synchronized UserSettings getInstance() throws ConfigurationException {

		if (uniqueInstance == null) {

			uniqueInstance = new UserSettings();

			// Load properties from cragsi.conf
			FileReader reader = null;
			try {
				reader = new FileReader("conf/craigsi.conf");
				uniqueInstance.load(reader);
			}
			catch (FileNotFoundException e) {
				throw new ConfigurationException("Unable to find file craigsi.conf");
			}
			catch (IOException e) {
				throw new ConfigurationException("Error while loading file: " + e.toString());
			}
			finally {
				if (reader != null) {
					try {
						reader.close();
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		return uniqueInstance;
	}
}
