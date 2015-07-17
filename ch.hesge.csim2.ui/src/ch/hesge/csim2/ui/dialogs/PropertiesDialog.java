package ch.hesge.csim2.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;

import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.ui.model.ApplicationManager;
import ch.hesge.csim2.ui.table.ConceptAttributesTable;
import ch.hesge.csim2.ui.table.ConceptClassesTable;
import ch.hesge.csim2.ui.utils.SimpleAction;
import ch.hesge.csim2.ui.utils.SwingUtils;

@SuppressWarnings("serial")
public class PropertiesDialog extends JDialog implements ActionListener {

	// Private attributes
	private Concept concept;
	private ApplicationManager appManager;
	private JCheckBox isActionConceptCheck;
	private ConceptAttributesTable attributesTable;
	private ConceptClassesTable classesTable;
	private JButton btnSave;
	private JButton btnCancel;
	private boolean dialogResult;
	private JButton btnAddAttribute;
	private JButton btnDelAttribute;
	private JButton btnAddAttributeClass;
	private JButton btnDelAttributeClass;

	/**
	 * Create the dialog with owner.
	 */
	public PropertiesDialog(Window parent, Concept concept) {
		super(parent);
		this.appManager = ApplicationManager.UNIQUE_INSTANCE;
		this.concept = appManager.cloneConcept(concept);
		initComponents();
	}

	/**
	 * Initialize the view components
	 */
	private void initComponents() {

		// Dialog configuration
		setTitle("Detail");
		setBounds(0, 0, 473, 481);
		setLocationRelativeTo(getParent());
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setResizable(false);

		// Initialize main panel
		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(new BorderLayout(0, 0));

		// Initialize title panel
		JPanel titlePanel = new JPanel();
		titlePanel.setBorder(new CompoundBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Concept", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)), new EmptyBorder(5, 5, 5, 5)));
		mainPanel.add(titlePanel, BorderLayout.NORTH);
		titlePanel.setLayout(new BorderLayout(0, 0));
		
		titlePanel.add(new JLabel("Name: " + concept.getName()), BorderLayout.CENTER);
		isActionConceptCheck = new JCheckBox("Is action concept");
		isActionConceptCheck.setSelected(concept.isAction());
		titlePanel.add(isActionConceptCheck, BorderLayout.EAST);
		
		// Initialize split panel
		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.7);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		mainPanel.add(splitPane, BorderLayout.CENTER);

		// Initialize button panel
		JPanel btnPane = new JPanel();
		FlowLayout flowBtnLayout = (FlowLayout) btnPane.getLayout();
		flowBtnLayout.setAlignment(FlowLayout.RIGHT);
		getContentPane().add(btnPane, BorderLayout.SOUTH);

		// Initialize attribute panel
		JPanel attributesPanel = new JPanel();
		splitPane.setLeftComponent(attributesPanel);
		attributesPanel.setBorder(new TitledBorder(null, "Attributes", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		attributesPanel.setLayout(new BorderLayout(0, 0));

		// Initialize attribute table
		attributesTable = new ConceptAttributesTable(concept.getAttributes());
		attributesTable.setFillsViewportHeight(true);
		JScrollPane attributeScrollPane = new JScrollPane();
		attributeScrollPane.setViewportView(attributesTable);
		attributesPanel.add(attributeScrollPane, BorderLayout.CENTER);

		// Initialize attribute buttons
		JPanel btnAttributePanel = new JPanel();
		FlowLayout fl_btnAttributePanel = (FlowLayout) btnAttributePanel.getLayout();
		fl_btnAttributePanel.setAlignment(FlowLayout.LEFT);
		attributesPanel.add(btnAttributePanel, BorderLayout.SOUTH);

		// Initialize classes panel
		JPanel classesPanel = new JPanel();
		splitPane.setRightComponent(classesPanel);
		classesPanel.setBorder(new TitledBorder(null, "Classes", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		classesPanel.setLayout(new BorderLayout(0, 0));
		
		// Initialize classes table
		classesTable = new ConceptClassesTable(concept.getClasses());
		classesTable.setFillsViewportHeight(true);
		JScrollPane classScrollPane = new JScrollPane();
		classScrollPane.setViewportView(classesTable);
		classesPanel.add(classScrollPane, BorderLayout.CENTER);

		// Initialize classes buttons
		JPanel btnClassPanel = new JPanel();
		FlowLayout fl_btnClassPanel = (FlowLayout) btnClassPanel.getLayout();
		fl_btnClassPanel.setAlignment(FlowLayout.LEFT);
		classesPanel.add(btnClassPanel, BorderLayout.SOUTH);

		btnAddAttribute = new JButton("Add");
		btnAddAttribute.setPreferredSize(new Dimension(80, 25));
		btnAddAttribute.addActionListener(this);
		btnAttributePanel.add(btnAddAttribute);

		btnDelAttribute = new JButton("Delete");
		btnDelAttribute.setEnabled(false);
		btnDelAttribute.setPreferredSize(new Dimension(80, 25));
		btnDelAttribute.addActionListener(this);
		btnAttributePanel.add(btnDelAttribute);

		btnAddAttributeClass = new JButton("Add");
		btnAddAttributeClass.setPreferredSize(new Dimension(80, 25));
		btnAddAttributeClass.addActionListener(this);
		btnClassPanel.add(btnAddAttributeClass);

		btnDelAttributeClass = new JButton("Delete");
		btnDelAttributeClass.setEnabled(false);
		btnDelAttributeClass.setPreferredSize(new Dimension(80, 25));
		btnDelAttributeClass.addActionListener(this);
		btnClassPanel.add(btnDelAttributeClass);

		btnSave = new JButton("Save");
		btnSave.setPreferredSize(new Dimension(100, 25));
		btnSave.addActionListener(this);
		btnPane.add(btnSave);

		btnCancel = new JButton("Cancel");
		btnCancel.setPreferredSize(new Dimension(100, 25));
		btnCancel.addActionListener(this);
		btnPane.add(btnCancel);

		initListeners();
	}

	/**
	 * Initialize component listeners
	 */
	private void initListeners() {

		// Listen to attribute selection
		SwingUtils.onTableSelection(attributesTable, new SimpleAction<ListSelectionEvent>() {
			@Override
			public void run(ListSelectionEvent e) {
				btnDelAttribute.setEnabled(attributesTable.getSelectedObject() != null);				
			}
		});

		// Listen to attribute selection
		SwingUtils.onTableSelection(classesTable, new SimpleAction<ListSelectionEvent>() {
			@Override
			public void run(ListSelectionEvent e) {
				btnDelAttributeClass.setEnabled(classesTable.getSelectedObject() != null);				
			}
		});

		// Replace default ENTER action
		SwingUtils.setInputKeyAction(this.getRootPane(), KeyEvent.VK_ESCAPE, "ENTER", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PropertiesDialog.this.actionPerformed(new ActionEvent(btnSave, e.getID(), null));
			}
		});

		// Replace default ESCAPE action
		SwingUtils.setInputKeyAction(this.getRootPane(), KeyEvent.VK_ESCAPE, "ESCAPE", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PropertiesDialog.this.actionPerformed(new ActionEvent(btnCancel, e.getID(), null));
			}
		});
	}

