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
	 * Retrieve the label to use in accountings generation.
	 * 
	 * @param activity
	 * @param collaboratorAccount
	 * @param academicYearS1
	 * @param academicYearS2
	 * @return the activity label
	 */
	public static String getAccountingLabel(Activity activity, Account collaboratorAccount, int academicYearS1, int academicYearS2) {
		
		String label = activity.getDetail();
		
		// Check if activity is associated to a project
		if (!StringUtils.isEmtpy(activity.getProjectNumber()) && !label.contains(activity.getProjectNumber())) {
			label = activity.getProjectNumber() + "-" + label;
		}
		
		// Add collaborator references
		label = (collaboratorAccount.getCode() + "-" + collaboratorAccount.getName() + "-" + label).replace("-", " - ");
		
		// Add academic years
		label += String.format(" (FDC %04d-%04d)", academicYearS1, academicYearS2);
		
		return label;
	}
	
	/**
	 * Retrieve the label to use in accountings generation.
	 * 
	 * @param agfLine
	 * @return an accounting label
	 */
	public static String getAccountingLabel(AGFLine agfLine) {
		return agfLine.getLibelle() + " (AGF)";
	}
	
	/**
	 * Retrieve the label to use in accountings generation.
	 * 
	 * @param financial
	 * @return an accounting label
	 */
	public static String getAccountingLabel(Financial financial) {
		return financial.getLibelle() + " (AGP)";
	}

	/**
	 * Retrieve the label to use in accountings generation.
	 * 
	 * @param partner
	 * @return an accounting label
	 */
	public static String getAccountingLabel(Partner partner) {
		return partner.getName() + " (AGP)";
	}

	/**
	 * Retrieve the account associated to a collaborator.
	 * 
	 * @param activity
	 * @param accounts
	 * @return an Account
	 * @throws IntegrityException 
	 * @throws  
	 */
	public static Account getCollaboratorAccount(Activity activity, List<Account> accounts) throws ConfigurationException, IntegrityException {

		Account collaboratorAccount = null;
		String accountSuffix = PropertyUtils.getProperty("collaboratorAccountSuffix");
		
		for (Account account : accounts) {

			if (account.getName().toLowerCase().contains(activity.getLastname().toLowerCase()) && account.getCode().startsWith(accountSuffix)) {
				collaboratorAccount = account;
				break;
			}
		}
		
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

				// Check if project as a startdate
				if (project.getStartDate() == null) {
					throw new IntegrityException("project with code '" + projectNumber + "' has no start date");
				}
				
				// Check if project is not already started
				if (currentDate.before(project.getStartDate())) {
					throw new IntegrityException("project with code '" + projectNumber + "' is not yet started");
				}

				// Check if project is already closed
				if (project.getEndDate() != null && currentDate.after(project.getEndDate())) {
					throw new IntegrityException("project with code '" + projectNumber + "' is already closed");
				}
				
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

		// Scan activities and retrieve the earliest date
		for (Activity activity : activities) {
			if (activity.getStartContract().before(calendar.getTime())) {
				calendar.setTime(activity.getStartContract());
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
		
		// Scan activities and retrieve the latest date
		for (Activity activity : activities) {
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
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

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
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		return new Date(calendar.getTime().getTime());		
	}

	/**
	 * Retrieve the first semester accounting date for a specific activity.
	 *  
	 * @param activity
	 * @param startDate
	 * @param endDate
	 * @return an accounting date
	 * @throws IntegrityException
	 */
	public static Date getFirstSemesterAccountingDate(Activity activity, Date startDate, Date endDate) throws IntegrityException  {

		Date accountingDate = null;

		// If the activity starts before the begin of semester, it's an error
		if (activity.getStartContract() == null || activity.getStartContract().before(startDate)) {
			throw new IntegrityException("invalid contract start date for collaborator '" + activity.getLastname() + "'");
		}
		
		// If the activity starts after the begin of first semester, adjust the date
		else if (activity.getStartContract().after(startDate)) {
			accountingDate = activity.getStartContract();
		}
		
		// In all other cases, the activity start at the beginning of the semester
		else {
			accountingDate = startDate;
		}

		return accountingDate;
	}

	/**
	 * Retrieve the second semester accounting date for a specific activity.
	 * 
	 * @param activity
	 * @param startDate
	 * @param endDate
	 * @return an accounting date
	 * @throws IntegrityException 
	 */
	public static Date getSecondSemesterAccountingDate(Activity activity, Date startDate, Date endDate) throws IntegrityException {

		Date accountingDate = null;

		// If the activity ends after the end of semester, it's an error
		if (activity.getEndContract() != null && activity.getEndContract().after(endDate)) {
			throw new IntegrityException("invalid contract end date for collaborator '" + activity.getLastname() + "'");
		}
		
		// If the activity starts after the begin of semester, adjust the date
		else if (activity.getStartContract().after(startDate)) {
			accountingDate = activity.getStartContract();
		}

		// In all other cases, the activity start at the beginning of the semester
		else {
			accountingDate = startDate;
		}

		return accountingDate;
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
