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
	private Map<String, ConceptClass> classMap;
	private Map<String, ConceptLink> relationMap;
	
	/**
	 * Default constructor
	 */
	public TurtleConverter(boolean debug) {		
		
		this.debug = debug;
		
		this.conceptMap   = new HashMap<>();
		this.attributeMap = new HashMap<>();
		this.classMap     = new HashMap<>();
		this.relationMap  = new HashMap<>();
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
	    	Console.writeDebug(TurtleConverter.class, "emit: " + triplet);
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
		classMap.clear();
		relationMap.clear();
		
		// Parse the reader content (in turtle format)
		TurtleParser parser = new TurtleParser(reader);
		parser.setEventHandler(new TurtleEventHandler() {
			
			@Override
			public void triple(int line, int col, Triple triple) {

				// Retrieve rdf triplet
				Node subjectNode   = triple.getSubject();
				Node predicateNode = triple.getPredicate();
				Node objectNode    = triple.getObject();

				// Extract names
				String subject    = subjectNode.getURI().substring(subjectNode.getURI().indexOf('#') + 1);
				String predicate  = predicateNode.getURI().substring(predicateNode.getURI().indexOf('#') + 1);
				String object     = objectNode.isURI() ? objectNode.getURI().substring(objectNode.getURI().indexOf('#') + 1) : objectNode.getLiteralLexicalForm().toString();
				String objectLang = objectNode.isURI() || objectNode.getLiteralLanguage() == null ? "" : objectNode.getLiteralLanguage();

				if (debug) {
					Console.writeDebug(TurtleConverter.class, "triplet: " + subject + " " + predicate + " " + object + "(" + objectLang + ").");
				}
				
				parseTriplet(subject, predicate, object, objectLang);
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
		
		Console.writeDebug(TurtleConverter.class, "parsing turtle file format...");

		// Start parsing
	    parser.parse();
	    
	    // Now extract all internal attribute (starting with @) and class attributes
		concepts = new ArrayList<>(conceptMap.values());
		
	    for (Concept concept : concepts) {

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
	    }
	    
	    if (debug) {
	    	
	    	Console.writeDebug(TurtleConverter.class, concepts.size() + " elements found while parsing:");
	    	
		    // Dump all concepts found
		    for (Concept concept : concepts) {
		    	
		    	Console.writeDebug(TurtleConverter.class, "concept: " + concept.getName());
		    	
		    	if (concept.getBounds() != null) {
			    	Console.writeDebug(TurtleConverter.class, "   bounds: x=" + concept.getBounds().x + ",y=" + concept.getBounds().y + ",width=" + concept.getBounds().width + ",height=" +concept.getBounds().height);
		    	}
		    	
		    	Console.writeDebug(TurtleConverter.class, "   action: " + concept.isAction());
		    	
			    for (ConceptAttribute attribute : concept.getAttributes()) {
			    	Console.writeDebug(TurtleConverter.class, "   attribute: " + attribute.getName() + ", identifier: " + attribute.getIdentifier());
			    }
			    
			    for (ConceptClass clazz : concept.getClasses()) {
			    	Console.writeDebug(TurtleConverter.class, "   class: " + clazz.getName() + ", identifier: " + clazz.getIdentifier());
			    }
			    
			    for (ConceptLink link : concept.getLinks()) {
			    	Console.writeDebug(TurtleConverter.class, "   link: " + link.getQualifier() + ", target: " + link.getTargetConcept().getName());
			    }
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
			if (object.equals("Class") && !conceptMap.containsKey(subject)) {
				conceptMap.put(subject, new Concept());
			}

			// Detect class attributes
			else if (object.equals("DatatypeProperty") && subject.startsWith("attributeClass") && !classMap.containsKey(subject)) {
				classMap.put(subject, new ConceptClass());
			}

			// Detect name attributes
			else if (object.equals("DatatypeProperty") && subject.startsWith("attribute") && !attributeMap.containsKey(subject)) {
				attributeMap.put(subject, new ConceptAttribute());
			}

			// Detect links between concepts
			else if (object.equals("ObjectProperty") && !relationMap.containsKey(subject)) {
				relationMap.put(subject, new ConceptLink());
			}
		}

		else if (predicate.equals("domain")) {

			// Auto create class, if not already declared
			if (object.startsWith("Class") && !conceptMap.containsKey(object)) {
				conceptMap.put(object, new Concept());
			}
			
			// Detect class attribute owner
			if (classMap.containsKey(subject)) {
				Concept concept = conceptMap.get(object);
				ConceptClass clazz = classMap.get(subject);
				concept.getClasses().add(clazz);
			}
			
			// Detect name attribute owner
			else if (attributeMap.containsKey(subject)) {
				Concept concept = conceptMap.get(object);
				ConceptAttribute attribute = attributeMap.get(subject);
				concept.getAttributes().add(attribute);
			}

			// Detect link source
			else if (relationMap.containsKey(subject)) {
				Concept concept = conceptMap.get(object);
				ConceptLink link = relationMap.get(subject);
				link.setSourceConcept(concept);
				concept.getLinks().add(link);
			}
		}

		// Detect link target
		else if (predicate.equals("range")) {

			// Auto create class, if not already declared
			if (object.startsWith("Class") && !conceptMap.containsKey(object)) {
				conceptMap.put(object, new Concept());
			}
			
			// Detect target concept
			if (relationMap.containsKey(subject)) {
				Concept concept = conceptMap.get(object);
				ConceptLink link = relationMap.get(subject);
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
			else if (classMap.containsKey(subject) && objectLang.isEmpty()) {
				ConceptClass clazz = classMap.get(subject);
				clazz.setName(object);
			}

			// Detect class identifier
			else if (classMap.containsKey(subject) && !objectLang.isEmpty()) {
				ConceptClass clazz = classMap.get(subject);
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
			else if (relationMap.containsKey(subject)) {
				ConceptLink link = relationMap.get(subject);
				link.setQualifier(object);
			}
		}
	}
	
}
