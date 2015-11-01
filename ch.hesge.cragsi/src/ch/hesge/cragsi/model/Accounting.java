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

public class Accounting {

	// Private attributes
	private String keyId; // id	
	private String date; // date
	private String journalId; // journal_id/id
	private String name; // name
	private String periodId; // period_id/id
	private String accountId; // line_id/account_id/id
	private String lineDate; // line_id/date
	private String lineName; // line_id/name
	private String lineCredit; // line_id/credit
	private String lineDebit; // line_id/debit
	private String lineJournalId; // line_id/journal_id/id
	private String linePeriodId; // line_id/period_id/id 

	/**
	 * Default constructor
	 */
	public Accounting() {
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

	public String getLineDate() {
		return lineDate;
	}

	public void setLineDate(String lineDate) {
		this.lineDate = lineDate;
	}

	public String getLineName() {
		return lineName;
	}

	public void setLineName(String lineName) {
		this.lineName = lineName;
	}

	public String getLineCredit() {
		return lineCredit;
	}

	public void setLineCredit(String lineCredit) {
		this.lineCredit = lineCredit;
	}

	public String getLineDebit() {
		return lineDebit;
	}

	public void setLineDebit(String lineDebit) {
		this.lineDebit = lineDebit;
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
		return "type: Accounting, keyId: " + keyId + ", date: " + date + ", journalId: " + journalId + ", name: " + name + ", periodId: " + periodId + ", lineDate: " + lineDate + ", lineName: " + lineCredit + ", creditId: " + lineCredit + ", lineDebit: " + lineDebit + ", lineJournalId: " + lineJournalId + ", linePeriodId: " + linePeriodId;
	}

}
