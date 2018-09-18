package com.shardbytes.music;

import com.shardbytes.music.client.JFXPlayer;
import com.shardbytes.music.common.Song;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
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
	
	public void preloadAsBytes(ByteArrayInputStream songByteStream) throws IOException, UnsupportedAudioFileException, LineUnavailableException, JavaLayerException{
		if(playing){
			stop();
		}
		
		inputStream = AudioSystem.getAudioInputStream(songByteStream);
		//format = constructNewAudioFormat(inputStream.getFormat());
		//dataInfo = new DataLine.Info(SourceDataLine.class, format);
		//clip = AudioSystem.getClip();
		//clip.open(inputStream);
		
		player = new Player(inputStream);
		
	}
	
	public void preloadAsBytes(ByteArrayInputStream songByteStream, Song songData) throws UnsupportedAudioFileException, IOException, LineUnavailableException, JavaLayerException{
		preloadAsBytes(songByteStream);
		JFXPlayer.getController().setSongData(songData);
	}
	
	public void play() throws JavaLayerException{
		currentPause = 0;
		playing = true;
		//clip.start();
		player.play();
	}
	
	public void stop(){
		currentPause = 0;
		playing = false;
		//clip.stop();
		player.close();
	}
	
	public void pause() throws JavaLayerException{
		if(playing && !paused){
			paused = true;
			currentPause = player.getPosition();
			
			player.close();
			
		}else if(playing && paused){
			paused = false;
			
			player = new Player(inputStream);
			player.play(currentPause);
			
		}
		
	}
	
	private AudioFormat constructNewAudioFormat(AudioFormat originalFormat){
		final int channels = originalFormat.getChannels();
		final float sampleRate = originalFormat.getSampleRate();
		
		return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sampleRate, 16, channels, channels * 2, sampleRate, false);
				
	}
	
}
