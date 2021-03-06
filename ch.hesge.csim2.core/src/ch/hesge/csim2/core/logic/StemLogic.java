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
import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.StemConcept;
import ch.hesge.csim2.core.model.StemConceptType;
import ch.hesge.csim2.core.model.StemMethod;
import ch.hesge.csim2.core.model.StemMethodType;
import ch.hesge.csim2.core.utils.DaoUtils;
import ch.hesge.csim2.core.utils.ObjectSorter;
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
	 * Retrieve all stems associated to a term.
	 * 
	 * Words present in rejectedList will not produce associated stems.
	 * The first item returned is the full-name-stem of the name passed in argument,
	 * all other items are subpart items retrieve with camel-case splitting.
	 * 
	 * @param term
	 *        the name to use to extract stems
	 * @param rejectedList
	 *        the list of forbidden words
	 * @return
	 *         a list of stems associated to name passed in argument or an empty
	 *         list
	 */
	public static List<String> getStems(String term, List<String> rejectedList) {

		List<String> stems = new ArrayList<>();

		if (term.isEmpty() || term.startsWith("_"))
			return stems;

		// Clean name before stemmisation 
		String cleanTerm = term;
		cleanTerm = Normalizer.normalize(term, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", ""); // remove accentuated chars
		cleanTerm = cleanTerm.replaceAll("\\s+", " "); // remove multiple consecutive spaces
		cleanTerm = cleanTerm.replaceAll("Class|class", ""); // remove class keyword
		cleanTerm = cleanTerm.replaceAll("\\[.*\\]|\\{.*\\}|\\(.*\\)", ""); // remove [, ], {, }, (, )
		cleanTerm = cleanTerm.replaceAll("[^_\\-\\s\\sA-Za-z0-9]", ""); // remove non alphabetics chars
		cleanTerm = cleanTerm.trim(); // trim left & right space
		cleanTerm = StringUtils.trimHungarian(cleanTerm); // remove lpsz, sz, etc...

		if (cleanTerm.length() > 0) {

			List<String> nameParts = new ArrayList<>();
			List<String> splitWords = new ArrayList<>();

			// The first stem/word is always the full name
			String fullName = cleanTerm.replaceAll("[\\_\\-\\s]", "").toLowerCase();

			// Skip full name, if included in rejection list
			if (rejectedList != null && rejectedList.contains(fullName)) {
				return stems;
			}
			else {
				splitWords.add(fullName);
			}

			// Split name into its parts (camel casing notation) 
			List<String> camelWords = StringUtils.splitCamelCase(cleanTerm);

			if (camelWords.size() > 1) {
				for (String word : camelWords) {
					splitWords.add(word.toLowerCase());
				}
			}
			
			// Skip part name, if included in rejection list
			for (String word : splitWords) {

				if (word.length() > 0) {

					// Add only words not in reject list and not already present
					if (!stems.contains(word) && (rejectedList == null || !rejectedList.contains(word))) {
						nameParts.add(word);
					}
				}
			}

			// Finally stemmize all name parts with snowball
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

		ApplicationLogic applicationLogic = ApplicationLogic.UNIQUE_INSTANCE;
		Map<String, List<StemConcept>> stemMap = new HashMap<>();

		Map<Integer, StemConcept> stemTreeMap = applicationLogic.getStemConceptTreeMap(project);

		// Populate map
		for (StemConcept rootStem : stemTreeMap.values()) {

			// Get all stem in hierarchy
			List<StemConcept> stems = applicationLogic.inflateStemConcepts(rootStem);

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

		Map<Integer, StemMethod> stemTreeMap = ApplicationLogic.UNIQUE_INSTANCE.getStemMethodTreeMap(project);

		// Populate map
		for (StemMethod rootStem : stemTreeMap.values()) {

			// Get all stem is hierarchy
			List<StemMethod> stems = ApplicationLogic.UNIQUE_INSTANCE.inflateStemMethods(rootStem);

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
	 * Return a set of all terms which are intersecting among stem concepts and
	 * stem methods.
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
	 * Delete all stems (method or concept) related to a project.
	 * 
	 * @param project
	 *        the project owning the stems.
	 */
	public static void deleteStems(Project project) {
		StemConceptDao.deleteByProject(project);
		StemMethodDao.deleteByProject(project);
	}
	
	/**
	 * Save a list of stem concept.
	 * 
	 * @param project
	 *        the project owning the stems to save
	 * @param stems
	 *        a list of StemConcept to save
	 */
	public static void saveStemConcepts(Project project, List<StemConcept> stems) {

		StemConceptDao.deleteByProject(project);

		for (StemConcept stem : stems) {
			
			// Update dependent ids
			stem.setProjectId(stem.getProject().getKeyId());
			stem.setParentId(stem.getParent() == null ? -1 : stem.getParent().getKeyId());
			stem.setConceptId(stem.getConcept().getKeyId());

			if (DaoUtils.isNewObject(stem)) {
				StemConceptDao.add(stem);
			}
			else {
				StemConceptDao.update(stem);
			}
		}
	}

	/**
	 * Save a list of stem method.
	 * 
	 * @param project
	 *        the project owning stems to save
	 * @param stem
	 *        the StemMethod list to save
	 */
	public static void saveStemMethods(Project project, List<StemMethod> stems) {
		
		StemMethodDao.deleteByProject(project);

		for (StemMethod stem : stems) {
			
			// Update dependency ids
			stem.setProjectId(stem.getProject().getKeyId());
			stem.setParentId(stem.getParent() == null ? -1 : stem.getParent().getKeyId());
			stem.setSourceMethodId(stem.getSourceMethod().getKeyId());
			
			if (DaoUtils.isNewObject(stem)) {
				StemMethodDao.add(stem);
			}
			else {
				StemMethodDao.update(stem);
			}
		}
	}
}