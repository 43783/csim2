package ch.hesge.csim2.matcher;
/**
 * 
 */


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
import ch.hesge.csim2.core.utils.SimpleMatrix;
import ch.hesge.csim2.core.utils.SimpleVector;
import ch.hesge.csim2.core.utils.StemMatrix;

/**
 * This engine allow matching calculation based
 * on the Levenshtein similarity measure.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 *
 */
public class WeightedTfidfMatcher implements IMethodConceptMatcher {

	// Private attributes
	private ApplicationLogic applicationLogic;

	/**
	 * Default constructor
	 */
	public WeightedTfidfMatcher() {
		applicationLogic = ApplicationLogic.UNIQUE_INSTANCE;
	}

	/**
	 * Get the engine name.
	 * @see ch.hesge.csim2.core.shell.IEngine#getName()
	 */
	@Override
	public String getName() {
		return "WeightedTfidfMatcher";
	}

	/**
	 * Get the engine version.
	 * @see ch.hesge.csim2.core.shell.IEngine#getVersion()
	 */
	@Override
	public String getVersion() {
		return "1.0.5";
	}

	/**
	 * Get the engine description
	 * @see ch.hesge.csim2.core.shell.IEngine#getDescription()
	 */
	@Override
	public String getDescription() {
		return "method concept matcher based on weighted tfidf algorithm.";
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

		// Retrieve concept and method map
		Map<Integer, Concept> conceptMap     = applicationLogic.getConceptMap(project);
		Map<Integer, SourceMethod> methodMap = applicationLogic.getSourceMethodMap(project);

		// Retrieve stem map
		Map<String, List<StemConcept>> stemConceptsMap = applicationLogic.getStemConceptByTermMap(project);
		Map<String, List<StemMethod>> stemMethodsMap   = applicationLogic.getStemMethodByTermMap(project);

		// Get linear concepts method and terms (used in matrix cols/rows)
		List<String> terms = new ArrayList<>(stemConceptsMap.keySet());
		List<Concept> concepts = new ArrayList<>(conceptMap.values());
		List<SourceMethod> methods = new ArrayList<>(methodMap.values());

		// Calculate term-concept matrix with identifier weight algorithm
		StemMatrix<StemConcept> stemConceptMatrix = new StemMatrix<StemConcept>(terms.size(), concepts.size());
		SimpleMatrix termConceptMatrix = getTermConceptMatrix(terms, concepts, conceptMap, stemConceptsMap, stemConceptMatrix);

		// Calculate term-method matrix
		StemMatrix<StemMethod> stemMethodMatrix = new StemMatrix<StemMethod>(terms.size(), methods.size());
		SimpleMatrix termMethodMatrix = getTermMethodMatrix(terms, methods, methodMap, stemMethodsMap, stemMethodMatrix);

		for (int i = 0; i < methods.size(); i++) {

			// Retrieve current method vector
			SourceMethod sourceMethod = methods.get(i);
			SimpleVector termMethodVector = termMethodMatrix.getColumnVector(i);

			// If method vector is null, skip
			if (!termMethodVector.isNullVector()) {

				// Select all concepts with similarity factor > 0
				for (int j = 0; j < termConceptMatrix.getColumnDimension(); j++) {

					// Retrieve current concept vector
					Concept concept = concepts.get(j);
					SimpleVector termConceptVector = termConceptMatrix.getColumnVector(j);

					// If concept vector is null, skip
					if (!termConceptVector.isNullVector()) {
						
						// Calculate similarity between method and concept vectors
						double similarity = termMethodVector.cosine(termConceptVector);

						// Register result within the matchMap
						if (similarity > 0d) {

							MethodConceptMatch match = new MethodConceptMatch();

							match.setProject(project);
							match.setSourceClass(sourceMethod.getSourceClass());
							match.setSourceMethod(sourceMethod);
							match.setConcept(concept);
							match.setWeight(similarity);

							// Gather concept and method stems found for matching
							for (int k = 0; k < terms.size(); k++) {
								
								if (termMethodVector.getValue(k) > 0) {
									match.getStemConcepts().addAll(stemConceptMatrix.get(k, j));
									match.getStemMethods().addAll(stemMethodMatrix.get(k, i));
								}
							}

							matchings.add(match);
						}
					}
				}
			}
		}

		// Now build a map of concept matching, classified by method id
		Map<Integer, List<MethodConceptMatch>> matchingMap = new HashMap<>();

		for (MethodConceptMatch match : matchings) {

			// Create an associated array if missing
			if (!matchingMap.containsKey(match.getSourceMethod().getKeyId())) {
				matchingMap.put(match.getSourceMethod().getKeyId(), new ArrayList<>());
			}

			// Add the match to the array for the specific method
			matchingMap.get(match.getSourceMethod().getKeyId()).add(match);
		}

		return matchingMap;
	}

