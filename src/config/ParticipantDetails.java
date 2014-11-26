package config;

public class ParticipantDetails {

	private final String hostName;
	private final int port;

	public ParticipantDetails(String hostName, int port) {
		this.hostName = hostName;
		this.port = port;
	}

	public ParticipantDetails(String hostName, String port) {
		this(hostName, Integer.parseInt(port));
	}

	public int getPort() {
		return port;
	}

	public String getHostName() {
		return hostName;
	}

}
