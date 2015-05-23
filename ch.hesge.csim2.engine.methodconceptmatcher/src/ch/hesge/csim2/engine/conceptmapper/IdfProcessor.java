package ch.hesge.csim2.engine.conceptmapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class IdfProcessor {

	private ArrayList<String> orderedTerms;
	//hashmap whose key is a stem and whose value is a list of stemoccurrences (stem, code, conceptidentifier)
	private HashMap<String, ArrayList<StemOccurrence>> conceptStemMap;
	private float conceptSetSize;

	IdfProcessor(ArrayList<String> orderedTerms, HashMap<String, ArrayList<StemOccurrence>> conceptStemMap, int conceptSetSize) {

		this.orderedTerms = orderedTerms; // T
		this.conceptSetSize = conceptSetSize; // C
		this.conceptStemMap = conceptStemMap;
	}

	//returns a vector that holds the idf values for all terms.
	TfIdfVector computeIdf() {

		int index = 0;
		TfIdfVector idf = new TfIdfVector(orderedTerms.size());
		try {
			for (String term : orderedTerms) {
				index = index + 1;
				//computing the number of concepts in which the term is found
				ArrayList<StemOccurrence> soList = conceptStemMap.get(term);
				HashSet result = new HashSet(); // to contain all the unique concepts ID numbers
				for (StemOccurrence so : soList)
					if (so.getStemLocation() == null)
						throw new Exception("error computing tfidf vector. Stem location is null");
					else
						result.add(so.getStemLocation().getSourceId());

				int numberOfConceptsInWhichItOccurs = result.size(); // |{ c in C | t in tc(c)}|

				double idf_value = Math.log10(conceptSetSize / (1 + numberOfConceptsInWhichItOccurs));

				idf.put(index, idf_value);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return idf;
	}
}
