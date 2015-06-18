package ch.hesge.csim2.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class MethodTermVector {

	// Private attributes
	private Vector<Double> values;
	private List<StemMethod> stems;

	public MethodTermVector(int size) {
		values = new Vector<>(size);
		stems = new ArrayList<>(size);
	}

	public double getValue(int i) {
		return values.get(i);
	}

	public void setValue(int i, double value) {
		values.set(i, value);
	}

	public StemMethod getStem(int i) {
		return stems.get(i);
	}

	public void setStem(int i, StemMethod stem) {
		stems.set(i, stem);
	}

	public List<StemMethod> getStems() {
		return stems;
	}
}
