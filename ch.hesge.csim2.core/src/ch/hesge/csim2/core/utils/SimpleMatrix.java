package ch.hesge.csim2.core.utils;

import java.util.Arrays;

public class SimpleMatrix {

	// Private attributes
	private int rows;
	private int cols;
	private double[][] values;

	/**
	 * Construct a matrix with zero values.
	 * 
	 * @param rows
	 *        number of rows.
	 * @param rows
	 *        number of columns.
	 */
	public SimpleMatrix(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		this.values = new double[rows][cols];
	}

	/**
	 * Construct a matrix with initialized values.
	 * 
	 * @param rows
	 *        number of rows.
	 * @param rows
	 *        number of columns.
	 * @param initialValue
	 *        all entries will be set with this value.
	 */
	public SimpleMatrix(int rows, int cols, double initialValue) {
		this.rows = rows;
		this.cols = cols;
		this.values = new double[rows][cols];
		Arrays.fill(values, initialValue);
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
	 * Get a single cell value.
	 * 
	 * @param i
	 *        row index.
	 * @param j
	 *        column index.
	 * @return
	 *         the cell value
	 * @exception ArrayIndexOutOfBoundsException
	 */
	public double getValue(int i, int j) {
		return values[i][j];
	}

	/**
	 * Set a single cell value.
	 * 
	 * @param i
	 *        row index
	 * @param j
	 *        column index
	 * @param value
	 *        the cell value
	 * @exception ArrayIndexOutOfBoundsException
	 */
	public void setValue(int i, int j, double value) {
		values[i][j] = value;
	}

	/**
	 * Add a value to a single cell value.
	 * 
	 * @param i
	 *        row index
	 * @param j
	 *        column index
	 * @param scalar
	 *        the scalar value to add
	 * @exception ArrayIndexOutOfBoundsException
	 */
	public void addValue(int i, int j, double scalar) {
		values[i][j] += scalar;
	}

	/**
	 * Compute the sum of {@code this} and {@code m}.
	 *
	 * @param m
	 *        Matrix to be added.
	 * @return {@code this + m}.
	 * @throws IllegalArgumentException
	 *         if {@code m} is not the same
	 *         size as {@code this}.
	 */
	public SimpleMatrix add(SimpleMatrix m) throws IllegalArgumentException {

		checkMatrixDimensions(this, m);
		SimpleMatrix result = new SimpleMatrix(rows, cols);

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				result.values[i][j] = values[i][j] + m.values[i][j];
			}
		}

