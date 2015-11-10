/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hesge.cragsi.model;

import java.util.Date;

/**
 * Represents a domain object.
 * 
 * Copyright HEG Geneva 2015, Switzerland
 * @author Eric Harth
 */
public class Activity {

	// from fdc.csv
	private String unit; // Unité
	private String lastname; // Nom
	private String firstname; // Prénom
	private String contractType; // Type Contrat
	private Date startContract; // Debut contrat
	private Date endContract; // Fin contrat
	private String function; // Fonction
	private int studentCount; // Nbr etud.
	private double hours; // Heures
	private double coefficient; // Coefficient
	private double weeks; // Semaine-s
	private double total; // Total
	private double totalS1; // first semester total (computed)
	private double totalS2; // second semester total (computed)
	private String activity; // Activité
	private String pillarGE; // Pilier GE
	private String pillarHES; // Pilier HES
	private String sector; // Filière
	private String detail; // Détail
	private String projectNumber; // Num. projet

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

	public String getContractType() {
		return contractType;
	}

	public void setContractType(String contractType) {
		this.contractType = contractType;
	}

	public Date getStartContract() {
		return startContract;
	}

	public void setStartContract(Date startContract) {
		this.startContract = startContract;
	}

	public Date getEndContract() {
		return endContract;
	}

	public void setEndContract(Date endContract) {
		this.endContract = endContract;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public int getStudentCount() {
		return studentCount;
	}

	public void setStudentCount(int studentCount) {
		this.studentCount = studentCount;
	}

	public double getHours() {
		return hours;
	}

	public void setHours(double hours) {
		this.hours = hours;
	}

	public double getCoefficient() {
		return coefficient;
	}

	public void setCoefficient(double coefficient) {
		this.coefficient = coefficient;
	}

	public double getWeeks() {
		return weeks;
	}

	public void setWeeks(double weeks) {
		this.weeks = weeks;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public double getTotalS1() {
		return totalS1;
	}

	public void setTotalS1(double totalS1) {
		this.totalS1 = totalS1;
	}

	public double getTotalS2() {
		return totalS2;
	}

	public void setTotalS2(double totalS2) {
		this.totalS2 = totalS2;
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
}
