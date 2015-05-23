package ch.hesge.csim2.core.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is a generic utility class for strings.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 */

public class StringUtils {

	// Private constants
	// Note: regex for uppercase letter = \\p{Lu} (ref:
	// http://fr.wikipedia.org/wiki/Notation_hongroise)
	private static Pattern	HUNGARIAN_PATTERN	= Pattern.compile("(m_|g_|s_|l_)?(class|char|C|rgb|str|ar|by|dw|fd|pt|sz|a|b|c|d|f|h|i|l|n|o|p|s|t|u|v|w)*(?<varname>\\p{Lu}\\w+)");

	/**
	 * Convert a string into a clean displayable string.
	 * 
	 * @param value
	 * @return a string
	 */
	public static String toVisualString(String value) {

		if (value == null || value.isEmpty() || value.trim().isEmpty()) {
			return "-";
		}

		return value;
	}

	/**
	 * Convert a String array into a single String
	 * 
	 * @param aStringArray
	 *            the array to convert
	 * @return a single String
	 */
	public static String toString(String[] aStringArray) {

		StringBuilder stringBuffer = new StringBuilder();

		if (aStringArray.length > 0) {

			stringBuffer.append(aStringArray[0]);

			for (int i = 0; i < aStringArray.length; i++) {

				stringBuffer.append(aStringArray[i]);

				if (i < aStringArray.length - 1)
					stringBuffer.append(" ");
			}
		}
		return stringBuffer.toString();
	}

	/**
	 * Convert a Throwable into its string representation. Basically, the
	 * throwable is converted with its stack-trace into a single string
	 * representation.
	 * 
	 * @param aThrowable
	 *            the Throwable to convert
	 * @return a String
	 */
	public static String toString(Throwable aThrowable) {

		Writer result = new StringWriter();
		PrintWriter printWriter = new PrintWriter(result);
		aThrowable.printStackTrace(printWriter);

		return result.toString();
	}

	/**
	 * Compute the join of to path between them. If second path is absolute, it
	 * will be returned. If relative, it will be concatenated to the first one
	 * (considered as the root path).
	 * 
	 * @param firstPath
	 * @param secondPath
	 * @return
	 */
	public static String combine(String firstPath, String secondPath) {

		String newPath = null;

		// If new path is absolute
		if (secondPath.startsWith("/")) {
			newPath = secondPath;
		}

		// If current path is root
		else if (firstPath.equals("/")) {
			newPath = firstPath + secondPath;
		}

		// Otherwise, concatenate currentPath with newPath
		else {
			newPath = firstPath + "/" + secondPath;
		}

		// Remove any leading slash
		while (!newPath.equals("/") && newPath.endsWith("/")) {
			newPath = removeLastChar(newPath);
		}

		// Remove double slashes
		newPath = newPath.replace("//", "/");

		// Resolve relative path part
		while (newPath.contains("/..")) {

			// If root path is the last one
			if (newPath.equals("/..")) {
				newPath = "/";
			}

			// Otherwise, consume one path level
			else {
				int backEndIndex = newPath.indexOf("/..");
				int backStartIndex = newPath.substring(0, backEndIndex).lastIndexOf("/");
				newPath = newPath.substring(0, backStartIndex) + newPath.substring(backEndIndex + 3);
			}

			// If root has been consumed
			if (newPath.isEmpty()) {
				newPath = "/";
			}
		}

		return newPath;
	}

	/**
	 * Return the string passed in parameters with its last character removed.
	 * 
	 * @param aString
	 *            the original string
	 * @return the original string minus its last character
	 */
	public static String removeLastChar(String aString) {

		if (aString.length() > 1)
			return aString.substring(0, aString.length() - 1);

		return aString;
	}

