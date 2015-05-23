/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hesge.csim2.core.logic;

import java.util.List;

import ch.hesge.csim2.core.dao.ProjectDao;
import ch.hesge.csim2.core.model.Ontology;
import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.Scenario;
import ch.hesge.csim2.core.model.SourceClass;
import ch.hesge.csim2.core.utils.Console;

/**
 * This class implement all logical rules associated to project.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

class ProjectLogic {

	/**
	 * Retrieve all available projects without their dependencies.
	 * 
	 * @return a list of projects
	 */
	public synchronized static List<Project> getProjects() {
		return ProjectDao.findAll();
	}

	/**
	 * Retrieve a project by its name.
	 * 
	 * @param name
	 *            the name of the project
	 * @return Project or null
	 */
	public static Project getProject(String name) {
		return ProjectDao.findByName(name);
	}

	/**
	 * Load a project with its direct dependencies only. That is scenarios, ontologies and sources.
	 * 
	 * @param project
	 *            the project to load.
	 * @return the initialized instance passed in argument
	 */
	public static void loadProject(Project project) {

		Console.writeLine("loading application: " + project.getName());

		// Load scenarios
		List<Scenario> scenarios = ApplicationLogic.getScenarios(project);
		project.getScenarios().clear();
		project.getScenarios().addAll(scenarios);
		Console.writeLine(" scenarios: " + project.getScenarios().size());

		// Load ontologies
		List<Ontology> ontologies = ApplicationLogic.getOntologies(project);
		project.getOntologies().clear();
		project.getOntologies().addAll(ontologies);
		Console.writeLine(" ontologies: " + project.getOntologies().size());

		// Load sources
		List<SourceClass> sourceClasses = ApplicationLogic.getSourceClasses(project);
		project.getSourceClasses().clear();
		project.getSourceClasses().addAll(sourceClasses);
		Console.writeLine(" source-classes: " + project.getSourceClasses().size());
	}

	/**
	 * Save a project with its direct dependencies (scenarios, ontologies
	 * 
	 * @param project
	 *            the project to save.
	 */
	public static void saveProject(Project project) {

		Console.writeLine("saving application: " + project.getName());

		// Save scenarios
		List<Scenario> scenarios = project.getScenarios();
		for (Scenario scenario : scenarios) {
			scenario.setProjectId(project.getKeyId());
		}
		
		ApplicationLogic.saveScenarios(scenarios);
		Console.writeLine(" scenarios: " + scenarios.size());

		// Save ontologies
		List<Ontology> ontologies = project.getOntologies();
		for (Ontology ontology : ontologies) {
			ontology.setProjectId(project.getKeyId());
		}
		
		ApplicationLogic.saveOntologies(ontologies);
		Console.writeLine(" ontologies: " + ontologies.size());

		// Save sources
		List<SourceClass> sourceClasses = project.getSourceClasses();
		for (SourceClass sourceClass : sourceClasses) {
			sourceClass.setProjectId(project.getKeyId());
		}
		
		ApplicationLogic.saveSourceClasses(project, sourceClasses);
		Console.writeLine(" source-classes: " + scenarios.size());
	}
}
