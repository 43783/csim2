/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hesge.csim2.core.logic;

import java.util.Comparator;
import java.util.List;

import ch.hesge.csim2.core.dao.ScenarioDao;
import ch.hesge.csim2.core.dao.ScenarioStepDao;
import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.Scenario;
import ch.hesge.csim2.core.model.ScenarioStep;
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
	 * Retrieve all scenarios owned by a project.
	 * 
	 * @param project
	 *        the owner
	 * 
	 * @return a list of scenario
	 */
	public static List<Scenario> getScenarios(Project project) {
		return ScenarioDao.findByProject(project);
	}

	/**
	 * Retrieves a scenario by its name.
	 * 
	 * @param project
	 *        the owner
	 * @param name
	 *        the name of the scenario
	 * @return a scenario or null
	 */
	public static Scenario getScenario(Project project, String name) {

		Scenario scenario = null;

		for (Scenario s : project.getScenarios()) {
			if (s.getName().equalsIgnoreCase(name)) {
				scenario = s;
				break;
			}
		}

		return scenario;
	}

	/**
	 * Retrieve all scenario with its steps.
	 * 
	 * @param scenario
	 *        the owner
	 * 
	 * @return
	 *         a scenario with its steps
	 */
	public static Scenario getScenarioWithDependencies(Scenario scenario) {

		// SourceClass comparator
		Comparator<ScenarioStep> stepComparator = new Comparator<ScenarioStep>() {
			@Override
			public int compare(ScenarioStep a, ScenarioStep b) {
				return a.getName().compareTo(b.getName());
			}
		};

		scenario.getSteps().clear();
		scenario.getSteps().addAll(ScenarioStepDao.findByScenario(scenario));
		scenario.getSteps().sort(stepComparator);

		return scenario;
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
