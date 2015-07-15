package ch.hesge.csim2.ui.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import ch.hesge.csim2.core.model.IEngine;
import ch.hesge.csim2.ui.comp.EngineTable;

@SuppressWarnings("serial")
public class EngineView extends JPanel {

	// Private attributes
	private ActionHandler actionHandler;
	private EngineTable engineTable;

	/**
	 * Default constructor.
	 */
	public EngineView(ActionHandler actionHandler) {
		
		this.actionHandler = actionHandler;
		initComponent();
	}

	/**
	 * Initialize the component
	 */
	private void initComponent() {
		
		setLayout(new BorderLayout(0, 0));
		setBorder(new LineBorder(Color.LIGHT_GRAY));
		
		engineTable = new EngineTable(actionHandler);
		add(engineTable, BorderLayout.CENTER);		
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
