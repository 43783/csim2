package ch.hesge.csim2.core.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import ch.hesge.csim2.core.dao.TraceDao;
import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.MethodConceptMatch;
import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.Scenario;
import ch.hesge.csim2.core.model.TimeSeries;
import ch.hesge.csim2.core.model.Trace;
import ch.hesge.csim2.core.utils.ObjectSorter;

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

		// Initialize time series
		timeSeries.setProject(project);
		timeSeries.setScenario(scenario);

		Map<Integer, List<MethodConceptMatch>> matchMap = ApplicationLogic.getMethodMatchingMap(project);

		// Retrieve all distinct concepts found in traces
		List<Concept> traceConcepts = getTraceConcepts(scenario, matchMap);
		timeSeries.getTraceConcepts().addAll(traceConcepts);

		// Retrieve all trace matrix (concept weight by trace step)
		RealMatrix traceMatrix = getTraceMatrix(scenario, traceConcepts, matchMap);
		timeSeries.setTraceMatrix(traceMatrix);

		return timeSeries;
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
	 *        a subset of timerSeries concepts to keep results to a specific
	 *        concepts
	 * @return
	 *         a new time series instance with segmented trace vectors
	 */
	public static TimeSeries getFilteredTimeSeries(TimeSeries timeSeries, int segmentCount, double threshold, List<Concept> concepts) {

		int traceStep = 0;
		List<Concept> originalConcepts = timeSeries.getTraceConcepts();
		RealMatrix traceMatrix = MatrixUtils.createRealMatrix(concepts.size(), segmentCount);
		int segmentSize = timeSeries.getTraceMatrix().getColumnDimension() / segmentCount;

		// Compute each segment value
		for (int i = 0; i < segmentCount; i++) {

			RealVector originalConceptVector = new ArrayRealVector(originalConcepts.size());

			// Scan steps in segment
			for (int j = traceStep; j < traceStep + segmentSize; j++) {

				// Retrieve concept vector in original matrix
				RealVector traceVector = timeSeries.getTraceMatrix().getColumnVector(traceStep);

				// Normalize its values according to threshold
				for (int k = 0; k < traceVector.getDimension(); k++) {
					if (traceVector.getEntry(k) > threshold) {
						traceVector.setEntry(k, 1d);
					}
					else {
						traceVector.setEntry(k, 0d);
					}
				}

				// Accounting concept occurrence into conceptVector
				originalConceptVector = originalConceptVector.add(traceVector);
			}

			// Convert original concept vector (with all concepts in trace) 
			// into target concept vector (with only specified concepts)
			RealVector targetConceptVector = new ArrayRealVector(concepts.size());

			for (int l = 0; l < concepts.size(); l++) {
				Concept concept = concepts.get(l);
				int originalConceptIndex = originalConcepts.indexOf(concept);
				targetConceptVector.setEntry(l, originalConceptVector.getEntry(originalConceptIndex));
			}

			traceMatrix.setColumnVector(i, targetConceptVector);
			traceStep += segmentSize;
		}

		TimeSeries newTimeSeries = new TimeSeries();
		newTimeSeries.setProject(timeSeries.getProject());
		newTimeSeries.setScenario(timeSeries.getScenario());
		newTimeSeries.setTraceConcepts(timeSeries.getTraceConcepts());
		newTimeSeries.setTraceMatrix(traceMatrix);

		return newTimeSeries;
	}

	/**
	 * Retrieve all distinct concepts found in a trace for a specific scenario.
	 * 
	 * @param scenario
	 *        the scenario whose trace should be considered
	 * @param matchMap
	 *        the method/concept matching map of current project
	 */
	private static List<Concept> getTraceConcepts(Scenario scenario, Map<Integer, List<MethodConceptMatch>> matchMap) {

		Map<Integer, Concept> traceConceptMap = new HashMap<>();

		// Retrieve unique methods found in trace
		List<Integer> uniqueMethodIds = TraceDao.findDistinctMethodIds(scenario);

		for (Integer methodId : uniqueMethodIds) {

			// Handle only methods with at least one matching
			if (matchMap.containsKey(methodId)) {

				// Scan all concepts matching
				for (MethodConceptMatch match : matchMap.get(methodId)) {

					// Handle only concept not already present in conceptMap
					if (!traceConceptMap.containsKey(match.getConceptId())) {
						traceConceptMap.put(match.getConceptId(), match.getConcept());
					}
				}
			}
		}

		// Finally sort concepts by name
		List<Concept> traceConcepts = new ArrayList<>(traceConceptMap.values());
		ObjectSorter.sortConcepts(traceConcepts);

		return traceConcepts;
	}

	/**
	 * <code>
	 * Retrieve all trace concept associated to each trace entry.
	 * The result will be a step-concept matrix.
	 * 
	 * The rows represents concepts and each column contains concept weight matching for a trace step.
	 * 
	 * for instance:
	 * 							concept weight column
	 * 		 lines					|		 concept weight column
	 * 		= concepts				|			|
	 * 		
	 * 		   A					0.2		0.5		0		0
	 * 		   B					0		0.3		0.7		0.3
	 * 		   C					0		0		0		0.5
	 * 
	 * 							----------------------------------> list of vectors
	 * 
	 * 		row = trace steps:		0	1	2	3	...
	 * 
	 * </code>
	 * 
	 * @param timeSeries
	 *        the timeSeries to initialize
	 * @param matchMap
	 *        the method/concept matching map of current project
	 * @return
	 *         a matrix of all concept's weight associated to each trace step
	 */
	private static RealMatrix getTraceMatrix(Scenario scenario, List<Concept> traceConcepts, Map<Integer, List<MethodConceptMatch>> matchMap) {

		// Retrieve scenario traces
		List<Trace> scenarioTraces = TraceDao.findByScenario(scenario);

		RealMatrix traceMatrix = MatrixUtils.createRealMatrix(scenarioTraces.size(), traceConcepts.size());

		for (int i = 0; i < scenarioTraces.size(); i++) {

			Trace trace = scenarioTraces.get(i);

			// Retrieve all concepts associated to method
			if (matchMap.containsKey(trace.getMethodId())) {

				// Retrieve all matches for current method
				List<MethodConceptMatch> matches = matchMap.get(trace.getMethodId());

				for (MethodConceptMatch match : matches) {

					int row = traceConcepts.indexOf(match.getConcept());

					// Update column weight associated to the concept
					if (row != -1) {
						traceMatrix.setEntry(row, i, match.getWeight());
					}
				}
			}
		}

		return traceMatrix;
	}
}
