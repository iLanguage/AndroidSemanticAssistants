package info.semanticsoftware.semassist.android.activity;

import info.semanticsoftware.semassist.android.application.SemAssistApp;
import info.semanticsoftware.semassist.android.business.AnnotationInstance;
import info.semanticsoftware.semassist.android.business.ServerResponseHandler;
import info.semanticsoftware.semassist.android.encryption.CustomSSLSocketFactory;
import info.semanticsoftware.semassist.csal.ClientUtils;
import info.semanticsoftware.semassist.csal.result.SemanticServiceResult;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Vector;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.impl.conn.SingleClientConnManager;
import org.restlet.data.ClientInfo;
import org.restlet.data.MediaType;
import org.restlet.resource.ClientResource;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.restlet.resource.ResourceException;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/** Presents the results of an assistant invocation.
 * @author Bahar Sateli
 */
public class SemanticResultsActivity extends Activity {
	String fileName;
	String fileContent;
	/** Presents the results in a list format.
	 * @param savedInstanceState saved instance state
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.results);

		TableLayout tblResults = (TableLayout) findViewById(R.id.tblResultsLayout);
		tblResults.setStretchAllColumns(true);

		TableRow resultRow;
		TextView txtContent;
		TextView txtType;
		TextView txtStart;
		TextView txtEnd;
		TextView txtFeats;
		String resultsXML = getIntent().getStringExtra("xml");

		Vector<SemanticServiceResult> results = ClientUtils.getServiceResults(resultsXML);
		for(SemanticServiceResult current: results){
			if (current.mResultType.equals(SemanticServiceResult.ANNOTATION)){
				List<AnnotationInstance> annots = ServerResponseHandler.createAnnotation(current);
				for(int i=0; i < annots.size(); i++){
					resultRow = new TableRow(getApplicationContext());

					txtContent = new TextView(getApplicationContext());
					txtContent.setText(annots.get(i).getContent());
					txtContent.setTextAppearance(getApplicationContext(), R.style.normalText);
					resultRow.addView(txtContent);

					txtType = new TextView(getApplicationContext());
					txtType.setText(annots.get(i).getType());
					txtType.setTextAppearance(getApplicationContext(), R.style.normalText);
					resultRow.addView(txtType);

					txtStart = new TextView(getApplicationContext());
					txtStart.setText(annots.get(i).getStart());
					txtStart.setTextAppearance(getApplicationContext(), R.style.normalText);
					resultRow.addView(txtStart);

					txtEnd = new TextView(getApplicationContext());
					txtEnd.setText(annots.get(i).getEnd());
					txtEnd.setTextAppearance(getApplicationContext(), R.style.normalText);
					resultRow.addView(txtEnd);

					txtFeats = new TextView(getApplicationContext());
					txtFeats.setText(annots.get(i).getFeatures());
					txtFeats.setTextAppearance(getApplicationContext(), R.style.normalText);
					resultRow.addView(txtFeats);

					tblResults.addView(resultRow);
				}
			}else if(current.mResultType.equals(SemanticServiceResult.BOUNDLESS_ANNOTATION)){
				//TODO find an actual pipeline to test this with
			}else if(current.mResultType.equals(SemanticServiceResult.FILE)){
				fileName = current.mFileUrl;
				fileName = fileName.substring(fileName.lastIndexOf("/")+1);
				System.out.println(fileName);
				getFileContentTask task = new getFileContentTask();
				System.out.println("Retrieving file content from " + SemanticAssistantsActivity.serverURL);
				task.execute(SemanticAssistantsActivity.serverURL);
			}
		}
	}

	/** Asynchronous task to retrieve list of available assistants. */
	private class getFileContentTask extends AsyncTask<String, Void, String> {
		/** Gets the list of available assistants from the provided URL.
		 * @param urls Semantic Assistants server URL
		 * @return server XML response
		 */
		protected String doInBackground(String... urls) {
			try {
				final String url = urls[0] + "/file";
				
				if(urls[0].indexOf("https") < 0){
					// Prepare the request
					ClientResource resource = new ClientResource(url+ "/" + fileName);
					ClientInfo info = new ClientInfo(MediaType.TEXT_XML);
					resource.setClientInfo(info);
					StringWriter writer = new StringWriter();
					resource.get(MediaType.TEXT_XML).write(writer);
					fileContent = writer.toString();
					System.out.println(fileContent);
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
						out += string;
					}
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
		 * to open the file in Android browser.
		 * @param result results to show
		 */
		@Override
		protected void onPostExecute(String result) {
			File tempDir = SemAssistApp.getInstance().getApplicationContext().getExternalFilesDir(null);
			File  LogFile = null;
			if(tempDir.canWrite()){
				try {
					LogFile = new File(tempDir, fileName+".htm");
					FileWriter LogWriter = new FileWriter(LogFile, false);
					BufferedWriter out = new BufferedWriter(LogWriter);
					out.write(fileContent);
					out.flush();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Intent browserIntent = new Intent(Intent.ACTION_VIEW);
		    browserIntent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
		    browserIntent.setData(Uri.fromFile(LogFile));
			startActivity(browserIntent);
		}
	}
}
