/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hesge.csim2.core.utils;

/**
 * Represents a turtle tripletin an ontology.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class TurtleTriplet {

	// Private attributes
	private String subject;
	private String predicate;
	private String object;
	private String lang;

	/**
	 * Default constructor
	 */
	public TurtleTriplet(String subject, String predicate, String object, String lang) {
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
		this.lang = lang;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getPredicate() {
		return predicate;
	}

	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}
	
	public String toString() {
		return "triplet subject:" + subject + ", predicate: " + predicate + ", object: " + object + ", lang:" + lang;
	}
}
