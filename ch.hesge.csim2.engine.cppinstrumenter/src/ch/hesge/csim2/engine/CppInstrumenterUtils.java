package ch.hesge.csim2.engine;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.core.dom.ast.ASTTypeUtil;
import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPBase;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.gnu.cpp.GPPLanguage;
import org.eclipse.cdt.core.index.IIndexFileLocation;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.core.parser.DefaultLogService;
import org.eclipse.cdt.core.parser.FileContent;
import org.eclipse.cdt.core.parser.IParserLogService;
import org.eclipse.cdt.core.parser.IScannerInfo;
import org.eclipse.cdt.core.parser.IncludeFileContentProvider;
import org.eclipse.cdt.core.parser.Keywords;
import org.eclipse.cdt.core.parser.ScannerInfo;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclaration;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPBaseClause;
import org.eclipse.cdt.internal.core.model.ASTStringUtil;
import org.eclipse.cdt.internal.core.parser.IMacroDictionary;
import org.eclipse.cdt.internal.core.parser.scanner.InternalFileContent;
import org.eclipse.cdt.internal.core.parser.scanner.InternalFileContentProvider;
import org.eclipse.core.runtime.CoreException;

import ch.hesge.csim2.core.model.Context;
import ch.hesge.csim2.core.model.SourceAttribute;
import ch.hesge.csim2.core.model.SourceClass;
import ch.hesge.csim2.core.model.SourceMethod;
import ch.hesge.csim2.core.model.SourceParameter;
import ch.hesge.csim2.core.model.SourceReference;
import ch.hesge.csim2.core.model.SourceReferenceOrigin;
import ch.hesge.csim2.core.model.SourceVariable;
import ch.hesge.csim2.core.utils.StringUtils;

/**
 * Utility class related to parsing source file with CDT.
 */
public class CppInstrumenterUtils {

	/**
	 * Create a parser for the file passed in argument.
	 * 
	 * @param sourceFile
	 *        the source file to parse
	 * @param scannerInfo
	 *        scanner info used to parse the file
	 * @param logService
	 *        logging service used to display error
	 * @param fileProvider
	 *        file provider used to retrieve dependencies
	 * @return a instance of IASTTranslationUnit linked to the sourceFile
	 * @throws CoreException
	 */

	public static IASTTranslationUnit createTranslationUnit(FileContent sourceFile, IScannerInfo scannerInfo, IParserLogService logService, IncludeFileContentProvider fileProvider) throws CoreException {
		return GPPLanguage.getDefault().getASTTranslationUnit(sourceFile, scannerInfo, fileProvider, null, ILanguage.OPTION_IS_SOURCE_UNIT, logService);
	}

	/**
	 * Create a parser log service for IASTTranslationUnit.
	 * 
	 * @return a log service
	 */
	public static IParserLogService createParserLogService() {
		return new DefaultLogService();
	}

	/**
	 * Create a parser include file provider to use will building
	 * IASTTranslationUnit.
	 * 
	 * @return a content provider
	 */
	public static IncludeFileContentProvider createFileProvider() {
		return new InternalFileContentProvider() {
			private Map<String, InternalFileContent> cache = new HashMap<>();

			@Override
			public InternalFileContent getContentForInclusion(String path, IMacroDictionary macroDictionary) {

				InternalFileContent fileContent = null;
				String filename = Paths.get(path).getFileName().toString();

				// Check if file is already loaded in cache
				if (cache.containsKey(filename)) {
					fileContent = cache.get(filename);
				}

				// Load file content
				else {
					fileContent = (InternalFileContent) InternalFileContent.createForExternalFileLocation(path);
				}

				return fileContent;
			}

			@Override
			public InternalFileContent getContentForInclusion(IIndexFileLocation arg0, String arg1) {
				return null;
			}
		};
	}

