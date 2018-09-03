package com.shardbytes.music.server.UI;

public class SongDB{
	private static SongDB ourInstance = new SongDB();
	
	public static SongDB getInstance(){
		return ourInstance;
	}
	
	private SongDB(){
		//TODO: Database
	}
}
