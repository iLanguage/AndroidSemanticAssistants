/**
   Semantic Assistants - http://www.semanticsoftware.info/semantic-assistants

   This file is part of the Semantic Assistants architecture.

   Copyright (C) 2009, 2010, 2011 Semantic Software Lab, http://www.semanticsoftware.info

   The Semantic Assistants architecture is free software: you can
   redistribute and/or modify it under the terms of the GNU Affero General
   Public License as published by the Free Software Foundation, either
   version 3 of the License, or (at your option) any later version.
   
   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Affero General Public License for more details.
   
   You should have received a copy of the GNU Affero General Public License
   along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package NLPAndroid.applicationUI;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class GlobalSettings extends Activity {
	
	private String host = "";
	private String port = "8879";
	private String namespace = "http://server.semassist.semanticsoftware.info/";
	private String action = "SemanticServiceBroker";
	private String method = "getAvailableServices" ;
	
	private boolean serviceUp ;
	private final String _FILENAME = "GlobalSettings" ;

    public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.global_config);
            
            final EditText hostInput = (EditText) findViewById(R.id.input_parameters_field_id);
            
            try {
            	InputStream is = openFileInput(_FILENAME) ;
				readConfig(is) ;
			} catch (IOException e1) {
				Toast.makeText(this , "Configuration could not be read, please try again later", Toast.LENGTH_LONG).show();
			}
            
			hostInput.setText(host) ;
			
    		Button saveGlobalSettings = (Button) findViewById(R.id.config_save);
    		Button cancel = (Button) findViewById(R.id.config_cancel);
    		saveGlobalSettings.setOnClickListener(new OnClickListener() {
    			@Override
    			public void onClick(View view) {

    				try {
						InetAddress hostINA = InetAddress.getByName(hostInput.getText().toString()) ;
    					host = hostINA.getHostName().toString() ;
    					System.out.println("host is "+host);
    					serviceUp = ( !hostINA.isReachable(5000) ) ;
    					
    					if(!serviceUp)
    					{
    						throw new IOException() ;
    					}
    					
    					FileOutputStream fos;
    					fos = openFileOutput(_FILENAME, Context.MODE_PRIVATE);
    					saveConfig(fos) ;
    					finish() ;
						
    				} catch (UnknownHostException e) {
    					Toast.makeText(getBaseContext() , "The host address is invalid", Toast.LENGTH_LONG).show();
					}catch(FileNotFoundException e){
						Toast.makeText(getBaseContext() , "Could not save configuration, please try again later", Toast.LENGTH_LONG).show();
					}catch (IOException e) {
						Toast.makeText(getBaseContext() , "The host address is unreachable", Toast.LENGTH_LONG).show();
					} 
    			}
    		});
    		cancel.setOnClickListener(new OnClickListener() {
    			@Override
    			public void onClick(View view) {
    				finish() ;
    			}
    		});
        }
    
    	public Properties saveConfig(FileOutputStream fos) throws FileNotFoundException, IOException
    	{
				fos.write(("http://"+host+":"+port+"/SemAssist?wsdl\n").getBytes());
				
				Properties properties = new Properties() ;
				properties.setProperty("url", "http://"+host+":"+port+"/SemAssist?wsdl") ;
	    	    properties.setProperty("namespace", namespace) ;
	    	    properties.setProperty("action", action) ;
	    	    properties.setProperty("methodname", method) ;
				
				fos.close();
				return properties ;
    	}
    	
    	public Properties readConfig(InputStream is) throws IOException
    	{
    		try
    		{
    			InputStreamReader inputreader = new InputStreamReader(is);
    		    BufferedReader buffreader = new BufferedReader(inputreader);
    		 
        		host = buffreader.readLine() ;
        		if(!host.equals(""))
        		{
        			int start = host.indexOf("http://") +7 ;
        	    	int end = host.indexOf(":"+port+"/SemAssist?wsdl") ;
        	    	host = host.substring(start, end) ;
        		}
        		System.out.println("host being read is "+host);
        	    
        	    Properties properties = new Properties() ;
        	    properties.setProperty("url", "http://"+host+":"+port+"/SemAssist?wsdl") ;
        	    properties.setProperty("namespace", namespace) ;
        	    properties.setProperty("action", action) ;
        	    properties.setProperty("methodname", method) ;
    		 
    		    is.close();
    		    return properties ;
    		}
    		catch(FileNotFoundException e)
    		{
//    			//no config exists yet, make default one
    			FileOutputStream fos;
				fos = openFileOutput(_FILENAME, Context.MODE_PRIVATE);
    			return saveConfig(fos) ;
    		}
    	}
    	
    	@SuppressWarnings("unused")
		private boolean validateIP(String host)
    	{
    		Pattern pattern;
    	    Matcher matcher;
    	    String IPADDRESS_PATTERN = 
    			"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
    			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
    			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
    			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
    	 
    		pattern = Pattern.compile(IPADDRESS_PATTERN);
    		matcher = pattern.matcher(host);
    		return matcher.matches();	    	    
    	}

		public String getHost() {
			return host;
		}

		public String getNamespace() {
			return namespace;
		}

		public String getAction() {
			return action;
		}
}
