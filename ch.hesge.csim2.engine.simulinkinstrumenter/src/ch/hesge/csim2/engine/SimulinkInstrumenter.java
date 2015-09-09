/**
 * 
 */
package ch.hesge.csim2.engine;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import ch.hesge.csim2.core.model.Context;
import ch.hesge.csim2.core.model.IEngine;
import ch.hesge.csim2.core.simulink.SimulinkBlock;
import ch.hesge.csim2.core.simulink.SimulinkFactory;
import ch.hesge.csim2.core.simulink.SimulinkModel;
import ch.hesge.csim2.core.simulink.SimulinkParser;
import ch.hesge.csim2.core.simulink.SimulinkUtils;
import ch.hesge.csim2.core.utils.Console;
import ch.hesge.csim2.core.utils.EngineException;
import ch.hesge.csim2.core.utils.FileUtils;
import ch.hesge.csim2.core.utils.StringUtils;

/**
 * This engine allow simulink model instrumentation.
 * 
 * Copyright HEG Geneva 2015, Switzerland
 * 
 * @author Eric Harth
 *
 */
public class SimulinkInstrumenter implements IEngine {

	// Private attributes
	private Context context;

	private Path sourceFolder;
	private Path targetFolder;

	private Map<String, String> visitedFiles;

	/**
	 * Default constructor.
	 */
	public SimulinkInstrumenter() {
		visitedFiles = new HashMap<>();
	}

	/**
	 * Get the engine name.
	 * 
	 * @see ch.hesge.csim2.core.shell.IEngine#getName()
	 */
	@Override
	public String getName() {
		return "SimuLinkInstrumenter";
	}

	/**
	 * Get the engine version.
	 * 
	 * @see ch.hesge.csim2.core.shell.IEngine#getVersion()
	 */
	@Override
	public String getVersion() {
		return "1.0.5";
	}

	/**
	 * Get the engine description.
	 * 
	 * @see ch.hesge.csim2.core.shell.IEngine#getDescription()
	 */
	@Override
	public String getDescription() {
		return "scan and instruments simulink files.";
	}

	/**
	 * Return the parameter map required by the engine.
	 * 
	 * @see ch.hesge.csim2.core.shell.IEngine#getParameters()
	 */
	@Override
	public Properties getParameters() {

		Properties params = new Properties();

		params.put("project", "project");
		params.put("source-folder", "folder");
		params.put("target-folder", "path");

		return params;
	}

	/**
	 * Retrieve the engine context.
	 * 
	 * @see ch.hesge.csim2.core.shell.IEngine#getContext()
	 */
	@Override
	public Context getContext() {
		return this.context;
	}

	/**
	 * Sets the engine context before starting.
	 * 
	 * @see ch.hesge.csim2.core.shell.IEngine#setContext()
	 */
	@Override
	public void setContext(Context context) {
		this.context = context;
	}

	/**
	 * Initialize the engine before starting.
	 * 
	 * @see ch.hesge.csim2.core.shell.IEngine#init()
	 */
	@Override
	public void init() {

		try {

			String inputFolder = null;
			String outputFolder = null;

			// Retrieve current source folder
			if (context.containsKey("source-folder")) {
				inputFolder = (String) context.getProperty("source-folder");
			}
			else {
				throw new EngineException("missing source folder specified !");
			}

			// Retrieve current source folder
			if (context.containsKey("target-folder")) {
				String outputFolderParam = (String) context.getProperty("target-folder");

				if (outputFolderParam.trim().length() > 0) {
					outputFolder = outputFolderParam;
				}
				else {
					outputFolder = inputFolder + ".instrumented";
				}
			}
			else {
				outputFolder = inputFolder + ".instrumented";
			}

			// Convert input/output string into path
			sourceFolder = Paths.get(inputFolder).toAbsolutePath().normalize();
			targetFolder = Paths.get(outputFolder).toAbsolutePath().normalize();

			// Now, check if input folder exists
			if (!sourceFolder.toFile().exists()) {
				throw new EngineException("source folder '" + sourceFolder + "' not found.");
			}
		}
		catch (Exception e) {
			Console.writeError(this, "error while instrumenting files: " + StringUtils.toString(e));
		}
	}

