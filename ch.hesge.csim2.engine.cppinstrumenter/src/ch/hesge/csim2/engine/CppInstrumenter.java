/**
 * 
 */
package ch.hesge.csim2.engine;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.gnu.cpp.GPPLanguage;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.core.parser.FileContent;
import org.eclipse.cdt.core.parser.IParserLogService;
import org.eclipse.cdt.core.parser.IScannerInfo;
import org.eclipse.cdt.core.parser.IncludeFileContentProvider;

import ch.hesge.csim2.core.model.Context;
import ch.hesge.csim2.core.model.IEngine;
import ch.hesge.csim2.core.utils.Console;
import ch.hesge.csim2.core.utils.EngineException;
import ch.hesge.csim2.core.utils.FileUtils;
import ch.hesge.csim2.core.utils.StringUtils;

/**
 * This engine instruments all CPP source found
 * in a specific folder, by inserting a instrumentation
 * code at the beginning of each method.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 * 
 */
public class CppInstrumenter implements IEngine {

	// Private attributes
	private Context context;
	
	private Path sourceFolder;
	private Path targetFolder;

	private Map<String, String> visitedFiles;
	
	private IScannerInfo scannerInfo;
	private IParserLogService logService;
	private IncludeFileContentProvider fileProvider;

	// Internal class to store instrumented method fragment
	class InstrumentedFragment {
		int line;
		String method;
		String code;
	}

	/**
	 * Default constructor.
	 */
	public CppInstrumenter() {
		visitedFiles = new HashMap<>();
	}

	/**
	 * Get the engine name.
	 * 
	 * @see ch.hesge.csim2.core.shell.IEngine#getName()
	 */
	@Override
	public String getName() {
		return "CppInstrumenter";
	}

	/**
	 * Get the engine version.
	 * 
	 * @see ch.hesge.csim2.core.shell.IEngine#getVersion()
	 */
	@Override
	public String getVersion() {
		return "1.0.7";
	}

