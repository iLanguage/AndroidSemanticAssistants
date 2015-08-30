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
package info.semanticsoftware.semassist.android.prefs;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class PrefUtils{

	private static String TAG = "PrefUtils";
	private static PrefUtils instance = null;
	
	private Application application = null;
	
	protected PrefUtils(){
		// Defeat instantiation
	}

	public static PrefUtils getInstance(Application app){
		if (instance == null){
			instance = new PrefUtils();
			instance.application = app;
		}
		return instance;
	}

	public PublicKey getPublicKey(){
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(application.getApplicationContext());
		String modValue = settings.getString("modValue","");
		if(!modValue.equals("")){
			try {
				BigInteger modulus = new BigInteger(modValue);
				RSAPublicKeySpec newSpec = new RSAPublicKeySpec(modulus, new BigInteger("65537"));
				KeyFactory fact = KeyFactory.getInstance("RSA");
				PublicKey pubkey = fact.generatePublic(newSpec);
				Log.i(TAG, pubkey.toString());
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
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(application.getApplicationContext());
		return settings.getString("username",null);
	}

	public String getPassword(){
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(application.getApplicationContext());
		return settings.getString("password",null);
	}

	public String getSessionId() {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(application.getApplicationContext());
		return settings.getString("sessionId",null);
	}
}
