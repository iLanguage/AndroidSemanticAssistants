/*
* Semantic Assistants -- http://www.semanticsoftware.info/semantic-assistants
* 
* Copyright (C) 2014 Semantic Software Lab, http://www.semanticsoftware.info
* Rene Witte
* Bahar Sateli
* 
* This file is part of the Semantic Assistants architecture, and is 
* free software, licensed under the GNU Lesser General Public License 
* as published by the Free Software Foundation, either version 3 of 
* the License, or (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package info.semanticsoftware.semassist.android.encryption;

import info.semanticsoftware.semassist.android.prefs.PrefUtils;
import info.semanticsoftware.semassist.android.utils.Constants;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.app.Application;
import android.util.Base64;
import android.util.Log;

public class EncryptionUtils {

	private static EncryptionUtils instance = null;
	private byte[] siv = null;
	private Application application = null;

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

	public String encryptInputData(String input, byte[] sessionKey) {
		byte[] encryptedText = null;
		try {
			Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
			SecretKeySpec keySpec = new SecretKeySpec(sessionKey, "AES");
			cipher.init(Cipher.ENCRYPT_MODE, keySpec);
			siv = cipher.getIV();
			encryptedText = cipher.doFinal(input.getBytes("UTF-8"));
			return Base64.encodeToString(encryptedText, Base64.DEFAULT);

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
		return "";
	}

	public String getIV(){
		return Base64.encodeToString(siv, Base64.DEFAULT);
	}

	public String encryptSessionKey(byte[] sessionKey){
		String encryptedSessionKeyString = null;
		//STEP 1: Get the public key component and create it from configuration
		PrefUtils prefUtils = PrefUtils.getInstance(application);
		PublicKey pubKey = prefUtils.getPublicKey();

		//STEP 2: Use the public key to encrypt the secret session key
		try {
			/*
			 With CBC or CFB modes, you must set an IV before encrypting or decrypting. 
			 Because there are no interblock dependencies in ECB mode, you do not need to set an IV. 
			 In fact, if you try to use the setIV() or generateIV() methods with a symmetric cipher 
			 that uses ECB mode, you will get an exception.
			 see http://www.rsa.com/products/bsafe/documentation/cryptoj35html/doc/dev_guide/group__CJ__SYM__ECB.html
			 */
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE,pubKey);
			byte[] cipherDataBytes = cipher.doFinal(sessionKey);
			encryptedSessionKeyString = Base64.encodeToString(cipherDataBytes, Base64.DEFAULT);
			Log.d(Constants.TAG, "dbg: " + encryptedSessionKeyString);
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

	public String encryptTest() {
		PrefUtils prefUtils = PrefUtils.getInstance(application);
		PublicKey pubKey = prefUtils.getPublicKey();
		Log.d(Constants.TAG, "PublicKey remade: " + pubKey);
		try {

			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE,pubKey);
			byte[] cipherDataBytes = cipher.doFinal("This is a test".getBytes("UTF-8"));
			String encryptedSessionKeyString = Base64.encodeToString(cipherDataBytes, Base64.DEFAULT);
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
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String encryptMe(byte[] sessionKey) {
		String encryptedSessionKeyString = null;
		//STEP 1: Get the public key component and create it from configuration
		PrefUtils prefUtils = PrefUtils.getInstance(application);
		PublicKey pubKey = prefUtils.getPublicKey();

		//STEP 2: Use the public key to encrypt the secret session key
		try {
			/*
			 With CBC or CFB modes, you must set an IV before encrypting or decrypting. 
			 Because there are no interblock dependencies in ECB mode, you do not need to set an IV. 
			 In fact, if you try to use the setIV() or generateIV() methods with a symmetric cipher 
			 that uses ECB mode, you will get an exception.
			 see http://www.rsa.com/products/bsafe/documentation/cryptoj35html/doc/dev_guide/group__CJ__SYM__ECB.html
			 */
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
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
}
