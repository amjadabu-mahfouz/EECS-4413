package service;



import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;
import g.Util;

import org.apache.derby.impl.tools.ij.util;


public class Auth extends Thread
{
	public static PrintStream log = System.out;
	private Socket client;
	
	public Auth(Socket client)
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
			out.println("Enter username");
			String usr = in.nextLine();
			
			out.println("Enter Password");
			String pw = in.nextLine(); 
			*/
			
			String request = in.nextLine();
			
			//split the inputted string (delimated by space)
			String[] token = request.split("\\s+");
			
			String usr = token[0];
			String pw = token[1];
			
		
			
			
			
			// Connect to DB 
			//***1***
			String dbURL =  "jdbc:sqlite:C:\\Users\\Mjad\\eclipse-workspace\\hr4413\\pkg\\sqlite\\Models_R_US.db";
					//"jdbc:sqlite:C:\\Users\\Mjad\\Desktop\\Eclipse stuff\\hr4413\\pkg\\sqlite\\Models_R_US.db";
			Connection conn = DriverManager.getConnection(dbURL);
			//***1***
			
			//query DB (prepared statement = no injection) 
			//***2***
			String query = "Select * from Client where name = ?";
			PreparedStatement statement  = conn.prepareStatement(query);
			
			// sets the 1st "?" to the value of "usr" in the query
			statement.setString(1, usr);
			
			ResultSet rs = statement.executeQuery();
			//***2***
			
			
			
			//do stuff with the query results
			//1)hash the pw the user input
			String response = "FAILURE";
			
			if(rs.next()) {
				
				String pw2 = g.Util.hash(pw, rs.getString("salt"), rs.getInt("count"));
				if(pw2.equals(rs.getString("hash"))) {
					response = "OK";
				}
				
			}
			
			
			out.println(response);
			//out.println(rs.getString(""));
			//g.Util.hash(pw, rs.getString("salt"), 1);
			
			
		
			
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
	
		BufferedWriter fileOut = new BufferedWriter(new FileWriter(cwd + "/ctrl/Auth.txt"));
		
		fileOut.write("" + server.getInetAddress().getHostAddress() + "\n" + server.getLocalPort());
		fileOut.close();
		File check  = new File(cwd + "/ctrl/Auth.txt");
		//////////FILE COMMANDS
		
		while(check.exists())
		{
			Socket client = server.accept();
			new Auth(client).start();
		}
		//server.close();
	}

}