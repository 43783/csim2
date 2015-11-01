package ch.hesge.cragsi.loader;


/**
 * Class responsible to manage DAO access for Account.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class UserSettings {

	// Private attributes
	private String accountPath  = "D:/projects/cragsi/files/accounts.csv";
	private String projectPath  = "D:/projects/cragsi/files/projects.csv";
	private String activityPath = "D:/projects/cragsi/files/fdc.csv";
	private String outputPath   = "D:/projects/cragsi/files/output.csv";

	// Singleton access
	private static UserSettings uniqueInstance;

	public static synchronized UserSettings getInstance() {

		if (uniqueInstance == null) {
			uniqueInstance = new UserSettings();
		}

		return uniqueInstance;
	}

	public String getAccountPath() {
		return accountPath;
	}

	public void setAccountPath(String accountPath) {
		this.accountPath = accountPath;
	}

	public String getProjectPath() {
		return projectPath;
	}

	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}

	public String getActivityPath() {
		return activityPath;
	}

	public void setActivityPath(String activityPath) {
		this.activityPath = activityPath;
	}

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	public static UserSettings getUniqueInstance() {
		return uniqueInstance;
	}

	public static void setUniqueInstance(UserSettings uniqueInstance) {
		UserSettings.uniqueInstance = uniqueInstance;
	}
}
