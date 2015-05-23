/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hesge.csim2.core.logic;

import java.awt.Rectangle;
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
import ch.hesge.csim2.core.persistence.PersistanceUtils;
import ch.hesge.csim2.core.utils.ObjectSorter;

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
		return ConceptDao.findByProject(project);
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
		return ConceptDao.findByOntology(ontology);
	}

	/**
	 * Retrieve all concepts owned by a project with the following
	 * dependencies:
	 * 
	 * - its concept attributes
	 * - its concept classes
	 * - its links
	 * - its superconcept
	 * - its children concept
	 * 
	 * @param ontology
	 *        the owner
	 * 
	 * @return
	 *         the list of concept
	 */
	public static List<Concept> getConceptsWithDependencies(Project project) {

		List<Concept> concepts = ApplicationLogic.getConcepts(project);

		// Create a map of concepts with identical instances
		Map<Integer, Concept> conceptMap = new HashMap<>();
		for (Concept concept : concepts) {
			conceptMap.put(concept.getKeyId(), concept);
		}

		for (Concept concept : concepts) {
			populateDependencies(concept, conceptMap);
		}

		return concepts;
	}

	/**
	 * Retrieve all concepts owned by an ontology with the following
	 * dependencies:
	 * 
	 * - its concept attributes
	 * - its concept classes
	 * - its links
	 * - its superconcept
	 * - its children concept
	 * 
	 * @param ontology
	 *        the owner
	 * 
	 * @return
	 *         the list of concept
	 */
	public static List<Concept> getConceptsWithDependencies(Ontology ontology) {

		List<Concept> concepts = ApplicationLogic.getConcepts(ontology);

		// Create a map of concepts with identical instances
		Map<Integer, Concept> conceptMap = new HashMap<>();
		for (Concept concept : concepts) {
			conceptMap.put(concept.getKeyId(), concept);
		}

		// Populate each concept with its dependencies
		for (Concept concept : concepts) {
			populateDependencies(concept, conceptMap);
		}

		return concepts;
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
			if (link.getQualifier().equals("subclass-of") && link.getTargetConcept() != null) {
				concept.setSuperConcept(link.getTargetConcept());
				link.getTargetConcept().getSubConcepts().add(concept);
				ObjectSorter.sortConcepts(link.getTargetConcept().getSubConcepts());
			}
		}
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
		List<Concept> concepts = ApplicationLogic.getConcepts(project);

		// Populate the concept map
		for (Concept concept : concepts) {
			conceptMap.put(concept.getKeyId(), concept);
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
		List<Concept> concepts = ApplicationLogic.getConcepts(ontology);

		// Populate the concept map
		for (Concept concept : concepts) {
			conceptMap.put(concept.getKeyId(), concept);
		}

		return conceptMap;
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

		concept.setBounds(new Rectangle(0, 0, 1, 1));
		concept.setName("Concept");
		ontology.getConcepts().add(concept);

		return concept;
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
	public static void deleteConcepts(Ontology ontology) {

		for (Concept concept : ConceptDao.findByOntology(ontology)) {
			ConceptLinkDao.deleteByConcept(concept);
			ConceptAttributeDao.deleteByConcept(concept);
			ConceptClassDao.deleteByConcept(concept);
			ConceptDao.delete(concept);
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

		// Now, save all concepts and their attributes
		for (Concept concept : ontology.getConcepts()) {

			concept.setOntologyId(ontology.getKeyId());

			// Save the concept
			if (PersistanceUtils.isNewObject(concept)) {
				ConceptDao.add(concept);
			}
			else {
				ConceptDao.update(concept);
			}

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
}
