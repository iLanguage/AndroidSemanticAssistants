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
package info.semanticsoftware.semassist.android.service;

import info.semanticsoftware.semassist.android.activity.GlobalSettingsActivity;
import info.semanticsoftware.semassist.android.activity.SemanticResultsActivity;
import info.semanticsoftware.semassist.android.intents.ServiceIntent;
import info.semanticsoftware.semassist.android.intents.ServiceIntentFactory;
import info.semanticsoftware.semassist.android.utils.Constants;
import info.semanticsoftware.semassist.csal.ClientUtils;
import info.semanticsoftware.semassist.csal.XMLElementModel;

import java.io.File;
import java.util.ArrayList;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class SemanticAssistantsService extends IntentService{

	private final String TAG = "SA Service";

	public SemanticAssistantsService(){
		super("Semantic Assistants Service");
	}

	public SemanticAssistantsService(String name) {
		super(name);
	}

	public void onCreate(){
		super.onCreate();
		Log.i(TAG, "Semantic Assistants service called.");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String input = intent.getExtras().getString(Intent.EXTRA_TEXT);
		String action = intent.getAction();
		intent.setAction(null);
		SharedPreferences prefs = this.getSharedPreferences("info.semanticsoftware.semassist.android.activity_preferences", MODE_MULTI_PROCESS);
		String serverURL = prefs.getString("selected_server_option", "-1");
		Log.d(Constants.TAG, "From prefs: " + serverURL);

		if(serverURL.equals("-1")){
			Log.i(TAG, "Not in prefs. Reading from XML");
			GlobalSettingsActivity.SERVERS_XML_FILE_PATH = getFilesDir().getAbsolutePath()+ File.separator + "servers.xml";
			ArrayList<XMLElementModel> defaultServer = GlobalSettingsActivity.getClientPreference("android", "server");
			serverURL = defaultServer.get(0).getAttribute().get(ClientUtils.XML_HOST_KEY) + ":" + defaultServer.get(0).getAttribute().get(ClientUtils.XML_PORT_KEY);
			Log.i(TAG, serverURL);
		}

		try{
			ServiceIntent instance = ServiceIntentFactory.getService(action);
			instance.setInputString(input);
			instance.setCandidServerURL(serverURL);
			//FIXME add RTP handling
			instance.setRTParams(null);
			String result = instance.execute();
			//Log.d(Constants.TAG, result);

			boolean silent_mode = Boolean.parseBoolean(intent.getExtras().getString("SILENT_MODE"));
			if(silent_mode){
				Log.d(Constants.TAG,"silent_mode "+ silent_mode);
				Intent broadcast = new Intent("info.semanticsoftware.semassist.android.BROADCAST");
				broadcast.putExtra("serverResponse", result);
				sendOrderedBroadcast(broadcast, null);
			}else{
				// open the results activity
				Intent resultsIntent = new Intent(getBaseContext(), SemanticResultsActivity.class);
				resultsIntent.putExtra("xml", result);
				startActivity(resultsIntent);
			}
		} catch (Exception e) {
			Log.d(Constants.TAG, e.getMessage());
		}
	}
}
