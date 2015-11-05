/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hesge.cragsi.model;

import java.util.Date;

/**
 * Represents a domain object.
 * 
 * Copyright HEG Geneva 2015, Switzerland
 * 
 * @author Eric Harth
 */

public class Funding {

	// Private attributes
	private String id; // id	
	private String hessoId; // N° HESSO
	private Date date; // Date
	private String name; // Nom du partenaire
	private String amount; // Montant

	/**
	 * Default constructor
	 */
	public Funding() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getHessoId() {
		return hessoId;
	}

	public void setHessoId(String hessoId) {
		this.hessoId = hessoId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
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
}
