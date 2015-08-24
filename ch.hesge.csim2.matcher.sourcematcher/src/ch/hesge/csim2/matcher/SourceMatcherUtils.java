/**
 * 
 */
package ch.hesge.csim2.matcher;

import java.util.ArrayList;
import java.util.List;

import ch.hesge.csim2.core.model.Concept;
import ch.hesge.csim2.core.model.StemConcept;
import ch.hesge.csim2.core.model.StemConceptType;
import ch.hesge.csim2.core.model.StemMethod;
import ch.hesge.csim2.core.model.StemMethodType;

/**
 * This utility class allow several concept,method matching rules.
 * 
 * Copyright HEG Geneva 2015, Switzerland
 * 
 * @author Eric Harth
 *
 */
public class SourceMatcherUtils {

	/**
	 * 
	 * @param rootStemConcept
	 * @param stemMethodList
	 * @param matchingStemConcepts
	 * @param matchingStemMethods
	 * @return
	 */
	public static boolean computeFullConceptNameMatching(StemConcept rootStemConcept, List<StemMethod> stemMethodList, List<StemConcept> matchingStemConcepts, List<StemMethod> matchingStemMethods) {

		boolean isMatching = false;
		String fullConceptName = rootStemConcept.getTerm();
		
		for (StemMethod stem : stemMethodList) {
			
			if (stem.getTerm().equals(fullConceptName) 
				&& (stem.getStemType() == StemMethodType.METHOD_NAME_FULL 
					|| stem.getStemType() == StemMethodType.PARAMETER_NAME_FULL 
					|| stem.getStemType() == StemMethodType.REFERENCE_NAME_FULL 
					|| stem.getStemType() == StemMethodType.PARAMETER_TYPE_FULL 
					|| stem.getStemType() == StemMethodType.REFERENCE_TYPE_FULL)) {
				
				isMatching = true;
				matchingStemConcepts.add(rootStemConcept);
				matchingStemMethods.add(stem);
			}
		}
		
		return isMatching;
	}
	
	/**
	 * 
	 * @param stemConceptList
	 * @param stemMethodList
	 * @param matchingStemConcepts
	 * @param matchingStemMethods
	 * @return
	 */
	public static boolean computeFullClassNameMatching(List<StemConcept> stemConceptList, List<StemMethod> stemMethodList, List<StemConcept> matchingStemConcepts, List<StemMethod> matchingStemMethods) {

		boolean isMatching = false;
		
		for (StemConcept conceptStem : stemConceptList) {
			
			if (conceptStem.getStemType() == StemConceptType.CLASS_NAME_FULL) {
				
				String fullClassName = conceptStem.getTerm();
				
				for (StemMethod stem : stemMethodList) {
					
					if (stem.getTerm().equals(fullClassName) 
							&& (stem.getStemType() == StemMethodType.METHOD_NAME_FULL 
								|| stem.getStemType() == StemMethodType.PARAMETER_NAME_FULL 
								|| stem.getStemType() == StemMethodType.REFERENCE_NAME_FULL 
								|| stem.getStemType() == StemMethodType.PARAMETER_TYPE_FULL 
								|| stem.getStemType() == StemMethodType.REFERENCE_TYPE_FULL)) {
						
						isMatching = true;
						matchingStemConcepts.add(conceptStem);
						matchingStemMethods.add(stem);
					}
				}
			}
		}
		
		return isMatching;
	}

	/**
	 * 
	 * @param stemConceptList
	 * @param stemMethodList
	 * @param matchingStemConcepts
	 * @param matchingStemMethods
	 * @return
	 */
	public static boolean computeFullClassIdentifierMatching(List<StemConcept> stemConceptList, List<StemMethod> stemMethodList, List<StemConcept> matchingStemConcepts, List<StemMethod> matchingStemMethods) {

		boolean isMatching = false;
		
		for (StemConcept conceptStem : stemConceptList) {
			
			if (conceptStem.getStemType() == StemConceptType.CLASS_IDENTIFIER_FULL) {
				
				String fullClassIdentifier = conceptStem.getTerm();
				
				for (StemMethod stem : stemMethodList) {
					
					if (stem.getTerm().equals(fullClassIdentifier) 
						&& (stem.getStemType() == StemMethodType.METHOD_NAME_FULL 
							|| stem.getStemType() == StemMethodType.PARAMETER_NAME_FULL 
							|| stem.getStemType() == StemMethodType.REFERENCE_NAME_FULL 
							|| stem.getStemType() == StemMethodType.PARAMETER_TYPE_FULL 
							|| stem.getStemType() == StemMethodType.REFERENCE_TYPE_FULL)) {
						
						isMatching = true;
						matchingStemConcepts.add(conceptStem);
						matchingStemMethods.add(stem);
					}
				}
			}
		}
		
		return isMatching;
	}

