




package service;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import java.nio.file.Paths;

import org.apache.derby.impl.tools.ij.util;


public class Gateway extends Thread
{
	public static PrintStream log = System.out;
	private Socket client;
	
	public Gateway(Socket client)
	{
		this.client = client;
	}
	
	
	public String[] getFileInfo(String ext) {
		String[] s = new String[2];
		
		String cwd = Paths.get("").toAbsolutePath().toString();
		String filePath = cwd + "/ctrl/" + ext + ".txt";
		
		
		try {
		File file = new File(filePath);
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		//String line;
		
		
		s[0] = br.readLine();
		s[1] = br.readLine();
		
		//System.out.println("HELLO WURLD 420 " + s[0] + " " + s[1]);
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
		return s;
	}
	
	public void run()
	{
		log.printf("Connected to %s:%d\n", client.getInetAddress(), client.getPort());
		
		
		
		try {
			
			Scanner in = new Scanner(client.getInputStream());
			PrintStream out = new PrintStream(client.getOutputStream(), true);
			
			/*
			out.println("Enter latitude for point 1");
			Double lat1 = in.nextDouble();
			
			out.println("Enter longitude for point 1");
			Double lng1 = in.nextDouble(); 
			
			out.println("Enter latitude for point 2");
			Double lat2 = in.nextDouble();
			
			out.println("Enter longitude for point 2");
			Double lng2 = in.nextDouble(); 
			
			
			
			out.println("HTTP/1.1 200 OK");
				out.println("Content-Type: text/plain");
				out.println();
			*/
			
			String request = in.nextLine();
			
			String response = "";
			
			//out.println(request);
			//split the inputted string (delimated by space)
			
			String[] token = request.split("\\?");
			String service = token[0];
			token = token[1].split("\\&");
			
			String[] temp = null;

			
			if(service.contains("Geo")) {
				
				temp = token[0].split("\\=");
				Double lat1 = Double.parseDouble(temp[1]);
				temp = token[1].split("\\=");
				Double lng1 = Double.parseDouble(temp[1]);
				temp = token[2].split("\\=");
				Double lat2 = Double.parseDouble(temp[1]);
				temp = token[3].split("\\=");
				temp = temp[1].split("\\s");
				Double lng2 = Double.parseDouble(temp[0]);
				
				String[] add = getFileInfo("Geo");
				int port = Integer.parseInt(add[1]);
				
				Socket connSocket = new Socket(add[0], port);
				PrintWriter clientOut = new PrintWriter(connSocket.getOutputStream(), true);
	    		BufferedReader clientIn = new BufferedReader( new InputStreamReader(connSocket.getInputStream()));
				
	    		clientOut.println(lat1 + " " + lng1 + " " + lat2 + " " + lng2 );
	    		
	    		response += clientIn.readLine() + "\n";
	    		
	    		

        		clientIn.close();
        		clientOut.close();
        		connSocket.close();
				
        		
        		try {client.close();} catch (Exception e) {log.print(e);}
        		log.printf("Dis-Connected to %s:%d\n", client.getInetAddress(), client.getPort());
				
			}
			else if(service.contains("Auth")){
				
				temp = token[0].split("\\=");
				String usr = temp[1];
				temp = token[1].split("\\=");
				temp = temp[1].split("\\s");
				String pw = temp[0];
				
				String[] add = getFileInfo("Auth");
				int port = Integer.parseInt(add[1]);
				Socket connSocket = new Socket(add[0], port);
				PrintWriter clientOut = new PrintWriter(connSocket.getOutputStream(), true);
	    		BufferedReader clientIn = new BufferedReader( new InputStreamReader(connSocket.getInputStream()));
				
	    		clientOut.println(usr + " " + pw );
	    		
	    		response += clientIn.readLine() + "\n";
	    		
	    		

        		clientIn.close();
        		clientOut.close();
        		connSocket.close();
				
				
        		try {client.close();} catch (Exception e) {log.print(e);}
        		log.printf("Dis-Connected to %s:%d\n", client.getInetAddress(), client.getPort());
				
			}
			else if(service.contains("Quote")) {
				
				temp = token[0].split("\\=");
				String pid = temp[1];
				temp = token[1].split("\\=");
				temp = temp[1].split("\\s");
				String format = temp[0];

				String[] add = getFileInfo("Quote");
				int port = Integer.parseInt(add[1]);
				Socket connSocket = new Socket(add[0], port);
				PrintWriter clientOut = new PrintWriter(connSocket.getOutputStream(), true);
	    		BufferedReader clientIn = new BufferedReader( new InputStreamReader(connSocket.getInputStream()));
				
	    		clientOut.println(pid + " " + format );
	    		
	    		response += clientIn.readLine() + "\n";
	    		
	    		

        		clientIn.close();
        		clientOut.close();
        		connSocket.close();
				
        		try {client.close();} catch (Exception e) {log.print(e);}
        		log.printf("Dis-Connected to %s:%d\n", client.getInetAddress(), client.getPort());
				
			}
			else if(service.contains("Loc")) {
			
				
				temp = token[0].split("\\=");
				temp = temp[1].split("\\s");
				String address = temp[0];
			
				String[] add = getFileInfo("Loc");
				int port = Integer.parseInt(add[1]);
				Socket connSocket = new Socket(add[0], port);
				PrintWriter clientOut = new PrintWriter(connSocket.getOutputStream(), true);
	    		BufferedReader clientIn = new BufferedReader( new InputStreamReader(connSocket.getInputStream()));
				
	    		clientOut.println(address);
	    		
	    		response += clientIn.readLine() + "\n";
	    		
	    		

        		clientIn.close();
        		clientOut.close();
        		connSocket.close();
				
        		try {client.close();} catch (Exception e) {log.print(e);}
        		log.printf("Dis-Connected to %s:%d\n", client.getInetAddress(), client.getPort());
        		
			}
			else if (service.contains("Stateful")) {
				
				
				temp = token[0].split("\\=");
				String lat = temp[1];
				temp = token[1].split("\\=");
				String lng = temp[1];
				temp = token[2].split("\\=");
				//temp = temp[1].split("\\s");
				String cuukie = temp[1];
				
				//System.out.println(lat + " " + lng + " " + cuukie);

				String[] add = getFileInfo("Stateful");
				int port = Integer.parseInt(add[1]);
				Socket connSocket = new Socket(add[0], port);
				PrintWriter clientOut = new PrintWriter(connSocket.getOutputStream(), true);
	    		BufferedReader clientIn = new BufferedReader( new InputStreamReader(connSocket.getInputStream()));
				
	    		clientOut.println(lat + " " + lng + " " + cuukie );
	    		
	    		response += clientIn.readLine() + "\n";
	    		
	    		

        		clientIn.close();
        		clientOut.close();
        		connSocket.close();
				
        		
        		try {client.close();} catch (Exception e) {log.print(e);}
        		log.printf("Dis-Connected to %s:%d\n", client.getInetAddress(), client.getPort());
			
			}
			else {
				
			}

			
			System.out.println(response);
			
		
			
		}
		catch(Exception e) {
			System.out.println("error: " + e);
		}
			
		
		try {client.close();} catch (Exception e) {log.print(e);}
		log.printf("Dis-Connected to %s:%d\n", client.getInetAddress(), client.getPort());
	}
	

	public static void main(String[] args) throws Exception
	{
		
		
		
		
		int port = 0;
		InetAddress host = InetAddress.getLocalHost(); //.getLoopbackAddress();
		ServerSocket server = new ServerSocket(port, 0, host);
		log.printf("Server listening on %s:%d\n", server.getInetAddress(), server.getLocalPort());
	
		///////////FILE COMMANDS
		//get path of this class
		String cwd = Paths.get("").toAbsolutePath().toString();
	
		BufferedWriter fileOut = new BufferedWriter(new FileWriter(cwd + "/ctrl/Gateway.txt"));
		
		fileOut.write("" + server.getInetAddress().getHostAddress() + "\n" + server.getLocalPort());
		fileOut.close();
		File check  = new File(cwd + "/ctrl/Gateway.txt");
		//////////FILE COMMANDS
		
		
		
		
		
		
		while(check.exists())
		{
			Socket client = server.accept();
			new Gateway(client).start();
		}
		//server.close();
	}

}