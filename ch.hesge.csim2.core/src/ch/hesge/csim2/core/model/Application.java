package ch.hesge.csim2.core.model;

import java.util.Properties;

import ch.hesge.csim2.core.logic.ApplicationLogic;

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
		return ApplicationLogic.getVersion();
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
