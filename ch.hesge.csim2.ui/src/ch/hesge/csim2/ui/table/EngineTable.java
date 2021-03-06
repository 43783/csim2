package ch.hesge.csim2.ui.table;

import java.awt.Color;
import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import ch.hesge.csim2.core.model.IEngine;

@SuppressWarnings("serial")
public class EngineTable extends JTable {

	// Private attributes
	private List<IEngine> engines;

	/**
	 * Default constructor
	 */
	public EngineTable() {
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
	 * Create a table model responsible to display engines.
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
						return "Name";
					case 1:
						return "Version";
					case 2:
						return "Description";
				}

				return null;
			}

			@Override
			public int getRowCount() {
				if (engines == null)
					return 0;
				return engines.size();
			}

			@Override
			public boolean isCellEditable(int row, int col) {
				return false;
			}

			@Override
			public Object getValueAt(int row, int col) {
				
				IEngine engine = engines.get(row);

				switch (col) {
					case 0:
						return engine.getName();
					case 1:
						return engine.getVersion();
					case 2:
						return engine.getDescription();
				}

				return null;
			}
		});
		
		// Sets column constraint
		TableColumnModel columnModel = getColumnModel();
		columnModel.getColumn(0).setMinWidth(200);
		columnModel.getColumn(0).setMaxWidth(200);
		columnModel.getColumn(1).setMinWidth(100);
		columnModel.getColumn(1).setMaxWidth(100);
	}

	/**
	 * Set engines displayed by this table
	 * 
	 * @param engines
	 *            the engines to set
	 */
	public void setEngines(List<IEngine> engines) {		
		this.engines = engines;
		initModel();
	}
	
	/**
	 * Return the current selection
	 * 
	 * @return an IEngine
	 */
	public IEngine getSelectedObject() {
		int selectedRow = this.getSelectedRow();
		return (selectedRow == -1 || engines.isEmpty()) ? null : engines.get(selectedRow);
	}
}
