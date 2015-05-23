package ch.hesge.csim2.ui.utils;

import java.awt.Point;

/**
 * This class represents a simple line data.
 */

public class Line {

	// Public attributes
	public int x1;
	public int y1;
	public int x2;
	public int y2;
	public int width;
	public int height;

	/**
	 * Default constructor.
	 */
	public Line() {
		super();
	}

	/**
	 * Constructs a line using the specifed coordinates.
	 *
	 * @param x1 first X coordinate
	 * @param y1 first Y coordinate
	 * @param x2 second X coordinate
	 * @param y2 second Y coordinate
	 */
	public Line(int x1, int y1, int x2, int y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.width = x2 - x1;
		this.height = y2 - y2;
	}

	/**
	 * Constructs a line using the specifed points.
	 *
	 * @param p1 first point
	 * @param p2 second point
	 */
	public Line(Point p1, Point p2) {
		this.x1 = p1.x;
		this.y1 = p1.y;
		this.x2 = p2.x;
		this.y2 = p2.y;
		this.width = p2.x - p1.x;
		this.height = p2.y - p1.y;
	}

	/**
	 * Constructs a line using coordinates from the specified line.
	 *
	 * @param line line to process
	 */
	public Line(Line line) {
		this.x1 = line.x1;
		this.y1 = line.y1;
		this.x2 = line.x2;
		this.y2 = line.y2;
		this.width = line.width;
		this.height = line.height;
	}

	/**
	 * Gets first point.
	 */
	public Point getSourcePoint() {
		return new Point(this.x1, this.y1);
	}
	
	/**
	 * Gets second point.
	 */
	public Point getTargetPoint() {
		return new Point(this.x2, this.y2);
	}
}
