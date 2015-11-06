package ch.hesge.cragsi.loader;

import java.util.Date;

import ch.hesge.cragsi.model.Account;
import ch.hesge.cragsi.model.Accounting;
import ch.hesge.cragsi.utils.StringUtils;

/**
 * Class responsible to create accounting entries.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
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
