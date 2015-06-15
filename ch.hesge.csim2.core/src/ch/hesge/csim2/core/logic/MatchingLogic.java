/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hesge.csim2.core.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.exception.ZeroException;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.MatrixDimensionMismatchException;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.MatchingAlgorithm;
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
	 * @return
	 *         a map of (MethodId, List<MethodConceptMatch>)
	 */
	public static Map<Integer, List<MethodConceptMatch>> getMethodMatchingMap(Project project, MatchingAlgorithm matchAlgo) {

		List<MethodConceptMatch> matchings = new ArrayList<>();

		// Load concepts, methods, stem-concepts and stem-methods
		Map<Integer, Concept> conceptMap = ApplicationLogic.getConceptMap(project);
		Map<Integer, SourceMethod> methodMap = ApplicationLogic.getSourceMethodMap(project);
		Map<String, List<StemConcept>> stemConceptsMap = ApplicationLogic.getStemConceptByTermMap(project);
		Map<String, List<StemMethod>> stemMethodsMap = ApplicationLogic.getStemMethodByTermMap(project);

		List<Concept> concepts = new ArrayList<>(conceptMap.values());
		List<String> terms = new ArrayList<>(stemConceptsMap.keySet());

		RealMatrix weightMatrix = null;
		
		if (matchAlgo == MatchingAlgorithm.TFIDF) {
			weightMatrix = MatchingLogic.getTfIdfMatrix(terms, concepts, stemConceptsMap);
		}
		else if (matchAlgo == MatchingAlgorithm.ID_L1NORM) {
			weightMatrix = MatchingLogic.getWeightMatrix(terms, concepts, conceptMap, stemConceptsMap);
		}
		else if (matchAlgo == MatchingAlgorithm.ID_COSINE) {
			weightMatrix = MatchingLogic.getWeightMatrix(terms, concepts, conceptMap, stemConceptsMap);
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
					if (matchAlgo == MatchingAlgorithm.TFIDF) {
						similarity = conceptTermVector.cosine(methodTermVector);
					}
					else if (matchAlgo == MatchingAlgorithm.ID_L1NORM) {
						similarity = conceptTermVector.ebeMultiply(methodTermVector).getL1Norm();
					}
					else if (matchAlgo == MatchingAlgorithm.ID_COSINE) {
						similarity = conceptTermVector.cosine(methodTermVector);
					}

					// Register result within the matchMap
					if (similarity > 0) {

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

		// Now build a map of matching classified by method id

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
	 * 
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
	 * @param stems
	 *        the stems allowing links between terms and concepts
	 * @return
	 *         a terms/concepts weight matrix
	 */

	public static RealMatrix getWeightMatrix(List<String> terms, List<Concept> concepts, Map<Integer, Concept> conceptMap, Map<String, List<StemConcept>> stemByTermMap) {

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
	 * </pre>
	 * 
	 * @param terms
	 *        the terms used to compute weights
	 * @param concepts
	 *        the concepts used to compute weights
	 * @param stems
	 *        the stems allowing links between terms and concepts
	 * @return
	 *         a tf-idf matrix
	 */
	public static RealMatrix getTfIdfMatrix(List<String> terms, List<Concept> concepts, Map<String, List<StemConcept>> stems) {

		RealMatrix tfMatrix = computeTfMatrix(terms, concepts, stems);
		RealMatrix idfMatrix = computeIdfMatrix(terms, concepts, stems);

		return ebeMultiply(tfMatrix, idfMatrix);
	}

	/**
	 * <pre>
	 * 
	 *  Compute the tf matrix based on terms and concepts passed in argument.
	 *  
	 *  The tf matrix (term frequency by concept) is computed with:
	 *  
	 *  	TF	=	TO / TC
	 *  
	 * 	
	 *  where:
	 * 		  						+->  occurrence of term T1 in concept C2
	 * 								|
	 * 					|						|		
	 * 					|	3		3		0	|		T1		
	 * 		TO		=	|	5		0		7	|		T2		terms
	 * 					|	0		0		8	|		T3
	 * 					|	0		20		5	|		T4
	 * 					|						|
	 * 
	 * 						C1		C2		C3		--> concepts
	 * 
	 *  and:
	 * 		  						+->  total number of terms in C2
	 * 								|
	 * 					|						|				
	 * 		TC'		=	|	8		23		20	|		
	 * 					|						|
	 * 
	 * 						C1		C2		C3		--> concepts
	 * 
	 *  then:
	 *  
	 *   	TC is based on the duplication of row vector TC' (as much rows there are terms):
	 * 
	 * 					|						|		
	 * 					|	8		23		20	|		T1		
	 * 		TC		=	|	8		23		20	|		T2		terms
	 * 					|	8		23		20	|		T3
	 * 					|	8		23		20	|		T4
	 * 					|						|
	 * 
	 * 						C1		C2		C3		--> concepts
	 * 
	 *  finally:
	 *  
	 *  	we can now calculate TF = TO / TC (with matrix element-by-element division)
	 * 
	 * 		  						+-> relative weight of term T1 within concept C2
	 * 								|
	 * 					|							|		
	 * 					|	0.38	0.13	0		|		T1		
	 * 		TF		=	|	0.62	0		0.35	|		T2		terms
	 * 					|	0		0		0.4		|		T3
	 * 					|	0		0.87	0.25	|		T4
	 * 					|							|
	 * 
	 * 						C1		C2		C3		--> concepts
	 * 
	 * </pre>
	 * 
	 * @param terms
	 *        the terms used to compute frequencies
	 * @param concepts
	 *        the concepts used to compute frequencies
	 * @param stems
	 *        the stems allowing links between terms and concepts
	 * @return
	 *         a tf matrix
	 */
	private static RealMatrix computeTfMatrix(List<String> terms, List<Concept> concepts, Map<String, List<StemConcept>> stems) {

		// First build a concept map for speed
		Map<Integer, Concept> conceptMap = new HashMap<>();
		for (Concept concept : concepts) {
			conceptMap.put(concept.getKeyId(), concept);
		}

		RealMatrix toMatrix = MatrixUtils.createRealMatrix(terms.size(), concepts.size());
		RealMatrix tcMatrix = MatrixUtils.createRealMatrix(terms.size(), concepts.size());

		RealVector tcRowVector = new ArrayRealVector(concepts.size());

		// Loop over all terms
		for (int i = 0; i < terms.size(); i++) {

			// Retrieve current term
			String currentTerm = terms.get(i);

			// Loop over all concepts referring term
			for (StemConcept stem : stems.get(currentTerm)) {

				// Retrieve concept and index
				Concept concept = conceptMap.get(stem.getConceptId());
				int conceptIndex = concepts.indexOf(concept);

				// Adjust occurrence count in matrix
				toMatrix.addToEntry(i, conceptIndex, 1d);

				// Adjust total term count in vector
				tcRowVector.addToEntry(conceptIndex, 1d);
			}
		}

		// Now create the total count matrix
		for (int i = 0; i < tcMatrix.getRowDimension(); i++) {
			tcMatrix.setRowVector(i, tcRowVector);
		}

		// Return TF = TO /TC
		return ebeDivide(toMatrix, tcMatrix);
	}

	/**
	 * 
	 * <pre>
	 *  Compute the idf matrix based on terms and concepts passed in argument.
	 *  
	 *  The idf matrix (inverse term frequency within all concepts) 
	 *  is computed based on IDF' column vector, as follow:
	 *  
	 * 					|			|		
	 * 					|	0.38	|		T1		
	 * 		IDF'	=	|	0.62	|		T2		terms
	 * 					|	0		|		T3
	 * 					|	0		|		T4
	 * 					|			|
	 * 
	 *  where:
	 * 						   total-term-count
	 * 		IDF'	=	log(  ------------------  )
	 * 						    concept-count
	 * 
	 *  and:
	 *  
	 * 		total-term-count	= total number of term in space
	 * 		concept-count		= number of concepts referring the term Ti
	 * 		
	 *  then:
	 *  
	 *   	IDF is based on the duplication of column vector IDF' (as much column there are concepts):
	 * 
	 * 					|							|		
	 * 					|	0.38	0.38	0.38	|		T1		
	 * 		IDF		=	|	0.62	0.62	0.62	|		T2		terms
	 * 					|	0		0		0		|		T3
	 * 					|	0.12	0.12	0.12	|		T4
	 * 					|							|
	 * 
	 * 						C1		C2		C3		--> concepts
	 * 
	 * </pre>
	 * 
	 * @param terms
	 *        the terms used to compute inverse frequencies
	 * @param concepts
	 *        the concepts used to compute inverse frequencies
	 * @param stems
	 *        the stems allowing links between terms and concepts
	 * @return
	 *         a idf matrix
	 */
	private static RealMatrix computeIdfMatrix(List<String> terms, List<Concept> concepts, Map<String, List<StemConcept>> stems) {

		RealMatrix idfMatrix = MatrixUtils.createRealMatrix(terms.size(), concepts.size());

		// Create the idf vector
		RealVector idfColumnVector = new ArrayRealVector(terms.size());

		// Loop over all terms
		for (int i = 0; i < terms.size(); i++) {

			String currentTerm = terms.get(i);

			// Keep track of all concepts referring current term
			Set<Integer> conceptIds = new HashSet<>();

			// Retrieve all concepts associated to current term
			for (StemConcept stem : stems.get(currentTerm)) {
				conceptIds.add(stem.getConceptId());
			}

			// Compute idf value
			double totalcount = concepts.size();
			double df = conceptIds.size() + 1;
			double idfValue = Math.log10(totalcount / df);

			// Set idf-value within the resulting vector
			idfColumnVector.setEntry(i, idfValue);
		}

		// Now create the idf matrix
		for (int i = 0; i < idfMatrix.getColumnDimension(); i++) {
			idfMatrix.setColumnVector(i, idfColumnVector);
		}

		return idfMatrix;
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
	public static Map<Integer, RealVector> getMethodTermVectorMap(List<String> terms, Map<Integer, SourceMethod> methods, Map<String, List<StemMethod>> stems) {

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
	 * <pre>
	 * 
	 * 	Multiply element-by-element matrix content.
	 * 	This is the hadamard matrix product of two matrix in entry.
	 * 
	 * 	Note: both matrix must have same size.
	 * 
	 *  for instance:
	 * 
	 * 					|	3		3		0	|
	 * 		A	=		|	5		0		7	|
	 * 					|	0		0		8	|
	 * 			
	 * 					|	2		3		1	|
	 * 		B	=		|	3		1		2	|
	 * 					|	0		3		0	|
	 * 	
	 * 	then:		
	 * 			
	 * 					|	6		9		0	|
	 * 		A * B =		|	15		0		14 	|
	 * 					|	0		0		0	|
	 * 
	 * </pre>
	 * 
	 * @param a
	 *        the first matrix
	 * @param b
	 *        the second matrix
	 * @return
	 *         the hadamard matrix product
	 * @throws MatrixDimensionMismatchException
	 */
	private static RealMatrix ebeMultiply(RealMatrix a, RealMatrix b) throws MatrixDimensionMismatchException {

		MatrixUtils.checkAdditionCompatible(a, b);
		RealMatrix result = MatrixUtils.createRealMatrix(a.getRowDimension(), a.getColumnDimension());

		for (int i = 0; i < a.getRowDimension(); i++) {
			for (int j = 0; j < a.getColumnDimension(); j++) {
				result.setEntry(i, j, a.getEntry(i, j) * b.getEntry(i, j));
			}
		}

		return result;
	}

	/**
	 * <pre>
	 * 	Divide element-by-element matrix content.
	 * 	This is the hadamard matrix inverse product of two matrix in entry.
	 * 
	 * 	Note: both matrix must have same size and second matrix must not contains 0 elements.
	 * 
	 *  for instance:
	 * 
	 * 					|		3		3		0		|
	 * 		A	=		|		5		0		7		|
	 * 					|		0		0		8		|
	 * 			
	 * 					|		2		3		1		|
	 * 		B	=		|		3		1		2		|
	 * 					|		1		3		4		|
	 * 	
	 * 	then:		
	 * 			
	 * 					|		1.5		1		0		|
	 * 		A / B =		|		1.66	0		3.5		|
	 * 					|		0		0		2		|
	 * </pre>
	 * 
	 * @param a
	 *        the first matrix
	 * @param b
	 *        the second matrix
	 * @return
	 *         the hadamard matrix inverse product
	 * @throws MatrixDimensionMismatchException
	 */
	private static RealMatrix ebeDivide(RealMatrix a, RealMatrix b) throws MatrixDimensionMismatchException {

		MatrixUtils.checkAdditionCompatible(a, b);
		RealMatrix result = MatrixUtils.createRealMatrix(a.getRowDimension(), a.getColumnDimension());

		for (int i = 0; i < a.getRowDimension(); i++) {
			for (int j = 0; j < a.getColumnDimension(); j++) {
				if (b.getEntry(i, j) == 0) {
					throw new ZeroException();
				}
				result.setEntry(i, j, a.getEntry(i, j) / b.getEntry(i, j));
			}
		}

		return result;
	}

	/**
	 * Detect if a vector has all its components to 0.
	 * 
	 * @param v
	 *        the vector to check
	 * @return
	 *         true if zero vector, false otherwise
	 */
	public static boolean isZeroVector(RealVector v) {

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
	public static boolean isNotZeroVector(RealVector v) {
		return !isZeroVector(v);
	}
	
}
