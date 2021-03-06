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
import ch.hesge.csim2.core.utils.DaoUtils;

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
			step.setExecutionTime(0);
		}
	}

	/**
	 * Create a new scenario.
	 * 
	 * @param name
	 *        the scenario name
	 * @param project
	 *        the owning project
	 * @return and instance of scenario
	 */
	public static Scenario createScenario(String name, Project project) {
		
		Scenario scenario = new Scenario();
		
		scenario.setName(name);
		scenario.setProjectId(project.getKeyId());
		ScenarioDao.add(scenario);
		
		return scenario;
	}

	/**
	 * Create a scenario step.
	 * 
	 * @param step
	 *        the step to attach to
	 * @param scenario
	 * @return the newly create step
	 */
	public static ScenarioStep createStep(String name, String description, Scenario scenario) {

		ScenarioStep step = new ScenarioStep();

		step.setName(name);
		step.setDescription(description);
		scenario.getSteps().add(step);

		saveScenario(scenario);

		return step;
	}

	/**
	 * Save a scenario with its steps.
	 * 
	 * @param scenario
	 *        the scenario to save
	 */
	public static void saveScenario(Scenario scenario) {

		// Save the scenario
		if (DaoUtils.isNewObject(scenario)) {
			ScenarioDao.add(scenario);
		}
		else {
			ScenarioDao.update(scenario);
		}

		// Save its steps
		for (ScenarioStep step : scenario.getSteps()) {

			step.setScenarioId(scenario.getKeyId());

			// Save the scenario
			if (DaoUtils.isNewObject(step)) {
				ScenarioStepDao.add(step);
			}
			else {
				ScenarioStepDao.update(step);
			}
		}
	}

	/**
	 * Save all scenarios.
	 * 
	 * @param scenarios
	 *        the scenario list to save
	 */
	public static void saveScenarios(List<Scenario> scenarios) {

		for (Scenario scenario : scenarios) {
			saveScenario(scenario);
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
			deleteScenario(scenario);
		}
	}

	/**
	 * Delete a single scenario.
	 * 
	 * @param scenario
	 *        the scenario to delete
	 */
	public static void deleteScenario(Scenario scenario) {
		ScenarioStepDao.deleteByScenario(scenario);
		ScenarioDao.delete(scenario);
	}

	/**
	 * Delete a single scenario step.
	 * 
	 * @param step
	 *        the scenario owning the step
	 * @param step
	 *        the scenario step to delete
	 */
	public static void deleteScenarioStep(Scenario scenario, ScenarioStep step) {
		scenario.getSteps().remove(step);
		ScenarioStepDao.delete(step);
	}
}
