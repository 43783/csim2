package ch.hesge.cragsi.loader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ch.hesge.cragsi.dao.AGFLineDao;
import ch.hesge.cragsi.dao.AccountDao;
import ch.hesge.cragsi.dao.AccountingDao;
import ch.hesge.cragsi.dao.ActivityDao;
import ch.hesge.cragsi.dao.FundingDao;
import ch.hesge.cragsi.dao.PartnerDao;
import ch.hesge.cragsi.dao.ProjectDao;
import ch.hesge.cragsi.exceptions.ConfigurationException;
import ch.hesge.cragsi.model.AGFLine;
import ch.hesge.cragsi.model.Account;
import ch.hesge.cragsi.model.Accounting;
import ch.hesge.cragsi.model.Activity;
import ch.hesge.cragsi.model.Funding;
import ch.hesge.cragsi.model.Partner;
import ch.hesge.cragsi.model.Price;
import ch.hesge.cragsi.model.Project;
import ch.hesge.cragsi.utils.AccountingFactory;
import ch.hesge.cragsi.utils.DateFactory;
import ch.hesge.cragsi.utils.PropertyUtils;
import ch.hesge.cragsi.utils.StringUtils;

/**
 * Main class used to load CRAGSI data from
 * files and generate ODOO accounting file.
 *  
 * Copyright HEG Geneva 2015, Switzerland
 * @author Eric Harth
 */
public class CragsiLoader {

	// Private attributes
	private int accountingSequence;

	private String firstSemesterJournalId;
	private String secondSemesterjournalId;
	private String firstSemesterPeriodId;
	private String secondSemesterPeriodId;

	private Account salaryAccount;
	private Account allProjectsAccount;

	private List<Account> accounts;
	private List<Project> projects;
	private Map<String, Price> priceMap;
	private List<Accounting> accountings;

	/**
	 * Default constructor
	 */
	public CragsiLoader() {
	}

	/**
	 * Initialize the loader.
	 * 
	 * @throws IOException
	 * @throws ConfigurationException 
	 */
	public void init() throws IOException, ConfigurationException {

		accountingSequence = 1;
		accountings = new ArrayList<>();

		// Retrieve global properties from config file
		firstSemesterJournalId  = PropertyUtils.getProperty("journalId_S1");
		firstSemesterPeriodId   = PropertyUtils.getProperty("periodId_S1");
		secondSemesterjournalId = PropertyUtils.getProperty("journalId_S2");
		secondSemesterPeriodId  = PropertyUtils.getProperty("periodId_S2");

		// Load common files
		accounts = AccountDao.findAll();
		projects = ProjectDao.findAll();
		priceMap = CragsiLogic.getActivityPriceMap();

		// Retrieve the projects account
		String allProjectsCode = PropertyUtils.getProperty("projectsAccount");
		allProjectsAccount = CragsiLogic.getAccountByCode(allProjectsCode, accounts);
		if (allProjectsAccount == null) {
			throw new ConfigurationException("==> missing projects account with code '" + allProjectsCode + "' !");
		}

		// Retrieve the salaries account
		String salaryCode = PropertyUtils.getProperty("salaryAccount");
		salaryAccount = CragsiLogic.getAccountByCode(salaryCode, accounts);
		if (salaryAccount == null) {
			throw new ConfigurationException("==> missing salaries account with code '" + salaryCode + "' !");
		}
	}

	/**
	 * Start loading all files and genered all accountings.
	 * @throws IOException
	 * @throws ConfigurationException 
	 */
	public void start() throws IOException, ConfigurationException {

		// Generate FDC accountings
		generateFDCAccountings();

		// Generate AGF accountings
		generateAGFAccountings();

		// Generate funding accountings
		generateFundingAccountings();

		// Generate partner accountings
		generatePartnerAccountings();

		// Finally save accountings
		AccountingDao.saveAll(accountings);
	}

