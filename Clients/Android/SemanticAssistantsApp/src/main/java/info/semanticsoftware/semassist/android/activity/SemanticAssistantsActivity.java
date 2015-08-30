/*
* Semantic Assistants -- http://www.semanticsoftware.info/semantic-assistants
* 
* Copyright (C) 2014 Semantic Software Lab, http://www.semanticsoftware.info
* Rene Witte
* Bahar Sateli
* 
* This file is part of the Semantic Assistants architecture, and is 
* free software, licensed under the GNU Lesser General Public License 
* as published by the Free Software Foundation, either version 3 of 
* the License, or (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package info.semanticsoftware.semassist.android.activity;

import info.semanticsoftware.semassist.android.application.SemAssistApp;
import info.semanticsoftware.semassist.android.encryption.CustomSSLSocketFactory;
import info.semanticsoftware.semassist.android.parser.ServiceParser;
import info.semanticsoftware.semassist.android.restlet.RequestRepresentation;
import info.semanticsoftware.semassist.csal.ClientUtils;
import info.semanticsoftware.semassist.csal.XMLElementModel;
import info.semanticsoftware.semassist.server.GateRuntimeParameter;
import info.semanticsoftware.semassist.server.ServiceInfoForClientArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.restlet.data.ClientInfo;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

/** The Semantic Assistants app main activity.
 * @author Bahar Sateli
 */
public class SemanticAssistantsActivity extends ListActivity{

	private String selectedService;
	private TextView lblAvAssist;
	private EditText txtInput;
	private Button btnClear;
	public static String serverURL;
	private final String TAG = "SemanticAssistantsActivity";
	private static ServiceInfoForClientArray servicesList;

	/** Sets the user interface and retrieves the list of
	 * available services.
	 * @param savedInstanceState saved instance state
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Find the user preferred Semantic Assistants server
		serverURL = readServerSettings();

		// Retrieve the list of available assistants
		getServicesTask task = new getServicesTask();
		Log.i(TAG, "Retrieving available assistants from " + serverURL);
		task.execute(serverURL);

		// while the task is being executed, create the user interface
		setContentView(R.layout.main);
		lblAvAssist = (TextView) findViewById(R.id.lblAvAssist);
		txtInput = (EditText) findViewById(R.id.txtInput);
		
		// catch the text sent from another app on the system
		if (Intent.ACTION_SEND.equals(getIntent().getAction())) {
			Bundle bundle = getIntent().getExtras();
			txtInput.setText(bundle.getString(Intent.EXTRA_TEXT));
			getIntent().setAction(null);
		}

		btnClear = (Button) findViewById(R.id.btnClear);
		btnClear.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				txtInput.setText("");
			}
		});
	}

	/**
	 * Reads the Semantic Assistants server URL from the user preferences. First, it looks
	 * into the application preference file. If there is no such preference key
	 * in the application preferences, it gets the Android-specific values from servers.xml.
	 */
	private String readServerSettings(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String server = prefs.getString("selected_server_option", "-1");
		if(server.equals("-1")){
			GlobalSettingsActivity.SERVERS_XML_FILE_PATH = getFilesDir().getAbsolutePath()+ File.separator + "servers.xml";
			ArrayList<XMLElementModel> defaultServer = GlobalSettingsActivity.getClientPreference("android", "server");
			server = defaultServer.get(0).getAttribute().get(ClientUtils.XML_HOST_KEY) + ":" + defaultServer.get(0).getAttribute().get(ClientUtils.XML_PORT_KEY);
		}
		return server;
	}

	/**
	 * Called when the activity starts. If the calling intent carries
	 * a text, it places the input in the text edit component.
	 */
	@Override
	public void onStart(){
		super.onStart();
		if (Intent.ACTION_SEND.equals(getIntent().getAction())){
			Bundle bundle = getIntent().getExtras();
			txtInput.setText(bundle.getString(Intent.EXTRA_TEXT));
			getIntent().setAction(null);
		}
	}

