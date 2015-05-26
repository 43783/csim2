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
	 * Retrieve all MethodConcetMatch instances in projects
	 * and for each one populate their classes, methods and concepts.
	 * 
	 * @return a list of MethodConceptMatch
	 */
	public static List<MethodConceptMatch> getMatchingsWithDependencies(Project project) {

		Map<Integer, SourceClass> classMap   = ApplicationLogic.getSourceClassMap(project);
		Map<Integer, SourceMethod> methodMap = ApplicationLogic.getSourceMethodMap(project);
		Map<Integer, Concept> conceptMap     = ApplicationLogic.getConceptMap(project);

		List<MethodConceptMatch> matchings = MethodConceptMatchDao.findByProject(project);

		// Retrieve class, method and concept for each match
		for (MethodConceptMatch match : matchings) {
			SourceMethod sourceMethod = methodMap.get(match.getSourceMethodId());
			match.setSourceClass(classMap.get(sourceMethod.getClassId()));
			match.setSourceMethod(sourceMethod);
			match.setConcept(conceptMap.get(match.getConceptId()));
		}

		return matchings;
	}

	/**
	 * Retrieve a map of all MethodConceptMatch classified by method Id.
	 * 
	 * @return
	 *         a map of (MethodId, List<MethodConceptMatch>)
	 */
	public static Map<Integer, List<MethodConceptMatch>> getMethodMatchingMap(Project project) {

		Map<Integer, List<MethodConceptMatch>> matchingMap = new HashMap<>();
		List<MethodConceptMatch> matchings = MatchingLogic.getMatchingsWithDependencies(project);

		for (MethodConceptMatch match : matchings) {

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
	 * Retrieve a map of all MethodConceptMatch classified by concept Id.
	 * 
	 * @return
	 *         a map of (ConceptId, List<MethodConceptMatch>)
	 */
	public static Map<Integer, List<MethodConceptMatch>> getConceptMatchingMap(Project project) {

		Map<Integer, List<MethodConceptMatch>> matchingMap = new HashMap<>();
		List<MethodConceptMatch> matchings = MatchingLogic.getMatchingsWithDependencies(project);

		for (MethodConceptMatch match : matchings) {

			// Create an associated array if missing
			if (!matchingMap.containsKey(match.getConceptId())) {
				matchingMap.put(match.getConceptId(), new ArrayList<>());
			}

			// Add the match to the array for the specific concept
			matchingMap.get(match.getConceptId()).add(match);
		}

		return matchingMap;
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
