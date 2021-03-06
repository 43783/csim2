/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hesge.csim2.core.model;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a domain concept within an ontology.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class Concept {

	// Private attributes
	private int keyId;
	private int ontologyId;
	private String name;
	private Rectangle bounds;
	private boolean isAction;
	private Ontology ontology;
	private Concept superconcept;
	private double weight;
	private List<ConceptAttribute> attributes;
	private List<ConceptClass> classes;
	private List<ConceptLink> links;
	private List<Concept> subconcepts;
	private List<Concept> parts;

	/**
	 * Default constructor
	 */
	public Concept() {
		bounds = new Rectangle(100, 100, 100, 32);
		attributes = new ArrayList<>();
		classes = new ArrayList<>();
		links = new ArrayList<>();
		subconcepts = new ArrayList<>();
		parts = new ArrayList<>();
	}

	public int getKeyId() {
		return keyId;
	}

	public void setKeyId(int keyId) {
		this.keyId = keyId;
	}

	public int getOntologyId() {
		return ontologyId;
	}

	public void setOntologyId(int ontologyId) {
		this.ontologyId = ontologyId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Ontology getOntology() {
		return ontology;
	}

	public void setOntology(Ontology ontology) {
		this.ontology = ontology;
	}

	public boolean isAction() {
		return isAction;
	}

	public void setAction(boolean isAction) {
		this.isAction = isAction;
	}

	public List<ConceptAttribute> getAttributes() {
		return attributes;
	}

	public List<ConceptClass> getClasses() {
		return classes;
	}

	public List<ConceptLink> getLinks() {
		return links;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}

	public Concept getSuperConcept() {
		return superconcept;
	}

	public void setSuperConcept(Concept superConcept) {
		this.superconcept = superConcept;
	}

	public List<Concept> getSubConcepts() {
		return subconcepts;
	}

	public List<Concept> getParts() {
		return parts;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}
}
