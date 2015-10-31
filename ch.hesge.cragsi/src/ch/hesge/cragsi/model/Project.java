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

public class Project {

	// Private attributes
	private String keyId; // N° HESSO
	
	private String code; // N° HESSO
	private String date; // Date
	private String startDate; // Date de début
	private String endDate; // Date de fin
	private String description; // Libellé
	private String status; // Statut

	/**
	 * Default constructor
	 */
	public Project() {
	}

	public String getKeyId() {
		return keyId;
	}

	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String toString() {
		return "type: Project, keyId: " + keyId + ", code: " + code + ", date: " + date + ", startDate: " + startDate + ", endDate: " + endDate + ", description: " + description + ", statut: " + status;
	}
}
