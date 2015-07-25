package ch.hesge.csim2.core.logic;

import java.util.ArrayList;
import java.util.List;

import ch.hesge.csim2.core.dao.TraceDao;
import ch.hesge.csim2.core.model.Scenario;
import ch.hesge.csim2.core.model.Trace;
import ch.hesge.csim2.core.utils.Console;
import ch.hesge.csim2.core.utils.DaoUtils;

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
		
		Console.writeDebug(TraceLogic.class, "loading scenario traces.");

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
	 * @return a list of trace root
	 */
	public static List<Trace> getTraceTree(Scenario scenario) {
		
		int level = -1;
		Trace currentTrace = null;
		List<Trace> rootTraces = new ArrayList<>();

		Console.writeDebug(TraceLogic.class, "loading scenario traces.");

		List<Trace> traces = TraceDao.findByScenario(scenario);

		Console.writeDebug(TraceLogic.class, "building trace hierarchy.");
		
		// Scan all trace entering/exiting
		for (Trace trace : traces) {
			
			if (trace.isEnteringTrace()) {
				
				level++;

				// The trace has no parent => it is a root trace item
				if (currentTrace == null) {
					rootTraces.add(trace);
				}
				
				// The trace has a parent, so it is a child
				else {
					
					// Update child and define as current element
					currentTrace.getChildren().add(trace);
					trace.setParent(currentTrace);
					trace.setLevel(level);
				}
				
				currentTrace = trace;
			}
			else {
				
				// Restore previous element from stack
				level--;
				currentTrace = currentTrace.getParent();
			}
		}
		
		Console.writeDebug(TraceLogic.class, "trace hierarchy successfully built.");
		
		return rootTraces;
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

		if (DaoUtils.isNewObject(trace)) {
			TraceDao.add(trace);
		}
		else {
			TraceDao.update(trace);
		}
	}
}
