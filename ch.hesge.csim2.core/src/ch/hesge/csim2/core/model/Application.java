package ch.hesge.csim2.core.model;

import java.util.Properties;

/**
 * Representing application state and all related properties.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class Application {

	// Private attributes
	private Project project;
	private Properties properties;

	public static String VERSION = "1.3.24.20150726";
	
	/**
	 * Default constructor
	 */
	public Application() {
		this.properties = new Properties();
	}

	/**
	 * Return the properties associated to the application environment.
	 * All properties can be accessed through predefined constant declared in
	 * AppConstants.
	 * 
	 * @return an properties object
	 */
	public Properties getProperties() {
		return properties;
	}

	/**
	 * Return the application version
	 * 
	 * @return a string containing the version
	 */
	public String getVersion() {
		return VERSION;
	}

	/**
	 * Get the current active project.
	 * 
	 * @return the current active project
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * Set the current active project.
	 * 
	 * @param project
	 *        the new current project
	 */
	public void setProject(Project project) {
		this.project = project;
	}
}
