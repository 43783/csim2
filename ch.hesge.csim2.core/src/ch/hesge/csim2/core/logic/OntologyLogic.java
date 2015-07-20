/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hesge.csim2.core.logic;

import java.awt.Rectangle;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.hesge.csim2.core.dao.ConceptAttributeDao;
import ch.hesge.csim2.core.dao.ConceptClassDao;
import ch.hesge.csim2.core.dao.ConceptDao;
import ch.hesge.csim2.core.dao.ConceptLinkDao;
import ch.hesge.csim2.core.dao.OntologyDao;
import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.ConceptAttribute;
import ch.hesge.csim2.core.model.ConceptClass;
import ch.hesge.csim2.core.model.ConceptLink;
import ch.hesge.csim2.core.model.Ontology;
import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.utils.Console;
import ch.hesge.csim2.core.utils.FileUtils;
import ch.hesge.csim2.core.utils.ObjectSorter;
import ch.hesge.csim2.core.utils.PersistanceUtils;
import ch.hesge.csim2.core.utils.StringUtils;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.n3.turtle.TurtleEventHandler;
import com.hp.hpl.jena.n3.turtle.parser.TurtleParser;

/**
 * This class implement all logical rules associated to ontology.
 *
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

class OntologyLogic {

	/**
	 * Retrieve all available ontologies without their dependencies.
	 * 
	 * @return
	 *         a list of ontology
	 */
	public static List<Ontology> getOntologies() {
		return OntologyDao.findAll();
	}

	/**
	 * Retrieve all ontologies owned by a project without their dependencies.
	 * 
	 * @param project
	 *        the owner
	 * 
	 * @return
	 *         a list of ontology
	 */
	public static List<Ontology> getOntologies(Project project) {
		return OntologyDao.findByProject(project);
	}

	/**
	 * Retrieve all concepts owned by an project and its ontologies.
	 * 
	 * @param project
	 *        the owner
	 * 
	 * @return
	 *         the list of concept
	 */
	public static List<Concept> getConcepts(Project project) {

		Map<Integer, Concept> conceptMap = ApplicationLogic.UNIQUE_INSTANCE.getConceptMap(project);

		// Convert the map into a list
		List<Concept> concepts = new ArrayList<>(conceptMap.values());

		// Sort concepts
		ObjectSorter.sortConcepts(concepts);

		return concepts;
	}

	/**
	 * Retrieve a list of all concepts owned by an ontology.
	 * 
	 * @param ontology
	 *        the owner
	 * 
	 * @return
	 *         the list of concept
	 */
	public static List<Concept> getConcepts(Ontology ontology) {

		Map<Integer, Concept> conceptMap = ApplicationLogic.UNIQUE_INSTANCE.getConceptMap(ontology);

		// Convert the map into a list
		List<Concept> concepts = new ArrayList<>(conceptMap.values());

		// Sort concepts
		ObjectSorter.sortConcepts(concepts);

		return concepts;
	}

	/**
	 * Retrieve all concepts owned by a project as a (keyId, Concept) map.
	 * 
	 * @param project
	 *        the owner
	 * 
	 * @return
	 *         a map of concept
	 */
	public static Map<Integer, Concept> getConceptMap(Project project) {

		Map<Integer, Concept> conceptMap = new HashMap<>();

		List<Concept> concepts = ConceptDao.findByProject(project);

		// First populate the concept map
		for (Concept concept : concepts) {
			conceptMap.put(concept.getKeyId(), concept);
		}

		// Then populate dependencies
		for (Concept concept : concepts) {
			populateDependencies(concept, conceptMap);
		}

		return conceptMap;
	}

	/**
	 * Retrieve a map of concepts owned by an ontology
	 * with each entries of the form (keyId, Concept) map.
	 * 
	 * @param ontology
	 *        the owner
	 * 
	 * @return
	 *         a map of concept
	 */
	public static Map<Integer, Concept> getConceptMap(Ontology ontology) {

		Map<Integer, Concept> conceptMap = new HashMap<>();

		List<Concept> concepts = ConceptDao.findByOntology(ontology);

		// First populate the concept map
		for (Concept concept : concepts) {
			conceptMap.put(concept.getKeyId(), concept);
		}

		// Then populate dependencies
		for (Concept concept : concepts) {
			populateDependencies(concept, conceptMap);
		}

		return conceptMap;
	}

	/**
	 * Populate a concept will its attributes:
	 * 
	 * - its concept attributes
	 * - its concept classes
	 * - its links
	 * - its superconcept
	 * - its children concept
	 * 
	 * @param concept
	 *        the concept to populate
	 * @param comparator
	 *        the comparator used to sort children
	 * @param conceptMap
	 *        the map of all concept
	 */
	private static void populateDependencies(Concept concept, Map<Integer, Concept> conceptMap) {

		// Populate attributes
		concept.getAttributes().clear();
		concept.getAttributes().addAll(ConceptAttributeDao.findByConcept(concept));
		ObjectSorter.sortConceptAttributes(concept.getAttributes());

		// Populate concept classes
		concept.getClasses().clear();
		concept.getClasses().addAll(ConceptClassDao.findByConcept(concept));
		ObjectSorter.sortConceptClasses(concept.getClasses());

		// Update concept hierarchy
		for (ConceptLink link : ConceptLinkDao.findByConcept(concept)) {

			// Update concept with instances
			link.setSourceConcept(concept);
			link.setTargetConcept(conceptMap.get(link.getTargetId()));

			// Add the link to the concept
			concept.getLinks().add(link);

			// Detect concept hierarchy
			if (link.getQualifier() != null && link.getTargetConcept() != null && link.getQualifier().equals("subclass-of")) {
				concept.setSuperConcept(link.getTargetConcept());
				link.getTargetConcept().getSubConcepts().add(concept);
				ObjectSorter.sortConcepts(link.getTargetConcept().getSubConcepts());
			}
		}
	}

	/**
	 * Create a new ontology.
	 * 
	 * @param name
	 *        the ontology name
	 * @param project
	 *        the owning project
	 * @return and instance of ontology
	 */
	public static Ontology createOntology(String name, Project project) {
		
		Ontology ontology = new Ontology();
		
		ontology.setName(name);
		ontology.setProjectId(project.getKeyId());
		OntologyDao.add(ontology);
		
		return ontology;
	}

	/**
	 * Create a new concept and attach it to the ontology.
	 * 
	 * @param ontology
	 *        the future owner
	 * 
	 * @return
	 *         a new concept
	 */
	public static Concept createConcept(Ontology ontology) {

		Concept concept = new Concept();

		concept.setName("Concept");
		concept.setBounds(new Rectangle(0, 0, 64, 32));
		ontology.getConcepts().add(concept);

		return concept;
	}
	
	/**
	 * Clone the concept passed in argument into a distinct instance (same
	 * keyId).
	 * 
	 * @param concept
	 *        the concept to clone
	 * @return a new concept instance
	 */
	public static Concept cloneConcept(Concept concept) {
		
		Concept c = new Concept();
		
		c.setKeyId(concept.getKeyId());
		c.setOntology(concept.getOntology());
		c.setSuperConcept(concept.getSuperConcept());
		c.setName(concept.getName());
		c.setBounds(concept.getBounds());
		c.setOntologyId(concept.getOntologyId());
		c.setSuperConceptId(concept.getSuperConceptId());
		c.setAction(concept.isAction());
		c.getAttributes().addAll(concept.getAttributes());
		c.getClasses().addAll(concept.getClasses());
		c.getLinks().addAll(concept.getLinks());
		c.getSubConcepts().addAll(concept.getSubConcepts());

		return c;
	}

	/**
	 * Copy concept properties to an other one, without modifying target instance identity.
	 * 
	 * @param source
	 *        the concept with properties to copy
	 * @param target
	 *        the concept to clear with source properties
	 */
	public static void copyConceptProperties(Concept source, Concept target) {
		
		target.setKeyId(source.getKeyId());
		target.setOntology(source.getOntology());
		target.setSuperConcept(source.getSuperConcept());
		target.setName(source.getName());
		target.setBounds(source.getBounds());
		target.setOntologyId(source.getOntologyId());
		target.setSuperConceptId(source.getSuperConceptId());
		target.setAction(source.isAction());
		
		target.getAttributes().clear();
		target.getClasses().clear();
		target.getLinks().clear();
		target.getSubConcepts().clear();

		target.getAttributes().addAll(source.getAttributes());
		target.getClasses().addAll(source.getClasses());
		target.getLinks().addAll(source.getLinks());
		target.getSubConcepts().addAll(source.getSubConcepts());
	}

	/**
	 * Create a new concept link and update the ontology.
	 * 
	 * @param ontology
	 *        the ontology to update
	 * @param source
	 *        the source concept
	 * @param target
	 *        the target concept
	 * @return
	 *         the new link created
	 */
	public static ConceptLink createConceptLink(Ontology ontology, Concept source, Concept target) {

		ConceptLink link = new ConceptLink();
		link.setSourceConcept(source);
		link.setTargetConcept(target);
		source.getLinks().add(link);

		return link;
	}

	/**
	 * Remove a concept from an ontology.
	 * 
	 * @param ontology
	 *        the ontology owning the concept
	 * @param concept
	 *        the concept to remove
	 */
	public static void removeConcept(Ontology ontology, Concept concept) {

		List<ConceptLink> linksToRemove = new ArrayList<>();

		// First select all concepts linked to the current one
		for (Concept c : ontology.getConcepts()) {
			for (ConceptLink link : c.getLinks()) {
				if (link.getTargetConcept() == concept) {
					linksToRemove.add(link);
				}
			}
		}

		// Now delete all marked links
		for (ConceptLink link : linksToRemove) {
			link.getSourceConcept().getLinks().remove(link);
		}

		// Finally remove the concept itself from ontology
		ontology.getConcepts().remove(concept);
	}

	/**
	 * Remove a link starting from a concept.
	 * 
	 * @param ontology
	 *        the ontology owning the concept
	 * @param concept
	 *        the owning concept
	 * @param link
	 *        the link to remove from ontology
	 */
	public static void removeConceptLink(Ontology ontology, Concept concept, ConceptLink link) {
		concept.getLinks().remove(link);
	}

	/**
	 * Delete all concepts owned by an ontology.
	 * 
	 * @param ontology
	 *        the ontology
	 */
	private static void deleteDependencies(Ontology ontology) {

		for (Concept concept : ConceptDao.findByOntology(ontology)) {
			ConceptLinkDao.deleteByConcept(concept);
			ConceptAttributeDao.deleteByConcept(concept);
			ConceptClassDao.deleteByConcept(concept);
			ConceptDao.delete(concept);
		}
	}

	/**
	 * Delete a single ontology and its dependencies
	 * 
	 * @param ontology
	 *        the ontology to delete
	 */
	public static void deleteOntology(Ontology ontology) {
		deleteDependencies(ontology);
		OntologyDao.delete(ontology);
	}

	/**
	 * Delete all ontologies owned by a project.
	 * 
	 * @param project
	 *        the project owning the ontologies
	 */
	public static void deleteOntologies(Project project) {
		
		for (Ontology ontology : project.getOntologies()) {
			deleteOntology(ontology);
		}
	}

	/**
	 * Save all ontologies without their concepts.
	 * 
	 * @param ontologies
	 *        the ontology list to save
	 */
	public static void saveOntologies(List<Ontology> ontologies) {

		for (Ontology ontology : ontologies) {

			if (PersistanceUtils.isNewObject(ontology)) {
				OntologyDao.add(ontology);
			}
			else {
				OntologyDao.update(ontology);
			}
		}
	}

	/**
	 * Save an ontology and its concepts.
	 * 
	 * @param ontology
	 *        the ontology to save
	 */
	public static void saveOntology(Ontology ontology) {
		
		// Save the ontology
		if (PersistanceUtils.isNewObject(ontology)) {
			OntologyDao.add(ontology);
		}
		else {
			OntologyDao.update(ontology);
		}

		// Delete current dependencies
		deleteDependencies(ontology);

		// Now, save all concepts and their attributes
		for (Concept concept : ontology.getConcepts()) {

			concept.setOntologyId(ontology.getKeyId());

			// Save the concept
			ConceptDao.add(concept);

			// Save its attributes
			for (ConceptAttribute conceptAttribute : concept.getAttributes()) {
				conceptAttribute.setConceptId(concept.getKeyId());
				ConceptAttributeDao.add(conceptAttribute);
			}

			// Save its associated classes
			for (ConceptClass conceptClass : concept.getClasses()) {
				conceptClass.setConceptId(concept.getKeyId());
				ConceptClassDao.add(conceptClass);
			}
		}

		// Finally save all links between concepts
		for (Concept concept : ontology.getConcepts()) {
			for (ConceptLink link : concept.getLinks()) {
				link.setSourceId(concept.getKeyId());
				link.setTargetId(link.getTargetConcept().getKeyId());
				ConceptLinkDao.add(link);
			}
		}
	}	
	
	/**
	 * Export an ontology as a Turtle owl file.
	 * 
	 * @param ontology
	 *        the ontology to export
	 * @param filename
	 *        the name of the turtle file
	 */
	public static void exportOntology(Ontology ontology, String filename) {
		
		if (ontology != null && filename != null) {

			FileWriter writer = null;

			try {

				String ontologyUrl = "http://hesge.ch/project/csim2/" + ontology.getName().toLowerCase();

				// Delete file, exists
				if (FileUtils.exists(filename)) {
					Files.delete(Paths.get(filename));
				}
				
				// Create a new file
				writer = new FileWriter(filename);
				
				// Generate file content
				writer.append("@prefix owl: <http://www.w3.org/2002/07/owl#>.\n");
				writer.append("@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>.\n");
				writer.append("@prefix xml: <http://www.w3.org/XML/1998/namespace>.\n");
				writer.append("@prefix xsd: <http://www.w3.org/2001/XMLSchema#>.\n");
				writer.append("@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>.\n");
				writer.append("@prefix : <http://hesge.ch/project/csim2#>.\n");
				writer.append("@base <http://hesge.ch/project/csim2#>.\n");

				writer.append("\n#\n");
				writer.append("# Classes\n");
				writer.append("#\n");
				
				for (Concept concept : ontology.getConcepts()) {
					
					writer.append(":Class" + concept.getKeyId() + " a owl:Class; rdfs:label \"" + concept.getName() + "\".\n");

					// Create an attribute for visual concept bounds
					writer.append("   :attributeName" + concept.getKeyId() + " a owl:DatatypeProperty;\n");
					writer.append("      rdfs:domain :Class" + concept.getKeyId() + ";\n");
					writer.append("      rdfs:label \"" + concept.getName() + "Location\";\n");
					writer.append("      rdfs:label \"" + concept.getBounds().x + "," + concept.getBounds().y + "," + concept.getBounds().width + "," + concept.getBounds().height + "\"@ie.\n");

					// Save all classes
					for (ConceptAttribute conceptAttribute : concept.getAttributes()) {
						
						writer.append("   :attributeName" + conceptAttribute.getKeyId() + " a owl:DatatypeProperty;\n");
						writer.append("      rdfs:domain :Class" + concept.getKeyId() + ";\n");
						writer.append("      rdfs:label \"" + conceptAttribute.getName() + "\";\n");
						writer.append("      rdfs:label \"" + conceptAttribute.getIdentifier() + "\"@ie.\n");
					}

					// Save all attributes
					for (ConceptClass conceptClass : concept.getClasses()) {

						writer.append("   :attributeClass" + conceptClass.getKeyId() + " a owl:DatatypeProperty;\n");
						writer.append("      rdfs:domain :Class" + concept.getKeyId() + ";\n");
						writer.append("      rdfs:label \"" + conceptClass.getName() + "\";\n");
						writer.append("      rdfs:label \"" + conceptClass.getIdentifier() + "\"@ie.\n");
					}
					
					writer.append("\n");
				}
				
				writer.append("#\n");
				writer.append("# Relations\n");
				writer.append("#\n");
				
				int linkCount = 0;

				// Save all links
				for (Concept concept : ontology.getConcepts()) {
					for (ConceptLink conceptLink : concept.getLinks()) {

						writer.append(":relation" + linkCount + " a owl:ObjectProperty;\n");
						writer.append("   rdfs:label \"" + conceptLink.getQualifier() + "\";\n");
						writer.append("   rdfs:domain :Class" + conceptLink.getSourceId() + ";\n");
						writer.append("   rdfs:range :Class" + conceptLink.getTargetId() + ".\n");
						
						linkCount++;
					}
				}
				
				writer.append("#\n");
				writer.flush();
				writer.close();
			}
			catch(Exception e)  {
				
				if (writer != null) {
					try {
						writer.flush();
						writer.close();
					}
					catch (IOException e1) {
						// Close silently
					}
				}
				
				Console.writeError(ApplicationLogic.class, "an unexpected error has occured: " + StringUtils.toString(e));
			}
		}
	}
	
	/**
	 * Import an ontology from a Turtle file.
	 * 
	 * @param ontology
	 *        the ontology to populate with file content
	 * @param filename
	 *        the name of the turtle file
	 */
	public static void importOntology(Ontology ontology, String filename) {
		
		if (ontology != null && filename != null) {

			FileReader reader = null;

			try {
			

				// Create a parser for the turtle file
				reader = new FileReader(filename);
				TurtleParser parser = new TurtleParser(reader);

				// Register the visitor
				parser.setEventHandler(new TurtleEventHandler() {
					
					@Override
					public void triple(int line, int col, Triple triple) {

						//Check it's valid triple.
				        Node subject   = triple.getSubject() ;
				        Node predicate = triple.getPredicate() ;
				        Node object    = triple.getObject() ;
				        
				        /*
				        if ( ! ( s.isURI() || s.isBlank() ) )
				            throw new TurtleParseException("["+line+", "+col+"] : Error: Subject is not a URI or blank node") ;
				        if ( ! p.isURI() )
				            throw new TurtleParseException("["+line+", "+col+"] : Error: Predicate is not a URI") ;
				        if ( ! ( o.isURI() || o.isBlank() || o.isLiteral() ) ) 
				            throw new TurtleParseException("["+line+", "+col+"] : Error: Object is not a URI, blank node or literal") ;
				        */
				        				        
				        outputNode(subject) ;
				        System.out.print(" ");
				        outputNode(predicate) ;
				        System.out.print(" ");
				        outputNode(object) ;
				        System.out.print(" .");
				        System.out.println() ;
				        System.out.flush() ;
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
					
					private void outputNode(Node node)
				    {
				        if ( node.isURI() ) 
				        { 
				            System.out.print("<") ;
				            System.out.print(node.getURI()) ;
				            System.out.print(">") ;
				            return ; 
				        }
				        if ( node.isBlank() )
				        {
				        	System.out.print("_:") ;
				        	System.out.print(node.getBlankNodeLabel()) ;
				            return ;
				        }
				        if ( node.isLiteral() )
				        {
				        	System.out.print('"') ;
				        	outputEsc(node.getLiteralLexicalForm()) ;
				        	System.out.print('"') ;

				            if ( node.getLiteralLanguage() != null && node.getLiteralLanguage().length()>0)
				            {
				            	System.out.print('@') ;
				            	System.out.print(node.getLiteralLanguage()) ;
				            }

				            if ( node.getLiteralDatatypeURI() != null )
				            {
				            	System.out.print("^^<") ;
				            	System.out.print(node.getLiteralDatatypeURI()) ;
				            	System.out.print(">") ;
				            }
				            return ; 
				        }
				        System.err.println("Illegal node: "+node) ;
				    }
					
					public  void outputEsc(String s)
				    {
				        int len = s.length() ;
				        for (int i = 0; i < len; i++) {
				            char c = s.charAt(i);
				            
				            // Escape escapes and quotes
				            if (c == '\\' || c == '"' ) 
				            {
				                System.out.print('\\') ;
				                System.out.print(c) ;
				            }
				            else if (c == '\n') System.out.print("\\n");
				            else if (c == '\t') System.out.print("\\t");
				            else if (c == '\r') System.out.print("\\r");
				            else if (c == '\f') System.out.print("\\f");
				            else if ( c >= 32 && c < 127 )
				            	System.out.print(c);
				            else
				            {
				                // Unsubtle.  Does not cover beyond 16 bits codepoints 
				                // which Java keeps as surrogate pairs and wil print as two \ u escapes. 
				                String hexstr = Integer.toHexString(c).toUpperCase();
				                int pad = 4 - hexstr.length();
				                System.out.print("\\u");
				                for (; pad > 0; pad--)
				                	System.out.print("0");
				                System.out.print(hexstr);
				            }
				        }
				    }					
					
				});
				
			    parser.parse();
			    reader.close();
			}
			catch (Exception e) {
				
				if (reader != null) {
					try {
						reader.close();
					}
					catch (IOException e1) {
						// Close silently
					}
				}
				
				Console.writeError(ApplicationLogic.class, "an unexpected error has occured: " + StringUtils.toString(e));
			}
		}
	}
}
