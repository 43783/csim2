package ch.hesge.csim2.engine;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.hesge.csim2.core.model.Trace;

/**
 * Utility class related to Trace parsing file
 */
public class TraceLoaderUtils {

	// Private static attributes
	private static Pattern traceLinePattern;

	// Regex used to match each trace line (validation)
	private static String traceLineRegex = "(?<endtag>END\\s)?" + "(?<dynapack>\\S+)?" + "\\s" + "(?<dynaclass>\\S+)?" + "\\s" + "(?<statpack>\\S+)?" + "\\s" + "(?<statclass>\\S+)?" + "\\s" + "\\[(?<threadid>\\d+)\\]" + "\\s" + "(?<signature>.*\\(.*\\))" + "\\s" + "AS" + "\\s" + "(?<returntype>.+)" + "\\s" + "\\[(?<timestamp>\\d+)\\]" + "\\s*";

	// Static initializer
	static {
		traceLinePattern = Pattern.compile(traceLineRegex);
	}

	/**
	 * Parse a string and extract Trace information contained in it.
	 * The result is a Trace instance initialized with attributes found in
	 * string.
	 *
	 * <pre>
	 * Samples:
	 * 
	 * 		Entry Trace line:
	 *  
	 * 			package class package CDBCTherm [5308] RecBinX(CStdioFile,eIDRB,TCHAR,void,int) AS void [1392998705] 
	 * 
	 * 		Exit Trace line:
	 *  
	 * 			END package class package CDBCTherm [5308] RecBinX(CStdioFile,eIDRB,TCHAR,void,int) AS void [1392998705]
	 * 
	 * </pre>
	 */
	public static Trace parseTraceLine(String traceLine) {

		Trace trace = null;
		Matcher regexMatcher = traceLinePattern.matcher(traceLine);

		if (regexMatcher.matches()) {

			trace = new Trace();

			boolean isEnteringTrace = regexMatcher.group("endtag") == null;

			String strThreadId = regexMatcher.group("threadid");
			long threadId = Long.valueOf(strThreadId);

			String strTimestamp = regexMatcher.group("timestamp");
			long timestamp = Long.valueOf(strTimestamp);

			String staticPackage = regexMatcher.group("statpack");
			String staticClass = regexMatcher.group("statclass");
			String staticInstance = "0";
			String dynamicPackage = regexMatcher.group("dynapack");
			String dynamicClass = regexMatcher.group("dynaclass");
			String dynamicInstance = "0";

			// Split class in classname and instance id, if available
			if (staticClass.contains("#")) {
				String[] parts = staticClass.split("#");
				staticClass = parts[0];
				staticInstance = parts[1];
			}
			
			// Split class in classname and instance id, if available
			if (dynamicClass.contains("#")) {
				String[] parts = dynamicClass.split("#");
				dynamicClass = parts[0];
				dynamicInstance = parts[1];
			}
			
			trace.setEnteringTrace(isEnteringTrace);
			trace.setStaticPackage(staticPackage);
			trace.setStaticClass(staticClass);
			trace.setStaticInstance(staticInstance);
			trace.setDynamicPackage(dynamicPackage);
			trace.setDynamicClass(dynamicClass);
			trace.setDynamicInstance(dynamicInstance);
			trace.setThreadId(threadId);
			trace.setSignature(regexMatcher.group("signature"));
			trace.setReturnType(regexMatcher.group("returntype"));
			trace.setTimestamp(timestamp);
		}

		return trace;
	}
}
