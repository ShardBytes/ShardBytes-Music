package com.shardbytes.music.client.audio;

import com.shardbytes.music.client.technicalUI.JFXPlayer;
import com.shardbytes.music.common.Song;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

public class AudioPlayer{
	
	private static AudioPlayer ourInstance = new AudioPlayer();
	public static AudioPlayer getInstance(){
		return ourInstance;
	}
	private AudioPlayer(){}
	
	private Clip clip;
	
	public void load(byte[] songBytes, Song songData) throws IOException, LineUnavailableException{
		JFXPlayer.getController().setSongData(songData);
		
		
		
		clip = AudioSystem.getClip();
		clip.open();
		
	}
	
	public void play(){
		clip.start();
	}
	
	public void pause(){
		System.out.println("us pos = " + clip.getMicrosecondPosition());
		clip.stop();
	}
	
	public void stop(){
		clip.stop();
	}
	
}
