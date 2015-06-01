package ch.hesge.csim2.ui.comp;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import ch.hesge.csim2.core.logic.ApplicationLogic;
import ch.hesge.csim2.core.model.StemMethod;
import ch.hesge.csim2.core.model.StemMethodType;

@SuppressWarnings("serial")
public class StemMethodTable extends JTable {

	// Private attributes
	private StemMethod stemTree;
	private List<StemMethod> stemMethods;

	/**
	 * Default constructor
	 */
	public StemMethodTable() {
		initComponent();
	}

	/**
	 * Default constructor
	 */
	public StemMethodTable(StemMethod stemTree) {

		this.stemTree = stemTree;
		
		if (stemTree != null) {
			this.stemMethods = ApplicationLogic.inflateStemMethods(stemTree);
		}
		else {
			this.stemMethods = null;
		}

		initComponent();
	}

	/**
	 * Initialize the component
	 */
	private void initComponent() {

		setEnabled(true);
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
			public Class<?> getColumnClass(int col) {
				return StemMethod.class;
			}

			@Override
			public int getColumnCount() {
				return 2;
			}

			@Override
			public String getColumnName(int col) {

				switch (col) {
					case 0:
						return "Stem";
					case 1:
						return "Type";
				}

				return null;
			}

			@Override
			public int getRowCount() {
				if (stemMethods == null)
					return 0;
				return stemMethods.size();
			}

			@Override
			public boolean isCellEditable(int row, int col) {
				return false;
			}

			@Override
			public Object getValueAt(int row, int col) {

				StemMethod stem = stemMethods.get(row);
				StemMethodType stemType = stem.getStemType();

				switch (col) {
					case 0:
						return stem.getTerm();
					case 1:
						if (stemType == StemMethodType.METHOD_NAME_FULL || stemType == StemMethodType.METHOD_NAME_PART) {
							return "method";
						}
						else if (stemType == StemMethodType.PARAMETER_NAME_FULL || stemType == StemMethodType.PARAMETER_NAME_PART) {
							return "parameter";
						}
						else if (stemType == StemMethodType.PARAMETER_TYPE_FULL || stemType == StemMethodType.PARAMETER_TYPE_PART) {
							return "type";
						}
						else if (stemType == StemMethodType.REFERENCE_NAME_FULL || stemType == StemMethodType.REFERENCE_NAME_PART) {
							return "reference";
						}
						else if (stemType == StemMethodType.REFERENCE_TYPE_FULL || stemType == StemMethodType.REFERENCE_TYPE_PART) {
							return "type";
						}
				}

				return "n/a";
			}
		});
	}

	/**
	 * Initialize the table renderer
	 */
	private void initRenderer() {

		setDefaultRenderer(StemMethod.class, new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {

				JLabel cellRenderer = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
				cellRenderer.setText(value.toString());

				StemMethod stem = stemMethods.get(row);
				StemMethodType stemType = stem.getStemType();

				if (col == 0) {

					// Detect full names
					if (stemType == StemMethodType.METHOD_NAME_FULL || stemType == StemMethodType.PARAMETER_NAME_FULL || stemType == StemMethodType.PARAMETER_TYPE_FULL || stemType == StemMethodType.REFERENCE_NAME_FULL || stemType == StemMethodType.REFERENCE_TYPE_FULL) {
						cellRenderer.setFont(cellRenderer.getFont().deriveFont(Font.BOLD));
						cellRenderer.setText(value.toString());
					}
					else {
						cellRenderer.setFont(cellRenderer.getFont().deriveFont(Font.PLAIN));
						cellRenderer.setText("  " + value.toString());
					}
				}
				else if (col == 1) {

					cellRenderer.setFont(cellRenderer.getFont().deriveFont(Font.PLAIN));

					if (stemType == StemMethodType.METHOD_NAME_FULL || stemType == StemMethodType.PARAMETER_NAME_FULL || stemType == StemMethodType.PARAMETER_TYPE_FULL || stemType == StemMethodType.REFERENCE_NAME_FULL || stemType == StemMethodType.REFERENCE_TYPE_FULL) {
						cellRenderer.setText(value.toString());
					}
					else {
						cellRenderer.setText(null);
					}
				}

				return this;
			}
		});
	}

	/**
	 * Return the root of the stem this table is displaying.
	 * 
	 * @return
	 *         the root of stem method hierarchy
	 */
	public StemMethod getStemTree() {
		return stemTree;
	}

	/**
	 * Set the stems to display in this table.
	 * 
	 * @param stemTree
	 *        the root of stem method hierarchy
	 */
	public void setStemTree(StemMethod stemTree) {
		this.stemTree = stemTree;
		
		if (stemTree != null) {
			this.stemMethods = ApplicationLogic.inflateStemMethods(stemTree);
		}
		else {
			this.stemMethods = null;
		}

		initModel();
	}
}
