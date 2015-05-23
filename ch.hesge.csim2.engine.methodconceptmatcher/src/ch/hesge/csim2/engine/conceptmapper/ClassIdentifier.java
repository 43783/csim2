package ch.hesge.csim2.engine.conceptmapper;

public class ClassIdentifier implements Comparable {

	//name of the element for which the identifier is created 
	protected String name = "";
	// context in which the name is defined
	protected String nameSpaceName = "";
	//ID of the exec trace in which the element has been found
	private int traceID = 0;

	public int compareTo(Object cid) {
		return toString().compareTo(cid.toString());
	}

	public String getClassName() {
		return name;
	}

	public String getPackageName() {
		return nameSpaceName;
	}

	protected void setNameAndNameSpaceName(String name, String nameSpaceName) {
		this.name = name;
		this.nameSpaceName = nameSpaceName;
	}

	public boolean isRoot() {
		return false;
		//		return  
		//		name.equalsIgnoreCase(TraceProcedure.ARN)|| 
		//		name.equalsIgnoreCase(TraceProcedure.TNN);
	}

	public ClassIdentifier() {
	}

	public ClassIdentifier(String packagename, String className) {
		if (packagename != null)
			this.nameSpaceName = packagename;
		this.name = className;
	}

	public ClassIdentifier(String packagename, String className, int traceID) {
		this(packagename, className);
		this.traceID = traceID;
	}

	public String toString() {
		if (nameSpaceName.length() > 0)
			return nameSpaceName + "." + name;
		else
			return name;
	}

	public boolean equals(java.lang.Object argument) {
		ClassIdentifier classIdentifier = (ClassIdentifier) argument;
		return nameSpaceName.equals(classIdentifier.getPackageName()) && name.equals(classIdentifier.getClassName());
	}

	public int hashCode() {
		return (nameSpaceName + name).hashCode();
	}
	/*	public ConceptIdentifier asConceptIdentifier(){
			return new ConceptIdentifier(packageName,name);
		}*/
}
