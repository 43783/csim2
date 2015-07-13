package ch.hesge.csim2.core.model;

/**
 * Representing kind of reference found in source code. Generally a local
 * variable reference can be associated to a local variable, a method parameter
 * or a class attribute.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public enum SourceReferenceOrigin {

	CLASS_FIELD(0), 
	METHOD_PARAMETER(1), 
	LOCAL_VARIABLE(2),
	DECLARATION(3),
	UNKOWN_ORIGIN(255);

	// Private attributes
	private final int value;
	
	/**
	 * Internal constructor 
	 * @param value
	 */
    private SourceReferenceOrigin(int value) {
        this.value = value;
    }

    /**
     * Retrieve integer value associated to the enum value 
     * @return a SourceReferenceKind
     */
    public int getValue() {
        return value;
    }	

    /**
     * Return enum value from int
     * @param int value
     * @return enum value
     */
    public static SourceReferenceOrigin valueOf(int value) {
    	
        switch (value) {
        	case 0:
        		return CLASS_FIELD;
        	case 1:
        		return METHOD_PARAMETER;
        	case 2:
        		return LOCAL_VARIABLE;
        	case 3:
        		return DECLARATION;
        	default:
        		return UNKOWN_ORIGIN;
       }
    }	
}
