// package Week5;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileOperation {
	private static File currentDirectory = new File(System.getProperty("user.dir"));
	public static void main(String[] args) throws java.io.IOException {

		String commandLine;
		File currentDir = new File(System.getProperty("user.dir"));
		BufferedReader console = new BufferedReader
				(new InputStreamReader(System.in));

		while (true) {
			// read what the user entered
			System.out.print("jsh>");
			commandLine = console.readLine();

			// clear the space before and after the command line
			commandLine = commandLine.trim();

			// if the user entered a return, just loop again
			if (commandLine.equals("")) {
				continue;
			}
			// if exit or quit
			else if (commandLine.equalsIgnoreCase("exit") | commandLine.equalsIgnoreCase("quit")) {
				System.exit(0);
			}

			// check the command line, separate the words
			String[] commandStr = commandLine.split(" ");

			ArrayList<String> command = new ArrayList<String>();
			for (int i = 0; i < commandStr.length; i++) {
				command.add(commandStr[i]);
			}
			
			// TODO: implement code to handle create here
			if (command.get(0).equals("create")){
				Java_create(currentDir,command.get(1));
			}
			// TODO: implement code to handle delete here
			else if (command.get(0).equals("delete")){
				Java_delete(currentDir,command.get(1));
			}
			// TODO: implement code to handle display here
			else if (command.get(0).equals("cat")){
				Java_cat(currentDir,command.get(1));
			}
			// TODO: implement code to handle list here
			else if (command.get(0).equals("list")){
				String display_method = "";
				String sort_method = "";
				if (command.size() > 1){
					display_method = command.get(1);
					if (command.size() >2){
						sort_method = command.get(2);
					}
				}
				Java_ls(currentDir,display_method,sort_method);
			}
			// TODO: implement code to handle find here
			else if (command.get(0).equals("find")){
				Boolean found = Java_find(currentDir,command.get(1));
				if (found == false){
					System.out.println("No such files exist");
				}
			}
			// TODO: implement code to handle tree here
			else if (command.get(0).equals("tree")){
				int depth = Integer.MAX_VALUE;
				String sort_method = "";
				int nested = 0;
				if (command.size() > 1){
					depth = Integer.parseInt(command.get(1));
					if (command.size() >2){
						sort_method = command.get(2);
					}
				}
				Java_tree(currentDir,depth,sort_method,nested);
			}

			// other commands
			else{
				ProcessBuilder pBuilder = new ProcessBuilder(command);
				pBuilder.directory(currentDir);
				try{
					Process process = pBuilder.start();
					// obtain the input stream
					InputStream is = process.getInputStream();
					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader br = new BufferedReader(isr);

					// read what is returned by the command
					String line;
					while ( (line = br.readLine()) != null)
						System.out.println(line);

					// close BufferedReader
					br.close();
				}
					// catch the IOexception and resume waiting for commands
				catch (IOException ex){
					System.out.println(ex);
					continue;
				}
			}
			
		}
	}

	/**
	 * Create a file
	 * @param dir - current working directory
	 * @param command - name of the file to be created
	 */
	public static void Java_create(File dir, String name) {
		// TODO: create a file
		try{
			File newFile = new File(dir.getAbsolutePath() + "/" + name);
			Boolean fileVar = newFile.createNewFile();
			if (fileVar){
			  System.out.println("File has been created successfully");
			}
			else{
			  System.out.println("File already present at the specified location");
			}
		}
		catch (IOException ex){
			System.out.println(ex);
		}
	}

	/**
	 * Delete a file
	 * @param dir - current working directory
	 * @param name - name of the file to be deleted
	 */
	public static void Java_delete(File dir, String name) {
		// TODO: delete a file
		File delFile = new File(dir.getAbsolutePath() + "/" + name);
		Boolean exists = delFile.exists();
		if (exists){
			delFile.delete();
			System.out.println("File has been deleted");
		} else {
			System.out.println("No such file exists");
		}
	}

	/**
	 * Display the file
	 * @param dir - current working directory
	 * @param name - name of the file to be displayed
	 */
	public static void Java_cat(File dir, String name) {
		// TODO: display a file
		try{
			File readFile = new File(dir.getAbsolutePath() + "/" + name);
			Boolean exists = readFile.exists();
			if (exists){
				BufferedReader br = new BufferedReader(new FileReader(readFile));
				String line;
				while ( (line = br.readLine()) != null)
					System.out.println(line);	
			}
			else{
				System.out.println("No such file exists");
			}
		}
		catch (Exception ex){
			System.out.println(ex);
		}
	}

	/**
	 * Function to sort the file list
	 * @param list - file list to be sorted
	 * @param sort_method - control the sort type
	 * @return sorted list - the sorted file list
	 */
	private static File[] sortFileList(File[] list, String sort_method) {
		// sort the file list based on sort_method
		// if sort based on name
		if (sort_method.equalsIgnoreCase("name")) {
			Arrays.sort(list, new Comparator<File>() {
				public int compare(File f1, File f2) {
					return (f1.getName()).compareTo(f2.getName());
				}
			});
		}
		else if (sort_method.equalsIgnoreCase("size")) {
			Arrays.sort(list, new Comparator<File>() {
				public int compare(File f1, File f2) {
					return Long.valueOf(f1.length()).compareTo(f2.length());
				}
			});
		}
		else if (sort_method.equalsIgnoreCase("time")) {
			Arrays.sort(list, new Comparator<File>() {
				public int compare(File f1, File f2) {
					return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
				}
			});
		}
		return list;
	}

	/**
	 * List the files under directory
	 * @param dir - current directory
	 * @param display_method - control the list type
	 * @param sort_method - control the sort type
	 */
	public static void Java_ls(File dir, String display_method, String sort_method) {
		// TODO: list files
		File[] filesList = dir.listFiles();
		if (display_method.equals("property")){
			if (sort_method != ""){
				filesList = sortFileList(filesList,sort_method);
			}
			for (File files : filesList){
				DateFormat df = new SimpleDateFormat("E, MMM dd HH:mm:ss z yyyy");
				Date date = new Date(files.lastModified());
				String dateString  = df.format(date);
				System.out.printf("%1$-35s Size: %2$-13s Last Modfied: %3$-13s\n",files.getName(),files.length(),dateString);
			}
		} else {
			for (File files : filesList){
				System.out.printf("%1$-35s\n",files.getName());
			}
		}
	}

	/**
	 * Find files based on input string
	 * @param dir - current working directory
	 * @param name - input string to find in file's name
	 * @return flag - whether the input string is found in this directory and its subdirectories
	 */
	public static boolean Java_find(File dir, String name) {
		// TODO: find files
		boolean flag = false;
		File[] filesList = dir.listFiles();
		for (File files : filesList){
			if (files.isDirectory()){
				File newFile = new File(dir.getAbsolutePath() + "/" + files.getName()+"\n");
				flag = Java_find(newFile, name);
			}
			else {
				if (files.getName().contains(name)){
					System.out.printf(dir.getAbsolutePath() + "/" + files.getName()+"\n");
					flag = true;
				}
			}			
		}	
		return flag;
	}

	/**
	 * Print file structure under current directory in a tree structure
	 * @param dir - current working directory
	 * @param depth - maximum sub-level file to be displayed
	 * @param sort_method - control the sort type
	 */
	public static void Java_tree(File dir, int depth, String sort_method, int nested) {
		// TODO: print file tree
		if (depth != 0){
			File[] filesList = dir.listFiles();
			String prefix = "";
			for (int i = 0; i < nested ; i++){
				prefix += "\t";
			}
			if (nested!=0){
				prefix += "|-";
			}
			if (sort_method != ""){
				filesList = sortFileList(filesList,sort_method);
			}
			for (File files : filesList){
				if (files.isDirectory()){
					File newFile = new File(dir.getAbsolutePath() + "/" + files.getName());
					Java_tree(newFile,depth-1,sort_method,nested+1);
				}
				else {
					System.out.println(prefix + files.getName());
				}			
			}
		}
	}

	// TODO: define other functions if necessary for the above functions

}