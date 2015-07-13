/**
 * 
 */
package ch.hesge.csim2.engine;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
	private List<String> rejectedMethodList;
	private List<String> rejectedTypeList;

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
		return "1.0.3";
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
		params.put("rejected-methods", "file");
		params.put("rejected-types", "file");

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

			// Retrieve path to rejected methods file
			Path rejectedMethodPath = Paths.get("conf", "rejected-methods.txt").toAbsolutePath();
			if (context.containsKey("rejected-methods")) {
				String rejectedFileParam = (String) context.getProperty("rejected-methods");
				if (rejectedFileParam != null && rejectedFileParam.trim().length() > 0) {
					rejectedMethodPath = Paths.get(rejectedFileParam);
				}
			}

			// Retrieve path to rejected types file
			Path rejectedTypePath = Paths.get("conf", "rejected-types.txt").toAbsolutePath();
			if (context.containsKey("rejected-types")) {
				String rejectedFileParam = (String) context.getProperty("rejected-types");
				if (rejectedFileParam != null && rejectedFileParam.trim().length() > 0) {
					rejectedTypePath = Paths.get(rejectedFileParam);
				}
			}

			// Check if rejected methods file exists
			if (!rejectedMethodPath.toFile().exists()) {
				throw new EngineException("file '" + rejectedMethodPath.getFileName().toString() + "' doesn't not exist !");
			}
			else {
				// Load rejected method list
				rejectedMethodList = Files.readAllLines(rejectedMethodPath, Charset.defaultCharset());
			}

			// Check if rejected types file exists
			if (!rejectedTypePath.toFile().exists()) {
				throw new EngineException("file '" + rejectedTypePath.getFileName().toString() + "' doesn't not exist !");
			}
			else {
				// Load rejected types list
				rejectedTypeList = Files.readAllLines(rejectedTypePath, Charset.defaultCharset());
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
	 * 
	 *      Scan all project sources and identify all concepts associated to
	 *      each methods.
	 */
	@Override
	public void start() {

		try {

			int stemMethodCount = 0;
			List<StemMethod> stems = new ArrayList<>();

			Console.writeInfo(this, "cleaning previous stem methods...");
			ApplicationLogic.deleteStemMethods(project);

			// Load all project classes
			Console.writeInfo(this, "loading code sources information...");
			List<SourceClass> sourceClasses = ApplicationLogic.getSourceClassMethodParam(project);

			Console.writeInfo(this, "scanning source classes...");

			// Build stem method table
			for (SourceClass sourceClass : sourceClasses) {
				for (SourceMethod sourceMethod : sourceClass.getMethods()) {

					int stemCount = 0;

					// Retrieve stems for the method
					List<StemMethod> methodStems = getMethodStems(sourceMethod);
					
					if (methodStems.size() > 0) {
						
						stems.addAll(methodStems);						
						stemCount =+ methodStems.size();
						
						StemMethod rootStem = methodStems.get(0);
						
						// Retrieve all parameters
						for (SourceParameter sourceParameter : sourceMethod.getParameters()) {
							List<StemMethod> parameterStems = getParameterStems(sourceParameter, sourceMethod, rootStem);
							stems.addAll(parameterStems);
							stemCount =+ parameterStems.size();
						}

						// Retrieve all unique references			
						Map<String, String> referenceMap = new HashMap<>();
						for (SourceReference sourceReference : sourceMethod.getReferences()) {
							String refName = sourceReference.getName();
							if (!referenceMap.containsKey(refName)) {
								referenceMap.put(refName, refName);
								List<StemMethod> referenceStems = getReferenceStems(sourceReference, sourceMethod, rootStem);
								stems.addAll(referenceStems);
								stemCount =+ referenceStems.size();
							}
						}
					}
					
					Console.writeInfo(this, stemCount + " stems found in method: " + sourceMethod.getName());
					stemMethodCount += stemCount;
				}
			}

			ApplicationLogic.saveStemMethods(stems);
			Console.writeInfo(this, stemMethodCount + " total stem-method found");
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
	 * Extract stems from method.
	 * 
	 * @param sourceMethod
	 * @return a list of StemMethod
	 */
	private List<StemMethod> getMethodStems(SourceMethod sourceMethod) {
		
		List<StemMethod> result = new ArrayList<>();

		// Retrieve stems for the method name
		String methodName = sourceMethod.getName();
		List<String> stems = ApplicationLogic.getStems(methodName, rejectedMethodList);

		if (stems.size() > 0) {

			// Create a stem for the method name
			String fullName = stems.remove(0);
			StemMethod fullStem = new StemMethod(project, null, sourceMethod, fullName, StemMethodType.METHOD_NAME_FULL, null);
			result.add(fullStem);

			// Create sub-stems for parts
			if (stems.size() > 0) {
				
				for (String partName : stems) {
					result.add(new StemMethod(project, fullStem, sourceMethod, partName, StemMethodType.METHOD_NAME_PART, null));
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Extract stems from parameter.
	 * 
	 * @param sourceParameter
	 * @param sourceMethod
	 * @param parent
	 * @return
	 */
	private List<StemMethod> getParameterStems(SourceParameter sourceParameter, SourceMethod sourceMethod, StemMethod parent) {

		List<StemMethod> result = new ArrayList<>();
		
		// Retrieve stems for parameter name
		String parameterName = sourceParameter.getName();
		List<String> stems = ApplicationLogic.getStems(parameterName, null);

		if (stems.size() > 0) {

			// Create a stem for the parameter name
			String fullName = stems.remove(0);
			StemMethod fullStem = new StemMethod(project, parent, sourceMethod, fullName, StemMethodType.PARAMETER_NAME_FULL, null);
			result.add(fullStem);

			// Create sub-stems for parts
			if (stems.size() > 0) {
				
				for (String partName : stems) {
					result.add(new StemMethod(project, fullStem, sourceMethod, partName, StemMethodType.PARAMETER_NAME_PART, null));
				}
			}
			
			// Retrieve stems for parameter type
			String parameterType = sourceParameter.getType();
			stems = ApplicationLogic.getStems(parameterType, rejectedTypeList);
			
			if (stems.size() > 0) {

				// Create a stem for the the parameter type
				String paramType = stems.remove(0);
				StemMethod typeStem = new StemMethod(project, fullStem, sourceMethod, paramType, StemMethodType.PARAMETER_TYPE_FULL, null);
				result.add(typeStem);
			}
		}
		
		return result;
	}
	
	/**
	 * Extract stems from reference.
	 * 
	 * @param sourceReference
	 * @param sourceMethod
	 * @param parent
	 * @return
	 */
	private List<StemMethod> getReferenceStems(SourceReference sourceReference, SourceMethod sourceMethod, StemMethod parent) {

		List<StemMethod> result = new ArrayList<>();
		
		// Retrieve stems for reference name
		String referenceName = sourceReference.getName();
		List<String> stems = ApplicationLogic.getStems(referenceName, null);

		if (stems.size() > 0) {

			// Create a stem for the reference name
			String fullName = stems.remove(0);
			StemMethod fullStem = new StemMethod(project, parent, sourceMethod, fullName, StemMethodType.REFERENCE_NAME_FULL, sourceReference.getOrigin());
			result.add(fullStem);
			
			// Create sub-stems for parts
			if (stems.size() > 0) {
				
				for (String partName : stems) {
					result.add(new StemMethod(project, fullStem, sourceMethod, partName, StemMethodType.REFERENCE_NAME_PART, sourceReference.getOrigin()));
				}
			}
			
			// Retrieve stems for reference type
			String referenceType = sourceReference.getType();
			stems = ApplicationLogic.getStems(referenceType, rejectedTypeList);
			
			if (stems.size() > 0) {

				// Create a stem for the the reference type
				String refType = stems.remove(0);
				StemMethod typeStem = new StemMethod(project, fullStem, sourceMethod, refType, StemMethodType.REFERENCE_TYPE_FULL, sourceReference.getOrigin());
				result.add(typeStem);
			}				
		}			
		
		return result;
	}
	
}