	/**
	 * Create a scanner configuration info. Allow to include specific include
	 * path and macro definitions.
	 * 
	 * @param rootPath
	 *        root path of the project
	 * @param properties
	 *        application properties
	 * @return an scanner info object
	 */
	public static IScannerInfo createScannerInfo(Context properties) {

		// Retrieve application properties
		String includePaths = properties.get("include-folders").toString();
		String macroDefinitions = properties.get("preprocessor-directives").toString();
		String rootPath = properties.get("root-path").toString();

		// Build the included folder array
		String[] paths = includePaths.split(",");
		for (int i = 0; i < paths.length; i++) {
			paths[i] = paths[i].replace("$rootpath", rootPath.toString());
			paths[i] = Paths.get(paths[i]).toAbsolutePath().normalize().toString();
		}

		// Retrieve preprocessorMacroDefinitions macros
		String[] macroArray = macroDefinitions.split(",");
		Map<String, String> macros = new HashMap<>();
		for (String macro : macroArray) {
			macros.put(macro, "");
		}

		return new ScannerInfo(macros, paths);
	}

	/**
	 * Create the default global class containing all global functions &
	 * variables.
	 * 
	 * @return a source class
	 */
	public static SourceClass createGlobalClass() {

		SourceClass globalClass = new SourceClass();

		globalClass.setType("class");
		globalClass.setName(CppInstrumenterUtils.getGlobalClassName());
		globalClass.setSuperClassName("COBject");

		return globalClass;
	}

	/**
	 * Get the name of the global class.
	 * 
	 * @return global class name
	 */
	public static String getGlobalClassName() {
		return "UndefinedClass";
	}

	/**
	 * Create a source class with all its attributes as defined by its composite
	 * specifier.
	 * 
	 * @param declarationSpecifier
	 *        the class specifier
	 * @return a source class
	 */
	public static SourceClass createSourceClass(ICPPASTCompositeTypeSpecifier declarationSpecifier) {

		List<SourceAttribute> attributes = new ArrayList<>();

		String classname = ASTStringUtil.getSimpleName(declarationSpecifier.getName()).trim();
		String classType = "unknown";
		String superclass = "CObject";
		String filename = Paths.get(declarationSpecifier.getContainingFilename()).getFileName().toString().toLowerCase();

		// First retrieve class type
		if (declarationSpecifier.getKey() == ICPPClassType.k_struct) {
			classType = "struct";
		}
		else if (declarationSpecifier.getKey() == ICPPClassType.k_union) {
			classType = "union";
		}
		else if (declarationSpecifier.getKey() == ICPPClassType.k_class) {
			classType = "class";
		}

		// For class, also get its base classes
		for (ICPPBase baseclass : declarationSpecifier.getScope().getClassType().getBases()) {
			if (baseclass instanceof CPPBaseClause) {
				superclass += ((CPPBaseClause) baseclass).getBaseClassSpecifierName().toString().trim() + ",";
			}
		}
		superclass = StringUtils.removeTrailString(superclass, ",");

		// And enumerate all member's declaration
		for (IASTDeclaration declaration : declarationSpecifier.getMembers()) {

			if (declaration instanceof CPPASTSimpleDeclaration) {

				CPPASTSimpleDeclaration fieldDeclaration = (CPPASTSimpleDeclaration) declaration;

				for (IASTDeclarator fieldDeclarator : fieldDeclaration.getDeclarators()) {

					// Take only attribute members and skip method members
					if (!(fieldDeclarator instanceof ICPPASTFunctionDeclarator)) {

						String attName = ASTStringUtil.getSimpleName(fieldDeclarator.getName()).trim();
						String attType = CppInstrumenterUtils.getDeclarationType(fieldDeclaration.getDeclSpecifier(), fieldDeclarator);

						// Skip CRuntimeClass attribute (MSVC specific)
						if (attName.equalsIgnoreCase("class" + classname))
							continue;

						SourceAttribute sourceAttribute = new SourceAttribute();
						sourceAttribute.setName(attName);
						sourceAttribute.setType(attType);

						if (!attributes.contains(sourceAttribute)) {
							attributes.add(sourceAttribute);
						}
					}
				}
			}
		}

		// Create the source class
		SourceClass sourceClass = new SourceClass();

		sourceClass.setName(classname);
		sourceClass.setType(classType);
		sourceClass.setSuperClassName(superclass);
		sourceClass.setFilename(filename);

		sourceClass.getAttributes().addAll(attributes);

		return sourceClass;
	}

