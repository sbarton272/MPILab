package xypt;

import java.util.Random;

import kmeans.Data;

public class XYPt implements Data {

	private static final long serialVersionUID = -4193103260528319739L;
	protected double x;
	protected double y;

	/**
	 * Initialize random x,y point
	 */
	public XYPt(double xMin, double xMax, double yMin, double yMax) {
		Random rand = new Random();

		// Generate rand loc and fit to range
		x = rand.nextDouble()*(xMax - xMin) + xMin;
		y = rand.nextDouble()*(yMax - yMin) + yMin;
	}

	public XYPt(double x, double y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public double distance(Data other) {
		if (!(other instanceof XYPt)) {
			return Double.NaN;
		}

		// Calculate euclidean distance
		XYPt p2 = (XYPt)other;
		double x = this.getX() - p2.getX();
		double y = this.y - p2.y;

		return Math.sqrt(x*x + y*y);
	}

	//---------------------------------------------

	@Override
	public String toString() {
		return "<" + Double.toString(x) + "," + Double.toString(y) + ">";
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}
}
