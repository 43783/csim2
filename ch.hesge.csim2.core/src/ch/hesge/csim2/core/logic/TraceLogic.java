package ch.hesge.csim2.core.logic;

import java.util.ArrayList;
import java.util.List;

import ch.hesge.csim2.core.dao.TraceDao;
import ch.hesge.csim2.core.model.Scenario;
import ch.hesge.csim2.core.model.Trace;
import ch.hesge.csim2.core.utils.PersistanceUtils;

/**
 * This class implement all logical rules associated to traces owned by scenarios.
 *
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

class TraceLogic {

	/**
	 * Retrieve all traces owned by a scenario.
	 * 
	 * @param scenario
	 *        the scenario
	 */
	public static List<Trace> getTraces(Scenario scenario) {
		
		int level = -1;
		List<Trace> traces = new ArrayList<>();
		
		for (Trace trace : TraceDao.findByScenario(scenario)) {
			
			// Compute trace level
			level += trace.isEnteringTrace() ? 1 : -1;
			
			if (trace.isEnteringTrace()) {
				trace.setLevel(level);
				traces.add(trace);
			}
		}
		
		return traces;
	}

	/**
	 * Retrieve all traces owned by a scenario as a hierarchy.
	 * 
	 * @param scenario
	 * @return an instance of the root trace
	 */
	public static Trace getTraceTree(Scenario scenario) {
		
		int level = -1;
		Trace root = null;
		Trace currentTrace = null;

		// Scan all trace entering/exiting
		for (Trace trace : TraceDao.findByScenario(scenario)) {
			
			if (trace.isEnteringTrace()) {
				
				level++;

				// Keep root reference to return
				if (currentTrace == null) {
					root = trace;
					currentTrace = trace;
				}
				else {
					
					// Update child and define as current element
					currentTrace.getChildren().add(trace);
					trace.setParent(currentTrace);
					trace.setLevel(level);
					currentTrace = trace;
				}
			}
			else {
				
				// Restore previous element in stack
				level--;
				currentTrace = currentTrace.getParent();
			}
		}
		
		return root;
	}
	
	/**
	 * Delete all traces owned by a scenario.
	 * 
	 * @param scenario
	 *        the scenario
	 */
	public static void deleteTraces(Scenario scenario) {
		TraceDao.deleteByScenario(scenario);
	}

	/**
	 * Save the trace passed in argument.
	 * 
	 * @param trace
	 *        the trace to save
	 */
	public static void saveTrace(Trace trace) {

		if (PersistanceUtils.isNewObject(trace)) {
			TraceDao.add(trace);
		}
		else {
			TraceDao.update(trace);
		}
	}
}
