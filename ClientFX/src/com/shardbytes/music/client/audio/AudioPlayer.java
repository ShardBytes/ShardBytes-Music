package com.shardbytes.music.client.audio;

import com.shardbytes.music.client.technicalUI.JFXPlayer;
import com.shardbytes.music.common.DecompressedData;
import com.shardbytes.music.common.Song;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

public class AudioPlayer{
	
	private static AudioPlayer ourInstance = new AudioPlayer();
	public static AudioPlayer getInstance(){
		return ourInstance;
	}
	private AudioPlayer(){}
	
	private Clip clip;
	
	public void load(DecompressedData songBytes) throws LineUnavailableException{
		if(clip == null){
			clip = AudioSystem.getClip();
		}
		
		stop();
		clip.open(songBytes.getAudioFormat().getFormat(), songBytes.getBytes(), 0, songBytes.getBytes().length);
		
	}
	
	public void play(){
		clip.start();
	}
	
	public void pause(){
		System.out.println("us pos = " + clip.getMicrosecondPosition());
	}
	
	public void stop(){
		clip.stop();
		clip.flush();
		clip.close();
	}
	
}
