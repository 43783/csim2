package ch.hesge.csim2.ui.tree;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import ch.hesge.csim2.core.model.Ontology;
import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.Scenario;
import ch.hesge.csim2.ui.model.ApplicationManager;
import ch.hesge.csim2.ui.popup.AnalysisPopup;
import ch.hesge.csim2.ui.popup.OntologyPopup;
import ch.hesge.csim2.ui.popup.ProjectPopup;
import ch.hesge.csim2.ui.popup.ScenarioPopup;
import ch.hesge.csim2.ui.utils.SimpleAction;
import ch.hesge.csim2.ui.utils.SwingUtils;
import ch.hesge.csim2.ui.views.MainView;

@SuppressWarnings("serial")
public class ProjectTree extends JTree {

	// Private attributes
	private Project project;
	private ApplicationManager appManager;
	private ProjectPopup projectPopup;
	private ScenarioPopup scenarioPopup;
	private OntologyPopup ontologyPopup;
	private AnalysisPopup analysisPopup;

	/**
	 * Default constructor
	 */
	public ProjectTree(ApplicationManager appManager) {

		setModel(null);
		setEnabled(false);
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		this.appManager = appManager;
		this.projectPopup  = new ProjectPopup();
		this.scenarioPopup = new ScenarioPopup();
		this.ontologyPopup = new OntologyPopup();
		this.analysisPopup = new AnalysisPopup();
	}

	/**
	 * Return project displayed by this tree
	 * 
	 * @return the project
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * Sets project displayed by this tree
	 * 
	 * @param project
	 *        the project to set
	 */
	public void setProject(Project project) {

		this.project = project;

		if (project == null) {
			setModel(null);
			setEnabled(false);
		}
		else {
			initModel();
			initRenderer();
			initListeners();
			setEnabled(true);
		}
	}

