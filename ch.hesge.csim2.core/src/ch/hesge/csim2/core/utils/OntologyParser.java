package ch.hesge.csim2.core.utils;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.formats.ManchesterSyntaxDocumentFormat;
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.model.AddOntologyAnnotation;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import ch.hesge.csim2.core.logic.OntologyLogic;
import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.ConceptAttribute;
import ch.hesge.csim2.core.model.ConceptClass;
import ch.hesge.csim2.core.model.ConceptLink;
import ch.hesge.csim2.core.model.Ontology;

/**
 * This class convert turtle triplet into concept.
 * Derived from Turtle2NTriples sources.
 * 
 * @author Eric Harth
 *
 */
public class OntologyParser {

	// Private attributes
	private boolean debug;
	private IRI ontologyIRI;
	private Ontology ontology;
	private Map<Integer, OWLClass> owlClassMap;
	private Map<String, Concept> conceptMap;
	private Map<String, ConceptAttribute> attributeMap;
	private Map<String, ConceptClass> conceptClassMap;
	private Map<String, ConceptLink> linkMap;

	/**
	 * Default constructor
	 */
	public OntologyParser(boolean debug) {

		this.debug = debug;
		this.ontology = new Ontology();
		this.owlClassMap = new HashMap<>();
		this.conceptMap = new HashMap<>();
		this.attributeMap = new HashMap<>();
		this.conceptClassMap = new HashMap<>();
		this.linkMap = new HashMap<>();
	}

	/**
	 * Generate OWL file associated to an ontology.
	 * According to file extension, these format are supported:
	 * <code>
	 * 		*.owl 	owl/xml format
	 * 		*.ttl	turtle format
	 * 		*.rdf	rdf/xml format
	 * 		*.man	manchester format
	 * 		*.fs	functionnal syntax format
	 * </code>
	 * 
	 * @param ontology
	 * @param filename
	 * @throws OWLOntologyCreationException
	 * @throws OWLOntologyStorageException
	 */
	public void generate(Ontology ontology, String filename) throws OWLOntologyCreationException, OWLOntologyStorageException {

		FileOutputStream fileStream = null;
		
		try {

			// Create a file writer (UTF8 support)
			fileStream = new FileOutputStream(new File(filename));
			
			owlClassMap.clear();
			ontologyIRI = IRI.create("http://hesge.ch/csim2/#");

			Console.writeInfo(this, "start generating.");

			// Initialize OWL-API
			OWLOntologyManager owlManager = OWLManager.createOWLOntologyManager();
			OWLOntology owlOntology = owlManager.createOntology(ontologyIRI);
			OWLDataFactory owlFactory = owlManager.getOWLDataFactory();

			// Build a map of all OWLClass
			for (Concept concept : ontology.getConcepts()) {			
				IRI owlIri = IRI.create(ontologyIRI + "class" + concept.getKeyId());
				OWLClass owlClass = owlFactory.getOWLClass(owlIri);
				owlClassMap.put(concept.getKeyId(), owlClass);
			}

			// Generate ontology content
			generateAnnotations(owlOntology, owlManager, ontology);		
			for (Concept concept : ontology.getConcepts()) {
				generateClass(owlOntology, owlManager, concept);
				generateAttributes(owlOntology, owlManager, concept);
				generateLinks(owlOntology, owlManager, concept);
			}

			// Select format
			OWLDocumentFormat owlFormat = new OWLXMLDocumentFormat();
			
			if (filename.endsWith(".ttl")) {
				owlFormat = new TurtleDocumentFormat();
			}
			else if (filename.endsWith(".rdf")) {
				owlFormat = new RDFXMLDocumentFormat();
			}
			else if (filename.endsWith(".man")) {
				owlFormat = new ManchesterSyntaxDocumentFormat();
			}
			else if (filename.endsWith(".fs")) {
				owlFormat = new FunctionalSyntaxDocumentFormat();
			}
			
			// Save the ontology
			owlManager.saveOntology(owlOntology, owlFormat, IRI.create(new File(filename)));
			
			Console.writeInfo(this, "generating done.");
			fileStream.flush();
			fileStream.close();
		}
		catch(Exception e)  {
			
			if (fileStream != null) {
				try {
					fileStream.close();
				}
				catch (IOException e1) {
					// Close silently
				}
			}
			
			Console.writeError(this, "an unexpected error has occured: " + StringUtils.toString(e));
		}		
	}

