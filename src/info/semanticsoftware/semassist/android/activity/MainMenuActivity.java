package info.semanticsoftware.semassist.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;

/** The Semantic Assistants app launch activity.
 * @author Bahar Sateli
 */
public class MainMenuActivity extends Activity {
	private WebView mWebView;

	/** Called when the activity is created.
	 * @param savedInstanceState saved instance state
	 */
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

	/** Attaches a click listener to the Available Assistants button. 
	 * @param v view
	 */
	public void onAssistantsClick(View v) {
		Intent getServices = new Intent(getBaseContext(), SemanticAssistantsActivity.class);
		startActivity(getServices);
	}

	/** Attaches a click listener to the Global Settings button. 
	 * @param v view
	 */
	public void onSettingsClick(View v) {
		Intent getSettings = new Intent(getBaseContext(), GlobalSettingsActivity.class);
		startActivity(getSettings);
	}
}
