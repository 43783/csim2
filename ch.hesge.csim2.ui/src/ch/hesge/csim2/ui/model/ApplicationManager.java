package ch.hesge.csim2.ui.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import ch.hesge.csim2.core.logic.ApplicationLogic;
import ch.hesge.csim2.core.model.Application;
import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.ConceptAttribute;
import ch.hesge.csim2.core.model.ConceptClass;
import ch.hesge.csim2.core.model.ConceptLink;
import ch.hesge.csim2.core.model.Context;
import ch.hesge.csim2.core.model.IEngine;
import ch.hesge.csim2.core.model.IMethodConceptMatcher;
import ch.hesge.csim2.core.model.MethodConceptMatch;
import ch.hesge.csim2.core.model.Ontology;
import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.Scenario;
import ch.hesge.csim2.core.model.ScenarioStep;
import ch.hesge.csim2.core.model.SourceClass;
import ch.hesge.csim2.core.model.SourceMethod;
import ch.hesge.csim2.core.model.StemConcept;
import ch.hesge.csim2.core.model.StemMethod;
import ch.hesge.csim2.core.model.TimeSeries;
import ch.hesge.csim2.core.model.Trace;
import ch.hesge.csim2.ui.dialogs.AboutDialog;
import ch.hesge.csim2.ui.dialogs.NameDialog;
import ch.hesge.csim2.ui.dialogs.NameIdentifierDialog;
import ch.hesge.csim2.ui.dialogs.ParametersDialog;
import ch.hesge.csim2.ui.dialogs.ScenarioStepDialog;
import ch.hesge.csim2.ui.dialogs.SelectProjectDialog;
import ch.hesge.csim2.ui.utils.SwingUtils;
import ch.hesge.csim2.ui.views.WeightedConceptView;
import ch.hesge.csim2.ui.views.MainView;
import ch.hesge.csim2.ui.views.MatchingView;
import ch.hesge.csim2.ui.views.OntologyView;
import ch.hesge.csim2.ui.views.ScenarioView;
import ch.hesge.csim2.ui.views.StemConceptsView;
import ch.hesge.csim2.ui.views.StemSourcesView;
import ch.hesge.csim2.ui.views.TimeSeriesView;
import ch.hesge.csim2.ui.views.TracesView;

/**
 * This class is responsible to handle all actions requested by user
 * through UI components and to redirect request to the ApplicationLogic.
 * 
 * @author Eric Harth
 */
public class ApplicationManager {

	// Unique instance
	public static final ApplicationManager UNIQUE_INSTANCE = new ApplicationManager();

	// Private attributes
	private MainView mainView;
	private Application application;
	private ApplicationLogic applicationLogic;

	/**
	 * Default constructor.
	 * 
	 * @param application
	 */
	public ApplicationManager() {
		this.applicationLogic = ApplicationLogic.UNIQUE_INSTANCE;
		this.application = createApplication();
	}

	/**
	 * Show the about dialog
	 */
	public void showAbout() {
		new AboutDialog(mainView).setVisible(true);
	}

	/**
	 * Show console logs visible.
	 */
	public void showConsole() {
		mainView.showConsole();
	}

	/**
	 * Display a dialog with a message
	 */
	public void showMessage(String title, String message, int options) {
		JOptionPane.showMessageDialog(mainView, message, title, options);
	}

	/**
	 * Display a confirmation dialog with a message
	 */
	public int showConfirmMessage(String title, String message, int options) {
		return JOptionPane.showConfirmDialog(mainView, message, title, options);
	}

	/**
	 * Show the scenario view
	 * 
	 * @param scenario
	 */
	public void showScenario(Scenario scenario) {

		SwingUtils.invokeLongOperation(mainView.getRootPane(), new Runnable() {
			@Override
			public void run() {
				mainView.showView(scenario.getName(), new ScenarioView(scenario));
			}
		});
	}

	/**
	 * Show the ontology view
	 * 
	 * @param ontology
	 */
	public void showOntology(Ontology ontology) {

		SwingUtils.invokeLongOperation(mainView.getRootPane(), new Runnable() {
			@Override
			public void run() {
				mainView.showView(ontology.getName(), new OntologyView(ontology));
			}
		});
	}

	/**
	 * Show the stem source view
	 */
	public void showSourceStems() {

		SwingUtils.invokeLongOperation(mainView.getRootPane(), new Runnable() {
			@Override
			public void run() {
				mainView.showView("Sources", new StemSourcesView(application.getProject()));
			}
		});
	}

