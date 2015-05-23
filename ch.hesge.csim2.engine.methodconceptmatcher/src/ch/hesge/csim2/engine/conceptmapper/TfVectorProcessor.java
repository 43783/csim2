package ch.hesge.csim2.engine.conceptmapper;

import java.util.ArrayList;
import java.util.HashMap;

abstract class TfVectorProcessor {

	private ArrayList<String> orderedTerms;
	private HashMap<String, ArrayList<StemOccurrence>> sourceStemMap;

	TfVectorProcessor(ArrayList orderedTerms, HashMap<String, ArrayList<StemOccurrence>> sourceStemMap) {
		this.orderedTerms = orderedTerms; // T
		this.sourceStemMap = sourceStemMap;
	}

	//compute the tf vector for each concept or method. 
	//StemLocationElement is either a methodIdentifier or a conceptIdentifier
	HashMap<StemLocationElement, TfIdfVector> compute() {
		// key is method or concept identifier, value is tf vector
		HashMap<StemLocationElement, TfIdfVector> vectorMap = new HashMap<StemLocationElement, TfIdfVector>();
		int index = 0;
		for (String term : orderedTerms) {
			index = index + 1;
			//soList: all the occurrences of the term among the concepts or methods.
			ArrayList<StemOccurrence> soList = sourceStemMap.get(term);

			for (StemOccurrence so : soList) {
				StemLocationElement document = so.getStemLocation(); //method or concept identifier
				TfIdfVector tf;
				if (vectorMap.containsKey(document))
					tf = vectorMap.get(document);
				else {
					tf = new TfIdfVector(orderedTerms.size(), document);
					vectorMap.put(document, tf);
				}
				//tf is the vector that belongs to the selected term
				processElement(tf, index);
			}
		}
		return vectorMap;
	}

	protected abstract void processElement(TfIdfVector tfv, int index);
}
