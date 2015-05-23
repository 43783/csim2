package ch.hesge.csim2.ui.comp;

import java.awt.Component;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import ch.hesge.csim2.core.model.SourceAttribute;
import ch.hesge.csim2.core.model.SourceClass;
import ch.hesge.csim2.core.model.SourceMethod;
import ch.hesge.csim2.ui.views.MainView;

@SuppressWarnings("serial")
public class SourceTree extends JTree {

	// Private attributes
	private List<SourceClass> sourceClasses;
	
	/**
	 * Default constructor
	 */
	public SourceTree(List<SourceClass> sourceClasses) {

		this.sourceClasses = sourceClasses;
		
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

				if (userObject instanceof SourceClass) {
					SourceClass sourceClass = (SourceClass) userObject;
					cellRenderer.setText(sourceClass.getName());
					cellRenderer.setIcon(new ImageIcon(MainView.class.getResource("/ch/hesge/csim2/ui/icons/class.png")));
				}
				else if (userObject instanceof SourceMethod) {
					SourceMethod sourceMethod = (SourceMethod) userObject;
					cellRenderer.setText(sourceMethod.getSignature());
					cellRenderer.setIcon(new ImageIcon(MainView.class.getResource("/ch/hesge/csim2/ui/icons/method.png")));
				}
				else if (userObject instanceof SourceAttribute) {
					SourceAttribute sourceAttribute = (SourceAttribute) userObject;
					cellRenderer.setText(sourceAttribute.getName());
					cellRenderer.setIcon(new ImageIcon(MainView.class.getResource("/ch/hesge/csim2/ui/icons/attribute.png")));
				}
				else {
					cellRenderer.setIcon(null);
				}
				
				return this;
			}			
		});
	}
	
	/**
	 * Initialize tree model and its hierarchy
	 */
	private void initModel() {
		
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
		
		// Create one node by source class
		for (SourceClass sourceClass : sourceClasses) {
			rootNode.add(createClassNode(sourceClass));
		}
					
		// Define concept tree model
		setModel(new DefaultTreeModel(rootNode));	
	}
	
	/**
	 * Create recursively a source node item within the project tree
	 */
	private DefaultMutableTreeNode createClassNode(SourceClass sourceClass) {
		
		DefaultMutableTreeNode classNode = new DefaultMutableTreeNode(sourceClass);
		
		for (SourceAttribute sourceAttribute : sourceClass.getAttributes()) {
			classNode.add(new DefaultMutableTreeNode(sourceAttribute));
		}

		for (SourceMethod sourceMethod : sourceClass.getMethods()) {
			classNode.add(new DefaultMutableTreeNode(sourceMethod));
		}

		for (SourceClass childClass : sourceClass.getSubClasses()) {
			DefaultMutableTreeNode childNode = createClassNode(childClass); 
			classNode.add(childNode);
		}		
		
		return classNode;
	}
}
