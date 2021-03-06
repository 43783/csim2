package ch.hesge.csim2.ui.table;

import java.awt.Color;
import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import ch.hesge.csim2.core.model.Project;

@SuppressWarnings("serial")
public class ProjectTable extends JTable {

	// Private attributes
	private List<Project> projects;

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
		setGridColor(Color.LIGHT_GRAY);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setShowGrid(false);
		setTableHeader(null);

		initModel();
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
			setSelectedObject(projects.get(0));
		}
	}

	/**
	 * Return the current selection
	 * 
	 * @return
	 *         a project
	 */
	public Project getSelectedObject() {
		int selectedRow = this.getSelectedRow();
		return (selectedRow == -1 || projects.isEmpty()) ? null : projects.get(selectedRow);
	}

	/**
	 * Set the current selection
	 * 
	 * @param project
	 *        the new current project
	 */
	public void setSelectedObject(Project project) {
		int row = projects.indexOf(project);
		getSelectionModel().setSelectionInterval(row, row);
	}
}
