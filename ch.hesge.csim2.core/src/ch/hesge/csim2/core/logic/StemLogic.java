/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hesge.csim2.core.logic;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;

import ch.hesge.csim2.core.dao.StemConceptDao;
import ch.hesge.csim2.core.dao.StemMethodDao;
import ch.hesge.csim2.core.model.Ontology;
import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.StemConcept;
import ch.hesge.csim2.core.model.StemConceptType;
import ch.hesge.csim2.core.model.StemMethod;
import ch.hesge.csim2.core.model.StemMethodType;
import ch.hesge.csim2.core.utils.ObjectSorter;
import ch.hesge.csim2.core.utils.PersistanceUtils;
import ch.hesge.csim2.core.utils.StringUtils;

/**
 * This class implement all logical rules globally associated to stem methods.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

class StemLogic {

	/**
	 * Retrieve all stems associated to a name.
	 * Words present in rejectedList will not produce associated stems.
	 * 
	 * @param name
	 *        the name to use to extract stems
	 * @param rejectedList
	 *        the list of forbidden words
	 * @return
	 *         a list of stems associated to the list of names
	 */
	public static List<String> getStems(String name, List<String> rejectedList) {
		
		List<String> stems = new ArrayList<>();

		if (name.isEmpty() || name.startsWith("_")) return stems;
		
		// Clean name before stemmisation 
		String cleanName = Normalizer.normalize(name, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", ""); // remove accentuated chars
		cleanName = cleanName.replaceAll("\\s+", " "); // remove multiple consecutive spaces
		cleanName = cleanName.replaceAll("\\[.*\\]|\\{.*\\}|\\(.*\\)", ""); // remove [, ], {, }, (, )
		cleanName = cleanName.replaceAll("[^_\\-\\s\\sA-Za-z0-9]", ""); // remove non alphabetics chars
		cleanName = cleanName.trim(); // trim left/right space
		cleanName = StringUtils.trimHungarian(cleanName); // remove lpsz, sz, etc...
		
		if (cleanName.length() > 0) {
			
			List<String> nameParts = new ArrayList<>();

			// Split name parts (camel casing notation) 
			List<String> words = StringUtils.splitCamelCase(cleanName);
			
			// Skip forbidden names
			if (rejectedList != null && rejectedList.contains(StringUtils.concatenate(words).toLowerCase())) {
				return stems;
			}
			
			// Skip part name included in rejection list
			for (String word : words) {

				if (word != null && word.length() > 1) {

					word = word.toLowerCase();
					
					// Add only words not in reject list and not already present
					if (!stems.contains(word) && (rejectedList == null || !rejectedList.contains(word))) {
						nameParts.add(word);
					}
				}
			}
			
			// Finally stemmize all name parts found
			SnowballStemmer stemmer = new englishStemmer();
			for (String word : nameParts) {

				stemmer.setCurrent(word);
				stemmer.stem();
				stems.add(stemmer.getCurrent().toLowerCase());
			}
		}
		
		return stems;
	}			
	/**
	 * <pre>
	 * Retrieve a hierarchy of stem concepts defined for a project.
	 * 
	 * More specifically allows one stem hierarchy to be retrieved for a
	 * specific concept.
	 * 
	 * For instance:
	 * 
	 * 		StemTree for a single concept:
	 * 
	 * 		CONCEPT_NAME_FULL
	 * 			CONCEPT_NAME_PART
	 * 			CONCEPT_NAME_PART
	 * 
	 * 			ATTRIBUTE_NAME_FULL
	 * 				ATTRIBUTE_NAME_PART
	 * 				ATTRIBUTE_NAME_PART
	 * 
	 * 				ATTRIBUTE_IDENTIFIER_FULL
	 * 					ATTRIBUTE_IDENTIFIER_PART
	 * 					ATTRIBUTE_IDENTIFIER_PART
	 * 
	 * 			CLASS_NAME_FULL
	 * 				CLASS_NAME_PART
	 * 				CLASS_NAME_PART
	 * 
	 * 				CLASS_IDENTIFIER_FULL
	 * 					CLASS_IDENTIFIER_PART
	 * 					CLASS_IDENTIFIER_PART
	 * 
	 * So entries are of the form (conceptId, root of StemConcept tree).
	 * 
	 * </pre>
	 * 
	 * @param project
	 *        the owner
	 * @return
	 *         the map of (conceptId, StemConcept)
	 */
	public static Map<Integer, StemConcept> getStemConceptTreeMap(Project project) {

		Map<Integer, StemConcept> stemConceptTree = new HashMap<>();

		// Create a map of all stems 
		Map<Integer, StemConcept> stemMap = new HashMap<>();
		for (StemConcept stem : StemConceptDao.findByProject(project)) {
			stemMap.put(stem.getKeyId(), stem);
		}

		// Loop over all stems
		for (StemConcept stem : stemMap.values()) {

			// Retrieve stem parent
			StemConcept parent = stemMap.get(stem.getParentId()); 
			stem.setParent(parent);

			if (stem.getStemType() == StemConceptType.CONCEPT_NAME_FULL) {
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

		return stemConceptTree;
	}

	/**
	 * Serialize a stem concept tree into a single flat list of stem children.
	 * 
	 * @param rootStem
	 *        the root stem of a stem tree
	 * 
	 * @return
	 *         a flat list of stem concepts
	 */
	public static List<StemConcept> inflateStemConcepts(StemConcept rootStem) {

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
			List<StemConcept> stems = ApplicationLogic.inflateStemConcepts(rootStem);

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
	 * <pre>
	 * Retrieve a hierarchy of stem methods defined for a project.
	 * 
	 * More specifically allows one stem hierarchy to be retrieved for a
	 * specific method.
	 * 
	 * For instance:
	 * 
	 * 		StemTree for a single method:
	 * 
	 * 		METHOD_NAME_FULL
	 * 			METHOD_NAME_PART
	 * 			METHOD_NAME_PART
	 * 
	 * 			PARAMETER_NAME_FULL
	 * 				PARAMETER_NAME_PART
	 * 				PARAMETER_NAME_PART
	 * 
	 * 				PARAMETER_TYPE_FULL
	 * 					PARAMETER_TYPE_PART
	 * 					PARAMETER_TYPE_PART
	 * 
	 * 			REFERENCE_NAME_FULL
	 * 				REFERENCE_NAME_PART
	 * 				REFERENCE_NAME_PART
	 * 
	 * 				REFERENCE_TYPE_FULL
	 * 					REFERENCE_TYPE_PART
	 * 					REFERENCE_TYPE_PART
	 * 
	 * So entries are of the form (methodId, root of StemMethod tree).
	 * 
	 * </pre>
	 * 
	 * @param project
	 *        the owner
	 * @return
	 *         the map of (methodId, StemConcept)
	 */
	public static Map<Integer, StemMethod> getStemMethodTreeMap(Project project) {

		Map<Integer, StemMethod> stemMethodTree = new HashMap<>();

		// Create a map of all stems
		Map<Integer, StemMethod> stemMap = new HashMap<>();
		for (StemMethod stem : StemMethodDao.findByProject(project)) {
			stemMap.put(stem.getKeyId(), stem);
		}

		// Loop over all stems
		for (StemMethod stem : stemMap.values()) {

			StemMethod parent = stemMap.get(stem.getParentId());

			if (stem.getStemType() == StemMethodType.METHOD_NAME_FULL) {
				stemMethodTree.put(stem.getSourceMethodId(), stem);
			}
			else if (stem.getStemType() == StemMethodType.METHOD_NAME_PART) {
				parent.getParts().add(stem);
				ObjectSorter.sortStemMethods(parent.getParts());
			}
			else if (stem.getStemType() == StemMethodType.PARAMETER_NAME_FULL) {
				parent.getParameters().add(stem);
				ObjectSorter.sortStemMethods(parent.getParameters());
			}
			else if (stem.getStemType() == StemMethodType.PARAMETER_NAME_PART) {
				parent.getParts().add(stem);
				ObjectSorter.sortStemMethods(parent.getParts());
			}
			else if (stem.getStemType() == StemMethodType.PARAMETER_TYPE_FULL) {
				parent.getParameterTypes().add(stem);
				ObjectSorter.sortStemMethods(parent.getParameterTypes());
			}
			else if (stem.getStemType() == StemMethodType.PARAMETER_TYPE_PART) {
				parent.getParts().add(stem);
				ObjectSorter.sortStemMethods(parent.getParts());
			}
			else if (stem.getStemType() == StemMethodType.REFERENCE_NAME_FULL) {
				parent.getReferences().add(stem);
				ObjectSorter.sortStemMethods(parent.getReferences());
			}
			else if (stem.getStemType() == StemMethodType.REFERENCE_NAME_PART) {
				parent.getParts().add(stem);
				ObjectSorter.sortStemMethods(parent.getParts());
			}
			else if (stem.getStemType() == StemMethodType.REFERENCE_TYPE_FULL) {
				parent.getReferenceTypes().add(stem);
				ObjectSorter.sortStemMethods(parent.getReferenceTypes());
			}
			else if (stem.getStemType() == StemMethodType.REFERENCE_TYPE_PART) {
				parent.getParts().add(stem);
				ObjectSorter.sortStemMethods(parent.getParts());
			}
		}

		return stemMethodTree;
	}

	/**
	 * Serialize a stem method tree into a single flat list of stem children.
	 * 
	 * @param rootStem
	 *        the root stem of a stem tree
	 * 
	 * @return
	 *         a flat list of stem methods
	 */
	public static List<StemMethod> inflateStemMethods(StemMethod rootStem) {

		List<StemMethod> flatList = new ArrayList<>();

		if (rootStem != null) {

			flatList.add(rootStem);
			flatList.addAll(rootStem.getParts());

			for (StemMethod paramStem : rootStem.getParameters()) {
				flatList.add(paramStem);
				flatList.addAll(paramStem.getParts());

				for (StemMethod paramTypeStem : paramStem.getParameterTypes()) {
					flatList.add(paramTypeStem);
					flatList.addAll(paramTypeStem.getParts());
				}
			}

			for (StemMethod refStem : rootStem.getReferences()) {
				flatList.add(refStem);
				flatList.addAll(refStem.getParts());

				for (StemMethod refTypeStem : refStem.getReferenceTypes()) {
					flatList.add(refTypeStem);
					flatList.addAll(refTypeStem.getParts());
				}
			}
		}

		return flatList;
	}

	/**
	 * Retrieve a map of all StemMethods in project, classified by term.
	 * Each entry will be of the form (term, List<StemMethod>).
	 * 
	 * @param project
	 *        the project owning stems
	 * 
	 * @return
	 *         a map of stems
	 */
	public static Map<String, List<StemMethod>> getStemMethodByTermMap(Project project) {

		Map<String, List<StemMethod>> stemMap = new HashMap<>();

		Map<Integer, StemMethod> stemTreeMap = ApplicationLogic.getStemMethodTreeMap(project);

		// Populate map
		for (StemMethod rootStem : stemTreeMap.values()) {

			// Get all stem is hierarchy
			List<StemMethod> stems = ApplicationLogic.inflateStemMethods(rootStem);

			for (StemMethod stem : stems) {

				if (!stemMap.containsKey(stem.getTerm())) {
					stemMap.put(stem.getTerm(), new ArrayList<>());
				}

				stemMap.get(stem.getTerm()).add(stem);
			}
		}

		return stemMap;
	}

	/**
	 * Return a set of all terms which are intersecting among stem concepts and stem methods.	 
	 * 
	 * @param stemConcepts
	 *        the stem concepts
	 * @param stemMethods
	 *        the stem methods
	 * @return a set of string (each item are stem term)
	 */
	public static Set<String> getTermIntersection(List<StemConcept> stemConcepts, List<StemMethod> stemMethods) {

		// Create a set of stem concept terms
		Set<String> stemConceptSet = new HashSet<>();
		for (StemConcept stem : stemConcepts) {
			stemConceptSet.add(stem.getTerm());
		}

		// Create a set of stem method terms
		Set<String> stemMethodSet = new HashSet<>();
		for (StemMethod stem : stemMethods) {
			stemMethodSet.add(stem.getTerm());
		}

		stemConceptSet.retainAll(stemMethodSet);

		return stemConceptSet;
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
