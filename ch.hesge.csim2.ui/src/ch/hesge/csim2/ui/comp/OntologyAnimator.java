package ch.hesge.csim2.ui.comp;

import java.awt.Rectangle;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.ConceptLink;
import ch.hesge.csim2.core.model.Ontology;
import ch.hesge.csim2.core.utils.Console;
import ch.hesge.csim2.core.utils.StringUtils;
import ch.hesge.csim2.ui.utils.Line;
import ch.hesge.csim2.ui.utils.PaintUtils;

public class OntologyAnimator implements Runnable {

	// Private attributes
	private OntologyPanel view;
	private Ontology ontology;
	private boolean isAnimating;
	private ExecutorService animator;

	/**
	 * Default constructor
	 */
	public OntologyAnimator(OntologyPanel view, Ontology ontology) {
		this.isAnimating = false;
		this.view = view;
		this.ontology = ontology;
	}

	/**
	 * Start adjusting dynamic concepts position
	 */
	public void start() {
		
		stop();
		
		isAnimating = true;
		animator = Executors.newFixedThreadPool(1);
		animator.submit(new Thread(this));
	}

	/**
	 * Stop adjusting dynamic concepts position
	 */
	public void stop() {
				
		if (animator != null) {
			animator.shutdownNow();
		}
		
		isAnimating = false;
		animator = null;
	}

	/**
	 * Suspend animation without changing internal state.
	 */
	public void suspend() {
		
		if (animator != null) {
			animator.shutdownNow();
		}
		
		animator = null;
	}
	
	/**
	 * Resume animation to previous animation state.
	 */
	public void resume() {
		
		if (isAnimating) {
			start();
		}
	}
	
	/**
	 * Scan all concepts and recompute their position to each other.
	 * This method is used internally in a separate thread.
	 */
	@Override
	public void run() {

		while (isAnimating) {

			try {

				List<Concept> concepts = Collections.synchronizedList(ontology.getConcepts());

				// Normalize distance between all concepts
				for (Concept sourceConcept : concepts) {
					for (Concept targetConcept : concepts) {

						// Skip concept if selected
						if (view.getSelectedConcept() == targetConcept) {
							continue;
						}

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
							linkLine = PaintUtils.getDiagonal(sourceBounds, targetBounds);
						}
						else {
							linkLine = PaintUtils.getLine(sourceBounds, targetBounds);
						}

						if (linkLine != null) {

							// Retrieve variation in x and y
							int vx = linkLine.x2 - linkLine.x1;
							int vy = linkLine.y2 - linkLine.y1;

							// Retrieve segment length
							double distance = PaintUtils.getLength(linkLine);

							// If concepts are intersecting, we should separate them
							if (isIntersecting) {

								// Compute distance amplification factor
								double ratio = PaintUtils.getAmplificationRatio(distance, 200d);

								// Calculate required variation in x and y
								targetBounds.x += ratio * vx;
								targetBounds.y += ratio * vy;
							}

							// If concepts are too close to each other, we should separate them
							else if (distance < 50d) {

								// Compute distance amplification factor
								double ratio = PaintUtils.getAmplificationRatio(distance, 50d);

								// Calculate required variation in x and y
								targetBounds.x += ratio * vx;
								targetBounds.y += ratio * vy;
							}

							// If concepts linked to each other, a minimal distance is required
							else if (isTargetLinkedToSource && distance > 50d) {

								// Compute distance compression factor
								double ratio = PaintUtils.getReductionRatio(distance, 50d);

								// Calculate required variation in x and y
								targetBounds.x -= ratio * vx;
								targetBounds.y -= ratio * vy;
							}
						}
					}
				}

				view.repaint();
				Thread.sleep(100);
			}
			catch (InterruptedException e) {
				// Skip interruption
				break;
			}
			catch (Exception e) {
				Console.writeError(this, StringUtils.toString(e));
			}
		}
	}
}
