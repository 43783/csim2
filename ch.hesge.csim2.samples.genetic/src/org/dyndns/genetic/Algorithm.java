package org.dyndns.genetic;

/**
 * http://slauncha.dyndns.org/index.php?article63/creation-d-un-algorithme-genetique
 */
public class Algorithm {

	/* GA parameters */
	private static final double uniformRate = 0.5;
	private static final double mutationRate = 0.00015;
	private static final int tournamentSize = 5;
	private static final boolean elitism = true;

	/**
	 * Evolve a population.
	 * 
	 * @param population
	 * @return
	 */
	public static Population evolvePopulation(Population population) {
		
		Population newPopulation = new Population(population.size(), false);

		// Keep our best individual
		if (elitism) {
			newPopulation.setIndividual(0, population.getMostCompetent());
		}

		// Crossover population
		int elitismOffset;
		if (elitism) {
			elitismOffset = 1;
		}
		else {
			elitismOffset = 0;
		}
		
		// Loop over the population size and create new individuals with
		// crossover
		for (int i = elitismOffset; i < population.size(); i++) {
			Individual a = tournamentSelection(population);
			Individual b = tournamentSelection(population);
			Individual newIndividual = crossover(a, b);
			newPopulation.setIndividual(i, newIndividual);
		}

		// Mutate population
		for (int i = elitismOffset; i < newPopulation.size(); i++) {
			mutate(newPopulation.getIndividual(i));
		}

		return newPopulation;
	}

	/**
	 * Crossover individuals.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private static Individual crossover(Individual a, Individual b) {
		
		Individual newIndividual = new Individual();
		
		// Loop through genes
		for (int i = 0; i < a.getGenes().size(); i++) {
			// Crossover
			if (Math.random() <= uniformRate) {
				newIndividual.getGenes().set(i, a.getGenes().get(i));
			}
			else {
				newIndividual.getGenes().set(i, b.getGenes().get(i));
			}
		}
		return newIndividual;
	}

	/**
	 * Mutate an individual.
	 * 
	 * @param individual
	 */
	private static void mutate(Individual individual) {
		
		// Loop through genes
		for (int i = 0; i < individual.getGenes().size(); i++) {
			
			if (Math.random() <= mutationRate) {
				// Create random gene
				byte gene = (byte) Math.round(Math.random());
				individual.getGenes().set(i, gene);
			}
		}
	}

	/**
	 * Select individuals for crossover.
	 * 
	 * @param population
	 * @return
	 */
	private static Individual tournamentSelection(Population population) {
		
		// Create a tournament population
		Population tournament = new Population(tournamentSize, false);
		
		// For each place in the tournament get a random individual
		for (int i = 0; i < tournamentSize; i++) {
			int randomId = (int) (Math.random() * population.size());
			tournament.setIndividual(i, population.getIndividual(randomId));
		}
		
		// Get the fittest
		Individual fittest = tournament.getMostCompetent();
		
		return fittest;
	}
}
