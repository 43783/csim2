package ch.hesge.csim2.ui.table;

import java.awt.Color;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import ch.hesge.csim2.core.model.Scenario;
import ch.hesge.csim2.core.model.ScenarioStep;
import ch.hesge.csim2.core.utils.ObjectSorter;

@SuppressWarnings("serial")
public class ScenarioTable extends JTable {

	// Private attributes
	private Scenario scenario;

	/**
	 * Default constructor
	 */
	public ScenarioTable(Scenario scenario) {
		this.scenario = scenario;
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
				return 3;
			}

			@Override
			public String getColumnName(int col) {

				switch (col) {
					case 0:
						return "Step";
					case 1:
						return "Description";
					case 2:
						return "Execution Time";
				}

				return null;
			}
			
			@Override
			public int getRowCount() {
				if (scenario == null)
					return 0;
				return scenario.getSteps().size();
			}
			
			@Override
			public boolean isCellEditable(int row, int col) {
				return false;
			}
			
			@Override
			public Object getValueAt(int row, int col) {

				switch (col) {
					case 0:
						return scenario.getSteps().get(row).getName();
					case 1:
						return scenario.getSteps().get(row).getDescription();
					case 2: {
						long executionTime = scenario.getSteps().get(row).getExecutionTime();
						if (executionTime < 1)
							return "-";
						return executionTime;
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
		columnModel.getColumn(0).setMaxWidth(60);
		columnModel.getColumn(0).setMinWidth(60);
		columnModel.getColumn(1).setMaxWidth(500);
		columnModel.getColumn(1).setMinWidth(500);
	}

	/**
	 * Return the current selected step.
	 * 
	 * @return a ScenarioStep
	 */
	public ScenarioStep getSelectedObject() {
		int selectedRow = this.getSelectedRow();
		return (selectedRow == -1 || scenario.getSteps().isEmpty()) ? null : scenario.getSteps().get(selectedRow);
	}

	/**
	 * Refresh table content (visually)
	 */
	public void refresh() {
		ObjectSorter.sortScenarioSteps(scenario.getSteps());
		((DefaultTableModel)getModel()).fireTableDataChanged();		
	}
}
