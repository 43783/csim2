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

		// Load simulink file
		List<String> lines = Files.readAllLines(filePath);

		// Create the root of all blocks
		SimulinkFactory.createRootBlock(model);
				
		// Main parsing loop
		while (lineNumber < lines.size()) {

			// Try to retrieve a block in current line
			SimulinkBlock block = parseBlock(null, lines);

			// If a block is found, add it to the model
			if (block != null) {
				model.getRoot().getChildren().add(block);
			}
			
			lineNumber++;
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
	private SimulinkBlock parseBlock(SimulinkBlock parent, List<String> lines) throws IOException {

		// Retrieve the current block type
		String line = lines.get(lineNumber).trim();
		String nodeType = SimulinkUtils.parseNodeType(line);
		
		if (nodeType == null)
			return null;

		SimulinkBlock block = new SimulinkBlock();
		block.setParent(parent);
		block.setSourceLine(lineNumber);
		block.setNodeType(nodeType);
		
		lineNumber++;
		
		// Block parsing loop
		while (lineNumber < lines.size()) {

			// Read current line
			String parsedLine = lines.get(lineNumber).trim();

			// Detect child block declaration
			if (parsedLine.contains("{")) {

				// Parse child block (with all its parameters)
				SimulinkBlock child = parseBlock(block, lines);

				// Add child to its parent
				if (parent != null) {
					parent.getChildren().add(child);
				}
			}

			// End parsing a child block
			else if (parsedLine.contains("}")) {
				break;
			}

			// Otherwise, we should have a parameter
			else {
				
				// Parse block parameter
				SimulinkBlock parameter = parseParameter(block, lines);
				
				if (parameter != null) {
					
					// Add parameter to current block
					block.getParameters().add(parameter);
					
					if (parameter.getName().equalsIgnoreCase("sid")) {
						block.setSid(parameter.getValue());
					}

					else if (parameter.getName().equalsIgnoreCase("name")) {
						block.setName(parameter.getValue());
					}

					else if (parameter.getName().equalsIgnoreCase("blocktype")) {
						block.setBlockType(parameter.getValue());
					}	
					
					
				}
			}
			
			lineNumber++;
		}

		// Update map of blocks by SID
		if (block.getSid() != null) {
			
			int blockSid = Integer.parseInt(block.getSid());

			if (blockSid > model.getMaxSid()) {
				model.setMaxSid(blockSid);
			}
			
			model.getBlocksBySid().put(block.getSid(), block);
		}

		// Create input/output port block
		String blockType = block.getBlockType();

		if (blockType != null) {

			if (blockType.equalsIgnoreCase("inport")) {
				SimulinkFactory.createInputPort(block);
			}

			if (blockType.equalsIgnoreCase("outport")) {
				SimulinkFactory.createOutputPort(block);
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
	private SimulinkBlock parseParameter(SimulinkBlock parent, List<String> lines) throws IOException {

		// Read current line
		int paramLineNumber = lineNumber;
		String parsedLine = lines.get(paramLineNumber).trim();
		
		// Extract parameter name + value
		SimulinkBlock parameter = SimulinkUtils.parseParameter(parsedLine);
						
		if (parameter != null) {

			parameter.setParent(parent);
			parameter.setSourceLine(paramLineNumber);
			parameter.setNodeType("Parameter");

			paramLineNumber++;
			parsedLine = lines.get(paramLineNumber).trim();
			
			// Merge multi lines parameter
			while (paramLineNumber < lines.size() && parsedLine.startsWith("\"")) {
				
				// Try to retrieve another parameter value
				SimulinkBlock param = SimulinkUtils.parseParameter(parsedLine);
				
				if (param == null) {
					break;
				}

				String newParameterValue = (parameter.getValue() + param.getValue()).replace("\"\"", " "); 
				parameter.setValue(newParameterValue);

				// Read next line
				parsedLine = lines.get(++paramLineNumber).trim();
			}
			
			lineNumber = paramLineNumber - 1;
		}
		
		return parameter;
	}
}
