/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hesge.csim2.core.logic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.hesge.csim2.core.model.IMethodConceptMatcher;
import ch.hesge.csim2.core.model.MethodConceptMatch;
import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.utils.Console;
import ch.hesge.csim2.core.utils.FileUtils;
import ch.hesge.csim2.core.utils.PluginManager;
import ch.hesge.csim2.core.utils.StringUtils;

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
	 * Retrieve all matchings between a method and a concept.
	 * 
	 * @param project
	 *        the project to analyse
	 * @param matcher
	 *        the matcher to use to compute matching
	 * @return
	 *         a map of (MethodId, List<MethodConceptMatch>)
	 */
	public static Map<Integer, List<MethodConceptMatch>> getMethodMatchingMap(Project project, IMethodConceptMatcher matcher) {
		return matcher.getMethodMatchingMap(project);
	}

	/**
	 * Export all matchings passed in argument in a CSV file.
	 * 
	 * @param matchings
	 *        the MethodConceptMatch to save
	 * @param filename
	 *        the csv filename target
	 */
	public static void exportMatchings(Map<Integer, List<MethodConceptMatch>> matchMap, String filename) {

		if (matchMap != null && filename != null) {

			FileOutputStream fileStream = null;

			try {

				if (FileUtils.exists(filename)) {
					Files.delete(Paths.get(filename));
				}
				
				String fieldSeparator = ";";

				// Create a file writer (UTF8 support)
				fileStream = new FileOutputStream(new File(filename));
				OutputStreamWriter writer = new OutputStreamWriter(fileStream, "UTF-8");				
				
				writer.append("Class" + fieldSeparator + "Method" + fieldSeparator + "Concept" + fieldSeparator + "Weight" + fieldSeparator + "Validated" + fieldSeparator + "Stems\n");

				for (Integer matchKey : matchMap.keySet()) {
					for (MethodConceptMatch match : matchMap.get(matchKey)) {

						writer.append(match.getSourceClass().getName() + fieldSeparator);
						writer.append(match.getSourceMethod().getSignature() + fieldSeparator);
						writer.append(match.getConcept().getName() + fieldSeparator);
						writer.append(match.getWeight() + fieldSeparator);
						writer.append(match.isValidated() + fieldSeparator);

						String stems = "";
						Set<String> matchingTerms = StemLogic.getTermIntersection(match.getStemConcepts(), match.getStemMethods());
						
						for (String stem : matchingTerms) {
							stems += stem + ",";
						}
						
						stems = StringUtils.removeTrailString(stems, ",");
						writer.append(stems);

						writer.append("\n");
					}
				}

				writer.flush();
				writer.close();
			}
			catch (Exception e) {

				if (fileStream != null) {
					try {
						fileStream.close();
					}
					catch (IOException e1) {
						// Close silently
					}
				}
				
				Console.writeError(MatchingLogic.class, "an unexpected error has occured: " + StringUtils.toString(e));
			}
		}
	}
}
