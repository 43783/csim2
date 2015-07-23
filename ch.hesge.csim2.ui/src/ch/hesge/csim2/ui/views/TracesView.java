package ch.hesge.csim2.ui.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;

import ch.hesge.csim2.core.model.IMethodConceptMatcher;
import ch.hesge.csim2.core.model.MethodConceptMatch;
import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.Scenario;
import ch.hesge.csim2.core.model.SourceMethod;
import ch.hesge.csim2.core.model.Trace;
import ch.hesge.csim2.ui.combo.MatcherComboBox;
import ch.hesge.csim2.ui.combo.ScenarioComboBox;
import ch.hesge.csim2.ui.model.ApplicationManager;
import ch.hesge.csim2.ui.popup.MethodPopup;
import ch.hesge.csim2.ui.table.MatchingTable;
import ch.hesge.csim2.ui.tree.TraceTree;
import ch.hesge.csim2.ui.utils.SimpleAction;
import ch.hesge.csim2.ui.utils.SwingUtils;

@SuppressWarnings("serial")
public class TracesView extends JPanel implements ActionListener {

	// Private attribute	
	private Project project;
	private ApplicationManager appManager;
	private List<Scenario> scenarios;
	private Map<Integer, SourceMethod> methodMap;
	private Map<Integer, List<MethodConceptMatch>> matchMap;

	private JPanel paramsPanel;
	private JPanel mainPanel;
	private JPanel methodPanel;
	private JPanel conceptPanel;
	private ScenarioComboBox scenarioComboBox;
	private MatcherComboBox matcherComboBox;
	private JButton loadBtn;
	private JSplitPane splitPane;
	private TraceTree traceTree;
	private MatchingTable matchTable;

	/**
	 * Default constructor.
	 */
	public TracesView(Project project) {

		this.project    = project;
		this.appManager = ApplicationManager.UNIQUE_INSTANCE;
		this.scenarios  = appManager.getScenarios(project);
		this.methodMap  = appManager.getSourceMethodMap(project);

		initComponent();
	}

	/**
	 * Initialize the component
	 */
	private void initComponent() {

		setLayout(new BorderLayout(0, 0));

		// Create the setting panel
		paramsPanel = new JPanel();
		FlowLayout fl_paramsPanel = (FlowLayout) paramsPanel.getLayout();
		fl_paramsPanel.setAlignment(FlowLayout.LEFT);
		this.add(paramsPanel, BorderLayout.NORTH);

		// Create the main panel
		mainPanel = new JPanel();
		this.add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(new BorderLayout(0, 0));
		splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.5);
		mainPanel.add(splitPane, BorderLayout.CENTER);

		// Create the scenario selection panel
		JLabel scenarioLabel = new JLabel("Scenario:");
		scenarioComboBox = new ScenarioComboBox(scenarios);
		scenarioComboBox.setPreferredSize(new Dimension(150, scenarioComboBox.getPreferredSize().height));
		paramsPanel.add(scenarioLabel);
		paramsPanel.add(scenarioComboBox);

		// Create the matcher selection panel
		JLabel matchingLabel = new JLabel("Matching:");
		paramsPanel.add(matchingLabel);		
		List<IMethodConceptMatcher> matchers = appManager.getMatchers();
		matcherComboBox = new MatcherComboBox(matchers);
		matcherComboBox.setPreferredSize(new Dimension(150, 20));
		paramsPanel.add(matcherComboBox);

		// Create the load button
		loadBtn = new JButton("Load");
		loadBtn.setPreferredSize(new Dimension(80, 25));
		loadBtn.addActionListener(this);
		paramsPanel.add(loadBtn);

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

		// Create trace tree
		traceTree = new TraceTree();
		JScrollPane scrollPane2 = new JScrollPane();
		scrollPane2.setPreferredSize(new Dimension(500, 500));
		scrollPane2.setViewportView(traceTree);
		methodPanel.add(scrollPane2, BorderLayout.CENTER);
		
		// Create match table
		matchTable = new MatchingTable();
		matchTable.setFillsViewportHeight(true);
		JScrollPane scrollPane3 = new JScrollPane();
		scrollPane3.setViewportView(matchTable);
		conceptPanel.add(scrollPane3, BorderLayout.CENTER);

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
				scenarioComboBox.requestFocus();
			}
		});

		// Listen to selection
		SwingUtils.onTreeSelection(traceTree, new SimpleAction<TreeSelectionEvent>() {
			@Override
			public void run(TreeSelectionEvent e) {

				// Retrieve current user object
				Object userObject = SwingUtils.getTreeUserObject(e.getPath());
				
				if (userObject != null && userObject instanceof Trace) {
					
					// Retrieve selected trace
					Trace trace = (Trace) userObject;

					// Retrieve its matching list
					if (trace != null) {
						List<MethodConceptMatch> matchings = matchMap.get(trace.getMethodId());
						matchTable.setMatchings(matchings);
					}
				}
				else {
					matchTable.setMatchings(null);
				}
			}
		});
		
		// Listen to right-click
		SwingUtils.onTreeRightClick(traceTree, new SimpleAction<MouseEvent>() {
			@Override
			public void run(MouseEvent e) {
				
				// Retrieve current user object
				Object userObject = SwingUtils.getTreeUserObject(traceTree, e.getX(), e.getY());
				
				if (userObject != null && userObject instanceof Trace) {
					
					// Retrieve selected trace
					Trace trace = (Trace) userObject;

					SourceMethod method = methodMap.get(trace.getMethodId());
					
					if (method != null) {
						
						// Show context menu
						MethodPopup popup = new MethodPopup();
						popup.setMethod(method);
						popup.show(e.getComponent(), e.getX(), e.getY());
					}
				}
			}
		});		
	}
	
	/**
	 * Handle action generated by button.
	 */
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == loadBtn) {
			
			Scenario scenario = (Scenario) scenarioComboBox.getSelectedItem();
			IMethodConceptMatcher matcher = (IMethodConceptMatcher) matcherComboBox.getSelectedItem();

			if (scenario != null && matcher != null) {

				SwingUtils.invokeLongOperation(TracesView.this, new Runnable() {
					@Override
					public void run() {

						// Retrieve matching for trace selection
						matchMap = matcher.getMethodMatchingMap(project);
						
						// Retrieve scenario trace and update trace tree
						List<Trace> traceRoot = appManager.getTraceTree(scenario);
						traceTree.setTraceRoots(traceRoot);
					}
				});
			}
			else {
				traceTree.setTraceRoots(null);
			}
		}
		
		traceTree.requestFocus();
	}
}
