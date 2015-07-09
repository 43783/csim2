package org.dyndns.genetic;

/**
 * http://slauncha.dyndns.org/index.php?article63/creation-d-un-algorithme-genetique
 */
public class Main {

	public static void main(String[] args) {

		// Set a candidate solution
		Skill.setSolution("1111000000000000000000000000001111100000000000000000000000001111");		

		// Create an initial population
		Population population = new Population(100, true);

		int generation = 0;

		// Evolve our population until we reach an optimum solution
		while (population.getMostCompetent().getCompetence() < Skill.getMaxSkill()) {
			generation++;
			System.out.println("Generation: " + generation + " competence: " + population.getMostCompetent().getCompetence());
			population = Algorithm.evolvePopulation(population);
		}
		
		System.out.println("\nSolution found!");
		System.out.println("Generation: " + generation);
		System.out.println("Genes:" + population.getMostCompetent().getGenes());

	}
}
