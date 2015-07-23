package ch.hesge.csim2.ui.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.ConceptLink;
import ch.hesge.csim2.core.model.Ontology;
import ch.hesge.csim2.ui.dialogs.PropertiesDialog;
import ch.hesge.csim2.ui.model.ApplicationManager;
import ch.hesge.csim2.ui.utils.PaintUtils;
import ch.hesge.csim2.ui.utils.SimpleAction;
import ch.hesge.csim2.ui.utils.SwingUtils;

@SuppressWarnings("serial")
public class OntologyView extends JPanel implements ActionListener {

	// Private attributes
	private Ontology ontology;
	private ApplicationManager appManager;
	private OntologyPanel ontologyPanel;
	private OntologyAnimator animator;
	private JCheckBox btnDynamic;
	private JButton btnShake;
	private JButton btnExport;
	private JButton btnImport;
	private JButton btnSave;

	/**
	 * Default constructor
	 */
	public OntologyView(Ontology ontology) {

		this.ontology = ontology;
		this.appManager = ApplicationManager.UNIQUE_INSTANCE;

		// Load ontology concepts
		List<Concept> concepts = appManager.getConcepts(ontology);
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
		ontologyPanel = new OntologyPanel(ontology, this, scrollPanel);
		scrollPanel.setViewportView(ontologyPanel);
		add(scrollPanel, BorderLayout.CENTER);

		animator = new OntologyAnimator(ontologyPanel, ontology);
		
		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new BorderLayout(0, 0));
		add(btnPanel, BorderLayout.SOUTH);

		JPanel rightPanel = new JPanel();
		FlowLayout fl_rightPanel = (FlowLayout) rightPanel.getLayout();
		fl_rightPanel.setAlignment(FlowLayout.RIGHT);
		btnPanel.add(rightPanel, BorderLayout.EAST);

		JPanel leftPanel = new JPanel();
		FlowLayout fl_leftPanel = (FlowLayout) leftPanel.getLayout();
		fl_leftPanel.setAlignment(FlowLayout.LEFT);
		btnPanel.add(leftPanel, BorderLayout.WEST);

		btnExport = new JButton("Export");
		btnExport.setPreferredSize(new Dimension(80, 25));
		btnExport.addActionListener(this);
		leftPanel.add(btnExport);

		btnImport = new JButton("Import");
		btnImport.setPreferredSize(new Dimension(80, 25));
		btnImport.addActionListener(this);
		leftPanel.add(btnImport);

		btnSave = new JButton("Save");
		btnSave.setPreferredSize(new Dimension(80, 25));
		btnSave.addActionListener(this);
		leftPanel.add(btnSave);

		btnDynamic = new JCheckBox("Dynamic");
		btnDynamic.setPreferredSize(new Dimension(80, 25));
		btnDynamic.addActionListener(this);
		rightPanel.add(btnDynamic);

		btnShake = new JButton("Shake");
		btnShake.setPreferredSize(new Dimension(80, 25));
		btnShake.addActionListener(this);
		rightPanel.add(btnShake);

		initListeners();
	}

	/**
	 * Initialize component inner listeners
	 */
	private void initListeners() {

		// Initialize the view when visible
		SwingUtils.onComponentVisible(this, new SimpleAction<Object>() {
			@Override
			public void run(Object o) {
				ontologyPanel.requestFocus();
				animator.resume();
			}
		});

		// Initialize the view when hidden
		SwingUtils.onComponentVisible(this, new SimpleAction<Object>() {
			@Override
			public void run(Object o) {
				animator.suspend();
			}
		});
	}

	/**
	 * Create a new concept and insert it to the ontology.
	 */
	public void createConcept(Point location) {

		// Create a new concept
		Concept concept = appManager.createConcept(ontology);

		double scaleFactor = ontologyPanel.getScaledFactor();
		Point ontoPoint = PaintUtils.toOriginalCoordinates(location, scaleFactor);
		concept.getBounds().x = ontoPoint.x;
		concept.getBounds().y = ontoPoint.y;
				
		// Put concept in edit mode
		ontologyPanel.startConceptEdition(concept);
	}

	/**
	 * Start a new link from concept passed in argument
	 */
	public void startLinkFrom(Concept concept) {
		ontologyPanel.startLinkFrom(concept);		
	}
	
	/**
	 * Create a link between two concept.
	 */
	public void createLink(Concept sourceConcept, Concept targetConcept) {
		
		ConceptLink link = appManager.createConceptLink(ontology, sourceConcept, targetConcept);
		ontologyPanel.selectConcept(null);
		ontologyPanel.selectLink(link);
	}
	
	/**
	 * Delete the concept passed in argument
	 */
	public void deleteConcept(Concept concept) {
		
		appManager.removeConcept(ontology, concept);
		ontologyPanel.clearSelection();
	}

	/**
	 * Delete the link passed in argument
	 */
	public void deleteLink(ConceptLink link) {
		
		appManager.removeConceptLink(ontology, link.getSourceConcept(), link);
		ontologyPanel.clearSelection();	
	}

	/**
	 * Display a dialog with concept properties
	 */
	public void showProperties(Concept concept) {
		
		// Display dialog
		MainView mainView = (MainView) SwingUtilities.getAncestorOfClass(MainView.class, this);
		PropertiesDialog dialog = new PropertiesDialog(mainView, concept);
		
		dialog.setVisible(true);

		// Save modifications
		if (dialog.getDialogResult()) {

			// Copy modified properties to current concept
			Concept source = dialog.getModifiedConcept();
			appManager.copyConceptProperties(source,  concept);
			ontologyPanel.selectConcept(concept);
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
			Rectangle conceptBounds = PaintUtils.toViewCoordinates(concept.getBounds(), scaleFactor);

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

		if (e.getSource() == btnExport) {
			appManager.exportOntology(ontology);
		}
		else if (e.getSource() == btnImport) {
			appManager.importOntology(ontology);
		}
		else if (e.getSource() == btnSave) {
			appManager.saveOntology(ontology);
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
		
		ontologyPanel.requestFocus();
	}
}
