package ch.hesge.csim2.ui.views;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.AncestorEvent;

import ch.hesge.csim2.core.logic.ApplicationLogic;
import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.ConceptLink;
import ch.hesge.csim2.core.model.Ontology;
import ch.hesge.csim2.core.utils.Console;
import ch.hesge.csim2.core.utils.StringUtils;
import ch.hesge.csim2.ui.comp.ConceptPopup;
import ch.hesge.csim2.ui.comp.OntologyPanel;
import ch.hesge.csim2.ui.dialogs.ConceptPropertiesDialog;
import ch.hesge.csim2.ui.utils.Line;
import ch.hesge.csim2.ui.utils.SwingUtils;

import com.alee.utils.swing.AncestorAdapter;

@SuppressWarnings("serial")
public class OntologyView extends JPanel implements Runnable, ActionListener {

	// Private attributes
	private Ontology ontology;
	private Concept currentConcept;

	private ExecutorService dynamicPositionner;
	private boolean isDynamicPosition;
	
	private JScrollPane scrollPanel;
	private JPanel ontologyPanel;
	private JCheckBox btnDynamic;
	private JButton btnShake;
	private JButton btnSave;


	/**
	 * Default constructor
	 */
	public OntologyView(Ontology ontology) {

		this.ontology = ontology;
				
		isDynamicPosition = false;
		
		// Retrieve concepts associated to the ontology
		List<Concept> concepts = ApplicationLogic.getConceptsWithDependencies(ontology);
		ontology.getConcepts().clear();
		ontology.getConcepts().addAll(concepts);

		initComponent();
	}
	
