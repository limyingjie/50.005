import java.io.*;
import java.util.ArrayList;

public class lab1 {
	public static void main(String[] args) throws java.io.IOException {
		String commandLine;
		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
		String directory=System.getProperty("user.dir");
		ArrayList<String> history=new ArrayList<String>();
		
		while (true) {
			// read what the user entered
			System.out.print("jsh>");
			commandLine = console.readLine();			

			// if the user entered a return, just loop again
			if (commandLine.equals("")) {
				continue;
			} else {
				try{
					String[] commandList=commandLine.split(" ");
									
					if (commandList[0].equals("cd")) {
						String uncheckedDirectory=directory;
						
						if (commandList.length>1) { 
							if (commandList[1].equals("..")){ 
								uncheckedDirectory=new File(directory).getParent(); 
							}
							else { 
								uncheckedDirectory+="/"+commandList[1]; 
							}
						}
						else { uncheckedDirectory=System.getProperty("user.home"); }
						
						if(new File(uncheckedDirectory).isDirectory()){
							directory=uncheckedDirectory;
							history.add(commandLine);
						}
						else{ System.out.println(uncheckedDirectory+" is not a valid directory");}						
						
						continue;
						
					} else if(commandList[0].equals("history")){
						int count=0;
						for (String command:history){
							System.out.println(count+" "+command);
							count++;
						}
						continue;
						
					} else if(commandList[0].equals("!!")){
						commandList=history.get(history.size()-1).split(" ");	
						
					} else if(commandList[0].matches("\\d+")){
						if (Integer.valueOf(commandList[0]) < history.size()){ 
							commandList=history.get(Integer.valueOf(commandList[0])).split(" "); 
						} else { 
							System.out.println("invalid command history search"); 
							continue;
						}
						
					} else {history.add(commandLine);}
					
					ProcessBuilder pb=new ProcessBuilder(commandList);
					pb.command(commandList);
					pb.directory(new File(directory));
					Process p=pb.start();
					BufferedReader br= new BufferedReader(new InputStreamReader(p.getInputStream()));
					String cOutput;
					while((cOutput=br.readLine())!=null){
						System.out.println(cOutput);
					}
				} catch (IOException ex) {
					System.out.println(ex.getMessage());
				}
			}			
		}
	}
}
