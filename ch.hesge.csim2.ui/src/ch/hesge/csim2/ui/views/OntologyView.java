package ch.hesge.csim2.ui.views;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.AncestorEvent;

import ch.hesge.csim2.core.logic.ApplicationLogic;
import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.ConceptLink;
import ch.hesge.csim2.core.model.Ontology;
import ch.hesge.csim2.ui.comp.OntologyAnimator;
import ch.hesge.csim2.ui.comp.OntologyPanel;
import ch.hesge.csim2.ui.dialogs.ConceptPropertiesDialog;
import ch.hesge.csim2.ui.utils.PaintUtils;
import ch.hesge.csim2.ui.utils.SwingUtils;

import com.alee.utils.swing.AncestorAdapter;

@SuppressWarnings("serial")
public class OntologyView extends JPanel implements ActionListener {

	// Private attributes
	private Ontology ontology;
	private OntologyPanel ontologyPanel;
	private OntologyAnimator animator;
	private JCheckBox btnDynamic;
	private JButton btnShake;
	private JButton btnSave;

	/**
	 * Default constructor
	 */
	public OntologyView(Ontology ontology) {

		this.ontology = ontology;

		// Load ontology concepts
		List<Concept> concepts = ApplicationLogic.getConcepts(ontology);
		ontology.getConcepts().clear();
		ontology.getConcepts().addAll(concepts);

		initComponent();
	}

	/**
	 * Initialize the component
	 */
	private void initComponent() {

		setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPanel = new JScrollPane();
		ontologyPanel = new OntologyPanel(ontology, scrollPanel);
		ontologyPanel.addActionListener(this);
		scrollPanel.setViewportView(ontologyPanel);
		add(scrollPanel, BorderLayout.CENTER);

		JPanel btnPanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) btnPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		add(btnPanel, BorderLayout.SOUTH);

		animator = new OntologyAnimator(ontologyPanel, ontology);

		btnDynamic = new JCheckBox("Dynamic");
		btnDynamic.setPreferredSize(new Dimension(80, 25));
		btnDynamic.addActionListener(this);
		btnPanel.add(btnDynamic);

		btnShake = new JButton("Shake");
		btnShake.setPreferredSize(new Dimension(80, 25));
		btnShake.addActionListener(this);
		btnPanel.add(btnShake);

		btnSave = new JButton("Save");
		btnSave.setPreferredSize(new Dimension(80, 25));
		btnSave.addActionListener(this);
		btnPanel.add(btnSave);

		initListeners();
	}

	/**
	 * Initialize component inner listeners
	 */
	private void initListeners() {

		// Listen to view visibility
		addAncestorListener(new AncestorAdapter() {
			@Override
			public void ancestorAdded(AncestorEvent event) {
				ontologyPanel.requestFocus();
				animator.resume();
			}

			@Override
			public void ancestorRemoved(AncestorEvent event) {
				animator.suspend();
			}
		});
	}

	/**
	 * Create a new concept
	 * and insert it to the ontology.
	 */
	private void createConcept() {

		Graphics g = getGraphics();
		double scaleFactor = ontologyPanel.getScaledFactor();

		// Sets font size
		Font scaledFont = getFont().deriveFont((float) Math.round(10 * scaleFactor));
		g.setFont(scaledFont);

		// Create a new concept
		Concept concept = ApplicationLogic.createConcept(ontology);

		// Calculate new concept bounds
		Rectangle viewBounds = PaintUtils.getCenteredText(g, concept.getBounds(), concept.getName());
		viewBounds.grow(g.getFont().getSize(), g.getFont().getSize());
		viewBounds.x = ontologyPanel.getMousePosition().x;
		viewBounds.y = ontologyPanel.getMousePosition().y;

		// Convert bounds into ontology coordinates
		Rectangle ontoBounds = PaintUtils.convertToOriginalCoordinates(viewBounds, scaleFactor);

		// Update concept bounds
		concept.setBounds(ontoBounds);

		// Put concept in edit mode
		ontologyPanel.selectConcept(concept);
		ontologyPanel.startEditMode();
	}

	/**
	 * Delete the current selected concept (if any)
	 */
	private void deleteConcept() {

		Concept concept = ontologyPanel.getSelectedConcept();

		if (concept != null) {
			ApplicationLogic.removeConcept(ontology, concept);
			ontologyPanel.clearSelection();
		}
	}

	/**
	 * Create a new link
	 * and insert it to the ontology.
	 */
	private void createLink() {		
		ConceptLink tmp = ontologyPanel.getSelectedLink();
		ConceptLink link = ApplicationLogic.createConceptLink(ontology, tmp.getSourceConcept(), tmp.getTargetConcept());
		ontologyPanel.selectLink(link);
		ontologyPanel.selectConcept(null);
	}

	/**
	 * Delete the current selected link (if any)
	 */
	private void deleteLink() {

		ConceptLink link = ontologyPanel.getSelectedLink();

		if (link != null) {
			ApplicationLogic.removeConceptLink(ontology, link.getSourceConcept(), link);
			ontologyPanel.clearSelection();	
		}
	}

	/**
	 * Display a dialog with concept properties
	 */
	private void showConceptDialog() {

		Concept concept = ontologyPanel.getSelectedConcept();

		if (concept != null) {
			MainView mainView = SwingUtils.getFirstParent(this, MainView.class);
			new ConceptPropertiesDialog(mainView, concept).setVisible(true);
		}
	}

	/**
	 * Move randomly all concepts
	 */
	private void shakeConcepts() {

		double scaleFactor = ontologyPanel.getScaledFactor();
		List<Concept> concepts = Collections.synchronizedList(ontology.getConcepts());

		// Randomize all concept location
		for (Concept concept : concepts) {

			// Retrieve adjusted concept coordinates
			Rectangle conceptBounds = PaintUtils.convertToViewCoordinates(concept.getBounds(), scaleFactor);

			// Recompute random position
			double newX = (ontologyPanel.getPreferredSize().width - conceptBounds.width) * Math.random();
			double newY = (ontologyPanel.getPreferredSize().height - conceptBounds.height) * Math.random();

			// Update concept position
			concept.getBounds().x = Double.valueOf(newX).intValue();
			concept.getBounds().y = Double.valueOf(newY).intValue();
		}

		ontologyPanel.repaint();
	}

	/**
	 * Handle button event This method is used internally to respond to button
	 * click.
	 */
	public void actionPerformed(ActionEvent e) {

		ontologyPanel.requestFocus();

		if (e.getSource() == btnSave) {

			try {
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				ApplicationLogic.saveOntology(ontology);
			}
			finally {
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}
		else if (e.getSource() == btnShake) {
			shakeConcepts();
		}
		else if (e.getSource() == btnDynamic) {

			if (btnDynamic.isSelected()) {
				animator.start();
			}
			else {
				animator.stop();
			}
		}
		else {

			// Handle context menu selection
			if (e.getActionCommand().equals("NEW_CONCEPT")) {
				createConcept();
			}
			else if (e.getActionCommand().equals("DELETE_CONCEPT")) {
				deleteConcept();
			}
			else if (e.getActionCommand().equals("NEW_LINK_START")) {
				ontologyPanel.startLinkMode();
			}
			else if (e.getActionCommand().equals("NEW_LINK_END")) {
				createLink();
			}
			else if (e.getActionCommand().equals("DELETE_LINK")) {
				deleteLink();
			}
			else if (e.getActionCommand().equals("CONCEPT_PROPERTIES")) {
				showConceptDialog();
			}
		}
	}
}
