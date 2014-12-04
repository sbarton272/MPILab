package config;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import kmeans.Data;
import kmeans.Mean;

public interface ConfigLoader {

	public String getJobname();

	public boolean getIsParallel();

	public Mean[] getMeans();

	public List<Data> getData();

	public double getTermination();

	public File getOutputFile();

	public HashMap<String,String> getUserConfig();

}
