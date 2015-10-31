package ch.hesge.cragsi.dao;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import ch.hesge.cragsi.model.AccountingEntry;
import ch.hesge.cragsi.utils.CsvWriter;

public class AccountingEntryDao {

	private static String outputPath = "D:/projects/cragsi/files/odoo.csv";

	public static void saveAll(List<AccountingEntry> entries, String filename) throws IOException {
		
		if (new File(outputPath).exists()) {
			throw new IOException("output path already exists !");
		}
		else {
			
			CsvWriter csvOutput = new CsvWriter(new FileWriter(outputPath, true), ',');
			
			// Write header
			csvOutput.write("id");
			csvOutput.write("date");
			csvOutput.write("journal_id/id");
			csvOutput.write("name");
			csvOutput.write("period_id/id");
			csvOutput.write("line_id/account_id/id");
			csvOutput.write("line_id/date");
			csvOutput.write("line_id/name");
			csvOutput.write("line_id/credit");
			csvOutput.write("line_id/debit");
			csvOutput.write("line_id/journal_id/id");
			csvOutput.write("line_id/period_id/id");
			csvOutput.endRecord();			
			
			// write out a few records
			csvOutput.write("7");
			csvOutput.write("2015-9-1");
			csvOutput.write("__export__.account_journal_5");
			csvOutput.write("1");
			csvOutput.write("__export__.account_period_10");
			csvOutput.write("__export__.account_account_172");
			csvOutput.write("2015-9-1");
			csvOutput.write("Career Women");
			csvOutput.write("6000");
			csvOutput.write("__export__.account_journal_5");
			csvOutput.write("__export__.account_period_10");
			csvOutput.endRecord();
						
			csvOutput.write("");
			csvOutput.write("");
			csvOutput.write("");
			csvOutput.write("");
			csvOutput.write("");
			csvOutput.write("__export__.account_account_134");
			csvOutput.write("2015-9-1");
			csvOutput.write("Career Women");
			csvOutput.write("6000");
			csvOutput.write("__export__.account_journal_5");
			csvOutput.write("__export__.account_period_10");
			csvOutput.endRecord();

			csvOutput.write("121");
			csvOutput.write("2015-9-1");
			csvOutput.write("__export__.account_journal_5");
			csvOutput.write("1");
			csvOutput.write("__export__.account_period_10");
			csvOutput.write("__export__.account_account_173");
			csvOutput.write("2015-9-1");
			csvOutput.write("Dépôt - RCSO");
			csvOutput.write("1600");
			csvOutput.write("__export__.account_journal_5");
			csvOutput.write("__export__.account_period_10");
			csvOutput.endRecord();

			csvOutput.write("121");
			csvOutput.write("2015-9-1");
			csvOutput.write("__export__.account_journal_5");
			csvOutput.write("1");
			csvOutput.write("__export__.account_period_10");
			csvOutput.write("__export__.account_account_173");
			csvOutput.write("2015-9-1");
			csvOutput.write("Dépôt - RCSO");
			csvOutput.write("1600");
			csvOutput.write("__export__.account_journal_5");
			csvOutput.write("__export__.account_period_10");
			csvOutput.endRecord();

			csvOutput.write("");
			csvOutput.write("");
			csvOutput.write("");
			csvOutput.write("");
			csvOutput.write("");
			csvOutput.write("");
			csvOutput.write("__export__.account_account_134");
			csvOutput.write("2015-9-1");
			csvOutput.write("Dépôt - RCSO;1600");
			csvOutput.write("__export__.account_journal_5");
			csvOutput.write("__export__.account_period_10");
			csvOutput.endRecord();
		}
	}
}
