package info.semanticsoftware.semassist.android.activity;

import java.io.StringWriter;

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
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class AuthenticationActivity extends AccountAuthenticatorActivity{
	
	@Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.setContentView(R.layout.user_credentials);
    }
	
	public void onCancelClick(View v) {
	        this.finish();
	}

	public void onSaveClick(View v) {
		TextView tvUsername;
        TextView tvPassword;
        String username;
        String password;

        tvUsername = (TextView) this.findViewById(R.id.uc_txt_username);
        tvPassword = (TextView) this.findViewById(R.id.uc_txt_password);

        username = tvUsername.getText().toString();
        String usernameTemp = username.substring(0, username.indexOf("@"));
        password = tvPassword.getText().toString();

        //TODO do client-side validation like password length etc.
        //TODO perform some network activity here
        
        String request = "<authenticate><username>" + usernameTemp + "</username><password>" + password + "</password></authenticate>";
		Representation representation = new StringRepresentation(request,MediaType.APPLICATION_XML);
    	Representation response = new ClientResource("http://192.168.4.110:8080/SemAssistRestlet/user").post(representation);
        try {
        	StringWriter writer = new StringWriter();
        	response.write(writer);
        	String responseString = writer.toString();
        	System.out.println(responseString);
        	
        	//Let's do some nasty string manipulation here TODO change this later
        	int temp = responseString.indexOf("<userKey>");
        	String pubKeyMod = responseString.substring(temp + "<userKey>".length());
        	temp = pubKeyMod.indexOf("</userKey>");
        	pubKeyMod = pubKeyMod.substring(0, temp);
        	System.out.println("pubKeyMod " + pubKeyMod);
        	
        	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());        	
        	Editor editor = settings.edit();
        	editor.putString("modValue", pubKeyMod);
        	editor.putString("username", username);
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
        final Account account = new Account(username, accountType);
        accMgr.addAccountExplicitly(account, password, null);

        // Now we tell our caller, could be the Android Account Manager or even our own application
        // that the process was successful

        final Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, username);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType);
        intent.putExtra(AccountManager.KEY_AUTHTOKEN, accountType);
        this.setAccountAuthenticatorResult(intent.getExtras());
        this.setResult(RESULT_OK, intent);
        this.finish();
	}
}
