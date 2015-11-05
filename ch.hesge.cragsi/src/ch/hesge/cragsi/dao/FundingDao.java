package ch.hesge.cragsi.dao;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import ch.hesge.cragsi.loader.UserSettings;
import ch.hesge.cragsi.model.Funding;
import ch.hesge.cragsi.utils.CsvReader;
import ch.hesge.cragsi.utils.StringUtils;

/**
 * Class responsible to manage DAO access for Account.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class FundingDao {

	/**
	 * Retrieve all fundings contained in file
	 * @return
	 * @throws IOException
	 */
	public static List<Funding> findAll() throws IOException {

		List<Funding> fundingList = new ArrayList<>();
		String fundingPath = UserSettings.getInstance().getProperty("fundingPath");

		CsvReader reader = new CsvReader(fundingPath, ';', Charset.forName("UTF8"));
		reader.setSkipEmptyRecords(true);
		reader.readHeaders();

		while (reader.readRecord()) {
			
			String date = reader.get(0);
			String hessoId = reader.get(1);
			String name = reader.get(2);
			String amount = reader.get(3);

			Funding funding = new Funding();
			
			funding.setId(hessoId);
			funding.setDate(StringUtils.fromString(date));
			funding.setHessoId(hessoId);
			funding.setName(name);
			funding.setAmount(amount);

			fundingList.add(funding);
		}

		reader.close();
		
		return fundingList;
	}
}
