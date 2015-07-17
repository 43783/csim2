package ch.hesge.csim2.ui.views;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.ConceptLink;
import ch.hesge.csim2.core.model.Ontology;
import ch.hesge.csim2.ui.popup.ConceptPopup;
import ch.hesge.csim2.ui.utils.Line;
import ch.hesge.csim2.ui.utils.PaintUtils;
import ch.hesge.csim2.ui.utils.SimpleAction;
import ch.hesge.csim2.ui.utils.SwingUtils;

@SuppressWarnings("serial")
public class OntologyPanel extends JPanel {

	// Private attributes
	private Ontology ontology;
	private OntologyView view;
	private Concept selectedConcept;
	private ConceptLink selectedLink;

	private Concept linkSource;
	private Concept linkTarget;

	private boolean isEditing;
	private boolean isMovingLink;
	private boolean isMovingConcept;

	private double scaleFactor;
	private double minScaleFactor;
	private JScrollPane scrollPanel;
	private Point mousePosition;
	private JTextField fieldEditor;

	// Private internal constants
	private static final int FONT_SIZE = 10;
	private static final int BORDER_SIZE = 20;
	private static final int SELECTION_HANDLE_SIZE = 4;

	private static final double ZOOM_FACTOR_INCREMENT = 0.05;

	private static final Color CONCEPT_TEXT = Color.BLACK;
	private static final Color CONCEPT_COLOR = Color.GRAY;
	private static final Color CONCEPT_BACKGROUND = new Color(251, 247, 180);
	private static final Color LINK_TEXT = Color.BLACK;
	private static final Color LINK_COLOR = Color.GRAY;
	private static final Color SELECTION_BACKGROUND = Color.WHITE;
	private static final Color SELECTION_COLOR = Color.BLACK;

	/**
	 * Default constructor
	 */
	public OntologyPanel(Ontology ontology, OntologyView view, JScrollPane scrollPanel) {

		this.ontology = ontology;
		this.view = view;
		this.scrollPanel = scrollPanel;
		this.scaleFactor = 1d;
		this.minScaleFactor = 0d;

		initComponent();
	}

	/**
	 * Initialize the component
	 */
	private void initComponent() {

		setOpaque(true);
		setLayout(null);
		setBackground(Color.WHITE);

		initListeners();
	}

	/**
	 * Initialize listeners
	 */
	private void initListeners() {

		// Set focus when visible
		SwingUtils.onComponentVisible(this, new SimpleAction<Object>() {
			@Override
			public void run(Object o) {
				computePreferredSize();
				computeMinScaleFactor();
				OntologyPanel.this.requestFocus();
			}
		});

		// Set panel resize
		SwingUtils.onComponentResized(this, new SimpleAction<ComponentEvent>() {
			@Override
			public void run(ComponentEvent e) {
			}
		});

		// Listen to mouse pressed
		SwingUtils.onMousePressed(this, new SimpleAction<MouseEvent>() {
			@Override
			public void run(MouseEvent e) {
				onMousePressed(e);
			}
		});

		// Listen to mouse released
		SwingUtils.onMouseReleased(this, new SimpleAction<MouseEvent>() {
			@Override
			public void run(MouseEvent e) {
				onMouseReleased(e);
			}
		});

		// Listen to mouse moved
		SwingUtils.onMouseMoved(this, new SimpleAction<MouseEvent>() {
			@Override
			public void run(MouseEvent e) {
				onMouseMoved(e);
			}
		});

		// Listen to mouse moved
		SwingUtils.onMouseDragged(this, new SimpleAction<MouseEvent>() {
			@Override
			public void run(MouseEvent e) {
				onMouseDragged(e);
			}
		});

		// Listen to mouse moved
		SwingUtils.onMouseWheeled(this, new SimpleAction<MouseWheelEvent>() {
			@Override
			public void run(MouseWheelEvent e) {
				onMouseWheelMoved(e);
			}
		});

		// Listen to panel key pressed (CTRL-0 + DELETE)
		SwingUtils.onKeyPressed(this, new SimpleAction<KeyEvent>() {
			@Override
			public void run(KeyEvent e) {
				onKeyPressed(e);
			}
		});
	}

