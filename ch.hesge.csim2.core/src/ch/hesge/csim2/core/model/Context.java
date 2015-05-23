package ch.hesge.csim2.core.model;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

/**
 * Represents an engine context containing named properties accessible while an
 * engine is running.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 **/

@SuppressWarnings("serial")
public class Context extends Hashtable<Object, Object> {

	// Private attributes
	private Context defaults;

	/**
	 * Creates an empty property list with no default values.
	 */
	public Context() {
		this.defaults = null;
	}

	/**
	 * Creates an empty property list with the specified defaults.
	 *
	 * @param defaults
	 *        the defaults.
	 */
	public Context(Context defaults) {
		this.defaults = defaults;
	}

	/**
	 * Creates a property list with properties passed in argument.
	 *
	 * @param properties
	 *        the properties this context will use.
	 */
	public Context(Properties properties) {
		for (String propertyName : properties.stringPropertyNames()) {
			this.setProperty(propertyName, properties.getProperty(propertyName));
		}
	}

	/**
	 * Add a new properties.
	 * 
	 * @param key
	 *        the key to be placed into this property list.
	 * @param value
	 *        the value corresponding to <tt>key</tt>.
	 * @return the previous value of the specified key in this property list, or
	 *         {@code null} if it did not have one.
	 */
	public synchronized boolean containsKey(String key) {
		return super.containsKey(key);
	}

	/**
	 * Add a new properties.
	 * 
	 * @param key
	 *        the key to be placed into this property list.
	 * @param value
	 *        the value corresponding to <tt>key</tt>.
	 * @return the previous value of the specified key in this property list, or
	 *         {@code null} if it did not have one.
	 */
	public synchronized Object setProperty(String key, Object value) {
		return super.put(key, value);
	}

	/**
	 * Searches for the property with the specified key in this property list.
	 * If the key is not found in this property list, the default property list,
	 * and its defaults, recursively, are then checked. The method returns
	 * {@code null} if the property is not found.
	 *
	 * @param key
	 *        the property key.
	 * @return the value in this property list with the specified key value.
	 * @see #setProperty
	 * @see #defaults
	 */
	public Object getProperty(String key) {
		Object val = super.get(key);
		return (val == null && defaults != null ? defaults.getProperty(key) : val);
	}

	/**
	 * Searches for the property with the specified key in this property list.
	 * If the key is not found in this property list, the default property list,
	 * and its defaults, recursively, are then checked. The method returns the
	 * default value argument if the property is not found.
	 *
	 * @param key
	 *        the hashtable key.
	 * @param defaultValue
	 *        a default value.
	 *
	 * @return the value in this property list with the specified key value.
	 * @see #setProperty
	 * @see #defaults
	 */
	public Object getProperty(String key, Object defaultValue) {
		Object val = this.getProperty(key);
		return (val == null ? defaultValue : val);
	}

	/**
	 * Removes the key (and its corresponding value) from this
	 * hashtable. This method does nothing if the key is not in the hashtable.
	 *
	 * @param key
	 *        the key that needs to be removed
	 * @return the value to which the key had been mapped in this hashtable,
	 *         or <code>null</code> if the key did not have a mapping
	 * @throws NullPointerException
	 *         if the key is <code>null</code>
	 */
	public Object remove(String key) {
		return super.remove(key);
	}

	/**
	 * Add a properties content to the context.
	 * 
	 * @param properties
	 */
	public synchronized void putAll(Properties properties) {

		Enumeration<Object> enumeration = properties.keys();

		while (enumeration.hasMoreElements()) {
			String key = (String) enumeration.nextElement();
			this.put(key, properties.get(key));
		}
	}
}
