package com.shardbytes.music.client.audio;

import com.shardbytes.music.client.technicalUI.JFXPlayer;
import com.shardbytes.music.common.Song;
import javafx.scene.media.MediaPlayer;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AudioPlayer{
	
	private static AudioPlayer ourInstance = new AudioPlayer();
	public static AudioPlayer getInstance(){
		return ourInstance;
	}
	private AudioPlayer(){
		try{
			clip = AudioSystem.getClip();
		}catch(LineUnavailableException e){
			System.err.println(e.getMessage());
		}
		
	}
	
	private ExecutorService service = Executors.newCachedThreadPool();
	private Clip clip;
	
	private ByteArrayInputStream stream;
	private boolean playing = false;
	private boolean paused = false;
	private int currentPause = 0;
	
	public void preloadAsBytes(ByteArrayInputStream songByteStream) throws IOException, UnsupportedAudioFileException, LineUnavailableException{
		if(playing){
			stopPrivate();
		}
		
		System.out.println(Arrays.toString(AudioSystem.getAudioFileTypes()));
		
		stream = songByteStream;
		clip.open(AudioSystem.getAudioInputStream(stream));
		
	}
	
	public void preloadAsBytes(ByteArrayInputStream songByteStream, Song songData) throws UnsupportedAudioFileException, IOException, LineUnavailableException{
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
		currentPause = 0;
		playing = true;
		clip.start();
		
	}
	
	private void stopPrivate(){
		currentPause = 0;
		playing = false;
		clip.stop();
		
	}
	
	private void pausePrivate(){
		if(playing && !paused){
			paused = true;
			currentPause = clip.getFramePosition();
			clip.stop();
			
		}else if(playing && paused){
			paused = false;
			clip.setFramePosition(currentPause);
			clip.start();
			
		}
		
	}
	
}