	/**
	 * Generate ontology annotation (description + version).
	 * 
	 * @param owlOntology
	 * @param owlManager
	 * @param ontology
	 */
	private void generateAnnotations(OWLOntology owlOntology, OWLOntologyManager owlManager, Ontology ontology) {
		
		OWLDataFactory owlFactory = owlManager.getOWLDataFactory();

		// Define description annotation
		OWLLiteral owlLiteral = owlFactory.getOWLLiteral("Ontology " + ontology.getName() + " (generated by csim2 environment)"); 
		OWLAnnotation owlOntoName = owlFactory.getOWLAnnotation(owlFactory.getRDFSLabel(), owlLiteral);
		owlManager.applyChange(new AddOntologyAnnotation(owlOntology, owlOntoName));

		// Define version annotation
		OWLAnnotation owlOntoVersion = owlFactory.getOWLAnnotation(owlFactory.getOWLAnnotationProperty(OWLRDFVocabulary.OWL_VERSION_INFO.getIRI()), owlFactory.getOWLLiteral("1.0"));
		owlManager.applyChange(new AddOntologyAnnotation(owlOntology, owlOntoVersion));
	}
	
	/**
	 * Generate all owl entities related to concept.
	 * 
	 * @param owlOntology
	 * @param owlManager
	 * @param concept
	 */
	private void generateClass(OWLOntology owlOntology, OWLOntologyManager owlManager, Concept concept) {
		
		OWLDataFactory owlFactory = owlManager.getOWLDataFactory();

		// Declare the class
		OWLClass owlClass = owlClassMap.get(concept.getKeyId());
		owlManager.addAxiom(owlOntology, owlFactory.getOWLDeclarationAxiom(owlClass));
		
		// Declare its name
		OWLAnnotation owlClassLabel = owlFactory.getOWLAnnotation(owlFactory.getRDFSLabel(), owlFactory.getOWLLiteral(concept.getName(), ""));			
		owlManager.addAxiom(owlOntology, owlFactory.getOWLAnnotationAssertionAxiom(owlClass.getIRI(), owlClassLabel));

		// Declare its subsumption relation
		if (concept.getSuperConcept() != null) {
			OWLClass owlSuperClass = owlClassMap.get(concept.getSuperConcept().getKeyId());
			owlManager.addAxiom(owlOntology, owlFactory.getOWLSubClassOfAxiom(owlClass, owlSuperClass));
		}
	}
	
	/**
	 * Generate all owl entities related to attributes.
	 * 
	 * @param owlOntology
	 * @param owlManager
	 * @param concept
	 */
	private void generateAttributes(OWLOntology owlOntology, OWLOntologyManager owlManager, Concept concept) {
		
		IRI attributeIRI;
		OWLClass owlClass = owlClassMap.get(concept.getKeyId());
		
		// Create an attribute for bounds
		attributeIRI = IRI.create(ontologyIRI + "attributeBounds" + concept.getKeyId());
		String attributeIdentifier = concept.getBounds().x + "," + concept.getBounds().y + "," + concept.getBounds().width + "," + concept.getBounds().height; 
		generateSingleAttribute(owlOntology, owlManager, owlClass, attributeIRI, "@Bounds", attributeIdentifier);
		
		// Create an attribute for action
		attributeIRI = IRI.create(ontologyIRI + "attributeAction" + concept.getKeyId());
		generateSingleAttribute(owlOntology, owlManager, owlClass, attributeIRI, "@Action", String.valueOf(concept.isAction()));
		
		// Create attributes
		for (ConceptAttribute attribute : concept.getAttributes()) {

			attributeIRI = IRI.create(ontologyIRI + "attributeName" + attribute.getKeyId());
			generateSingleAttribute(owlOntology, owlManager, owlClass, attributeIRI, attribute.getName(), attribute.getIdentifier());
		}

		// Create class attributes
		for (ConceptClass attribute : concept.getClasses()) {
			
			attributeIRI = IRI.create(ontologyIRI + "attributeClass" + attribute.getKeyId());
			generateSingleAttribute(owlOntology, owlManager, owlClass, attributeIRI, attribute.getName(), attribute.getIdentifier());
		}
	}
	
