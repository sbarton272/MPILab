package test;

import java.util.ArrayList;
import java.util.List;

import kmeans.Data;
import kmeans.Mean;
import xypt.MeanXYPt;
import xypt.XYPt;
import dna.DNA;
import dna.MeanDNA;

public class testMeans {

	public static void main(String[] args) {

		int DNA_LEN = 10;
		String DNA_SEQ = "GGACACTGGG";

		Mean d0 = new MeanDNA(DNA_LEN);
		System.out.println(d0);

		Data d1 = new DNA(DNA_LEN);
		Data d2 = new DNA(DNA_SEQ);
		Data d3 = new DNA(DNA_SEQ);

		System.out.println(d1);
		System.out.println(d2);
		System.out.println(d3);
		System.out.println(d1.distance(d2));
		System.out.println(d3.distance(d2));

		Mean m = new MeanDNA((DNA)d2);
		m.addToMean(d3);
		System.out.println(m.distance(d3));
		List<Data> l = new ArrayList<Data>();
		l.add(d1);
		l.add(d1);
		m.resetMean(l);
		Mean m2 = m.newMean(l);
		System.out.println(m.distance(d1));
		System.out.println(m.distance(m2));

		Mean p0 = new MeanXYPt(3,5,5,10);
		System.out.println(p0);

		Data p1 = new XYPt(3,3);
		Data p2 = new XYPt(3,4);
		System.out.println(p1);
		System.out.println(p2);
		System.out.println(p1.distance(p2));

		p0.addToMean(p2);
		p0.addToMean(p2);
		p0.addToMean(p2);
		p0.addToMean(p2);
		System.out.println(p0);
		System.out.println(p0.distance(p2));
		p0.resetMean(null);
		System.out.println(p0);
		l.clear();
		l.add(p1);
		p0.resetMean(l);
		Mean m3 = p0.newMean(l);
		System.out.println(p0.distance(m3));



	}
}
