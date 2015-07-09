package org.dyndns.genetic;

import java.util.ArrayList;
import java.util.List;

public class Population {

	// Private attributes
	private int size;
	private List<Individual> individuals;

	/**
	 * Create a population of specific size.
	 * 
	 * @param size
	 * @param initialize
	 */
	public Population(int size, boolean initialize) {

		this.size = size;
		this.individuals = new ArrayList<>(size);

		// Initialize population
		for (int i = 0; i < size; i++) {

			if (initialize) {
				individuals.add(new Individual());
			}
			else {
				individuals.add(null);
			}
		}
	}

	/**
	 * Return population size
	 * 
	 * @return count of individuals
	 */
	public int size() {
		return size;
	}

	/**
	 * Retrieve an individual by index.
	 * 
	 * @param index
	 * @return
	 */
	public Individual getIndividual(int index) {
		return individuals.get(index);
	}

	/**
	 * Set an individual at specified index.
	 * 
	 * @param index
	 * @param individual
	 */
	public void setIndividual(int index, Individual individual) {
		individuals.set(index, individual);
	}

	/**
	 * Retrieve the most competent individual in current population.
	 * That is the individual with the best competence factor.
	 * 
	 * @return an Individual
	 */
	public Individual getMostCompetent() {

		Individual mostCompetent = individuals.get(0);

		for (Individual individual : individuals) {
			if (individual.getCompetence() > mostCompetent.getCompetence()) {
				mostCompetent = individual;
			}
		}

		return mostCompetent;
	}
}
