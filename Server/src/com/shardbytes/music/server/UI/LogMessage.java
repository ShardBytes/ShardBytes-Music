package com.shardbytes.music.server.UI;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LogMessage{
	
	private String message;
	private Date time;
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss");
	
	public LogMessage(String message){
		this.message = message;
		this.time = new Date();
	}
	
	public String getMessage(){
		return message;
	}
	
	public String getFormattedDate(){
		return "[" + dateFormatter.format(time) + "] ";
	}
	
	@Override
	public String toString(){
		return getFormattedDate() + getMessage();
	}
	
}
