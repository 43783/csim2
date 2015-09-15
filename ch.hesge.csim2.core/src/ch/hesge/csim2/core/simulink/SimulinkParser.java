package ch.hesge.csim2.core.simulink;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class SimulinkParser {

	// Private attributes
	private Path			filePath;
	private int				lineNumber;
	private SimulinkModel	model;

	/**
	 * Default constructor
	 */
	public SimulinkParser(String filepath) {

		// Create a simulink model
		model = new SimulinkModel();
		filePath = Paths.get(filepath).toAbsolutePath().normalize();
	}

	/**
	 * Parse current simulink file and return a associated model.
	 * 
	 * @return the simulink model produce while parsing
	 * @throws IOException
	 */
	public SimulinkModel parse() throws IOException {

		lineNumber = 0;

		// Create the root of all future blocks
		SimulinkFactory.createRootBlock(model);
		
		// Load file
		List<String> lines = Files.readAllLines(filePath);
		
		// Main parsing loop
		while (lineNumber < lines.size()) {

			// Read current line
			String parsedLine = lines.get(lineNumber).trim();

			// Retrieve the current block
			String nodeType = SimulinkUtils.parseNodeType(parsedLine);
			
			if (nodeType != null) {
				
				SimulinkBlock block = parseBlock(null, nodeType, lines);

				// Add the block to the model
				if (block != null) {
					model.getRoot().getChildren().add(block);
				}
			}
		}

		return model;
	}

	/**
	 * Parse a single block
	 * 
	 * @param parent
	 *            the owning block (or null for root)
	 * @param type
	 *            the simulink type of the block to parse
	 * @param reader
	 *            the reader used for parsing
	 * @return a simulink block
	 * @throws IOException
	 */
	private SimulinkBlock parseBlock(SimulinkBlock parent, String nodeType, List<String> lines) throws IOException {

		SimulinkBlock block = new SimulinkBlock();

		block.setNodeType(nodeType);
		block.setParent(parent);
		block.setSourceLine(lineNumber);

		// Block parsing loop
		while (true) {

			lineNumber++;

			if (lineNumber > lines.size() - 1)
				break;

			// Read current line
			String parsedLine = lines.get(lineNumber).trim();

			// Detect child block declaration
			if (parsedLine.contains("{")) {

				// Parse child block (with all its parameters)
				String childType = SimulinkUtils.parseNodeType(parsedLine);
				
				if (childType != null) {
					parseBlock(block, childType, lines);
				}
			}

			// End parsing a child block
			else if (parsedLine.contains("}")) {
				break;
			}

			// Otherwise, we have a parameter, so parse it
			else {
				
				SimulinkBlock parameter = parseParameter(block, parsedLine, lines);
				
				if (parameter != null) {
					block.getParameters().add(parameter);
				}
			}
		}

		// Add block to its parent
		if (parent != null) {
			parent.getChildren().add(block);
		}

		// Update map of blocks by SID
		if (block.getSid() != null) {
			model.getBlocksBySid().put(block.getSid(), block);
		}

		// Create input/output port block
		String blockType = block.getBlockType();

		if (blockType != null) {

			if (blockType.equals("Inport")) {
				SimulinkFactory.createInputPort(block);
			}

			if (blockType.equals("Outport")) {
				SimulinkFactory.createOutputPort(block);
			}
		}

		// Update max available sid
		if (block.getSid() != null) {

			int blockSid = Integer.parseInt(block.getSid());

			if (blockSid > model.getMaxSid()) {
				model.setMaxSid(blockSid);
			}
		}

		return block;
	}

	/**
	 * Parse a block parameter
	 * 
	 * @param parent
	 *            the block owning the parameter (or null for root)
	 * @param parsedLine
	 *            the string containing the parameter and its value
	 * @throws IOException
	 */
	private SimulinkBlock parseParameter(SimulinkBlock parent, String parsedLine, List<String> lines) throws IOException {

		SimulinkBlock parameter = SimulinkUtils.parseParameter(parsedLine);
						
		if (parameter != null) {

			lineNumber++;
			parsedLine = lines.get(lineNumber).trim();

			// Merge multi lines parameter
			while (lineNumber < lines.size() && parsedLine.startsWith("\"")) {
				
				SimulinkBlock param = SimulinkUtils.parseParameter(parsedLine);
				String concatValue = (parameter.getValue() + param.getValue()).replace("\"\"", " "); 
				parameter.setValue(concatValue);

				lineNumber++;
				parsedLine = lines.get(lineNumber).trim();
			}

			parameter.setParent(parent);
			parameter.setSourceLine(lineNumber);
			parameter.setNodeType("Parameter");

			if (parameter.getName().equals("SID")) {
				parent.setSid(parameter.getValue());
			}

			else if (parameter.getName().equals("Name")) {
				parent.setName(parameter.getValue());
			}

			else if (parameter.getName().equals("BlockType")) {
				parent.setBlockType(parameter.getValue());
			}
		}
		
		return parameter;
	}
}
