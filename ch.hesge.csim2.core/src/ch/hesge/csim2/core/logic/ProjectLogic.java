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
	 * Create a new project within the application.
	 * 
	 * @param name
	 *        the name of the new project
	 * 
	 * @return and instance of project
	 */
	public static Project createProject(String name) {

		Project project = new Project();
		project.setName(name);

		ProjectDao.add(project);

		return project;
	}

	/**
	 * Retrieve a project by its name.
	 * 
	 * @param name
	 *        the name of the project
	 * @return Project or null
	 */
	public static Project getProject(String name) {
		return ProjectDao.findByName(name);
	}

	/**
	 * Load a project with its direct dependencies only. That is scenarios,
	 * ontologies and sources.
	 * 
	 * @param project
	 *        the project to load.
	 * @return the initialized instance passed in argument
	 */
	public static void loadProject(Project project) {

		Console.writeInfo(ProjectLogic.class, "loading project: " + project.getName());

		// Load scenarios
		List<Scenario> scenarios = ApplicationLogic.UNIQUE_INSTANCE.getScenarios(project);
		project.getScenarios().clear();
		project.getScenarios().addAll(scenarios);

		// Load ontologies
		List<Ontology> ontologies = ApplicationLogic.UNIQUE_INSTANCE.getOntologies(project);
		project.getOntologies().clear();
		project.getOntologies().addAll(ontologies);
	}

	/**
	 * Save a project with its direct dependencies (scenarios, ontologies
	 * 
	 * @param project
	 *        the project to save.
	 */
	public static void saveProject(Project project) {

		Console.writeInfo(ProjectLogic.class, "saving application: " + project.getName());

		// Save the project itself
		ProjectDao.update(project);
		
		// Save scenarios
		List<Scenario> scenarios = project.getScenarios();
		for (Scenario scenario : scenarios) {
			scenario.setProjectId(project.getKeyId());
		}

		ApplicationLogic.UNIQUE_INSTANCE.saveScenarios(scenarios);
		Console.writeInfo(ProjectLogic.class, " scenarios: " + scenarios.size());

		// Save ontologies
		List<Ontology> ontologies = project.getOntologies();
		for (Ontology ontology : ontologies) {
			ontology.setProjectId(project.getKeyId());
		}

		ApplicationLogic.UNIQUE_INSTANCE.saveOntologies(ontologies);
		Console.writeInfo(ProjectLogic.class, " ontologies: " + ontologies.size());

		// Save sources
		List<SourceClass> sourceClasses = project.getSourceClasses();
		for (SourceClass sourceClass : sourceClasses) {
			sourceClass.setProjectId(project.getKeyId());
		}

		ApplicationLogic.UNIQUE_INSTANCE.saveSourceClasses(project, sourceClasses);
		Console.writeInfo(ProjectLogic.class, " source-classes: " + scenarios.size());
	}
	
	/**
	 * Delete a project and all its dependencies.
	 * 
	 * @param project
	 *        the project to delete
	 */
	public static void deleteProject(Project project) {
		SourceLogic.deleteSources(project);
		OntologyLogic.deleteOntologies(project);
		ScenarioLogic.deleteScenarios(project);
		StemLogic.deleteStems(project);
		ProjectDao.delete(project);
	}
}
