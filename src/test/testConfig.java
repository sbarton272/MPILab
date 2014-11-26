package test;

import java.io.IOException;
import java.util.Arrays;

import config.ConfigLoader;

public class testConfig {

	public static void main(String[] args) throws IOException {

		ConfigLoader cl = new ConfigLoader("test/test.config");
		System.out.println(cl.getJobname());
		System.out.println(cl.getTermination());
		System.out.println(cl.getParticipants());
		System.out.println(cl.getData());
		System.out.println(Arrays.toString(cl.getMeans()));

		cl = new ConfigLoader("test/test2.config");
		System.out.println(cl.getJobname());
		System.out.println(cl.getTermination());
		System.out.println(cl.getParticipants());
		System.out.println(cl.getData());
		System.out.println(Arrays.toString(cl.getMeans()));

	}

}
