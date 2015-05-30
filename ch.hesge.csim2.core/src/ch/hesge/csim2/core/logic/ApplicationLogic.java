package ch.hesge.csim2.core.logic;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import ch.hesge.csim2.core.dao.SettingsDao;
import ch.hesge.csim2.core.model.Application;
import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.ConceptLink;
import ch.hesge.csim2.core.model.IEngine;
import ch.hesge.csim2.core.model.MethodConceptMatch;
import ch.hesge.csim2.core.model.Ontology;
import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.Scenario;
import ch.hesge.csim2.core.model.ScenarioStep;
import ch.hesge.csim2.core.model.SourceClass;
import ch.hesge.csim2.core.model.SourceMethod;
import ch.hesge.csim2.core.model.StemConcept;
import ch.hesge.csim2.core.model.StemMethod;
import ch.hesge.csim2.core.model.TimeSeries;
import ch.hesge.csim2.core.model.Trace;
import ch.hesge.csim2.core.persistence.ConnectionUtils;
import ch.hesge.csim2.core.utils.ApplicationCache;
import ch.hesge.csim2.core.utils.Console;
import ch.hesge.csim2.core.utils.StringUtils;

/**
 * This class implement all logical rules globally provided publicly.
 * 
 * It is main purpose is to delegate request to specific logic classes
 * and caching result if required.
 *
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class ApplicationLogic {

	// Private static attributes
	private static ApplicationCache<String, Object> APPCACHE = new ApplicationCache<>(50);

	public static final String USER_NAME_PROPERTY = "user-name";
	public static final String USER_FOLDER_PROPERTY = "user-folder";
	public static final String APPLICATION_BASE_PROPERTY = "application-folder";
	public static final String DATABASE_CONNECTION_PROPERTY = "database-connection";
	public static final String DATABASE_USER_PROPERTY = "database-user";
	public static final String DATABASE_PASSWORD_PROPERTY = "database-password";

	/**
	 * Create an initialize a new application instance.
	 * 
	 * @return
	 *         the new application instance
	 */
	public static Application createApplication() {

		Application application = new Application();

		initJavaLogging();
		initAppProperties(application);
		initDbProperties(application);

		return application;
	}

	/**
	 * Retrieve the application version.
	 * 
	 * @return
	 *         a string containing the version
	 */
	public static String getVersion() {
		return ApplicationVersion.VERSION;
	}

	/**
	 * Clear all data in cache.
	 */
	public static void clearCache() {
		ApplicationLogic.APPCACHE.clear();
	}

	/**
	 * Initialize the java standard logging system.
	 * Can be override by specifying an -D property on java launching argument.
	 */
	private static void initJavaLogging() {

		// Retrieve configuration path
		String configPath = "conf/logging.properties";
		if (System.getProperties().contains("java.util.logging.config.file")) {
			configPath = System.getProperties().getProperty("java.util.logging.config.file");
		}
		System.getProperties().setProperty("java.util.logging.config.file", configPath);

		try {
			LogManager.getLogManager().readConfiguration();
		}
		catch (SecurityException | IOException e) {
			Logger.getAnonymousLogger().severe(e.getLocalizedMessage());
		}
	}

	/**
	 * Load all local settings:
	 * 
	 * - current user
	 * - current user folder
	 * - base application folder
	 * - other settings
	 * 
	 * as defined in csim2.conf file.
	 * 
	 * @param properties
	 *        the application properties
	 */
	private static void initAppProperties(Application application) {

		Properties properties = application.getProperties();

		Console.writeDebug("initializing application properties.");

		// Load properties from environment variables
		properties.setProperty(USER_NAME_PROPERTY, System.getProperty("user.name"));
		properties.setProperty(USER_FOLDER_PROPERTY, System.getProperty("user.home"));

		// Retrieve application's base folder
		String applicationFolder = FileSystems.getDefault().getPath(".").toAbsolutePath().toString();
		applicationFolder = StringUtils.removeTrailString(applicationFolder, "\\");
		applicationFolder = StringUtils.removeTrailString(applicationFolder, "\\");
		properties.setProperty(APPLICATION_BASE_PROPERTY, applicationFolder);

		// Retrieve configuration path
		String configPath = "conf/csim2.conf";
		if (System.getProperties().contains("ch.hesge.csim2.config.file")) {
			configPath = System.getProperties().getProperty("ch.hesge.csim2.config.file");
		}

		Console.writeDebug("loading application configuration from " + configPath + ".");

		// Load properties defined in csim2.conf
		try (FileReader reader = new FileReader(configPath)) {
			Properties confProperties = new Properties();
			confProperties.load(reader);
			properties.putAll(confProperties);
		}
		catch (IOException e) {
			Console.writeError("an unexpected error has occured: " + e.toString());
		}
	}

	/**
	 * Load all remote settings, that is all settings defined in the database
	 * within the 'settings' table.
	 * 
	 * @param properties
	 *        the application properties
	 */
	private static void initDbProperties(Application application) {

		Properties properties = application.getProperties();

		Console.writeDebug("initializing database connection.");

		// Retrieve database config
		String connectionString = properties.getProperty(DATABASE_CONNECTION_PROPERTY);
		String databaseUser = properties.getProperty(DATABASE_USER_PROPERTY);
		String databasePassword = properties.getProperty(DATABASE_PASSWORD_PROPERTY);

		// Initialize parameters used to database connection
		ConnectionUtils.setUrl(connectionString);
		ConnectionUtils.setUser(databaseUser);
		ConnectionUtils.setPassword(databasePassword);

		// Load properties from database
		Properties settings = SettingsDao.findAll();
		properties.putAll(settings);
	}

	/**
	 * Retrieve all available projects without their dependencies.
	 * 
	 * @return
	 *         a list of project
	 */
	public static List<Project> getProjects() {

		String cacheKey = "getProjects";

		if (ApplicationLogic.APPCACHE.isCacheMissed(cacheKey)) {
			ApplicationLogic.APPCACHE.put(cacheKey, ProjectLogic.getProjects());
		}

		return ApplicationLogic.APPCACHE.get(cacheKey);
	}

	/**
	 * Retrieve all available ontologies without their dependencies.
	 * 
	 * @return
	 *         a list of ontology
	 */
	public static List<Ontology> getOntologies() {

		String cacheKey = "getOntologies";

		if (ApplicationLogic.APPCACHE.isCacheMissed(cacheKey)) {
			ApplicationLogic.APPCACHE.put(cacheKey, OntologyLogic.getOntologies());
		}

		return ApplicationLogic.APPCACHE.get(cacheKey);
	}

	/**
	 * Retrieve all ontologies owned by a project without their dependencies.
	 * 
	 * @param project
	 *        the owner
	 * 
	 * @return
	 *         a list of ontology
	 */
	public static List<Ontology> getOntologies(Project project) {

		String cacheKey = "getOntologies_" + project.getKeyId();

		if (ApplicationLogic.APPCACHE.isCacheMissed(cacheKey)) {
			ApplicationLogic.APPCACHE.put(cacheKey, OntologyLogic.getOntologies(project));
		}

		return ApplicationLogic.APPCACHE.get(cacheKey);
	}

	/**
	 * Return a list of all scenarios registered within the application.
	 * 
	 * @return
	 *         a list of scenario
	 */
	public static List<Scenario> getScenarios() {

		String cacheKey = "getScenarios";

		if (ApplicationLogic.APPCACHE.isCacheMissed(cacheKey)) {
			ApplicationLogic.APPCACHE.put(cacheKey, ScenarioLogic.getScenarios());
		}

		return ApplicationLogic.APPCACHE.get(cacheKey);
	}

	/**
	 * Return a list of all engines registered within the application.
	 * 
	 * @return
	 *         a list of engine
	 */
	public static List<IEngine> getEngines() {

		String cacheKey = "getEngines";

		if (ApplicationLogic.APPCACHE.isCacheMissed(cacheKey)) {
			ApplicationLogic.APPCACHE.put(cacheKey, EngineLogic.getEngines());
		}

		return ApplicationLogic.APPCACHE.get(cacheKey);
	}

	/**
	 * Return a list of scenario owned by a project.
	 * 
	 * @param project
	 *        the scenarios owner
	 * 
	 * @return
	 *         a list of scenario
	 */
	public static List<Scenario> getScenarios(Project project) {

		String cacheKey = "getScenarios_" + project.getKeyId();

		if (ApplicationLogic.APPCACHE.isCacheMissed(cacheKey)) {
			ApplicationLogic.APPCACHE.put(cacheKey, ScenarioLogic.getScenarios(project));
		}

		return ApplicationLogic.APPCACHE.get(cacheKey);
	}

	/**
	 * Retrieve all scenario with its steps.
	 * 
	 * @param scenario
	 *        the owner
	 * 
	 * @return
	 *         a scenario with its steps
	 */
	public static Scenario getScenarioWithDependencies(Scenario scenario) {

		String cacheKey = "getScenarioWithDependencies_" + scenario.getKeyId();

		if (ApplicationLogic.APPCACHE.isCacheMissed(cacheKey)) {
			ApplicationLogic.APPCACHE.put(cacheKey, ScenarioLogic.getScenarioWithDependencies(scenario));
		}

		return ApplicationLogic.APPCACHE.get(cacheKey);
	}

	/**
	 * Retrieve a list of all concepts owned by an ontology.
	 * 
	 * @param ontology
	 *        the owner
	 * 
	 * @return
	 *         the list of concept
	 */
	public static List<Concept> getConcepts(Ontology ontology) {

		String cacheKey = "getConceptsByOntology_" + ontology.getKeyId();

		if (ApplicationLogic.APPCACHE.isCacheMissed(cacheKey)) {
			ApplicationLogic.APPCACHE.put(cacheKey, OntologyLogic.getConcepts(ontology));
		}

		return ApplicationLogic.APPCACHE.get(cacheKey);
	}

	/**
	 * Retrieve all concepts owned by an project and its ontologies.
	 * 
	 * @param project
	 *        the owner
	 * 
	 * @return
	 *         the list of concept
	 */
	public static List<Concept> getConcepts(Project project) {

		String cacheKey = "getConceptsByProject_" + project.getKeyId();

		if (ApplicationLogic.APPCACHE.isCacheMissed(cacheKey)) {
			ApplicationLogic.APPCACHE.put(cacheKey, OntologyLogic.getConcepts(project));
		}

		return ApplicationLogic.APPCACHE.get(cacheKey);
	}

	/**
	 * Retrieve all concepts owned by an ontology with the following
	 * dependencies:
	 * 
	 * - its concept attributes
	 * - its concept classes
	 * - its links
	 * - its superconcept
	 * - its children concept
	 * 
	 * @param ontology
	 *        the owner
	 * 
	 * @return
	 *         the list of concept
	 */
	public static List<Concept> getConceptsWithDependencies(Ontology ontology) {

		String cacheKey = "getConceptsWithDependenciesByOntology_" + ontology.getKeyId();

		if (ApplicationLogic.APPCACHE.isCacheMissed(cacheKey)) {
			ApplicationLogic.APPCACHE.put(cacheKey, OntologyLogic.getConceptsWithDependencies(ontology));
		}

		return ApplicationLogic.APPCACHE.get(cacheKey);
	}

	/**
	 * Retrieve all concepts owned by a project with the following
	 * dependencies:
	 * 
	 * - its concept attributes
	 * - its concept classes
	 * - its links
	 * - its superconcept
	 * - its children concept
	 * 
	 * @param ontology
	 *        the owner
	 * 
	 * @return
	 *         the list of concept
	 */
	public static List<Concept> getConceptsWithDependencies(Project project) {

		String cacheKey = "getConceptsWithDependenciesByProject_" + project.getKeyId();

		if (ApplicationLogic.APPCACHE.isCacheMissed(cacheKey)) {
			ApplicationLogic.APPCACHE.put(cacheKey, OntologyLogic.getConceptsWithDependencies(project));
		}

		return ApplicationLogic.APPCACHE.get(cacheKey);
	}

	/**
	 * Retrieve a map of concepts owned by an ontology
	 * with each entries of the form (keyId, Concept) map.
	 * 
	 * @param ontology
	 *        the owner
	 * 
	 * @return
	 *         a map of concept
	 */
	public static Map<Integer, Concept> getConceptMap(Ontology ontology) {

		String cacheKey = "getConceptMapByOntology_" + ontology.getKeyId();

		if (ApplicationLogic.APPCACHE.isCacheMissed(cacheKey)) {
			ApplicationLogic.APPCACHE.put(cacheKey, OntologyLogic.getConceptMap(ontology));
		}

		return ApplicationLogic.APPCACHE.get(cacheKey);
	}

	/**
	 * Retrieve all concepts owned by a project as a (keyId, Concept) map.
	 * 
	 * @param project
	 *        the owner
	 * 
	 * @return
	 *         a map of concept
	 */
	public static Map<Integer, Concept> getConceptMap(Project project) {

		String cacheKey = "getConceptsMapByProject_" + project.getKeyId();

		if (ApplicationLogic.APPCACHE.isCacheMissed(cacheKey)) {
			ApplicationLogic.APPCACHE.put(cacheKey, OntologyLogic.getConceptMap(project));
		}

		return ApplicationLogic.APPCACHE.get(cacheKey);
	}

	/**
	 * Retrieve all matchings in projects.
	 * 
	 * @return
	 *         a list of MethodConceptMatch
	 */
	public static List<MethodConceptMatch> getMatchings(Project project) {

		String cacheKey = "getMatchings_" + project.getKeyId();

		if (ApplicationLogic.APPCACHE.isCacheMissed(cacheKey)) {
			ApplicationLogic.APPCACHE.put(cacheKey, MatchingLogic.getMatchings(project));
		}

		return ApplicationLogic.APPCACHE.get(cacheKey);
	}

	/**
	 * Retrieve a map of all MethodConceptMatch classified by method Id.
	 * 
	 * @return
	 *         a map of (MethodId, List<MethodConceptMatch>)
	 */
	public static Map<Integer, List<MethodConceptMatch>> getMethodMatchingMap(Project project) {

		String cacheKey = "getMethodMatchingMap_" + project.getKeyId();

		if (ApplicationLogic.APPCACHE.isCacheMissed(cacheKey)) {
			ApplicationLogic.APPCACHE.put(cacheKey, MatchingLogic.getMethodMatchingMap(project));
		}

		return ApplicationLogic.APPCACHE.get(cacheKey);
	}

	/**
	 * Retrieve a map of all MethodConceptMatch classified by concept Id.
	 * 
	 * @return
	 *         a map of (ConceptId, List<MethodConceptMatch>)
	 */
	public static Map<Integer, List<MethodConceptMatch>> getConceptMatchingMap(Project project) {

		String cacheKey = "getConceptMatchingMap_" + project.getKeyId();

		if (ApplicationLogic.APPCACHE.isCacheMissed(cacheKey)) {
			ApplicationLogic.APPCACHE.put(cacheKey, MatchingLogic.getConceptMatchingMap(project));
		}

		return ApplicationLogic.APPCACHE.get(cacheKey);
	}

	/**
	 * Retrieve all matching with its dependencies SourceMethod and Concept.
	 * 
	 * @return a list of MethodConceptMatch
	 */
	public static List<MethodConceptMatch> getMatchingsWithDependencies(Project project) {

		String cacheKey = "getMatchingsWidthDependencies_" + project.getKeyId();

		if (ApplicationLogic.APPCACHE.isCacheMissed(cacheKey)) {
			ApplicationLogic.APPCACHE.put(cacheKey, MatchingLogic.getMatchingsWithDependencies(project));
		}

		return ApplicationLogic.APPCACHE.get(cacheKey);
	}

	/**
	 * Retrieve all source classes owned by a project.
	 * 
	 * @param project
	 *        the owner
	 * 
	 * @return a list of source classes
	 */
	public static List<SourceClass> getSourceClasses(Project project) {

		String cacheKey = "getSourceClasses_" + project.getKeyId();

		if (ApplicationLogic.APPCACHE.isCacheMissed(cacheKey)) {
			ApplicationLogic.APPCACHE.put(cacheKey, SourceLogic.getSourceClasses(project));
		}

		return ApplicationLogic.APPCACHE.get(cacheKey);
	}

	/**
	 * Retrieve all source classes owned by a project with the following
	 * dependencies:
	 * 
	 * - its attributes
	 * - its methods
	 * - its subclasses
	 * 
	 * @param project
	 *        the owner
	 * @param includeMethodParamsAndRefs
	 *        true to retrieve also method parameters & references
	 * 
	 * @return a list of source classes
	 */
	public static List<SourceClass> getSourceClassesWithDependencies(Project project, boolean includeMethodParamsAndRefs) {

		String cacheKey = "getSourceClassesWithDependencies_" + project.getKeyId() + "_" + includeMethodParamsAndRefs;

		if (ApplicationLogic.APPCACHE.isCacheMissed(cacheKey)) {
			ApplicationLogic.APPCACHE.put(cacheKey, SourceLogic.getSourceClassesWithDependencies(project, includeMethodParamsAndRefs));
		}

		return ApplicationLogic.APPCACHE.get(cacheKey);
	}

	/**
	 * Retrieve all source classes owned by a project as a (keyId, SourceClass)
	 * map.
	 * 
	 * @param project
	 *        the owner
	 * 
	 * @return
	 *         a map of source classe
	 */
	public static Map<Integer, SourceClass> getSourceClassMap(Project project) {

		String cacheKey = "getSourceClassMap_" + project.getKeyId();

		if (ApplicationLogic.APPCACHE.isCacheMissed(cacheKey)) {
			ApplicationLogic.APPCACHE.put(cacheKey, SourceLogic.getSourceClassMap(project));
		}

		return ApplicationLogic.APPCACHE.get(cacheKey);
	}

	/**
	 * Retrieve all source methods owned by a project as a (keyId, SourceMethod)
	 * map.
	 * 
	 * @param project
	 *        the owner
	 * 
	 * @return
	 *         a map of source method
	 */
	public static Map<Integer, SourceMethod> getSourceMethodMap(Project project) {

		String cacheKey = "getSourceMethods_" + project.getKeyId();

		if (ApplicationLogic.APPCACHE.isCacheMissed(cacheKey)) {
			ApplicationLogic.APPCACHE.put(cacheKey, SourceLogic.getSourceMethodMap(project));
		}

		return ApplicationLogic.APPCACHE.get(cacheKey);
	}

	/**
	 * Retrieve a source method by its signature.
	 * 
	 * @param sourceClass
	 *        the owner
	 * @param signature
	 *        the method signature
	 * 
	 * @return
	 *         a SourceMethod or null
	 */
	public static SourceMethod getSourceMethodBySignature(SourceClass sourceClass, String signature) {

		String cacheKey = "getSourceMethodByClassAndSignature_" + sourceClass.getKeyId() + "_" + signature;

		if (ApplicationLogic.APPCACHE.isCacheMissed(cacheKey)) {
			ApplicationLogic.APPCACHE.put(cacheKey, SourceLogic.getSourceMethodBySignature(sourceClass, signature));
		}

		return ApplicationLogic.APPCACHE.get(cacheKey);
	}

	/**
	 * Retrieve all stem methods associated to a project.
	 * 
	 * @param project
	 *        the project owning stems
	 * 
	 * @return
	 *         a list of stem
	 */
	public static List<StemMethod> getStemMethods(Project project) {

		String cacheKey = "getStemMethods_" + project.getKeyId();

		if (ApplicationLogic.APPCACHE.isCacheMissed(cacheKey)) {
			ApplicationLogic.APPCACHE.put(cacheKey, StemLogic.getStemMethods(project));
		}

		return ApplicationLogic.APPCACHE.get(cacheKey);
	}

	/**
	 * Retrieve all stem methods owned by a project
	 * as a (stemId, StemMethod) map.
	 * 
	 * @param project
	 *        the owner
	 * 
	 * @return
	 *         a map of stem method
	 */
	public static Map<Integer, StemMethod> getStemMethodMap(Project project) {

		String cacheKey = "getStemMethodMap_" + project.getKeyId();

		if (ApplicationLogic.APPCACHE.isCacheMissed(cacheKey)) {
			ApplicationLogic.APPCACHE.put(cacheKey, StemLogic.getStemMethodMap(project));
		}

		return ApplicationLogic.APPCACHE.get(cacheKey);
	}

	/**
	 * Retrieve a hierarchy of stem methods defined for a project.
	 * 
	 * More specifically allows one stem hierarchy to be retrieved for a
	 * specific method. So entries are of the form (methodId, root of
	 * StemMethod tree).
	 * 
	 * @param project
	 *        the owner
	 * 
	 * @return
	 *         the map of (methodId, StemMethod)
	 */
	public static Map<Integer, StemMethod> getStemMethodTree(Project project) {

		String cacheKey = "getStemMethodTree_" + project.getKeyId();

		if (ApplicationLogic.APPCACHE.isCacheMissed(cacheKey)) {
			ApplicationLogic.APPCACHE.put(cacheKey, StemLogic.getStemMethodTree(project));
		}

		return ApplicationLogic.APPCACHE.get(cacheKey);
	}

	/**
	 * Retrieve a map of all StemMethods in project, classified by term. Each
	 * entry will then contains a couple (term, List<StemMethods>).
	 * 
	 * @param project
	 *        the project owning stems
	 * 
	 * @return
	 *         a map of stems
	 */
	public static Map<String, List<StemMethod>> getStemMethodByTermMap(Project project) {

		String cacheKey = "getStemMethodsByTermMap_" + project.getKeyId();

		if (ApplicationLogic.APPCACHE.isCacheMissed(cacheKey)) {
			ApplicationLogic.APPCACHE.put(cacheKey, StemLogic.getStemMethodByTermMap(project));
		}

		return ApplicationLogic.APPCACHE.get(cacheKey);
	}

	/**
	 * Retrieve a hierarchy of stem concepts defined for a project.
	 * 
	 * More specifically allows one stem hierarchy to be retrieved for a
	 * specific concept. So entries are of the form (conceptId, root of
	 * StemConcept tree).
	 * 
	 * @param project
	 *        the owner
	 * 
	 * @return
	 *         the map of (conceptId, StemConcept)
	 */
	public static Map<Integer, StemConcept> getStemConceptTree(Project project) {

		String cacheKey = "getStemConceptTree_" + project.getKeyId();

		if (ApplicationLogic.APPCACHE.isCacheMissed(cacheKey)) {
			ApplicationLogic.APPCACHE.put(cacheKey, StemLogic.getStemConceptTree(project));
		}

		return ApplicationLogic.APPCACHE.get(cacheKey);
	}

	/**
	 * Serialize stem concept tree into a single flat list of stem concepts.
	 * 
	 * @param rootStem
	 *        the root stem of a stem tree
	 * 
	 * @return
	 *         a flat list of stem concepts
	 */
	public static List<StemConcept> getStemConceptList(StemConcept rootStem) {

		List<StemConcept> stemList = null;
		
		if (rootStem != null) {
			
			String cacheKey = "getStemConceptList_" + rootStem.getKeyId();

			if (ApplicationLogic.APPCACHE.isCacheMissed(cacheKey)) {
				ApplicationLogic.APPCACHE.put(cacheKey, StemLogic.getStemConceptList(rootStem));
			}
			
			stemList = ApplicationLogic.APPCACHE.get(cacheKey);
		}

		return stemList;
	}

	/**
	 * Retrieve a map of all StemConcepts in project, classified by term. Each
	 * entry will then contains a couple (term, List<StemConcept>).
	 * 
	 * @param project
	 *        the project owning stems
	 * 
	 * @return
	 *         a map of stems
	 */
	public static Map<String, List<StemConcept>> getStemConceptByTermMap(Project project) {

		String cacheKey = "getStemConceptsByTermMap_" + project.getKeyId();

		if (ApplicationLogic.APPCACHE.isCacheMissed(cacheKey)) {
			ApplicationLogic.APPCACHE.put(cacheKey, StemLogic.getStemConceptByTermMap(project));
		}

		return ApplicationLogic.APPCACHE.get(cacheKey);
	}

	/**
	 * Retrieve all traces owned by a scenario.
	 * 
	 * @param scenario
	 *        the scenario
	 */
	public static List<Trace> getTraces(Scenario scenario) {

		String cacheKey = "getTraces_" + scenario.getKeyId();

		if (ApplicationLogic.APPCACHE.isCacheMissed(cacheKey)) {
			ApplicationLogic.APPCACHE.put(cacheKey, TraceLogic.getTraces(scenario));
		}

		return ApplicationLogic.APPCACHE.get(cacheKey);
	}

	/**
	 * Retrieve the time series associated to a scenario traces.
	 * 
	 * @param project
	 *        the project owning the traces
	 * @param scenario
	 *        the scenario owning the traces
	 * @return
	 *         the TimeSeries object gathering trace information
	 */
	public static TimeSeries getTimeSeries(Project project, Scenario scenario) {

		String cacheKey = "getTimeSeries_" + project.getKeyId() + "_" + scenario.getKeyId();

		if (ApplicationLogic.APPCACHE.isCacheMissed(cacheKey)) {
			ApplicationLogic.APPCACHE.put(cacheKey, TimeSeriesLogic.getTimeSeries(project, scenario));
		}

		return ApplicationLogic.APPCACHE.get(cacheKey);
	}

	/**
	 * Create a segmented time series.
	 * 
	 * @param timeSeries
	 *        the time series to compress
	 * @param segmentCount
	 *        the total number of segment to generate
	 * @param threshold
	 *        the concept weight threshold to use to select concepts
	 * @param concepts
	 *        a subset of timerSeries concepts
	 * @return
	 *         a new time series instance with segmented trace with only history
	 *         of concepts passed in argument
	 */
	public static TimeSeries getFilteredTimeSeries(TimeSeries timeSeries, int segmentCount, double threshold, List<Concept> concepts) {
		return TimeSeriesLogic.getFilteredTimeSeries(timeSeries, segmentCount, threshold, concepts);
	}

	/**
	 * Delete all traces owned by a scenario.
	 * 
	 * @param scenario
	 *        the scenario
	 */
	public static void deleteTraces(Scenario scenario) {
		TraceLogic.deleteTraces(scenario);
	}

	/**
	 * Delete all MethodConcetMatch instances owned by a project.
	 * 
	 * @param project
	 *        the owner
	 */
	public static void deleteMatching(Project project) {
		MatchingLogic.deleteMatching(project);
	}

	/**
	 * Delete all sources owned by an project. Thas is class, attribute, method,
	 * parameter and reference.
	 * 
	 * @param project
	 *        the project to clean sources
	 */
	public static void deleteSources(Project project) {
		SourceLogic.deleteSources(project);
	}

	/**
	 * Delete all stem concepts associated to an ontology
	 * 
	 * @param ontology
	 *        the ontology owning stems to delete
	 */
	public static void deleteStemConcepts(Ontology ontology) {
		StemLogic.deleteStemConcepts(ontology);
	}

	/**
	 * Delete all stems methods associated to a project.
	 * 
	 * @param project
	 *        the project owning stems to delete
	 */
	public static void deleteStemMethods(Project project) {
		StemLogic.deleteStemMethods(project);
	}

	/**
	 * Save project passed in argument.
	 * 
	 * @param project
	 *        the project to save
	 */
	public static void saveProject(Project project) {
		ProjectLogic.saveProject(project);
	}

	/**
	 * Save scenario passed in argument.
	 * 
	 * @param scenario
	 *        the concerned scenario
	 */
	public static void saveScenario(Scenario scenario) {
		ScenarioLogic.saveScenario(scenario);
	}

	/**
	 * Save a list of scenarios passed in argument.
	 * 
	 * @param scenarios
	 *        the concerned scenarios
	 */
	public static void saveScenarios(List<Scenario> scenarios) {
		ScenarioLogic.saveScenarios(scenarios);
	}

	/**
	 * Save project's source classes.
	 * 
	 * @param projectClasses
	 *        the owning project
	 * @param sourceClasses
	 *        the list of SourceClass to save
	 */
	public static void saveSourceClasses(Project project, List<SourceClass> sourceClasses) {
		SourceLogic.saveSources(project, sourceClasses);
	}

	/**
	 * Save an ontology and its concepts.
	 * 
	 * @param ontology
	 *        the ontology to save
	 */
	public static void saveOntology(Ontology ontology) {
		OntologyLogic.saveOntology(ontology);
	}

	/**
	 * Save all ontologies without their concepts.
	 * 
	 * @param ontologies
	 *        the ontology list to save
	 */
	public static void saveOntologies(List<Ontology> ontologies) {
		OntologyLogic.saveOntologies(ontologies);
	}

	/**
	 * Save the trace passed in argument.
	 * 
	 * @param trace
	 *        the trace to save
	 */
	public static void saveTrace(Trace trace) {
		TraceLogic.saveTrace(trace);
	}

	/**
	 * Save a single stem concept.
	 * 
	 * @param stem
	 *        the StemConcept to save
	 */
	public static void saveStemConcept(StemConcept stem) {
		StemLogic.saveStemConcept(stem);
	}

	/**
	 * Save a single stem method.
	 * 
	 * @param stem
	 *        the StemMethod to save
	 */
	public static void saveStemMethod(StemMethod stem) {
		StemLogic.saveStemMethod(stem);
	}

	/**
	 * Save a single MethodConcetMatch.
	 * 
	 * @param match
	 *        the MethodConcetMatch to save
	 */
	public static void saveMatching(MethodConceptMatch match) {
		MatchingLogic.saveMatching(match);
	}

	/**
	 * Start the engine passed in argument. A new thread is allocation from pool
	 * 
	 * @param engine
	 *        the engine to start
	 */
	public static void startEngine(IEngine engine) {
		EngineLogic.startEngine(engine);
	}

	/**
	 * Stop the engine passed in argument. The thread release return the
	 * allocation from pool
	 * 
	 * @param engine
	 *        the engine to stop
	 */
	public static void stopEngine(IEngine engine) {
		EngineLogic.stopEngine(engine);
	}

	/**
	 * Load a project with its direct dependencies only.
	 * That is scenarios, ontologies and sources.
	 * 
	 * @param project
	 *        the project to load.
	 * 
	 * @return
	 *         the initialized instance passed in argument
	 */
	public static void loadProject(Project project) {
		ProjectLogic.loadProject(project);
	}

	/**
	 * Return the engine running state.
	 * 
	 * @param engine
	 * @return true, if the engine is current running, false otherwise.
	 */
	public static boolean isEngineRunning(IEngine engine) {
		return EngineLogic.isEngineRunning(engine);
	}

	/**
	 * Initialize current time of a specific scenario step.
	 * 
	 * @param scenario
	 *        the concerned scenario
	 */
	public static void initExecutionTime(ScenarioStep scenarioStep) {
		ScenarioLogic.initExecutionTime(scenarioStep);
	}

	/**
	 * Clear execution time of all scenario steps.
	 * 
	 * @param scenario
	 *        the concerned scenario
	 */
	public static void resetExecutionTimes(Scenario scenario) {
		ScenarioLogic.resetExecutionTimes(scenario);
	}

	/**
	 * Create a new concept and attach it to the ontology.
	 * 
	 * @param ontology
	 *        the future owner
	 * 
	 * @return
	 *         a new concept
	 */
	public static Concept createConcept(Ontology ontology) {
		return OntologyLogic.createConcept(ontology);
	}

	/**
	 * Create a new concept link and update the ontology.
	 * 
	 * @param ontology
	 *        the ontology to update
	 * @param source
	 *        the source concept
	 * @param target
	 *        the target concept
	 * 
	 * @return
	 *         the new link created
	 */
	public static ConceptLink createConceptLink(Ontology ontology, Concept source, Concept target) {
		return OntologyLogic.createConceptLink(ontology, source, target);
	}

	/**
	 * Remove a concept from an ontology.
	 * 
	 * @param ontology
	 *        the ontology owning the concept
	 * @param concept
	 *        the concept to remove
	 */
	public static void removeConcept(Ontology ontology, Concept concept) {
		OntologyLogic.removeConcept(ontology, concept);
	}

	/**
	 * Remove a link starting from a concept.
	 * 
	 * @param ontology
	 *        the ontology owning the concept
	 * @param concept
	 *        the owning concept
	 * @param link
	 *        the link to remove from ontology
	 */
	public static void removeConceptLink(Ontology ontology, Concept concept, ConceptLink link) {
		OntologyLogic.removeConceptLink(ontology, concept, link);
	}

}
