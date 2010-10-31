package info.semanticsoftware.semassist.client.eclipse.utils;

import java.util.Date;

public class Log {
	
	private String message;
	
	public Log(String message){
		Date date = new Date();
		System.out.println();
		this.message = "[" + date.toString() + "] >> " + message;
	}
	
	public String getMessage(){
		return message;
	}

}
