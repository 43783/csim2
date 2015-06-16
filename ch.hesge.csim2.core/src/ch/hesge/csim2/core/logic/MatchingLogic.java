/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hesge.csim2.core.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.MethodConceptMatch;
import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.SourceMethod;
import ch.hesge.csim2.core.model.StemConcept;
import ch.hesge.csim2.core.model.StemConceptType;
import ch.hesge.csim2.core.model.StemMethod;

/**
 * This class implement all logical rules associated to method/concept matching.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

class MatchingLogic {

	/**
	 * Retrieve a map of all MethodConceptMatch classified by method Id.
	 * 
	 * @param project
	 *        the project where to calculate matching
	 * @param matchAlgo
	 *        the matching algorithm (tfidf, id-l1norm or id-cosine)
	 * @return
	 *         a map of (MethodId, List<MethodConceptMatch>)
	 */
	public static Map<Integer, List<MethodConceptMatch>> getMethodMatchingMap(Project project, MatchingAlgorithm matchAlgo) {

		List<MethodConceptMatch> matchings = new ArrayList<>();

		// Retrieve concept and method map
		Map<Integer, Concept> conceptMap     = ApplicationLogic.getConceptMap(project);
		Map<Integer, SourceMethod> methodMap = ApplicationLogic.getSourceMethodMap(project);

		// Retrieve stem map
		Map<String, List<StemConcept>> stemConceptsMap = ApplicationLogic.getStemConceptByTermMap(project);
		Map<String, List<StemMethod>> stemMethodsMap   = ApplicationLogic.getStemMethodByTermMap(project);

		// Get linear concepts and terms (matrix cols/rows)
		List<Concept> concepts = new ArrayList<>(conceptMap.values());
		List<String> terms     = new ArrayList<>(stemConceptsMap.keySet());

		RealMatrix weightMatrix = null;

		if (matchAlgo == MatchingAlgorithm.TFIDF) {
			weightMatrix = getTfIdfMatrix(terms, concepts, conceptMap, stemConceptsMap);
		}
		else if (matchAlgo == MatchingAlgorithm.ID_L1NORM || matchAlgo == MatchingAlgorithm.ID_COSINE) {
			weightMatrix = getWeightMatrix(terms, concepts, conceptMap, stemConceptsMap);
		}

		// Calculate on term vectors for each method
		Map<Integer, RealVector> methodTermVectorMap = MatchingLogic.getMethodTermVectorMap(terms, methodMap, stemMethodsMap);

		for (SourceMethod sourceMethod : methodMap.values()) {

			RealVector methodTermVector = methodTermVectorMap.get(sourceMethod.getKeyId());
			boolean isNotZeroMethodVector = MatchingLogic.isNotZeroVector(methodTermVector);

			// Skip null method vector
			if (isNotZeroMethodVector) {

				// Select all concepts with similarity factor > 0
				for (int i = 0; i < weightMatrix.getColumnDimension(); i++) {

					Concept concept = concepts.get(i);
					RealVector conceptTermVector = weightMatrix.getColumnVector(i);

					double similarity = 0d;

					// Calculate similarity between method and concept vectors
					if (matchAlgo == MatchingAlgorithm.TFIDF || matchAlgo == MatchingAlgorithm.ID_COSINE) {
						similarity = conceptTermVector.cosine(methodTermVector);
					}
					else if (matchAlgo == MatchingAlgorithm.ID_L1NORM) {
						similarity = conceptTermVector.ebeMultiply(methodTermVector).getL1Norm();
					}

					// Register result within the matchMap
					if (similarity > 0d) {

						MethodConceptMatch match = new MethodConceptMatch();

						match.setProjectId(project.getKeyId());
						match.setSourceMethod(sourceMethod);
						match.setConcept(concept);
						match.setWeight(similarity);

						match.setSourceMethodId(sourceMethod.getKeyId());
						match.setConceptId(concept.getKeyId());

						matchings.add(match);
					}
				}
			}
		}

		// Now build a map of concept matching, classified by method id
		Map<Integer, List<MethodConceptMatch>> matchingMap = new HashMap<>();

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
	 * <pre>
	 * 
	 * Compute the tf-idf matrix based on terms and concepts passed in argument.
	 * 
	 * For instance:
	 * 
	 * 		  						+->  tf-idf weight for term T1, concept C2
	 * 								|
	 * 					|						|		
	 * 					|	0.3		3		0	|		T1		
	 * 		TF-IDF	=	|	1.5		0		7	|		T2		terms
	 * 					|	0		0		8	|		T3
	 * 					|	0		20		5	|		T4
	 * 					|						|
	 * 
	 * 						C1		C2		C3		--> concepts
	 * 
	 * 
	 * First, we compute the tf matrix (term = rows, concept = column).
	 * The tf-matrix represents the relative frequency of a term within a concept.
	 * 
	 * 	for a single term, and a single concept, tf is calculated as:
	 * 
	 *   					 	 	 term-occurrence-in-concept
	 *  		TF(i,j)		=		----------------------------  
	 *  					  	  		total-term-in-concept
	 *  
	 * 	
	 *   Then, we compute the idf matrix (term = rows, concept = column).
	 * 
	 *   The idf-matrix represents the inverse frequency of a term within a concept, weighted 
	 *  by the frequency of the term among the whole terms space.
	 * 
	 * 	for a single term, and a single concept, idf is calculated as:
	 * 
	 *  						       		total-term-count
	 *  		IDF(i,j)	=	log(  ----------------------------  )
	 *  						   		term-occurrence-in-concept
	 * 
	 * Finally the TFIDF matrix is calculated as:
	 * 
	 * 		TFIDF		=	TF	*	IDF
	 *
	 * </pre>
	 * 
	 * @param terms
	 *        the terms used to compute weights
	 * @param concepts
	 *        the concepts used to compute weights
	 * @param conceptMap
	 *        a map of all concepts classified by their keyId
	 * @param stems
	 *        the stems allowing links between terms and concepts
	 * @return
	 *         a tf-idf matrix
	 */
	private static RealMatrix getTfIdfMatrix(List<String> terms, List<Concept> concepts, Map<Integer, Concept> conceptMap, Map<String, List<StemConcept>> stems) {

		// Prepare matrix/vectors used to perform tf and idf calculation
		RealMatrix termOccurrenceInConcept = MatrixUtils.createRealMatrix(terms.size(), concepts.size());
		RealVector totalTermInConcept = new ArrayRealVector(concepts.size());

		for (int i = 0; i < terms.size(); i++) {

			// Retrieve current term
			String term = terms.get(i);

			// Loop over all concepts referring a term
			for (StemConcept stem : stems.get(term)) {

				// Retrieve concept and index
				Concept concept = conceptMap.get(stem.getConceptId());
				int conceptIndex = concepts.indexOf(concept);

				// Count term occurrences in concept
				termOccurrenceInConcept.addToEntry(i, conceptIndex, 1d);

				// Adjust total term count in concept
				totalTermInConcept.addToEntry(conceptIndex, 1d);
			}
		}

		// Calculate the term frequency matrix
		RealMatrix tfMatrix = MatrixUtils.createRealMatrix(terms.size(), concepts.size());
		for (int i = 0; i < tfMatrix.getRowDimension(); i++) {
			RealVector occurrenceInConceptRow = termOccurrenceInConcept.getRowVector(i);
			tfMatrix.setRowVector(i, occurrenceInConceptRow.ebeDivide(totalTermInConcept));
		}

		// Calculate the inverse term frequency matrix
		RealMatrix idfMatrix = MatrixUtils.createRealMatrix(terms.size(), concepts.size());
		for (int i = 0; i < idfMatrix.getRowDimension(); i++) {
			RealVector occurrenceInConceptRow = termOccurrenceInConcept.getRowVector(i);
			for (int j = 0; j < occurrenceInConceptRow.getDimension(); j++) {
				double tfidf = Math.log10(((double) terms.size()) / (occurrenceInConceptRow.getEntry(j) + 1));
				idfMatrix.setEntry(i, j, tfidf);
			}
		}

		// Finally calculate tfidf = tf * idf (element-by-element)
		RealMatrix tfidfMatrix = MatrixUtils.createRealMatrix(terms.size(), concepts.size());
		for (int i = 0; i < tfMatrix.getRowDimension(); i++) {
			for (int j = 0; j < tfMatrix.getColumnDimension(); j++) {
				tfidfMatrix.setEntry(i, j, tfMatrix.getEntry(i, j) * idfMatrix.getEntry(i, j));
			}
		}

		return tfidfMatrix;
	}

	/**
	 * <pre>
	 * 
	 * Compute the weight matrix based on terms and concepts passed in argument.
	 * 
	 * For instance:
	 * 
	 * 		  						+->  tf-idf weight for term T1, concept C2
	 * 								|
	 * 					|						|		
	 * 					|	0.3		3		0	|		T1		
	 * 		WEIGHT	=	|	1.5		0		7	|		T2		terms
	 * 					|	0		0		8	|		T3
	 * 					|	0		20		5	|		T4
	 * 					|						|
	 * 
	 * 						C1		C2		C3		--> concepts
	 * 
	 * @param terms
	 *        the terms used to compute weights
	 * @param concepts
	 *        the concepts used to compute weights
	 * @param conceptMap
	 *        a map of all concepts classified by their keyId
	 * @param stems
	 *        the stems allowing links between terms and concepts
	 * @return
	 * a terms/concepts weight matrix
	 */

	private static RealMatrix getWeightMatrix(List<String> terms, List<Concept> concepts, Map<Integer, Concept> conceptMap, Map<String, List<StemConcept>> stemByTermMap) {

		RealMatrix weightMatrix = MatrixUtils.createRealMatrix(terms.size(), concepts.size());

		// Loop over all terms
		for (int i = 0; i < terms.size(); i++) {

			// Retrieve current term
			String currentTerm = terms.get(i);

			if (currentTerm.length() == 0)
				continue;

			// Loop over all stem concepts referring a single term
			for (StemConcept stem : stemByTermMap.get(currentTerm)) {

				// Retrieve stem parent
				StemConcept parent = stem.getParent();

				// Retrieve concept associated to the stem
				Concept concept = conceptMap.get(stem.getConceptId());

				double conceptWeight = 0d;

				// Evaluate for current term, the stem matching weight
				if (stem.getStemType() == StemConceptType.CLASS_IDENTIFIER_FULL) {
					conceptWeight = 1.0;
				}
				else if (stem.getStemType() == StemConceptType.CLASS_IDENTIFIER_PART) {
					int partCount = parent.getParts().isEmpty() ? 1 : parent.getParts().size();
					conceptWeight = 1.0 / partCount;
				}
				else if (stem.getStemType() == StemConceptType.ATTRIBUTE_IDENTIFIER_FULL) {
					int attrCount = parent.getParent().getAttributes().isEmpty() ? 1 : parent.getParent().getAttributes().size();
					conceptWeight = 0.9 / attrCount;
				}
				else if (stem.getStemType() == StemConceptType.ATTRIBUTE_IDENTIFIER_PART) {
					int attrCount = parent.getParent().getParent().getAttributes().isEmpty() ? 1 : parent.getParent().getParent().getAttributes().size();
					int partCount = parent.getParts().isEmpty() ? 1 : parent.getParts().size();
					conceptWeight = 0.9 / attrCount / partCount;
				}
				else if (stem.getStemType() == StemConceptType.CLASS_NAME_FULL) {
					conceptWeight = 0.8;
				}
				else if (stem.getStemType() == StemConceptType.CLASS_NAME_PART) {
					int partSize = parent.getParts().isEmpty() ? 1 : parent.getParts().size();
					conceptWeight = 0.8 / partSize;
				}
				else if (stem.getStemType() == StemConceptType.CONCEPT_NAME_FULL) {
					conceptWeight = 0.7;
				}
				else if (stem.getStemType() == StemConceptType.CONCEPT_NAME_PART) {
					int partCount = parent.getParts().isEmpty() ? 1 : parent.getParts().size();
					conceptWeight = 0.7 / partCount;
				}
				else if (stem.getStemType() == StemConceptType.ATTRIBUTE_NAME_FULL) {
					int attrCount = parent.getAttributes().isEmpty() ? 1 : parent.getAttributes().size();
					conceptWeight = 0.6 / attrCount;
				}
				else if (stem.getStemType() == StemConceptType.ATTRIBUTE_NAME_PART) {
					int attrCount = parent.getParent().getAttributes().isEmpty() ? 1 : parent.getParent().getAttributes().size();
					int partSize = parent.getParts().isEmpty() ? 1 : parent.getParts().size();
					conceptWeight = 0.6 / attrCount / partSize;
				}

				// Update matrix weight for concept
				int conceptIndex = concepts.indexOf(concept);
				weightMatrix.addToEntry(i, conceptIndex, conceptWeight);
			}
		}

		return weightMatrix;
	}

	/**
	 * Retrieve a map of term vector for each method passed in argument.
	 * Note: each vector component is based on term presence (1) or missing (0).
	 * 
	 * @param terms
	 *        the terms used to compute vectors
	 * @param concepts
	 *        the methods used to compute vectors
	 * @param stems
	 *        the stems allowing links between terms and methods
	 * @return
	 *         a map of all vectors associated to methods
	 */
	private static Map<Integer, RealVector> getMethodTermVectorMap(List<String> terms, Map<Integer, SourceMethod> methods, Map<String, List<StemMethod>> stems) {

		Map<Integer, RealVector> methodTermVectorMap = new HashMap<>();

		// First fill ensure all methods are registered in map
		for (SourceMethod method : methods.values()) {
			methodTermVectorMap.put(method.getKeyId(), new ArrayRealVector(terms.size()));
		}

		// Now Loop over all terms
		for (int i = 0; i < terms.size(); i++) {

			// Retrieve current terms and stems
			String currentTerm = terms.get(i);

			// If at least one stem method is found, 
			// the ième coordinate should be 1.0. Otherwise, should be 0.0
			if (stems.containsKey(currentTerm)) {

				// Count each method referring the term
				for (StemMethod stem : stems.get(currentTerm)) {

					SourceMethod method = methods.get(stem.getSourceMethodId());

					// Update term counter in current concept vector
					methodTermVectorMap.get(method.getKeyId()).setEntry(i, 1d);
				}
			}
		}

		return methodTermVectorMap;
	}

	/**
	 * Detect if a vector has all its components to 0.
	 * 
	 * @param v
	 *        the vector to check
	 * @return
	 *         true if zero vector, false otherwise
	 */
	private static boolean isZeroVector(RealVector v) {

		for (int i = 0; i < v.getDimension(); i++) {
			if (v.getEntry(i) != 0) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Detect if a vector has at least one component different than 0.
	 * 
	 * @param v
	 *        the vector to check
	 * @return
	 *         true if not zero vector, false otherwise
	 */
	private static boolean isNotZeroVector(RealVector v) {
		return !isZeroVector(v);
	}
}