	/**
	 * Show the stem concepts view
	 */
	public void showConceptStems() {

		SwingUtils.invokeLongOperation(mainView.getRootPane(), new Runnable() {
			@Override
			public void run() {
				mainView.showView("Concepts", new StemConceptsView(application.getProject()));
			}
		});
	}

	/**
	 * Show the matching view
	 */
	public void showMatching() {

		SwingUtils.invokeLongOperation(mainView.getRootPane(), new Runnable() {
			@Override
			public void run() {
				mainView.showView("Matching", new MatchingView(application.getProject()));
			}
		});
	}

	/**
	 * Show the granularity view
	 */
	public void showConceptWeights() {

		SwingUtils.invokeLongOperation(mainView.getRootPane(), new Runnable() {
			@Override
			public void run() {
				mainView.showView("Weighted Concepts", new WeightedConceptView(application.getProject()));
			}
		});
	}

	/**
	 * Show the trace graph view
	 */
	public void showTraceView() {

		SwingUtils.invokeLongOperation(mainView.getRootPane(), new Runnable() {
			@Override
			public void run() {
				mainView.showView("Traces", new TracesView(application.getProject()));
			}
		});
	}

	/**
	 * Show the time series view
	 */
	public void showTimeSeriesView() {

		SwingUtils.invokeLongOperation(mainView.getRootPane(), new Runnable() {
			@Override
			public void run() {
				mainView.showView("Timeseries", new TimeSeriesView(application.getProject()));
			}
		});
	}

	/**
	 * Show a source file with default editor
	 */
	public void showSourceFile(SourceMethod method) {

		if (method != null && method.getFilename() != null) {
			
			String rootPath = application.getProperties().getProperty("rootSourcePath");
			
			if (rootPath == null) {
				rootPath = SwingUtils.selectFolder(mainView, null);
				
				if (rootPath != null) {
					 application.getProperties().setProperty("rootSourcePath", rootPath);
				}
			}

			// Try to open the file
			if (!SwingUtils.openFile(rootPath, method.getFilename())) {
				showMessage("Warning", "Unable to find file: " + method.getFilename(), JOptionPane.OK_OPTION);
			}
		}
	}

	/**
	 * Return the unique application object.
	 * 
	 * @return
	 *         an instance of Application
	 */
	public Application getApplication() {
		return application;
	}

	/**
	 * Return the main application view.
	 * 
	 * @return
	 *         an instance of MainView
	 */
	public MainView getMainView() {
		return mainView;
	}

	/**
	 * Set the reference to the main view.
	 * 
	 * @param mainView
	 */
	public void setMainView(MainView mainView) {
		this.mainView = mainView;
	}

	/**
	 * Retrieve all available projects.
	 */
	public List<Project> getProjects() {
		return applicationLogic.getProjects();
	}

	/**
	 * Retrieve all available ontologies without their dependencies.
	 */
	public List<Ontology> getOntologies() {
		return applicationLogic.getOntologies();
	}

	/**
	 * Return a list of all scenarios registered within the application.
	 */
	public List<Scenario> getScenarios() {
		return applicationLogic.getScenarios();
	}

	/**
	 * Return a list of scenario owned by a project.
	 */
	public List<Scenario> getScenarios(Project project) {
		return applicationLogic.getScenarios(project);
	}
	
	/**
	 * Return a list of all engines registered within the application.
	 */
	public List<IEngine> getEngines() {
		return applicationLogic.getEngines();
	}

	/**
	 * Retrieve all traces owned by a scenario.
	 */
	public List<Trace> getTraces(Scenario scenario) {
		return applicationLogic.getTraces(scenario);
	}

	/**
	 * Retrieve all traces owned by a scenario as a hierarchy.
	 */
	public List<Trace> getTraceTree(Scenario scenario) {
		return applicationLogic.getTraceTree(scenario);
	}

	/**
	 * Retrieve a list of all concepts owned by an ontology.
	 */
	public List<Concept> getConcepts(Ontology ontology) {
		return applicationLogic.getConcepts(ontology);
	}

	/**
	 * Retrieve all ontology concepts as a hierarchy.
	 */
	public List<Concept> getConceptTree(Project project) {
		return applicationLogic.getConceptTree(project);
	}
	
	/**
	 * Retrieve a list of all concepts with their computed weight.
	 */
	public List<Concept> getWeightedConcepts(Project project) {
		return applicationLogic.getWeightedConcepts(project);
	}
	