	/**
	 * Initialize tree model and its hierarchy
	 */
	private void initModel() {

		// Create main project node
		DefaultMutableTreeNode projectNode = new DefaultMutableTreeNode(project);

		// Create scenario node
		DefaultMutableTreeNode scenariosNode = new DefaultMutableTreeNode("Scenarios");
		projectNode.add(scenariosNode);
		for (Scenario scenario : project.getScenarios()) {
			scenariosNode.add(new DefaultMutableTreeNode(scenario));
		}

		// Create the ontology node
		DefaultMutableTreeNode ontologiesNode = new DefaultMutableTreeNode("Ontologies");
		projectNode.add(ontologiesNode);
		for (Ontology ontology : project.getOntologies()) {
			ontologiesNode.add(new DefaultMutableTreeNode(ontology));
		}

		// Create the analysis node
		DefaultMutableTreeNode analysisNode = new DefaultMutableTreeNode("Analysis");
		projectNode.add(analysisNode);
		analysisNode.add(new DefaultMutableTreeNode("Sources"));
		analysisNode.add(new DefaultMutableTreeNode("Concepts"));
		analysisNode.add(new DefaultMutableTreeNode("Matching"));
		analysisNode.add(new DefaultMutableTreeNode("Traces"));
		analysisNode.add(new DefaultMutableTreeNode("TimeSeries"));

		// Set new model
		setModel(new DefaultTreeModel(projectNode));

		// Expand first two levels
		Enumeration<?> rootNodes = ((TreeNode) getModel().getRoot()).children();
		while (rootNodes.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) rootNodes.nextElement();
			expandPath(new TreePath(node.getPath()));
		}
	}

	/**
	 * Initialize tree node renderer
	 */
	private void initRenderer() {

		setCellRenderer(new DefaultTreeCellRenderer() {
			@Override
			public Component getTreeCellRendererComponent(JTree tree, Object node, boolean isSelected, boolean isExpanded, boolean isLeaf, int row, boolean hasFocus) {

				JLabel cellRenderer = (JLabel) super.getTreeCellRendererComponent(tree, node, isSelected, isExpanded, isLeaf, row, hasFocus);
				Object userObject = ((DefaultMutableTreeNode) node).getUserObject();

				if (userObject instanceof Project) {
					Project project = (Project) userObject;
					cellRenderer.setText(project.getName());
					cellRenderer.setFont(cellRenderer.getFont().deriveFont(Font.BOLD));
					cellRenderer.setIcon(new ImageIcon(MainView.class.getResource("/ch/hesge/csim2/ui/icons/project.png")));
				}
				else if (userObject instanceof Scenario) {
					Scenario scenario = (Scenario) userObject;
					cellRenderer.setText(scenario.getName());
					cellRenderer.setFont(cellRenderer.getFont().deriveFont(Font.PLAIN));
					cellRenderer.setIcon(new ImageIcon(MainView.class.getResource("/ch/hesge/csim2/ui/icons/scenario.png")));
				}
				else if (userObject instanceof Ontology) {
					Ontology ontology = (Ontology) userObject;
					cellRenderer.setText(ontology.getName());
					cellRenderer.setFont(cellRenderer.getFont().deriveFont(Font.PLAIN));
					cellRenderer.setIcon(new ImageIcon(MainView.class.getResource("/ch/hesge/csim2/ui/icons/ontology.png")));
				}
				else if (userObject.toString().equals("Sources")) {
					cellRenderer.setIcon(new ImageIcon(MainView.class.getResource("/ch/hesge/csim2/ui/icons/class.png")));
				}
				else if (userObject.toString().equals("Concepts")) {
					cellRenderer.setIcon(new ImageIcon(MainView.class.getResource("/ch/hesge/csim2/ui/icons/concept.png")));
				}
				else if (userObject.toString().equals("Matching")) {
					cellRenderer.setIcon(new ImageIcon(MainView.class.getResource("/ch/hesge/csim2/ui/icons/match.png")));
				}
				else if (userObject.toString().equals("Traces")) {
					cellRenderer.setIcon(new ImageIcon(MainView.class.getResource("/ch/hesge/csim2/ui/icons/trace.png")));
				}
				else if (userObject.toString().equals("TimeSeries")) {
					cellRenderer.setIcon(new ImageIcon(MainView.class.getResource("/ch/hesge/csim2/ui/icons/chart.png")));
				}
				else {
					cellRenderer.setText(userObject.toString());
					cellRenderer.setFont(cellRenderer.getFont().deriveFont(Font.PLAIN));
					cellRenderer.setIcon(new ImageIcon(MainView.class.getResource("/ch/hesge/csim2/ui/icons/folder.png")));
				}

				return this;
			}
		});
	}

	/**
	 * Initialize tree node renderer
	 */
	private void initListeners() {

		SwingUtils.onTreeDoubleClick(this, new SimpleAction<MouseEvent>() {
			@Override
			public void run(MouseEvent e) {
				onDoubleClick(e);

			}
		});

		SwingUtils.onTreeRightClick(this, new SimpleAction<MouseEvent>() {
			@Override
			public void run(MouseEvent e) {
				onRightClick(e);
			}
		});
	}

	/**
	 * User as double clicked on tree
	 */
	private void onDoubleClick(MouseEvent e) {

		// Retrieve user object under the mouse
		Object userObject = SwingUtils.getTreeUserObject(this, e.getX(), e.getY());

		if (userObject != null) {

			if (userObject instanceof Scenario) {
				appManager.showScenario((Scenario) userObject);
			}
			else if (userObject instanceof Ontology) {
				appManager.showOntology((Ontology) userObject);
			}
			else if (userObject.toString().equals("Sources")) {
				appManager.showSourceStems();
			}
			else if (userObject.toString().equals("Concepts")) {
				appManager.showConceptStems();
			}
			else if (userObject.toString().equals("Matching")) {
				appManager.showMatching();
			}
			else if (userObject.toString().equals("Traces")) {
				appManager.showTraceView();
			}
			else if (userObject.toString().equals("TimeSeries")) {
				appManager.showTimeSeriesView();
			}
		}
	}

	/**
	 * User as right clicked on tree
	 */
	private void onRightClick(MouseEvent e) {

		// Retrieve current user object
		Object userObject = SwingUtils.getTreeUserObject(this, e.getX(), e.getY());

		if (userObject != null) {
			
			if (userObject instanceof Project) {
				projectPopup.setProject(project);
				projectPopup.show(e.getComponent(), e.getX(), e.getY());
			}
			else if (userObject.toString().equals("Scenarios")) {
				scenarioPopup.clearMenuState();
				scenarioPopup.setCreateMenuState(true);
				scenarioPopup.show(e.getComponent(), e.getX(), e.getY());
			}
			else if (userObject.toString().equals("Ontologies")) {
				ontologyPopup.clearMenuState();
				ontologyPopup.setCreateMenuState(true);
				ontologyPopup.show(e.getComponent(), e.getX(), e.getY());
			}
			else if (userObject.toString().equals("Analysis")) {
				analysisPopup.clearMenuState();
				analysisPopup.show(e.getComponent(), e.getX(), e.getY());
			}
			else if (userObject instanceof Scenario) {
				scenarioPopup.clearMenuState();
				scenarioPopup.setRenameMenuState(true);
				scenarioPopup.setDeleteMenuState(true);
				scenarioPopup.setOpenMenuState(true);
				scenarioPopup.show(e.getComponent(), e.getX(), e.getY());
				scenarioPopup.setScenario((Scenario) userObject);
			}
			else if (userObject instanceof Ontology) {
				ontologyPopup.clearMenuState();
				ontologyPopup.setRenameMenuState(true);
				ontologyPopup.setOpenMenuState(true);
				ontologyPopup.setDeleteMenuState(true);
				ontologyPopup.show(e.getComponent(), e.getX(), e.getY());
				ontologyPopup.setOntology((Ontology) userObject);
			}
			else if (userObject.toString().equals("Sources")) {
				analysisPopup.clearMenuState();
				analysisPopup.setSourceStemMenuState(true);
				analysisPopup.show(e.getComponent(), e.getX(), e.getY());
			}
			else if (userObject.toString().equals("Concepts")) {
				analysisPopup.clearMenuState();
				analysisPopup.setConceptStemMenuState(true);
				analysisPopup.show(e.getComponent(), e.getX(), e.getY());
			}
			else if (userObject.toString().equals("Matching")) {
				analysisPopup.clearMenuState();
				analysisPopup.setMatchingMenuState(true);
				analysisPopup.show(e.getComponent(), e.getX(), e.getY());
			}
			else if (userObject.toString().equals("Traces")) {
				analysisPopup.clearMenuState();
				analysisPopup.setTraceMenuState(true);
				analysisPopup.show(e.getComponent(), e.getX(), e.getY());
			}
			else if (userObject.toString().equals("TimeSeries")) {
				analysisPopup.clearMenuState();
				analysisPopup.setTimeSeriesMenuState(true);
				analysisPopup.show(e.getComponent(), e.getX(), e.getY());
			}		}
	}
}
