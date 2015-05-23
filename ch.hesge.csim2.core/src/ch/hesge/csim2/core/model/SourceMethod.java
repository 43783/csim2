/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hesge.csim2.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a class method within a source class.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class SourceMethod {

	// Private attributes
	private int keyId;
	private int classId;
	private String filename;
	private String name;
	private String classname;
	private String signature;
	private String returnType;
	private List<SourceParameter> parameters;
	private List<SourceReference> references;

	// Local variables are never persisted on database 
	// See SourceVariable class comment.
	private List<SourceVariable> variables;

	/**
	 * Default constructor
	 */
	public SourceMethod() {
		parameters = new ArrayList<>();
		references = new ArrayList<>();
		variables = new ArrayList<>();
	}

	public int getKeyId() {
		return keyId;
	}

	public void setKeyId(int keyId) {
		this.keyId = keyId;
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

	public int getClassId() {
		return classId;
	}

	public void setClassId(int classId) {
		this.classId = classId;
	}

	public String getClassName() {
		return classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public List<SourceParameter> getParameters() {
		return parameters;
	}

	// Local variables are never persisted on database 
	// See SourceVariable class comment.
	public List<SourceVariable> getVariables() {
		return variables;
	}

	public List<SourceReference> getReferences() {
		return references;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
}
