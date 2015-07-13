package ch.hesge.csim2.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import ch.hesge.csim2.core.logic.ApplicationLogic;
import ch.hesge.csim2.core.model.SourceAttribute;
import ch.hesge.csim2.core.model.SourceClass;
import ch.hesge.csim2.core.model.SourceMethod;
import ch.hesge.csim2.core.model.SourceParameter;
import ch.hesge.csim2.core.model.SourceReference;
import ch.hesge.csim2.core.model.SourceReferenceOrigin;
import ch.hesge.csim2.core.model.SourceVariable;
import ch.hesge.csim2.core.utils.StringUtils;

/**
 * Utility class related to parsing java source file
 */
public class JavaAnalyzerUtils {

	/**
	 * Create a source class with all its attributes as defined by the
	 * TypeDeclaration passed in argument.
	 * 
	 * @param declaration
	 *        the class declaration
	 * @return a source class
	 */
	public static SourceClass createSourceClass(TypeDeclaration declaration) {

		if (declaration == null)
			return null;

		SourceClass sourceClass = new SourceClass();

		sourceClass.setName(declaration.getName().getIdentifier());
		sourceClass.setType("class");

		String superclass = "Object";

		if (declaration.getSuperclassType() != null) {
			superclass = JavaAnalyzerUtils.getSimpleTypeName(declaration.getSuperclassType().toString());
		}

		sourceClass.setSuperClassName(superclass);

		for (FieldDeclaration field : declaration.getFields()) {

			String attType = field.getType().toString();
			if (attType.contains(".")) {
				attType = attType.substring(attType.lastIndexOf(".") + 1);
			}

			for (Object o : field.fragments()) {

				VariableDeclarationFragment fragment = (VariableDeclarationFragment) o;

				SourceAttribute sourceAttribute = new SourceAttribute();
				sourceAttribute.setName(fragment.getName().getIdentifier());
				sourceAttribute.setType(attType);

				sourceClass.getAttributes().add(sourceAttribute);
			}
		}

		// Retrieve class fields
		return sourceClass;
	}

	/**
	 * Create a source method with all its parameters as defined by the
	 * MethodDeclaration passed in argument.
	 * 
	 * @param declaration
	 *        the method declaration
	 * @return a source class
	 */
	public static SourceMethod createSourceMethod(MethodDeclaration declaration) {

		if (declaration == null)
			return null;

		SourceMethod sourceMethod = new SourceMethod();

		// Retrieve method type (return type)
		String methodType = (declaration.getReturnType2() == null ? null : declaration.getReturnType2().toString());
		sourceMethod.setReturnType(JavaAnalyzerUtils.getSimpleTypeName(methodType));

		// Retrieve all method's parameters
		for (Object p : declaration.parameters()) {

			SingleVariableDeclaration param = (SingleVariableDeclaration) p;

			SourceParameter sourceParameter = new SourceParameter();
			sourceParameter.setName(param.getName().getIdentifier());
			sourceParameter.setType(JavaAnalyzerUtils.getSimpleTypeName(param.getType().toString()));

			if (!sourceMethod.getParameters().contains(sourceParameter)) {
				sourceMethod.getParameters().add(sourceParameter);
			}
		}

		// Retrieve method signature
		String signature = JavaAnalyzerUtils.getMethodSignature(declaration);
		sourceMethod.setSignature(signature);
		sourceMethod.setName(declaration.getName().getIdentifier());

		return sourceMethod;
	}

