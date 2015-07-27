package ch.hesge.csim2.core.utils;

import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

		// Generate file content
		emitLine(writer, "@prefix owl: <http://www.w3.org/2002/07/owl#>.");
		emitLine(writer, "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>.");
		emitLine(writer, "@prefix xml: <http://www.w3.org/XML/1998/namespace>.");
		emitLine(writer, "@prefix xsd: <http://www.w3.org/2001/XMLSchema#>.");
		emitLine(writer, "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>.");
		emitLine(writer, "@prefix : <http://hesge.ch/project/csim2#>.");
		emitLine(writer, "@base <http://hesge.ch/project/csim2#>.");
		emitLine(writer, "");
		emitLine(writer, "#");
		emitLine(writer, "# Classes");
		emitLine(writer, "#");
		emitLine(writer, "");

		for (Concept concept : concepts) {

			emitLine(writer, ":Class" + concept.getKeyId() + " a owl:Class; rdfs:label \"" + concept.getName() + "\".");

			// Create an attribute for visual concept location
			emitLine(writer, "   :attributeBounds" + concept.getKeyId() + " a owl:DatatypeProperty;");
			emitLine(writer, "      rdfs:domain :Class" + concept.getKeyId() + ";");
			emitLine(writer, "      rdfs:label \"@Bounds\";");
			emitLine(writer, "      rdfs:label \"" + concept.getBounds().x + "," + concept.getBounds().y + "," + concept.getBounds().width + "," + concept.getBounds().height + "\"@ie.");

			// Create an attribute for visual concept location
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

			// Save all attributes
			for (ConceptClass conceptClass : concept.getClasses()) {

				emitLine(writer, "   :attributeClass" + conceptClass.getKeyId() + " a owl:DatatypeProperty;");
				emitLine(writer, "      rdfs:domain :Class" + concept.getKeyId() + ";");
				emitLine(writer, "      rdfs:label \"" + conceptClass.getName() + "\";");
				emitLine(writer, "      rdfs:label \"" + conceptClass.getIdentifier() + "\"@ie.");
			}

			emitLine(writer, "");
		}

		emitLine(writer, "#");
		emitLine(writer, "# Relations");
		emitLine(writer, "#");
		emitLine(writer, "");

		int linkCount = 0;

		// Save all links
		for (Concept concept : concepts) {
			for (ConceptLink conceptLink : concept.getLinks()) {

				emitLine(writer, ":relation" + linkCount + " a owl:ObjectProperty;");
				emitLine(writer, "   rdfs:label \"" + conceptLink.getQualifier() + "\";");
				emitLine(writer, "   rdfs:domain :Class" + conceptLink.getSourceId() + ";");
				emitLine(writer, "   rdfs:range :Class" + conceptLink.getTargetId() + ".");

				linkCount++;
			}
		}
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

		List<TurtleTriplet> triplets = new ArrayList<>();

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
				triplets.add(triplet);

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

		// Start parsing
		parser.parse();

		Console.writeDebug(this, "extracting model information...");

		// Detect all concepts, properties and link between them 
		for (TurtleTriplet triplet : triplets) {

			String object = triplet.getObject().toLowerCase();
			String predicate = triplet.getPredicate().toLowerCase();
			String subject = triplet.getSubject();

			if (predicate.equals("type")) {

				// Detect classes
				if (!conceptMap.containsKey(subject) && object.equals("class")) {
					conceptMap.put(triplet.getSubject(), new Concept());
				}
				
				// Detect attributes
				else if (!attributeMap.containsKey(subject) && object.equals("DatatypeProperty")) {
					attributeMap.put(subject, new ConceptAttribute());
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
			String predicate = triplet.getPredicate().toLowerCase();
			String subject = triplet.getSubject();
			String lang = triplet.getLang();

			// Detect subsumption relation
			if (predicate.equals("subclassof")) {
				
				if (conceptMap.containsKey(subject) && conceptMap.containsKey(object)) {
					Concept concept = conceptMap.get(subject);
					Concept superconcept = conceptMap.get(object);
					ConceptLink link = new ConceptLink();
					link.setQualifier("subclass-of");
					link.setSourceConcept(concept);
					link.setTargetConcept(superconcept);
					concept.getLinks().add(link);
					linkMap.put(concept.getClass() + "_subclassof_" + superconcept.getClass(), link);
				}
			}
			
			// Detect labels
			else if (predicate.equals("label")) {

				// Detect class label
				if (conceptMap.containsKey(subject)) {
					Concept concept = conceptMap.get(subject);
					concept.setName(object);
				}

				// Detect attribute name
				else if (attributeMap.containsKey(subject) && lang.isEmpty()) {
					attributeMap.get(subject).setName(object);
				}

				// Detect attribute identifier
				else if (attributeMap.containsKey(subject) && !lang.isEmpty()) {
					attributeMap.get(subject).setIdentifier(subject);
				}

				// Detect relation qualifier
				else if (linkMap.containsKey(subject)) {
					linkMap.get(subject).setQualifier(object);
				}
			}
			
			// Detect object owner
			else if (predicate.equals("domain")) {

				// Detect attribute owner
				if (attributeMap.containsKey(subject) && conceptMap.containsKey(object)) {
					conceptMap.get(object).getAttributes().add(attributeMap.get(subject));
				}

				// Detect link owner
				else if (linkMap.containsKey(subject) && conceptMap.containsKey(object)) {
					Concept concept = conceptMap.get(object);
					ConceptLink link = linkMap.get(subject);
					link.setSourceConcept(concept);
					concept.getLinks().add(link);
				}
			}

			// Detect target links
			else if (predicate.equals("range")) {
				if (linkMap.containsKey(subject) && conceptMap.containsKey(object)) {
					linkMap.get(subject).setTargetConcept(conceptMap.get(object));
				}
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

		if (debug) {
			dumpConcept(concepts);
		}
	}

	public void dumpConcept(List<Concept> concepts) {
		
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
	
	/**
	 * Parse a single triplet.
	 * 
	 * @param subject
	 * @param predicate
	 * @param subject
	 */
	private void parseTriplet(String subject, String predicate, String object, String objectLang) {

		if (predicate.equals("type")) {

			// Detect classes
			if (object.toLowerCase().equals("class") && !conceptMap.containsKey(subject)) {
				conceptMap.put(subject, new Concept());
			}

			// Detect data properties
			else if (object.equals("DatatypeProperty")) {

				if (subject.startsWith("attributeClass") && !conceptClassMap.containsKey(subject)) {
					conceptClassMap.put(subject, new ConceptClass());
				}
				else {
					attributeMap.put(subject, new ConceptAttribute());
				}
			}

			// Detect links between concepts
			else if (object.equals("ObjectProperty") && !linkMap.containsKey(subject)) {
				linkMap.put(subject, new ConceptLink());
			}
		}

		else if (predicate.equals("domain")) {

			// Auto create class, if not already declared
			if (object.toLowerCase().equals("class") && !conceptMap.containsKey(subject)) {
				conceptMap.put(object, new Concept());
			}

			// Detect class attribute owner
			if (conceptClassMap.containsKey(subject)) {
				Concept concept = conceptMap.get(object);
				ConceptClass clazz = conceptClassMap.get(subject);
				concept.getClasses().add(clazz);
			}

			// Detect name attribute owner
			else if (attributeMap.containsKey(subject)) {
				Concept concept = conceptMap.get(object);
				ConceptAttribute attribute = attributeMap.get(subject);
				concept.getAttributes().add(attribute);
			}

			// Detect link source
			else if (linkMap.containsKey(subject)) {
				Concept concept = conceptMap.get(object);
				ConceptLink link = linkMap.get(subject);
				link.setSourceConcept(concept);
				concept.getLinks().add(link);
			}
		}

		// Detect link target
		else if (predicate.equals("range")) {

			// Auto create class, if not already declared
			if (object.toLowerCase().equals("class") && !conceptMap.containsKey(subject)) {
				conceptMap.put(object, new Concept());
			}

			// Detect target concept
			if (linkMap.containsKey(subject)) {
				Concept concept = conceptMap.get(object);
				ConceptLink link = linkMap.get(subject);
				link.setTargetConcept(concept);
			}
		}

		// Detect concept, attribute, link names
		else if (predicate.equals("label")) {

			// Detect class label
			if (conceptMap.containsKey(subject)) {
				Concept concept = conceptMap.get(subject);
				concept.setName(object);
			}

			// Detect class name
			else if (conceptClassMap.containsKey(subject) && objectLang.isEmpty()) {
				ConceptClass clazz = conceptClassMap.get(subject);
				clazz.setName(object);
			}

			// Detect class identifier
			else if (conceptClassMap.containsKey(subject) && !objectLang.isEmpty()) {
				ConceptClass clazz = conceptClassMap.get(subject);
				clazz.setIdentifier(object);
			}

			// Detect attribute name
			else if (attributeMap.containsKey(subject) && objectLang.isEmpty()) {
				ConceptAttribute attribute = attributeMap.get(subject);
				attribute.setName(object);
			}

			// Detect attribute identifier
			else if (attributeMap.containsKey(subject) && !objectLang.isEmpty()) {
				ConceptAttribute attribute = attributeMap.get(subject);
				attribute.setIdentifier(object);
			}

			// Detect relation qualifier
			else if (linkMap.containsKey(subject)) {
				ConceptLink link = linkMap.get(subject);
				link.setQualifier(object);
			}
		}
	}

}
