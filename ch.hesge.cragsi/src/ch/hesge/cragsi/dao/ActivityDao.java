package ch.hesge.cragsi.dao;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import ch.hesge.cragsi.model.Account;
import ch.hesge.cragsi.model.Activity;
import ch.hesge.cragsi.utils.CsvReader;
import ch.hesge.cragsi.utils.StringUtils;

/**
 * Class responsible to manage DAO access for Activity.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class ActivityDao {

	private static String accountPath = "res/activities.csv";

	/**
	 * Retrieve all activities contained in file
	 * @return
	 * @throws IOException
	 */
	public static List<Activity> findAll() throws IOException {

		List<Activity> activityList = new ArrayList<>();

		CsvReader reader = new CsvReader(accountPath, ',', Charset.forName("UTF8"));
		reader.readHeaders();

		while (reader.readRecord()) {

			String unit = StringUtils.clean(reader.get("Unité"));
			String lastname = StringUtils.clean(reader.get("Nom"));
			String firstname = StringUtils.clean(reader.get("Prénom"));
			String contractType = StringUtils.clean(reader.get("Type Contrat"));
			String function = StringUtils.clean(reader.get("Fonction"));
			String studentCount = StringUtils.clean(reader.get("Nbr Etud."));
			String hours = StringUtils.clean(reader.get("Heures"));
			String coefficient = StringUtils.clean(reader.get("Coefficient"));
			String weeks = StringUtils.clean(reader.get("Semaine-s"));
			String total = StringUtils.clean(reader.get("Total"));
			String activityType = StringUtils.clean(reader.get("Activité"));
			String gePillar = StringUtils.clean(reader.get("Pilier GE"));
			String hegPillar = StringUtils.clean(reader.get("Pilier HES"));
			String studyType = StringUtils.clean(reader.get("Filière"));
			String detail = StringUtils.clean(reader.get("Détail"));
			String projectNumber = StringUtils.clean(reader.get("Num. projet"));
			
			Activity activity = new Activity();
			activity.setUnit(unit);
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
			activity.setGePillar(gePillar);
			activity.setHegPillar(hegPillar);
			activity.setStudyType(studyType);
			activity.setDetail(detail);
			activity.setProjectNumber(projectNumber);

			activityList.add(activity);
		}

		reader.close();
		
		return activityList;
	}
}
