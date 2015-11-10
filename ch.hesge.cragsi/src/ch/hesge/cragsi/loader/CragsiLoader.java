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
	 */
	public void init() throws IOException, ConfigurationException {
		
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
	public void start() throws IOException, ConfigurationException {

		// Generate all accountings
		generateFDCAccountings();
//		generateAGFAccountings();
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

		for (Activity activity : ActivityDao.findAll()) {

			try {
				
				// Retrieve accounting dates
				Date accountingDateS1 = CragsiLogic.getFirstSemesterAccountingDate(activity);
				Date accountingDateS2 = CragsiLogic.getSecondSemesterAccountingDate(activity);

				// Retrieve first semester dates
				Date startDateS1 = CragsiLogic.getFirstSemesterStartDate();
				Date endDateS1   = CragsiLogic.getFirstSemesterEndDate();
				
				// Retrieve second semester dates
				Date startDateS2 = CragsiLogic.getSecondSemesterStartDate();
				Date endDateS2   = CragsiLogic.getSecondSemesterEndDate();

				// Retrieve the collaborator account
				Account collaboratorAccount = CragsiLogic.getCollaboratorAccount(activity, accounts);
	
				// Retrieve the project account
				Account projectAccount = CragsiLogic.getProjectAccount(activity, projects, accounts);
				
				if (activity.getLastname().equalsIgnoreCase("dugerdil")) {
					System.out.println("break");
				}
				
				// Calculate activity costs
				Price activityPrice = CragsiLogic.getActivityPrice(activity, priceMap);
				double costS1 = activity.getTotalS1() * activityPrice.getPrice();
				double costS2 = activity.getTotalS2() * activityPrice.getPrice();
				
				// Calculate accounting labels
				String activityLabel = CragsiLogic.getActivityLabel(activity, collaboratorAccount);
				String accountingLabelS1 = activityLabel + " (" + PropertyUtils.getProperty("academicYear_S1") + ")";
				String accountingLabelS2 = activityLabel + " (" + PropertyUtils.getProperty("academicYear_S2") + ")";
				
				// Generate first semester accountings, but only if the activity has enough amount and is within the semester date range
				if (costS1 > 0 && !accountingDateS1.before(startDateS1) && !accountingDateS1.after(endDateS1)) {

					// Collaborator accountings
					accountings.add(CragsiLogic.createDebitEntry(sequenceId, accountingDateS1, journalIdS1, periodIdS1, collaboratorAccount, accountingLabelS1, costS1));
					accountings.add(CragsiLogic.createCreditEntry(sequenceId, accountingDateS1, journalIdS1, periodIdS1, salaryAccount, accountingLabelS1, costS1));
					sequenceId++;

					// Project accounting
					accountings.add(CragsiLogic.createDebitEntry(sequenceId, accountingDateS1, journalIdS1, periodIdS1, projectAccount, accountingLabelS1, costS1));
					accountings.add(CragsiLogic.createCreditEntry(sequenceId, accountingDateS1, journalIdS1, periodIdS1, allProjectsAccount, accountingLabelS1, costS1));
					sequenceId++;
				}

				// Generate second semester accountings, but only if the activity has enough amount and is within the semester date range
				if (costS2 > 0 && !accountingDateS2.before(startDateS2) && !accountingDateS2.after(endDateS2)) {

					// Collaborator accountings
					accountings.add(CragsiLogic.createDebitEntry(sequenceId, accountingDateS2, journalIdS2, periodIdS2, collaboratorAccount, accountingLabelS2, costS2));
					accountings.add(CragsiLogic.createCreditEntry(sequenceId, accountingDateS2, journalIdS2, periodIdS2, salaryAccount, accountingLabelS2, costS2));
					sequenceId++;

					// Project accounting
					accountings.add(CragsiLogic.createDebitEntry(sequenceId, accountingDateS2, journalIdS2, periodIdS2, projectAccount, accountingLabelS2, costS2));
					accountings.add(CragsiLogic.createCreditEntry(sequenceId, accountingDateS2, journalIdS2, periodIdS2, allProjectsAccount, accountingLabelS2, costS2));
					sequenceId++;
				}
			}
			catch(Exception e) {
				LOGGER.error("FDC loading error", e);
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
	private void generateAGFAccountings() throws IOException, ConfigurationException {

		LOGGER.info("Generating AGF accounting...");

		for (AGFLine afgLine : AGFLineDao.findAll()) {
			
			try {				
				Account projectAccount = CragsiLogic.getProjectAccount(afgLine, accounts);
				accountings.add(CragsiLogic.createDebitEntry(sequenceId, afgLine.getDate(), journalIdS1, periodIdS1, allProjectsAccount, afgLine.getName(), afgLine.getAmount()));
				accountings.add(CragsiLogic.createCreditEntry(sequenceId, afgLine.getDate(), journalIdS1, periodIdS1, projectAccount, afgLine.getName(), afgLine.getAmount()));
				sequenceId++;
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
	private void generateFinancialAccountings() throws IOException, ConfigurationException {

		LOGGER.info("Generating financial accounting...");

		for (Financial financial : FinancialDao.findAll()) {

			try {
				Account projectAccount = CragsiLogic.getProjectAccount(financial, accounts);
				accountings.add(CragsiLogic.createDebitEntry(sequenceId, financial.getDate(), journalIdS1, periodIdS1, allProjectsAccount, financial.getName(), financial.getAmount()));
				accountings.add(CragsiLogic.createCreditEntry(sequenceId, financial.getDate(), journalIdS1, periodIdS1, projectAccount, financial.getName(), financial.getAmount()));
				sequenceId++;
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
	private void generatePartnerAccountings() throws IOException, ConfigurationException {

		LOGGER.info("Generating partner accounting...");

		for (Partner partner : PartnerDao.findAll()) {

			try {
				Account projectAccount = CragsiLogic.getProjectAccount(partner, accounts);
				accountings.add(CragsiLogic.createDebitEntry(sequenceId, partner.getDate(), journalIdS1, periodIdS1, allProjectsAccount, partner.getName(), partner.getAmount()));
				accountings.add(CragsiLogic.createCreditEntry(sequenceId, partner.getDate(), journalIdS1, periodIdS1, projectAccount, partner.getName(), partner.getAmount()));
				sequenceId++;
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
