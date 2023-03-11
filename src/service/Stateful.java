package service;



import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;
import java.util.TreeMap;


import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import g.Util;

import org.apache.derby.impl.tools.ij.util;


public class Stateful extends Thread
{
	public static PrintStream log = System.out;
	private Socket client;
	
	public static TreeMap<Integer, Double[]> valz = new TreeMap<Integer, Double[]>();
	
	public Stateful(Socket client)
	{
		this.client = client;
	}
	
	
	
	public String[] getFileInfo() {
		String[] s = new String[2];
		
		String cwd = Paths.get("").toAbsolutePath().toString();
		String filePath = cwd + "/ctrl/Geo.txt";
		
		
		try {
		File file = new File(filePath);
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		//String line;
		
		
		s[0] = br.readLine();
		s[1] = br.readLine();
		
		System.out.println("HELLO WURLD 420 " + s[0] + " " + s[1]);
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
			out.println("Enter latitude");
			Double lat = in.nextDouble();
			
			out.println("Enter longitude");
			Double lng = in.nextDouble(); 
			
			out.println("Enter cookie # (or negative # if no cookie)");
			int cuukie = in.nextInt();
			*/
			
String request = in.nextLine();
			
			//split the inputted string (delimated by space)
			String[] token = request.split("\\s+");
			
			Double lat = Double.parseDouble(token[0]);
			Double lng = Double.parseDouble(token[1]);
			
			
			
			int cuukie = Integer.parseInt(token[2]);

			
			
			Double[] temp; 
			String response = "";
			
			
			if(cuukie < 0) {
				temp = new Double[4];
				temp[0] = lat;
				temp[1] = lng;
				response = "cookie number is : " + valz.size();
				
				valz.put(valz.size(), temp);
			}
			else {
				//response = "cookie is : " + cuukie + "\n";
				
				temp = valz.get(cuukie);
				temp[2] = lat;
				temp[3] = lng;
				
				//out.println(temp[0] + temp[1] + temp[2] + temp[3]);
				//todo: call geo and return out.println
				String[] fileInfo = getFileInfo();
				int port = Integer.parseInt(fileInfo[1]);
				Socket connSocket = new Socket(fileInfo[0], port);
				
				
				
        		PrintWriter clientOut = new PrintWriter(connSocket.getOutputStream(), true);
        		BufferedReader clientIn = new BufferedReader( new InputStreamReader(connSocket.getInputStream()));
				
        		clientOut.println(temp[0] + " " + temp[1] + " " + temp[2] + " " + temp[3] );
        		
        		response += clientIn.readLine();
        		
        		clientIn.close();
        		clientOut.close();
        		connSocket.close();
				//end of Geo conn
				
				
				temp[0] = temp[2];
				temp[1] = temp[3];
				valz.put(cuukie, temp);
			}
			
			
			
			
			
			
			out.println(response + "");
		
			
		}
		catch(Exception e) {
			System.out.println("error: " + e.getMessage());
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
	
		BufferedWriter fileOut = new BufferedWriter(new FileWriter(cwd + "/ctrl/Stateful.txt"));
		
		fileOut.write("" + server.getInetAddress().getHostAddress() + "\n" + server.getLocalPort());
		fileOut.close();
		
		File check  = new File(cwd + "/ctrl/Stateful.txt");
		//////////FILE COMMANDS
		
		
		while(check.exists())
		{
			Socket client = server.accept();
			new Stateful(client).start();
		}
		//server.close();
	}

}