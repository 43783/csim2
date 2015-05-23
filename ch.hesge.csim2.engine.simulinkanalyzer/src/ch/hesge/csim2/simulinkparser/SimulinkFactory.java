package ch.hesge.csim2.simulinkparser;

import java.awt.Rectangle;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SimulinkFactory {

	/**
	 * Create a new SID for current model.
	 * 
	 * @param model
	 *            the model where the SID should be created
	 * @return the SID as a string
	 */
	public static String createNewSID(SimulinkModel model) {

		// Retrieve max SID available in model
		int sid = model.getMaxSid() + 1;

		// Change max SID
		model.setMaxSid(sid);

		return String.valueOf(sid);
	}

	/**
	 * Create a parameter with value for block passed in argument.
	 * 
	 * @param paramName
	 * @param paramValue
	 * @param paramQuotedValue
	 */
	public static SimulinkBlock createParameter(String paramName, String paramValue) {

		SimulinkBlock parameterBlock = new SimulinkBlock();

		parameterBlock.setNodeType("Parameter");
		parameterBlock.setName(paramName);
		parameterBlock.setValue(paramValue);

		return parameterBlock;
	}

	/**
	 * Create a line connecting two block by their name.
	 * 
	 * @param srcName
	 *            the source block name
	 * @param srcPort
	 *            the port within the source block
	 * @param dstName
	 *            the destination block name
	 * @param dstPort
	 *            the port within the destination block
	 * @return a new line block
	 */
	public static SimulinkBlock createLine(String srcName, String srcPort, String dstName, String dstPort) {

		SimulinkBlock lineBlock = new SimulinkBlock();
		lineBlock.setNodeType("Line");

		SimulinkUtils.setParameterValue(lineBlock, "SrcBlock", srcName);
		SimulinkUtils.setParameterValue(lineBlock, "SrcPort", srcPort);
		SimulinkUtils.setParameterValue(lineBlock, "DstBlock", dstName);
		SimulinkUtils.setParameterValue(lineBlock, "DstPort", dstPort);

		return lineBlock;
	}

	/**
	 * Create a simulink input port from the block passed in argument
	 * 
	 * @param block
	 */
	public static void createInputPort(SimulinkBlock block) {

		String portNumber = SimulinkUtils.getParameterValue(block, "Port");

		// Add default port, if required
		if (portNumber == null) {
			SimulinkUtils.setParameterValue(block, "Port", "1");
		}

		// Update parent block
		block.getParent().getInputPorts().add(block);
	}

	/**
	 * Create a simulink output port from the block passed in argument
	 * 
	 * @param block
	 */
	public static void createOutputPort(SimulinkBlock block) {

		String portNumber = SimulinkUtils.getParameterValue(block, "Port");

		// Add default port, if required
		if (portNumber == null) {
			SimulinkUtils.setParameterValue(block, "Port", "1");
		}

		// Update parent block
		block.getParent().getOutputPorts().add(block);
	}

	/**
	 * Create a simulink root node block
	 * 
	 * <pre>
	 *   Block {
	 *     name "Root"
	 *   }
	 * </pre>
	 */
	public static void createRootBlock(SimulinkModel model) {

		// Create a root block
		SimulinkBlock block = new SimulinkBlock();
		block.setNodeType("Root");

		// Add it to the model
		model.setRoot(block);
	}

	/**
	 * Create a simulink function type block
	 * 
	 * <pre>
	 *   Block {
	 *     BlockType "S-Function"
	 *     FunctionName "system"
	 *     SFunctionModules "''"
	 *     PortCounts "[]"
	 *     SFunctionDeploymentMode "off"
	 *   }
	 * </pre>
	 * 
	 * @return a simulink function block
	 */
	public static SimulinkBlock createFunctionTypeBlock() {

		SimulinkBlock functionBlock = new SimulinkBlock();
		functionBlock.setNodeType("Block");

		SimulinkUtils.setParameterValue(functionBlock, "BlockType", "\"S-Function\"");
		SimulinkUtils.setParameterValue(functionBlock, "FunctionName", "\"system\"");
		SimulinkUtils.setParameterValue(functionBlock, "SFunctionModules", "\"''\"");
		SimulinkUtils.setParameterValue(functionBlock, "PortCounts", "[]");
		SimulinkUtils.setParameterValue(functionBlock, "SFunctionDeploymentMode", "\"off\"");

		return functionBlock;
	}

	/**
	 * Create a new simulink tracing block to be inserted in a model
	 * 
	 * <pre>
	 *   Block {
	 *     SID "XXX"
	 *     BlockType "S-Function"
	 *     Name "@original_block_name"
	 *     Ports "[1, 1]"
	 *     BackgroundColor "green"
	 *     FunctionName "logfct"
	 *     EnableBusSupport "off"
	 * }
	 * </pre>
	 * 
	 * where XXX is the block SID computed for current model
	 * 
	 * @param model
	 *            the model where the SID should be created
	 * @return a logging block
	 */
	public static SimulinkBlock createTraceBlock(SimulinkModel model, SimulinkBlock block) {

		String functionSID = String.valueOf(SimulinkFactory.createNewSID(model));
		String functionName = block.getName().replaceAll("\"(.*)\"", "\"@$1\"");

		SimulinkBlock functionBlock = new SimulinkBlock();
		functionBlock.setNodeType("Block");
		functionBlock.setSid(functionSID);
		functionBlock.setBlockType("S-Function");
		functionBlock.setName(functionName);

		SimulinkUtils.setParameterValue(functionBlock, "SID", functionSID);
		SimulinkUtils.setParameterValue(functionBlock, "BlockType", "\"S-Function\"");
		SimulinkUtils.setParameterValue(functionBlock, "Name", functionName);
		SimulinkUtils.setParameterValue(functionBlock, "Ports", "[1, 1]");
		SimulinkUtils.setParameterValue(functionBlock, "BackgroundColor", "\"green\"");
		SimulinkUtils.setParameterValue(functionBlock, "FunctionName", "\"logfct\"");
		SimulinkUtils.setParameterValue(functionBlock, "EnableBusSupport", "\"off\"");

		Rectangle rect = SimulinkUtils.getBlockPosition(block);

		if (block.getBlockType() != null) {
			
			// Adjust position the left of input-port
			if (block.getBlockType().equals("Inport")) {
				rect.translate(rect.width + 10, 0);
				SimulinkUtils.setBlockPosition(functionBlock, rect);
			}
			// Adjust position the right of input-port
			else if (block.getBlockType().equals("Outport")) {
				rect.translate(-rect.width - 10, 0);
				SimulinkUtils.setBlockPosition(functionBlock, rect);
			}
		}

		return functionBlock;
	}

	/**
	 * Create a simulink logging block where XXX is a SID
	 * 
	 * <pre>
	 * Stateflow {
	 *   machine {
	 *     id 1
	 * 	   name "Calcul_batterie_instrumentee"
	 * 	   created "11-Aug-2014 16:47:01"
	 * 	   isLibrary 0
	 * 	   firstTarget 2
	 * 	   sfVersion 71014000.00001
	 *   }
	 *   target {
	 *     id 2
	 *     name "sfun"
	 * 	   description "Default Simulink S-Function Target."
	 * 	   machine 1
	 * 	   linkNode[1 0 0]
	 *   }
	 * }
	 * </pre>
	 * 
	 * @return a stateflow block
	 */
	public static SimulinkBlock createStateflowBlock() {

		SimulinkBlock stateflowBlock = new SimulinkBlock();
		stateflowBlock.setNodeType("Stateflow");

		String machineName = "Calcul_batterie_instrumentee";
		String targetName = "sfun";

		// Create & initialize the machine block
		SimulinkBlock machineBlock = new SimulinkBlock();
		machineBlock.setNodeType("machine");
		machineBlock.setName(machineName);
		stateflowBlock.getChildren().add(machineBlock);

		SimulinkUtils.setParameterValue(machineBlock, "id", "1");
		SimulinkUtils.setParameterValue(machineBlock, "name", "\"" + machineName + "\"");
		SimulinkUtils.setParameterValue(machineBlock, "isLibrary", "0");
		SimulinkUtils.setParameterValue(machineBlock, "firstTarget", "2");
		SimulinkUtils.setParameterValue(machineBlock, "sfVersion", "71014000.00001");
		
		String dateString = new SimpleDateFormat("dd-MMMM-yyyy HH:mm:ss").format(new Date());
		SimulinkUtils.setParameterValue(machineBlock, "created", "\"" + dateString + "\"");

		// Create & initialize the target block
		SimulinkBlock targetBlock = new SimulinkBlock();
		targetBlock.setNodeType("target");
		targetBlock.setName(targetName);
		stateflowBlock.getChildren().add(targetBlock);

		SimulinkUtils.setParameterValue(targetBlock, "id", "2");
		SimulinkUtils.setParameterValue(targetBlock, "name", "\"" + targetName + "\"");
		SimulinkUtils.setParameterValue(targetBlock, "description", "\"Default simulink s-function target\"");
		SimulinkUtils.setParameterValue(targetBlock, "machine", "1");
		SimulinkUtils.setParameterValue(targetBlock, "linkNode", "[1 0 0]");

		return stateflowBlock;
	}
}
