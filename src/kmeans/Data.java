package kmeans;

import java.io.Serializable;

public interface Data extends Serializable {

	/**
	 * 
	 * @param other
	 * @return NaN if unable to compare distances
	 */
	public double getDistance(Data other);

}
