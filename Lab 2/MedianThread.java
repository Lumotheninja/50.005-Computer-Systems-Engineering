import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MedianThread {

	public static double computeMedian ( ArrayList<Integer> inputArray) {
	  //TODO: implement your function that computes median of values of an array 
		return (inputArray.get(inputArray.size()/2)+inputArray.get(inputArray.size()/2-1))/2;
	}

	public static void main(String[] args) throws InterruptedException, FileNotFoundException, IOException  {
		

		File file = new File(args[0]);
		String line = null;
		ArrayList<Integer> numArray = new ArrayList<Integer>();
		ArrayList<ArrayList<Integer>> splitArray = new ArrayList<ArrayList<Integer>>();
		ArrayList<MedianMultiThread> threadList = new ArrayList<MedianMultiThread>();
		ArrayList<MergeThread> mergeThreadList = new ArrayList<MergeThread>();
		ArrayList<Integer> finalArray = new ArrayList<Integer>();
		// TODO: read data from external file and store it in an array
	       // Note: you should pass the file as a first command line argument at runtime. 
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
			ArrayList<Integer> subArray = new ArrayList(numArray.subList(x*blockSize,(x+1)*blockSize));
			splitArray.add(subArray);
		}
		ArrayList<Integer> lastSubArray = new ArrayList(numArray.subList(blockSize*NumOfThread,numArray.size()));
		if (!lastSubArray.isEmpty()){
			splitArray.add(lastSubArray);
		} 
	
		// TODO: start recording time
		final long startTime = System.currentTimeMillis();

		// TODO: create N threads and assign subArrays to the threads so that each thread sorts
		    // its repective subarray. For example,
		for (ArrayList<Integer> subArray: splitArray){
			MedianMultiThread thread = new MedianMultiThread(subArray);
			threadList.add(thread);
		}	
		//Tip: you can't create big number of threads in the above way. So, create an array list of threads. 
		
		// TODO: start each thread to execute your sorting algorithm defined under the run() method, for example, 
		for (MedianMultiThread thread: threadList){
			thread.start();
		}

		for (MedianMultiThread thread: threadList){
			thread.join();
		}	
		
		int reductionTime = (int) (Math.log(NumOfThread)/Math.log(2));

		if (reductionTime==0){
			finalArray=threadList.get(0).getInternal();
		} else{
			for (int i=0; i<reductionTime; i++){
				ArrayList<MergeThread> tempList = new ArrayList<MergeThread>();
				if (i==0){
					for (int x=0; x<threadList.size(); x+=2){
						MergeThread newthread = new MergeThread(threadList.get(x).getInternal(),threadList.get(x+1).getInternal());
						tempList.add(newthread);
					}
				}
				else{
					for (int x=0; x<mergeThreadList.size(); x+=2){
						MergeThread newthread = new MergeThread(mergeThreadList.get(x).getInternal(),mergeThreadList.get(x+1).getInternal());
						tempList.add(newthread);
					}
				}
				for (MergeThread thread : tempList){
					thread.start();
				}
				for (MergeThread thread : tempList){
					thread.join();
				}
				
				mergeThreadList=new ArrayList<MergeThread>(tempList);
				tempList.clear();
			}
			finalArray=mergeThreadList.get(0).getInternal();
		}
		// TODO: use any merge algorithm to merge the sorted subarrays and store it to another array, e.g., sortedFullArray. 

		//TODO: get median from sortedFullArray
		
		    //e.g, computeMedian(sortedFullArray);
		// TODO: stop recording time and compute the elapsed time 
		// TODO: printout the final sorted array
		// TODO: printout median

		final long endTime = System.currentTimeMillis();
		final long runningTime = endTime - startTime;
		double median = computeMedian(finalArray);

		System.out.println("The Median value is ..." + median);
		System.out.println("Running time is " + runningTime + " milliseconds\n");
	}
}

// extend Thread
class MedianMultiThread extends Thread {
	private ArrayList<Integer> list;

	MedianMultiThread(ArrayList<Integer> array) {
		list = array;
	}

	public void run() {
		// called by object.start()
		list = mergeSort(list);
	}
	
	// TODO: implement merge sort here, recursive algorithm
	public ArrayList<Integer> mergeSort(ArrayList<Integer> array) {
		if (array.size() > 1){
			int midnum = array.size()/2;
			int i = 0;
			int j = 0;
			ArrayList<Integer> result = new ArrayList<Integer>();
			ArrayList<Integer> leftarray = mergeSort(new ArrayList(array.subList(0,midnum)));
			ArrayList<Integer> rightarray = mergeSort(new ArrayList(array.subList(midnum,array.size())));
			while (i < midnum && j < (array.size() - midnum)){
				if (leftarray.get(i) <= rightarray.get(j)){
					result.add(leftarray.get(i));
					i++;
				} else {
					result.add(rightarray.get(j));
					j++;
				}
			}	
			if (i < midnum){
				result.addAll(new ArrayList(leftarray.subList(i,midnum)));
			}			
			if (j<(array.size() - midnum)){
				result.addAll(new ArrayList(rightarray.subList(j,(array.size()-midnum))));
			}
			i=0;
			j=0;
			return result;
		}
		else{
			return array;
		}
	}

	public ArrayList<Integer> getInternal() {
		return list;
	}

}

class MergeThread extends Thread {
	private ArrayList<Integer> list1;
	private ArrayList<Integer> list2;
	private ArrayList<Integer> result;

	public ArrayList<Integer> getInternal() {
		return result;
	}

	MergeThread(ArrayList<Integer> array1, ArrayList<Integer> array2) {
		list1 = array1;
		list2 = array2;
	}

	public void run() {
		// called by object.start()
		result = merge(list1,list2);
	}
	
	// TODO: implement merge sort here, recursive algorithm
	public ArrayList<Integer> merge(ArrayList<Integer> array1, ArrayList<Integer> array2) {
		int i = 0;
		int j = 0;

		ArrayList<Integer> result = new ArrayList<Integer>();

		while (i < array1.size() && j < array2.size()){
			if (array1.get(i) <= array2.get(j)){
				result.add(array1.get(i));
				i++;
			} else if (array1.get(i) > array2.get(j)){
				result.add(array2.get(j));
				j++;
			}
		}	
		if (i < array1.size()){
			result.addAll(new ArrayList(array1.subList(i,array1.size())));
		}			
		if (j < array2.size()){
			result.addAll(new ArrayList(array2.subList(j,array2.size())));
		}
		i=0;
		j=0;
		return result;
	}
}
