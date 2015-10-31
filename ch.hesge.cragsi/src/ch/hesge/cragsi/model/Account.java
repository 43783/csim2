/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hesge.cragsi.model;


/**
 * Represents a domain object.
 * 
 * Copyright HEG Geneva 2015, Switzerland
 * 
 * @author Eric Harth
 */

public class Account {

	// Private attributes
	private String keyId; // id
	
	private String code; // code
	private String name; // name
	private String type; // type

	/**
	 * Default constructor
	 */
	public Account() {
	}

	public String getKeyId() {
		return keyId;
	}

	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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
	
	public String toString() {
		return "type: Account, keyId: " + keyId + ", name: " + name + ", code: " + code + ", type: " + type;
	}
}
