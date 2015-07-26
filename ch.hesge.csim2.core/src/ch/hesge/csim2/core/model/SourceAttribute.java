/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hesge.csim2.core.model;

/**
 * Represents a class attribute within sources project.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class SourceAttribute {

	// Private attributes
	private int keyId;
	private int classId;
	private String name;
	private String type;
	private SourceClass sourceClass;

	/**
	 * Default constructor
	 */
	public SourceAttribute() {
	}

	public int getKeyId() {
		return keyId;
	}

	public void setKeyId(int keyId) {
		this.keyId = keyId;
	}

	public int getClassId() {
		return classId;
	}

	public void setClassId(int classId) {
		this.classId = classId;
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

	public SourceClass getSourceClass() {
		return sourceClass;
	}

	public void setSourceClass(SourceClass sourceClass) {
		this.sourceClass = sourceClass;
	}
}
