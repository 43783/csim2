package ch.hesge.csim2.simulinkparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

		lineNumber = 1;

		// Create the of all future blocks
		SimulinkFactory.createRootBlock(model);

		// Create a file reader
		BufferedReader reader = Files.newBufferedReader(filePath, Charset.defaultCharset());

		// Main parsing loop
		while (true) {

			// Read current line
			String parsedLine = reader.readLine();
			if (parsedLine == null)
				break;

			// Retrieve the current block
			String nodeType = SimulinkUtils.parseNodeType(parsedLine);
			SimulinkBlock block = parseBlock(null, nodeType, reader);

			// Add add block to model
			if (block != null) {
				model.getRoot().getChildren().add(block);
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
	private SimulinkBlock parseBlock(SimulinkBlock parent, String nodeType, BufferedReader reader) throws IOException {

		// Skip nodes without type
		if (nodeType == null)
			return null;

		SimulinkBlock block = new SimulinkBlock();

		block.setNodeType(nodeType);
		block.setParent(parent);
		block.setSourceLine(lineNumber);

		// Block parsing loop
		while (true) {

			// Read current line
			String parsedLine = reader.readLine();
			if (parsedLine == null)
				break;

			lineNumber++;

			// Detect child block declaration
			if (parsedLine.contains("{")) {

				// Parse child block (with all its parameters)
				String childType = SimulinkUtils.parseNodeType(parsedLine);
				parseBlock(block, childType, reader);
			}

			// End parsing a child block
			else if (parsedLine.contains("}")) {
				break;
			}

			// Otherwise, we have a parameter, so parse it
			else if (parsedLine.trim().length() > 0) {
				parseParameter(block, parsedLine);
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
	private void parseParameter(SimulinkBlock parent, String parsedLine) throws IOException {

		SimulinkBlock parameter = SimulinkUtils.parseParameter(parsedLine);

		if (parameter != null) {

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

			parent.getChildren().add(parameter);
		}
	}
}
