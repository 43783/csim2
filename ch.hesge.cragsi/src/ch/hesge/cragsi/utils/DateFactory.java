package ch.hesge.cragsi.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ch.hesge.cragsi.dao.AGFLineDao;
import ch.hesge.cragsi.dao.AccountDao;
import ch.hesge.cragsi.dao.AccountingDao;
import ch.hesge.cragsi.dao.ActivityDao;
import ch.hesge.cragsi.dao.FundingDao;
import ch.hesge.cragsi.dao.PartnerDao;
import ch.hesge.cragsi.dao.PriceDao;
import ch.hesge.cragsi.dao.ProjectDao;
import ch.hesge.cragsi.loader.UserSettings;
import ch.hesge.cragsi.model.AGFLine;
import ch.hesge.cragsi.model.Account;
import ch.hesge.cragsi.model.Accounting;
import ch.hesge.cragsi.model.Activity;
import ch.hesge.cragsi.model.Funding;
import ch.hesge.cragsi.model.Partner;
import ch.hesge.cragsi.model.Price;
import ch.hesge.cragsi.model.Project;

public class DateFactory {

	/**
	 * 
	 * @return
	 */
	public static Date getFirstSemesterStartDate() {

		Calendar calendar = Calendar.getInstance();
		String periodYear = UserSettings.getInstance().getProperty("academicPeriod_S1");
		
		calendar.set(Calendar.YEAR, StringUtils.toInteger(periodYear));
		calendar.set(Calendar.MONTH, 8);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		return new Date(calendar.getTime().getTime());
	}

	/**
	 * 
	 * @return
	 */
	public static Date getFirstSemesterEndDate() {

		Calendar calendar = Calendar.getInstance();
		String periodYear = UserSettings.getInstance().getProperty("academicPeriod_S1");
		
		calendar.set(Calendar.YEAR, StringUtils.toInteger(periodYear));
		calendar.set(Calendar.MONTH, 11);
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		
		return new Date(calendar.getTime().getTime());
	}
	
	/**
	 * 
	 * @return
	 */
	public static Date getSecondSemesterStartDate() {
		
		Calendar calendar = Calendar.getInstance();
		String periodYear = UserSettings.getInstance().getProperty("academicPeriod_S2");
		
		calendar.set(Calendar.YEAR, StringUtils.toInteger(periodYear));
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		return new Date(calendar.getTime().getTime());
		
	}
	
	/**
	 * 
	 * @return
	 */
	public static Date getSecondSemesterEndDate() {
		
		Calendar calendar = Calendar.getInstance();
		String periodYear = UserSettings.getInstance().getProperty("academicPeriod_S2");
		
		calendar.set(Calendar.YEAR, StringUtils.toInteger(periodYear));
		calendar.set(Calendar.MONTH, 7);
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		
		return new Date(calendar.getTime().getTime());
		
	}
}