	/**
	 * Generate activities accountings
	 * @throws ConfigurationException 
	 */
	private void generateFDCAccountings() throws IOException, ConfigurationException {

		System.out.println("Generating FDC accounting...");

		for (Activity activity : ActivityDao.findAll()) {

			try {
				Price activityPrice = CragsiLogic.getActivityPrice(activity, priceMap);
				
				if (activity != null) {
					
					// Retrieve the collaborator account
					Account collaboratorAccount = CragsiLogic.getCollaboratorAccount(activity, accounts);
	
					// Retrieve the project account
					Account projectAccount = CragsiLogic.getProjectAccount(activity, projects, accounts);
	
					if (collaboratorAccount != null && projectAccount != null) {
						
						// Retrieve accounting dates
						Date firstSemesterAccountingDate = CragsiLogic.getFirstSemesterAccountingDate(activity);
						Date secondSemesterAccountingDate = CragsiLogic.getSecondSemesterAccountingDate(activity);					
	
						// Calculate activity costs
						activity.setCostS1(activity.getTotalS1() * activityPrice.getPrice());
						activity.setCostS2(activity.getTotalS2() * activityPrice.getPrice());
	
						// Adjust activityLabel with project number
						String activityLabel = activity.getDetail();
						if (!StringUtils.isEmtpy(activity.getProjectNumber()) && !activityLabel.contains(activity.getProjectNumber())) {
							activityLabel = activity.getProjectNumber() + "-" + activityLabel;
						}
	
						// Calculate accounting labels
						String accountingLabel = (collaboratorAccount.getCode() + "-" + collaboratorAccount.getName() + "-" + activityLabel).replace("-", " - ");
						String academicPeriodYearS1 = PropertyUtils.getProperty("academicPeriod_S1");
						String academicPeriodYearS2 = PropertyUtils.getProperty("academicPeriod_S2");
						String accountingLabelS1 = accountingLabel + " (" + academicPeriodYearS1 + ")";
						String accountingLabelS2 = accountingLabel + " (" + academicPeriodYearS2 + ")";
						
						// ==> Generate accounting for first semester, if accounting date is within range
						Date firstSemesterStartDate = DateFactory.getFirstSemesterStartDate();
						Date firstSemesterEndDate = DateFactory.getFirstSemesterEndDate();
						if (activity.getCostS1() != 0 && !firstSemesterAccountingDate.before(firstSemesterStartDate) && !firstSemesterAccountingDate.after(firstSemesterEndDate)) {
	
							// Collaborator accountings
							accountings.add(AccountingFactory.createDebitEntry(accountingSequence, firstSemesterAccountingDate, firstSemesterJournalId, firstSemesterPeriodId, collaboratorAccount, accountingLabelS1, activity.getCostS1()));
							accountings.add(AccountingFactory.createCreditEntry(accountingSequence, firstSemesterAccountingDate, firstSemesterJournalId, firstSemesterPeriodId, salaryAccount, accountingLabelS1, activity.getCostS1()));
							accountingSequence++;
	
							// Check if activity is associated to a project
							if (projectAccount != null) {
								accountings.add(AccountingFactory.createDebitEntry(accountingSequence, firstSemesterAccountingDate, firstSemesterJournalId, firstSemesterPeriodId, projectAccount, accountingLabelS1, activity.getCostS1()));
								accountings.add(AccountingFactory.createCreditEntry(accountingSequence, firstSemesterAccountingDate, firstSemesterJournalId, firstSemesterPeriodId, allProjectsAccount, accountingLabelS1, activity.getCostS1()));
								accountingSequence++;
							}
						}
	
						// ==> Generate accounting for second semester (S2)
						Date secondSemesterStartDate = DateFactory.getFirstSemesterStartDate();
						Date secondSemesterEndDate = DateFactory.getFirstSemesterEndDate();
						if (activity.getCostS2() != 0 && !secondSemesterAccountingDate.before(secondSemesterStartDate) && !secondSemesterAccountingDate.after(secondSemesterEndDate)) {
	
							// Collaborator accountings
							accountings.add(AccountingFactory.createDebitEntry(accountingSequence, secondSemesterAccountingDate, secondSemesterjournalId, secondSemesterPeriodId, collaboratorAccount, accountingLabelS2, activity.getCostS2()));
							accountings.add(AccountingFactory.createCreditEntry(accountingSequence, secondSemesterAccountingDate, secondSemesterjournalId, secondSemesterPeriodId, salaryAccount, accountingLabelS2, activity.getCostS2()));
							accountingSequence++;
	
							// Check if activity is associated to a project
							if (projectAccount != null) {
								accountings.add(AccountingFactory.createDebitEntry(accountingSequence, secondSemesterAccountingDate, secondSemesterjournalId, secondSemesterPeriodId, projectAccount, accountingLabelS2, activity.getCostS2()));
								accountings.add(AccountingFactory.createCreditEntry(accountingSequence, secondSemesterAccountingDate, secondSemesterjournalId, secondSemesterPeriodId, allProjectsAccount, accountingLabelS2, activity.getCostS2()));
								accountingSequence++;
							}
						}
					}
				}
			}
			catch(Exception e) {
				System.out.println(StringUtils.toString(e));
			}
		}

		System.out.println("FDC generation done.");

	}

