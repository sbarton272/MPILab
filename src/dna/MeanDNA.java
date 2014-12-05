package dna;

import java.io.Serializable;
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

	public MeanDNA(DNA dna) {
		super(dna.dna);
		meanCounts = BaseCount.dna2Count(this.dna);
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
		updateMean();

	}

	@Override
	public void addMean(Mean m) {

		// Must be same type
		if (!(m instanceof MeanDNA)) {
			return;
		}

		// Must have same length DNA
		MeanDNA dna = (MeanDNA) m;
		if (dna.length() != this.length()) {
			return;
		}

		// Add to counts
		for (int i=0; i < meanCounts.length; i++) {
			meanCounts[i].addCounts(dna.meanCounts[i]);
		}

		// Update mean representation based off of counts
		updateMean();

	}

	private void updateMean() {

		// Update dna with the highest counted base
		for (int i=0; i < meanCounts.length; i++) {
			dna[i] = meanCounts[i].getBase();
		}
	}

	//----------------------------------------

	@Override
	public Mean newMean(List<Data> data) {
		if (data.size() < 1) {
			return null;
		}

		// Data must be right type
		Data d0 = data.get(0);
		if (!(d0 instanceof DNA)) {
			return null;
		}

		// Get size of DNA strands
		int dnaLen = ((DNA)d0).length();
		Mean mean = new MeanDNA(dnaLen);

		// Update mean with new data
		mean.resetMean(data);
		return mean;
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

	@Override
	public void resetMean() {
		// Reset counts
		for(int i=0; i < meanCounts.length; i++) {
			meanCounts[i].resetCount();
		}
	}

	@Override
	public Mean newEmpMean() {
		MeanDNA mean = new MeanDNA(this.length());
		mean.resetMean();
		return mean;
	}

}

//---------------------------------------------

class BaseCount implements Serializable{

	private static final long serialVersionUID = 1L;
	private final int[] counts;

	public BaseCount() {
		counts = new int[DNA.BASES.length];
		for(int i=0; i < counts.length; i++) {
			counts[i] = 0;
		}
	}

	public void addCounts(BaseCount baseCount) {
		// Add all counts to base
		for(int i=0; i < counts.length; i++) {
			counts[i] += baseCount.counts[i];
		}
	}

	public void addBase(char b) {
		int i = new String(DNA.BASES).indexOf(b);
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
			counts[i] = new BaseCount();
			counts[i].addBase(dna[i]);
		}
		return counts;
	}

	public void resetCount() {
		for(int i=0; i < counts.length; i++) {
			counts[i] = 0;
		}
	}

	@Override
	public String toString() {
		return counts.toString();
	}

}