	/**
	 * Retrieve all source classes owned by a project.
	 */
	public List<SourceClass> getSourceClassTree(Project project) {
		return applicationLogic.getSourceClassTree(project);
	}

	/**
	 * Retrieve all source methods owned by a project as a map of (methodId,
	 * SourceMethod).
	 */
	public Map<Integer, SourceMethod> getSourceMethodMap(Project project) {
		return applicationLogic.getSourceMethodMap(project);
	}

	/**
	 * Retrieve a hierarchy of stem concepts defined for a project.
	 */
	public Map<Integer, StemConcept> getStemConceptTreeMap(Project project) {
		return applicationLogic.getStemConceptTreeMap(project);
	}

	/**
	 * Retrieve a hierarchy of stem methods defined for a project.
	 */
	public Map<Integer, StemMethod> getStemMethodTreeMap(Project project) {
		return applicationLogic.getStemMethodTreeMap(project);
	}

	/**
	 * Retrieves all method concept matchers declared in configuration file.
	 */
	public synchronized List<IMethodConceptMatcher> getMatchers() {
		return applicationLogic.getMatchers();
	}

	/**
	 * Retrieve all matchings between a method and a concept.
	 */
	public Map<Integer, List<MethodConceptMatch>> getMethodMatchingMap(Project project, IMethodConceptMatcher matcher) {
		return applicationLogic.getMethodMatchingMap(project, matcher);
	}

	/**
	 * Return a set of all terms which are intersecting among stem concepts and
	 * stem methods.
	 */
	public Set<String> getTermsIntersection(List<StemConcept> stemConcepts, List<StemMethod> stemMethods) {
		return applicationLogic.getTermsIntersection(stemConcepts, stemMethods);
	}

	/**
	 * Retrieve the time series associated to a scenario traces.
	 */
	public TimeSeries getTimeSeries(Project project, Scenario scenario, IMethodConceptMatcher matcher) {
		return applicationLogic.getTimeSeries(project, scenario, matcher);
	}

	/**
	 * Create a segmented time series.
	 */
	public TimeSeries getFilteredTimeSeries(TimeSeries timeSeries, int segmentCount, double threshold, List<Concept> concepts) {
		return applicationLogic.getFilteredTimeSeries(timeSeries, segmentCount, threshold, concepts);
	}

	/**
	 * Create an initialize a new application instance.
	 */
	public Application createApplication() {
		return applicationLogic.createApplication();
	}

	/**
	 * Create a new project
	 */
	public void createNewProject() {

		// Display dialog
		NameDialog dialog = new NameDialog(mainView);
		dialog.setTitle("New Project");
		dialog.setVisible(true);

		// Detect if use clicked OK
		if (dialog.getDialogResult()) {
			Project project = applicationLogic.createProject(dialog.getNameField());
			application.setProject(project);
			reloadProject();
		}
	}

	/**
	 * Create a new scenario
	 */
	public void createNewScenario() {

		// Display dialog
		NameDialog dialog = new NameDialog(mainView);
		dialog.setTitle("New Scenario");
		dialog.setVisible(true);

		// Detect if use clicked OK
		if (dialog.getDialogResult()) {
			String scenarioName = dialog.getNameField();
			applicationLogic.createScenario(scenarioName, application.getProject());
			reloadProject();
		}
	}

	/**
	 * Create a new scenario
	 */
	public void createScenarioStep(Scenario scenario) {

		// Display dialog
		ScenarioStepDialog dialog = new ScenarioStepDialog(mainView);
		dialog.setTitle("New Step");
		dialog.setVisible(true);

		// Detect if use clicked OK
		if (dialog.getDialogResult()) {
			applicationLogic.createScenarioStep(dialog.getNameField(), dialog.getDescriptionField(), scenario);
		}
	}

	/**
	 * Create a new ontology
	 */
	public void createNewOntology() {

		// Display dialog
		NameDialog dialog = new NameDialog(mainView);
		dialog.setTitle("New Ontology");
		dialog.setVisible(true);

		// Detect if use clicked OK
		if (dialog.getDialogResult()) {
			String ontologyName = dialog.getNameField();
			applicationLogic.createOntology(ontologyName, application.getProject());
			reloadProject();
		}
	}

	/**
	 * Create a new concept and attach it to the ontology.
	 */
	public Concept createConcept(Ontology ontology) {
		return applicationLogic.createConcept(ontology);
	}

