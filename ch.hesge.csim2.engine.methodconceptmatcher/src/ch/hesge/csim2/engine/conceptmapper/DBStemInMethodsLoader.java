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
		List<SourceClass> sourceClasses = ApplicationLogic.getSourceClassesWithDependencies(project, false);

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
		Map<Integer, StemMethod> stemMethodTree = ApplicationLogic.getStemMethodTree(project);
		
		// Scan all identifier
		for (MethodIdentifier mi : miMap.values()) {

			// Retrieve all stems associated to the method			
			StemMethod stemMethodRoot = stemMethodTree.get(mi.getMethodID());
			List<StemMethod> stemMethodList = inflateStemTree(stemMethodRoot);
			
			// For each stem, create an new occurrence
			for (StemMethod stem : stemMethodList) {

				if (!soMap.containsKey(stem.getTerm())) {
					soMap.put(stem.getTerm(), new ArrayList<StemOccurrence>());
				}

				StemOccurrence stemOccurence = new StemOccurrence(stem.getTerm(), stem.getStemType().getValue(), mi);
				soMap.get(stem.getTerm()).add(stemOccurence);
			}
		}

		return soMap;
	}
	
	/**
	 * Serialize stem method tree into a single flat list of stem methods.
	 * 
	 * @param stem
	 *        the stem node
	 * 
	 * @return
	 *         a flat list of stem methods
	 */
	private List<StemMethod> inflateStemTree(StemMethod stem) {

		List<StemMethod> flatList = new ArrayList<>();

		if (stem != null) {

			flatList.add(stem);

			for (StemMethod childStem : stem.getChildren()) {
				flatList.addAll(inflateStemTree(childStem));
			}
		}

		return flatList;
	}
	
}