	/**
	 * Initialize the component
	 */
	private void initComponent() {

		setBorder(new TitledBorder(null, ontology.getName(), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setLayout(new BorderLayout(0, 0));

		ontologyPanel = new OntologyPanel();
		scrollPanel = new JScrollPane();
		scrollPanel.setViewportView(ontologyPanel);
		add(scrollPanel, BorderLayout.CENTER);

		JPanel btnPanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) btnPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		add(btnPanel, BorderLayout.SOUTH);

		btnDynamic = new JCheckBox("Dynamic");
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

				if (isDynamicPosition) {
					startDynamicPositionning();
				}
			}

			@Override
			public void ancestorRemoved(AncestorEvent event) {
				if (isDynamicPosition) {
					stopDynamicPositioning();
				}
			}
		});
	}

	/**
	 * Show concept properties dialog
	 */
	public void showConceptProperties() {

//		if (selectedConcept != null) {
//			MainView mainView = SwingUtils.getFirstParent(this, MainView.class);
//			new ConceptPropertiesDialog(mainView, selectedConcept).setVisible(true);
//		}
	}

	/**
	 * Delete the current selected concept (if any)
	 */
	public void deleteCurrentConcept() {

//		// Remove the concept from the ontology
//		ApplicationLogic.removeConcept(ontology, selectedConcept);
//		selectedConcept = null;
//
//		// Clear current state
//		stopDraggingConcept();
//		stopDraggingLink();
//		cancelEditing();
//		ontologyPanel.repaint();
	}

	/**
	 * Delete the current selected link (if any)
	 */
	public void deleteCurrentLink() {

//		ApplicationLogic.removeConceptLink(ontology, selectedLink.getSourceConcept(), selectedLink);
//		selectedLink = null;
//
//		// Clear current state
//		stopDraggingConcept();
//		stopDraggingLink();
//		cancelEditing();
//		ontologyPanel.repaint();
	}


	/**
	 * Move randomly all concepts
	 */
	private void shakeConcepts() {

//		List<Concept> concepts = Collections.synchronizedList(ontology.getConcepts());
//
//		// Randomize all concept location
//		for (Concept concept : concepts) {
//
//			// Retrieve adjusted concept coordinates
//			Rectangle conceptBounds = SwingUtils.convertToViewCoordinates(concept.getBounds(), scaleFactor);
//
//			// Recompute random position
//			double newX = (ontologyPanel.getPreferredSize().width - conceptBounds.width) * Math.random();
//			double newY = (ontologyPanel.getPreferredSize().height - conceptBounds.height) * Math.random();
//
//			// Update concept position
//			concept.getBounds().x = Double.valueOf(newX).intValue();
//			concept.getBounds().y = Double.valueOf(newY).intValue();
//		}
//
//		refreshOntology();
	}

	/**
	 * Start adjusting dynamic concepts position
	 */
	private void startDynamicPositionning() {
		isDynamicPosition = true;
		dynamicPositionner = Executors.newFixedThreadPool(1);
		dynamicPositionner.submit(new Thread(this));
	}

	/**
	 * Stop adjusting dynamic concepts position
	 */
	private void stopDynamicPositioning() {
		isDynamicPosition = false;
		dynamicPositionner.shutdownNow();
	}

	/**
	 * Scan all concepts and recompute their position to each other. This method is used internally in a separate thread.
	 */
	public void run() {

		while (isDynamicPosition) {

			try {

				List<Concept> concepts = Collections.synchronizedList(ontology.getConcepts());

				// Normalize distance between all concepts
				for (Concept sourceConcept : concepts) {
					for (Concept targetConcept : concepts) {

//						if (selectedConcept == targetConcept) {
//							continue;
//						}

						Line linkLine;

						// Retrieve source/target bounds in original coordinates
						Rectangle sourceBounds = sourceConcept.getBounds();
						Rectangle targetBounds = targetConcept.getBounds();

						// Check if concepts are intersecting
						boolean isIntersecting = sourceBounds.intersects(targetBounds);

						// Check if target is linked to source
						boolean isTargetLinkedToSource = false;
						for (ConceptLink link : targetConcept.getLinks()) {
							if (link.getTargetConcept() == sourceConcept) {
								isTargetLinkedToSource = true;
								break;
							}
						}

						// Now we retrieve the line linking the two rectangles
						if (isIntersecting) {
							linkLine = SwingUtils.getDiagonal(sourceBounds, targetBounds);
						}
						else {
							linkLine = SwingUtils.getLine(sourceBounds, targetBounds);
						}

						if (linkLine != null) {

							// Retrieve variation in x and y
							int vx = linkLine.x2 - linkLine.x1;
							int vy = linkLine.y2 - linkLine.y1;

							// Retrieve segment length
							double distance = SwingUtils.getLength(linkLine);

							// If concepts are intersecting, we should separate them
							if (isIntersecting) {

								// Compute distance amplification factor
								double ratio = SwingUtils.getAmplificationRatio(distance, 200d);

								// Calculate required variation in x and y
								targetBounds.x += ratio * vx;
								targetBounds.y += ratio * vy;
							}

							// If concepts are too close to each other, we should separate them
							else if (distance < 50d) {

								// Compute distance amplification factor
								double ratio = SwingUtils.getAmplificationRatio(distance, 50d);

								// Calculate required variation in x and y
								targetBounds.x += ratio * vx;
								targetBounds.y += ratio * vy;
							}

							// If concepts linked to each other, a minimal distance is required
							else if (isTargetLinkedToSource && distance > 50d) {

								// Compute distance compression factor
								double ratio = SwingUtils.getReductionRatio(distance, 50d);

								// Calculate required variation in x and y
								targetBounds.x -= ratio * vx;
								targetBounds.y -= ratio * vy;
							}
						}
					}
				}

				ontologyPanel.repaint();
				Thread.sleep(100);
			}
			catch (InterruptedException e) {
				// Skip interruption
				break;
			}
			catch (Exception e) {
				Console.writeError(StringUtils.toString(e));
			}
		}
	}

	/**
	 * Handle button event This method is used internally to respond to button click.
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
				startDynamicPositionning();
			}
			else {
				stopDynamicPositioning();
			}
		}

	}

}
