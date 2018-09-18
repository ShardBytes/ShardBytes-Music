package com.shardbytes.music.server;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Configs{
	
	private static Configs ourInstance = new Configs();
	private Configs(){}
	private static boolean loaded = false;
	public static Configs getInstance(){
		if(!loaded){
			ourInstance.load();
		}
		return ourInstance;
		
	}
	
	private String databaseLocation;
	private String serverIP;
	private int serverPort;
	private int backlog;
	
	void load(){
		Properties prop = new Properties();
		
		try(FileInputStream in = new FileInputStream("serverconfig.properties")){
			prop.load(in);
			
			serverIP = prop.getProperty("serverIp");
			serverPort = Integer.parseInt(prop.getProperty("serverPort"));
			backlog = Integer.parseInt(prop.getProperty("backlog"));
			databaseLocation = prop.getProperty("dbLocation");
			
		}catch(IOException e){
			System.err.println(e.getMessage());
			serverIP = "127.0.0.1";
			serverPort = 8192;
			backlog = 10;
			
			try(FileOutputStream out = new FileOutputStream("config.properties")){
				prop.setProperty("serverIp", "127.0.0.1");
				prop.setProperty("serverPort", "8192");
				prop.setProperty("backlog", String.valueOf(10));
				prop.setProperty("dbLocation", "");
				
				prop.store(out, null);
				
			}catch(IOException e1){
				System.err.println(e1.getMessage());
			}
			
		}
		
	}
	
	void save(){
		Properties prop = new Properties();
		
		try(FileOutputStream fos = new FileOutputStream("config.properties")){
			prop.setProperty("serverIp", serverIP);
			prop.setProperty("serverPort", String.valueOf(serverPort));
			prop.setProperty("backlog", String.valueOf(backlog));
			prop.setProperty("dbLocation", databaseLocation);
			
			prop.store(fos, null);
			
		}catch(IOException e){
			System.err.println(e.getMessage());
		}
		
	}
	
	public String getServerIP(){
		return serverIP;
	}
	
	public int getServerPort(){
		return serverPort;
	}
	
	public String getDatabaseLocation(){
		return databaseLocation;
	}
	
	public int getBacklog(){
		return backlog;
	}
	
}
