package ch.hesge.cragsi.loader;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ch.hesge.cragsi.dao.AccountDao;
import ch.hesge.cragsi.dao.ProjectDao;
import ch.hesge.cragsi.model.AGFLine;
import ch.hesge.cragsi.model.Account;
import ch.hesge.cragsi.model.Activity;
import ch.hesge.cragsi.model.Funding;
import ch.hesge.cragsi.model.Partner;
import ch.hesge.cragsi.model.Price;
import ch.hesge.cragsi.model.Project;
import ch.hesge.cragsi.utils.DateFactory;
import ch.hesge.cragsi.utils.StringUtils;

public class CragsiLogic {

	/**
	 * Retrieve a property from configuration file.
	 * 
	 * @param name the property name
	 * @return the value found
	 */
	public static String getProperty(String name) {
		
		String propertyValue = UserSettings.getInstance().getProperty(name);
		
		if (propertyValue == null) {
			throw new IllegalArgumentException("==> missing property '" + name + "' in configuration file !");
		}
		
		return propertyValue;
	}
	
	/**
	 * Retrieve the price of an activity.
	 * 
	 * @param activity an Activity
	 * @return a Price or null
	 */
	public static Price getActivityPrice(Activity activity, Map<String, Price> priceMap) {
		
		Price activityPrice = null;
		
		if (!priceMap.containsKey(activity.getContractType())) {
			System.out.println("==> missing contract '" + activity.getContractType() + "' !");
		}
		else {
			activityPrice = priceMap.get(activity.getContractType());
		}
		
		return activityPrice;
	}
	
	/**
	 * 
	 * @param activity
	 * @param accounts
	 * @param projects
	 * @return
	 */
	public static Account getCollaboratorAccount(Activity activity, List<Account> accounts) {

		Account collaboratorAccount = AccountDao.findByName(activity.getLastname(), accounts);
		
		if (collaboratorAccount == null) {
			System.out.println("==> missing collaborator account for '" + activity.getFirstname() + " " + activity.getLastname() + "' with contract '" + activity.getContractType() + "' !");
		}
		
		return collaboratorAccount;
	}
	
	/**
	 * 
	 * @param activity
	 * @param accounts
	 * @param projects
	 * @return
	 */
	public static Account getProjectAccount(Activity activity, List<Project> projects, List<Account> accounts) {
		
		// Retrieve the collaborator account
		Account collaboratorAccount = getCollaboratorAccount(activity, accounts);

		if (collaboratorAccount == null) {
			return null;
		}
		
		// Retrieve FDC project account
		Account projectAccount = null;
		String projectNumber = StringUtils.toNumber(activity.getProjectNumber());
		if (!StringUtils.isEmtpy(projectNumber)) {

			// Retrieve project from its number
			Project project = ProjectDao.findByCode(projectNumber, projects);

			// Check if project exists
			if (project == null) {
				System.out.println("==> missing project with code '" + projectNumber + "' !");
				return null;
			}
			else {

				Date currentDate = Calendar.getInstance().getTime();

				// Check if project is not closed
				if (currentDate.equals(project.getStartDate()) || (currentDate.after(project.getStartDate()) && currentDate.before(project.getEndDate())) || currentDate.equals(project.getEndDate())) {

					String accountSuffix = UserSettings.getInstance().getProperty("projectAccountSuffix");
					projectAccount = AccountDao.findByCode(accountSuffix + projectNumber, accounts);

					// Check if an account for the project exists
					if (projectAccount == null) {
						System.out.println("==> missing project account with code '" + projectNumber + "' !");
						return null;
					}
				}
				else {
					System.out.println("==> project with code '" + projectNumber + "' is already closed !");
					return null;
				}
			}
		}
		
		return projectAccount;
	}
	
	public static Account getProjectAccount(AGFLine agfLine, List<Account> accounts) {
		
		String accountSuffix = UserSettings.getInstance().getProperty("projectAccountSuffix");
		Account projectAccount = AccountDao.findByCode(accountSuffix + agfLine.getProjectNumber(), accounts);
		
		// Check if an account for the project exists
		if (projectAccount == null) {
			System.out.println("==> missing project account with code '" + agfLine.getProjectNumber() + "' !");
		}
		
		return projectAccount;
	}
	
	public static Account getProjectAccount(Funding funding, List<Account> accounts) {
		
		String accountSuffix = UserSettings.getInstance().getProperty("projectAccountSuffix");
		Account projectAccount = AccountDao.findByCode(accountSuffix + funding.getProjectNumber(), accounts);

		// Check if an account for the project exists
		if (projectAccount == null) {
			System.out.println("==> missing project account with code '" + funding.getProjectNumber() + "' !");
		}

		return projectAccount;
	}

	public static Account getProjectAccount(Partner partner, List<Account> accounts) {

		String accountSuffix = UserSettings.getInstance().getProperty("projectAccountSuffix");
		Account projectAccount = AccountDao.findByCode(accountSuffix + partner.getProjectNumber(), accounts);

		// Check if an account for the project exists
		if (projectAccount == null) {
			System.out.println("==> missing project account with code '" + partner.getProjectNumber() + "' !");
		}
		
		return projectAccount;
	}
	
	public static Date getFirstSemesterAccountingDate(Activity activity) {
		
		Date firstSemesterStartDate = DateFactory.getFirstSemesterStartDate();
		Date firstSemesterEndDate = DateFactory.getFirstSemesterEndDate();

		// Calculate accounting date for first semester
		Date accountingDateS1 = firstSemesterStartDate;
		
		if (activity.getStartContract().before(firstSemesterStartDate)) {
			System.out.println("==> invalid contract start date for collaborator '" + activity.getLastname() + " !");
			return null;
		}
		else if (activity.getStartContract().after(firstSemesterStartDate)) {
			accountingDateS1 = activity.getStartContract();
		}
		
		return accountingDateS1;
	}
	
	public static Date getSecondSemesterAccountingDate(Activity activity) {
		
		Date secondSemesterStartDate = DateFactory.getSecondSemesterStartDate();
		Date secondSemesterEndDate = DateFactory.getSecondSemesterEndDate();

		// Calculate accounting date for second semester
		Date accountingDateS2 = secondSemesterStartDate;
		if (activity.getEndContract().after(secondSemesterEndDate)) {
			System.out.println("==> invalid contract end date for collaborator '" + activity.getLastname() + " !");
			return null;
		}
		else if (activity.getStartContract().after(secondSemesterStartDate)) {
			accountingDateS2 = activity.getStartContract();
		}
		
		return accountingDateS2;
	}
}
