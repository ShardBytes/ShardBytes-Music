package com.shardbytes.music.common;

import java.io.File;
import java.io.Serializable;

public class Song implements Serializable{
	
	private String title;
	private String artist;
	private String album;
	private File file;
	
	public Song(String title, String artist, String album, File file){
		this.title = title;
		this.artist = artist;
		this.album = album;
		this.file = file;
	}
	
	public String getTitle(){
		return title;
	}
	
	public String getArtist(){
		return artist;
	}
	
	public String getAlbum(){
		return album;
	}
	
	public File getFile(){
		return file;
	}
	
	@Override
	public String toString(){
		return artist + ": " + title + ", " + album;
	}
	
}
