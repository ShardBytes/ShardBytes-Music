package com.shardbytes.music.common;

import org.jaudiotagger.tag.images.Artwork;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class Album implements Serializable{
	
	private ArrayList<Song> songs;
	private Artwork albumArt;
	private String title;
	private String artist;
	private String genre;
	private int year;
	
	public Album(String title){
		this.title = title;
		
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
	
	public void setSongs(ArrayList<Song> songs){
		this.songs = songs;
	}
	
	public void setAlbumArt(Artwork albumArt){
		this.albumArt = albumArt;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public void setArtist(String artist){
		this.artist = artist;
	}
	
	public void setGenre(String genre){
		this.genre = genre;
	}
	
	public void setYear(int year){
		this.year = year;
	}
	
	@Override
	public String toString(){
		return "Album{" +
				"songs=" + songs +
				", albumArt=" + albumArt +
				", title='" + title + '\'' +
				", artist='" + artist + '\'' +
				", genre='" + genre + '\'' +
				", year=" + year +
				'}';
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		Album album = (Album)o;
		return year == album.year &&
				Objects.equals(songs, album.songs) &&
				Objects.equals(albumArt, album.albumArt) &&
				Objects.equals(title, album.title) &&
				Objects.equals(artist, album.artist) &&
				Objects.equals(genre, album.genre);
		
	}
	
	@Override
	public int hashCode(){
		return Objects.hash(songs, albumArt, title, artist, genre, year);
		
	}
	
}
