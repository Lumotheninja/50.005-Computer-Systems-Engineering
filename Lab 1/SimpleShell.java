import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class SimpleShell {
	public static void main(String[] args) throws java.io.IOException {
		String commandLine;
		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
		File currentDir = new File(System.getProperty("user.dir"));
		ArrayList<String> pastcmd = new ArrayList<String>();
		int runpast = 0;
		
		
		while (true) {

			//if !! command is run
			if (runpast != 0){
				commandLine = (String) pastcmd.get(runpast);
				
				
			}
			
			// read what the user entered
			else{
				System.out.print("jsh>");
				commandLine = console.readLine();

				// if the user entered a return, just loop again
				if (commandLine.equals("")) {
					continue;
				}
			}
			
			ArrayList<String> tokencmd = new ArrayList<String>(); 
			tokencmd.addAll(Arrays.asList(commandLine.trim().split(" ")));
			for (String tokens: tokencmd){
				if (tokens.equals("") || tokens.equals(" ")){
					tokencmd.remove(tokens);
				}
			}
			
			ProcessBuilder pb = new ProcessBuilder(tokencmd);
			pb.directory(currentDir);
			
			
			try {
				
				
				
				if (tokencmd.get(0).equals("cd")){
					ArrayList<String> tokendir = new ArrayList<String>(); 
					
					//cd wih nothing behind has the same properties as ~
					if (tokencmd.size()==1){
						String homepath = System.getProperty("user.home");
						currentDir = new File(homepath);
					}
					
					else{
						tokendir.addAll(Arrays.asList(tokencmd.get(1).trim().split("/")));
					
						
						for (String tokens: tokendir){
							
							//cd with ~ goes back to homedir
							if (tokens.equals("~") || tokens.equals("")){
								String homepath = System.getProperty("user.home");
								currentDir = new File(homepath);
							}
							
							//cd with .. goes back to parentdir
							else if (tokens.equals("..")){
								currentDir = new File(currentDir.getAbsolutePath()).getParentFile();
							}
							
							//cd to files in currentdir
							else{
								File cdfile = new File(currentDir.getAbsolutePath() + "/" + tokens);
								if (! cdfile.isDirectory()){
									System.out.println("error: new directory is invalid" + currentDir);
									break;
								}
								else{
									currentDir=cdfile;
								}
							}
							System.out.println(currentDir.getAbsolutePath());
						}
					}
				}
				
				else if (tokencmd.get(0).equals("!!")){
					runpast = 1;
					continue;
				}
				
				//print history together with index, !! commands are not included but history commands are
				else if (tokencmd.get(0).equals("history")){
					for (int i=0;i<pastcmd.size();i++){
						System.out.println((i+1) + " " + pastcmd.get(i).toString());
					}
				}
				
				else if (tokencmd.get(0).matches("[0-9]+")){
					if (Integer.parseInt(tokencmd.get(0)) < pastcmd.size()){
						runpast = Integer.parseInt(tokencmd.get(0));
						continue;
					}
					else{
						System.out.println("the integer is out of range");
					}
				}
				
				//other processes
				else{
					Process p = pb.start();
					BufferedReader br = new BufferedReader(new BufferedReader(new InputStreamReader(p.getInputStream())));
					for (String line; (line = br.readLine()) != null;){
						System.out.println(line);
					}
					br.close();
				}
				
				//!! does not add to history
				if (runpast!=0){
					runpast=0;
				}
				else{
					pastcmd.add(0,commandLine);
				}
				
				
			}
			catch (java.io.IOException e){
				System.out.println(e);
				continue;
			}
			
			

		}
	}
}