	/**
	 * Return the first string passed in parameters with its last part removed,
	 * if it matches the last string passed in argument.
	 * 
	 * @param aString
	 *            the original string
	 * @param trailString
	 *            the trailing string that should be removed, if present
	 * @return the original string minus the trailing string sequence
	 */
	public static String removeTrailString(String aString, String trailString) {

		if (aString.length() >= trailString.length()) {
			int trailStringIndex = aString.lastIndexOf(trailString);
			if (trailStringIndex > -1)
				return aString.substring(0, aString.length() - trailString.length());
		}

		return aString;
	}

	/**
	 * Repeat a string a specific times.
	 * 
	 * @param string
	 *            the string to repeat
	 * @param count
	 *            the number of times the string should be repeated
	 * @return the repeated string
	 */
	public static String repeat(String string, int count) {

		int length = string.length();
		int size = length * count;

		char[] aChar = new char[size];

		for (int i = 0; i < size; i++) {
			aChar[i] = string.charAt(i % length);
		}

		return new String(aChar);
	}

	/**
	 * Return true if args passed contains the paramName.
	 * 
	 * @param args
	 *            the argument to scan
	 * @param switchList
	 *            the switch name (with or without '-')
	 * @return a boolean
	 */
	public static boolean hasSwitch(String[] args, String... switchList) {

		if (args != null) {

			for (String arg : args) {

				for (String switchName : switchList) {

					if (arg.equals(switchName)) {
						return true;
					}
				}
			}

		}

		return false;
	}

	/**
	 * Return true if args passed contains the paramName.
	 * 
	 * @param args
	 *            the argument to scan
	 * @param switchList
	 *            the switch name (with or without '-')
	 * @return a boolean
	 */
	public static boolean hasSwitch(String args, String... switchList) {

		if (args != null) {

			for (String param : switchList) {

				if (args.contains(param)) {
					return true;
				}
			}

		}

		return false;
	}

	/**
	 * Return false if args passed contains the paramName.
	 * 
	 * @param args
	 *            the argument to scan
	 * @param switchList
	 *            the switch name (with or without '-')
	 * @return a boolean
	 */
	public static boolean hasNotSwitch(String[] args, String... params) {
		return !hasSwitch(args, params);
	}

	/**
	 * Return false if args passed contains the paramName.
	 * 
	 * @param args
	 *            the argument to scan
	 * @param switchList
	 *            the switch name (with or without '-')
	 * @return a boolean
	 */
	public static boolean hasNotSwitch(String args, String... params) {
		return !hasSwitch(args, params);
	}

	/**
	 * Return the list parameters (not switch) contained within the arguments
	 * passed in parameters.
	 * 
	 * @param args
	 *            the argument to scan
	 * @return a list of parameter
	 */
	public static List<String> getParameters(String[] args) {

		List<String> params = new ArrayList<>();

		for (String arg : args) {

			if (arg.startsWith("-"))
				continue;

			params.add(arg);
		}

		return params;
	}

	/**
	 * Compute a string with elapsed-time between to time expressed in
	 * milliseconds.
	 * 
	 * @param startTime
	 *            start time in ms
	 * @param endTime
	 *            end time in ms
	 * @return a string representation of the time elapsed
	 */
	public static String getElapseTime(long startTime, long endTime) {

		String result;

		long elapseTime = endTime - startTime;

		if (elapseTime < 1000) {
			result = String.format("%d ms", elapseTime);
		}
		else if (elapseTime < 60000) {
			result = String.format("%.2f sec", elapseTime / 1000.0);
		}
		else if (elapseTime < 3600000) {
			result = String.format("%.2f min", elapseTime / 60000.0);
		}
		else {
			result = String.format("%.2f hour", elapseTime / 3600000.0);
		}

		return result;
	}

