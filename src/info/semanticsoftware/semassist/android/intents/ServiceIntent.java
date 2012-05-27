package info.semanticsoftware.semassist.android.intents;

import info.semanticsoftware.semassist.android.activity.R;
import info.semanticsoftware.semassist.android.application.SemAssistApp;
import info.semanticsoftware.semassist.android.encryption.CustomSSLSocketFactory;
import info.semanticsoftware.semassist.android.restlet.RequestRepresentation;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.security.KeyStore;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;

/**
 * Intents abstract class to be extended by concrete instances.
 * @author Bahar Sateli
 * */
public abstract class ServiceIntent {

	private String pipelineName = null;
	private String inputString = null;
	private Map<String, String> RTParams = null;
	
	private String candidServerURL = null;

	public ServiceIntent(final String pipelineName){
		this.pipelineName = pipelineName;
	}

	public String getPipelineName(){
		return this.pipelineName;
	}

	public void setRTParams(final Map<String, String> RTParams){
		this.RTParams = RTParams;
	}
	
	public void setInputString(final String input){
		this.inputString = input;
	}

	public void setCandidServerURL(final String URL){
		this.candidServerURL = URL+"/service";
	}

	public String execute(){
		System.out.println("factory execute for " + pipelineName + " on server " + candidServerURL);
		if(candidServerURL.indexOf("https") < 0){
			RequestRepresentation request = new RequestRepresentation(pipelineName, RTParams, inputString);
			Representation representation = new StringRepresentation(request.getXML(),MediaType.APPLICATION_XML);
			Representation response = new ClientResource(candidServerURL).post(representation);
			String responseString = "";
			try {
				StringWriter writer = new StringWriter();
				response.write(writer);
				responseString = writer.toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return responseString;
		}else{
			try{
				HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
				DefaultHttpClient client = new DefaultHttpClient();

				SchemeRegistry registry = new SchemeRegistry();
				final KeyStore ks = KeyStore.getInstance("BKS");
				// NOTE: the keystore must have been generated with BKS 146 and not later
				final InputStream in = SemAssistApp.getInstance().getContext().getResources().openRawResource(R.raw.clientkeystorenew);  
				try {
					ks.load(in,SemAssistApp.getInstance().getContext().getString(R.string.keystorePassword).toCharArray());
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
				RequestRepresentation request = new RequestRepresentation(pipelineName, RTParams, inputString);
				Representation representation = new StringRepresentation(request.getXML(),MediaType.APPLICATION_XML);

				HttpPost post = new HttpPost(candidServerURL);
				post.setEntity(new StringEntity(representation.getText()));

				HttpResponse response = httpClient.execute(post);
				HttpEntity entity = response.getEntity();
				InputStream inputstream = entity.getContent();
				InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
				BufferedReader bufferedreader = new BufferedReader(inputstreamreader);

				String string = null;
				String responseString = "";
				while ((string = bufferedreader.readLine()) != null) {
					responseString += string;
				}
				return responseString;
			}catch (Exception e){
				e.printStackTrace();
			}
		}//else
		return null;
	}//execute
}
