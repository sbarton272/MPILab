package kmeans;

import java.util.List;

public abstract class Kmeans <T> {

	private List<T> pts;

	public Kmeans(List<T> pts, int k, float convThresh) {

	}

	public runKMeans() {
		// Run all
	}

	public initMeans() {

	}

	public boolean updateMeans(newMeanMap);

	public newMeanMap cluster(List<T> means) {
		// Called by participant
	}

	private T findClosestMean(x) {

	}

	private float calcDist(T x, T mean);

	private boolean converged(){

	}

	public List<T> getMeans() {

	}



}
