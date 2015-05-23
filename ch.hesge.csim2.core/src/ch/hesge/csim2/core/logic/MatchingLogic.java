/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hesge.csim2.core.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.hesge.csim2.core.dao.MethodConceptMatchDao;
import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.MethodConceptMatch;
import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.SourceClass;
import ch.hesge.csim2.core.model.SourceMethod;
import ch.hesge.csim2.core.persistence.PersistanceUtils;

/**
 * This class implement all logical rules associated to method/concept matching.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

class MatchingLogic {

	/**
	 * Retrieve all MethodConcetMatch instances in projects.
	 * 
	 * @return a list of MethodConceptMatch
	 */
	public static List<MethodConceptMatch> getMatchings(Project project) {
		return MethodConceptMatchDao.findByProject(project);
	}

	/**
	 * Retrieve all map of all MethodConceptMatch classified by method Id.
	 * 
	 * @return
	 *         a map of (MethodId, List<MethodConceptMatch>)
	 */
	public static Map<Integer, List<MethodConceptMatch>> getMatchingMap(Project project) {

		Map<Integer, List<MethodConceptMatch>> matchingMap = new HashMap<>();

		List<MethodConceptMatch> matchings = MethodConceptMatchDao.findByProject(project);

		Map<Integer, Concept> conceptMap = ApplicationLogic.getConceptMap(project);
		Map<Integer, SourceMethod> methodMap = ApplicationLogic.getSourceMethodMap(project);
		Map<Integer, SourceClass> classMap = ApplicationLogic.getSourceClassMap(project);

		// Populate each match with concept, class and method
		for (MethodConceptMatch match : matchings) {

			// Populate dependencies
			populateDependencies(match, conceptMap, classMap, methodMap);

			// Create an associated array if missing
			if (!matchingMap.containsKey(match.getSourceMethodId())) {
				matchingMap.put(match.getSourceMethodId(), new ArrayList<>());
			}

			// Add the match to the array for the specific method
			matchingMap.get(match.getSourceMethodId()).add(match);
		}

		return matchingMap;
	}

	/**
	 * Retrieve all matching with its dependencies SourceMethod and Concept.
	 * 
	 * @return a list of MethodConceptMatch
	 */
	public static List<MethodConceptMatch> getMatchingsWithDependencies(Project project) {

		List<MethodConceptMatch> matchings = ApplicationLogic.getMatchings(project);

		Map<Integer, Concept> conceptMap = ApplicationLogic.getConceptMap(project);
		Map<Integer, SourceMethod> methodMap = ApplicationLogic.getSourceMethodMap(project);
		Map<Integer, SourceClass> classMap = ApplicationLogic.getSourceClassMap(project);

		// Populate each match with concept, class and method
		for (MethodConceptMatch match : matchings) {
			populateDependencies(match, conceptMap, classMap, methodMap);
		}

		return matchings;
	}

	/**
	 * Populate a MethodConceptMatch with the following dependencies:
	 * 
	 * - its concept
	 * - its class
	 * - its method
	 * 
	 * @param match
	 *        the MethodConceptMatch to populate
	 * @param conceptMap
	 *        the map of all source classes
	 * @param classMap
	 *        the map of all source classes
	 * @param methodMap
	 *        the map of all source methods
	 */
	private static void populateDependencies(MethodConceptMatch match, Map<Integer, Concept> conceptMap, Map<Integer, SourceClass> classMap, Map<Integer, SourceMethod> methodMap) {

		Concept concept = conceptMap.get(match.getConceptId());
		SourceMethod sourceMethod = methodMap.get(match.getSourceMethodId());
		SourceClass sourceClass = classMap.get(sourceMethod.getClassId());

		match.setConcept(concept);
		match.setSourceClass(sourceClass);
		match.setSourceMethod(sourceMethod);
	}

	/**
	 * Delete all MethodConcetMatch instances owned by a project.
	 * 
	 * @param project
	 *        the owner
	 */
	public static void deleteMatching(Project project) {
		MethodConceptMatchDao.deleteByProject(project);
	}

	/**
	 * Save a single MethodConcetMatch.
	 * 
	 * @param match
	 *        the MethodConcetMatch to save
	 */
	public static void saveMatching(MethodConceptMatch match) {

		if (PersistanceUtils.isNewObject(match)) {
			MethodConceptMatchDao.add(match);
		}
		else {
			MethodConceptMatchDao.add(match);
		}
	}
}
