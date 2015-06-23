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
import ch.hesge.csim2.core.utils.ObjectSorter;
import ch.hesge.csim2.core.utils.StringUtils;
import ch.hesge.csim2.ui.comp.MatcherComboBox;
import ch.hesge.csim2.ui.comp.MatchingTable;
import ch.hesge.csim2.ui.comp.SourceMethodTable;
import ch.hesge.csim2.ui.comp.StemConceptTable;
import ch.hesge.csim2.ui.comp.StemMethodTable;
import ch.hesge.csim2.ui.utils.SwingUtils;

@SuppressWarnings("serial")
public class MatchingView extends JPanel {

	// Private attribute	
	private String rootSourceFolder;
	private Project project;
	private Map<Integer, SourceClass> classMap;
	private List<SourceMethod> sourceMethods;
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
	public MatchingView(Project project, List<Scenario> scenarios) {

		this.project = project;
		this.classMap = ApplicationLogic.getSourceClassMap(project);

		this.sourceMethods = new ArrayList<>(ApplicationLogic.getSourceMethodMap(project).values());
		ObjectSorter.sortSourceMethods(sourceMethods, classMap);
		
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
		((FlowLayout)paramsPanel.getLayout()).setAlignment(FlowLayout.LEFT);
		paramsPanel.add(new JLabel("Matching algorithm:"));
		matcherComboBox = new MatcherComboBox(ApplicationLogic.getMatchers());
		matcherComboBox.setPreferredSize(new Dimension(100, 20));
		paramsPanel.add(matcherComboBox);		
		loadBtn = new JButton("Load");
		paramsPanel.add(loadBtn);
		settingsPanel.add(paramsPanel, BorderLayout.CENTER);

		// Create export panel
		JPanel exportPanel = new JPanel();
		((FlowLayout)exportPanel.getLayout()).setAlignment(FlowLayout.RIGHT);
		exportBtn = new JButton("Export");
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
		JScrollPane scrollPane1 = new JScrollPane();
		scrollPane1.setViewportView(methodTable);
		methodPanel.add(scrollPane1, BorderLayout.CENTER);

		// Create match table
		matchTable = new MatchingTable();
		JScrollPane scrollPane2 = new JScrollPane();
		scrollPane2.setViewportView(matchTable);
		conceptPanel.add(scrollPane2, BorderLayout.CENTER);
		
		// Create stem-concept table
		stemConceptTable = new StemConceptTable();
		JScrollPane scrollPane3 = new JScrollPane();
		scrollPane3.setViewportView(stemConceptTable);
		stemConceptPanel.add(scrollPane3, BorderLayout.CENTER);

		// Create stem-concept table
		stemMethodTable = new StemMethodTable();
		JScrollPane scrollPane4 = new JScrollPane();
		scrollPane4.setViewportView(stemMethodTable);
		stemMethodPanel.add(scrollPane4, BorderLayout.CENTER);

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

				methodTable.setSourceMethods(null);
				matchTable.setMatchings(null);
				stemConceptTable.setStemList(null);
				stemMethodTable.setStemList(null);

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
			}
		});

		// Add listener to export button
		exportBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {				
				String filepath = selectFileFolder();
				ApplicationLogic.exportMatchings(matchMap, filepath);
			}
		});

		// Add listener to trace selection
		methodTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {

				matchTable.setMatchings(null);
				stemConceptTable.setStemList(null);
				stemMethodTable.setStemList(null);
				
				// Retrieve selected trace
				SourceMethod sourceMethod = methodTable.getSelectedValue();

				// Retrieve the matching list
				if (sourceMethod != null) {
					List<MethodConceptMatch> matchings = matchMap.get(sourceMethod.getKeyId());
					matchTable.setMatchings(matchings);
				}
			}
		});

		// Add listener to concept selection
		matchTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {

				stemConceptTable.setStemList(null);
				stemMethodTable.setStemList(null);

				// Retrieve selected match
				MethodConceptMatch match = matchTable.getSelectedValue();

				// Retrieve the matching list
				if (match != null) {
					stemConceptTable.setStemList(match.getStemConcepts());
					stemMethodTable.setStemList(match.getStemMethods());
				}
			}
		});

		methodTable.addDoubleClickListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {

				SourceMethod method = methodTable.getSelectedValue();

				if (method != null && method.getFilename() != null) {

					if (rootSourceFolder == null) {
						rootSourceFolder = selectFileFolder();
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
	 * and ask user to select a folder.
	 */
	private String selectFileFolder() {

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		if (fileChooser.showOpenDialog(MatchingView.this) == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFile().getAbsolutePath();
		}
		
		return null;
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

					Console.writeError(this, "looking for file: " + filepath);
					
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