	/**
	 * Clone the concept passed in argument into a distinct instance (same
	 * keyId).
	 * 
	 * @param concept
	 *        the concept to clone
	 * @return a new concept instance
	 */
	public Concept cloneConcept(Concept concept) {
		return applicationLogic.cloneConcept(concept);
	}
	
	/**
	 * Copy concept properties to an other one, without modifying target instance identity.
	 * 
	 * @param source
	 *        the concept with properties to copy
	 * @param target
	 *        the concept to clear with source properties
	 */
	public void copyConceptProperties(Concept source, Concept target) {
		applicationLogic.copyConceptProperties(source, target);
	}
	
	/**
	 * Create a new concept link and update the ontology.
	 */
	public ConceptLink createConceptLink(Ontology ontology, Concept source, Concept target) {
		return applicationLogic.createConceptLink(ontology, source, target);
	}

	/**
	 * Create a concept attribute
	 */
	public void createConceptAttribute(Concept concept) {
		
		// Display dialog
		NameIdentifierDialog dialog = new NameIdentifierDialog(mainView);
		dialog.setTitle("New Attribute");
		dialog.setVisible(true);

		// Detect if use clicked OK
		if (dialog.getDialogResult()) {
			
			String attributeName = dialog.getNameField();
			String attributeIdentifier = dialog.getIdentifierField();
			
			ConceptAttribute attribute = new ConceptAttribute();
			attribute.setName(attributeName);
			attribute.setIdentifier(attributeIdentifier);
			concept.getAttributes().add(attribute);
		}
	}	
	
	/**
	 * Delete a concept attribute
	 */
	public void deleteConceptAttribute(Concept concept, ConceptAttribute attribute) {
		concept.getAttributes().remove(attribute);
	}
	
	/**
	 * Create a concept class 
	 */
	public void createConceptClass(Concept concept) {
		
		// Display dialog
		NameIdentifierDialog dialog = new NameIdentifierDialog(mainView);
		dialog.setTitle("New Class");
		dialog.setVisible(true);

		// Detect if use clicked OK
		if (dialog.getDialogResult()) {
			
			String attributeName = dialog.getNameField();
			String attributeIdentifier = dialog.getIdentifierField();
			
			ConceptClass attributeClass = new ConceptClass();
			attributeClass.setName(attributeName);
			attributeClass.setIdentifier(attributeIdentifier);
			concept.getClasses().add(attributeClass);
		}
	}	
	
	/**
	 * Delete a concept class
	 */
	public void deleteConceptClass(Concept concept, ConceptClass clazz) {
		concept.getClasses().remove(clazz);
	}

	/**
	 * Remove a link starting from a concept.
	 */
	public void removeConceptLink(Ontology ontology, Concept concept, ConceptLink link) {
		applicationLogic.removeConceptLink(ontology, link.getSourceConcept(), link);
	}

	/**
	 * Remove a concept from an ontology.
	 */
	public void removeConcept(Ontology ontology, Concept concept) {
		applicationLogic.removeConcept(ontology, concept);
	}

	/**
	 * Delete a project and all its dependencies.
	 * 
	 * @param project
	 *        the project to delete
	 */
	public void deleteProject(Project project) {

		// Display confirmation dialog
		int dialogResult = JOptionPane.showConfirmDialog(mainView, "Do you really want to delete the project '" + project.getName() + "' ?", "Warning", JOptionPane.YES_NO_OPTION);

		if (dialogResult == JOptionPane.YES_OPTION) {
			applicationLogic.deleteProject(project);
			application.setProject(null);
			reloadProject();
		}
	}

	/**
	 * Delete the scenario passed in argument.
	 * 
	 * @param scenario
	 *        the scenario to delete
	 */
	public void deleteScenario(Scenario scenario) {

		// Display confirmation dialog
		int dialogResult = JOptionPane.showConfirmDialog(mainView, "Do you really want to delete the scenario '" + scenario.getName() + "' ?", "Warning", JOptionPane.YES_NO_OPTION);

		if (dialogResult == JOptionPane.YES_OPTION) {
			applicationLogic.deleteScenario(scenario);
			reloadProject();
		}
	}

	/**
	 * Delete a scenario step
	 */
	public void deleteScenarioStep(Scenario scenario, ScenarioStep step) {

		// Execution is completed
		int dialogResult = showConfirmMessage("Confirmation", "Would you like to delete current step ?", JOptionPane.YES_NO_OPTION);

		if (dialogResult == JOptionPane.YES_OPTION) {
			applicationLogic.deleteScenarioStep(scenario, step);
		}
	}

