package test;

import java.util.ArrayList;
import java.util.List;

import kmeans.Data;
import kmeans.Kmeans;
import kmeans.Mean;
import kmeans.SeqKmeans;
import dna.DNA;
import dna.MeanDNA;

public class testKmeans {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		int DNA_LEN = 10;
		String DNA_SEQ = "GGACACTGGG";

		Data d1 = new DNA(DNA_SEQ);
		Data d2 = new DNA(DNA_SEQ);
		Data d3 = new DNA(DNA_SEQ);

		List<Data> pts = new ArrayList<Data>();
		pts.add(d1);
		pts.add(d2);
		pts.add(d3);

		Mean m = new MeanDNA(DNA_LEN);

		Mean[] means = new Mean[1];
		means[0] = m;

		SeqKmeans kmeans = new Kmeans(pts, means, .5);

		Mean[] res = kmeans.run();

		System.out.println(m);
		System.out.println(res[0]);
	}

}
