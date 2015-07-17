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

import ch.hesge.csim2.core.logic.ApplicationLogic;
import ch.hesge.csim2.core.model.Context;
import ch.hesge.csim2.core.model.IEngine;
import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.SourceAttribute;
import ch.hesge.csim2.core.model.SourceClass;
import ch.hesge.csim2.core.utils.Console;
import ch.hesge.csim2.core.utils.EngineException;
import ch.hesge.csim2.core.utils.FileUtils;
import ch.hesge.csim2.core.utils.StringUtils;
import ch.hesge.csim2.simulinkparser.SimulinkBlock;
import ch.hesge.csim2.simulinkparser.SimulinkModel;
import ch.hesge.csim2.simulinkparser.SimulinkParser;

/**
 * This engine allow simulink analysis.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 *
 */
public class SimulinkAnalyzer implements IEngine {

	// Private attributes
	private Context context;
	private ApplicationLogic applicationLogic;

	private Project project;
	private Path sourceFolder;

	private Map<String, String> visitedFiles;
	private List<SourceClass> parsedClasses;

	/**
	 * Default constructor.
	 */
	public SimulinkAnalyzer() {
		applicationLogic = ApplicationLogic.UNIQUE_INSTANCE;
		visitedFiles = new HashMap<>();
		parsedClasses = new ArrayList<>();
	}

	/**
	 * Get the engine name.
	 * 
	 * @see ch.hesge.csim2.core.shell.IEngine#getName()
	 */
	@Override
	public String getName() {
		return "SimulinkAnalyzer";
	}

	/**
	 * Get the engine version.
	 * 
	 * @see ch.hesge.csim2.core.shell.IEngine#getVersion()
	 */
	@Override
	public String getVersion() {
		return "1.0.2";
	}

	/**
	 * Get the engine description.
	 * 
	 * @see ch.hesge.csim2.core.shell.IEngine#getDescription()
	 */
	@Override
	public String getDescription() {
		return "extract meta information from simulink files.";
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
		params.put("source-folder", "file");

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

			// Retrieve current project
			if (context.containsKey("project")) {
				project = (Project) context.getProperty("project");
			}
			else {
				throw new EngineException("missing project specified !");
			}

			// Retrieve source folder
			if (context.containsKey("source-folder")) {
				inputFolder = (String) context.getProperty("source-folder");
			}
			else {
				throw new EngineException("missing source folder specified !");
			}

			// Convert input string into path
			sourceFolder = Paths.get(inputFolder).toAbsolutePath().normalize();

			// Now, check if input folder exists
			if (!sourceFolder.toFile().exists()) {
				throw new EngineException("folder '" + sourceFolder + "' doesn't not exist !");
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
			parsedClasses.clear();

			Console.writeInfo(this, "source scanning started.");

			// Scan all folder recursively to discover source file
			Files.walkFileTree(Paths.get(sourceFolder.toString()), new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(Path filepath, BasicFileAttributes attrs) throws IOException {

					// Retrieves file extension
					String fileExtension = FileUtils.getFileExtension(filepath.toString());

					// Parse source file only and analyse it
					if (fileExtension.equalsIgnoreCase(".mdl") && canVisitFile(filepath.toString())) {

						try {

							// Extract metadata information from file
							doScanFile(filepath.toString());

							// Mark current file as visited
							visitedFiles.put(filepath.toString(), filepath.toString());
						}
						catch (Exception e) {
							Console.writeError(this, "error while analyzing files: " + StringUtils.toString(e));
						}
					}

					return FileVisitResult.CONTINUE;
				}
			});

			// Trace end of operations
			Console.writeInfo(this, "saving " + parsedClasses.size() + " classes found...");

			// Updating project
			applicationLogic.deleteSources(project);
			project.getSourceClasses().clear();
			project.getSourceClasses().addAll(parsedClasses);
			applicationLogic.saveSourceClasses(project, project.getSourceClasses());
		}
		catch (Exception e) {
			Console.writeError(this, "error while analyzing files: " + StringUtils.toString(e));
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
		if (!filepath.startsWith(sourceFolder.toString())) {
			return false;
		}

		// Reject files already parsed
		if (visitedFiles.containsKey(filepath)) {
			return false;
		}

		return true;
	}

	/**
	 * Analyzing a single source file
	 * 
	 * @param filepath
	 *        the file to parse
	 * @throws Exception
	 */
	private void doScanFile(final String filepath) throws Exception {

		String filename = Paths.get(filepath).getFileName().toString().toLowerCase();

		Console.writeInfo(this, "parsing file " + filename + ".");

		SimulinkModel model = new SimulinkParser(filepath.toString()).parse();

		// Scan all blocks in model and extract source classes
		for (SimulinkBlock block : model.getRoot().getChildren()) {

			// Retrieve class from block
			SourceClass sourceClass = doAnalyzeBlock(block, filepath);

			// Add it to the parsed class list
			parsedClasses.add(sourceClass);
		}
	}

	/**
	 * Analyze a simulink block and extract metadata information.
	 * 
	 * @param parent
	 *        the simulink parent
	 * @param block
	 *        the simulink block to analyze
	 * @param filename
	 *        the file name where the block is defined
	 */
	private SourceClass doAnalyzeBlock(SimulinkBlock block, String filename) {

		// Parse the block as a class
		SourceClass sourceClass = createSourceClass(block, filename);

		// Retrieve block name
		Console.writeInfo(this, "visiting " + sourceClass.getName() + " in " + filename + ".");

		// Parse children
		for (SimulinkBlock child : block.getChildren()) {

			// Parameter block
			if (child.isParameter()) {

				// Create an attribute for each parameter
				SourceAttribute sourceAttribute = new SourceAttribute();
				sourceAttribute.setName(child.getName());
				sourceAttribute.setType("String");

				// Add only if not already present
				if (!sourceClass.getAttributes().contains(sourceAttribute)) {
					sourceClass.getAttributes().add(sourceAttribute);
				}
			}

			// Standard block
			else {

				// Create a child source class
				SourceClass childClass = doAnalyzeBlock(child, filename);

				// Add child to parent
				sourceClass.getSubClasses().add(childClass);
			}
		}

		return sourceClass;
	}

	/**
	 * Create a source class with all its attributes as defined by the block
	 * passed in parameter.
	 * 
	 * @param block
	 * @param filename
	 * @return a source class
	 */
	private SourceClass createSourceClass(SimulinkBlock block, String filename) {

		String classname = block.getValue();

		if (classname == null) {
			classname = block.getBlockType();
		}

		if (classname == null) {
			classname = block.getNodeType();
		}

		// Create the source class
		SourceClass sourceClass = new SourceClass();

		sourceClass.setName(classname);
		sourceClass.setType("class");
		sourceClass.setSuperClassName("Block");
		sourceClass.setFilename(filename);

		return sourceClass;
	}
}