	/**
	 * Delete the ontology passed in argument.
	 * 
	 * @param ontology
	 *        the ontology to delete
	 */
	public void deleteOntology(Ontology ontology) {

		// Display confirmation dialog
		int dialogResult = JOptionPane.showConfirmDialog(mainView, "Do you really want to delete the ontology '" + ontology.getName() + "' ?", "Warning", JOptionPane.YES_NO_OPTION);

		if (dialogResult == JOptionPane.YES_OPTION) {
			applicationLogic.deleteOntology(ontology);
			reloadProject();
		}
	}

	/**
	 * Save the scenario with its steps
	 */
	public void saveScenario(Scenario scenario) {

		// Execution is completed
		int dialogResult = showConfirmMessage("Confirmation", "Would you like to save scenario times ?", JOptionPane.YES_NO_OPTION);

		if (dialogResult == JOptionPane.YES_OPTION) {
			
			SwingUtils.invokeLongOperation(mainView.getRootPane(), new Runnable() {
				@Override
				public void run() {
					applicationLogic.saveScenario(scenario);
				}
			});
		}
	}

	/**
	 * Export an ontology as a Turtle file.
	 * 
	 * @param ontology
	 *        the ontology to export
	 */
	public void exportOntology(Ontology ontology) {

		String filename = SwingUtils.selectSaveFile(mainView, null);

		if (filename != null) {

			SwingUtils.invokeLongOperation(mainView.getRootPane(), new Runnable() {
				@Override
				public void run() {
					applicationLogic.exportOntology(ontology, filename);
				}
			});
		}
	}

	/**
	 * Import an ontology from a Turtle file.
	 * 
	 * @param ontology
	 *        the ontology to populate with file content
	 */
	public void importOntology(Ontology ontology) {

		String filename = SwingUtils.selectOpenFile(mainView, null);

		if (filename != null) {

			SwingUtils.invokeLongOperation(mainView.getRootPane(), new Runnable() {
				@Override
				public void run() {
					applicationLogic.importOntology(ontology, filename);
				}
			});
		}
	}

	/**
	 * Save an ontology and its dependencies.
	 * 
	 * @param ontology
	 *        the ontology to save
	 */
	public void saveOntology(Ontology ontology) {

		SwingUtils.invokeLongOperation(mainView.getRootPane(), new Runnable() {
			@Override
			public void run() {
				applicationLogic.saveOntology(ontology);
			}
		});
	}

	/**
	 * Edit a scenario step
	 */
	public void editScenarioStep(ScenarioStep step) {

		// Display dialog
		ScenarioStepDialog dialog = new ScenarioStepDialog(mainView);
		dialog.setTitle("Edit Step");
		dialog.setNameField(step.getName());
		dialog.setDescriptionField(step.getDescription());
		dialog.setVisible(true);

		// Detect if use clicked OK
		if (dialog.getDialogResult()) {
			step.setName(dialog.getNameField());
			step.setDescription(dialog.getDescriptionField());
			applicationLogic.saveScenario(step.getScenario());
		}
	}

	/**
	 * Rename the project passed in argument.
	 * 
	 * @param project
	 *        the project to rename
	 */
	public void renameProject(Project project) {

		// Display dialog
		NameDialog dialog = new NameDialog(mainView);
		dialog.setTitle("Rename Project");
		dialog.setNameField(project.getName());
		dialog.setVisible(true);

		// Detect if use clicked OK
		if (dialog.getDialogResult()) {
			project.setName(dialog.getNameField());
			applicationLogic.saveProject(project);
		}
	}

	/**
	 * Rename the project passed in argument.
	 * 
	 * @param project
	 *        the project to rename
	 */
	public void renameScenario(Scenario scenario) {

		// Display dialog
		NameDialog dialog = new NameDialog(mainView);
		dialog.setTitle("Rename Scenario");
		dialog.setNameField(scenario.getName());
		dialog.setVisible(true);

		// Detect if use clicked OK
		if (dialog.getDialogResult()) {
			String scenarioName = dialog.getNameField();
			scenario.setName(scenarioName);
			applicationLogic.saveScenario(scenario);
			reloadProject();
		}
	}

