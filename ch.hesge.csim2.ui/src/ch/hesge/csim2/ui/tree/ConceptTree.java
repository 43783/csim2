package ch.hesge.csim2.ui.tree;

import java.awt.Component;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.ConceptAttribute;
import ch.hesge.csim2.core.model.ConceptClass;
import ch.hesge.csim2.ui.views.MainView;

@SuppressWarnings("serial")
public class ConceptTree extends JTree {

	// Private attributes
	private List<Concept> concepts;
	
	/**
	 * Default constructor
	 */
	public ConceptTree(List<Concept> concepts) {

		this.concepts = concepts;
		
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

				if (userObject instanceof Concept) {
					Concept concept = (Concept) userObject;
					cellRenderer.setText(concept.getName());
					cellRenderer.setIcon(new ImageIcon(MainView.class.getResource("/ch/hesge/csim2/ui/icons/concept.png")));
				}
				else if (userObject instanceof ConceptAttribute) {
					ConceptAttribute conceptAttribute = (ConceptAttribute) userObject;
					cellRenderer.setText(conceptAttribute.getName());
					cellRenderer.setIcon(new ImageIcon(MainView.class.getResource("/ch/hesge/csim2/ui/icons/method.png")));
				}
				else if (userObject instanceof ConceptClass) {
					ConceptClass conceptClass = (ConceptClass) userObject;
					cellRenderer.setText(conceptClass.getName());
					cellRenderer.setIcon(new ImageIcon(MainView.class.getResource("/ch/hesge/csim2/ui/icons/class.png")));
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
		
		// Create one node by concept
		for (Concept concept : concepts) {
			
			DefaultMutableTreeNode conceptNode = new DefaultMutableTreeNode(concept);
			
			for (ConceptAttribute conceptAttribute : concept.getAttributes()) {
				
				DefaultMutableTreeNode attributeNode = new DefaultMutableTreeNode(conceptAttribute);
				conceptNode.add(attributeNode);
								
				DefaultMutableTreeNode identifierNode = new DefaultMutableTreeNode(conceptAttribute.getIdentifier());
				attributeNode.add(identifierNode);
			}

			for (ConceptClass conceptClass : concept.getClasses()) {

				DefaultMutableTreeNode classNode = new DefaultMutableTreeNode(conceptClass);
				conceptNode.add(classNode);
								
				DefaultMutableTreeNode identifierNode = new DefaultMutableTreeNode(conceptClass.getIdentifier());
				classNode.add(identifierNode);
			}

			rootNode.add(conceptNode);
		}
					
		// Define concept tree model
		setModel(new DefaultTreeModel(rootNode));	
	}
	
}
