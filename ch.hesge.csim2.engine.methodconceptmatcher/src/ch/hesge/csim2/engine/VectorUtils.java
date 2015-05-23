package ch.hesge.csim2.engine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.SourceMethod;
import ch.hesge.csim2.core.model.StemConcept;
import ch.hesge.csim2.core.model.StemMethod;

/**
 * Utility class related to concept/method matching & analysis.
 * 
 * Copyright HEG Geneva 2015, Switzerland
 * 
 * @author Eric Harth
 *
 */
public class VectorUtils {

	/**
	 * Create and initialize a vector.
	 * 
	 * @param size
	 * @return a zero vector
	 */
	public static Vector<Double> createVector(int size) {
		
		Vector<Double> vector = new Vector<>(size);
		
		for (int i = 0; i < size; i++) {
			vector.add(0d);
		}
		
		return vector;
	}
	
	/**
	 * <pre>
	 * Returns a new vector that corresponds to the hadamard product 
	 * of the two vectors passed in argument (multiplication of each components).
	 * 
	 *  	hadamard-product(a,b) = {ai * bi}
	 *  
	 *  	where 
	 *  		a = {ai}
	 *  		b = {bi}
	 * 
	 *  	exemple:
	 * 
	 * 				v1		*	v2		=	v
	 * 
	 * 				(5)		*	(3)			(15)
	 * 				(3)		*	(2)		=	(6)
	 * 				(4) 	*	(1)			(4)
	 * 
	 * </pre>
	 * 
	 * @param a
	 *            the first column vector
	 * @param b
	 *            the second column vector
	 * @return the resulting hadamar vector
	 */
	public static Vector<Double> getHadamardProduct(Vector<Double> a, Vector<Double> b) {

		if (a.size() != b.size()) {
			throw new RuntimeException("incompatible vector size");
		}

		Vector<Double> product = VectorUtils.createVector(a.size());

		for (int i = 0; i < a.size(); i++) {
			product.set(i, a.get(i) * b.get(i));
		}

		return product;
	}

	/**
	 * <pre>
	 * Returns a number that corresponds to the scalar product 
	 * of the two vectors passed in argument.
	 * 
	 *  	scalar-product = sum(ai * bi)
	 *  
	 *  	where:
	 *  		a = {ai}
	 *  		b = {bi}
	 * 
	 *  	exemple:
	 * 
	 * 			v1		*	v2		=	result
	 * 
	 * 			(5)			(3)			
	 * 			(3)		*	(2)		=	25
	 * 			(4) 		(1)
	 * 
	 * </pre>
	 * 
	 * @param a
	 *            the first column vector
	 * @param b
	 *            the second column vector
	 * @return the resulting hadamar vector
	 */
	public static double getScalarProduct(Vector<Double> a, Vector<Double> b) {

		if (a.size() != b.size()) {
			throw new RuntimeException("incompatible vector size");
		}

		double dotProduct = 0;

		for (int i = 0; i < a.size(); i++) {
			dotProduct += a.get(i) * b.get(i);
		}

		return dotProduct;
	}

	/**
	 * <pre>
	 * Returns the euclidian length of the vector passed in argument.
	 * 
	 *  	euclidian-length = sqrt(ai^2)
	 *  
	 *  	where:
	 *  		a = {ai}
	 * 
	 *  	exemple:
	 * 
	 * 			v		=>		length(v)		
	 * 
	 * 			(5)						
	 * 			(3)		=>		sqrt(5*5 + 3*3 + 4*4) = sqrt(50) = 7.07
	 * 			(4)
	 * 
	 * </pre>
	 * 
	 * @param v
	 *            a column vector
	 * @return the length of vector v
	 */
	public static double getLength(Vector<Double> v) {

		double length = 0;

		for (int i = 0; i < v.size(); i++) {
			length += v.get(i) * v.get(i);
		}

		return Math.sqrt(length);
	}

	/**
	 * <pre>
	 * Returns a vector of idf-values associated to a list of term. 
	 * Basically idf represent the inverse document frequency of a term within a corpus of documents.
	 * 
	 * In our case, we consider:
	 * 
	 *  	concept = document.
	 * 
	 * 	So, for a single term t, we calculate idf(t) as:
	 * 
	 * 		totalConceptCount	= total number of concepts
	 * 		conceptCount(t)		= number of concepts referring term t
	 * 
	 * 		idf(t) 	= log( totalConceptCount / 1 + conceptCount(t) ) 
	 * 
	 * And as we have a list of term, we should calculate an array of idf values (as a vector)
	 * which correspond to the inverse document frequencies of a list of term among concept stems.
	 * 
	 * </pre>
	 *
	 * @param terms
	 *            the list of term used to compute frequencies
	 * @param stems
	 *            the concept stem to analyze
	 * @param concepts
	 *            a concept map
	 * @return the idf vector representing all frequencies associated to the term list
	 * @return
	 */

