package ch.hesge.csim2.engine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.SourceMethod;
import ch.hesge.csim2.core.model.StemConcept;
import ch.hesge.csim2.core.model.StemMethod;

public class MethodConceptMatcherUtils {

	/**
	 * <pre>
	 * 
	 * Compute the tf-idf matrix based on terms and concepts passed in argument.
	 * 
	 * For instance:
	 * 
	 * 			T1		0.3		3		0
	 * 			T2		1.5		0		7
	 * 			T3		0		0		8
	 * 			T4		0		20		5
	 * 
	 * 					C1		C2		C3
	 * 
	 * where:
	 * 
	 * 		a(i,j) = tf-idf value for term i and concept j
	 * 
	 * </pre>
	 * 
	 * @param terms
	 *        the terms used to compute frequencies
	 * @param concepts
	 *        the concepts used to compute frequencies
	 * @param stems
	 *        the stems allowing links between term and concepts
	 * @return
	 *         a tf-idf matrix
	 */
	public static RealMatrix getTfIdfMatrix(List<String> terms, List<Concept> concepts, Map<String, List<StemConcept>> stems) {

		RealMatrix tfMatrix = getTfMatrix(terms, concepts, stems);
		RealMatrix idfMatrix = getIdfMatrix(terms, concepts, stems);
		return getHadamardProduct(tfMatrix, idfMatrix);
	}

	public static Map<Integer, RealVector> getMethodVectorMap(List<String> terms, Map<Integer, SourceMethod> methods, Map<String, List<StemMethod>> stems) {
		
		Map<Integer, RealVector> methodVectorMap = new HashMap<>();
		
		// Loop over all terms
		for (int i = 0; i < terms.size(); i++) {

			// Retrieve current terms and stems
			String currentTerm = terms.get(i);

			if (stems.containsKey(currentTerm)) {
				
				// Count each method referring the term
				for (StemMethod stem : stems.get(currentTerm)) {

					SourceMethod method = methods.get(stem.getSourceMethodId());

					// Create a new entry for the concept
					if (!methodVectorMap.containsKey(method.getKeyId())) {
						methodVectorMap.put(method.getKeyId(), new ArrayRealVector(terms.size()));
					}

					// Update term counter in current concept vector
					methodVectorMap.get(method.getKeyId()).setEntry(i, 1d);
				}
			}
		}
		
		return methodVectorMap;
	}
	
	/**
	 * <pre>
	 *  Compute the tf matrix based on terms and concepts passed in argument.
	 *  
	 *  The tf matrix (term frequency by concept) is computed with:
	 *  
	 *  	TF	=	TO / TCF
	 *  
	 *  where:
	 * 
	 * 		TO	=	term occurrence by concept	=	matrix
	 * 
	 * 			T1		3		3		0
	 * 			T2		5		0		7
	 * 			T3		0		0		8
	 * 			T4		0		20		5
	 * 
	 * 					C1		C2		C3
	 * 
	 * 		TC	=	total term count for each concepts	= row vector
	 * 
	 * 					8		23		20
	 * 
	 * 					C1		C2		C3
	 * 
	 *  then:
	 * 
	 * 		TF	=	TO / TCF (row by row)
	 * 
	 * 			T1		0.38	0.13	0
	 * 			T2		0.62	0		0.35
	 * 			T3		0		0		0.4
	 * 			T4		0		0.87	0.25
	 * 
	 * 					C1		C2		C3
	 * 
	 * 
	 * </pre>
	 */
	private static RealMatrix getTfMatrix(List<String> terms, List<Concept> concepts, Map<String, List<StemConcept>> stems) {

		RealMatrix toMatrix = MatrixUtils.createRealMatrix(terms.size(), concepts.size());
		RealVector tcVector = new ArrayRealVector(concepts.size());

		Map<Integer, Concept> conceptMap = new HashMap<>();
		for (Concept concept : concepts) {
			conceptMap.put(concept.getKeyId(), concept);
		}
		
		// Loop over all terms
		for (int i = 0; i < terms.size(); i++) {

			// Retrieve current term
			String currentTerm = terms.get(i);

			// Loop over all concept referring term
			for (StemConcept stem : stems.get(currentTerm)) {

				Concept concept = conceptMap.get(stem.getConceptId());

				// Retrieve concept column
				int conceptIndex = concepts.indexOf(concept);

				// count term occurrences in matrix
				toMatrix.addToEntry(i, conceptIndex, 1d);

				// count term count in vector
				tcVector.addToEntry(conceptIndex, 1d);
			}
		}

		RealMatrix tfMatrix = MatrixUtils.createRealMatrix(terms.size(), concepts.size());

		// Compute row by row tf = to / tc
		for (int i = 0; i < toMatrix.getRowDimension(); i++) {

			RealVector toVector = toMatrix.getRowVector(i);
			RealVector tfVector = toVector.ebeDivide(tcVector);

			tfMatrix.setRowVector(i, tfVector);
		}

		return tfMatrix;
	}