	/**
	 * Get the engine description.
	 * 
	 * @see ch.hesge.csim2.core.shell.IEngine#getDescription()
	 */
	@Override
	public String getDescription() {
		return "scan and instruments C++ source files.";
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
				inputFolder = Console.readLine("enter source folder: ");
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

			context.setProperty("root-path", sourceFolder.toString());
		}
		catch (Exception e) {
			Console.writeError("error while instrumenting files: " + StringUtils.toString(e));
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
			
			// Create parser services common to all files
			logService   = CppInstrumenterUtils.createParserLogService();
			fileProvider = CppInstrumenterUtils.createFileProvider();
			scannerInfo  = CppInstrumenterUtils.createScannerInfo(context);

			Console.writeLine("cloning folder " + sourceFolder.getFileName().toString().toLowerCase());

			// Clone source folder into target one
			FileUtils.removeFolder(targetFolder);
			FileUtils.copyFolder(sourceFolder, targetFolder);

			Console.writeLine("source scanning started.");

			// Scan all folder recursively to discover source file
			Files.walkFileTree(Paths.get(targetFolder.toString()), new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(Path filepath, BasicFileAttributes attrs) throws IOException {

					// Retrieves file extension
					String fileExtension = FileUtils.getFileExtension(filepath.toString());

					// Parse source file only and analyse it
					if (fileExtension.equalsIgnoreCase(".cpp") && canVisitFile(filepath.toString())) {

						try {		
							
							// Keep track of all dependency files
							Map<String, String> dependencyMap = new HashMap<>();

							// Keep track of all instrumented fragments
							Map<String, Map<ICPPASTFunctionDefinition, InstrumentedFragment>> fragmentsByFile = new HashMap<>();
							
							// Extract instrumented fragments from file and its dependencies
							doInstrumentFile(filepath.toString(), dependencyMap, fragmentsByFile);

							// And save all fragments found							
							doSaveFragments(fragmentsByFile);
							
							// Mark file dependencies as visited
							for (String dependentFile : dependencyMap.keySet()) {
								visitedFiles.put(dependentFile, dependentFile);
							}
							
							// Mark current file as visited
							visitedFiles.put(filepath.toString(), filepath.toString());
						}
						catch (Exception e) {
							Console.writeError("error while instrumenting files: " + StringUtils.toString(e));
						}
					}

					return FileVisitResult.CONTINUE;
				}
			});
		}
		catch(Exception e) {
			Console.writeError("error while instrumenting files: " + StringUtils.toString(e));
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
	 *            the filepath to check
	 * @return true if the file is not yet parsed, false otherwise
	 */
	private boolean canVisitFile(String filepath) {
	
		// Reject file outside root folder
		if (!filepath.startsWith(targetFolder.toString())) {
			return false;
		}
	
		// Reject TraceLogger.h and TraceLogger.cpp
		if (filepath.endsWith("TraceLogger.h") || filepath.endsWith("TraceLogger.cpp")) {
			return false;
		}
	
		// Reject files already parsed
		if (visitedFiles.containsKey(filepath)) {
			return false;
		}
	
		return true;
	}
	
	/**
	 * Analyze a single source file by scanning all its function definition.
	 * In one file, multiple function can be defined in multiple sub files (header or include). 
	 * 
	 * @param filepath
	 * @throws Exception
	 */
	private void doInstrumentFile(final String filepath, Map<String, String> dependencyMap, Map<String, Map<ICPPASTFunctionDefinition, InstrumentedFragment>> fragmentsByFile) throws Exception {
		
		String filename = Paths.get(filepath).getFileName().toString().toLowerCase();

		// Retrieve source content and its associated translation unit
		FileContent sourceFile = FileContent.createForExternalFileLocation(filepath);
		final IASTTranslationUnit translationUnit = GPPLanguage.getDefault().getASTTranslationUnit(sourceFile, scannerInfo, fileProvider, null, ILanguage.OPTION_IS_SOURCE_UNIT, logService);

		Console.writeLine("parsing file " + filename + ".");

		// Analyze all declared methods
		translationUnit.accept(new ASTVisitor() {

			{
				// Visitor configuration
				shouldVisitDeclarations = true;
			}

			// Parse all declarations and analyze method implementations
			public int visit(IASTDeclaration declaration) {

				// Retrieve file containing the declaration
				String declaringFilename = declaration.getContainingFilename();

				// Scan only files not yet visited
				if (canVisitFile(declaringFilename)) {

					// Mark containing file as parsed
					dependencyMap.put(declaringFilename, declaringFilename);
					
					// Detect function definition (that is a method implementation)
					if (declaration instanceof ICPPASTFunctionDefinition) {
						
						// Retrieve the method definition
						ICPPASTFunctionDefinition functionDefinition = (ICPPASTFunctionDefinition) declaration;
						
						// Create the default map entry storing all instrumented fragment for current file
						if (!fragmentsByFile.containsKey(declaringFilename)) {
							fragmentsByFile.put(declaringFilename, new HashMap<ICPPASTFunctionDefinition, InstrumentedFragment>());			
						}
						
						// Retrieve the instrument fragment map
						Map<ICPPASTFunctionDefinition, InstrumentedFragment> fragmentsByFunction = fragmentsByFile.get(declaringFilename);
						
						// Add new instrumented fragment for detected method
						if (!fragmentsByFunction.containsKey(functionDefinition)) {
							InstrumentedFragment fragment = doCreateInstrumentedFragment(functionDefinition);
							fragmentsByFunction.put(functionDefinition, fragment);
						}
					}
				}

				return PROCESS_CONTINUE;
			}
		});
	}

	/**
	 * Parse a function definition and 
	 * create its associated instrumentation fragment.
	 * 
	 * @param functionDefinition
	 * @param fragmentsByFile
	 */
	private InstrumentedFragment doCreateInstrumentedFragment(ICPPASTFunctionDefinition functionDefinition) {

		// Parse class, method signature and return type
		String currentClassName = CppInstrumenterUtils.getClassName(functionDefinition);
		String methodSignature  = CppInstrumenterUtils.getMethodSignature(functionDefinition);
		String returnTypeString = CppInstrumenterUtils.getReturnType(functionDefinition);

		// Create the instrumentation code fragment for current method
		String instrumentationCode = String.format("\r\nTRACE_LOGGER(\"%s\", \"%s\", \"%s\", \"\");", currentClassName, methodSignature, returnTypeString);
		int bodyStartLine = functionDefinition.getBody().getFileLocation().getStartingLineNumber() - 1;

		// Create the instrumentation object for current method
		InstrumentedFragment fragment = new InstrumentedFragment();
		fragment.method = methodSignature;
		fragment.line   = bodyStartLine;
		fragment.code   = instrumentationCode;
		
		return fragment;
	}

	/**
	 * Instrument all fragment found by inserting instrumentation fragment
	 * at beginning of each method founds.
	 * 
	 */
	private void doSaveFragments(Map<String, Map<ICPPASTFunctionDefinition, InstrumentedFragment>> fragmentsByFile) throws Exception {
		
		// Scan all files parsed while parsing single source file
		for (String filepath : fragmentsByFile.keySet()) {

			Path path = Paths.get(filepath);

			// Load original file content
			List<String> originalFileContent = FileUtils.readFileAsStringList(path);

			// Retrieve all methods to instrumented
			Map<ICPPASTFunctionDefinition, InstrumentedFragment> fragmentByFunction = fragmentsByFile.get(filepath); 
			
			// For each method modify directly the original content
			for (InstrumentedFragment fragment : fragmentByFunction.values()) {
				
				// Replace { with instrumentation fragment
				String originalLine = originalFileContent.get(fragment.line);
				String modifiedLine = originalLine.replaceFirst("\\{", "{" + fragment.code);

				// Modify original body content
				originalFileContent.set(fragment.line, modifiedLine);
				
				Console.writeLine("instrumenting file: " + path.getFileName().toString() + ", function: " + fragment.method + ".");
			}

			Console.writeLine("saving file: " + path.getFileName().toString() + ".");

			// Finally save modified file
			Files.write(path, originalFileContent, Charset.defaultCharset(), StandardOpenOption.TRUNCATE_EXISTING);
		}
	}
}
