package com.shardbytes.music.server.Database;

import com.shardbytes.music.server.UI.ServerUI;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class SongDB{
	
	/*
	 * TODO: Just a friendly reminder to change the DB version if anything has changed :)
	 */
	private final static String SONG_DB_VERSION = "1.0.0";
	
	private static SongDB ourInstance = new SongDB();
	
	private File databaseFolder = new File("D:" + File.separator + "ShardBytes Music.sbmd");			//ShardBytes music database
	private File databaseJSON = new File(databaseFolder.toString() + File.separator + "songdb.sbmj");	//ShardBytes music JSON
	
	public static SongDB getInstance(){
		return ourInstance;
	}
	
	private SongDB(){
		ServerUI.log("SongDB init");
		
		if(!databaseFolder.exists() || !databaseFolder.isDirectory()){
			createNewDatabase(databaseFolder, databaseJSON, true);
		}
		
		if(!getDatabaseVersion(databaseJSON).equals(SONG_DB_VERSION)){
			ServerUI.log("Rebuilding the database");
			createNewDatabase(databaseFolder, databaseJSON, false);
		}
		
		sync(databaseFolder, databaseJSON);
		
	}
	
	private String getDatabaseVersion(File dbfile){
		try{
			JSONObject json = new JSONObject(readFile(dbfile));
			return json.get("version").toString();
			
		}catch(IOException | JSONException e){
			ServerUI.addExceptionMessage(e.getMessage());
			
		}
		return "Invalid JSON file";
		
	}
	
	private String readFile(File file) throws IOException{
		byte[] bytes = Files.readAllBytes(Paths.get(file.toURI()));
		
		return new String(bytes, StandardCharsets.UTF_8);
		
	}
	
	private void createNewDatabase(File dbfolder, File dbjson, boolean removeAll){
		try{
			if(removeAll){
				dbfolder.delete();
				dbfolder.mkdirs();
			}
			
			dbjson.delete();
			dbjson.createNewFile();
			
			JSONObject emptyJSON = new JSONObject();
			emptyJSON.put("version", SONG_DB_VERSION);
			
			JSONArray music = new JSONArray();
			emptyJSON.put("music", music);
			
			PrintWriter writer = new PrintWriter(dbjson);
			writer.print(emptyJSON.toString(4));
			writer.flush();
			writer.close();
			
		}catch(IOException e){
			e.printStackTrace();
			ServerUI.addExceptionMessage(e.getMessage());
		}
		
	}
	
	private void sync(File dbfolder, File dbjson){
		try{
			//Read all artists folders
			File[] artistDirectories = dbfolder.listFiles(File::isDirectory);
			JSONObject json = new JSONObject(readFile(dbjson));
			JSONArray music = json.getJSONArray("music");
			
			//Read all albums and songs for each artist
			for(File artistDir : artistDirectories){
				File[] albumDirectories = artistDir.listFiles(File::isDirectory);
				JSONObject artist = new JSONObject();
				JSONArray albums = new JSONArray();
				artist.put("name", artistDir.getName());
				
				for(File albumDir : albumDirectories){
					JSONObject album = new JSONObject();
					JSONArray albumSongs = new JSONArray();
					album.put("albumTitle", albumDir.getName());
					
					File[] songs = albumDir.listFiles();
					for(File songFile : songs){
						JSONObject song = new JSONObject();
						song.put("songTitle", getStringFromID3Tag(songFile, FieldKey.TITLE));
						song.put("songPath", songFile.getAbsolutePath());
						albumSongs.put(song);
						
					}
					album.put("songs", albumSongs);
					albums.put(album);
				
				}
				artist.put("albums", albums);
				music.put(artist);
				
			}
			
			PrintWriter writer = new PrintWriter(dbjson);
			writer.print(json.toString(4));
			writer.flush();
			writer.close();
			
		}catch(IOException e){
			ServerUI.addExceptionMessage(e.getMessage());
		}
		
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
	
	private File dbFolder(){
		return databaseFolder;
	}
	
	private File dbJson(){
		return databaseJSON;
	}
	
	public ArrayList<Song> getSongList(){
		ArrayList<Song> songlist = new ArrayList<>();
		File[] artists = databaseFolder.listFiles(File::isDirectory);
		
		for(File artist : artists){
			File[] albums = artist.listFiles(File::isDirectory);
			
			for(File album : albums){
				File[] songs = album.listFiles(pathname -> {
					
					if(!pathname.getName().endsWith(".mp3")){
						return false;
					}
					return true;
					
				});
				
				for(File song : songs){
					songlist.add(new Song(getStringFromID3Tag(song, FieldKey.TITLE), getStringFromID3Tag(song, FieldKey.ALBUM_ARTIST), getStringFromID3Tag(song, FieldKey.ALBUM)));
				}
				
			}
			
		}
		return songlist;
		
	}
	
}
