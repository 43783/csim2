package ch.hesge.csim2.ui.comp;

import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import ch.hesge.csim2.core.model.ConceptAttribute;

@SuppressWarnings("serial")
public class ConceptAttributesTable extends JTable {

	// Private attributes
	private List<ConceptAttribute> conceptAttributes;

	/**
	 * Default constructor
	 */
	public ConceptAttributesTable(List<ConceptAttribute> conceptAttributes) {
		this.conceptAttributes = conceptAttributes;
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
				if (conceptAttributes == null)
					return 0;
				return conceptAttributes.size();
			}

			@Override
			public boolean isCellEditable(int row, int col) {
				return false;
			}

			@Override
			public Object getValueAt(int row, int col) {

				ConceptAttribute attr = conceptAttributes.get(row);

				switch (col) {
					case 0:
						return attr.getName();
					case 1:
						return attr.getIdentifier();
				}

				return null;
			}
		});
	}
}
