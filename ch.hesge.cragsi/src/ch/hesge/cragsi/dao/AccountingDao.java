package ch.hesge.cragsi.dao;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import ch.hesge.cragsi.exceptions.ConfigurationException;
import ch.hesge.cragsi.model.Accounting;
import ch.hesge.cragsi.utils.CsvWriter;
import ch.hesge.cragsi.utils.PropertyUtils;
import ch.hesge.cragsi.utils.StringUtils;

public class AccountingDao {

	/**
	 * Save all accountings to file.
	 * 
	 * @throws IOException
	 * @throws ConfigurationException 
	 */
	public static void saveAll(List<Accounting> accountings) throws IOException, ConfigurationException {
		
		String accountingPath = PropertyUtils.getProperty("accountingPath");

		// Delete old file, if present
		if (new File(accountingPath).exists()) {
			Files.delete(Paths.get(accountingPath));
		}

		CsvWriter csvOutput = new CsvWriter(accountingPath, ';', Charset.forName("UTF8"));

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
		
		// Write one record by accounting
		for (Accounting accounting : accountings) {
			csvOutput.write(StringUtils.toString(accounting.getId()));
			csvOutput.write(StringUtils.toString(accounting.getDate()));
			csvOutput.write(accounting.getJournalId());
			csvOutput.write(accounting.getName());
			csvOutput.write(accounting.getPeriodId());
			csvOutput.write(accounting.getAccountId());
			csvOutput.write(StringUtils.toString(accounting.getLineDate()));
			csvOutput.write(accounting.getLineName());
			csvOutput.write(StringUtils.toString(accounting.getLineDebit()));
			csvOutput.write(StringUtils.toString(accounting.getLineCredit()));
			csvOutput.write(accounting.getLineJournalId());
			csvOutput.write(accounting.getLinePeriodId());
			csvOutput.endRecord();
		}
		
		csvOutput.close();
	}
}
