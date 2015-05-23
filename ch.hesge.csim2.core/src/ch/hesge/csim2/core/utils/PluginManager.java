package ch.hesge.csim2.core.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A simple plugin manager, allowing dynamically loading facility. Code
 * originally taken and adapted from the standard JDK:
 * 
 * java.util.ServiceLoader (since 1.6)
 * 
 * For more information, see official reference on the Java 1.7 JDK source.
 * 
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public final class PluginManager<T> implements Iterable<T> {

	// Private attributes
	private ClassLoader	classLoader;
	private Class<T>	pluginInterface;
	private List<T>		pluginList;
	private String		configPath;

	/**
	 * Create a plugin manager for a specific interface.
	 * 
	 * @param pluginInterface
	 *            the interface providers should implements
	 * @return an instance of the plugin manager
	 */
	public static <T> PluginManager<T> loadPlugins(Class<T> pluginInterface) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		return PluginManager.loadPlugins(pluginInterface, classLoader, null);
	}

	/**
	 * Create a plugin manager for a specific interface and load all plugins
	 * from
	 * 
	 * @param pluginInterface
	 *            the interface providers should implements
	 * @param classLoader
	 *            the classloader used to instanciate plugin providers
	 * @return an instance of the plugin manager
	 */
	public static <T> PluginManager<T> loadPlugins(Class<T> pluginInterface, ClassLoader classLoader) {
		return PluginManager.loadPlugins(pluginInterface, classLoader, null);
	}

	/**
	 * Create a plugin manager for a specific interface and load all plugins
	 * from
	 * 
	 * @param pluginInterface
	 *            the interface plugins should implements
	 * @param classLoader
	 *            the classloader used to instanciate plugins
	 * @param configPath
	 *            the path to the configuration file to use to load plugins
	 * @return an instance of the plugin manager
	 */
	public static <T> PluginManager<T> loadPlugins(Class<T> pluginInterface, ClassLoader classLoader, String configPath) {
		return new PluginManager<T>(pluginInterface, classLoader, configPath);
	}

	/**
	 * Create a plugin manager for a specific interface.
	 * 
	 * @param pluginInterface
	 *            the interface the providers should implements
	 * @param classLoader
	 *            the class loader used to load providers
	 * @param configPath
	 *            the path to the configuration file to use to load plugins
	 */
	private PluginManager(Class<T> pluginInterface, ClassLoader classLoader, String configPath) {

		this.classLoader = classLoader;
		this.pluginInterface = pluginInterface;
		this.pluginList = new ArrayList<>();
		this.configPath = configPath;

		// Retrieve configuration path
		if (this.configPath == null) {
			this.configPath = "conf/plugins.properties";
			if (System.getProperties().contains("ch.hesge.csim2.plugins.file")) {
				this.configPath = System.getProperties().getProperty("ch.hesge.csim2.plugins.file");
			}
		}

		// Load all plugins registered in config file
		loadPlugins();
	}

	/**
	 * Reload the plugin configuration file (plugins.properties) and
	 * reinitialize current loaded plugins.
	 */
	private void loadPlugins() {

		pluginList.clear();
		Console.writeDebug("loading " + pluginInterface.getSimpleName() + " plugins defined in " + this.configPath + ".");

		// Retrieve path to plugin configuration file
		Path configurationPath = Paths.get(configPath);

		try {
			List<String> configLines = Files.readAllLines(configurationPath);

			// Scan each line of the config file
			for (String line : configLines) {

				if (line == null || line.trim().length() == 0 || line.trim().startsWith("#"))
					continue;

				// Extract line information
				String[] configLineItems = line.split("=");

				if (configLineItems.length != 2) {
					Console.writeError("wrong formatted entry '" + line + "' found in file " + configurationPath + ".");
				}
				else {

					String interfaceName = configLineItems[0].trim();
					String pluginClassName = configLineItems[1].trim();

					// Take into account only plugins with specified interface
					if (pluginInterface.getName().equals(interfaceName)) {

						// Create a new plugin instance
						T pluginInstance = createNewInstance(pluginClassName);

						if (pluginInstance != null) {
							pluginList.add(pluginInstance);
							Console.writeDebug(pluginInstance.getClass().getSimpleName() + " plugin loaded.");
						}
					}
				}
			}
		}
		catch (IOException e) {
			Console.writeError("error while reading config file '" + configurationPath + ": " + StringUtils.toString(e));
		}
	}

	/**
	 * Return an iterator on all plugin declared within the configuration file.
	 */
	public Iterator<T> iterator() {

		return new Iterator<T>() {

			Iterator<T>	pluginIterator	= pluginList.iterator();

			public boolean hasNext() {
				return pluginIterator.hasNext();
			}

			public T next() {
				return pluginIterator.next();
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	/**
	 * Create a new instance of the plugin whose classname is passed in
	 * argument.
	 * 
	 * @param pluginClassName
	 *            the name of the class to instanciate
	 * @return the new plugin instance
	 */
	private T createNewInstance(String pluginClassName) {

		T pluginInstance = null;
		Class<?> pluginClass = null;

		try {
			// Load class
			pluginClass = Class.forName(pluginClassName, true, classLoader);

			// Check if it implements correct interface
			if (!pluginInterface.isAssignableFrom(pluginClass)) {
				Console.writeError("class " + pluginClassName + " does not implement interface " + pluginInterface.getName() + ".");
			}

			// Try to cast instance to correct class
			else {
				try {
					pluginInstance = pluginInterface.cast(pluginClass.newInstance());
				}
				catch (Throwable t) {
					Console.writeError("unable to casting " + pluginClassName + " into interface " + pluginInterface.getName() + ": " + StringUtils.toString(t));
				}
			}
		}
		catch (ClassNotFoundException e) {
			Console.writeError("ClassNotFoundException while loading class " + pluginClassName + ": " + StringUtils.toString(e));
		}

		return pluginInstance;
	}

	/**
	 * Returns a string describing this service.
	 * 
	 * @return a descriptive string
	 */
	public String toString() {
		return this.getClass().getName() + "[" + pluginInterface.getName() + "]";
	}
}
