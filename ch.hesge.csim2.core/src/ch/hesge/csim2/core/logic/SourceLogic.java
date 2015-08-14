/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hesge.csim2.core.logic;

import java.util.ArrayList;
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
	 * Retrieve all source class owned by a project as a map of (classId,
	 * SourceClass).
	 * 
	 * @param project
	 *        the owner
	 * 
	 * @return a map of (classId, SourceClass)
	 */
	public static Map<Integer, SourceClass> getSourceClassMap(Project project) {

		List<SourceClass> sourceClasses = SourceClassDao.findByProject(project);
		ObjectSorter.sortSourceClasses(sourceClasses);

		Map<Integer, SourceClass> classMap = new HashMap<>();

		// First populate the class map
		for (SourceClass sourceClass : sourceClasses) {
			classMap.put(sourceClass.getKeyId(), sourceClass);
		}

		// Then populate dependencies
		for (SourceClass sourceClass : sourceClasses) {
			
			// Populate class attributes
			for (SourceAttribute sourceAttribute : SourceAttributeDao.findByClass(sourceClass)) {
				sourceAttribute.setSourceClass(sourceClass);
				sourceClass.getAttributes().add(sourceAttribute);				
			}

			ObjectSorter.sortSourceAttributes(sourceClass.getAttributes());

			// Populate class method
			for (SourceMethod sourceMethod : SourceMethodDao.findByClass(sourceClass)) {
				sourceMethod.setSourceClass(sourceClass);
				sourceClass.getMethods().add(sourceMethod);				
			}

			ObjectSorter.sortSourceMethods(sourceClass.getMethods());
			
			// Populate superclass
			SourceClass superclass = classMap.get(sourceClass.getSuperClassId());
			
			if (superclass != null) {
				sourceClass.setSuperClass(superclass);
				superclass.getSubClasses().add(sourceClass);
				ObjectSorter.sortSourceClasses(superclass.getSubClasses());
			}			
		}
		
		return classMap;
	}

	/**
	 * Retrieve all source classes as a hierarchy.
	 * 
	 * @param project
	 *        the owner
	 * 
	 * @return a list of source class root
	 */
	public static List<SourceClass> getSourceClassTree(Project project) {

		List<SourceClass> classRoots = new ArrayList<>();
		
		// Retrieve a map of all concepts
		Map<Integer, SourceClass> classMap = ApplicationLogic.UNIQUE_INSTANCE.getSourceClassMap(project);

		// And extract those without parent
		for (SourceClass sourceClass : classMap.values()) {
			if (sourceClass.getSuperClass() == null) {
				classRoots.add(sourceClass);
			}
		}
		
		// Sort concepts
		ObjectSorter.sortSourceClasses(classRoots);

		return classRoots;
	}

	/**
	 * Retrieve all source methods owned by a project 
	 * as a map of (methodId, SourceMethod).
	 * 
	 * @param project
	 *        the owner
	 * 
	 * @return a map of (methodId, SourceMethod)
	 */
	public static Map<Integer, SourceMethod> getSourceMethodMap(Project project) {

		// Retrieve a map of all classes
		Map<Integer, SourceClass> classMap = ApplicationLogic.UNIQUE_INSTANCE.getSourceClassMap(project);

		// Create the map of all methods
		Map<Integer, SourceMethod> methodMap = new HashMap<>();

		for (SourceClass sourceClass : classMap.values()) {
			for (SourceMethod sourceMethod : sourceClass.getMethods()) {
				methodMap.put(sourceMethod.getKeyId(), sourceMethod);
			}
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
	 * Retrieve a list of all source class with their abstract factor computed.
	 * 
	 * @param project
	 *        the owner
	 * 
	 * @return
	 *         the list of source classes
	 */
	public static List<SourceClass> getSourceClassAbstractions(Project project) {
		
		Map<Integer, SourceClass> classMap = getSourceClassMap(project);
		
		
		return new ArrayList<>(classMap.values());
	}

	/**
	 * Retrieve all method parameters and references.
	 * 
	 * @param sourceClassMap
	 *        the source class to populate
	 */
	public static void populateMethodParametersAndReferences(Map<Integer, SourceClass> sourceClassMap) {
		
		for (SourceClass sourceClass : sourceClassMap.values()) {
			for (SourceMethod sourceMethod : sourceClass.getMethods()) {
				
				// Retrieve method parameters
				List<SourceParameter> parameters = SourceParameterDao.findByMethod(sourceMethod);
				sourceMethod.getParameters().clear();
				sourceMethod.getParameters().addAll(parameters);

				// Retrieve method references
				List<SourceReference> references = SourceReferenceDao.findByMethod(sourceMethod);
				sourceMethod.getReferences().clear();
				sourceMethod.getReferences().addAll(references);
			}
		}
	}
	
	/**
	 * Delete all sources and their dependencies owned by an project. 
	 * Thas is class, attribute, method, parameter and reference.
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
	 * Save all sources passed in argument.
	 * 
	 * @param project
	 *        the project owning classes
	 * @param sourceClasses
	 *        the sourceClasses to save
	 */
	public static void saveSourceClasses(Project project, List<SourceClass> sourceClasses) {

		// First delete all sources associated to project
		deleteSources(project);

		// Create a map of all classes
		Map<String, SourceClass> sourceClassMap = new HashMap<>();
		for (SourceClass sourceClass : sourceClasses) {
			sourceClass.setProjectId(project.getKeyId());
			SourceClassDao.add(sourceClass);
			sourceClassMap.put(sourceClass.getName(), sourceClass);
		}
		
		// Now update all source dependencies
		for (SourceClass sourceClass : sourceClasses) {

			// Retrieve its superclass
			SourceClass superclass = sourceClassMap.get(sourceClass.getSuperClassName());

			// Save the class
			sourceClass.setSuperClassId(superclass == null ? -1 : superclass.getKeyId());
			SourceClassDao.update(sourceClass);
			
			// Save its attributes
			for (SourceAttribute sourceAttribute : sourceClass.getAttributes()) {
				sourceAttribute.setClassId(sourceClass.getKeyId());
				SourceAttributeDao.add(sourceAttribute);
			}

			// Save its methods, parameters and references
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
		}
	}
}
