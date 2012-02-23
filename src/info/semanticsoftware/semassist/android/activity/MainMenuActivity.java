package info.semanticsoftware.semassist.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;

public class MainMenuActivity extends Activity {
	private View mServicesButton;
	private View mSettingsButton;
	private WebView mWebView;

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
        
        mServicesButton = findViewById(R.id.av_assist);
        mServicesButton.setOnClickListener(sServicesListener);
        
        mSettingsButton = findViewById(R.id.gl_sett);
        mSettingsButton.setOnClickListener(sSettingsListener);
        
        mWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = mWebView.getSettings();
		webSettings.setBuiltInZoomControls(false);
		mWebView.setBackgroundColor(0x00000000);
		mWebView.loadUrl("file:///android_asset/main.html");
	}
	
	 private View.OnClickListener sServicesListener = new View.OnClickListener() {
         public void onClick(View v) {
                System.out.println("Clicked");
     			Intent getServices = new Intent(getBaseContext(), SemanticAssistantsActivity.class);
                startActivity(getServices);
         }
	 };
	 
	 private View.OnClickListener sSettingsListener = new View.OnClickListener() {
         public void onClick(View v) {
                 System.out.println("settings Clicked");
         }
	 };

}
