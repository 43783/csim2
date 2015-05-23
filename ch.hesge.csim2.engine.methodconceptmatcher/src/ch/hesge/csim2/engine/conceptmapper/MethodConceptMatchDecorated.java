package ch.hesge.csim2.engine.conceptmapper;

public class MethodConceptMatchDecorated extends MethodConceptMatch2 {

	private MethodConceptMatch2 mcm;

	public MethodConceptMatchDecorated(MethodConceptMatch2 mcm) {
		this.mcm = mcm;
	}

	public int compareTo(Object o) {
		return mcm.compareTo(o);
	}

	public ConceptIdentifier getConceptId() {
		return mcm.getConceptId();
	}

	public MethodIdentifier getMethodId() {
		return mcm.getMethodId();
	}

	public float getMatchingStrength() {
		return mcm.getMatchingStrength();
	}

	public boolean equals(java.lang.Object argument) {
		return mcm.equals(argument);
	}

	public int hashCode() {
		return mcm.hashCode();
	}

	public String toString() {
		return " > " + mcm.toString();
	}
}
