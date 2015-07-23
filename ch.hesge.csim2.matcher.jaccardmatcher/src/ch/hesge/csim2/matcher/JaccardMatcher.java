/**
 * 
 */
package ch.hesge.csim2.matcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.hesge.csim2.core.logic.ApplicationLogic;
import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.IMethodConceptMatcher;
import ch.hesge.csim2.core.model.MethodConceptMatch;
import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.SourceMethod;
import ch.hesge.csim2.core.model.StemConcept;
import ch.hesge.csim2.core.model.StemMethod;

/**
 * This engine allow matching calculation based
 * on the Jaccard similarity measure.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 *
 */
public class JaccardMatcher implements IMethodConceptMatcher {

	// Private attributes
	private ApplicationLogic applicationLogic;

	/**
	 * Default constructor
	 */
	public JaccardMatcher() {
		applicationLogic = ApplicationLogic.UNIQUE_INSTANCE;
	}

	/**
	 * Get the engine name.
	 * @see ch.hesge.csim2.core.shell.IEngine#getName()
	 */
	@Override
	public String getName() {
		return "JaccardMatcher*";
	}

	/**
	 * Get the engine version.
	 * @see ch.hesge.csim2.core.shell.IEngine#getVersion()
	 */
	@Override
	public String getVersion() {
		return "1.0.18";
	}

	/**
	 * Get the engine description
	 * @see ch.hesge.csim2.core.shell.IEngine#getDescription()
	 */
	@Override
	public String getDescription() {
		return "method concept matcher based on jaccard comparison.";
	}

	/**
	 * Retrieve a map of all MethodConceptMatch classified by method Id.
	 * 
	 * @param project
	 *        the project where to calculate matching
	 * @return
	 *         a map of (MethodId, List<MethodConceptMatch>)
	 */
	public Map<Integer, List<MethodConceptMatch>> getMethodMatchingMap(Project project) {

		List<MethodConceptMatch> matchings = new ArrayList<>();

		// Load concepts, methods and stems data
		Map<Integer, SourceMethod> methodMap = applicationLogic.getSourceMethodMap(project);
		Map<Integer, Concept> conceptMap     = applicationLogic.getConceptMap(project);
		Map<Integer, StemMethod> stemMethodTreeMap   = applicationLogic.getStemMethodTreeMap(project);
		Map<Integer, StemConcept> stemConceptTreeMap = applicationLogic.getStemConceptTreeMap(project);;
		
		double maxWeight = 0d;

		// Scan all method and lookup for matching concepts
		for (SourceMethod method : methodMap.values()) {
			for (Concept concept : conceptMap.values()) {

				List<StemMethod> matchingMethodStems = new ArrayList<>();
				List<StemConcept> matchingConceptStems = new ArrayList<>();

				double similarity = computeSimilarity(method, concept, stemMethodTreeMap, stemConceptTreeMap, matchingMethodStems, matchingConceptStems);

				// Register result within the matchMap
				if (similarity > 0d) {

					MethodConceptMatch match = new MethodConceptMatch();

					match.setProject(project);
					match.setSourceClass(method.getSourceClass());
					match.setSourceMethod(method);
					match.setConcept(concept);
					match.setWeight(similarity);
					match.getStemMethods().addAll(matchingMethodStems);
					match.getStemConcepts().addAll(matchingConceptStems);

					matchings.add(match);
					maxWeight = Math.max(maxWeight, similarity);
				}
			}
		}

		Map<Integer, List<MethodConceptMatch>> matchingMap = new HashMap<>();

		// Now, aggregate all matchings by method
		for (MethodConceptMatch match : matchings) {

			// Create an method list if not already initialized
			if (!matchingMap.containsKey(match.getSourceMethod().getKeyId())) {
				matchingMap.put(match.getSourceMethod().getKeyId(), new ArrayList<>());
			}

			// Add the match to the array for the specific method
			matchingMap.get(match.getSourceMethod().getKeyId()).add(match);
		}

		return matchingMap;
	}

	/**
	 * Compute similarity between a method and a concept.
	 * 
	 * @param method
	 * @param concept
	 * @param stemMethodTreeMap
	 * @param stemConceptTreeMap
	 * @param matchingMethodStems
	 * @param matchingConceptStems
	 * @return a similarity weight
	 */
	private double computeSimilarity(SourceMethod method, Concept concept, Map<Integer, StemMethod> stemMethodTreeMap, Map<Integer, StemConcept> stemConceptTreeMap, List<StemMethod> matchingMethodStems, List<StemConcept> matchingConceptStems) {

		// Retrieve method terms
		StemMethod methodRootStem = stemMethodTreeMap.get(method.getKeyId());
		List<StemMethod> methodStems = applicationLogic.inflateStemMethods(methodRootStem);
		
		// Retrieve concept terms
		StemConcept conceptRootStem = stemConceptTreeMap.get(concept.getKeyId());
		List<StemConcept> conceptStems = applicationLogic.inflateStemConcepts(conceptRootStem);

		// Retrieve intersecting terms
		Set<String> intersectionTerms = new HashSet<>();
		for (StemMethod stem : methodStems) intersectionTerms.add(stem.getTerm());
		List<String> conceptTerms = new ArrayList<>();
		for (StemConcept stem : conceptStems) conceptTerms.add(stem.getTerm());
		intersectionTerms.retainAll(conceptTerms);
		
		// Retrieve intersecting method stems
		for (StemMethod stem : methodStems) {
			if (intersectionTerms.contains(stem.getTerm())) {
				matchingMethodStems.add(stem);
			}
		}
		
		// Retrieve intersecting concept stems
		for (StemConcept stem : conceptStems) {
			if (intersectionTerms.contains(stem.getTerm())) {
				matchingConceptStems.add(stem);
			}
		}
		
		// Compute jaccard indice
		int intersectionSize = intersectionTerms.size();
		int totalTermCount = methodStems.size() + conceptStems.size();
		double similarity = ((double) 2 * intersectionSize / totalTermCount);
		
		return similarity;
	}
}