	/**
	 * Create a source method with all parameters as defined by its function
	 * definition.
	 * 
	 * @param definition
	 *        the method definition
	 * @return a source method
	 */
	public static SourceMethod createMethod(ICPPASTFunctionDefinition definition) {

		String filename = Paths.get(definition.getContainingFilename()).getFileName().toString().toLowerCase();
		String methodName = CppInstrumenterUtils.getMethodName(definition);
		String methodSignature = CppInstrumenterUtils.getMethodSignature(definition);
		String returnType = CppInstrumenterUtils.getReturnType(definition);

		if (returnType.length() == 0) {
			returnType = "void";
		}

		SourceMethod sourceMethod = new SourceMethod();

		sourceMethod.setName(methodName);
		sourceMethod.setSignature(methodSignature);
		sourceMethod.setReturnType(returnType);
		sourceMethod.setFilename(filename);

		// Build the parameter names declared in the method
		if (definition.getDeclarator() instanceof ICPPASTFunctionDeclarator) {

			ICPPASTFunctionDeclarator functionDeclarator = (ICPPASTFunctionDeclarator) definition.getDeclarator();

			for (ICPPASTParameterDeclaration param : functionDeclarator.getParameters()) {

				String paramName = ASTStringUtil.getSimpleName(param.getDeclarator().getName()).trim();
				String paramType = CppInstrumenterUtils.getDeclaratorType(param.getDeclarator());

				SourceParameter sourceParameter = new SourceParameter();
				sourceParameter.setName(paramName);
				sourceParameter.setType(paramType);

				if (!sourceMethod.getParameters().contains(sourceParameter)) {
					sourceMethod.getParameters().add(sourceParameter);
				}
			}
		}

		return sourceMethod;
	}

	/**
	 * Create a source variable from a local variable declaration.
	 * 
	 * @param declaration
	 *        the local type declaration
	 * @param declarator
	 *        the local variable declarator
	 * @return a source variable
	 */
	public static SourceVariable createVariable(IASTDeclSpecifier specifier, IASTDeclarator declarator) {

		String varName = declarator.getName().toString();
		String varType = CppInstrumenterUtils.getDeclarationType(specifier, declarator);

		SourceVariable sourceVariable = new SourceVariable();

		sourceVariable.setName(varName);
		sourceVariable.setType(varType);

		return sourceVariable;
	}

	/**
	 * Create a source reference from a identifier expression.
	 * 
	 * @param declaration
	 *        the local type declaration
	 * @param declarator
	 *        the local variable declarator
	 * @return a source reference
	 */
	public static SourceReference createReference(SourceClass sourceClass, SourceMethod sourceMethod, IASTIdExpression expression) {

		SourceReference sourceReference = null;

		if (sourceClass != null && sourceMethod != null) {

			String expressionName = CppInstrumenterUtils.filterTypeName(ASTStringUtil.getSimpleName(expression.getName()));

			SourceReferenceOrigin expressionOrigin = SourceReferenceOrigin.UNKOWN_ORIGIN;
			String expressionType = null;
			
			// Check if reference is a class attribute
			for (SourceAttribute attribute : sourceClass.getAttributes()) {
				if (attribute.getName().equals(expressionName)) {
					expressionOrigin = SourceReferenceOrigin.CLASS_ATTRIBUTE;
					expressionType   = attribute.getType();
					break;
				}
			}
			
			// Check if reference is a method parameter
			if (expressionOrigin != SourceReferenceOrigin.UNKOWN_ORIGIN) {
				
				for (SourceParameter param : sourceMethod.getParameters()) {
					if (param.getName().equals(expressionName)) {
						expressionOrigin = SourceReferenceOrigin.METHOD_PARAMETER;
						expressionType   = param.getType();
						break;
					}
				}
			}

			// Check if reference is a local variable
			if (expressionOrigin != SourceReferenceOrigin.UNKOWN_ORIGIN) {
				
				for (SourceVariable var : sourceMethod.getVariables()) {
					if (var.getName().equals(expressionName)) {
						expressionOrigin = SourceReferenceOrigin.LOCAL_VARIABLE;
						expressionType   = var.getType();
						break;
					}
				}
			}

			// Resolve expression type, if not found
			if (expressionType == null) {
				expressionType = CppInstrumenterUtils.getExpressionType(expression);
			}

			sourceReference = new SourceReference();
			sourceReference.setName(expressionName);
			sourceReference.setType(expressionType);
			sourceReference.setOrigin(expressionOrigin);
		}

		return sourceReference;
	}