	/**
	 * Generate all owl entities related to links.
	 * 
	 * @param owlOntology
	 * @param owlManager
	 * @param concept
	 */
	private void generateLinks(OWLOntology owlOntology, OWLOntologyManager owlManager, Concept concept) {
		
		int linkCount = 0;
		OWLDataFactory owlFactory = owlManager.getOWLDataFactory();
		
		for (ConceptLink link : concept.getLinks()) {
			
			if (!OntologyLogic.isSubsumptionLink(link)) {

				IRI attributeIRI = IRI.create(ontologyIRI + "link_" + concept.getKeyId() + "_" + linkCount++);
				
				// Declare the link
				OWLObjectProperty owlObjectProperty = owlFactory.getOWLObjectProperty(attributeIRI);
				owlManager.addAxiom(owlOntology, owlFactory.getOWLDeclarationAxiom(owlObjectProperty));
				
				// Declare its source
				OWLClass owlSource = owlClassMap.get(concept.getKeyId());
			    OWLObjectPropertyDomainAxiom owlDomain = owlFactory.getOWLObjectPropertyDomainAxiom(owlObjectProperty, owlSource);
				owlManager.addAxiom(owlOntology, owlDomain);

				// Declare its target
				OWLClass owlTarget = owlClassMap.get(link.getTargetConcept().getKeyId());
			    OWLObjectPropertyRangeAxiom owlRange = owlFactory.getOWLObjectPropertyRangeAxiom(owlObjectProperty, owlTarget);
				owlManager.addAxiom(owlOntology, owlRange);

				// Define its name 
				OWLAnnotation owlLinkLabel = owlFactory.getOWLAnnotation(owlFactory.getRDFSLabel(), owlFactory.getOWLLiteral(link.getQualifier(), ""));			
				owlManager.addAxiom(owlOntology, owlFactory.getOWLAnnotationAssertionAxiom(owlObjectProperty.getIRI(), owlLinkLabel));
			}
		}
	}
	
	/**
	 * Generate all owl entities related to a single attribute.
	 * 
	 * @param owlOntology
	 * @param owlManager
	 * @param owlClass
	 * @param attributeIRI
	 * @param attributeName
	 * @param attributeIdentifier
	 */
	private void generateSingleAttribute(OWLOntology owlOntology, OWLOntologyManager owlManager, OWLClass owlClass, IRI attributeIRI, String attributeName, String attributeIdentifier) {
		
		OWLDataFactory owlFactory = owlManager.getOWLDataFactory();
		
		// Declare the attribute
		OWLDataProperty owlDataProperty = owlFactory.getOWLDataProperty(attributeIRI);
		owlManager.addAxiom(owlOntology, owlFactory.getOWLDeclarationAxiom(owlDataProperty));
		
		// Declare its owner
	    OWLDataPropertyDomainAxiom owlDomain = owlFactory.getOWLDataPropertyDomainAxiom(owlDataProperty, owlClass);
		owlManager.addAxiom(owlOntology, owlDomain);

		// Define its name 
		OWLAnnotation owlAttributeLabel = owlFactory.getOWLAnnotation(owlFactory.getRDFSLabel(), owlFactory.getOWLLiteral(attributeName, ""));			
		owlManager.addAxiom(owlOntology, owlFactory.getOWLAnnotationAssertionAxiom(owlDataProperty.getIRI(), owlAttributeLabel));
		
		// Define its identifier
		OWLAnnotation owlAttributeIdentifier = owlFactory.getOWLAnnotation(owlFactory.getRDFSLabel(), owlFactory.getOWLLiteral(attributeIdentifier, "ie"));			
		owlManager.addAxiom(owlOntology, owlFactory.getOWLAnnotationAssertionAxiom(owlDataProperty.getIRI(), owlAttributeIdentifier));				
	}

