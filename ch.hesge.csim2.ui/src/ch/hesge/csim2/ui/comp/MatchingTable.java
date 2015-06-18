package ch.hesge.csim2.ui.comp;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import ch.hesge.csim2.core.model.MethodConceptMatch;
import ch.hesge.csim2.core.utils.ObjectSorter;

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
				return 2;
			}

			@Override
			public String getColumnName(int col) {

				switch (col) {
					case 0:
						return "Concept";
					case 1:
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
			public boolean isCellEditable(int row, int col) {
				return false;
			}

			@Override
			public Object getValueAt(int row, int col) {

				MethodConceptMatch match = matchings.get(row);

				switch (col) {
					case 0:
						return match.getConcept().getName();
					case 1:
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
		columnModel.getColumn(1).setMaxWidth(60);
		columnModel.getColumn(1).setMinWidth(60);
	}
	
	/**
	 * Sets the matching this table is displaying.
	 * 
	 * @param matchings
	 *        a list of MethodConceptMatch
	 */
	public void setMatchings(List<MethodConceptMatch> matchings) {
		
		if (matchings == null) {
			this.matchings = null;
		}
		else {
			this.matchings = new ArrayList<>(matchings);
			ObjectSorter.sortMatchingByWeight(this.matchings);
		}
		
		initModel();
		initRenderer();
	}
}
