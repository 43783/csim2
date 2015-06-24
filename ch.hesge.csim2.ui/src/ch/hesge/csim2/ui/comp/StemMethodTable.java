package ch.hesge.csim2.ui.comp;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.List;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import ch.hesge.csim2.core.logic.ApplicationLogic;
import ch.hesge.csim2.core.model.StemMethod;
import ch.hesge.csim2.core.model.StemMethodType;
import ch.hesge.csim2.core.utils.ObjectSorter;

@SuppressWarnings("serial")
public class StemMethodTable extends JTable {

	// Private attributes
	private Set<String> termsIntersection;
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
						if (stemType == StemMethodType.METHOD_NAME_FULL) {
							return "method";
						}
						else if (stemType == StemMethodType.METHOD_NAME_PART) {
							return "method part";
						}
						else if (stemType == StemMethodType.PARAMETER_NAME_FULL) {
							return "parameter";
						}
						else if (stemType == StemMethodType.PARAMETER_NAME_PART) {
							return "parameter part";
						}
						else if (stemType == StemMethodType.PARAMETER_TYPE_FULL) {
							return "param type";
						}
						else if (stemType == StemMethodType.PARAMETER_TYPE_PART) {
							return "param type part";
						}
						else if (stemType == StemMethodType.REFERENCE_NAME_FULL) {
							return "reference";
						}
						else if (stemType == StemMethodType.REFERENCE_NAME_PART) {
							return "reference part";
						}
						else if (stemType == StemMethodType.REFERENCE_TYPE_FULL) {
							return "ref type";
						}
						else if (stemType == StemMethodType.REFERENCE_TYPE_PART) {
							return "ref type part";
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

				if (termsIntersection.contains(stem.getTerm())) {
					cellRenderer.setForeground(Color.RED);					
				}
				else {
					cellRenderer.setForeground(Color.BLACK);					
				}
				
				if (col == 0) {

					if (stemType == StemMethodType.METHOD_NAME_FULL || stemType == StemMethodType.PARAMETER_NAME_FULL || stemType == StemMethodType.REFERENCE_NAME_FULL) {
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
					cellRenderer.setText(value.toString());
				}

				return this;
			}
		});
	}

	/**
	 * Set the term intersection between concept and method stem.
	 * All stem included in this intersection will be display in red.
	 * 
	 * @param term
	 *        the set of term intersection between concept and method stems
	 */
	public void setTermsIntersection(Set<String> termsIntersection) {
		this.termsIntersection = termsIntersection;
	}

	/**
	 * Set the stems to display in this table.
	 * 
	 * @param stemTree
	 *        the root of stem method hierarchy
	 */
	public void setStemTree(StemMethod stemTree) {

		if (stemTree != null) {
			stemMethods = ApplicationLogic.inflateStemMethods(stemTree);
			ObjectSorter.sortStemMethods(stemMethods);
		}
		else {
			stemMethods = null;
		}

		initModel();
	}
}