	/**
	 * Rename the ontology passed in argument.
	 * 
	 * @param ontology
	 *        the ontology to rename
	 */
	public void renameOntology(Ontology ontology) {

		// Display dialog
		NameDialog dialog = new NameDialog(mainView);
		dialog.setTitle("Rename Ontology");
		dialog.setNameField(ontology.getName());
		dialog.setVisible(true);

		// Detect if use clicked OK
		if (dialog.getDialogResult()) {
			String ontologyName = dialog.getNameField();
			ontology.setName(ontologyName);
			applicationLogic.saveOntology(ontology);
			reloadProject();
		}
	}

	/**
	 * Reload project passed in argument.
	 * 
	 * @param project
	 */
	public void reloadProject() {

		Project project = application.getProject();

		// Update main view
		mainView.setProjectDisplayName(project);

		// Clear project content
		mainView.setActiveProject(null);
		mainView.clearWorkspace();
		applicationLogic.clearCache();

		// Clear current application project
		application.setProject(project);

		// Load specified project
		if (project != null) {

			// Load specified project
			SwingUtils.invokeLongOperation(mainView.getRootPane(), new Runnable() {
				@Override
				public void run() {

					// Start loading project
					applicationLogic.loadProject(project);
					application.setProject(project);
					mainView.setActiveProject(project);
				}
			});
		}
	}

	/**
	 * Open project selection dialog
	 */
	public void selectProject() {

		SelectProjectDialog dialog = new SelectProjectDialog(mainView);
		dialog.setVisible(true);

		if (dialog.getDialogResult()) {
			application.setProject(dialog.getProject());
			reloadProject();
		}
	}

	/**
	 * Close current project
	 */
	public void closeProject() {
		application.setProject(null);
		reloadProject();
	}

    /**
	 * Clear console content.
	 */
	public void clearConsole() {
		mainView.clearConsole();
	}

	/**
	 * Start the engine passed in argument.
	 * 
	 * @param engine
	 */
	public void startEngine(IEngine engine) {

		// First display parameter view
		ParametersDialog dialog = new ParametersDialog(mainView);
		dialog.setEngine(engine);
		dialog.setVisible(true);

		if (dialog.getDialogResult()) {

			// Prepare the engine context
			Context context = new Context();
			context.putAll(application.getProperties());
			context.putAll(dialog.getParameters());
			engine.setContext(context);

			// Console cleaning
			showConsole();

			// // Start the engine
			applicationLogic.startEngine(engine);
		}
	}

	/**
	 * Stop the engine passed in argument.
	 * 
	 * @param engine
	 */
	public void stopEngine(IEngine engine) {
		applicationLogic.stopEngine(engine);
	}

	/**
	 * Start executing a scenario
	 * 
	 * @param scenario
	 */
	public void startScenario(Scenario scenario) {
		ScenarioView view = (ScenarioView) SwingUtilities.getAncestorOfClass(ScenarioView.class, mainView);
		view.startScenario();
	}

	/**
	 * Stop executing a scenario
	 * 
	 * @param scenario
	 */
	public void stopScenario(Scenario scenario) {
		ScenarioView view = (ScenarioView) SwingUtilities.getAncestorOfClass(ScenarioView.class, mainView);
		view.stopScenario();
	}

	/**
	 * Return the engine running state.
	 */
	public boolean isEngineRunning(IEngine engine) {
		return applicationLogic.isEngineRunning(engine);
	}

	/**
	 * Clear execution time of all scenario steps.
	 */
	public void resetExecutionTimes(Scenario scenario) {
		applicationLogic.resetExecutionTimes(scenario);
	}

	/**
	 * Initialize current time of a specific scenario step.
	 */
	public void initExecutionTime(ScenarioStep step) {
		applicationLogic.initExecutionTime(step);
	}

	/**
	 * Serialize a stem method tree into a single flat list of stem children.
	 */
	public List<StemMethod> inflateStemMethods(StemMethod rootStem) {
		return applicationLogic.inflateStemMethods(rootStem);
	}

	/**
	 * Serialize a stem concept tree into a single flat list of stem children.
	 */
	public List<StemConcept> inflateStemConcepts(StemConcept rootStem) {
		return applicationLogic.inflateStemConcepts(rootStem);
	}

	/**
	 * Export all matchings passed in argument in a CSV file.
	 */
	public void exportMatchings(Map<Integer, List<MethodConceptMatch>> matchMap) {

		String filename = SwingUtils.selectSaveFile(mainView, null);

		if (filename != null) {

			// Export matchings
			applicationLogic.exportMatchings(matchMap, filename);
		}
	}

	/**
	 * Quit the application
	 */
	public void exitApplication() {
		applicationLogic.shutdownApplication(application);
	}
}
