package ch.hesge.csim2.ui.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.SourceClass;
import ch.hesge.csim2.ui.model.ApplicationManager;
import ch.hesge.csim2.ui.table.GranularityClassTable;
import ch.hesge.csim2.ui.table.GranularityConceptTable;
import ch.hesge.csim2.ui.utils.SimpleAction;
import ch.hesge.csim2.ui.utils.SwingUtils;

@SuppressWarnings("serial")
public class GranularityView extends JPanel {

	// Private attribute	
	private List<Concept> conceptList;
	private List<SourceClass> classList;

	private JPanel mainPanel;
	private JPanel conceptsPanel;
	private JPanel classesPanel;
	private JSplitPane splitPane;
	private GranularityConceptTable conceptTable;
	private GranularityClassTable classTable;

	/**
	 * Default constructor.
	 */
	public GranularityView(Project project) {

		ApplicationManager appManager = ApplicationManager.UNIQUE_INSTANCE;
		this.conceptList = appManager.getConceptsGranularity(project);
		this.classList   = appManager.getSourceClassGranularity(project);

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
		splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.5);
		mainPanel.add(splitPane, BorderLayout.CENTER);

		// Create the method panel
		conceptsPanel = new JPanel();
		conceptsPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Concepts", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		splitPane.setLeftComponent(conceptsPanel);
		conceptsPanel.setLayout(new BorderLayout(0, 0));

		// Create the concept panel
		classesPanel = new JPanel();
		classesPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Classes", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		splitPane.setRightComponent(classesPanel);
		classesPanel.setLayout(new BorderLayout(0, 0));

		// Create concept table
		conceptTable = new GranularityConceptTable(conceptList);
		conceptTable.setFillsViewportHeight(true);
		JScrollPane scrollPane2 = new JScrollPane();
		scrollPane2.setPreferredSize(new Dimension(500, 500));
		scrollPane2.setViewportView(conceptTable);
		conceptsPanel.add(scrollPane2, BorderLayout.CENTER);

		// Create match table
		classTable = new GranularityClassTable(classList);
		classTable.setFillsViewportHeight(true);
		JScrollPane scrollPane3 = new JScrollPane();
		scrollPane3.setViewportView(classTable);
		classesPanel.add(scrollPane3, BorderLayout.CENTER);

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
