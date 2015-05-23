/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hesge.csim2.core.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

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
	private Map<Integer, Double> weightMap;
	private List<Vector<Integer>> traceVectors;

	/**
	 * Default constructor
	 */
	public TimeSeries() {
		concepts = new ArrayList<>();
		weightMap = new HashMap<>();
		traceVectors = new ArrayList<>();
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

	public List<Concept> getConcepts() {
		return concepts;
	}

	public Map<Integer, Double> getWeightMap() {
		return weightMap;
	}

	public List<Vector<Integer>> getTraceVectors() {
		return traceVectors;
	}
}
