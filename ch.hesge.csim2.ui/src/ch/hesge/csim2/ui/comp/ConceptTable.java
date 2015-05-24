package ch.hesge.csim2.ui.comp;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import ch.hesge.csim2.core.model.Concept;

@SuppressWarnings("serial")
public class ConceptTable extends JTable {

	// Private attributes
	private List<Concept> concepts;
	private List<Concept> selectedConcepts;

	/**
	 * Default constructor
	 */
	public ConceptTable() {
		
		selectedConcepts = new ArrayList<>();
		
		initComponent();
	}

	/**
	 * Initialize the component
	 */
	private void initComponent() {

		setRowSelectionAllowed(true);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setGridColor(Color.LIGHT_GRAY);
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
				return 2;
			}

			@Override
			public String getColumnName(int col) {

				switch (col) {
					case 0:
						return "Name";
					case 1:
						return "";
				}

				return null;
			}

			@Override
			public Class<?> getColumnClass(int col) {
			    if (col == 1)
			        return Boolean.class;
			    return super.getColumnClass(col);
			}
			
			@Override
			public int getRowCount() {
				if (concepts == null)
					return 0;
				return concepts.size();
			}

			@Override
			public boolean isCellEditable(int row, int col) {
				return col == 1;
			}

			@Override
			public Object getValueAt(int row, int col) {

				Concept concept = concepts.get(row);

				switch (col) {
					case 0:
						return concept.getName();
					case 1:
						return selectedConcepts.contains(concept);
				}

				return null;
			}
			
			@Override
			public void setValueAt(Object value, int row, int col) {

				if (col == 1) {
					
					Concept concept = concepts.get(row);
					boolean isSelected = (boolean) value;
					
					if (isSelected) {
						selectedConcepts.add(concept);
					}
					else {
						selectedConcepts.remove(concept);
					}
				}
			}
		});
		
		// Sets column constraint
		TableColumnModel columnModel = getColumnModel();
		columnModel.getColumn(1).setMinWidth(50);
		columnModel.getColumn(1).setMaxWidth(50);
		
	}

	/**
	 * Set concepts displayed by this table
	 * 
	 * @param concepts
	 *        the concept list
	 */
	public void setConcepts(List<Concept> concepts) {
		this.concepts = concepts;
		this.selectedConcepts.clear();
		initModel();
	}

	/**
	 * Return the current selection
	 * 
	 * @return
	 *         a list of concepts
	 */
	public List<Concept> getSelectedConcepts() {	
		return selectedConcepts;
	}

	/**
	 * Set the current selection
	 * 
	 * @param selectedConcepts
	 *        the list of concept currently selected
	 */
	public void setSelectedConcepts(List<Concept> concepts) {
		selectedConcepts.clear();
		selectedConcepts.addAll(concepts);
	}
}
