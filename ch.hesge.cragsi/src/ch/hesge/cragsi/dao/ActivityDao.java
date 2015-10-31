package ch.hesge.cragsi.dao;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import ch.hesge.cragsi.model.Activity;
import ch.hesge.cragsi.utils.CsvReader;

/**
 * Class responsible to manage DAO access for Activity.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class ActivityDao {

	private static String activityPath = "D:/projects/cragsi/files/fdc.csv";

	/**
	 * Retrieve all activities contained in file
	 * @return
	 * @throws IOException
	 */
	public static List<Activity> findAll() throws IOException {

		List<Activity> activityList = new ArrayList<>();

		CsvReader reader = new CsvReader(activityPath, ';', Charset.forName("UTF8"));
		reader.setSkipEmptyRecords(true);
		reader.setCaptureRawRecord(true);

		// Detect headers
		while (reader.readHeaders()) {
			if (reader.getRawRecord().startsWith("Unité")) {
				break;
			}
		}

		// Start parsing activities
		while (reader.readRecord()) {
				
			String unit = reader.get(0);
			String lastname = reader.get(1);
			String firstname = reader.get(2);
			String contract = reader.get(3);
			String function = reader.get(4);
			String studentCount = reader.get(5);
			String hours = reader.get(6);
			String coefficient = reader.get(6);
			String weeks = reader.get(7);
			String total = reader.get(8);
			String activityType = reader.get(9);
			String pillarGe = reader.get(10);
			String pillarHeg = reader.get(11);
			String studyType = reader.get(12);
			String detail = reader.get(13);
			String projectNumber = reader.get(14);
			
			if (!unit.equals("Total")) {
				
				Activity activity = new Activity();
				
				activity.setUnit(unit);
				activity.setLastname(lastname);
				activity.setFirstname(firstname);
				activity.setContract(contract);
				activity.setFunction(function);
				activity.setStudentCount(studentCount);
				activity.setHours(hours);
				activity.setCoefficient(coefficient);
				activity.setWeeks(weeks);
				activity.setTotal(total);
				activity.setActivity(activityType);
				activity.setPillarGe(pillarGe);
				activity.setPillarHeg(pillarHeg);
				activity.setStudyType(studyType);
				activity.setDetail(detail);
				activity.setProjectNumber(projectNumber);

				activityList.add(activity);
			}
		}

		reader.close();
		
		return activityList;
	}
}
