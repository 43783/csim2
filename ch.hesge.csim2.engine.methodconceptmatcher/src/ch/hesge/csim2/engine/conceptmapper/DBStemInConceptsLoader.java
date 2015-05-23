package ch.hesge.csim2.engine.conceptmapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.hesge.csim2.core.logic.ApplicationLogic;
import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.ConceptLink;
import ch.hesge.csim2.core.model.Ontology;
import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.StemConcept;

class DBStemInConceptsLoader {

	// the key = concept ID and value = ConceptIdentifier
	// provided that the concept ID are unique, the Concept Identifiers are unique too
	//in other words, there is one ConceptIdentifier for each concept in the DB
	public HashMap<Integer, ConceptIdentifier> getAllConceptIdentifiers(Ontology ontology) {

		HashMap<Integer, ConceptIdentifier> ciMap = new HashMap<Integer, ConceptIdentifier>();

		// Load all concepts in ontology
		List<Concept> concepts = ApplicationLogic.getConcepts(ontology);

		// Create one concept-identifier by concept
		for (Concept concept : concepts) {

			// Create a concept identifier from concept
			ConceptIdentifier ci = new ConceptIdentifier(concept.getKeyId(), concept.getName());

			// Populate map
			ciMap.put(ci.getConceptID(), ci);
		}

		// Update father and children for each concept
		for (Concept concept : concepts) {
			for (ConceptLink link : concept.getLinks()) {

				if (link.getQualifier().equals("subclass-of")) {

					// Retrieve source/target identifiers
					ConceptIdentifier sourceIdentifier = ciMap.get(link.getSourceId());
					ConceptIdentifier targetIdentifier = ciMap.get(link.getTargetId());

					// Update each one
					sourceIdentifier.setFatherConcept(targetIdentifier);
					targetIdentifier.addDirectChildConcept(sourceIdentifier);
				}
			}
		}

		return ciMap;
	}

	//returns a hashmap whose key is a stem and whose value is a list of stemoccurrences (stem, code, conceptidentifier)	
	public HashMap<String, ArrayList<StemOccurrence>> getStemLocations(Project project, Map<Integer, ConceptIdentifier> ciMap) {
		//invariant: any stem referenced as key is associated at least with 1 source element
		//all the concept identifiers referenced in the StemOccurrences are unique for each concept
		//in other words if a set of stem belong to the same concept they reference the same ConceptIdentifier

		HashMap<String, ArrayList<StemOccurrence>> soMap = new HashMap<>();
		Map<String, List<StemConcept>> stemConceptMap = ApplicationLogic.getStemConceptByTermMap(project);
		
		// Scan all identifier
		for (ConceptIdentifier ci : ciMap.values()) {

			// Retrieve all stem associated to the concept
			List<StemConcept> conceptStems = stemConceptMap.get(ci.getConceptName());
			
			// For each stem, create an new occurrence
			for (StemConcept stem : conceptStems) {

				if (!soMap.containsKey(stem.getTerm())) {
					soMap.put(stem.getTerm(), new ArrayList<StemOccurrence>());
				}

				StemOccurrence stemOccurence = new StemOccurrence(stem.getTerm(), stem.getStemType().getValue(), ci);
				soMap.get(stem).add(stemOccurence);
			}
		}

		return soMap;
	}
}
