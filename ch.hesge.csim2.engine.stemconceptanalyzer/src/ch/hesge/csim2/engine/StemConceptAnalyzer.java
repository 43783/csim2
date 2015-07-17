/**
 * 
 */
package ch.hesge.csim2.engine;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import ch.hesge.csim2.core.logic.ApplicationLogic;
import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.ConceptAttribute;
import ch.hesge.csim2.core.model.ConceptClass;
import ch.hesge.csim2.core.model.Context;
import ch.hesge.csim2.core.model.IEngine;
import ch.hesge.csim2.core.model.Ontology;
import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.StemConcept;
import ch.hesge.csim2.core.model.StemConceptType;
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
public class StemConceptAnalyzer implements IEngine {

	// Private attributes
	private Context context;
	private ApplicationLogic applicationLogic;

	private Project project;
	private Ontology ontology;
	private List<String> rejectedList;

	/**
	 * Default constructor.
	 */
	public StemConceptAnalyzer() {
		applicationLogic = ApplicationLogic.UNIQUE_INSTANCE;
	}

	/**
	 * Get the engine name.
	 * 
	 * @see ch.hesge.csim2.core.shell.IEngine#getName()
	 */
	@Override
	public String getName() {
		return "StemConceptAnalyzer";
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
		return "analyze all concepts and generate all stem concepts.";
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
		params.put("ontology", "ontology");
		params.put("rejected-names", "file");

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

			// Retrieve current ontology
			if (context.containsKey("ontology")) {
				ontology = (Ontology) context.getProperty("ontology");
			}
			else {
				throw new EngineException("missing ontology specified !");
			}

			// Retrieve path to rejected words file
			Path rejectedPath = Paths.get("conf", "rejected-names.txt").toAbsolutePath();
			if (context.containsKey("rejected-names")) {
				String rejectedFileParam = (String) context.getProperty("rejected-names");
				if (rejectedFileParam != null && rejectedFileParam.trim().length() > 0) {
					rejectedPath = Paths.get(rejectedFileParam);
				}
			}

			// Check if rejected word file exists
			if (!rejectedPath.toFile().exists()) {
				throw new EngineException("file '" + rejectedPath.getFileName().toString() + "' doesn't not exist !");
			}

			// Load rejected word list
			rejectedList = Files.readAllLines(rejectedPath, Charset.defaultCharset());
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
	 *      Scan all concepts and identify all stem associated to each concept.
	 */
	@Override
	public void start() {

		try {

			List<StemConcept> stems = new ArrayList<>();

			// Load all ontology concepts
			Console.writeInfo(this, "loading ontology concepts...");
			List<Concept> conceptList = applicationLogic.getConcepts(ontology);

			Console.writeInfo(this, "scanning all concepts...");

			// Build stem concept table
			for (Concept concept : conceptList) {

				int stemCount = 0;
				
				// Retrieve stems for the method name
				List<StemConcept> conceptStems = getConceptStems(concept);
				
				if (conceptStems.size() > 0) {
					
					stems.addAll(conceptStems);						
					stemCount =+ conceptStems.size();
					
					StemConcept rootStem = conceptStems.get(0);

					// Retrieve stems for the attributes
					for (ConceptAttribute conceptAttribute : concept.getAttributes()) {
						List<StemConcept> attributeStems = getAttributeStems(conceptAttribute, concept, rootStem);
						stems.addAll(attributeStems);
						stemCount += attributeStems.size();
					}
					
					for (ConceptClass conceptClass : concept.getClasses()) {
						List<StemConcept> classStems = getClassStems(conceptClass, concept, rootStem);
						stems.addAll(classStems);
						stemCount += classStems.size();
					}					
				}
				
				Console.writeInfo(this, stemCount + " stems found in concept: " + concept.getName());
			}

			// Save stems found
			Console.writeInfo(this, "saving " + stems.size() + " stems found...");
			applicationLogic.saveStemConcepts(ontology, stems);
		}
		catch (Exception e) {
			Console.writeError(this, "error while analyzing concepts: " + StringUtils.toString(e));
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
	 * Extract stems from concept name.
	 * 
	 * @param concept
	 * @return a list of StemConcept
	 */
	private List<StemConcept> getConceptStems(Concept concept) {

		List<StemConcept> result = new ArrayList<>();
		
		// Retrieve stems for the concept name
		String conceptName = concept.getName();
		List<String> stems = applicationLogic.getStems(conceptName, rejectedList);

		if (stems.size() > 0) {
			
			// Create a stem for the full concept name
			String fullName = stems.remove(0);
			StemConcept fullStem = new StemConcept(project, null, concept, fullName, StemConceptType.CONCEPT_NAME_FULL);
			result.add(fullStem);
			
			// Create sub-stems for parts
			if (stems.size() > 0) {
				
				for (String partName : stems) {
					result.add(new StemConcept(project, fullStem, concept, partName, StemConceptType.CONCEPT_NAME_PART));
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Extract stems from attribute.
	 * 
	 * @param conceptAttribute
	 * @param concept
	 * @param parent
	 * @return a list of StemConcept
	 */
	private List<StemConcept> getAttributeStems(ConceptAttribute conceptAttribute, Concept concept, StemConcept parent) {

		List<StemConcept> result = new ArrayList<>();
		
		// Retrieve stems for the attribute
		String attributeName = conceptAttribute.getName();
		List<String> stems = applicationLogic.getStems(attributeName, rejectedList);

		if (stems.size() > 0) {
			
			// Create a stem for the full attribute name
			String fullName = stems.remove(0);
			StemConcept fullStem = new StemConcept(project, parent, concept, fullName, StemConceptType.ATTRIBUTE_NAME_FULL);
			result.add(fullStem);
			
			// Create sub-stems for parts
			if (stems.size() > 0) {
				
				for (String partName : stems) {
					result.add(new StemConcept(project, fullStem, concept, partName, StemConceptType.ATTRIBUTE_NAME_PART));
				}
			}

			// Retrieve stems for the identifier
			String identifierName = conceptAttribute.getIdentifier();
			stems = applicationLogic.getStems(identifierName, rejectedList);
			
			if (stems.size() > 0) {

				// Create a stem for the the parameter type
				String fullIdentifierName = stems.remove(0);
				result.add(new StemConcept(project, fullStem, concept, fullIdentifierName, StemConceptType.ATTRIBUTE_IDENTIFIER_FULL));
			}
		}
		
		return result;
	}
	
	/**
	 * Extract stems from concept class.
	 * 
	 * @param conceptClass
	 * @param concept
	 * @param parent
	 * @return a list of StemConcept
	 */
	private List<StemConcept> getClassStems(ConceptClass conceptClass, Concept concept, StemConcept parent) {

		List<StemConcept> result = new ArrayList<>();
		
		// Retrieve stems for the class
		String className = conceptClass.getName();
		List<String> stems = applicationLogic.getStems(className, rejectedList);

		if (stems.size() > 0) {
			
			// Create a stem for the full class name
			String fullName = stems.remove(0);
			StemConcept fullStem = new StemConcept(project, parent, concept, fullName, StemConceptType.CLASS_NAME_FULL);
			result.add(fullStem);

			// Retrieve stems for the class identifier
			String identifierName = conceptClass.getIdentifier();
			stems = applicationLogic.getStems(identifierName, rejectedList);

			if (stems.size() > 0) {
				
				// Create a stem for the full identifier name
				String fullIdentifierName = stems.remove(0);
				result.add(new StemConcept(project, fullStem, concept, fullIdentifierName, StemConceptType.CLASS_IDENTIFIER_FULL));
			}
		}

		return result;
	}
	
	
	/**
	 * TestCase to study how names are splitted through camel casing.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		String name = "dE";
		
		/**
		 * 		Données de calcul Propriété physique 	=	donne, de, calcul, propriet,  physiqu
		 * 
		 * 		CamelCasingTest      = Camel, Casing, Test
		 * 		methodRemoveProperty = method, Remove, Property
		 * 		ConceptNumber328Real = Concept, Number328, Real
		 * 		TestCPTSmall         = Test, CPT, Small
		 * 		CBatFluM0            = Bat, Flu, M0
		 * 		CCircBatLiqCalcul    = Circ, Bat, Liq, Calcul
		 *  	Petits_Calculs       = Petits, Calculs
		 *  	_Petits_Calculs_     = Petits, Calculs
		 *  	Petits Calculs       = Petits, Calculs
		 *      dE                   = d, E
		 */
		
		System.out.println(name);
		
		List<String> stems = ApplicationLogic.UNIQUE_INSTANCE.getStems(name, new ArrayList<String>());
		
		for (String stem : stems) {
			System.out.println(" " + stem);
		}
	}
}
