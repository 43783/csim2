package ch.hesge.cragsi.loader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import ch.hesge.cragsi.dao.AccountDao;
import ch.hesge.cragsi.dao.ActivityDao;
import ch.hesge.cragsi.dao.ProjectDao;
import ch.hesge.cragsi.model.Account;
import ch.hesge.cragsi.model.Activity;
import ch.hesge.cragsi.model.Project;

public class CragLoader {

	/**
	 * Default constructor
	 */
	public CragLoader() {
	}

	public void start() {
		
		try {
			
			System.out.println("----------------------------------");
			System.out.println(" Account list:");
			System.out.println("----------------------------------");

			List<Account> accounts = AccountDao.findAll();

			for (Account account : accounts) {
				
			}

			System.out.println("----------------------------------");
			System.out.println(" Activity list:");
			System.out.println("----------------------------------");

			List<Activity> activities = ActivityDao.findAll();

			for (Activity activity : activities) {
				
				System.out.println("name: " + activity.getFirstname() + " " + activity.getLastname());
				System.out.println("  unit: " + activity.getUnit());
				System.out.println("  contractType: " + activity.getContract());
				System.out.println("  function: " + activity.getFunction());
				System.out.println("  studentCount: " + activity.getStudentCount());
				System.out.println("  hours: " + activity.getHours());
				System.out.println("  coefficient: " + activity.getCoefficient());
				System.out.println("  weeks: " + activity.getWeeks());
				System.out.println("  total: " + activity.getTotal());
				System.out.println("  activityType: " + activity.getActivity());
				System.out.println("  gePillar: " + activity.getPillarGe());
				System.out.println("  hegPillar: " + activity.getPillarHeg());
				System.out.println("  studyType: " + activity.getStudyType());
				System.out.println("  detail: " + activity.getDetail());
				System.out.println("  projectNumber: " + activity.getProjectNumber());
			}
			
			System.out.println("----------------------------------");
			System.out.println(" Project list:");
			System.out.println("----------------------------------");

			List<Project> projects = ProjectDao.findAll();

			for (Project project : projects) {
				
				System.out.println("code: " + project.getCode());
				System.out.println("  date: " + project.getDate());
				System.out.println("  start: " + project.getStartDate());
				System.out.println("  end: " + project.getEndDate());
				System.out.println("  description: " + project.getDescription());
				System.out.println("  statut: " + project.getStatus());
			}
				
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Main entry point
	 * @param args
	 */
	public static void main(String[] args) {
		new CragLoader().start();
	}

}