	/**
	 * Generate financial accountings.
	 * 
	 * @throws IOException
	 * @throws ConfigurationException 
	 */
	private void generateAGFAccountings() throws IOException, ConfigurationException {

		System.out.println("Generating AGF accounting...");

		for (AGFLine afgLine : AGFLineDao.findAll()) {
			
			try {
				
				Account projectAccount = CragsiLogic.getProjectAccount(afgLine, accounts);
				accountings.add(AccountingFactory.createDebitEntry(accountingSequence, afgLine.getDate(), firstSemesterJournalId, firstSemesterPeriodId, allProjectsAccount, afgLine.getName(), afgLine.getAmount()));
				accountings.add(AccountingFactory.createCreditEntry(accountingSequence, afgLine.getDate(), firstSemesterJournalId, firstSemesterPeriodId, projectAccount, afgLine.getName(), afgLine.getAmount()));
				accountingSequence++;
			}
			catch(Exception e) {
				System.out.println(StringUtils.toString(e));
			}
		}

		System.out.println("AGF generation done.");

	}

	/**
	 * Generation funding accounting.
	 * 
	 * @throws IOException
	 * @throws ConfigurationException 
	 */
	private void generateFundingAccountings() throws IOException, ConfigurationException {

		System.out.println("Generating FINANCIAL accounting...");

		for (Funding funding : FundingDao.findAll()) {

			try {
				Account projectAccount = CragsiLogic.getProjectAccount(funding, accounts);
				accountings.add(AccountingFactory.createDebitEntry(accountingSequence, funding.getDate(), firstSemesterJournalId, firstSemesterPeriodId, allProjectsAccount, funding.getName(), funding.getAmount()));
				accountings.add(AccountingFactory.createCreditEntry(accountingSequence, funding.getDate(), firstSemesterJournalId, firstSemesterPeriodId, projectAccount, funding.getName(), funding.getAmount()));
				accountingSequence++;
			}
			catch(Exception e) {
				System.out.println(StringUtils.toString(e));
			}
		}

		System.out.println("FINANCIAL generation done.");
	}

	/**
	 * Generating partner accountings.
	 * 
	 * @throws IOException
	 * @throws ConfigurationException 
	 */
	private void generatePartnerAccountings() throws IOException, ConfigurationException {

		System.out.println("Generating SUBCONTRACTOR accounting...");

		for (Partner partner : PartnerDao.findAll()) {

			try {
				Account projectAccount = CragsiLogic.getProjectAccount(partner, accounts);
				accountings.add(AccountingFactory.createDebitEntry(accountingSequence, partner.getDate(), firstSemesterJournalId, firstSemesterPeriodId, allProjectsAccount, partner.getName(), partner.getAmount()));
				accountings.add(AccountingFactory.createCreditEntry(accountingSequence, partner.getDate(), firstSemesterJournalId, firstSemesterPeriodId, projectAccount, partner.getName(), partner.getAmount()));
				accountingSequence++;
			}
			catch(Exception e) {
				System.out.println(StringUtils.toString(e));
			}
		}

		System.out.println("SUBCONTRACTOR generation done.");
	}

	/**
	 * Main entry point
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		try {
			
			CragsiLoader loader = new CragsiLoader();
			loader.init();
			loader.start();
		}
		catch (Exception e) {
			System.out.println("Cragsi exception: " + StringUtils.toString(e));
		}
		
	}

}
