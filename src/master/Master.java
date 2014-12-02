package master;

import java.util.Scanner;

public class Master {

	//TODO Read command line config file
	//TODO Run seq and parallel forms

	public static void main(String[] args) {

		// Useful startup messages
		System.out.println("Welcome to K-Means :)");

		//constantly accept commands from the command line
		final Scanner scanner = new Scanner(System.in);
		Thread commandThread = new comLineParser(scanner);
		commandThread.start();
	}

}

//-------------------------------------------------------

class comLineParser extends Thread {

	final Scanner scanner;

	public comLineParser(final Scanner scanner) {
		this.scanner = scanner;
	}

	@Override
	public void run() {
		int pid = 1;

		System.out.println("Enter a command: ");
		while (true) {
			final String command = scanner.nextLine();

			final int threadPid = pid;
			pid++;

			//handle a given command
			Thread handleThread = new Thread(new Runnable() {
				@Override
				public void run() {
					//TODO handle command
				}
			});
			handleThread.start();
		}
	}

}