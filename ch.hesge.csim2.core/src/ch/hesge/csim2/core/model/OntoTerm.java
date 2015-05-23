/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hesge.csim2.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents term association to concept or attribut.
 * 
 * <pre>
 * WARNING: this class is not persisted in database. 
 * It is only used while parsing and ontology.
 * </pre>
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class OntoTerm {

	// Private attributes
	private String				name;
	private List<String>		identifiers;
	private Concept				ownerConcept;
	private ConceptAttribute	ownerAttribute;

	/**
	 * Default constructor
	 */
	public OntoTerm() {
		identifiers = new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getIdentifiers() {
		return identifiers;
	}

	public Concept getOwnerConcept() {
		return ownerConcept;
	}

	public void setOwnerConcept(Concept ownerConcept) {
		this.ownerConcept = ownerConcept;
	}

	public ConceptAttribute getOwnerAttribute() {
		return ownerAttribute;
	}

	public void setOwnerAttribute(ConceptAttribute ownerAttribute) {
		this.ownerAttribute = ownerAttribute;
	}
}
