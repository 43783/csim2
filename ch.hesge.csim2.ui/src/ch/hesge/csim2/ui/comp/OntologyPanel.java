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

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import ch.hesge.csim2.core.logic.ApplicationLogic;
import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.ConceptLink;
import ch.hesge.csim2.core.model.Ontology;
import ch.hesge.csim2.ui.utils.Line;
import ch.hesge.csim2.ui.utils.SwingUtils;

@SuppressWarnings("serial")
public class OntologyPanel extends JPanel {

	// Private attributes
	private Ontology ontology;
	private Concept selectedConcept;
	private ConceptLink selectedLink;
	private Concept targetConcept;

	private JScrollPane scrollPanel;
	private ConceptPopup contextMenu;
	private JTextField editorField;
	private Point mouseLocation;
	private double minScaleFactor;
	private double scaleFactor;
	private ActionListener actionListener;

	private boolean isEditing;
	private boolean isDraggingConcept;
	private boolean isDraggingLink;
	private boolean isDynamicPosition;

	// Internal constants
	private static int FONT_SIZE = 10;
	private static int BORDER_SIZE = 20;
	private static int SELECTION_HANDLE_SIZE = 4;
	private static double ZOOM_FACTOR_INCREMENT = 0.05;
	private static Color CONCEPT_TEXT = Color.BLACK;
	private static Color CONCEPT_COLOR = Color.GRAY;
	private static Color CONCEPT_BACKGROUND = new Color(251, 247, 180);
	private static Color LINK_TEXT = Color.BLACK;
	private static Color LINK_COLOR = Color.GRAY;
	private static Color SELECTION_BACKGROUND = Color.WHITE;
	private static Color SELECTION_COLOR = Color.BLACK;

	/**
	 * Default constructor
	 */
	public OntologyPanel(Ontology ontology, JScrollPane scrollPanel) {

		this.minScaleFactor = -1d;
		this.ontology = ontology;
		this.scrollPanel = scrollPanel;

		initComponent();
		initListeners();
	}

	/**
	 * Initialize the component
	 */
	private void initComponent() {

		setOpaque(true);
		setBackground(Color.WHITE);

		contextMenu = new ConceptPopup();
		contextMenu.addActionListener(actionListener);

		editorField = new JTextField();
		editorField.setVisible(false);
		add(editorField);
	}

	/**
	 * Initialize scale factor to display to whole ontology
	 */
	private void initScaleFactor() {

		if (minScaleFactor == -1) {

			scaleFactor = 1d;
			Rectangle drawArea = getDrawBounds();
			Rectangle ontologyArea = getOntologyBounds();

			// Adjust scale factor to display all concepts		
			if (ontologyArea.width > ontologyArea.height) {
				minScaleFactor = 1d * drawArea.width / ontologyArea.width;
			}
			else {
				minScaleFactor = 1d * drawArea.height / ontologyArea.height;
			}
			
			scaleFactor = minScaleFactor;
		}
	}

