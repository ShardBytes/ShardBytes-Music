package com.shardbytes.music.server;

import com.shardbytes.music.server.UI.ServerUI;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client{
	
	private Socket socket;
	private ObjectOutputStream toClient;
	private ObjectInputStream fromClient;
	private String nickname;
	private boolean connected = true;
	
	public Client(Socket clientSocket){
		socket = clientSocket;
		try{
			toClient = new ObjectOutputStream(socket.getOutputStream());
			fromClient = new ObjectInputStream(socket.getInputStream());
			nickname = socket.getInetAddress().getHostAddress();
		}catch(IOException e){
			ServerUI.addExceptionMessage(e.getMessage());
		}
		
	}
	
	Client process(){
		try{
			send("getNickname");
			nickname = (String)fromClient.readObject();
			ServerUI.log(nickname + " connected.");
			
			while(connected){
				byte command = ((Integer)fromClient.readObject()).byteValue();
				processCommand(command);
				
			}
			
		}catch(IOException | ClassNotFoundException e){
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
				ServerUI.log("0");
				break;
			
			case 60:
				connected = false;
				ServerUI.log(nickname + " disconnected. (60)");
				break;
		}
		
	}
	
	@Override
	public String toString(){
		return "- " + socket.getInetAddress().getHostAddress() + " (" + nickname + ")";
	}
	
}
