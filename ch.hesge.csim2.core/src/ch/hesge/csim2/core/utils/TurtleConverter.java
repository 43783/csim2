package ch.hesge.csim2.core.utils;

import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.hesge.csim2.core.logic.OntologyLogic;
import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.ConceptAttribute;
import ch.hesge.csim2.core.model.ConceptClass;
import ch.hesge.csim2.core.model.ConceptLink;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.n3.turtle.TurtleEventHandler;
import com.hp.hpl.jena.n3.turtle.parser.ParseException;
import com.hp.hpl.jena.n3.turtle.parser.TurtleParser;

/**
 * This class convert turtle triplet into concept.
 * Derived from Turtle2NTriples sources.
 * 
 * @author Eric Harth
 *
 */
public class TurtleConverter {

	// Private attributes
	private boolean debug;
	private List<Concept> concepts;
	private Map<String, Concept> conceptMap;
	private Map<String, ConceptAttribute> attributeMap;
	private Map<String, ConceptClass> conceptClassMap;
	private Map<String, ConceptLink> linkMap;

	/**
	 * Default constructor
	 */
	public TurtleConverter(boolean debug) {

		this.debug = debug;

		this.concepts = new ArrayList<>();
		this.conceptMap = new HashMap<>();
		this.attributeMap = new HashMap<>();
		this.conceptClassMap = new HashMap<>();
		this.linkMap = new HashMap<>();
	}

	/**
	 * Return all concepts found during parsing process (through TurtleParser)
	 * @return
	 */
	public List<Concept> getConcepts() {
		return concepts;
	}

	/**
	 * Generate all concepts in turtle format on
	 * the writer passed in argument.
	 * 
	 * @param writer
	 * @throws IOException
	 */
	public void generate(OutputStreamWriter writer, List<Concept> concepts) throws IOException {

		int linkCount = 0;

		// Generate file content
		emitLine(writer, "@prefix owl: <http://www.w3.org/2002/07/owl#>.");
		emitLine(writer, "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>.");
		emitLine(writer, "@prefix xml: <http://www.w3.org/XML/1998/namespace>.");
		emitLine(writer, "@prefix xsd: <http://www.w3.org/2001/XMLSchema#>.");
		emitLine(writer, "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>.");
		emitLine(writer, "@prefix : <http://hesge.ch/project/csim2#>.");
		emitLine(writer, "@base <http://hesge.ch/project/csim2#>.");
		emitLine(writer, "");

		for (Concept concept : concepts) {

			// Create a class
			emitLine(writer, ":Class" + concept.getKeyId() + " a owl:Class;");

			// Retrieve subsumption links
			List<ConceptLink> subclassLinks = new ArrayList<>();
			for (ConceptLink link : concept.getLinks()) {
				if (OntologyLogic.isSubsumptionLink(link)) {
					subclassLinks.add(link);
				}
			}
			
			// Create subclass predicates
			if (subclassLinks.isEmpty()) {
				emitLine(writer, "   rdfs:label \"" + concept.getName() + "\".");
				
			}
			else {
				emitLine(writer, "   rdfs:label \"" + concept.getName() + "\";");
				
				for (int i = 0; i < subclassLinks.size(); i++) {
					ConceptLink link = subclassLinks.get(i);
					if (i != subclassLinks.size() - 1) {
						emitLine(writer, "   rdfs:subClassOf :Class" + link.getTargetConcept().getKeyId() + ";");
					}
					else {
						emitLine(writer, "   rdfs:subClassOf :Class" + link.getTargetConcept().getKeyId() + ".");
					}
				}
			}
			
			// Create an attribute for concept location
			emitLine(writer, "   :attributeBounds" + concept.getKeyId() + " a owl:DatatypeProperty;");
			emitLine(writer, "      rdfs:domain :Class" + concept.getKeyId() + ";");
			emitLine(writer, "      rdfs:label \"@Bounds\";");
			emitLine(writer, "      rdfs:label \"" + concept.getBounds().x + "," + concept.getBounds().y + "," + concept.getBounds().width + "," + concept.getBounds().height + "\"@ie.");

			// Create an attribute for concept action
			emitLine(writer, "   :attributeAction" + concept.getKeyId() + " a owl:DatatypeProperty;");
			emitLine(writer, "      rdfs:domain :Class" + concept.getKeyId() + ";");
			emitLine(writer, "      rdfs:label \"@IsAction\";");
			emitLine(writer, "      rdfs:label \"" + concept.isAction() + "\"@ie.");

			// Save all classes
			for (ConceptAttribute conceptAttribute : concept.getAttributes()) {

				emitLine(writer, "   :attributeName" + conceptAttribute.getKeyId() + " a owl:DatatypeProperty;");
				emitLine(writer, "      rdfs:domain :Class" + concept.getKeyId() + ";");
				emitLine(writer, "      rdfs:label \"" + conceptAttribute.getName() + "\";");
				emitLine(writer, "      rdfs:label \"" + conceptAttribute.getIdentifier() + "\"@ie.");
			}

			// Save attributes
			for (ConceptClass conceptClass : concept.getClasses()) {

				emitLine(writer, "   :attributeClass" + conceptClass.getKeyId() + " a owl:DatatypeProperty;");
				emitLine(writer, "      rdfs:domain :Class" + concept.getKeyId() + ";");
				emitLine(writer, "      rdfs:label \"" + conceptClass.getName() + "\";");
				emitLine(writer, "      rdfs:label \"" + conceptClass.getIdentifier() + "\"@ie.");
			}

			// Save links
			for (ConceptLink link : concept.getLinks()) {
				if (!OntologyLogic.isSubsumptionLink(link)) {
					emitLine(writer, "   :conceptLink" + linkCount++ + " a owl:ObjectProperty;");
					emitLine(writer, "      rdfs:domain :Class" + concept.getKeyId() + ";");
					emitLine(writer, "      rdfs:range :Class" + link.getTargetConcept().getKeyId() + ";");
					emitLine(writer, "      rdfs:label \"" + link.getQualifier() + "\".");
				}
			}
			
			emitLine(writer, "");
		}

		emitLine(writer, "");
	}

