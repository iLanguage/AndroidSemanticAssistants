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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import NLPAndroid.domainLogic.Service;
import NLPAndroid.domainLogic.WebServiceHandler;
import NLPAndroid.domainLogic.serviceInvocationAddParams;
import NLPAndroid.domainLogic.Utils.webServiceConnectionUtil;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ServiceParamConfig extends Activity {
	private String selectedService ;
	private int serviceIndex ;
	private int desiredResults ;
	
    public void onCreate(Bundle savedInstanceState) {

    	super.onCreate(savedInstanceState);
		setContentView(R.layout.service_text_input_screen);
		
		final Context context = this ;
		Button serviceParametersSubmitButton = (Button) findViewById(R.id.service_parameters_submit_button_id);
		serviceParametersSubmitButton.setEnabled(true) ;
		
		try
		{
			selectedService = Services.self.getIntent().getStringExtra("selection") ;
			Toast.makeText(this , "The service is " + selectedService, Toast.LENGTH_LONG).show();
			serviceIndex = Integer.parseInt( Services.self.getIntent().getStringExtra("serviceIndex") ) ;
			System.out.println( Services.self.getIntent().getStringExtra("serviceIndex") ) ;
			serviceParametersSubmitButton.setEnabled(true) ;
		}
		catch(Exception e)
		{
			Toast.makeText(context , "Please select a service" , Toast.LENGTH_LONG).show();
			serviceParametersSubmitButton.setEnabled(false) ;
		}
		
    	final EditText serviceTextInputEditText = (EditText) findViewById(R.id.service_text_input_edit_text_id);
    	TextView label = (TextView)findViewById(R.id.service_parameters_input_instructions_id) ;
    	label.setVisibility(View.VISIBLE) ;
//    	String clipboardText = ((ClipboardManager)getSystemService(CLIPBOARD_SERVICE)).getText().toString() ;
//    	if(!clipboardText.equals(""))
//    	{
//    		serviceTextInputEditText.setText( clipboardText );
//    	}
//		final String input = clipboardText+serviceTextInputEditText.getText().toString() ;
//    	serviceTextInputEditText.setText( "John Travolta" );
//		final String input = "Montreal" ;
		
//		System.out.println("input was "+input);
		System.out.println("service "+selectedService);
		
		final EditText results = (EditText) findViewById(R.id.input_parameters_field_id);
		System.out.println("desired results "+ desiredResults);
		results.setVisibility(View.VISIBLE) ;
		
		try
		{
			if( selectedService.matches(".*Extractor.*"))
			{
				label.setVisibility(View.GONE) ;
				results.setVisibility(View.GONE) ;
			}
		}
		catch(Exception e)
		{
			System.out.println(e.getStackTrace()) ;
		}
		
		
		serviceParametersSubmitButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				
				try
				{
					desiredResults = Integer.parseInt(results.getText().toString()) ;
				}
				catch(NumberFormatException e)
				{
					desiredResults = 5 ;
				}
				
				if(desiredResults <= 0)
					desiredResults = 5 ;
				
				String clipboardText = ((ClipboardManager)getSystemService(CLIPBOARD_SERVICE)).getText().toString() ;
		    	if(!clipboardText.equals(""))
		    	{
		    		serviceTextInputEditText.setText( clipboardText );
		    	}
				final String input = clipboardText+serviceTextInputEditText.getText().toString() ;
				if(input.equals(""))
				{
					Toast.makeText(context , "Selection cannot be empty, please input text" + selectedService, Toast.LENGTH_LONG).show();
				}
				else
				{
					ProgressDialog dialog = ProgressDialog.show(ServiceParamConfig.this, "", 
	                        "Loading. Please wait...", true);
					
					String result = invoke(input) ;

					int index = result.indexOf("<?xml") ; // the xml is embedded in the response, wtf
					result = result.substring(index) ;
					result = result.replace("&", "&amp;" ) ; //seriously, who doesn't escape their urls ?
//					System.out.println(result);				//debug
					
		        	System.out.println("Service returned successfully");
		        	Intent intent = new Intent(view.getContext(), Results.class);
		        	Bundle b = new Bundle();
		        	b.putString("result", result);
		        	b.putString("input", input) ;
                  	intent.putExtras(b);	
					startActivityForResult(intent, 0);
			        	
					finish() ;
				}
			}
		});
    }
    
    private String invoke(String input)
    {
    	GlobalSettings globalSettings = new GlobalSettings() ;
    	InputStream is;
    	String result = "" ;
		try {
			is = openFileInput("GlobalSettings");
			Properties properties = globalSettings.readConfig(is) ;
			webServiceConnectionUtil wsutil = new webServiceConnectionUtil(properties);
	        
			WebServiceHandler ktest = new WebServiceHandler(wsutil);
	        serviceInvocationAddParams usrOptPr = new serviceInvocationAddParams();
	        usrOptPr.setConnID(0);
	        usrOptPr.getDocuments().listOfuriList.add("#literal");
	        usrOptPr.getDocuments().listOfuriList.add("http://www.concordia.ca");
	        usrOptPr.getDocuments().listOfuriList.add("http://www.cse.concordia.ca");
	        
	        usrOptPr.getLiteralDocs().item = input ;
	        usrOptPr.getUserCtx().mDocLang ="es";
	        usrOptPr.getUserCtx().mUserLanguages ="en";
        	List<Service> availableServices = ktest.getListOfServices();
	        
        	//CC HERE
        	List<userInput> usrInList =new ArrayList<userInput>();
        	userInput usr = new userInput();
        	usr.setIntValue(this.desiredResults);
        	usrInList.add(usr);
        	
        	//CC
	        Service chosenService = availableServices.get(serviceIndex-1) ;
	           
	        ktest.getResultFile("file:/C:/Users/Chadz/AppData/Local/Temp/serviceResult-java.text.SimpleDateFormat@f85ae543.8800044551085974873txt");
	        
        	result = ktest.invokeServices(chosenService, usrOptPr , usrInList ) ;
        		
	    }
		catch (FileNotFoundException e) {
			Toast.makeText(this, "Could not read configuration, please try again later " + serviceIndex, Toast.LENGTH_LONG).show();
			e.printStackTrace();
		} catch (IOException e) {
			Toast.makeText(this, "Could not read configuration, please try again later " + serviceIndex, Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}

		return result ;
		
    }
}