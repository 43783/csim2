package ch.hesge.csim2.ui.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.CWorkingArea;
import bibliothek.gui.dock.common.DefaultMultipleCDockable;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.MultipleCDockable;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.theme.ThemeMap;
import ch.hesge.csim2.core.logic.ApplicationLogic;
import ch.hesge.csim2.core.model.Application;
import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.Context;
import ch.hesge.csim2.core.model.IEngine;
import ch.hesge.csim2.core.model.MethodConceptMatch;
import ch.hesge.csim2.core.model.Ontology;
import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.Scenario;
import ch.hesge.csim2.core.model.SourceClass;
import ch.hesge.csim2.core.model.StemConcept;
import ch.hesge.csim2.core.utils.Console;
import ch.hesge.csim2.core.utils.StringUtils;
import ch.hesge.csim2.ui.dialogs.AboutDialog;
import ch.hesge.csim2.ui.dialogs.ParametersDialog;
import ch.hesge.csim2.ui.dialogs.ProjectDialog;
import ch.hesge.csim2.ui.dialogs.SettingsDialog;
import ch.hesge.csim2.ui.utils.SwingUtils;

/**
 * Main application frame
 * 
 * @author harth2
 *
 */
@SuppressWarnings("serial")
public class MainView extends JFrame implements ActionListener {

	// Private attributes
	private Application application;
	private Project project;

	private JMenuItem mnuNew;
	private JMenuItem mnuOpen;
	private JMenuItem mnuClose;
	private JMenuItem mnuSave;
	private JMenuItem mnuExit;
	private JMenuItem mnuCloseAll;
	private JMenuItem mnuSettings;
	private JMenuItem mnuAbout;

	private CControl dockControl;
	private CGrid dockGrid;
	private CWorkingArea workingArea;

	private ProjectView projectView;
	private ConsoleView consoleView;
	private EngineView engineView;

	private Dimension defaultSize = new Dimension(1024, 768);

	/**
	 * Default constructor
	 */
	public MainView() {
		initComponents();
	}

	/**
	 * Initialize the view components
	 */
	private void initComponents() {

		// Create the unique application instance
		application = ApplicationLogic.createApplication();

		// Load LAF specified config file (csim2.conf)
		String defaultLAF = (String) application.getProperties().getProperty(
				"look-and-feel");
		if (defaultLAF != null) {
			try {
				UIManager.setLookAndFeel(defaultLAF);
			} catch (Exception e) {
				Console.writeError("unable to load proper look-and-feel "
						+ StringUtils.toString(e));
			}
		}

		// Sets window properties
		setTitle("Csim2 Environment");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout(0, 0));

		// Initialize application icons
		List<Image> appIcons = new ArrayList<>();
		appIcons.add(Toolkit
				.getDefaultToolkit()
				.getImage(
						MainView.class
								.getResource("/ch/hesge/csim2/ui/icons/csim2-16x16.png")));
		appIcons.add(Toolkit
				.getDefaultToolkit()
				.getImage(
						MainView.class
								.getResource("/ch/hesge/csim2/ui/icons/csim2-32x32.png")));
		appIcons.add(Toolkit
				.getDefaultToolkit()
				.getImage(
						MainView.class
								.getResource("/ch/hesge/csim2/ui/icons/csim2-48x48.png")));
		appIcons.add(Toolkit
				.getDefaultToolkit()
				.getImage(
						MainView.class
								.getResource("/ch/hesge/csim2/ui/icons/csim2-72x72.png")));
		setIconImages(appIcons);

		// Create menu & statusbar
		initMenu();
		initStatusbar();

		// Initialize docking area
		dockControl = new CControl(this);
		dockControl.setTheme(ThemeMap.KEY_SMOOTH_THEME);
		dockGrid = new CGrid(dockControl);
		getContentPane().add(dockControl.getContentArea(), BorderLayout.CENTER);

		DefaultSingleCDockable docking;

		// Create project view
		projectView = new ProjectView();
		docking = new DefaultSingleCDockable("project", "Project");
		docking.setMinimizable(false);
		docking.setMaximizable(false);
		docking.setStackable(false);
		docking.setExternalizable(false);
		docking.setCloseable(false);
		docking.add(projectView);
		dockGrid.add(0, 0, 40, 100, docking);

		// Create workspace area
		workingArea = dockControl.createWorkingArea("workspace");
		dockGrid.add(40, 0, 100, 60, workingArea);

