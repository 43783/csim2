package ch.hesge.csim2.core.utils;

import java.util.Arrays;

import org.apache.commons.math3.linear.RealVector;

public class SimpleVector {

	// Private attribute
	private int size;
	private double values[];

	/**
	 * Construct a vector of zeroes.
	 *
	 * @param size
	 *        size of the vector.
	 */
	public SimpleVector(int size) {
		this.size = size;
		this.values = new double[size];
	}

	/**
	 * Construct a vector with preset values.
	 *
	 * @param size
	 *        size of the vector
	 * @param initialValue
	 *        all entries will be set with this value.
	 */
	public SimpleVector(int size, double initialValue) {
		this.size = size;
		this.values = new double[size];
		Arrays.fill(values, initialValue);
	}

	/**
	 * Get vector dimension.
	 * 
	 * @return
	 *         the vector dimension.
	 */
	public int getDimension() {
		return size;
	}

	/**
	 * Get a single coordinate value.
	 * 
	 * @param i
	 *        coordinate.
	 * @return
	 *         a scalar value
	 * @exception ArrayIndexOutOfBoundsException
	 */
	public double getValue(int i) {
		return values[i];
	}

	/**
	 * Add a scalar to a single vector coordinate.
	 * 
	 * @param i
	 *        coordinate
	 * @param value
	 *        the scalar value to add
	 * @exception ArrayIndexOutOfBoundsException
	 */
	public void addValue(int i, double value) {
		values[i] += value;
	}

	/**
	 * Set a single coordinate value.
	 * 
	 * @param i
	 *        coordinate index
	 * @param value
	 *        a scalar value
	 * @exception ArrayIndexOutOfBoundsException
	 */
	public void setValue(int i, double value) {
		values[i] = value;
	}

	/**
	 * Compute the sum of this vector and {@code v}.
	 * Returns a new vector. Does not change instance data.
	 *
	 * @param v
	 *        Vector to be added.
	 * @return {@code this} + {@code v}.
	 * @throws IllegalArgumentException
	 *         if {@code v} is not the same size as {@code this} vector.
	 */
	public SimpleVector add(SimpleVector v) throws IllegalArgumentException {

		checkVectorDimensions(this, v);
		SimpleVector result = new SimpleVector(size);

		for (int i = 0; i < size; i++) {
			result.values[i] = values[i] + v.values[i];
		}

		return result;
	}

	/**
	 * Subtract {@code v} from this vector.
	 * Returns a new vector. Does not change instance data.
	 *
	 * @param v
	 *        Vector to be subtracted.
	 * @return {@code this} - {@code v}.
	 * @throws IllegalArgumentException
	 *         if {@code v} is not the same size as {@code this} vector.
	 */
	public SimpleVector substract(SimpleVector v) throws IllegalArgumentException {

		checkVectorDimensions(this, v);
		SimpleVector result = new SimpleVector(size);

		for (int i = 0; i < size; i++) {
			result.values[i] = values[i] - v.values[i];
		}

		return result;
	}

	/**
	 * Element-by-element addition.
	 *
	 * @param scalar
	 *        the scalar to add.
	 * @return a vector containing this[i] + scaéar for all i.
	 * @throws IllegalArgumentException
	 *         if {@code v} is not the same size as {@code this} vector.
	 */
	public SimpleVector ebeAdd(double scalar) throws IllegalArgumentException {

		SimpleVector result = new SimpleVector(size);

		for (int i = 0; i < size; i++) {
			result.values[i] = values[i] + scalar;
		}

		return result;
	}

	/**
	 * Element-by-element multiplication.
	 *
	 * @param v
	 *        Vector by which instance elements must be multiplied
	 * @return a vector containing this[i] * v[i] for all i.
	 * @throws IllegalArgumentException
	 *         if {@code v} is not the same size as {@code this} vector.
	 */
	public SimpleVector ebeMultiply(SimpleVector v) throws IllegalArgumentException {

		checkVectorDimensions(this, v);
		SimpleVector result = new SimpleVector(size);

		for (int i = 0; i < size; i++) {
			result.values[i] = values[i] * v.values[i];
		}

		return result;
	}

	/**
	 * Element-by-element division.
	 *
	 * @param v
	 *        Vector by which instance elements must be divided.
	 * @return a vector containing this[i] / v[i] for all i.
	 * @throws IllegalArgumentException
	 *         if {@code v} is not the same size as {@code this} vector.
	 */
	public SimpleVector ebeDivide(SimpleVector v) throws IllegalArgumentException {

		checkVectorDimensions(this, v);
		SimpleVector result = new SimpleVector(size);

		for (int i = 0; i < size; i++) {
			result.values[i] = values[i] / v.values[i];
		}

		return result;
	}

