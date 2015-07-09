package ch.hesge.csim2.engine;

import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import ch.hesge.csim2.core.utils.StringUtils;

/**
 * Utility class related to parsing source file
 */
public class JavaInstrumenterUtils {

	/**
	 * Return the name of the compilation unit is defining.
	 * 
	 * @param compilationUnit
	 * @return the name of the current package
	 */
	public static String getPackageName(CompilationUnit compilationUnit) {
		return compilationUnit.getPackage().getName().getFullyQualifiedName();
	}

	/**
	 * Return true if the method passed in argument is owned by an interface.
	 * Otherwise, return false.
	 * 
	 * @param declaration
	 *            the method to use
	 * @return true for interface, false otherwise
	 */
	public static boolean isInterfaceMethod(MethodDeclaration declaration) {

		ASTNode parent = declaration.getParent();

		// Lookup trough the ownership hierarchy the declaring class
		while (parent != null) {

			if (TypeDeclaration.class.isAssignableFrom(parent.getClass())) {
				return ((TypeDeclaration) parent).isInterface();
			}

			parent = parent.getParent();
		}

		return false;
	}

	/**
	 * Return the class owning the declaration passed in argument.
	 * 
	 * @param declaration
	 * @return a classname
	 */
	public static String getClassName(MethodDeclaration declaration) {

		String className = "UndefinedClass";
		String anonymousType = null;

		ASTNode parent = declaration.getParent();

		// Lookup trough the ownership hierarchy the declaring class
		while (parent != null) {

			if (TypeDeclaration.class.isAssignableFrom(parent.getClass())) {
				className = ((TypeDeclaration) parent).getName().getFullyQualifiedName();
				break;
			}

			parent = parent.getParent();
		}

		parent = declaration.getParent();

		// Lookup trough the ownership hierarchy if the method is owned by an
		// anonymous class
		while (parent != null) {

			if (ClassInstanceCreation.class.isAssignableFrom(parent.getClass())) {
				anonymousType = ((ClassInstanceCreation) parent).getType().toString();
				break;
			}

			parent = parent.getParent();
		}

		if (anonymousType != null) {
			return className + "$" + anonymousType;
		}

		return className;
	}

	/**
	 * Return the method name defined by the declaration.
	 * 
	 * @param declaration
	 * @return a method name
	 */
	public static String getMethodName(MethodDeclaration declaration) {
		return declaration.getName().getFullyQualifiedName();
	}

	/**
	 * Return a string corresponding to all parameters defined in a method
	 * declaration.
	 * 
	 * @param declaration
	 * @return the parameter list separated by comma.
	 */
	@SuppressWarnings("unchecked")
	public static String getParametersTypes(MethodDeclaration declaration) {

		String methodParameters = "";

		List<SingleVariableDeclaration> parameters = declaration.parameters();

		if (parameters.size() > 0) {
			for (SingleVariableDeclaration parameter : parameters) {
				methodParameters += parameter.getType().toString();
				methodParameters += " ";
				methodParameters += parameter.getName().getFullyQualifiedName();
				methodParameters += ",";
			}

			methodParameters = StringUtils.removeLastChar(methodParameters);
		}

		return methodParameters;
	}

	/**
	 * Retrieve the return type of the method passed in argument.
	 * 
	 * @param declaration
	 * @return the return type
	 */
	public static String getReturnType(MethodDeclaration declaration) {
		return declaration.getReturnType2() == null ? "void" : declaration.getReturnType2().toString();
	}

	/**
	 * Create a MethodInvocation based on the method specified in argument.
	 * 
	 * @param compilationUnit
	 *            the top level unit containing the method
	 * @param methodName
	 *            entering or exiting
	 * @param declaration
	 *            the method declaration
	 * @return the method invocation for the class, method passed in argument.
	 */
	@SuppressWarnings("unchecked")
	public static MethodInvocation createTraceInvocation(CompilationUnit compilationUnit, String methodName, MethodDeclaration declaration) {

		AST ast = compilationUnit.getAST();

		StringLiteral staticPackageNameLiteral = ast.newStringLiteral();
		staticPackageNameLiteral.setLiteralValue(JavaInstrumenterUtils.getPackageName(compilationUnit));

		StringLiteral staticClassNameLiteral = ast.newStringLiteral();
		staticClassNameLiteral.setLiteralValue(JavaInstrumenterUtils.getClassName(declaration));

		StringLiteral dynamicPackageNameLiteral = ast.newStringLiteral();
		dynamicPackageNameLiteral.setLiteralValue(JavaInstrumenterUtils.getPackageName(compilationUnit));

		StringLiteral dynamicClassNameLiteral = ast.newStringLiteral();
		dynamicClassNameLiteral.setLiteralValue(JavaInstrumenterUtils.getClassName(declaration));

		StringLiteral methodNameLiteral = ast.newStringLiteral();
		methodNameLiteral.setLiteralValue(JavaInstrumenterUtils.getMethodName(declaration));

		StringLiteral parametersTypesLiteral = ast.newStringLiteral();
		parametersTypesLiteral.setLiteralValue(JavaInstrumenterUtils.getParametersTypes(declaration));

		StringLiteral returnTypeLiteral = ast.newStringLiteral();
		returnTypeLiteral.setLiteralValue(JavaInstrumenterUtils.getReturnType(declaration));

		// Create method invocation with proper parameters
		MethodInvocation methodInvocation = ast.newMethodInvocation();
		methodInvocation.setExpression(ast.newName("ch.hesge.csim2.engine.TraceLogger"));
		methodInvocation.setName(ast.newSimpleName(methodName));
		methodInvocation.arguments().add(staticPackageNameLiteral);
		methodInvocation.arguments().add(staticClassNameLiteral);
		methodInvocation.arguments().add(dynamicPackageNameLiteral);
		methodInvocation.arguments().add(dynamicClassNameLiteral);
		methodInvocation.arguments().add(methodNameLiteral);
		methodInvocation.arguments().add(parametersTypesLiteral);
		methodInvocation.arguments().add(returnTypeLiteral);

		return methodInvocation;
	}
}
