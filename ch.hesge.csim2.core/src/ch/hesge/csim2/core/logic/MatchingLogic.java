/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hesge.csim2.core.logic;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.hesge.csim2.core.model.IMethodConceptMatcher;
import ch.hesge.csim2.core.model.MethodConceptMatch;
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

	/**
	 * Export all matchings passed in argument in a CSV file.
	 * 
	 * @param matchings
	 * @param filename
	 */
	public static void exportMatchings(Map<Integer, List<MethodConceptMatch>> matchMap, String filename) {

		if (matchMap != null && filename != null) {
			
			try {
				FileWriter writer = new FileWriter(filename + ".csv");
				writer.append("Class,Method,Concept,Weight,Concept Stems, Method Stems\n");

				for (Integer matchKey : matchMap.keySet()) {
					for (MethodConceptMatch match : matchMap.get(matchKey)) {

						writer.append(match.getSourceClass().getName() + ",");
						writer.append(match.getSourceMethod().getSignature() + ",");
						writer.append(match.getConcept().getName() + ",");
						writer.append(match.getWeight() + ",");
						writer.append("n/a,");
						writer.append("n/a,");
					}
				}

				writer.flush();
				writer.close();
			}
			catch (IOException e) {
				// Do nothing
			}
		}
	}
}
