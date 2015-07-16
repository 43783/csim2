package ch.hesge.csim2.ui.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;

import ch.hesge.csim2.core.model.IEngine;
import ch.hesge.csim2.ui.comp.EngineTable;
import ch.hesge.csim2.ui.model.ApplicationManager;

@SuppressWarnings("serial")
public class EngineView extends JPanel {

	// Private attributes
	private ApplicationManager appManager;
	private EngineTable engineTable;

	/**
	 * Default constructor.
	 */
	public EngineView() {
		this.appManager = ApplicationManager.UNIQUE_INSTANCE;
		initComponent();
	}

	/**
	 * Initialize the component
	 */
	private void initComponent() {
		
		setLayout(new BorderLayout(0, 0));
		setBorder(new LineBorder(Color.LIGHT_GRAY));
		
		engineTable = new EngineTable(appManager);		
		engineTable.setFillsViewportHeight(true);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(engineTable);
		add(scrollPane, BorderLayout.CENTER);		
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
