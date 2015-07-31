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
import ch.hesge.csim2.core.utils.Console;

/**
 * This engine allow matching calculation based
 * on the Levenshtein similarity measure.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 *
 */
public class LevenshteinMatcher implements IMethodConceptMatcher {

	// Private attributes
	private ApplicationLogic applicationLogic;

	/**
	 * Default constructor
	 */
	public LevenshteinMatcher() {
		applicationLogic = ApplicationLogic.UNIQUE_INSTANCE;
	}

	/**
	 * Get the engine name.
	 * @see ch.hesge.csim2.core.shell.IEngine#getName()
	 */
	@Override
	public String getName() {
		return "LevenshteinMatcher";
	}

	/**
	 * Get the engine version.
	 * @see ch.hesge.csim2.core.shell.IEngine#getVersion()
	 */
	@Override
	public String getVersion() {
		return "1.0.10";
	}

	/**
	 * Get the engine description
	 * @see ch.hesge.csim2.core.shell.IEngine#getDescription()
	 */
	@Override
	public String getDescription() {
		return "method concept matcher based on levenshtein comparison.";
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

		double similarity = 0d;

		// Retrieve method terms
		StemMethod methodRootStem = stemMethodTreeMap.get(method.getKeyId());
		List<StemMethod> methodStems = applicationLogic.inflateStemMethods(methodRootStem);
		
		// Retrieve concept terms
		StemConcept conceptRootStem = stemConceptTreeMap.get(concept.getKeyId());
		List<StemConcept> conceptStems = applicationLogic.inflateStemConcepts(conceptRootStem);

		Console.writeDebug(this, "computing levenshtein coefficient:"); 
		Console.writeDebug(this, "  method: " + method.getSourceClass().getName() + "." + method.getSignature());
		Console.writeDebug(this, "  concept: " + concept.getName()); 
		
		// Scan all method & method stems
		for (StemMethod stemMethod : methodStems) {
			for (StemConcept stemConcept : conceptStems) {
				
				// Compute the levenshtein coefficient for current method/concept stem
				double simTerm = computeLevenshteinCoefficient(stemMethod.getTerm(), stemConcept.getTerm());
				
				Console.writeDebug(this, "  terms: (" + stemMethod.getTerm() + ", " + stemConcept.getTerm() + "), similarity: " + simTerm); 
				
				// Compute average weight globally to all stems
				similarity += simTerm / (methodStems.size() * conceptStems.size());
			}
		}
		
		Console.writeDebug(this, "  similarity: " + similarity);
		
		return similarity;
	}
	
