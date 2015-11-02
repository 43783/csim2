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

	// from fdc.csv
	private String unit; // Unité
	private String lastname; // Nom
	private String firstname; // Prénom
	private String category; // Catégorie officielle
	private String studentCount; // Nbr etud.
	private String hours; // Heures
	private String coefficient; // Coefficient
	private String weeks; // Semaine-s
	private String total; // Total
	private String activity; // Activité
	private String pillarGE; // Pilier GE
	private String pillarHES; // Pilier HES
	private String sector; // Filière
	private String projectNumber; // Num. projet
	private String startContract; // Debut contrat
	private String endContract; // Fin contrat
	private String totalMonths; // Nb mois total
	private String firstSemesterTotalMonths; // Nb mois total 2015
	private String firstSemesterTotalHours; // Nb heures total 2015
	private String secondSemesterTotalMonth; // Nb mois total 2016
	private String secondSemesterTotalHours; // Nb heure total 2016
	private String personId; // Id personne
	private String contractId; // Id contrat
	private String cursus; // Cursus

	// from fdc.detail.csv
	private String contractType; // Type Contrat
	private String function; // Fonction
	private String detail; // Détail

	/**
	 * Default constructor
	 */
	public Activity() {
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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
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

	public String getPillarGE() {
		return pillarGE;
	}

	public void setPillarGE(String pillarGE) {
		this.pillarGE = pillarGE;
	}

	public String getPillarHES() {
		return pillarHES;
	}

	public void setPillarHES(String pillarHES) {
		this.pillarHES = pillarHES;
	}

	public String getSector() {
		return sector;
	}

	public void setSector(String sector) {
		this.sector = sector;
	}

	public String getProjectNumber() {
		return projectNumber;
	}

	public void setProjectNumber(String projectNumber) {
		this.projectNumber = projectNumber;
	}

	public String getStartContract() {
		return startContract;
	}

	public void setStartContract(String startContract) {
		this.startContract = startContract;
	}

	public String getEndContract() {
		return endContract;
	}

	public void setEndContract(String endContract) {
		this.endContract = endContract;
	}

	public String getTotalMonths() {
		return totalMonths;
	}

	public void setTotalMonths(String totalMonths) {
		this.totalMonths = totalMonths;
	}

	public String getFirstSemesterTotalMonths() {
		return firstSemesterTotalMonths;
	}

	public void setFirstSemesterTotalMonths(String firstSemesterTotalMonths) {
		this.firstSemesterTotalMonths = firstSemesterTotalMonths;
	}

	public String getFirstSemesterTotalHours() {
		return firstSemesterTotalHours;
	}

	public void setFirstSemesterTotalHours(String firstSemesterTotalHours) {
		this.firstSemesterTotalHours = firstSemesterTotalHours;
	}

	public String getSecondSemesterTotalMonth() {
		return secondSemesterTotalMonth;
	}

	public void setSecondSemesterTotalMonth(String secondSemesterTotalMonth) {
		this.secondSemesterTotalMonth = secondSemesterTotalMonth;
	}

	public String getSecondSemesterTotalHours() {
		return secondSemesterTotalHours;
	}

	public void setSecondSemesterTotalHours(String secondSemesterTotalHours) {
		this.secondSemesterTotalHours = secondSemesterTotalHours;
	}

	public String getPersonId() {
		return personId;
	}

	public void setPersonId(String personId) {
		this.personId = personId;
	}

	public String getContractId() {
		return contractId;
	}

	public void setContractId(String contractId) {
		this.contractId = contractId;
	}

	public String getCursus() {
		return cursus;
	}

	public void setCursus(String cursus) {
		this.cursus = cursus;
	}

	public String getContractType() {
		return contractType;
	}

	public void setContractType(String contractType) {
		this.contractType = contractType;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

}
