/**
 * 
 */
package ch.hesge.csim2.engine;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;

import ch.hesge.csim2.core.utils.StringUtils;

/**
 * Utility class related to stem concept analysis.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 *
 */
public class StemConceptAnalyzerUtils {

	/**
	 * Retrieve all stems associated to a name.
	 * Words present in rejectedList will not produce associated stems.
	 * 
	 * @param name
	 *        the name to use to extract stems
	 * @param rejectedList
	 *        the list of forbidden words
	 * @return
	 *         a list of stems associated to the list of names
	 */
	public static List<String> getStems(String name, List<String> rejectedList) {

		String canonicalName = getCanonicalName(name);
		List<String> words = splitWords(canonicalName, rejectedList);
		List<String> stems = extractStems(words);

		return stems;
	}

	/**
	 * Retrieve a canonical reference/identifier name.
	 * All decoration comming from hungarian notation are cleaned and left name
	 * is returned.
	 * 
	 * For instance:
	 * the reference m_lpSize will return 'Size'.
	 * the reference iWidth will return 'Width'.
	 * 
	 * All technical notation (pointer, reference, etc..) are also removed.
	 * 
	 * @param identifier
	 *        the string to analyze
	 * @return
	 *         a simplified name without hungarian decoration
	 */
	private static String getCanonicalName(String identifier) {

		if (identifier == null || identifier.length() == 0) 
			return identifier;
		
		// First remove pointer/reference/etc.. decorator
		identifier = identifier.replaceAll("[~/\\\\]", "");
		identifier = identifier.replaceAll("[-+*&,.<>;:(){}]", " ");

		// Then remove hungarian notation
		return StringUtils.trimHungarian(identifier);
	}

	/**
	 * Retrieve all words composing the name passed in argument, following the
	 * camel casing convention.
	 * 
	 * The following rules are applied to each part found:
	 * <ul>
	 * <li>if a word is included in reject-word-list.txt, it is skipped</li>
	 * <li>if a word is a duplicate from previous word, it is skipped</li>
	 * <li>if a word contains only one character, is skipped</li>
	 * <li>all words are converted to lowercase</li>
	 * <ul>
	 * 
	 * @param name
	 *        the name to decompose
	 * @return
	 *         a list of words
	 */
	private static List<String> splitWords(String name, List<String> rejectedWords) {

		List<String> result = new ArrayList<>();

		if (name != null && name.length() > 0) {
			
			// First, remove all diacritic characters (french accents)
			String normalizedName = Normalizer.normalize(name, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

			// Split name into its words following camel casing convention
			List<String> words = StringUtils.splitCamelCase(normalizedName);

			// Scan all words found
			for (String word : words) {

				if (word != null && word.length() > 0) {

					String elligibleWord = word.toLowerCase();

					// Add only words not in reject list or not already present
					if (!rejectedWords.contains(elligibleWord) && !result.contains(elligibleWord)) {
						result.add(elligibleWord);
					}
				}
			}
		}

		return result;
	}

	/**
	 * Retrieve all stems associated to a list of words (in same order).
	 * 
	 * @param words
	 *        the list of words to analyze
	 * @return
	 *         a list of stems associated to the list of names
	 */
	private static List<String> extractStems(List<String> words) {

		List<String> stemList = new ArrayList<>();

		// Create the stemmer
		SnowballStemmer stemmer = new englishStemmer();

		// Retrieve one stem for each word
		for (String word : words) {

			if (word != null && word.length() > 0) {
				stemmer.setCurrent(word);
				stemmer.stem();
				stemList.add(stemmer.getCurrent().toLowerCase());
			}
		}

		return stemList;
	}
}
