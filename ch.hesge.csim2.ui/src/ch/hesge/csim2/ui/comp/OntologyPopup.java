package ch.hesge.csim2.ui.comp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import ch.hesge.csim2.core.model.Ontology;
import ch.hesge.csim2.ui.views.ActionHandler;

@SuppressWarnings("serial")
public class OntologyPopup extends JPopupMenu implements ActionListener {

	// Private attributes
	private Ontology ontology;
	private ActionHandler actionHandler;
	private JMenuItem mnuNew;
	private JMenuItem mnuRename;
	private JMenuItem mnuOpen;
	private JMenuItem mnuDelete;

	/**
	 * Default constructor
	 */
	public OntologyPopup(ActionHandler actionHandler) {
		
		this.actionHandler = actionHandler;
		initComponent();
	}

	/**
	 * Initialize the component
	 */
	private void initComponent() {
		
		mnuNew = new JMenuItem("New Ontology");
		mnuNew.addActionListener(this);
		add(mnuNew);

		mnuRename = new JMenuItem("Rename");
		mnuRename.addActionListener(this);
		add(mnuRename);

		mnuDelete = new JMenuItem("Delete");
		mnuDelete.addActionListener(this);
		add(mnuDelete);

		add(new JSeparator());

		mnuOpen = new JMenuItem("Open");
		mnuOpen.addActionListener(this);
		add(mnuOpen);
	}

	/**
	 * Clear menu state
	 */
	public void clearMenuState() {	
		
		mnuNew.setEnabled(false);
		mnuRename.setEnabled(false);
		mnuOpen.setEnabled(false);
		mnuDelete.setEnabled(false);
	}
	
	/**
	 * Enable/disable new menu
	 */
	public void setCreateMenuState(boolean state) {
		mnuNew.setEnabled(state);
	}

	/**
	 * Enable/disable rename menu
	 */
	public void setRenameMenuState(boolean state) {
		mnuRename.setEnabled(state);
	}

	/**
	 * Enable/disable edit menu
	 */
	public void setOpenMenuState(boolean state) {
		mnuOpen.setEnabled(state);
	}

	/**
	 * Enable/disable delete menu
	 */
	public void setDeleteMenuState(boolean state) {
		mnuDelete.setEnabled(state);
	}

	/**
	 * @return the ontology
	 */
	public Ontology getOntology() {
		return ontology;
	}

	/**
	 * @param ontology
	 *            the ontology to set
	 */
	public void setOntology(Ontology ontology) {
		this.ontology = ontology;
	}

	/**
	 * Handle action generated by menu.
	 */
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == mnuNew) {
			actionHandler.showMessage("Warning", "This feature is not yet implemented !", JOptionPane.WARNING_MESSAGE);
		}
		else if (e.getSource() == mnuRename) {
			actionHandler.showMessage("Warning", "This feature is not yet implemented !", JOptionPane.WARNING_MESSAGE);
		}
		else if (e.getSource() == mnuOpen) {
			actionHandler.showOntology(ontology);
		}
		else if (e.getSource() == mnuDelete) {
			actionHandler.showMessage("Warning", "This feature is not yet implemented !", JOptionPane.WARNING_MESSAGE);
		}
	}

}
