/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hesge.csim2.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a class within sources project.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class SourceClass {

	// Private attributes
	private int keyId;
	private int projectId;
	private int superClassId;
	private String filename;
	private String name;
	private String type;
	private String superclassName;
	private Project project;
	private SourceClass superclass;
	private List<SourceAttribute> attributes;
	private List<SourceMethod> methods;
	private List<SourceClass> subclasses;

	/**
	 * Default constructor
	 */
	public SourceClass() {
		attributes = new ArrayList<>();
		methods = new ArrayList<>();
		subclasses = new ArrayList<>();
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

	public int getSuperClassId() {
		return superClassId;
	}

	public void setSuperClassId(int superClassId) {
		this.superClassId = superClassId;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public String getSuperClassName() {
		return superclassName;
	}

	public void setSuperClassName(String superClassName) {
		this.superclassName = superClassName;
	}

	public SourceClass getSuperClass() {
		return superclass;
	}

	public void setSuperClass(SourceClass superclass) {
		this.superclass = superclass;
	}

	public List<SourceClass> getSubClasses() {
		return subclasses;
	}

	public List<SourceAttribute> getAttributes() {
		return attributes;
	}

	public List<SourceMethod> getMethods() {
		return methods;
	}
}
