package openmpi;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import config.ConfigLoader;
import config.ConfigLoaderImpl;
import kmeans.Data;
import kmeans.Kmeans;
import kmeans.Mean;
import kmeans.SeqKmeans;
import mpi.*;

public class OpenMPIKmeans {

	public static void main(String[] args) throws MPIException,IOException,ClassNotFoundException,InterruptedException{
		MPI.Init(args);
		
		if(args.length != 1){
			System.out.println("You must pass the config file location as an argument.");
			return;
		}
		
		String where = MPI.Get_processor_name();
		int myrank = MPI.COMM_WORLD.Rank();
		
		//if this is the master
		if(myrank == 0) {
			//Send takes: (array, offset(0), size of array, type of object in array, rank of recipient, message number)
			//Send proper length and kmeans object to each participant, put in loop until threshold met?
			ConfigLoader configLoader = new ConfigLoaderImpl(args[0]);
			
			if(!configLoader.getIsParallel()){
				//run sequential version
				SeqKmeans kmeans = new Kmeans(configLoader.getData(), configLoader.getMeans(), configLoader.getTermination());
				try {
					saveResults(kmeans.run(), configLoader.getOutputFile());
				} catch (IOException e) {
					System.err.println("Unable to save results");
					e.printStackTrace();
				}
				return;
			}
			
			Kmeans masterKMeans = new Kmeans(configLoader.getData(), configLoader.getMeans(), configLoader.getTermination());
			Mean[] currentMeans = configLoader.getMeans();
			
			//Partition data amongst participant indices
			Map<Integer, List<Data>> dataByIdx = new HashMap<Integer, List<Data>>();
			List<Data> allData = configLoader.getData();
			for(int i = 0; i < allData.size(); i++){
				Data data = allData.get(i);
				List<Data> points;
				if(dataByIdx.containsKey(i%(MPI.COMM_WORLD.Size()-1))){
					points = dataByIdx.get(i%(MPI.COMM_WORLD.Size()-1));
				}
				else{
					points = new ArrayList<Data>();
				}
				points.add(data);
				dataByIdx.put(i%(MPI.COMM_WORLD.Size()-1), points);
			}
			
			//Create kMeans for each participant and send to each
			for (int j = 0; j < MPI.COMM_WORLD.Size()-1; j++){
				Kmeans kmeans = new Kmeans(dataByIdx.get(j), configLoader.getMeans(), configLoader.getTermination());
				byte[] kmeansBytes = objAsBytes(kmeans);
				int[] kmeansLen = new int[1];
				kmeansLen[0] = kmeansBytes.length;
				
				MPI.COMM_WORLD.Send(kmeansLen, 0, 1, MPI.INT, j+1, (j+1+j));
				MPI.COMM_WORLD.Send(kmeansBytes, 0, kmeansBytes.length, MPI.BYTE, j+1, (j+2+j));
			}
			System.out.println((MPI.COMM_WORLD.Size()-1)+" Kmeans partitions distributed to participants.");
			
			int messageNum = ((MPI.COMM_WORLD.Size()-1)*2)+1;
			boolean finished = false;
			while(!finished){
				//run kMeans using participants
				Mean[][] meansFromParts = new Mean[MPI.COMM_WORLD.Size()-1][configLoader.getMeans().length];
				for(int i = 1; i < MPI.COMM_WORLD.Size(); i++){
					byte[] currMeansBytes = objAsBytes(currentMeans);
					int[] currMeansLen = new int[1];
					currMeansLen[0] = currMeansBytes.length;
					
					//send current means to participant
					MPI.COMM_WORLD.Send(currMeansLen, 0, 1, MPI.INT, i, messageNum);
					MPI.COMM_WORLD.Send(currMeansBytes, 0, currMeansBytes.length, MPI.BYTE, i, messageNum+1);
					System.out.println("Master sent current means to participant "+i);
					
					//receive results from participant
					int[] numBytes = new int[1];
					MPI.COMM_WORLD.Recv(numBytes, 0, 1, MPI.INT, i, messageNum+2);
					byte[] meanBytes = new byte[numBytes[0]];
					MPI.COMM_WORLD.Recv(meanBytes, 0, numBytes[0], MPI.BYTE, i, messageNum+3);
					System.out.println("Master got partial new means from participant "+i);
					Mean[] meansBack = (Mean[])bytesAsObj(meanBytes);
					meansFromParts[i-1] = meansBack;
					messageNum += 4;
				}
				//update means
				Mean[] newMeans = new Mean[configLoader.getMeans().length];
				for (int i = 0; i < configLoader.getMeans().length; i++){
					newMeans[i] = null;
				}
				for(Mean[] partMeans : meansFromParts){
					for(int j = 0; j < configLoader.getMeans().length; j++){
						if(newMeans[j] == null){
							newMeans[j] = partMeans[j];
						}
						else{
							Mean temp = newMeans[j];
							temp.addMean(partMeans[j]);
							newMeans[j] = temp;
						}
					}
				}
				finished = masterKMeans.updateMeans(newMeans);
				System.out.println("Master updated means");
				currentMeans = newMeans;
				if(finished){
					//threshold met, signal participants to stop listening and return new means
					for(int i = 0; i < MPI.COMM_WORLD.Size()-1; i++){
						int[] end = new int[1];
						end[0] = 1;
						MPI.COMM_WORLD.Send(end, 0, 1, MPI.INT, i+1, messageNum);
						System.out.println("Master instructed participant "+i+" to terminate; kmeans is complete.");
						messageNum++;
					}
					saveResults(newMeans, configLoader.getOutputFile());
					System.out.println("KMeans finished! Results saved to: " + configLoader.getOutputFile().getName());
					break;
				}
				else{
					//tell participants to keep listening
					for(int i = 0; i < MPI.COMM_WORLD.Size()-1; i++){
						int[] cont = new int[1];
						cont[0] = 0;
						MPI.COMM_WORLD.Send(cont, 0, 1, MPI.INT, i+1, messageNum);
						System.out.println("Master instructed participant "+i+" to run another iteration; kmeans is not yet complete.");
						messageNum++;
					}
				}
			}
		}
		//if this is a participant
		else {
			//Receive takes: (array to fill, offset(0), size of array, type of object in array, rank of sender, message number)
			//accept messages in a loop until threshold met, then exit loop
			System.out.println("Participant "+myrank+" is "+where);
			int[] numBytes = new int[1];
			MPI.COMM_WORLD.Recv(numBytes, 0, 1, MPI.INT, 0, (2*myrank)-1);
			byte[] testBytes = new byte[numBytes[0]];
			MPI.COMM_WORLD.Recv(testBytes, 0, numBytes[0], MPI.BYTE, 0, (2*myrank));
			System.out.println("Participant "+myrank+" received Kmeans partition from master.");
			Kmeans kmeans = (Kmeans)bytesAsObj(testBytes);

			int messNum = ((MPI.COMM_WORLD.Size()-1)*2)+1+((myrank-1)*4);
			boolean finished = false;
			while(!finished){
				numBytes = new int[1];
				MPI.COMM_WORLD.Recv(numBytes, 0, 1, MPI.INT, 0, messNum);
				messNum++;
				byte[] meansBytes = new byte[numBytes[0]];
				MPI.COMM_WORLD.Recv(meansBytes, 0, numBytes[0], MPI.BYTE, 0, messNum);
				System.out.println("Participant "+myrank+" received current means from master.");
				messNum++;
				Mean[] oldMeans = (Mean[])bytesAsObj(meansBytes);
			
				//calculate new means for appropriate points, send back result
				Mean[] recalculated = kmeans.calcNewMeans(oldMeans);
				byte[] newMeansBytes = objAsBytes(recalculated);
				int[] meansLen = new int[1];
				meansLen[0] = newMeansBytes.length;
				MPI.COMM_WORLD.Send(meansLen, 0, 1, MPI.INT, 0, messNum);
				messNum++;
				MPI.COMM_WORLD.Send(newMeansBytes, 0, newMeansBytes.length, MPI.BYTE, 0, messNum);
				System.out.println("Participant "+myrank+" sent partial new means to master.");
				messNum += (4*(MPI.COMM_WORLD.Size()-1-myrank))+myrank;
				
				//receive whether or not to continue
				int[] end = new int[1];
				MPI.COMM_WORLD.Recv(end, 0, 1, MPI.INT, 0, messNum);
				messNum += MPI.COMM_WORLD.Size()-myrank+(4*(myrank-1));
				if(end[0] == 1){
					//finished, exit
					finished = true;
					System.out.println("Participant "+myrank+" instructed to terminate; kmeans is complete.");
					break;
				}
				System.out.println("Participant "+myrank+" instructed to run another iteration; kmeans is not yet complete.");
			}
		}
		MPI.Finalize();
	}
	
	private static byte[] objAsBytes(Object obj) throws IOException {
	    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
	    ObjectOutputStream out = new ObjectOutputStream(byteOut);
	    out.writeObject(obj);
	    return byteOut.toByteArray();
	}
	
	private static Object bytesAsObj(byte[] data) throws IOException, ClassNotFoundException {
	    ByteArrayInputStream byteIn = new ByteArrayInputStream(data);
	    ObjectInputStream in = new ObjectInputStream(byteIn);
	    return in.readObject();
	}
	
	private static void saveResults(Mean[] results, File outFile) throws IOException {
		FileOutputStream fos = new FileOutputStream(outFile);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));

		for (Mean m : results) {
			writer.write(m.toString());
			writer.newLine();
		}

		writer.close();
	}

}
