package com.shardbytes.music.server.Database;

import org.jaudiotagger.tag.images.Artwork;
import java.util.ArrayList;

public class Album{
	
	private ArrayList<Song> songs;
	private Artwork albumArt;
	private String title;
	private String artist;
	private String genre;
	private int year;
	private transient int songCount;
	
	public Album(ArrayList<Song> songs, Artwork albumArt, String title, String artist, String genre, int year){
		this.songs = songs;
		this.albumArt = albumArt;
		this.title = title;
		this.artist = artist;
		this.genre = genre;
		this.year = year;
		
		this.songCount = songs.size();
		
	}
	
	public ArrayList<Song> getSongs(){
		return songs;
	}
	
	public Artwork getAlbumArt(){
		return albumArt;
	}
	
	public String getTitle(){
		return title;
	}
	
	public String getArtist(){
		return artist;
	}
	
	public String getGenre(){
		return genre;
	}
	
	public int getYear(){
		return year;
	}
	
	public int getSongCount(){
		return songCount;
	}
	
}
