package com.shardbytes.music.client.audio;

public class AudioQueue{
	
	private static AudioQueue ourInstance = new AudioQueue();
	public static AudioQueue getInstance(){
		return ourInstance;
	}
	private AudioQueue(){}
	
}
