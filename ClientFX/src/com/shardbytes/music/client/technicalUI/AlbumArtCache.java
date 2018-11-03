package com.shardbytes.music.client.technicalUI;

import com.shardbytes.music.client.Networking;
import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class AlbumArtCache{
	
	private static HashMap<String, byte[]> albumArtCache = new HashMap<>();
	
	public static Image getImage(String albumName){
		try{
			if(albumArtCache.containsKey(albumName)){
				return new Image(new ByteArrayInputStream(albumArtCache.get(albumName)));
			}else{
				byte[] imageBytes = Networking.getInstance().getAlbumNonPrecise(albumName).getAlbumArt();
				albumArtCache.put(albumName, imageBytes);
				return new Image(new ByteArrayInputStream(imageBytes));
			}
			
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
		return null;
		
	}
	
	public static void save(){
		File dbFile = new File("album.db");
		try(FileOutputStream fileOutputStream = new FileOutputStream(dbFile); ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)){
			if(!dbFile.exists()){
				dbFile.createNewFile();
			}
			
			objectOutputStream.writeObject(albumArtCache);
			
		}catch(IOException e){
			System.err.println(e.getMessage());
		}
		
	}
	
	public static void load(){
		File dbFile = new File("album.db");
		if(dbFile.exists()){
			try(FileInputStream fileInputStream = new FileInputStream(dbFile); ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)){
				albumArtCache = (HashMap<String, byte[]>)objectInputStream.readObject();
				
			}catch(IOException | ClassNotFoundException e){
				System.err.println(e.getMessage());
			}
			
		}
		
	}
	
}
