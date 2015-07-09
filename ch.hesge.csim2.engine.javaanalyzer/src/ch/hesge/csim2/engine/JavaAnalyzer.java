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
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.OperatorExpression;
import org.eclipse.jdt.internal.compiler.ast.Reference;
import org.eclipse.jface.text.Document;

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
 * This engine allow JAVA analysing and class parsing.
 * Reference: http://www.programcreek.com/2011/01/a-complete-standalone-example-of-astparser/
 * http://www.eclipse.org/articles/article.php?file=Article-JavaCodeManipulation_AST/index.html
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 *
 */
public class JavaAnalyzer implements IEngine {

	// Private attributes
	private Context context;

	private Project project;
	private Path sourceFolder;

	private Map<String, String> visitedFiles;
	private Map<String, SourceClass> parsedClasses;

	/**
	 * Default constructor.
	 */
	public JavaAnalyzer() {
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
		return "JavaAnalyzer";
	}

	/**
	 * Get the engine version.
	 * 
	 * @see ch.hesge.csim2.core.shell.IEngine#getVersion()
	 */
	@Override
	public String getVersion() {
		return "1.0.3";
	}

	/**
	 * Get the engine description.
	 * 
	 * @see ch.hesge.csim2.core.shell.IEngine#getDescription()
	 */
	@Override
	public String getDescription() {
		return "extract meta information from Java source files.";
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
					if (fileExtension.equalsIgnoreCase(".java") && canVisitFile(filepath.toString())) {

						try {

							// Extract metadata information from file
							doScanFile(filepath.toString());

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
			ApplicationLogic.deleteSources(project);
			project.getSourceClasses().clear();
			project.getSourceClasses().addAll(parsedClasses.values());
			ApplicationLogic.saveSourceClasses(project, project.getSourceClasses());
		}
		catch (IOException e) {
			Console.writeError(this, "error while analyzing files: " + StringUtils.toString(e));
		}
	}

	/**
	 * Stop the engine
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

		final String filename = Paths.get(filepath).getFileName().toString().toLowerCase();

		// Retrieve source content
		String fileContent = FileUtils.readFileAsString(Paths.get(filepath));
		Document sourceDocument = new Document(fileContent);

		Console.writeInfo(this, "parsing file " + filename + ".");

		// Create a parser
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setSource(sourceDocument.get().toCharArray());
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

		// Instrument all declared methods (standard and anonymous classes)
		compilationUnit.accept(new ASTVisitor() {

			// Analyze class declaration
			public boolean visit(TypeDeclaration declaration) {

				// Parse class name
				String classname = JavaAnalyzerUtils.getClassName(declaration);

				// Add class if not already parsed
				if (!parsedClasses.containsKey(classname)) {

					// Parse class declaration
					SourceClass sourceClass = JavaAnalyzerUtils.createSourceClass(declaration);
					sourceClass.setFilename(filename);

					parsedClasses.put(classname, sourceClass);

					Console.writeInfo(this, "visiting " + sourceClass.getType() + " " + sourceClass.getName() + " in " + sourceClass.getFilename() + ".");

					for (SourceAttribute sourceAttribute : sourceClass.getAttributes()) {
						Console.writeInfo(this, "  attribute " + sourceAttribute.getType() + " " + sourceAttribute.getName() + ".");
					}
				}

				return true;
			}

			// Analyze method declaration
			public boolean visit(MethodDeclaration declaration) {

				// Parse owning class name
				String classname = JavaAnalyzerUtils.getClassName(declaration);

				// Check if class is already parsed
				if (parsedClasses.containsKey(classname)) {

					// Retrieve the owning class
					SourceClass sourceClass = parsedClasses.get(classname);

					// Parse method declaration 
					SourceMethod sourceMethod = JavaAnalyzerUtils.createSourceMethod(declaration);

					// Add method if not already parsed
					if (!sourceClass.getMethods().contains(sourceMethod)) {
						sourceMethod.setFilename(filename);
						sourceClass.getMethods().add(sourceMethod);
						Console.writeInfo(this, "  method " + sourceMethod.getReturnType() + " " + sourceClass.getName() + "::" + sourceMethod.getSignature());
					}
				}

				return true;
			}

			// Analyze variable declaration in methods
			public boolean visit(VariableDeclarationStatement declaration) {

				// Parse enclosing class name
				String classname = JavaAnalyzerUtils.getClassName(declaration);

				// Check if class is already parsed
				if (parsedClasses.containsKey(classname)) {

					// Retrieve the enclosing class
					SourceClass sourceClass = parsedClasses.get(classname);

					// Retrieve the owning method
					String methodSignature = JavaAnalyzerUtils.getMethodSignature(declaration);
					SourceMethod sourceMethod = ApplicationLogic.getSourceMethodBySignature(sourceClass, methodSignature);

					// Check if method is already parsed
					if (sourceMethod != null) {

						// Parse variables declaration
						List<SourceVariable> sourceVariables = JavaAnalyzerUtils.createSourceVariables(declaration);

						// And add each one to its owning method
						for (SourceVariable sourceVariable : sourceVariables) {
							if (!sourceMethod.getVariables().contains(sourceVariable)) {
								sourceMethod.getVariables().add(sourceVariable);
								Console.writeInfo(this, "    var " + sourceVariable.getType() + " " + sourceVariable.getName());
							}
						}
					}
				}

				return true;
			}

			// Analyze variable declaration in expression
			public boolean visit(VariableDeclarationExpression declaration) {

				// Parse enclosing class name
				String classname = JavaAnalyzerUtils.getClassName(declaration);

				// Check if class is already parsed
				if (parsedClasses.containsKey(classname)) {

					// Retrieve the enclosing class
					SourceClass sourceClass = parsedClasses.get(classname);

					// Retrieve the owning method
					String methodSignature = JavaAnalyzerUtils.getMethodSignature(declaration);
					SourceMethod sourceMethod = ApplicationLogic.getSourceMethodBySignature(sourceClass, methodSignature);

					// Check if method is already parsed
					if (sourceMethod != null) {

						// Parse variables declaration
						List<SourceVariable> sourceVariables = JavaAnalyzerUtils.createSourceVariables(declaration);

						// And add each one to its owning method
						for (SourceVariable sourceVariable : sourceVariables) {
							if (!sourceMethod.getVariables().contains(sourceVariable)) {
								sourceMethod.getVariables().add(sourceVariable);
								Console.writeInfo(this, "    var " + sourceVariable.getType() + " " + sourceVariable.getName());
							}
						}
					}
				}

				return true;
			}

			public boolean visit(SimpleName name) {

				boolean referenceFound = false;
				
				if (name.getParent() instanceof Expression) {
					
					Expression expression = (Expression) name.getParent();
					
					if (expression instanceof Assignment) {
						Console.writeDebug(this, "Assignment: " + name);
						referenceFound = true;
					}
					else if (expression instanceof InfixExpression) {
						Console.writeDebug(this, "InfixExpression: " + name);
						referenceFound = true;
					}
					else if (expression instanceof PostfixExpression) {
						Console.writeDebug(this, "PostExpression: " + name);
						referenceFound = true;
					}
					else if (expression instanceof PrefixExpression) {
						Console.writeDebug(this, "PrefixExpression: " + name);
						referenceFound = true;
					}
					else if (expression instanceof ClassInstanceCreation) {
						Console.writeDebug(this, "PrefixExpression: " + name);
						referenceFound = true;
					}
					else if (expression instanceof FieldAccess) {
						Console.writeDebug(this, "FieldAccess: " + name);
						referenceFound = true;
					}
					else if (expression instanceof SuperFieldAccess) {
						Console.writeDebug(this, "SuperFieldAccess: " + name);
						referenceFound = true;
					}
					else if (expression instanceof ArrayAccess) {
						Console.writeDebug(this, "ArrayAccess: " + name);
						referenceFound = true;
					}
				}

				if (referenceFound) {
					
					// Parse enclosing class name
					String classname = JavaAnalyzerUtils.getClassName(name);

					// Check if class is already parsed
					if (parsedClasses.containsKey(classname)) {

						// Retrieve the enclosing class
						SourceClass sourceClass = parsedClasses.get(classname);

						// Retrieve the owning method
						String methodSignature = JavaAnalyzerUtils.getMethodSignature(name);
						SourceMethod sourceMethod = ApplicationLogic.getSourceMethodBySignature(sourceClass, methodSignature);

						// Check if method is already parsed
						if (sourceMethod != null) {

							// Parse reference defininition
							String referenceName = name.getIdentifier();
							SourceReference sourceReference = JavaAnalyzerUtils.createSourceReference(sourceClass, sourceMethod, referenceName);

							// And add each one to its owning method
							if (sourceReference != null && !sourceMethod.getReferences().contains(sourceReference)) {
								sourceMethod.getReferences().add(sourceReference);
								Console.writeInfo(this, "    ref " + sourceReference.getType() + " " + sourceReference.getName() + ", origin: " + sourceReference.getOrigin());
							}
						}
					}
				}

				return true;
			}
		});
	}
}
