package ch.hesge.csim2.ui.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;

import ch.hesge.csim2.core.model.IMethodConceptMatcher;
import ch.hesge.csim2.core.model.MethodConceptMatch;
import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.SourceMethod;
import ch.hesge.csim2.core.model.StemConcept;
import ch.hesge.csim2.core.model.StemMethod;
import ch.hesge.csim2.core.utils.ObjectSorter;
import ch.hesge.csim2.ui.combo.MatcherComboBox;
import ch.hesge.csim2.ui.model.ApplicationManager;
import ch.hesge.csim2.ui.popup.MethodPopup;
import ch.hesge.csim2.ui.table.MatchingTable;
import ch.hesge.csim2.ui.table.SourceMethodTable;
import ch.hesge.csim2.ui.table.StemConceptTable;
import ch.hesge.csim2.ui.table.StemMethodTable;
import ch.hesge.csim2.ui.utils.SimpleAction;
import ch.hesge.csim2.ui.utils.SwingUtils;

@SuppressWarnings("serial")
public class MatchingView extends JPanel implements ActionListener {

	// Private attribute	
	private Project project;
	private ApplicationManager appManager;
	private List<SourceMethod> sourceMethods;
	private Map<Integer, StemConcept> stemConceptMap;
	private Map<Integer, StemMethod> stemMethodMap;
	private Map<Integer, List<MethodConceptMatch>> matchMap;

	private MatcherComboBox matcherComboBox;
	private JButton loadBtn;
	private JButton exportBtn;
	private JPanel mainPanel;
	private JPanel methodPanel;
	private JPanel stemConceptPanel;
	private JPanel stemMethodPanel;
	private JPanel conceptPanel;
	private SourceMethodTable methodTable;
	private MatchingTable matchTable;
	private StemConceptTable stemConceptTable;
	private StemMethodTable stemMethodTable;

	/**
	 * Default constructor.
	 */
	public MatchingView(Project project) {

		this.project = project;
		this.appManager = ApplicationManager.UNIQUE_INSTANCE;
		
		this.sourceMethods = new ArrayList<>(appManager.getSourceMethodMap(project).values());
		ObjectSorter.sortSourceMethods(sourceMethods);

		stemConceptMap = appManager.getStemConceptTreeMap(project);
		stemMethodMap = appManager.getStemMethodTreeMap(project);

		initComponent();
	}

	/**
	 * Initialize the component
	 */
	private void initComponent() {

		this.setLayout(new BorderLayout(0, 0));

		// Create settings panel
		JPanel settingsPanel = new JPanel();
		settingsPanel.setLayout(new BorderLayout(0, 0));
		this.add(settingsPanel, BorderLayout.NORTH);

		// Create the params panel
		JPanel paramsPanel = new JPanel();
		((FlowLayout) paramsPanel.getLayout()).setAlignment(FlowLayout.LEFT);
		paramsPanel.add(new JLabel("Matching algorithm:"));
		matcherComboBox = new MatcherComboBox(appManager.getMatchers());
		matcherComboBox.setPreferredSize(new Dimension(150, 20));
		paramsPanel.add(matcherComboBox);

		loadBtn = new JButton("Load");
		loadBtn.setPreferredSize(new Dimension(80, 25));
		loadBtn.addActionListener(this);
		paramsPanel.add(loadBtn);
		settingsPanel.add(paramsPanel, BorderLayout.CENTER);

		// Create export panel
		JPanel exportPanel = new JPanel();
		((FlowLayout) exportPanel.getLayout()).setAlignment(FlowLayout.RIGHT);

		exportBtn = new JButton("Export");
		exportBtn.setPreferredSize(new Dimension(80, 25));
		exportBtn.addActionListener(this);
		exportBtn.setEnabled(false);
		exportPanel.add(exportBtn);
		settingsPanel.add(exportPanel, BorderLayout.EAST);

		// Create the main panel
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout(0, 0));
		JSplitPane splitPane1 = new JSplitPane();
		splitPane1.setResizeWeight(0.3);
		JSplitPane splitPane2 = new JSplitPane();
		splitPane2.setResizeWeight(0.5);
		splitPane1.setRightComponent(splitPane2);
		JSplitPane splitPane3 = new JSplitPane();
		splitPane3.setResizeWeight(0.5);
		splitPane3.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane2.setRightComponent(splitPane3);
		mainPanel.add(splitPane1, BorderLayout.CENTER);
		this.add(mainPanel, BorderLayout.CENTER);

		// Create the method panel
		methodPanel = new JPanel();
		methodPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Methods", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		methodPanel.setLayout(new BorderLayout(0, 0));
		splitPane1.setLeftComponent(methodPanel);