	/**
	 * Return dialog result.
	 * true = user clicked on OK
	 * false = use clicked on Cancel or ESC
	 * 
	 * @return the dialogResult
	 */
	public boolean getDialogResult() {
		return dialogResult;
	}

	/**
	 * Return the modified concept.
	 * 
	 * @return a clone of the original concept modified
	 */
	public Concept getModifiedConcept() {
		return concept;
	}

	/**
	 * Handle action generated by the view.
	 */
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == btnSave) {
			dialogResult = true;
			concept.setAction(isActionConceptCheck.isSelected());
			this.setVisible(false);
		}
		else if (e.getSource() == btnCancel) {
			dialogResult = false;
			this.setVisible(false);
		}
		else if (e.getSource() == btnAddAttribute) {
			appManager.createConceptAttribute(concept);
			attributesTable.refresh();
		}
		else if (e.getSource() == btnDelAttribute) {
			if (attributesTable.getSelectedObject() != null) {
				appManager.deleteConceptAttribute(concept, attributesTable.getSelectedObject());
				attributesTable.refresh();
			}
		}
		else if (e.getSource() == btnAddAttributeClass) {
			appManager.createConceptClass(concept);
			classesTable.refresh();
		}
		else if (e.getSource() == btnDelAttributeClass) {
			if (classesTable.getSelectedObject() != null) {
				appManager.deleteConceptClass(concept, classesTable.getSelectedObject());
				classesTable.refresh();
			}
		}
		
		requestFocus();
	}
}
