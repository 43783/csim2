/**
 * 
 */
package ch.hesge.csim2.engine;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import ch.hesge.csim2.core.logic.ApplicationLogic;
import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.ConceptAttribute;
import ch.hesge.csim2.core.model.ConceptClass;
import ch.hesge.csim2.core.model.ConceptLink;
import ch.hesge.csim2.core.model.Context;
import ch.hesge.csim2.core.model.IEngine;
import ch.hesge.csim2.core.model.OntoTerm;
import ch.hesge.csim2.core.model.Ontology;
import ch.hesge.csim2.core.utils.Console;
import ch.hesge.csim2.core.utils.EngineException;
import ch.hesge.csim2.core.utils.StringUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;

/**
 * This engine initialize an ontology with
 * information contained in RDF file or txt files.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 *
 */
public class OntologyLoader implements IEngine {

	// Private attributes
	private Context context;
	private ApplicationLogic applicationLogic;
	private Path ontologyFile;
	private Ontology ontology;
	
	private Map<String, Concept> conceptMap;
	private Map<String, ConceptLink> conceptLinksMap;
	private boolean isActionOntology;
	private boolean isDebugMode;

	/**
	 * Default constructor.
	 */
	public OntologyLoader() {
		applicationLogic = ApplicationLogic.UNIQUE_INSTANCE;
		conceptMap = new HashMap<>();
		conceptLinksMap = new HashMap<>();
	}

	/**
	 * Get the engine name.
	 * 
	 * @see ch.hesge.csim2.core.shell.IEngine#getName()
	 */
	@Override
	public String getName() {
		return "OntologyLoader";
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
		return "load concepts found in a RDF file or a txt file (action concepts).";
	}

