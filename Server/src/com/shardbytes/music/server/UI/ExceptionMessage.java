package com.shardbytes.music.server.UI;

import java.util.Objects;

class ExceptionMessage{
	
	private boolean dismissed = false;
	private final String message;
	
	ExceptionMessage(String message){
		this.message = message;
	}
	
	public boolean isDismissed(){
		return dismissed;
	}
	
	public String getMessage(){
		return message;
	}
	
	public void dismiss(){
		dismissed = true;
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		ExceptionMessage that = (ExceptionMessage)o;
		return dismissed == that.dismissed && Objects.equals(message, that.message);
	}
	
	@Override
	public int hashCode(){
		return Objects.hash(dismissed, message);
	}
	
	@Override
	public String toString(){
		return "ExceptionMessage{" + "dismissed=" + dismissed + ", message='" + message + '\'' + '}';
	}
	
}
