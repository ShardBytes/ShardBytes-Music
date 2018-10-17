package com.shardbytes.music.common.javasound;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SerializableAudioFormat implements Serializable{

	private transient AudioFormat format;

	public SerializableAudioFormat(AudioFormat format){
		this.format = format;
	}

	public AudioFormat getFormat(){
		return format;
	}

	private void writeObject(ObjectOutputStream out) throws IOException{
		out.defaultWriteObject();
		out.writeObject(new SerializableEncoding(format.getEncoding()));
		out.writeFloat(format.getSampleRate());
		out.writeInt(format.getSampleSizeInBits());
		out.writeInt(format.getChannels());
		out.writeInt(format.getFrameSize());
		out.writeFloat(format.getFrameRate());
		out.writeBoolean(format.isBigEndian());

	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
		in.defaultReadObject();
		format = new AudioFormat(((SerializableEncoding)in.readObject()).getEncoding(), in.readFloat(), in.readInt(), in.readInt(), in.readInt(), in.readFloat(), in.readBoolean());

	}

}
