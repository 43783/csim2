/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hesge.csim2.core.model;

import java.util.ArrayList;
import java.util.List;

import ch.hesge.csim2.core.utils.SimpleMatrix;
import ch.hesge.csim2.core.utils.SimpleVector;

/**
 * Represents a scenario time series describing trace evolution from the concept
 * point of view.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class TimeSeries {

	// Private attributes
	private Project project;
	private Scenario scenario;
	private List<Concept> concepts;
	private SimpleVector occurrences;
	private SimpleMatrix matrix;

	/**
	 * Default constructor
	 */
	public TimeSeries() {
		concepts = new ArrayList<>();
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Scenario getScenario() {
		return scenario;
	}

	public void setScenario(Scenario scenario) {
		this.scenario = scenario;
	}

	public List<Concept> getTraceConcepts() {
		return concepts;
	}

	public void setTraceConcepts(List<Concept> concepts) {
		this.concepts = concepts;
	}

	public SimpleMatrix getTraceMatrix() {
		return matrix;
	}

	public void setTraceMatrix(SimpleMatrix matrix) {
		this.matrix = matrix;
	}

	public SimpleVector getOccurrences() {
		return occurrences;
	}

	public void setOccurrences(SimpleVector occurrences) {
		this.occurrences = occurrences;
	}

}
