package ch.hesge.csim2.ui.comp;

import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import ch.hesge.csim2.core.model.MethodConceptMatch;

@SuppressWarnings("serial")
public class MatchingTable extends JTable {

	// Private attributes
	private List<MethodConceptMatch> matchings;

	/**
	 * Default constructor
	 */
	public MatchingTable() {
		initComponent();
	}

	/**
	 * Default constructor
	 */
	public MatchingTable(List<MethodConceptMatch> matchings) {
		this.matchings = matchings;
		initComponent();
	}

	/**
	 * Initialize the component
	 */
	private void initComponent() {

		setEnabled(true);
		setRowSelectionAllowed(true);
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
				return 4;
			}

			@Override
			public String getColumnName(int columnIndex) {

				switch (columnIndex) {
					case 0:
						return "Class";
					case 1:
						return "Method";
					case 2:
						return "Concept";
					case 3:
						return "Weight";
				}

				return null;
			}

			@Override
			public int getRowCount() {
				if (matchings == null)
					return 0;
				return matchings.size();
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}

			@Override
			public Object getValueAt(int row, int col) {

				MethodConceptMatch match = matchings.get(row);

				switch (col) {
					case 0:
						return match.getSourceClass().getName();
					case 1:
						return match.getSourceMethod().getSignature();
					case 2:
						return match.getConcept().getName();
					case 3:
						return String.format("%.2f", match.getWeight());
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
		columnModel.getColumn(0).setMaxWidth(100);
		columnModel.getColumn(0).setMinWidth(100);
		columnModel.getColumn(3).setMaxWidth(60);
		columnModel.getColumn(3).setMinWidth(60);
	}
	
}