	/**
	 * Handle mouse button clicked.
	 * 
	 * @param e
	 */
	private void onMousePressed(MouseEvent e) {

		requestFocus();
		mousePosition = getMousePosition();

		// Retrieve mouse position in ontology coordinates
		Point selectedPoint = PaintUtils.toOriginalCoordinates(mousePosition, scaleFactor);

		// Detect if user has clicked on a concept
		selectedConcept = PaintUtils.hitConcept(ontology.getConcepts(), selectedPoint);

		// Detect if user has clicked on a link
		selectedLink = PaintUtils.hitLink(ontology.getConcepts(), selectedPoint, scaleFactor * 4);

		// Detect if we are edit mode
		if (isEditing) {
			stopConceptEdition();
			stopLinkEdition();
		}

		// Detect if a building link
		else if (isMovingLink) {

			// A link is fully established
			if (linkSource != null && linkTarget != null) {
				view.createLink(linkSource, linkTarget);
			}

			// Reset link building
			isMovingLink = false;
			linkSource = null;
			linkTarget = null;
		}

		// Detect if use made a left click
		else if (SwingUtilities.isLeftMouseButton(e)) {

			// Single click
			if (e.getClickCount() == 1) {

				stopConceptEdition();
				stopLinkEdition();

				if (selectedConcept != null) {
					isMovingConcept = true;
					selectedLink = null;
				}
				else if (selectedLink != null) {
					isMovingConcept = false;
					selectedConcept = null;
				}
			}

			// Double click
			else if (e.getClickCount() == 2) {

				if (selectedConcept != null) {
					startConceptEdition(selectedConcept);
				}
				else if (selectedLink != null) {
					startLinkEdition(selectedLink);
				}
			}
		}

		// Detect if user made a right click
		else if (SwingUtilities.isRightMouseButton(e)) {

			// Create a context menu
			ConceptPopup contextMenu = new ConceptPopup(view);

			// Attach current object
			if (selectedConcept != null) {
				contextMenu.setConcept(selectedConcept);
			}
			else if (selectedLink != null) {
				contextMenu.setLink(selectedLink);
			}
			else {
				contextMenu.setConcept(null);
				contextMenu.setLink(null);
			}

			// Show the menu
			contextMenu.setPopupLocation(e.getPoint());
			contextMenu.show(e.getComponent(), e.getX(), e.getY());
		}

		repaint();
	}

	/**
	 * Handle mouse button released.
	 * 
	 * @param e
	 */
	private void onMouseReleased(MouseEvent e) {

		// Stop dragging concept or link
		isMovingConcept = false;
		isMovingLink = false;

		repaint();
	}

	/**
	 * Handle mouse motion while left button is still pressed.
	 * Typically, this method is called when a concept is clicked and moved.
	 * 
	 * @param e
	 */
	private void onMouseDragged(MouseEvent e) {

		if (isMovingConcept) {

			// Retrieve mouse position in ontology coordinates
			Point currentPoint = PaintUtils.toOriginalCoordinates(e.getPoint(), scaleFactor);
			Point previousPoint = PaintUtils.toOriginalCoordinates(mousePosition, scaleFactor);

			// Adjust concept position within ontology
			selectedConcept.getBounds().translate(currentPoint.x - previousPoint.x, currentPoint.y - previousPoint.y);

			// Refresh new mouse position
			mousePosition = e.getPoint();
			repaint();
		}
	}

	/**
	 * Handle mouse motion while no button is currently pressed.
	 * Typically, this method is called when mouse is moving after a
	 * startLinkMode has been called.
	 * 
	 * @param a
	 */
	private void onMouseMoved(MouseEvent e) {

		if (isMovingLink) {

			linkTarget = null;

			// Retrieve mouse position in ontology coordinates
			Point currentPoint = PaintUtils.toOriginalCoordinates(e.getPoint(), scaleFactor);

			// Retrieve concept under mouse point
			Concept hitConcept = PaintUtils.hitConcept(ontology.getConcepts(), currentPoint);

			// Check if a link is established
			if (hitConcept != null) {

				boolean isAlreadyLinked = false;

				// Check if linkSource is already linkded to hitConcept
				for (ConceptLink link : linkSource.getLinks()) {
					if (link.getTargetConcept() == hitConcept) {
						isAlreadyLinked = true;
						break;
					}
				}

				// Check if hitConcept is already linked to linkSource
				for (ConceptLink link : hitConcept.getLinks()) {
					if (link.getTargetConcept() == linkSource) {
						isAlreadyLinked = true;
						break;
					}
				}

				// Mark hitConcept as elligible only if no previous link exist already
				if (!isAlreadyLinked && hitConcept != linkSource) {
					linkTarget = hitConcept;
				}
			}

			// Refresh new mouse position
			mousePosition = e.getPoint();
			repaint();
		}
	}

