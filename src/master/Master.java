package master;

import java.io.IOException;
import java.util.Scanner;

public class Master {

	public static void main(String[] args) {

		// Useful startup messages
		System.out.println("Welcome to K-Means :)");

		//constantly accept commands from the command line
		final Scanner scanner = new Scanner(System.in);
		Thread commandThread = new ComLineParser(scanner);
		commandThread.start();
	}

}

//-----------------------------------------------------------

class ComLineParser extends Thread implements Runnable {

	final static String PROMPT = "Enter a command: ";
	final static String HELP_MSG = "Available Commands:\n" +
			"run <config file>";
	final static String RUN_CMD = "run";
	final static String DELIM = " ";

	final Scanner scanner;

	public ComLineParser(final Scanner scanner) {
		this.scanner = scanner;
	}

	@Override
	public void run() {
		int pid = 0;

		while (true) {
			System.out.println(PROMPT);

			final String cmd = scanner.nextLine();

			// Run command
			String[] split = cmd.split(DELIM);
			if ((split.length == 2) && (split[0].equals(RUN_CMD))) {

				// Valid run command so spawn process to run
				String configFile = split[1];

				// Run KMeans
				pid++;
				Thread runKMeans;
				try {
					runKMeans = new KmeansRunnable(pid, configFile);

					System.out.println("[" + Integer.toString(pid) + "] Running k means on " + configFile);

					runKMeans.start();

				} catch (IOException e) {

					System.out.println("Invalid config file");
					e.printStackTrace();
				}

			} else {

				// Invalid input
				System.out.println(HELP_MSG);
			}
		}
	}

}

