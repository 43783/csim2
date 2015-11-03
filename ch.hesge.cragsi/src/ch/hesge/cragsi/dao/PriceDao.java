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

public class PriceDao {

	/**
	 * Retrieve all prices contained in file
	 * @return
	 * @throws IOException
	 */
	public static List<Price> findAll() throws IOException {

		List<Price> priceList = new ArrayList<>();
		String pricePath = UserSettings.getInstance().getProperty("pricePath");

		CsvReader reader = new CsvReader(pricePath, ';', Charset.forName("UTF8"));
		reader.setSkipEmptyRecords(true);
		reader.readHeaders();

		while (reader.readRecord()) {

			String category = reader.get(0);
			String libelle = reader.get(1);
			String price = reader.get(2);

			Price priceObject = new Price();

			priceObject.setCategory(category);
			priceObject.setLibelle(libelle);
			priceObject.setPrice(StringUtils.toDouble(price));

			priceList.add(priceObject);
		}

		reader.close();

		return priceList;
	}

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
