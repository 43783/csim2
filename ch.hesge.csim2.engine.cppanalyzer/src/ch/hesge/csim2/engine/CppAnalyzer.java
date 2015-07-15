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
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.parser.FileContent;
import org.eclipse.cdt.core.parser.IParserLogService;
import org.eclipse.cdt.core.parser.IScannerInfo;
import org.eclipse.cdt.core.parser.IncludeFileContentProvider;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclaration;

import ch.hesge.csim2.core.logic.ApplicationLogic;
import ch.hesge.csim2.core.model.Context;
import ch.hesge.csim2.core.model.IEngine;
import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.SourceAttribute;
import ch.hesge.csim2.core.model.SourceClass;
import ch.hesge.csim2.core.model.SourceMethod;
import ch.hesge.csim2.core.model.SourceReference;
import ch.hesge.csim2.core.model.SourceVariable;
import ch.hesge.csim2.core.utils.Console;
import ch.hesge.csim2.core.utils.EngineException;
import ch.hesge.csim2.core.utils.FileUtils;
import ch.hesge.csim2.core.utils.StringUtils;

/**
 * This engine analyze all CPP sources and extract
 * meta information in database.
 * 
 * This object are identified during the analysis:
 * 
 * classes
 * attributes
 * methods
 * parameters
 * local variable declarations
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 * 
 */
public class CppAnalyzer implements IEngine {

	// Private attributes
	private Context context;
	private ApplicationLogic appLogic;

	private Project project;
	private Path sourceFolder;

	private Map<String, String> visitedFiles;
	private Map<String, SourceClass> parsedClasses;

	private IScannerInfo scannerInfo;
	private IParserLogService logService;
	private IncludeFileContentProvider fileProvider;

	/**
	 * Default constructor
	 */
	public CppAnalyzer() {
		appLogic = ApplicationLogic.UNIQUE_INSTANCE;
		visitedFiles = new HashMap<>();
		parsedClasses = new HashMap<>();
	}

	/**
	 * Get the engine name.
	 * 
	 * @see ch.hesge.csim2.core.shell.IEngine#getName()
	 */
	@Override
	public String getName() {
		return "CppAnalyzer";
	}

	/**
	 * Get the engine version.
	 * 
	 * @see ch.hesge.csim2.core.shell.IEngine#getVersion()
	 */
	@Override
	public String getVersion() {
		return "1.0.21";
	}

	/**
	 * Get the engine description.
	 * 
	 * @see ch.hesge.csim2.core.shell.IEngine#getDescription()
	 */
	@Override
	public String getDescription() {
		return "extract meta information from C++ source files.";
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
			
			context.setProperty("root-path", sourceFolder.toString());
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

			// Create parser services common to all files
			logService   = CppAnalyzerUtils.createParserLogService();
			fileProvider = CppAnalyzerUtils.createFileProvider();
			scannerInfo  = CppAnalyzerUtils.createScannerInfo(context);

			// Create & register the global class
			SourceClass globalClass = CppAnalyzerUtils.createGlobalClass();
			parsedClasses.put(globalClass.getName(), globalClass);

			Console.writeInfo(this, "source scanning started.");

			// Scan all folder recursively to discover source file
			Files.walkFileTree(Paths.get(sourceFolder.toString()), new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(Path filepath, BasicFileAttributes attrs) throws IOException {

					// Retrieves file extension
					String fileExtension = FileUtils.getFileExtension(filepath.toString());

					// Parse source file only and analyse it
					if (fileExtension.equalsIgnoreCase(".cpp") && canVisitFile(filepath.toString())) {

						try {

							// Keep track of all dependency files
							Map<String, String> dependencyMap = new HashMap<>();

							// Extract metadata information from files
							doScanFile(filepath.toString(), dependencyMap);

							// Mark file dependency as visited
							for (String dependentFile : dependencyMap.keySet()) {
								visitedFiles.put(dependentFile, dependentFile);
							}

							// Mark current file as visited
							visitedFiles.put(filepath.toString(), filepath.toString());
						}
						catch (Exception e) {
							Console.writeError(this, StringUtils.toString(e));
						}
					}

					return FileVisitResult.CONTINUE;
				}
			});

			// Trace end of operations
			Console.writeInfo(this, "saving " + parsedClasses.size() + " classes found...");

