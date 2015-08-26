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
import ch.hesge.csim2.core.model.StemMethod;
import ch.hesge.csim2.core.utils.SimpleMatrix;
import ch.hesge.csim2.core.utils.SimpleVector;
import ch.hesge.csim2.core.utils.StemMatrix;

/**
 * This engine allow matching calculation based
 * on the source-code, ontology item comparison, weighted by TFIDF.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 *
 */
public class TfidfMatcher implements IMethodConceptMatcher {

	// Private attributes
	private ApplicationLogic applicationLogic;

	/**
	 * Default constructor
	 */
	public TfidfMatcher() {
		applicationLogic = ApplicationLogic.UNIQUE_INSTANCE;
	}

	/**
	 * Get the engine name.
	 * @see ch.hesge.csim2.core.shell.IEngine#getName()
	 */
	@Override
	public String getName() {
		return "TfidfMatcher";
	}

	/**
	 * Get the engine version.
	 * @see ch.hesge.csim2.core.shell.IEngine#getVersion()
	 */
	@Override
	public String getVersion() {
		return "1.0.8";
	}

	/**
	 * Get the engine description
	 * @see ch.hesge.csim2.core.shell.IEngine#getDescription()
	 */
	@Override
	public String getDescription() {
		return "method concept matcher based on tfidf algorithm.";
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

		List<MethodConceptMatch> matchings = new ArrayList<>();

		// Retrieve concept and method map
		Map<Integer, Concept> conceptMap = applicationLogic.getConceptMap(project);
		Map<Integer, SourceMethod> methodMap = applicationLogic.getSourceMethodMap(project);

		// Retrieve stem map
		Map<String, List<StemConcept>> stemConceptsMap = applicationLogic.getStemConceptByTermMap(project);
		Map<String, List<StemMethod>> stemMethodsMap = applicationLogic.getStemMethodByTermMap(project);

		// Get linear concepts method and terms (used in matrix cols/rows)
		List<Concept> concepts = new ArrayList<>(conceptMap.values());
		List<SourceMethod> methods = new ArrayList<>(methodMap.values());
		List<String> conceptTerms = new ArrayList<>(stemConceptsMap.keySet());
		List<String> methodTerms = new ArrayList<>(stemMethodsMap.keySet());

		// Calculate TFIDF matrix for concepts
		StemMatrix<StemConcept> stemConceptMatrix = new StemMatrix<>(conceptTerms.size(), concepts.size());
		SimpleMatrix conceptTfidfMatrix = getConceptTfidfMatrix(conceptTerms, concepts, conceptMap, stemConceptsMap, stemConceptMatrix);

		// Calculate TFIDF matrix for methods
		StemMatrix<StemMethod> stemMethodMatrix = new StemMatrix<>(conceptTerms.size(), methods.size());
		SimpleMatrix methodTfidfMatrix = getMethodTfidfMatrix(conceptTerms, methodTerms, methods, methodMap, stemMethodsMap, stemMethodMatrix, conceptTfidfMatrix);

		// Scan all methods
		for (int i = 0; i < methods.size(); i++) {

			SourceMethod sourceMethod = methods.get(i);

			// Retrieve the associated tfidf vector
			SimpleVector tfidfMethodVector = methodTfidfMatrix.getColumnVector(i);

			// Lookup over all concept vectors
			for (int j = 0; j < conceptTfidfMatrix.getColumnDimension(); j++) {

				Concept concept = concepts.get(j);

				// Retrieve current concept vector
				SimpleVector tfidfConceptVector = conceptTfidfMatrix.getColumnVector(j);
				
				// Propagate tfidfConcept values to tfidfMethod
				tfidfMethodVector.ebeMultiply(tfidfConceptVector);

				// Skip null vectors
				if (!tfidfMethodVector.isNullVector() && !tfidfConceptVector.isNullVector()) {
					
					// Now calculate similarity between method and concept vectors
					double similarity = tfidfMethodVector.cosine(tfidfConceptVector);
					
					// If threshold is reached, register result within the matchMap
					if (similarity > threshold) {

						MethodConceptMatch match = new MethodConceptMatch();

						match.setProject(project);
						match.setSourceClass(sourceMethod.getSourceClass());
						match.setSourceMethod(sourceMethod);
						match.setConcept(concept);
						match.setWeight(similarity);

						// Gather concept and method stems found for matching
						for (int k = 0; k < conceptTerms.size(); k++) {

							if (tfidfMethodVector.getValue(k) > 0) {
								match.getStemConcepts().addAll(stemConceptMatrix.get(k, j));
								match.getStemMethods().addAll(stemMethodMatrix.get(k, i));
							}
						}

						matchings.add(match);
					}				
				}
			}
			

			/*
			// Check if terms in method contains the current term
			// If method vector is null, skip
			if (!tfidfMethodVector.isNullVector()) {

				// Select all concepts with similarity factor > 0
				for (int j = 0; j < conceptTfidfMatrix.getColumnDimension(); j++) {

					// Retrieve current concept vector
					Concept concept = concepts.get(j);
					SimpleVector termConceptVector = conceptTfidfMatrix.getColumnVector(j);

					// If concept vector is null, skip
					if (!termConceptVector.isNullVector()) {

						// Calculate similarity between method and concept vectors
						double similarity = tfidfMethodVector.cosine(termConceptVector);

						// Register result within the matchMap
						if (similarity > threshold) {

							MethodConceptMatch match = new MethodConceptMatch();

							match.setProject(project);
							match.setSourceClass(sourceMethod.getSourceClass());
							match.setSourceMethod(sourceMethod);
							match.setConcept(concept);
							match.setWeight(similarity);

							// Gather concept and method stems found for matching
							for (int k = 0; k < conceptTerms.size(); k++) {

								if (tfidfMethodVector.getValue(k) > 0) {
									match.getStemConcepts().addAll(stemConceptMatrix.get(k, j));
									match.getStemMethods().addAll(stemMethodMatrix.get(k, i));
								}
							}

							matchings.add(match);
						}
					}
				}
			}
			*/
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
	 * @param conceptTerms
	 *        the terms used to compute weights
	 * @param concepts
	 *        the concepts used to compute weights
	 * @param conceptMap
	 *        a map of all concepts classified by their keyId
	 * @param stemConceptsMap
	 *        the stems linking terms and concepts
	 * @param stemConceptMatrix
	 *        the stems used to compute weight in tfidf matrix
	 * @return
	 *         a tf-idf matrix
	 */
	private static SimpleMatrix getConceptTfidfMatrix(List<String> conceptTerms, List<Concept> concepts, Map<Integer, Concept> conceptMap, Map<String, List<StemConcept>> stemConceptsMap, StemMatrix<StemConcept> stemConceptMatrix) {

		SimpleVector totalTermInConcept = new SimpleVector(concepts.size());
		SimpleMatrix termOccurrenceInConcept = new SimpleMatrix(conceptTerms.size(), concepts.size());

		// Scan all terms and build an occurrence matrix.
		// For instance:
		// 					|						|
		// 					|	3		3		0	|		T1		
		//		TERM 	 =	|	1		0		1	|		T2		terms
		// 		OCCURR.		|	0		0		0	|		T3
		// 					|	0		5		2	|		T4
		// 					|						|
		// 
		//						C1		C2		C3		--> concepts

		for (int i = 0; i < conceptTerms.size(); i++) {

			// Retrieve current term
			String conceptTerm = conceptTerms.get(i);

			// If some stems have been found for this term
			if (stemConceptsMap.containsKey(conceptTerm)) {

				// Loop over all stems referring the term
				for (StemConcept stem : stemConceptsMap.get(conceptTerm)) {

					// Retrieve column associated to concept
					int conceptId = stem.getConceptId();
					Concept concept = conceptMap.get(conceptId);
					int j = concepts.indexOf(concept);

					// Count term occurrences in concept
					termOccurrenceInConcept.addValue(i, j, 1d);
					totalTermInConcept.addValue(j, 1d);
					stemConceptMatrix.add(i, j, stem);
				}
			}
		}

		// Calculate the term frequency: tf = termOccurrenceInConcept / totalTermInConcept
		SimpleMatrix tfMatrix = new SimpleMatrix(conceptTerms.size(), concepts.size());
		for (int i = 0; i < tfMatrix.getRowDimension(); i++) {
			SimpleVector occurrenceInConcept = termOccurrenceInConcept.getRowVector(i);
			SimpleVector tfRowVector = occurrenceInConcept.ebeDivide(totalTermInConcept);
			tfMatrix.setRowVector(i, tfRowVector);
		}

		// Calculate the inverse term frequency: idf = log( totalConceptCount / (1 + occurrenceInConcept )
		SimpleMatrix idfMatrix = new SimpleMatrix(conceptTerms.size(), concepts.size());
		for (int i = 0; i < idfMatrix.getRowDimension(); i++) {
			SimpleVector occurrenceInConcept = termOccurrenceInConcept.getRowVector(i);
			SimpleVector idfRowVector = new SimpleVector(idfMatrix.getColumnDimension());
			idfRowVector.setValues(Math.log(concepts.size() / (1d + occurrenceInConcept.getZeroNorm())));
			idfMatrix.setRowVector(i, idfRowVector);
		}

		// Finally calculate final matrix: tfidf = tf * idf
		SimpleMatrix tfidfMatrix = new SimpleMatrix(conceptTerms.size(), concepts.size());
		tfidfMatrix = tfMatrix.ebeMultiply(idfMatrix);

		return tfidfMatrix;
	}

	/**
	 * <pre>
	 * 
	 * Compute the tf-idf matrix based on terms and methods passed in argument.
	 * The matrix returned contains the exact same terms (same ordering) as the concept tfidf matrix.
	 * This is required as conceptTfidf and methodTfidf matrixes will be used to compare each other their column-vectors.
	 * 
	 * For instance:
	 * 
	 * 		  						+->  term presence/absence within the method
	 * 								|
	 * 					|						|		
	 * 					|	0		1		0	|		T1		
	 * 		TF-IDF	=	|	1		1		0	|		T2		terms
	 * 					|	0		0		0	|		T3
	 * 					|	1		0		0	|		T4
	 * 					|						|
	 * 
	 * 						M1		M2		M3		--> method
	 * 
	 *
	 * </pre>
	 * 
	 * @param conceptTerms
	 *        the terms used to order rows
	 * @param methodTerms
	 *        the terms used to compute weights
	 * @param methods
	 *        the methods used to compute weights
	 * @param methodMap
	 *        a map of all methods classified by their keyId
	 * @param stemMethodsMap
	 *        the stems linking terms and methods
	 * @param stemMethodMatrix
	 *        the stems used to compute weight in tfidf matrix
	 * @return
	 *         a tf-idf matrix
	 */
	private static SimpleMatrix getMethodTfidfMatrix(List<String> conceptTerms, List<String> methodTerms, List<SourceMethod> methods, Map<Integer, SourceMethod> methodMap, Map<String, List<StemMethod>> stemMethodsMap, StemMatrix<StemMethod> stemMethodMatrix, SimpleMatrix termConceptMatrix) {

		SimpleMatrix tfidfMatrix = new SimpleMatrix(conceptTerms.size(), methods.size());

		// Scan all concept terms
		for (int i = 0; i < conceptTerms.size(); i++) {
			
			// Retrieve current term
			String conceptTerm = conceptTerms.get(i);
			
			// If the term is used in method and has some method stems
			if (methodTerms.contains(conceptTerm) && stemMethodsMap.containsKey(conceptTerm)) {
				
				// Loop over all method stems referring the term
				for (StemMethod stem : stemMethodsMap.get(conceptTerm)) {
					
					// Retrieve column associated to method
					int methodId = stem.getSourceMethodId();
					SourceMethod method = methodMap.get(methodId);
					int j = methods.indexOf(method);
					
					// Init cell with 1.0 (cell with no method matching term are marked by 0.0)
					tfidfMatrix.setValue(i, j, 1d);
					stemMethodMatrix.add(i, j, stem);
				}
			}
		}

		return tfidfMatrix;
	}
}
