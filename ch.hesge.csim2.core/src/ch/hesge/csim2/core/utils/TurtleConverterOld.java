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
public class TurtleConverterOld implements TurtleEventHandler {

	// Private attributes
	private boolean debug;
	private Map<String, Concept> conceptMap;
	private Map<String, ConceptAttribute> attributeMap;
	private Map<String, ConceptClass> classMap;
	private Map<String, ConceptLink> relationMap;
	
	/**
	 * Default constructor
	 */
	public TurtleConverterOld(boolean debug) {
		
		this.debug = debug;
		this.conceptMap = new HashMap<>();
		this.attributeMap = new HashMap<>();
		this.classMap = new HashMap<>();
		this.relationMap = new HashMap<>();
		
		Console.writeDebug(TurtleConverterOld.class, "parsing turtle file format...");
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
	    	Console.writeDebug(TurtleConverterOld.class, "Elements found while parsing:");
	    	
		    // Dump all concepts found
		    for (Concept concept : concepts) {
		    	
		    	Console.writeDebug(TurtleConverterOld.class, "concept: " + concept.getName());
		    	
		    	Console.writeDebug(TurtleConverterOld.class, "   location: x=" + concept.getBounds().x + ",y=" + concept.getBounds().y + ",width=" + concept.getBounds().width + ",height=" +concept.getBounds().height);
		    	
			    for (ConceptAttribute attribute : concept.getAttributes()) {
			    	Console.writeDebug(TurtleConverterOld.class, "   attribute: " + attribute.getName() + ", identifier: " + attribute.getIdentifier());
			    }
			    
			    for (ConceptClass clazz : concept.getClasses()) {
			    	Console.writeDebug(TurtleConverterOld.class, "   class: " + clazz.getName() + ", identifier: " + clazz.getIdentifier());
			    }
			    
			    for (ConceptLink link : concept.getLinks()) {
			    	Console.writeDebug(TurtleConverterOld.class, "   link: " + link.getQualifier() + ", target: " + link.getTargetConcept().getName());
			    }
		    }
	    }
	    
	    return concepts;
	}
	
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

		if (debug) {
			Console.writeDebug(TurtleConverterOld.class, "triplet: " + subject + " " + predicate + " " + object + ".");
		}

		if (predicate.equals("type")) {

			// Detect classes
			if (object.equals("Class")) {
				conceptMap.put(subject, new Concept());
			}

			// Detect attributes
			else if (object.equals("DatatypeProperty")) {

				if (subject.startsWith("attributeName")) {
					attributeMap.put(subject, new ConceptAttribute());
				}
				else if (subject.startsWith("attributeClass")) {
					classMap.put(subject, new ConceptClass());
				}
			}

			// Detect links
			else if (object.equals("ObjectProperty")) {
				relationMap.put(subject, new ConceptLink());
			}
		}

		if (predicate.equals("domain")) {

			// Detect attribute owner
			if (attributeMap.containsKey(subject) && conceptMap.containsKey(object)) {
				Concept concept = conceptMap.get(object);
				ConceptAttribute attribute = attributeMap.get(subject);
				concept.getAttributes().add(attribute);
			}

			// Detect class owner
			else if (classMap.containsKey(subject) && conceptMap.containsKey(object)) {
				Concept concept = conceptMap.get(object);
				ConceptClass clazz = classMap.get(subject);
				concept.getClasses().add(clazz);
			}

			// Detect link source
			else if (relationMap.containsKey(subject) && conceptMap.containsKey(object)) {
				Concept concept = conceptMap.get(object);
				ConceptLink link = relationMap.get(subject);
				link.setSourceConcept(concept);
				concept.getLinks().add(link);
			}
		}

		// Detect link target
		if (predicate.equals("range")) {

			if (relationMap.containsKey(subject) && conceptMap.containsKey(object)) {
				Concept concept = conceptMap.get(object);
				ConceptLink link = relationMap.get(subject);
				link.setTargetConcept(concept);
			}
		}

		// Detect concept, attribute, link names
		else if (predicate.equals("label")) {

			if (conceptMap.containsKey(subject)) {
				Concept concept = conceptMap.get(subject);
				concept.setName(object);
			}
			else if (attributeMap.containsKey(subject)) {

				ConceptAttribute attribute = attributeMap.get(subject);

				if (lang.isEmpty()) {
					attribute.setName(object);
				}
				else {
					attribute.setIdentifier(object);
				}
			}
			else if (classMap.containsKey(subject)) {

				ConceptClass clazz = classMap.get(subject);

				if (lang.isEmpty()) {
					clazz.setName(object);
				}
				else {
					clazz.setIdentifier(object);
				}
			}
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