	/**
	 * Create a source attribute from a variable declaration.
	 * 
	 * @param declaration
	 *        the local type declaration
	 * @param declarator
	 *        the local variable declarator
	 * @return a source attribute
	 */
	public static SourceAttribute createAttribute(IASTDeclSpecifier specifier, IASTDeclarator declarator) {

		String attName = declarator.getName().toString();
		String attType = CppInstrumenterUtils.getDeclarationType(specifier, declarator);

		SourceAttribute sourceAttribute = new SourceAttribute();

		sourceAttribute.setName(attName);
		sourceAttribute.setType(attType);

		return sourceAttribute;
	}

	/**
	 * Retrieve the source class name of a function.
	 * 
	 * @param definition
	 *        the function definition to use while extracting informations
	 * @return the name of the class part or 'UndefinedClass'
	 */
	public static String getClassName(IASTFunctionDefinition definition) {

		String classname = CppInstrumenterUtils.getGlobalClassName();
		String qualifiedFunctionName = ASTStringUtil.getQualifiedName(definition.getDeclarator().getName());

		if (qualifiedFunctionName.indexOf("::") > -1) {
			classname = qualifiedFunctionName.substring(0, qualifiedFunctionName.indexOf("::"));
		}
		else {
			// Retrieve classname if methods is inlined within its declaring class
			if (definition.getParent() instanceof ICPPASTCompositeTypeSpecifier) {
				ICPPASTCompositeTypeSpecifier declaringClass = (ICPPASTCompositeTypeSpecifier) definition.getParent();
				classname = ASTStringUtil.getSimpleName(declaringClass.getName());
			}
		}

		return classname;
	}

	/**
	 * Retrieve the method name without parameter or return type.
	 * 
	 * @param definition
	 *        the function definition to use while extracting informations
	 * @return a method name
	 */
	public static String getMethodName(IASTFunctionDefinition definition) {

		String methodName = "UndefinedMethod";

		// Retrieve method name
		String qualifiedFunctionName = ASTStringUtil.getQualifiedName(definition.getDeclarator().getName());

		if (qualifiedFunctionName.indexOf("::") > -1) {
			methodName = qualifiedFunctionName.substring(qualifiedFunctionName.indexOf("::") + 2);
		}
		else {
			methodName = qualifiedFunctionName;
		}

		return methodName;
	}

	/**
	 * Retrieve the method signature of the function definition passed in
	 * argument. A signature doesn't contain return type and is of the form:
	 * methodName ( parameter-type, parameter-type, ...)
	 * 
	 * @param definition
	 *        the function definition to use while extracting informations
	 * @return a method signature
	 */
	public static String getMethodSignature(ICPPASTFunctionDefinition definition) {

		// Retrieve method name
		String methodName = CppInstrumenterUtils.getMethodName(definition);

		// Retrieve parameter types
		String parameterTypes = "";

		if (definition.getDeclarator() instanceof ICPPASTFunctionDeclarator) {

			ICPPASTFunctionDeclarator functionDeclarator = (ICPPASTFunctionDeclarator) definition.getDeclarator();

			for (ICPPASTParameterDeclaration param : functionDeclarator.getParameters()) {
				parameterTypes += CppInstrumenterUtils.getDeclaratorType(param.getDeclarator()) + ",";
			}
		}

		parameterTypes = StringUtils.removeTrailString(parameterTypes.trim(), ",");

		if (parameterTypes.length() == 0) {
			parameterTypes = "void";
		}

		return String.format("%s(%s)", methodName, parameterTypes);
	}