	/**
	 * Create a reference whose name passed in argument is contained within a
	 * specific method and class.
	 * 
	 * @param sourceClass
	 *        the class owning the reference
	 * @param sourceMethod
	 *        the method owning the reference
	 * @param referenceName
	 *        the name of the reference
	 * @return an instance of SourceReference or null
	 */
	public static SourceReference createSourceReference(SourceClass sourceClass, SourceMethod sourceMethod, String referenceName) {

		SourceReference sourceReference = null;

		if (sourceClass != null && sourceMethod != null) {

			String referenceType = "Unkown";
			SourceReferenceOrigin referenceOrigin = null;

			// Check if reference is a local variable
			if (referenceOrigin == null) {

				for (SourceVariable var : sourceMethod.getVariables()) {
					if (var.getName().equals(referenceName)) {
						referenceOrigin = SourceReferenceOrigin.LOCAL_VARIABLE;
						referenceType = var.getType();
						break;
					}
				}
			}

			// Check if reference is a method parameter
			if (referenceOrigin == null) {

				for (SourceParameter param : sourceMethod.getParameters()) {
					if (param.getName().equals(referenceName)) {
						referenceOrigin = SourceReferenceOrigin.METHOD_PARAMETER;
						referenceType = param.getType();
						break;
					}
				}
			}

			// Check if reference is a class attribute
			if (referenceOrigin == null) {

				for (SourceAttribute attribute : sourceClass.getAttributes()) {
					if (attribute.getName().equals(referenceName)) {
						referenceOrigin = SourceReferenceOrigin.CLASS_FIELD;
						referenceType = attribute.getType();
						break;
					}
				}
			}

			// If no origin found
			if (referenceOrigin == null) {
				referenceOrigin = SourceReferenceOrigin.UNKOWN_ORIGIN;
			}

			// Create the reference
			sourceReference = new SourceReference();
			sourceReference.setName(referenceName);
			sourceReference.setType(referenceType);
			sourceReference.setOrigin(referenceOrigin);
		}

		return sourceReference;
	}

	/**
	 * Create a reference from variable declaration passed in argument
	 * 
	 * @param sourceClass
	 *        the class owning the reference
	 * @param sourceMethod
	 *        the method owning the reference
	 * @param sourceVariable
	 *        the source variable to use as based refernce
	 * @return an instance of SourceReference or null
	 */
	public static SourceReference createSourceReference(SourceClass sourceClass, SourceMethod sourceMethod, SourceVariable sourceVariable) {

		SourceReference sourceReference = null;

		if (sourceClass != null && sourceMethod != null) {

			sourceReference = new SourceReference();
			sourceReference.setName(sourceVariable.getName());
			sourceReference.setType(sourceVariable.getType());
			sourceReference.setOrigin(SourceReferenceOrigin.DECLARATION);
		}
		
		return sourceReference;
	}
		
	/**
	 * Create a variable reference list base on the declaration statement passed
	 * passed in argument
	 * 
	 * @param declaration
	 * @return a list of SourceVariable
	 */
	public static List<SourceVariable> createSourceVariables(VariableDeclarationStatement declaration) {

		List<SourceVariable> sourceVariables = new ArrayList<>();

		// Retrieve variable type
		String varType = JavaAnalyzerUtils.getSimpleTypeName(declaration.getType().toString());

		for (Object o : declaration.fragments()) {

			VariableDeclarationFragment varName = (VariableDeclarationFragment) o;

			SourceVariable sourceVariable = new SourceVariable();
			sourceVariable.setName(varName.getName().getIdentifier());
			sourceVariable.setType(varType);

			sourceVariables.add(sourceVariable);
		}

		return sourceVariables;
	}

	/**
	 * Create a variable reference list base on the declaration statement passed
	 * passed in argument
	 * 
	 * @param expression
	 * @return a list of SourceVariable
	 */
	public static List<SourceVariable> createSourceVariables(VariableDeclarationExpression expression) {

		List<SourceVariable> sourceVariables = new ArrayList<>();

		// Retrieve variable type
		String varType = JavaAnalyzerUtils.getSimpleTypeName(expression.getType().toString());

		for (Object o : expression.fragments()) {

			VariableDeclarationFragment varName = (VariableDeclarationFragment) o;

			SourceVariable sourceVariable = new SourceVariable();
			sourceVariable.setName(varName.getName().getIdentifier());
			sourceVariable.setType(varType);

			sourceVariables.add(sourceVariable);
		}

		return sourceVariables;
	}

	/**
	 * Create a variable reference list base on the declaration statement passed
	 * passed in argument
	 * 
	 * @param declaration
	 * @return a list of SourceVariable
	 */
	public static SourceVariable createSourceVariables(SingleVariableDeclaration declaration) {

		// Retrieve variable type
		String varType = JavaAnalyzerUtils.getSimpleTypeName(declaration.getType().toString());

		SourceVariable sourceVariable = new SourceVariable();
		sourceVariable.setName(declaration.getName().getIdentifier());
		sourceVariable.setType(varType);

		return sourceVariable;
	}

