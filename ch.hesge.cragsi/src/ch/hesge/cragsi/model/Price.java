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

public class Price {

	// Private attributes
	private String category; // Categorie	
	private String libelle; // Libelle
	private String price; // Prix

	/**
	 * Default constructor
	 */
	public Price() {
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String toString() {
		return "type: Price, category: " + category + ", libelle: " + libelle + ", price: " + price;
	}
}