	/**
	 * Checks if a character is contained within a character array.
	 * 
	 * @param ch
	 *            the character we are checking
	 * @param chars
	 *            the array where to look for
	 * @return true if ch is contained in chars array, false otherwise
	 */
	public static boolean contains(char ch, char[] chars) {

		if (chars == null || chars.length == 0) {
			return false;
		}

		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == ch) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Checks if a string is contained within a string list.
	 * 
	 * @param str
	 *            the string we are checking
	 * @param strArray
	 *            the string array where to look for
	 * @return true if str is contained within the string list, false otherwise
	 */
	public static boolean contains(String str, String[] strArray) {

		if (strArray == null || strArray.length == 0) {
			return false;
		}

		for (int i = 0; i < strArray.length; i++) {
			if (strArray[i].equals(str)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * >>>>>>> .r12487 Concatenate all words into a single string.
	 * 
	 * @param words
	 *            the words to join
	 * @return a single string
	 */
	public static String concatenate(List<String> words) {

		String result = "";

		for (String word : words) {
			result += word;
		}

		return result;
	}

	/**
	 * Remove all character based on hungarian notation (HN) and keep only
	 * relevant name without decoration.
	 * 
	 * @param s
	 *            the string to clean
	 * @return a cleaned string without decoration
	 */
	public static String trimHungarian(String s) {

		Matcher regexMatcher = HUNGARIAN_PATTERN.matcher(s.trim());

		if (regexMatcher.matches()) {
			s = regexMatcher.group("varname");
		}

		return s;
	}

	/**
	 * Split a camelcase string into its word components. <code>
	 * Here are some use cases: 
	 * <pre>
	 * 		CamelCasingTest      = Camel, Casing, Test
	 * 		methodRemoveProperty = method, Remove, Property
	 * 		ConceptNumber328Real = Concept, Number328, Real
	 * 		TestCPTSmall         = Test, CPT, Small
	 * 		CBatFluM0            = C, Bat, Flu, M0
	 * 		CCircBatLiqCalcul    = C, Circ, Bat, Liq, Calcul
	 *  	Petits_Calculs       = Petits, Calculs
	 *  	_Petits_Calculs_     = Petits, Calculs
	 *  	Petits Calculs       = Petits, Calculs
	 *      dE                   = d, E
	 *  </pre>
	 * 
	 * @param s
	 *            the string to split
	 * @return a string list
	 */
	public static List<String> splitCamelCase(String s) {
		List<String> result = new ArrayList<>();

		if (s != null && s.trim().length() > 0) {

			char[] wordSeparators = new char[] { ' ', '_', '-' };

			boolean prevIsUpper = false;
			boolean prevIsSeparator = true;
			boolean capitalizedWord = false;

			// First clean space
			StringBuilder word = new StringBuilder();
			char[] buf = s.toCharArray();

			for (int i = 0; i < buf.length; i++) {

				char ch = buf[i];
				boolean isUpperCase = Character.isUpperCase(ch);
				boolean isSeparator = StringUtils.contains(ch, wordSeparators);

				// Detect start of capitalization
				if (isUpperCase && prevIsUpper) {
					word.append(ch);
					capitalizedWord = true;
				}

				// Detect word separator (skip)
				else if (isSeparator) {
					prevIsUpper = false;
					prevIsSeparator = true;
					capitalizedWord = false;
				}

				// Detect start of word
				else if (prevIsSeparator || (isUpperCase && !prevIsUpper)) {
					String newWord = word.toString();
					if (newWord.length() > 0)
						result.add(newWord);
					word.setLength(0);
					word.append(ch);
					prevIsUpper = isUpperCase;
					prevIsSeparator = false;
					capitalizedWord = false;
				}

				// Detect end of capitalization
				else if (!isUpperCase && prevIsUpper && capitalizedWord) {

					String currentWord = word.toString();
					String newWord = currentWord.substring(0, currentWord.length() - 1);
					char lastUpperCaseChar = currentWord.charAt(currentWord.length() - 1);

					result.add(newWord);
					word.setLength(0);
					word.append(lastUpperCaseChar);
					word.append(ch);

					prevIsUpper = false;
					capitalizedWord = false;
				}

				// Detect word feeding
				else if (!isUpperCase) {
					word.append(ch);
					prevIsUpper = false;
				}
			}

			// Take last word, if present
			if (word != null && word.length() > 0) {
				result.add(word.toString());
			}
		}

		return result;
	}
}
