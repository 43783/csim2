package ch.hesge.cragsi.loader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.hesge.cragsi.dao.AGFLineDao;
import ch.hesge.cragsi.dao.AccountDao;
import ch.hesge.cragsi.dao.AccountingDao;
import ch.hesge.cragsi.dao.ActivityDao;
import ch.hesge.cragsi.dao.FinancialDao;
import ch.hesge.cragsi.dao.PartnerDao;
import ch.hesge.cragsi.dao.ProjectDao;
import ch.hesge.cragsi.exceptions.ConfigurationException;
import ch.hesge.cragsi.model.AGFLine;
import ch.hesge.cragsi.model.Account;
import ch.hesge.cragsi.model.Accounting;
import ch.hesge.cragsi.model.Activity;
import ch.hesge.cragsi.model.Financial;
import ch.hesge.cragsi.model.Partner;
import ch.hesge.cragsi.model.Price;
import ch.hesge.cragsi.model.Project;
import ch.hesge.cragsi.utils.PropertyUtils;

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

	// Log4j init
	static { System.setProperty("log4j.configurationFile", "conf/log4j2.xml"); }
	private static Logger LOGGER = LogManager.getLogger(CragsiLoader.class);
	
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
			throw new ConfigurationException("missing account for allprojects code '" + allProjectsCode + "' !");
		}

		// Retrieve the salaries account
		String salaryCode = PropertyUtils.getProperty("salaryAccount");
		salaryAccount = CragsiLogic.getAccountByCode(salaryCode, accounts);
		if (salaryAccount == null) {
			throw new ConfigurationException("missing account for salaries code '" + salaryCode + "' !");
		}
	}

	/**
	 * Start loading all files and genered all accountings.
	 * 
	 * @throws IOException
	 * @throws ConfigurationException 
	 */
	public void start() throws IOException, ConfigurationException {

		// Generate FDC accountings
		generateFDCAccountings();

		// Generate AGF accountings
		//generateAGFAccountings();

		// Generate funding accountings
		generateFinancialAccountings();

		// Generate partner accountings
		generatePartnerAccountings();

		// Finally save accountings
		AccountingDao.saveAll(accountings);
	}

	/**
	 * Generate all accounting associated to activities (FDC).
	 * 
	 * @throws IOException 
	 * @throws ConfigurationException 
	 */
	private void generateFDCAccountings() throws IOException, ConfigurationException {

		LOGGER.info("Generating FDC accounting...");

		for (Activity activity : ActivityDao.findAll()) {

			try {
				
				// Retrieve accounting dates
				Date accountingDateS1 = CragsiLogic.getFirstSemesterAccountingDate(activity);
				Date accountingDateS2 = CragsiLogic.getSecondSemesterAccountingDate(activity);

				// Retrieve first semester dates
				Date startDateS1 = CragsiLogic.getFirstSemesterStartDate();
				Date endDateS1   = CragsiLogic.getFirstSemesterEndDate();
				
				// Retrieve second semester dates
				Date startDateS2 = CragsiLogic.getFirstSemesterStartDate();
				Date endDateS2   = CragsiLogic.getFirstSemesterEndDate();

				// Retrieve the collaborator account
				Account collaboratorAccount = CragsiLogic.getCollaboratorAccount(activity, accounts);
	
				// Retrieve the project account
				Account projectAccount = CragsiLogic.getProjectAccount(activity, projects, accounts);
				
				// Calculate activity costs
				Price activityPrice = CragsiLogic.getActivityPrice(activity, priceMap);
				activity.setCostS1(activity.getTotalS1() * activityPrice.getPrice());
				activity.setCostS2(activity.getTotalS2() * activityPrice.getPrice());
				
				// Calculate accounting labels
				String activityLabel = CragsiLogic.getActivityLabel(activity, collaboratorAccount);
				String accountingLabelS1 = activityLabel + " (" + PropertyUtils.getProperty("academicYear_S1") + ")";
				String accountingLabelS2 = activityLabel + " (" + PropertyUtils.getProperty("academicYear_S2") + ")";
				
				// Generate first semester accountings, but only if the activity has an amount and is within the semester date range
				if (activity.getCostS1() != 0 && !accountingDateS1.before(startDateS1) && !accountingDateS1.after(endDateS1)) {

					// Collaborator accountings
					accountings.add(CragsiLogic.createDebitEntry(accountingSequence, accountingDateS1, firstSemesterJournalId, firstSemesterPeriodId, collaboratorAccount, accountingLabelS1, activity.getCostS1()));
					accountings.add(CragsiLogic.createCreditEntry(accountingSequence, accountingDateS1, firstSemesterJournalId, firstSemesterPeriodId, salaryAccount, accountingLabelS1, activity.getCostS1()));
					accountingSequence++;

					// Project accounting
					accountings.add(CragsiLogic.createDebitEntry(accountingSequence, accountingDateS1, firstSemesterJournalId, firstSemesterPeriodId, projectAccount, accountingLabelS1, activity.getCostS1()));
					accountings.add(CragsiLogic.createCreditEntry(accountingSequence, accountingDateS1, firstSemesterJournalId, firstSemesterPeriodId, allProjectsAccount, accountingLabelS1, activity.getCostS1()));
					accountingSequence++;
				}

				// Generate second semester accountings, but only if the activity has an amount and is within the semester date range
				if (activity.getCostS2() != 0 && !accountingDateS2.before(startDateS2) && !accountingDateS2.after(endDateS2)) {

					// Collaborator accountings
					accountings.add(CragsiLogic.createDebitEntry(accountingSequence, accountingDateS2, secondSemesterjournalId, secondSemesterPeriodId, collaboratorAccount, accountingLabelS2, activity.getCostS2()));
					accountings.add(CragsiLogic.createCreditEntry(accountingSequence, accountingDateS2, secondSemesterjournalId, secondSemesterPeriodId, salaryAccount, accountingLabelS2, activity.getCostS2()));
					accountingSequence++;

					// Project accounting
					accountings.add(CragsiLogic.createDebitEntry(accountingSequence, accountingDateS2, secondSemesterjournalId, secondSemesterPeriodId, projectAccount, accountingLabelS2, activity.getCostS2()));
					accountings.add(CragsiLogic.createCreditEntry(accountingSequence, accountingDateS2, secondSemesterjournalId, secondSemesterPeriodId, allProjectsAccount, accountingLabelS2, activity.getCostS2()));
					accountingSequence++;
				}
			}
			catch(Exception e) {
				LOGGER.error(e);
			}
		}

		LOGGER.info("FDC generation done.");
	}

	/**
	 * Generate financial accountings.
	 * 
	 * @throws IOException
	 * @throws ConfigurationException 
	 */
	private void generateAGFAccountings() throws IOException, ConfigurationException {

		LOGGER.info("Generating AGF accounting...");

		for (AGFLine afgLine : AGFLineDao.findAll()) {
			
			try {				
				Account projectAccount = CragsiLogic.getProjectAccount(afgLine, accounts);
				accountings.add(CragsiLogic.createDebitEntry(accountingSequence, afgLine.getDate(), firstSemesterJournalId, firstSemesterPeriodId, allProjectsAccount, afgLine.getName(), afgLine.getAmount()));
				accountings.add(CragsiLogic.createCreditEntry(accountingSequence, afgLine.getDate(), firstSemesterJournalId, firstSemesterPeriodId, projectAccount, afgLine.getName(), afgLine.getAmount()));
				accountingSequence++;
			}
			catch(Exception e) {
				LOGGER.error(e);
			}
		}

		LOGGER.info("AFG generation done.");
	}

	/**
	 * Generation financial accounting.
	 * 
	 * @throws IOException
	 * @throws ConfigurationException 
	 */
	private void generateFinancialAccountings() throws IOException, ConfigurationException {

		LOGGER.info("Generating financial accounting...");

		for (Financial financial : FinancialDao.findAll()) {

			try {
				Account projectAccount = CragsiLogic.getProjectAccount(financial, accounts);
				accountings.add(CragsiLogic.createDebitEntry(accountingSequence, financial.getDate(), firstSemesterJournalId, firstSemesterPeriodId, allProjectsAccount, financial.getName(), financial.getAmount()));
				accountings.add(CragsiLogic.createCreditEntry(accountingSequence, financial.getDate(), firstSemesterJournalId, firstSemesterPeriodId, projectAccount, financial.getName(), financial.getAmount()));
				accountingSequence++;
			}
			catch(Exception e) {
				LOGGER.error(e);
			}
		}

		LOGGER.info("Financial generation done.");
	}

	/**
	 * Generating partner (subcontractor) accountings.
	 * 
	 * @throws IOException
	 * @throws ConfigurationException 
	 */
	private void generatePartnerAccountings() throws IOException, ConfigurationException {

		LOGGER.info("Generating partner accounting...");

		for (Partner partner : PartnerDao.findAll()) {

			try {
				Account projectAccount = CragsiLogic.getProjectAccount(partner, accounts);
				accountings.add(CragsiLogic.createDebitEntry(accountingSequence, partner.getDate(), firstSemesterJournalId, firstSemesterPeriodId, allProjectsAccount, partner.getName(), partner.getAmount()));
				accountings.add(CragsiLogic.createCreditEntry(accountingSequence, partner.getDate(), firstSemesterJournalId, firstSemesterPeriodId, projectAccount, partner.getName(), partner.getAmount()));
				accountingSequence++;
			}
			catch(Exception e) {
				LOGGER.error(e);
			}
		}

		LOGGER.info("Partner generation done.");
	}

	/**
	 * Main entry point.
	 * 
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
			LOGGER.error("Loading error", e);
		}		
	}

}
