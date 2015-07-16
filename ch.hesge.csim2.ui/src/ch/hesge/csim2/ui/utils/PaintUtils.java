package ch.hesge.csim2.ui.utils;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.ListIterator;

import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.ConceptLink;

/**
 * This is a general purpose utility class dedicated to swing utility functions.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */
public class PaintUtils {

	/**
	 * Retrieve the rect area, within another rectangle, where a text should be
	 * displayed. The rectangle returned will allow displaying the text centered
	 * within the provided rectangle.
	 * 
	 * the graphics where the text will be displayed
	 * 
	 * @param g
	 *        the graphics used to compute visual size
	 * @param rect
	 *        the rectangle area where the text should be displayed
	 * @param text
	 *        the text to display
	 * @return
	 *         the rectangle area the text take on screen
	 */
	public static Rectangle getCenteredBounds(Graphics g, Rectangle rect, String text) {

		// Retrieve text metrics
		FontMetrics fm = g.getFontMetrics(g.getFont());
		Rectangle2D textRect = fm.getStringBounds(text, g);

		// Calculate center text position
		int x = (int) Math.round(rect.x + (rect.width - textRect.getWidth()) / 2);
		int y = (int) Math.round(rect.y + (rect.height - textRect.getHeight() - fm.getDescent()) / 2);
		int width = (int) Math.round(textRect.getWidth());
		int height = (int) Math.round(textRect.getHeight());

		// Return resulting text rectangle
		return new Rectangle(x, y, width, height);
	}

