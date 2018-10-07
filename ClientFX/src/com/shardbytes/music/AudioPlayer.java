package com.shardbytes.music;

import com.shardbytes.music.client.technicalUI.JFXPlayer;
import com.shardbytes.music.common.Song;
import javazoom.jl.player.Player;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AudioPlayer{
	
	private static AudioPlayer ourInstance = new AudioPlayer();
	public static AudioPlayer getInstance(){
		return ourInstance;
	}
	private AudioPlayer(){}
	
	private AudioInputStream inputStream;
	private AudioFormat format;
	private DataLine.Info dataInfo;
	private SourceDataLine line;
	private Clip clip;
	private Player player;
	
	private boolean playing = false;
	private boolean paused = false;
	private int currentPause = 0;
	
	public void preloadAsBytes(ByteArrayInputStream songByteStream) throws IOException, UnsupportedAudioFileException, LineUnavailableException{
		if(playing){
			stop();
		}
		
		inputStream = AudioSystem.getAudioInputStream(songByteStream);
		format = constructNewAudioFormat(inputStream.getFormat());
		dataInfo = new DataLine.Info(SourceDataLine.class, format);
		line = (SourceDataLine)AudioSystem.getLine(dataInfo);
		line.open();
		
	}
	
	public void preloadAsBytes(ByteArrayInputStream songByteStream, Song songData) throws UnsupportedAudioFileException, IOException, LineUnavailableException{
		preloadAsBytes(songByteStream);
		JFXPlayer.getController().setSongData(songData);
	}
	
	public void getFromStream(InputStream stream) throws IOException, UnsupportedAudioFileException, LineUnavailableException{
		if(playing){
			stop();
		}
		
		inputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(stream));
		format = constructNewAudioFormat(inputStream.getFormat());
		dataInfo = new DataLine.Info(SourceDataLine.class, format);
		line = (SourceDataLine)AudioSystem.getLine(dataInfo);
		line.open();
		
	}
	
	public void play(){
		currentPause = 0;
		playing = true;
		line.start();
		
	}
	
	public void stop(){
		currentPause = 0;
		playing = false;
		line.stop();
		
	}
	
	public void pause(){
		if(playing && !paused){
			paused = true;
			currentPause = line.getFramePosition();
			line.stop();
			
		}else if(playing && paused){
			paused = false;
			line.start();
			
		}
		
	}
	
	private AudioFormat constructNewAudioFormat(AudioFormat originalFormat){
		final int channels = originalFormat.getChannels();
		final float sampleRate = originalFormat.getSampleRate();
		
		return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sampleRate, 16, channels, channels * 2, sampleRate, false);
				
	}
	
}
