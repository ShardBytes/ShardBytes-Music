package com.shardbytes.music.server.Database;

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
	
	private File databaseFolder = new File("D:" + File.separator + "ShardBytes Music.sbmd");			//ShardBytes music database
	
	public static SongDB getInstance(){
		return ourInstance;
	}
	
	private SongDB(){}
	
	private String readFile(File file) throws IOException{
		byte[] bytes = Files.readAllBytes(Paths.get(file.toURI()));
		
		return new String(bytes, StandardCharsets.UTF_8);
		
	}
	
	private String getStringFromID3Tag(File mp3file, FieldKey tagKey){
		try{
			AudioFile song = AudioFileIO.read(mp3file);
			Tag tag = song.getTag();
			
			return tag.getFirst(tagKey);
			
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
								songlist.add(new Song(getStringFromID3Tag(song, FieldKey.TITLE), getStringFromID3Tag(song, FieldKey.ARTIST), getStringFromID3Tag(song, FieldKey.ALBUM)));
							}
							
						}
						
					}
					
				}
				
			}
			
		}
		return songlist;
		
	}
	
}