	/**
	 * Start the engine.
	 * 
	 * @see ch.hesge.csim2.core.shell.IEngine#start()
	 */
	@Override
	public void start() {

		try {

			// Initialization
			visitedFiles.clear();

			Console.writeInfo(this, "cloning folder " + sourceFolder.getFileName().toString().toLowerCase());

			// Clone source folder into target one
			FileUtils.removeFolder(targetFolder);
			FileUtils.copyFolder(sourceFolder, targetFolder);

			Console.writeInfo(this, "source scanning started.");

			// Scan all folder recursively to discover source file
			Files.walkFileTree(Paths.get(targetFolder.toString()), new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(Path filepath, BasicFileAttributes attrs) throws IOException {

					// Retrieves file extension
					String fileExtension = FileUtils.getFileExtension(filepath.toString());

					// Parse source file only and analyse it
					if (fileExtension.equalsIgnoreCase(".mdl") && canVisitFile(filepath.toString())) {

						try {

							// Analyze file and produce instrumented model
							SimulinkModel model = doInstrumentFile(filepath.toString());

							// Dump modified model to output
							SimulinkInstrumenterUtils.dumpModel(model);

							// Save instrumented model on file system
							SimulinkInstrumenterUtils.save(model, filepath.toString());

							// Mark current file as visited
							visitedFiles.put(filepath.toString(), filepath.toString());
						}
						catch (Exception e) {
							Console.writeError(this, "error while instrumenting files: " + StringUtils.toString(e));
						}
					}

					return FileVisitResult.CONTINUE;
				}
			});

			Console.writeInfo(this, "instrumentation completed.");
		}
		catch (Exception e) {
			Console.writeError(this, "error while instrumenting files: " + StringUtils.toString(e));
		}
	}

	/**
	 * Stop the engine.
	 * 
	 * @see ch.hesge.csim2.core.shell.IEngine#stop()
	 */
	@Override
	public void stop() {
	}

	/**
	 * Check if a file should be visited.
	 * 
	 * @param filepath
	 *        the filepath to check
	 * @return true if the file is not yet parsed, false otherwise
	 */
	private boolean canVisitFile(String filepath) {

		// Reject file outside root folder
		if (!filepath.startsWith(targetFolder.toString())) {
			return false;
		}

		// Reject files already parsed
		if (visitedFiles.containsKey(filepath)) {
			return false;
		}

		return true;
	}

	/**
	 * Analyze a single simulink file and produce an MDL model.
	 * Instrument a single simulink file.
	 * 
	 * @param filepath
	 *        the file path to analyse
	 * @return
	 *         the replacing instrumented model
	 * @throws IOException
	 */
	private SimulinkModel doInstrumentFile(String filepath) throws IOException {

		final String filename = Paths.get(filepath).getFileName().toString().toLowerCase();

		Console.writeDebug(this, "parsing file " + filename + ".");

		// Parse mdl file and produce its model in memory 
		SimulinkModel model = new SimulinkParser(filepath).parse();

		// Instrument the model (first root children)
		doInstrumentModel(model, model.getRoot().getChildren().get(0));

		// Check if model contains a simulink function type block
		SimulinkBlock functionBlock = SimulinkUtils.findBlockByBlockType("S-Function", model.getRoot());

		// If no function type found, create a new one
		if (functionBlock == null) {
			SimulinkBlock parameterBlock = SimulinkUtils.findBlockByNodeType("BlockParameterDefaults", model.getRoot());
			functionBlock = SimulinkFactory.createFunctionTypeBlock();
			parameterBlock.getChildren().add(functionBlock);
		}

		// Check if model contains stateflow block
		SimulinkBlock stateflowBlock = SimulinkUtils.findBlockByNodeType("Stateflow", model.getRoot());

		// If no stateflow found, create a new one
		if (stateflowBlock == null) {
			stateflowBlock = SimulinkFactory.createStateflowBlock();
			model.getRoot().getChildren().add(stateflowBlock);
		}

		return model;
	}

	/**
	 * Do instrument a block by inserting trace blocks between input/output
	 * ports and inner blocks. Instrument recursively block children.
	 * 
	 * @param model
	 *        the model where the SID should be created
	 * @param block
	 *        the block to instrument
	 */
	private void doInstrumentModel(SimulinkModel model, SimulinkBlock block) {

		// Instrument all output ports
		doInstrumentOutputPort(model, block);

		// Instrument all input ports
		doInstrumentInputPort(model, block);

		// Propagate instrumentation to children
		for (SimulinkBlock child : block.getChildren()) {
			doInstrumentModel(model, child);
		}
	}

	/**
	 * Instrument input ports. Procedure to instrument a input block is as
	 * follow:
	 * 
	 * <pre>
	 * 1. For each input-port, create a new trace-block aimed at intercepting signals
	 * 2. Scan all links between input-port and internal-block and replace each one by a link between trace-block and internal-block
	 * 3. Create a new link between input-port and trace-block
	 * </pre>
	 * 
	 * @param model
	 *        the model where the SID should be created
	 * @param block
	 *        the block to instrument
	 */
	private void doInstrumentInputPort(SimulinkModel model, SimulinkBlock block) {

		for (SimulinkBlock inputPort : block.getInputPorts()) {

			// Skip system port without name
			if (inputPort.getName() == null)
				continue;

			// Create a new trace-block and insert it right after the input-port
			SimulinkBlock traceBlock = SimulinkFactory.createTraceBlock(model, inputPort);
			int insertionPoint = SimulinkUtils.findBlockIndex(inputPort.getSid(), inputPort.getParent());
			inputPort.getParent().getChildren().add(insertionPoint + 1, traceBlock);

			// Now, changed all lines with src-block as input-port
			for (SimulinkBlock child : new ArrayList<SimulinkBlock>(inputPort.getParent().getChildren())) {

				// Detect line blocks
				if (child.getNodeType().equals("Line")) {

					// Retrieve line defining input-port as the src-block
					String srcBlock = SimulinkUtils.getParameterValue(child, "SrcBlock");

					// Make line source pointing to trace-block
					if (srcBlock != null && srcBlock.equals(inputPort.getName())) {
						SimulinkUtils.setParameterValue(child, "SrcBlock", traceBlock.getName());
					}
				}
			}

			// Create a new line between input-port and trace-block
			SimulinkBlock newLineBlock = SimulinkFactory.createLine(inputPort.getName(), "1", traceBlock.getName(), "1");
			inputPort.getParent().getChildren().add(newLineBlock);
		}
	}

	/**
	 * Instrument output ports
	 * 
	 * @param model
	 *        the model where the SID should be created
	 * @param block
	 *        the block to instrument
	 */
	private void doInstrumentOutputPort(SimulinkModel model, SimulinkBlock block) {

		for (SimulinkBlock outputPort : block.getOutputPorts()) {

			// Skip system port without name
			if (outputPort.getName() == null)
				continue;

			// Create a new trace-block and insert it right after the output-port
			SimulinkBlock traceBlock = SimulinkFactory.createTraceBlock(model, outputPort);
			int insertionPoint = SimulinkUtils.findBlockIndex(outputPort.getSid(), outputPort.getParent());
			outputPort.getParent().getChildren().add(insertionPoint + 1, traceBlock);

			// Now, changed all lines with src-block as output-port
			for (SimulinkBlock child : new ArrayList<SimulinkBlock>(outputPort.getParent().getChildren())) {

				// Detect line blocks
				if (child.getNodeType().equals("Line")) {

					// Retrieve all destination branches contained within the
					// line
					List<SimulinkBlock> branches = SimulinkUtils.getLineBranches(child);

					// Scan all branches
					for (SimulinkBlock branch : branches) {

						// Retrieve line defining output-port as the dst-block
						String dstBlock = SimulinkUtils.getParameterValue(branch, "DstBlock");

						// Make line destination pointing to trace-block
						if (dstBlock != null && dstBlock.equals(outputPort.getName())) {
							SimulinkUtils.setParameterValue(branch, "DstBlock", traceBlock.getName());
						}
					}
				}
			}

			// Create a new line between trace-block and output-port
			SimulinkBlock newLineBlock = SimulinkFactory.createLine(traceBlock.getName(), "1", outputPort.getName(), "1");
			outputPort.getParent().getChildren().add(newLineBlock);
		}
	}
}
