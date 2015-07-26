package ch.hesge.csim2.ui.tree;

import java.awt.Component;
import java.util.List;

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
	private List<Trace> traceRoots;
	
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
		setRootVisible(false);
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
		
		if (traceRoots != null) {

			DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
			
			// Create one node by source class
			for (Trace traceRoot : traceRoots) {
				rootNode.add(createTraceNode(traceRoot));
			}
						
			// Define concept tree model
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
	public void setTraceRoots(List<Trace> traceRoots) {
		this.traceRoots = traceRoots;
		initModel();
		repaint();
	}
	
	/**
	 * Create recursively a trace node for each trace
	 */
	private DefaultMutableTreeNode createTraceNode(Trace trace) {
		
		DefaultMutableTreeNode traceNode = new DefaultMutableTreeNode(trace);
		
		for (Trace child : trace.getChildren()) {
			traceNode.add(createTraceNode(child));
		}
		
		return traceNode;
	}
}
