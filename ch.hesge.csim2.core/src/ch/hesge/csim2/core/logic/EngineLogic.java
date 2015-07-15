/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hesge.csim2.core.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ch.hesge.csim2.core.model.IEngine;
import ch.hesge.csim2.core.utils.Console;
import ch.hesge.csim2.core.utils.PluginManager;
import ch.hesge.csim2.core.utils.StringUtils;

/**
 * This class implement all logical rules associated to engines.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

class EngineLogic {

	// Private static attributes
	private static final int THREAD_NUMBER = 10;
	private static Map<IEngine, Future<?>> runningEngines = new HashMap<>();
	private static ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_NUMBER);

	/**
	 * Return a list of all engines registered within the application.
	 * 
	 * @return
	 *         a list of IEngine
	 */
	public static synchronized List<IEngine> getEngines() {

		List<IEngine> engines = new ArrayList<>();
		PluginManager<IEngine> engineManager = PluginManager.loadPlugins(IEngine.class);

		// Load engines into a standard list
		for (IEngine engine : engineManager) {
			engines.add(engine);
		}

		return engines;
	}

	/**
	 * Retrieves an engine by its name.
	 * 
	 * @param project
	 *        the project owning the engine
	 * @param name
	 *        the name of the engine
	 * @return an IEngine or null
	 */
	public static IEngine getEngineByName(String name) {

		IEngine engine = null;

		for (IEngine e : ApplicationLogic.UNIQUE_INSTANCE.getEngines()) {
			if (e.getName().equalsIgnoreCase(name)) {
				engine = e;
				break;
			}
		}

		return engine;
	}

	/**
	 * Return the engine running state.
	 * 
	 * @param engine
	 * @return true, if the engine is current running, false otherwise.
	 */
	public static boolean isEngineRunning(IEngine engine) {
		return runningEngines.containsKey(engine);
	}

	/**
	 * Start the engine passed in argument.
	 * A new thread is allocation on pool and used to execute the engine in a
	 * separate thread.
	 * 
	 * @param engine
	 *        the engine to start
	 */
	public static void startEngine(final IEngine engine) {

		// Create a thread for the engine
		Future<?> task = threadPool.submit(new Runnable() {

			@Override
			public void run() {

				try {

					long startTime = System.currentTimeMillis();
					Console.writeDebug(EngineLogic.class, engine.getName() + " started.");

					engine.init();
					engine.start();
					engine.stop();

					long endTime = System.currentTimeMillis();
					Console.writeDebug(EngineLogic.class, engine.getName() + " ended after " + StringUtils.getElapseTime(startTime, endTime) + ".");
				}
				catch (Exception e) {
					Console.writeError(EngineLogic.class, "error while running engine: " + StringUtils.toString(e));
				}
				finally {

					// Update running collection
					Collections.synchronizedMap(runningEngines).remove(engine);
				}
			}
		});

		// Register the engine in running map
		Collections.synchronizedMap(runningEngines).put(engine, task);
	}

	/**
	 * Stop the engine passed in argument.
	 * The thread release return the allocation from pool
	 * 
	 * @param engine
	 *        the engine to stop
	 */
	public static void stopEngine(IEngine engine) {

		if (isEngineRunning(engine)) {
			Future<?> task = runningEngines.get(engine);
			task.cancel(true);
			Console.writeDebug(EngineLogic.class, engine.getName() + " cancelled.");
		}
	}
}
