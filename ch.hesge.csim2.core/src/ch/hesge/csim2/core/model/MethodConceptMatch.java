/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hesge.csim2.core.model;

/**
 * Represents a single match between a method and a concept.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class MethodConceptMatch {

	// Private attributes
	private int keyId;
	private int projectId;
	private int conceptId;
	private int sourceMethodId;
	private Concept concept;
	private SourceClass sourceClass;
	private SourceMethod sourceMethod;
	private double weight;

	/**
	 * Default constructor
	 */
	public MethodConceptMatch() {
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

	public int getSourceMethodId() {
		return sourceMethodId;
	}

	public void setSourceMethodId(int sourceMethodId) {
		this.sourceMethodId = sourceMethodId;
	}

	public int getConceptId() {
		return conceptId;
	}

	public void setConceptId(int conceptId) {
		this.conceptId = conceptId;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public Concept getConcept() {
		return concept;
	}

	public void setConcept(Concept concept) {
		this.concept = concept;
	}

	public SourceClass getSourceClass() {
		return sourceClass;
	}

	public void setSourceClass(SourceClass sourceClass) {
		this.sourceClass = sourceClass;
	}

	public SourceMethod getSourceMethod() {
		return sourceMethod;
	}

	public void setSourceMethod(SourceMethod sourceMethod) {
		this.sourceMethod = sourceMethod;
	}
}
