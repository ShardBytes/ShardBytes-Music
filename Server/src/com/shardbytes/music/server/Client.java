package com.shardbytes.music.server;

import com.shardbytes.music.server.UI.ServerUI;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

class Client{
	
	private Socket socket;
	private ObjectOutputStream toClient;
	private ObjectInputStream fromClient;
	private String nickname;
	private boolean connected;
	
	Client(Socket clientSocket){
		socket = clientSocket;
		try{
			toClient = new ObjectOutputStream(socket.getOutputStream());
			fromClient = new ObjectInputStream(socket.getInputStream());
		}catch(IOException e){
			ServerUI.addExceptionMessage(e.getMessage());
		}
	}
	
	Client process(){
		try{
			send("getNickname");
			nickname = fromClient.readUTF();
			
			while(connected){
				byte command = fromClient.readByte();
				processCommand(command);
				
			}
			
		}catch(IOException e){
			ServerUI.addExceptionMessage(e.getMessage());
		}
		
		return this;
	}
	
	private void send(Object o){
		try{
			toClient.writeObject(o);
		}catch(IOException e){
			ServerUI.addExceptionMessage(e.getMessage());
		}
		
	}
	
	private void processCommand(byte command){
		switch(command){
			case 0:
				break;
			
		}
		
	}
	
}
