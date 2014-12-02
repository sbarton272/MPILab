package kmeans;

public interface MasterKmeans {

	/**
	 * Updates current means as well as getting distances
	 * @return converge boolean
	 */
	public boolean updateMeans(Mean[] newMeans);

}
