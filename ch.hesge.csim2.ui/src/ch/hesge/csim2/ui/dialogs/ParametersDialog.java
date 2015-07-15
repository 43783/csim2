package ch.hesge.csim2.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

import ch.hesge.csim2.core.model.IEngine;
import ch.hesge.csim2.core.model.Ontology;
import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.Scenario;
import ch.hesge.csim2.ui.comp.OntologyComboBox;
import ch.hesge.csim2.ui.comp.ProjectComboBox;
import ch.hesge.csim2.ui.comp.ScenarioComboBox;
import ch.hesge.csim2.ui.model.ApplicationManager;
import ch.hesge.csim2.ui.utils.FileTableCellEditor;
import ch.hesge.csim2.ui.utils.SwingUtils;

import com.alee.utils.swing.WebDefaultCellEditor;

@SuppressWarnings("serial")
public class ParametersDialog extends JDialog implements ActionListener {

	// Private attributes
	private JButton btnOK;
	private JButton btnCancel;
	private boolean dialogResult;

	private ApplicationManager appManager;
	private IEngine engine;
	private List<Project> projects;
	private List<Ontology> ontologies;
	private List<Scenario> scenarios;

	private Properties parameters = new Properties();
	private List<String> paramNames = new ArrayList<>();
	private List<String> paramTypes = new ArrayList<>();

	private JTable paramsTable;

	private TableCellEditor projectCellEditor;
	private TableCellEditor ontologyCellEditor;
	private TableCellEditor scenarioCellEditor;
	private TableCellEditor textCellEditor;
	private TableCellEditor fileCellEditor;

	/**
	 * Create the dialog with owner.
	 */
	public ParametersDialog(Window parent) {
		super(parent);
		this.appManager = ApplicationManager.UNIQUE_INSTANCE;
		initComponents();
	}

	/**
	 * Initialize the view components
	 */
	private void initComponents() {

		// Dialog configuration
		setTitle("Parameters");
		setBounds(0, 0, 345, 266);
		setResizable(false);
		setLocationRelativeTo(getParent());
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());

		// Create layout structure
		JPanel mainPane = new JPanel();
		mainPane.setLayout(new BorderLayout(0, 0));
		getContentPane().add(mainPane, BorderLayout.CENTER);

		// Create the parameters JTable (with custome editors)
		paramsTable = createParametersTable();
		paramsTable.setFont(new Font("Tahoma", Font.PLAIN, 12));
		paramsTable.setShowGrid(true);
		paramsTable.setGridColor(Color.LIGHT_GRAY);
		paramsTable.setRowSelectionAllowed(true);
		paramsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(paramsTable);
		mainPane.add(scrollPane, BorderLayout.CENTER);

		JPanel btnPane = new JPanel();
		FlowLayout flowLayout = (FlowLayout) btnPane.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		getContentPane().add(btnPane, BorderLayout.SOUTH);

		// Initialize button pane
		btnOK = new JButton("OK");
		btnOK.setPreferredSize(new Dimension(80, 25));
		btnOK.addActionListener(this);
		btnPane.add(btnOK);

		btnCancel = new JButton("Cancel");
		btnCancel.setPreferredSize(new Dimension(80, 25));
		btnCancel.addActionListener(this);
		btnPane.add(btnCancel);

		// Load combo contents
		projects   = appManager.getProjects();
		ontologies = appManager.getOntologies();
		scenarios  = appManager.getScenarios();

