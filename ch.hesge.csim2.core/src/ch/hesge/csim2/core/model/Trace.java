/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hesge.csim2.core.model;

/**
 * Represents a single trace entry generated by an code instrumentor.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class Trace {

	// Private attributes
	private int		keyId;
	private int		scenarioId;
	private int 	classId;
	private int		methodId;
	private int		sequenceNumber;
	private String	dynamicPackage;
	private String	dynamicClass;
	private String	staticPackage;
	private String	staticClass;
	private long	threadId;
	private String	signature;
	private String	parameters;
	private boolean	isEnteringTrace;
	private String	returnType;
	private long	timestamp;
	private long	duration;

	/**
	 * Default constructor
	 */
	public Trace() {
	}

	public int getKeyId() {
		return keyId;
	}

	public void setKeyId(int keyId) {
		this.keyId = keyId;
	}

	public int getScenarioId() {
		return scenarioId;
	}

	public void setScenarioId(int scenarioId) {
		this.scenarioId = scenarioId;
	}

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public String getDynamicPackage() {
		return dynamicPackage;
	}

	public void setDynamicPackage(String dynamicPackage) {
		this.dynamicPackage = dynamicPackage;
	}

	public String getDynamicClass() {
		return dynamicClass;
	}

	public void setDynamicClass(String dynamicClass) {
		this.dynamicClass = dynamicClass;
	}

	public String getStaticPackage() {
		return staticPackage;
	}

	public void setStaticPackage(String staticPackage) {
		this.staticPackage = staticPackage;
	}

	public String getStaticClass() {
		return staticClass;
	}

	public void setStaticClass(String staticClass) {
		this.staticClass = staticClass;
	}

	public long getThreadId() {
		return threadId;
	}

	public void setThreadId(long threadId) {
		this.threadId = threadId;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	public boolean isEnteringTrace() {
		return isEnteringTrace;
	}

	public void setEnteringTrace(boolean isEnteringTrace) {
		this.isEnteringTrace = isEnteringTrace;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}
	
	public int getClassId() {
		return classId;
	}

	public void setClassId(int classId) {
		this.classId = classId;
	}

	public int getMethodId() {
		return methodId;
	}

	public void setMethodId(int methodId) {
		this.methodId = methodId;
	}
}
