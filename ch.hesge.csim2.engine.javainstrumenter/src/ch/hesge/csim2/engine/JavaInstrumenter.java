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

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

import ch.hesge.csim2.core.model.Context;
import ch.hesge.csim2.core.model.IEngine;
import ch.hesge.csim2.core.utils.Console;
import ch.hesge.csim2.core.utils.EngineException;
import ch.hesge.csim2.core.utils.FileUtils;
import ch.hesge.csim2.core.utils.StringUtils;

/**
 * This engine allow JAVA sources instrumentation.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 *
 */
public class JavaInstrumenter implements IEngine {

	// Private attributes
	private Context context;

	private Path sourceFolder;
	private Path targetFolder;

	private Map<String, String> visitedFiles;

	/**
	 * Default constructor
	 */
	public JavaInstrumenter() {
		visitedFiles = new HashMap<>();
	}

	/**
	 * Get the engine name.
	 * 
	 * @see ch.hesge.csim2.core.shell.IEngine#getName()
	 */
	@Override
	public String getName() {
		return "JavaInstrumenter";
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
		return "scan and instruments Java source files.";
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

					// Parse source file only and analyze it
					if (fileExtension.equalsIgnoreCase(".java") && canVisitFile(filepath.toString())) {

						try {

							// Extract instrumented fragment from files
							doInstrumentFile(filepath.toString());

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
	 * Instrument a single source file by surrounding body method with
	 * try/finally statement.
	 * 
	 * @param filepath
	 *        the file to parse
	 * @throws Exception
	 */
	private void doInstrumentFile(String filepath) throws Exception {
		
		final String filename = Paths.get(filepath).getFileName().toString().toLowerCase();

		// Retrieve source content
		String originalContent = FileUtils.readFileAsString(Paths.get(filepath));
		Document originalSources = new Document(originalContent);

		Console.writeDebug(this, "parsing file " + filename + ".");

		// Create a parser
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setSource(originalSources.get().toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		Map<?, ?> options = JavaCore.getOptions();
		JavaCore.setComplianceOptions(JavaCore.VERSION_1_7, options);
		parser.setCompilerOptions(options);
		parser.setResolveBindings(true);

		// Parse the source file
		final CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);

		// Display parsing problems
		for (IProblem parsingProblem : compilationUnit.getProblems()) {
			Console.writeError(this, parsingProblem.getMessage());
		}

		// Start recording all AST modifications
		compilationUnit.recordModifications();

		// Instrument all declared methods (standard and anonymous classes)
		compilationUnit.accept(new ASTVisitor() {

			// Instrument all methods
			public boolean visit(MethodDeclaration methodDeclaration) {

				String className = JavaInstrumenterUtils.getClassName(methodDeclaration);

				// Skip method interface or TraceLogger methods
				if (!className.equals("TraceLogger") && !JavaInstrumenterUtils.isInterfaceMethod(methodDeclaration)) {
					doInstrumentMethod(compilationUnit, methodDeclaration);
				}

				return true;
			}
		});

		// Retrieve all source modifications made within the compilation unit
		TextEdit sourceModifications = compilationUnit.rewrite(originalSources, null);

		// Create a new copy of the original sources
		Document modifiedSources = new Document(originalSources.get());

		// And apply all modifications to separate copy of the original sources code
		sourceModifications.apply(modifiedSources);
		String modifiedSource = modifiedSources.get();

		// Save modified (instrumented) version of the original source file
		Files.delete(Paths.get(filepath));
		FileUtils.writeFile(Paths.get(filepath), modifiedSource);
	}

	/**
	 * Parse a function definition and
	 * create its associated instrumentation fragment directly within the
	 * compilation unit.
	 * 
	 * @param compilationUnit
	 * @param methodDeclaration
	 */
	@SuppressWarnings("unchecked")
	private void doInstrumentMethod(CompilationUnit compilationUnit, MethodDeclaration methodDeclaration) {

		AST ast = compilationUnit.getAST();
		Block originalBody = methodDeclaration.getBody();

		// Skip abstract method without body
		if (originalBody != null) {

			ASTNode constructorInvocation = null;

			// Retrieve super invocation for future use
			if (originalBody.statements().size() > 0) {

				// Retrieve first statement type
				int nodeType = ((ASTNode) originalBody.statements().get(0)).getNodeType();

				// Detect constructor invocation
				if (nodeType == ASTNode.SUPER_CONSTRUCTOR_INVOCATION || nodeType == ASTNode.CONSTRUCTOR_INVOCATION) {
					constructorInvocation = (ASTNode) originalBody.statements().remove(0);
				}
			}

			// Create a try/finally block
			TryStatement tryStatement = ast.newTryStatement();
			tryStatement.setBody(ast.newBlock());
			tryStatement.setFinally(ast.newBlock());

			// Add a Trace enter invocation within the try-statement
			MethodInvocation traceEnterCode = JavaInstrumenterUtils.createTraceInvocation(compilationUnit, "entering", methodDeclaration);
			ExpressionStatement expressionStatement = ast.newExpressionStatement(traceEnterCode);
			tryStatement.getBody().statements().add(expressionStatement);

			// Move all original statements within the try-statement (but after the trace invocation)
			while (originalBody.statements().size() > 0) {
				ASTNode statement = (ASTNode) originalBody.statements().remove(0);
				tryStatement.getBody().statements().add(statement);
			}

			// Add a Trace exit invocation within the try-statement
			MethodInvocation traceExitCode = JavaInstrumenterUtils.createTraceInvocation(compilationUnit, "exiting", methodDeclaration);
			tryStatement.getFinally().statements().add(ast.newExpressionStatement(traceExitCode));

			// Restore the constructor invocation as the first body statement
			if (constructorInvocation != null) {
				originalBody.statements().add(constructorInvocation);
			}

			// Apply the new try-statement as the second statement
			originalBody.statements().add(tryStatement);
			
			Console.writeDebug(this, "  method: " + methodDeclaration.getName().toString() + " instrumented.");
		}
	}
}
