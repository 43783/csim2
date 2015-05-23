package ch.hesge.csim2.ui.comp;

import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import ch.hesge.csim2.core.model.Trace;

@SuppressWarnings("serial")
public class TraceEntryTable extends JTable {

	// Private attributes
	private List<Trace> traces;

	/**
	 * Default constructor
	 */
	public TraceEntryTable() {
		initComponent();
	}

	/**
	 * Initialize the component
	 */
	private void initComponent() {

		setRowSelectionAllowed(true);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		initModel();
		initRenderer();
	}

	/**
	 * Initialize the component's model
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
						return "Class";
					case 1:
						return "Method";
					case 2:
						return "Type";
				}

				return null;
			}

			@Override
			public int getRowCount() {
				if (traces == null)
					return 0;
				return traces.size();
			}

			@Override
			public boolean isCellEditable(int row, int col) {
				return false;
			}

			@Override
			public Object getValueAt(int row, int col) {

				switch (col) {
					case 0:
						return traces.get(row).getStaticClass();
					case 1:
						return traces.get(row).getSignature();
					case 2: {
						if (traces.get(row).isEnteringTrace())
							return "ENTER";
						return "EXIT";
					}
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
	}

	/**
	 * Retrieve the list this table is displaying.
	 * 
	 * @return
	 *         a list of Trace objects
	 */
	public List<Trace> getTraces() {
		return traces;
	}

	/**
	 * Sets the list this table should display.
	 * 
	 * @param traces
	 */
	public void setTraces(List<Trace> traces) {
		this.traces = traces;
		initModel();
		repaint();
	}
	
	/**
	 * Return the current selection
	 * 
	 * @return
	 *         a trace object
	 */
	public Trace getSelectedValue() {
		
		int row = getSelectedRow();
		
		if (row > -1) {
			return traces.get(row);
		}
		
		return null;
	}
}
