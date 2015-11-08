package ch.hesge.cragsi.utils;

import java.util.Calendar;
import java.util.Date;

import ch.hesge.cragsi.exceptions.ConfigurationException;

public class DateFactory {

	/**
	 * Retrieve the first semester start date.
	 * 
	 * @return the start date
	 * @throws ConfigurationException 
	 */
	public static Date getFirstSemesterStartDate() throws ConfigurationException {

		Calendar calendar = Calendar.getInstance();
		String periodYear = PropertyUtils.getProperty("academicPeriod_S1");
		
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
	 * Retrieve the first semester end date.
	 * 
	 * @return the end date
	 * @throws ConfigurationException 
	 */
	public static Date getFirstSemesterEndDate() throws ConfigurationException {

		Calendar calendar = Calendar.getInstance();
		String periodYear = PropertyUtils.getProperty("academicPeriod_S1");
		
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
	 * Retrieve the second semester start date.
	 * 
	 * @return the start date
	 * @throws ConfigurationException 
	 */
	public static Date getSecondSemesterStartDate() throws ConfigurationException {
		
		Calendar calendar = Calendar.getInstance();
		String periodYear = PropertyUtils.getProperty("academicPeriod_S2");
		
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
	 * Retrieve the second semester end date.
	 * 
	 * @return the end date
	 * @throws ConfigurationException 
	 */
	public static Date getSecondSemesterEndDate() throws ConfigurationException {
		
		Calendar calendar = Calendar.getInstance();
		String periodYear = PropertyUtils.getProperty("academicPeriod_S2");
		
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
