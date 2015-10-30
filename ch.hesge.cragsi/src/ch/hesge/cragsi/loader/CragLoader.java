package ch.hesge.cragsi.loader;

import java.io.FileNotFoundException;
import java.io.IOException;

import ch.hesge.cragsi.utils.CsvReader;

public class CragLoader {

	private String inputPath = "res/adoo.export.écritures.account.move.line.csv";
	//private String outputPath = "";

	/**
	 * Default constructor
	 */
	public CragLoader() {
	}

	public void start() {
		
		try {

			CsvReader reader = new CsvReader(inputPath);
			reader.readHeaders();

			while (reader.readRecord()) {
				
				String lineId = reader.get("id");
				String accountId = reader.get("account_id/id");
				String date = reader.get("date");
				String name = reader.get("name");
				String journalId = reader.get("journal_id/id");
				String moveId = reader.get("move_id/id");
				String periodId = reader.get("period_id/id");

				lineId = lineId.replaceAll("__export__.", "");
				accountId = accountId.replaceAll("__export__.", "");
				date = date.replaceAll("__export__.", "");
				name = name.replaceAll("__export__.", "");
				journalId = journalId.replaceAll("__export__.", "");
				moveId = moveId.replaceAll("__export__.", "");
				periodId = periodId.replaceAll("__export__.", "");
				
				// perform program logic here
				System.out.println("id: " + lineId);
				System.out.println("  accountId: " + accountId);
				System.out.println("  date: " + date);
				System.out.println("  name: " + name);
				System.out.println("  journalId: " + journalId);
				System.out.println("  moveId: " + moveId);
				System.out.println("  periodId: " + periodId);
			}

			reader.close();

		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Main entry point
	 * @param args
	 */
	public static void main(String[] args) {
		new CragLoader().start();
	}

}
