/**
 * Created by jit_biswas on 2/1/2018.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.ArrayList;

public class ProcessManagement {

    //set the working directory
    private static File currentDirectory = new File("");
    //set the instructions file
    private static File instructionSet = new File("graph-file2");
    public static Boolean allExecuted = false;
    public static ArrayList<Integer> executedNodes = new ArrayList<Integer> ();
    public static void main(String[] args) throws InterruptedException {
        int count = 0;
        Boolean allExecuted = false;
        //parse the instruction file and construct a data structure, stored inside ProcessGraph class
        ParseFile.generateGraph(new File(currentDirectory.getAbsolutePath() + "/"+instructionSet));

        // Print the graph information
        // WRITE YOUR CODE
        ProcessGraph.printGraph();

        // Using index of ProcessGraph, loop through each ProcessGraphNode, to check whether it is ready to run        
        
        while (allExecuted == false) {
            for (ProcessGraphNode node : ProcessGraph.nodes) {
                // check if all the nodes are executed
                // WRITE YOUR CODE
                System.out.println("Checking..." + node.getNodeId());
                if (node.isExecuted() && !executedNodes.contains(node.getNodeId())) {
                    count++;
                }
                //mark all the runnable nodes
                // WRITE YOUR CODE

                //run the node if it is runnable
                // WRITE YOUR CODE
                if (node.isRunnable() && node.allParentsExecuted() && !node.isRunning()){
                    node.setRunning();
                    ProcessThread processThread = new ProcessThread(node);
                    processThread.run();
                    System.out.println(node.getNodeId() + " is runnning");
                }

                if (count == ProcessGraph.nodes.size()) {
                    allExecuted = true;
                }
            }
        }
        System.out.println("All process finished successfully");
    }

}

class ProcessThread extends Thread {
    private ProcessGraphNode processThread;
    ProcessThread(ProcessGraphNode node) {
        processThread = node;
    }
    public void run() {
        // called by object.start()
        String commandLine = processThread.getCommand();
        ArrayList<String> tokencmd= new ArrayList<String>();
        tokencmd.addAll(Arrays.asList(commandLine.trim().split(" ")));
        File currentDir = new File(System.getProperty("user.dir"));
        ProcessBuilder pBuilder = new ProcessBuilder(tokencmd);
        pBuilder.directory(currentDir);
        if (!processThread.getInputFile().toString().equals("stdin")) {
            pBuilder.redirectInput(processThread.getInputFile());
        }
        if (!processThread.getOutputFile().toString().equals("stdout")) {
            pBuilder.redirectOutput(processThread.getOutputFile());
        }
        try{
            Process process = pBuilder.start();
            BufferedReader br = new BufferedReader(new BufferedReader(new InputStreamReader(process.getInputStream())));
            for (String line; (line = br.readLine()) != null;){
                System.out.println(line);
            }
            br.close();
            processThread.setExecuted();
        }
            // catch the IOexception and resume waiting for commands
        catch (IOException ex){
            System.out.println(ex);
        }
    }
}