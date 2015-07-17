package ch.hesge.csim2.ui.views;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.TreeSelectionEvent;

import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.SourceClass;
import ch.hesge.csim2.core.model.SourceMethod;
import ch.hesge.csim2.core.model.StemMethod;
import ch.hesge.csim2.ui.model.ApplicationManager;
import ch.hesge.csim2.ui.table.StemMethodTable;
import ch.hesge.csim2.ui.tree.SourceTree;
import ch.hesge.csim2.ui.utils.SimpleAction;
import ch.hesge.csim2.ui.utils.SwingUtils;

@SuppressWarnings("serial")
public class StemSourcesView extends JPanel {

	// Private attributes
	private List<SourceClass> sourceClasses;
	private Map<Integer, StemMethod> stemTree;

	private SourceTree sourceTree;
	private StemMethodTable stemTable;
	
	/**
	 * Default constructor.
	 */
	public StemSourcesView(Project project) {

		ApplicationManager appManager = ApplicationManager.UNIQUE_INSTANCE;

		this.sourceClasses = appManager.getSourceClasses(project);
		this.stemTree = appManager.getStemMethodTreeMap(project);
		
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
		sourceTree = new SourceTree(sourceClasses);
		sourceTree.setFocusable(true);
		JScrollPane scrollPane1 = new JScrollPane();
		scrollPane1.setViewportView(sourceTree);
		splitPanel1.setLeftComponent(scrollPane1);

		// Initialize stem table
		stemTable = new StemMethodTable();
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
		SwingUtils.onComponentVisible(sourceTree, new SimpleAction<Object>() {
			@Override
			public void run(Object o) {
				sourceTree.requestFocus();
			}
		});

		// Listen to tree selection
		SwingUtils.onTreeSelection(sourceTree, new SimpleAction<TreeSelectionEvent>() {
			@Override
			public void run(TreeSelectionEvent e) {

				// Retrieve current user object
				Object userObject = SwingUtils.getTreeUserObject(e.getPath());
				
				if (userObject != null) {
					
					if (userObject instanceof SourceMethod) {
						
						// Update current stem list
						SourceMethod method = (SourceMethod) userObject;
						StemMethod stemMethodTree = stemTree.get(method.getKeyId());
						stemTable.setStemTree(stemMethodTree);
					}
					else {
						stemTable.setStemTree(null);
					}
				}
			}
		});
	}
}