			// Updating project
			appLogic.deleteSources(project);
			project.getSourceClasses().clear();
			project.getSourceClasses().addAll(parsedClasses.values());
			appLogic.saveSourceClasses(project, project.getSourceClasses());
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
	 * Analyze a single source file by scanning all its definition.
	 * In one file, multiple definition can be defined in multiple sub files
	 * (header or include).
	 * 
	 * <pre>
	 * See cpp cdt grammar in:
	 * 		https://eclipse.googlesource.com/cdt/org.eclipse.cdt/+/v201106061419/lrparser/org.eclipse.cdt.core.lrparser/grammar/cpp/CPPGrammar.g 
	 * 		https://github.com/eclipse/cdt/blob/master/lrparser/org.eclipse.cdt.core.lrparser/grammar/cpp/CPPGrammar.g
	 * 		http://homepages.e3.net.nz/~djm/cppgrammar.html
	 * </pre>
	 * 
	 * @param filepath
	 *        the file to parse
	 * @throws Exception
	 */
	private void doScanFile(final String filepath, final Map<String, String> dependencyMap) throws Exception {

		String filename = Paths.get(filepath).getFileName().toString().toLowerCase();

		// Retrieve source content and its associated translation unit
		FileContent sourceFile = FileContent.createForExternalFileLocation(filepath);
		final IASTTranslationUnit translationUnit = CppAnalyzerUtils.createTranslationUnit(sourceFile, scannerInfo, logService, fileProvider);

		Console.writeInfo(this, "parsing file " + filename + ".");

		// Analyze all declared methods
		translationUnit.accept(new ASTVisitor() {

			{
				// Visitor configuration
				shouldVisitDeclarations = true;
				shouldVisitExpressions = true;
			}

			// Parse all declarations (class, attribute, method, variable)
			public int visit(IASTDeclaration declaration) {

				// Retrieve file containing the declaration
				String declaringFilename = declaration.getContainingFilename();

				// Scan only files not yet visited
				if (canVisitFile(declaringFilename)) {

					// Mark containing file as parsed
					dependencyMap.put(declaringFilename, declaringFilename);

					// Detect function definition (and its implementation)
					if (declaration instanceof ICPPASTFunctionDefinition) {
						ICPPASTFunctionDefinition functionDefinition = (ICPPASTFunctionDefinition) declaration;
						doParseFunctionDefinition(functionDefinition);
					}

					// Detect class or variable declaration
					else if (declaration instanceof CPPASTSimpleDeclaration) {

						CPPASTSimpleDeclaration simpleDeclaration = (CPPASTSimpleDeclaration) declaration;
						IASTDeclSpecifier specifier = simpleDeclaration.getDeclSpecifier(); // declaration type
						IASTDeclarator[] declarators = simpleDeclaration.getDeclarators(); // declaration names

						// Detect variable declaration
						if (specifier instanceof ICPPASTSimpleDeclSpecifier) {
							ICPPASTSimpleDeclSpecifier variableDeclaration = (ICPPASTSimpleDeclSpecifier) specifier;
							doParseVariableDeclaration(variableDeclaration, declarators);
						}

						// Detect class/struct/union declaration
						else if (specifier instanceof ICPPASTCompositeTypeSpecifier) {
							ICPPASTCompositeTypeSpecifier classTypeSpecifier = (ICPPASTCompositeTypeSpecifier) specifier;
							doParseClassDeclaration(classTypeSpecifier);
						}
					}
				}

				return PROCESS_CONTINUE;
			}

			// Parse all expressions defined within methods
			public int visit(IASTExpression expression) {

				// Retrieve file containing the declaration
				String declaratiingFile = expression.getContainingFilename();

				// Scan only files not yet visited
				if (canVisitFile(declaratiingFile)) {

					// Mark containing file as parsed
					dependencyMap.put(declaratiingFile, declaratiingFile);

					// Handle expression within function
					if (!(expression.getParent() instanceof ICPPASTFunctionCallExpression)) {
						doParseVariableReference(expression);
					}
				}

				return PROCESS_CONTINUE;
			}
		});
	}

	/**
	 * Parse a class declaration with its attribute members
	 * 
	 * @param declarationSpecifier
	 *        the specifier to use
	 */
	private void doParseClassDeclaration(ICPPASTCompositeTypeSpecifier declarationSpecifier) {

		// Parse the class declaration
		SourceClass sourceClass = CppAnalyzerUtils.createSourceClass(declarationSpecifier);

		// Add class if not already parsed
		if (!parsedClasses.containsKey(sourceClass.getName())) {

			parsedClasses.put(sourceClass.getName(), sourceClass);

			Console.writeInfo(this, "visiting " + sourceClass.getType() + " " + sourceClass.getName() + " in " + sourceClass.getFilename() + ".");

			for (SourceAttribute sourceAttribute : sourceClass.getAttributes()) {
				Console.writeInfo(this, "  attribute " + sourceAttribute.getType() + " " + sourceAttribute.getName() + ".");
			}
		}
	}

