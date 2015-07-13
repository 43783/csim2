package ch.hesge.csim2.core.logic;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.ConfigurationFactory;
import ch.hesge.csim2.core.dao.SettingsDao;
import ch.hesge.csim2.core.model.Application;
import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.ConceptLink;
import ch.hesge.csim2.core.model.IEngine;
import ch.hesge.csim2.core.model.IMethodConceptMatcher;
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
import ch.hesge.csim2.core.utils.ConnectionUtils;
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

@SuppressWarnings("unchecked")
public class ApplicationLogic {

	// Private static attributes
	//private static Cache APPCACHE = CacheManager.getInstance().getCache("csim2");
	private static Cache APPCACHE;

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

		initAppProperties(application);
		initDbProperties(application);

		return application;
	}

	/**
	 * Shutdown an application.
	 * 
	 * @param application
	 *        the new application instance
	 */
	public static void shutdownApplication(Application application) {
		CacheManager.getInstance().shutdown();
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
		APPCACHE.removeAll();
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

		// Configuration log4j configuration
		String log4jConfigPath = "conf/log4j2.xml";
		System.setProperty("log4j.configurationFile", log4jConfigPath);

		Console.writeDebug(ApplicationLogic.class, "initializing application properties.");

		// Load properties from environment variables
		properties.setProperty(USER_NAME_PROPERTY, System.getProperty("user.name"));
		properties.setProperty(USER_FOLDER_PROPERTY, System.getProperty("user.home"));

		// Retrieve application's base folder
		String applicationFolder = FileSystems.getDefault().getPath(".").toAbsolutePath().toString();
		applicationFolder = StringUtils.removeTrailString(applicationFolder, "\\");
		applicationFolder = StringUtils.removeTrailString(applicationFolder, "\\");
		properties.setProperty(APPLICATION_BASE_PROPERTY, applicationFolder);

		// Retrieve app configuration
		String appConfigPath = "conf/csim2.conf";
		if (System.getProperties().contains("ch.hesge.csim2.config.file")) {
			appConfigPath = System.getProperties().getProperty("ch.hesge.csim2.config.file");
		}

		Console.writeDebug(ApplicationLogic.class, "loading application configuration from " + appConfigPath + ".");

		// Load properties defined in csim2.conf
		try (FileReader reader = new FileReader(appConfigPath)) {
			Properties confProperties = new Properties();
			confProperties.load(reader);
			properties.putAll(confProperties);
		}
		catch (IOException e) {
			Console.writeError(ApplicationLogic.class, "an unexpected error has occured: " + e.toString());
		}

		// Retrieve ehcache configuration
		String ehConfigPath = "conf/ehcache.conf";
		Configuration config = ConfigurationFactory.parseConfiguration(new File("conf/ehcache.xml"));
		CacheManager cacheManager = CacheManager.create(config);
		APPCACHE = cacheManager.getCache("csim2");

		Console.writeDebug(ApplicationLogic.class, "loading ehcache configuration from " + ehConfigPath + ".");
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

		Console.writeDebug(ApplicationLogic.class, "initializing database connection.");

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

		List<Project> result = null;
		
		try {
			
			String cacheKey = "getProjects";

			if (APPCACHE.get(cacheKey) == null) {
				APPCACHE.put(new Element(cacheKey, ProjectLogic.getProjects()));
			}

			result = (List<Project>) APPCACHE.get(cacheKey).getObjectValue();
		}
		catch (Exception e) {
			Console.writeError(ApplicationLogic.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
		
		return result;
	}

	/**
	 * Retrieve all available ontologies without their dependencies.
	 * 
	 * @return
	 *         a list of ontology
	 */
	public static List<Ontology> getOntologies() {

		List<Ontology> result = null;
		
		try {
			
			String cacheKey = "getOntologies";

			if (APPCACHE.get(cacheKey) == null) {
				APPCACHE.put(new Element(cacheKey, OntologyLogic.getOntologies()));
			}

			result = (List<Ontology>) APPCACHE.get(cacheKey).getObjectValue();
		}
		catch (Exception e) {
			Console.writeError(ApplicationLogic.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
		
		return result;
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

		List<Ontology> result = null;
		
		try {
			
			String cacheKey = "getOntologies_" + project.getKeyId();

			if (APPCACHE.get(cacheKey) == null) {
				APPCACHE.put(new Element(cacheKey,  OntologyLogic.getOntologies(project)));
			}

			result = (List<Ontology>) APPCACHE.get(cacheKey).getObjectValue();
		}
		catch (Exception e) {
			Console.writeError(ApplicationLogic.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
		
		return result;
	}

	/**
	 * Return a list of all scenarios registered within the application.
	 * 
	 * @return
	 *         a list of scenario
	 */
	public static List<Scenario> getScenarios() {

		List<Scenario> result = null;
		
		try {
			
			String cacheKey = "getScenarios";

			if (APPCACHE.get(cacheKey) == null) {
				APPCACHE.put(new Element(cacheKey, ScenarioLogic.getScenarios()));
			}

			result = (List<Scenario>) APPCACHE.get(cacheKey).getObjectValue();
		}
		catch (Exception e) {
			Console.writeError(ApplicationLogic.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
		
		return result;
	}

	/**
	 * Return a list of all engines registered within the application.
	 * 
	 * @return
	 *         a list of IEngine
	 */
	public static List<IEngine> getEngines() {

		List<IEngine> result = null;
		
		try {
			
			String cacheKey = "getEngines";

			if (APPCACHE.get(cacheKey) == null) {
				APPCACHE.put(new Element(cacheKey, EngineLogic.getEngines()));
			}

			result = (List<IEngine>) APPCACHE.get(cacheKey).getObjectValue();
		}
		catch (Exception e) {
			Console.writeError(ApplicationLogic.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
		
		return result;
	}

	/**
	 * Retrieves all method concept matchers declared in configuration file.
	 * 
	 * @return
	 *         a list of IMethodConceptMatcher
	 */
	public static synchronized List<IMethodConceptMatcher> getMatchers() {

		List<IMethodConceptMatcher> result = null;
		
		try {
			
			String cacheKey = "getMatchers";

			if (APPCACHE.get(cacheKey) == null) {
				APPCACHE.put(new Element(cacheKey, MatchingLogic.getMatchers()));
			}

			result = (List<IMethodConceptMatcher>) APPCACHE.get(cacheKey).getObjectValue();
		}
		catch (Exception e) {
			Console.writeError(ApplicationLogic.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
		
		return result;
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

		List<Scenario> result = null;
		
		try {
			
			String cacheKey = "getScenarios_" + project.getKeyId();

			if (APPCACHE.get(cacheKey) == null) {
				APPCACHE.put(new Element(cacheKey, ScenarioLogic.getScenarios(project)));
			}

			result = (List<Scenario>) APPCACHE.get(cacheKey).getObjectValue();
		}
		catch (Exception e) {
			Console.writeError(ApplicationLogic.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
		
		return result;
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

		List<Concept> result = null;
		
		try {
			
			String cacheKey = "getConceptsByOntology_" + ontology.getKeyId();

			if (APPCACHE.get(cacheKey) == null) {
				APPCACHE.put(new Element(cacheKey, OntologyLogic.getConcepts(ontology)));
			}

			result = (List<Concept>) APPCACHE.get(cacheKey).getObjectValue();
		}
		catch (Exception e) {
			Console.writeError(ApplicationLogic.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
		
		return result;
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

		List<Concept> result = null;
		
		try {
			
			String cacheKey = "getConceptsByProject_" + project.getKeyId();

			if (APPCACHE.get(cacheKey) == null) {
				APPCACHE.put(new Element(cacheKey, OntologyLogic.getConcepts(project)));
			}

			result = (List<Concept>) APPCACHE.get(cacheKey).getObjectValue();
		}
		catch (Exception e) {
			Console.writeError(ApplicationLogic.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
		
		return result;
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

		Map<Integer, Concept> result = null;
		
		try {
			
			String cacheKey = "getConceptMapByOntology_" + ontology.getKeyId();

			if (APPCACHE.get(cacheKey) == null) {
				APPCACHE.put(new Element(cacheKey, OntologyLogic.getConceptMap(ontology)));
			}

			result = (Map<Integer, Concept>) APPCACHE.get(cacheKey).getObjectValue();
		}
		catch (Exception e) {
			Console.writeError(ApplicationLogic.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
		
		return result;
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

		Map<Integer, Concept> result = null;
		
		try {
			
			String cacheKey = "getConceptsMapByProject_" + project.getKeyId();

			if (APPCACHE.get(cacheKey) == null) {
				APPCACHE.put(new Element(cacheKey, OntologyLogic.getConceptMap(project)));
			}

			result = (Map<Integer, Concept>) APPCACHE.get(cacheKey).getObjectValue();
		}
		catch (Exception e) {
			Console.writeError(ApplicationLogic.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
		
		return result;
	}

	/**
	 * Retrieve all source classes owned by a project.
	 * 
	 * @param project
	 *        the owner
	 * 
	 * @return a list of SourceClass
	 */
	public static List<SourceClass> getSourceClasses(Project project) {

		List<SourceClass> result = null;
		
		try {
			
			String cacheKey = "getSourceClasses_" + project.getKeyId();

			if (APPCACHE.get(cacheKey) == null) {
				APPCACHE.put(new Element(cacheKey, SourceLogic.getSourceClasses(project)));
			}

			result = (List<SourceClass>) APPCACHE.get(cacheKey).getObjectValue();
		}
		catch (Exception e) {
			Console.writeError(ApplicationLogic.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
		
		return result;
	}

	/**
	 * Retrieve all source class with methods, parameters and references owned by a project.
	 * 
	 * @param project
	 *        the owner
	 * 
	 * @return a list of SourceClass
	 */
	public static List<SourceClass> getSourceClassMethodParam(Project project) {

		List<SourceClass> result = null;
		
		try {
			
			String cacheKey = "getSourceClassMethodParam_" + project.getKeyId();

			if (APPCACHE.get(cacheKey) == null) {
				APPCACHE.put(new Element(cacheKey, SourceLogic.getSourceClassMethodParam(project)));
			}

			result = (List<SourceClass>) APPCACHE.get(cacheKey).getObjectValue();
		}
		catch (Exception e) {
			Console.writeError(ApplicationLogic.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
		
		return result;
	}
	
	/**
	 * Retrieve all source class owned by a project as a map of (classId,
	 * SourceClass).
	 * 
	 * @param project
	 *        the owner
	 * 
	 * @return a map of (classId, SourceClass)
	 */
	public static Map<Integer, SourceClass> getSourceClassMap(Project project) {

		Map<Integer, SourceClass> result = null;
		
		try {
			
			String cacheKey = "getSourceClassMap_" + project.getKeyId();

			if (APPCACHE.get(cacheKey) == null) {
				APPCACHE.put(new Element(cacheKey, SourceLogic.getSourceClassMap(project)));
			}

			result = (Map<Integer, SourceClass>) APPCACHE.get(cacheKey).getObjectValue();
		}
		catch (Exception e) {
			Console.writeError(ApplicationLogic.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
		
		return result;
	}

	/**
	 * Retrieve all source methods owned by a project as a map of (methodId,
	 * SourceMethod).
	 * 
	 * @param project
	 *        the owner
	 * 
	 * @return a map of (methodId, SourceMethod)
	 */
	public static Map<Integer, SourceMethod> getSourceMethodMap(Project project) {

		Map<Integer, SourceMethod> result = null;
		
		try {
			
			String cacheKey = "getSourceMethodMap_" + project.getKeyId();

			if (APPCACHE.get(cacheKey) == null) {
				APPCACHE.put(new Element(cacheKey, SourceLogic.getSourceMethodMap(project)));
			}

			result = (Map<Integer, SourceMethod>) APPCACHE.get(cacheKey).getObjectValue();
		}
		catch (Exception e) {
			Console.writeError(ApplicationLogic.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
		
		return result;
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

		SourceMethod result = null;
		
		try {
			
			String cacheKey = "getSourceMethodByClassAndSignature_" + sourceClass.getKeyId() + "_" + signature;

			if (APPCACHE.get(cacheKey) == null) {
				APPCACHE.put(new Element(cacheKey, SourceLogic.getSourceMethodBySignature(sourceClass, signature)));
			}

			result = (SourceMethod) APPCACHE.get(cacheKey).getObjectValue();
		}
		catch (Exception e) {
			Console.writeError(ApplicationLogic.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
		
		return result;
	}

	/**
	 * Retrieve all stems associated to a name.
	 * Words present in rejectedList will not produce associated stems.
	 * 
	 * @param name
	 *        the name to use to extract stems
	 * @param rejectedList
	 *        the list of forbidden words
	 * @return
	 *         a list of stems associated to the list of names
	 */
	public static List<String> getStems(String name, List<String> rejectedList) {
		return StemLogic.getStems(name, rejectedList);
	}
	
	/**
	 * Retrieve a hierarchy of stem methods defined for a project.
	 * 
	 * More specifically allows one stem hierarchy to be retrieved for a
	 * specific concept.
	 * 
	 * So entries are of the form (methodId, root of StemMethod tree).
	 * 
	 * @param project
	 *        the owner
	 * @return
	 *         the map of (methodId, StemConcept)
	 */
	public static Map<Integer, StemMethod> getStemMethodTreeMap(Project project) {

		Map<Integer, StemMethod> result = null;
		
		try {
			
			String cacheKey = "getStemMethodTreeMap_" + project.getKeyId();

			if (APPCACHE.get(cacheKey) == null) {
				APPCACHE.put(new Element(cacheKey, StemLogic.getStemMethodTreeMap(project)));
			}

			result = (Map<Integer, StemMethod>) APPCACHE.get(cacheKey).getObjectValue();
		}
		catch (Exception e) {
			Console.writeError(ApplicationLogic.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
		
		return result;
	}

	/**
	 * Serialize a stem method tree into a single flat list of stem children.
	 * 
	 * @param rootStem
	 *        the root stem of a stem tree
	 * 
	 * @return
	 *         a flat list of stem methods
	 */
	public static List<StemMethod> inflateStemMethods(StemMethod rootStem) {
		return StemLogic.inflateStemMethods(rootStem);
	}

	/**
	 * Retrieve a map of all StemMethods in project, classified by term.
	 * Each entry will be of the form (term, List<StemMethod>).
	 * 
	 * @param project
	 *        the project owning stems
	 * 
	 * @return
	 *         a map of stems
	 */
	public static Map<String, List<StemMethod>> getStemMethodByTermMap(Project project) {

		Map<String, List<StemMethod>> result = null;
		
		try {
			
			String cacheKey = "getStemMethodsByTermMap_" + project.getKeyId();

			if (APPCACHE.get(cacheKey) == null) {
				APPCACHE.put(new Element(cacheKey, StemLogic.getStemMethodByTermMap(project)));
			}

			result = (Map<String, List<StemMethod>>) APPCACHE.get(cacheKey).getObjectValue();
		}
		catch (Exception e) {
			Console.writeError(ApplicationLogic.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
		
		return result;
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
	public static Map<Integer, StemConcept> getStemConceptTreeMap(Project project) {

		Map<Integer, StemConcept> result = null;
		
		try {
			
			String cacheKey = "getStemConceptTree_" + project.getKeyId();

			if (APPCACHE.get(cacheKey) == null) {
				APPCACHE.put(new Element(cacheKey, StemLogic.getStemConceptTreeMap(project)));
			}

			result = (Map<Integer, StemConcept>) APPCACHE.get(cacheKey).getObjectValue();
		}
		catch (Exception e) {
			Console.writeError(ApplicationLogic.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
		
		return result;
	}

	/**
	 * Serialize a stem concept tree into a single flat list of stem children.
	 * 
	 * @param rootStem
	 *        the root stem of a stem tree
	 * 
	 * @return
	 *         a flat list of stem concepts
	 */
	public static List<StemConcept> inflateStemConcepts(StemConcept rootStem) {
		return StemLogic.inflateStemConcepts(rootStem);
	}

	/**
	 * Retrieve a map of all StemConcepts in project, classified by term.
	 * Each entry will be of the form (term, List<StemConcept>).
	 * 
	 * @param project
	 *        the project owning stems
	 * 
	 * @return
	 *         a map of stems
	 */
	public static Map<String, List<StemConcept>> getStemConceptByTermMap(Project project) {

		Map<String, List<StemConcept>> result = null;
		
		try {
			
			String cacheKey = "getStemConceptsByTermMap_" + project.getKeyId();

			if (APPCACHE.get(cacheKey) == null) {
				APPCACHE.put(new Element(cacheKey, StemLogic.getStemConceptByTermMap(project)));
			}

			result = (Map<String, List<StemConcept>>) APPCACHE.get(cacheKey).getObjectValue();
		}
		catch (Exception e) {
			Console.writeError(ApplicationLogic.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
		
		return result;
	}

	/**
	 * Return a set of all terms which are intersecting among stem concepts and stem methods.	 
	 *  
	 * @param stemConcepts
	 *        the stem concepts
	 * @param stemMethods
	 *        the stem methods
	 * @return a set of string (each item are stem term)
	 */
	public static Set<String> getTermsIntersection(List<StemConcept> stemConcepts, List<StemMethod> stemMethods) {
		return StemLogic.getTermIntersection(stemConcepts, stemMethods);
	}
	
	/**
	 * Retrieve all traces owned by a scenario.
	 * 
	 * @param scenario
	 *        the scenario
	 */
	public static List<Trace> getTraces(Scenario scenario) {

		List<Trace> result = null;
		
		try {
			
			String cacheKey = "getTraces_" + scenario.getKeyId();

			if (APPCACHE.get(cacheKey) == null) {
				APPCACHE.put(new Element(cacheKey, TraceLogic.getTraces(scenario)));
			}

			result = (List<Trace>) APPCACHE.get(cacheKey).getObjectValue();
		}
		catch (Exception e) {
			Console.writeError(ApplicationLogic.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
		
		return result;
	}

	/**
	 * Retrieve the time series associated to a scenario traces.
	 * 
	 * @param project
	 *        the project owning the traces
	 * @param scenario
	 *        the scenario owning the traces
	 * @param matchMap
	 *        the matching map used to associate concepts to method
	 * @return the TimeSeries object gathering trace information
	 */
	public static TimeSeries getTimeSeries(Project project, Scenario scenario, IMethodConceptMatcher matcher) {

		TimeSeries result = null;
		
		try {
			
			String cacheKey = "getTimeSeries_" + project.getKeyId() + "_" + scenario.getKeyId() + "_" + matcher.toString();

			if (APPCACHE.get(cacheKey) == null) {
				APPCACHE.put(new Element(cacheKey, TimeSeriesLogic.getTimeSeries(project, scenario, matcher)));
			}

			result = (TimeSeries) APPCACHE.get(cacheKey).getObjectValue();
		}
		catch (Exception e) {
			Console.writeError(ApplicationLogic.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
		
		return result;
	}

	/**
	 * Retrieve all matchings between a method and a concept.
	 * 
	 * @param project
	 *        the project to analyse
	 * @param matcher
	 *        the matcher to use to compute matching
	 * @return
	 *         a map of (MethodId, List<MethodConceptMatch>)
	 */
	public static Map<Integer, List<MethodConceptMatch>> getMethodMatchingMap(Project project, IMethodConceptMatcher matcher) {

		Map<Integer, List<MethodConceptMatch>> result = null;
		
		try {
			
			String cacheKey = "getMethodConceptMap_" + project.getKeyId() + "_" + matcher.toString();

			if (APPCACHE.get(cacheKey) == null) {
				APPCACHE.put(new Element(cacheKey, MatchingLogic.getMethodMatchingMap(project, matcher)));
			}

			result = (Map<Integer, List<MethodConceptMatch>>) APPCACHE.get(cacheKey).getObjectValue();
		}
		catch (Exception e) {
			Console.writeError(ApplicationLogic.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
		
		return result;
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
	 * Export all matchings passed in argument in a CSV file.
	 * 
	 * @param matchings
	 *        the MethodConceptMatch to save
	 * @param filename
	 *        the csv filename target
	 */
	public static void exportMatchings(Map<Integer, List<MethodConceptMatch>> matchMap, String filename) {
		MatchingLogic.saveMatchings(matchMap, filename);
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
	 * Save a list of stem method.
	 * 
	 * @param stem
	 *        the StemMethod list to save
	 */
	public static void saveStemMethods(List<StemMethod> stems) {
		StemLogic.saveStemMethods(stems);
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
