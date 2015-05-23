package ch.hesge.csim2.engine.conceptmapper;

public class MethodIdentifier extends ClassIdentifier implements Comparable, StemLocationElement {

	//name of the element for which the identifier is created 
	private String signature = "";
	private String className = "";
	private String packageName = "";
	private int methodID = -1;

	public String getSignature() {
		return signature;
	}

	public int getMethodID() {
		return methodID;
	}

	public MethodIdentifier(int methodID, String packageName, String className, String signature) {
		//This string replacement is used to correct a problem with the parsing of the source code
		//In fact, the signature of the method in the source_method database is not the same as
		//the signature in the execution trace. Then there are many method that cannot nbe matched
		String sign = signature;
		sign = sign.replace("*", "");
		sign = sign.replace("&", "");
		sign = sign.replace("[]", "");
		this.signature = sign.replace("(void)", "()");
		if (className.equals("UndefinedClass"))
			this.className = "Global";
		else
			this.className = className;

		this.packageName = packageName;
		this.methodID = methodID;
		//we must assign the name space and signature in the attributes of the superclass after having
		//corrected the strings. One cannot use the constructor of the superclass for it because we
		//must process the strings before
		setNameAndNameSpaceName(this.signature, this.packageName + this.className);
	}

	public int compareTo(Object o) {
		return signature.compareTo(((MethodIdentifier) o).getSignature());
	}

	public String getStringIdentifier() {
		return packageName + className + signature;
	}

	public boolean match(String packageName, String className, String signature) {
		return this.signature.equals(signature) && this.className.equals(className) && this.packageName.equals(packageName);
	}

	public int getSourceId() {
		return methodID;
	}

	public int hashCode() {
		return methodID;
	}

	public boolean equals(StemLocationElement sle) {
		return methodID == sle.getSourceId();
	}
}
