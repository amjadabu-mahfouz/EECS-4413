package service;



import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.*;
import com.google.gson.Gson;


import org.apache.derby.impl.tools.ij.util;


public class Quote extends Thread
{
	public static PrintStream log = System.out;
	private Socket client;
	
	public Quote(Socket client)
	{
		this.client = client;
	}
	
	public void run()
	{
		log.printf("Connected to %s:%d\n", client.getInetAddress(), client.getPort());
		
		
		
		try {
			
			Scanner in = new Scanner(client.getInputStream());
			PrintStream out = new PrintStream(client.getOutputStream(), true);
			
			String response = "";
			
			/*
			out.println("Enter Product ID");
			String pid = in.nextLine();
			
			out.println("Enter Format");
			String format = in.nextLine(); 
			*/
			
			
			String request = in.nextLine();
			
			//split the inputted string (delimated by space)
			String[] token = request.split("\\s+");
			
			String pid = token[0];
			String format = token[1];
			
			
			if (format.equals("xml") || format.equals("json"))
			{
				
				
				// Connect to DB  !!!!!!!!!!!!!!!! user derby (redo this part) 
				//***1***
				String dbURL = "jdbc:sqlite:C:\\Users\\Mjad\\eclipse-workspace\\hr4413\\pkg\\sqlite\\Models_R_US.db";  
						//"jdbc:sqlite:C:\\Users\\Mjad\\Desktop\\Eclipse stuff\\hr4413\\pkg\\sqlite\\Models_R_US.db";
				Connection conn = DriverManager.getConnection(dbURL);
				//***1***
				
				//query DB (prepared statement = no injection) 
				//***2***
				String query = "select * from Product where id = ?";
				PreparedStatement statement  = conn.prepareStatement(query);
				
				// sets the 1st "?" to the value of "usr" in the query
				statement.setString(1, pid);
				
				ResultSet rs = statement.executeQuery();
				//***2***
			
				//make a bean (basic "Json-like" class)
				Product pb = new Product();
				
				//populate bean (use setter methods) !!!**!!
				
				
				
				if(rs.next()) {
					pb.setId(rs.getString("id"));
					pb.setName(rs.getString("name"));
					pb.setPrice(rs.getDouble("cost"));
					
					
				}
				
				
				
				//display in user-secified format..
				
				//convert Bean class into XML *********!!!************
				if(format.equals("xml")) {
					JAXBContext context = JAXBContext.newInstance(Product.class);
					Marshaller m = context.createMarshaller();
					m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					m.marshal(pb, baos);
					response = baos.toString();
					
					
				}
				//convert Bean class into JSON ****!!!!!!!!!!****
				else {
					Gson gson = new Gson();
					response = gson.toJson(pb);
				}
				
				
				
				
			}
			else
			{
				response = "invalid input";
				
			}
			
			
			
		out.println(response);
			
			
			
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
	
		BufferedWriter fileOut = new BufferedWriter(new FileWriter(cwd + "/ctrl/Quote.txt"));
		
		fileOut.write("" + server.getInetAddress().getHostAddress() + "\n" + server.getLocalPort());
		fileOut.close();
		File check  = new File(cwd + "/ctrl/Quote.txt");
		//////////FILE COMMANDS
		
		while(check.exists())
		{
			Socket client = server.accept();
			new Quote(client).start();
		}
		//server.close();
	}

}