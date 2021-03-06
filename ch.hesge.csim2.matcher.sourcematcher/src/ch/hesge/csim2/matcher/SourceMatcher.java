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
import ch.hesge.csim2.core.utils.Console;

/**
 * This engine allow matching calculation based
 * on the source-code, ontology item comparison.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 *
 */
public class SourceMatcher implements IMethodConceptMatcher {

	// Private attributes
	private ApplicationLogic applicationLogic;
	private List<MethodConceptMatch> matchings;

	/**
	 * Default constructor
	 */
	public SourceMatcher() {
		applicationLogic = ApplicationLogic.UNIQUE_INSTANCE;
		matchings = new ArrayList<>();
	}

	/**
	 * Get the engine name.
	 * @see ch.hesge.csim2.core.shell.IEngine#getName()
	 */
	@Override
	public String getName() {
		return "SourceMatcher";
	}

	/**
	 * Get the engine version.
	 * @see ch.hesge.csim2.core.shell.IEngine#getVersion()
	 */
	@Override
	public String getVersion() {
		return "1.0.6";
	}

	/**
	 * Get the engine description
	 * @see ch.hesge.csim2.core.shell.IEngine#getDescription()
	 */
	@Override
	public String getDescription() {
		return "method concept matcher based on method/concept stems comparison.";
	}

