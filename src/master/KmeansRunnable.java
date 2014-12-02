package master;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import kmeans.Kmeans;
import kmeans.Mean;
import kmeans.SeqKmeans;
import config.ConfigLoader;
import config.ConfigLoaderImpl;

public class KmeansRunnable extends Thread implements Runnable {

	final private int pid;
	final String configFile;
	final ConfigLoader config;

	public KmeansRunnable(int pid, String configFile) throws IOException {
		this.pid = pid;
		this.configFile = configFile;
		this.config = new ConfigLoaderImpl(configFile);
	}

	@Override
	public void run() {

		if (config.getIsParallel()) {

			// Parallel
			System.out.println("Parallel");
			// TODO

		} else {

			// Sequential: init kmeans, run, save output
			SeqKmeans kmeans = new Kmeans(config.getData(), config.getMeans(), config.getTermination());
			try {
				saveResults(kmeans.run(), config.getOutputFile());
			} catch (IOException e) {
				System.err.println("Unable to save results");
				e.printStackTrace();
			}
		}

		System.out.println("[" + Integer.toString(pid) + "] Completed k-means. Results in " +
				config.getOutputFile().getName());

	}

	//----------------------------------------------------

	private void saveResults(Mean[] results, File outFile) throws IOException {
		FileOutputStream fos = new FileOutputStream(outFile);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));

		for (Mean m : results) {
			writer.write(m.toString());
			writer.newLine();
		}

		writer.close();
	}

}
