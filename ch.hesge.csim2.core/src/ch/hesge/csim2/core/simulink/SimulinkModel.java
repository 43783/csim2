package ch.hesge.csim2.core.simulink;

import java.util.HashMap;
import java.util.Map;

/**
 * Represent a simulink model with its block hierarchy. Note: block parameter
 * are also represented as block with nodeType = "Parameter".
 * 
 * @author Eric Harth
 *
 */
public class SimulinkModel {

	// Private attributes
	private int							maxSid;
	private SimulinkBlock				root;
	private Map<String, SimulinkBlock>	blocksBySid;

	/**
	 * Default constructor
	 */
	public SimulinkModel() {
		blocksBySid = new HashMap<>();
	}

	/**
	 * Return max SID in model
	 * 
	 * @return the maxSid
	 */
	public int getMaxSid() {
		return maxSid;
	}

	/**
	 * Sets max SID in model
	 * 
	 * @param maxSid
	 *            the maxSid to set
	 */
	public void setMaxSid(int maxSid) {
		this.maxSid = maxSid;
	}

	/**
	 * Return the root block
	 * 
	 * @return the root block
	 */
	public SimulinkBlock getRoot() {
		return root;
	}

	/**
	 * Set the root block
	 * 
	 * @param root
	 *            the root block to set
	 */
	public void setRoot(SimulinkBlock root) {
		this.root = root;
	}

	/**
	 * Return a map of all block classified by their SID
	 * 
	 * @return the blocks map
	 */
	public Map<String, SimulinkBlock> getBlocksBySid() {
		return blocksBySid;
	}
}
