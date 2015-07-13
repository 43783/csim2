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
	private StemMethod parent;
	private StemMethodType stemType;
	private SourceReferenceOrigin refOrigin;
	private List<StemMethod> parts;
	private List<StemMethod> parameters;
	private List<StemMethod> parameterTypes;
	private List<StemMethod> references;
	private List<StemMethod> referenceTypes;

	/**
	 * Default constructor
	 */
	public StemMethod() {
		parts = new ArrayList<>();
		parameters = new ArrayList<>();
		parameterTypes = new ArrayList<>();
		references = new ArrayList<>();
		referenceTypes = new ArrayList<>();
	}

	/**
	 * Parameterized constructor
	 */
	public StemMethod(Project project, StemMethod parent, SourceMethod sourceMethod, String term, StemMethodType stemType, SourceReferenceOrigin refOrigin) {

		this.projectId = project.getKeyId();
		this.parent = parent;
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

	public StemMethod getParent() {
		return parent;
	}
	
	public List<StemMethod> getParts() {
		return parts;
	}

	public List<StemMethod> getParameters() {
		return parameters;
	}

	public List<StemMethod> getParameterTypes() {
		return parameterTypes;
	}

	public List<StemMethod> getReferences() {
		return references;
	}

	public List<StemMethod> getReferenceTypes() {
		return referenceTypes;
	}
}
