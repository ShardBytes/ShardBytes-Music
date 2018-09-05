package com.shardbytes.music.server;

import com.shardbytes.music.common.Song;
import com.shardbytes.music.server.Database.SongDB;
import com.shardbytes.music.server.UI.ServerUI;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

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
				
			case 1:
				ServerUI.log(nickname + " requested a song list. (1)");
				send(SongDB.getInstance().getSongList());
				break;
				
			case 2:
				ServerUI.log(nickname + " requested a album list. (2)");
				send(SongDB.getInstance().getAlbumList());
				break;
				
			case 3:
				ServerUI.log(nickname + " requested an album. (3)");
				try{
					String albumTitle = (String)fromClient.readObject();
					String albumAuthor = (String)fromClient.readObject();
					send(SongDB.getInstance().getAlbum(albumTitle, albumAuthor));
				}catch(IOException | ClassNotFoundException e){
					ServerUI.addExceptionMessage(e.getMessage());
				}
				break;
				
			case 4:
				ServerUI.log(nickname + " requested a song. (4)");
				try{
					String author = (String)fromClient.readObject();
					String album = (String)fromClient.readObject();
					String title = (String)fromClient.readObject();
					send(Files.readAllBytes(SongDB.getInstance().getSong(author, album, title).getFile().toPath()));
					
				}catch(IOException | ClassNotFoundException e){
					ServerUI.addExceptionMessage(e.getMessage());
				}
				
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
