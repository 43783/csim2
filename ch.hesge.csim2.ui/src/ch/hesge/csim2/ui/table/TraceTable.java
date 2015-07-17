package ch.hesge.csim2.ui.table;

import java.awt.Color;
import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import ch.hesge.csim2.core.model.Trace;
import ch.hesge.csim2.core.utils.StringUtils;

@SuppressWarnings("serial")
public class TraceTable extends JTable {

	// Private attributes
	private List<Trace> traces;

	/**
	 * Default constructor
	 */
	public TraceTable() {
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
	 * Initialize the component's model
	 */
	private void initModel() {

		setModel(new DefaultTableModel() {

			@Override
			public int getColumnCount() {
				return 4;
			}

			@Override
			public String getColumnName(int col) {

				switch (col) {
					case 0:
						return "Class";
					case 1:
						return "Method";
					case 2:
						return "Level";
					case 3:
						return "Instance Id";
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
						Trace trace = traces.get(row);
						String identation = StringUtils.repeat("   ", (int) trace.getLevel());
						return identation + traces.get(row).getSignature();
					case 2:
						return traces.get(row).getLevel();
					case 3:
						return traces.get(row).getInstanceId();
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
		columnModel.getColumn(0).setMaxWidth(80);
		columnModel.getColumn(0).setMinWidth(80);
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
	public Trace getSelectedObject() {
		int selectedRow = this.getSelectedRow();
		return (selectedRow == -1 || traces.isEmpty()) ? null : traces.get(selectedRow);
	}
}
