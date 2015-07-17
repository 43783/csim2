package ch.hesge.csim2.ui.table;

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

import ch.hesge.csim2.core.model.StemConcept;
import ch.hesge.csim2.core.model.StemConceptType;
import ch.hesge.csim2.core.utils.ObjectSorter;
import ch.hesge.csim2.ui.model.ApplicationManager;

@SuppressWarnings("serial")
public class StemConceptTable extends JTable {

	// Private attributes
	private ApplicationManager appManager;
	private Set<String> termsIntersection;
	private List<StemConcept> stemConcepts;

	/**
	 * Default constructor
	 */
	public StemConceptTable() {
		appManager = ApplicationManager.UNIQUE_INSTANCE;
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
						else if (stemType == StemConceptType.CONCEPT_NAME_PART) {
							return "concept part";
						}
						else if (stemType == StemConceptType.ATTRIBUTE_NAME_FULL) {
							return "attribute";
						}
						else if (stemType == StemConceptType.ATTRIBUTE_NAME_PART) {
							return "attribute part";
						}
						else if (stemType == StemConceptType.ATTRIBUTE_IDENTIFIER_FULL) {
							return "identifier";
						}
						else if (stemType == StemConceptType.ATTRIBUTE_IDENTIFIER_PART) {
							return "identifier part";
						}
						else if (stemType == StemConceptType.CLASS_NAME_FULL) {
							return "class";
						}
						else if (stemType == StemConceptType.CLASS_NAME_PART) {
							return "class part";
						}
						else if (stemType == StemConceptType.CLASS_IDENTIFIER_FULL) {
							return "class identifier";
						}
						else if (stemType == StemConceptType.CLASS_IDENTIFIER_PART) {
							return "class identifier part";
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

				if (termsIntersection != null && termsIntersection.contains(stem.getTerm())) {
					cellRenderer.setForeground(Color.RED);					
				}
				else {
					cellRenderer.setForeground(Color.BLACK);					
				}

				if (col == 0) {

					if (stemType == StemConceptType.CONCEPT_NAME_FULL || stemType == StemConceptType.ATTRIBUTE_NAME_FULL || stemType == StemConceptType.CLASS_NAME_FULL) {
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
	 *        the root of stem concept hierarchy
	 */
	public void setStemTree(StemConcept stemTree) {

		if (stemTree != null) {
			stemConcepts = appManager.inflateStemConcepts(stemTree);
			ObjectSorter.sortStemConcepts(stemConcepts);
		}
		else {
			stemConcepts = null;
		}

		initModel();
	}
}
