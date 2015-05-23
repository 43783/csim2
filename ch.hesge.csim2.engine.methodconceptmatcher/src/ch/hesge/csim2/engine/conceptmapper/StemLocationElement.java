package ch.hesge.csim2.engine.conceptmapper;

public interface StemLocationElement {

	//represent the interface to elements in which a stem has been found
	public int getSourceId();

	public int hashCode();

	public boolean equals(StemLocationElement sle);

}