		// Create the concept panel
		conceptPanel = new JPanel();
		conceptPanel.setBorder(new TitledBorder(null, "Concepts", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		conceptPanel.setLayout(new BorderLayout(0, 0));
		splitPane2.setLeftComponent(conceptPanel);

		// Create the stem-concept panel
		stemConceptPanel = new JPanel();
		stemConceptPanel.setBorder(new TitledBorder(null, "Stem Concepts", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		stemConceptPanel.setLayout(new BorderLayout(0, 0));
		splitPane3.setLeftComponent(stemConceptPanel);

		// Create the stem-method panel
		stemMethodPanel = new JPanel();
		stemMethodPanel.setBorder(new TitledBorder(null, "Stem Methods", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		stemMethodPanel.setLayout(new BorderLayout(0, 0));
		splitPane3.setRightComponent(stemMethodPanel);

		// Create method table
		methodTable = new SourceMethodTable();
		methodTable.setFillsViewportHeight(true);
		JScrollPane scrollPane1 = new JScrollPane();
		scrollPane1.setViewportView(methodTable);
		methodPanel.add(scrollPane1, BorderLayout.CENTER);

		// Create match table
		matchTable = new MatchingTable();
		matchTable.setFillsViewportHeight(true);
		JScrollPane scrollPane2 = new JScrollPane();
		scrollPane2.setViewportView(matchTable);
		conceptPanel.add(scrollPane2, BorderLayout.CENTER);

		// Create stem-concept table
		stemConceptTable = new StemConceptTable();
		stemConceptTable.setFillsViewportHeight(true);
		JScrollPane scrollPane3 = new JScrollPane();
		scrollPane3.setViewportView(stemConceptTable);
		stemConceptPanel.add(scrollPane3, BorderLayout.CENTER);

		// Create stem-concept table
		stemMethodTable = new StemMethodTable();
		stemMethodTable.setFillsViewportHeight(true);
		JScrollPane scrollPane4 = new JScrollPane();
		scrollPane4.setViewportView(stemMethodTable);
		stemMethodPanel.add(scrollPane4, BorderLayout.CENTER);

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
				matcherComboBox.requestFocus();
			}
		});

		// Listen to method right-click
		SwingUtils.onTableRightClick(methodTable, new SimpleAction<MouseEvent>() {
			@Override
			public void run(MouseEvent e) {

				// Retrieve selected trace
				SourceMethod method = methodTable.getSelectedObject();

				// Retrieve trace method
				if (method != null) {

					// Show context menu
					MethodPopup popup = new MethodPopup();
					popup.setMethod(method);
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});

		// Listen to method selection
		SwingUtils.onTableSelection(methodTable, new SimpleAction<ListSelectionEvent>() {
			@Override
			public void run(ListSelectionEvent e) {

				matchTable.setMatchings(null);
				stemConceptTable.setStemTree(null);
				stemMethodTable.setStemTree(null);

				// Retrieve selected trace
				SourceMethod sourceMethod = methodTable.getSelectedObject();

				// Retrieve the matching list
				if (sourceMethod != null) {
					List<MethodConceptMatch> matchings = matchMap.get(sourceMethod.getKeyId());
					matchTable.setMatchings(matchings);
				}
			}
		});

		// Listen to match selection
		SwingUtils.onTableSelection(matchTable, new SimpleAction<ListSelectionEvent>() {
			@Override
			public void run(ListSelectionEvent e) {

				stemConceptTable.setStemTree(null);
				stemMethodTable.setStemTree(null);

				// Retrieve selected match
				MethodConceptMatch match = matchTable.getSelectedObject();

				// Retrieve the matching list
				if (match != null) {

					Set<String> termsIntersection = appManager.getTermsIntersection(match.getStemConcepts(), match.getStemMethods());

					StemConcept rootStemConcept = stemConceptMap.get(match.getConcept().getKeyId());
					stemConceptTable.setTermsIntersection(termsIntersection);
					stemConceptTable.setStemTree(rootStemConcept);

					StemMethod rootStemMethod = stemMethodMap.get(match.getSourceMethod().getKeyId());
					stemMethodTable.setTermsIntersection(termsIntersection);
					stemMethodTable.setStemTree(rootStemMethod);
				}
			}
		});
	}

	/**
	 * Handle action generated by button.
	 */
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == loadBtn) {

			IMethodConceptMatcher matcher = (IMethodConceptMatcher) matcherComboBox.getSelectedItem();

			methodTable.setSourceMethods(null);
			matchTable.setMatchings(null);
			stemConceptTable.setStemTree(null);
			stemMethodTable.setStemTree(null);

			if (matcher != null) {

				SwingUtils.invokeLongOperation(MatchingView.this, new Runnable() {
					@Override
					public void run() {

						// Retrieve method-concept matchings
						matchMap = appManager.getMethodMatchingMap(project, matcher);

						// Initialize method table
						methodTable.setSourceMethods(sourceMethods);

						// Enable export button
						exportBtn.setEnabled(true);
					}
				});
			}
		}
		else if (e.getSource() == exportBtn) {
			appManager.exportMatchings(matchMap);
		}
		
		methodTable.requestFocus();
	}
}
