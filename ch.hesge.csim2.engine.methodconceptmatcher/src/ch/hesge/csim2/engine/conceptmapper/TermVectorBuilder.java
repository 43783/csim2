package ch.hesge.csim2.engine.conceptmapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import ch.hesge.csim2.core.model.Ontology;
import ch.hesge.csim2.core.model.Project;

public class TermVectorBuilder {

	private HashMap<String, ArrayList<StemOccurrence>> methodStemMap;
	private HashMap<String, ArrayList<StemOccurrence>> conceptStemMap;
	private HashMap<Integer, ConceptIdentifier> conceptIdentifiersMap;
	private HashMap<Integer, MethodIdentifier> methodIdentifiersMap;
	//hashmap whose key is the string identifier of a method and value 
	//is the collection of method concept match (mcm) for the method given as key
	//Hence all the mcm in a given list reference the same methodId
	private HashMap<String, ArrayList<MethodConceptMatch2>> mcmMap = new HashMap<String, ArrayList<MethodConceptMatch2>>();;

	public TermVectorBuilder() {
		//computeVectors();
	}

	public HashMap<String, ArrayList<MethodConceptMatch2>> getMethodConceptMatchMap() {
		return mcmMap;
	}

	private void loadMaps(Project project, Ontology ontology) {
		conceptIdentifiersMap = new DBStemInConceptsLoader().getAllConceptIdentifiers(ontology);
		methodIdentifiersMap = new DBStemInMethodsLoader().getAllMethodIdentifiers(project);
		//hashmap whose key is a stem and whose value is a list of stemoccurrences (stem, code, methodidentifier)	
		methodStemMap = new DBStemInMethodsLoader().getStemLocations(project, methodIdentifiersMap);
		//hashmap whose key is a stem and whose value is a list of stemoccurrences (stem, code, conceptidentifier)	
		conceptStemMap = new DBStemInConceptsLoader().getStemLocations(project, conceptIdentifiersMap);

	}

	//concept should be sorted
	public ArrayList<ConceptIdentifier> getConceptIdentifiers() {
		ArrayList<ConceptIdentifier> result = new ArrayList<ConceptIdentifier>();
		TreeSet<ConceptIdentifier> ciSet = new TreeSet<ConceptIdentifier>();
		for (ArrayList<MethodConceptMatch2> mcmList : mcmMap.values()) {
			for (MethodConceptMatch2 mcm : mcmList) {
				ciSet.add(mcm.getConceptId());
			}
		}
		result.addAll(ciSet);
		return result;
	}

	//returns the ordered collection of common terms among concepts and methods: T
	private ArrayList<String> computeCommonTermSet() {
		Set<String> commonTerms = methodStemMap.keySet();
		commonTerms.retainAll(conceptStemMap.keySet());
		ArrayList<String> orderedCommonTerms = new ArrayList<String>();
		orderedCommonTerms.addAll(commonTerms);
		//	for(String term : orderedCommonTerms)System.out.println(term);
		return orderedCommonTerms;
	}

	//main method: computes the idf ,tfc(ci) and tfm(mi) vectors for all ci and mi 
	private void computeVectors(Project project, Ontology ontology) {
		loadMaps(project, ontology);
		int conceptSetSize = conceptIdentifiersMap.size();
		//computes the T set : the intersection of the two sets of terms
		//its elements order give the order of the vectors elements
		ArrayList<String> orderedTerms = computeCommonTermSet();

		//compute the idf vector for concepts that is used for both concepts and methods
		TfIdfVector idf = new IdfProcessor(orderedTerms, conceptStemMap, conceptSetSize).computeIdf();

		//compute the tf vector for each concept. Returns a map whose Key is the concept ID and value is the vector
		HashMap<StemLocationElement, TfIdfVector> tfcVectorMap = new TfcVectorProcessor(orderedTerms, conceptStemMap).compute();

		//compute the tf vector for each method. Returns a map whose Key is the method ID and value is the vector
		HashMap<StemLocationElement, TfIdfVector> tfmVectorMap = new TfmVectorProcessor(orderedTerms, methodStemMap).compute();

		try {
			for (StemLocationElement mId : tfmVectorMap.keySet()) {
				MethodIdentifier methodId = (MethodIdentifier) mId;
				ArrayList<MethodConceptMatch2> mcmList = new ArrayList<MethodConceptMatch2>();
				mcmMap.put(methodId.getStringIdentifier(), mcmList);
				TfIdfVector tfm = tfmVectorMap.get(methodId);
				TfIdfVector tfIdfMethodVector = tfm.hardamardProduct(idf);

				for (StemLocationElement conceptId : tfcVectorMap.keySet()) {
					TfIdfVector tfc = tfcVectorMap.get(conceptId);
					TfIdfVector tfIdfConceptVector = tfc.hardamardProduct(idf);
					float scalarProduct = tfIdfConceptVector.dotProduct(tfIdfMethodVector);
					// result should be between 0 and 1.
					float result = scalarProduct / (tfIdfMethodVector.length() * tfIdfConceptVector.length());
					if (result > 0)
						mcmList.add(new MethodConceptMatch2((MethodIdentifier) methodId, (ConceptIdentifier) conceptId, result));
				}
			}
		}
		catch (Exception e) {
			System.out.println(e);
		}
	}
}
