package ch.hesge.csim2.ui.table;

import java.awt.Color;
import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import ch.hesge.csim2.core.model.Concept;

@SuppressWarnings("serial")
public class GranularityConceptTable extends JTable {

	// Private attributes
	private List<Concept> concepts;

	/**
	 * Default constructor
	 */
	public GranularityConceptTable(List<Concept> concepts) {
		this.concepts = concepts;
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
		initRenderer();
	}

	/**
	 * Initialize the table model
	 */
	private void initModel() {

		setModel(new DefaultTableModel() {

			@Override
			public int getColumnCount() {
				return 3;
			}

			@Override
			public String getColumnName(int col) {

				switch (col) {
					case 0:
						return "Concept";
					case 1:
						return "Ontology";
					case 2:
						return "Granularity";
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
			public Object getValueAt(int row, int col) {

				Concept concept = concepts.get(row);

				switch (col) {
					case 0:
						return concept.getName();
					case 1:
						return concept.getOntology().getName();
					case 2:
						return String.format("%.3f", concept.getGranularity());
				}

				return null;
			}
		});
	}

	/**
	 * Initialize the table renderer
	 */
	private void initRenderer() {
		
		// Adjust column size
		TableColumnModel columnModel = getColumnModel(); 
		columnModel.getColumn(1).setMaxWidth(80);
		columnModel.getColumn(1).setMinWidth(80);
		columnModel.getColumn(2).setMaxWidth(80);
		columnModel.getColumn(2).setMinWidth(80);
	}
}
