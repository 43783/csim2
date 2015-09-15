package ch.hesge.csim2.core.simulink;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimulinkUtils {

	/**
	 * Return the node type of a block from a parsed line
	 * 
	 * <pre>
	 * parsing rule = <name> <space> <{>
	 * </pre>
	 * 
	 * @param parsedline
	 *            the line currently being parsed
	 * @return the node type of the block or null if nothing found
	 */
	public static String parseNodeType(String parsedline) {

		Matcher matcher = Pattern.compile("^\\s*(?<name>[^\\s]+)\\s+\\{$").matcher(parsedline);

		if (matcher.matches()) {
			return matcher.group("name").trim();
		}

		return null;
	}

/**
	 * Return a simulink parameter based on parsed line passed in argument
	 * 
	 * <pre>
	 * 
	 * parse rule for single value parameter: 
	 * 
	 * 		<name> <space> (<">) <value> (<">)
	 * 
	 * parse rule for multi valuated parameter:
	 * 
	 * 		<name> (<">) <[> <value> (<,> value)* <]>) (<">)
	 * 
	 * </pre>
	 * 
	 * @param parsedLine
	 *            the line currently being parsed
	 * @return an instance of SimulinkBlock
	 */
	public static SimulinkBlock parseParameter(String parsedLine) {

		String paramName = null;
		String paramValue = null;

		Matcher matcher = Pattern.compile("^\\s*(?<name>\\w+)?\\s*((?<quottedvalue>\\\"[^\\\"]+\\\")|(?<bracketvalue>\\[[^\\]]+\\])|(?<value>\\w+))\\s*$").matcher(parsedLine);

		if (matcher.matches()) {
			
			paramName = matcher.group("name");
			paramValue = matcher.group("quottedvalue");
			if (paramValue == null) paramValue = matcher.group("bracketvalue");
			if (paramValue == null) paramValue = matcher.group("value");
			
			SimulinkBlock parameterBlock = new SimulinkBlock();

			parameterBlock.setName(paramName);
			parameterBlock.setValue(paramValue);

			return parameterBlock;			
		}

		/*
		// Parse parameter with format: name "value"
		//Matcher matcher = Pattern.compile("^\\s*(?<name>[^\\\"]+)(?<value>\\\"[^\\\"]+\\\")\\s*$").matcher(parsedLine);
		Matcher matcher = Pattern.compile("^\\s*(?<name>\\w+)\\s*(?<value>\\\"[^\\\"]+\\\")\\s*$").matcher(parsedLine);

		if (matcher.matches()) {
			paramName = matcher.group("name").trim();
			paramValue = matcher.group("value").trim();
		}
		else {

			// Parse parameter rule: name <[> value <]>
			matcher = Pattern.compile("^\\s*(?<name>[^\\[]+)(?<value>\\[[^\\]]+\\])\\s*$").matcher(parsedLine);

			if (matcher.matches()) {
				paramName = matcher.group("name").trim();
				paramValue = matcher.group("value").trim();
			}
			else {

				// Parse parameter rule: name <space> value
				matcher = Pattern.compile("^\\s*(?<name>[^\\s]+)\\s+(?<value>[^\\s]+)\\s*$").matcher(parsedLine);

				if (matcher.matches()) {
					paramName = matcher.group("name").trim();
					paramValue = matcher.group("value").trim();
				}
				else {
					
					// Parse parameter rule: <"> value <">
					matcher = Pattern.compile("^\\s*([^\\\"]+)(?<value>\\\"[^\\\"]+\\\")\\s*$").matcher(parsedLine);

					if (matcher.matches()) {
						paramValue = matcher.group("value").trim();
					}
				}
			}
		}

		if (paramName != null || paramValue != null) {
			
			SimulinkBlock parameterBlock = new SimulinkBlock();

			parameterBlock.setName(paramName);
			parameterBlock.setValue(paramValue);

			return parameterBlock;
		}
		*/

		return null;
	}

	/**
	 * Return the block position as a rectangle
	 * 
	 * @param block
	 *            the block we want to retrieve the position
	 * @return a Rectangle or null
	 */
	public static Rectangle getBlockPosition(SimulinkBlock block) {

		SimulinkBlock position = SimulinkUtils.getParameter(block, "Position");

		if (position != null) {

			String strPosition = position.getValue().replaceAll("\\\"|\\[|\\]", "");
			String[] coordinates = strPosition.split(",");

			int x1 = Integer.valueOf(coordinates[0].trim()).intValue();
			int y1 = Integer.valueOf(coordinates[1].trim()).intValue();
			int x2 = Integer.valueOf(coordinates[2].trim()).intValue();
			int y2 = Integer.valueOf(coordinates[3].trim()).intValue();

			return new Rectangle(x1, y1, x2 - x1, y2 - y1);
		}

		return null;
	}

	/**
	 * Set the position of a block
	 * 
	 * @param block
	 * @param rect
	 */
	public static void setBlockPosition(SimulinkBlock block, Rectangle rect) {

		String position = "";

		position += "[" + String.valueOf(rect.x) + ", ";
		position += String.valueOf(rect.y) + ", ";
		position += String.valueOf(rect.x + rect.width) + ", ";
		position += String.valueOf(rect.y + rect.height) + "]";

		SimulinkUtils.setParameterValue(block, "Position", position);
	}

	/**
	 * Retrieve a block index within a block children.
	 * 
	 * @param blockSid
	 *            the sid of the block whose index is looked for
	 * @param block
	 *            the block children were to look for sid
	 * @return the index of the block found or -1 if not found
	 */
	public static int findBlockIndex(String blockSid, SimulinkBlock block) {

		for (int i = 0; i < block.getChildren().size(); i++) {

			SimulinkBlock childBlock = block.getChildren().get(i);

			if (childBlock.getSid() != null && childBlock.getSid().equals(blockSid)) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * Retrieve a block by its node type
	 * 
	 * @param nodeType
	 *            the node type of the block to retrieve
	 * @param block
	 *            the block were to look for specific node type
	 * @return the first block whose node type is matching specified name or
	 *         null
	 */
	public static SimulinkBlock findBlockByNodeType(String nodeType, SimulinkBlock block) {

		for (SimulinkBlock childBlock : block.getChildren()) {

			if (childBlock.getNodeType().equals(nodeType)) {
				return childBlock;
			}

			// Lookup for nodeType in child children
			SimulinkBlock matchingBlock = SimulinkUtils.findBlockByNodeType(nodeType, childBlock);

			if (matchingBlock != null) {
				return matchingBlock;
			}
		}

		return null;
	}

	/**
	 * Retrieve a block by its block type
	 * 
	 * @param blockType
	 *            the block type of the block to retrieve
	 * @param block
	 *            the block were to look for specific block type
	 * @return the first block whose node type is matching specified name or
	 *         null
	 */
	public static SimulinkBlock findBlockByBlockType(String blockType, SimulinkBlock block) {

		for (SimulinkBlock childBlock : block.getChildren()) {

			if (childBlock.getBlockType() != null && childBlock.equals(blockType)) {
				return childBlock;
			}

			// Lookup for nodeType in child children
			SimulinkBlock matchingBlock = SimulinkUtils.findBlockByBlockType(blockType, childBlock);

			if (matchingBlock != null) {
				return matchingBlock;
			}
		}

		return null;
	}

	/**
	 * Return a block parameter value based on its name. If parameter is not
	 * found, return null.
	 * 
	 * @param block
	 * @param paramName
	 * @return String the parameter value or null
	 */
	public static String getParameterValue(SimulinkBlock block, String paramName) {

		for (SimulinkBlock parameter : block.getParameters()) {

			if (parameter.getName().equals(paramName)) {
				return parameter.getValue();
			}
		}

		return null;
	}

	/**
	 * Set a block parameter value. If the parameter exists, the new value is
	 * replaced. Otherwise a new parameer is added to the block
	 * 
	 * @param block
	 * @param paramName
	 * @param paramValue
	 */
	public static void setParameterValue(SimulinkBlock block, String paramName, String paramValue) {

		SimulinkBlock param = SimulinkUtils.getParameter(block, paramName);

		if (param == null) {
			param = new SimulinkBlock();
			param.setNodeType("Parameter");
			block.getChildren().add(param);
		}

		param.setName(paramName);
		param.setValue(paramValue);
	}

	/**
	 * Return a block parameter based on its name. If parameter is not found,
	 * return null.
	 * 
	 * @param block
	 * @param paramName
	 * @return SimulinkParameter the parameter or null
	 */
	public static SimulinkBlock getParameter(SimulinkBlock block, String paramName) {

		for (SimulinkBlock parameter : block.getParameters()) {

			if (parameter.getName().equals(paramName)) {
				return parameter;
			}
		}

		return null;
	}

	/**
	 * Return all branches defined within a single line block
	 * 
	 * @param lineBlock
	 *            the block line to analyze
	 * @return a list of branch block
	 */
	public static List<SimulinkBlock> getLineBranches(SimulinkBlock lineBlock) {

		List<SimulinkBlock> branches = new ArrayList<>();

		// Consider the line itself a kind of branch
		branches.add(lineBlock);

		// Scan all branches defined for each block
		for (SimulinkBlock child : lineBlock.getChildren()) {

			if (child.getNodeType().equals("Branch")) {
				branches.addAll(getLineBranches(child));
			}
		}

		return branches;
	}
	
	public static void main(String[] args) {
		
		//String parsedLine = "	MinMaxOverflowLogging		\"Use LocalSettings\"";
		//String parsedLine = "	\"UseLocal Settings\"    ";
		//String parsedLine = "		MinMaxOverflowLogging		UseLocalSettings";
		String parsedLine = "		MinMaxOverflowLogging		[1, 2, 3]";

		Matcher matcher = Pattern.compile("^\\s*(?<name>\\w+)?\\s*((?<quottedvalue>\\\"[^\\\"]+\\\")|(?<bracketvalue>\\[[^\\]]+\\])|(?<value>\\w+))\\s*$").matcher(parsedLine);
		
		if (matcher.matches()) {
			System.out.println("name: " + matcher.group("name"));
			
			String value = matcher.group("quottedvalue");
			if (value == null) value = matcher.group("bracketvalue");
			if (value == null) value = matcher.group("value");
			
			System.out.println("value: " + value);
		}
	}
}
