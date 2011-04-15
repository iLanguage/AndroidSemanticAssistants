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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import NLPAndroid.domainLogic.Service;
import NLPAndroid.domainLogic.WebServiceHandler;
import NLPAndroid.domainLogic.Utils.webServiceConnectionUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.Toast;

public class ServiceListConfig extends Activity {
	
	private List<Service> availableServices = null ;
	
	private ArrayList<String> spinnerArray = new ArrayList<String>() ;

        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.service_list_tab);

            Spinner spinner = (Spinner) findViewById(R.id.spinner);
            spinnerArray.add(0, "") ;
            
            try {
				loadAvailableServices() ;
			} catch (IOException e) {
				e.printStackTrace();
			}
            
            ArrayAdapter<String> adapterTest = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);
            adapterTest.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapterTest);
            spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
            
            final RadioButton radio_red = (RadioButton) findViewById(R.id.radio_red);
            final RadioButton radio_blue = (RadioButton) findViewById(R.id.radio_blue);
            radio_red.setOnClickListener(radio_listener);
            radio_blue.setOnClickListener(radio_listener);
        }
        
        public void loadAvailableServices() throws IOException {
        	
        	GlobalSettings globalSettings = new GlobalSettings() ;
        	InputStream is = openFileInput("GlobalSettings") ;
        	Properties properties = globalSettings.readConfig(is) ;
        	
        	
	        webServiceConnectionUtil wsutil = new webServiceConnectionUtil(properties);
        	WebServiceHandler ktest = new WebServiceHandler(wsutil);
        	availableServices = ktest.getListOfServices();
 	        
 	       for( int i=0 ; i<availableServices.size() ; i++)
 	       {
 	    	   spinnerArray.add(availableServices.get(i).getServiceName()) ;
 	       }
			
		}
		private OnClickListener radio_listener = new OnClickListener() {
            public void onClick(View v) {
                //eventually this will govern the type of literal
            	//when invoking services with html output
            }
        };
        
        public class MyOnItemSelectedListener implements OnItemSelectedListener {
            
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) 
            {
              String selectedService = parent.getItemAtPosition(pos).toString() ;

              if(selectedService.toLowerCase().contains(".*annie.*") || selectedService.toLowerCase().matches(".*durm.*"))
              {
            	  Toast.makeText(getBaseContext() , "Coming soon...", Toast.LENGTH_LONG).show();
              }
              else if(!selectedService.equalsIgnoreCase("") )
              {
	              Intent intent = new Intent(view.getContext(), ServiceParamConfig.class);
	              intent.putExtra("selection", selectedService);
	              intent.putExtra("serviceIndex", String.valueOf(pos) ) ;
	              System.out.println("position is "+pos);
	              TabHost tabHost = Services.self.getTabHost(); 
	              Services.self.setIntent(intent) ;
	              tabHost.setCurrentTab(1);
              }
            }

            public void onNothingSelected(AdapterView<?> parent) {
              // Do nothing.
            }
        }

		public ArrayList<String> getSpinnerArray() {
			return spinnerArray;
		}

		public void setSpinnerArray(ArrayList<String> spinnerArray) {
			this.spinnerArray = spinnerArray;
		}

		public List<Service> getAvailableServices() {
			return availableServices;
		}
}
