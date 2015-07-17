package ch.hesge.csim2.ui.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;

import ch.hesge.csim2.core.model.IEngine;
import ch.hesge.csim2.ui.model.ApplicationManager;
import ch.hesge.csim2.ui.popup.EnginePopup;
import ch.hesge.csim2.ui.table.EngineTable;
import ch.hesge.csim2.ui.utils.SimpleAction;
import ch.hesge.csim2.ui.utils.SwingUtils;

@SuppressWarnings("serial")
public class EngineView extends JPanel {

	// Private attributes
	private EngineTable engineTable;
	private ApplicationManager appManager;

	/**
	 * Default constructor.
	 */
	public EngineView() {
		appManager = ApplicationManager.UNIQUE_INSTANCE;
		initComponent();
	}

	/**
	 * Initialize the component
	 */
	private void initComponent() {
		
		setLayout(new BorderLayout(0, 0));
		setBorder(new LineBorder(Color.LIGHT_GRAY));
		
		engineTable = new EngineTable();		
		engineTable.setFillsViewportHeight(true);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(engineTable);
		add(scrollPane, BorderLayout.CENTER);	
		
		initListeners();
	}

	/**
	 * Initialize component inner listeners
	 */
	private void initListeners() {

		// Set focus when visible
		SwingUtils.onComponentVisible(engineTable, new SimpleAction<Object>() {
			@Override
			public void run(Object o) {
				engineTable.requestFocus();
			}
		});
				
		// Listen to double-click
		SwingUtils.onTableDoubleClick(engineTable, new SimpleAction<MouseEvent>() {
			@Override
			public void run(MouseEvent e) {
				
				// Start the engine
				appManager.startEngine(engineTable.getSelectedObject());
			}
		});

		// Listen to right-click
		SwingUtils.onTableRightClick(engineTable, new SimpleAction<MouseEvent>() {
			@Override
			public void run(MouseEvent e) {
				
				// Show context menu
				EnginePopup popup = new EnginePopup();
				popup.setEngine(engineTable.getSelectedObject());
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
	
	/**
	 * Set engines to display.
	 * 
	 * @param engines
	 *            the engines to set
	 */
	public void setEngines(List<IEngine> engines) {		
		engineTable.setEngines(engines);
	}
}
