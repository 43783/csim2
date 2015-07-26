package ch.hesge.csim2.core.logic;

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

	// Unique instance
	public static final ApplicationLogic UNIQUE_INSTANCE = new ApplicationLogic();
	
	// Private attributes
	private Cache APPCACHE;
	
	// Internal constants
	private static final String LOG4J_CONFIG_PATH   = "conf/log4j2.xml";
	private static final String EHCACHE_CONFIG_PATH = "conf/ehcache.xml";
	
	private static final String USER_NAME_PROPERTY        = "user-name";
	private static final String USER_FOLDER_PROPERTY      = "user-folder";
	private static final String APPLICATION_BASE_PROPERTY = "application-folder";

	private static final String DATABASE_CONNECTION_PROPERTY = "database-connection";
	private static final String DATABASE_USER_PROPERTY       = "database-user";
	private static final String DATABASE_PASSWORD_PROPERTY   = "database-password";

	/**
	 * Private constructor
	 */
	private ApplicationLogic() {
		
		// Configuration log4j
		System.setProperty("log4j.configurationFile", LOG4J_CONFIG_PATH);

		// Configure cache management
		APPCACHE = CacheManager.create(EHCACHE_CONFIG_PATH).getCache("csim2");
	}
	
	/**
	 * Create an initialize a new application instance.
	 * 
	 * @return
	 *         the new application instance
	 */
	public Application createApplication() {

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
	public void shutdownApplication(Application application) {
		CacheManager.getInstance().shutdown();
		System.exit(0);
	}

	/**
	 * Retrieve the application version.
	 * 
	 * @return
	 *         a string containing the version
	 */
	public String getVersion() {
		return Application.VERSION;
	}

	/**
	 * Clear all data in cache.
	 */
	public void clearCache() {
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
	private void initAppProperties(Application application) {

		Properties properties = application.getProperties();

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
	}

	/**
	 * Load all remote settings, that is all settings defined in the database
	 * within the 'settings' table.
	 * 
	 * @param properties
	 *        the application properties
	 */
	private void initDbProperties(Application application) {

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
	 * Retrieve all available projects.
	 * 
	 * @return
	 *         a list of project
	 */
	public List<Project> getProjects() {

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
	public List<Ontology> getOntologies() {

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
	public List<Ontology> getOntologies(Project project) {

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
	public List<Scenario> getScenarios() {

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
	public List<IEngine> getEngines() {

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
	public synchronized List<IMethodConceptMatcher> getMatchers() {

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
	public List<Scenario> getScenarios(Project project) {

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
	public List<Concept> getConcepts(Ontology ontology) {

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
	 * Retrieve all ontology concepts as a hierarchy.
	 * 
	 * @param project
	 *        the owner
	 * 
	 * @return a list of concept root
	 */
	public List<Concept> getConceptTree(Project project) {

		List<Concept> result = null;
		
		try {
			
			String cacheKey = "getConceptTreeByProject_" + project.getKeyId();

			if (APPCACHE.get(cacheKey) == null) {
				APPCACHE.put(new Element(cacheKey, OntologyLogic.getConceptTree(project)));
			}

			result = (List<Concept>) APPCACHE.get(cacheKey).getObjectValue();
		}
		catch (Exception e) {
			Console.writeError(ApplicationLogic.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
		
		return result;
	}

	/**
	 * Retrieve a map of concepts owned by a project
	 * with each entries of the form (keyId, Concept) map.
	 * 
	 * @param project
	 *        the owner
	 * 
	 * @return
	 *         a map of concept
	 */
	public Map<Integer, Concept> getConceptMap(Project project) {

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
	 * Retrieve a list of all concepts with their granularity computed.
	 * 
	 * @param ontology
	 *        the owner
	 * 
	 * @return
	 *         the list of concept
	 */
	public List<Concept> getConceptsGranularity(Project project) {

		List<Concept> result = null;
		
		try {
			
			String cacheKey = "getConceptsGranularity_" + project.getKeyId();

			if (APPCACHE.get(cacheKey) == null) {
				APPCACHE.put(new Element(cacheKey, OntologyLogic.getConceptsGranularity(project)));
			}

			result = (List<Concept>) APPCACHE.get(cacheKey).getObjectValue();
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
	public Map<Integer, SourceClass> getSourceClassMap(Project project) {

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
	 * Retrieve all source classes as a hierarchy.
	 * 
	 * @param project
	 *        the owner
	 * 
	 * @return a list of source class root
	 */
	public List<SourceClass> getSourceClassTree(Project project) {

		List<SourceClass> result = null;
		
		try {
			
			String cacheKey = "getSourceClassTree_" + project.getKeyId();

			if (APPCACHE.get(cacheKey) == null) {
				APPCACHE.put(new Element(cacheKey, SourceLogic.getSourceClassTree(project)));
			}

			result = (List<SourceClass>) APPCACHE.get(cacheKey).getObjectValue();
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
	public Map<Integer, SourceMethod> getSourceMethodMap(Project project) {

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
	public SourceMethod getSourceMethodBySignature(SourceClass sourceClass, String signature) {

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
	 * Retrieve a list of all source class with their granularity computed.
	 * 
	 * @param project
	 *        the owner
	 * 
	 * @return
	 *         the list of source classes
	 */
	public List<SourceClass> getSourceClassesGranularity(Project project) {

		List<SourceClass> result = null;
		
		try {
			
			String cacheKey = "getSourceClassesGranularity_" + project.getKeyId();

			if (APPCACHE.get(cacheKey) == null) {
				APPCACHE.put(new Element(cacheKey, SourceLogic.getSourceClassesGranularity(project)));
			}

			result = (List<SourceClass>) APPCACHE.get(cacheKey).getObjectValue();
		}
		catch (Exception e) {
			Console.writeError(ApplicationLogic.class, "an unexpected error has occured: " + StringUtils.toString(e));
		}
		
		return result;
	}
	
	/**
	 * Retrieve all stems associated to a term.
	 * Words present in rejectedList will not produce associated stems.
	 * 
	 * @param term
	 *        the name to use to extract stems
	 * @param rejectedList
	 *        the list of forbidden words
	 * @return
	 *         a list of stems associated to the list of names
	 */
	public List<String> getStems(String term, List<String> rejectedList) {
		return StemLogic.getStems(term, rejectedList);
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
	public Map<Integer, StemMethod> getStemMethodTreeMap(Project project) {

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
	public List<StemMethod> inflateStemMethods(StemMethod rootStem) {
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
	public Map<String, List<StemMethod>> getStemMethodByTermMap(Project project) {

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
	public Map<Integer, StemConcept> getStemConceptTreeMap(Project project) {

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
	public List<StemConcept> inflateStemConcepts(StemConcept rootStem) {
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
	public Map<String, List<StemConcept>> getStemConceptByTermMap(Project project) {

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
	public Set<String> getTermsIntersection(List<StemConcept> stemConcepts, List<StemMethod> stemMethods) {
		return StemLogic.getTermIntersection(stemConcepts, stemMethods);
	}
	
	/**
	 * Retrieve all traces owned by a scenario.
	 * 
	 * @param scenario
	 *        the scenario
	 */
	public List<Trace> getTraces(Scenario scenario) {

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
	 * Retrieve all traces owned by a scenario as a hierarchy.
	 * 
	 * @param scenario
	 * @return a list of trace root
	 */
	public List<Trace> getTraceTree(Scenario scenario) {

		List<Trace> result = null;
		
		try {
			
			String cacheKey = "getTraceTree_" + scenario.getKeyId();

			if (APPCACHE.get(cacheKey) == null) {
				APPCACHE.put(new Element(cacheKey, TraceLogic.getTraceTree(scenario)));
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
	public TimeSeries getTimeSeries(Project project, Scenario scenario, IMethodConceptMatcher matcher) {

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
	public Map<Integer, List<MethodConceptMatch>> getMethodMatchingMap(Project project, IMethodConceptMatcher matcher) {

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
	public TimeSeries getFilteredTimeSeries(TimeSeries timeSeries, int segmentCount, double threshold, List<Concept> concepts) {
		return TimeSeriesLogic.getFilteredTimeSeries(timeSeries, segmentCount, threshold, concepts);
	}

	/**
	 * Create a new project.
	 * 
	 * @param name
	 *        the name of the new project
	 * 
	 * @return and instance of project
	 */
	public Project createProject(String name) {
		return ProjectLogic.createProject(name);
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
	public Concept createConcept(Ontology ontology) {
		return OntologyLogic.createConcept(ontology);
	}

	/**
	 * Clone the concept passed in argument into a distinct instance (same
	 * keyId).
	 * 
	 * @param concept
	 *        the concept to clone
	 * @return a new concept instance
	 */
	public Concept cloneConcept(Concept concept) {
		return OntologyLogic.cloneConcept(concept);
	}
	
	/**
	 * Copy concept properties to an other one, without modifying target instance identity.
	 * 
	 * @param source
	 *        the concept with properties to copy
	 * @param target
	 *        the concept to clear with source properties
	 */
	public void copyConceptProperties(Concept source, Concept target) {
		OntologyLogic.copyConceptProperties(source, target);
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
	public ConceptLink createConceptLink(Ontology ontology, Concept source, Concept target) {
		return OntologyLogic.createConceptLink(ontology, source, target);
	}
	
	/**
	 * Create a new scenario.
	 * 
	 * @param name
	 *        the scenario name
	 * @param project
	 *        the owning project
	 * @return and instance of scenario
	 */
	public Scenario createScenario(String name, Project project) {
		return ScenarioLogic.createScenario(name, project);
	}
	
	/**
	 * Create a scenario step.
	 * 
	 * @param step
	 *        the step to attach to
	 * @param scenario
	 * @return the newly create step
	 */
	public ScenarioStep createScenarioStep(String name, String description, Scenario scenario) {
		return ScenarioLogic.createStep(name, description, scenario);
	}

	/**
	 * Create a new ontology.
	 * 
	 * @param name
	 *        the ontology name
	 * @param project
	 *        the owning project
	 * @return and instance of ontology
	 */
	public Ontology createOntology(String name, Project project) {
		return OntologyLogic.createOntology(name, project);
	}
	
	/**
	 * Delete all traces owned by a scenario.
	 * 
	 * @param scenario
	 *        the scenario
	 */
	public void deleteTraces(Scenario scenario) {
		TraceLogic.deleteTraces(scenario);
	}

	/**
	 * Delete all sources owned by an project. Thas is class, attribute, method,
	 * parameter and reference.
	 * 
	 * @param project
	 *        the project to clean sources
	 */
	public void deleteSources(Project project) {
		SourceLogic.deleteSources(project);
	}

	/**
	 * Delete a scenario and all its dependencies.
	 * 
	 * @param project
	 *        the scenario to delete
	 */
	public void deleteScenario(Scenario scenario) {
		ScenarioLogic.deleteScenario(scenario);
	}
	
	/**
	 * Delete a single scenario step.
	 * 
	 * @param step
	 *        the scenario step to delete
	 */
	public void deleteScenarioStep(Scenario scenario, ScenarioStep step) {
		ScenarioLogic.deleteScenarioStep(scenario, step);
	}
	
	/**
	 * Delete an ontology and all its dependencies.
	 * 
	 * @param ontology
	 *        the ontology to delete
	 */
	public void deleteOntology(Ontology ontology) {
		OntologyLogic.deleteOntology(ontology);
	}
	
	/**
	 * Delete a project and all its dependencies.
	 * 
	 * @param project
	 *        the project to delete
	 */
	public void deleteProject(Project project) {
		ProjectLogic.deleteProject(project);
	}
	
	/**
	 * Save a project without its dependencies.
	 * 
	 * @param project
	 *        the project to save.
	 */
	public void saveProject(Project project) {
		ProjectLogic.saveProject(project);
	}

	/**
	 * Save scenario passed in argument.
	 * 
	 * @param scenario
	 *        the concerned scenario
	 */
	public void saveScenario(Scenario scenario) {
		ScenarioLogic.saveScenario(scenario);
	}

	/**
	 * Save a list of scenarios passed in argument.
	 * 
	 * @param scenarios
	 *        the concerned scenarios
	 */
	public void saveScenarios(List<Scenario> scenarios) {
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
	public void saveSourceClasses(Project project, List<SourceClass> sourceClasses) {
		SourceLogic.saveSourceClasses(project, sourceClasses);
	}

	/**
	 * Export an ontology as a Turtle file.
	 * 
	 * @param ontology
	 *        the ontology to export
	 * @param filename
	 *        the name of the turtle file
	 */
	public void exportOntology(Ontology ontology, String filename) {
		OntologyLogic.exportOntology(ontology, filename);
	}

	/**
	 * Import an ontology from a Turtle file.
	 * 
	 * @param ontology
	 *        the ontology to populate with file content
	 * @param filename
	 *        the name of the turtle file
	 */
	public void importOntology(Ontology ontology, String filename) {
		OntologyLogic.importOntology(ontology, filename);
	}

	/**
	 * Save an ontology and its concepts.
	 * 
	 * @param ontology
	 *        the ontology to save
	 */
	public void saveOntology(Ontology ontology) {
		OntologyLogic.saveOntology(ontology);
	}

	/**
	 * Save the trace passed in argument.
	 * 
	 * @param trace
	 *        the trace to save
	 */
	public void saveTrace(Trace trace) {
		TraceLogic.saveTrace(trace);
	}

	/**
	 * Save a list of stem concept.
	 * 
	 * @param ontology
	 *        the ontology owning the stems to save
	 * @param stems
	 *        a list of StemConcept to save
	 */
	public void saveStemConcepts(Ontology ontology, List<StemConcept> stems) {
		StemLogic.saveStemConcepts(ontology, stems);
	}

	/**
	 * Save a list of stem method.
	 * 
	 * @param project
	 *        the project owning stems to save
	 * @param stem
	 *        the StemMethod list to save
	 */
	public void saveStemMethods(Project project, List<StemMethod> stems) {
		StemLogic.saveStemMethods(project, stems);
	}

	/**
	 * Start the engine passed in argument. A new thread is allocation from pool
	 * 
	 * @param engine
	 *        the engine to start
	 */
	public void startEngine(IEngine engine) {
		EngineLogic.startEngine(engine);
	}

	/**
	 * Stop the engine passed in argument. The thread release return the
	 * allocation from pool
	 * 
	 * @param engine
	 *        the engine to stop
	 */
	public void stopEngine(IEngine engine) {
		EngineLogic.stopEngine(engine);
	}

	/**
	 * Load a project with its direct dependencies only. 
	 * That is scenarios, ontologies (not sources).
	 * 
	 * @param project
	 *        the project to load.
	 * @return the initialized instance passed in argument
	 */
	public void loadProject(Project project) {
		ProjectLogic.loadProject(project);
	}

	/**
	 * Export all matchings passed in argument in a CSV file.
	 * 
	 * @param matchings
	 *        the MethodConceptMatch to save
	 * @param filename
	 *        the csv filename target
	 */
	public void exportMatchings(Map<Integer, List<MethodConceptMatch>> matchMap, String filename) {
		MatchingLogic.exportMatchings(matchMap, filename);
	}
	
	/**
	 * Return the engine running state.
	 * 
	 * @param engine
	 * @return true, if the engine is current running, false otherwise.
	 */
	public boolean isEngineRunning(IEngine engine) {
		return EngineLogic.isEngineRunning(engine);
	}

	/**
	 * Initialize current time of a specific scenario step.
	 * 
	 * @param scenario
	 *        the concerned scenario
	 */
	public void initExecutionTime(ScenarioStep scenarioStep) {
		ScenarioLogic.initExecutionTime(scenarioStep);
	}

	/**
	 * Clear execution time of all scenario steps.
	 * 
	 * @param scenario
	 *        the concerned scenario
	 */
	public void resetExecutionTimes(Scenario scenario) {
		ScenarioLogic.resetExecutionTimes(scenario);
	}

	/**
	 * Remove a concept from an ontology.
	 * 
	 * @param ontology
	 *        the ontology owning the concept
	 * @param concept
	 *        the concept to remove
	 */
	public void removeConcept(Ontology ontology, Concept concept) {
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
	public void removeConceptLink(Ontology ontology, Concept concept, ConceptLink link) {
		OntologyLogic.removeConceptLink(ontology, concept, link);
	}	
}
