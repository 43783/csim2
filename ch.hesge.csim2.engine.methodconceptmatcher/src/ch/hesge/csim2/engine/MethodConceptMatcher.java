/**
 * 
 */
package ch.hesge.csim2.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
		return "1.0.7";
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
				matchingAlgorithm = "tfidf1";
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
			if (matchingAlgorithm.equals("tfidf1")) {
				matchings = computeMatchingWithTfIdf1();
			}
			else if (matchingAlgorithm.equals("tfidf2")) {
				matchings = computeMatchingWithTfIdf2();
			}
			else if (matchingAlgorithm.equals("norm")) {
				matchings = computeMatchingWithNorm();
			}
			else if (matchingAlgorithm.equals("cos")) {
				matchings = computeMatchingWithCosine();
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
	 * Compute method-concept match through TF-IDF (phd).
	 */
	public List<MethodConceptMatch> computeMatchingWithTfIdf1() {

		List<MethodConceptMatch> matchings = new ArrayList<>();

		// Compute matchings for each ontology
		for (Ontology ontology : project.getOntologies()) {

			// Load ontology concepts
			ontology.getConcepts().clear();
			ontology.getConcepts().addAll(ApplicationLogic.getConcepts(ontology));

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
	 * Compute method-concept match through TF-IDF (eha).
	 * 
	 * 	TF 	= term frequency
	 * 		= relative term frequency within a concept
	 * 
	 * 	IDF	= inverse document frequencies 
	 * 		= inverse frequency of terms among all concepts.
	 * 
	 * 	TF-IDF = relevance of a term within a concept.
	 * 
	 * </pre>
	 */
	public List<MethodConceptMatch> computeMatchingWithTfIdf2() {

		List<MethodConceptMatch> matchings = new ArrayList<>();

		Console.writeLine("loading method & concept information...");

		// Load concepts, methods and stems into maps
		Map<Integer, Concept> conceptMap = ApplicationLogic.getConceptMap(project);
		Map<Integer, SourceMethod> methodMap = ApplicationLogic.getSourceMethodMap(project);
		Map<String, List<StemConcept>> stemConceptsMap = ApplicationLogic.getStemConceptByTermMap(project);
		Map<String, List<StemMethod>> stemMethodsMap = ApplicationLogic.getStemMethodByTermMap(project);

		Console.writeLine("analyzing potential matching elements...");

		List<Concept> concepts = new ArrayList<>(conceptMap.values());
		List<String> terms = new ArrayList<>(stemConceptsMap.keySet());

		// Calculate the TFIDF matrix (row = terms, col = concepts, cell = tf-idf weight)
		RealMatrix tfidfMatrix = MethodConceptMatcherUtils.getTfIdfMatrix(terms, concepts, stemConceptsMap);

		// Calculate on term vectors for each method
		Map<Integer, RealVector> methodTermVectorMap = MethodConceptMatcherUtils.getMethodTermVectorMap(terms, methodMap, stemMethodsMap);

		for (SourceMethod sourceMethod : methodMap.values()) {

			RealVector methodTermVector = methodTermVectorMap.get(sourceMethod.getKeyId());
			boolean isNotZeroMethodVector = MethodConceptMatcherUtils.isNotZeroVector(methodTermVector);

			// Skip null method vector
			if (isNotZeroMethodVector) {

				// Select all concepts with similarity factor > 0
				for (int i = 0; i < tfidfMatrix.getColumnDimension(); i++) {

					Concept concept = concepts.get(i);
					RealVector conceptTermVector = tfidfMatrix.getColumnVector(i);

					// Calculate similarity between method and concept vectors
					// => computed through cosine similarity (cosine angle between the two vectors)
					double similarity = conceptTermVector.cosine(methodTermVector);

					// Register result within the matchMap
					if (similarity > 0) {

						MethodConceptMatch match = new MethodConceptMatch();

						match.setProjectId(project.getKeyId());
						match.setSourceMethodId(sourceMethod.getKeyId());
						match.setConceptId(concept.getKeyId());
						match.setWeight(similarity);

						matchings.add(match);
					}
				}
			}
		}

		return matchings;
	}

	/**
	 * Compute method-concept match through concept information weight (eha).
	 */
	public List<MethodConceptMatch> computeMatchingWithNorm() {

		List<MethodConceptMatch> matchings = new ArrayList<>();

		Console.writeLine("loading method & concept information...");

		// Load concepts and stems
		Map<Integer, Concept> conceptMap = ApplicationLogic.getConceptMap(project);
		Map<String, List<StemConcept>> stemConceptByTermMap = ApplicationLogic.getStemConceptByTermMap(project);

		// Load methods and stems
		Map<Integer, SourceMethod> methodMap = ApplicationLogic.getSourceMethodMap(project);
		Map<String, List<StemMethod>> stemMethodByTermMap = ApplicationLogic.getStemMethodByTermMap(project);

		// Serialize concepts / terms 
		List<Concept> concepts = new ArrayList<>(conceptMap.values());
		List<String> terms = new ArrayList<>(stemConceptByTermMap.keySet());

		Console.writeLine("analyzing potential matching elements...");

		// Retrieve the term/concept matrix (row = terms, col = concepts, cell = weight)
		RealMatrix weightMatrix = MethodConceptMatcherUtils.getWeightMatrix(terms, concepts, conceptMap, stemConceptByTermMap);

		// Calculate on term vectors for each method
		Map<Integer, RealVector> methodTermVectorMap = MethodConceptMatcherUtils.getMethodTermVectorMap(terms, methodMap, stemMethodByTermMap);

		for (SourceMethod sourceMethod : methodMap.values()) {

			RealVector methodTermVector = methodTermVectorMap.get(sourceMethod.getKeyId());
			boolean isNotZeroMethodVector = MethodConceptMatcherUtils.isNotZeroVector(methodTermVector);

			// Skip null method vector
			if (isNotZeroMethodVector) {

				// Select all concepts with similarity factor > 0
				for (int i = 0; i < weightMatrix.getColumnDimension(); i++) {

					Concept concept = concepts.get(i);
					RealVector conceptTermVector = weightMatrix.getColumnVector(i);

					// Calculate similarity between method and concept vectors
					// => computed through sum of concept weight
					double similarity = Math.min(1d, conceptTermVector.ebeMultiply(methodTermVector).getL1Norm());

					// Register result within the matchMap
					if (similarity > 0) {

						// Limit similarity amplitude
						similarity = Math.min(1d, similarity);

						MethodConceptMatch match = new MethodConceptMatch();

						match.setProjectId(project.getKeyId());
						match.setSourceMethodId(sourceMethod.getKeyId());
						match.setConceptId(concept.getKeyId());
						match.setWeight(similarity);

						matchings.add(match);
					}
				}
			}
		}

		return matchings;
	}

	/**
	 * Compute method-concept match through concept information weight (eha).
	 */
	public List<MethodConceptMatch> computeMatchingWithCosine() {

		List<MethodConceptMatch> matchings = new ArrayList<>();

		Console.writeLine("loading method & concept information...");

		// Load concepts and stems
		Map<Integer, Concept> conceptMap = ApplicationLogic.getConceptMap(project);
		Map<String, List<StemConcept>> stemConceptByTermMap = ApplicationLogic.getStemConceptByTermMap(project);

		// Load methods and stems
		Map<Integer, SourceMethod> methodMap = ApplicationLogic.getSourceMethodMap(project);
		Map<String, List<StemMethod>> stemMethodByTermMap = ApplicationLogic.getStemMethodByTermMap(project);

		// Serialize concepts / terms 
		List<Concept> concepts = new ArrayList<>(conceptMap.values());
		List<String> terms = new ArrayList<>(stemConceptByTermMap.keySet());

		Console.writeLine("analyzing potential matching elements...");

		// Retrieve the term/concept matrix (row = terms, col = concepts, cell = weight)
		RealMatrix weightMatrix = MethodConceptMatcherUtils.getWeightMatrix(terms, concepts, conceptMap, stemConceptByTermMap);

		// Calculate on term vectors for each method
		Map<Integer, RealVector> methodTermVectorMap = MethodConceptMatcherUtils.getMethodTermVectorMap(terms, methodMap, stemMethodByTermMap);

		for (SourceMethod sourceMethod : methodMap.values()) {

			RealVector methodTermVector = methodTermVectorMap.get(sourceMethod.getKeyId());
			boolean isNotZeroMethodVector = MethodConceptMatcherUtils.isNotZeroVector(methodTermVector);

			// Skip null method vector
			if (isNotZeroMethodVector) {

				// Select all concepts with similarity factor > 0
				for (int i = 0; i < weightMatrix.getColumnDimension(); i++) {

					Concept concept = concepts.get(i);
					RealVector conceptTermVector = weightMatrix.getColumnVector(i);

					// Calculate similarity between method and concept vectors
					// => computed through cosine similarity (cosine angle between the two vectors)
					double similarity = conceptTermVector.cosine(methodTermVector);

					// Register result within the matchMap
					if (similarity > 0) {

						MethodConceptMatch match = new MethodConceptMatch();

						match.setProjectId(project.getKeyId());
						match.setSourceMethodId(sourceMethod.getKeyId());
						match.setConceptId(concept.getKeyId());
						match.setWeight(similarity);

						matchings.add(match);
					}
				}
			}
		}

		return matchings;
	}
}
