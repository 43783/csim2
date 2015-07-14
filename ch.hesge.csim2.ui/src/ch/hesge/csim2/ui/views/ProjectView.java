package ch.hesge.csim2.ui.views;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ch.hesge.csim2.ui.comp.ProjectTree;

@SuppressWarnings("serial")
public class ProjectView extends JPanel {

	// Private attributes
	private ProjectTree	projectTree;

	/**
	 * Default constructor
	 */
	public ProjectView(MainView mainView) {
		
		setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		projectTree = new ProjectTree(mainView.getActionHandler());
		scrollPane.setViewportView(projectTree);

		add(scrollPane);
	}

    /**
	 * @return the projectTree
	 */
	public ProjectTree getProjectTree() {
		return projectTree;
	}

	/**
	 * @param projectTree
	 *            the projectTree to set
	 */
	public void setProjectTree(ProjectTree projectTree) {
		this.projectTree = projectTree;
	}
}
