package info.semanticsoftware.semassist.android.activity;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;

import info.semanticsoftware.semassist.android.service.SemAssistAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import info.semanticsoftware.semassist.android.activity.R;
import info.semanticsoftware.semassist.csal.ClientUtils;
import info.semanticsoftware.semassist.csal.XMLElementModel;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class AuthenticationActivity extends AccountAuthenticatorActivity{
	
	private String serverURL = null;
	private final String TAG = "AuthenticationActivity";
	
	@Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.setContentView(R.layout.user_credentials);
        getServerSettings();
    }
	
	public void onCancelClick(View v) {
	        this.finish();
	}

	public void onSaveClick(View v) {
		TextView tvUsername;
        TextView tvPassword;
        
        // Qualified username, i.e, user@semanticassistants.com
        String qUsername;
        
        String password;

        tvUsername = (TextView) this.findViewById(R.id.uc_txt_username);
        tvPassword = (TextView) this.findViewById(R.id.uc_txt_password);

        qUsername = tvUsername.getText().toString();
        String username = qUsername.substring(0, qUsername.indexOf("@"));
        password = tvPassword.getText().toString();

        //TODO do client-side validation like password length etc.
        
        String request = "<authenticate><username>" + username + "</username><password>" + password + "</password></authenticate>";
		Representation representation = new StringRepresentation(request,MediaType.APPLICATION_XML);
    	
		String uri = serverURL + "/SemAssistRestlet/user";
		System.out.println("sending auth req to " + uri);
		Representation response = new ClientResource(uri).post(representation);
        try {
        	StringWriter writer = new StringWriter();
        	response.write(writer);
        	String responseString = writer.toString();
        	Log.d(TAG, "Authentication response: " + responseString);
        	
        	//Let's do some nasty string manipulation here TODO change this later
        	int temp = responseString.indexOf("<userKey>");
        	String pubKeyMod = responseString.substring(temp + "<userKey>".length());
        	temp = pubKeyMod.indexOf("</userKey>");
        	pubKeyMod = pubKeyMod.substring(0, temp);
        	System.out.println("pubKeyMod " + pubKeyMod);
        	
        	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());        	
        	Editor editor = settings.edit();
        	editor.putString("modValue", pubKeyMod);
        	editor.putString("username", qUsername);
        	boolean result = editor.commit();
        	if(result){
        		Toast.makeText(this, "Successfully authenticated", Toast.LENGTH_LONG).show();            	
        	}else{
        		Toast.makeText(this, "Authentication failed", Toast.LENGTH_LONG).show();	
        	}
        	
		} catch (Exception e) {
			e.printStackTrace();
		}

        String accountType = this.getIntent().getStringExtra("auth.token");
        if (accountType == null) {
            accountType = SemAssistAuthenticator.ACCOUNT_TYPE;
        }

        AccountManager accMgr = AccountManager.get(this);

        // Add the account to the Android Account Manager
        final Account account = new Account(qUsername, accountType);
        accMgr.addAccountExplicitly(account, password, null);

        // Now we tell our caller, could be the Android Account Manager or even our own application
        // that the process was successful

        final Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, qUsername);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType);
        intent.putExtra(AccountManager.KEY_AUTHTOKEN, accountType);
        this.setAccountAuthenticatorResult(intent.getExtras());
        this.setResult(RESULT_OK, intent);
        this.finish();
	}
	
	private void getServerSettings(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		serverURL = prefs.getString("selected_server_option", "-1");
		if(serverURL.equals("-1")){
			GlobalSettingsActivity.SERVERS_XML_FILE_PATH = getFilesDir().getAbsolutePath()+ File.separator + "servers.xml";
			ArrayList<XMLElementModel> defaultServer = GlobalSettingsActivity.getClientPreference("android", "server");
			serverURL = "http://" + defaultServer.get(0).getAttribute().get(ClientUtils.XML_HOST_KEY) + ":" + defaultServer.get(0).getAttribute().get(ClientUtils.XML_PORT_KEY);
		}
	}
}