	/**
	 * Parse a function definition.
	 * 
	 * @param functionDefinition
	 *        the definition to use
	 */
	private void doParseFunctionDefinition(ICPPASTFunctionDefinition functionDefinition) {

		// Parse owning class name
		String classname = CppAnalyzerUtils.getClassName(functionDefinition);

		// Check if class is already parsed
		if (parsedClasses.containsKey(classname)) {

			// Retrieve the owning class
			SourceClass sourceClass = parsedClasses.get(classname);

			// Parse method declaration 
			SourceMethod sourceMethod = CppAnalyzerUtils.createMethod(functionDefinition);

			// Add method if not already parsed
			if (!sourceClass.getMethods().contains(sourceMethod)) {
				sourceClass.getMethods().add(sourceMethod);
				Console.writeInfo(this, "visiting method " + sourceClass.getName() + "::" + sourceMethod.getSignature() + " in " + sourceMethod.getFilename() + ".");
			}
		}
	}

	/**
	 * Parse a variable declaration (local or global)
	 * with its type (specifier) and its name (declarator)
	 * 
	 * @param specifier
	 *        the specifier to use
	 * @param declarators
	 *        the declarator array
	 */
	private void doParseVariableDeclaration(IASTDeclSpecifier specifier, IASTDeclarator[] declarators) {

		// Detect variables declared globally (not declared within a function)
		if (specifier.getParent() instanceof IASTTranslationUnit) {

			// Parse owning class name
			String classname = CppAnalyzerUtils.getGlobalClassName();

			// Check if class is already parsed
			if (parsedClasses.containsKey(classname)) {

				// Retrieve the owning class
				SourceClass sourceClass = parsedClasses.get(CppAnalyzerUtils.getGlobalClassName());

				// Scan all declared attribute
				for (IASTDeclarator declarator : declarators) {

					// Parse attribute declaration
					SourceAttribute sourceAttribute = CppAnalyzerUtils.createAttribute(specifier, declarator);

					// And add each one to its owning class
					if (!sourceClass.getAttributes().contains(sourceAttribute)) {
						sourceClass.getAttributes().add(sourceAttribute);
						Console.writeInfo(this, "  global variable " + sourceAttribute.getType() + " " + sourceAttribute.getName());
					}
				}
			}
		}

		// Detect variables declared within a function
		else if (specifier.getParent() instanceof ICPPASTFunctionDefinition) {

			ICPPASTFunctionDefinition functionDefinition = (ICPPASTFunctionDefinition) specifier.getParent();

			// Parse owning class name
			String classname = CppAnalyzerUtils.getClassName(functionDefinition);

			// Check if class is already parsed
			if (parsedClasses.containsKey(classname)) {

				// Retrieve the enclosing class
				SourceClass sourceClass = parsedClasses.get(classname);

				// Retrieve the owning method
				String methodSignature = CppAnalyzerUtils.getMethodSignature(functionDefinition);
				SourceMethod sourceMethod = appLogic.getSourceMethodBySignature(sourceClass, methodSignature);

				// Check if the owning method is already parsed
				if (sourceMethod != null) {

					// Scan all declared variable
					for (IASTDeclarator declarator : declarators) {

						// Parse variables declaration
						SourceVariable sourceVariable = CppAnalyzerUtils.createVariable(specifier, declarator);

						// And add each one to its owning method
						if (!sourceMethod.getVariables().contains(sourceVariable)) {
							sourceMethod.getVariables().add(sourceVariable);
							Console.writeInfo(this, "  local variable " + sourceVariable.getType() + " " + sourceVariable.getName());
						}
					}
				}
			}
		}
	}

	/**
	 * Parse an identifier contained in a function.
	 * 
	 * @param definition
	 */
	private void doParseVariableReference(IASTExpression expression) {

		// Skip method call expression and accept only identifier expression
		if (!(expression.getParent() instanceof ICPPASTFunctionCallExpression) && expression instanceof IASTIdExpression) {

			IASTIdExpression identifierExpression = (IASTIdExpression) expression;

			// Try to retrieve the function owning the expression
			ICPPASTFunctionDefinition functionDefinition = CppAnalyzerUtils.getFunctionDefinition(expression);

			// Handle function references only within function
			if (functionDefinition != null) {

				// Retrieve the enclosing class
				String classname = CppAnalyzerUtils.getClassName(functionDefinition);

				// Check if class is already parsed
				if (parsedClasses.containsKey(classname)) {

					// Retrieve the enclosing class
					SourceClass sourceClass = parsedClasses.get(classname);

					// Retrieve the owning method
					String methodSignature = CppAnalyzerUtils.getMethodSignature(functionDefinition);
					SourceMethod sourceMethod = appLogic.getSourceMethodBySignature(sourceClass, methodSignature);

					// Check if the owning method is already parsed
					if (sourceMethod != null) {

						// Parse reference declaration
						SourceReference sourceReference = CppAnalyzerUtils.createReference(sourceClass, sourceMethod, identifierExpression);

						// And add each one to its owning method
						if (!sourceMethod.getReferences().contains(sourceReference)) {
							sourceMethod.getReferences().add(sourceReference);
							Console.writeInfo(this, "  identifier " + sourceReference.getName() + ", type: " + sourceReference.getType() + ".");
						}
					}
				}
			}
		}
	}
}
