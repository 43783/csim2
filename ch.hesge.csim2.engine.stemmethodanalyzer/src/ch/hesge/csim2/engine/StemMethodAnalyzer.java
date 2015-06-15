/**
 * 
 */
package ch.hesge.csim2.engine;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;

import ch.hesge.csim2.core.logic.ApplicationLogic;
import ch.hesge.csim2.core.model.Context;
import ch.hesge.csim2.core.model.IEngine;
import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.SourceClass;
import ch.hesge.csim2.core.model.SourceMethod;
import ch.hesge.csim2.core.model.SourceParameter;
import ch.hesge.csim2.core.model.SourceReference;
import ch.hesge.csim2.core.model.StemMethod;
import ch.hesge.csim2.core.model.StemMethodType;
import ch.hesge.csim2.core.utils.Console;
import ch.hesge.csim2.core.utils.EngineException;
import ch.hesge.csim2.core.utils.StringUtils;

/**
 * This engine analyze all source information and try to evaluate all concepts
 * used in each source-method available.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 *
 */
public class StemMethodAnalyzer implements IEngine {

	// Private attributes
	private Context context;

	private Project project;
	private List<String> rejectedList;

	/**
	 * Default constructor.
	 */
	public StemMethodAnalyzer() {
	}

	/**
	 * Get the engine name.
	 * 
	 * @see ch.hesge.csim2.core.shell.IEngine#getName()
	 */
	@Override
	public String getName() {
		return "StemMethodAnalyzer";
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
		return "analyze all methods and generate all stem methods.";
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
		params.put("rejected-words", "file");

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

			// Retrieve current project
			if (context.containsKey("project")) {
				project = (Project) context.getProperty("project");
			}
			else {
				throw new EngineException("missing project specified !");
			}

			// Retrieve path to rejected words file
			Path rejectedWordsPath = Paths.get("conf", "rejected-word-list.txt").toAbsolutePath();
			if (context.containsKey("rejected-words")) {
				String rejectedWordsFileParam = (String) context.getProperty("rejected-words");
				if (rejectedWordsFileParam != null && rejectedWordsFileParam.trim().length() > 0) {
					rejectedWordsPath = Paths.get(rejectedWordsFileParam);
				}
			}

			// Check if rejected word file exists
			if (!rejectedWordsPath.toFile().exists()) {
				throw new EngineException("file '" + rejectedWordsPath.getFileName().toString() + "' doesn't not exist !");
			}

			// Load rejected word list
			rejectedList = Files.readAllLines(rejectedWordsPath, Charset.defaultCharset());
		}
		catch (Exception e) {
			Console.writeError(this, "error while instrumenting files: " + StringUtils.toString(e));
		}
	}

	/**
	 * Start the engine.
	 * 
	 * @see ch.hesge.csim2.core.shell.IEngine#start()
	 * 
	 *      Scan all project sources and identify all concepts associated to
	 *      each methods.
	 */
	@Override
	public void start() {

		try {

			int stemMethodCount = 0;

			Console.writeInfo(this, "cleaning previous stem methods...");
			ApplicationLogic.deleteStemMethods(project);

			// Load all project classes
			Console.writeInfo(this, "loading code sources information...");
			List<SourceClass> sourceClasses = ApplicationLogic.getSourceClassesWithDependencies(project, true);

			Console.writeInfo(this, "scanning source classes...");

			// Build stem method table
			for (SourceClass sourceClass : sourceClasses) {

				for (SourceMethod sourceMethod : sourceClass.getMethods()) {

					// Retrieve stems for the method
					String methodName = sourceMethod.getName();
					List<String> methodStems = getStems(methodName, rejectedList);

					// Create a stem for the full method name
					String methodNameFull = StringUtils.concatenate(methodStems);
					StemMethod stemMethodNameFull = new StemMethod(project, null, sourceMethod, methodNameFull, StemMethodType.METHOD_NAME_FULL, null);
					ApplicationLogic.saveStemMethod(stemMethodNameFull);
					stemMethodCount++;

					// Create a stem for each part of the method name
					for (String methodNamePart : methodStems) {
						StemMethod stemMethodNamePart = new StemMethod(project, stemMethodNameFull, sourceMethod, methodNamePart, StemMethodType.METHOD_NAME_PART, null);
						ApplicationLogic.saveStemMethod(stemMethodNamePart);
						stemMethodCount++;
					}

					for (SourceParameter sourceParameter : sourceMethod.getParameters()) {

						// Retrieve stems for the parameter
						String parameterName = sourceParameter.getName();
						List<String> parameterStems = getStems(parameterName, rejectedList);

						// Create a stem for the full parameter name
						String parameterNameFull = StringUtils.concatenate(parameterStems);
						StemMethod stemParameterNameFull = new StemMethod(project, stemMethodNameFull, sourceMethod, parameterNameFull, StemMethodType.PARAMETER_NAME_FULL, null);
						ApplicationLogic.saveStemMethod(stemParameterNameFull);
						stemMethodCount++;

						// Create a stem for each part of the parameter name
						for (String parameterNamePart : parameterStems) {
							StemMethod stemParameterNamePart = new StemMethod(project, stemParameterNameFull, sourceMethod, parameterNamePart, StemMethodType.PARAMETER_NAME_PART, null);
							ApplicationLogic.saveStemMethod(stemParameterNamePart);
							stemMethodCount++;
						}

						// Retrieve stems for the type
						String parameterType = sourceParameter.getType();
						List<String> typeStems = getStems(parameterType, rejectedList);

						// Create a stem for the full type name
						String parameterTypeFull = StringUtils.concatenate(typeStems);
						StemMethod stemParameterTypeFull = new StemMethod(project, stemParameterNameFull, sourceMethod, parameterTypeFull, StemMethodType.PARAMETER_TYPE_FULL, null);
						ApplicationLogic.saveStemMethod(stemParameterTypeFull);
						stemMethodCount++;

						// Create a stem for each part of the type name
						for (String parameterTypePart : typeStems) {
							StemMethod stemParameterTypePart = new StemMethod(project, stemParameterTypeFull, sourceMethod, parameterTypePart, StemMethodType.PARAMETER_TYPE_PART, null);
							ApplicationLogic.saveStemMethod(stemParameterTypePart);
							stemMethodCount++;
						}
					}

					for (SourceReference sourceReference : sourceMethod.getReferences()) {

						// Retrieve stems for the reference
						String referenceName = sourceReference.getName();
						List<String> referenceStems = getStems(referenceName, rejectedList);

						// Create a stem for the full reference name
						String referenceNameFull = StringUtils.concatenate(referenceStems);
						StemMethod stemReferenceNameFull = new StemMethod(project, stemMethodNameFull, sourceMethod, referenceNameFull, StemMethodType.REFERENCE_NAME_FULL, sourceReference.getOrigin());
						ApplicationLogic.saveStemMethod(stemReferenceNameFull);
						stemMethodCount++;

						// Create a stem for each part of the reference name
						for (String referenceNamePart : referenceStems) {
							StemMethod stemReferenceNamePart = new StemMethod(project, stemReferenceNameFull, sourceMethod, referenceNamePart, StemMethodType.REFERENCE_NAME_PART, sourceReference.getOrigin());
							ApplicationLogic.saveStemMethod(stemReferenceNamePart);
							stemMethodCount++;
						}

						// Retrieve stems for the type
						String referenceType = sourceReference.getType();
						List<String> typeStems = getStems(referenceType, rejectedList);
						
						// Create a stem for the full type name
						String referenceTypeFull = StringUtils.concatenate(typeStems);
						StemMethod stemReferenceTypeFull = new StemMethod(project, stemReferenceNameFull, sourceMethod, referenceTypeFull, StemMethodType.REFERENCE_TYPE_FULL, sourceReference.getOrigin());
						ApplicationLogic.saveStemMethod(stemReferenceTypeFull);
						stemMethodCount++;

						// Create a stem for each part of the type name
						for (String referenceTypePart : typeStems) {
							StemMethod stemReferenceTypePart = new StemMethod(project, stemReferenceTypeFull, sourceMethod, referenceTypePart, StemMethodType.REFERENCE_TYPE_PART, sourceReference.getOrigin());
							ApplicationLogic.saveStemMethod(stemReferenceTypePart);
							stemMethodCount++;
						}
					}
				}
			}

			Console.writeInfo(this, stemMethodCount + " stems methods found");
		}
		catch (Exception e) {
			Console.writeError(this, "error while analyzing sources: " + StringUtils.toString(e));
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
	 * Retrieve all stems associated to a name.
	 * Words present in rejectedList will not produce associated stems.
	 * 
	 * @param name
	 *        the name to use to extract stems
	 * @param rejectedList
	 *        the list of forbidden words
	 * @return
	 *         a list of stems associated to the list of names
	 */
	public static List<String> getStems(String name, List<String> rejectedList) {
		
		List<String> stems = new ArrayList<>();

		// First, clean original name (diacritic and non alphanum chars) 
		String cleanName = Normalizer.normalize(name, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
		cleanName = cleanName.replaceAll("\\[.*\\]|\\{.*\\}|\\(.*\\)", "");
		cleanName = cleanName.replaceAll("[^A-Za-z0-9]", "");
		cleanName = cleanName != null ? cleanName.trim() : "";
		cleanName = StringUtils.trimHungarian(cleanName);
		
		if (cleanName.length() > 0) {
			
			List<String> nameParts = new ArrayList<>();

			// Then retrieve name parts (camel casing notation) 
			List<String> words = StringUtils.splitCamelCase(cleanName);

			// Filter name present in rejection list
			for (String word : words) {

				if (word != null && word.length() > 0) {

					word = word.toLowerCase();

					// Add only words not in reject list or not already present
					if (!rejectedList.contains(word) && !stems.contains(word)) {
						nameParts.add(word);
					}
				}
			}
			
			// Finally stemmize all name parts
			SnowballStemmer stemmer = new englishStemmer();
			for (String word : nameParts) {

				stemmer.setCurrent(word);
				stemmer.stem();
				stems.add(stemmer.getCurrent().toLowerCase());
			}
		}
		
		return stems;
	}		
}
