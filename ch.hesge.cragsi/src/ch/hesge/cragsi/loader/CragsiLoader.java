package ch.hesge.cragsi.loader;

import java.io.IOException;
import java.sql.SQLException;
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

/**
 * Main class used to load CRAGSI data from
 * files and generate ODOO accounting file.
 *  
 * Copyright HEG Geneva 2015, Switzerland
 * @author Eric Harth
 */
public class CragsiLoader {

	// Private attributes
	private int sequenceId;

	private String journalIdS1;
	private String journalIdS2;
	private String periodIdS1;
	private String periodIdS2;

	private Account salaryAccount;
	private Account allProjectsAccount;

	private List<Account> accounts;
	private List<Project> projects;
	private Map<String, Price> priceMap;
	private List<Accounting> accountings;

	// Set Log4j config file
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
	 * @throws SQLException 
	 */
	public void init() throws IOException, ConfigurationException, SQLException {
		
		sequenceId = 1;
		accountings = new ArrayList<>();
		
		// Retrieve global properties from config file
		journalIdS1  = PropertyUtils.getProperty("journalId_S1");
		periodIdS1   = PropertyUtils.getProperty("periodId_S1");
		journalIdS2 = PropertyUtils.getProperty("journalId_S2");
		periodIdS2  = PropertyUtils.getProperty("periodId_S2");

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
	public void start() throws IOException, SQLException, ConfigurationException {

		// Generate all accountings
		generateFDCAccountings();
		generateAGFAccountings();
		generateFinancialAccountings();
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
		
		// Retrieve first/second semester years
		List<Activity> activities = ActivityDao.findAll();
		int academicYearS1 = CragsiLogic.getFirstSemesterYear(activities);
		int academicYearS2 = CragsiLogic.getSecondSemesterYear(activities);

		// Retrieve first/second semester dates
		Date startDateS1 = CragsiLogic.getFirstSemesterStartDate(academicYearS1);
		Date endDateS1   = CragsiLogic.getFirstSemesterEndDate(academicYearS1);
		Date startDateS2 = CragsiLogic.getSecondSemesterStartDate(academicYearS2);
		Date endDateS2   = CragsiLogic.getSecondSemesterEndDate(academicYearS2);
		
		// Scan all activities
		for (Activity activity : activities) {

			try {
				
				// Retrieve accounting dates
				Date accountingDateS1 = CragsiLogic.getFirstSemesterAccountingDate(activity, startDateS1, endDateS1);
				Date accountingDateS2 = CragsiLogic.getSecondSemesterAccountingDate(activity, startDateS2, endDateS2);

				// Retrieve the collaborator account
				Account collaboratorAccount = CragsiLogic.getCollaboratorAccount(activity, accounts);
	
				// Retrieve the project account
				Account projectAccount = CragsiLogic.getProjectAccount(activity, projects, accounts);
				
				// Calculate activity costs
				Price activityPrice = CragsiLogic.getActivityPrice(activity, priceMap);
				double costS1 = activity.getTotalS1() * activityPrice.getPrice();
				double costS2 = activity.getTotalS2() * activityPrice.getPrice();
				
				// Retrieve accounting label
				String accountingLabel = CragsiLogic.getAccountingLabel(activity, collaboratorAccount, academicYearS1, academicYearS2);
				
				// Generate first semester accountings, but only if the activity has enough amount and is within the semester date range
				if (costS1 > 0 && !accountingDateS1.before(startDateS1) && !accountingDateS1.after(endDateS1)) {

					// Collaborator accountings
					accountings.add(CragsiLogic.createDebitEntry(sequenceId, accountingDateS1, journalIdS1, periodIdS1, collaboratorAccount, accountingLabel, costS1));
					accountings.add(CragsiLogic.createCreditEntry(sequenceId, accountingDateS1, journalIdS1, periodIdS1, salaryAccount, accountingLabel, costS1));
					
					sequenceId++;

					// Project accounting
					accountings.add(CragsiLogic.createDebitEntry(sequenceId, accountingDateS1, journalIdS1, periodIdS1, projectAccount, accountingLabel, costS1));
					accountings.add(CragsiLogic.createCreditEntry(sequenceId, accountingDateS1, journalIdS1, periodIdS1, allProjectsAccount, accountingLabel, costS1));
					
					sequenceId++;
				}

				// Generate second semester accountings, but only if the activity has enough amount and is within the semester date range
				if (costS2 > 0 && !accountingDateS2.before(startDateS2) && !accountingDateS2.after(endDateS2)) {

					// Collaborator accountings
					accountings.add(CragsiLogic.createDebitEntry(sequenceId, accountingDateS2, journalIdS2, periodIdS2, collaboratorAccount, accountingLabel, costS2));
					accountings.add(CragsiLogic.createCreditEntry(sequenceId, accountingDateS2, journalIdS2, periodIdS2, salaryAccount, accountingLabel, costS2));
					
					sequenceId++;

					// Project accounting
					accountings.add(CragsiLogic.createDebitEntry(sequenceId, accountingDateS2, journalIdS2, periodIdS2, projectAccount, accountingLabel, costS2));
					accountings.add(CragsiLogic.createCreditEntry(sequenceId, accountingDateS2, journalIdS2, periodIdS2, allProjectsAccount, accountingLabel, costS2));
					
					sequenceId++;
				}
			}
			catch(IntegrityException e1) {
				LOGGER.error(e1.getMessage());
			}
			catch(Exception e2) {
				LOGGER.error("FDC loading error", e2);
			}
		}

		LOGGER.info("FDC generation completed.");
	}

	/**
	 * Generate financial accountings.
	 * 
	 * @throws IOException
	 * @throws ConfigurationException 
	 */
	private void generateAGFAccountings() throws SQLException, ConfigurationException {

		LOGGER.info("Generating AGF accounting...");

		for (AGFLine afgLine : AGFLineDao.findAll()) {
			
			try {
				
				// Retrieve project account
				Account projectAccount = CragsiLogic.getProjectAccount(afgLine, accounts);
				
				// Retrieve accounting label
				String accountingLabel = CragsiLogic.getAccountingLabel(afgLine);
				
				// Create debit/credit accountings line
				accountings.add(CragsiLogic.createDebitEntry(sequenceId, afgLine.getDate(), journalIdS1, periodIdS1, allProjectsAccount, accountingLabel, afgLine.getAmount()));
				accountings.add(CragsiLogic.createCreditEntry(sequenceId, afgLine.getDate(), journalIdS1, periodIdS1, projectAccount, accountingLabel, afgLine.getAmount()));
				
				sequenceId++;
			}
			catch(IntegrityException e1) {
				LOGGER.error(e1.getMessage());
			}
			catch(Exception e) {
				LOGGER.error("AFG loading error", e);
			}
		}

		LOGGER.info("AFG generation completed.");
	}

	/**
	 * Generation financial accounting.
	 * 
	 * @throws IOException
	 * @throws ConfigurationException 
	 */
	private void generateFinancialAccountings() throws SQLException, ConfigurationException {

		LOGGER.info("Generating financial accounting...");

		for (Financial financial : FinancialDao.findAll()) {

			try {
				
				// Retrieve project account
				Account projectAccount = CragsiLogic.getProjectAccount(financial, accounts);
				
				// Retrieve accounting label
				String accountingLabel = CragsiLogic.getAccountingLabel(financial);

				// Create debit/credit accountings line
				accountings.add(CragsiLogic.createDebitEntry(sequenceId, financial.getDate(), journalIdS1, periodIdS1, allProjectsAccount, accountingLabel, financial.getAmount()));
				accountings.add(CragsiLogic.createCreditEntry(sequenceId, financial.getDate(), journalIdS1, periodIdS1, projectAccount, accountingLabel, financial.getAmount()));
				
				sequenceId++;
			}
			catch(IntegrityException e1) {
				LOGGER.error(e1.getMessage());
			}
			catch(Exception e) {
				LOGGER.error("Financial loading error", e);
			}
		}

		LOGGER.info("Financial generation completed.");
	}

	/**
	 * Generating partner (subcontractor) accountings.
	 * 
	 * @throws IOException
	 * @throws ConfigurationException 
	 */
	private void generatePartnerAccountings() throws SQLException, ConfigurationException {

		LOGGER.info("Generating partner accounting...");

		for (Partner partner : PartnerDao.findAll()) {

			try {

				// Retrieve project account
				Account projectAccount = CragsiLogic.getProjectAccount(partner, accounts);
				
				// Retrieve accounting label
				String accountingLabel = CragsiLogic.getAccountingLabel(partner);

				// Create debit/credit accountings line
				accountings.add(CragsiLogic.createDebitEntry(sequenceId, partner.getDate(), journalIdS1, periodIdS1, allProjectsAccount, accountingLabel, partner.getAmount()));
				accountings.add(CragsiLogic.createCreditEntry(sequenceId, partner.getDate(), journalIdS1, periodIdS1, projectAccount, accountingLabel, partner.getAmount()));
				sequenceId++;
			}
			catch(IntegrityException e1) {
				LOGGER.error(e1.getMessage());
			}
			catch(Exception e) {
				LOGGER.error("Partner loading error", e);
			}
		}

		LOGGER.info("Partner generation completed.");
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
			LOGGER.error("Unexpected error", e);
		}		
	}

}
