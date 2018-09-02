package com.shardbytes.music.server;

import com.shardbytes.music.server.UI.ServerUI;

public class Server{

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
	}

	/**
	 * Creates server's UI
	 */
	private void createUI(){
		new ServerUI();
		
	}
	
}
