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
	private boolean connected = true;
	
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
			Thread.sleep(5000);
			send("getNickname");
			nickname = (String)fromClient.readObject();
			System.out.println("nickname = " + nickname);
			
			while(connected){
				byte command = ((Integer)fromClient.readObject()).byteValue();
				System.out.println("command = " + command);
				processCommand(command);
				
			}
			
		}catch(IOException | InterruptedException | ClassNotFoundException e){
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
				System.out.println("nulla ciii");
				break;
			
			case 60:
				connected = false;
				System.out.println("DISCONNEEEEEEEEEEEECT");
				break;
		}
		
	}
	
	@Override
	public String toString(){
		return nickname;
	}
}
