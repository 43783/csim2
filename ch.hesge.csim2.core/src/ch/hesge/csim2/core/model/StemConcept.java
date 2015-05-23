/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hesge.csim2.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a stem identified during concept analysis.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class StemConcept {

	// Private attributes
	private int keyId;
	private int projectId;
	private int parentId;
	private int conceptId;
	private String term;
	private StemConceptType stemType;
	private List<StemConcept> children;

	/**
	 * Default constructor
	 */
	public StemConcept() {
		children = new ArrayList<>();
	}

	/**
	 * Parameterized constructor
	 */
	public StemConcept(Project project, StemConcept parent, Concept concept, String term, StemConceptType stemType) {

		this.projectId = project.getKeyId();
		this.parentId = parent == null ? -1 : parent.getKeyId();
		this.term = term;
		this.stemType = stemType;
		this.conceptId = concept == null ? -1 : concept.getKeyId();
		this.children = new ArrayList<>();
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

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public StemConceptType getStemType() {
		return stemType;
	}

	public void setStemType(StemConceptType stemType) {
		this.stemType = stemType;
	}

	public int getConceptId() {
		return conceptId;
	}

	public void setConceptId(int conceptId) {
		this.conceptId = conceptId;
	}
	
	public List<StemConcept> getChildren() {
		return children;
	}
}
