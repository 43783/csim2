/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hesge.csim2.core.logic;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
import ch.hesge.csim2.core.utils.DaoUtils;
import ch.hesge.csim2.core.utils.FileUtils;
import ch.hesge.csim2.core.utils.ObjectSorter;
import ch.hesge.csim2.core.utils.StringUtils;
import ch.hesge.csim2.core.utils.TurtleConverter;

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
	 * Retrieve a map of concepts owned by a project
	 * with each entries of the form (keyId, Concept) map.
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
			
			// Populate attributes
			concept.getAttributes().addAll(ConceptAttributeDao.findByConcept(concept));
			ObjectSorter.sortConceptAttributes(concept.getAttributes());

			// Populate concept classes
			concept.getClasses().addAll(ConceptClassDao.findByConcept(concept));
			ObjectSorter.sortConceptClasses(concept.getClasses());
			
			// Populate links between concepts
			for (ConceptLink link : ConceptLinkDao.findByConcept(concept)) {

				// Update concept with instances
				link.setSourceConcept(concept);
				link.setTargetConcept(conceptMap.get(link.getTargetId()));

				// Add the link to the concept
				concept.getLinks().add(link);

				// Detect concept hierarchy
				if (link.getQualifier() != null && link.getTargetConcept() != null) {
					concept.setSuperConcept(link.getTargetConcept());
					link.getTargetConcept().getSubConcepts().add(concept);
					ObjectSorter.sortConcepts(link.getTargetConcept().getSubConcepts());
				}
				
				// Detect concept hierarchy
				if (link.getQualifier() != null && link.getTargetConcept() != null && link.getQualifier().equals("subclass-of")) {
					concept.setSuperConcept(link.getTargetConcept());
					link.getTargetConcept().getSubConcepts().add(concept);
					ObjectSorter.sortConcepts(link.getTargetConcept().getSubConcepts());
				}
			}
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
			
			// Populate attributes
			concept.getAttributes().addAll(ConceptAttributeDao.findByConcept(concept));
			ObjectSorter.sortConceptAttributes(concept.getAttributes());

			// Populate concept classes
			concept.getClasses().addAll(ConceptClassDao.findByConcept(concept));
			ObjectSorter.sortConceptClasses(concept.getClasses());
			
			// Populate links between concepts
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

		return conceptMap;
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

		// Now remove all marked links
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
	 * Delete a single ontology and its dependencies
	 * 
	 * @param ontology
	 *        the ontology to delete
	 */
	public static void deleteOntology(Ontology ontology) {
		
		// First delete all current concepts
		for (Concept concept : ConceptDao.findByOntology(ontology)) {
			ConceptLinkDao.deleteByConcept(concept);
			ConceptAttributeDao.deleteByConcept(concept);
			ConceptClassDao.deleteByConcept(concept);
			ConceptDao.delete(concept);
		}

		// Then delete the ontology
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
	 * Save an ontology and its concepts.
	 * 
	 * @param ontology
	 *        the ontology to save
	 */
	public static void saveOntology(Ontology ontology) {
		
		// First delete all current concepts
		for (Concept concept : ConceptDao.findByOntology(ontology)) {
			ConceptLinkDao.deleteByConcept(concept);
			ConceptAttributeDao.deleteByConcept(concept);
			ConceptClassDao.deleteByConcept(concept);
			ConceptDao.delete(concept);
		}

		// Save the ontology
		if (DaoUtils.isNewObject(ontology)) {
			OntologyDao.add(ontology);
		}
		else {
			OntologyDao.update(ontology);
		}

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

			FileOutputStream fileStream = null;

			try {

				// Delete file, exists
				if (FileUtils.exists(filename)) {
					Files.delete(Paths.get(filename));
				}
				
				// Create a file writer (UTF8 support)
				fileStream = new FileOutputStream(new File(filename));
				OutputStreamWriter writer = new OutputStreamWriter(fileStream, "UTF-8");
				
				// Generate the turtle file
				TurtleConverter emitter = new TurtleConverter(true);
				emitter.generate(writer, ontology.getConcepts());

				writer.flush();
				writer.close();
			}
			catch(Exception e)  {
				
				if (fileStream != null) {
					try {
						fileStream.flush();
						fileStream.close();
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

			FileInputStream fileStream = null;

			try {

				// Create a file reader (UTF8 support)
				fileStream = new FileInputStream(new File(filename));
				InputStreamReader reader = new InputStreamReader(fileStream, "UTF-8");
				
				// Parse the turtle file
				TurtleConverter parser = new TurtleConverter(true);
			    parser.parse(reader);
			    reader.close();
			    			    
			    // Retrieve all concepts found
			    List<Concept> concepts = parser.getConcepts();
			    
			    // Now save the ontology with the concepts
			    ontology.getConcepts().clear();
			    ontology.getConcepts().addAll(concepts);
			    saveOntology(ontology);
			}
			catch (Throwable t) {
				
				if (fileStream != null) {
					try {
						fileStream.close();
					}
					catch (IOException e1) {
						// Close silently
					}
				}
				
				Console.writeError(ApplicationLogic.class, "an unexpected error has occured: " + StringUtils.toString(t));
			}
		}
	}
}
