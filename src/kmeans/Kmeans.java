package kmeans;

import java.util.List;

public class Kmeans implements SeqKmeans, MasterKmeans, ParticipantKmeans {

	private final List<Data> pts;
	private Mean[] means;
	private final float convThresh;

	public Kmeans(List<Data> pts, Mean[] means, float convThresh) {
		this.pts = pts;
		this.means = means;
		this.convThresh = convThresh;
	}

	public Kmeans(List<Data> pts, float convThresh) {
		this.pts = pts;
		this.convThresh = convThresh;
	}

	//-----------------------------------------------

	@Override
	public Mean[] run() {
		if (this.means == null) {
			return null;
		}
		means = run(this.means);
		return means;
	}

	public Mean[] run(Mean[] curMeans) {

		// Generate new empty means
		Mean[] newMeans = new Mean[curMeans.length];
		for (int i=0; i < curMeans.length; i++) {
			newMeans[i] = curMeans[i].newEmpMean();
		}

		// Calc new means until convergence
		double meansDist = convThresh;
		while (meansDist >= convThresh) {

			// Calc new means
			for (Data pt : pts) {
				newMeans[findClosestMean(curMeans, pt)].addToMean(pt);
			}

			// Get distance from old means
			meansDist = calcMeansDist(curMeans, newMeans);

			// Update current means
			curMeans = newMeans;

			// Reset newMeans
			for (Mean m : newMeans) {
				m.resetMean();
			}
		}

		return curMeans;
	}

	//-----------------------------------------------

	public Mean[] calcNewMeans(Mean[] curMeans) {

		// Generate new empty means
		Mean[] newMeans = new Mean[curMeans.length];
		for (int i=0; i < curMeans.length; i++) {
			newMeans[i] = curMeans[i].newEmpMean();
		}

		// Calc new means with closest points
		for (Data pt : pts) {
			newMeans[findClosestMean(curMeans, pt)].addToMean(pt);
		}

		return newMeans;
	}

	@Override
	public boolean updateMeans(Mean[] newMeans) {
		double dist = calcMeansDist(means, newMeans);
		means = newMeans;
		return dist < convThresh;
	}

	//-----------------------------------------------

	private double calcMeansDist(Mean[] curMeans, Mean[] newMeans) {
		// Means must have same length
		double dist = 0;

		for (int i=0; i < curMeans.length; i++) {
			dist += curMeans[i].distance(newMeans[i]);
		}

		return dist;
	}

	/**
	 * @return Index of closest mean 0-len(means)
	 */
	private int findClosestMean(Mean[] curMeans, Data pt) {

		int closestMean = -1;
		double closestDist = -1;
		double dist;

		for (int i=0; i < curMeans.length; i++) {
			dist = pt.distance(curMeans[i]);

			// Found closer mean, or first mean
			if ((dist < closestDist) || (closestDist == -1)) {
				closestDist = dist;
				closestMean = i;
			}

		}

		return closestMean;
	}

}
