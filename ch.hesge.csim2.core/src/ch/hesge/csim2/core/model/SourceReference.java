/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hesge.csim2.core.model;

/**
 * Represents a reference within a method. Could be a reference to an attribute,
 * a parameter or a local variable.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class SourceReference {

	// Private attributes
	private int keyId;
	private int methodId;
	private String name;
	private String type;
	private SourceReferenceOrigin origin;

	/**
	 * Default constructor
	 */
	public SourceReference() {
	}

	public int getKeyId() {
		return keyId;
	}

	public void setKeyId(int keyId) {
		this.keyId = keyId;
	}

	public int getMethodId() {
		return methodId;
	}

	public void setMethodId(int methodId) {
		this.methodId = methodId;
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

	public SourceReferenceOrigin getOrigin() {
		return origin;
	}

	public void setOrigin(SourceReferenceOrigin origin) {
		this.origin = origin;
	}
}