	/**
	 * Handle mouse wheel motion.
	 * 
	 * @param e
	 */
	private void onMouseWheelMoved(MouseWheelEvent e) {

		// Ctrl + wheel => resize ontology
		if (e.isControlDown()) {
			scaleFactor = Math.max(minScaleFactor, scaleFactor - ZOOM_FACTOR_INCREMENT * e.getWheelRotation());
			computePreferredSize();
		}

		// Schift + wheel => horizontal scroll
		if (e.isShiftDown()) {
			JScrollBar scrollBar = scrollPanel.getHorizontalScrollBar();
			scrollBar.setValue(scrollBar.getValue() + scrollBar.getUnitIncrement() * e.getWheelRotation());
			scrollPanel.revalidate();
		}

		// Wheel => vertical scroll
		else {
			JScrollBar scrollBar = scrollPanel.getVerticalScrollBar();
			scrollBar.setValue(scrollBar.getValue() + scrollBar.getUnitIncrement() * e.getWheelRotation());
			scrollPanel.revalidate();
		}
	}

	/**
	 * Handle CTRL key within current view
	 * 
	 * @param e
	 */
	private void onKeyPressed(KeyEvent e) {

		if (e.isControlDown()) {

			// Ctrl+0 => resize optimally
			if (e.getKeyCode() == KeyEvent.VK_0 || e.getKeyCode() == KeyEvent.VK_NUMPAD0) {
				scaleFactor = minScaleFactor;
				computePreferredSize();
			}
		}

		// Del key => delete selection
		else if (e.getKeyCode() == KeyEvent.VK_DELETE) {

			if (selectedConcept != null) {
				view.deleteConcept(selectedConcept);
			}
			else if (selectedLink != null) {
				view.deleteLink(selectedLink);
			}
		}
	}

	/**
	 * Recompute current ontology size
	 */
	private void computePreferredSize() {

		Rectangle ontoRect = new Rectangle();

		// Recompute size with new scaleFactor
		for (Concept concept : ontology.getConcepts()) {
			Rectangle rect = PaintUtils.toViewCoordinates(concept.getBounds(), scaleFactor);
			ontoRect = ontoRect.union(rect);
		}
		ontoRect.width += BORDER_SIZE;
		ontoRect.height += BORDER_SIZE;

		setPreferredSize(ontoRect.getSize());

		scrollPanel.getHorizontalScrollBar().setUnitIncrement(ontoRect.width / 20);
		scrollPanel.getVerticalScrollBar().setUnitIncrement(ontoRect.height / 20);

		scrollPanel.revalidate();
		repaint();
	}

	/**
	 * Recompute minimum scaling factor to respect
	 */
	private void computeMinScaleFactor() {

		// Get the visible drawing area
		Rectangle drawRect = getVisibleRect();

		// Calculate ontology bounds (normalized with scaleFactor to 1)
		Rectangle ontoRect = new Rectangle();
		for (Concept concept : ontology.getConcepts()) {
			Rectangle rect = PaintUtils.toViewCoordinates(concept.getBounds(), 1d);
			ontoRect = ontoRect.union(rect);
		}
		ontoRect.width += BORDER_SIZE;
		ontoRect.height += BORDER_SIZE;

		// Don't go in to small ontology area, in case their is only one concept
		ontoRect.width = Math.max(600, ontoRect.width);
		ontoRect.height = Math.max(480, ontoRect.height);

		// Check horizontal/vertical factor
		double horizontalFactor = 1d * drawRect.width / ontoRect.width;
		double verticalFactor = 1d * drawRect.height / ontoRect.height;

		// Calculate minimal factor to display full ontology
		minScaleFactor = Math.min(horizontalFactor, verticalFactor);

		// Check if current scaleFactor is too small
		if (scaleFactor < minScaleFactor) {
			scaleFactor = minScaleFactor;
			computePreferredSize();
		}
	}

