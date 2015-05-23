/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hesge.csim2.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a stem identified during source code analysis.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class StemMethod {

	// Private attributes
	private int keyId;
	private int projectId;
	private int parentId;
	private int methodId;
	private String term;
	private double weight;
	private StemMethodType stemType;
	private SourceReferenceOrigin refOrigin;
	private List<StemMethod> children;

	/**
	 * Default constructor
	 */
	public StemMethod() {
		children = new ArrayList<>();
	}

	/**
	 * Parameterized constructor
	 */
	public StemMethod(Project project, StemMethod parent, SourceMethod sourceMethod, String term, StemMethodType stemType, SourceReferenceOrigin refOrigin) {

		this.projectId = project.getKeyId();
		this.parentId = parent == null ? -1 : parent.getKeyId();
		this.term = term;
		this.stemType = stemType;
		this.refOrigin = refOrigin;
		this.methodId = sourceMethod == null ? -1 : sourceMethod.getKeyId();
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

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public StemMethodType getStemType() {
		return stemType;
	}

	public void setStemType(StemMethodType stemType) {
		this.stemType = stemType;
	}

	public SourceReferenceOrigin getRefOrigin() {
		return refOrigin;
	}

	public void setRefOrigin(SourceReferenceOrigin refOrigin) {
		this.refOrigin = refOrigin;
	}

	public int getSourceMethodId() {
		return methodId;
	}

	public void setSourceMethodId(int methodId) {
		this.methodId = methodId;
	}

	public List<StemMethod> getChildren() {
		return children;
	}

}
