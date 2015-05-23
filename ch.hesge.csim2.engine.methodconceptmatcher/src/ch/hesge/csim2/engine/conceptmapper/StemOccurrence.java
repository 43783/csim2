package ch.hesge.csim2.engine.conceptmapper;

class StemOccurrence {

	private String stem;
	private int semanticCode;
	protected StemLocationElement stemLocation;

	StemOccurrence(String stem, int semanticCode, StemLocationElement stemLocation) {
		this.stem = stem;
		this.semanticCode = semanticCode;
		this.stemLocation = stemLocation;
	}

	StemLocationElement getStemLocation() {
		return stemLocation;
	}

	String getStem() {
		return stem;
	}

	int getSemanticCode() {
		return semanticCode;
	}
}
