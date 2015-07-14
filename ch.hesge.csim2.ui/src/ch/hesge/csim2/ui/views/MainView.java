package ch.hesge.csim2.ui.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.CWorkingArea;
import bibliothek.gui.dock.common.DefaultMultipleCDockable;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.event.CVetoClosingEvent;
import bibliothek.gui.dock.common.event.CVetoClosingListener;
import ch.hesge.csim2.core.logic.ApplicationLogic;
import ch.hesge.csim2.core.model.Application;
import ch.hesge.csim2.core.model.IEngine;
import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.utils.Console;
import ch.hesge.csim2.core.utils.StringUtils;
import ch.hesge.csim2.ui.comp.ProjectTree;
import ch.hesge.csim2.ui.utils.SwingAppender;

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
	private ActionHandler actionHandler;

	private JMenuItem mnuNew;
	private JMenuItem mnuOpen;
	private JMenuItem mnuClose;
	private JMenuItem mnuExit;
	private JCheckBoxMenuItem mnuProject;
	private JCheckBoxMenuItem mnuEngine;
	private JCheckBoxMenuItem mnuConsole;
	private JMenuItem mnuCloseAll;
	private JMenuItem mnuSettings;
	private JMenuItem mnuAbout;

	private CControl dockingControl;
	private CWorkingArea workspace;
	private Map<String, JComponent> views;
	private Map<String, DefaultMultipleCDockable> dockables;

	private Dimension defaultSize = new Dimension(1024, 768);

	private ProjectView projectView;
	private ConsoleView consoleView;
	private EngineView  engineView;
	
	private DefaultSingleCDockable projectWrapper;
	private DefaultSingleCDockable consoleWrapper;
	private DefaultSingleCDockable engineWrapper;

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

		// Sets window properties
		setTitle("Csim2 Environment");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout(0, 0));

		// Initialize docking area
		dockingControl = new CControl(this);
		getContentPane().add(dockingControl.getContentArea(), BorderLayout.CENTER);

		// Create view/dockable maps
		views = new ConcurrentHashMap<>();
		dockables = new ConcurrentHashMap<>();

		// Create the object responsible to handle all UI actions
		application = ApplicationLogic.createApplication();
		actionHandler = new ActionHandler(application, this);

		// Init main layout
		initLAF();
		initMenu();
		initStatusbar();
		initLayout();
		initListeners();

		// Reset current project
		application.setProject(null);
		actionHandler.reloadProject();
	}

	/**
	 * Initialize look and feel.
	 */
	private void initLAF() {

		// Load LAF specified in config file (csim2.conf)
		String defaultLAF = (String) application.getProperties().getProperty("look-and-feel");
		if (defaultLAF != null) {
			try {
				UIManager.setLookAndFeel(defaultLAF);
			}
			catch (Exception e) {
				Console.writeError(this, "unable to load proper look-and-feel " + StringUtils.toString(e));
			}
		}

		// Initialize application icons
		List<Image> appIcons = new ArrayList<>();
		appIcons.add(Toolkit.getDefaultToolkit().getImage(MainView.class.getResource("/ch/hesge/csim2/ui/icons/csim2-16x16.png")));
		appIcons.add(Toolkit.getDefaultToolkit().getImage(MainView.class.getResource("/ch/hesge/csim2/ui/icons/csim2-32x32.png")));
		appIcons.add(Toolkit.getDefaultToolkit().getImage(MainView.class.getResource("/ch/hesge/csim2/ui/icons/csim2-48x48.png")));
		appIcons.add(Toolkit.getDefaultToolkit().getImage(MainView.class.getResource("/ch/hesge/csim2/ui/icons/csim2-72x72.png")));
		setIconImages(appIcons);
	}

	/**
	 * Initialize docking layout
	 */
	private void initLayout() {

		// Create the workspace area
		workspace = dockingControl.createWorkingArea("workspace");
		workspace.setLocation(CLocation.base().normal());
		dockingControl.addDockable(workspace);
		workspace.setVisible(true);

		// Create console view (on south)
		consoleView = new ConsoleView();
		consoleWrapper = new DefaultSingleCDockable("console");
		consoleWrapper.setTitleText("Console");
		consoleWrapper.setMinimizable(false);
		consoleWrapper.setMaximizable(true);
		consoleWrapper.setExternalizable(false);
		consoleWrapper.setCloseable(true);
		consoleWrapper.setLocation(CLocation.base().normalSouth(0.3));
		consoleWrapper.add(consoleView);
		
		consoleWrapper.addVetoClosingListener(new CVetoClosingListener() {
			@Override
			public void closed(CVetoClosingEvent event) {
				mnuConsole.setSelected(false);
			}
			@Override
			public void closing(CVetoClosingEvent event) {
				// Never vetoing
			}
		});

		dockingControl.addDockable(consoleWrapper);
		consoleWrapper.setVisible(true);

		// Create engines view (on south)
		engineView = new EngineView(actionHandler);
		engineWrapper = new DefaultSingleCDockable("engines");
		engineWrapper.setTitleText("Engines");
		engineWrapper.setMinimizable(false);
		engineWrapper.setMaximizable(true);
		engineWrapper.setExternalizable(false);
		engineWrapper.setCloseable(true);
		engineWrapper.setLocation(CLocation.base().normalSouth(0.3));
		engineWrapper.add(engineView);
		
		engineWrapper.addVetoClosingListener(new CVetoClosingListener() {
			@Override
			public void closed(CVetoClosingEvent event) {
				mnuEngine.setSelected(false);
			}
			@Override
			public void closing(CVetoClosingEvent event) {
				// Never vetoing
			}
		});

		dockingControl.addDockable(engineWrapper);
		engineWrapper.setVisible(true);
		
		// Create project view (on west)
		projectView = new ProjectView(this);
		projectWrapper = new DefaultSingleCDockable("project");
		projectWrapper.setTitleText("Project");
		projectWrapper.setMinimizable(false);
		projectWrapper.setMaximizable(false);
		projectWrapper.setExternalizable(false);
		projectWrapper.setCloseable(true);
		projectWrapper.setLocation(CLocation.base().normalWest(0.3));
		projectWrapper.add(projectView);
		
		projectWrapper.addVetoClosingListener(new CVetoClosingListener() {
			@Override
			public void closed(CVetoClosingEvent event) {
				mnuProject.setSelected(false);
			}
			@Override
			public void closing(CVetoClosingEvent event) {
				// Never vetoing
			}
		});

		dockingControl.addDockable(projectWrapper);
		projectWrapper.setVisible(true);
	}

	/**
	 * Initialize the application menu.
	 */
	private void initMenu() {

		JMenuBar menuBar = new JMenuBar();

		JMenu mnuFiles = new JMenu("Files");
		menuBar.add(mnuFiles);

		mnuNew = new JMenuItem("New Project");
		mnuNew.setIcon(new ImageIcon(MainView.class.getResource("/ch/hesge/csim2/ui/icons/new.png")));
		mnuNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
		mnuNew.addActionListener(this);
		mnuFiles.add(mnuNew);

		mnuFiles.add(new JSeparator());

		mnuOpen = new JMenuItem("Open Project");
		mnuOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		mnuOpen.setIcon(new ImageIcon(MainView.class.getResource("/ch/hesge/csim2/ui/icons/project.png")));
		mnuOpen.addActionListener(this);
		mnuFiles.add(mnuOpen);

		mnuClose = new JMenuItem("Close Project");
		mnuClose.setIcon(new ImageIcon(MainView.class.getResource("/ch/hesge/csim2/ui/icons/close.png")));
		mnuClose.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_MASK));
		mnuClose.setEnabled(false);
		mnuClose.addActionListener(this);
		mnuFiles.add(mnuClose);

		mnuFiles.add(new JSeparator());

		mnuExit = new JMenuItem("Exit");
		mnuExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK));
		mnuExit.addActionListener(this);
		mnuFiles.add(mnuExit);

		JMenu mnuViews = new JMenu("Views");
		menuBar.add(mnuViews);

		mnuProject = new JCheckBoxMenuItem("Project");
		mnuProject.setEnabled(true);
		mnuProject.setSelected(true);
		mnuProject.addActionListener(this);
		mnuViews.add(mnuProject);

		mnuEngine = new JCheckBoxMenuItem("Engines");
		mnuEngine.setEnabled(true);
		mnuEngine.setSelected(true);
		mnuEngine.addActionListener(this);
		mnuViews.add(mnuEngine);

		mnuConsole = new JCheckBoxMenuItem("Console");
		mnuConsole.setEnabled(true);
		mnuConsole.setSelected(true);
		mnuConsole.addActionListener(this);
		mnuViews.add(mnuConsole);

		mnuViews.add(new JSeparator());

		mnuCloseAll = new JMenuItem("Close All");
		mnuCloseAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.SHIFT_MASK | InputEvent.CTRL_MASK));
		mnuCloseAll.setEnabled(false);
		mnuCloseAll.addActionListener(this);
		mnuViews.add(mnuCloseAll);

		JMenu mnuTools = new JMenu("Tools");
		menuBar.add(mnuTools);

		mnuSettings = new JMenuItem("Settings");
		mnuSettings.setIcon(new ImageIcon(MainView.class.getResource("/ch/hesge/csim2/ui/icons/settings.png")));
		mnuSettings.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.ALT_MASK));
		mnuSettings.addActionListener(this);
		mnuTools.add(mnuSettings);

		JMenu mnuHelp = new JMenu("Help");
		menuBar.add(mnuHelp);

		mnuAbout = new JMenuItem("About");
		mnuAbout.setIcon(new ImageIcon(MainView.class.getResource("/ch/hesge/csim2/ui/icons/help.png")));
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
		JLabel statusLabel = new JLabel("Copyright © 2010-2015, HEG Geneva, Switzerland. All rights reserved.");
		statusPanel.add(statusLabel);
		getContentPane().add(statusPanel, BorderLayout.SOUTH);
	}

	/**
	 * Initialize the application listener.
	 */
	private void initListeners() {

		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowOpened(WindowEvent e) {
				initView();
			}

			@Override
			public void windowClosing(WindowEvent e) {
				ApplicationLogic.shutdownApplication(application);
			}
		});
	}

	/**
	 * Initialize the view and its data.
	 */
	private void initView() {

		// Retrieve windows size from csim2.conf file
		String windowSize = (String) application.getProperties().getProperty("window-size");
		if (windowSize != null) {
			String[] widthHeight = windowSize.split("x");
			defaultSize.width = Integer.parseInt(widthHeight[0].trim());
			defaultSize.height = Integer.parseInt(widthHeight[1].trim());
		}
		else {
			defaultSize.width = 1024;
			defaultSize.height = 768;
		}

		// Compute main window size & position
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) (dimension.getWidth() - defaultSize.width) / 2;
		int y = (int) (dimension.getHeight() - defaultSize.height) / 2;
		setLocation(x, y);
		setSize(defaultSize.width, defaultSize.height);

		// Redirect log appender to textarea
		SwingAppender.setTextArea(consoleView.getLogArea());
		Console.writeInfo(this, "Csim2 v" + application.getVersion() + " initialized.");

		// Populate engine table
		List<IEngine> engineList = ApplicationLogic.getEngines();
		engineView.setEngines(engineList);
	}

	/**
	 * Handle action generated by menu.
	 */
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == mnuAbout) {
			actionHandler.showAbout();
		}
		else if (e.getSource() == mnuNew) {
			actionHandler.createNewProject();
		}
		else if (e.getSource() == mnuOpen) {
			actionHandler.selectProject();
		}
		else if (e.getSource() == mnuClose) {
			actionHandler.closeProject();
		}
		else if (e.getSource() == mnuCloseAll) {
			actionHandler.closeAllViews();
		}
		else if (e.getSource() == mnuSettings) {
			actionHandler.showSettings();
		}
		else if (e.getSource() == mnuExit) {
			actionHandler.exitApplication();
		}
		else if (e.getSource() == mnuProject) {
			projectWrapper.setVisible(mnuProject.isSelected());
		}
		else if (e.getSource() == mnuConsole) {
			consoleWrapper.setVisible(mnuConsole.isSelected());
		}
		else if (e.getSource() == mnuEngine) {
			engineWrapper.setVisible(mnuEngine.isSelected());
		}
	}

	/**
	 * Return the application object.
	 * 
	 * @return and instance of ApplicationHandler
	 */
	public Application getApplication() {
		return application;
	}

	/**
	 * Return the object responsible to handle all user action.
	 * 
	 * @return and instance of ActionHandler
	 */
	public ActionHandler getActionHandler() {
		return actionHandler;
	}

	/**
	 * Return the tree displaying project structure.
	 * 
	 * @return and instance of ProjectTree
	 */
	public ProjectTree getProjectTree() {
		return projectView.getProjectTree();
	}
	
	/**
	 * Return the object responsible to handle all user action.
	 * 
	 * @return and instance of ActionHandler
	 */
	public void setProjectDisplayName(Project project) {

		if (project == null) {
			setTitle("Csim2 Environment");
			mnuClose.setEnabled(false);
			mnuCloseAll.setEnabled(false);
		}
		else {
			setTitle("Csim2 Environment - " + project.getName());
			mnuClose.setEnabled(true);
			mnuCloseAll.setEnabled(true);
		}
	}
	
	/**
	 * Show the view passed in argument within the workspace area.
	 * 
	 * @param title
	 *        title of the view
	 * @param view
	 *        the view itself
	 */
	public void showView(String title, JComponent view) {
		
		// If view already exists, just display it
		if (views.containsKey(title)) {
			dockables.get(title).toFront();
		}
		else {
			
			// Otherwise, create a new one
			DefaultMultipleCDockable dockable = new DefaultMultipleCDockable(null);

			dockable.setTitleText(title);
			dockable.setMinimizable(false);
			dockable.setMaximizable(true);
			dockable.setExternalizable(false);
			dockable.setCloseable(true);
			dockable.setRemoveOnClose(true);
			
			dockable.addVetoClosingListener(new CVetoClosingListener() {
				@Override
				public void closed(CVetoClosingEvent event) {
					views.remove(title, view);
					dockables.remove(title, dockable);
				}
				@Override
				public void closing(CVetoClosingEvent event) {
					// Never vetoing
				}
			});
			
			dockable.add(view);
			
			// Keep track off new view
			views.put(title, view);
			dockables.put(title, dockable);

			// Display the view
			workspace.show(dockable);
			dockable.toFront();
		}
	}
	
	/**
	 * Remove all dockables from workspace
	 */
	public void resetWorkspace() {
		
		for (DefaultMultipleCDockable dockable : dockables.values()) {
			dockingControl.removeDockable(dockable);
		}
	}
	
}
