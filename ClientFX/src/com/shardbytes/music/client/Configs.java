package com.shardbytes.music.client;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Configs{
	
	private static Configs ourInstance = new Configs();
	private Configs(){}
	public static Configs getInstance(){
		return ourInstance;
	}
	
	private String serverIP;
	private int serverPort;
	
	void load(){
		Properties prop = new Properties();
		
		try(FileInputStream in = new FileInputStream("config.properties")){
			prop.load(in);
			
			serverIP = prop.getProperty("serverIp");
			serverPort = Integer.parseInt(prop.getProperty("serverPort"));
			
		}catch(IOException e){
			System.err.println(e.getMessage());
			serverIP = "127.0.0.1";
			serverPort = 8192;
			
			try(FileOutputStream out = new FileOutputStream("config.properties")){
				prop.setProperty("serverIp", "127.0.0.1");
				prop.setProperty("serverPort", "8192");
				
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
			
			prop.store(fos, null);
			
		}catch(IOException e){
			System.err.println(e.getMessage());
		}
		
	}
	
	public String getServerIP(){
		return serverIP;
	}
	
	public void setServerIP(String serverIP){
		this.serverIP = serverIP;
	}
	
	public int getServerPort(){
		return serverPort;
	}
	
	public void setServerPort(int serverPort){
		this.serverPort = serverPort;
	}
	
}
