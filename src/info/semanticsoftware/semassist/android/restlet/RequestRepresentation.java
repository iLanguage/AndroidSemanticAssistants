package info.semanticsoftware.semassist.android.restlet;

import info.semanticsoftware.semassist.android.encryption.EncryptionUtils;
import info.semanticsoftware.semassist.android.prefs.PrefUtils;

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

		EncryptionUtils utils = EncryptionUtils.getInstance();

		// get the session key to encrypt input data
		byte[] sessionKey = utils.getSessionKey();

		System.out.println("Original Session key: " + sessionKey + " " + sessionKey.length + " bytes");
		// encrypted secret session key
		//String sessionKeyString = EncryptionUtils.getInstance().encryptSessionKey(sessionKey);
		String unencryptedSessionKey = Base64.encodeToString(sessionKey, Base64.DEFAULT);

		/*if(sessionKeyString != null){
			buffer.append("<sessionKey>").append(sessionKeyString).append("</sessionKey>");
		}*/

		/******** DBG ***********/

		//String encryptedKeyString = utils.encryptSessionKey(sessionKey);

		//buffer.append("<sessionKey>").append(unencryptedSessionKey).append("</sessionKey>");
		//buffer.append("<test>").append(encryptedKeyString).append("</test>");

		//String encryptedKeyString = utils.encryptTest();
		String encryptedKeyString = utils.encryptMe(sessionKey);
		buffer.append("<sessionKey>").append(encryptedKeyString).append("</sessionKey>");

		buffer.append("<input><![CDATA[");

		//byte[] encryptedBytes = EncryptionUtils.getInstance().encryptInputData(input, sessionKey);
		//String encryptedtString = Base64.encodeToString(encryptedBytes, android.util.Base64.DEFAULT);

		String temp = utils.encryptInputData(input, sessionKey);

		System.out.println("Encrypted text: " + temp);

		buffer.append(temp);
		buffer.append("]]></input>");

		buffer.append("<sessionIV>").append(utils.getIV()).append("</sessionIV>");

		buffer.append("</invocation>");
		return buffer.toString();
	}
}