	/**
	 * Return the parameter map required by the engine.
	 * Each entry should contains: a parameter name and its type.
	 * 
	 * @see ch.hesge.csim2.core.shell.IEngine#getParameters()
	 */
	@Override
	public Properties getParameters() {

		Properties params = new Properties();

		params.put("project", "project");
		params.put("ontology", "ontology");
		params.put("filename", "file");
		params.put("is-action", "boolean");
		params.put("is-debug", "boolean");

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

			String inputFile = null;

			// Retrieve current ontology
			if (context.containsKey("ontology")) {
				ontology = (Ontology) context.getProperty("ontology");
			}
			else {
				throw new EngineException("missing ontology specified !");
			}

			// Retrieve input file specified
			if (context.containsKey("filename")) {
				inputFile = (String) context.getProperty("filename");
			}
			else {
				throw new EngineException("missing ontology path specified !");
			}

			// Retrieve if input file is an action-list or entity-list
			if (context.containsKey("is-action")) {
				String isActionConceptParam = (String) context.getProperty("is-action");
				isActionOntology = isActionConceptParam.equalsIgnoreCase("true") || isActionConceptParam.equalsIgnoreCase("yes") || isActionConceptParam.equalsIgnoreCase("t") || isActionConceptParam.equalsIgnoreCase("y");
			}
			else {
				isActionOntology = false;
			}

			// Retrieve if we are in debug mode
			if (context.containsKey("is-debug")) {
				String isDebugParam = (String) context.getProperty("is-debug");
				isDebugMode = isDebugParam.equalsIgnoreCase("yes") || isDebugParam.equalsIgnoreCase("true") || isDebugParam.equalsIgnoreCase("y") || isDebugParam.equalsIgnoreCase("t");
			}
			else {
				isDebugMode = false;
			}

			// Check if input file exists
			ontologyFile = Paths.get(inputFile).toAbsolutePath().normalize();
			if (!ontologyFile.toFile().exists()) {
				throw new EngineException("file '" + ontologyFile + "' doesn't not exist !");
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

			conceptMap.clear();
			conceptLinksMap.clear();

			if (isDebugMode) {
				doDumpRdfContent();
				return;
			}
			else if (isActionOntology) {

				// Parse action concept file
				Console.writeInfo(this, "parsing action file " + ontologyFile.getFileName().toString() + ".");
				doParseActionConcepts();
			}
			else {

				// Parse entity concept file
				Console.writeInfo(this, "parsing rdf file " + ontologyFile.getFileName().toString() + ".");
				doParseEntityConcepts();
			}

			Console.writeInfo(this, conceptMap.size() + " concepts found.");

			// Add all concepts found into the ontology
			ontology.getConcepts().clear();
			ontology.getConcepts().addAll(conceptMap.values());

			// Save the ontology and its new concepts
			Console.writeInfo(this, "saving ontology in database...");
			applicationLogic.saveOntology(ontology);
			
			Console.writeInfo(this, "done.");
		}
		catch (Exception e) {
			Console.writeError(this, "error while loading ontology: " + StringUtils.toString(e));
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
	 * Parse an RDF file.
	 */
	private void doParseEntityConcepts() {

		Map<String, ConceptLink> tupleMap = new HashMap<>();
		Map<String, ConceptAttribute> attributeMap = new HashMap<>();
		Map<String, OntoTerm> ontoTermMap = new HashMap<>();

		// Load RDF model from file
		Model model = ModelFactory.createDefaultModel();
		FileManager.get().readModel(model, ontologyFile.toString());

		// First phase: iterate over all statement to identify objects
		StmtIterator iterator = model.listStatements();

		while (iterator.hasNext()) {

			Statement statement = iterator.next();

			String subject = statement.getSubject().getLocalName();
			String predicate = statement.getPredicate().getLocalName();
			String object = statement.getObject().toString();

			if (predicate.equals("type")) {

				if (object.endsWith("Class")) {

					// Skip concept Relation & Tuple
					if (!subject.endsWith("Relation") && !subject.endsWith("Tuple")) {
						Concept concept = new Concept();
						concept.setAction(false);
						conceptMap.put(subject, concept);
					}
				}
				else if (object.endsWith("Tuple")) {
					ConceptLink link = new ConceptLink();
					tupleMap.put(subject, link);
				}
				else if (object.endsWith("attribute")) {
					ConceptAttribute attribute = new ConceptAttribute();
					attributeMap.put(subject, attribute);
				}
				else if (object.endsWith("ontoterm")) {

					OntoTerm term = new OntoTerm();
					ontoTermMap.put(subject, term);
				}
			}
		}

		// Second phase: iterate over all statement to retrieve object
		// information
		iterator = model.listStatements();

		while (iterator.hasNext()) {

			Statement statement = iterator.next();

			String subject = statement.getSubject().getLocalName();
			String predicate = statement.getPredicate().getLocalName();
			String object = statement.getObject().toString();

			// Detect label node (concept name, concept class name, attribute
			// name or link name)
			if (predicate.equals("label")) {

				// Label is contained within a concept
				if (conceptMap.containsKey(subject)) {
					Concept concept = conceptMap.get(subject);
					concept.setName(object);
				}

				// Label is contained within an attribute
				else if (attributeMap.containsKey(subject)) {
					ConceptAttribute attribute = attributeMap.get(subject);
					attribute.setName(object);
				}

				// Label is contained within a ontoterm
				else if (ontoTermMap.containsKey(subject)) {

					OntoTerm ontoTerm = ontoTermMap.get(subject);

					// Label is an class name with its identifiers (owned by a
					// concept)
					if (object.endsWith("@#P")) {

						String termName = object.replace("@#P", "");
						String[] terms = termName.split(" ");

						for (int i = 0; i < terms.length; i++) {
							if (i == 0) {
								// First term is the class name
								ontoTerm.setName(terms[i]);
							}
							else {
								// Other terms are class identifiers
								ontoTerm.getIdentifiers().add(terms[i]);
							}
						}
					}

					// Label is an attribute name (owned by an attribute)
					else {
						ontoTerm.setName(object);
					}
				}

				// Label is a link name (owned by a link)
				else if (tupleMap.containsKey(subject)) {
					ConceptLink link = tupleMap.get(subject);
					String qualifier = object.substring(0, object.indexOf("(")).replace(" ", "_");
					link.setQualifier(qualifier);
				}
			}

			// Detect attribute node
			else if (predicate.equals("has_attribute")) {

				String attributeId = object.split("#")[1];

				if (conceptMap.containsKey(subject) && attributeMap.containsKey(attributeId)) {
					Concept concept = conceptMap.get(subject);
					ConceptAttribute attribute = attributeMap.get(attributeId);
					concept.getAttributes().add(attribute);
				}
			}

			// Detect term node
			else if (predicate.equals("has_ontoterm")) {

				String ontoTermId = object.split("#")[1];

				if (ontoTermMap.containsKey(ontoTermId)) {

					OntoTerm ontoTerm = ontoTermMap.get(ontoTermId);

					// If the onterm is owned by a concept
					if (conceptMap.containsKey(subject)) {
						Concept concept = conceptMap.get(subject);
						ontoTerm.setOwnerConcept(concept);
					}
					// If the onterm is owned by an attribute
					else if (attributeMap.containsKey(subject)) {
						ConceptAttribute attribute = attributeMap.get(subject);
						ontoTerm.setOwnerAttribute(attribute);
					}
				}
			}

			// Detect subclass node
			else if (predicate.equals("subClassOf")) {

				String targetConceptId = object.split("#")[1];

				// Retrieve source & target concept
				if (conceptMap.containsKey(subject) && conceptMap.containsKey(targetConceptId)) {

					Concept sourceConcept = conceptMap.get(subject);
					Concept targetConcept = conceptMap.get(targetConceptId);

					// Create a new link between both concepts
					ConceptLink link = new ConceptLink();
					link.setQualifier("subclass-of");
					link.setSourceConcept(sourceConcept);
					link.setTargetConcept(targetConcept);

					sourceConcept.getLinks().add(link);
				}
			}

			// Detect from-relation part
			else if (predicate.equals("from")) {

				String fromConceptId = object.split("#")[1];

				// Retrieve link and the from concept
				if (tupleMap.containsKey(subject) && conceptMap.containsKey(fromConceptId)) {
					ConceptLink link = tupleMap.get(subject);
					Concept fromConcept = conceptMap.get(fromConceptId);
					link.setSourceConcept(fromConcept);
				}
			}

			// Detect to-relation part
			else if (predicate.equals("to")) {

				String toConceptId = object.split("#")[1];

				// Retrieve link and the from concept
				if (tupleMap.containsKey(subject) && conceptMap.containsKey(toConceptId)) {
					ConceptLink link = tupleMap.get(subject);
					Concept toConcept = conceptMap.get(toConceptId);
					link.setTargetConcept(toConcept);
				}
			}
		}

		// Add all links to their source concept
		for (ConceptLink link : tupleMap.values()) {
			if (link.getSourceConcept() != null && !link.getQualifier().equalsIgnoreCase("is_a")) {
				link.getSourceConcept().getLinks().add(link);
			}
		}

		// Resolve term in concept/attribute
		for (OntoTerm ontoTerm : ontoTermMap.values()) {

			if (ontoTerm.getOwnerConcept() != null) {

				Concept concept = ontoTerm.getOwnerConcept();

				// Create one class concept by identifier
				for (String identifier : ontoTerm.getIdentifiers()) {

					ConceptClass conceptClass = new ConceptClass();
					conceptClass.setName(ontoTerm.getName());
					conceptClass.setIdentifier(identifier);

					concept.getClasses().add(conceptClass);
				}
			}
			else {
				ConceptAttribute attribute = ontoTerm.getOwnerAttribute();
				attribute.setIdentifier(ontoTerm.getName());
			}
		}

		// Dump all concepts found into the ontology
		for (Concept concept : conceptMap.values()) {

			Console.writeInfo(this, "concept: " + concept.getName());

			for (ConceptClass conceptClass : concept.getClasses()) {
				Console.writeInfo(this, "  class: " + conceptClass.getName() + ", id: " + conceptClass.getIdentifier());
			}

			for (ConceptAttribute conceptAttribute : concept.getAttributes()) {
				Console.writeInfo(this, "  attribute: " + conceptAttribute.getName() + ", id: " + conceptAttribute.getIdentifier());
			}

			for (ConceptLink conceptLink : concept.getLinks()) {
				Console.writeInfo(this, "  link: <" + conceptLink.getQualifier() + "> " + conceptLink.getTargetConcept().getName());
			}
		}
	}

	/**
	 * Parse an action-concept list.
	 */
	private void doParseActionConcepts() {

		try {
			Path txtPath = Paths.get(ontologyFile.toString());
			List<String> conceptTerms = Files.readAllLines(txtPath, Charset.defaultCharset());

			for (String term : conceptTerms) {

				Concept concept = new Concept();
				concept.setName(term);
				concept.setAction(true);
				concept.setOntologyId(ontology.getKeyId());

				conceptMap.put(term, concept);
			}
		}
		catch (IOException e) {
			Console.writeError(this, "error while reading concept action file: " + StringUtils.toString(e));
		}
	}

	/**
	 * Dump RDF file content.
	 */
	private void doDumpRdfContent() {

		// Load RDF model from file
		Model model = ModelFactory.createDefaultModel();
		FileManager.get().readModel(model, ontologyFile.toString());

		// First phase: iterate over all statement to identify objects
		StmtIterator iterator = model.listStatements();

		while (iterator.hasNext()) {

			Statement statement = iterator.next();

			String subject = statement.getSubject().getLocalName();
			String predicate = statement.getPredicate().getLocalName();
			String object = statement.getObject().toString();

			System.out.println("subject: " + subject + ", predicate: " + predicate + ", object: " + object);
		}
	}
}
