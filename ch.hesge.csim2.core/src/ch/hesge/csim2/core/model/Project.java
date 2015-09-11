/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hesge.csim2.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a project within the CSIM2 application
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class Project {

	// Private attributes
	private int					keyId;
	private String				name;
	private List<Scenario>		scenarios;
	private List<Ontology>		ontologies;
	private List<SourceClass>	sourceClasses;

	/**
	 * Default constructor
	 */
	public Project() {
		scenarios = new ArrayList<>();
		ontologies = new ArrayList<>();
		sourceClasses = new ArrayList<>();
	}

	public int getKeyId() {
		return keyId;
	}

	public void setKeyId(int keyId) {
		this.keyId = keyId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Scenario> getScenarios() {
		return scenarios;
	}

	public List<Ontology> getOntologies() {
		return ontologies;
	}

	public List<SourceClass> getSourceClasses() {
		return sourceClasses;
	}
}