	/**
	 * Retrieve the current selected concept.
	 * 
	 * @return
	 *         the selected concept or null
	 */
	public Concept getSelectedConcept() {
		return this.selectedConcept;
	}

	/**
	 * Retrieve current scale factor.
	 * 
	 * @return
	 *         the scale factor between 0.0 and 1.0
	 */
	public double getScaledFactor() {
		return this.scaleFactor;
	}

	/**
	 * Repaint the ontology and all its concepts
	 */
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);

		// Graphics initialization
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Sets font size
		Font scaledFont = getFont().deriveFont((float) Math.round(FONT_SIZE * scaleFactor));
		g.setFont(scaledFont);

		if (ontology != null) {

			// Draw all links
			for (Concept concept : ontology.getConcepts()) {
				for (ConceptLink link : concept.getLinks()) {
					paintLink(g, link);
				}
			}

			// Draw all concepts
			for (Concept concept : ontology.getConcepts()) {
				paintConcept(g, concept);
			}

			// Draw dragging link
			if (isMovingLink) {
				paintBuildingLink(g);
			}

			// Draw selected link
			else if (selectedLink != null) {
				paintSelectedLink(g);
			}
		}

		/*
		// Draw bounds around ontology area
		g.setColor(Color.RED);
		Rectangle a = new Rectangle(getPreferredSize());
		a.width  -= 1;
		a.height -= 1;
		g.drawRect(a.x, a.y, a.width, a.height);
		*/

		/*
		// Draw bounds around visible area
		g.setColor(Color.RED);
		Rectangle b = getVisibleRect();
		b.width  -= 1;
		b.height -= 1;
		g.drawRect(b.x, b.y, b.width, b.height);
		*/
	}

	/**
	 * Paint a concept.
	 * If selected, also draw little squares at each corner.
	 */
	private void paintConcept(Graphics g, Concept concept) {

		// Retrieve concept bounds in view coordinates
		Rectangle bounds = PaintUtils.toViewCoordinates(concept.getBounds(), scaleFactor);

		// Retrieve centered text bounds
		Rectangle textBounds = PaintUtils.getCenteredBounds(g, bounds, concept.getName());

		// Draw concept background
		g.setColor(CONCEPT_BACKGROUND);
		g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

		// Draw concept border
		g.setColor(CONCEPT_COLOR);
		g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);

		// Draw centered text
		g.setColor(CONCEPT_TEXT);
		g.drawString(concept.getName(), textBounds.x, textBounds.y + textBounds.height);

		// Draw selected concept
		if (concept == selectedConcept) {

			// Compute white square handle size
			int handleSize = (int) Math.round(SELECTION_HANDLE_SIZE * scaleFactor);

			// Draw top left handler
			Rectangle topLeft = new Rectangle(bounds.x - handleSize / 2, bounds.y - handleSize / 2, handleSize, handleSize);
			g.setColor(SELECTION_BACKGROUND);
			g.fillRect(topLeft.x, topLeft.y, topLeft.width, topLeft.height);
			g.setColor(SELECTION_COLOR);
			g.drawRect(topLeft.x, topLeft.y, topLeft.width, topLeft.height);

			// Draw top right handler
			Rectangle topRight = new Rectangle(bounds.x + bounds.width - handleSize / 2, bounds.y - handleSize / 2, handleSize, handleSize);
			g.setColor(SELECTION_BACKGROUND);
			g.fillRect(topRight.x, topRight.y, topRight.width, topRight.height);
			g.setColor(SELECTION_COLOR);
			g.drawRect(topRight.x, topRight.y, topRight.width, topRight.height);

			// Draw bottom left handler
			Rectangle bottomLeft = new Rectangle(bounds.x - handleSize / 2, bounds.y + bounds.height - handleSize / 2, handleSize, handleSize);
			g.setColor(SELECTION_BACKGROUND);
			g.fillRect(bottomLeft.x, bottomLeft.y, bottomLeft.width, bottomLeft.height);
			g.setColor(SELECTION_COLOR);
			g.drawRect(bottomLeft.x, bottomLeft.y, bottomLeft.width, bottomLeft.height);

			// Draw bottom right handler
			Rectangle bottomRight = new Rectangle(bounds.x + bounds.width - handleSize / 2, bounds.y + bounds.height - handleSize / 2, handleSize, handleSize);
			g.setColor(SELECTION_BACKGROUND);
			g.fillRect(bottomRight.x, bottomRight.y, bottomRight.width, bottomRight.height);
			g.setColor(SELECTION_COLOR);
			g.drawRect(bottomRight.x, bottomRight.y, bottomRight.width, bottomRight.height);
		}
	}

	/**
	 * Paint a link between two concepts.
	 * If selected, also draw little circles at each ends.
	 */
	private void paintLink(Graphics g, ConceptLink link) {

		// Retrieve source/target bounds in view coordinates
		Rectangle sourceBounds = PaintUtils.toViewCoordinates(link.getSourceConcept().getBounds(), scaleFactor);
		Rectangle targetBounds = PaintUtils.toViewCoordinates(link.getTargetConcept().getBounds(), scaleFactor);

		// Retrieve line between source/target concepts
		Line linkLine = PaintUtils.getLine(sourceBounds, targetBounds);

		if (linkLine != null) {

			// Draw line
			g.setColor(LINK_COLOR);
			g.drawLine(linkLine.x1, linkLine.y1, linkLine.x2, linkLine.y2);

			// Draw arrow
			int arrowSize = (int) Math.round(5 * scaleFactor);
			PaintUtils.drawArrowAtEnd(g, linkLine, arrowSize);

			// Draw link qualifier
			if (link.getQualifier() != null) {

				// Retrieve link text bounds
				Rectangle linkBounds = new Rectangle(linkLine.x1, linkLine.y1, linkLine.width, linkLine.height);
				Rectangle textBounds = PaintUtils.getCenteredBounds(g, linkBounds, link.getQualifier());

				// Draw link name
				g.setColor(LINK_TEXT);
				g.drawString(link.getQualifier(), textBounds.x, textBounds.y);
			}
		}
	}

	/**
	 * Paint the selected link with little circle at each ends.
	 */
	private void paintSelectedLink(Graphics g) {

		// Retrieve source/target bounds in view coordinates
		Rectangle sourceBounds = PaintUtils.toViewCoordinates(selectedLink.getSourceConcept().getBounds(), scaleFactor);
		Rectangle targetBounds = PaintUtils.toViewCoordinates(selectedLink.getTargetConcept().getBounds(), scaleFactor);

		// Retrieve line between source/target concepts
		Line linkLine = PaintUtils.getLine(sourceBounds, targetBounds);

		if (linkLine != null) {

			// Calculate circle size
			int handleSize = (int) Math.round(SELECTION_HANDLE_SIZE * scaleFactor);

			// Draw link start point
			Point startPoint = linkLine.getSourcePoint();
			Rectangle startBounds = new Rectangle(startPoint.x - handleSize / 2, startPoint.y - handleSize / 2, handleSize, handleSize);
			g.setColor(SELECTION_BACKGROUND);
			g.fillOval(startBounds.x, startBounds.y, startBounds.width, startBounds.height);
			g.setColor(SELECTION_COLOR);
			g.drawOval(startBounds.x, startBounds.y, startBounds.width, startBounds.height);

			// Draw link end point
			Point endPoint = linkLine.getTargetPoint();
			Rectangle endBounds = new Rectangle(endPoint.x - handleSize / 2, endPoint.y - handleSize / 2, handleSize, handleSize);
			g.setColor(SELECTION_BACKGROUND);
			g.fillOval(endBounds.x, endBounds.y, endBounds.width, endBounds.height);
			g.setColor(SELECTION_COLOR);
			g.drawOval(endBounds.x, endBounds.y, endBounds.width, endBounds.height);
		}
	}

	/**
	 * Paint a link currently being dragged
	 */
	private void paintBuildingLink(Graphics g) {

		// Retrieve source/target bounds in view coordinates
		Rectangle sourceBounds = PaintUtils.toViewCoordinates(linkSource.getBounds(), scaleFactor);
		Rectangle targetBounds = new Rectangle(mousePosition.x - 1, mousePosition.y - 1, 2, 2);

		// Retrieve line between source concepts and mouse position
		Line linkLine = PaintUtils.getLine(sourceBounds, targetBounds);

		// Recalculate line to target, if target available
		if (linkTarget != null) {
			targetBounds = PaintUtils.toViewCoordinates(linkTarget.getBounds(), scaleFactor);
			linkLine = PaintUtils.getLine(sourceBounds, targetBounds);
		}

		if (linkLine != null) {

			g.setColor(SELECTION_COLOR);
			((Graphics2D) g).setStroke(new BasicStroke(2));

			// Draw link line
			g.drawLine(linkLine.x1, linkLine.y1, linkLine.x2, linkLine.y2);

			// Draw link start point
			Point startPoint = linkLine.getSourcePoint();
			int handleSize = (int) Math.round(SELECTION_HANDLE_SIZE * scaleFactor);
			Rectangle startBounds = new Rectangle(startPoint.x - handleSize / 2, startPoint.y - handleSize / 2, handleSize, handleSize);
			g.fillOval(startBounds.x, startBounds.y, startBounds.width, startBounds.height);

			// Draw link target
			if (linkTarget != null) {
				g.drawRect(targetBounds.x, targetBounds.y, targetBounds.width, targetBounds.height);
			}
		}
	}

	/**
	 * Set the name of a specific concept.
	 * 
	 * @param concept
	 * @param name
	 */
	private void setConceptName(Concept concept, String name) {

		Graphics g = getGraphics();
		String conceptName = "Concept";

		// Sets font size
		Font scaledFont = getFont().deriveFont((float) Math.round(FONT_SIZE * scaleFactor));
		g.setFont(scaledFont);

		// Clean concept name
		if (name != null && name.trim().length() > 0) {
			conceptName = name.trim();
		}

		// Retrieve concepts bounds in view coordinates
		Rectangle bounds = PaintUtils.toViewCoordinates(selectedConcept.getBounds(), scaleFactor);

		// Calculate new concept bounds
		Rectangle viewBounds = PaintUtils.getCenteredBounds(g, new Rectangle(bounds), conceptName);
		viewBounds.grow(g.getFont().getSize(), g.getFont().getSize());
		viewBounds.x = bounds.x;
		viewBounds.y = bounds.y;

		// Convert bounds into ontology coordinates
		bounds = PaintUtils.toOriginalCoordinates(viewBounds, scaleFactor);

		// Update concept size and name
		concept.setBounds(bounds);
		concept.setName(name);
	}

	/**
	 * Set the name of a specific link.
	 * 
	 * @param link
	 * @param name
	 */
	private void setLinkName(ConceptLink link, String name) {

		String linkName = null;

		// Clean concept name
		if (name != null && name.trim().length() > 0) {
			linkName = name.trim();
		}

		// Update link name
		link.setQualifier(linkName);
	}

	/**
	 * Programmatically select a concept.
	 * 
	 * @param concept
	 *        the concept to select
	 */
	public void selectConcept(Concept concept) {
		selectedConcept = concept;
		repaint();
	}

	/**
	 * Programmatically select a link.
	 * 
	 * @param link
	 *        the link to select
	 */
	public void selectLink(ConceptLink link) {
		selectedLink = link;
		repaint();
	}

	/**
	 * Start editing a concept name.
	 */
	public void startConceptEdition(Concept concept) {

		isEditing = true;
		selectedConcept = concept;

		Graphics g = getGraphics();

		// Sets font size
		Font scaledFont = getFont().deriveFont((float) Math.round(FONT_SIZE * scaleFactor));
		g.setFont(scaledFont);

		// Retrieve current concept bounds in view coordinates
		Rectangle editorBounds = PaintUtils.toViewCoordinates(selectedConcept.getBounds(), scaleFactor);

		// Create a qualifier editor
		createFieldEditor(g, editorBounds, selectedConcept.getName());
	}

	/**
	 * Stop editing a concept name.
	 */
	private void stopConceptEdition() {
		isEditing = false;
		deleteFieldEditor();
		repaint();
		requestFocus();
	}

	/**
	 * Start editing a link name.
	 */
	private void startLinkEdition(ConceptLink link) {

		isEditing = true;
		selectedLink = link;

		Graphics g = getGraphics();

		// Sets font size
		Font scaledFont = getFont().deriveFont((float) Math.round(FONT_SIZE * scaleFactor));
		g.setFont(scaledFont);

		// Retrieve source/target bounds in view coordinates
		Rectangle sourceBounds = PaintUtils.toViewCoordinates(selectedLink.getSourceConcept().getBounds(), scaleFactor);
		Rectangle targetBounds = PaintUtils.toViewCoordinates(selectedLink.getTargetConcept().getBounds(), scaleFactor);

		// Retrieve line between source/target concepts
		Line linkLine = PaintUtils.getLine(sourceBounds, targetBounds);

		if (linkLine != null) {

			// Retrieve link text size
			String linkText = selectedLink.getQualifier() == null ? "XXXXX" : selectedLink.getQualifier();

			// Calculate text bounds
			Rectangle linkBounds = new Rectangle(linkLine.x1, linkLine.y1, linkLine.width, linkLine.height);
			Rectangle editorBounds = PaintUtils.getCenteredBounds(g, linkBounds, linkText);
			editorBounds.grow(g.getFont().getSize() / 2, g.getFont().getSize());

			// Create a qualifier editor
			createFieldEditor(g, editorBounds, selectedLink.getQualifier());
		}
	}

	/**
	 * Stop editing a link name.
	 */
	private void stopLinkEdition() {
		isEditing = false;
		deleteFieldEditor();
		repaint();
		requestFocus();
	}

	/**
	 * Put view in link mode.
	 * That is build interactively a link.
	 */
	public void startLinkFrom(Concept sourceConcept) {

		selectedConcept = sourceConcept;
		linkSource = sourceConcept;
		linkTarget = null;
		isMovingLink = true;

		repaint();
	}

	/**
	 * Create a field editor (JTextField) to edit concept/link name.
	 * 
	 * @param bounds
	 *        the bounds where to place the editor
	 * @param name
	 *        the name to edit
	 */
	private void createFieldEditor(Graphics g, Rectangle bounds, String name) {

		fieldEditor = new JTextField();
		fieldEditor.setFont(g.getFont());
		fieldEditor.setText(name);
		fieldEditor.setBounds(bounds);
		fieldEditor.selectAll();
		add(fieldEditor);

		// Put focus on the field
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				fieldEditor.requestFocus();
			}
		});

		// Listen to editor field key pressed (ENTER + ESCAPE)
		SwingUtils.onKeyPressed(fieldEditor, new SimpleAction<KeyEvent>() {
			@Override
			public void run(KeyEvent e) {

				if (e.getKeyCode() == KeyEvent.VK_ENTER) {

					if (selectedConcept != null) {
						setConceptName(selectedConcept, fieldEditor.getText());
						stopConceptEdition();
					}
					else if (selectedLink != null) {
						setLinkName(selectedLink, fieldEditor.getText());
						stopLinkEdition();
					}
				}
				else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {

					if (selectedConcept != null) {
						stopConceptEdition();
					}
					else if (selectedLink != null) {
						stopLinkEdition();
					}
				}
			}
		});

		// Listen to editor lost focus
		SwingUtils.onFocusLost(fieldEditor, new SimpleAction<FocusEvent>() {
			@Override
			public void run(FocusEvent e) {
				stopConceptEdition();
				stopLinkEdition();
			}
		});
	}

	/**
	 * Delete the current field editor.
	 */
	private void deleteFieldEditor() {
		if (fieldEditor != null) {
			remove(fieldEditor);
			fieldEditor = null;
		}
	}

	/**
	 * Reset current selection.
	 */
	public void clearSelection() {

		selectedConcept = null;
		selectedLink = null;
		isMovingConcept = false;
		isMovingLink = false;

		stopConceptEdition();
		stopLinkEdition();
	}
}