		return result;
	}

	/**
	 * Returns {@code this} minus {@code m}.
	 *
	 * @param m
	 *        Matrix to be subtracted.
	 * @return {@code this - m}
	 * @throws IllegalArgumentException
	 *         if {@code m} is not the same
	 *         size as {@code this}.
	 */
	public SimpleMatrix subtract(SimpleMatrix m) throws IllegalArgumentException {

		checkMatrixDimensions(this, m);
		SimpleMatrix result = new SimpleMatrix(rows, cols);

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				result.values[i][j] = values[i][j] - m.values[i][j];
			}
		}

		return result;
	}

	/**
	 * Returns the result of postmultiplying {@code this} by {@code m}.
	 *
	 * @param m
	 *        matrix to postmultiply by
	 * @return {@code this * m}
	 * @throws IllegalArgumentException
	 *         if {@code columnDimension(this) != rowDimension(m)}
	 */
	public SimpleMatrix multiply(SimpleMatrix m) throws IllegalArgumentException {

		checkMatrixMultiplicationDimensions(this, m);
		SimpleMatrix result = new SimpleMatrix(rows, cols);

		// Will hold a column of "m".
		double[] mColValues = new double[cols];

		// Multiply.
		for (int col = 0; col < m.cols; col++) {

			// Copy all elements of column "col" of "m" so that
			// will be in contiguous memory.
			for (int mRow = 0; mRow < cols; mRow++) {
				mColValues[mRow] = m.values[mRow][col];
			}

			for (int row = 0; row < rows; row++) {
				final double[] dataRow = values[row];
				double sum = 0;
				for (int i = 0; i < cols; i++) {
					sum += dataRow[i] * mColValues[i];
				}

				result.values[row][col] = sum;
			}
		}

		return result;
	}
	
	/**
	 * Returns {@code this} multiplied by the scalar {@code factor}.
	 *
	 * @param i
	 *        row index.
	 * @param j
	 *        column index.
	 * @param scalar
	 *        the scalar to multiply.
	 * @return {@code this * factor}
	 * @exception ArrayIndexOutOfBoundsException
	 */
	public SimpleMatrix multiply(double scalar) {

		SimpleMatrix result = new SimpleMatrix(rows, cols);

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				result.values[i][j] = values[i][j] * scalar;
			}
		}

		return result;
	}

	/**
	 * Returns {@code this} log, that is for each element of {@code this} a
	 * log10 is calculated element-by-element.
	 * @return log10({@code this})
	 */
	public SimpleMatrix log10() {

		SimpleMatrix result = new SimpleMatrix(rows, cols);

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				result.values[i][j] = Math.log10(values[i][j]);
			}
		}

		return result;
	}

	/**
	 * Returns the result of multiplying element-by-element {@code this} by
	 * {@code m}.
	 *
	 * @param m
	 *        matrix to multiply by
	 * @return {@code this * m}
	 * @throws IllegalArgumentException
	 *         if {@code columnDimension(this) != rowDimension(m)}
	 */
	public SimpleMatrix ebeMultiply(SimpleMatrix m) throws IllegalArgumentException {

		checkMatrixDimensions(this, m);
		SimpleMatrix result = new SimpleMatrix(rows, cols);

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				result.values[i][j] = values[i][j] * m.values[i][j];
			}
		}

		return result;
	}

	/**
	 * Returns the result of dividing element-by-element {@code this} by
	 * {@code m}.
	 *
	 * @param m
	 *        matrix to divide by
	 * @return {@code this * m}
	 * @throws IllegalArgumentException
	 *         if {@code columnDimension(this) != rowDimension(m)}
	 */
	public SimpleMatrix ebeDivide(SimpleMatrix m) throws IllegalArgumentException {

		checkMatrixDimensions(this, m);
		SimpleMatrix result = new SimpleMatrix(rows, cols);

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				result.values[i][j] = values[i][j] / m.values[i][j];
			}
		}

		return result;
	}

	/**
	 * Returns the entries in row number {@code row} as a vector. Row indices
	 * start at 0.
	 *
	 * @param row
	 *        row to be fetched.
	 * @return a row vector.
	 * @throws ArrayIndexOutOfBoundsException
	 *         if the specified row index is invalid.
	 */
	public SimpleVector getRowVector(int row) throws ArrayIndexOutOfBoundsException {

		SimpleVector result = new SimpleVector(cols);

		for (int i = 0; i < cols; ++i) {
			result.setValue(i, values[row][i]);
		}

		return result;
	}

	/**
	 * Sets the specified {@code row} of {@code this} matrix to the entries of
	 * the specified {@code vector}. Row indices start at 0.
	 *
	 * @param row
	 *        row to be set.
	 * @param vector
	 *        row vector to be copied (must have the same dimension of
	 *        current matrix column dimension).
	 * @throws ArrayIndexOutOfBoundsException
	 *         if the specified row index is invalid.
	 * @throws IllegalArgumentException
	 *         if the {@code vector} dimension
	 *         does not match the column dimension of {@code this} matrix.
	 */
	public void setRowVector(int row, SimpleVector v) throws ArrayIndexOutOfBoundsException, IllegalArgumentException {

		if (cols != v.getDimension()) {
			throw new IllegalArgumentException("Matrix and vector dimension mismatch.");
		}

		for (int i = 0; i < cols; ++i) {
			values[row][i] = v.getValue(i);
		}
	}

	/**
	 * Get the entries at the given column index as a vector. Column indices
	 * start at 0.
	 *
	 * @param column
	 *        column to be fetched.
	 * @return a column vector.
	 * @throws ArrayIndexOutOfBoundsException
	 *         if the specified column index is invalid
	 */
	public SimpleVector getColumnVector(int col) throws ArrayIndexOutOfBoundsException {

		SimpleVector result = new SimpleVector(rows, cols);

		for (int i = 0; i < rows; ++i) {
			result.setValue(i, values[i][col]);
		}

		return result;
	}

	/**
	 * Sets the specified {@code column} of {@code this} matrix to the entries
	 * of the specified {@code vector}. Column indices start at 0.
	 *
	 * @param col
	 *        column to be set.
	 * @param vector
	 *        column vector to be copied (must have the same number of
	 *        rows as the instance).
	 * @throws ArrayIndexOutOfBoundsException
	 *         if the specified column index is invalid.
	 * @throws IllegalArgumentException
	 *         if the {@code vector} dimension
	 *         does not match the row dimension of {@code this} matrix.
	 */
	public void setColumnVector(int col, SimpleVector v) throws ArrayIndexOutOfBoundsException, IllegalArgumentException {

		if (rows != v.getDimension()) {
			throw new IllegalArgumentException("Matrix and vector dimension mismatch.");
		}

		for (int i = 0; i < rows; ++i) {
			values[i][col] = v.getValue(i);
		}
	}

	/**
	 * Check if matrices are compatible in size.
	 *
	 * @param a
	 *        left hand side matrix.
	 * @param b
	 *        right hand side matrix.
	 * @throws IllegalArgumentException
	 *         if the matrices are not compatible.
	 */
	private void checkMatrixDimensions(SimpleMatrix a, SimpleMatrix b) {
		if (a.rows != b.rows || a.cols != b.cols) {
			throw new IllegalArgumentException("Matrix dimension mismatch.");
		}
	}

	/**
	 * Check if matrix are compatible in size for multiplication.
	 *
	 * @param a
	 *        lLeft hand side matrix.
	 * @param b
	 *        right hand side matrix.
	 * @throws IllegalArgumentException
	 *         if the matrices are not multiplication compatible.
	 */
	private void checkMatrixMultiplicationDimensions(SimpleMatrix a, SimpleMatrix b) {
		if (a.rows != b.rows || a.cols != b.cols) {
			throw new IllegalArgumentException("Matrix dimension mismatch.");
		}
	}
}
