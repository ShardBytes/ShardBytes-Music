package com.shardbytes.music.server;

import com.shardbytes.music.server.Database.PasswordDB;
import com.shardbytes.music.server.UI.ServerUI;
import com.shardbytes.music.server.Database.SongDB;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

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
				ServerSocket server = new ServerSocket(8192, 10, InetAddress.getByName("192.168.100.166"));
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
	
}
