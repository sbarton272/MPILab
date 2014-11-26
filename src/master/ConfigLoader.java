package master;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;

public class ConfigLoader {

	// Parse strings
	private final String STR_JOB_NAME = "JOB_NAME";
	private final String STR_KMEANS_TYPE = "KMEANS_TYPE";
	private final String STR_KMEANS_TYPE_SEQ = "sequential";
	private final String STR_KMEANS_TYPE_PAR = "parallel";
	private final String STR_N_CLUSTERS = "N_CLUSTERS";
	private final String STR_DATA_TYPE = "DATA_TYPE";
	private final String STR_DATA_TYPE_XY = "XY";
	private final String STR_DATA_TYPE_DNA = "DNA";
	private final String STR_DATA_TYPE_TERM = "TEMINATION";
	private final String STR_INPUT_FILE = "INPUT_FILE";
	private final String STR_OUTPUT_FILE = "OUTPUT_FILE";
	private final String STR_MASTER_PORT = "MASTER_PORT";
	private final String STR_DATA_TYPE_PARTICIPANT = "PARTICIPANT";
	private final String STR_DELIM = ":";

	// Config variables
	private String jobname = "";
	private final boolean isParallel = false;
	private final int nClusters = 2;
	private Class isXY // TODO left off here, how to load Kmeans?
	private File inputFile = null;
	private File outputFile = null;
	private int masterPort = 9042;
	private final List<ParticipantDetails> participants = new ArrayList<ParticipantDetails>();
	private final HashMap<String,String> userConfig = new HashMap<String,String>();
	private ParticipantDetails lastParticipantRecorded;
	private String fileServerDir;
	private int fileServerPort;

	public ConfigLoader(String filePath) throws IOException {
		File configFile = new File(filePath);
		BufferedReader reader = new BufferedReader(new FileReader(configFile));
		String line;
		while ((line = reader.readLine()) != null) {
			String[] split = line.split(STR_KEY_DELIM);
			if (split.length != 2) {
				reader.close();
				throw(new IOException("Invalid config file, follow KEY=VAL format"));
			}
			setKeyVal(split[0], split[1]);
		}
		reader.close();
	}

	private void setKeyVal(String key, String val) throws IOException {
		String[] hostPort;

		switch(key) {
		case STR_JOB_NAME:
			jobname = val;
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
		case STR_MAP_FN:
			try {
				mapFn = (MapFn) loadClass(val);
			} catch (Exception e) {
				throw(new IOException("Unable to load mapFn"));
			}
			break;
		case STR_MAP_TIMEOUT_SEC:
			mapTimeoutSec = Integer.parseInt(val);
			break;
		case STR_REDUCE_FN:
			try {
				reduceFn = (ReduceFn) loadClass(val);
			} catch (Exception e) {
				throw(new IOException("Unable to load reduceFn"));
			}
			break;
		case STR_REDUCE_TIMEOUT_SEC:
			reduceTimeoutSec = Integer.parseInt(val);
			break;
		case STR_NUM_REDUCERS:
			numReducers = Integer.parseInt(val);
			break;
		case STR_PARITION_SIZE:
			partitionSize = Integer.parseInt(val);
			break;
		case STR_MASTER:
			hostPort = splitHostPort(val);
			masterHostName = hostPort[0];
			masterPort = Integer.parseInt(hostPort[1]);
			break;
		case STR_PARTICIPANT:
			hostPort = splitHostPort(val);
			lastParticipantRecorded = new ParticipantDetails(hostPort[0], hostPort[1]);
			participants.add(lastParticipantRecorded);
			break;
		case STR_FILE_SERVER_DIR:
			fileServerDir = val;
			break;
		case STR_FILE_SERVER_PORT:
			fileServerPort = Integer.parseInt(val);
			break;
		default:
			// Store all unrecognized keys to a list of KV pairs
			userConfig.put(key, key);
			break;
		}

	}

	//------------------------------------

	private String[] splitHostPort(String val) {
		String[] split = val.split(STR_DELIM);
		if (split.length != 2) {
			return null;
		}
		return split;
	}

	private Object loadClass(String val) throws Exception {
		Class<?> c = Class.forName(val);
		Constructor<?> ctor = c.getConstructor();
		return ctor.newInstance();
	}

	//------------------------------------

	public List<ParticipantDetails> getParticipants() {
		return participants;
	}

	public HashMap<String,String> getUserConfig() {
		return userConfig;
	}

	public String getJobname() {
		return jobname;
	}

	public File getInputFile() {
		return inputFile;
	}

	public File getOutputFile() {
		return outputFile;
	}

	public MapFn getMapFn() {
		return mapFn;
	}

	public int getMapTimeoutSec() {
		return mapTimeoutSec;
	}

	public ReduceFn getReduceFn() {
		return reduceFn;
	}

	public int getReduceTimeoutSec() {
		return reduceTimeoutSec;
	}

	public int getNumReducers() {
		return numReducers;
	}

	public int getPartitionSize() {
		return partitionSize;
	}

	public String getMasterHostName() {
		return masterHostName;
	}

	public int getMasterPort() {
		return masterPort;
	}

	public String getFileServerDir() {
		return fileServerDir;
	}

	public int getFileServerPort() {
		return fileServerPort;
	}
}