	/**
	 * Retrieve a map of all MethodConceptMatch classified by method Id.
	 * 
	 * @param project
	 *        the project where to calculate matching
	 * @return
	 *         a map of (MethodId, List<MethodConceptMatch>)
	 */
	public Map<Integer, List<MethodConceptMatch>> getMethodMatchingMap(Project project, float threshold) {

		matchings.clear();
		
		// Load stem concepts and methods
		Map<Integer, StemMethod> stemMethodTreeMap   = applicationLogic.getStemMethodTreeMap(project);
		Map<Integer, StemConcept> stemConceptTreeMap = applicationLogic.getStemConceptTreeMap(project);

		// Load concepts, methods and stems data
		Map<Integer, SourceMethod> methodMap = applicationLogic.getSourceMethodMap(project);
		Map<Integer, Concept> conceptMap = applicationLogic.getConceptMap(project);
		
		// Scan all method and lookup for matching concepts
		for (SourceMethod method : methodMap.values()) {
			for (Concept concept : conceptMap.values()) {

				ArrayList<StemConcept> matchingStemConcepts = new ArrayList<>();
				ArrayList<StemMethod> matchingStemMethods = new ArrayList<>();

				StemConcept rootStemConcept = stemConceptTreeMap.get(concept.getKeyId());
				StemMethod rootStemMethod   = stemMethodTreeMap.get(method.getKeyId());

				// Compute structural similarity
				double similarity = computeSimilarity(concept, method, rootStemConcept, rootStemMethod, matchingStemConcepts, matchingStemMethods);

				// Compute structural similarity
				//double similarity = computeSimilarity2(method, concept, stemMethodTreeMap, stemConceptTreeMap, matchingStemMethods, matchingStemConcepts);

				// Register result within the matchMap
				if (similarity > threshold) {
					
					MethodConceptMatch match = new MethodConceptMatch();

					match.setProject(project);
					match.setSourceClass(method.getSourceClass());
					match.setSourceMethod(method);
					match.setConcept(concept);
					match.setWeight(similarity);
					match.getStemMethods().addAll(matchingStemMethods);
					match.getStemConcepts().addAll(matchingStemConcepts);

					matchings.add(match);
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
	 * @param concept
	 * @param method
	 * @param rootStemConcept
	 * @param rootStemMethod
	 * @return a similarity weight
	 */
	private double computeSimilarity(Concept concept, SourceMethod method, StemConcept rootStemConcept, StemMethod rootStemMethod, ArrayList<StemConcept> matchingStemConcepts, ArrayList<StemMethod> matchingStemMethods) {
		
		double similarity = 0d;
		
		List<StemConcept> stemConceptList = SourceMatcherUtils.inflateStemConcepts(rootStemConcept);
		List<StemMethod> stemMethodList   = SourceMatcherUtils.inflateStemMethods(rootStemMethod);
		
		// Check if full concept name is matching
		if (SourceMatcherUtils.computeFullConceptNameMatching(rootStemConcept, stemMethodList, matchingStemConcepts, matchingStemMethods)) {
			similarity = 1d;
		}

		// Check if full concept classname is matching
		else if (SourceMatcherUtils.computeFullClassNameMatching(stemConceptList, stemMethodList, matchingStemConcepts, matchingStemMethods)) {
			similarity = 1d;
		}

		// Check if full concept identifier is matching
		else if (SourceMatcherUtils.computeFullClassIdentifierMatching(stemConceptList, stemMethodList, matchingStemConcepts, matchingStemMethods)) {
			similarity = 1d;
		}
		
		// Check if concept identifier have some partial matching
		else if (SourceMatcherUtils.computePartialClassIdentifierMatching(stemConceptList, stemMethodList, matchingStemConcepts, matchingStemMethods)) {
			similarity = 0.8;
		}
		else {
			
			List<StemConcept> stemAttributes = SourceMatcherUtils.getStemConceptAttributes(concept, stemConceptList);
			
			for (StemConcept rootStemAttribute : stemAttributes) {
				
				List<StemConcept> stemAttributeList = SourceMatcherUtils.inflateStemConcepts(rootStemAttribute);
				
				if (SourceMatcherUtils.computeFullAttributeNameMatching(rootStemAttribute, stemMethodList, matchingStemConcepts, matchingStemMethods)) {
					similarity += 0.8 / concept.getAttributes().size();
				}
				else if (SourceMatcherUtils.computeFullAttributeIdentifierMatching(stemAttributeList, stemMethodList, matchingStemConcepts, matchingStemMethods)) {
					similarity += 0.8 / concept.getAttributes().size();
				}
				else if (SourceMatcherUtils.computePartialAttributeIdentifierMatching(stemAttributeList, stemMethodList, matchingStemConcepts, matchingStemMethods)) {
					similarity += 0.8 / concept.getAttributes().size();
				}
				else {
					
					for (StemConcept stemAttributeNamePart : rootStemAttribute.getParts()) {
						
						if (SourceMatcherUtils.computePartialAttributeWordMatching(stemAttributeNamePart, stemMethodList, matchingStemConcepts, matchingStemMethods)) {
							similarity += 0.7 / rootStemAttribute.getParts().size() / concept.getAttributes().size();
						}
					}
				}
			}
		}
				
		// If a weight is found and the stem concept is not yet used in weight calculation
		/*
		if (similarity > 0d) {

			Console.writeDebug(this, "found concept in method:"); 
			Console.writeDebug(this, "  weight: " + similarity); 

			Console.writeDebug(this, "  concept: " + concept.getName()); 
			for (StemConcept stem : matchingStemConcepts) {
				Console.writeDebug(this, "    term: " + stem.getTerm() + ", conceptStem: " + stem.getStemType().name()); 
			}

			Console.writeDebug(this, "  method: " + method.getSourceClass().getName() + "." + method.getSignature());
			for (StemMethod stem : matchingStemMethods) {
				Console.writeDebug(this, "    term: " + stem.getTerm() + ", methodStem: " + stem.getStemType().name()); 
			}
		}
		*/
		

		return similarity;
	}
	
	/**
	 * Compute similarity between a method and a concept.
	 * 
	 * @param method
	 * @param concept
	 * @return a similarity weight
	 */
	public double computeSimilarity2(SourceMethod method, Concept concept, Map<Integer, StemMethod> stemMethodTreeMap, Map<Integer, StemConcept> stemConceptTreeMap, List<StemMethod> matchingStemMethods, List<StemConcept> matchingStemConcepts) {

		double similarity = 0d;

		StemMethod methodRootStem    = stemMethodTreeMap.get(method.getKeyId());
		List<StemMethod> methodStems = applicationLogic.inflateStemMethods(methodRootStem);

		StemConcept conceptRootStem    = stemConceptTreeMap.get(concept.getKeyId());
		List<StemConcept> conceptStems = applicationLogic.inflateStemConcepts(conceptRootStem);

		// Get number of concept attributes
		int conceptAttrCount = concept.getAttributes().isEmpty() ? 1 : concept.getAttributes().size();

		Console.writeDebug(this, "computing matching coefficient:"); 
		Console.writeDebug(this, "  method: " + method.getSourceClass().getName() + "." + method.getSignature());
		Console.writeDebug(this, "  concept: " + concept.getName()); 
		
		// Scan all method stems
		for (StemMethod stemMethod : methodStems) {

			StemMethodType stemMethodType = stemMethod.getStemType();			
			
			// Scan all concept stems
			for (StemConcept stemConcept : conceptStems) {

				StemConceptType stemConceptType = stemConcept.getStemType();
				
				// Skip stem concept not matching
				if (!stemConcept.getTerm().equals(stemMethod.getTerm())) continue;
				
				double stemWeight = 0d;
				int stemPartCount = 0;
				
				// Evaluate full matching for parameter or reference types
				if (stemMethodType == StemMethodType.PARAMETER_TYPE_FULL || stemMethodType == StemMethodType.REFERENCE_TYPE_FULL) {

					if (stemConceptType == StemConceptType.CLASS_NAME_FULL) {
						stemWeight = 1d;						
					}
				}

				// Evaluate full matching for method, parameter or reference names
				else if (stemMethodType == StemMethodType.METHOD_NAME_FULL || stemMethodType == StemMethodType.PARAMETER_NAME_FULL || stemMethodType == StemMethodType.REFERENCE_NAME_FULL) {

					if (stemConceptType == StemConceptType.CONCEPT_NAME_FULL) {
						stemWeight = 0.9;						
					}
					else if (stemConceptType == StemConceptType.ATTRIBUTE_IDENTIFIER_FULL) {
						stemWeight = 0.9 / conceptAttrCount;											
					}
					else if (stemConceptType == StemConceptType.ATTRIBUTE_NAME_FULL) {
						stemWeight = 0.9 / conceptAttrCount;											
					}
				}

				// Evaluate partial matching for method, parameter or reference names
				else if (stemMethodType == StemMethodType.METHOD_NAME_PART || stemMethodType == StemMethodType.PARAMETER_NAME_PART || stemMethodType == StemMethodType.REFERENCE_NAME_PART) {

					if (stemConceptType == StemConceptType.CONCEPT_NAME_FULL) {
						stemWeight = 0.8;											
					}
					else if (stemConceptType == StemConceptType.ATTRIBUTE_NAME_FULL) {
						stemWeight = 0.8 / conceptAttrCount;
					}
					else if (stemConceptType == StemConceptType.CONCEPT_NAME_PART) {
						
						StemConcept fullStem = stemConcept.getParent();
						stemPartCount = fullStem.getParts().isEmpty() ? 1 : fullStem.getParts().size();
						stemWeight = 0.8 / stemPartCount;
					}
					else if (stemConceptType == StemConceptType.ATTRIBUTE_NAME_PART) {
						
						StemConcept fullStem = stemConcept.getParent();
						stemPartCount = fullStem.getParts().isEmpty() ? 1 : fullStem.getParts().size();
						stemWeight = 0.8 / conceptAttrCount / stemPartCount;
					}
				}

				// If a weight is found, 
				// and the stem concept is not yet used in weight calculation
				if (stemWeight > 0d && !matchingStemConcepts.contains(stemConcept)) {

					Console.writeDebug(this, "  methodStem: " + stemMethodType.name() + ", conceptStem: " + stemConceptType.name() + ", conceptAttrCount: " + conceptAttrCount + ", stemPartCount: " + stemPartCount + ", weight: " + stemWeight); 
					
					matchingStemMethods.add(stemMethod);
					matchingStemConcepts.add(stemConcept);
					similarity += stemWeight;
				}
			}
		}
		
		// Bound similarity between [0..1] 
		double normalizeSimilarity = Math.min(1d, similarity);
		
		Console.writeDebug(this, "  similarity: " + similarity + ", normalized: " + normalizeSimilarity);

		return normalizeSimilarity;
	}
}
