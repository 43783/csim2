package ch.hesge.csim2.core.model;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * This interface represents a csim2 matcher used to calculate
 * a list of all concepts associated to a single method.
 *  
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public interface IMethodConceptMatcher extends IEngine {

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
	
	// Matcher information retrieval
	Map<Integer, List<MethodConceptMatch>> getMethodMatchingMap();	
}
