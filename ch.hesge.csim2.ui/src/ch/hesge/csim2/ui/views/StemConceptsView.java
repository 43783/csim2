package ch.hesge.csim2.ui.views;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.TreeSelectionEvent;

import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.StemConcept;
import ch.hesge.csim2.ui.model.ApplicationManager;
import ch.hesge.csim2.ui.table.StemConceptTable;
import ch.hesge.csim2.ui.tree.ConceptTree;
import ch.hesge.csim2.ui.utils.SimpleAction;
import ch.hesge.csim2.ui.utils.SwingUtils;

@SuppressWarnings("serial")
public class StemConceptsView extends JPanel {

	// Private attribute
	private List<Concept> concepts;
	private Map<Integer, StemConcept> stemTree;

	private ConceptTree conceptTree;
	private StemConceptTable stemTable;

	/**
	 * Default constructor.
	 */
	public StemConceptsView(Project project) {

		// Retrieve required data from cache
		ApplicationManager appManager = ApplicationManager.UNIQUE_INSTANCE;
		this.concepts = appManager.getConcepts(project);
		this.stemTree = appManager.getStemConceptTreeMap(project);

		initComponent();
	}

	/**
	 * Initialize the component
	 */
	private void initComponent() {

		setLayout(new BorderLayout(0, 0));

		// Initialize split panes
		JSplitPane splitPanel1 = new JSplitPane();
		splitPanel1.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		splitPanel1.setResizeWeight(0.4);
		add(splitPanel1, BorderLayout.CENTER);

		// Initialize concept tree
		conceptTree = new ConceptTree(concepts);
		conceptTree.setFocusable(true);
		JScrollPane scrollPane1 = new JScrollPane();
		scrollPane1.setViewportView(conceptTree);
		splitPanel1.setLeftComponent(scrollPane1);

		// Initialize stem table
		stemTable = new StemConceptTable();
		stemTable.setFillsViewportHeight(true);
		stemTable.setFocusable(true);
		JScrollPane scrollPane2 = new JScrollPane();
		scrollPane2.setViewportView(stemTable);
		splitPanel1.setRightComponent(scrollPane2);
		
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
				conceptTree.requestFocus();
			}
		});

		// Listen to tree selection
		SwingUtils.onTreeSelection(conceptTree, new SimpleAction<TreeSelectionEvent>() {
			@Override
			public void run(TreeSelectionEvent e) {

				// Retrieve current user object
				Object userObject = SwingUtils.getTreeUserObject(e.getPath());
				
				if (userObject != null && userObject instanceof Concept) {
					
					// Retrieve selected concept
					Concept concept = (Concept) userObject;
					
					// Update current stem list
					StemConcept stemConceptTree = stemTree.get(concept.getKeyId());
					stemTable.setStemTree(stemConceptTree);
				}
				else {
					stemTable.setStemTree(null);
				}
			}
		});
	}
}
