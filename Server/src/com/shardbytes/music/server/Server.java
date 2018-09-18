package com.shardbytes.music.server;

import com.shardbytes.music.server.Database.PasswordDB;
import com.shardbytes.music.server.UI.ServerUI;
import com.shardbytes.music.server.Database.SongDB;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Properties;

public class Server{
	
	private static ServerUI ui;
	private static ArrayList<Client> clients = new ArrayList<>();

	/**
	 * Server main method
	 * @param args Command line arguments
	 */
	public static void main(String[] args){
		new Server().start();
	}

	/**
	 * Starts the server
	 */
	private void start(){
		createUI();
		SongDB.getInstance();
		PasswordDB.getInstance()/*.register("plajdo", "heslo".toCharArray())*/;
		
		Thread serverThread = new Thread(() -> {
			try{
				ServerSocket server = createSocketBySettings();
				ServerUI.log(server.toString());
				while(ui.getRenderStatus()){
					Socket clientSocket = server.accept();
					Client client = new Client(clientSocket);
					clients.add(client);
					new Thread(() -> {
						clients.remove(client.process());
					}).start();
					
				}
			}catch(IOException e){
				ServerUI.addExceptionMessage(e.getMessage());
			}
		});
		
		serverThread.setDaemon(true);
		serverThread.start();
		
	}

	/**
	 * Creates server's UI
	 */
	private void createUI(){
		ui = new ServerUI();
		
	}
	
	public static ArrayList<Client> getClients(){
		return clients;
	}
	
	private ServerSocket createSocketBySettings() throws IOException{
		Properties prop = new Properties();
		
		int port;
		int backlog;
		String ip;
		
		try(InputStream in = new FileInputStream("serverconfig.properties")){
			prop.load(in);
			
			port = Integer.parseInt(prop.getProperty("port", "8192"));
			backlog = Integer.parseInt(prop.getProperty("backlog", "10"));
			ip = prop.getProperty("ipAddress", "localhost");
			
		}catch(IOException e){
			ServerUI.addExceptionMessage(e.getMessage());
			port = 8192;
			backlog = 10;
			ip = "localhost";
			
			try(OutputStream out = new FileOutputStream("serverconfig.properties")){
				prop.setProperty("port", "8192");
				prop.setProperty("backlog", "10");
				prop.setProperty("ipAddress", "127.0.0.1");
				
				prop.store(out, null);
				
			}catch(IOException e1){
				ServerUI.addExceptionMessage(e1.getMessage());
			}
			
		}
		
		return new ServerSocket(port, backlog, InetAddress.getByName(ip));
		
	}
	
}
