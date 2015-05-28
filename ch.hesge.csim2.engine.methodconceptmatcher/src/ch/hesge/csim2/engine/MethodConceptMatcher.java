/**
 * 
 */
package ch.hesge.csim2.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import ch.hesge.csim2.core.logic.ApplicationLogic;
import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.Context;
import ch.hesge.csim2.core.model.IEngine;
import ch.hesge.csim2.core.model.MethodConceptMatch;
import ch.hesge.csim2.core.model.Ontology;
import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.SourceMethod;
import ch.hesge.csim2.core.model.StemConcept;
import ch.hesge.csim2.core.model.StemMethod;
import ch.hesge.csim2.core.utils.Console;
import ch.hesge.csim2.core.utils.EngineException;
import ch.hesge.csim2.core.utils.StringUtils;
import ch.hesge.csim2.engine.conceptmapper.RddaMethodConceptMatch;
import ch.hesge.csim2.engine.conceptmapper.TermVectorBuilder;

/**
 * This engine is responsible to compute score for all stem in system (stem and
 * concept).
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 *
 */
public class MethodConceptMatcher implements IEngine {

	// Private attributes
	private Project project;
	private Context context;
	private String matchingAlgorithm;

	/**
	 * Default constructor.
	 */
	public MethodConceptMatcher() {
	}

	/**
	 * Get the engine name.
	 * 
	 * @see ch.hesge.csim2.core.shell.IEngine#getName()
	 */
	@Override
	public String getName() {
		return "MethodConceptMatcher";
	}

	/**
	 * Get the engine version.
	 * 
	 * @see ch.hesge.csim2.core.shell.IEngine#getVersion()
	 */
	@Override
	public String getVersion() {
		return "1.0.0";
	}

	/**
	 * Get the engine description.
	 * 
	 * @see ch.hesge.csim2.core.shell.IEngine#getDescription()
	 */
	@Override
	public String getDescription() {
		return "match method and concept with TFIDF metrics.";
	}

	/**
	 * Return the parameter map required by the engine.
	 * 
	 * @see ch.hesge.csim2.core.shell.IEngine#getParameters()
	 */
	@Override
	public Properties getParameters() {

		Properties params = new Properties();
		params.put("project", "project");
		params.put("algo", "algo");
		return params;
	}

	/**
	 * Retrieve the engine context.
	 * 
	 * @see ch.hesge.csim2.core.shell.IEngine#getContext()
	 */
	@Override
	public Context getContext() {
		return this.context;
	}

	/**
	 * Sets the engine context before starting.
	 * 
	 * @see ch.hesge.csim2.core.shell.IEngine#setContext()
	 */
	@Override
	public void setContext(Context context) {
		this.context = context;
	}

	/**
	 * Initialize the engine before starting.
	 * 
	 * @see ch.hesge.csim2.core.shell.IEngine#init()
	 */
	@Override
	public void init() {

		try {

			// Retrieve current project
			if (context.containsKey("project")) {
				project = (Project) context.getProperty("project");
			}
			else {
				throw new EngineException("missing project specified !");
			}

			// Retrieve algorithm used for matching
			if (context.containsKey("algo")) {
				matchingAlgorithm = (String) context.getProperty("algo");
			}
			else {
				matchingAlgorithm = "phd";
			}
		}
		catch (Exception e) {
			Console.writeError("error while instrumenting files: " + StringUtils.toString(e));
		}
	}

	/**
	 * Start the engine.
	 * 
	 * @see ch.hesge.csim2.core.shell.IEngine#start()
	 * 
	 *      Scan all project sources and identify all concepts associated to
	 *      each methods.
	 */
	@Override
	public void start() {

		try {

			Console.writeLine("loading ontologies...");

			// Load project and its ontologies
			ApplicationLogic.loadProject(project);

			Console.writeLine("cleaning previous matches...");

			// Clean project matchings
			ApplicationLogic.deleteMatching(project);

			List<MethodConceptMatch> matchings = null;

			// Compute method-concept matching
			if (matchingAlgorithm.equals("phd")) {
				matchings = computeMatchingWithPhd();
			}
			else {
				matchings = computeMatchingWithEha();
			}

			for (MethodConceptMatch match : matchings) {
				System.out.println("  " + "method: " + match.getSourceMethodId() + ", " + "concept: " + match.getConceptId() + ", " + "weight: " + match.getWeight());
				ApplicationLogic.saveMatching(match);
			}
		}
		catch (Exception e) {
			Console.writeError("error while analyzing sources: " + StringUtils.toString(e));
		}
	}

	/**
	 * Stop the engine.
	 * 
	 * @see ch.hesge.csim2.core.shell.IEngine#stop()
	 */
	@Override
	public void stop() {
	}

