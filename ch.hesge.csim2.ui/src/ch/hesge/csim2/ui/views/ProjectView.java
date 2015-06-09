package ch.hesge.csim2.ui.views;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.vldocking.swing.docking.DockKey;
import com.vldocking.swing.docking.Dockable;

import ch.hesge.csim2.ui.comp.ProjectTree;

@SuppressWarnings("serial")
public class ProjectView extends JPanel implements Dockable {

	// Private attributes
	private ProjectTree	projectTree;

	/**
	 * Default constructor
	 */
	public ProjectView() {

		setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();

		projectTree = new ProjectTree();
		scrollPane.setViewportView(projectTree);

		add(scrollPane);
	}

	public DockKey getDockKey(){
        return new DockKey("project");
    }
	
    public Component getComponent(){
        return this;
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
