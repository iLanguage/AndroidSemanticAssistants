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
package info.semanticsoftware.semassist.android.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;

import info.semanticsoftware.semassist.android.service.SemAssistAuthenticator;
import info.semanticsoftware.semassist.android.utils.Constants;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import info.semanticsoftware.semassist.android.activity.R;
import info.semanticsoftware.semassist.android.encryption.CustomSSLSocketFactory;
import info.semanticsoftware.semassist.csal.ClientUtils;
import info.semanticsoftware.semassist.csal.XMLElementModel;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import java.security.KeyStore;

/**
 * Provides an activity to authenticate users.
 * @author Bahar Sateli
 */
public class AuthenticationActivity extends AccountAuthenticatorActivity{

	/** URL to send authentication request. */
	private String serverURL = null;
	/** Logging tag. */
	private final String TAG = "AuthenticationActivity";

	/** Called when the activity is created.
	 * @param bundle bundle */
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		this.setContentView(R.layout.user_credentials);
		readServerSettings();
	}

	/** Closes the activity when cancel button is pushed.
	 * @param view view */
	public void onCancelClick(View view) {
		this.finish();
	}

	/** Sends an authentication request when the save button is pushed.
	 * @param v view 
	 */
	public void onSaveClick(View v) {
		TextView tvUsername = (TextView) this.findViewById(R.id.uc_txt_username);
		TextView tvPassword = (TextView) this.findViewById(R.id.uc_txt_password);

		//Qualified username, i.e, user@semanticassistants.com
		String qUsername = tvUsername.getText().toString();
		String username = null;
		if(qUsername.indexOf("@") > 0){
			username = qUsername.substring(0, qUsername.indexOf("@"));
		}else{
			username = qUsername;
		}
		String password = tvPassword.getText().toString();

		//TODO do client-side validation like password length etc.
		String response = authenicate(username, password);
		if(!response.equals(Constants.AUTHENTICATION_FAIL)){
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			Editor editor = settings.edit();
			editor.putString("username", username);
			editor.putString("password", password);
			//FIXME replace this with a descent XML parser
			String acctype = "";
			int start = response.indexOf("<accType>");
			int end = response.indexOf("</accType>");
			if(start > -1 && end > -1){
				acctype = response.substring(start+"<accType>".length(), end);
			}
			editor.putString("acctype", acctype);
			String sessionId = "";
			start = response.indexOf("<sessionId>");
			end = response.indexOf("</sessionId>");
			if(start > -1 && end > -1){
				sessionId = response.substring(start+"<sessionId>".length(), end);
			}
			editor.putString("sessionId", sessionId);
			String reqNum = "";
			start = response.indexOf("<reqNum>");
			end = response.indexOf("</reqNum>");
			if(start > -1 && end > -1){
				reqNum = response.substring(start+"<reqNum>".length(), end);
			}
			editor.putString("reqNum", reqNum);
			boolean result = editor.commit();
			if(result){
				Toast.makeText(this, R.string.authenticationSuccess, Toast.LENGTH_LONG).show();
				String accountType = this.getIntent().getStringExtra("auth.token");
				if (accountType == null) {
					accountType = SemAssistAuthenticator.ACCOUNT_TYPE;
				}

				AccountManager accMgr = AccountManager.get(this);

				// Add the account to the Android Account Manager
				String accountName = username + "@semanticassistants.com";
				final Account account = new Account(accountName, accountType);
				accMgr.addAccountExplicitly(account, password, null);

				// Inform the caller (could be the Android Account Manager or the SA app) that the process was successful
				final Intent intent = new Intent();
				intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, accountName);
				intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType);
				intent.putExtra(AccountManager.KEY_AUTHTOKEN, accountType);
				this.setAccountAuthenticatorResult(intent.getExtras());
				this.setResult(RESULT_OK, intent);
				this.finish();
			}else{
				Toast.makeText(this, "Could not write the preferences.", Toast.LENGTH_LONG).show();
			}
		}else{
			Toast.makeText(this, R.string.authenticationFail, Toast.LENGTH_LONG).show();
		}
	}

	private String authenicate(String username, String password) {
		String uri = serverURL + "/user";
		System.out.println(uri);
		String request = "<authenticate><username>" + username + "</username><password>" + password + "</password></authenticate>";
		Representation representation = new StringRepresentation(request,MediaType.APPLICATION_XML);
		String serverResponse = null;

		if(serverURL.indexOf("https") < 0){
			Log.i(TAG, "Sending authentication request to " + uri);
			Representation response = new ClientResource(uri).post(representation);
			try {
				StringWriter writer = new StringWriter();
				response.write(writer);
				serverResponse = writer.toString();
				Log.i(TAG, "Authentication response: " + serverResponse);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			try{
				Log.i(TAG, "Sending authentication request to " + uri);
				HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
				DefaultHttpClient client = new DefaultHttpClient();
	
				SchemeRegistry registry = new SchemeRegistry();
				final KeyStore ks = KeyStore.getInstance("BKS");
				// NOTE: the keystore must have been generated with BKS 146 and not later
				final InputStream in = getApplicationContext().getResources().openRawResource(R.raw.clientkeystorenew);  
				try {
					ks.load(in,getString(R.string.keystorePassword).toCharArray());
				} finally {
					in.close();
				}
	
				SSLSocketFactory socketFactory = new CustomSSLSocketFactory(ks);
				socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
				registry.register(new Scheme("https", socketFactory, 443));
				SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
				DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());
	
				// Set verifier
				HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
	
				HttpPost post = new HttpPost(uri);
				post.setEntity(new StringEntity(representation.getText()));
	
				HttpResponse response = httpClient.execute(post);
				HttpEntity entity = response.getEntity();
				InputStream inputstream = entity.getContent();
				InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
				BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
	
				String string = null;
				while ((string = bufferedreader.readLine()) != null) {
					serverResponse += string;
				}
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		return serverResponse;
	}

	/**
	 * Reads the Semantic Assistants server URL from the user preferences. First, it looks
	 * into the application preference file. If there is no such preference key
	 * in the application preferences, it gets the Android-specific values from servers.xml.
	 */
	private void readServerSettings(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		serverURL = prefs.getString("selected_server_option", "-1");
		if(serverURL.equals("-1")){
			GlobalSettingsActivity.SERVERS_XML_FILE_PATH = getFilesDir().getAbsolutePath()+ File.separator + "servers.xml";
			ArrayList<XMLElementModel> defaultServer = GlobalSettingsActivity.getClientPreference("android", "server");
			serverURL = defaultServer.get(0).getAttribute().get(ClientUtils.XML_HOST_KEY) + ":" + defaultServer.get(0).getAttribute().get(ClientUtils.XML_PORT_KEY);
		}
	}
}
