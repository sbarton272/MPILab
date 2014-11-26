package dna;

import java.util.Arrays;
import java.util.List;

import kmeans.Data;
import kmeans.Mean;

public class MeanDNA extends DNA implements Mean {

	private static final long serialVersionUID = -2420680066988477691L;
	private final BaseCount[] meanCounts;

	/**
	 * Create new random mean
	 * @param len
	 */
	public MeanDNA(int len) {
		super(len);
		meanCounts = BaseCount.dna2Count(dna);
	}

	//----------------------------------------

	@Override
	public void addToMean(Data d) {

		// Must be same type
		if (!(d instanceof DNA)) {
			return;
		}

		// Must have same length DNA
		DNA dna = (DNA) d;
		if (dna.length() != this.length()) {
			return;
		}

		// Add to counts
		for (int i=0; i < meanCounts.length; i++) {
			meanCounts[i].addBase(dna.dna[i]);
		}

		// Update mean representation based off of counts
		calculateMean();

	}

	@Override
	public void resetMean(List<Data> data) {

		// Reset counts
		for(int i=0; i < meanCounts.length; i++) {
			meanCounts[i].resetCount();
		}

		// Add new data to mean
		for(int i=0; i < data.size(); i++) {
			addToMean(data.get(i));
		}
	}

	private void calculateMean() {

		// Update dna with the highest counted base
		for (int i=0; i < meanCounts.length; i++) {
			dna[i] = meanCounts[i].getBase();
		}
	}

}

//---------------------------------------------

class BaseCount {

	private static final int[] START_COUNT = {0,0,0,0};
	private final int[] counts = START_COUNT;

	public void addBase(char b) {
		int i = Arrays.asList(DNA.BASES).indexOf(b);
		if (i >= 0) {
			counts[i]++;
		}
	}

	public char getBase() {

		// TODO renorm counts if get too large

		// Get base with max count
		char base = 0;
		int maxCount = 0;
		for(int i=0; i < counts.length; i++) {
			if (counts[i] >= maxCount) {
				base = DNA.BASES[i];
				maxCount = counts[i];
			}
		}
		return base;
	}

	public static BaseCount[] dna2Count(char[] dna) {
		BaseCount[] counts = new BaseCount[dna.length];
		for(int i=0; i < dna.length; i++) {
			counts[i].addBase(dna[i]);
		}
		return counts;
	}

	public void resetCount() {
		for(int i=0; i < counts.length; i++) {
			counts[i] = START_COUNT[i];
		}
	}

}
