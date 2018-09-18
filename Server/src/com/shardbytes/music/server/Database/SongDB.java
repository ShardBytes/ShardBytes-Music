package com.shardbytes.music.server.Database;

import com.shardbytes.music.common.Album;
import com.shardbytes.music.common.Song;
import com.shardbytes.music.server.Configs;
import com.shardbytes.music.server.UI.ServerUI;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class SongDB{
	
	private static SongDB ourInstance = new SongDB();
	
	//private File databaseFolder = new File("/Users/filipsasala/Desktop/ShardBytes Music.sbmd");				//Mac test version
	private File databaseFolder = new File(Configs.getInstance().getDatabaseLocation());
	
	private ArrayList<Song> allDatabaseSongs;	//TODO: Save & load these from file when there is no need to sync stuff
	private ArrayList<Album> allDatabaseAlbums;
	
	public static SongDB getInstance(){
		return ourInstance;
	}
	
	private SongDB(){
		ServerUI.log("Database located in: " + databaseFolder.getAbsolutePath());
		recreate();
		
	}
	
	private void recreate(){
		ArrayList<Song> allSongs = readAllSongs();
		ArrayList<Album> allAlbums = new ArrayList<>();
		
		allSongs.forEach(song -> {
			Album album = new Album(song.getAlbum());
			if(!allAlbums.contains(album)){
				allAlbums.add(album);
			}
			
		});
		
		allAlbums.forEach(album -> {
			ArrayList<Song> albumSongs = new ArrayList<>();
			
			allSongs.forEach(song -> {
				if(song.getAlbum().equals(album.getTitle())){
					if(album.getArtist() == null || album.getArtist().isEmpty() ||
							album.getGenre() == null || album.getGenre().isEmpty() ||
							album.getYear() == 0 || album.getAlbumArt() == null){
						String[] tags = getStringFromID3Tag(song.getFile(), FieldKey.ALBUM_ARTIST, FieldKey.GENRE, FieldKey.YEAR);
						
						if(tags != null){
							album.setArtist(tags[0]);
							album.setGenre(tags[1]);
							album.setYear(Integer.parseInt(tags[2]));
						}
						album.setAlbumArt(getAlbumArtFromID3Tag(song.getFile()));
						
					}
					albumSongs.add(song);
					
				}
				
			});
			album.setSongs(albumSongs);
			
		});
		
		allDatabaseSongs = allSongs;
		allDatabaseAlbums = allAlbums;
		
	}
	
	private String readFile(File file) throws IOException{
		byte[] bytes = Files.readAllBytes(Paths.get(file.toURI()));
		
		return new String(bytes, StandardCharsets.UTF_8);
		
	}
	
	private String[] getStringFromID3Tag(File mp3file, FieldKey... tagKey){
		try{
			AudioFile song = AudioFileIO.read(mp3file);
			Tag tag = song.getTag();
			
			String[] results = new String[tagKey.length];
			for(int i = 0; i < tagKey.length; i++){
				results[i] = tag.getFirst(tagKey[i]);
				
			}
			return results;
			
		}catch(CannotReadException | TagException | ReadOnlyFileException | InvalidAudioFrameException | IOException e){
			ServerUI.addExceptionMessage(e.getMessage());
		}
		return null;
	
	}
	
	private byte[] getAlbumArtFromID3Tag(File mp3file){
		try{
			AudioFile song = AudioFileIO.read(mp3file);
			Tag tag = song.getTag();
			
			return tag.getFirstArtwork().getBinaryData();
			
		}catch(CannotReadException | TagException | ReadOnlyFileException | InvalidAudioFrameException | IOException e){
			ServerUI.addExceptionMessage(e.getMessage());
		}
		return null;
	}
	
	private ArrayList<Song> readAllSongs(){
		ArrayList<Song> songlist = new ArrayList<>();
		File[] artists = databaseFolder.listFiles(File::isDirectory);
		
		if(artists != null){
			for(File artist : artists){
				File[] albums = artist.listFiles(File::isDirectory);
				
				if(albums != null){
					for(File album : albums){
						File[] songs = album.listFiles(pathname -> pathname.getName().endsWith(".mp3"));
						
						if(songs != null){
							for(File song : songs){
								String[] tags = getStringFromID3Tag(song, FieldKey.TITLE, FieldKey.ARTIST, FieldKey.ALBUM);
								if(tags != null){
									songlist.add(new Song(tags[0], tags[1], tags[2], song));
									
								}
								
							}
							
						}
						
					}
					
				}
				
			}
			
		}
		return songlist;
		
	}
	
	public ArrayList<Album> getAlbumList(){
		return allDatabaseAlbums;
	}
	
	public ArrayList<Song> getSongList(){
		return allDatabaseSongs;
	}
	
	public Album getAlbum(String albumTitle, String albumArtist){
		for(Album album : allDatabaseAlbums){
			if(album.getTitle().equals(albumTitle) && album.getArtist().equals(albumArtist)){
				return album;
				
			}
			
		}
		return null;
		
	}
	
	public Album getAlbumNonPrecise(String albumTitle){
		for(Album album : allDatabaseAlbums){
			if(album.getTitle().equals(albumTitle)){
				return album;
				
			}
			
		}
		return null;
		
	}
	
	public Song getSong(String artist, String album, String title){
		for(Song song : allDatabaseSongs){
			if(song.getArtist().equals(artist) && song.getAlbum().equals(album) && song.getTitle().equals(title)){
				return song;
				
			}
			
		}
		return null;
		
	}
	
	public ArrayList<Song> doSongSearch(String searchString, int maxResults){
		ArrayList<Song> resultSongs = new ArrayList<>();
		
		final String search = searchString.toLowerCase();
		
		allDatabaseSongs.forEach(song -> {
			if(song.getTitle().toLowerCase().contains(search) && resultSongs.size() < maxResults) resultSongs.add(song);
			
			else if(song.getArtist().toLowerCase().contains(search) && resultSongs.size() < maxResults) resultSongs.add(song);
			
			else if(song.getAlbum().toLowerCase().contains(search) && resultSongs.size() < maxResults) resultSongs.add(song);
			
		});
		
		return resultSongs;
		
	}
	
}