		initModel();
		initEditors();
		initRenderer();
		initListeners();
	}

	/**
	 * Initialize the parameter JTable
	 */
	private JTable createParametersTable() {

		return new JTable() {

			//  Determine editor to be used by row
			public TableCellEditor getCellEditor(int row, int col) {

				TableCellEditor editor;
				String rowType = paramTypes.get(row);

				if (rowType.equalsIgnoreCase("project")) {
					editor = projectCellEditor;
				}
				else if (rowType.equalsIgnoreCase("ontology")) {
					editor = ontologyCellEditor;
				}
				else if (rowType.equalsIgnoreCase("scenario")) {
					editor = scenarioCellEditor;
				}
				else if (rowType.equalsIgnoreCase("file") || rowType.equalsIgnoreCase("folder")) {
					editor = fileCellEditor;
				}
				else {
					editor = textCellEditor;
				}

				return editor;
			}
		};
	}

	/**
	 * Initialize component listeners
	 */
	private void initListeners() {

		// Replace default ENTER action
		SwingUtils.setInputKeyAction(this.getRootPane(), KeyEvent.VK_ESCAPE, "ENTER", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ParametersDialog.this.actionPerformed(new ActionEvent(btnOK, e.getID(), null));
			}
		});

		// Replace default ESCAPE action
		SwingUtils.setInputKeyAction(this.getRootPane(), KeyEvent.VK_ESCAPE, "ESCAPE", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ParametersDialog.this.actionPerformed(new ActionEvent(btnCancel, e.getID(), null));
			}
		});
	}

	/**
	 * Initialize editors used in table
	 */
	private void initEditors() {

		// Initialize project editor
		ProjectComboBox projectComboBox = new ProjectComboBox(projects);
		projectCellEditor = new WebDefaultCellEditor<>(projectComboBox);

		// Initialize ontology editor
		OntologyComboBox ontologyComboBox = new OntologyComboBox(ontologies);
		ontologyCellEditor = new WebDefaultCellEditor<>(ontologyComboBox);

		// Initialize scenario editor
		ScenarioComboBox scenarioComboBox = new ScenarioComboBox(scenarios);
		scenarioCellEditor = new WebDefaultCellEditor<>(scenarioComboBox);

		// Initialize file browser editor
		fileCellEditor = new FileTableCellEditor();

		// Initialize text field editor
		JTextField textField = new JTextField();
		textField.setBorder(null);
		textCellEditor = new DefaultCellEditor(textField);
	}

	/**
	 * Initialize the table model
	 */
	private void initModel() {

		paramsTable.setModel(new DefaultTableModel() {

			@Override
			public int getColumnCount() {
				return 2;
			}

			@Override
			public String getColumnName(int col) {

				switch (col) {
					case 0:
						return "Name";
					case 1:
						return "Value";
				}

				return null;
			}

			@Override
			public int getRowCount() {
				return paramNames.size();
			}

			public boolean isCellEditable(int row, int col) {
				return row > 0 && col > 0;
			}

			@Override
			public Object getValueAt(int row, int col) {

				String paramName = paramNames.get(row);

				if (col == 0) {
					return paramName;
				}

				return parameters.get(paramName);
			}

			@Override
			public void setValueAt(Object value, int row, int col) {

				if (value != null) {
					String paramName = paramNames.get(row);
					parameters.put(paramName, value);
				}
			}
		});
	}

	/**
	 * Initialize the table renderer
	 */
	private void initRenderer() {

		paramsTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {

				JLabel cellRenderer = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

				if (value != null) {

					String rowType = paramTypes.get(row);

					if (rowType.equalsIgnoreCase("project")) {
						cellRenderer.setText(((Project) value).getName());
					}
					else if (rowType.equalsIgnoreCase("ontology")) {
						cellRenderer.setText(((Ontology) value).getName());
					}
					else if (rowType.equalsIgnoreCase("scenario")) {
						cellRenderer.setText(((Scenario) value).getName());
					}
					else {
						cellRenderer.setText(value.toString());
					}
				}

				return cellRenderer;
			}
		});
	}

	/**
	 * Handle action generated by the view.
	 */
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == btnOK || e.getActionCommand().equals("ENTER")) {
			dialogResult = true;
		}
		else {
			dialogResult = false;
		}

		setVisible(false);
	}

	/**
	 * Return dialog result (true if use clicked on OK, false otherwise).
	 * 
	 * @return the dialogResult
	 */
	public boolean getDialogResult() {
		return dialogResult;
	}

	/**
	 * Retrieve engine this dialog is responsible of.
	 * 
	 * @return the engine
	 */
	public IEngine getEngine() {
		return engine;
	}

	/**
	 * Retrieve engine parameters as a properties object.
	 * 
	 * @return the properties object
	 */
	public Properties getParameters() {
		return parameters;
	}

	/**
	 * Sets the engine this dialog is responsible of.
	 * 
	 * @param engine
	 *        the engine to set
	 */
	public void setEngine(IEngine engine) {

		this.engine = engine;

		paramNames.clear();
		paramTypes.clear();
		parameters.clear();

		// Initialize first parameter
		paramNames.add("engine");
		paramTypes.add("none");
		parameters.put("engine", engine.getName());

		// Extract engine parameter keys and types
		Enumeration<?> e = engine.getParameters().propertyNames();

		// Initialize all engine parameters
		while (e.hasMoreElements()) {

			String paramName = (String) e.nextElement();
			paramNames.add(paramName);
			paramTypes.add(engine.getParameters().getProperty(paramName));
		}

		repaint();
	}
}
