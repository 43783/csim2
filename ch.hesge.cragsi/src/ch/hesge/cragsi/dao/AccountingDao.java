package ch.hesge.cragsi.dao;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import ch.hesge.cragsi.model.Accounting;
import ch.hesge.cragsi.utils.CsvWriter;

public class AccountingDao {

	public static void saveAll(List<Accounting> accountings) throws IOException {
		
		String outputPath = UserSettings.getInstance().getOutputPath();
		
		if (new File(outputPath).exists()) {
			Files.delete(Paths.get(outputPath));
		}

		CsvWriter csvOutput = new CsvWriter(new FileWriter(outputPath, true), ';');
		
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
			csvOutput.write(accounting.getKeyId());
			csvOutput.write(accounting.getDate());
			csvOutput.write(accounting.getJournalId());
			csvOutput.write(accounting.getName());
			csvOutput.write(accounting.getPeriodId());
			csvOutput.write(accounting.getAccountId());
			csvOutput.write(accounting.getLineDate());
			csvOutput.write(accounting.getLineName());
			csvOutput.write(accounting.getLineCredit());
			csvOutput.write(accounting.getLineDebit());
			csvOutput.write(accounting.getLineJournalId());
			csvOutput.write(accounting.getLinePeriodId());
			csvOutput.endRecord();
		}
		
		csvOutput.close();
	}
}
