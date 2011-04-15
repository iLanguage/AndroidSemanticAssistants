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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

public class SemanticAssistantsMain extends Activity {
	
	private WebView browserWebView;
	private EditText urlText;
	private Button goButton;
	private Button listServicesButton;
	private Button globalSettingsButton;

	public void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);
	    this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
	    
	    urlText = (EditText) findViewById(R.id.url_field);
	    goButton = (Button) findViewById(R.id.go_button);
	    browserWebView = (WebView) findViewById(R.id.webview);
	    listServicesButton = (Button) findViewById(R.id.list_services_button);
	    globalSettingsButton = (Button) findViewById(R.id.global_config_button);
	    
	    // Turn on JavaScript in the embedded browser
	    browserWebView.getSettings().setJavaScriptEnabled(true);
	
	    // Set up a function to be called when JavaScript tries to open an alert window
	    browserWebView.setWebChromeClient(new WebChromeClient() {
	    	   public void onProgressChanged(WebView view, int progress) {
	    		     setProgress(progress * 1000);
	    		   }
	       }
	    );
	    
//	    mainWebView.loadUrl("http://www.google.ca");
	    browserWebView.setWebViewClient(new MyWebViewClient());
	    KeyEvent shiftPressEvent = new KeyEvent(0,0,KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_SHIFT_LEFT,0,0);
        shiftPressEvent.dispatch(browserWebView); 
	    //if someone clicked on a link in the results page
	    Bundle bundle = this.getIntent().getExtras();
        if(bundle != null)
        {
            String url = bundle.getString("url");
        	browserWebView.loadUrl(url) ;
        }
	   
	    // Setup Some event handlers for the url bar:
	    goButton.setOnClickListener(new OnClickListener() {
	       public void onClick(View view) {
	          openBrowser();
	       }
	    });
	    urlText.setOnKeyListener(new OnKeyListener() {
	       public boolean onKey(View view, int keyCode, KeyEvent event) {
	          if (keyCode == KeyEvent.KEYCODE_ENTER) {
	             openBrowser();
	             return true;
	          }
	          return false;
	          
	       }
	    });
	    
		listServicesButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Intent intent = new Intent(view.getContext(), Services.class);
				startActivityForResult(intent, 0);
			}
		});
		
		globalSettingsButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Intent intent = new Intent(view.getContext(), GlobalSettings.class);
				startActivityForResult(intent, 0);
			}
		});
	}
 
    private void openBrowser() {
    	browserWebView.getSettings().setJavaScriptEnabled(true);
    	browserWebView.loadUrl(urlText.getText().toString());
    }
    
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}