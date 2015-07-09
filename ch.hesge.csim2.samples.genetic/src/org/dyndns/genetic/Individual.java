package org.dyndns.genetic;

public class Individual {

	// Private attributes
	private int competence = -1;
	private Genes genes;

	/**
	 * Default constructor
	 */
	public Individual() {
		 genes = new Genes(Skill.getMaxSkill());
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
