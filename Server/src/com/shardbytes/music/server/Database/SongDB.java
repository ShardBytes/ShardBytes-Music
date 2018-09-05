package com.shardbytes.music.server.Database;

import com.shardbytes.music.common.Album;
import com.shardbytes.music.common.Song;
import com.shardbytes.music.server.UI.ServerUI;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class SongDB{
	
	private static SongDB ourInstance = new SongDB();
	
	private File databaseFolder = new File("/Users/filipsasala/Desktop/ShardBytes Music.sbmd");	//Mac test version
	//private File databaseFolder = new File("D:" + File.separator + "ShardBytes Music.sbmd");			//ShardBytes music database
	private File databaseJSON = new File(databaseFolder.toString() + File.separator + "data.sbmj");	//ShardBytes music JSON
	
	ArrayList<Song> allDatabaseSongs;	//TODO: Save & load these from file when there is no need to sync stuff
	ArrayList<Album> allDatabaseAlbums;
	
	public static SongDB getInstance(){
		return ourInstance;
	}
	
	private SongDB(){
		ServerUI.log(databaseFolder.toString());
		recreate();
		
	}
	
	private void recreate(){
		ArrayList<Song> allSongs = getSongList();
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
						
						albumSongs.add(song);
						album.setArtist(tags[0]);
						album.setGenre(tags[1]);
						album.setYear(Integer.parseInt(tags[2]));
						album.setAlbumArt(getAlbumArtFromID3Tag(song.getFile()));
						
					}
					albumSongs.add(song);
					
				}
				
			});
			album.setSongs(albumSongs);
			
		});
		
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
	
	public ArrayList<Song> getSongList(){
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
								songlist.add(new Song(tags[0], tags[1], tags[2], song));
								
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
	
}