	/**
	 * Return the method signature owning the node passed in argument.
	 * 
	 * @param node
	 *        the node where are interested
	 * @return the signature of the method owning the node or null
	 */
	private static String getMethodSignature(ASTNode node) {

		String signature = "";

		// Retrieve class owning the node
		MethodDeclaration methodDeclaration = JavaAnalyzerUtils.getMethodDeclaration(node);

		if (methodDeclaration != null) {

			// Retrieve method name
			String methodName = methodDeclaration.getName().getIdentifier();

			// Retrieve method parameters
			String parameters = "";

			for (Object p : methodDeclaration.parameters()) {

				SingleVariableDeclaration param = (SingleVariableDeclaration) p;

				String paramType = JavaAnalyzerUtils.getSimpleTypeName(param.getType().toString());
				String paramName = param.getName().getIdentifier();

				parameters += paramType + " " + paramName + ",";
			}

			// Check for empty parameters
			if (parameters.trim().length() == 0) {
				parameters = "void";
			}

			// Retrieve full method signature
			signature = methodName + "(" + StringUtils.removeTrailString(parameters, ",") + ")";
		}

		return signature;
	}

	/**
	 * Return the simple name of a type class without its package component
	 * name.
	 * 
	 * @param typeName
	 * @return the simple type name
	 */
	private static String getSimpleTypeName(String typeName) {

		if (typeName == null || typeName.trim().length() == 0)
			return "void";

		if (typeName.contains(".")) {
			return typeName.substring(typeName.lastIndexOf(".") + 1);
		}

		return typeName;
	}

	/**
	 * Return the class owning the node passed in argument.
	 * 
	 * @param node
	 *        the for which we are looking for class owner
	 * @return a type-declaration or null
	 */
	private static TypeDeclaration getTypeDeclaration(ASTNode node) {

		TypeDeclaration typeDeclaration = null;

		if (node instanceof TypeDeclaration) {
			typeDeclaration = (TypeDeclaration) node;
		}
		else {

			ASTNode parent = node.getParent();

			while (parent != null && !(parent instanceof TypeDeclaration)) {
				parent = parent.getParent();
			}

			typeDeclaration = (TypeDeclaration) parent;
		}

		return typeDeclaration;
	}

	/**
	 * Return the method owning the node passed in argument.
	 * 
	 * @param node
	 *        the for which we are looking for method owner
	 * @return a method-declaration or null
	 */
	private static MethodDeclaration getMethodDeclaration(ASTNode node) {

		MethodDeclaration methodDeclaration = null;

		if (node instanceof MethodDeclaration) {
			methodDeclaration = (MethodDeclaration) node;
		}
		else {

			ASTNode parent = node.getParent();

			while (parent != null && !(parent instanceof MethodDeclaration)) {
				parent = parent.getParent();
			}

			methodDeclaration = (MethodDeclaration) parent;
		}

		return methodDeclaration;
	}

	/**
	 * Retrieve the source-class owning a node.
	 * 
	 * @param node
	 *        the node where are interested
	 * @param parsedClasses
	 *        source-class already parsed
	 * @return the source-class or null
	 */

	public static SourceClass getSourceClass(ASTNode node, Map<String, SourceClass> parsedClasses) {

		SourceClass sourceClass = null;

		// Retrieve owning method
		MethodDeclaration methodDeclaration = getMethodDeclaration(node);

		// And retrieve class owning the method
		TypeDeclaration typeDeclaration = getTypeDeclaration(methodDeclaration);

		if (typeDeclaration != null) {
			String classname = typeDeclaration.getName().getIdentifier();
			sourceClass = parsedClasses.get(classname);
		}

		return sourceClass;
	}

	/**
	 * Retrieve the source-method owning a node.
	 * 
	 * @param node
	 *        the node where are interested
	 * @param parsedClasses
	 *        source-class already parsed
	 * @return the source-class or null
	 */

	public static SourceMethod getSourceMethod(ASTNode node, Map<String, SourceClass> parsedClasses) {

		SourceMethod sourceMethod = null;

		// Retrieve source class owning the node
		SourceClass sourceClass = getSourceClass(node, parsedClasses);

		if (sourceClass != null) {

			MethodDeclaration methodDeclaration = getMethodDeclaration(node);

			// Retrieve the owning method
			if (methodDeclaration != null) {
				String methodSignature = getMethodSignature(methodDeclaration);
				sourceMethod = ApplicationLogic.getSourceMethodBySignature(sourceClass, methodSignature);
			}
		}

		return sourceMethod;
	}

}
