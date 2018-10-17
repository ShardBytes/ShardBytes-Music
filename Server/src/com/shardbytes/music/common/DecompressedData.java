package com.shardbytes.music.common;

import com.shardbytes.music.common.javasound.SerializableAudioFormat;

import java.io.Serializable;

public class DecompressedData implements Serializable{

	private SerializableAudioFormat audioFormat;
	private byte[] bytes;

	public SerializableAudioFormat getAudioFormat(){
		return audioFormat;
	}

	public void setAudioFormat(SerializableAudioFormat audioFormat){
		this.audioFormat = audioFormat;
	}

	public byte[] getBytes(){
		return bytes;
	}

	public void setBytes(byte[] bytes){
		this.bytes = bytes;
	}

}
