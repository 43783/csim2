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
import ch.hesge.cragsi.model.Accounting;
import ch.hesge.cragsi.model.Activity;
import ch.hesge.cragsi.model.Financial;
import ch.hesge.cragsi.model.Partner;
import ch.hesge.cragsi.model.Price;
import ch.hesge.cragsi.model.Project;
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
	 * @return a Price
	 * @throws IntegrityException 
	 */
	public static Price getActivityPrice(Activity activity, Map<String, Price> priceMap) throws IntegrityException {

		Price activityPrice = null;

		if (!priceMap.containsKey(activity.getContractType())) {
			throw new IntegrityException("missing price for contract '" + activity.getContractType() + "'");
		}
		else {
			activityPrice = priceMap.get(activity.getContractType());
		}

		return activityPrice;
	}

	/**
	 * Retrieve the activity label and include, if required,
	 * the project number associated to the activity.
	 * 
	 * @param activity
	 * @return the activity label
	 */
	public static String getActivityLabel(Activity activity, Account collaboratorAccount) {
		
		String label = activity.getDetail();
		
		// Check if activity is associated to a project
		if (!StringUtils.isEmtpy(activity.getProjectNumber()) && !label.contains(activity.getProjectNumber())) {
			label = activity.getProjectNumber() + "-" + label;
		}
		
		return (collaboratorAccount.getCode() + "-" + collaboratorAccount.getName() + "-" + label).replace("-", " - ");
	}
	
	/**
	 * Retrieve the account associated to a collaborator.
	 * 
	 * @param activity
	 * @param accounts
	 * @return an Account
	 * @throws IntegrityException 
	 */
	public static Account getCollaboratorAccount(Activity activity, List<Account> accounts) throws IntegrityException {

		Account collaboratorAccount = getAccountByName(activity.getLastname(), accounts);

		if (collaboratorAccount == null) {
			throw new IntegrityException("missing account for collaborator '" + activity.getFirstname() + " " + activity.getLastname() + "' with contract '" + activity.getContractType() + "'");
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

		// Retrieve activity project number
		String projectNumber = StringUtils.toNumber(activity.getProjectNumber());
		
		// If not project, lookup for the socle account
		if (StringUtils.isEmtpy(projectNumber)) {
			
			String socleCode = PropertyUtils.getProperty("socleAccount");
			projectAccount = CragsiLogic.getAccountByCode(socleCode, accounts);
			
			if (projectAccount == null) {
				throw new ConfigurationException("missing account with socle code '" + socleCode + "'");
			}
		}
		else {

			// Retrieve project from its number
			Project project = getProjectByCode(projectNumber, projects);

			// Check if project exists
			if (project == null) {
				throw new IntegrityException("missing project with code '" + projectNumber + "'");
			}
			else {

				// The project exists
				Date currentDate = Calendar.getInstance().getTime();

				// Now, check if project is not closed
				if (currentDate.equals(project.getStartDate()) || (currentDate.after(project.getStartDate()) && currentDate.before(project.getEndDate())) || currentDate.equals(project.getEndDate())) {

					// Retrieve full project number
					String accountSuffix = PropertyUtils.getProperty("projectAccountSuffix");
					String fullProjectNumber = accountSuffix + projectNumber;
					
					// Retrieve account associated to the project
					projectAccount = getAccountByCode(fullProjectNumber, accounts);

					// Check if an account for the project exists
					if (projectAccount == null) {
						throw new IntegrityException("missing account with project code '" + fullProjectNumber + "'");
					}
				}
				else {
					throw new IntegrityException("project with code '" + projectNumber + "' is already closed");
				}
			}
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

		// Retrieve full project number
		String accountSuffix = PropertyUtils.getProperty("projectAccountSuffix");
		String fullProjectNumber = accountSuffix + agfLine.getProjectCode();
		
		// Retrieve account associated to the project
		Account projectAccount = getAccountByCode(fullProjectNumber, accounts);

		// Check if an account for the project exists
		if (projectAccount == null) {
			throw new IntegrityException("missing account with project code '" + fullProjectNumber + "'");
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
	public static Account getProjectAccount(Financial funding, List<Account> accounts) throws ConfigurationException, IntegrityException {

		// Retrieve full project number
		String accountSuffix = PropertyUtils.getProperty("projectAccountSuffix");
		String fullProjectNumber = accountSuffix + funding.getProjectCode();
		
		// Retrieve account associated to the project
		Account projectAccount = getAccountByCode(fullProjectNumber, accounts);

		// Check if an account for the project exists
		if (projectAccount == null) {
			throw new IntegrityException("missing account with project code '" + fullProjectNumber + "'");
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

		// Retrieve full project number
		String accountSuffix = PropertyUtils.getProperty("projectAccountSuffix");
		String fullProjectNumber = accountSuffix + partner.getProjectCode();
		
		// Retrieve account associated to the project
		Account projectAccount = getAccountByCode(fullProjectNumber, accounts);

		// Check if an account for the project exists
		if (projectAccount == null) {
			throw new IntegrityException("missing account with project code '" + fullProjectNumber + "'");
		}

		return projectAccount;
	}

	/**
	 * Retrieve the first semester year from an activity list.
	 * 
	 * @param activities 
	 * @return the semester year
	 */
	public static int getFirstSemesterYear(List<Activity> activities) {
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, 100);
		
		for (Activity activity : activities) {
			
			if (activity.getStartContract().before(calendar.getTime())) {
				calendar.setTime(activity.getStartContract());
			}
			
			if (activity.getEndContract().before(calendar.getTime())) {
				calendar.setTime(activity.getEndContract());
			}
		}
		
		return calendar.get(Calendar.YEAR);
	}
	
	/**
	 * Retrieve the second semester year from an activity list.
	 * 
	 * @param activities 
	 * @return the semester year
	 */
	public static int getSecondSemesterYear(List<Activity> activities) {
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, -100);
		
		for (Activity activity : activities) {
			
			if (activity.getStartContract().after(calendar.getTime())) {
				calendar.setTime(activity.getStartContract());
			}
			
			if (activity.getEndContract().after(calendar.getTime())) {
				calendar.setTime(activity.getEndContract());
			}
		}
		
		return calendar.get(Calendar.YEAR);
	}
	
	/**
	 * Retrieve the first semester start date.
	 * 
	 * @param academicYearS1
	 * @return the start date
	 */
	public static Date getFirstSemesterStartDate(int academicYearS1) {

		Calendar calendar = Calendar.getInstance();
		
		calendar.set(Calendar.YEAR, academicYearS1);
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
	 * @param academicYearS1
	 * @return the end date
	 */
	public static Date getFirstSemesterEndDate(int academicYearS1) {

		Calendar calendar = Calendar.getInstance();
		
		calendar.set(Calendar.YEAR, academicYearS1);
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
	 * @param academicYearS2
	 * @return the start date
	 */
	public static Date getSecondSemesterStartDate(int academicYearS2) {
		
		Calendar calendar = Calendar.getInstance();
		
		calendar.set(Calendar.YEAR, academicYearS2);
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
	 * @param academicYearS2
	 * @return the end date
	 */
	public static Date getSecondSemesterEndDate(int academicYearS2) {
		
		Calendar calendar = Calendar.getInstance();
		
		calendar.set(Calendar.YEAR, academicYearS2);
		calendar.set(Calendar.MONTH, 7);
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		
		return new Date(calendar.getTime().getTime());		
	}

	/**
	 * Retrieve the first semester accounting date.
	 *  
	 * @param activity
	 * @param academicYear
	 * @return an accounting date
	 * @throws IntegrityException
	 */
	public static Date getFirstSemesterAccountingDate(Activity activity, int academicYear) throws IntegrityException  {

		Date accountingDate = null;
		Date firstSemesterStartDate = getFirstSemesterStartDate(academicYear);

		// If activity starts before the begin of semester, it's an error
		if (activity.getStartContract().before(firstSemesterStartDate)) {
			throw new IntegrityException("invalid contract start date for collaborator '" + activity.getLastname() + "'");
		}
		
		// If activity starts after the begin of semester, adjust it
		else if (activity.getStartContract().after(firstSemesterStartDate)) {
			accountingDate = activity.getStartContract();
		}
		
		// Otherwise, the activity start at the beginning of the semester
		else {
			accountingDate = firstSemesterStartDate;
		}

		return accountingDate;
	}

	/**
	 * Retrieve the second semester accounting date.
	 * 
	 * @param activity
	 * @param academicYear
	 * @return an accounting date
	 * @throws IntegrityException 
	 */
	public static Date getSecondSemesterAccountingDate(Activity activity, int academicYear) throws IntegrityException {

		Date date = null;
		Date secondSemesterStartDate = getSecondSemesterStartDate(academicYear);
		Date secondSemesterEndDate = getSecondSemesterEndDate(academicYear);

		// If activity ends after the end of semester, it's an error
		if (activity.getEndContract().after(secondSemesterEndDate)) {
			throw new IntegrityException("invalid contract end date for collaborator '" + activity.getLastname() + "'");
		}
		
		// If activity starts after the begin of semester, adjust it
		else if (activity.getStartContract().after(secondSemesterStartDate)) {
			date = activity.getStartContract();
		}

		// Otherwise, the activity start at the beginning of the semester
		else {
			date = secondSemesterStartDate;
		}

		return date;
	}
	
	/**
	 * Create a debit accounting entry.
	 * 
	 * @param sequenceId
	 * @param date
	 * @param journalId
	 * @param periodId
	 * @param account
	 * @param label
	 * @param value
	 * @return
	 */
	public static Accounting createDebitEntry(int sequenceId, Date date, String journalId, String periodId, Account account, String label, double value) {

		Accounting accounting = new Accounting();

		accounting.setId(sequenceId);
		accounting.setDate(date);
		accounting.setJournalId(journalId);
		accounting.setName(StringUtils.toString(sequenceId));
		accounting.setPeriodId(periodId);
		accounting.setAccountId(account.getId());
		accounting.setLineDate(date);
		accounting.setLineName(label);
		accounting.setLineDebit(value);
		accounting.setLineJournalId(journalId);
		accounting.setLinePeriodId(periodId);

		return accounting;
	}

	/**
	 * Create a credit accounting entry.
	 * 
	 * @param sequenceId
	 * @param date
	 * @param journalId
	 * @param periodId
	 * @param account
	 * @param label
	 * @param value
	 * @return
	 */
	public static Accounting createCreditEntry(int sequenceId, Date date, String journalId, String periodId, Account account, String label, double value) {

		Accounting accounting = new Accounting();

		accounting.setAccountId(account.getId());
		accounting.setLineDate(date);
		accounting.setLineName(label);
		accounting.setLineCredit(value);
		accounting.setLineJournalId(journalId);
		accounting.setLinePeriodId(periodId);

		return accounting;
	}
}
