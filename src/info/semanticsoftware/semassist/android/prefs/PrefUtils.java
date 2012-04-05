package info.semanticsoftware.semassist.android.prefs;

import info.semanticsoftware.semassist.android.application.SemAssistApp;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PrefUtils{
	
	private static PrefUtils instance = null;
	protected PrefUtils(){
    	// Defeat instantiation
	}
	
	public static PrefUtils getInstance(){
		if (instance == null){
			instance = new PrefUtils();
		}
		return instance;
	}
	
	public PublicKey getPublicKey(){
    	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(SemAssistApp.getInstance().getContext());
		String modValue = settings.getString("modValue","");
		if(!modValue.equals("")){
			try {
				BigInteger modulus = new BigInteger(modValue);
				RSAPublicKeySpec newSpec = new RSAPublicKeySpec(modulus, new BigInteger("65537"));
				KeyFactory fact = KeyFactory.getInstance("RSA");
				PublicKey pubkey = fact.generatePublic(newSpec);
				System.out.println(pubkey.toString());
				return pubkey;
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (InvalidKeySpecException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public String getUsername(){
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(SemAssistApp.getInstance().getContext());
		return settings.getString("username",null);
	}

}
