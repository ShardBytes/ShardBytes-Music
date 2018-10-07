package com.shardbytes.music.client.audio;

import com.shardbytes.music.client.technicalUI.JFXPlayer;
import com.shardbytes.music.common.Song;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.io.ByteArrayInputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AudioPlayer{
	
	private static AudioPlayer ourInstance = new AudioPlayer();
	public static AudioPlayer getInstance(){
		return ourInstance;
	}
	private AudioPlayer(){}
	
	private ExecutorService service = Executors.newCachedThreadPool();
	private Player player;
	
	private ByteArrayInputStream stream;
	private boolean playing = false;
	private boolean paused = false;
	private int currentPause = 0;
	
	public void preloadAsBytes(ByteArrayInputStream songByteStream) throws JavaLayerException{
		if(playing){
			stopPrivate();
		}
		stream = songByteStream;
		player = new Player(stream);
		
	}
	
	public void preloadAsBytes(ByteArrayInputStream songByteStream, Song songData) throws JavaLayerException{
		preloadAsBytes(songByteStream);
		JFXPlayer.getController().setSongData(songData);
	}
	
	public void play(){
		service.submit(this::playPrivate);
	}
	
	public void pause(){
		service.submit(this::pausePrivate);
	}
	
	private void playPrivate(){
		try{
			currentPause = 0;
			playing = true;
			player.play();
		}catch(JavaLayerException e){
			System.err.println(e.getMessage());
		}
		
	}
	
	private void stopPrivate(){
		currentPause = 0;
		playing = false;
		player.close();
		
	}
	
	private void pausePrivate(){
		try{
			if(playing && !paused){
				paused = true;
				currentPause = player.getPosition();
				player.close();
				
			}else if(playing && paused){
				paused = false;
				player = new Player(stream);
				player.play(currentPause);
				
			}
			
		}catch(JavaLayerException e){
			System.err.println(e.getMessage());
		}
		
	}
	
}