	private static SimpleMatrix getTermConceptMatrix(List<String> terms, List<Concept> concepts, Map<Integer, Concept> conceptMap, Map<String, List<StemConcept>> stems, StemMatrix<StemConcept> stemMatrix) {

		SimpleMatrix tfMatrix    = new SimpleMatrix(terms.size(), concepts.size());
		SimpleMatrix idfMatrix   = new SimpleMatrix(terms.size(), concepts.size());
		SimpleMatrix tfidfMatrix = new SimpleMatrix(terms.size(), concepts.size());

		SimpleMatrix termOccurrenceInConcept = new SimpleMatrix(terms.size(), concepts.size());
		SimpleVector totalTermInConcept      = new SimpleVector(concepts.size());

		// Scan all terms and build an occurrence matrix
		for (int i = 0; i < terms.size(); i++) {

			// Retrieve current term
			String term = terms.get(i);

			if (stems.containsKey(term)) {
				
				// Loop over all concepts referring a term
				for (StemConcept stem : stems.get(term)) {

					// Retrieve concept index in row
					int j = concepts.indexOf(conceptMap.get(stem.getConceptId()));
					
					double conceptWeight = 0d;
					
					// Evaluate stem matching weight for full terms
					if (stem.getStemType() == StemConceptType.CLASS_NAME_FULL) {
						conceptWeight = 1d;
					}
					else if (stem.getStemType() == StemConceptType.CONCEPT_NAME_FULL) {
						conceptWeight = 1d;
					}
					else if (stem.getStemType() == StemConceptType.CLASS_IDENTIFIER_FULL) {
						conceptWeight = 1d;
					}
					else if (stem.getStemType() == StemConceptType.ATTRIBUTE_IDENTIFIER_FULL) {
						StemConcept stemAttrIdFull = stem.getParent();
						StemConcept conceptNameFull = stemAttrIdFull.getParent();
						Concept concept = conceptMap.get(conceptNameFull.getConceptId());
						int attrCount = concept.getAttributes().isEmpty() ? 1 : concept.getAttributes().size();
						conceptWeight = 1d / attrCount;
					}
					else if (stem.getStemType() == StemConceptType.ATTRIBUTE_NAME_FULL) {
						StemConcept conceptNameFull = stem.getParent();
						Concept concept = conceptMap.get(conceptNameFull.getConceptId());
						int attrCount = concept.getAttributes().isEmpty() ? 1 : concept.getAttributes().size();
						conceptWeight = 1d / attrCount;
					}

					// Evaluate stem matching weight for part terms
					else if (stem.getStemType() == StemConceptType.CONCEPT_NAME_PART) {
						StemConcept stemNameFull = stem.getParent();
						int partCount = stemNameFull.getParts().isEmpty() ? 1 : stemNameFull.getParts().size();
						conceptWeight = 1d / partCount;
					}
					else if (stem.getStemType() == StemConceptType.ATTRIBUTE_NAME_PART) {
						StemConcept stemAttrNameFull = stem.getParent();
						StemConcept conceptNameFull = stemAttrNameFull.getParent();
						Concept concept = conceptMap.get(conceptNameFull.getConceptId());
						int attrCount = concept.getAttributes().isEmpty() ? 1 : concept.getAttributes().size();
						int partCount = stemAttrNameFull.getParts().isEmpty() ? 1 : stemAttrNameFull.getParts().size();
						conceptWeight = 1d / attrCount / partCount;
					}
					
					// Count term occurrences in concept
					termOccurrenceInConcept.addValue(i, j, conceptWeight);

					// Count total terms in each concepts
					totalTermInConcept.addValue(j, conceptWeight);
					
					stemMatrix.add(i, j, stem);
				}
			}
		}

		// Calculate the term frequency: tf = termOccurrenceInConcept / totalTermInConcept
		for (int i = 0; i < tfMatrix.getRowDimension(); i++) {
			SimpleVector occurrenceInConcept = termOccurrenceInConcept.getRowVector(i);
			tfMatrix.setRowVector(i, occurrenceInConcept.ebeDivide(totalTermInConcept));
		}

		// Calculate the inverse term frequency: idf = log(totalTermCount/(occurrenceInConcept+1)
		for (int i = 0; i < idfMatrix.getRowDimension(); i++) {
			SimpleVector totalTermCount = new SimpleVector(concepts.size(), terms.size());
			SimpleVector occurrenceInConcept = termOccurrenceInConcept.getRowVector(i);
			occurrenceInConcept = occurrenceInConcept.ebeAdd(1d);
			idfMatrix.setRowVector(i, totalTermCount.ebeDivide(occurrenceInConcept));
		}
		
		idfMatrix = idfMatrix.log10();

		// Finally calculate final matrix: tfidf = tf * idf
		tfidfMatrix = tfMatrix.ebeMultiply(idfMatrix);

		return tfidfMatrix;
	}	

	/**
	 * <pre>
	 * 
	 * Compute the term-method matrix based on terms and methods passed in argument.
	 * 
	 * For instance:
	 * 
	 * 		  						+->  term presence/absence within the method
	 * 								|
	 * 					|						|		
	 * 					|	0		1		0	|		T1		
	 * 		A		=	|	1		1		0	|		T2		terms
	 * 					|	0		0		0	|		T3
	 * 					|	1		0		0	|		T4
	 * 					|						|
	 * 
	 * 						M1		M2		M3		--> method
	 * 
	 *
	 * </pre>
	 * 
	 * @param terms
	 *        the terms used to compute weights
	 * @param methods
	 *        the methods used to compute weights
	 * @param methodMap
	 *        a map of all methods classified by their keyId
	 * @param stems
	 *        the stems allowing links between terms and methods
	 * @return
	 *         a term-method matrix
	 */
	private static SimpleMatrix getTermMethodMatrix(List<String> terms, List<SourceMethod> methods, Map<Integer, SourceMethod> methodMap, Map<String, List<StemMethod>> stems, StemMatrix<StemMethod> stemMatrix) {
		
		SimpleMatrix methodMatrix = new SimpleMatrix(terms.size(), methods.size());

		// Scan all terms and build an occurrence matrix
		for (int i = 0; i < terms.size(); i++) {

			// Retrieve current term
			String term = terms.get(i);

			if (stems.containsKey(term)) {
				
				// Loop over all concepts referring a term
				for (StemMethod stem : stems.get(term)) {

					// Retrieve method index in row
					int j = methods.indexOf(methodMap.get(stem.getSourceMethodId()));

					// Count term occurrences in concept
					methodMatrix.setValue(i, j, 1d);
					stemMatrix.add(i, j, stem);
				}
			}
		}

		return methodMatrix;
	}
}
