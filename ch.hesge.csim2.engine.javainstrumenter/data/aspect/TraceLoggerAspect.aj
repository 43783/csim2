package ch.hesge.test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.MethodSignature;

import ch.hesge.csim2.core.utils.StringUtils;

public aspect TraceLoggerAspect {
	
	// Trace main execution entry
	@SuppressWarnings("rawtypes")
	before() : execution(public static void main(String[])) {
		
		Class declaringType = thisJoinPoint.getSignature().getDeclaringType();
		Method method = ((MethodSignature) thisJoinPoint.getSignature()).getMethod();
		String staticPackage = declaringType.getPackage().getName();
		String staticClass = declaringType.getSimpleName();
		String methodName = method.getName();
		String parameters = getParameterTypes(method.getParameterTypes());
		String returnType = method.getReturnType().getName();
		
		TraceLogger.entering(staticPackage, staticClass, staticPackage, staticClass, methodName, parameters, returnType);
    }
    
	// Trace main execution exit
	@SuppressWarnings("rawtypes")
	after() : execution(public static void main(String[])) {

		Class declaringType = thisJoinPoint.getSignature().getDeclaringType();
		Method method = ((MethodSignature) thisJoinPoint.getSignature()).getMethod();
		String staticPackage = declaringType.getPackage().getName();
		String staticClass = declaringType.getSimpleName();
		String methodName = method.getName();
		String parameters = getParameterTypes(method.getParameterTypes());
		String returnType = method.getReturnType().getName();
		
		TraceLogger.exiting(staticPackage, staticClass, staticPackage, staticClass, methodName, parameters, returnType);
    }

	// Trace constructor entry
	@SuppressWarnings("rawtypes")
    before() : initialization(*.new(..)) && !within(TraceLoggerAspect) && !within(TraceLogger) {

		Class declaringType = thisJoinPoint.getSignature().getDeclaringType();
		Constructor constructor = ((ConstructorSignature) thisJoinPoint.getSignature()).getConstructor();
		String staticPackage = declaringType.getPackage().getName();
		String staticClass = declaringType.getSimpleName();
		String methodName = "new";
		String parameters = constructor == null ? "" : getParameterTypes(constructor.getParameterTypes());
		String returnType = "void";
		
		TraceLogger.entering(staticPackage, staticClass, staticPackage, staticClass, methodName, parameters, returnType);
    }
    
	// Trace constructor exit
	@SuppressWarnings("rawtypes")
    after() : initialization(*.new(..)) && !within(TraceLoggerAspect) && !within(TraceLogger) {

		Class declaringType = thisJoinPoint.getSignature().getDeclaringType();
		Constructor constructor = ((ConstructorSignature) thisJoinPoint.getSignature()).getConstructor();
		String staticPackage = declaringType.getPackage().getName();
		String staticClass = declaringType.getSimpleName();
		String methodName = "new";
		String parameters = constructor == null ? "" : getParameterTypes(constructor.getParameterTypes());
		String returnType = "void";
		
		TraceLogger.exiting(staticPackage, staticClass, staticPackage, staticClass, methodName, parameters, returnType);
    }

	// Trace method invocation entry
	@SuppressWarnings("rawtypes")
    before() : call(* *(..)) && !within(TraceLoggerAspect) && !within(TraceLogger) {

    	Class declaringType = thisJoinPoint.getSignature().getDeclaringType();
		Method method = ((MethodSignature) thisJoinPoint.getSignature()).getMethod();
		String staticPackage = declaringType.getPackage().getName();
		String staticClass = declaringType.getSimpleName();
		String methodName = method.getName();
		String parameters = getParameterTypes(method.getParameterTypes());
		String returnType = method.getReturnType().getName();
		
		TraceLogger.entering(staticPackage, staticClass, staticPackage, staticClass, methodName, parameters, returnType);
    }
    
	// Trace method invocation exit
	@SuppressWarnings("rawtypes")
    after() : call(* *(..)) && !within(TraceLoggerAspect) && !within(TraceLogger) {

    	Class declaringType = thisJoinPoint.getSignature().getDeclaringType();
		Method method = ((MethodSignature) thisJoinPoint.getSignature()).getMethod();
		String staticPackage = declaringType.getPackage().getName();
		String staticClass = declaringType.getSimpleName();
		String methodName = method.getName();
		String parameters = getParameterTypes(method.getParameterTypes());
		String returnType = method.getReturnType().getName();
		
		TraceLogger.exiting(staticPackage, staticClass, staticPackage, staticClass, methodName, parameters, returnType);
    }    
	
	/**
	 * Return a string corresponding to all types of parameters 
	 * passed in argument.
	 * 
     * @param parameterTypes
     * @return
	 */
    @SuppressWarnings("rawtypes")
    public static String getParameterTypes( Class[] parameterTypes) {
		
		String methodParameters = "";
		
		if (parameterTypes != null && parameterTypes.length > 0) {
			for (Class parameterType : parameterTypes) {
				methodParameters += parameterType.getSimpleName();
				methodParameters += ",";
			}

			methodParameters = StringUtils.removeTrailString(methodParameters, ",");
		}
		
		return methodParameters;
	}
}
