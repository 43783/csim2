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

public class Funding {

	// Private attributes
	private String keyId; // id	
	private String hessoId; // N° HESSO
	private String date; // Date
	private String name; // Nom du partenaire
	private String amount; // Montant

	/**
	 * Default constructor
	 */
	public Funding() {
	}

	public String getKeyId() {
		return keyId;
	}

	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}

	public String getHessoId() {
		return hessoId;
	}

	public void setHessoId(String hessoId) {
		this.hessoId = hessoId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String toString() {
		return "type: Funding, keyId: " + keyId + ", hessoId: " + hessoId + ", date: " + date + ", name: " + name + ", amount: " + amount;
	}
}
