package ch.hesge.csim2.engine.instrumentation;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;


public class TraceLogger {

	// Private attributes
	private static Writer traceWriter;
	private static String defautTraceFile = "log/trace.log";

	// Private constants
	private static int TRACE_ENTER = 0;
	private static int TRACE_EXIT  = 1;

	// Predefined formatter
	private static final String TRACE_ENTER_FORMAT = "%3$s %4$s %3$s %4$s [%2$s] %7$s(%8$s) AS %9$s [%1$s]%n";
	private static final String TRACE_EXIT_FORMAT  = "END %3$s %4$s %3$s %4$s [%2$s] %7$s(%8$s) AS %9$s [%1$s]%n";
	
	/*
	 * Available info on format within the TRACE_FORMAT string:
	 *   1$ = timestamp
	 *   2$ = threadId
	 *   3$ = static package name
	 *   4$ = static class name
	 *   5$ = dynamic package name
	 *   6$ = dynamic class name
	 *   7$ = methodname
	 *   8$ = parameters types
	 *   9$ = return type
	 */

	// Create a trace logger
	private static final Logger LOGGER = Logger.getLogger(TraceLogger.class.getName());	
		
	/**
     * Log a single method entry
	 */
	public static void entering(String staticPackageName, String staticClassName, String dynamicPackageName, String dynamicClassName, String methodName, String parametersTypes, String returnType) {
		trace(TRACE_ENTER, staticPackageName, staticClassName, dynamicPackageName, dynamicClassName, methodName, parametersTypes, returnType);
	}
		
	/**
     * Log a single method exit
	 */	
	public static void exiting(String staticPackageName, String staticClassName, String dynamicPackageName, String dynamicClassName, String methodName, String parametersTypes, String returnType) {
		trace(TRACE_EXIT, staticPackageName, staticClassName, dynamicPackageName, dynamicClassName, methodName, parametersTypes, returnType);
	}
	
	/**
	 * Retrieve a writer to the output stream
	 */
	private static Writer getTraceWriter() {

		if (traceWriter == null) {
			
	    	// Retrieve output file name
	    	String propertyValue = System.getProperties().getProperty("ch.hesge.csim2.tracefile");
	    	
	    	if (propertyValue != null) {
	    		defautTraceFile = propertyValue;
	    	}
	
	    	// Create the trace file
	    	try {
	    		Path filepath = Paths.get(defautTraceFile);	    		
	    		Files.createDirectories(filepath.getParent());
	    		Files.deleteIfExists(filepath);
	    		Files.createFile(filepath);
	    		traceWriter = new FileWriter(filepath.toFile());
			}
			catch (IOException e) {
				LOGGER.severe("unable to open trace file: " + e.toString() + " ! Exception: " + e.toString());
			}
		}
		
		return traceWriter;
	}	
	
	/**
	 * Write a trace into the trace file 
	 */
	private static void trace(int traceType, String staticPackageName, String staticClassName, String dynamicPackageName, String dynamicClassName, String methodName, String parameterTypes, String returnType) {
				
		try {
			long timestamp     = System.currentTimeMillis();		
			long threadId      = Thread.currentThread().getId();
			
			Writer writer = getTraceWriter();

			// Create the trace entry
			String traceMessage = String.format(
				(traceType == TRACE_ENTER ? TRACE_ENTER_FORMAT : TRACE_EXIT_FORMAT), 
				timestamp, 
				threadId, 
				staticPackageName, 
				staticClassName, 
				dynamicPackageName, 
				dynamicClassName, 
				methodName, 
				parameterTypes,
				returnType);
			
			// Add it to the trace file
			writer.append(traceMessage);
			writer.flush();
		}
		catch (IOException e) {
			LOGGER.severe("error while creating a trace: " + e.toString() + " ! Exception: " + e.toString());
		}
	}
}
