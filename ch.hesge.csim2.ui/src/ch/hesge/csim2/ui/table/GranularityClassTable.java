package ch.hesge.csim2.ui.table;

import java.awt.Color;
import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import ch.hesge.csim2.core.model.SourceClass;

@SuppressWarnings("serial")
public class GranularityClassTable extends JTable {

	// Private attributes
	private List<SourceClass> sourceClasses;

	/**
	 * Default constructor
	 */
	public GranularityClassTable(List<SourceClass> sourceClasses) {
		this.sourceClasses = sourceClasses;
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
						return "Class";
					case 1:
						return "Granularity";
				}

				return null;
			}

			@Override
			public int getRowCount() {
				if (sourceClasses == null)
					return 0;
				return sourceClasses.size();
			}

			@Override
			public Object getValueAt(int row, int col) {

				SourceClass sourceClass = sourceClasses.get(row);

				switch (col) {
					case 0:
						return sourceClass.getName();
					case 1:
						return String.format("%.3f", sourceClass.getGranularity());
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
		columnModel.getColumn(1).setMaxWidth(120);
		columnModel.getColumn(1).setMinWidth(120);
	}
}
