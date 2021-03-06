package ch.hesge.csim2.core.utils;

import java.util.HashSet;
import java.util.Set;

/**
 * This class allows storing/retrieving, for each cell, a set of objects.
 * The set can be retrieve by its coordinates (row, column).
 * 
 * @author Eric Harth
 *
 * @param <T>
 *        the type of objects stored in cell
 */
@SuppressWarnings("unchecked")
public class StemMatrix<T> {

	// Private attributes
	private int rows;
	private int cols;
	private Set<T>[][] entrySets;

	/**
	 * Construct a matrix allowing storing objects of class T.
	 * 
	 * @param rows
	 *        number of rows.
	 * @param rows
	 *        number of columns.
	 */
	public StemMatrix(int rows, int cols) {

		this.rows = rows;
		this.cols = cols;

		this.entrySets = new Set[rows][cols];

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				entrySets[i][j] = new HashSet<T>();
			}
		}
	}

	/**
	 * Get row dimension.
	 * 
	 * @return
	 *         the number of rows.
	 */
	public int getRowDimension() {
		return rows;
	}

	/**
	 * Get column dimension.
	 * 
	 * @return
	 *         the number of columns.
	 */
	public int getColumnDimension() {
		return cols;
	}

	/**
	 * Retrieve all entries stored in a cell.
	 * 
	 * @param i
	 *        row index.
	 * @param j
	 *        column index.
	 * @return
	 *         the set of elements
	 * @exception ArrayIndexOutOfBoundsException
	 */
	public Set<T> get(int i, int j) {
		return new HashSet<T>(entrySets[i][j]);
	}

	/**
	 * Put an element within a cell.
	 * 
	 * @param i
	 *        row index
	 * @param j
	 *        column index
	 * @param item
	 *        the item to store in cell
	 * @exception ArrayIndexOutOfBoundsException
	 */
	public void add(int i, int j, T item) {
		entrySets[i][j].add(item);
	}

}
