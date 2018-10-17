package com.shardbytes.music.common;

import javax.sound.sampled.AudioFormat;
import java.io.Serializable;

public class DecompressedData implements Serializable{

	private AudioFormat audioFormat;
	private byte[] bytes;

	public AudioFormat getAudioFormat(){
		return audioFormat;
	}

	public void setAudioFormat(AudioFormat audioFormat){
		this.audioFormat = audioFormat;
	}

	public byte[] getBytes(){
		return bytes;
	}

	public void setBytes(byte[] bytes){
		this.bytes = bytes;
	}

}
