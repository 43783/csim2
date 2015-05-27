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
	 * <code>
	 * Retrieve the time series associated to a scenario traces.
	 * 
	 * A time series contains a list of concepts (found within the trace),
	 * and matrix representing all concepts weights associated to each step trace.
	 * 
	 * The matrix can be for instance:
	 * 
	 * 								column = concepts weight
	 * 		 rows						|		 	concepts weight
	 * 		= concepts					|				|
	 * 			
	 * 		   A						0.2		0.5		0		0
	 * 		   B						0		0.3		0.7		0.3
	 * 		   C						0		0		0		0.5
	 * 
	 * 								----------------------------------> list of column vectors
	 * 
	 * 		columns = trace steps:		0	1	2	3	...
	 * 
	 * </code>
	 * 
	 * @param project
	 *            the project owning the traces
	 * @param scenario
	 *            the scenario owning the traces
	 * @return the TimeSeries object gathering trace information
	 */
	public static TimeSeries getTimeSeries(Project project, Scenario scenario) {

		Map<Integer, Concept> conceptsFoundInTrace = new HashMap<>();
		Map<Integer, List<MethodConceptMatch>> matchMap = ApplicationLogic
				.getMethodMatchingMap(project);

		// First retrieve unique methods found in trace
		List<Integer> uniqueIds = TraceDao.findDistinctMethodIds(scenario);

		// Then for each method, retrieve matching concepts
		for (Integer methodId : uniqueIds) {

			// Handle only methods with at least one matching
			if (matchMap.containsKey(methodId)) {

				// Scan all concepts matching method
				for (MethodConceptMatch match : matchMap.get(methodId)) {

					// If concept not already include, put it on map
					if (!conceptsFoundInTrace.containsKey(match.getConceptId())) {
						conceptsFoundInTrace.put(match.getConceptId(),
								match.getConcept());
					}
				}
			}
		}

		// Finally sort all concepts by name
		List<Concept> traceConcepts = new ArrayList<>(
				conceptsFoundInTrace.values());
		ObjectSorter.sortConcepts(traceConcepts);

		// Retrieve all trace for specified scenario
		List<Trace> scenarioTraces = TraceDao.findByScenario(scenario);

		// Create an empty matrix
		RealMatrix traceMatrix = MatrixUtils.createRealMatrix(
				traceConcepts.size(), scenarioTraces.size());

		int col = 0;
		for (Trace trace : scenarioTraces) {

			// Check if current trace has some concept matching
			if (matchMap.containsKey(trace.getMethodId())) {

				// Scan all matching for current method
				for (MethodConceptMatch match : matchMap.get(trace
						.getMethodId())) {

					// Retrieve concept index concerned by the match
					int conceptIndex = traceConcepts
							.indexOf(match.getConcept());

					// Update associated column in matrix with weight associated
					// to the concept
					if (conceptIndex != -1) {
						traceMatrix.setEntry(conceptIndex, col,
								match.getWeight());
					}
				}
			}

			col++;
		}

		TimeSeries timeSeries = new TimeSeries();

		// Initialize time series
		timeSeries.setProject(project);
		timeSeries.setScenario(scenario);
		timeSeries.setTraceConcepts(traceConcepts);
		timeSeries.setTraceMatrix(traceMatrix);

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
	 *            the time series to compress
	 * @param segmentCount
	 *            the total number of segment to generate
	 * @param threshold
	 *            the concept weight threshold to use to select concepts
	 * @param concepts
	 *            all concepts found in segmented time series
	 * @return a new time series instance with segmented trace matrix gathering
	 *         only information associated to concepts passed in argument.
	 */
	public static TimeSeries getFilteredTimeSeries(TimeSeries timeSeries,
			int segmentCount, double threshold, List<Concept> concepts) {

		// Retrieve segmented time series
		TimeSeries segmentedSeries = getSegmentedTimeSeries(timeSeries,
				segmentCount, threshold);

		List<Concept> traceConcepts = concepts;
		List<Concept> foundConcepts = segmentedSeries.getTraceConcepts();

		if (traceConcepts.size() == 0) {
			traceConcepts = foundConcepts;
		}

		RealMatrix reducedMatrix = null;

		if (traceConcepts.size() > 0) {

			// Create a reduced matrix with a subset of all concepts
			reducedMatrix = MatrixUtils.createRealMatrix(traceConcepts.size(),
					segmentCount);

			// Convert each matrix column vector (with all concepts) in proper
			// column vector (concept subset)
			for (int i = 0; i < segmentCount; i++) {

				RealVector traceVector = segmentedSeries.getTraceMatrix()
						.getColumnVector(i);
				RealVector reducedVector = new ArrayRealVector(
						traceConcepts.size());

				// Gather occurrences only for specified concepts
				for (int j = 0; j < traceConcepts.size(); j++) {

					Concept concept = traceConcepts.get(j);
					int conceptIndex = foundConcepts.indexOf(concept);

					if (conceptIndex != -1) {
						double conceptCount = traceVector
								.getEntry(conceptIndex);
						reducedVector.setEntry(j, conceptCount);
					}
				}

				// Update matrix column for current segment
				reducedMatrix.setColumnVector(i, reducedVector);
			}
		}

		TimeSeries newTimeSeries = new TimeSeries();
		newTimeSeries.setProject(timeSeries.getProject());
		newTimeSeries.setScenario(timeSeries.getScenario());
		newTimeSeries.setTraceConcepts(traceConcepts);
		newTimeSeries.setTraceMatrix(reducedMatrix);

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
	 * 		   B		1		1		1
	 * 		   C		0		2		1
	 * 
	 * 		------------------------------------------------------------
	 * 
	 * 	Note: the resulting matrix contains occurrence count depending of threshold (not weights)
	 * 
	 * @param timeSeries
	 *            the time series to compress
	 * @param segmentCount
	 *            the total number of segment to generate
	 * @param threshold
	 *            the concept weight threshold to use to select concepts
	 * @return a new time series instance with segmented trace matrix gathering
	 *         only concept occurrences (not weight).
	 */
	private static TimeSeries getSegmentedTimeSeries(TimeSeries timeSeries,
			int segmentCount, double threshold) {

		List<Concept> traceConcepts = timeSeries.getTraceConcepts();
		Map<Integer, Concept> conceptsFoundInTrace = new HashMap<>();

		// Create an empty segmented matrix
		RealMatrix segmentedMatrix = MatrixUtils.createRealMatrix(
				traceConcepts.size(), segmentCount);

		// Retrieve size of each segment (based on segment count)
		int segmentSize = timeSeries.getTraceMatrix().getColumnDimension() / segmentCount;

		int traceNumber = 0;

		// Compute each segment value
		for (int segmentNumber = 0; segmentNumber < segmentCount; segmentNumber++) {

			RealVector matrixVector = new ArrayRealVector(traceConcepts.size());

			// Scan steps in segment
			for (int i = traceNumber; i < traceNumber + segmentSize; i++) {

				// Retrieve concept vector from original matrix
				RealVector traceVector = timeSeries.getTraceMatrix()
						.getColumnVector(i);

				for (int j = 0; j < traceVector.getDimension(); j++) {

					// Detect vector component based on threshold
					if (traceVector.getEntry(j) > threshold) {

						// Normalize vector values to 1.0
						traceVector.setEntry(j, 1d);

						// Retrieve concept from vector component index
						Concept concept = traceConcepts.get(j);

						// If concept not already include, put it on map
						if (!conceptsFoundInTrace.containsKey(concept
								.getKeyId())) {
							conceptsFoundInTrace.put(concept.getKeyId(),
									concept);
						}
					} else {
						// Normalize vector values to 0.0
						traceVector.setEntry(j, 0d);
					}
				}

				// Add current vector the new concept vector (so it will sum all
				// concepts occurrences)
				matrixVector = matrixVector.add(traceVector);
			}

			// Update matrix column for current segment
			segmentedMatrix.setColumnVector(segmentNumber, matrixVector);
			traceNumber += segmentSize;
		}

		List<Concept> conceptsFound = new ArrayList<>(
				conceptsFoundInTrace.values());

		TimeSeries newTimeSeries = new TimeSeries();
		newTimeSeries.setProject(timeSeries.getProject());
		newTimeSeries.setScenario(timeSeries.getScenario());
		newTimeSeries.setTraceConcepts(conceptsFound);
		newTimeSeries.setTraceMatrix(segmentedMatrix);

		return newTimeSeries;
	}
}
