package ch.hesge.cragsi.dao;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.hesge.cragsi.loader.UserSettings;
import ch.hesge.cragsi.model.Price;
import ch.hesge.cragsi.utils.CsvReader;
import ch.hesge.cragsi.utils.StringUtils;

/**
 * Class responsible to manage physical access to underlying
 * files and to load them in memory.
 * 
 * Copyright HEG Geneva 2015, Switzerland
 * 
 * @author Eric Harth
 */
public class PriceDao {

	/**
	 * Retrieve all prices contained in file.
	 * 
	 * @return a list of Price
	 * @throws IOException
	 */
	public static List<Price> findAll() throws IOException {

		CsvReader reader = null;
		List<Price> priceList = new ArrayList<>();
		String pricePath = UserSettings.getInstance().getProperty("pricePath");

		try {

			// Open file to load
			reader = new CsvReader(pricePath, ';', Charset.forName("UTF8"));
			reader.setSkipEmptyRecords(true);
			reader.readHeaders();

			// Start parsing prices
			while (reader.readRecord()) {

				// Retrieve field values
				String category = reader.get(0);
				String libelle  = reader.get(1);
				String price    = reader.get(2);

				// Create and initialize an new instance
				Price priceObject = new Price();

				priceObject.setCategory(category);
				priceObject.setLibelle(libelle);
				priceObject.setPrice(StringUtils.toDouble(price));

				priceList.add(priceObject);
			}
		}
		finally {
			if (reader != null) 
				reader.close();
		}

		return priceList;
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public static Map<String, Price> findMapByLibelle() throws IOException {

		Map<String, Price> priceMap = new HashMap<>();

		for (Price price : findAll()) {

			if (!priceMap.containsKey(price.getLibelle())) {
				priceMap.put(price.getLibelle(), price);
			}
		}

		return priceMap;
	}

}
