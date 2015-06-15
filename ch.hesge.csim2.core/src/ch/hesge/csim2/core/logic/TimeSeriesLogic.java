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
import ch.hesge.csim2.core.model.MatchingAlgorithm;
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
	 * <code>
	 * Retrieve the time series associated to a scenario traces.
	 * 
	 * A time series contains a list of all concepts found in trace,
	 * and a matrix concept references (in weight) by each step in trace.
	 * 
	 * The matrix can be for instance:
	 * 
	 * 						column = weights of each concepts
	 * 		 rows				|		 	concept weights
	 * 		= concepts			|				|
	 * 			
	 * 		   A				0.2		0.5		0		0
	 * 		   B				0		0.3		0.7		0.3
	 * 		   C				0		0		0		0.5
	 * 
	 * 		trace steps:		0	1	2	3	...
	 * 
	 * </code>
	 * 
	 * @param project
	 *        the project owning the traces
	 * @param scenario
	 *        the scenario owning the traces
	 * @param matchAlgo
	 *        the matching algorithm to use (SIMPLE, COSINE, TFIDF)
	 * @return the TimeSeries object gathering trace information
	 */
	public static TimeSeries getTimeSeries(Project project, Scenario scenario, MatchingAlgorithm matchAlgo) {

		Map<Integer, List<MethodConceptMatch>> matchMap = ApplicationLogic.getMethodMatchingMap(project, matchAlgo);
		Map<Integer, Concept> conceptsInTrace = new HashMap<>();

		// First retrieve unique methods found in trace
		List<Integer> uniqueIds = TraceDao.findDistinctMethodIds(scenario);

		// Then for each method, retrieve its matching concepts
		for (Integer methodId : uniqueIds) {

			// Handle only methods with at least one matching
			if (matchMap.containsKey(methodId)) {

				// Scan all concepts matching method
				for (MethodConceptMatch match : matchMap.get(methodId)) {

					// If concept not already include, put it on map
					if (!conceptsInTrace.containsKey(match.getConceptId())) {
						conceptsInTrace.put(match.getConceptId(), match.getConcept());
					}
				}
			}
		}

		// Finally sort all concepts by name
		List<Concept> traceConcepts = new ArrayList<>(conceptsInTrace.values());
		ObjectSorter.sortConcepts(traceConcepts);

		// Retrieve all trace for specified scenario
		List<Trace> scenarioTraces = TraceDao.findByScenario(scenario);

		// Create an empty matrix
		RealMatrix traceMatrix = MatrixUtils.createRealMatrix(traceConcepts.size(), scenarioTraces.size());

		// Create a vector for global concept occurrences
		RealVector occurrenceVector = new ArrayRealVector(traceConcepts.size());

		for (int i = 0; i < scenarioTraces.size(); i++) {

			Trace traceStep = scenarioTraces.get(i);

			// Check if current step has some concept matching
			if (matchMap.containsKey(traceStep.getMethodId())) {

				// Scan all matching for current method
				for (MethodConceptMatch match : matchMap.get(traceStep.getMethodId())) {

					// Retrieve matching concept
					Concept concept = match.getConcept();

					// And its position within the trace concepts
					int conceptIndex = traceConcepts.indexOf(concept);

					// Update associated column in matrix with weight associated to the concept
					if (conceptIndex != -1) {
						traceMatrix.setEntry(conceptIndex, i, match.getWeight());
					}
				}
			}

			// Update global concept occurrences (actually global weights)
			occurrenceVector.add(traceMatrix.getColumnVector(i));
		}

		TimeSeries timeSeries = new TimeSeries();

		// Initialize time series
		timeSeries.setProject(project);
		timeSeries.setScenario(scenario);
		timeSeries.setTraceConcepts(traceConcepts);
		timeSeries.setTraceMatrix(traceMatrix);
		timeSeries.setOccurrences(occurrenceVector);

		return timeSeries;
	}

	/**
	 * <code>
	 * Create a filtered time series.
	 * 
	 * Filtering a time series consist to reduce all column vectors associated
	 * to a group of steps into a single vector iteratively.
	 * 
	 * The group of steps is named segment. And reducing all steps in segment,
	 * is kind of compression of the original matrix.
	 * 
	 * Notice: if concepts list is empty all concepts are selected in resulting
	 * time series.
	 * 
	 * More precisely:
	 * 
	 * 	if we call current method with:
	 *  
	 * 		segment-count = 3
	 * 		threshold     = 0.2
	 * 		concept       = [A, C]
	 * 	
	 * 	with this matrix:
	 * 
	 * 		------------------------------------------------------------
	 * 
	 * 		   A		0.1		0.5		0		0		0.1		0.78
	 * 		   B		0		0.3		0		0.3		0		0.4
	 * 		   C		0		0		0.7		0.5		0.6		0
	 * 
	 * 		------------------------------------------------------------
	 * 
	 * 	we will obtains the following matrix:
	 * 
	 * 		------------------------------------------------------------
	 * 			
	 * 		   A		1		0		1
	 * 		   C		0		2		1
	 * 
	 * 		------------------------------------------------------------
	 * 
	 * 	Note: the resulting matrix contains occurrence count depending of threshold (not weights)
	 * 
	 * </code>
	 * 
	 * @param timeSeries
	 *        the time series to compress
	 * @param segmentCount
	 *        the total number of segment to generate
	 * @param threshold
	 *        the concept weight threshold to use to select concepts
	 * @param concepts
	 *        all concepts found in segmented time series
	 * @return a new time series instance with segmented trace matrix gathering
	 *         only information associated to concepts passed in argument.
	 */
	public static TimeSeries getFilteredTimeSeries(TimeSeries timeSeries, int segmentCount, double threshold, List<Concept> concepts) {

		RealMatrix reducedMatrix = null;
		RealVector occurrenceVector = null;

		// Retrieve segmented time series (for all available concepts in trace)
		TimeSeries segmentedSeries = getSegmentedTimeSeries(timeSeries, segmentCount, threshold);

		List<Concept> traceConcepts = new ArrayList<>();

		// Retrieve the list of concepts found in segments, based on occurrences
		for (int i = 0; i < segmentedSeries.getOccurrences().getDimension(); i++) {

			if (segmentedSeries.getOccurrences().getEntry(i) > 0) {

				Concept elligibleConcept = segmentedSeries.getTraceConcepts().get(i);

				// Include concept only if specified in entry
				if (concepts == null || concepts.isEmpty() || concepts.contains(elligibleConcept)) {
					traceConcepts.add(elligibleConcept);
				}
			}
		}

		// If concepts are found in trace
		if (traceConcepts.size() > 0) {

			// Create a reduced matrix with a subset of all concepts
			reducedMatrix = MatrixUtils.createRealMatrix(traceConcepts.size(), segmentCount);

			// Create a vector for global concept occurrences
			occurrenceVector = new ArrayRealVector(traceConcepts.size());

			// Now convert each column vector into reduced vector
			for (int i = 0; i < segmentCount; i++) {

				RealVector originalVector = segmentedSeries.getTraceMatrix().getColumnVector(i);
				RealVector reducedVector = new ArrayRealVector(traceConcepts.size());

				// Keep all occurrences for specified concepts
				for (int j = 0; j < traceConcepts.size(); j++) {

					Concept concept = traceConcepts.get(j);
					int foundIndex = segmentedSeries.getTraceConcepts().indexOf(concept);

					if (foundIndex != -1) {
						double conceptCount = originalVector.getEntry(foundIndex);
						reducedVector.setEntry(j, conceptCount);
					}
				}

				// Update reduced matrix column with current vector
				reducedMatrix.setColumnVector(i, reducedVector);

				// Update global concept occurrences
				occurrenceVector = occurrenceVector.add(reducedVector);
			}
		}

		TimeSeries newTimeSeries = new TimeSeries();
		newTimeSeries.setProject(timeSeries.getProject());
		newTimeSeries.setScenario(timeSeries.getScenario());
		newTimeSeries.setTraceConcepts(traceConcepts);
		newTimeSeries.setTraceMatrix(reducedMatrix);
		newTimeSeries.setOccurrences(occurrenceVector);

		return newTimeSeries;
	}

	/**
	 * <code>
	 * Create a segmented time series.
	 * 
	 * Segmenting a time series consist in reducing all column vectors associated
	 * to a group of steps into a single vector iteratively.
	 * 
	 * The group of steps is named segment. And reducing all steps in segment,
	 * is kind of compression of the original time series.
	 * 
	 * More precisely:
	 * 
	 * 	if we call current method with:
	 *  
	 * 		segment-count = 3
	 * 		threshold     = 0.2
	 * 		concept       = [A, C]
	 * 	
	 * 	with this matrix:
	 * 
	 * 		------------------------------------------------------------
	 * 
	 * 		   A		0.2		0.5		0		0		0.1		0.78
	 * 		   B		0		0.3		0		0.3		0		0.4
	 * 		   C		0		0		0.7		0.5		0.6		0
	 * 
	 * 		------------------------------------------------------------
	 * 
	 * 	we will obtains the following matrix:
	 * 
	 * 		------------------------------------------------------------
	 * 			
	 * 		   A		2		0		1
	 * 		   B		1		1		1
	 * 		   C		0		2		1
	 * 
	 * 		------------------------------------------------------------
	 * 
	 * 	Note: the resulting matrix contains occurrence count (njot weight) depending of threshold specified
	 * 
	 * @param timeSeries
	 *        the time series to compress
	 * @param segmentCount
	 *        the total number of segment to generate
	 * @param threshold
	 *        the concept weight threshold to use to select concepts
	 * @return a new time series instance with segmented trace matrix gathering
	 *         only concept occurrences (not weight).
	 */
	private static TimeSeries getSegmentedTimeSeries(TimeSeries timeSeries, int segmentCount, double threshold) {

		List<Concept> traceConcepts = timeSeries.getTraceConcepts();

		// Create an empty segmented matrix
		RealMatrix segmentedMatrix = MatrixUtils.createRealMatrix(traceConcepts.size(), segmentCount);

		// Retrieve size of each segment (based on segment count)
		int segmentSize = timeSeries.getTraceMatrix().getColumnDimension() / segmentCount;

		// Create a vector for global concept occurrences
		RealVector occurrenceVector = new ArrayRealVector(traceConcepts.size());

		int traceNumber = 0;

		// Compute each segment value
		for (int segmentNumber = 0; segmentNumber < segmentCount; segmentNumber++) {

			RealVector matrixVector = new ArrayRealVector(traceConcepts.size());

			// Scan steps in segment
			for (int i = traceNumber; i < traceNumber + segmentSize; i++) {

				// Retrieve concept vector from original matrix
				RealVector traceVector = timeSeries.getTraceMatrix().getColumnVector(i);

				for (int j = 0; j < traceVector.getDimension(); j++) {

					// Detect vector component based on threshold
					if (traceVector.getEntry(j) >= threshold) {
						traceVector.setEntry(j, 1d);
					}
					else {
						traceVector.setEntry(j, 0d);
					}
				}

				// Add current vector the new concept vector,
				// So it will sum up, for current segment, all concepts occurrences
				matrixVector = matrixVector.add(traceVector);
			}

			// Update matrix column for current segment
			segmentedMatrix.setColumnVector(segmentNumber, matrixVector);

			// Update global concept occurrences
			occurrenceVector = occurrenceVector.add(matrixVector);

			// Move to next segment
			traceNumber += segmentSize;
		}

		TimeSeries newTimeSeries = new TimeSeries();
		newTimeSeries.setProject(timeSeries.getProject());
		newTimeSeries.setScenario(timeSeries.getScenario());
		newTimeSeries.setTraceConcepts(timeSeries.getTraceConcepts());
		newTimeSeries.setTraceMatrix(segmentedMatrix);
		newTimeSeries.setOccurrences(occurrenceVector);

		return newTimeSeries;
	}
}