	/**
	 * Parse a owl file.
	 * These format are supported:
	 * <code>
	 * 		*.owl 	owl/xml format
	 * 		*.ttl	turtle format
	 * 		*.rdf	rdf/xml format
	 * 		*.man	manchester format
	 * 		*.fs	functionnal syntax format
	 * </code>
	 * 
	 * @param filename
	 * @throws OWLException
	 */
	public Ontology parse(String filename) throws OWLException {

		FileInputStream fileStream = null;
		
		try {

			// Create a file writer (UTF8 support)
			fileStream = new FileInputStream(new File(filename));
			
			// Load the ontology
			IRI documentIRI = IRI.create(filename);
			OWLOntologyManager owlManager = OWLManager.createOWLOntologyManager();
			OWLOntology owlOntology = owlManager.loadOntologyFromOntologyDocument(fileStream);

			// Report information about the ontology
			Console.writeInfo(this, "ontology loading...");
			Console.writeInfo(this, "document IRI: " + documentIRI);
			Console.writeInfo(this, "ontology:     " + owlOntology.getOntologyID());
			Console.writeInfo(this, "format:       " + owlManager.getOntologyFormat(owlOntology));
			Console.writeInfo(this, "axioms:       " + owlOntology.getAxiomCount());

			Console.writeInfo(this, "start parsing.");
			
			// Parse the ontology
			parseClasses(owlOntology);
			parseAttributes(owlOntology);
			parseLinks(owlOntology);
			parseAnnotations(owlOntology);
			parseFiltering();

			// Update ontology concepts
			ontology.getConcepts().clear();
			ontology.getConcepts().addAll(conceptMap.values());

			// Dump result
			if (debug) {

				Console.writeDebug(this, ontology.getConcepts().size() + " concepts found:");

				// Dump all concepts
				for (Concept concept : ontology.getConcepts()) {
					dumpConcept(concept);
				}
			}

			Console.writeInfo(this, "parsing done.");
			fileStream.close();
		}
		catch(Exception e)  {
			
			if (fileStream != null) {
				try {
					fileStream.close();
				}
				catch (IOException e1) {
					// Close silently
				}
			}
			
			Console.writeError(this, "an unexpected error has occured: " + StringUtils.toString(e));
		}		
		
		return ontology;
	}

	/**
	 * Parse all classes contained in ontology.
	 * 
	 * @param owlOntology
	 * @throws OWLException
	 */
	private void parseClasses(OWLOntology owlOntology) throws OWLException {

		// First detect class declarations
		for (OWLAxiom owlAxiom : owlOntology.getAxioms(AxiomType.DECLARATION)) {

			OWLEntity owlEntity = ((OWLDeclarationAxiom) owlAxiom).getEntity();
			String owlName = owlEntity.getIRI().getShortForm();

			// Detect classes
			if (owlEntity.isOWLClass() && !conceptMap.containsKey(owlName)) {

				Concept concept = new Concept();
				concept.setName(owlName);

				conceptMap.put(owlName, concept);
				Console.writeDebug(this, "class: " + owlName);
			}
		}

		// Then detect subclassof axioms
		for (OWLAxiom owlAxiom : owlOntology.getAxioms()) {

			// Detect super concept
			if (owlAxiom.getAxiomType() == AxiomType.SUBCLASS_OF) {
				
				OWLEntity owlEntity = null;
				OWLEntity owlSuperEntity = null;
				OWLSubClassOfAxiom owlSubclassOf = (OWLSubClassOfAxiom) owlAxiom;

				if (owlSubclassOf.getSubClass().getClassesInSignature().size() > 0) {
					owlEntity = owlSubclassOf.getSubClass().getClassesInSignature().iterator().next();
				}
				
				if (owlSubclassOf.getSuperClass().getClassesInSignature().size() > 0) {
					owlSuperEntity = owlSubclassOf.getSuperClass().getClassesInSignature().iterator().next();				
				}
				else if (owlSubclassOf.getSuperClass().getIndividualsInSignature().size() > 0) {
					owlSuperEntity = owlSubclassOf.getSuperClass().getIndividualsInSignature().iterator().next();				
				}

				if (owlEntity != null && owlSuperEntity != null) {
					
					String conceptName = owlEntity.getIRI().getShortForm();
					String superConceptName = owlSuperEntity.getIRI().getShortForm();

					if (conceptMap.containsKey(conceptName) && conceptMap.containsKey(superConceptName)) {

						Concept concept = conceptMap.get(conceptName);
						Concept superconcept = conceptMap.get(superConceptName);

						ConceptLink link = new ConceptLink();
						link.setQualifier("subclass-of");
						link.setSourceConcept(concept);
						link.setTargetConcept(superconcept);

						concept.setSuperConcept(superconcept);
						concept.getLinks().add(link);

						Console.writeDebug(this, "concept: " + conceptName + ", superconcept: " + superConceptName);
					}
				}
			}
		}
	}

