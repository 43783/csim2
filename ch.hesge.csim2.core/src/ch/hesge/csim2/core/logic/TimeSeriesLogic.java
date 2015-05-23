package ch.hesge.csim2.core.logic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import ch.hesge.csim2.core.dao.TraceDao;
import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.MethodConceptMatch;
import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.Scenario;
import ch.hesge.csim2.core.model.TimeSeries;
import ch.hesge.csim2.core.model.Trace;

/**
 * This class implement all logical rules associated to timeseries.
 *
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

class TimeSeriesLogic {

	/**
	 * Retrieve the time series associated to a scenario traces.
	 * 
	 * @param project
	 *        the project owning the traces
	 * @param scenario
	 *        the scenario owning the traces
	 * @return
	 *         the TimeSeries object gathering trace information
	 */
	public static TimeSeries getTimeSeries(Project project, Scenario scenario) {

		TimeSeries timeSeries = new TimeSeries();
		Map<Integer, List<MethodConceptMatch>> matchMap = ApplicationLogic.getMatchingMap(project);

		// Initialize time series
		timeSeries.setProject(project);
		timeSeries.setScenario(scenario);

		// Retrieve all concepts found in traces
		initTraceConcepts(timeSeries, matchMap);

		// Retrieve all trace frequency vectors
		initTraceVectors(timeSeries, matchMap);

		return timeSeries;
	}

	/**
	 * Retrieve all distinct concepts found in a trace according to the
	 * threshold passed in argument.
	 * 
	 * The threshold allows selecting only concepts whose relevant matching
	 * concepts are insure to a specific level.
	 * 
	 * Note: concepts returned are also sorted by relevance (weight)
	 * 
	 * @param timeSeries
	 *        the timeSeries to initialize
	 * @param matchMap
	 *        the method/concept matching map of current project
	 */
	private static void initTraceConcepts(TimeSeries timeSeries, Map<Integer, List<MethodConceptMatch>> matchMap) {

		Map<Integer, Concept> conceptMap = new HashMap<>();
		Map<Integer, Double> weightMap = new HashMap<>();

		// Retrieve unique methods found in trace
		List<Integer> uniqueMethodIds = TraceDao.findDistinctMethodIds(timeSeries.getScenario());

		for (Integer methodId : uniqueMethodIds) {

			// Retrieve all associated concept matchings
			if (matchMap.containsKey(methodId)) {

				// Scan all concepts matching
				for (MethodConceptMatch match : matchMap.get(methodId)) {

					// Add concept found, if not already present and if proper weight
					if (!conceptMap.containsKey(match.getConceptId())) {
						conceptMap.put(match.getConceptId(), match.getConcept());
						weightMap.put(match.getConceptId(), match.getWeight());
					}
				}
			}
		}

		// Convert concepts found into a single array
		timeSeries.getConcepts().addAll(conceptMap.values());

		// Sort concept by relevance (weight)
		timeSeries.getConcepts().sort(new Comparator<Concept>() {
			@Override
			public int compare(Concept a, Concept b) {
				double aWeight = weightMap.get(a.getKeyId());
				double bWeight = weightMap.get(b.getKeyId());
				return (int) (bWeight - aWeight);
			}
		});

		// Finally populate concept weight
		timeSeries.getWeightMap().putAll(weightMap);
	}

	/**
	 * Retrieve all trace vectors associated to each trace entry.
	 * Each vector represents concept presences (1) or not (0) among the
	 * time-series concepts.
	 * 
	 * @param timeSeries
	 *        the timeSeries to initialize
	 * @param matchMap
	 *        the method/concept matching map of current project
	 */
	private static void initTraceVectors(TimeSeries timeSeries, Map<Integer, List<MethodConceptMatch>> matchMap) {

		// Retrieve concepts in trace
		List<Concept> traceConcepts = timeSeries.getConcepts();

		// Retrieve scenario traces
		List<Trace> scenarioTraces = TraceDao.findByScenario(timeSeries.getScenario());

		for (Trace trace : scenarioTraces) {

			// Create one concept vector by trace entry
			Vector<Integer> conceptVector = createVector(traceConcepts.size(), 0);

			// Retrieve all associated concept matchings
			if (matchMap.containsKey(trace.getMethodId())) {

				List<MethodConceptMatch> matches = matchMap.get(trace.getMethodId());

				for (MethodConceptMatch match : matches) {

					int conceptIndex = traceConcepts.indexOf(match.getConcept());

					// Update concept vector frequency
					if (conceptIndex != -1) {
						conceptVector.set(conceptIndex, 1);
					}
				}
			}

			timeSeries.getTraceVectors().add(conceptVector);
		}
	}

	/**
	 * Create a segmented time series.
	 * 
	 * @param timeSeries
	 *        the time series to use for segmentation
	 * @param segmentCount
	 *        the number of segment to generate
	 * @param threshold
	 *        the concept weight threshold to use to select concept
	 * @param concepts
	 *        a subset of timerSeries concepts to keep results to a specific concepts
	 * @return
	 *         a new time series instance with segmented trace vectors
	 */
	public static TimeSeries getFilteredTimeSeries(TimeSeries timeSeries, int segmentCount, double threshold, List<Concept> concepts) {

		TimeSeries filteredTimeSeries = new TimeSeries();
		List<Concept> filteredConcepts = new ArrayList<>();
		Map<Integer, Double> weightMap = new HashMap<>();

		// First filter timeSeries concepts;
		for (Concept concept : timeSeries.getConcepts()) {

			double weight = timeSeries.getWeightMap().get(concept.getKeyId());

			if (weight >= threshold && (concepts.size() == 0 || concepts.contains(concept))) {
				filteredConcepts.add(concept);
				weightMap.put(concept.getKeyId(), weight);
			}
		}

		// Sort concept by relevance (weight)
		filteredConcepts.sort(new Comparator<Concept>() {
			@Override
			public int compare(Concept a, Concept b) {
				double aWeight = weightMap.get(a.getKeyId());
				double bWeight = weightMap.get(b.getKeyId());
				return (int) (bWeight - aWeight);
			}
		});

		// Finally populate concepts and weights
		filteredTimeSeries.getConcepts().addAll(filteredConcepts);
		filteredTimeSeries.getWeightMap().putAll(weightMap);

		// Now segment trace vectors
		int sequenceNumber = 0;
		List<Vector<Integer>> traceVectors = new ArrayList<>();
		int segmentSize = timeSeries.getTraceVectors().size() / segmentCount;

		// Generate each segment
		for (int i = 0; i < segmentCount; i++) {

			Vector<Integer> conceptVector = createVector(filteredConcepts.size(), 0);

			for (int j = sequenceNumber; j < sequenceNumber + segmentSize; j++) {

				Vector<Integer> traceVector = timeSeries.getTraceVectors().get(j);
				conceptVector = addVectors(conceptVector, traceVector);
			}

			traceVectors.add(conceptVector);
			sequenceNumber += segmentSize;
		}

		filteredTimeSeries.getTraceVectors().addAll(traceVectors);

		return filteredTimeSeries;
	}

	/**
	 * Create and initialize a vector.
	 * 
	 * @param size
	 *        the size of the vector to initialize
	 * @param initialValue
	 *        the initial value for all the coordinates
	 * @return
	 *         a zero vector
	 */
	private static <T> Vector<T> createVector(int size, T initialValue) {

		Vector<T> vector = new Vector<>(size);

		for (int i = 0; i < size; i++) {
			vector.add(initialValue);
		}

		return vector;
	}

	/**
	 * Add two vector (should be same size).
	 * 
	 * @param a
	 *        the first vector
	 * @param b
	 *        the second vector
	 * @return
	 *         the sum of a and b
	 */
	private static Vector<Integer> addVectors(Vector<Integer> a, Vector<Integer> b) {

		Vector<Integer> result = createVector(a.size(), 0);

		for (int i = 0; i < a.size(); i++) {
			result.set(i, a.get(i) + b.get(i));
		}

		return result;
	}
}