	/**
	 * Initialize listeners
	 */
	private void initListeners() {

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

		// Listen to key typed
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				onControlKeyPressed(e);
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
	 * Add an action listener to handle context menu selection.
	 * 
	 * @param listener
	 */
	public void addActionListener(ActionListener listener) {
		this.actionListener = listener;
	}

	/**
	 * Handle mouse button clicked.
	 * 
	 * @param e
	 */
	private void onMousePressed(MouseEvent e) {

		requestFocus();

		// Keep current mouse location (needed by context menu)
		mouseLocation = e.getPoint();			

		// Retrieve mouse position in ontology coordinates
		Point selectedPoint = SwingUtils.convertToOriginalCoordinates(mouseLocation, scaleFactor);

		// If user select a concept, get concept under the mouse
		selectedConcept = SwingUtils.hitConcept(ontology.getConcepts(), selectedPoint);

		// If user is dragging a link
		if (isDraggingLink) {
			
			// If a target is selected, dragging link is completed
			if (selectedConcept != null && targetConcept != null) {
				selectedLink = ApplicationLogic.createConceptLink(ontology, selectedConcept, targetConcept);
				selectedConcept = null;
			}

			targetConcept = null;
			isDraggingConcept = false;
			isDraggingLink = false;

			repaint();
		}
		else if (selectedConcept != null) {
			
			// Put concept in front of other
			ontology.getConcepts().remove(selectedConcept);
			ontology.getConcepts().add(selectedConcept);
			selectedLink = null;
		}
		else {
			// Otherwise, try to retrieve a link
			selectedLink = SwingUtils.hitLink(ontology.getConcepts(), selectedPoint, scaleFactor * 4);
		}

		// Handle single right-click (show context menu)
		if (SwingUtilities.isRightMouseButton(e)) {

			// Init context menu
			contextMenu.clearMenuState();
			contextMenu.setCreateConceptMenuState(true);
			contextMenu.setDeleteConceptMenuState(selectedConcept != null);
			contextMenu.setCreateLinkMenuState(selectedConcept != null);
			contextMenu.setDeleteLinkMenuState(selectedLink != null);
			contextMenu.setPropertiesMenuState(selectedConcept != null);

			// Show the menu
			contextMenu.show(e.getComponent(), e.getX(), e.getY());
		}

		// Handle single left-click on concept
		else if (SwingUtilities.isLeftMouseButton(e)) {

			if (e.getClickCount() == 1) {

				// Start dragging concept
				isDraggingConcept = true;
				isDraggingLink = false;
				repaint();
			}
			else if (e.getClickCount() == 2) {
				startEditing();
			}
		}

		/*
		else {

			// If a label is currently being edited
			if (isEditing) {
				stopEditing();
			}


		}
		*/
	}

	/**
	 * Handle mouse button released.
	 * 
	 * @param e
	 */
	private void onMouseReleased(MouseEvent e) {
		
		// Stop dragging concept or link
		isDraggingConcept = false;
		isDraggingLink = false;
		
		repaint();
	}

	/**
	 * Handle mouse motion while clicking on left button.
	 * 
	 * @param mousePoint
	 */
	private void onMouseDragged(MouseEvent e) {

		if (isDraggingConcept && selectedConcept != null) {

			// Retrieve mouse position in ontology coordinates
			Point currentPoint = SwingUtils.convertToOriginalCoordinates(e.getPoint(), scaleFactor);
			Point previousPoint = SwingUtils.convertToOriginalCoordinates(mouseLocation, scaleFactor);

			// Adjust concept position within ontology
			selectedConcept.getBounds().translate(currentPoint.x - previousPoint.x, currentPoint.y - previousPoint.y);

			// Update mouse position
			mouseLocation = e.getPoint();			
			repaint();
		}
	}

	/**
	 * Handle mouse motion.
	 * 
	 * @param mousePoint
	 */
	private void onMouseMoved(MouseEvent e) {

		if (isDraggingLink && selectedConcept != null) {

			// Retrieve mouse position in ontology coordinates
			Point currentPoint = SwingUtils.convertToOriginalCoordinates(e.getPoint(), scaleFactor);

			// Retrieve concept under mouse pointer
			targetConcept = SwingUtils.hitConcept(ontology.getConcepts(), currentPoint);

			if (targetConcept != null) {

				boolean isConceptAlreadyLinked = false;

				// Check if the target concept is already linked from current
				// one
				for (ConceptLink link : selectedConcept.getLinks()) {
					if (link.getTargetConcept() == targetConcept) {
						isConceptAlreadyLinked = true;
						break;
					}
				}

				// Check if the target concept is already linked to current one
				for (ConceptLink link : targetConcept.getLinks()) {
					if (link.getTargetConcept() == selectedConcept) {
						isConceptAlreadyLinked = true;
						break;
					}
				}

				// If target concept is already linked
				if (isConceptAlreadyLinked || targetConcept == selectedConcept) {
					targetConcept = null;
				}
			}

			// Update mouse position
			mouseLocation = e.getPoint();
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
	private void onControlKeyPressed(KeyEvent e) {
		
		if (e.isControlDown() && (e.getKeyCode() == KeyEvent.VK_0 || e.getKeyCode() == KeyEvent.VK_NUMPAD0)) {
			scaleFactor = minScaleFactor;
			repaint();
		}
	}

	/**
	 * Handle ENTER or ESC within the editor field.
	 * 
	 * @param e
	 */
	private void onEditorKeyPressed(KeyEvent e) {

		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			stopEditing();
		}
		else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			cancelEditing();
		}
	}

	/**
	 * Handle lost of focus.
	 * 
	 * @param e
	 */
	private void onFocusLost(FocusEvent e) {
		cancelEditing();
		selectedConcept = null;
		repaint();
	}

	/**
	 * Return the surface area bounds (without the border)
	 * 
	 * @return a Rectangle
	 */
	private Rectangle getDrawBounds() {

		int x = getInsets().left;
		int y = getInsets().top;
		int width = getSize().width - getInsets().left - getInsets().right - 1;
		int height = getSize().height - getInsets().top - getInsets().bottom - 1;

		return new Rectangle(x, y, width, height);
	}

	/**
	 * Return the surface taken by all ontology concepts.
	 * 
	 * @return a Rectangle
	 */
	private Rectangle getOntologyBounds() {

		Rectangle bounds = new Rectangle();

		if (ontology != null) {

			// Recalculate ontology panel size
			for (Concept concept : ontology.getConcepts()) {

				// Convert bounds into view coordinates
				Rectangle conceptBounds = SwingUtils.convertToViewCoordinates(concept.getBounds(), scaleFactor);

				// Include concept bounds into current global rectangle
				bounds = bounds.union(conceptBounds);
			}

			// Create view size
			bounds.x -= BORDER_SIZE;
			bounds.y -= BORDER_SIZE;
			bounds.width += BORDER_SIZE * 2;
			bounds.height += BORDER_SIZE * 2;
		}

		return bounds;
	}

	/**
	 * Return the panel preferred size.
	 */
	@Override
	public Dimension getPreferredSize() {
		Rectangle ontologyArea = getOntologyBounds();
		return new Dimension(getInsets().left + ontologyArea.width + getInsets().right, getInsets().top + ontologyArea.height + getInsets().bottom);
	}

	/**
	 * Retrieve the current selected concept.
	 * 
	 * @return
	 *         the selected concept or null
	 */
	public Concept getSelectedConcept(Concept concept) {
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
		initScaleFactor();

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

			// Draw selected link
			if (selectedLink != null) {
				paintSelectedLink(g);
			}

			// Draw dragging link
			if (isDraggingLink && selectedConcept != null) {
				paintDraggingLink(g);
			}
		}
	}

