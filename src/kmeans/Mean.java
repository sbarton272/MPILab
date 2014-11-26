package kmeans;

import java.util.List;

public interface Mean extends Data {

	/**
	 * Add element d to the mean
	 * @param d
	 */
	public void addToMean(Data d);

	/**
	 * Generate a new mean from the given list of data
	 * @return
	 */
	public void resetMean(List<Data> data);

}