package org.dyndns.genetic;

public class Individual {

	static int DEFAULT_GENE_LENGTH = 64;

	// Private attributes
	private int competence = -1;
	private Genes genes = new Genes(DEFAULT_GENE_LENGTH);

	/**
	 * Default constructor
	 */
	public Individual() {
	}

	/**
	 * Return the individual genotype.
	 * 
	 * @return a Genotype
	 */
	public Genes getGenes() {
		return genes;
	}

	/**
	 * Retrieve the individual competence.
	 * 
	 * @return an integer
	 */
	public int getCompetence() {

		// Cache competence (
		if (competence == -1) {
			competence = Skill.getSkill(this);
		}

		return competence;
	}
}
