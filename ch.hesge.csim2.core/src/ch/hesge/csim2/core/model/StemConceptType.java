package ch.hesge.csim2.core.model;

/**
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public enum StemConceptType {

	CONCEPT_NAME_FULL(0), 
	CONCEPT_NAME_PART(1),

	ATTRIBUTE_NAME_FULL(2), 
	ATTRIBUTE_NAME_PART(3),

	ATTRIBUTE_IDENTIFIER_FULL(4), 
	ATTRIBUTE_IDENTIFIER_PART(5),

	CLASS_NAME_FULL(6), 
	CLASS_NAME_PART(7),

	CLASS_IDENTIFIER_FULL(8), 
	CLASS_IDENTIFIER_PART(9);

	// Private attributes
	private final int	value;

	/**
	 * Internal constructor
	 * 
	 * @param value
	 */
	private StemConceptType(int value) {
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
	public static StemConceptType valueOf(int value) {

		switch (value) {
			case 0:
				return CONCEPT_NAME_FULL;
			case 1:
				return CONCEPT_NAME_PART;
			case 2:
				return ATTRIBUTE_NAME_FULL;
			case 3:
				return ATTRIBUTE_NAME_PART;
			case 4:
				return ATTRIBUTE_IDENTIFIER_FULL;
			case 5:
				return ATTRIBUTE_IDENTIFIER_PART;
			case 6:
				return CLASS_NAME_FULL;
			case 7:
				return CLASS_NAME_PART;
			case 8:
				return CLASS_IDENTIFIER_FULL;
			default:
				return CLASS_IDENTIFIER_PART;
		}
	}

}
