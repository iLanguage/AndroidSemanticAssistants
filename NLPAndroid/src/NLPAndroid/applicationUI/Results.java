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

import java.util.ArrayList;
import java.util.HashMap;

import NLPAndroid.domainLogic.Utils.Annotation;
import NLPAndroid.domainLogic.Utils.AnnotationInstance;
import NLPAndroid.domainLogic.Utils.XMLHandler;
import NLPAndroid.domainLogic.Utils.XMLParser;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Results extends Activity 
{
	HashMap<String, Annotation> annotation = null ;
	ArrayList<String> documentOutput = null ;
	
	 public void onCreate(Bundle savedInstanceState) {
	    
		super.onCreate(savedInstanceState);
		setContentView(R.layout.results) ;

		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(1);
		
        Bundle bundle = this.getIntent().getExtras();
        String results = bundle.getString("result");
        String input = bundle.getString("input") ;
        
        TextView titleInput = new TextView(this) ;
        titleInput.setText(input) ;
		
		layout.addView(titleInput) ;
		
		System.out.println(results);	 //debug
 
	    if( process(results) )
	    {
	    	System.out.println("Service returned successfully"); //debug
	    	
	    	annotation = XMLHandler.annotation ;
	    	documentOutput = XMLHandler.outputDocument ;
	    	
	    	try
	    	{
		    	//output is either list of urls
		    	if(!documentOutput.equals(""))
		    	{
		    		for(String each : documentOutput)
		    		{
		    			final TextView url = new TextView(this) ;
		    			url.setText(each) ;
		    			url.setClickable(true) ;
		    			
		    			url.setOnClickListener(new OnClickListener() {
		    				public void onClick(View v) {
		    				    loadUrl(url.getText().toString());
		    				}
		    			});
		    			
		    			layout.addView(url) ;
		    		}
		    	}
		    	//or set of annotations
	    		if(!annotation.equals(null))
		    	{
		    		System.out.println("annotation not empty");
		    		//multiple annotations per response
		    		for(Annotation each : annotation.values())
		    		{
		    			if( !each.getDocumentAnnotationInstance().isEmpty() )
		    			{
		    				System.out.println("annotationInst not empty");
//		    				TextView type = new TextView(this) ;
//	    					type.setText("Type: "+each.getType()+", ") ;
//	    					
//	    					layout.addView(type) ;
	    					
	    					//one documentURL per annotation, generally empty for this response type
		    				if(!each.getDocumentUrl().equals(""))
		    				{
		    					TextView documentURL = new TextView(this) ;
		    					documentURL.setText("URL: "+each.getDocumentUrl()) ;
		    					
		    					layout.addView(documentURL) ;
		    					System.out.println("added docurl tv: "+each.getDocumentUrl());
		    				}
	    					
		    				//multiple annotationInstances per annotation
		    				for(AnnotationInstance annoInstance : each.getDocumentAnnotationInstance() )
		    				{
		    					//one feature per annotationInstance
		    					TextView feature = new TextView(this) ;
		    					feature.setText(annoInstance.getContent()+":\n"+each.getType()+", "+annoInstance.getFeatureName()) ;
		    					
		    					layout.addView(feature) ;
		    					
		    					System.out.println("added feature tv: "+annoInstance.getFeatureValue());
		    				}
		    			}
		    		}
		    	}
	    	}
	    	catch(NullPointerException e)
	    	{
	    		TextView errorResponse = new TextView(this) ;
	    		errorResponse.setText("Service returned no results.") ;
	    		
	    		layout.addView(errorResponse) ;
	    	}
	    	catch(ArrayIndexOutOfBoundsException e)
	    	{
	    		TextView errorResponse = new TextView(this) ;
	    		errorResponse.setText("Service returned no results.") ;
	    		
	    		layout.addView(errorResponse) ;
	    	}
	    }
	    else
    	{
    		TextView errorResponse = new TextView(this) ;
    		errorResponse.setText("There was an error parsing results.") ;
    		
    		layout.addView(errorResponse) ;
    	}
	    
    	Button okButton = new Button(Results.this);
        okButton.setText("Close");
         layout.addView(okButton);
    	okButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) 
			{
				finish() ;
			}
		});

	    setContentView(layout);
	    
//	    ScrollView sView = (ScrollView)findViewById(R.layout.scrollbar);
//	    sView.setVerticalScrollBarEnabled(true);
//		sView.setHorizontalScrollBarEnabled(false);
	 }
	
    private boolean process(String response)
    {
    	XMLParser parser = new XMLParser() ;
    	return parser.parse(response) ;
    }
    
    private void loadUrl(String url)
    {
    	Intent intent = new Intent(getBaseContext(), SemanticAssistantsMain.class);
    	Bundle b = new Bundle();
    	b.putString("url", url) ;
      	intent.putExtras(b);	
		startActivityForResult(intent, 0);
    }
}
