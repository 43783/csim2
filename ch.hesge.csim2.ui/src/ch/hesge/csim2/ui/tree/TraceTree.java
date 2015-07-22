package ch.hesge.csim2.ui.tree;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import ch.hesge.csim2.core.model.Trace;

@SuppressWarnings("serial")
public class TraceTree extends JTree {

	// Private attributes
	private Trace traceRoot;
	
	/**
	 * Default constructor
	 */
	public TraceTree() {

		initComponent();
		initRenderer();
		initModel();
	}

	/**
	 * Initialize the component
	 */
	private void initComponent() {
		
		setEnabled(true);
		//setRootVisible(false);
		setLargeModel(true);
		setShowsRootHandles(true);
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	}
	
	/**
	 * Initialize tree node renderer
	 */
	private void initRenderer() {

		setCellRenderer(new DefaultTreeCellRenderer() {
			@Override
			public Component getTreeCellRendererComponent(JTree tree, Object node, boolean isSelected, boolean isExpanded, boolean isLeaf, int row, boolean hasFocus) {
				
				JLabel cellRenderer = (JLabel) super.getTreeCellRendererComponent(tree, node, isSelected, isExpanded, isLeaf, row, hasFocus);
				Object userObject = ((DefaultMutableTreeNode) node).getUserObject();

				if (userObject instanceof Trace) {
					Trace trace = (Trace) userObject;
					cellRenderer.setText(trace.getStaticClass() + "." + trace.getSignature());
				}

				cellRenderer.setIcon(null);
				
				return this;
			}			
		});
	}
	
	/**
	 * Initialize tree model and its hierarchy
	 */
	private void initModel() {
		
		if (traceRoot != null) {
			
			// Create the hierarchy nodes
			DefaultMutableTreeNode rootNode = createTraceNode(traceRoot);
						
			// Define tree model
			setModel(new DefaultTreeModel(rootNode));	
		}
		else {
			// Define a null model
			setModel(null);	
		}
	}
	
	/**
	 * Sets the tree root to display.
	 * 
	 * @param traceRoot
	 */
	public void setTraceRoot(Trace traceRoot) {
		this.traceRoot = traceRoot;
		initModel();
		repaint();
	}
	
	/**
	 * Create recursively a trace node for each trace item
	 */
	private DefaultMutableTreeNode createTraceNode(Trace trace) {
		
		DefaultMutableTreeNode traceNode = new DefaultMutableTreeNode(trace);
		
		for (Trace child : trace.getChildren()) {
			traceNode.add(createTraceNode(child));
		}
		
		return traceNode;
	}
	/*
	@Override
	public Enumeration<TreePath> getExpandedDescendants(TreePath parent) {
		if (!isExpanded(parent)) {
			return null;
		}

		List<TreePath> openedChildren = getOpenedChildren(parent, new ArrayList<>());

		Iterator<TreePath> iterator = openedChildren.iterator();
		return new Enumeration<TreePath>() {
			public boolean hasMoreElements() {
				return iterator.hasNext();
			}

			public TreePath nextElement() {
				return (TreePath) iterator.next();
			}
		};
	}
	*/

	/**
	 * Search for opened children recursively
	 */
	/*
	private List<TreePath> getOpenedChildren(TreePath paramTreeNode, List<TreePath> list) {
		final Object parent = paramTreeNode.getLastPathComponent();
		final TreeModel model = getModel();
		int nbChild = model.getChildCount(parent);
		for (int i = 0; i < nbChild; i++) {
			Object child = model.getChild(parent, i);
			final TreePath childPath = paramTreeNode.pathByAddingChild(child);
			if (!model.isLeaf(child) && isExpanded(childPath)) {
				//Add child if oppened
				list.add(childPath);
				getOpenedChildren(childPath, list);
			}
		}
		return list;
	}
	*/
}