	/**
	 * Compute the dot product of this vector with {@code v}.
	 *
	 * @param v
	 *        Vector with which dot product should be computed
	 * @return the scalar dot product between this instance and {@code v}.
	 * @throws IllegalArgumentException
	 *         if {@code v} is not the same size as {@code this} vector.
	 */
	public double dotProduct(SimpleVector v) throws IllegalArgumentException {

		double dot = 0d;

		for (int i = 0; i < size; i++) {
			dot += values[i] * v.values[i];
		}

		return dot;
	}

	/**
	 * Returns the L<sub>2</sub> norm of the vector.
	 * <p>
	 * The L<sub>2</sub> norm is the root of the sum of the squared elements.
	 * </p>
	 *
	 * @return the norm.
	 * @see #getL1Norm()
	 * @see #getDistance(SimpleVector)
	 */
	public double getNorm() {

		double sum = 0d;

		for (int i = 0; i < size; i++) {
			sum += values[i] * values[i];
		}

		return Math.sqrt(sum);
	}

	/**
	 * Returns the L<sub>1</sub> norm of the vector.
	 * <p>
	 * The L<sub>1</sub> norm is the sum of the absolute values of the elements.
	 * </p>
	 *
	 * @return the norm.
	 * @see #getNorm()
	 * @see #getL1Distance(RealVector)
	 */
	public double getL1Norm() {

		double sum = 0d;

		for (int i = 0; i < size; i++) {
			sum += Math.abs(values[i]);
		}

		return sum;
	}

	/**
	 * Distance between two vectors.
	 * <p>
	 * This method computes the distance consistent with the L<sub>2</sub> norm,
	 * i.e. the square root of the sum of element differences, or Euclidean
	 * distance.
	 * </p>
	 *
	 * @param v
	 *        Vector to which distance is requested.
	 * @return the distance between two vectors.
	 * @throws IllegalArgumentException
	 *         if {@code v} is not the same size as {@code this} vector.
	 * @see #getL1Distance(RealVector)
	 * @see #getNorm()
	 */
	public double getDistance(SimpleVector v) throws IllegalArgumentException {

		checkVectorDimensions(this, v);
		double sum = 0d;

		for (int i = 0; i < size; i++) {
			double delta = values[i] - v.values[i];
			sum += delta * delta;
		}

		return Math.sqrt(sum);
	}

	/**
	 * Distance between two vectors.
	 * <p>
	 * This method computes the distance consistent with L<sub>1</sub> norm,
	 * i.e. the sum of the absolute values of the elements differences.
	 * </p>
	 *
	 * @param v
	 *        Vector to which distance is requested.
	 * @return the distance between two vectors.
	 * @throws IllegalArgumentException
	 *         if {@code v} is not the same size as {@code this} vector.
	 */
	public double getL1Distance(SimpleVector v) throws IllegalArgumentException {

		checkVectorDimensions(this, v);
		double sum = 0d;

		for (int i = 0; i < size; i++) {
			double delta = values[i] - v.values[i];
			sum += Math.abs(delta);
		}

		return sum;
	}

	/**
	 * Computes the cosine of the angle between this vector and the
	 * argument.
	 *
	 * @param v
	 *        Vector.
	 * @return the cosine of the angle between this vector and {@code v}.
	 * @throws IllegalArgumentException
	 *         if the dimensions of {@code this} and {@code v} do not match or
	 *         if {@code this} or {@code v} is the null
	 *         vector
	 */
	public double cosine(SimpleVector v) throws IllegalArgumentException {

		final double norm = getNorm();
		final double vNorm = v.getNorm();

		if (norm == 0 || vNorm == 0) {
			throw new IllegalArgumentException("Vector norm zero not allowed.");
		}

		return dotProduct(v) / (norm * vNorm);
	}

	/**
	 * Check if any coordinate of this vector is {@code NaN}.
	 *
	 * @return {@code true} if any coordinate of this vector is {@code NaN},
	 *         {@code false} otherwise.
	 */
	public boolean isNaN() {

		for (int i = 0; i < size; i++) {
			if (Double.isNaN(values[i])) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Check whether any coordinate of this vector is infinite and none
	 * are {@code NaN}.
	 *
	 * @return {@code true} if any coordinate of this vector is infinite and
	 *         none are {@code NaN}, {@code false} otherwise.
	 */
	public boolean isInfinite() {

		if (isNaN()) {
			return false;
		}

		for (int i = 0; i < size; i++) {
			if (Double.isInfinite(values[i])) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Check if this vector as all coordinates to zero.
	 *
	 * @return {@code true} if any coordinate of this vector is zero and
	 *         {@code false} otherwise.
	 */
	public boolean isNullVector() {

		if (getNorm() == 0) {
			return true;
		}

		return false;
	}

	/**
	 * Check if vectors are compatible in size.
	 *
	 * @param a
	 *        left hand side vector.
	 * @param b
	 *        right hand side vector.
	 * @throws IllegalArgumentException
	 *         if the matrices are not compatible.
	 */
	private void checkVectorDimensions(SimpleVector a, SimpleVector b) {
		if (a.size != b.size || a.size != b.size) {
			throw new IllegalArgumentException("Vector dimension mismatch.");
		}
	}

}
