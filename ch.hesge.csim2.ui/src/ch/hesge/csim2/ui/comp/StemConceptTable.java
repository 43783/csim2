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
import ch.hesge.csim2.core.model.StemConcept;
import ch.hesge.csim2.core.model.StemConceptType;
import ch.hesge.csim2.core.utils.ObjectSorter;

@SuppressWarnings("serial")
public class StemConceptTable extends JTable {

	// Private attributes
	private List<StemConcept> stemConcepts;

	/**
	 * Default constructor
	 */
	public StemConceptTable() {
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
				return StemConcept.class;
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
				if (stemConcepts == null)
					return 0;
				return stemConcepts.size();
			}

			@Override
			public boolean isCellEditable(int row, int col) {
				return false;
			}

			@Override
			public Object getValueAt(int row, int col) {

				StemConcept stem = stemConcepts.get(row);
				StemConceptType stemType = stem.getStemType();

				switch (col) {
					case 0:
						return stem.getTerm();
					case 1:
						if (stemType == StemConceptType.CONCEPT_NAME_FULL) {
							return "concept";
						}
						else if (stemType == StemConceptType.ATTRIBUTE_NAME_FULL) {
							return "attribute";
						}
						else if (stemType == StemConceptType.ATTRIBUTE_IDENTIFIER_FULL) {
							return "identifier";
						}
						else if (stemType == StemConceptType.CLASS_NAME_FULL) {
							return "class";
						}
						else if (stemType == StemConceptType.CLASS_IDENTIFIER_FULL) {
							return "identifier";
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

		setDefaultRenderer(StemConcept.class, new DefaultTableCellRenderer() {

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {

				JLabel cellRenderer = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

				StemConcept stem = stemConcepts.get(row);
				StemConceptType stemType = stem.getStemType();

				if (col == 0) {

					cellRenderer.setFont(cellRenderer.getFont().deriveFont(Font.PLAIN));

					if (stemType == StemConceptType.CONCEPT_NAME_FULL) {
						cellRenderer.setFont(cellRenderer.getFont().deriveFont(Font.BOLD));
						cellRenderer.setText(value.toString());
					}
					else if (stemType == StemConceptType.CONCEPT_NAME_PART) {
						cellRenderer.setText("  " + value.toString());
					}
					else if (stemType == StemConceptType.ATTRIBUTE_NAME_FULL) {
						cellRenderer.setFont(cellRenderer.getFont().deriveFont(Font.BOLD));
						cellRenderer.setText(value.toString());
					}
					else if (stemType == StemConceptType.ATTRIBUTE_NAME_PART) {
						cellRenderer.setText("  " + value.toString());
					}
					else if (stemType == StemConceptType.ATTRIBUTE_IDENTIFIER_FULL) {
						cellRenderer.setText("  " + value.toString());
					}
					else if (stemType == StemConceptType.ATTRIBUTE_IDENTIFIER_PART) {
						cellRenderer.setText("    " + value.toString());
					}
					else if (stemType == StemConceptType.CLASS_NAME_FULL) {
						cellRenderer.setFont(cellRenderer.getFont().deriveFont(Font.BOLD));
						cellRenderer.setText(value.toString());
					}
					else if (stemType == StemConceptType.CLASS_NAME_PART) {
						cellRenderer.setText("  " + value.toString());
					}
					else if (stemType == StemConceptType.CLASS_IDENTIFIER_FULL) {
						cellRenderer.setText("  " + value.toString());
					}
					else if (stemType == StemConceptType.CLASS_IDENTIFIER_PART) {
						cellRenderer.setText("    " + value.toString());
					}
				}
				else if (col == 1) {

					cellRenderer.setFont(cellRenderer.getFont().deriveFont(Font.PLAIN));

					// Detect full names
					if (stemType.getValue() % 2 == 0) {
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
	 * Set the stems to display in this table.
	 * 
	 * @param stemTree
	 *        the root of stem concept hierarchy
	 */
	public void setStemTree(StemConcept stemTree) {

		if (stemTree != null) {
			stemConcepts = ApplicationLogic.inflateStemConcepts(stemTree);
			ObjectSorter.sortStemConcepts(stemConcepts);
		}
		else {
			stemConcepts = null;
		}
		
		initModel();
	}

	/**
	 * Set the stems to display in this table.
	 * 
	 * @param stem
	 *        the list of stem to display
	 */
	public void setStemList(List<StemConcept> stems) {
		
		stemConcepts = stems;
		
		if (stemConcepts != null) {
			ObjectSorter.sortStemConcepts(stemConcepts);
		}

		initModel();
	}
}
