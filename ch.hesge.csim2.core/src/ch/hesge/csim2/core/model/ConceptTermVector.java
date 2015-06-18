package ch.hesge.csim2.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


public class ConceptTermVector {

	// Private attributes
	private Vector<Double> values;
	private List<StemConcept> stems;

	public ConceptTermVector(int size) {
		values = new Vector<>(size);
		stems = new ArrayList<>(size);
	}

	public double getValue(int i) {
		return values.get(i);
	}

	public void setValue(int i, double value) {
		values.set(i, value);
	}
	
	public void addValue(int i, double value) {
		values.set(i, values.get(i) + value);
	}

	public void addStem(StemConcept stem) {
		stems.add(stem);
	}

	public List<StemConcept> getStems() {
		return stems;
	}
}
