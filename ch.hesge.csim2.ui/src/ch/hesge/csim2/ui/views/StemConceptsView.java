package ch.hesge.csim2.ui.views;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.StemConcept;
import ch.hesge.csim2.ui.comp.ConceptTree;
import ch.hesge.csim2.ui.comp.StemConceptTable;
import ch.hesge.csim2.ui.model.ApplicationManager;
import ch.hesge.csim2.ui.utils.SwingUtils;

@SuppressWarnings("serial")
public class StemConceptsView extends JPanel {

	// Private attribute
	private ApplicationManager appManager;
	private ConceptTree conceptTree;
	private StemConceptTable stemTable;

	private List<Concept> concepts;
	private Map<Integer, StemConcept> stemTree;

	/**
	 * Default constructor.
	 */
	public StemConceptsView(List<Concept> concepts, Map<Integer, StemConcept> stemTree) {
		this.appManager = ApplicationManager.UNIQUE_INSTANCE;
		this.concepts = concepts;
		this.stemTree = stemTree;
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
		stemTable = new StemConceptTable(appManager);
		stemTable.setFocusable(true);
		JScrollPane scrollPane2 = new JScrollPane();
		scrollPane2.setViewportView(stemTable);
		splitPanel1.setRightComponent(scrollPane2);
		
		initListeners();

		// Focus on class tree
		SwingUtils.invokeWhenVisible(this, new Runnable() {
			@Override
			public void run() {
				conceptTree.requestFocus();
			}
		});
	}

	/**
	 * Initialize component inner listeners
	 */
	private void initListeners() {

		// Add tree listener
		conceptTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {

				Object userObject = ((DefaultMutableTreeNode) e.getPath().getLastPathComponent()).getUserObject();

				if (userObject instanceof Concept) {

					// Update current stem list
					Concept concept = (Concept) userObject;
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
