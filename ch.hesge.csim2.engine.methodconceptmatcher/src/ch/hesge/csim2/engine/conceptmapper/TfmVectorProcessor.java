package ch.hesge.csim2.engine.conceptmapper;

import java.util.ArrayList;
import java.util.HashMap;

class TfmVectorProcessor extends TfVectorProcessor {

	public TfmVectorProcessor(ArrayList orderedTerms, HashMap methodStemMap) {
		super(orderedTerms, methodStemMap);
	}

	//in  methods, whatever the number of occurrences of a term, we record only 1 occurrence.
	protected void processElement(TfIdfVector tfv, int index) {
		tfv.put(index, 1);
	}
}
