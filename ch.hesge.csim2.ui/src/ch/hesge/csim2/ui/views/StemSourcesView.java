package ch.hesge.csim2.ui.views;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import ch.hesge.csim2.core.logic.ApplicationLogic;
import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.SourceClass;
import ch.hesge.csim2.core.model.SourceMethod;
import ch.hesge.csim2.core.model.StemMethod;
import ch.hesge.csim2.ui.comp.SourceTree;
import ch.hesge.csim2.ui.comp.StemMethodTable;
import ch.hesge.csim2.ui.utils.SwingUtils;

@SuppressWarnings("serial")
public class StemSourcesView extends JPanel {

	// Private attributes
	private SourceTree sourceTree;
	private StemMethodTable stemTable;
	
	private List<SourceClass> sourceClasses;
	private Map<Integer, StemMethod> stemTree;
	/**
	 * Default constructor.
	 */
	public StemSourcesView(Project project, List<SourceClass> sourceClasses) {

		this.sourceClasses = sourceClasses;
		this.stemTree = ApplicationLogic.getStemMethodTreeMap(project);
		
		initComponent();
	}

	/**
	 * Initialize the component
	 */
	private void initComponent() {

		setBorder(new TitledBorder(null, "Stem sources", TitledBorder.LEADING, TitledBorder.TOP, null, null));
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
		stemTable.setFocusable(true);
		JScrollPane scrollPane2 = new JScrollPane();
		scrollPane2.setViewportView(stemTable);
		splitPanel1.setRightComponent(scrollPane2);
		
		initListeners();

		// Focus on class tree
		SwingUtils.invokeWhenVisible(this, new Runnable() {
			@Override
			public void run() {
				sourceTree.requestFocus();
			}
		});
	}

	/**
	 * Initialize component inner listeners
	 */
	private void initListeners() {

		// Add tree listener
		sourceTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {

				Object userObject = ((DefaultMutableTreeNode) e.getPath().getLastPathComponent()).getUserObject();

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
		});		
	}
}
