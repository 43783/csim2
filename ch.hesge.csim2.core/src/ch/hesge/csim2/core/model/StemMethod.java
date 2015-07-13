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
	private Project project;
	private SourceMethod method;
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

		this();

		this.projectId = project.getKeyId();
		this.project = project;
		this.parent = parent;
		this.method = sourceMethod;
		this.term = term;
		this.stemType = stemType;
		this.refOrigin = refOrigin;
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

	public int getSourceMethodId() {
		return methodId;
	}

	public void setSourceMethodId(int methodId) {
		this.methodId = methodId;
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

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public SourceMethod getSourceMethod() {
		return method;
	}

	public void setSourceMethod(SourceMethod method) {
		this.method = method;
	}

	public StemMethod getParent() {
		return parent;
	}

	public void setParent(StemMethod parent) {
		this.parent = parent;
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

	public List<StemMethod> getParts() {
		return parts;
	}

	public void setParts(List<StemMethod> parts) {
		this.parts = parts;
	}

	public List<StemMethod> getParameters() {
		return parameters;
	}

	public void setParameters(List<StemMethod> parameters) {
		this.parameters = parameters;
	}

	public List<StemMethod> getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(List<StemMethod> parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public List<StemMethod> getReferences() {
		return references;
	}

	public void setReferences(List<StemMethod> references) {
		this.references = references;
	}

	public List<StemMethod> getReferenceTypes() {
		return referenceTypes;
	}

	public void setReferenceTypes(List<StemMethod> referenceTypes) {
		this.referenceTypes = referenceTypes;
	}
}
