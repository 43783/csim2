/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hesge.csim2.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single match between a method and a concept.
 * this class is never persisted on database.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class MethodConceptMatch {

	// Private attributes
	private int keyId;
	private Project project;
	private Concept concept;
	private SourceClass sourceClass;
	private SourceMethod sourceMethod;
	private List<StemConcept> stemConcepts;
	private List<StemMethod> stemMethods;
	private double weight;

	/**
	 * Default constructor
	 */
	public MethodConceptMatch() {
		stemConcepts = new ArrayList<>();
		stemMethods = new ArrayList<>();
	}

	public int getKeyId() {
		return keyId;
	}

	public void setKeyId(int keyId) {
		this.keyId = keyId;
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

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public List<StemConcept> getStemConcepts() {
		return stemConcepts;
	}

	public List<StemMethod> getStemMethods() {
		return stemMethods;
	}
}