	/**
	 * 
	 * <pre>
	 * Returns a vector of idf-values associated to a list of term.
	 *  
	 * Basically idf vector represents for each term a value that allows
	 * measuring the term relevance among all concepts.
	 * 
	 * For a specific term, 
	 * 		a great idf value indicated that the term is discriminant in search.
	 * 		a small idf value indicated that the term is not really important.
	 * 
	 * For each term t, we calculate idf(t) as follow:
	 * 
	 * 		totalCount		= number of concepts in space
	 * 		conceptCount(t)	= number of concepts referring the term t
	 * 
	 * 			idf(t) 	= log( totalCount / conceptCount(t) )
	 * 
	 *  	and as conceptCount(t) can be zero, 
	 *  	we rewrite the formula to get:
	 * 
	 * 			idf(t) 	= log( totalCount / (conceptCount(t) + 1) )
	 * 
	 * The vector returned contains all idf values for each term passed in argument (same order).
	 * </pre>
	 * 
	 * @param terms
	 * @param concepts
	 * @param stems
	 * @return
	 */
	private static RealMatrix getIdfMatrix(List<String> terms, List<Concept> concepts, Map<String, List<StemConcept>> stems) {

		// Create the idf vector
		RealVector idfVector = new ArrayRealVector(terms.size());

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
			idfVector.setEntry(i, idfValue);
		}

		RealMatrix idfMatrix = MatrixUtils.createRealMatrix(terms.size(), concepts.size());

		// Generate as column there is concepts in matrix
		for (int i = 0; i < idfMatrix.getColumnDimension(); i++) {
			idfMatrix.setColumnVector(i, idfVector);
		}

		return idfMatrix;
	}

	/**
	 * <pre>
	 * Returns a new matrix that corresponds to the hadamard product of two matrix.
	 * That is element-by-element multiplication. The matrix must be of same dimension.
	 * 
	 *  For instance:
	 * 
	 * 		A	=
	 * 
	 * 			3		3		0
	 * 			5		0		7
	 * 			0		0		8
	 * 			0		20		5
	 * 
	 * 		B	=
	 * 
	 * 			2		3		1
	 * 			3		1		2
	 * 			0		3		0
	 * 			3		20		3
	 * 
	 * 
	 * 		A * B = 
	 * 
	 * 			9		9		0
	 * 			25		0		14
	 * 			0		0		0
	 * 			0		40		15
	 * 
	 * </pre>
	 * 
	 * @param a
	 *        the first matrix
	 * @param b
	 *        the second matrix
	 * @return
	 *         the hadamard resulting matrix
	 */
	private static RealMatrix getHadamardProduct(RealMatrix a, RealMatrix b) {

		RealMatrix result = MatrixUtils.createRealMatrix(a.getRowDimension(), a.getColumnDimension());

		for (int i = 0; i < a.getRowDimension(); i++) {
			for (int j = 0; j < a.getColumnDimension(); j++) {
				result.setEntry(i, j, a.getEntry(i, j) * b.getEntry(i, j));
			}
		}

		return result;
	}

}