	/**
	 * <code>
	 * Compute the edit distance with the levenshtein algorithm.
	 * References:
	 * 		https://en.wikipedia.org/wiki/Levenshtein_distance
	 * 		https://en.wikipedia.org/wiki/Wagner%E2%80%93Fischer_algorithm
	 * 
	 * For instance:
	 * 
	 * 		edit operations required to transform KITTEN to SITTING:
	 * 
	 * 		1. substitute K by S		KITTEN	->	SITTEN
	 * 		2. substitute E by I		SITTEN	->	SITTIN
	 * 		3. insert a G at the end	SITTING	->	SITTING
	 * 
	 * 		we made 5 operations to transform original string
	 * 
	 * 			=>	cost = 5
	 * 
	 * Algorithm:
	 * 
	 * 		we want 
	 * 			transform str1 to str2
	 * 		and 
	 * 			m = str1.size()
	 * 			n = str2.size() 
	 * 		
	 * 			1. create an M {m+1,n+1} matrix, initialized with:
	 * 
	 * 					first row numbered from 0 to n 
	 * 					first column numbered from 0 to m
	 *  
	 * 				for instance
	 * 
	 * 										S	I	T	T	I	N	G
	 *	 
	 *  								0	1	2	3	4	5	6	7
	 * 							K		1	0	0	0	0	0	0	0
	 * 							I		2	0	0	0	0	0	0	0
	 * 				M	=		T		3	0	0	0	0	0	0	0
	 * 							T		4	0	0	0	0	0	0	0
	 * 							E		5	0	0	0	0	0	0	0
	 * 							N		6	0	0	0	0	0	0	0	<-- source string
	 * 
	 * 			2. calculate M[i,j] values with:
	 * 
	 * 
	 * 									|	M[i-1, j] + 1 				(deletion)
	 * 				M[i,j]	= 	min		|	M[i, j-1] + 1				(insertion)
	 * 									|	M[i-1, j-1] + COST[i, j]	(substitution)
	 * 
	 * 				with:
	 * 					COST[i,j]	= 	1, if str1(i) != str2(j), otherwise 0

	 * 				for instance
	 * 
	 * 										S	I	T	T	I	N	G
	 *	 
	 *  								0	1	2	3	4	5	6	7
	 * 							K		1	1	2	3	4	5	6	7
	 * 							I		2	2	1	2	3	4	5	6
	 * 				M	=		T		3	3	2	1	2	3	4	5
	 * 							T		4	4	3	2	1	2	3	4
	 * 							E		5	5	4	3	2	2	3	4
	 * 							N		6	6	5	4	3	3	2	3		<=	cost
	 * 		
	 * 			4. the lower right cell value is the levenshtein cost
	 * 
	 * Explanation:
	 * 												|
	 * 										S	I	|	T	T	I	N	G
	 *	 											|
	 *  								0	1	2	|	3	4	5	6	7
	 * 							K		1	1	2	|	3	4	5	6	7
	 * 							I		2	2	1	|	2	3	4	5	6
	 * 				M	=		T		3	3	2	|	1	2	3	4	5
	 * 						------------------------+
	 * 							T		4	4	3	2	1	2	3	4
	 * 							E		5	5	4	3	2	2	3	4
	 * 							N		6	6	5	4	3	3	2	3		<=	cost
	 * 
	 * 
	 * 			The submatrix marked, implies:
	 * 
	 * 				- we can pass from KIT to SI with 2 operations
	 * 				- going one column to the right  =>  char inserting (with cost 1)
	 * 				- going one row down             =>  char deleting (with cost 1)
	 * 				- going in diagonal right, down  =>  substitution if chars are differents (with cost 1)
	 * 				- going in diagonal right, down  =>  no operation if chars are identicals (with cost 0)
	 * 
	 * 
	 * Pseudo code:
	 * 
	 * 		int EditDistance(char s[1..m], char t[1..n])
	 * 
	 * 			let d be a 2-d array of int with dimensions [0..m, 0..n]
	 * 			
	 * 			for i in [0..m]
	 * 				d[i, 0] <- i // the distance of any first string to an empty second string
	 * 			for j in [0..n]
	 * 				d[0, j] <- j // the distance of any second string to an empty first string
	 * 
	 * 			for j in [1..n]
	 * 				for i in [1..m]
	 * 
	 * 					if s[i] = t[j] then 
	 * 						d[i, j] <-	 d[i-1, j-1]       // no operation required
	 * 					else
	 * 						let be cost1 <- d[i-1, j] + 1,  // a deletion
	 * 						let be cost2 <- d[i, j-1] + 1,  // an insertion
	 * 						let be cost3 <- d[i-1, j-1] + 1 // a substitution
	 * 
	 * 						d[i, j] <- minimum of cost1, cost2, cost3
	 * 
	 * 			return d[m,n]
	 * 		
	 * </code>
	 * 
	 * @param firstTerm
	 * @param secondTerm
	 * @return
	 */
	private double computeLevenshteinCoefficient(String a, String b) {

		a = a.toLowerCase();
		b = b.toLowerCase();
		int[][] costs = new int[a.length() + 1][b.length() + 1];

		// Initialize first column
		for (int i = 0; i < a.length() + 1; i++) {
			costs[i][0] = i;
		}

		// Initialize first row
		for (int j = 0; j < b.length() + 1; j++) {
			costs[0][j] = j;
		}

		// Compute cell's value
		for (int i = 1; i < a.length() + 1; i++) {
			for (int j = 1; j < b.length() + 1; j++) {

				if (a.charAt(i - 1) == b.charAt(j - 1)) {
					costs[i][j] = costs[i - 1][j - 1];
				}
				else {
					int cost1 = costs[i - 1][j] + 1; // a deletion
					int cost2 = costs[i][j - 1] + 1; // an insertion
					int cost3 = costs[i - 1][j - 1] + 1; // a substitution

					// Take the less costly
					costs[i][j] = Math.min(Math.min(cost1, cost2), cost3);
				}
			}
		}
		
		// Now normalize result between [0..1]
		int editCost = costs[a.length()][b.length()];
		int maxLength = Math.max(a.length(), b.length());
		double disimilarity = ((double) editCost / maxLength);
				
		// Make it a similarity extents
		return 1d - disimilarity;
	}
	
	/**
	 * Test method
	 * @param args
	 */
	public static void main(String[] args) {

		/*
		String term1 = "levenshtein";
		String term2 = "meilenstein";
		*/
		String term1 = "levenshtein";
		String term2 = "meilenstein";
		LevenshteinMatcher matcher = new LevenshteinMatcher();
		
		double cost = matcher.computeLevenshteinCoefficient(term1,  term2);
		
		System.out.println("similarity: " + cost);
		
	}
	
}