	/**
	 * Parse all class attributes contained in ontology.
	 * 
	 * @param owlOntology
	 * @throws OWLException
	 */
	private void parseAttributes(OWLOntology owlOntology) throws OWLException {

		// First detect attribute declarations
		for (OWLAxiom owlAxiom : owlOntology.getAxioms(AxiomType.DECLARATION)) {

			OWLEntity owlEntity = ((OWLDeclarationAxiom) owlAxiom).getEntity();
			String owlName = owlEntity.getIRI().getShortForm();

			// Detect attributes
			if (owlEntity.isOWLDataProperty()) {

				// Detect class attributes
				if (!conceptClassMap.containsKey(owlName) && owlName.startsWith("attributeClass")) {

					ConceptClass conceptClass = new ConceptClass();
					conceptClass.setName(owlName);

					conceptClassMap.put(owlName, conceptClass);
					Console.writeDebug(this, "attribute classname: " + owlName);
				}

				// Detect standard attributes
				else if (!attributeMap.containsKey(owlName)) {

					ConceptAttribute attribute = new ConceptAttribute();
					attribute.setName(owlName);

					attributeMap.put(owlName, attribute);
					Console.writeDebug(this, "attribute name: " + owlName);
				}
			}
		}

		// Then detect attribute owner
		for (OWLAxiom owlAxiom : owlOntology.getAxioms()) {

			// Detect super concept
			if (owlAxiom.getAxiomType() == AxiomType.DATA_PROPERTY_DOMAIN) {

				OWLEntity owlAttributeEntity = null;
				OWLEntity owlOwnerEntity = null;
				OWLDataPropertyDomainAxiom owlDomain = (OWLDataPropertyDomainAxiom) owlAxiom;
				
				if (owlDomain.getProperty().getSignature().size() > 0) {
					owlAttributeEntity = owlDomain.getProperty().getSignature().iterator().next();
				}
				
				if (owlDomain.getDomain().getSignature().size() > 0) {
					owlOwnerEntity = owlDomain.getDomain().getSignature().iterator().next();
				}

				if (owlAttributeEntity != null && owlOwnerEntity != null) {
					
					String conceptName = owlOwnerEntity.getIRI().getShortForm();
					String attributeName = owlAttributeEntity.getIRI().getShortForm();

					// Detect class attributes owner
					if (conceptClassMap.containsKey(attributeName) && conceptMap.containsKey(conceptName)) {
						conceptMap.get(conceptName).getClasses().add(conceptClassMap.get(attributeName));
						Console.writeDebug(this, "concept: " + conceptName + ", attribute class: " + attributeName);
					}

					// Detect standard attributes owner
					else if (attributeMap.containsKey(attributeName) && conceptMap.containsKey(conceptName)) {
						conceptMap.get(conceptName).getAttributes().add(attributeMap.get(attributeName));
						Console.writeDebug(this, "concept: " + conceptName + ", attribute: " + attributeName);
					}
				}
			}
		}
	}

