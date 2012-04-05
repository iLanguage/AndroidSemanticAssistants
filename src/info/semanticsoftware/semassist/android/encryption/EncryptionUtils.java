package info.semanticsoftware.semassist.android.encryption;

import info.semanticsoftware.semassist.android.prefs.PrefUtils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;


public class EncryptionUtils {
	
	private static EncryptionUtils instance = null;
	private byte[] siv = null;
	
	protected EncryptionUtils(){
		// defeat instantiation
	}
	
	public static EncryptionUtils getInstance(){
		if (instance == null){
			instance = new EncryptionUtils();
		}
		
		return instance;
	}
	
	/**
	 * Returns a 128-bit symmetric key for server-side encryption
	 * @return a random key byte array or null if exception is thrown
	 */
	public byte[] getSessionKey(){
		byte[] key = null;
		try{
			KeyGenerator keyGen = KeyGenerator.getInstance("AES");
			keyGen.init(128);
			
			SecretKey secretKey = keyGen.generateKey();
			key = secretKey.getEncoded();
			return key;
		} catch (Exception e){
			e.printStackTrace();
		}
		return key;
	}
	
	/**
	 * Encrypts the secret session key with the saved public key
	 * so that nobody except the server can use it to encrypt
	 * the service output.
	 * @param sessionKey secret session key
	 * @return encrypted secret session key string
	 * */
	public String encryptSessionKey(byte[] sessionKey){
		String encryptedSessionKeyString = null;
		//STEP 1: Get the public key component and create it from configuration
		PrefUtils prefUtils = PrefUtils.getInstance();
		PublicKey pubKey = prefUtils.getPublicKey();
		
		//STEP 2: Use the public key to encrypt the secret session key
		try {
			Cipher cipher;
			/*
			 With CBC or CFB modes, you must set an IV before encrypting or decrypting. 
			 Because there are no interblock dependencies in ECB mode, you do not need to set an IV. 
			 In fact, if you try to use the setIV() or generateIV() methods with a symmetric cipher 
			 that uses ECB mode, you will get an exception.
			 see http://www.rsa.com/products/bsafe/documentation/cryptoj35html/doc/dev_guide/group__CJ__SYM__ECB.html
			 */
			cipher = Cipher.getInstance("RSA/ECB/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE,pubKey);
			byte[] cipherDataBytes = cipher.doFinal(sessionKey);
			encryptedSessionKeyString = Base64.encodeToString(cipherDataBytes, Base64.DEFAULT);
			return encryptedSessionKeyString;
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return encryptedSessionKeyString;
	}
	
	/**
	 * Encrypts the secret session key with the saved public key
	 * so that nobody except the server can use it to encrypt
	 * the service output.
	 * @param sessionKey secret session key
	 * @return encrypted secret session key string
	 * */
	public String encryptData(byte[] input){
		String encryptedSessionKeyString = null;
		//STEP 1: Get the public key component and create it from configuration
		PrefUtils prefUtils = PrefUtils.getInstance();
		PublicKey pubKey = prefUtils.getPublicKey();
		
		//STEP 2: Use the public key to encrypt the secret session key
		try {
			Cipher cipher;
			cipher = Cipher.getInstance("RSA/ECB/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE,pubKey);
			byte[] cipherDataBytes = cipher.doFinal(input);
			encryptedSessionKeyString = Base64.encodeToString(cipherDataBytes, Base64.DEFAULT);
			return encryptedSessionKeyString;
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return encryptedSessionKeyString;
	}
	
	/* client-side
	// encryptedText and IV will come from server, we have secretKey in the client
	public String decryptMessage(String encryptedText, byte[] secretSessionKey, byte[] iv){
		String strOriginal = "";
		try {
			SecretKeySpec keySpec = new SecretKeySpec(secretSessionKey, "AES");
			Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv));
			byte[] original = cipher.doFinal(Base64.decode(encryptedText, Base64.DEFAULT));
			strOriginal = new String(original, "UTF-8");
			return strOriginal;
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}
		return strOriginal;
	}*/
	
	/**
	 * Encrypts the input data using the provided public key
	 * @param input data string to encrypt
	 * @param key public key to use in encryption
	 * @return byte[] encrypted byte array
	 * */
	public byte[] encryptData(String input, PublicKey key){
		byte[] cipherData = null;
		try {
			Cipher cipher;
			cipher = Cipher.getInstance("RSA/ECB/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE,key);
			cipherData = cipher.doFinal(input.getBytes("UTF-8"));
			return cipherData;
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public byte[] encryptInputData(String input, byte[] sessionKey) {
		byte[] encryptedText = null;
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
			SecretKeySpec keySpec = new SecretKeySpec(sessionKey, "AES");
			cipher.init(Cipher.ENCRYPT_MODE, keySpec);
			encryptedText = cipher.doFinal(input.getBytes("UTF-8"));
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}	
		return encryptedText;
	}
	
}

