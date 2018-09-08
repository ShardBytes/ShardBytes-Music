package com.shardbytes.music.server.Database;

import com.amdelamar.jhash.Hash;
import com.amdelamar.jhash.algorithms.Type;
import com.amdelamar.jhash.exception.InvalidHashException;
import com.shardbytes.music.server.UI.ServerUI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class PasswordDB{
	
	private static boolean loaded = false;
	private static PasswordDB ourInstance = new PasswordDB();
	private PasswordDB(){}
	
	private HashMap<String, String> users = new HashMap<>();
	
	public static PasswordDB getInstance(){
		if(loaded){
			return ourInstance;
		}else{
			ourInstance.load();
			loaded = true;
			return ourInstance;
		}
		
	}
	
	public boolean auth(String nickname, char[] password) throws InvalidHashException{
		if(users.containsKey(nickname)){
			return Hash.password(password).algorithm(Type.SCRYPT).saltLength(128).factor(1048576).verify(users.get(nickname));
			
		}
		return false;
		
	}
	
	public boolean register(String nickname, char[] password){
		String hash = Hash.password(password).algorithm(Type.SCRYPT).saltLength(128).factor(1048576).create();
		if(!users.containsKey(nickname)){
			users.put(nickname, hash);
			return true;
		}else{
			return false;
		}
		
	}
	
	public boolean remove(String nickname, char[] password) throws InvalidHashException{
		if(auth(nickname, password)){
			users.remove(nickname);
			return true;
		}else{
			return false;
		}
		
	}
	
	public void save(){
		File dbFile = new File("D:" + File.separator + "ShardBytes Music.sbmd" + File.separator + "pdb.db");
		try(FileOutputStream fileOutputStream = new FileOutputStream(dbFile); ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)){
			if(!dbFile.exists()){
				dbFile.createNewFile();
			}
			
			objectOutputStream.writeObject(users);
			
		}catch(IOException e){
			ServerUI.addExceptionMessage(e.getMessage());
		}
		
	}
	
	public void load(){
		File dbFile = new File("D:" + File.separator + "ShardBytes Music.sbmd" + File.separator + "pdb.db");
		if(dbFile.exists()){
			try(FileInputStream fileInputStream = new FileInputStream(dbFile); ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)){
				users = (HashMap<String, String>)objectInputStream.readObject();
				
			}catch(IOException | ClassNotFoundException e){
				ServerUI.addExceptionMessage(e.getMessage());
			}
			
		}
		
	}
	
}
