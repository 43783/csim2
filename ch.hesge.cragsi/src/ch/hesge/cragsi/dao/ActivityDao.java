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
import ch.hesge.cragsi.utils.StringUtils;

public class ActivityDao {

	/**
	 * 
	 * @param activity
	 * @return
	 */
	private static String getActivityKey(Activity activity) {
		return activity.getLastname() + "_" + activity.getFirstname();
	}

	/**
	 * Retrieve all activities contained in file
	 * @return
	 * @throws IOException
	 */
	public static List<Activity> findAll() throws IOException {

		List<Activity> activityList = new ArrayList<>();

		// Load activity map for all collaborator
		Map<String, List<Activity>> activityMap = loadActivityDetailMap();

		// Now load splitted times for all activities (S1 and S2)
		Map<String, Activity> activityTimeMap = loadActivityTimeMap();

		// Finally update S1 and S2 time for each activity
		for (String activityKey : activityMap.keySet()) {

			for (Activity activity : activityMap.get(activityKey)) {

				Activity activityTime = activityTimeMap.get(activityKey);

				activity.setTotalS1(activityTime.getTotalS1());
				activity.setTotalS2(activityTime.getTotalS2());

				activityList.add(activity);
			}
		}

		return activityList;
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	private static Map<String, List<Activity>> loadActivityDetailMap() throws IOException {

		Map<String, List<Activity>> activityMap = new HashMap<>();
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

			// Select only activities with C1 and C2 pillar GE
			if (pillarGE.equalsIgnoreCase("C1") || pillarGE.equalsIgnoreCase("C2")) {

				Activity activity = new Activity();

				activity.setUnit(unit);
				activity.setLastname(lastname);
				activity.setFirstname(firstname);
				activity.setContractType(contractType);
				activity.setFunction(function);
				activity.setStudentCount(StringUtils.toInteger(studentCount));
				activity.setHours(StringUtils.toDouble(hours));
				activity.setCoefficient(StringUtils.toDouble(coefficient));
				activity.setWeeks(StringUtils.toDouble(weeks));
				activity.setTotal(StringUtils.toDouble(total));
				activity.setActivity(activityType);
				activity.setPillarGE(pillarGE);
				activity.setPillarHES(pillarHES);
				activity.setSector(sector);
				activity.setDetail(detail);
				activity.setProjectNumber(projectNumber);

				String activityKey = getActivityKey(activity);

				// Initialize entry for current activity, if not already done
				if (!activityMap.containsKey(activityKey)) {
					activityMap.put(activityKey, new ArrayList<Activity>());
				}

				// Register the activity
				activityMap.get(activityKey).add(activity);
			}
		}

		return activityMap;
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	private static Map<String, Activity> loadActivityTimeMap() throws IOException {

		Map<String, Activity> activityMap = new HashMap<>();
		String activityDetailPath = UserSettings.getInstance().getProperty("activityPath");

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

			String lastname = reader.get(1);
			String firstname = reader.get(2);
			String pillarGE = reader.get(10);
			String totalS1 = reader.get(18);
			String totalS2 = reader.get(20);

			// Select only activities with C1 and C2 pillar GE
			if (pillarGE.equalsIgnoreCase("C1") || pillarGE.equalsIgnoreCase("C2")) {

				Activity activity = new Activity();

				activity.setLastname(lastname);
				activity.setFirstname(firstname);
				activity.setTotalS1(StringUtils.toDouble(totalS1));
				activity.setTotalS2(StringUtils.toDouble(totalS2));

				String activityKey = getActivityKey(activity);

				// Register the activity, if not already done
				if (!activityMap.containsKey(activityKey)) {
					activityMap.put(activityKey, activity);
				}
			}
		}

		return activityMap;
	}
}
