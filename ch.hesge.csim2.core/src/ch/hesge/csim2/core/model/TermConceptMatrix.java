package ch.hesge.csim2.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class TermConceptMatrix {

	// Private attributes
	private int rowCount;
	private int colCount;
	private double[][] values;
	private List<StemConcept>[][] stems;

	public TermConceptMatrix(int rows, int cols) {
		
		this.rowCount = rows;
		this.colCount = cols;
		
		values = new double[rows][cols];
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				values[i][j] = 0d;
				stems[i][j]  = new ArrayList<>();
			}
		}
	}
	
	public int getRowCount() {
		return rowCount;
	}
	
	public int getColCount() {
		return colCount;
	}

	public Vector<Double> getRowValues(int row) {
		Vector<Double> rowValues = new Vector<>();
		for (int j = 0; j < colCount; j++) {
			rowValues.add(values[row][j]);
		}
		return rowValues;
	}
	
	public void setRowValues(int row, Vector<Double> rowValues) {
		for (int j = 0; j < colCount; j++) {
			values[row][j] = rowValues.get(j);
		}
	}

	public void setValue(int row, int col, double value) {
		values[row][col] = value;
	}
	
	public double getValue(int row, int col) {
		return values[row][col];
	}

	public void addValue(int row, int col, double value) {
		values[row][col] = values[row][col] + value;
	}

	public void addStem(int row, int col, StemConcept stem) {
		stems[row][col].add(stem);
	}

	public void addAllStems(int row, int col, List<StemConcept> stems) {
		(this.stems[row][col]).addAll(stems);
	}

	public List<StemConcept> getStems(int row, int col) {
		return stems[row][col];
	}
}
