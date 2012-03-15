package info.semanticsoftware.semassist.android.service;

import info.semanticsoftware.semassist.android.activity.GlobalSettingsActivity;
import info.semanticsoftware.semassist.android.restlet.RequestRepresentation;
import info.semanticsoftware.semassist.csal.ClientUtils;
import info.semanticsoftware.semassist.csal.XMLElementModel;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;
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
        intent.setAction(null);
        SharedPreferences prefs = this.getSharedPreferences("info.semanticsoftware.semassist.android.activity_preferences", MODE_MULTI_PROCESS);
		String serverURL = prefs.getString("selected_server_option", "-1");
		System.out.println("From prefs: " + serverURL);
		
		if(serverURL.equals("-1")){
			System.out.println("Not in prefs. Reading from XML");
			GlobalSettingsActivity.SERVERS_XML_FILE_PATH = getFilesDir().getAbsolutePath()+ File.separator + "servers.xml";
			ArrayList<XMLElementModel> defaultServer = GlobalSettingsActivity.getClientPreference("android", "server");
			serverURL = "http://" + defaultServer.get(0).getAttribute().get(ClientUtils.XML_HOST_KEY) + ":" + defaultServer.get(0).getAttribute().get(ClientUtils.XML_PORT_KEY);
			System.out.println(serverURL);
		}
		
		try{
			RequestRepresentation request = new RequestRepresentation("Person and Location Extractor", null, input);
	    	Representation representation = new StringRepresentation(request.getXML(),MediaType.APPLICATION_XML);
			Representation response = new ClientResource(serverURL + "/SemAssistRestlet/services/Person and Location Extractor").post(representation);
	    	StringWriter writer = new StringWriter();
	    	response.write(writer);
	    	System.out.println(writer.toString());
	    	boolean silent_mode = Boolean.parseBoolean(intent.getExtras().getString("SILENT_MODE"));
	    	if(silent_mode){
	    		Intent broadcast = new Intent("info.semanticsoftware.semassist.android.BROADCAST");
	    		broadcast.putExtra("serverResponse", writer.toString());
	            sendOrderedBroadcast(broadcast, null);
	    	}
    	} catch (IOException e) {
			e.printStackTrace();
		}catch (Exception e){
    		System.out.println(e.getMessage());
    	} 
       	
	}
}
