package com.shardbytes.music.common.javasound;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SerializableEncoding implements Serializable{

	private transient AudioFormat.Encoding encoding;

	public SerializableEncoding(AudioFormat.Encoding encoding){
		this.encoding = encoding;
	}

	public AudioFormat.Encoding getEncoding(){
		return encoding;
	}

	private void writeObject(ObjectOutputStream out) throws IOException{
		out.defaultWriteObject();
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
		in.defaultReadObject();
		encoding = ((SerializableEncoding)in.readObject()).getEncoding();

	}

}
