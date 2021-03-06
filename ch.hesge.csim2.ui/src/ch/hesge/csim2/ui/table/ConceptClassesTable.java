package ch.hesge.csim2.ui.table;

import java.awt.Color;
import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import ch.hesge.csim2.core.model.ConceptClass;
import ch.hesge.csim2.core.utils.ObjectSorter;

@SuppressWarnings("serial")
public class ConceptClassesTable extends JTable {

	// Private attributes
	private List<ConceptClass> conceptClasses;

	/**
	 * Default constructor
	 */
	public ConceptClassesTable(List<ConceptClass> conceptClasses) {
		this.conceptClasses = conceptClasses;
		initComponent();
	}

	/**
	 * Initialize the component
	 */
	private void initComponent() {

		setRowSelectionAllowed(true);
		setGridColor(Color.LIGHT_GRAY);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		initModel();
	}

	/**
	 * Initialize the table model
	 */
	private void initModel() {

		setModel(new DefaultTableModel() {

			@Override
			public int getColumnCount() {
				return 2;
			}

			@Override
			public String getColumnName(int col) {

				switch (col) {
					case 0:
						return "Name";
					case 1:
						return "Identifiers";
				}

				return null;
			}

			@Override
			public int getRowCount() {
				if (conceptClasses == null)
					return 0;
				return conceptClasses.size();
			}

			@Override
			public boolean isCellEditable(int row, int col) {
				return false;
			}

			@Override
			public Object getValueAt(int row, int col) {

				ConceptClass conceptClass = conceptClasses.get(row);

				switch (col) {
					case 0:
						return conceptClass.getName();
					case 1:
						return conceptClass.getIdentifier();
				}

				return null;
			}
		});
	}

	/**
	 * Return the current concept class.
	 * 
	 * @return a ConceptClass
	 */
	public ConceptClass getSelectedObject() {
		int selectedRow = this.getSelectedRow();
		return (selectedRow == -1 || conceptClasses.isEmpty()) ? null : conceptClasses.get(selectedRow);
	}

	/**
	 * Visually refresh table content
	 */
	public void refresh() {
		ObjectSorter.sortConceptClasses(conceptClasses);
		((DefaultTableModel)getModel()).fireTableDataChanged();		
	}
}
