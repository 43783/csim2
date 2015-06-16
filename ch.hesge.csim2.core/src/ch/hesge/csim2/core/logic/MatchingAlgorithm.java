package ch.hesge.csim2.core.logic;

/**
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public enum MatchingAlgorithm {

	// Enumeration values
	TFIDF(0), ID_L1NORM(1), ID_COSINE(2);

	// Private attributes
	private final int value;

	/**
	 * Internal constructor
	 * 
	 * @param value
	 */
	private MatchingAlgorithm(int value) {
		this.value = value;
	}

	/**
	 * Retrieve integer value associated to the enum value
	 * 
	 * @return a SourceReferenceKind
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Return enum value from int
	 * 
	 * @param int value
	 * @return enum value
	 */
	public static MatchingAlgorithm fromInt(int value) {

		switch (value) {
			case 0:
				return TFIDF;
			case 1:
				return ID_L1NORM;
			default:
				return ID_COSINE;
		}
	}

	/**
	 * Return enum value from String
	 * 
	 * @param int value
	 * @return enum value
	 */
	public static MatchingAlgorithm fromString(String value) {

		switch (value) {
			case "TFIDF":
				return TFIDF;
			case "ID_L1NORM":
				return ID_L1NORM;
			case "ID_COSINE":
				return ID_COSINE;
			default:
				return TFIDF;
		}
	}

}
