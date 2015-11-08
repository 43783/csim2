package ch.hesge.cragsi.loader;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.hesge.cragsi.dao.PriceDao;
import ch.hesge.cragsi.exceptions.ConfigurationException;
import ch.hesge.cragsi.exceptions.IntegrityException;
import ch.hesge.cragsi.model.AGFLine;
import ch.hesge.cragsi.model.Account;
import ch.hesge.cragsi.model.Activity;
import ch.hesge.cragsi.model.Funding;
import ch.hesge.cragsi.model.Partner;
import ch.hesge.cragsi.model.Price;
import ch.hesge.cragsi.model.Project;
import ch.hesge.cragsi.utils.DateFactory;
import ch.hesge.cragsi.utils.PropertyUtils;
import ch.hesge.cragsi.utils.StringUtils;

/**
 * Class responsible to implement business logic
 * use throughout the application.
 * 
 * Copyright HEG Geneva 2015, Switzerland
 * @author Eric Harth
 */
public class CragsiLogic {

	/**
	 * Retrieve a single project based on its code.
	 * If an project match exactly the code, it is returned.
	 * 
	 * @param code the project code name to find
	 * @param projects the list of project to use while scanning code
	 * @return a Project or null
	 */
	public static Project getProjectByCode(String code, List<Project> projects) {

		for (Project project : projects) {

			if (project.getCode().toLowerCase().equals(code.toLowerCase())) {
				return project;
			}
		}

		return null;
	}

	/**
	 * Retrieve a single account based on its name.
	 * If an account match partially the name (that is it contains part of the name), it is returned.
	 * 
	 * @param name the account name to find
	 * @param accounts the list of account to use while scanning name
	 * @return an Account or null
	 */
	public static Account getAccountByName(String name, List<Account> accounts) {

		for (Account account : accounts) {

			if (account.getName().toLowerCase().contains(name.toLowerCase())) {
				return account;
			}
		}

		return null;
	}
	
	/**
	 * Retrieve a single account based on its code.
	 * If an account match exactly the code, it is returned.
	 * 
	 * @param code the account code to find
	 * @param accounts the list of account to use while scanning name
	 * @return an Account or null
	 */
	public static Account getAccountByCode(String code, List<Account> accounts) {

		for (Account account : accounts) {

			if (account.getCode().toLowerCase().equals(code.toLowerCase())) {
				return account;
			}
		}

		return null;
	}

	/**
	 * Retrieve a map of all activity prices.
	 *  
	 * @return
	 * @throws IOException
	 * @throws ConfigurationException 
	 */
	public static Map<String, Price> getActivityPriceMap() throws IOException, ConfigurationException {

		Map<String, Price> priceMap = new HashMap<>();

		for (Price price : PriceDao.findAll()) {

			if (!priceMap.containsKey(price.getLibelle())) {
				priceMap.put(price.getLibelle(), price);
			}
		}

		return priceMap;
	}
	
	/**
	 * Retrieve the price of an activity.
	 * 
	 * @param activity
	 *        an Activity
	 * @return a Price or null
	 * @throws IntegrityException 
	 */
	public static Price getActivityPrice(Activity activity, Map<String, Price> priceMap) throws IntegrityException {

		Price activityPrice = null;

		if (!priceMap.containsKey(activity.getContractType())) {
			throw new IntegrityException("==> missing price for contract '" + activity.getContractType() + "' !");
		}
		else {
			activityPrice = priceMap.get(activity.getContractType());
		}

		return activityPrice;
	}

	/**
	 * Retrieve the account associated to a collaborator.
	 * 
	 * @param activity
	 * @param accounts
	 * @param projects
	 * @return an Account
	 * @throws IntegrityException 
	 */
	public static Account getCollaboratorAccount(Activity activity, List<Account> accounts) throws IntegrityException {

		Account collaboratorAccount = getAccountByName(activity.getLastname(), accounts);

		if (collaboratorAccount == null) {
			throw new IntegrityException("==> missing collaborator account for '" + activity.getFirstname() + " " + activity.getLastname() + "' with contract '" + activity.getContractType() + "' !");
		}

		return collaboratorAccount;
	}

