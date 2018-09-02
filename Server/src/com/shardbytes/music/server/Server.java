package com.shardbytes.music.server;

import com.shardbytes.music.server.UI.ServerUI;

import java.io.IOException;
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
		
		try{
			ServerSocket server = new ServerSocket(8192);
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
		
	}

	/**
	 * Creates server's UI
	 */
	private void createUI(){
		ui = new ServerUI();
		
	}
	
}
