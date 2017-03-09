
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

public class lab4 {
	private static File currentDirectory = new File(System.getProperty("user.dir"));
	public static void main(String[] args) throws java.io.IOException {

		String commandLine;

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

			// code to handle create here
			if (commandStr[0].matches("create")){Java_create(currentDirectory,commandStr[1]);}

			// code to handle delete here
			else if (commandStr[0].matches("delete")){Java_delete(currentDirectory,commandStr[1]);}

			// code to handle display here
			else if (commandStr[0].matches("display")){Java_cat(currentDirectory,commandStr[1]);}

			// code to handle list here
			else if (commandStr[0].matches("list")){
				if (commandStr.length==1){Java_ls(currentDirectory,null,null);}
				else if(commandStr.length==2 && commandStr[1].matches("property")){Java_ls(currentDirectory,commandStr[1],null);}
				else if(commandStr.length==3 && commandStr[1].matches("property")){Java_ls(currentDirectory,commandStr[1],commandStr[2]);}
				}

			// implement code to handle find here
			else if (commandStr[0].matches("find")){Java_find(currentDirectory,commandStr[1]);}

			// implement code to handle tree here
			else if (commandStr[0].matches("tree")){
				if (commandStr.length==1){Java_tree(currentDirectory,99,null);}
				else if(commandStr.length==2){Java_tree(currentDirectory,Integer.parseInt(commandStr[1]),null);}
				else if(commandStr.length==3){Java_tree(currentDirectory,Integer.parseInt(commandStr[1]),commandStr[2]);}
			}

			// other commands
			else{
				ProcessBuilder pBuilder = new ProcessBuilder(command);
				pBuilder.directory(currentDirectory);
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
	public static void Java_create(File dir, String name) throws java.io.IOException {
		// create a file
		File file=new File(dir,name);
		if (!file.exists()){file.createNewFile();}
	}

	/**
	 * Delete a file
	 * @param dir - current working directory
	 * @param name - name of the file to be deleted
	 */
	public static void Java_delete(File dir, String name) {
		// delete a file
		File file=new File(dir,name);
		if(file.exists()){file.delete();}
	}

	/**
	 * Display the file
	 * @param dir - current working directory
	 * @param name - name of the file to be displayed
	 */
	public static void Java_cat(File dir, String name) throws java.io.IOException {
		// display a file
		File file=new File(dir,name);
		FileReader fileReader=new FileReader(file);
		BufferedReader in=new BufferedReader(fileReader);
		String line;
		while( (line=in.readLine())!=null ){System.out.println(line);}
		in.close();
		
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
		// list files
		File[] list=dir.listFiles();
		if (display_method==null){
			list=sortFileList(list,"name");
			for (File file:list){System.out.println(file.getName());}
		} else {
			if (sort_method==null){sort_method="name";}
			list=sortFileList(list,sort_method);
			for (File file:list){
				String name=file.getName();
				String size=Long.toString(file.length());
				String date=(new Date(file.lastModified())).toString();
				System.out.println(String.format("%-20s Size: %-8s Last Modified: %s",name,size,date));
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
		boolean flag = false;
		// find files
		File[] list=sortFileList(dir.listFiles(),"name");
		for (File file:list){
			String path=file.getAbsolutePath();
			if (file.isDirectory()){Java_find(new File(path),name);}
			else if (path.contains(name)){System.out.println(path);}
		}
		return flag;
	}

	/**
	 * Print file structure under current directory in a tree structure
	 * @param dir - current working directory
	 * @param depth - maximum sub-level file to be displayed
	 * @param sort_method - control the sort type
	 */
	public static void Java_tree(File dir, int depth, String sort_method) {
		// print file tree
		if (sort_method==null){sort_method="name";}
		File[] list=sortFileList(dir.listFiles(),sort_method);
		for (File file:list){
			System.out.println(file.getName());
			if (depth>1 && file.isDirectory()){Java_treebranch(new File( file.getAbsolutePath()),depth-1,sort_method,"  |-" );}
		}
	}

	// define other functions if necessary for the above functions
	public static void Java_treebranch(File dir, int depth, String sort_method, String affix) {
		File[] list=sortFileList(dir.listFiles(),sort_method);
		for (File file:list){
			System.out.println(affix+file.getName());
			if (depth>1 && file.isDirectory()){Java_treebranch(new File( file.getAbsolutePath()),depth-1,sort_method,"  "+affix );}
		}
	}

}
