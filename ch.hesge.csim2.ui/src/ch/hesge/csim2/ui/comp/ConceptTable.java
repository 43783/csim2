package ch.hesge.csim2.ui.comp;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import ch.hesge.csim2.core.model.Concept;

@SuppressWarnings("serial")
public class ConceptTable extends JTable {

	// Private attributes
	private List<Concept> concepts;

	/**
	 * Default constructor
	 */
	public ConceptTable() {
		initComponent();
	}

	/**
	 * Initialize the component
	 */
	private void initComponent() {

		setRowSelectionAllowed(true);
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
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
				if (concepts == null)
					return 0;
				return concepts.size();
			}

			@Override
			public boolean isCellEditable(int row, int col) {
				return false;
			}

			@Override
			public Object getValueAt(int row, int col) {

				Concept concept = concepts.get(row);

				switch (col) {
					case 0:
						return concept.getName();
				}

				return null;
			}
		});
	}

	/**
	 * Set concepts displayed by this table
	 * 
	 * @param concepts
	 *        the concept list
	 */
	public void setConcepts(List<Concept> concepts) {

		this.concepts = concepts;
		initModel();
	}

	/**
	 * Return the current selection
	 * 
	 * @return
	 *         a list of concepts
	 */
	public List<Concept> getSelectedConcepts() {
		
		List<Concept> selectedConcepts = new ArrayList<>();
		
		for (int selectedRow : getSelectedRows()) {
			selectedConcepts.add(concepts.get(selectedRow));
		}
		
		return selectedConcepts;
	}

	/**
	 * Set the current selection
	 * 
	 * @param selectedConcepts
	 *        the list of concept currently selected
	 */
	public void setSelectedConcepts(List<Concept> selectedConcepts) {
		
		ListSelectionModel model = getSelectionModel();
		model.clearSelection();
		
		if (selectedConcepts != null) {
			
			for (Concept concept : selectedConcepts) {
				int row = concepts.indexOf(concept);
				model.addSelectionInterval(row, row);
			}
		}
	}
}
