package ch.hesge.csim2.ui.views;

import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import ch.hesge.csim2.core.logic.ApplicationLogic;
import ch.hesge.csim2.core.model.Application;
import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.Context;
import ch.hesge.csim2.core.model.IEngine;
import ch.hesge.csim2.core.model.Ontology;
import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.Scenario;
import ch.hesge.csim2.core.model.ScenarioStep;
import ch.hesge.csim2.core.model.SourceClass;
import ch.hesge.csim2.core.model.StemConcept;
import ch.hesge.csim2.ui.dialogs.AboutDialog;
import ch.hesge.csim2.ui.dialogs.ParametersDialog;
import ch.hesge.csim2.ui.dialogs.NameDialog;
import ch.hesge.csim2.ui.dialogs.ScenarioStepDialog;
import ch.hesge.csim2.ui.dialogs.SelectProjectDialog;
import ch.hesge.csim2.ui.dialogs.SettingsDialog;
import ch.hesge.csim2.ui.utils.SwingUtils;

/**
 * This class is responsible to handle all actions requested by user
 * through UI components.
 * 
 * @author Eric Harth
 */
public class ActionHandler {

	// Private attributes
	private MainView mainView;
	private Application application;

	/**
	 * Default constructor.
	 * 
	 * @param application
	 */
	public ActionHandler(Application application, MainView mainView) {
		this.application = application;
		this.mainView = mainView;
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
			Project project = ApplicationLogic.createProject(dialog.getNameField());
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
			ApplicationLogic.createScenario(scenarioName, application.getProject());
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
			ApplicationLogic.createScenarioStep(dialog.getNameField(), dialog.getDescriptionField(), scenario);
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
			ApplicationLogic.createOntology(ontologyName, application.getProject());
			reloadProject();
		}
	}
	
	/**
	 * Delete the project passed in argument.
	 * 
	 * @param project
	 *        the project to delete
	 */
	public void deleteProject(Project project) {

		// Display confirmation dialog
		int dialogResult = JOptionPane.showConfirmDialog(mainView, "Do you really want to delete the project '" + project.getName() + "' ?", "Warning", JOptionPane.YES_NO_OPTION);

		if (dialogResult == JOptionPane.YES_OPTION) {
			ApplicationLogic.deleteProject(project);
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
			ApplicationLogic.deleteScenario(scenario);
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
			ApplicationLogic.deleteScenarioStep(scenario, step);
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
			ApplicationLogic.deleteOntology(ontology);
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
			ApplicationLogic.saveScenario(scenario);
		}
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
			ApplicationLogic.saveScenario(step.getScenario());
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
			ApplicationLogic.saveProject(project);
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
			ApplicationLogic.saveScenario(scenario);
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
			ApplicationLogic.saveOntology(ontology);
			reloadProject();
		}
	}

	/**
	 * Close all views on workspace.
	 */
	public void closeAllViews() {
		mainView.resetWorkspace();
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
		mainView.getProjectTree().setProject(null);
		mainView.resetWorkspace();
		ApplicationLogic.clearCache();

		// Clear current application project
		application.setProject(project);
		
		// Load specified project
		if (project != null) {
			
			// Load specified project
			SwingUtils.invokeLongOperation(mainView.getRootPane(), new Runnable() {
				@Override
				public void run() {

					// Start loading project
					ApplicationLogic.loadProject(project);
					application.setProject(project);
					mainView.getProjectTree().setProject(project);
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
			ApplicationLogic.startEngine(engine);
		}
	}

	/**
	 * Stop the engine passed in argument.
	 * 
	 * @param engine
	 */
	public void stopEngine(IEngine engine) {
		ApplicationLogic.stopEngine(engine);
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
	 * Quit the application
	 */
	public void exitApplication() {
		System.exit(0);
	}
	
	/**
	 * Show the about dialog
	 */
	public void showAbout() {
		new AboutDialog(mainView).setVisible(true);
	}

	/**
	 * Show settings dialog
	 */
	public void showSettings() {
		new SettingsDialog(mainView).setVisible(true);
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
				mainView.showView(scenario.getName(), new ScenarioView(scenario, ActionHandler.this));
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

				// Retrieve required data from cache
				List<SourceClass> classes = ApplicationLogic.getSourceClasses(application.getProject());

				// Create the view
				mainView.showView("Sources", new StemSourcesView(application.getProject(), classes));
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

				// Retrieve required data from cache
				List<Concept> concepts = ApplicationLogic.getConcepts(application.getProject());
				Map<Integer, StemConcept> stemTree = ApplicationLogic.getStemConceptTreeMap(application.getProject());

				// Create the view
				mainView.showView("Concepts", new StemConceptsView(concepts, stemTree));
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

				// Retrieve required data from cache
				List<Scenario> scenarios = ApplicationLogic.getScenarios(application.getProject());

				// Create the view
				mainView.showView("Matching", new MatchingView(application.getProject(), scenarios));
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

				// Retrieve required data from cache
				List<Scenario> scenarios = ApplicationLogic.getScenarios(application.getProject());

				// Create the view
				mainView.showView("Traces", new TracesView(application.getProject(), scenarios));
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

				// Retrieve required data from cache
				List<Scenario> scenarios = ApplicationLogic.getScenarios(application.getProject());

				// Create the view
				mainView.showView("Timeseries", new TimeSeriesView(application.getProject(), scenarios));
			}
		});
	}
}