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

public class Contributor {

	// Private attributes
	private String keyId; // IdCollaborateur
	private String date; // Date
	private String hessoId; // N° HESSO
	private String gestpacId; // idGestpac
	private String firstname; // Prénom
	private String lastname; // Nom
	private String classe; // Classe
	private String rate; // Taux

	/**
	 * Default constructor
	 */
	public Contributor() {
	}

	public String getKeyId() {
		return keyId;
	}

	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getHessoId() {
		return hessoId;
	}

	public void setHessoId(String hessoId) {
		this.hessoId = hessoId;
	}

	public String getGestpacId() {
		return gestpacId;
	}

	public void setGestpacId(String gestpacId) {
		this.gestpacId = gestpacId;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getClasse() {
		return classe;
	}

	public void setClasse(String classe) {
		this.classe = classe;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String toString() {
		return "type: Contributor, keyId: " + keyId + ", date: " + date + ", hessoId: " + hessoId + ", gestpacId: " + gestpacId + ", firstname: " + firstname + ", lastname: " + lastname + ", classe: " + classe + ", rate: " + rate;
	}
}