	/**
	 * Retrieve the type of a declaration.
	 * 
	 * @param specifier
	 * @param declarator
	 * @return the type as a string
	 */
	public static String getDeclarationType(IASTDeclSpecifier specifier, IASTDeclarator declarator) {

		String typeName = ASTStringUtil.getSignatureString(specifier, declarator);

		if (typeName == null) {
			return "void";
		}

		// Clean declaration type
		typeName = CppInstrumenterUtils.filterTypeName(typeName);

		if (typeName == null) {
			return "void";
		}

		return typeName.trim();
	}

	/**
	 * Retrieve the type of a declaration.
	 * 
	 * @param declarator
	 * @return the type as a string
	 */
	public static String getDeclaratorType(IASTDeclarator declarator) {

		String typeName = ASTStringUtil.getSignatureString(declarator);

		if (typeName == null) {
			return "void";
		}

		// Clean declaration type
		typeName = CppInstrumenterUtils.filterTypeName(typeName);

		if (typeName == null) {
			return "void";
		}

		return typeName.trim();
	}

	/**
	 * Retrieve the type of an expression.
	 * 
	 * @param expression
	 * @return the type as a string
	 */
	public static String getExpressionType(IASTExpression expression) {

		String typeName = ASTTypeUtil.getType(expression.getExpressionType());

		if (typeName == null) {
			return "void";
		}

		// Clean declaration type
		typeName = CppInstrumenterUtils.filterTypeName(typeName);

		if (typeName == null) {
			return "void";
		}

		return typeName.trim();
	}

	/**
	 * Retrieve the return type of a function definition.
	 * 
	 * @param definition
	 * @return the type as a string
	 */
	public static String getReturnType(ICPPASTFunctionDefinition definition) {

		String returnType = ASTStringUtil.getReturnTypeString(definition.getDeclSpecifier(), definition.getDeclarator());

		if (returnType == null) {
			return "void";
		}

		// Clean declaration type
		returnType = CppInstrumenterUtils.filterTypeName(returnType);

		if (returnType == null) {
			return "void";
		}

		return returnType.trim();
	}

	/**
	 * Retrieve the function definition (or method name) containing the node
	 * passed in argument.
	 * 
	 * @param node
	 *        the node to use at the start the lookup
	 * @return a node of type ICPPASTFunctionDefinition or null
	 */
	public static ICPPASTFunctionDefinition getFunctionDefinition(IASTNode node) {

		IASTNode parent = node;

		while (parent != null && !(parent instanceof ICPPASTFunctionDefinition)) {
			parent = parent.getParent();
		}

		if (parent instanceof ICPPASTFunctionDefinition) {
			return (ICPPASTFunctionDefinition) parent;
		}

		return null;
	}

	/**
	 * Retrieve the class definition containing the node passed in argument.
	 * 
	 * @param node
	 *        the node to use at the start the lookup
	 * @return a node of type IASTCompositeTypeSpecifier or null
	 */
	public static IASTCompositeTypeSpecifier getClassDefinition(IASTNode node) {

		IASTNode parent = node;

		while (parent != null && !(parent instanceof IASTCompositeTypeSpecifier)) {
			parent = parent.getParent();
		}

		if (parent instanceof IASTCompositeTypeSpecifier) {
			return (IASTCompositeTypeSpecifier) parent;
		}

		return null;
	}

	/**
	 * Filter type passed in argument by cleaning:
	 * 
	 * & reference char
	 * * pointer char
	 * enum declaration
	 * struct declaration
	 * union declaration
	 * class declaration
	 * const declaration
	 * volatile declaration
	 * 
	 * @param node
	 *        the node to use at the start the lookup
	 * @return a node of type IASTCompositeTypeSpecifier or null
	 */
	public static String filterTypeName(String typeName) {

		// Clean declaration type
		typeName = typeName.replace("*", "");
		typeName = typeName.replace("&", "");
		typeName = typeName.replace("ATL::", "");
		typeName = typeName.replace(Keywords.ENUM, "");
		typeName = typeName.replace(Keywords.STRUCT, "");
		typeName = typeName.replace(Keywords.UNION, "");
		typeName = typeName.replace(Keywords.CLASS, "");
		typeName = typeName.replace(Keywords.CONST, "");
		typeName = typeName.replace(Keywords.VOLATILE, "");

		return typeName;
	}
}