	/**
	 * 
	 * @param stemConceptList
	 * @param stemMethodList
	 * @param matchingStemConcepts
	 * @param matchingStemMethods
	 * @return
	 */
	public static boolean computePartialClassIdentifierMatching(List<StemConcept> stemConceptList, List<StemMethod> stemMethodList, List<StemConcept> matchingStemConcepts, List<StemMethod> matchingStemMethods) {

		boolean isMatching = false;
		
		for (StemConcept conceptStem : stemConceptList) {
			
			if (conceptStem.getStemType() == StemConceptType.CLASS_IDENTIFIER_FULL) {
				
				String fullClassIdentifier = conceptStem.getTerm();

				for (StemMethod stem : stemMethodList) {
					
					if (stem.getTerm().equals(fullClassIdentifier) 
						&& (stem.getStemType() == StemMethodType.METHOD_NAME_PART 
							|| stem.getStemType() == StemMethodType.PARAMETER_NAME_PART 
							|| stem.getStemType() == StemMethodType.REFERENCE_NAME_PART 
							|| stem.getStemType() == StemMethodType.PARAMETER_TYPE_PART 
							|| stem.getStemType() == StemMethodType.REFERENCE_TYPE_PART)) {
						
						isMatching = true;
						matchingStemConcepts.add(conceptStem);
						matchingStemMethods.add(stem);
					}
				}
			}
		}
		
		return isMatching;
	}

	/**
	 * 
	 * @param rootStemAttribute
	 * @param stemMethodList
	 * @param matchingStemConcepts
	 * @param matchingStemMethods
	 * @return
	 */
	public static boolean computeFullAttributeNameMatching(StemConcept rootStemAttribute, List<StemMethod> stemMethodList, List<StemConcept> matchingStemConcepts, List<StemMethod> matchingStemMethods) {
		
		boolean isMatching = false;		
		String fullAttributeName = rootStemAttribute.getTerm();
		
		for (StemMethod stem : stemMethodList) {
			
			if (stem.getTerm().equals(fullAttributeName) 
				&& (stem.getStemType() == StemMethodType.METHOD_NAME_FULL 
					|| stem.getStemType() == StemMethodType.PARAMETER_NAME_FULL 
					|| stem.getStemType() == StemMethodType.REFERENCE_NAME_FULL 
					|| stem.getStemType() == StemMethodType.PARAMETER_TYPE_FULL 
					|| stem.getStemType() == StemMethodType.REFERENCE_TYPE_FULL)) {
				
				isMatching = true;
				matchingStemConcepts.add(rootStemAttribute);
				matchingStemMethods.add(stem);
			}
		}
		
		return isMatching;
	}

	/**
	 * 
	 * @param stemAttributeList
	 * @param stemMethodList
	 * @param matchingStemConcepts
	 * @param matchingStemMethods
	 * @return
	 */
	public static boolean computeFullAttributeIdentifierMatching(List<StemConcept> stemAttributeList, List<StemMethod> stemMethodList, List<StemConcept> matchingStemConcepts, List<StemMethod> matchingStemMethods) {

		boolean isMatching = false;
		
		for (StemConcept attributetStem : stemAttributeList) {
			
			if (attributetStem.getStemType() == StemConceptType.ATTRIBUTE_IDENTIFIER_FULL) {
				
				String fullAttributeIdentifier = attributetStem.getTerm();
				
				for (StemMethod stem : stemMethodList) {
					
					if (stem.getTerm().equals(fullAttributeIdentifier) 
						&& (stem.getStemType() == StemMethodType.METHOD_NAME_FULL 
							|| stem.getStemType() == StemMethodType.PARAMETER_NAME_FULL 
							|| stem.getStemType() == StemMethodType.REFERENCE_NAME_FULL 
							|| stem.getStemType() == StemMethodType.PARAMETER_TYPE_FULL 
							|| stem.getStemType() == StemMethodType.REFERENCE_TYPE_FULL)) {
						
						isMatching = true;
						matchingStemConcepts.add(attributetStem);
						matchingStemMethods.add(stem);
					}
				}
			}
		}
		
		return isMatching;
	}

	/**
	 * 
	 * @param stemAttributeList
	 * @param stemMethodList
	 * @param matchingStemConcepts
	 * @param matchingStemMethods
	 * @return
	 */
	public static boolean computePartialAttributeIdentifierMatching(List<StemConcept> stemAttributeList, List<StemMethod> stemMethodList, List<StemConcept> matchingStemConcepts, List<StemMethod> matchingStemMethods) {

		boolean isMatching = false;
		
		for (StemConcept attributetStem : stemAttributeList) {
			
			if (attributetStem.getStemType() == StemConceptType.ATTRIBUTE_IDENTIFIER_FULL) {
				
				String fullAttributeIdentifier = attributetStem.getTerm();

				for (StemMethod stem : stemMethodList) {
					
					if (stem.getTerm().equals(fullAttributeIdentifier) 
						&& (stem.getStemType() == StemMethodType.METHOD_NAME_PART 
							|| stem.getStemType() == StemMethodType.PARAMETER_NAME_PART 
							|| stem.getStemType() == StemMethodType.REFERENCE_NAME_PART 
							|| stem.getStemType() == StemMethodType.PARAMETER_TYPE_PART 
							|| stem.getStemType() == StemMethodType.REFERENCE_TYPE_PART)) {
						
						isMatching = true;
						matchingStemConcepts.add(attributetStem);
						matchingStemMethods.add(stem);
					}
				}
			}
		}
		
		return isMatching;
	}

