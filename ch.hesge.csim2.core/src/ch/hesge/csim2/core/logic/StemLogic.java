/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hesge.csim2.core.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.hesge.csim2.core.dao.StemConceptDao;
import ch.hesge.csim2.core.dao.StemMethodDao;
import ch.hesge.csim2.core.model.Ontology;
import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.StemConcept;
import ch.hesge.csim2.core.model.StemConceptType;
import ch.hesge.csim2.core.model.StemMethod;
import ch.hesge.csim2.core.persistence.PersistanceUtils;
import ch.hesge.csim2.core.utils.ObjectSorter;

/**
 * This class implement all logical rules globally associated to stem methods.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

class StemLogic {

	/**
	 * Retrieve a map of all stem concepts classified by concept.
	 * 
	 * @param project
	 *        the owner
	 * @return
	 *         the map of (conceptId, StemConcept)
	 */
	public static Map<Integer, StemConcept> getStemConceptMap(Project project) {

		Map<Integer, StemConcept> stemMap = new HashMap<>();

		for (StemConcept stem : StemConceptDao.findByProject(project)) {
			stemMap.put(stem.getKeyId(), stem);
		}

		return stemMap;
	}

	/**
	 * Retrieve a hierarchy of stem concepts defined for a project.
	 * 
	 * More specifically allows one stem hierarchy to be retrieved for a
	 * specific concept. So entries are of the form (conceptId, root of
	 * StemConcept tree).
	 * 
	 * @param project
	 *        the owner
	 * @return
	 *         the map of (conceptId, StemConcept)
	 */
	public static Map<Integer, StemConcept> getStemConceptTreeMap(Project project) {

		Map<Integer, StemConcept> stemConceptTree = new HashMap<>();

		// First retrieve all stem concepts classified by concept 
		Map<Integer, StemConcept> stemMap = ApplicationLogic.getStemConceptMap(project);

		// Loop over all stems
		for (StemConcept stem : stemMap.values()) {

			// If stem has a parent, just update stem hierarchy
			if (stemMap.containsKey(stem.getParentId())) {

				StemConcept parent = stemMap.get(stem.getParentId());

				if (parent.getStemType() == StemConceptType.CONCEPT_NAME_FULL) {
					stemConceptTree.put(stem.getConceptId(), stem);
				}
				else if (stem.getStemType() == StemConceptType.CONCEPT_NAME_PART) {
					parent.getParts().add(stem);
					ObjectSorter.sortStemConcepts(parent.getParts());
				}
				else if (stem.getStemType() == StemConceptType.ATTRIBUTE_NAME_FULL) {
					parent.getAttributes().add(stem);
					ObjectSorter.sortStemConcepts(parent.getAttributes());
				}
				else if (stem.getStemType() == StemConceptType.ATTRIBUTE_NAME_PART) {
					parent.getParts().add(stem);
					ObjectSorter.sortStemConcepts(parent.getParts());
				}
				else if (stem.getStemType() == StemConceptType.ATTRIBUTE_IDENTIFIER_FULL) {
					parent.getAttributeIdentifiers().add(stem);
					ObjectSorter.sortStemConcepts(parent.getAttributeIdentifiers());
				}
				else if (stem.getStemType() == StemConceptType.ATTRIBUTE_IDENTIFIER_PART) {
					parent.getParts().add(stem);
					ObjectSorter.sortStemConcepts(parent.getParts());
				}
				else if (stem.getStemType() == StemConceptType.CLASS_NAME_FULL) {
					parent.getClasses().add(stem);
					ObjectSorter.sortStemConcepts(parent.getClasses());
				}
				else if (stem.getStemType() == StemConceptType.CLASS_NAME_PART) {
					parent.getParts().add(stem);
					ObjectSorter.sortStemConcepts(parent.getParts());
				}
				else if (stem.getStemType() == StemConceptType.CLASS_IDENTIFIER_FULL) {
					parent.getClassIdentifiers().add(stem);
					ObjectSorter.sortStemConcepts(parent.getClassIdentifiers());
				}
				else if (stem.getStemType() == StemConceptType.CLASS_IDENTIFIER_PART) {
					parent.getParts().add(stem);
					ObjectSorter.sortStemConcepts(parent.getParts());
				}
			}
		}

		return stemConceptTree;
	}

	/**
	 * Serialize stem concept tree into a single flat list of stem concepts.
	 * 
	 * @param rootStem
	 *        the root stem of a stem tree
	 * 
	 * @return
	 *         a flat list of stem concepts
	 */
	public static List<StemConcept> getStemConceptList(StemConcept rootStem) {

		List<StemConcept> flatList = new ArrayList<>();

		if (rootStem != null) {

			flatList.add(rootStem);
			flatList.addAll(rootStem.getParts());

			for (StemConcept attrStem : rootStem.getAttributes()) {

				flatList.add(attrStem);
				flatList.addAll(attrStem.getParts());

				for (StemConcept identifierStem : attrStem.getAttributeIdentifiers()) {
					flatList.add(identifierStem);
					flatList.addAll(identifierStem.getParts());
				}
			}

			for (StemConcept classStem : rootStem.getClasses()) {

				flatList.add(classStem);
				flatList.addAll(classStem.getParts());

				for (StemConcept identifierStem : classStem.getClassIdentifiers()) {
					flatList.add(identifierStem);
					flatList.addAll(identifierStem.getParts());
				}
			}
		}

		return flatList;
	}

	/**
	 * Retrieve a map of all StemConcepts in project, classified by term.
	 * Each entry will be of the form (term, List<StemConcept>).
	 * 
	 * @param project
	 *        the project owning stems
	 * 
	 * @return
	 *         a map of stems
	 */
	public static Map<String, List<StemConcept>> getStemConceptByTermMap(Project project) {

		Map<String, List<StemConcept>> stemMap = new HashMap<>();

		Map<Integer, StemConcept> stemTreeMap = ApplicationLogic.getStemConceptTreeMap(project);

		// Populate map
		for (StemConcept rootStem : stemTreeMap.values()) {

			// Get all stem is hierarchy
			List<StemConcept> stems = ApplicationLogic.getStemConceptList(rootStem);

			for (StemConcept stem : stems) {

				if (!stemMap.containsKey(stem.getTerm())) {
					stemMap.put(stem.getTerm(), new ArrayList<>());
				}

				stemMap.get(stem.getTerm()).add(stem);
			}
		}

		return stemMap;
	}

	/**
	 * Retrieve all stem methods associated to a project.
	 * 
	 * @param project
	 *        the project owning stems
	 * 
	 * @return
	 *         a list of stem
	 */
	public static List<StemMethod> getStemMethods(Project project) {
		return StemMethodDao.findByProject(project);
	}

	/**
	 * Retrieve all stem methods owned by a project
	 * as a (stemId, StemMethod) map.
	 * 
	 * @param project
	 *        the owner
	 * 
	 * @return
	 *         a map of stem method
	 */
	public static Map<Integer, StemMethod> getStemMethodMap(Project project) {

		Map<Integer, StemMethod> stemMethodMap = new HashMap<>();
		List<StemMethod> stemMethods = ApplicationLogic.getStemMethods(project);

		// Populate the stem concept map
		for (StemMethod stemMethod : stemMethods) {
			stemMethodMap.put(stemMethod.getKeyId(), stemMethod);
		}

		return stemMethodMap;
	}

	/**
	 * Retrieve a hierarchy of stem methods defined for a project.
	 * 
	 * More specifically allows one stem hierarchy to be retrieved for a
	 * specific method. So entries are of the form (methodId, root of
	 * StemMethod tree).
	 * 
	 * @param project
	 *        the owner
	 * 
	 * @return
	 *         the map of (methodId, StemMethod)
	 */
	public static Map<Integer, StemMethod> getStemMethodTree(Project project) {

		Map<Integer, StemMethod> stemMethodTree = new HashMap<>();
		Map<Integer, StemMethod> stemMethodMap = ApplicationLogic.getStemMethodMap(project);

		for (StemMethod stemMethod : stemMethodMap.values()) {

			// If stem has a parent, just update stem hierarchy
			if (stemMethodMap.containsKey(stemMethod.getParentId())) {
				StemMethod parent = stemMethodMap.get(stemMethod.getParentId());
				parent.getChildren().add(stemMethod);
				ObjectSorter.sortStemMethods(parent.getChildren());
			}
			else {
				// Otherwise it is a root stem
				stemMethodTree.put(stemMethod.getSourceMethodId(), stemMethod);
			}
		}

		return stemMethodTree;
	}

	/**
	 * Retrieve a map of all StemMethods in project, classified by term.
	 * Each entry will be of the form (term, List<StemMethods>).
	 * 
	 * @param project
	 *        the project owning stems
	 * 
	 * @return
	 *         a map of stems
	 */
	public static Map<String, List<StemMethod>> getStemMethodByTermMap(Project project) {

		Map<String, List<StemMethod>> stemMethodMap = new HashMap<>();

		// Populate concept stems
		for (StemMethod stem : ApplicationLogic.getStemMethods(project)) {

			if (!stemMethodMap.containsKey(stem.getTerm())) {
				stemMethodMap.put(stem.getTerm(), new ArrayList<>());
			}

			stemMethodMap.get(stem.getTerm()).add(stem);
		}

		return stemMethodMap;
	}

	/**
	 * Delete all stem concepts associated to an ontology.
	 * 
	 * @param ontology
	 *        the ontology owning stems to delete
	 */
	public static void deleteStemConcepts(Ontology ontology) {
		StemConceptDao.deleteByOntology(ontology);
	}

	/**
	 * Delete all stems methods associated to a project.
	 * 
	 * @param project
	 *        the project owning stems to delete
	 */
	public static void deleteStemMethods(Project project) {
		StemMethodDao.deleteByProject(project);
	}

	/**
	 * Save a single stem concept.
	 * 
	 * @param stem
	 *        the StemConcept to save
	 */
	public static void saveStemConcept(StemConcept stem) {
		if (PersistanceUtils.isNewObject(stem)) {
			StemConceptDao.add(stem);
		}
		else {
			StemConceptDao.update(stem);
		}
	}

	/**
	 * Save a single stem method.
	 * 
	 * @param stem
	 *        the StemMethod to save
	 */
	public static void saveStemMethod(StemMethod stem) {
		if (PersistanceUtils.isNewObject(stem)) {
			StemMethodDao.add(stem);
		}
		else {
			StemMethodDao.update(stem);
		}
	}
}
