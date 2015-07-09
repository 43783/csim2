package org.dyndns.genetic;

public class Skill {

	static byte[] solution;

	// 
	/**
	 * To make it easier we can use this method to set our candidate solution
	 * with string of 0s and 1s.
	 * 
	 * @param newSolution
	 */
	static void setSolution(String newSolution) {

		solution = new byte[newSolution.length()];

		// Loop through each character of our string and save it in our byte
		// array
		for (int i = 0; i < newSolution.length(); i++) {
			String character = newSolution.substring(i, i + 1);
			if (character.contains("0") || character.contains("1")) {
				solution[i] = Byte.parseByte(character);
			}
			else {
				solution[i] = 0;
			}
		}
	}

	/**
	 * Get optimum skill
	 * @return
	 */
	public static int getMaxSkill() {
		return solution.length;
	}

	/**
	 * Compute skill by comparing it to our candidate solution.
	 * The score return is the number genes matching with solution. 
	 * 
	 * @param individual
	 * @return
	 */
	public static int getSkill(Individual individual) {

		int skill = 0;

		// Loop through our individuals genes and compare them to our candidates
		for (int i = 0; i < individual.getGenes().size() && i < solution.length; i++) {
			if (individual.getGenes().get(i) == solution[i]) {
				skill++;
			}
		}

		return skill;
	}
}
