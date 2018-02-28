import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MeanThread {	
	public static void main(String[] args) throws InterruptedException, FileNotFoundException, IOException {

	// TODO: read data from external file and store it in an array
		       // Note: you should pass the file as a first command line argument at runtime. 
		File file = new File(args[0]);
		String line = null;
		ArrayList<Integer> numArray = new ArrayList<Integer>();
		ArrayList<ArrayList<Integer>> splitArray = new ArrayList<ArrayList<Integer>>();
		ArrayList<MeanMultiThread> threadList = new ArrayList<MeanMultiThread>();
		double globalSum=0;
		double globalMean;
		
		try {
     	    BufferedReader input = new BufferedReader (new FileReader( file ));
     	    line = input.readLine();
     	    input.close();
     	    String[] splited = line.split("\\s+");
     	    for (String integer : splited){
     	    	if (!integer.equals("")) {
     	    		numArray.add(Integer.parseInt(integer));
     	    	}
     	    }

	    } 
	    catch (Exception e) {	        
	        e.printStackTrace();
	    }

		// define number of threads
		int NumOfThread = Integer.valueOf(args[1]);// this way, you can pass number of threads as 
		     // a second command line argument at runtime.
		int blockSize = numArray.size()/NumOfThread;

		// TODO: partition the array list into N subArrays, where N is the number of threads
		for (int x=0; x<NumOfThread; x++){
			ArrayList subArray = new ArrayList(numArray.subList(x*blockSize,(x+1)*blockSize));
			splitArray.add(subArray);
		}
		ArrayList lastSubArray = new ArrayList(numArray.subList(blockSize*NumOfThread,numArray.size()));
		if (!lastSubArray.isEmpty()){
			splitArray.add(lastSubArray);
		} 

		// TODO: start recording time
		final long startTime = System.currentTimeMillis();

		// TODO: create N threads and assign subArrays to the threads so that each thread computes mean of 
		    // its repective subarray. For example,
		for (ArrayList<Integer> subArray: splitArray){
			MeanMultiThread thread = new MeanMultiThread(subArray);
			threadList.add(thread);
		}		
		//Tip: you can't create big number of threads in the above way. So, create an array list of threads. 
		
		// TODO: start each thread to execute your computeMean() function defined under the run() method
		   //so that the N mean values can be computed. for example, 
		for (MeanMultiThread thread: threadList){
			thread.start();
		}

		for (MeanMultiThread thread: threadList){
			thread.join();
		}		

		for (MeanMultiThread thread: threadList){
			globalSum+=thread.getMean();
			System.out.println("Temporal mean value of thread" + threadList.indexOf(thread) + "is ... " + thread.getMean());
		}
		
		globalMean = globalSum/threadList.size();
		// TODO: compute the global mean value from N mean values. 
		
		// TODO: stop recording time and compute the elapsed time 
		final long endTime = System.currentTimeMillis();
		final long runTime = endTime - startTime;

		System.out.println("The global mean value is ... " + globalMean);
		System.out.println("Time taken is ..." + runTime);		
	}
}

class MeanMultiThread extends Thread {
	private ArrayList<Integer> list;
	private double mean;
	MeanMultiThread(ArrayList<Integer> array) {
		list = array;
		double sum = 0;
	}
	public double getMean() {
		return mean;
	}
	public void run() {
		// called by object.start()
		double sum = 0;
		for (int i :list){
			sum+=i;
		}
		mean = sum/list.size();
	}
}

