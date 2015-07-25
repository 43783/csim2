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
import ch.hesge.csim2.core.utils.DaoUtils;

/**
 * This class implement all logical rules associated to project.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

class ProjectLogic {

	/**
	 * Retrieve all available projects.
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
	 * Load a project with its direct dependencies only. 
	 * That is scenarios, ontologies (not sources).
	 * 
	 * @param project
	 *        the project to load.
	 * @return the initialized instance passed in argument
	 */
	public static void loadProject(Project project) {

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
	 * Save a project without its dependencies.
	 * 
	 * @param project
	 *        the project to save.
	 */
	public static void saveProject(Project project) {
		
		// Save the ontology
		if (DaoUtils.isNewObject(project)) {
			ProjectDao.add(project);
		}
		else {
			ProjectDao.update(project);
		}
		
		ProjectDao.update(project);
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
