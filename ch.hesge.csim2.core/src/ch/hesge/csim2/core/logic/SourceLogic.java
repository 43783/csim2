/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hesge.csim2.core.logic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.hesge.csim2.core.dao.SourceAttributeDao;
import ch.hesge.csim2.core.dao.SourceClassDao;
import ch.hesge.csim2.core.dao.SourceMethodDao;
import ch.hesge.csim2.core.dao.SourceParameterDao;
import ch.hesge.csim2.core.dao.SourceReferenceDao;
import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.SourceAttribute;
import ch.hesge.csim2.core.model.SourceClass;
import ch.hesge.csim2.core.model.SourceMethod;
import ch.hesge.csim2.core.model.SourceParameter;
import ch.hesge.csim2.core.model.SourceReference;
import ch.hesge.csim2.core.utils.ObjectSorter;
import ch.hesge.csim2.core.utils.PersistanceUtils;

/**
 * This class implement all logical rules associated to source
 * class/method/attributes/etc...
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

class SourceLogic {

	/**
	 * Retrieve all source classes owned by a project.
	 * 
	 * @param project
	 *        the owner
	 * 
	 * @return a list of SourceClass
	 */
	public static List<SourceClass> getSourceClasses(Project project) {

		List<SourceClass> sourceClasses = SourceClassDao.findByProject(project);

		// Create a map of class
		Map<Integer, SourceClass> classMap = new HashMap<>();
		for (SourceClass concept : sourceClasses) {
			classMap.put(concept.getKeyId(), concept);
		}

		// Now populate classes with attributes, methods and superclass
		for (SourceClass sourceClass : sourceClasses) {

			// Populate superclass
			SourceClass superclass = classMap.get(sourceClass.getSuperClassId());
			if (superclass != null) {
				sourceClass.setSuperClass(superclass);
				superclass.getSubClasses().add(sourceClass);
				ObjectSorter.sortSourceClasses(superclass.getSubClasses());
			}

			// Populate attributes
			sourceClass.getAttributes().clear();
			sourceClass.getAttributes().addAll(SourceAttributeDao.findByClass(sourceClass));
			ObjectSorter.sortSourceAttributes(sourceClass.getAttributes());

			// Populate methods
			sourceClass.getMethods().clear();
			sourceClass.getMethods().addAll(SourceMethodDao.findByClass(sourceClass));
			ObjectSorter.sortSourceMethods(sourceClass.getMethods());
		}

		return sourceClasses;
	}

	/**
	 * Retrieve all source class with methods, parameters and references owned by a project.
	 * 
	 * @param project
	 *        the owner
	 * 
	 * @return a list of SourceClass
	 */
	public static List<SourceClass> getSourceClassMethodParam(Project project) {
		
		List<SourceClass> sourceClasses = ApplicationLogic.getSourceClasses(project);
		
		for (SourceClass sourceClass : sourceClasses) {
			for (SourceMethod sourceMethod : sourceClass.getMethods()) {

				// Populate method parameters
				sourceMethod.getParameters().clear();
				sourceMethod.getParameters().addAll(SourceParameterDao.findByMethod(sourceMethod));
				ObjectSorter.sortSourceParameters(sourceMethod.getParameters());
	
				// Populate method references
				sourceMethod.getReferences().clear();
				sourceMethod.getReferences().addAll(SourceReferenceDao.findByMethod(sourceMethod));
				ObjectSorter.sortSourceReferences(sourceMethod.getReferences());
			}
		}
		
		return sourceClasses;
	}

	/**
	 * Retrieve all source class owned by a project as a map of (classId,
	 * SourceClass).
	 * 
	 * @param project
	 *        the owner
	 * 
	 * @return a map of (classId, SourceClass)
	 */
	public static Map<Integer, SourceClass> getSourceClassMap(Project project) {

		Map<Integer, SourceClass> classMap = new HashMap<>();

		// Populate the map
		for (SourceClass sourceClass : ApplicationLogic.getSourceClasses(project)) {
			classMap.put(sourceClass.getKeyId(), sourceClass);
		}

		return classMap;
	}

	/**
	 * Retrieve all source methods owned by a project as a map of (methodId,
	 * SourceMethod).
	 * 
	 * @param project
	 *        the owner
	 * 
	 * @return a map of (methodId, SourceMethod)
	 */
	public static Map<Integer, SourceMethod> getSourceMethodMap(Project project) {

		// Create a map of class
		Map<Integer, SourceClass> classMap = new HashMap<>();
		for (SourceClass concept : SourceClassDao.findByProject(project)) {
			classMap.put(concept.getKeyId(), concept);
		}

		// Create the matp of method
		Map<Integer, SourceMethod> methodMap = new HashMap<>();

		for (SourceMethod sourceMethod : SourceMethodDao.findByProject(project)) {
			sourceMethod.setSourceClass(classMap.get(sourceMethod.getClassId()));
			methodMap.put(sourceMethod.getKeyId(), sourceMethod);
		}

		return methodMap;
	}

	/**
	 * Retrieve a source method by its signature.
	 * 
	 * @param sourceClass
	 *        the owner
	 * @param signature
	 *        the method signature
	 * 
	 * @return a SourceMethod or null
	 */
	public static SourceMethod getSourceMethodBySignature(SourceClass sourceClass, String signature) {

		for (SourceMethod sourceMethod : sourceClass.getMethods()) {
			if (sourceMethod.getSignature() != null && sourceMethod.getSignature().equals(signature)) {
				return sourceMethod;
			}
		}

		return null;
	}

	/**
	 * Delete all sources owned by an project. Thas is class, attribute, method,
	 * parameter and reference.
	 * 
	 * @param project
	 *        the project to clean sources
	 */
	public static void deleteSources(Project project) {

		SourceAttributeDao.deleteByProject(project);
		SourceReferenceDao.deleteByProject(project);
		SourceParameterDao.deleteByProject(project);
		SourceMethodDao.deleteByProject(project);
		SourceClassDao.deleteByProject(project);
	}

	/**
	 * Save all source classes without attribute/method/parameters.
	 * 
	 * @param sourceClasses
	 *        the class list to save
	 */
	public static void saveSourceClasses(List<SourceClass> sourceClasses) {

		for (SourceClass sourceClass : sourceClasses) {

			if (PersistanceUtils.isNewObject(sourceClass)) {
				SourceClassDao.add(sourceClass);
			}
			else {
				SourceClassDao.update(sourceClass);
			}
		}
	}

	/**
	 * Save all sources passed in argument.
	 * 
	 * @param project
	 *        the project owning classes
	 * @param sourceClasses
	 *        the sourceClasses to save
	 */
	public static void saveSources(Project project, List<SourceClass> sourceClasses) {

		// Save recursively each source-class
		for (SourceClass sourceClass : sourceClasses) {
			if (sourceClass.getName() != null) {
				save(project, null, sourceClass);
			}
		}
	}

	/**
	 * Save a source-class with its dependencies.
	 * 
	 * @param project
	 *        the project owning the source-class
	 * @param parentClass
	 *        the parent owning the source-class
	 * @param sourceClass
	 *        the source-class to save
	 */
	private static void save(Project project, SourceClass parentClass, SourceClass sourceClass) {

		// Save the source class
		if (PersistanceUtils.isNewObject(sourceClass)) {
			sourceClass.setProjectId(project.getKeyId());
			sourceClass.setSuperClassId(parentClass == null ? -1 : parentClass.getKeyId());
			SourceClassDao.add(sourceClass);
		}
		else {
			SourceClassDao.update(sourceClass);
		}

		// Now save all class attributes
		for (SourceAttribute sourceAttribute : sourceClass.getAttributes()) {
			sourceAttribute.setClassId(sourceClass.getKeyId());
			SourceAttributeDao.add(sourceAttribute);
		}

		// Now save all class methods, parameters and references
		for (SourceMethod sourceMethod : sourceClass.getMethods()) {

			sourceMethod.setClassId(sourceClass.getKeyId());
			SourceMethodDao.add(sourceMethod);

			// Save parameters for each method
			for (SourceParameter sourceParameter : sourceMethod.getParameters()) {
				sourceParameter.setMethodId(sourceMethod.getKeyId());
				SourceParameterDao.add(sourceParameter);
			}

			// Save variable references for each method
			for (SourceReference sourceReference : sourceMethod.getReferences()) {
				sourceReference.setMethodId(sourceMethod.getKeyId());
				SourceReferenceDao.add(sourceReference);
			}
		}

		// Finally save recursively its children
		for (SourceClass childClass : sourceClass.getSubClasses()) {
			save(project, sourceClass, childClass);
		}
	}
}
