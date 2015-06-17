package ch.hesge.csim2.ui.views;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ch.hesge.csim2.core.logic.ApplicationLogic;
import ch.hesge.csim2.core.model.IMethodConceptMatcher;
import ch.hesge.csim2.core.model.MethodConceptMatch;
import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.Scenario;
import ch.hesge.csim2.core.model.SourceMethod;
import ch.hesge.csim2.core.model.Trace;
import ch.hesge.csim2.core.utils.Console;
import ch.hesge.csim2.core.utils.StringUtils;
import ch.hesge.csim2.ui.comp.MatcherComboBox;
import ch.hesge.csim2.ui.comp.ScenarioComboBox;
import ch.hesge.csim2.ui.comp.TraceEntryTable;
import ch.hesge.csim2.ui.comp.TraceMatchTable;
import ch.hesge.csim2.ui.utils.SwingUtils;

@SuppressWarnings("serial")
public class TracesView extends JPanel {

	// Private attribute	
	private String rootSourceFolder;
	private Project project;
	private List<Scenario> scenarios;
	private List<Trace> traces;
	private Map<Integer, List<MethodConceptMatch>> matchMap;
	private Map<Integer, SourceMethod> methodMap;

	private JPanel settingsPanel;
	private JPanel mainPanel;
	private JPanel methodPanel;
	private JPanel conceptPanel;
	private ScenarioComboBox scenarioComboBox;
	private MatcherComboBox matcherComboBox;
	private JButton loadBtn;
	private JSplitPane splitPane;
	private TraceEntryTable traceTable;
	private TraceMatchTable matchTable;

	/**
	 * Default constructor.
	 */
	public TracesView(Project project, List<Scenario> scenarios) {

		this.project   = project;
		this.scenarios = scenarios;
		this.methodMap = ApplicationLogic.getSourceMethodMap(project);

		initComponent();
	}

	/**
	 * Initialize the component
	 */
	private void initComponent() {

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

		// Create the matcher selection panel
		JLabel algoLabel = new JLabel("Matching:");
		settingsPanel.add(algoLabel);		
		List<IMethodConceptMatcher> matchers = ApplicationLogic.getMatchers();
		matcherComboBox = new MatcherComboBox(matchers);
		matcherComboBox.setPreferredSize(new Dimension(100, 20));
		settingsPanel.add(matcherComboBox);

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
				IMethodConceptMatcher matcher = (IMethodConceptMatcher) matcherComboBox.getSelectedItem();

				if (scenario != null && matcher != null) {

					SwingUtils.invokeLongOperation(TracesView.this, new Runnable() {
						@Override
						public void run() {

							// Retrieve method-concept matchings
							matchMap = matcher.getMethodMatchingMap(project);
							
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

		// Add listener to trace selection
		traceTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				
				// Retrieve selected trace
				Trace trace = traceTable.getSelectedValue();

				// Retrieve the matching list
				if (trace != null) {
					List<MethodConceptMatch> matchings = matchMap.get(trace.getMethodId());
					matchTable.setMatchings(matchings);
				}
				else {
					matchTable.setMatchings(null);
				}
			}
		});
		
		traceTable.addDoubleClickListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {

				Trace trace = traceTable.getSelectedValue();

				if (trace != null) {

					SourceMethod method = methodMap.get(trace.getMethodId());

					if (method != null && method.getFilename() != null) {

						if (rootSourceFolder == null) {
							selectRootFolderFile();
						}

						if (rootSourceFolder != null) {
							openFile(method.getFilename());
						}
					}
				}
			}
		});
	}

	/**
	 * Open the folder selection dialog
	 * and set root source dialog.
	 */
	private void selectRootFolderFile() {

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		if (fileChooser.showOpenDialog(TracesView.this) == JFileChooser.APPROVE_OPTION) {
			rootSourceFolder = fileChooser.getSelectedFile().getAbsolutePath();
		}
	}

	/**
	 * Open the file specified in argument.
	 * 
	 * @param filename
	 *        the name of the file to open
	 */
	private void openFile(String filename) {

		// Scan all folder recursively to discover filename full path
		try {
			Files.walkFileTree(Paths.get(rootSourceFolder), new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(Path filepath, BasicFileAttributes attrs) throws IOException {

					if (filepath.getFileName().toString().equals(filename)) {
						try {
							Desktop.getDesktop().open(filepath.toFile());
						}
						catch (IOException e1) {
							Console.writeError(this, "error while opening file " + filepath + ": " + StringUtils.toString(e1));
						}
					}

					return FileVisitResult.CONTINUE;
				}
			});
		}
		catch (IOException e1) {
			Console.writeError(this, "error while scanning file: '" + filename + "', error = " + StringUtils.toString(e1));
		}
	}

}
