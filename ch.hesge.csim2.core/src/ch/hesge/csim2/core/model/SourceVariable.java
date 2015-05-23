/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hesge.csim2.core.model;

/**
 * Represents a local variable within a source method.
 * 
 * Warning:
 * 
 * this class is never persisted on database. Its only purpose is to allow
 * source code analyzer to detect if a reference is related to local variable,
 * parameters or class attribute.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class SourceVariable {

	// Private attributes
	private int keyId;
	private int methodId;
	private String name;
	private String type;

	/**
	 * Default constructor
	 */
	public SourceVariable() {
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
}
