package info.semanticsoftware.semassist.android.restlet;

import info.semanticsoftware.semassist.android.encryption.EncryptionUtils;
import info.semanticsoftware.semassist.android.prefs.PrefUtils;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import  android.util.Base64;

public class RequestRepresentation {
	
	private String serviceName;
	private String input;
	private Map<String, String> params = new HashMap<String, String>();
	
	public RequestRepresentation(String iServiceName, Map<String,String> iParams, String input){
		this.serviceName = iServiceName;
		this.params = iParams;
		this.input = input;
	}
	
	public String getXML(){
		PrefUtils prefUtil = PrefUtils.getInstance();
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		buffer.append("<invocation>");
		String username = prefUtil.getUsername();
		if(username != null){
			username = username.substring(0, username.indexOf("@"));
			buffer.append("<username>").append(username).append("</username>");
		}
		
		buffer.append("<serviceName>").append(serviceName).append("</serviceName>");	
		if(params !=null){
			Set<String> paramNames = params.keySet();
			for(String name:paramNames){
				buffer.append("<param>");
					buffer.append("<name>").append(name).append("</name>");
					buffer.append("<value>");
						params.get(name);
					buffer.append("</value>");
				buffer.append("</param>");
			}
		}
				
		// get the session key to encrypt input data
		byte[] sessionKey = EncryptionUtils.getInstance().getSessionKey();
		
		System.out.println("Original Session key: " + sessionKey);
		// encrypted secret session key
		String sessionKeyString = EncryptionUtils.getInstance().encryptSessionKey(sessionKey);
				
		if(sessionKeyString != null){
			buffer.append("<sessionKey>").append(sessionKeyString).append("</sessionKey>");
		}
		
		buffer.append("<input><![CDATA[");
		
		byte[] encryptedBytes = EncryptionUtils.getInstance().encryptInputData(input, sessionKey);
		String encryptedtString = Base64.encodeToString(encryptedBytes, android.util.Base64.DEFAULT);
		
		System.out.println("Encrypted text: " + encryptedtString);
		buffer.append(encryptedtString);
		buffer.append("]]></input>");
		
        buffer.append("</invocation>");
        return buffer.toString();
	}

}
