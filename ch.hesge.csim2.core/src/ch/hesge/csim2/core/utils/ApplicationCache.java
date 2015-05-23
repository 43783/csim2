package ch.hesge.csim2.core.utils;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class ApplicationCache<A, B> extends LinkedHashMap<A, B> {

	// Private attributes
	private final int maxEntries;

	/**
	 * Default constructor.
	 * 
	 * @param maxEntries
	 *            max allowed entries in cache
	 */
	public ApplicationCache(final int maxEntries) {
		super(maxEntries + 1, 1.0f, true);
		this.maxEntries = maxEntries;
	}

	/**
	 * Return a casted object from cache content.
	 * 
	 * @param key
	 *            the key to the object to return
	 * @return the casted object
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		return (T) super.get(key);
	}

	/**
	 * Return true if a key is missing in cache.
	 * 
	 * @param key
	 *            the key to check
	 * @return true, if the key is missing, false otherwise
	 */
	public boolean isCacheMissed(String key) {
		return !super.containsKey(key);
	}

	/**
	 * Returns <tt>true</tt> if this <code>LruCache</code> has more entries than the maximum specified when it was created.
	 *
	 * <p>
	 * This method <em>does not</em> modify the underlying <code>Map</code>; it relies on the implementation of <code>LinkedHashMap</code> to do that, but that behavior is documented in the JavaDoc
	 * for <code>LinkedHashMap</code>.
	 * </p>
	 *
	 * @param eldest
	 *            the <code>Entry</code> in question; this implementation doesn't care what it is, since the implementation is only dependent on the size of the cache
	 * @return <tt>true</tt> if the oldest
	 * @see java.util.LinkedHashMap#removeEldestEntry(Map.Entry)
	 */
	@Override
	protected boolean removeEldestEntry(final Map.Entry<A, B> eldest) {
		
		boolean isPruningRequired = super.size() > maxEntries;
		
		if (isPruningRequired) {
			System.out.println("ApplicationCache pruning required:");
			System.out.println("  current: " + super.size());
			System.out.println("  maximum: " + maxEntries);
		}
		return isPruningRequired;
	}
}
