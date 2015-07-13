/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hesge.csim2.core.logic;

import java.util.List;

import ch.hesge.csim2.core.dao.ScenarioDao;
import ch.hesge.csim2.core.dao.ScenarioStepDao;
import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.Scenario;
import ch.hesge.csim2.core.model.ScenarioStep;
import ch.hesge.csim2.core.utils.ObjectSorter;
import ch.hesge.csim2.core.utils.PersistanceUtils;

/**
 * This class implement all logical rules associated to scenario.
 *
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

class ScenarioLogic {

	/**
	 * Retrieve all available scenarios.
	 * 
	 * @return a list of scenario
	 */
	public static List<Scenario> getScenarios() {
		return ScenarioDao.findAll();
	}

	/**
	 * Retrieve all scenarios and its step owned by a project.
	 * 
	 * @param project
	 *        the owner
	 * 
	 * @return a list of scenario
	 */
	public static List<Scenario> getScenarios(Project project) {

		List<Scenario> scenarios = ScenarioDao.findByProject(project);

		for (Scenario scenario : scenarios) {
			List<ScenarioStep> steps = ScenarioStepDao.findByScenario(scenario);
			ObjectSorter.sortScenarioSteps(steps);
			scenario.getSteps().addAll(steps);
		}

		return scenarios;
	}

	/**
	 * Initialize a step execution time with current internal timer.
	 * 
	 * @param step
	 *        the scenario step to modify
	 */
	public static void initExecutionTime(ScenarioStep step) {
		step.setExecutionTime(System.currentTimeMillis());
	}

	/**
	 * Reset all step execution time of a scenario.
	 * 
	 * @param scenario
	 *        the scenario owning the steps to reset
	 */
	public static void resetExecutionTimes(Scenario scenario) {

		for (ScenarioStep step : scenario.getSteps()) {
			step.setExecutionTime(-1);
		}
	}

	/**
	 * Save all scenarios without their steps.
	 * 
	 * @param scenarios
	 *        the scenario list to save
	 */
	public static void saveScenarios(List<Scenario> scenarios) {

		for (Scenario scenario : scenarios) {

			if (PersistanceUtils.isNewObject(scenario)) {
				ScenarioDao.add(scenario);
			}
			else {
				ScenarioDao.update(scenario);
			}
		}
	}

	/**
	 * Delete all scenario owned by a project.
	 * 
	 * @param project
	 *        the project owning scenarios
	 */
	public static void deleteScenarios(Project project) {

		for (Scenario scenario : project.getScenarios()) {
			ScenarioStepDao.deleteByScenario(scenario);
			ScenarioDao.delete(scenario);
		}
	}

	/**
	 * Save a scenario with its steps.
	 * 
	 * @param scenario
	 *        the scenario to save
	 */
	public static void saveScenario(Scenario scenario) {

		// Save the scenario
		if (PersistanceUtils.isNewObject(scenario)) {
			ScenarioDao.add(scenario);
		}
		else {
			ScenarioDao.update(scenario);
		}

		// Save its steps
		for (ScenarioStep step : scenario.getSteps()) {

			step.setScenarioId(scenario.getKeyId());

			// Save the scenario
			if (PersistanceUtils.isNewObject(step)) {
				ScenarioStepDao.add(step);
			}
			else {
				ScenarioStepDao.update(step);
			}
		}
	}
}
