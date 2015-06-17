/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hesge.csim2.core.logic;

import java.util.ArrayList;
import java.util.List;

import ch.hesge.csim2.core.model.IMethodConceptMatcher;
import ch.hesge.csim2.core.utils.PluginManager;

/**
 * This class implement all logical rules associated to method/concept matching.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

class MatchingLogic {

	/**
	 * Retrieves all method concept matchers declared in configuration file.
	 * 
	 * @return
	 *         a list of IMethodConceptMatcher
	 */
	public static synchronized List<IMethodConceptMatcher> getMatchers() {

		List<IMethodConceptMatcher> matchers = new ArrayList<>();

		PluginManager<IMethodConceptMatcher> matcherManager = PluginManager.loadPlugins(IMethodConceptMatcher.class);

		// Load engines into a standard list
		for (IMethodConceptMatcher matcher : matcherManager) {
			matchers.add(matcher);
		}

		return matchers;
	}
}