	/**
	 * Retrieve the project account associated to the Activity.
	 * 
	 * @param activity
	 * @param accounts
	 * @param projects
	 * @return
	 * @throws ConfigurationException 
	 * @throws IntegrityException 
	 */
	public static Account getProjectAccount(Activity activity, List<Project> projects, List<Account> accounts) throws ConfigurationException, IntegrityException {

		Account projectAccount = null;

		// Retrieve the collaborator account
		Account collaboratorAccount = getCollaboratorAccount(activity, accounts);

		// Retrieve activity project number
		String projectNumber = StringUtils.toNumber(activity.getProjectNumber());
		
		if (StringUtils.isEmtpy(projectNumber)) {
		if (!StringUtils.isEmtpy(projectNumber)) {

			// Retrieve project from its number
			Project project = getProjectByCode(projectNumber, projects);

			// Check if project exists
			if (project == null) {
				throw new IllegalArgumentException("==> missing project with code '" + projectNumber + "' !");
			}
			else {

				Date currentDate = Calendar.getInstance().getTime();

				// Check if project is not closed
				if (currentDate.equals(project.getStartDate()) || (currentDate.after(project.getStartDate()) && currentDate.before(project.getEndDate())) || currentDate.equals(project.getEndDate())) {

					String accountSuffix = PropertyUtils.getProperty("projectAccountSuffix");
					projectAccount = getAccountByCode(accountSuffix + projectNumber, accounts);

					// Check if an account for the project exists
					if (projectAccount == null) {
						throw new IntegrityException("==> missing project account with code '" + projectNumber + "' !");
					}
				}
				else {
					throw new IntegrityException("==> project with code '" + projectNumber + "' is already closed !");
				}
			}

			/*
			// Retrieve the socle account
			String socleCode = PropertyUtils.getProperty("socleAccount");
			socleAccount = CragsiLogic.getAccountByCode(socleCode, accounts);
			if (socleAccount == null) {
				throw new ConfigurationException("==> missing socle account with code '" + socleCode + "' !");
			}
			*/
			
		}

		return projectAccount;
	}

	/**
	 * Retrieve the project account associated to the AGF line.
	 * 
	 * @param agfLine
	 * @param accounts
	 * @return an Account
	 * @throws ConfigurationException 
	 * @throws IntegrityException 
	 */
	public static Account getProjectAccount(AGFLine agfLine, List<Account> accounts) throws ConfigurationException, IntegrityException {

		String accountSuffix = PropertyUtils.getProperty("projectAccountSuffix");
		Account projectAccount = getAccountByCode(accountSuffix + agfLine.getProjectNumber(), accounts);

		// Check if an account for the project exists
		if (projectAccount == null) {
			throw new IntegrityException("==> missing project account with code '" + agfLine.getProjectNumber() + "' !");
		}

		return projectAccount;
	}

	/**
	 * Retrieve the project account associated to a Funding.
	 * 
	 * @param funding
	 * @param accounts
	 * @return an Account
	 * @throws ConfigurationException 
	 * @throws IntegrityException 
	 */
	public static Account getProjectAccount(Funding funding, List<Account> accounts) throws ConfigurationException, IntegrityException {

		String accountSuffix = PropertyUtils.getProperty("projectAccountSuffix");
		Account projectAccount = getAccountByCode(accountSuffix + funding.getProjectNumber(), accounts);

		// Check if an account for the project exists
		if (projectAccount == null) {
			throw new IntegrityException("==> missing project account with code '" + funding.getProjectNumber() + "' !");
		}

		return projectAccount;
	}

	/**
	 * Retrieve the project account associated to a Partner.
	 * 
	 * @param partner
	 * @param accounts
	 * @return an Account
	 * @throws ConfigurationException 
	 * @throws IntegrityException 
	 */
	public static Account getProjectAccount(Partner partner, List<Account> accounts) throws ConfigurationException, IntegrityException {

		String accountSuffix = PropertyUtils.getProperty("projectAccountSuffix");
		Account projectAccount = getAccountByCode(accountSuffix + partner.getProjectNumber(), accounts);

		// Check if an account for the project exists
		if (projectAccount == null) {
			throw new IntegrityException("==> missing project account with code '" + partner.getProjectNumber() + "' !");
		}

		return projectAccount;
	}

	/**
	 *  Retrieve the first semester accounting date.
	 *  
	 * @param activity
	 * @return an accounting date
	 * @throws IntegrityException 
	 * @throws  
	 */
	public static Date getFirstSemesterAccountingDate(Activity activity) throws ConfigurationException, IntegrityException  {

		Date firstSemesterStartDate = DateFactory.getFirstSemesterStartDate();
		Date firstSemesterEndDate   = DateFactory.getFirstSemesterEndDate();

		Date date = firstSemesterStartDate;

		// Calculate accounting date for first semester
		if (activity.getStartContract().before(firstSemesterStartDate)) {
			throw new IntegrityException("==> invalid contract start date for collaborator '" + activity.getLastname() + " !");
		}
		else if (activity.getStartContract().after(firstSemesterStartDate)) {
			date = activity.getStartContract();
		}

		return date;
	}

	/**
	 * Retrieve the second semester accounting date.
	 * 
	 * @param activity
	 * @return an accounting date
	 * @throws IntegrityException 
	 */
	public static Date getSecondSemesterAccountingDate(Activity activity) throws ConfigurationException, IntegrityException {

		Date secondSemesterStartDate = DateFactory.getSecondSemesterStartDate();
		Date secondSemesterEndDate   = DateFactory.getSecondSemesterEndDate();

		Date date = secondSemesterStartDate;

		// Calculate accounting date for second semester
		if (activity.getEndContract().after(secondSemesterEndDate)) {
			throw new IntegrityException("==> invalid contract end date for collaborator '" + activity.getLastname() + " !");
		}
		else if (activity.getStartContract().after(secondSemesterStartDate)) {
			date = activity.getStartContract();
		}

		return date;
	}
}
