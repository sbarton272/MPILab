package kmeans;

import java.util.List;

public interface Mean extends Data {

	/**
	 * Add element d to the mean
	 * @param d
	 */
	public void addToMean(Data d);

	/**
	 * Add mean m to the mean
	 * @param m
	 */
	public void addMean(Mean m);

	/**
	 * Generate a new mean from the given list of data
	 * @return
	 */
	public Mean newMean(List<Data> data);

	/**
	 * Creates new empty mean. Unto itself the empty mean is an invalid point
	 * @return
	 */
	public Mean newEmpMean();

	/**
	 * Reset the mean location using data
	 * @param data
	 */
	public void resetMean(List<Data> data);

	/**
	 * Reset the mean to empty
	 */
	public void resetMean();

}
