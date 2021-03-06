package ch.hesge.csim2.ui.popup;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.ConceptLink;
import ch.hesge.csim2.ui.views.OntologyView;

@SuppressWarnings("serial")
public class ConceptPopup extends JPopupMenu implements ActionListener {

	// Private attributes
	private OntologyView view;
	private Concept concept;
	private ConceptLink link;
	private Point popupLocation;
	private JMenuItem mnuNewConcept;
	private JMenuItem mnuDeleteConcept;
	private JMenuItem mnuNewLink;
	private JMenuItem mnuDeleteLink;
	private JMenuItem mnuProperties;

	/**
	 * Default constructor
	 */
	public ConceptPopup(OntologyView view) {
		this.view = view;
		initComponent();
	}

	/**
	 * Initialize the component
	 */
	private void initComponent() {

		mnuNewConcept = new JMenuItem("Create a new concept");
		mnuNewConcept.addActionListener(this);
		add(mnuNewConcept);

		mnuDeleteConcept = new JMenuItem("Delete the concept");
		mnuDeleteConcept.addActionListener(this);
		add(mnuDeleteConcept);

		add(new JSeparator());

		mnuNewLink = new JMenuItem("Create a new link");
		mnuNewLink.addActionListener(this);
		add(mnuNewLink);

		mnuDeleteLink = new JMenuItem("Delete the link");
		mnuDeleteLink.addActionListener(this);
		add(mnuDeleteLink);

		add(new JSeparator());

		mnuProperties = new JMenuItem("Properties");
		mnuProperties.addActionListener(this);
		add(mnuProperties);
	}

	/**
	 * Return the concept this popup is attached, or null.
	 * 
	 * @return a concept or null
	 */
	public Concept getConcept() {
		return concept;
	}

	/**
	 * Set the concept this popup is attached to.
	 * 
	 * @param concept
	 */
	public void setConcept(Concept concept) {

		this.concept = concept;

		mnuNewConcept.setEnabled(true);
		mnuDeleteConcept.setEnabled(concept != null);
		mnuNewLink.setEnabled(concept != null);
		mnuDeleteLink.setEnabled(false);
		mnuProperties.setEnabled(concept != null);
	}

	/**
	 * Return the link this popup is attached, or null.
	 * 
	 * @return a link or null
	 */
	public ConceptLink getLink() {
		return link;
	}

	/**
	 * Set the link this popup is attached to.
	 * 
	 * @param link
	 */
	public void setLink(ConceptLink link) {

		this.link = link;

		mnuNewConcept.setEnabled(true);
		mnuDeleteConcept.setEnabled(false);
		mnuNewLink.setEnabled(false);
		mnuDeleteLink.setEnabled(link != null);
		mnuProperties.setEnabled(false);
	}

	/**
	 * Return the popup upper-left corner location.
	 * 
	 * @return a point
	 */
	public Point getPopupLocation() {
		return popupLocation;
	}

	/**
	 * Sets the popup upper-left corner location.
	 * 
	 * @param popupLocation
	 */
	public void setPopupLocation(Point popupLocation) {
		this.popupLocation = popupLocation;
	}

	/**
	 * Handle action generated by menu.
	 */
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == mnuNewConcept) {
			view.createConcept(popupLocation);
		}
		else if (e.getSource() == mnuDeleteConcept) {
			view.deleteConcept(concept);
		}
		else if (e.getSource() == mnuNewLink) {
			view.startLinkFrom(concept);
		}
		else if (e.getSource() == mnuDeleteLink) {
			view.deleteLink(link);
		}
		else if (e.getSource() == mnuProperties) {
			view.showProperties(concept);
		}
	}
}
