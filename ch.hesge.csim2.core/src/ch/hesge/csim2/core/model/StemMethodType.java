package ch.hesge.csim2.core.model;

/**
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public enum StemMethodType {

	METHOD_NAME_FULL(0),
	METHOD_NAME_PART(1),
	
	PARAMETER_NAME_FULL(2),
	PARAMETER_NAME_PART(3),	
	
	PARAMETER_TYPE_FULL(4),
	PARAMETER_TYPE_PART(5),
	
	REFERENCE_NAME_FULL(6),
	REFERENCE_NAME_PART(7),	
	
	REFERENCE_TYPE_FULL(8),
	REFERENCE_TYPE_PART(9);

	// Private attributes
	private final int value;

	/**
	 * Internal constructor
	 * 
	 * @param value
	 */
	private StemMethodType(int value) {
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
	public static StemMethodType valueOf(int value) {

		switch (value) {
			case 0:
				return METHOD_NAME_FULL;
			case 1:
				return METHOD_NAME_PART;
			case 2:
				return PARAMETER_NAME_FULL;
			case 3:
				return PARAMETER_NAME_PART;
			case 4:
				return PARAMETER_TYPE_FULL;
			case 5:
				return PARAMETER_TYPE_PART;
			case 6:
				return REFERENCE_NAME_FULL;
			case 7:
				return REFERENCE_NAME_PART;
			case 8:
				return REFERENCE_TYPE_FULL;
			default:
				return REFERENCE_TYPE_PART;
		}
	}

}
