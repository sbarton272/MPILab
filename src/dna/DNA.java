package dna;

import java.util.Random;

import kmeans.Data;

public class DNA implements Data {

	public static final char[] BASES = {'C', 'T', 'A', 'G'};

	private static final long serialVersionUID = 1L;
	protected final char[] dna;

	//---------------------------------------------

	/**
	 * Generate a random DNA string of specified length
	 * @param len
	 */
	public DNA(int len) {
		dna = new char[len];

		// Generate bases
		Random rand = new Random();
		for(int i=0; i < len; i++) {
			dna[i] = int2Base(rand.nextInt(BASES.length));
		}
	}

	public DNA(String dna) {
		this.dna = dna.toCharArray();
	}

	public DNA(char[] dna) {
		this.dna = dna;
	}

	//---------------------------------------------

	/**
	 * Distance is the number of different bases
	 */
	@Override
	public double distance(Data other) {

		if (!(other instanceof DNA)) {
			return Double.NaN;
		}

		// Must have same length DNA
		DNA dna = (DNA) other;
		if (dna.length() != this.length()) {
			return Double.NaN;
		}

		// Count number of different bases
		int dist = 0;
		for (int i=0; i < dna.length(); i++){
			if (dna.dna[i] != this.dna[i]) {
				dist++;
			}
		}

		return dist;
	}

	//---------------------------------------------

	@Override
	public String toString() {
		return String.valueOf(dna);
	}

	//---------------------------------------------

	private char int2Base(int i) {
		return BASES[i%BASES.length];
	}

	public int length() {
		return dna.length;
	}

}
