package com.shardbytes.music.client.audio;

import com.shardbytes.music.client.technicalUI.JFXPlayer;
import com.shardbytes.music.client.ui.PlayerController;
import com.shardbytes.music.common.DecompressedData;
import javafx.stage.Stage;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

public class AudioPlayer{
	
	private static AudioPlayer ourInstance = new AudioPlayer();
	public static AudioPlayer getInstance(){
		return ourInstance;
	}
	private AudioPlayer(){
		Thread timeThread = new Thread(() -> {
			Stage stage = JFXPlayer.getStage();
			PlayerController controller = JFXPlayer.getController();
			
			try{
				while(true){
					if(!stage.isIconified()){
						if(clip != null){
							if(clip.isRunning()){
								controller.setTime(clip.getMicrosecondPosition(), clip.getMicrosecondLength());
								System.out.println("time set to " + clip.getMicrosecondPosition());
							}
							
						}
						
					}
					Thread.sleep(33);
					
				}
				
			}catch(InterruptedException e){
				System.err.println(e.getMessage());
				
			}
			
		});
		
		timeThread.setDaemon(true);
		timeThread.setName("timeThread");
		timeThread.start();
		
	}
	
	private Clip clip;
	private long stoppedOn = 0;
	boolean pauseToggle = false;
	
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
		if(clip != null){
			if(clip.isOpen()){
				if(pauseToggle){
					pauseToggle = false;
					clip.setMicrosecondPosition(stoppedOn);
					clip.start();
					
				}else{
					pauseToggle = true;
					stoppedOn = clip.getMicrosecondPosition();
					clip.stop();
					
				}
				
			}
			
		}
		
	}
	
	public void stop(){
		stoppedOn = 0;
		pauseToggle = false;
		clip.stop();
		clip.flush();
		clip.close();
	}
	
}
