package service;



import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

import org.apache.derby.impl.tools.ij.util;
import org.json.simple.JSONArray;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;


public class Loc extends Thread
{
	public static PrintStream log = System.out;
	private Socket client;
	
	public Loc(Socket client)
	{
		this.client = client;
	}
	
	public void run()
	{
		log.printf("Connected to %s:%d\n", client.getInetAddress(), client.getPort());
		
		
		
		try {
			String response = "error: invalid address";
			Scanner in = new Scanner(client.getInputStream());
			PrintStream out = new PrintStream(client.getOutputStream(), true);
			
			/*
			out.println("Enter address");
			String add = in.nextLine();
			*/
			
			
String request = in.nextLine();
			
			//split the inputted string (delimated by space)
			String[] token = request.split("\\s+");
		
			String add = token[0];
			
		//1) connect to website and get the JSON
			URL url = new URL ("https://maps.googleapis.com/maps/api/geocode/json?key=AIzaSyB3UDuemf4mZZce6StM78nwAzRnJ-NMrq8&address=" + add);
			Scanner http = new Scanner(url.openStream());
			String payload = "";
			//get the json output of website to payload (string format)
			while(http.hasNextLine()) {
				payload += http.nextLine();
				
			}
			
		
		//2) convert payload(String) to JSON --> then get the lattitude, and longitude fields out of this JSON object
			JsonParser parser = new JsonParser();
			//the rest is based on the JSON object returned (and might require multiple "getasjsonobject().get(field/nested_obj)" statements to traverse nested objects)
		
			//payload = parser.parse(payload).getAsJsonArray().get(0).toString();
			
			
			payload = parser.parse(payload).getAsJsonObject().get("results").toString();
			
			//JsonElement content = parser.parse(payload).getAsJsonObject().get("results");	
			
			JsonArray x = parser.parse(payload).getAsJsonArray();
			
			
			payload = x.get(0).getAsJsonObject().get("geometry").toString();
			
			
				
			
			//payload = parser.parse(payload).getAsJsonObject().get("results").toString();
			
			//payload = parser.parse(payload).getAsJsonObject().get("geometry").toString();
			//out.println(" LOL \n" + payload);
			payload = parser.parse(payload).getAsJsonObject().get("location").toString();
			
			
	
			
			response = "Latitude : " + parser.parse(payload).getAsJsonObject().get("lat").toString(); 
			response += " & ";
			response += "Longitude : " + parser.parse(payload).getAsJsonObject().get("lng").toString();
		
			
			out.println("address coordinates : " + response);
			
		}
		catch(Exception e) {
			log.println("Error: " + e);
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
	
		BufferedWriter fileOut = new BufferedWriter(new FileWriter(cwd + "/ctrl/Loc.txt"));
		
		fileOut.write("" + server.getInetAddress().getHostAddress() + "\n" + server.getLocalPort());
		fileOut.close();
		File check  = new File(cwd + "/ctrl/Loc.txt");
		//////////FILE COMMANDS
		while(check.exists())
		{
			Socket client = server.accept();
			new Loc(client).start();
		}
		//server.close();
	}

}