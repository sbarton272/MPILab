package config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kmeans.Data;
import kmeans.Mean;
import xypt.MeanXYPt;
import xypt.XYPt;
import dna.DNA;
import dna.MeanDNA;

public class ConfigLoader implements DataLoader {

	// Parse strings
	private final String STR_JOB_NAME = "JOB_NAME";
	private final String STR_KMEANS_TYPE = "KMEANS_TYPE";
	private final String STR_KMEANS_TYPE_PAR = "parallel";
	private final String STR_N_CLUSTERS = "N_CLUSTERS";
	private final String STR_DATA_TYPE = "DATA_TYPE";
	private final String STR_DATA_TYPE_XY = "XY";
	private final String STR_DATA_TYPE_DNA = "DNA";
	private final String STR_TERM = "TEMINATION";
	private final String STR_INPUT_FILE = "INPUT_FILE";
	private final String STR_OUTPUT_FILE = "OUTPUT_FILE";
	private final String STR_PARTICIPANT = "PARTICIPANT";
	private final String STR_PORT_DELIM = ":";
	private final String STR_KEY_DELIM = "=";
	private final String STR_CSV_DELIM = ",";

	// Config variables
	private String jobname = "";
	private boolean isParallel = false;
	private int nClusters = 2;
	private Mean[] means = null;
	private List<Data> data = null;
	private DataType dataType = DataType.UNKNOWN;
	private double termination = 2;
	private File inputFile = null;
	private File outputFile = null;
	private final List<ParticipantDetails> participants = new ArrayList<ParticipantDetails>();
	private final HashMap<String,String> userConfig = new HashMap<String,String>();

	public ConfigLoader(String filePath) throws IOException {
		File configFile = new File(filePath);
		BufferedReader reader = new BufferedReader(new FileReader(configFile));

		// Parse line by line
		String line;
		while ((line = reader.readLine()) != null) {
			String[] split = split2(line, STR_KEY_DELIM);
			setKeyVal(split[0], split[1]);
		}
		reader.close();

		loadDataAndMeans();

	}

	//------------------------------------

	private void setKeyVal(String key, String val) throws IOException {
		String[] hostPort;

		switch(key) {
		case STR_JOB_NAME:
			jobname = val;
			break;
		case STR_KMEANS_TYPE:
			// Use sequential as default, set parallel only is specified
			if (val.toLowerCase().equals(STR_KMEANS_TYPE_PAR)) {
				isParallel = true;
			}
			break;
		case STR_N_CLUSTERS:
			nClusters = Integer.parseInt(val);
			break;
		case STR_DATA_TYPE:
			if (val.equals(STR_DATA_TYPE_DNA)) {
				dataType = DataType.DNA;
			} else if (val.equals(STR_DATA_TYPE_XY)) {
				dataType = DataType.PT;
			}
			break;
		case STR_TERM:
			termination = Double.parseDouble(val);
			break;
		case STR_INPUT_FILE:
			inputFile = new File(val);

			// Check that input file exists
			if (!inputFile.exists()) {
				throw(new IOException("Loading config: input file does not exist"));
			}
			break;
		case STR_OUTPUT_FILE:
			outputFile = new File(val);
			break;
		case STR_PARTICIPANT:
			hostPort = split2(val, STR_PORT_DELIM);
			participants.add(new ParticipantDetails(hostPort[0], hostPort[1]));
			break;
		default:
			// Store all unrecognized keys to a list of KV pairs
			userConfig.put(key, key);
			break;
		}

	}

	//------------------------------------

	private String[] split2(String val, String delim) throws IOException {
		String[] split = val.split(delim);
		if (split.length != 2) {
			throw(new IOException("Loading config: line must contain two values split by " + delim));
		}
		return split;
	}

	//------------------------------------

	private void loadDataAndMeans() throws IOException {
		if (dataType.equals(DataType.UNKNOWN)) {
			throw(new IOException("Loading config: must specify data type"));
		}

		// Init data
		data = new ArrayList<Data>();

		// Read in data files
		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		String line;
		while ((line = reader.readLine()) != null) {

			// Some lines are empty so ignore
			if (line.length() > 0) {

				// Decide datatype to create
				if (dataType.equals(DataType.DNA)) {
					data.add(new DNA(line));
				} else if (dataType.equals(DataType.PT)) {
					String[] pt = split2(line, STR_CSV_DELIM);
					double x = Double.parseDouble(pt[0]);
					double y = Double.parseDouble(pt[1]);
					data.add(new XYPt(x,y));
				}
			}
		}
		reader.close();

		// Generate correct means
		if (dataType.equals(DataType.DNA)) {
			generateDnaMeans();
		} else if (dataType.equals(DataType.PT)) {
			generateXYPtMeans();
		}

	}

	private void generateXYPtMeans() {

		// Generate new means based on the type
		means = new Mean[nClusters];

		// Find range of data
		XYPt pt = (XYPt)data.get(0);
		double minX = pt.getX();
		double maxX = minX;
		double minY = pt.getY();
		double maxY = minY;
		for(Data d : data) {
			pt = (XYPt)d;
			if (pt.getX() < minX) {minX = pt.getX();}
			if (pt.getX() > maxX) {maxX = pt.getX();}
			if (pt.getY() < minY) {minY = pt.getY();}
			if (pt.getY() > maxY) {maxY = pt.getY();}
		}

		// Generate random means in data range
		for(int i=0; i < means.length; i++ ) {
			means[i] = new MeanXYPt(minX, maxX, minY, maxY);
		}

	}

	private void generateDnaMeans() {

		// Generate new means based on the type
		means = new Mean[nClusters];

		// Get length of DNA strands, assume that all are same len
		int len = ((DNA)data.get(0)).length();

		// Generate random means
		for(int i=0; i < means.length; i++ ) {
			means[i] = new MeanDNA(len);
		}

	}

	//------------------------------------

	public String getJobname() {
		return jobname;
	}

	public boolean getIsParallel() {
		return isParallel;
	}

	public Mean[] getMeans() {
		return means;
	}

	@Override
	public List<Data> getData() {
		return data;
	}

	public double getTermination() {
		return termination;
	}

	public File getInputFile() {
		return inputFile;
	}

	public File getOutputFile() {
		return outputFile;
	}

	public List<ParticipantDetails> getParticipants() {
		return participants;
	}

	public HashMap<String,String> getUserConfig() {
		return userConfig;
	}

	//-------------------------------------

	enum DataType {
		DNA, PT, UNKNOWN
	}
}
