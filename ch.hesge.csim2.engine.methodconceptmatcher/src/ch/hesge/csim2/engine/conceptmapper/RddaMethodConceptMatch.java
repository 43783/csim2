package ch.hesge.csim2.engine.conceptmapper;

public class RddaMethodConceptMatch implements Comparable {

	private ConceptIdentifier conceptId;
	private MethodIdentifier methodId;
	private float matchingStrength;

	public RddaMethodConceptMatch() {
	}

	public RddaMethodConceptMatch(MethodIdentifier mId, ConceptIdentifier cId, float matchingStrength) {
		super();
		this.methodId = mId;
		this.conceptId = cId;
		this.matchingStrength = matchingStrength;
	}

	public int compareTo(Object o) {
		if (matchingStrength > ((RddaMethodConceptMatch) o).getMatchingStrength())
			return 1;
		else if (matchingStrength < ((RddaMethodConceptMatch) o).getMatchingStrength())
			return -1;
		else
			return 0;
	}

	public String toString() {
		return methodId.toString();
	}

	public ConceptIdentifier getConceptId() {
		return conceptId;
	}

	public MethodIdentifier getMethodId() {
		return methodId;
	}

	public float getMatchingStrength() {
		return matchingStrength;
	}

}
