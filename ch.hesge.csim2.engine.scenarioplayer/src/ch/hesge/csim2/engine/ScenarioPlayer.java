package ch.hesge.csim2.engine;

import java.util.List;
import java.util.Properties;

import ch.hesge.csim2.core.logic.ApplicationLogic;
import ch.hesge.csim2.core.model.Context;
import ch.hesge.csim2.core.model.IEngine;
import ch.hesge.csim2.core.model.Scenario;
import ch.hesge.csim2.core.model.ScenarioStep;
import ch.hesge.csim2.core.utils.Console;
import ch.hesge.csim2.core.utils.EngineException;
import ch.hesge.csim2.core.utils.StringUtils;

/**
 * This engine plays all step of a scenario
 * and keep track of execution time of each step.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 *
 */
public class ScenarioPlayer implements IEngine {

	// Private attributes
	private Scenario scenario;
	private Context context;

	/**
	 * Default constructor.
	 */
	public ScenarioPlayer() {
	}

	/**
	 * Get the engine name.
	 * 
	 * @see ch.hesge.csim2.core.shell.IEngine#getName()
	 */
	@Override
	public String getName() {
		return "ScenarioPlayer";
	}

	/**
	 * Get the engine version.
	 * 
	 * @see ch.hesge.csim2.core.shell.IEngine#getVersion()
	 */
	@Override
	public String getVersion() {
		return "1.0.1";
	}

	/**
	 * get the engine description.
	 * 
	 * @see ch.hesge.csim2.core.shell.IEngine#getDescription()
	 */
	@Override
	public String getDescription() {
		return "play a scenario with all its steps.";
	}

	/**
	 * Return the parameter map required by the engine.
	 * 
	 * @see ch.hesge.csim2.core.shell.IEngine#getParameters()
	 */
	@Override
	public Properties getParameters() {

		Properties params = new Properties();

		params.put("project", "project");
		params.put("scenario", "scenario");

		return params;
	}

	/**
	 * Retrieve the engine context.
	 * 
	 * @see ch.hesge.csim2.core.shell.IEngine#getContext()
	 */
	@Override
	public Context getContext() {
		return this.context;
	}

	/**
	 * Sets the engine context before starting.
	 * 
	 * @see ch.hesge.csim2.core.shell.IEngine#setContext()
	 */
	@Override
	public void setContext(Context context) {
		this.context = context;
	}

	/**
	 * Initialize the engine before starting.
	 * 
	 * @see ch.hesge.csim2.core.shell.IEngine#init()
	 */
	@Override
	public void init() {

		try {

			// Retrieve current project
			if (context.containsKey("scenario")) {
				scenario = (Scenario) context.getProperty("scenario");
			}
			else {
				throw new EngineException("missing scenario specified !");
			}
		}
		catch (Exception e) {
			Console.writeError("error while instrumenting files: " + StringUtils.toString(e));
		}
	}

	/**
	 * Start the engine.
	 * 
	 * @see ch.hesge.csim2.core.shell.IEngine#start()
	 */
	@Override
	public void start() {

		try {

			List<ScenarioStep> steps = scenario.getSteps();

			Console.writeLine("scenario '" + scenario.getName() + "' started.");
			Console.writeLine("for each steps: <enter> confirm a step, <anykey> stop the scenario:");

			boolean isCancelled = false;

			for (int i = 0; i < steps.size(); i++) {

				// Retrieve current step
				ScenarioStep step = steps.get(i);

				// Trace which step is running and ask user to continue
				String userResponse = Console.readLine("  step: " + step.getName() + ", " + step.getDescription() + " ?");

				// If user has confirmed the step
				if (userResponse.length() == 0) {
					step.setExecutionTime(System.currentTimeMillis());
				}
				else {
					// Otherwise, cancel scenario
					isCancelled = true;
					break;
				}
			}

			// If scenario ended properly, make it persistent
			if (isCancelled) {
				Console.writeLine("scenario '" + scenario.getName() + "' cancelled.");
			}
			else {
				ApplicationLogic.saveScenario(scenario);
				Console.writeLine("scenario '" + scenario.getName() + "' ended.");
			}
		}
		catch (Exception e) {
			Console.writeError("error while running scenario: " + StringUtils.toString(e));
		}
	}

	/**
	 * Stop the engine.
	 * 
	 * @see ch.hesge.csim2.core.shell.IEngine#stop()
	 */
	@Override
	public void stop() {
	}
}
