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

public class AccountingEntry {

	// Private attributes
	private String keyId; // id
	
	private String date; // date
	private String journalId; // journal_id/id
	private String name; // name
	private String periodId; // period_id/id
	private String accountId; // line_id/account_id/id
	private String accountDate; // line_id/date
	private String libelle; // line_id/name
	private String creditId; // line_id/credit
	private String debitId; // line_id/debit
	private String lineJournalId; // line_id/journal_id/id
	private String linePeriodId; // line_id/period_id/id 

	/**
	 * Default constructor
	 */
	public AccountingEntry() {
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

	public String getJournalId() {
		return journalId;
	}

	public void setJournalId(String journalId) {
		this.journalId = journalId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPeriodId() {
		return periodId;
	}

	public void setPeriodId(String periodId) {
		this.periodId = periodId;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getAccountDate() {
		return accountDate;
	}

	public void setAccountDate(String accountDate) {
		this.accountDate = accountDate;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public String getCreditId() {
		return creditId;
	}

	public void setCreditId(String creditId) {
		this.creditId = creditId;
	}

	public String getDebitId() {
		return debitId;
	}

	public void setDebitId(String debitId) {
		this.debitId = debitId;
	}

	public String getLineJournalId() {
		return lineJournalId;
	}

	public void setLineJournalId(String lineJournalId) {
		this.lineJournalId = lineJournalId;
	}

	public String getLinePeriodId() {
		return linePeriodId;
	}

	public void setLinePeriodId(String linePeriodId) {
		this.linePeriodId = linePeriodId;
	}

	public String toString() {
		return "type: AccountEntry, date: " + date + ", journalId: " + journalId + ", name: " + name + ", periodId: " + periodId + ", accountDate: " + accountDate + ", libelle: " + libelle + ", creditId: " + creditId + ", debitId: " + debitId + ", lineJournalId: " + lineJournalId + ", linePeriodId: " + linePeriodId;
	}
}
