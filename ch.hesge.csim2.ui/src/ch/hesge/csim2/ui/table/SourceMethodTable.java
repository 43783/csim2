package ch.hesge.csim2.ui.table;

import java.awt.Color;
import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import ch.hesge.csim2.core.model.SourceMethod;

@SuppressWarnings("serial")
public class SourceMethodTable extends JTable {

	// Private attributes
	private List<SourceMethod> sourceMethods;

	/**
	 * Default constructor
	 */
	public SourceMethodTable() {
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
				return 2;
			}

			@Override
			public String getColumnName(int col) {

				switch (col) {
					case 0:
						return "Class";
					case 1:
						return "Method";
				}

				return null;
			}

			@Override
			public int getRowCount() {
				if (sourceMethods == null)
					return 0;
				return sourceMethods.size();
			}

			@Override
			public boolean isCellEditable(int row, int col) {
				return false;
			}

			@Override
			public Object getValueAt(int row, int col) {

				SourceMethod method = sourceMethods.get(row);
				
				switch (col) {
					case 0:
						return method.getSourceClass().getName();
					case 1:
						return method.getSignature();
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
	 * Sets the method list to display.
	 * 
	 * @param methods
	 */
	public void setSourceMethods(List<SourceMethod> methods) {
		this.sourceMethods = methods;
		initModel();
		repaint();
	}
	
	/**
	 * Return the current selection
	 * 
	 * @return
	 *         a SourceMethod object
	 */
	public SourceMethod getSelectedObject() {
		int selectedRow = this.getSelectedRow();
		return (selectedRow == -1 || sourceMethods.isEmpty()) ? null : sourceMethods.get(selectedRow);
	}
}