	/**
	 * Retrieve the center of a rectangle.
	 * 
	 * @param bounds
	 *        the rectangle used for calculation
	 * @return
	 *         the coordinates at the center of the rectangle
	 */
	public static Point getMidPoint(Rectangle bounds) {
		return new Point(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
	}

	/**
	 * Retrieve the line linking two rectangles. If rectangles intersect, return
	 * null.
	 * 
	 * @param a
	 *        first rectangle
	 * @param b
	 *        second rectangle
	 * @return
	 *         the line start from a center to b center
	 */
	public static Line getLine(Rectangle a, Rectangle b) {

		// Retrieve source/target centered point
		Point sourcePoint = PaintUtils.getMidPoint(a);
		Point targetPoint = PaintUtils.getMidPoint(b);

		// Get line linking both rectangles
		Line linkLine = new Line(sourcePoint, targetPoint);

		// Calculate source/line intersection
		Point startPoint = PaintUtils.getIntersection(linkLine, a);

		// Calculate target/line intersection
		Point endPoint = PaintUtils.getIntersection(linkLine, b);

		if (startPoint != null && endPoint != null) {
			return new Line(startPoint, endPoint);
		}

		return null;
	}

	/**
	 * Retrieve the distance between two points a and b.
	 * 
	 * <pre>
	 * 
	 * We use the following algorithm:
	 *  
	 *              B
	 *  		    .
	 *  		  . |
	 *  		 .  |
	 *  		.   |
	 *  	   .    |
	 *  	  .-----+
	 *      A         C
	 *           
	 * Let be:
	 * 
	 * 		A = (Ax, Ay) 
	 * 		B = (Bx, By)
	 * 
	 * 		AB = distance between A and B
	 * 
	 * Then:
	 * 
	 * 		AB^2 = AC^2 + BC^2
	 * 
	 * So
	 * 		AB = sqrt( AC^2 + BC^2 )
	 *  
	 * Which can be represented by:
	 * 
	 * 		dis(a, b) = sqrt( (Bx - Ax)^2 + (By - Ay)^2 )
	 * 
	 * </pre>
	 * 
	 * @param a
	 *        the first point
	 * @param b
	 *        the second point
	 * @param a
	 *        the distance between a and b
	 * @return
	 *         the distance between the two points
	 */
	public static int getDistance(Point a, Point b) {
		double ABx = b.x - a.x;
		double ABy = b.y - a.y;
		return (int) Math.sqrt(ABx * ABx + ABy * ABy);
	}

	/**
	 * Retrieve the distance between a point and a line.
	 * 
	 * <pre>
	 * 
	 * We use the following algorithm:
	 *  
	 *  		    P
	 *  		  . |
	 *  		 .  |
	 *  		.   |
	 *  	   .    |
	 *  	A  -----H--------->  B
	 *  
	 * Parametric AB equation:
	 *  
	 *  	Y = A + k * AB
	 *  	k = (Y - A) / AB
	 *  	with 0 < k < 1 and Y € AB
	 *  
	 * Calculation of PH:
	 *  
	 *  	AP^2 = AH^2 + PH^2
	 *  	PH = sqrt(AP^2 - AH^2)
	 *  	
	 * Calculation of AH (with scalar product):
	 *  
	 *  	(AP ° AB) = AH * AB 
	 *  	AH = (AP ° AB) / AB 
	 *  
	 *  	And by definition of scalar product: 
	 *  
	 *  	AP ° AB = APx*ABx + APy*ABy
	 *  
	 *  	So
	 *  
	 *  	AH = (APx*ABx + APy*ABy) / AB 
	 *  
	 *  Finally: 
	 *  
	 *  	PH = sqrt(AP*AP - AH*AH)
	 *  	with AH = (APx*ABx + APy*ABy) / AB
	 *  			     
	 * ------------------------------------
	 * To synthesize the algorithm:
	 * ------------------------------------
	 * 
	 * if AB = 0
	 * 
	 * 		no AP projection fall into AB
	 * 
	 * otherwise, calculate k:
	 * 
	 * 		if ABx != 0		=>		k = (Yx - Ax) / ABx
	 * 		if ABy != 0		=>		k = (Yy - Ay) / ABy
	 * 
	 * and if k is outside [0,1]
	 * 
	 * 		projection point H is outside segment AB
	 * 
	 * otherwise
	 * 
	 * 		a projection exists with distance PH
	 * 		given by the equation: PH = sqrt(AP*AP - AH*AH)
	 *
	 * </pre>
	 * 
	 * @param point
	 *        the point
	 * @param line
	 *        the line
	 * @param a
	 *        the distance between the point and line
	 * @return
	 *         the distance between the point and line
	 */
	public static int getDistance(Point point, Line line) {

		double ABx = line.x2 - line.x1;
		double ABy = line.y2 - line.y1;

		double AB = Math.sqrt(ABx * ABx + ABy * ABy);

		// Check if segment has a length
		if (AB != 0) {

			double Yx = point.x;
			double Ax = line.x1;
			double Yy = point.y;
			double Ay = line.y1;

			// Retrieve equation parameter
			double k = ABx != 0 ? (Yx - Ax) / ABx : (Yy - Ay) / ABy;

			// Check if parameter is between 0 and 1
			if (k >= 0 && k <= 1) {

				double APx = point.x - line.x1;
				double APy = point.y - line.y1;

				double AP = Math.sqrt(APx * APx + APy * APy);
				double AH = (APx * ABx + APy * ABy) / AB;
				double PH = Math.sqrt(AP * AP - AH * AH);

				return (int) PH;
			}
		}

		return Integer.MAX_VALUE;
	}

	/**
	 * Retrieve the intersection point between two lines.
	 * 
	 * <pre>
	 * 
	 * We use the following algorithm:
	 *  
	 *  		    C
	 *  		    |
	 *  	        |
	 *  	A  -----H--------->  B
	 *  		    |
	 *  	        |
	 *  		    D
	 *  
	 * With parametric equations:
	 *  
	 *  	AB segment:		Y1 = A + k * AB, with 0 < k < 1	
	 *  	CD segment:		Y2 = C + m * CD, with 0 < m < 1
	 *  
	 * We can deduce AB intersection with CD:
	 *  
	 *  	A + k * AB = C + m * CD
	 *  
	 * So, we can now express equations for both dimension x and y:
	 * 
	 *  	Ax + k * ABx = Cx + m * CDx		(1)
	 *  	Ay + k * ABy = Cy + m * CDy		(2)
	 *  
	 * We now have 2 equations with 2 unknowns (k an m).
	 * We can solve this parametric system and found k,m for intersection:
	 *  
	 * To calculate k, we should eliminate m:
	 *  
	 *  	(1) * CDy - (2) * CDx           cleared by substraction
	 *  	                                 | 
	 *  	Ax*CDy + k*ABx*CDy = Cx*CDy + m*CDx*CDy		(1) * CDy
	 *   	Ay*CDx + k*ABy*CDx = Cy*CDx + m*CDy*CDx		(2) * CDx
	 *   --------------------------------------------------------------
	 *  	Ax*CDy - Ay*CDx + k*ABx*CDy - k*ABy*CDx = Cx*CDy - Cy*CDx	
	 *  								      
	 * So:									
	 *  
	 *  	=>	k*ABx*CDy - k*ABy*CDx = Cx*CDy - Cy*CDx - Ax*CDy + Ay*CDx  
	 * 	=>	k = (Cx*CDy - Cy*CDx - Ax*CDy + Ay*CDx) / (ABx*CDy - ABy*CDx)
	 *     
	 * To calculate m, we should eliminate k:
	 *  
	 *  	(1) * ABy - (2) * ABx
	 * 
	 *                 cleared by subtraction	
	 *  	            | 
	 *  	Ax*ABy + k*ABx*ABy = Cx*ABy + m*CDx*ABy		(1) * ABy
	 *  	Ay*ABx + k*ABy*ABx = Cy*ABx + m*CDy*ABx		(2) * ABx
	 *   --------------------------------------------------------------
	 *   	Ax*ABy - Ay*ABx = Cx*ABy - Cy*ABx + m*CDx*ABy - m*CDy*ABx
	 *   
	 * So:				
	 * 
	 * 	=>	Ax*ABy - Ay*ABx - Cx*ABy + Cy*ABx = m*CDx*ABy - m*CDy*ABx
	 * 	=> 	m = (Ax*ABy - Ay*ABx - Cx*ABy + Cy*ABx) / (CDx*ABy - CDy*ABx)
	 * 
	 * By cleaning denominator
	 * 
	 * 	k = (Cx*CDy - Cy*CDx - Ax*CDy + Ay*CDx) / (ABx*CDy - ABy*CDx)
	 * 	m = (Ax*ABy - Ay*ABx - Cx*ABy + Cy*ABx) / (ABy*CDx - ABx*CDy)
	 * 
	 * So we see that:
	 * 
	 * 		ABx*CDy - ABy*CDx = 0		=>		ABx*CDy = ABy*CDx	=>	no solution for k
	 * 		ABy*CDx - ABx*CDy = 0		=>		ABy*CDx = ABx*CDy	=>	no solution for m
	 * 
	 * So if:
	 * 
	 * 		ABx*CDy = ABy*CDx	=>	there is no intersection
	 * 
	 * ------------------------------------
	 * To synthesize the algorithm:
	 * ------------------------------------
	 * 
	 * if ABx*CDy = ABy*CDx
	 * 
	 * 		segments are parallel, there no intersection
	 * 
	 * otherwise, calculate k,m with:
	 * 
	 * 		k = (Cx*CDy - Cy*CDx - Ax*CDy + Ay*CDx) / (ABx*CDy - ABy*CDx)
	 * 		m = (Ax*ABy - Ay*ABx - Cx*ABy + Cy*ABx) / (ABy*CDx - ABx*CDy)
	 * 
	 * and if k outside [0,1] or m outside [0,1]
	 * 
	 * 
	 * intersection point is outside one or both segments
	 * 
	 * otherwise:
	 * 
	 * 		an intersection exists with coordinates 
	 * 		given by the equation Y1 = A + k * AB, with k parameter:
	 *  
	 *  		x =  Ax + k * ABx
	 *  		y =  Ay + k * ABy
	 * 
	 * </pre>
	 * 
	 * @param a
	 *        first line
	 * @param a
	 *        second rectangle
	 * @return
	 *         the intersection point or null
	 */
	public static Point getInsersection(Line a, Line b) {

		double ABx = a.x2 - a.x1;
		double ABy = a.y2 - a.y1;
		double CDx = b.x2 - b.x1;
		double CDy = b.y2 - b.y1;

		double ABxCDy = ABx * CDy;
		double AByCDx = ABy * CDx;

		// Check if denominator is not null
		if (ABxCDy != AByCDx) {

			double Ax = a.x1;
			double Ay = a.y1;
			double Cx = b.x1;
			double Cy = b.y1;

			// Retrieve equation parameters
			double k = (Cx * CDy - Cy * CDx - Ax * CDy + Ay * CDx) / (ABxCDy - AByCDx);
			double m = (Ax * ABy - Ay * ABx - Cx * ABy + Cy * ABx) / (AByCDx - ABxCDy);

			// Check if parameters are between 0 and 1
			if (k >= 0 && k <= 1 && m >= 0 && m <= 1) {

				// Use equation: Y1 = A + k * AB
				int x = (int) (Ax + k * ABx);
				int y = (int) (Ay + k * ABy);

				// Return the point
				return new Point(x, y);
			}
		}

		return null;
	}

	/**
	 * Retrieve the intersection between to rectangle.
	 * 
	 * <pre>
	 * 
	 *       A   
	 *    +-------+
	 *    |       | 
	 *    |  +----|---+  <-- maxTop 
	 *    |  |    |   |
	 *    +------ +   |  <-- minBottom
	 *       |        |
	 *       +--------+
	 *           B
	 *       ^    ^
	 *       |    |
	 *       |    minRight
	 *       maxLeft
	 * 
	 * </pre>
	 * 
	 * @param a
	 *        first rectangle
	 * @param b
	 *        second rectangle
	 * @return
	 *         the intersecting rectangle or null
	 */
	public static Rectangle getIntersection(Rectangle a, Rectangle b) {

		int maxLeft = Math.max(a.x, b.x);
		int maxTop = Math.max(a.y, b.y);
		int minRight = Math.min(a.x + a.width, b.x + b.width);
		int minBottom = Math.min(a.y + a.height, b.y + b.height);

		if (minRight >= maxLeft || minBottom >= maxTop) {
			return new Rectangle(maxLeft, maxTop, minRight - maxLeft, minBottom - maxTop);
		}

		return null;
	}

	/**
	 * Retrieve the diagonal between two rectangle.
	 * 
	 * <pre>
	 * 
	 *       A   
	 *    +-------+      <-- minTop
	 *    |       | 
	 *    |  +----|---+ 
	 *    |  |    |   |
	 *    +------ +   |
	 *       |        |
	 *       +--------+  <-- maxBottom
	 *           B
	 *    ^           ^
	 *    |           |
	 *    |           maxRight
	 *    minLeft
	 * 
	 * </pre>
	 * 
	 * @param a
	 *        first rectangle
	 * @param b
	 *        second rectangle
	 * @return
	 *         the diagonal line or null
	 */
	public static Rectangle getUnion(Rectangle a, Rectangle b) {

		int minLeft = Math.min(a.x, b.x);
		int minTop = Math.min(a.y, b.y);
		int maxRight = Math.max(a.x + a.width, b.x + b.width);
		int maxBottom = Math.max(a.y + a.height, b.y + b.height);

		if (minLeft <= maxRight && minTop <= maxBottom) {
			return new Rectangle(minLeft, minTop, maxRight - minLeft, maxBottom - minTop);
		}

		return null;
	}

	/**
	 * Retrieve the longest diagonal between two rectangle.
	 * 
	 * <pre>
	 * 
	 * A
	 * 		+---+
	 * 		| * |
	 * 		+---+
	 * 			*
	 * 			 *
	 * 			 +---+
	 * 			 | * | B
	 * 			 +---+
	 * 
	 * </pre>
	 * 
	 * @param a
	 *        first rectangle
	 * @param b
	 *        second rectangle
	 * @return
	 *         the diagonal line or null
	 */
	public static Line getDiagonal(Rectangle a, Rectangle b) {

		Point pointA = PaintUtils.getMidPoint(a);
		Point pointB = PaintUtils.getMidPoint(b);

		return new Line(pointA, pointB);
	}

	/**
	 * Retrieve the ratio required to extends a line to a specific distance to
	 * reach the minimumDistance provided.
	 * 
	 * <pre>
	 * 
	 * Prerequisite: distance > dmin
	 * 
	 *      |------------ dmin ------------|
	 *   A  x----------------> B
	 *      |--- distance ---|--- delta ---|
	 *      
	 * let be:	delta = (dmin - distance)
	 * then:	k = delta / dmin
	 * 
	 * so:
	 * 		k = (dmin - distance) / dmin
	 * 
	 * we see that:
	 * 
	 * 		distance = 0	=>	k = 1.0
	 * 		distance = dmin	=>	k = 0.0
	 * 
	 *  => k is an amplification factor proportional to the inverse distance
	 * 
	 *  => we will reduce the factor amplitude by 3d, to get a smooth animation
	 *  => we apply the factor to horizontal distance => the nearest, the fastest
	 *  => we apply the factor to vertical distance   => the nearest, the fastest
	 * 
	 * </pre>
	 * 
	 * @param distance
	 *        the actual distance to normalize
	 * @param dmin
	 *        the minimum distance to respect
	 * @return
	 *         the ration to use to expand distance
	 */
	public static double getAmplificationRatio(double distance, double dmin) {
		return (dmin - distance) / dmin / 3d;
	}

	/**
	 * Retrieve the ratio required to reduce a line to a specific distance to
	 * reach the minimumDistance provided.
	 * 
	 * <pre>
	 * 
	 * Prerequisite: dmin < distance
	 * 
	 *       |---------- distance ----------|
	 *    A  x------------------------------>  B
	 *       |---- dmin ----|---- delta ----|
	 *      
	 * let be:	delta = (distance - dmin)
	 * then:	k = delta / distance
	 * 
	 * so:
	 * 		k = (distance - dmin) / distance
	 * 
	 * we see that:
	 * 
	 * 		distance = dmin		=>	k = 0.0
	 * 		distance = infinite	=>	k = 1.0
	 * 
	 * => k is a compression factor proportional to the distance
	 * 
	 * => we then reduce the factor amplitude by 3d, to get a smooth animation
	 * => we apply the factor to horizontal distance => the farest, the fastest
	 * => we apply the factor to vertical distance   => the farest, the fastest
	 * 
	 * </pre>
	 * 
	 * @param distance
	 *        the actual distance to normalize
	 * @param dmin
	 *        the minimum distance to respect
	 * @return
	 *         the ration to use to reduce distance
	 */
	public static double getReductionRatio(double distance, double dmin) {
		return (distance - dmin) / distance / 3d;
	}

	/**
	 * Retrieve the euclidian line length.
	 * 
	 * @param line
	 *        the line to mesure
	 * @return
	 *         the line norm (or line length)
	 */
	public static int getLength(Line line) {
		return getDistance(line.getSourcePoint(), line.getTargetPoint());
	}

	/**
	 * Retrieve the intersection point between a line and a rectangle.
	 * 
	 * @param line
	 *        the line
	 * @param rect
	 *        the rectangle
	 * @return
	 *         the intersection point or null
	 */
	public static Point getIntersection(Line line, Rectangle rect) {

		Point intersectionPoint = null;

		// Check intersection with top line
		Line topLine = new Line(rect.x, rect.y, rect.x + rect.width, rect.y);
		intersectionPoint = PaintUtils.getInsersection(line, topLine);
		if (intersectionPoint != null) {
			return intersectionPoint;
		}

		// Check intersection with bottom line
		Line bottomLine = new Line(rect.x, rect.y + rect.height, rect.x + rect.width, rect.y + rect.height);
		intersectionPoint = PaintUtils.getInsersection(line, bottomLine);
		if (intersectionPoint != null) {
			return intersectionPoint;
		}

		// Check intersection with left line
		Line leftLine = new Line(rect.x, rect.y, rect.x, rect.y + rect.height);
		intersectionPoint = PaintUtils.getInsersection(line, leftLine);
		if (intersectionPoint != null) {
			return intersectionPoint;
		}

		// Check intersection with right line
		Line rightLine = new Line(rect.x + rect.width, rect.y, rect.x + rect.width, rect.y + rect.height);
		intersectionPoint = PaintUtils.getInsersection(line, rightLine);
		if (intersectionPoint != null) {
			return intersectionPoint;
		}

		return null;
	}

	/**
	 * Draw an arrow at the end of the line.
	 * 
	 * @param g
	 *        the graphics to use
	 * @param line
	 *        the line to use
	 * @param arrowSize
	 *        the size of the arrow
	 */
	public static void drawArrowAtEnd(Graphics g, Line line, int arrowSize) {

		final double ARROW_ANGLE = Math.PI / 7.0; // PI/7 rad = 25°
		final double arrowAngle = Math.atan2(line.y2 - line.y1, line.x2 - line.x1);

		// Compute first arrow edge
		Point a = new Point();
		a.x = (int) (line.x2 - Math.cos(arrowAngle + ARROW_ANGLE) * arrowSize);
		a.y = (int) (line.y2 - Math.sin(arrowAngle + ARROW_ANGLE) * arrowSize);

		// Compute second arrow edge
		Point b = new Point();
		b.x = (int) (line.x2 - Math.cos(arrowAngle - ARROW_ANGLE) * arrowSize);
		b.y = (int) (line.y2 - Math.sin(arrowAngle - ARROW_ANGLE) * arrowSize);

		// Draw arrow
		g.drawLine(line.x2, line.y2, a.x, a.y);
		g.drawLine(line.x2, line.y2, b.x, b.y);
	}

	/**
	 * Retrieve a concept whose bounds include the point passed in argument
	 * 
	 * @param concepts
	 *        concept list to scan
	 * @param point
	 *        the point used to locate the concept
	 * @return
	 *         a concept or null
	 */
	public static Concept hitConcept(List<Concept> concepts, Point point) {

		ListIterator<Concept> iterator = concepts.listIterator(concepts.size());

		// Scan concepts in reverse order of the paint process
		while (iterator.hasPrevious()) {
			Concept concept = iterator.previous();
			if (concept.getBounds().contains(point)) {
				return concept;
			}
		}

		return null;
	}

	/**
	 * Retrieve a link whose bounds include the point passed in argument
	 * 
	 * @param links
	 *        link list to scan
	 * @param point
	 *        the point used to locate the link
	 * @param distance
	 *        the minimum distance used to consider a hit
	 * @return
	 *         a link or null
	 */
	public static ConceptLink hitLink(List<Concept> concepts, Point point, double distance) {

		for (Concept concept : concepts) {
			for (ConceptLink link : concept.getLinks()) {

				// Retrieve line between source/target concepts
				Line linkLine = PaintUtils.getLine(link.getSourceConcept().getBounds(), link.getTargetConcept().getBounds());

				// Check if distance between objects are enough
				if (linkLine != null && PaintUtils.getDistance(point, linkLine) < distance) {
					return link;
				}
			}
		}

		return null;
	}

	/**
	 * Convert a rectangle coordinates into view coordinates (adapted to a
	 * specific scale).
	 * 
	 * @param rect
	 *        the rectangle in original coordinates
	 * @param scaleFactor
	 *        the scale factor to apply
	 * @return
	 *         the rectangle in view coordinates
	 */
	public static Rectangle toViewCoordinates(Rectangle rect, double scaleFactor) {

		int x = (int) (rect.x * scaleFactor);
		int y = (int) (rect.y * scaleFactor);
		int width = (int) (rect.width * scaleFactor);
		int height = (int) (rect.height * scaleFactor);

		return new Rectangle(x, y, width, height);
	}

	/**
	 * Convert a rectangle view coordinates into original coordinates.
	 * 
	 * @param rect
	 *        the rectangle in view coordinates
	 * @param scaleFactor
	 *        the scale factor to apply
	 * @return
	 *         the rectangle original coordinates
	 */
	public static Rectangle toOriginalCoordinates(Rectangle rect, double scaleFactor) {

		int x = (int) (rect.x / scaleFactor);
		int y = (int) (rect.y / scaleFactor);
		int width = (int) (rect.width / scaleFactor);
		int height = (int) (rect.height / scaleFactor);

		return new Rectangle(x, y, width, height);
	}

	/**
	 * Convert a point view coordinates into original coordinates.
	 * 
	 * @param point
	 *        the point in view coordinates
	 * @param scaleFactor
	 *        the scale factor to apply
	 * @return
	 *         the point in original coordinates
	 */
	public static Point toOriginalCoordinates(Point point, double scaleFactor) {

		// Retrieve coordinates for current scale
		int x = (int) (point.x / scaleFactor);
		int y = (int) (point.y / scaleFactor);

		return new Point(x, y);
	}
}
