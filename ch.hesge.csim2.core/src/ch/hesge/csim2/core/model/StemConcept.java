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
	private StemConcept parent;
	private List<StemConcept> parts;
	private List<StemConcept> attributes;
	private List<StemConcept> classes;
	private List<StemConcept> attributeIdentifiers;
	private List<StemConcept> classIdentifiers;

	/**
	 * Default constructor
	 */
	public StemConcept() {
		parts = new ArrayList<>();
		attributes = new ArrayList<>();
		classes = new ArrayList<>();
		attributeIdentifiers = new ArrayList<>();
		classIdentifiers = new ArrayList<>();
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

		parts = new ArrayList<>();
		attributes = new ArrayList<>();
		classes = new ArrayList<>();
		attributeIdentifiers = new ArrayList<>();
		classIdentifiers = new ArrayList<>();
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
	
	public StemConcept getParent() {
		return parent;
	}

	public void setParent(StemConcept parent) {
		this.parent = parent;
	}

	public List<StemConcept> getParts() {
		return parts;
	}

	public List<StemConcept> getAttributes() {
		return attributes;
	}

	public List<StemConcept> getClasses() {
		return classes;
	}

	public List<StemConcept> getAttributeIdentifiers() {
		return attributeIdentifiers;
	}

	public List<StemConcept> getClassIdentifiers() {
		return classIdentifiers;
	}
}
