/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hesge.cragsi.model;

/**
 * Represents a domain object.
 * 
 * Copyright HEG Geneva 2015, Switzerland
 * 
 * @author Eric Harth
 */

public class Activity {

	// Private attributes
	private String keyId;

	private String unit; // Unit�
	private String lastname; // Nom
	private String firstname; // Pr�nom
	private String contract; // Type Contrat
	private String function; // Fonction
	private String studentCount; // Nbr Etud.
	private String hours; // Heures
	private String coefficient; // Coefficient
	private String weeks; // Semaine-s
	private String total; // Total
	private String activity; // Activit�
	private String pillarGe; // Pilier GE
	private String pillarHeg; // Pilier HES
	private String studyType; // Fili�re
	private String detail; // D�tail
	private String projectNumber; // Num. projet

	/**
	 * Default constructor
	 */
	public Activity() {
	}

	public String getKeyId() {
		return keyId;
	}

	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getContract() {
		return contract;
	}

	public void setContract(String contract) {
		this.contract = contract;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public String getStudentCount() {
		return studentCount;
	}

	public void setStudentCount(String studentCount) {
		this.studentCount = studentCount;
	}

	public String getHours() {
		return hours;
	}

	public void setHours(String hours) {
		this.hours = hours;
	}

	public String getCoefficient() {
		return coefficient;
	}

	public void setCoefficient(String coefficient) {
		this.coefficient = coefficient;
	}

	public String getWeeks() {
		return weeks;
	}

	public void setWeeks(String weeks) {
		this.weeks = weeks;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public String getPillarGe() {
		return pillarGe;
	}

	public void setPillarGe(String gePillar) {
		this.pillarGe = gePillar;
	}

	public String getPillarHeg() {
		return pillarHeg;
	}

	public void setPillarHeg(String hegPillar) {
		this.pillarHeg = hegPillar;
	}

	public String getStudyType() {
		return studyType;
	}

	public void setStudyType(String studyType) {
		this.studyType = studyType;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getProjectNumber() {
		return projectNumber;
	}

	public void setProjectNumber(String projectNumber) {
		this.projectNumber = projectNumber;
	}

	public String toString() {
		return "type: Activity, keyId: " + keyId + ", unit: " + unit + ", lastname: " + lastname + ", firstname: " + firstname + ", contract: " + contract + ", function: " + function + ", studentCount: " + studentCount + ", hours: " + hours + ", coefficient: " + coefficient + ", weeks: " + weeks + ", total: " + total + ", activity: " + activity + ", pillarGe: " + pillarGe + ", pillarHeg: " + pillarHeg + ", studyType: " + studyType + ", detail: " + detail + ", projectNumber: " + projectNumber;
	}
}
