package org.dyndns.genetic;

import java.util.ArrayList;
import java.util.List;

public class Genes {

	// Private attributes
	private final List<Byte> genes;

	/**
	 * Default constructor
	 */
	public Genes(int size) {

		genes = new ArrayList<>(size);

		// Initialize its genes
		for (int i = 0; i < size; i++) {
			genes.add((byte) Math.round(Math.random()));
		}
	}

	/**
	 * Return the gene at the specified index.
	 * 
	 * @param index
	 * @return
	 */
	public byte get(int index) {
		return genes.get(index);
	}

	/**
	 * Sets the gene at the specified index.
	 * 
	 * @param index
	 * @param value
	 */
	public void set(int index, byte value) {
		genes.set(index, value);
	}

	/**
	 * Return the genotype size.
	 * 
	 * @return the size
	 */
	public int size() {
		return genes.size();
	}

	/**
	 * Render current individual gene
	 */
	@Override
	public String toString() {

		String strGenes = "";

		for (int i = 0; i < genes.size(); i++) {
			strGenes += genes.get(i);
		}

		return strGenes;
	}
}
