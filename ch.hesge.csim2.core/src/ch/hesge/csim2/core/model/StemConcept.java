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
	private Project project;
	private Concept concept;
	private StemConcept parent;
	private StemConceptType stemType;

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

		this();

		this.project = project;
		this.parent = parent;
		this.concept = concept;
		this.term = term;
		this.stemType = stemType;
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

	public int getConceptId() {
		return conceptId;
	}

	public void setConceptId(int conceptId) {
		this.conceptId = conceptId;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Concept getConcept() {
		return concept;
	}

	public void setConcept(Concept concept) {
		this.concept = concept;
	}

	public StemConcept getParent() {
		return parent;
	}

	public void setParent(StemConcept parent) {
		this.parent = parent;
	}

	public StemConceptType getStemType() {
		return stemType;
	}

	public void setStemType(StemConceptType stemType) {
		this.stemType = stemType;
	}

	public List<StemConcept> getParts() {
		return parts;
	}

	public void setParts(List<StemConcept> parts) {
		this.parts = parts;
	}

	public List<StemConcept> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<StemConcept> attributes) {
		this.attributes = attributes;
	}

	public List<StemConcept> getClasses() {
		return classes;
	}

	public void setClasses(List<StemConcept> classes) {
		this.classes = classes;
	}

	public List<StemConcept> getAttributeIdentifiers() {
		return attributeIdentifiers;
	}

	public void setAttributeIdentifiers(List<StemConcept> attributeIdentifiers) {
		this.attributeIdentifiers = attributeIdentifiers;
	}

	public List<StemConcept> getClassIdentifiers() {
		return classIdentifiers;
	}

	public void setClassIdentifiers(List<StemConcept> classIdentifiers) {
		this.classIdentifiers = classIdentifiers;
	}
}
