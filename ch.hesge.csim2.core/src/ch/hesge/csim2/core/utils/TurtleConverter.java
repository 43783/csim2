package ch.hesge.csim2.core.utils;

import java.awt.Rectangle;
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

/**
 * This class convert turtle triplet into concept.
 * Derived from Turtle2NTriples sources.
 * 
 * @author Eric Harth
 *
 */
public class TurtleConverter implements TurtleEventHandler {

	// Private attributes
	private boolean debug;
	private Map<String, Concept> conceptMap;
	private Map<String, ConceptAttribute> attributeMap;
	private Map<String, ConceptClass> classMap;
	private Map<String, ConceptLink> relationMap;
	
	/**
	 * Default constructor
	 */
	public TurtleConverter(boolean debug) {
		
		this.debug = debug;
		this.conceptMap = new HashMap<>();
		this.attributeMap = new HashMap<>();
		this.classMap = new HashMap<>();
		this.relationMap = new HashMap<>();
		
		Console.writeDebug(TurtleConverter.class, "parsing turtle file format...");
	}
	
	/**
	 * Return all concepts found during parsing process (through TurtleParser)
	 * @return
	 */
	public List<Concept> getConcepts() {
		
		List<Concept> concepts = new ArrayList<>(conceptMap.values());
		
	    for (Concept concept : concepts) {

			List<ConceptAttribute> attributes = new ArrayList<>();
			
			// Filter attribute location
		    for (ConceptAttribute attribute : concept.getAttributes()) {
		    	
		    	if (attribute.getName().endsWith("Location")) {
		    		
					String[] boundsItems = attribute.getIdentifier().split(",");
					Rectangle bounds = new Rectangle();
					bounds.x = (int) Integer.valueOf(boundsItems[0]);
					bounds.y = (int) Integer.valueOf(boundsItems[1]);
					bounds.width = (int) Integer.valueOf(boundsItems[2]);
					bounds.height = (int) Integer.valueOf(boundsItems[3]);
					
					concept.setBounds(bounds);
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
			    	Console.writeDebug(TurtleConverter.class, "   location: x=" + concept.getBounds().x + ",y=" + concept.getBounds().y + ",width=" + concept.getBounds().width + ",height=" +concept.getBounds().height);
		    	}
		    	
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
	    
	    return concepts;
	}
	
	@Override
	public void triple(int line, int col, Triple triple) {

		// Retrieve rdf triplet
		Node subjectNode   = triple.getSubject();
		Node predicateNode = triple.getPredicate();
		Node objectNode    = triple.getObject();

		// Extract names
		String subject   = subjectNode.getURI().substring(subjectNode.getURI().indexOf('#') + 1);
		String predicate = predicateNode.getURI().substring(predicateNode.getURI().indexOf('#') + 1);
		String object    = objectNode.isURI() ? objectNode.getURI().substring(objectNode.getURI().indexOf('#') + 1) : objectNode.getLiteralLexicalForm().toString();
		String lang      = objectNode.isURI() || objectNode.getLiteralLanguage() == null ? "" : objectNode.getLiteralLanguage();

		if (debug) {
			Console.writeDebug(TurtleConverter.class, "triplet: " + subject + " " + predicate + " " + object + ".");
		}

		if (predicate.equals("type")) {

			// Detect classes
			if (object.equals("Class") && !conceptMap.containsKey(subject)) {
				conceptMap.put(subject, new Concept());
			}

			// Detect name attributes
			else if (object.equals("DatatypeProperty") && subject.startsWith("attributeName") && !attributeMap.containsKey(subject)) {
				attributeMap.put(subject, new ConceptAttribute());
			}

			// Detect class attributes
			else if (object.equals("DatatypeProperty") && subject.startsWith("attributeClass") && !attributeMap.containsKey(subject)) {
				classMap.put(subject, new ConceptClass());
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
			
			// Detect attribute owner
			if (attributeMap.containsKey(subject)) {
				Concept concept = conceptMap.get(object);
				ConceptAttribute attribute = attributeMap.get(subject);
				concept.getAttributes().add(attribute);
			}

			// Detect class owner
			else if (classMap.containsKey(subject)) {
				Concept concept = conceptMap.get(object);
				ConceptClass clazz = classMap.get(subject);
				concept.getClasses().add(clazz);
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
			
			// Detect attribute name
			else if (attributeMap.containsKey(subject) && lang.isEmpty()) {
				ConceptAttribute attribute = attributeMap.get(subject);
				attribute.setName(object);
			}

			// Detect attribute identifier
			else if (attributeMap.containsKey(subject) && !lang.isEmpty()) {
				ConceptAttribute attribute = attributeMap.get(subject);
				attribute.setIdentifier(object);
			}
			
			// Detect class name
			else if (classMap.containsKey(subject) && lang.isEmpty()) {
				ConceptClass clazz = classMap.get(subject);
				clazz.setName(object);
			}

			// Detect class identifier
			else if (classMap.containsKey(subject) && !lang.isEmpty()) {
				ConceptClass clazz = classMap.get(subject);
				clazz.setIdentifier(object);
			}
			
			// Detect relation qualifier
			else if (relationMap.containsKey(subject)) {
				ConceptLink link = relationMap.get(subject);
				link.setQualifier(object);
			}
		}
	}

	@Override
	public void startFormula(int arg0, int arg1) {
	}

	@Override
	public void prefix(int arg0, int arg1, String arg2, String arg3) {
	}

	@Override
	public void endFormula(int arg0, int arg1) {
	}
}
