package ch.hesge.csim2.core.model;

import java.util.List;
import java.util.Map;

/**
 * This interface represents a csim2 matcher used to calculate,
 * for all methods a list of associated concept.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public interface IMethodConceptMatcher {

	// Attributes
	String getName();

	String getVersion();

	String getDescription();

	// Matcher information retrieval
	Map<Integer, List<MethodConceptMatch>> getMethodMatchingMap(Project project, float threshold);
}
