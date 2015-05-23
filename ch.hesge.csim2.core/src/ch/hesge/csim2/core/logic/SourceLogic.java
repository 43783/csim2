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
import ch.hesge.csim2.core.persistence.PersistanceUtils;
import ch.hesge.csim2.core.utils.ObjectSorter;

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
	 * @return a list of source classes
	 */
	public static List<SourceClass> getSourceClasses(Project project) {
		return SourceClassDao.findByProject(project);
	}

	/**
	 * Retrieve all source classes owned by a project with the following
	 * dependencies:
	 * 
	 * - its attributes
	 * - its methods
	 * - its subclasses
	 * 
	 * @param project
	 *        the owner
	 * @param withMethodDependencies
	 *        true to retrieve also method dependencies (parameters &
	 *        references)
	 * 
	 * @return a list of source classes
	 */
	public static List<SourceClass> getSourceClassesWithDependencies(Project project, boolean withMethodDependencies) {

		List<SourceClass> sourceClasses = ApplicationLogic.getSourceClasses(project);

		// Create a map of class with identical instances
		Map<Integer, SourceClass> classMap = new HashMap<>();
		for (SourceClass concept : sourceClasses) {
			classMap.put(concept.getKeyId(), concept);
		}

		// Populate each class with its dependencies
		for (SourceClass sourceClass : sourceClasses) {
			populateDependencies(sourceClass, withMethodDependencies, classMap);
		}

		return sourceClasses;
	}

	/**
	 * Populate a source class will the following dependencies:
	 * 
	 * - its attributes
	 * - its methods
	 * - its subclasses
	 * 
	 * @param sourceClass
	 *        the source class to populate
	 * @param withMethodDependencies
	 *        true to retrieve also method dependencies (parameters &
	 *        references)
	 * @param conceptMap
	 *        the map of all source classes
	 */
	private static void populateDependencies(SourceClass sourceClass, boolean withMethodDependencies, Map<Integer, SourceClass> classMap) {

		// Populate attributes
		sourceClass.getAttributes().clear();
		sourceClass.getAttributes().addAll(SourceAttributeDao.findByClass(sourceClass));
		ObjectSorter.sortSourceAttributes(sourceClass.getAttributes());

		// Populate methods
		sourceClass.getMethods().clear();
		sourceClass.getMethods().addAll(SourceMethodDao.findByClass(sourceClass));
		ObjectSorter.sortSourceMethods(sourceClass.getMethods());

		// Populate method dependencies
		if (withMethodDependencies) {

			for (SourceMethod sourceMethod : sourceClass.getMethods()) {

				// Populate parameters
				sourceMethod.getParameters().clear();
				sourceMethod.getParameters().addAll(SourceParameterDao.findByMethod(sourceMethod));
				ObjectSorter.sortSourceParameters(sourceMethod.getParameters());

				// Populate references
				sourceMethod.getReferences().clear();
				sourceMethod.getReferences().addAll(SourceReferenceDao.findByMethod(sourceMethod));
				ObjectSorter.sortSourceReferences(sourceMethod.getReferences());
			}
		}

		// Populate superclass
		sourceClass.setSuperClass(classMap.get(sourceClass.getSuperClassId()));

		if (sourceClass.getSuperClass() != null) {
			sourceClass.getSuperClass().getSubClasses().add(sourceClass);
			ObjectSorter.sortSourceClasses(sourceClass.getSuperClass().getSubClasses());
		}
	}

	/**
	 * Retrieve all source class owned by a project as a (keyId,SourceClass)
	 * map.
	 * 
	 * @param project
	 *        the owner
	 * 
	 * @return a map of source attribute
	 */
	public static Map<Integer, SourceClass> getSourceClassMap(Project project) {

		List<SourceClass> sourceClasses = SourceClassDao.findByProject(project);
		Map<Integer, SourceClass> classMap = new HashMap<Integer, SourceClass>();

		for (SourceClass sourceClass : sourceClasses) {
			classMap.put(sourceClass.getKeyId(), sourceClass);
		}

		return classMap;
	}

	/**
	 * Retrieve all source methods owned by a project as a (keyId, SourceMethod)
	 * map.
	 * 
	 * @param project
	 *        the owner
	 * 
	 * @return a map of source method
	 */
	public static Map<Integer, SourceMethod> getSourceMethodMap(Project project) {

		Map<Integer, SourceMethod> methodMap = new HashMap<>();
		List<SourceMethod> sourceMethods = SourceMethodDao.findByProject(project);

		for (SourceMethod sourceMethod : sourceMethods) {
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
