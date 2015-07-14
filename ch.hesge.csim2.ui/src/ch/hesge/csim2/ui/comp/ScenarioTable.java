package ch.hesge.csim2.ui.comp;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import ch.hesge.csim2.core.model.Scenario;

@SuppressWarnings("serial")
public class ScenarioTable extends JTable {

	// Private attributes
	private Scenario scenario;
	private ScenarioPopup contextMenu;

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
		
		// Create a context menu
		/*
		contextMenu = new ScenarioPopup();
		contextMenu.clearMenuState();		
		contextMenu.setStartMenuState(true);
		*/
		
		initModel();
		initRenderer();		
		initListeners();
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
						if (executionTime == -1)
							return "-";
						return executionTime;
					}
				}

				return null;
			}
		});
	}

	/**
	 * Initialize the table listeners
	 */
	private void initListeners() {

		// Listen to mouse click
		addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent e) {

				// Handle right-click
				if (SwingUtilities.isRightMouseButton(e)) {

					// Select row under the mouse
					int clickedRow = rowAtPoint(new Point((int)e.getX(), (int)e.getY()));
					setRowSelectionInterval(clickedRow,  clickedRow);

					// Show context menu
					contextMenu.show(e.getComponent(), e.getX(), e.getY());
					
					e.consume();
				}
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
	 * Selecting a row already selected is forbidden.
	 */
	@Override
	public void changeSelection(int row, int col, boolean isToggle, boolean isExtend) {
		if (!isRowSelected(row)) {
			super.changeSelection(row, col, isToggle, isExtend);
		}
	}

	/**
	 * Activate start option in context menu
	 */
	public void setStartContextMenu() {
		contextMenu.clearMenuState();		
		contextMenu.setStartMenuState(true);
	}
	
	/**
	 * Activate stop option in context menu
	 */
	public void setStopContextMenu() {
		contextMenu.clearMenuState();		
		contextMenu.setStopMenuState(true);
	}
}
