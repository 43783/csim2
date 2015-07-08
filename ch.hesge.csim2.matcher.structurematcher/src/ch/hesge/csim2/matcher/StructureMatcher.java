/**
 * 
 */
package ch.hesge.csim2.matcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.hesge.csim2.core.logic.ApplicationLogic;
import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.IMethodConceptMatcher;
import ch.hesge.csim2.core.model.MethodConceptMatch;
import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.SourceMethod;
import ch.hesge.csim2.core.model.StemConcept;
import ch.hesge.csim2.core.model.StemConceptType;
import ch.hesge.csim2.core.model.StemMethod;
import ch.hesge.csim2.core.model.StemMethodType;

/**
 * This engine allow trace files generated by instrumentation to be loaded into
 * database.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 *
 */
public class StructureMatcher implements IMethodConceptMatcher {

	// Private attributes
	private Map<Integer, Concept> conceptMap;
	private Map<Integer, SourceMethod> methodMap;
	private Map<Integer, StemConcept> stemConceptTreeMap;
	private Map<Integer, StemMethod> stemMethodTreeMap;

	private List<StemMethod> matchingMethodStems;
	private List<StemConcept> matchingConceptStems;

	/**
	 * Default constructor
	 */
	public StructureMatcher() {
		matchingMethodStems = new ArrayList<>();
		matchingConceptStems = new ArrayList<>();
	}

	/**
	 * Get the engine name.
	 * @see ch.hesge.csim2.core.shell.IEngine#getName()
	 */
	@Override
	public String getName() {
		return "StructureMatcher";
	}

	/**
	 * Get the engine version.
	 * @see ch.hesge.csim2.core.shell.IEngine#getVersion()
	 */
	@Override
	public String getVersion() {
		return "1.0.1";
	}

	/**
	 * Get the engine description
	 * @see ch.hesge.csim2.core.shell.IEngine#getDescription()
	 */
	@Override
	public String getDescription() {
		return "structure method concept matcher.";
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

		// Load concept, method and stem data
		conceptMap = ApplicationLogic.getConceptMap(project);
		methodMap = ApplicationLogic.getSourceMethodMap(project);
		stemConceptTreeMap = ApplicationLogic.getStemConceptTreeMap(project);
		stemMethodTreeMap = ApplicationLogic.getStemMethodTreeMap(project);

		// Scan all method and lookup for matching concepts
		for (SourceMethod method : methodMap.values()) {
			for (Concept concept : conceptMap.values()) {

				matchingMethodStems.clear();
				matchingConceptStems.clear();

				double similarity = getMethodConceptSimilarity(method, concept);

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
				}
			}
		}

		// Retrieve max weight
		double maxWeight = 0d;
		for (MethodConceptMatch match : matchings) {
			maxWeight = Math.max(maxWeight, match.getWeight());
		}

		Map<Integer, List<MethodConceptMatch>> matchingMap = new HashMap<>();

		// Now build a map of concept matching, classified by method id
		for (MethodConceptMatch match : matchings) {

			// Normalize all weights
			match.setWeight(match.getWeight() / maxWeight);

			if (match.getWeight() > 0.001) {

				// Create an method list if not already initialized
				if (!matchingMap.containsKey(match.getSourceMethod().getKeyId())) {
					matchingMap.put(match.getSourceMethod().getKeyId(), new ArrayList<>());
				}

				// Add the match to the array for the specific method
				matchingMap.get(match.getSourceMethod().getKeyId()).add(match);
			}
		}

