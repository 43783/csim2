package ch.hesge.csim2.ui.popup;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.ui.model.ApplicationManager;

@SuppressWarnings("serial")
public class ProjectPopup extends JPopupMenu implements ActionListener {

	// Private attributes
	private Project project;
	private ApplicationManager appManager;
	private JMenuItem mnuRename;
	private JMenuItem mnuDelete;
	private JMenuItem mnuClose;

	/**
	 * Default constructor
	 */
	public ProjectPopup() {
		appManager = ApplicationManager.UNIQUE_INSTANCE;
		initComponent();
	}

	/**
	 * Initialize the component
	 */
	private void initComponent() {

		mnuRename = new JMenuItem("Rename project");
		mnuRename.addActionListener(this);
		add(mnuRename);

		mnuDelete = new JMenuItem("Delete");
		mnuDelete.addActionListener(this);
		add(mnuDelete);

		add(new JSeparator());

		mnuClose = new JMenuItem("Close");
		mnuClose.addActionListener(this);
		add(mnuClose);
	}

	/**
	 * Return the project concerned by this popup.
	 * 
	 * @return the project
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * Sets the project concerned by this popup.
	 * 
	 * @param project
	 *            the project to set
	 */
	public void setProject(Project project) {
		this.project = project;
	}

	/**
	 * Handle action generated by menu.
	 */
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == mnuRename) {
			appManager.renameProject(project);
		}
		else if (e.getSource() == mnuDelete) {
			appManager.deleteProject(project);
		}
		else if (e.getSource() == mnuClose) {
			appManager.closeProject();
		}
	}

}