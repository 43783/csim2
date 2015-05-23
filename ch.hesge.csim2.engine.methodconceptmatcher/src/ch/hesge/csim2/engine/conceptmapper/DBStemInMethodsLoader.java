package ch.hesge.csim2.engine.conceptmapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.hesge.csim2.core.logic.ApplicationLogic;
import ch.hesge.csim2.core.model.Project;
import ch.hesge.csim2.core.model.SourceClass;
import ch.hesge.csim2.core.model.SourceMethod;
import ch.hesge.csim2.core.model.StemMethod;

class DBStemInMethodsLoader {

	// In the HashMap returned, the key = method keyID and value = MethodIdentifier
	// provided that the method ID are unique, the Method Identifiers are unique too
	//in other words, there is one MethodIdentifier for each pair <class,method> in the DB
	public HashMap<Integer, MethodIdentifier> getAllMethodIdentifiers(Project project) {

		HashMap<Integer, MethodIdentifier> miMap = new HashMap<Integer, MethodIdentifier>();

		// Load all sources in project
		List<SourceClass> sourceClasses = ApplicationLogic.getSourceClassesWithDependencies(project, true);

		for (SourceClass sourceClass : sourceClasses) {
			for (SourceMethod sourceMethod : sourceClass.getMethods()) {

				// Create a method identifier from method
				MethodIdentifier mi = new MethodIdentifier(sourceMethod.getKeyId(), "", sourceClass.getName(), sourceMethod.getSignature());

				// Populate map
				miMap.put(mi.getMethodID(), mi);
			}
		}

		return miMap;
	}

	//returns a hashmap whose key is a stem and whose value is a list of stemoccurrences (stem, code, methodidentifier)	
	HashMap<String, ArrayList<StemOccurrence>> getStemLocations(Project project, HashMap<Integer, MethodIdentifier> miMap) {
		//invariant: any stem referenced as key is associated at least with 1 source element

		HashMap<String, ArrayList<StemOccurrence>> soMap = new HashMap<>();
		Map<String, List<StemMethod>> stemMethodMap = ApplicationLogic.getStemMethodByTermMap(project);

		// Scan all identifier
		for (MethodIdentifier mi : miMap.values()) {

			// Create a dummy method
			SourceMethod method = new SourceMethod();
			method.setKeyId(mi.getMethodID());

			// Retrieve all stem associated to the concept
			List<StemMethod> methodStems = stemMethodMap.get(mi.getSignature());

			// For each stem, create an new occurrence
			for (StemMethod stem : methodStems) {

				if (!soMap.containsKey(stem.getTerm())) {
					soMap.put(stem.getTerm(), new ArrayList<StemOccurrence>());
				}

				StemOccurrence stemOccurence = new StemOccurrence(stem.getTerm(), stem.getStemType().getValue(), mi);
				soMap.get(stem).add(stemOccurence);
			}
		}

		return soMap;
	}
}
