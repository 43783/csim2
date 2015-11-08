package ch.hesge.cragsi.utils;

import java.util.Date;

import ch.hesge.cragsi.model.Account;
import ch.hesge.cragsi.model.Accounting;

/**
 * Class responsible to manage accounting entries.
 * 
 * Copyright HEG Geneva 2015, Switzerland
 * @author Eric Harth
 */
public class AccountingFactory {

	public static Accounting createDebitEntry(int sequenceId, Date date, String journalId, String periodId, Account account, String label, double value) {

		Accounting accounting = new Accounting();

		accounting.setId(sequenceId);
		accounting.setDate(date);
		accounting.setJournalId(journalId);
		accounting.setName(StringUtils.toString(sequenceId));
		accounting.setPeriodId(periodId);
		accounting.setAccountId(account.getId());
		accounting.setLineDate(date);
		accounting.setLineName(label);
		accounting.setLineDebit(value);
		accounting.setLineJournalId(journalId);
		accounting.setLinePeriodId(periodId);

		return accounting;
	}

	public static Accounting createCreditEntry(int sequenceId, Date date, String journalId, String periodId, Account account, String label, double value) {

		Accounting accounting = new Accounting();

		accounting.setAccountId(account.getId());
		accounting.setLineDate(date);
		accounting.setLineName(label);
		accounting.setLineCredit(value);
		accounting.setLineJournalId(journalId);
		accounting.setLinePeriodId(periodId);

		return accounting;
	}

}
