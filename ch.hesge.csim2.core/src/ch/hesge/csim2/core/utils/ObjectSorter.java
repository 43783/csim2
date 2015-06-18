package ch.hesge.csim2.core.utils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.ConceptAttribute;
import ch.hesge.csim2.core.model.ConceptClass;
import ch.hesge.csim2.core.model.MethodConceptMatch;
import ch.hesge.csim2.core.model.ScenarioStep;
import ch.hesge.csim2.core.model.SourceAttribute;
import ch.hesge.csim2.core.model.SourceClass;
import ch.hesge.csim2.core.model.SourceMethod;
import ch.hesge.csim2.core.model.SourceParameter;
import ch.hesge.csim2.core.model.SourceReference;
import ch.hesge.csim2.core.model.SourceVariable;
import ch.hesge.csim2.core.model.StemConcept;
import ch.hesge.csim2.core.model.StemMethod;

/**
 * This class is a generic utility class to sort object in domain.
 * 
 * Copyright HEG Geneva 2015, Switzerland
 * 
 * @author Eric Harth
 */

public class ObjectSorter {

	/**
	 * Sort a list of step within a scenario.
	 * 
	 * @param scenario steps
	 */
	public static void sortScenarioSteps(List<ScenarioStep> steps) {

		steps.sort(new Comparator<ScenarioStep>() {
			@Override
			public int compare(ScenarioStep a, ScenarioStep b) {
				return a.getName().compareTo(b.getName());
			}
		});
	}

	/**
	 * Sort a list of concepts.
	 * 
	 * @param concepts
	 */
	public static void sortConcepts(List<Concept> concepts) {

		concepts.sort(new Comparator<Concept>() {
			@Override
			public int compare(Concept a, Concept b) {
				return a.getName().compareTo(b.getName());
			}
		});
	}

	/**
	 * Sort a list of concept attributes.
	 * 
	 * @param attributes
	 */
	public static void sortConceptAttributes(List<ConceptAttribute> attributes) {

		attributes.sort(new Comparator<ConceptAttribute>() {
			@Override
			public int compare(ConceptAttribute a, ConceptAttribute b) {
				return a.getName().compareTo(b.getName());
			}
		});
	}

	/**
	 * Sort a list of concept classes.
	 * 
	 * @param classes
	 */
	public static void sortConceptClasses(List<ConceptClass> classes) {

		classes.sort(new Comparator<ConceptClass>() {
			@Override
			public int compare(ConceptClass a, ConceptClass b) {
				return a.getName().compareTo(b.getName());
			}
		});
	}

	/**
	 * Sort a list of source classes.
	 * 
	 * @param classes
	 */
	public static void sortSourceClasses(List<SourceClass> classes) {

		classes.sort(new Comparator<SourceClass>() {
			@Override
			public int compare(SourceClass a, SourceClass b) {
				return a.getName().compareTo(b.getName());
			}
		});
	}

	/**
	 * Sort a list of source attributes.
	 * 
	 * @param attributes
	 */
	public static void sortSourceAttributes(List<SourceAttribute> attributes) {

		attributes.sort(new Comparator<SourceAttribute>() {
			@Override
			public int compare(SourceAttribute a, SourceAttribute b) {
				return a.getName().compareTo(b.getName());
			}
		});
	}

	/**
	 * Sort a list of source methods.
	 * 
	 * @param methods
	 */
	public static void sortSourceMethods(List<SourceMethod> methods) {

		methods.sort(new Comparator<SourceMethod>() {
			@Override
			public int compare(SourceMethod a, SourceMethod b) {
				return a.getName().compareTo(b.getName());
			}
		});
	}

	/**
	 * Sort a list of source methods.
	 * 
	 * @param methods
	 */
	public static void sortSourceMethods(List<SourceMethod> methods, Map<Integer, SourceClass> classMap) {

		methods.sort(new Comparator<SourceMethod>() {
			@Override
			public int compare(SourceMethod a, SourceMethod b) {
				
				if (a.getClassId() == b.getClassId()) {
					return a.getName().compareTo(b.getName());
				}
				
				SourceClass aClass = classMap.get(a.getClassId());
				SourceClass bClass = classMap.get(b.getClassId());
				
				return aClass.getName().compareTo(bClass.getName());
			}
		});
	}

	/**
	 * Sort a list of source parameters.
	 * 
	 * @param parameters
	 */
	public static void sortSourceParameters(List<SourceParameter> parameters) {

		parameters.sort(new Comparator<SourceParameter>() {
			@Override
			public int compare(SourceParameter a, SourceParameter b) {
				return a.getName().compareTo(b.getName());
			}
		});
	}

	/**
	 * Sort a list of source variables.
	 * 
	 * @param variables
	 */
	public static void sortSourceVariables(List<SourceVariable> variables) {

		variables.sort(new Comparator<SourceVariable>() {
			@Override
			public int compare(SourceVariable a, SourceVariable b) {
				return a.getName().compareTo(b.getName());
			}
		});
	}

	/**
	 * Sort a list of source references.
	 * 
	 * @param references
	 */
	public static void sortSourceReferences(List<SourceReference> references) {

		references.sort(new Comparator<SourceReference>() {
			@Override
			public int compare(SourceReference a, SourceReference b) {
				return a.getName().compareTo(b.getName());
			}
		});
	}

	/**
	 * Sort a list of stem methods.
	 * 
	 * @param stems
	 */
	public static void sortStemMethods(List<StemMethod> stems) {

		stems.sort(new Comparator<StemMethod>() {
			@Override
			public int compare(StemMethod a, StemMethod b) {
				if (a.getStemType().getValue() == b.getStemType().getValue())
					return a.getTerm().compareTo(b.getTerm());
				return a.getStemType().getValue() - b.getStemType().getValue();
			}
		});
	}

	/**
	 * Sort a list of stem concepts.
	 * 
	 * @param stems
	 */
	public static void sortStemConcepts(List<StemConcept> stems) {

		stems.sort(new Comparator<StemConcept>() {
			@Override
			public int compare(StemConcept a, StemConcept b) {
				if (a.getStemType().getValue() == b.getStemType().getValue())
					return a.getTerm().compareTo(b.getTerm());
				return a.getStemType().getValue() - b.getStemType().getValue();
			}
		});
	}

	/**
	 * Sort a list of mathings.
	 * 
	 * @param matchings
	 */
	public static void sortMatchingByWeight(List<MethodConceptMatch> matchings) {

		matchings.sort(new Comparator<MethodConceptMatch>() {
			@Override
			public int compare(MethodConceptMatch a, MethodConceptMatch b) {
				return (int) ((b.getWeight() - a.getWeight()) * 100);
			}
		});
	}
}