	/**
	 * Parse all link relation between concepts.
	 * 
	 * @param owlOntology
	 * @throws OWLException
	 */
	private void parseLinks(OWLOntology owlOntology) throws OWLException {

		// First detect relation declarations
		for (OWLAxiom owlAxiom : owlOntology.getAxioms(AxiomType.DECLARATION)) {

			OWLEntity owlEntity = ((OWLDeclarationAxiom) owlAxiom).getEntity();
			String owlName = owlEntity.getIRI().getShortForm();

			// Detect relations
			if (owlEntity.isOWLObjectProperty()) {

				if (!linkMap.containsKey(owlName)) {

					ConceptLink link = new ConceptLink();
					link.setQualifier(owlName);

					linkMap.put(owlName, link);
					Console.writeDebug(this, "link name: " + owlName);
				}
			}
		}

		// Then detect source & target link
		for (OWLAxiom owlAxiom : owlOntology.getAxioms()) {

			// Detect link source
			if (owlAxiom.getAxiomType() == AxiomType.OBJECT_PROPERTY_DOMAIN) {

				OWLEntity owlLinkEntity = null;
				OWLEntity owlOwnerEntity = null;
				OWLObjectPropertyDomainAxiom owlDomain = (OWLObjectPropertyDomainAxiom) owlAxiom;
				
				if (owlDomain.getProperty().getSignature().size() > 0) {
					owlLinkEntity = owlDomain.getProperty().getSignature().iterator().next();
				}
				
				if (owlDomain.getDomain().getSignature().size() > 0) {
					owlOwnerEntity = owlDomain.getDomain().getSignature().iterator().next();
				}

				if (owlLinkEntity != null && owlOwnerEntity != null) {
					
					String linkIdentifier = owlLinkEntity.getIRI().getShortForm();
					String sourceConceptName = owlOwnerEntity.getIRI().getShortForm();

					if (linkMap.containsKey(linkIdentifier) && conceptMap.containsKey(sourceConceptName)) {
						linkMap.get(linkIdentifier).setSourceConcept(conceptMap.get(sourceConceptName));
						conceptMap.get(sourceConceptName).getLinks().add(linkMap.get(linkIdentifier));
						Console.writeDebug(this, "link: " + linkIdentifier + ", source concept: " + sourceConceptName);
					}
				}
			}

			// Detect link target
			else if (owlAxiom.getAxiomType() == AxiomType.OBJECT_PROPERTY_RANGE) {

				OWLEntity owlLinkEntity = null;
				OWLEntity owlOwnerEntity = null;
				OWLObjectPropertyRangeAxiom owlRange = (OWLObjectPropertyRangeAxiom) owlAxiom;
				
				if (owlRange.getProperty().getSignature().size() > 0) {
					owlLinkEntity = owlRange.getProperty().getSignature().iterator().next();
				}
				
				if (owlRange.getRange().getSignature().size() > 0) {
					owlOwnerEntity = owlRange.getRange().getSignature().iterator().next();
				}
				
				if (owlLinkEntity != null && owlOwnerEntity != null) {
					
					String linkIdentifier = owlLinkEntity.getIRI().getShortForm();
					String targetConceptName = owlOwnerEntity.getIRI().getShortForm();

					if (linkMap.containsKey(linkIdentifier) && conceptMap.containsKey(targetConceptName)) {
						linkMap.get(linkIdentifier).setTargetConcept(conceptMap.get(targetConceptName));
						Console.writeDebug(this, "link: " + linkIdentifier + ", target concept: " + targetConceptName);
					}
				}
			}
		}
	}

