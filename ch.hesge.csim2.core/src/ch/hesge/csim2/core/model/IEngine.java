package ch.hesge.csim2.core.model;

import java.util.Properties;

/**
 * This interface represents a csim2 engine runnable within the shell console.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public interface IEngine {

	// Engine attributes
	String getName();
	String getVersion();
	String getDescription();
	Properties getParameters();

	// Engine context
	Context getContext();
	void setContext(Context context);

	// Engine control
	void init();
	void start();
	void stop();
}
