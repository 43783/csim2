package ch.hesge.csim2.ui.comp;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import ch.hesge.csim2.core.model.IEngine;

@SuppressWarnings("serial")
public class EngineTable extends JTable {

	// Private attributes
	private List<IEngine> engines;
	private EnginePopup contextMenu;
	private ActionListener actionListener;

	/**
	 * Default constructor
	 */
	public EngineTable() {
		initComponent();
	}

	/**
	 * Default constructor
	 */
	public EngineTable(List<IEngine> engines) {
		this.engines = engines;
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
		contextMenu = new EnginePopup(this);

		initModel();
		initListeners();
	}

	/**
	 * Initialize component inner listeners
	 */
	private void initListeners() {

		// Listen to mouse click
		addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent e) {

				// Handle single right-click
				if (SwingUtilities.isRightMouseButton(e)) {
					
					// Select row under the mouse
					int clickedRow = rowAtPoint(new Point((int)e.getX(), (int)e.getY()));
					setRowSelectionInterval(clickedRow,  clickedRow);
					
					// Show context menu
					IEngine engine = getSelectedValue();
					contextMenu.setEngine(engine);
					contextMenu.show(e.getComponent(), e.getX(), e.getY());
					e.consume();
				}

				// Handle double-click
				else if (e.getClickCount() == 2 && actionListener != null && getSelectedValue() != null) {
					actionListener.actionPerformed(new ActionEvent(EngineTable.this, ActionEvent.ACTION_PERFORMED, null));
				}
			}
		});
	}

	/**
	 * Create a table model responsible to display engines.
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
						return "Name";
					case 1:
						return "Version";
					case 2:
						return "Description";
				}

				return null;
			}

			@Override
			public int getRowCount() {
				if (engines == null)
					return 0;
				return engines.size();
			}

			@Override
			public boolean isCellEditable(int row, int col) {
				return false;
			}

			@Override
			public Object getValueAt(int row, int col) {
				
				IEngine engine = engines.get(row);

				switch (col) {
					case 0:
						return engine.getName();
					case 1:
						return engine.getVersion();
					case 2:
						return engine.getDescription();
				}

				return null;
			}
		});
		
		// Sets column constraint
		TableColumnModel columnModel = getColumnModel();
		columnModel.getColumn(0).setMinWidth(200);
		columnModel.getColumn(0).setMaxWidth(200);
		columnModel.getColumn(1).setMinWidth(100);
		columnModel.getColumn(1).setMaxWidth(100);
	}

	/**
	 * Set engines displayed by this table
	 * 
	 * @param engines
	 *            the engines to set
	 */
	public void setEngines(List<IEngine> engines) {		
		this.engines = engines;
		initModel();
	}
	
	/**
	 * Return the current selection
	 * 
	 * @return
	 *         an engine
	 */
	public IEngine getSelectedValue() {
		
		int row = getSelectedRow();
		
		if (row > -1) {
			return engines.get(row);
		}
		
		return null;
	}
	
	/**
	 * Register an action listener to the project list
	 * 
	 * @param actionListener
	 */
	public void addActionListener(ActionListener actionListener) {
		this.actionListener = actionListener;
	}
}
