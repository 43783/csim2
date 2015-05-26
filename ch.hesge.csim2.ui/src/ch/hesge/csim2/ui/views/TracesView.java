package ch.hesge.csim2.ui.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ch.hesge.csim2.core.logic.ApplicationLogic;
import ch.hesge.csim2.core.model.MethodConceptMatch;
import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.Scenario;
import ch.hesge.csim2.core.model.Trace;
import ch.hesge.csim2.ui.comp.ScenarioComboBox;
import ch.hesge.csim2.ui.comp.TraceEntryTable;
import ch.hesge.csim2.ui.comp.TraceMatchTable;
import ch.hesge.csim2.ui.utils.SwingUtils;

@SuppressWarnings("serial")
public class TracesView extends JPanel {

	// Private attribute
	private List<Scenario> scenarios;
	private Map<Integer, List<MethodConceptMatch>> matchMap;
	private List<Trace> traces;

	private JPanel settingsPanel;
	private JPanel mainPanel;
	private JPanel methodPanel;
	private JPanel conceptPanel;
	private ScenarioComboBox scenarioComboBox;
	private JButton loadBtn;
	private JSplitPane splitPane;
	private TraceEntryTable traceTable;
	private TraceMatchTable matchTable;

	/**
	 * Default constructor.
	 */
	public TracesView(Project project, List<Scenario> scenarios) {
		
		this.scenarios = scenarios;
		this.matchMap = ApplicationLogic.getMethodMatchingMap(project);

		initComponent();
	}

	/**
	 * Initialize the component
	 */
	private void initComponent() {

		setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Traces", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		setLayout(new BorderLayout(0, 0));

		// Create the setting panel
		settingsPanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) settingsPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		this.add(settingsPanel, BorderLayout.NORTH);

		// Create the main panel
		mainPanel = new JPanel();
		this.add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(new BorderLayout(0, 0));
		splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.7);
		mainPanel.add(splitPane, BorderLayout.CENTER);

		// Create the scenario selection panel
		JLabel scenarioLabel = new JLabel("Scenario:");
		scenarioComboBox = new ScenarioComboBox(scenarios);
		scenarioComboBox.setPreferredSize(new Dimension(150, scenarioComboBox.getPreferredSize().height));
		settingsPanel.add(scenarioLabel);
		settingsPanel.add(scenarioComboBox);

		// Create the load button
		loadBtn = new JButton("Load scenario");
		settingsPanel.add(loadBtn);

		// Create the method panel
		methodPanel = new JPanel();
		methodPanel.setBorder(new TitledBorder(null, "Method sequence", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		splitPane.setLeftComponent(methodPanel);
		methodPanel.setLayout(new BorderLayout(0, 0));

		// Create the concept panel
		conceptPanel = new JPanel();
		conceptPanel.setBorder(new TitledBorder(null, "Concepts", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		splitPane.setRightComponent(conceptPanel);
		conceptPanel.setLayout(new BorderLayout(0, 0));

		// Create trace table
		traceTable = new TraceEntryTable();
		JScrollPane scrollPane1 = new JScrollPane();
		scrollPane1.setViewportView(traceTable);
		methodPanel.add(scrollPane1, BorderLayout.CENTER);

		// Create match table
		matchTable = new TraceMatchTable();
		JScrollPane scrollPane2 = new JScrollPane();
		scrollPane2.setViewportView(matchTable);
		conceptPanel.add(scrollPane2, BorderLayout.CENTER);

		initListeners();
	}

	/**
	 * Initialize component inner listeners
	 */
	private void initListeners() {

		// Add listener to load button
		loadBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				Scenario scenario = (Scenario) scenarioComboBox.getSelectedItem();

				if (scenario != null) {

					SwingUtils.invokeLongOperation(TracesView.this, new Runnable() {
						@Override
						public void run() {

							// Retrieve required trace list for current scenario
							traces = ApplicationLogic.getTraces(scenario);

							// Initialize trace table
							traceTable.setTraces(traces);
						}
					});
				}
				else {
					traceTable.setTraces(null);
				}
			}
		});

		// Add listener to trace entry selection
		traceTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {

				if (traceTable.getSelectedValue() != null) {

					// Retrieve selected trace
					Trace trace = traceTable.getSelectedValue();

					// Retrieve its associated match list
					List<MethodConceptMatch> matchings = matchMap.get(trace.getMethodId());
					matchTable.setMatchings(matchings);
				}
				else {
					matchTable.setMatchings(null);
				}
			}
		});
	}
}
