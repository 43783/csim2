package ch.hesge.csim2.ui.comp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.ui.utils.SwingUtils;

@SuppressWarnings("serial")
public class ProjectTable extends JTable {

	// Private attributes
	private List<Project> projects;
	private ActionListener actionListener;

	/**
	 * Default constructor
	 */
	public ProjectTable() {
		initComponent();
	}

	/**
	 * Initialize the component
	 */
	private void initComponent() {

		setRowSelectionAllowed(true);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setShowGrid(false);
		setTableHeader(null);

		initModel();
		initListeners();
	}

	/**
	 * Initialize component listeners
	 */
	private void initListeners() {

		// Catch double-click
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {

				// Handle double-click
				if (e.getClickCount() == 2 && actionListener != null) {
					actionListener.actionPerformed(new ActionEvent(ProjectTable.this, ActionEvent.ACTION_PERFORMED, null));
					e.consume();
				}
			}
		});

		// Replace default ENTER action
		SwingUtils.setInputKeyAction(this, KeyEvent.VK_ENTER, "ENTER", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionListener.actionPerformed(new ActionEvent(ProjectTable.this, ActionEvent.ACTION_PERFORMED, null));
			}
		});

	}

	/**
	 * Initialize component model
	 */
	private void initModel() {

		setModel(new DefaultTableModel() {

			@Override
			public int getColumnCount() {
				return 1;
			}

			@Override
			public String getColumnName(int col) {

				switch (col) {
					case 0:
						return "Name";
				}

				return null;
			}

			@Override
			public int getRowCount() {
				if (projects == null)
					return 0;
				return projects.size();
			}

			@Override
			public boolean isCellEditable(int row, int col) {
				return false;
			}

			@Override
			public Object getValueAt(int row, int col) {

				Project project = projects.get(row);

				switch (col) {
					case 0:
						return project.getName();
				}

				return null;
			}
		});
	}

	/**
	 * Selecting a row already selected is forbidden.
	 */
	@Override
	public void changeSelection(int row, int col, boolean isToggle, boolean isExtend) {
		if (!isRowSelected(row)) {
			super.changeSelection(row, col, isToggle, isExtend);
		}
	}

	/**
	 * Set engines displayed by this table
	 * 
	 * @param engines
	 *        the engines to set
	 */
	public void setProjects(List<Project> projects) {

		this.projects = projects;
		initModel();

		// Preselect first project
		if (projects.size() > 0) {
			setSelectedValue(projects.get(0));
		}
	}

	/**
	 * Return the current selection
	 * 
	 * @return
	 *         a project
	 */
	public Project getSelectedValue() {
		int row = getSelectedRow();
		return projects.get(row);
	}

	/**
	 * Set the current selection
	 * 
	 * @param project
	 *        the new current project
	 */
	public void setSelectedValue(Project project) {
		int row = projects.indexOf(project);
		getSelectionModel().setSelectionInterval(row, row);
	}

	/**
	 * Register an action listener to the project list
	 * 
	 * @param actionListener
	 */
	public void addActionListener(ActionListener actionListener) {
		this.actionListener = actionListener;
	}

}
