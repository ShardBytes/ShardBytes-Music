package com.shardbytes.music.client.technicalUI;

import com.shardbytes.music.client.Networking;
import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.HashSet;

public class AlbumArtCache{
	
	private static HashMap<String, Image> albumArtCache = new HashMap<>(); //TODO: maybe save to file?
	
	public static Image getImage(String albumName){
		try{
			if(albumArtCache.containsKey(albumName)){
				return albumArtCache.get(albumName);
			}else{
				Image image = new Image(new ByteArrayInputStream(Networking.getInstance().getAlbumNonPrecise(albumName).getAlbumArt()));
				albumArtCache.put(albumName, image);
				return image;
			}
			
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
		return null;
		
	}
	
}