	/**
	 * 
	 * @param stemAttributeNamePart
	 * @param stemMethodList
	 * @param matchingStemConcepts
	 * @param matchingStemMethods
	 * @return
	 */
	public static boolean computePartialAttributeWordMatching(StemConcept stemAttributeNamePart, List<StemMethod> stemMethodList, List<StemConcept> matchingStemConcepts, List<StemMethod> matchingStemMethods) {

		boolean isMatching = false;		
		String attributeNamePart = stemAttributeNamePart.getTerm();
		
		for (StemMethod stem : stemMethodList) {
			
			if (stem.getTerm().equals(attributeNamePart) 
				&& (stem.getStemType() == StemMethodType.METHOD_NAME_PART 
					|| stem.getStemType() == StemMethodType.PARAMETER_NAME_PART 
					|| stem.getStemType() == StemMethodType.REFERENCE_NAME_PART 
					|| stem.getStemType() == StemMethodType.PARAMETER_TYPE_PART 
					|| stem.getStemType() == StemMethodType.REFERENCE_TYPE_PART)) {
				
				isMatching = true;
				matchingStemConcepts.add(stemAttributeNamePart);
				matchingStemMethods.add(stem);
			}
		}
		
		return isMatching;
	}

	
	/**
	 * 
	 * @param conceptAttribute
	 * @param stemConceptList
	 * @return
	 */
	public static List<StemConcept> getStemConceptAttributes(Concept concept, List<StemConcept> stemConceptList) {
		
		List<StemConcept> stemAttributes = new ArrayList<>();
		
		for (StemConcept stem : stemConceptList) {
			
			if (stem.getConceptId() == concept.getKeyId() && stem.getStemType() == StemConceptType.ATTRIBUTE_NAME_FULL) {
				stemAttributes.add(stem);
			}
		}
		
		return stemAttributes;
	}
	
	/**
	 * Serialize a stem concept tree into a single flat list of stem children.
	 * 
	 * @param rootStem
	 *        the root stem of a stem tree
	 * 
	 * @return
	 *         a flat list of stem concepts
	 */
	public static List<StemConcept> inflateStemConcepts(StemConcept rootStem) {

		List<StemConcept> flatList = new ArrayList<>();

		if (rootStem != null) {

			flatList.add(rootStem);
			flatList.addAll(rootStem.getParts());

			for (StemConcept attrStem : rootStem.getAttributes()) {

				flatList.add(attrStem);
				flatList.addAll(attrStem.getParts());

				for (StemConcept identifierStem : attrStem.getAttributeIdentifiers()) {
					flatList.add(identifierStem);
					flatList.addAll(identifierStem.getParts());
				}
			}

			for (StemConcept classStem : rootStem.getClasses()) {

				flatList.add(classStem);
				flatList.addAll(classStem.getParts());

				for (StemConcept identifierStem : classStem.getClassIdentifiers()) {
					flatList.add(identifierStem);
					flatList.addAll(identifierStem.getParts());
				}
			}
		}

		return flatList;
	}

	/**
	 * Serialize a stem method tree into a single flat list of stem children.
	 * 
	 * @param rootStem
	 *        the root stem of a stem tree
	 * 
	 * @return
	 *         a flat list of stem methods
	 */
	public static List<StemMethod> inflateStemMethods(StemMethod rootStem) {

		List<StemMethod> flatList = new ArrayList<>();

		if (rootStem != null) {

			flatList.add(rootStem);
			flatList.addAll(rootStem.getParts());

			for (StemMethod paramStem : rootStem.getParameters()) {
				flatList.add(paramStem);
				flatList.addAll(paramStem.getParts());

				for (StemMethod paramTypeStem : paramStem.getParameterTypes()) {
					flatList.add(paramTypeStem);
					flatList.addAll(paramTypeStem.getParts());
				}
			}

			for (StemMethod refStem : rootStem.getReferences()) {
				flatList.add(refStem);
				flatList.addAll(refStem.getParts());

				for (StemMethod refTypeStem : refStem.getReferenceTypes()) {
					flatList.add(refTypeStem);
					flatList.addAll(refTypeStem.getParts());
				}
			}
		}

		return flatList;
	}
}