		// Create console view
		consoleView = new ConsoleView();
		docking = new DefaultSingleCDockable("console", "Console");
		docking.setMinimizable(false);
		docking.setExternalizable(false);
		docking.setCloseable(false);
		docking.add(consoleView);
		dockGrid.add(40, 60, 100, 40, docking);

		// Create console view
		engineView = new EngineView();
		docking = new DefaultSingleCDockable("engines", "Engines");
		docking.setMinimizable(false);
		docking.setExternalizable(false);
		docking.setCloseable(false);
		docking.add(engineView);
		dockGrid.add(40, 60, 100, 40, docking);

		// Deploy dockings on control
		dockControl.getContentArea().deploy(dockGrid);

		// Clear current project
		setProject(null);

		// Initialize the view when visible
		SwingUtils.invokeWhenVisible(this.getRootPane(), new Runnable() {
			@Override
			public void run() {
				initView();
			}
		});
	}

	/**
	 * Initialize the view and its components.
	 */
	private void initView() {

		// Retrieve windows size from csim2.conf file
		String windowSize = (String) application.getProperties().getProperty(
				"window-size");
		if (windowSize != null) {
			String[] widthHeight = windowSize.split("x");
			defaultSize.width = Integer.parseInt(widthHeight[0].trim());
			defaultSize.height = Integer.parseInt(widthHeight[1].trim());
		} else {
			defaultSize.width = 1024;
			defaultSize.height = 768;
		}

		// Compute main window size & position
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) (dimension.getWidth() - defaultSize.width) / 2;
		int y = (int) (dimension.getHeight() - defaultSize.height) / 2;
		setLocation(x, y);
		setSize(defaultSize.width, defaultSize.height);

		// Show the view
		setVisible(true);

		// Populate engine table
		List<IEngine> engineList = ApplicationLogic.getEngines();
		engineView.setEngines(engineList);

		// Redirect standard input/output to console
		SwingUtils.redirectStandardStreams(consoleView.getLogArea());
	}

	/**
	 * Initialize the application menu.
	 */
	private void initMenu() {

		JMenuBar menuBar = new JMenuBar();

		JMenu mnuFiles = new JMenu("Files");
		menuBar.add(mnuFiles);

		mnuNew = new JMenuItem("New Project");
		mnuNew.setIcon(new ImageIcon(MainView.class
				.getResource("/ch/hesge/csim2/ui/icons/new.png")));
		mnuNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				InputEvent.CTRL_MASK));
		mnuNew.addActionListener(this);
		mnuFiles.add(mnuNew);

		mnuFiles.add(new JSeparator());

		mnuOpen = new JMenuItem("Open Project");
		mnuOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				InputEvent.CTRL_MASK));
		mnuOpen.setIcon(new ImageIcon(MainView.class
				.getResource("/ch/hesge/csim2/ui/icons/project.png")));
		mnuOpen.addActionListener(this);
		mnuFiles.add(mnuOpen);

		mnuClose = new JMenuItem("Close Project");
		mnuClose.setIcon(new ImageIcon(MainView.class
				.getResource("/ch/hesge/csim2/ui/icons/close.png")));
		mnuClose.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,
				InputEvent.CTRL_MASK));
		mnuClose.setEnabled(false);
		mnuClose.addActionListener(this);
		mnuFiles.add(mnuClose);

		mnuSave = new JMenuItem("Save Project");
		mnuSave.setIcon(new ImageIcon(MainView.class
				.getResource("/ch/hesge/csim2/ui/icons/save.png")));
		mnuSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				InputEvent.CTRL_MASK));
		mnuSave.setEnabled(false);
		mnuSave.addActionListener(this);
		mnuFiles.add(mnuSave);

		mnuFiles.add(new JSeparator());

		mnuExit = new JMenuItem("Exit");
		mnuExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4,
				InputEvent.ALT_MASK));
		mnuExit.addActionListener(this);
		mnuFiles.add(mnuExit);

		JMenu mnuViews = new JMenu("Views");
		menuBar.add(mnuViews);

		mnuCloseAll = new JMenuItem("Close All");
		mnuCloseAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,
				InputEvent.SHIFT_MASK | InputEvent.CTRL_MASK));
		mnuCloseAll.setEnabled(false);
		mnuCloseAll.addActionListener(this);
		mnuViews.add(mnuCloseAll);

		JMenu mnuTools = new JMenu("Tools");
		menuBar.add(mnuTools);

		mnuSettings = new JMenuItem("Settings");
		mnuSettings.setIcon(new ImageIcon(MainView.class
				.getResource("/ch/hesge/csim2/ui/icons/settings.png")));
		mnuSettings.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,
				InputEvent.ALT_MASK));
		mnuSettings.addActionListener(this);
		mnuTools.add(mnuSettings);

		JMenu mnuHelp = new JMenu("Help");
		menuBar.add(mnuHelp);

		mnuAbout = new JMenuItem("About");
		mnuAbout.setIcon(new ImageIcon(MainView.class
				.getResource("/ch/hesge/csim2/ui/icons/help.png")));
		mnuAbout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		mnuAbout.addActionListener(this);
		mnuHelp.add(mnuAbout);

		setJMenuBar(menuBar);
	}

	/**
	 * Initialize the application statusbar.
	 */
	private void initStatusbar() {

		JPanel statusPanel = new JPanel();
		statusPanel.setLayout(new BorderLayout(0, 0));
		statusPanel.setBorder(new EmptyBorder(2, 10, 2, 10));
		JLabel statusLabel = new JLabel(
				"Copyright © 2010-2015, HEG Geneva, Switzerland. All rights reserved.");
		statusPanel.add(statusLabel);
		getContentPane().add(statusPanel, BorderLayout.SOUTH);
	}

	/**
	 * Return the application model.
	 * 
	 * @return the instance of the model
	 */
	public Application getApplication() {
		return application;
	}

	/**
	 * Show the view passed in argument within a docking area.
	 * 
	 * @param title
	 * @param view
	 */
	public void showView(String title, JComponent view) {

		MultipleCDockable dockable1 = dockControl.getMultipleDockable(title);
		SingleCDockable dockable2 = dockControl.getSingleDockable(title);
		
		if (dockable1 != null) {
			dockable1.setVisible(true);
		}
		else {

			DefaultMultipleCDockable editor = new DefaultMultipleCDockable(null);
			editor.setTitleText(title);
			editor.setMinimizable(false);
			editor.setExternalizable(false);
			editor.setCloseable(true);
			editor.add(view);

			workingArea.show(editor);
			editor.toFront();
		}
	}

	/**
	 * Change current project
	 * 
	 * @param project
	 */
	public void setProject(Project project) {

		if (project == null) {

			setTitle("Csim2 Environment");
			this.project = null;
			
			/*
			for (int i = 0 ; i < workingArea.getWorkingArea().getCDockableCount(); i++) {
				MultipleCDockable dockable = (MultipleCDockable)CDockable(i);
				dockControl.removeDockable(dockable);
			}
			*/
			// showView(null);

			mnuClose.setEnabled(false);
			mnuCloseAll.setEnabled(false);
			mnuSave.setEnabled(false);

			// Clear application data
			application.setProject(null);
			ApplicationLogic.clearCache();
			projectView.getProjectTree().setProject(null);
		} else {
			setTitle("Csim2 Environment - " + project.getName());

			mnuClose.setEnabled(true);
			mnuCloseAll.setEnabled(true);
			mnuSave.setEnabled(true);

			// Clear application data
			// showView(null);
			this.project = project;
			ApplicationLogic.clearCache();

			//dockControl.segetRegister().gete rid.select(0,  0, width, height, dockable);
			// consoleView.setActiveTabIndex(0);
			consoleView.clearLogConsole();

			SwingUtils.invokeLongOperation(this.getRootPane(), new Runnable() {
				@Override
				public void run() {
					ApplicationLogic.loadProject(project);
					application.setProject(project);
					projectView.getProjectTree().setProject(project);
				}
			});
		}
	}

	/**
	 * Handle action generated by menu.
	 */
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == mnuAbout) {
			new AboutDialog(this).setVisible(true);
		} else if (e.getSource() == mnuNew) {
			JOptionPane.showMessageDialog(this,
					"This feature is not yet implemented !", "Warning",
					JOptionPane.WARNING_MESSAGE);
		} else if (e.getSource() == mnuOpen) {
			new ProjectDialog(this).setVisible(true);
		} else if (e.getSource() == mnuClose) {
			setProject(null);
		} else if (e.getSource() == mnuCloseAll) {
			JOptionPane.showMessageDialog(this,
					"This feature is not yet implemented !", "Warning",
					JOptionPane.WARNING_MESSAGE);
		} else if (e.getSource() == mnuSettings) {
			new SettingsDialog(this).setVisible(true);
		} else if (e.getSource() == mnuExit) {
			System.exit(0);
		}
	}

	/**
	 * Show the scenario view
	 * 
	 * @param scenario
	 */
	public void showScenario(Scenario scenario) {

		SwingUtils.invokeLongOperation(this.getRootPane(), new Runnable() {
			@Override
			public void run() {

				// Retrieve step associated to the scenario
				ApplicationLogic.getScenarioWithDependencies(scenario);

				// Create the view
				showView(scenario.getName(), new ScenarioView(scenario));
			}
		});
	}

	/**
	 * Show the ontology view
	 * 
	 * @param ontology
	 */
	public void showOntology(Ontology ontology) {

		SwingUtils.invokeLongOperation(this.getRootPane(), new Runnable() {
			@Override
			public void run() {

				// Create the view
				showView(ontology.getName(), new OntologyView(ontology));
			}
		});
	}

	/**
	 * Show the stem source view
	 */
	public void showSourceStems() {

		SwingUtils.invokeLongOperation(this.getRootPane(), new Runnable() {
			@Override
			public void run() {

				// Retrieve required data from cache
				List<SourceClass> classes = ApplicationLogic
						.getSourceClassesWithDependencies(project, false);

				// Create the view
				showView("Sources", new StemSourcesView(project, classes));
			}
		});
	}

	/**
	 * Show the stem concepts view
	 */
	public void showConceptStems() {

		SwingUtils.invokeLongOperation(this.getRootPane(), new Runnable() {
			@Override
			public void run() {

				// Retrieve required data from cache
				List<Concept> concepts = ApplicationLogic.getConcepts(project);
				Map<Integer, StemConcept> stemTree = ApplicationLogic
						.getStemConceptTreeMap(project);

				// Create the view
				showView("Concepts", new StemConceptsView(concepts, stemTree));
			}
		});
	}

	/**
	 * Show the method concept matching view
	 */
	public void showMatchingView() {

		SwingUtils.invokeLongOperation(this.getRootPane(), new Runnable() {
			@Override
			public void run() {

				// Retrieve required data from cache
				List<MethodConceptMatch> matchings = ApplicationLogic
						.getMatchingsWithDependencies(project);

				// Create the view
				showView("Matchings", new MatchingView(matchings));
			}
		});
	}

	/**
	 * Show the trace graph view
	 */
	public void showTraceView() {

		SwingUtils.invokeLongOperation(this.getRootPane(), new Runnable() {
			@Override
			public void run() {

				// Retrieve required data from cache
				List<Scenario> scenarios = ApplicationLogic
						.getScenarios(project);

				// Create the view
				showView("Traces", new TracesView(project, scenarios));
			}
		});
	}

	/**
	 * Show the time series view
	 */
	public void showTimeSeriesView() {

		SwingUtils.invokeLongOperation(this.getRootPane(), new Runnable() {
			@Override
			public void run() {

				// Retrieve required data from cache
				List<Scenario> scenarios = ApplicationLogic
						.getScenarios(project);

				// Create the view
				showView("Timeseries", new TimeSeriesView(project, scenarios));
			}
		});
	}

	/**
	 * Start the engine passed in argument.
	 * 
	 * @param engine
	 */
	public void startEngine(IEngine engine) {

		// First display parameter view
		ParametersDialog dialog = new ParametersDialog(this);
		dialog.setEngine(engine);
		dialog.setVisible(true);

		if (dialog.getDialogResult()) {

			// Prepare the engine context
			Context context = new Context();
			context.putAll(application.getProperties());
			context.putAll(dialog.getParameters());
			engine.setContext(context);

			// // Start the engine
			// consoleView.setActiveTabIndex(0);
			// consoleView.clearLogConsole();

			ApplicationLogic.startEngine(engine);
		}
	}

	/**
	 * Stop the engine passed in argument.
	 * 
	 * @param engine
	 */
	public void stopEngine(IEngine engine) {

		// consoleView.setActiveTabIndex(0);
		// ApplicationLogic.stopEngine(engine);
	}

	/**
	 * Clear console content.
	 */
	public void clearLogConsole() {
		// consoleView.clearLogConsole();
	}
}