		return matchingMap;
	}

	/**
	 * Compute similarity between a method and a concept.
	 * 
	 * @param method
	 * @param concept
	 * @return a similarity weight
	 */
	private double getMethodConceptSimilarity(SourceMethod method, Concept concept) {

		double weight = 0d;

		StemMethod methodRootStem = stemMethodTreeMap.get(method.getKeyId());
		List<StemMethod> methodStems = ApplicationLogic.inflateStemMethods(methodRootStem);

		StemConcept conceptRootStem = stemConceptTreeMap.get(concept.getKeyId());
		List<StemConcept> conceptStems = ApplicationLogic.inflateStemConcepts(conceptRootStem);

		// Build a map of all method term
		Map<String, List<StemConcept>> conceptTermMap = new HashMap<>();
		for (StemConcept stem : conceptStems) {

			if (!conceptTermMap.containsKey(stem.getTerm())) {
				conceptTermMap.put(stem.getTerm(), new ArrayList<>());
			}

			conceptTermMap.get(stem.getTerm()).add(stem);
		}

		// Loop over all concepts referring a term
		for (StemMethod methodStem : methodStems) {

			StemMethodType methodStemType = methodStem.getStemType();

			if (conceptTermMap.containsKey(methodStem.getTerm())) {

				for (StemConcept conceptStem : conceptTermMap.get(methodStem.getTerm())) {

					StemConceptType conceptStemType = conceptStem.getStemType();

					// Evaluate full matching for parameter and reference types
					if (methodStemType == StemMethodType.PARAMETER_TYPE_FULL || methodStemType == StemMethodType.REFERENCE_TYPE_FULL) {

						if (conceptStemType == StemConceptType.CLASS_NAME_FULL) {
							weight += 1d;
							matchingMethodStems.add(methodStem);
							matchingConceptStems.add(conceptStem);
						}
					}

					// Evaluate full matching for parameter and reference names
					else if (methodStemType == StemMethodType.PARAMETER_NAME_FULL || methodStemType == StemMethodType.REFERENCE_NAME_FULL) {

						if (conceptStem.getStemType() == StemConceptType.CONCEPT_NAME_FULL) {
							weight += 1d;
							matchingMethodStems.add(methodStem);
							matchingConceptStems.add(conceptStem);
						}
						else if (conceptStem.getStemType() == StemConceptType.CLASS_IDENTIFIER_FULL) {
							weight += 1d;
							matchingMethodStems.add(methodStem);
							matchingConceptStems.add(conceptStem);
						}
						else if (conceptStem.getStemType() == StemConceptType.ATTRIBUTE_IDENTIFIER_FULL) {
							int attrCount = concept.getAttributes().isEmpty() ? 1 : concept.getAttributes().size();
							weight += 1d / attrCount;
							matchingMethodStems.add(methodStem);
							matchingConceptStems.add(conceptStem);
						}
						else if (conceptStem.getStemType() == StemConceptType.ATTRIBUTE_NAME_FULL) {
							int attrCount = concept.getAttributes().isEmpty() ? 1 : concept.getAttributes().size();
							weight += 1d / attrCount;
							matchingMethodStems.add(methodStem);
							matchingConceptStems.add(conceptStem);
						}
					}

					// Evaluate partial matching of method name
					else if (methodStemType == StemMethodType.METHOD_NAME_PART) {

						if (conceptStem.getStemType() == StemConceptType.CONCEPT_NAME_PART) {
							StemConcept stemNameFull = conceptStem.getParent();
							int partCount = stemNameFull.getParts().isEmpty() ? 1 : stemNameFull.getParts().size();
							weight += 1d / partCount;
							matchingMethodStems.add(methodStem);
							matchingConceptStems.add(conceptStem);
						}
					}

					// Evaluate partial matching for parameter and reference names
					else if (methodStemType == StemMethodType.PARAMETER_NAME_PART || methodStemType == StemMethodType.REFERENCE_NAME_PART) {

						if (conceptStem.getStemType() == StemConceptType.CONCEPT_NAME_PART) {
							StemConcept stemNameFull = conceptStem.getParent();
							int partCount = stemNameFull.getParts().isEmpty() ? 1 : stemNameFull.getParts().size();
							weight += 1d / partCount;
							matchingMethodStems.add(methodStem);
							matchingConceptStems.add(conceptStem);
						}
						else if (conceptStem.getStemType() == StemConceptType.CLASS_IDENTIFIER_PART) {
							StemConcept stemNameFull = conceptStem.getParent();
							int partCount = stemNameFull.getParts().isEmpty() ? 1 : stemNameFull.getParts().size();
							weight += 1d / partCount;
							matchingMethodStems.add(methodStem);
							matchingConceptStems.add(conceptStem);
						}
						else if (conceptStem.getStemType() == StemConceptType.ATTRIBUTE_IDENTIFIER_PART) {
							StemConcept stemAttrIdFull = conceptStem.getParent();
							int attrCount = concept.getAttributes().isEmpty() ? 1 : concept.getAttributes().size();
							int partCount = stemAttrIdFull.getParts().isEmpty() ? 1 : stemAttrIdFull.getParts().size();
							weight += 1d / attrCount / partCount;
							matchingMethodStems.add(methodStem);
							matchingConceptStems.add(conceptStem);
						}
						else if (conceptStem.getStemType() == StemConceptType.ATTRIBUTE_NAME_PART) {
							StemConcept stemAttrNameFull = conceptStem.getParent();
							int attrCount = concept.getAttributes().isEmpty() ? 1 : concept.getAttributes().size();
							int partCount = stemAttrNameFull.getParts().isEmpty() ? 1 : stemAttrNameFull.getParts().size();
							weight += 1d / attrCount / partCount;
							matchingMethodStems.add(methodStem);
							matchingConceptStems.add(conceptStem);
						}
					}
				}
			}
		}

		return weight;
	}
}