	/** Asynchronous task to retrieve list of available assistants. */
	private class getServicesTask extends AsyncTask<String, Void, String> {
		/** Gets the list of available assistants from the provided URL.
		 * @param urls Semantic Assistants server URL
		 * @return server XML response
		 */
		protected String doInBackground(String... urls) {
			try {
				System.out.println(urls[0]);
				final String url = urls[0] + "/services";
				
				if(urls[0].indexOf("https") < 0){
					Log.i(TAG, "Sending GET via Restlet to " + url);
					// Prepare the request
					ClientResource resource = new ClientResource(url);
					ClientInfo info = new ClientInfo(MediaType.TEXT_XML);
					resource.setClientInfo(info);
					StringWriter writer = new StringWriter();
					resource.get(MediaType.TEXT_XML).write(writer);

					ServiceParser parser = new ServiceParser(writer.toString());

					Log.i(TAG, "Parsing server response...");
					servicesList = parser.parseToObject();

					return writer.toString();
				}else{
					HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
					DefaultHttpClient client = new DefaultHttpClient();
	
					SchemeRegistry registry = new SchemeRegistry();
					final KeyStore ks = KeyStore.getInstance("BKS");
					// NOTE: the keystore must have been generated with BKS 146 and not later
					final InputStream in = getApplicationContext().getResources().openRawResource(R.raw.clientkeystorenew);  
					ks.load(in,getString(R.string.keystorePassword).toCharArray());
					in.close();

					SSLSocketFactory socketFactory = new CustomSSLSocketFactory(ks);
					socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
					registry.register(new Scheme("https", socketFactory, 443));
					SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
					DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());

					// Set verifier      
					HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);

					HttpGet get = new HttpGet(url);
					HttpResponse response = httpClient.execute(get);
					HttpEntity entity = response.getEntity();

					InputStream inputstream = entity.getContent();
					InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
					BufferedReader bufferedreader = new BufferedReader(inputstreamreader);

					String string = null;
					String out = "";
					while ((string = bufferedreader.readLine()) != null) {
						System.out.println("Received " + string);
						out += string;
					}

					ServiceParser parser = new ServiceParser(out);

					Log.i(TAG, "Parsing server response...");
					servicesList = parser.parseToObject();

					return out;
				}
			} catch (KeyStoreException e){
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e){
				e.printStackTrace();
			} catch (CertificateException e){
				e.printStackTrace();
			} catch (UnrecoverableKeyException e){
				e.printStackTrace();
			} catch (KeyManagementException e){
				e.printStackTrace();
			} catch (ResourceException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return "error callService";
		}

