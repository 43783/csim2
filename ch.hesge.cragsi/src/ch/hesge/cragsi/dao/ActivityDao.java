package ch.hesge.cragsi.dao;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.hesge.cragsi.loader.UserSettings;
import ch.hesge.cragsi.model.Activity;
import ch.hesge.cragsi.utils.CsvReader;

public class ActivityDao {

	public static boolean isSelectable(Activity activity) {
		return !activity.getUnit().equalsIgnoreCase("Total") && (activity.getPillarGE().equalsIgnoreCase("C1") || activity.getPillarGE().equalsIgnoreCase("C2"));
	}
	
	public static String getActivityKey(Activity activity) {
		return activity.getLastname() + "_" + activity.getFirstname() + "_" + activity.getTotal();
	}

	/**
	 * Retrieve all activities contained in file
	 * @return
	 * @throws IOException
	 */
	public static List<Activity> findAll() throws IOException {

		List<Activity> activityList = new ArrayList<>();
		Map<String, Activity> activityDetailMap = loadActivityDetailMap();
				
		String activityPath = UserSettings.getInstance().getProperty("activityPath");

		CsvReader reader = new CsvReader(activityPath, ';', Charset.forName("UTF8"));
		reader.setSkipEmptyRecords(true);
		reader.setCaptureRawRecord(true);

		// Skip lines without headers
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
			String category = reader.get(3);
			String total = reader.get(8);
			String pillarGE = reader.get(10);
			String startContract = reader.get(14);
			String endContract = reader.get(15);
			String totalMonths = reader.get(16);
			String firstSemesterTotalMonths = reader.get(17);
			String firstSemesterTotalHours = reader.get(18);
			String secondSemesterTotalMonth = reader.get(19);
			String secondSemesterTotalHours = reader.get(20);
			String personId = reader.get(21);
			String contractId = reader.get(22);
			String cursus = reader.get(23);
			
			// Skip last line with Total, and select only C1,C2 pillar GE activities
			if (!unit.equals("Total") && pillarGE.equalsIgnoreCase("C1") || pillarGE.equalsIgnoreCase("C2")) {
				
				// Retrieve original activity from detail map
				String activityKey = lastname + "_" + firstname + "_" + total;
				
				if (activityDetailMap.containsKey(activityKey)) {
					
					Activity activity = activityDetailMap.get(activityKey);
					
					activity.setCategory(category);
					activity.setStartContract(startContract);
					activity.setEndContract(endContract);
					activity.setTotalMonths(totalMonths);
					activity.setFirstSemesterTotalMonths(firstSemesterTotalMonths);
					activity.setFirstSemesterTotalHours(firstSemesterTotalHours);
					activity.setSecondSemesterTotalMonth(secondSemesterTotalMonth);
					activity.setSecondSemesterTotalHours(secondSemesterTotalHours);
					activity.setPersonId(personId);
					activity.setContractId(contractId);
					activity.setCursus(cursus);

					activityList.add(activity);
				}
			}
		}

		reader.close();
		
		return activityList;
	}
	
	private static Map<String, Activity> loadActivityDetailMap() throws IOException {
		
		Map<String, Activity> activityMap = new HashMap<>();
		String activityDetailPath = UserSettings.getInstance().getProperty("activityDetailPath");

		CsvReader reader = new CsvReader(activityDetailPath, ';', Charset.forName("UTF8"));
		reader.setSkipEmptyRecords(true);
		reader.setCaptureRawRecord(true);

		// Skip lines without headers
		while (reader.readHeaders()) {
			if (reader.getRawRecord().startsWith("Unité")) {
				break;
			}
		}

		// Start parsing detail activities
		while (reader.readRecord()) {
			
			String unit = reader.get(0);
			String lastname = reader.get(1);
			String firstname = reader.get(2);
			String contractType = reader.get(3);
			String function = reader.get(4);
			String studentCount = reader.get(5);
			String hours = reader.get(6);
			String coefficient = reader.get(7);
			String weeks = reader.get(8);			
			String total = reader.get(9);
			String activityType = reader.get(10);
			String pillarGE = reader.get(11);
			String pillarHES = reader.get(12);
			String sector = reader.get(13);
			String detail = reader.get(14);
			String projectNumber = reader.get(15);
			
			// Skip last line with Total, and select only C1,C2 pillar GE activities
			if (!unit.equals("Total") && pillarGE.equalsIgnoreCase("C1") || pillarGE.equalsIgnoreCase("C2")) {
				
				Activity activity = new Activity();
				
				activity.setLastname(lastname);
				activity.setFirstname(firstname);
				activity.setContractType(contractType);
				activity.setFunction(function);
				activity.setStudentCount(studentCount);
				activity.setHours(hours);
				activity.setCoefficient(coefficient);
				activity.setWeeks(weeks);				
				activity.setTotal(total);
				activity.setActivity(activityType);
				activity.setPillarGE(pillarGE);
				activity.setPillarHES(pillarHES);
				activity.setSector(sector);				
				activity.setDetail(detail);
				activity.setProjectNumber(projectNumber);

				String activityKey = lastname + "_" + firstname + "_" + total;
				activityMap.put(activityKey, activity);
			}
		}
		
		return activityMap;
	}
	
}
