package info.semanticsoftware.semassist.android.service;

import info.semanticsoftware.semassist.android.activity.GlobalSettingsActivity;
import info.semanticsoftware.semassist.android.intents.ServiceIntent;
import info.semanticsoftware.semassist.android.intents.ServiceIntentFactory;
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
		System.out.println("From prefs: " + serverURL);

		if(serverURL.equals("-1")){
			Log.i(TAG, "Not in prefs. Reading from XML");
			GlobalSettingsActivity.SERVERS_XML_FILE_PATH = getFilesDir().getAbsolutePath()+ File.separator + "servers.xml";
			ArrayList<XMLElementModel> defaultServer = GlobalSettingsActivity.getClientPreference("android", "server");
			serverURL = "http://" + defaultServer.get(0).getAttribute().get(ClientUtils.XML_HOST_KEY) + ":" + defaultServer.get(0).getAttribute().get(ClientUtils.XML_PORT_KEY);
			Log.i(TAG, serverURL);
		}

		try{
			ServiceIntent instance = ServiceIntentFactory.getService(action);
			instance.setInputString(input);
			instance.setCandidServerURL(serverURL);
			instance.setRTParams(null);
			String result = instance.execute();
			System.out.println(result);

			boolean silent_mode = Boolean.parseBoolean(intent.getExtras().getString("SILENT_MODE"));
			if(silent_mode){
				Intent broadcast = new Intent("info.semanticsoftware.semassist.android.BROADCAST");
				broadcast.putExtra("serverResponse", result);
				sendOrderedBroadcast(broadcast, null);
			}//TODO handle other case
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
