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

	private Project project;
	private Ontology ontology;
	private List<String> rejectedList;

	/**
	 * Default constructor.
	 */
	public StemConceptAnalyzer() {
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
		return "1.0.3";
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

			// Retrieve current ontology
			if (context.containsKey("ontology")) {
				ontology = (Ontology) context.getProperty("ontology");
			}
			else {
				throw new EngineException("missing ontology specified !");
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
	 *      Scan all concepts and identify all stem associated to each concept.
	 */
	@Override
	public void start() {

		try {

			int stemConceptCount = 0;

			Console.writeInfo(this, "cleaning previous stem concepts...");
			ApplicationLogic.deleteStemConcepts(ontology);

			// Load all ontology concepts
			Console.writeInfo(this, "loading ontology concepts...");
			List<Concept> conceptList = ApplicationLogic.getConcepts(ontology);

			Console.writeInfo(this, "scanning all concepts...");

			// Build stem concept table
			for (Concept concept : conceptList) {

				// Retrieve stems for the concept
				String conceptName = concept.getName();
				List<String> conceptStems = getStems(conceptName, rejectedList);

				// Create a stem for the full concept name
				String conceptNameFull = StringUtils.concatenate(conceptStems);
				StemConcept stemConceptNameFull = new StemConcept(project, null, concept, conceptNameFull, StemConceptType.CONCEPT_NAME_FULL);
				ApplicationLogic.saveStemConcept(stemConceptNameFull);
				stemConceptCount++;

				// Create a stem for each part of the concept name
				for (String conceptNamePart : conceptStems) {
					StemConcept stemConceptNamePart = new StemConcept(project, stemConceptNameFull, concept, conceptNamePart, StemConceptType.CONCEPT_NAME_PART);
					ApplicationLogic.saveStemConcept(stemConceptNamePart);
					stemConceptCount++;
				}

				for (ConceptAttribute conceptAttribute : concept.getAttributes()) {

					// Retrieve stems for the attribute
					String attributeName = conceptAttribute.getName();
					List<String> attributeStems = getStems(attributeName, rejectedList);

					// Create a stem for the full attribute name
					String attributeNameFull = StringUtils.concatenate(attributeStems);
					StemConcept stemAttributNameFull = new StemConcept(project, stemConceptNameFull, concept, attributeNameFull, StemConceptType.ATTRIBUTE_NAME_FULL);
					ApplicationLogic.saveStemConcept(stemAttributNameFull);
					stemConceptCount++;

					// Create a stem for each part of the attribute
					for (String attributNamePart : attributeStems) {
						StemConcept stemAttributeNamePart = new StemConcept(project, stemAttributNameFull, concept, attributNamePart, StemConceptType.ATTRIBUTE_NAME_PART);
						ApplicationLogic.saveStemConcept(stemAttributeNamePart);
						stemConceptCount++;
					}

					// Retrieve stems for the identifier
					String identifierName = conceptAttribute.getIdentifier();
					List<String> identifierStems = getStems(identifierName, rejectedList);

					// Create a stem for the full identifier name
					String identifierNameFull = StringUtils.concatenate(identifierStems);
					StemConcept stemIdentifierNameFull = new StemConcept(project, stemAttributNameFull, concept, identifierNameFull, StemConceptType.ATTRIBUTE_IDENTIFIER_FULL);
					ApplicationLogic.saveStemConcept(stemIdentifierNameFull);
					stemConceptCount++;

					// Create a stem for each part of the identifier
					for (String identifierNamePart : identifierStems) {
						StemConcept stemIdentifierNamePart = new StemConcept(project, stemIdentifierNameFull, concept, identifierNamePart, StemConceptType.ATTRIBUTE_IDENTIFIER_PART);
						ApplicationLogic.saveStemConcept(stemIdentifierNamePart);
						stemConceptCount++;
					}
				}

				for (ConceptClass conceptClass : concept.getClasses()) {

					// Retrieve stems for the class
					String className = conceptClass.getName();
					List<String> classStems = getStems(className, rejectedList);

					// Create a stem for the full class name
					String classNameFull = StringUtils.concatenate(classStems);
					StemConcept stemClassNameFull = new StemConcept(project, stemConceptNameFull, concept, classNameFull, StemConceptType.CLASS_NAME_FULL);
					ApplicationLogic.saveStemConcept(stemClassNameFull);
					stemConceptCount++;

					// Create a stem for each part of the class
					for (String classNamePart : classStems) {
						StemConcept stemClassNamePart = new StemConcept(project, stemClassNameFull, concept, classNamePart, StemConceptType.CLASS_NAME_PART);
						ApplicationLogic.saveStemConcept(stemClassNamePart);
						stemConceptCount++;
					}

					// Retrieve stems for the class identifier
					String identifierName = conceptClass.getIdentifier();
					List<String> identifierStems = getStems(identifierName, rejectedList);

					// Create a stem for the full identifier name
					String identifierNameFull = StringUtils.concatenate(identifierStems);
					StemConcept stemIdentifierNameFull = new StemConcept(project, stemClassNameFull, concept, identifierNameFull, StemConceptType.CLASS_IDENTIFIER_FULL);
					ApplicationLogic.saveStemConcept(stemIdentifierNameFull);
					stemConceptCount++;

					// Create a stem for each part of the identifier
					for (String identifierNamePart : identifierStems) {
						StemConcept stemIdentifierNamePart = new StemConcept(project, stemIdentifierNameFull, concept, identifierNamePart, StemConceptType.CLASS_IDENTIFIER_PART);
						ApplicationLogic.saveStemConcept(stemIdentifierNamePart);
						stemConceptCount++;
					}
				}
			}

			Console.writeInfo(this, stemConceptCount + " stems concepts found");
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
