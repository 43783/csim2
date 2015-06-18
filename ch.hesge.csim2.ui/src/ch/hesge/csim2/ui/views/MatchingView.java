package ch.hesge.csim2.ui.views;

import java.awt.BorderLayout;
import java.awt.Color;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ch.hesge.csim2.core.logic.ApplicationLogic;
import ch.hesge.csim2.core.model.IMethodConceptMatcher;
import ch.hesge.csim2.core.model.MethodConceptMatch;
import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.Scenario;
import ch.hesge.csim2.core.model.SourceClass;
import ch.hesge.csim2.core.model.SourceMethod;
import ch.hesge.csim2.core.utils.Console;
import ch.hesge.csim2.core.utils.StringUtils;
import ch.hesge.csim2.ui.comp.MatcherComboBox;
import ch.hesge.csim2.ui.comp.MatchingTable;
import ch.hesge.csim2.ui.comp.SourceMethodTable;
import ch.hesge.csim2.ui.utils.SwingUtils;

@SuppressWarnings("serial")
public class MatchingView extends JPanel {

	// Private attribute	
	private String rootSourceFolder;
	private Project project;
	private Map<Integer, SourceClass> classMap;
	private List<SourceMethod> sourceMethods;
	private Map<Integer, List<MethodConceptMatch>> matchMap;

	private JPanel paramsPanel;
	private JPanel mainPanel;
	private JPanel methodPanel;
	private JPanel conceptPanel;
	private MatcherComboBox matcherComboBox;
	private JButton loadBtn;
	private JSplitPane splitPane;
	private SourceMethodTable methodTable;
	private MatchingTable matchTable;

	/**
	 * Default constructor.
	 */
	public MatchingView(Project project, List<Scenario> scenarios) {

		this.project = project;
		this.sourceMethods = new ArrayList<>(ApplicationLogic.getSourceMethodMap(project).values());
		this.classMap = ApplicationLogic.getSourceClassMap(project);

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
		splitPane.setResizeWeight(0.7);
		mainPanel.add(splitPane, BorderLayout.CENTER);

		// Create the matcher selection panel
		JLabel matchingLabel = new JLabel("Matching algorithm:");
		paramsPanel.add(matchingLabel);
		List<IMethodConceptMatcher> matchers = ApplicationLogic.getMatchers();
		matcherComboBox = new MatcherComboBox(matchers);
		matcherComboBox.setPreferredSize(new Dimension(100, 20));
		paramsPanel.add(matcherComboBox);

		// Create the load button
		loadBtn = new JButton("Load");
		paramsPanel.add(loadBtn);

		// Create the method panel
		methodPanel = new JPanel();
		methodPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Methods", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		splitPane.setLeftComponent(methodPanel);
		methodPanel.setLayout(new BorderLayout(0, 0));

		// Create the concept panel
		conceptPanel = new JPanel();
		conceptPanel.setBorder(new TitledBorder(null, "Concepts", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		splitPane.setRightComponent(conceptPanel);
		conceptPanel.setLayout(new BorderLayout(0, 0));

		// Create trace table
		methodTable = new SourceMethodTable();
		JScrollPane scrollPane1 = new JScrollPane();
		scrollPane1.setViewportView(methodTable);
		methodPanel.add(scrollPane1, BorderLayout.CENTER);

		// Create match table
		matchTable = new MatchingTable();
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

				IMethodConceptMatcher matcher = (IMethodConceptMatcher) matcherComboBox.getSelectedItem();

				if (matcher != null) {

					SwingUtils.invokeLongOperation(MatchingView.this, new Runnable() {
						@Override
						public void run() {

							// Retrieve method-concept matchings
							matchMap = matcher.getMethodMatchingMap(project);

							// Initialize method table
							methodTable.setSourceClasses(classMap);
							methodTable.setSourceMethods(sourceMethods);
						}
					});
				}
				else {
					methodTable.setSourceMethods(null);
				}
			}
		});

		// Add listener to trace selection
		methodTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {

				// Retrieve selected trace
				SourceMethod method = methodTable.getSelectedValue();

				// Retrieve the matching list
				if (method != null) {
					List<MethodConceptMatch> matchings = matchMap.get(method.getKeyId());
					matchTable.setMatchings(matchings);
				}
				else {
					matchTable.setMatchings(null);
				}
			}
		});

		methodTable.addDoubleClickListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {

				SourceMethod method = methodTable.getSelectedValue();

				if (method != null && method.getFilename() != null) {

					if (rootSourceFolder == null) {
						selectRootFolderFile();
					}

					if (rootSourceFolder != null) {
						openFile(method.getFilename());
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

		if (fileChooser.showOpenDialog(MatchingView.this) == JFileChooser.APPROVE_OPTION) {
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