	/**
	 * Parse all labels.
	 * 
	 * @param owlOntology
	 * @throws OWLException
	 */
	private void parseAnnotations(OWLOntology owlOntology) throws OWLException {

		// Detect all annotations
		for (OWLAxiom owlAxiom : owlOntology.getAxioms()) {

			// Detect link source
			if (owlAxiom.getAxiomType() == AxiomType.ANNOTATION_ASSERTION) {

				OWLAnnotationAssertionAxiom owlAnnotationAssertionAxiom = (OWLAnnotationAssertionAxiom) owlAxiom;
				OWLAnnotationSubject subject   = owlAnnotationAssertionAxiom.getSubject();
				OWLAnnotationProperty property = owlAnnotationAssertionAxiom.getProperty();
				OWLAnnotationValue value       = owlAnnotationAssertionAxiom.getValue();

				if (subject instanceof IRI && value instanceof OWLLiteral && property.getIRI().getShortForm().equals("label")) {

					OWLLiteral owlLiteral = (OWLLiteral) value;
					String owlName = ((IRI) subject).getShortForm();

					// Detect class name
					if (conceptMap.containsKey(owlName)) {
						conceptMap.get(owlName).setName(owlLiteral.getLiteral());
					}

					// Detect concept class name + identifier
					else if (conceptClassMap.containsKey(owlName)) {

						// Detect identifier
						if (owlLiteral.getLang().equals("ie")) {
							conceptClassMap.get(owlName).setIdentifier(owlLiteral.getLiteral());
						}

						// Detect name
						else {
							conceptClassMap.get(owlName).setName(owlLiteral.getLiteral());
						}
					}

					// Detect attribute name + identifier
					else if (attributeMap.containsKey(owlName)) {

						// Detect identifier
						if (owlLiteral.getLang().equals("ie")) {
							attributeMap.get(owlName).setIdentifier(owlLiteral.getLiteral());
						}

						// Detect name
						else {
							attributeMap.get(owlName).setName(owlLiteral.getLiteral());
						}
					}

					// Detect relation qualifier
					else if (linkMap.containsKey(owlName)) {
						linkMap.get(owlName).setQualifier(owlLiteral.getLiteral());
					}
				}
			}
		}
	}

	/**
	 * Finalize parsing by filtering bounds & location attributes
	 * and removing invalid links.
	 */
	private void parseFiltering() {

		for (Concept concept : conceptMap.values()) {

			// Filter bounds & location attributes
			List<ConceptAttribute> attributes = new ArrayList<>();

			for (ConceptAttribute attribute : concept.getAttributes()) {

				if (attribute.getName().equals("@Bounds")) {

					String[] boundsItems = attribute.getIdentifier().split(",");
					Rectangle bounds = new Rectangle();
					bounds.x = (int) Integer.valueOf(boundsItems[0]);
					bounds.y = (int) Integer.valueOf(boundsItems[1]);
					bounds.width = (int) Integer.valueOf(boundsItems[2]);
					bounds.height = (int) Integer.valueOf(boundsItems[3]);

					concept.setBounds(bounds);
				}
				else if (attribute.getName().equals("@Action")) {

					boolean isAction = Boolean.valueOf(attribute.getIdentifier());
					concept.setAction(isAction);
				}
				else {
					attributes.add(attribute);
				}
			}

			// Update concept attributes
			concept.getAttributes().clear();
			concept.getAttributes().addAll(attributes);

			// Filter links without target
			List<ConceptLink> links = new ArrayList<>();

			for (ConceptLink link : concept.getLinks()) {
				if (link.getTargetConcept() != null) {
					links.add(link);
				}
			}

			// Update concept links
			concept.getLinks().clear();
			concept.getLinks().addAll(links);
		}
	}

	/**
	 * Dump concepts found.
	 * 
	 * @param concepts
	 */
	private void dumpConcept(Concept concept) {

		Console.writeDebug(this, "concept: " + concept.getName());

		Console.writeDebug(this, "   bounds: x=" + concept.getBounds().x + ",y=" + concept.getBounds().y + ",width=" + concept.getBounds().width + ",height=" + concept.getBounds().height);

		Console.writeDebug(this, "   action: " + concept.isAction());

		for (ConceptAttribute attribute : concept.getAttributes()) {
			Console.writeDebug(this, "   attribute: " + attribute.getName() + ", identifier: " + attribute.getIdentifier());
		}

		for (ConceptClass clazz : concept.getClasses()) {
			Console.writeDebug(this, "   class: " + clazz.getName() + ", identifier: " + clazz.getIdentifier());
		}

		for (ConceptLink link : concept.getLinks()) {
			Console.writeDebug(this, "   link: " + link.getQualifier() + ", target: " + link.getTargetConcept().getName());
		}
	}
}
