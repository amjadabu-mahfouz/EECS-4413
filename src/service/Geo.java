package service;



import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import java.nio.file.Paths;

import org.apache.derby.impl.tools.ij.util;


public class Geo extends Thread
{
	public static PrintStream log = System.out;
	private Socket client;
	
	public Geo(Socket client)
	{
		this.client = client;
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
			*/
			
			String request = in.nextLine();
			//System.out.println("Geo: " + request);
			//split the inputted string (delimated by space)
			String[] token = request.split("\\s+");
			
			//get degree into radians by (degree * (Math.PI /180)0
			Double lat1 = Double.parseDouble(token[0])* (Math.PI /180);
			Double lng1 = Double.parseDouble(token[1])* (Math.PI /180);
			Double lat2 = Double.parseDouble(token[2])* (Math.PI /180);
			Double lng2 = Double.parseDouble(token[3])* (Math.PI /180);
		
			Double y = Math.cos(lat1) * Math.cos(lat2);
			
			Double xx = Math.pow(Math.sin((lat2 - lat1) / 2), 2);
			Double yy = y * (Math.pow(Math.sin((lng2 - lng1) / 2), 2));
			
			//Double x = ( Math.pow(Math.sin((lat2 - lat1) / 2), 2) ) + (y * ( Math.pow(Math.sin(((lng2 - lng1) / 2)), 2)));
			Double x = xx + yy;
			double result = 12742 * Math.atan2(Math.sqrt(x), Math.sqrt(1 - x));  
			
			out.print("the distance between the 2 points is : " + result + "\n");
			
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
	
		BufferedWriter fileOut = new BufferedWriter(new FileWriter(cwd + "/ctrl/Geo.txt"));
		
		fileOut.write("" + server.getInetAddress().getHostAddress() + "\n" + server.getLocalPort());
		fileOut.close();
		File check  = new File(cwd + "/ctrl/Geo.txt");
		//////////FILE COMMANDS
		
		
		
		while(check.exists())
		{
			Socket client = server.accept();
			new Geo(client).start();
		}
		//server.close();
	}

}