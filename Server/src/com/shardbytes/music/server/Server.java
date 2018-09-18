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
		Configs.getInstance();
		SongDB.getInstance();
		PasswordDB.getInstance()/*.register("plajdo", "heslo".toCharArray())*/;
		
		Thread serverThread = new Thread(() -> {
			try{
				ServerSocket server = createSocketBySettings();
				ServerUI.log("Running on " + server.getInetAddress().getHostName() + ":" + server.getLocalPort());
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
		Configs configs = Configs.getInstance();
		return new ServerSocket(configs.getServerPort(), configs.getBacklog(), InetAddress.getByName(configs.getServerIP()));
		
	}
	
}