	/**
	 * Parse a single triplet.
	 * 
	 * @param subject
	 * @param predicate
	 * @param subject
	 * @throws IOException
	 */
	private void emitLine(OutputStreamWriter writer, String triplet) throws IOException {

		writer.append(triplet + "\n");

		if (debug) {
			Console.writeDebug(this, "emit: " + triplet);
		}

	}

	/**
	 * Parse a file concent in turtle format and
	 * extract all concepts found.
	 * 
	 * @param reader
	 * @throws ParseException
	 */
	public void parse(InputStreamReader reader) throws ParseException {

		conceptMap.clear();
		attributeMap.clear();
		conceptClassMap.clear();
		linkMap.clear();

		// Parse and analyze found triples
		List<TurtleTriplet> triples = parseTriples(reader);
		analyzeTriples(triples);

		// Dump result
		if (debug) {
			dumpConcept(concepts);
		}
	}

	/**
	 * Parse turtle triples.
	 * 
	 * @param triplets
	 * @throws ParseException
	 */
	private List<TurtleTriplet> parseTriples(InputStreamReader reader) throws ParseException {

		List<TurtleTriplet> triples = new ArrayList<>();

		Console.writeDebug(this, "parsing turtle triplets...");

		// Parse the reader content (in turtle format)
		TurtleParser parser = new TurtleParser(reader);
		parser.setEventHandler(new TurtleEventHandler() {

			@Override
			public void triple(int line, int col, Triple triple) {

				// Retrieve rdf triplet
				Node subjectNode = triple.getSubject();
				Node predicateNode = triple.getPredicate();
				Node objectNode = triple.getObject();

				// Extract names
				String subject = subjectNode.getURI().substring(subjectNode.getURI().indexOf('#') + 1);
				String predicate = predicateNode.getURI().substring(predicateNode.getURI().indexOf('#') + 1);
				String object = objectNode.isURI() ? objectNode.getURI().substring(objectNode.getURI().indexOf('#') + 1) : objectNode.getLiteralLexicalForm().toString();
				String lang = objectNode.isURI() || objectNode.getLiteralLanguage() == null ? "" : objectNode.getLiteralLanguage();

				// Create the triplet
				TurtleTriplet triplet = new TurtleTriplet(subject, predicate, object, lang);
				triples.add(triplet);

				// Trace triplet, if required
				if (debug) {
					Console.writeDebug(this, triplet.toString() + ".");
				}
			}

			@Override
			public void startFormula(int line, int col) {
			}

			@Override
			public void prefix(int line, int col, String arg2, String arg3) {

			}

			@Override
			public void endFormula(int line, int col) {
			}
		});

		// Use JENA to parse triples
		parser.parse();

		return triples;
	}

