package ch.hesge.csim2.ui.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.ui.model.ApplicationManager;
import ch.hesge.csim2.ui.table.WeightedConceptTable;
import ch.hesge.csim2.ui.utils.SimpleAction;
import ch.hesge.csim2.ui.utils.SwingUtils;

@SuppressWarnings("serial")
public class AbstractionView extends JPanel {

	// Private attribute	
	private List<Concept> weightedConcepts;

	private JPanel mainPanel;
	private WeightedConceptTable conceptTable;

	/**
	 * Default constructor.
	 */
	public AbstractionView(Project project) {
		this.weightedConcepts = ApplicationManager.UNIQUE_INSTANCE.getWeightedConcepts(project);
		initComponent();
	}

	/**
	 * Initialize the component
	 */
	private void initComponent() {

		setLayout(new BorderLayout(0, 0));

		// Create the main panel
		mainPanel = new JPanel();
		this.add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(new BorderLayout(0, 0));
		
		// Create concept table
		conceptTable = new WeightedConceptTable(weightedConcepts);
		conceptTable.setFillsViewportHeight(true);
		JScrollPane scrollPane = new JScrollPane();
		mainPanel.add(scrollPane, BorderLayout.CENTER);
		scrollPane.setPreferredSize(new Dimension(500, 500));
		scrollPane.setViewportView(conceptTable);

		initListeners();
	}

	/**
	 * Initialize component inner listeners
	 */
	private void initListeners() {

		// Set focus when visible
		SwingUtils.onComponentVisible(this, new SimpleAction<Object>() {
			@Override
			public void run(Object o) {
				conceptTable.requestFocus();
			}
		});
	}
}
