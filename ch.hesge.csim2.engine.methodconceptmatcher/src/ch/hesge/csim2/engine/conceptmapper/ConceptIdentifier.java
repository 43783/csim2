package ch.hesge.csim2.engine.conceptmapper;

import java.util.ArrayList;

public class ConceptIdentifier extends ClassIdentifier implements Comparable, StemLocationElement {

	private int conceptID = 0;
	//this is the father concept in the tree of concepts.
	private ConceptIdentifier fatherConcept;
	//this is the list of all DIRECT children concept in the tree of concepts (depth=1)
	private ArrayList<ConceptIdentifier> directChildrenConcepts = new ArrayList<ConceptIdentifier>();

	public static ConceptIdentifier dummy() {
		ConceptIdentifier ci = new ConceptIdentifier(0, "", "-----inherited-----");
		return ci;
	}

	public ConceptIdentifier getFatherConcept() {
		return fatherConcept;
	}

	//returns all father concept of this
	public ArrayList<ConceptIdentifier> getFatherConcepts() {
		if (fatherConcept == null)
			return new ArrayList<ConceptIdentifier>();
		else {
			ArrayList<ConceptIdentifier> lCi = fatherConcept.getFatherConcepts();
			lCi.add(fatherConcept);
			return lCi;
		}
	}

	public ArrayList<ConceptIdentifier> getAllChildrenConcepts() {
		ArrayList<ConceptIdentifier> result = new ArrayList<ConceptIdentifier>();
		gatherAllChildrenConcepts(result);
		return result;
	}

	private void gatherAllChildrenConcepts(ArrayList<ConceptIdentifier> result) {
		for (ConceptIdentifier dcc : directChildrenConcepts) {
			result.add(dcc);
			dcc.gatherAllChildrenConcepts(result);
		}
	}

	public void setFatherConcept(ConceptIdentifier fatherConcept) {
		this.fatherConcept = fatherConcept;
	}

	public void addDirectChildConcept(ConceptIdentifier childConcept) {
		directChildrenConcepts.add(childConcept);
	}

	public String getConceptName() {
		return name;
	}

	public String getContextName() {
		return nameSpaceName;
	}

	public int getConceptID() {
		return conceptID;
	}

	public ConceptIdentifier(int conceptID, String contextName, String conceptName) {
		super(contextName, conceptName);
		this.conceptID = conceptID;
	}

	public ConceptIdentifier(int conceptID, String conceptName) {
		this(conceptID, "", conceptName);
	}

	public ConceptIdentifier(String contextName, String conceptName) {
		this(0, contextName, conceptName);
	}

	public ConceptIdentifier(String conceptName) {
		this(0, "", conceptName);
	}

	public int compareTo(Object o) {
		return name.compareTo(((ConceptIdentifier) o).getConceptName());
	}

	public int getSourceId() {
		return conceptID;
	}

	public int hashCode() {
		return conceptID;
	}

	public boolean equals(StemLocationElement sle) {
		return conceptID == sle.getSourceId();
	}
}
