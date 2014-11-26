package xypt;

import java.util.List;

import kmeans.Data;
import kmeans.Mean;

public class MeanXYPt extends XYPt implements Mean {

	private static final long serialVersionUID = -6682291068495074609L;
	private double sumX;
	private double sumY;
	private int numPts;

	public MeanXYPt(double xMin, double xMax, double yMin, double yMax) {
		super(xMin, xMax, yMin, yMax);
		// Update running sums on mean location
		sumX = getX();
		sumY = getY();
		numPts = 1;
	}

	@Override
	public void addToMean(Data d) {

		// Must be same type
		if (!(d instanceof XYPt)) {
			return;
		}

		XYPt pt = (XYPt)d;

		// Update count and X, Y
		sumX += pt.x;
		sumY += pt.y;
		numPts += 1;

		// Update underlying mean
		x = sumX / numPts;
		y = sumY / numPts;

	}

	@Override
	public void resetMean(List<Data> data) {

		// Restart counts
		sumX = 0;
		sumY = 0;
		numPts = 0;

		for(Data d : data) {

			// Must be same type
			if (!(d instanceof XYPt)) {
				return;
			}

			XYPt pt = (XYPt)d;

			// Update count and X, Y
			sumX += pt.x;
			sumY += pt.y;
			numPts += 1;
		}

		// Update underlying mean
		x = sumX / numPts;
		y = sumY / numPts;
	}

}
