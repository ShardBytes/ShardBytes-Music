package com.shardbytes.music.server.Database;

import com.shardbytes.music.common.DecompressedData;
import com.shardbytes.music.common.Song;
import com.shardbytes.music.server.Configs;
import com.shardbytes.music.server.UI.ServerUI;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Fancy hashmap wrapper with loading and saving
 */
public class PCMCache{

	private static PCMCache ourInstance = new PCMCache();
	public static PCMCache getInstance(){
		return ourInstance;
	}
	private PCMCache(){
		load();
	}

	private HashMap<Song, DecompressedData> cache = new HashMap<>();

	public HashMap<Song, DecompressedData> get(){
		return cache;
	}

	public void save(){
		File dbFile = new File(Configs.getInstance().getDatabaseLocation() + File.separator + "cache.pcm");
		try(FileOutputStream fileOutputStream = new FileOutputStream(dbFile); ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)){
			if(!dbFile.exists()){
				dbFile.createNewFile();
			}

			objectOutputStream.writeObject(cache);

		}catch(IOException e){
			ServerUI.addExceptionMessage(e.getMessage());
		}

	}

	public void load(){
		File dbFile = new File(Configs.getInstance().getDatabaseLocation() + File.separator + "cache.pcm");
		if(dbFile.exists()){
			try(FileInputStream fileInputStream = new FileInputStream(dbFile); ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)){
				cache = (HashMap<Song, DecompressedData>)objectInputStream.readObject();

			}catch(IOException | ClassNotFoundException e){
				ServerUI.addExceptionMessage(e.getMessage());
			}

		}

	}

}