	/**
	 * Paint a concept.
	 * If selected, also draw little squares at each corner.
	 */
	private void paintConcept(Graphics g, Concept concept) {

		// Retrieve concept bounds in view coordinates
		Rectangle bounds = SwingUtils.convertToViewCoordinates(concept.getBounds(), scaleFactor);

		// Retrieve centered text bounds
		Rectangle textBounds = SwingUtils.getCenteredText(g, bounds, concept.getName());

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
		Rectangle sourceBounds = SwingUtils.convertToViewCoordinates(link.getSourceConcept().getBounds(), scaleFactor);
		Rectangle targetBounds = SwingUtils.convertToViewCoordinates(link.getTargetConcept().getBounds(), scaleFactor);

		// Retrieve line between source/target concepts
		Line linkLine = SwingUtils.getLine(sourceBounds, targetBounds);

		if (linkLine != null) {

			// Draw line
			g.setColor(LINK_COLOR);
			g.drawLine(linkLine.x1, linkLine.y1, linkLine.x2, linkLine.y2);

			// Draw arrow
			int arrowSize = (int) Math.round(5 * scaleFactor);
			SwingUtils.drawArrowAtEnd(g, linkLine, arrowSize);

			// Draw link qualifier
			if (link.getQualifier() != null) {

				// Retrieve link text bounds
				Rectangle linkBounds = new Rectangle(linkLine.x1, linkLine.y1, linkLine.width, linkLine.height);
				Rectangle textBounds = SwingUtils.getCenteredText(g, linkBounds, link.getQualifier());

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
		Rectangle sourceBounds = SwingUtils.convertToViewCoordinates(selectedLink.getSourceConcept().getBounds(), scaleFactor);
		Rectangle targetBounds = SwingUtils.convertToViewCoordinates(selectedLink.getTargetConcept().getBounds(), scaleFactor);

		// Retrieve line between source/target concepts
		Line linkLine = SwingUtils.getLine(sourceBounds, targetBounds);

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
	private void paintDraggingLink(Graphics g) {

		// Retrieve source/target bounds in view coordinates
		Rectangle sourceBounds = SwingUtils.convertToViewCoordinates(selectedConcept.getBounds(), scaleFactor);
		Rectangle targetBounds = new Rectangle(mouseLocation.x - 1, mouseLocation.y - 1, 2, 2);

		// Retrieve line between source concepts and mouse position
		Line linkLine = SwingUtils.getLine(sourceBounds, targetBounds);

		// Recalculate line to target, if a target concept is available
		if (targetConcept != null) {
			targetBounds = SwingUtils.convertToViewCoordinates(targetConcept.getBounds(), scaleFactor);
			linkLine = SwingUtils.getLine(sourceBounds, targetBounds);
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
			if (targetConcept != null) {
				g.drawRect(targetBounds.x, targetBounds.y, targetBounds.width, targetBounds.height);
			}
		}
	}

	/**
	 * Start concept/link editing mode
	 */
	private void startEditing() {

		// Stop dragging concept or link
		isDraggingConcept = false;
		isDraggingLink = false;
		
		
		cancelEditing();

		if (selectedConcept != null && !isDynamicPosition) {

			Graphics g = getGraphics();

			// Sets font size
			Font scaledFont = getFont().deriveFont((float) Math.round(FONT_SIZE * scaleFactor));
			g.setFont(scaledFont);

			// Retrieve current concept bounds in view coordinates
			Rectangle editorBounds = SwingUtils.convertToViewCoordinates(selectedConcept.getBounds(), scaleFactor);

			// Update text field
			editorField.setFont(g.getFont());
			editorField.setBounds(editorBounds);
			editorField.setText(selectedConcept.getName());

			// Make field visible
			editorField.setVisible(true);
			editorField.selectAll();
			editorField.requestFocus();

			this.isEditing = true;
		}
		else if (selectedLink != null && !isDynamicPosition) {

			Graphics g = getGraphics();

			// Sets font size
			Font scaledFont = getFont().deriveFont((float) Math.round(FONT_SIZE * scaleFactor));
			g.setFont(scaledFont);

			// Retrieve source/target bounds in view coordinates
			Rectangle sourceBounds = SwingUtils.convertToViewCoordinates(selectedLink.getSourceConcept().getBounds(), scaleFactor);
			Rectangle targetBounds = SwingUtils.convertToViewCoordinates(selectedLink.getTargetConcept().getBounds(), scaleFactor);

			// Retrieve line between source/target concepts
			Line linkLine = SwingUtils.getLine(sourceBounds, targetBounds);

			if (linkLine != null) {

				// Retrieve link text size
				String linkText = selectedLink.getQualifier() == null ? "XXXXX" : selectedLink.getQualifier();

				// Calculate text bounds
				Rectangle linkBounds = new Rectangle(linkLine.x1, linkLine.y1, linkLine.width, linkLine.height);
				Rectangle editorBounds = SwingUtils.getCenteredText(g, linkBounds, linkText);
				editorBounds.grow(g.getFont().getSize() / 2, g.getFont().getSize());

				// Update field text
				editorField.setFont(g.getFont());
				editorField.setBounds(editorBounds);
				editorField.setText(selectedLink.getQualifier());

				// Make field visible
				editorField.setVisible(true);
				editorField.selectAll();
				editorField.requestFocus();

				this.isEditing = true;
			}
		}

		repaint();
	}

	/**
	 * Stop concept or link editing mode
	 */
	private void stopEditing() {

		if (selectedConcept != null) {

			Graphics g = getGraphics();

			// Sets font size
			Font scaledFont = getFont().deriveFont((float) Math.round(FONT_SIZE * scaleFactor));
			g.setFont(scaledFont);

			String newConceptName = "Concept";

			// Retrieve new concept name
			if (editorField.getText().trim().length() > 0) {
				newConceptName = editorField.getText().trim();
			}

			// Retrieve concepts bounds in view coordinates
			Rectangle bounds = SwingUtils.convertToViewCoordinates(selectedConcept.getBounds(), scaleFactor);

			// Calculate new concept bounds
			Rectangle viewBounds = SwingUtils.getCenteredText(g, new Rectangle(bounds), newConceptName);
			viewBounds.grow(g.getFont().getSize(), g.getFont().getSize());
			viewBounds.x = bounds.x;
			viewBounds.y = bounds.y;

			// Convert bounds into ontology coordinates
			Rectangle ontoBounds = SwingUtils.convertToOriginalCoordinates(viewBounds, scaleFactor);

			// Update concept size
			selectedConcept.setBounds(ontoBounds);
			selectedConcept.setName(editorField.getText());
		}
		else if (selectedLink != null) {

			if (editorField.getText().trim().length() > 0) {
				selectedLink.setQualifier(editorField.getText().trim());
			}
			else {
				selectedLink.setQualifier(null);
			}
		}

		isEditing = false;
		editorField.setVisible(false);
		requestFocus();
	}

	/**
	 * Cancel field editing (concept or link)
	 */
	private void cancelEditing() {
		editorField.setVisible(false);
		isEditing = false;
	}
}
