package com.shardbytes.music.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client{
	
	public static void main(String[] args){
		try{
			Socket socket = new Socket("localhost", 8192);
			ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());
			
			while(true){
				String message = (String)fromServer.readObject();
				System.out.println("message = " + message);
				toServer.writeObject("cykablyatname");
				if(message.equals("getNickname")){
					toServer.writeObject(0);
					System.out.println("wrajtbajt");
					Thread.sleep(5000);
					toServer.writeObject(60);
					System.out.println("dic");
					
				}
				
			}
			
		}catch(IOException | InterruptedException | ClassNotFoundException e){
			System.err.println(e.getMessage());
		}
		
	}
	
}
