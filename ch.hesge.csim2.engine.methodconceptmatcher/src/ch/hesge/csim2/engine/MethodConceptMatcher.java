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

import ch.hesge.csim2.core.logic.ApplicationLogic;
import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.Context;
import ch.hesge.csim2.core.model.IEngine;
import ch.hesge.csim2.core.model.MethodConceptMatch;
import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.SourceMethod;
import ch.hesge.csim2.core.model.StemConcept;
import ch.hesge.csim2.core.model.StemMethod;
import ch.hesge.csim2.core.utils.Console;
import ch.hesge.csim2.core.utils.EngineException;
import ch.hesge.csim2.core.utils.StringUtils;

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

			Console.writeLine("cleaning previous matches...");
			ApplicationLogic.deleteMatching(project);

			Console.writeLine("loading sources information...");

			// Load all concepts & methods
			Map<Integer, Concept> conceptMap = ApplicationLogic.getConceptMap(project);
			Map<Integer, SourceMethod> methodMap = ApplicationLogic.getSourceMethodMap(project);

			// Load all concept-stem / method-stems
			Map<String, List<StemConcept>> stemConceptsMap = ApplicationLogic.getStemConceptByTermMap(project);
			Map<String, List<StemMethod>>  stemMethodsMap  = ApplicationLogic.getStemMethodByTermMap(project);

			Console.writeLine("selecting term intersection...");

			// Retrieve intersecting terms
			Set<String> termSet = stemConceptsMap.keySet();
			termSet.retainAll(stemMethodsMap.keySet());
			List<String> termList = new ArrayList<String>();
			termList.addAll(termSet);

			Console.writeLine("analyzing potential matching elements...");

			// Retrieve idf vector for the common term list
			Vector<Double> idfVector = VectorUtils.getIdfVector(termList, stemConceptsMap, conceptMap);

			// Now retrieve tf vectors for methods & concepts
			Map<SourceMethod, Vector<Double>> tfmVectorMap = VectorUtils.getTfmVectorMap(termList, stemMethodsMap, methodMap);
			Map<Concept, Vector<Double>> tfcVectortMap = VectorUtils.getTfcVectorMap(termList, stemConceptsMap, conceptMap);

			for (SourceMethod sourceMethod : tfmVectorMap.keySet()) {

				// Calculate the tfidf vector for current method
				Vector<Double> tfMethodVector = tfmVectorMap.get(sourceMethod);
				Vector<Double> tfidfMethodVector = VectorUtils.getHadamardProduct(tfMethodVector, idfVector);

				for (Concept concept : tfcVectortMap.keySet()) {

					// Calculate the tfidf vector for current concept
					Vector<Double> tfConceptVector = tfcVectortMap.get(concept);
					Vector<Double> tfidfConceptVector = VectorUtils.getHadamardProduct(tfConceptVector, idfVector);

					// Now calculate distance between the method and concept (cosine similarity)
					double scalarProduct = VectorUtils.getScalarProduct(tfidfMethodVector, tfidfConceptVector);
					double tfidfMethodLength = VectorUtils.getLength(tfidfMethodVector);
					double tfidfConceptLength = VectorUtils.getLength(tfidfConceptVector);

					double vectorSimilarity = scalarProduct / (tfidfMethodLength * tfidfConceptLength);

					// Register result within the matchMap
					if (vectorSimilarity > 0) {

						MethodConceptMatch match = new MethodConceptMatch();

						match.setProjectId(project.getKeyId());
						match.setSourceMethodId(sourceMethod.getKeyId());
						match.setConceptId(concept.getKeyId());
						match.setWeight(vectorSimilarity);

						ApplicationLogic.saveMatching(match);

						System.out.println("  " + "method: " + sourceMethod.getName() + ", " + "concept: " + concept.getName() + ", " + "weight: " + vectorSimilarity);
					}
				}
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
	 * Static engine tester (debug).
	 */
	/**
	 * public static void main(String args[]) {
	 * 
	 * // Initialize database connection
	 * ConnectionUtils.setUser("csim2");
	 * ConnectionUtils.setPassword("csim2");
	 * ConnectionUtils.setUrl("jdbc:mysql://localhost:3306/csim2");
	 * 
	 * // Initialize engine properties
	 * Context context = new Context();
	 * context.setProperty("project", ProjectDao.sfindByName("OntoReverse"));
	 * 
	 * // And start the engine IEngine engine = new ConceptAnalyzer();
	 * IEngine engine = new MethodConceptMatcher();
	 * engine.setContext(context);
	 * engine.init();
	 * engine.start();
	 * }
	 **/
}