	public static Vector<Double> getIdfVector(List<String> terms, Map<String, List<StemConcept>> stems, Map<Integer, Concept> concepts) {

		Vector<Double> idfVector = VectorUtils.createVector(terms.size());

		// Lookup each term
		for (int i = 0; i < terms.size(); i++) {

			// Set keeping track of concept referring current term
			HashSet<Integer> uniqueIdSet = new HashSet<>();

			// Retrieve current terms and stems
			String currentTerm = terms.get(i);
			List<StemConcept> stemConcepts = stems.get(currentTerm);

			// Scan each stem associated to current term
			for (StemConcept stem : stemConcepts) {
				uniqueIdSet.add(stem.getConceptId());
			}

			// Compute idf value
			double totalConceptCount = concepts.size();
			double referringConcepts = uniqueIdSet.size();
			double idfValue = Math.log10(totalConceptCount / (1 + referringConcepts));

			// Set idf-value within the resulting vector
			idfVector.set(i, idfValue);
		}

		return idfVector;
	}

	/**
	 * <pre>
	 * Returns a map of tf vectors associated to concepts. 
	 * Each vector represents the term frequencies, among a concept, of the list of term passed in argument.
	 * 
	 * In our case, we consider:
	 * 
	 *  	concept = document.
	 * 
	 * 	So, for a single concept c, we calculate tf(t,c) as:
	 * 
	 * 		totalTermCount(c)	= total number of terms among concept
	 * 		termCount(t,c)		= number of occurrence of the term t among the concept c
	 * 
	 * Then:
	 * 
	 * 		tf(t,c)			= termCount(t,c) / totalTermCount(c)	=>		tf(t,c) € [0, 1]
	 * 
	 * </pre>
	 * 
	 * @param terms
	 *            the list of term used to compute frequencies
	 * @param stems
	 *            the concept stem to analyze
	 * @param concepts
	 *            a map of concepts
	 * @return the map of concept tf vectors
	 */
	public static Map<Concept, Vector<Double>> getTfcVectorMap(List<String> terms, Map<String, List<StemConcept>> stems, Map<Integer, Concept> concepts) {

		Map<Concept, Vector<Double>> tfMap = new HashMap<>();

		// Lookup each term
		for (int i = 0; i < terms.size(); i++) {

			// Retrieve current terms and stems
			String currentTerm = terms.get(i);
			List<StemConcept> stemConcepts = stems.get(currentTerm);

			// Count each concept referring the term
			for (StemConcept stem : stemConcepts) {

				Concept concept = concepts.get(stem.getConceptId());

				// Create a new entry for the concept
				if (!tfMap.containsKey(concept)) {
					tfMap.put(concept, VectorUtils.createVector(terms.size()));
				}

				// Update term counter in current concept vector
				tfMap.get(concept).set(i, tfMap.get(concept).get(i) + 1);
			}
		}

		// Now normalize each vector associated to a concept
		for (Vector<Double> tf : tfMap.values()) {

			double totalValue = 0;

			// Retrieve sum of vector components
			for (int i = 0; i < tf.size(); i++) {
				totalValue += tf.get(i);
			}

			// Adjust absolute frequency to relative one
			for (int i = 0; i < tf.size(); i++) {
				tf.set(i, tf.get(i) / totalValue);
			}
		}

		return tfMap;
	}

	/**
	 * <pre>
	 * Returns a map of tf vectors associated to methods. 
	 * Each vector represents the term frequencies, among a method, of the list of term passed in argument.
	 * 
	 * In our case, we consider:
	 * 
	 *  	method = document.
	 * 
	 * 	So, for a single method m, we calculate tf(t,m) as:
	 * 
	 * 		totalTermCount(m)	= total number of terms among method
	 * 		termCount(t,m)		= number of occurrence of the term t among the method m
	 * 
	 * Then:
	 * 
	 * 		tf(t,m)			= termCount(t,m) / totalTermCount(m)	=>		tf(t,m) € [0, 1]
	 * 
	 * </pre>
	 * 
	 * @param terms
	 *            the list of term used to compute frequencies
	 * @param stems
	 *            the method stem to analyze
	 * @param methods
	 *            a map of methods
	 * @return the map of method tf vectors
	 */
	public static Map<SourceMethod, Vector<Double>> getTfmVectorMap(List<String> terms, Map<String, List<StemMethod>> stems, Map<Integer, SourceMethod> methods) {

		Map<SourceMethod, Vector<Double>> tfMap = new HashMap<>();

		// Lookup each term
		for (int i = 0; i < terms.size(); i++) {

			// Retrieve current terms and stems
			String currentTerm = terms.get(i);
			List<StemMethod> stemMethods = stems.get(currentTerm);

			// Count each method referring the term
			for (StemMethod stem : stemMethods) {

				SourceMethod method = methods.get(stem.getSourceMethodId());

				// Create a new entry for the method
				if (!tfMap.containsKey(method)) {
					tfMap.put(method, VectorUtils.createVector(terms.size()));
				}

				// Update term counter in current concept vector
				tfMap.get(method).set(i, tfMap.get(method).get(i) + 1);
			}
		}

		// Now normalize each vector associated to a method
		for (Vector<Double> tf : tfMap.values()) {

			double totalValue = 0;

			// Retrieve sum of vector components
			for (int i = 0; i < tf.size(); i++) {
				totalValue += tf.get(i);
			}

			// Adjust absolute frequency to relative one
			for (int i = 0; i < tf.size(); i++) {
				tf.set(i, tf.get(i) / totalValue);
			}
		}

		return tfMap;
	}
}