	/**
	 * Extract all informations from triples.
	 * 
	 * @param triplets
	 */
	private void analyzeTriples(List<TurtleTriplet> triplets) {

		Console.writeDebug(this, "extracting model information...");

		// Detect all concepts, properties and links 
		for (TurtleTriplet triplet : triplets) {

			String subject = triplet.getSubject();
			String predicate = triplet.getPredicate();
			String object = triplet.getObject();

			if (predicate.equals("type")) {

				// Detect classes
				if (!conceptMap.containsKey(subject) && object.equals("Class")) {
					conceptMap.put(subject, new Concept());
				}

				// Detect attributes
				if (object.equals("DatatypeProperty")) {
					
					// Detect standard/class attributes
					if (subject.startsWith("attributeClass") && !conceptClassMap.containsKey(subject)) {
						conceptClassMap.put(subject, new ConceptClass());
					}
					else if (!attributeMap.containsKey(subject)) {
						attributeMap.put(subject, new ConceptAttribute());
					}
				}

				// Detect relations
				else if (!linkMap.containsKey(subject) && object.equals("ObjectProperty")) {
					linkMap.put(subject, new ConceptLink());
				}
			}
		}

		// Now scan all object properties 
		for (TurtleTriplet triplet : triplets) {

			String object = triplet.getObject();
			String predicate = triplet.getPredicate();
			String subject = triplet.getSubject();
			String lang = triplet.getLang();

			// Detect labels
			if (predicate.equals("label")) {

				// Detect class label
				if (conceptMap.containsKey(subject)) {
					conceptMap.get(subject).setName(object);
				}

				// Detect class attribute
				else if (conceptClassMap.containsKey(subject)) {
					
					// Detect attribute/identifier name
					if (lang.equals("ie")) {
						conceptClassMap.get(subject).setIdentifier(object);
					}
					else {
						conceptClassMap.get(subject).setName(object);
					}
				}
				
				// Detect standard attribute
				else if (attributeMap.containsKey(subject)) {
					
					// Detect attribute/identifier name
					if (lang.equals("ie")) {
						attributeMap.get(subject).setIdentifier(object);
					}
					else {
						attributeMap.get(subject).setName(object);
					}
				}

				// Detect relation qualifier
				else if (linkMap.containsKey(subject)) {
					linkMap.get(subject).setQualifier(object);
				}
			}

			// Detect object owner
			else if (predicate.equals("domain")) {

				// Detect class attribute owner
				if (conceptClassMap.containsKey(subject)) {
					conceptMap.get(object).getClasses().add(conceptClassMap.get(subject));
				}
				
				// Detect standard attribute owner
				else if (attributeMap.containsKey(subject)) {
					conceptMap.get(object).getAttributes().add(attributeMap.get(subject));
				}

				// Detect link owner
				else if (linkMap.containsKey(subject)) {
					linkMap.get(subject).setSourceConcept(conceptMap.get(object));
					conceptMap.get(object).getLinks().add(linkMap.get(subject));
				}
			}

			// Detect target links
			else if (predicate.equals("range")) {
				if (linkMap.containsKey(subject)) {
					linkMap.get(subject).setTargetConcept(conceptMap.get(object));
				}
			}

			// Detect rdf subsumption relation
			else if (predicate.equals("subClassOf")) {

				Concept concept = conceptMap.get(subject);
				Concept superconcept = conceptMap.get(object);

				ConceptLink link = new ConceptLink();
				link.setQualifier("subclass-of");
				link.setSourceConcept(concept);
				link.setTargetConcept(superconcept);
				
				concept.setSuperConcept(superconcept);
				concept.getLinks().add(link);
			}
		}

		concepts.clear();

		// Finally filter special internal attribute (starting with @)
		for (Concept concept : conceptMap.values()) {

			// Skip objects without names
			if (concept.getName() == null || concept.getName().length() == 0) {
				continue;
			}

			List<ConceptAttribute> attributes = new ArrayList<>();

			// Filter attribute location
			for (ConceptAttribute attribute : concept.getAttributes()) {

				if (attribute.getName().equals("@Bounds")) {

					String[] boundsItems = attribute.getIdentifier().split(",");
					Rectangle bounds = new Rectangle();
					bounds.x = (int) Integer.valueOf(boundsItems[0]);
					bounds.y = (int) Integer.valueOf(boundsItems[1]);
					bounds.width = (int) Integer.valueOf(boundsItems[2]);
					bounds.height = (int) Integer.valueOf(boundsItems[3]);

					concept.setBounds(bounds);
				}
				else if (attribute.getName().equals("@IsAction")) {

					boolean isAction = Boolean.valueOf(attribute.getIdentifier());
					concept.setAction(isAction);
				}
				else {
					attributes.add(attribute);
				}
			}

			concept.getAttributes().clear();
			concept.getAttributes().addAll(attributes);

			concepts.add(concept);
		}
	}

	/**
	 * Dump concepts found.
	 * 
	 * @param concepts
	 */
	private void dumpConcept(List<Concept> concepts) {

		Console.writeDebug(this, concepts.size() + " concepts found:");

		// Dump all concepts found
		for (Concept concept : concepts) {

			Console.writeDebug(this, "concept: " + concept.getName());

			Console.writeDebug(this, "   bounds: x=" + concept.getBounds().x + ",y=" + concept.getBounds().y + ",width=" + concept.getBounds().width + ",height=" + concept.getBounds().height);

			Console.writeDebug(this, "   action: " + concept.isAction());

			for (ConceptAttribute attribute : concept.getAttributes()) {
				Console.writeDebug(this, "   attribute: " + attribute.getName() + ", identifier: " + attribute.getIdentifier());
			}

			for (ConceptClass clazz : concept.getClasses()) {
				Console.writeDebug(this, "   class: " + clazz.getName() + ", identifier: " + clazz.getIdentifier());
			}

			for (ConceptLink link : concept.getLinks()) {
				Console.writeDebug(this, "   link: " + link.getQualifier() + ", target: " + link.getTargetConcept().getName());
			}
		}
	}
}
