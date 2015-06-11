package ch.hesge.csim2.core.logic;

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
		return TraceDao.findByScenario(scenario);
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
