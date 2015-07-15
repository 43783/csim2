package ch.hesge.csim2.ui.comp;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.ConceptLink;
import ch.hesge.csim2.core.model.Ontology;
import ch.hesge.csim2.ui.utils.Line;
import ch.hesge.csim2.ui.utils.PaintUtils;
import ch.hesge.csim2.ui.utils.SwingUtils;
import ch.hesge.csim2.ui.views.OntologyView;

@SuppressWarnings("serial")
public class OntologyPanel extends JPanel implements ActionListener {

	// Private attributes
	private Ontology ontology;
	private OntologyView view;
	private Concept selectedConcept;
	private ConceptLink selectedLink;

	private Concept linkSource;
	private Concept linkTarget;

	private boolean isEditing;
	private boolean isBuildingLink;
	private boolean isMovingConcept;

	private double scaleFactor;
	private double minScaleFactor;
	private JScrollPane scrollPanel;
	private Point mousePosition;
	private ConceptPopup contextMenu;
	private JTextField editorField;
	private ActionListener actionListener;

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

		this.ontology       = ontology;
		this.view           = view;
		this.scrollPanel    = scrollPanel;
		this.scaleFactor    = 1d;
		this.minScaleFactor = 0d;

		initComponent();
	}

	/**
	 * Initialize the component
	 */
	private void initComponent() {

		setOpaque(true);
		setBackground(Color.WHITE);

		// Create a context menu
		contextMenu = new ConceptPopup(view);

		// Create the concept/link name editor
		editorField = new JTextField();
		editorField.setVisible(false);
		add(editorField);
		
		initListeners();
	}

	/**
	 * Initialize listeners
	 */
	private void initListeners() {

		// Set focus when visible
		SwingUtils.setFocusWhenVisible(this);
		
		// Listen to panel resize
		scrollPanel.addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {
			}
			@Override
			public void componentResized(ComponentEvent e) {
				computeMinScaleFactor();
				scaleFactor = Math.max(minScaleFactor,  scaleFactor);
				scrollPanel.revalidate();
				repaint();
			}
			@Override
			public void componentMoved(ComponentEvent e) {
			}
			@Override
			public void componentHidden(ComponentEvent e) {
			}
		});
		 
		// Listen to mouse click
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				onMousePressed(e);
			}

			public void mouseReleased(MouseEvent e) {
				onMouseReleased(e);
			}
		});

		// Listen to mouse motion
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				onMouseMoved(e);
			}

			public void mouseDragged(MouseEvent e) {
				onMouseDragged(e);
			}
		});

		// Listen to mouse wheel motion
		addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				onMouseWheelMoved(e);
			}
		});

		// Listen to key typed (CTRL-0 + DELETE)
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				onPanelKeyPressed(e);
			}
		});

		// Listen to editor field keys (ENTER + ESCAPE)
		editorField.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				onEditorKeyPressed(e);
			}
		});

		// Listen to editor focus
		editorField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				onFocusLost(e);
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
		Point selectedPoint = PaintUtils.convertToOriginalCoordinates(mousePosition, scaleFactor);

		// Detect if user has clicked on a concept
		selectedConcept = PaintUtils.hitConcept(ontology.getConcepts(), selectedPoint);

		// Detect if user has clicked on a link
		selectedLink = PaintUtils.hitLink(ontology.getConcepts(), selectedPoint, scaleFactor * 4);

		// Detect if we are edit mode
		if (isEditing) {
			isEditing = false;
			hideNameEditor();
		}
		
		// Detect if a building link
		else if (isBuildingLink) {

			// A link is fully established
			if (linkSource != null && linkTarget != null) {
				selectedLink = new ConceptLink();
				selectedLink.setSourceConcept(linkSource);
				selectedLink.setTargetConcept(linkTarget);
				actionPerformed(new ActionEvent(e.getSource(), e.getID(), "NEW_LINK_END"));
			}
			
			// Reset link building
			isBuildingLink = false;
			linkSource = null;
			linkTarget = null;
		}

		// Detect if use made a left click
		else if (SwingUtilities.isLeftMouseButton(e)) {

			// Single click
			if (e.getClickCount() == 1) {

				isEditing = false;

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

				isEditing = true;

				if (selectedConcept != null) {
					showConceptNameEditor();
				}
				else if (selectedLink != null) {
					showLinkNameEditor();
				}
			}
		}

		// Detect if user made a right click
		else if (SwingUtilities.isRightMouseButton(e)) {

			// Init context menu
			contextMenu.clearMenuState();
			contextMenu.setCreateConceptMenuState(selectedConcept == null);
			contextMenu.setDeleteConceptMenuState(selectedConcept != null);
			contextMenu.setCreateLinkMenuState(selectedConcept != null);
			contextMenu.setDeleteLinkMenuState(selectedLink != null);
			contextMenu.setPropertiesMenuState(selectedConcept != null);

			// Show the menu
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
		isBuildingLink = false;

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
			Point currentPoint = PaintUtils.convertToOriginalCoordinates(e.getPoint(), scaleFactor);
			Point previousPoint = PaintUtils.convertToOriginalCoordinates(mousePosition, scaleFactor);

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

		if (isBuildingLink) {

			linkTarget = null;

			// Retrieve mouse position in ontology coordinates
			Point currentPoint = PaintUtils.convertToOriginalCoordinates(e.getPoint(), scaleFactor);

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

		if (e.isControlDown()) {
			scaleFactor = Math.max(minScaleFactor, scaleFactor - ZOOM_FACTOR_INCREMENT * e.getWheelRotation());
			repaint();
		}
		if (scrollPanel != null) {

			if (e.isShiftDown()) {
				JScrollBar scrollBar = scrollPanel.getHorizontalScrollBar();
				scrollBar.setValue(scrollBar.getValue() + scrollBar.getUnitIncrement() * e.getWheelRotation());
				scrollPanel.revalidate();
			}
			else {
				JScrollBar scrollBar = scrollPanel.getVerticalScrollBar();
				scrollBar.setValue(scrollBar.getValue() + scrollBar.getUnitIncrement() * e.getWheelRotation());
				scrollPanel.revalidate();
			}
		}
	}

	/**
	 * Handle CTRL key within current view
	 * 
	 * @param e
	 */
	private void onPanelKeyPressed(KeyEvent e) {

		if (e.isControlDown()) {

			if (e.getKeyCode() == KeyEvent.VK_0 || e.getKeyCode() == KeyEvent.VK_NUMPAD0) {
				scaleFactor = minScaleFactor;
				scrollPanel.revalidate();
				repaint();
			}
		}
		else if (e.getKeyCode() == KeyEvent.VK_DELETE) {

			if (selectedConcept != null) {
				actionPerformed(new ActionEvent(this, -1, "DELETE_CONCEPT"));
			}
			else if (selectedLink != null) {
				actionPerformed(new ActionEvent(this, -1, "DELETE_LINK"));
			}
		}
	}

	/**
	 * Handle ENTER or ESC within the editor field.
	 * 
	 * @param e
	 */
	private void onEditorKeyPressed(KeyEvent e) {

		if (e.getKeyCode() == KeyEvent.VK_ENTER) {

			if (selectedConcept != null) {
				setConceptName(selectedConcept, editorField.getText());
			}
			else if (selectedLink != null) {
				setLinkName(selectedLink, editorField.getText());
			}

			isEditing = false;
			hideNameEditor();
			requestFocus();
			repaint();
		}
		else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			
			isEditing = false;
			hideNameEditor();
			requestFocus();
			repaint();
		}
	}

	/**
	 * Handle lost of focus.
	 * 
	 * @param e
	 */
	private void onFocusLost(FocusEvent e) {
		isEditing = false;
		hideNameEditor();
	}

	/**
	 * Return the surface taken by all ontology concepts (in view area).
	 * 
	 * @return a Rectangle
	 */
	private Rectangle getOntologyBounds() {

		Rectangle bounds = new Rectangle();

		// Scan all concepts and compute the whole ontology area 
		for (Concept concept : ontology.getConcepts()) {
			Rectangle rect = PaintUtils.convertToViewCoordinates(concept.getBounds(), scaleFactor);
			bounds = bounds.union(rect);
		}

		bounds.width  += BORDER_SIZE * scaleFactor;
		bounds.height += BORDER_SIZE * scaleFactor;
		
		return bounds;
	}

	/**
	 * Return the panel preferred size.
	 */
	@Override
	public Dimension getPreferredSize() {
		Rectangle ontoArea = getOntologyBounds();
		return new Dimension(ontoArea.width, ontoArea.height);
	}

	/**
	 * Handle panel resizing
	 */
	public void computeMinScaleFactor() {

		// Get visible drawing area
		Rectangle drawRect = getVisibleRect();
		
		// Get ontology area to display
		Rectangle ontoRect = getOntologyBounds();

		// Check horizontal/vertical factor
		double horizontalFactor = 1d * drawRect.width / ontoRect.width;
		double verticalFactor   = 1d * drawRect.height / ontoRect.height;
		
		// Calculate minimal factor to display full ontology
		minScaleFactor = Math.min(horizontalFactor, verticalFactor);
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
	 * Retrieve the current selected link.
	 * 
	 * @return
	 *         the selected link or null
	 */
	public ConceptLink getSelectedLink() {
		return this.selectedLink;
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
			if (isBuildingLink) {
				paintBuildingLink(g);
			}

			// Draw selected link
			else if (selectedLink != null) {
				paintSelectedLink(g);
			}
		}
		
		/*
		g.setColor(Color.RED);
		Rectangle rect = getVisibleRect();
		rect.width -= 2;
		rect.height -= 2;
		g.drawRect(rect.x, rect.y, rect.width, rect.height);
		*/		
	}

	/**
	 * Paint a concept.
	 * If selected, also draw little squares at each corner.
	 */
	private void paintConcept(Graphics g, Concept concept) {

		// Retrieve concept bounds in view coordinates
		Rectangle bounds = PaintUtils.convertToViewCoordinates(concept.getBounds(), scaleFactor);

		// Retrieve centered text bounds
		Rectangle textBounds = PaintUtils.getCenteredText(g, bounds, concept.getName());

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
		Rectangle sourceBounds = PaintUtils.convertToViewCoordinates(link.getSourceConcept().getBounds(), scaleFactor);
		Rectangle targetBounds = PaintUtils.convertToViewCoordinates(link.getTargetConcept().getBounds(), scaleFactor);

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
				Rectangle textBounds = PaintUtils.getCenteredText(g, linkBounds, link.getQualifier());

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
		Rectangle sourceBounds = PaintUtils.convertToViewCoordinates(selectedLink.getSourceConcept().getBounds(), scaleFactor);
		Rectangle targetBounds = PaintUtils.convertToViewCoordinates(selectedLink.getTargetConcept().getBounds(), scaleFactor);

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
		Rectangle sourceBounds = PaintUtils.convertToViewCoordinates(linkSource.getBounds(), scaleFactor);
		Rectangle targetBounds = new Rectangle(getMousePosition().x - 1, getMousePosition().y - 1, 2, 2);

		// Retrieve line between source concepts and mouse position
		Line linkLine = PaintUtils.getLine(sourceBounds, targetBounds);

		// Recalculate line to target, if target available
		if (linkTarget != null) {
			targetBounds = PaintUtils.convertToViewCoordinates(linkTarget.getBounds(), scaleFactor);
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
	 * Show editing field
	 */
	private void showConceptNameEditor() {

		Graphics g = getGraphics();

		// Sets font size
		Font scaledFont = getFont().deriveFont((float) Math.round(FONT_SIZE * scaleFactor));
		g.setFont(scaledFont);

		// Retrieve current concept bounds in view coordinates
		Rectangle editorBounds = PaintUtils.convertToViewCoordinates(selectedConcept.getBounds(), scaleFactor);

		// Update text field
		editorField.setFont(g.getFont());
		editorField.setBounds(editorBounds);
		editorField.setText(selectedConcept.getName());

		// Make field visible
		editorField.setVisible(true);
		editorField.selectAll();
		editorField.requestFocus();
	}

	/**
	 * Show editing field
	 */
	private void showLinkNameEditor() {

		Graphics g = getGraphics();

		// Sets font size
		Font scaledFont = getFont().deriveFont((float) Math.round(FONT_SIZE * scaleFactor));
		g.setFont(scaledFont);

		// Retrieve source/target bounds in view coordinates
		Rectangle sourceBounds = PaintUtils.convertToViewCoordinates(selectedLink.getSourceConcept().getBounds(), scaleFactor);
		Rectangle targetBounds = PaintUtils.convertToViewCoordinates(selectedLink.getTargetConcept().getBounds(), scaleFactor);

		// Retrieve line between source/target concepts
		Line linkLine = PaintUtils.getLine(sourceBounds, targetBounds);

		if (linkLine != null) {

			// Retrieve link text size
			String linkText = selectedLink.getQualifier() == null ? "XXXXX" : selectedLink.getQualifier();

			// Calculate text bounds
			Rectangle linkBounds = new Rectangle(linkLine.x1, linkLine.y1, linkLine.width, linkLine.height);
			Rectangle editorBounds = PaintUtils.getCenteredText(g, linkBounds, linkText);
			editorBounds.grow(g.getFont().getSize() / 2, g.getFont().getSize());

			// Update field text
			editorField.setFont(g.getFont());
			editorField.setBounds(editorBounds);
			editorField.setText(selectedLink.getQualifier());

			// Make field visible
			editorField.setVisible(true);
			editorField.selectAll();
			editorField.requestFocus();
		}
	}

	/**
	 * Hide concept/link editor
	 */
	private void hideNameEditor() {
		isEditing = false;
		editorField.setVisible(false);
		requestFocus();
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
		Rectangle bounds = PaintUtils.convertToViewCoordinates(selectedConcept.getBounds(), scaleFactor);

		// Calculate new concept bounds
		Rectangle viewBounds = PaintUtils.getCenteredText(g, new Rectangle(bounds), conceptName);
		viewBounds.grow(g.getFont().getSize(), g.getFont().getSize());
		viewBounds.x = bounds.x;
		viewBounds.y = bounds.y;

		// Convert bounds into ontology coordinates
		bounds = PaintUtils.convertToOriginalCoordinates(viewBounds, scaleFactor);

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
	 * Put view in edit mode.
	 * That is displaying an name editor for current concept/link.
	 */
	public void startEditMode() {

		if (selectedLink != null) {
			isEditing = true;
			showLinkNameEditor();
		}
		else if (selectedConcept != null) {
			isEditing = true;
			showConceptNameEditor();
		}
	}

	/**
	 * Put view in link mode.
	 * That is build interactively a link.
	 */
	public void startLinkMode() {

		linkSource = selectedConcept;
		linkTarget = null;

		isBuildingLink = true;
		repaint();
	}

	/**
	 * Reset current selection.
	 */
	public void clearSelection() {

		selectedConcept = null;
		selectedLink = null;
		isMovingConcept = false;
		isBuildingLink = false;
		isEditing = false;

		hideNameEditor();
		repaint();
	}

	/**
	 * Add an action listener to handle context menu selection.
	 * 
	 * @param listener
	 */
	public void addActionListener(ActionListener listener) {
		this.actionListener = listener;
	}
	
	/**
	 * Handle action generated by menu.
	 */
	public void actionPerformed(ActionEvent e) {
		if (actionListener != null) {
			actionListener.actionPerformed(e);
		}
	}	
}
