package ch.hesge.csim2.engine.conceptmapper;

import java.util.ArrayList;
import java.util.HashMap;

class TfcVectorProcessor extends TfVectorProcessor {

	public TfcVectorProcessor(ArrayList orderedTerms, HashMap conceptStemMap) {
		super(orderedTerms, conceptStemMap);
	}

	//in concepts, each occurrence of a term increments the corresponding vector element
	protected void processElement(TfIdfVector tfv, int index) {
		tfv.increment(index);
	}
}