		/** Gets called after the doInBackground method on the UI thread 
		 * to populate the list of assistants.
		 * @param result results to show
		 */
		@Override
		protected void onPostExecute(String result) {
			populateServicesList();
		}
	}

	/**
	 * Reads the available assistants from the server response and 
	 * populates the list on the UI. 
	 */
	private void populateServicesList(){
		if(servicesList != null){
			Toast.makeText(this, "Connected to " + serverURL, Toast.LENGTH_LONG).show();
			String[] values = null;
			List<String> names = new ArrayList<String>();
			for(int i=0; i < servicesList.getItem().size(); i++){
				names.add(servicesList.getItem().get(i).getServiceName());
			}
			values = names.toArray(new String[names.size()]);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.row, R.id.label, values);
			setListAdapter(adapter);
		}else{
			Toast.makeText(this, "No assistants found! The server may be offline.", Toast.LENGTH_LONG).show();
		}
	}

	/** Attaches a click listener to the list of available assistants. 
	 * Upon selecting an assistant from the list, it shows additional information
	 * about the assistants in a new layout.
	 * @param list list of available assistants
	 * @param view view
	 * @param position position of the selected item
	 * @param id id of the selected item
	 */
	@Override
	protected void onListItemClick(ListView list, View view, int position, long id) {
		list.setVisibility(View.GONE);
		lblAvAssist.setVisibility(View.GONE);
		LinearLayout linearLayout =  (LinearLayout) findViewById(R.id.servicesLayout);
		selectedService = (String) list.getItemAtPosition(position);
		linearLayout.addView(getServiceDescLayout());
	}

	/** Provides static access to the list of available assistants.
	 * @return array of service info objects
	 */
	public static ServiceInfoForClientArray getServices(){
		return servicesList;
	}

	/** Presents additional information about a specific assistant.
	 * @return a dynamically generated linear layout
	 */
	private LinearLayout getServiceDescLayout(){
		final LinearLayout output = new LinearLayout(this);
		final RelativeLayout topButtonsLayout = new RelativeLayout(this);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

		final Button btnBack = new Button(this);
		btnBack.setText(R.string.btnAllServices);
		btnBack.setId(5);
		btnBack.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				output.setVisibility(View.GONE);
				getListView().setVisibility(View.VISIBLE);
				lblAvAssist.setVisibility(View.VISIBLE);
			}
		});

		topButtonsLayout.addView(btnBack);

		final Button btnInvoke = new Button(this);
		btnInvoke.setText(R.string.btnInvokeLabel);
		btnInvoke.setId(6);

		btnInvoke.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				new InvocationTask().execute();
			}
		});

		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, btnInvoke.getId());
		btnInvoke.setLayoutParams(layoutParams);
		topButtonsLayout.addView(btnInvoke);

		output.addView(topButtonsLayout);

		TableLayout serviceInfoTbl = new TableLayout(this);
		output.addView(serviceInfoTbl);

		serviceInfoTbl.setColumnShrinkable(1, true);

		/* FIRST ROW */
		TableRow rowServiceName = new TableRow(this);

		TextView lblServiceName = new TextView(this);
		lblServiceName.setText(R.string.lblServiceName);
		lblServiceName.setTextAppearance(getApplicationContext(), R.style.titleText);

		TextView txtServiceName = new TextView(this);
		txtServiceName.setText(selectedService);
		txtServiceName.setTextAppearance(getApplicationContext(), R.style.normalText);
		txtServiceName.setPadding(10, 0, 0, 0);

		rowServiceName.addView(lblServiceName);
		rowServiceName.addView(txtServiceName);

		/* SECOND ROW */
		TableRow rowServiceDesc = new TableRow(this);

		TextView lblServiceDesc = new TextView(this);
		lblServiceDesc.setText(R.string.lblServiceDesc);
		lblServiceDesc.setTextAppearance(getApplicationContext(), R.style.titleText);

		TextView txtServiceDesc = new TextView(this);
		txtServiceDesc.setTextAppearance(getApplicationContext(), R.style.normalText);
		txtServiceDesc.setPadding(10, 0, 0, 0);
		List<GateRuntimeParameter> params = null;
		ServiceInfoForClientArray list = getServices();
		for(int i=0; i < list.getItem().size(); i++){
			if(list.getItem().get(i).getServiceName().equals(selectedService)){
				txtServiceDesc.setText(list.getItem().get(i).getServiceDescription());
				params = list.getItem().get(i).getParams();
				break;
			}
		}

		TextView lblParams = new TextView(this);
		lblParams.setText(R.string.lblServiceParams);
		lblParams.setTextAppearance(getApplicationContext(), R.style.titleText);
		output.addView(lblParams);

		LayoutParams txtParamsAttrbs = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		LinearLayout paramsLayout = new LinearLayout(this);
		paramsLayout.setId(0);

		if(params.size() > 0){
			ScrollView scroll = new ScrollView(this);
			scroll.setLayoutParams(txtParamsAttrbs);
			paramsLayout.setOrientation(LinearLayout.VERTICAL);
			scroll.addView(paramsLayout);
			for(int j=0; j < params.size(); j++){
				TextView lblParamName = new TextView(this);
				lblParamName.setText(params.get(j).getParamName());
				EditText tview = new EditText(this);
				tview.setId(1);
				tview.setText(params.get(j).getDefaultValueString());
				LayoutParams txtViewLayoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				tview.setLayoutParams(txtViewLayoutParams);
				paramsLayout.addView(lblParamName);
				paramsLayout.addView(tview);
			}
			output.addView(scroll);
		}else{
			TextView lblParamName = new TextView(this);
			lblParamName.setText(R.string.lblRTParams);
			output.addView(lblParamName);
		}

		rowServiceDesc.addView(lblServiceDesc);
		rowServiceDesc.addView(txtServiceDesc);

		serviceInfoTbl.addView(rowServiceName);
		serviceInfoTbl.addView(rowServiceDesc);

		output.setOrientation(LinearLayout.VERTICAL);
		output.setGravity(Gravity.TOP);

		return output;
	}

	/*public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:	{
				// call the same method as the All services button, if the user has gone deeper into this page.
				return false;
			}
			default:{
				return super.onKeyDown(keyCode, event);
			}
		}
	}*/

	/** Asynchronous task to invoke an assistant. */
	class InvocationTask extends AsyncTask<Void, Void, Void>{
		String responseString = "";
		Map<String, String> userParams = new HashMap<String,String>();
		/** Sends a POST request to invoke a selected assistant. */
		@Override
		protected Void doInBackground(Void... params) {
			if(serverURL.indexOf("https") < 0){
				LinearLayout paramsLayout = (LinearLayout) findViewById(0);
				ArrayList<EditText> paramsList = new ArrayList<EditText>();
				if(paramsLayout != null){
					// find all the parameters text edits
					for( int i = 0; i < paramsLayout.getChildCount(); i++ ){
						if( paramsLayout.getChildAt( i ) instanceof EditText ){
							paramsList.add( (EditText) paramsLayout.getChildAt( i ));
						}
					}

					// find their names in the service object
					List<GateRuntimeParameter> PipelineParamsList = null;
					ServiceInfoForClientArray list = getServices();
					for(int i=0; i < list.getItem().size(); i++){
						if(list.getItem().get(i).getServiceName().equals(selectedService)){
							PipelineParamsList = list.getItem().get(i).getParams();
							break;
						}
					}

					// prepare for request representation
					for(int i=0; i < PipelineParamsList.size(); i++){
						userParams.put(PipelineParamsList.get(i).getParamName(), paramsList.get(i).getText().toString());
					}
				}

				RequestRepresentation request = new RequestRepresentation(SemAssistApp.getInstance(), selectedService, userParams, txtInput.getText().toString());
				Representation representation = new StringRepresentation(request.getXML(),MediaType.APPLICATION_XML);
				String uri = serverURL + "/service";
				Log.i(TAG, "Sending POST invocation request via Restlet to " + uri);
				Representation response = new ClientResource(uri).post(representation);
				try {
					StringWriter writer = new StringWriter();
					response.write(writer);
					responseString = writer.toString();
					System.out.println(responseString);

					// open the results activity
					Intent intent = new Intent(getBaseContext(), SemanticResultsActivity.class);
					intent.putExtra("xml", responseString);
					Log.i(TAG, "Parsing server response: " + responseString);
					startActivity(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}else{
				try{
					URI uri = new URI(serverURL+"/service");
					Log.i(TAG, "Sending secure POST invocation request to " + uri);
					HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
					DefaultHttpClient client = new DefaultHttpClient();

					SchemeRegistry registry = new SchemeRegistry();
					final KeyStore ks = KeyStore.getInstance("BKS");
					// NOTE: the keystore must have been generated with BKS 146 and not later
					final InputStream in = getApplicationContext().getResources().openRawResource(R.raw.clientkeystorenew);  
					try {
						ks.load(in,getString(R.string.keystorePassword).toCharArray());
					} finally {
						in.close();
					}

					SSLSocketFactory socketFactory = new CustomSSLSocketFactory(ks);
					socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
					registry.register(new Scheme("https", socketFactory, 443));
					SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
					DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());

					// Set verifier
					HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
					RequestRepresentation request = new RequestRepresentation(SemAssistApp.getInstance(),selectedService, null, txtInput.getText().toString());
					Representation representation = new StringRepresentation(request.getXML(),MediaType.APPLICATION_XML);

					HttpPost post = new HttpPost(uri);
					post.setEntity(new StringEntity(representation.getText()));

					HttpResponse response = httpClient.execute(post);
					HttpEntity entity = response.getEntity();
					InputStream inputstream = entity.getContent();
					InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
					BufferedReader bufferedreader = new BufferedReader(inputstreamreader);

					String string = null;
					while ((string = bufferedreader.readLine()) != null) {
						responseString += string;
					}
				}catch (Exception e){
					e.printStackTrace();
				}
				Intent intent = new Intent(getBaseContext(), SemanticResultsActivity.class);
				intent.putExtra("xml", responseString);
				Log.i(TAG, "Parsing server response: " + responseString);
				startActivity(intent);
				finish();
			}
			return null;
		}
	}
}
