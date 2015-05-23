/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hesge.csim2.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an ontology with a collection of concepts.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class Ontology {

	// Private attributes
	private int				keyId;
	private int				projectId;
	private String			name;
	private Project			project;
	private List<Concept>	concepts;

	/**
	 * Default constructor
	 */
	public Ontology() {
		concepts = new ArrayList<Concept>();
	}

	public int getKeyId() {
		return keyId;
	}

	public void setKeyId(int keyId) {
		this.keyId = keyId;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public List<Concept> getConcepts() {
		return concepts;
	}
}
