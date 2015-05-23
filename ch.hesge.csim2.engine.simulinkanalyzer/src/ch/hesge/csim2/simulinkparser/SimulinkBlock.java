package ch.hesge.csim2.simulinkparser;

import java.util.ArrayList;
import java.util.List;

public class SimulinkBlock {

	// Private attributes
	private String				sid;
	private String				nodeType;
	private String				blockType;
	private SimulinkBlock		parent;
	private String				name;
	private String				value;
	private int					sourceLine;

	private List<SimulinkBlock>	inputPorts;
	private List<SimulinkBlock>	outputPorts;
	private List<SimulinkBlock>	inputs;
	private List<SimulinkBlock>	outputs;
	private List<SimulinkBlock>	children;

	/**
	 * Default constructor
	 */
	public SimulinkBlock() {
		children = new ArrayList<>();
		inputPorts = new ArrayList<>();
		outputPorts = new ArrayList<>();
		inputs = new ArrayList<>();
		outputs = new ArrayList<>();
	}

	/**
	 * Return the block sid
	 * 
	 * @return the sid
	 */
	public String getSid() {
		return sid;
	}

	/**
	 * Sets the block sid
	 * 
	 * @param sid
	 *            the sid to set
	 */
	public void setSid(String sid) {
		this.sid = sid;
	}

	/**
	 * Return true if block is a parameter
	 */
	public boolean isParameter() {
		return this.nodeType.equals("Parameter");
	}

	/**
	 * Return the type of the block
	 * 
	 * @return the blockType
	 */
	public String getBlockType() {
		return blockType;
	}

	/**
	 * Sets the type of the block
	 * 
	 * @param blockType
	 *            the blockType to set
	 */
	public void setBlockType(String blockType) {
		this.blockType = blockType;
	}

	/**
	 * Return block name
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the block name
	 * 
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Return the simulink node type.
	 * 
	 * @return the simulink node type
	 */
	public String getNodeType() {
		return nodeType;
	}

	/**
	 * Set the simulink node type
	 * 
	 * @param nodeType
	 *            the simulink node type to set
	 */
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	/**
	 * Return the owner of the block
	 * 
	 * @return the parent
	 */
	public SimulinkBlock getParent() {
		return parent;
	}

	/**
	 * Sets the owner of the block
	 * 
	 * @param parent
	 *            the parent to set
	 */
	public void setParent(SimulinkBlock parent) {
		this.parent = parent;
	}

	/**
	 * Return the source line in file of current block
	 * 
	 * @return the line
	 */
	public int getSourceLine() {
		return sourceLine;
	}

	/**
	 * Set the source line in file of current block
	 * 
	 * @param line
	 *            the line to set
	 */
	public void setSourceLine(int sourceLine) {
		this.sourceLine = sourceLine;
	}

	/**
	 * Return a list of all blocks owned by current one
	 * 
	 * @return the children
	 */
	public List<SimulinkBlock> getChildren() {
		return children;
	}

	/**
	 * Return all input port of current block
	 * 
	 * @return the inputPorts
	 */
	public List<SimulinkBlock> getInputPorts() {
		return inputPorts;
	}

	/**
	 * Return output ports of current block
	 * 
	 * @return the outputPorts
	 */
	public List<SimulinkBlock> getOutputPorts() {
		return outputPorts;
	}

	/**
	 * Return all blocks directly connected to current block
	 * 
	 * @return the inputs
	 */
	public List<SimulinkBlock> getInputs() {
		return inputs;
	}

	/**
	 * Return all blocks this block is directly connected to
	 * 
	 * @return the port outputs
	 */
	public List<SimulinkBlock> getOutputs() {
		return outputs;
	}

	/**
	 * Return the parameter value
	 * 
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Return string object representation
	 */
	public String toString() {

		if (this.isParameter()) {
			return this.getName() + " " + this.getValue();
		}
		else {

			if (this.getName() != null) {
				return this.getName();
			}

			return this.getNodeType();
		}
	}
}
