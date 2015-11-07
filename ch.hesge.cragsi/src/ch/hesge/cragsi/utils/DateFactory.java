package ch.hesge.cragsi.utils;

import java.util.Calendar;
import java.util.Date;

import ch.hesge.cragsi.loader.UserSettings;

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