	/**
	 * Compute method-concept match through PHD algo.
	 */
	public List<MethodConceptMatch> computeMatchingWithPhd() {

		List<MethodConceptMatch> matchings = new ArrayList<>();

		// Compute matchings for each ontology
		for (Ontology ontology : project.getOntologies()) {

			// Load ontology concepts
			ontology.getConcepts().clear();
			ontology.getConcepts().addAll(ApplicationLogic.getConceptsWithDependencies(ontology));

			// Retrieve method/concept matchings
			List<RddaMethodConceptMatch> rddaMatchings = new TermVectorBuilder().computeVectors(project, ontology);

			// Convert rdda matching into csim2 matching
			for (RddaMethodConceptMatch rddaMatch : rddaMatchings) {

				MethodConceptMatch match = new MethodConceptMatch();

				match.setProjectId(project.getKeyId());
				match.setSourceMethodId(rddaMatch.getMethodId().getMethodID());
				match.setConceptId(rddaMatch.getConceptId().getConceptID());
				match.setWeight(rddaMatch.getMatchingStrength());

				matchings.add(match);
			}
		}

		return matchings;
	}

	/**
	 * <pre>
	 * Compute method-concept match through EHA algo.
	 * 
	 * 	TF 	= term frequency
	 * 		= relative term frequency within a concept
	 * 
	 * 	IDF	= inverse document frequencies 
	 * 		= inverse frequency of terms among all concepts.
	 * 
	 * 	TF-IDF = relevance of a term within a concept.
	 * 
	 * 
	 * </pre>
	 */
	public List<MethodConceptMatch> computeMatchingWithEha() {

		List<MethodConceptMatch> matchings = new ArrayList<>();

		Console.writeLine("loading method & concept information...");

		// Load all concepts, methods and stems
		Map<Integer, Concept> conceptMap = ApplicationLogic.getConceptMap(project);
		Map<Integer, SourceMethod> methodMap = ApplicationLogic.getSourceMethodMap(project);
		Map<String, List<StemConcept>> stemConceptsMap = ApplicationLogic.getStemConceptByTermMap(project);
		Map<String, List<StemMethod>> stemMethodsMap = ApplicationLogic.getStemMethodByTermMap(project);

		Console.writeLine("selecting terms intersection...");

		/*
		// Retrieve intersection between concept terns and method terms
		Set<String> termSet = stemConceptsMap.keySet();
		termSet.retainAll(stemMethodsMap.keySet());
		List<String> termList = new ArrayList<String>();
		termList.addAll(termSet);
		*/

		Console.writeLine("analyzing potential matching elements...");

		List<Concept> concepts = new ArrayList<>(conceptMap.values());
		List<String> terms = new ArrayList<>(stemConceptsMap.keySet());

		// Calculate the TFIDF matrix for all terms/concepts
		RealMatrix tfidfMatrix = MethodConceptMatcherUtils.getTfIdfMatrix(terms, concepts, stemConceptsMap);

		for (SourceMethod sourceMethod : methodMap.values()) {

			/*
			// Calculate the TFIDF vector for current method
			Vector<Double> tfMethodVector = tfmVectorMap.get(sourceMethod);
			Vector<Double> tfidfMethodVector = VectorUtils.getHadamardProduct(tfMethodVector, idfVector);

			for (Concept concept : tfcVectortMap.keySet()) {

				// Calculate the TFIDF vector for current concept
				Vector<Double> tfConceptVector = tfcVectortMap.get(concept);
				Vector<Double> tfidfConceptVector = VectorUtils.getHadamardProduct(tfConceptVector, idfVector);

				// Now calculate distance between the method and concept (cosine similarity)
				double scalarProduct = VectorUtils.getScalarProduct(tfidfMethodVector, tfidfConceptVector);
				double tfidfMethodLength = VectorUtils.getLength(tfidfMethodVector);
				double tfidfConceptLength = VectorUtils.getLength(tfidfConceptVector);

				// weight = tfidf(t,m) = tf(t,m) * idf(t), where:
				//   tf(t,m) = 
				//   idf(t)  = 

				double vectorSimilarity = scalarProduct / (tfidfMethodLength * tfidfConceptLength);

				// Register result within the matchMap
				if (vectorSimilarity > 0) {

					MethodConceptMatch match = new MethodConceptMatch();

					match.setProjectId(project.getKeyId());
					match.setSourceMethodId(sourceMethod.getKeyId());
					match.setConceptId(concept.getKeyId());
					match.setWeight(vectorSimilarity);

					matchings.add(match);
				}
			}
			*/
		}

		return matchings;
	}

}
