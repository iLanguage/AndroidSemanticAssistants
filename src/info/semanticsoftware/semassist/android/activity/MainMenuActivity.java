package info.semanticsoftware.semassist.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;

public class MainMenuActivity extends Activity {
	private WebView mWebView;
	private static final String TAG = "SemanticAssistants";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        int width = getWindow().getWindowManager().getDefaultDisplay().getWidth();
        if (width < 500 ){
			//phones
			LinearLayout buttonArea = (LinearLayout)findViewById(R.id.button_area);
	        buttonArea.setOrientation(LinearLayout.VERTICAL);
		}
        
        mWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = mWebView.getSettings();
		webSettings.setBuiltInZoomControls(false);
		mWebView.setBackgroundColor(0x00000000);
		mWebView.loadUrl("file:///android_asset/main.html");
	}
	
	public void onAssistantsClick(View v) {
		Log.d(TAG,"Clicked");
		Intent getServices = new Intent(getBaseContext(), SemanticAssistantsActivity.class);
        startActivity(getServices);
	}

	public void onSettingsClick(View v) {
		Log.d(TAG,"settings Clicked");
		Intent getSettings = new Intent(getBaseContext(), GlobalSettingsActivity.class);
        startActivity(getSettings);
	}
	 

}
