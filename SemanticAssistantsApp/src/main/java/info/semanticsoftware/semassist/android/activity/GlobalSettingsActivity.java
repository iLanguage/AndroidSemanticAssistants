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

import info.semanticsoftware.semassist.android.utils.Constants;
import info.semanticsoftware.semassist.csal.ClientUtils;
import info.semanticsoftware.semassist.csal.XMLElementModel;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

/**
 * The Semantic Assistants app settings activity.
 * @author Bahar Sateli
 * */
public class GlobalSettingsActivity extends PreferenceActivity{

	/** Logging tag. */
	private final String TAG = "GlobalSettingsActivity";

	/** The Semantic Assistants properties file path. */
	public static String SERVERS_XML_FILE_PATH = "";

	/** Called when the activity is created.
	 * @param savedInstanceState saved instance state */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.semassist_settings);
		SERVERS_XML_FILE_PATH = getFilesDir().getAbsolutePath()+ File.separator + "servers.xml";

		populateServersList();

		final EditTextPreference serverCreator = (EditTextPreference) findPreference("new_server_info");
		TextWatcher watcher = new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(serverCreator.getEditText().getText().toString().length() == 0){
					serverCreator.getEditText().setError("URL cannot be empty");
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void afterTextChanged(Editable s) {}
		};

		serverCreator.getEditText().addTextChangedListener(watcher);

		serverCreator.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference arg0, Object arg1) {
				final EditTextPreference serverCreator = (EditTextPreference) findPreference("new_server_info");

				String address = serverCreator.getEditText().getText().toString();
				if(address.equals("")){
					Toast.makeText(getApplicationContext(), "Invalid Server URL. Please try again.", Toast.LENGTH_LONG).show();
					return false;
				}else{
					int colonPosition = address.lastIndexOf(":");
					String host = address.substring(0, colonPosition);
					String port = address.substring(colonPosition+1);
					Map<String, String> map = new HashMap<String, String>();
					Log.i(TAG, "input host is " + host);
					Log.i(TAG, "input port is " + port);
					map.put(ClientUtils.XML_HOST_KEY, host);
					map.put(ClientUtils.XML_PORT_KEY, port);
					setClientPreference("android", "server", map);

					try {
						FileInputStream fstream;
						fstream = new FileInputStream(new File(SERVERS_XML_FILE_PATH));
						DataInputStream in = new DataInputStream(fstream);
						BufferedReader br = new BufferedReader(new InputStreamReader(in));
						String strLine;
						while ((strLine = br.readLine()) != null){
							Log.d(Constants.TAG, strLine);
						}
						in.close();
						populateServersList();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					catch (IOException e) {
						e.printStackTrace();
					}
					return true;
				}
			}
		});

		/* To restrict the input type
		 * serverCreator.getEditText().setKeyListener(DigitsKeyListener.getInstance());
		 * serverCreator.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER); */

	}

	/**
	 * Returns a list of elements in the client scope in form of XMLElementModel objects.
	 * If the client or element does not exist, it returns an empty list.
	 * @see XMLElementModel
	 * @param client the target client scope
	 * @param element the specific target element to retrieve or null to retrieve all
	 * elements at the @a client scope.
	 * @return a list of elements or an empty list if no such client or element exists
	 */
	public static ArrayList<XMLElementModel> getClientPreference(final String client, final String element){
		final ArrayList<XMLElementModel> result = new ArrayList<XMLElementModel>();
		File propertiesFile = new File(SERVERS_XML_FILE_PATH);
		if(propertiesFile.exists()){
			try {
				DocumentBuilderFactory builder = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder;
				docBuilder = builder.newDocumentBuilder();
				Document doc = docBuilder.parse(propertiesFile);
				doc.getDocumentElement().normalize();

				// Check if such client exists in the XML file or if there are more than one target
				NodeList clientElement = doc.getElementsByTagName(client);
				if(clientElement.getLength() == 0 || clientElement.getLength() > 1){
					Log.w(Constants.TAG, " Cannot resolve client: \"" + client + "\"");
				}else{
					Element clientTag = (Element) clientElement.item(0);
					NodeList children = clientTag.getChildNodes();

					for(int i=0; i < children.getLength(); i++){
						final Element elementNode = (Element) children.item(i);
						// If not specified, include all preference elements defined
						// for the client, else keep just the wanted ones.
						if(element == null || elementNode.getNodeName().equals(element)){
							final XMLElementModel candid = new XMLElementModel();
							candid.setName(elementNode.getNodeName());
							candid.setAttributes(elementNode.getAttributes());
							result.add(candid);
						}
					}

					// if there is no such element in the XML file, results are empty
					if (result.isEmpty()) {
						Log.w(Constants.TAG, " No \"" + element + "\" element found for client \"" + client + "\"");
					}
				}
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			// Generate property file & recursively retry.
			createPropertiesFile();
			return getClientPreference(client, element);
		}
		return result;
	}

	/**
	 * Creates a new preference file in the user home directory
	 * */
	public static void createPropertiesFile(){
		File propertiesFile = new File(SERVERS_XML_FILE_PATH);

		XmlSerializer serializer = Xml.newSerializer();
		try{
			FileOutputStream fileos = new FileOutputStream(propertiesFile);
			serializer.setOutput(fileos, "UTF-8");
			serializer.startDocument(null, Boolean.valueOf(true));
			serializer.startTag(null, "saProperties");
			serializer.startTag(null, "global");

			serializer.startTag(null, "lastCalledServer");
			serializer.attribute(null, "host", "http://loompa.cs.concordia.ca");
			serializer.attribute(null, "port", "8182");
			serializer.endTag(null, "lastCalledServer");

			serializer.startTag(null, "server");
			serializer.attribute(null, "host", "http://loompa.cs.concordia.ca");
			serializer.attribute(null, "port", "8182");
			serializer.endTag(null, "server");

			serializer.endTag(null, "global");

			serializer.startTag(null, "android");

			serializer.startTag(null, "server");
			serializer.attribute(null, "host", "http://loompa.cs.concordia.ca");
			serializer.attribute(null, "port", "8182");
			serializer.endTag(null, "server");

			serializer.endTag(null, "android");

			serializer.endTag(null,"saProperties");
			serializer.endDocument();
			serializer.flush();
			fileos.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}

		try {
			FileInputStream fstream;
			fstream = new FileInputStream(new File(SERVERS_XML_FILE_PATH));
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null){
				Log.d(Constants.TAG, strLine);
			}
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Finds and sets the attribute values of the specified element in the client scope.
	 * If the client does not exist, it first creates the client and then adds the element.
	 * If the client, element and attributes all exist, it just updates the attributes with the provided values in the map.
	 * @param client the target client scope
	 * @param element the target element
	 * @param map a hash map of element attributes in form of <key,value> pairs
	 */
	void setClientPreference(final String client, final String element, final Map<String, String> map){
		ArrayList<XMLElementModel> existingPrefs = getClientPreference("android", "server");
		File propertiesFile = new File(SERVERS_XML_FILE_PATH);
		if(propertiesFile.exists()){
			propertiesFile.delete();
		}

		XmlSerializer serializer = Xml.newSerializer();
		try{
			FileOutputStream fileos = new FileOutputStream(propertiesFile);
			serializer.setOutput(fileos, "UTF-8");
			serializer.startDocument(null, Boolean.valueOf(true));
			//serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
			serializer.startTag(null, "saProperties");
			serializer.startTag(null, "global");

			serializer.startTag(null, "lastCalledServer");
			serializer.attribute(null, "host", "http://loompa.cs.concordia.ca");
			serializer.attribute(null, "port", "8182");
			serializer.endTag(null, "lastCalledServer");

			serializer.startTag(null, "server");
			serializer.attribute(null, "host", "http://loompa.cs.concordia.ca");
			serializer.attribute(null, "port", "8182");
			serializer.endTag(null, "server");

			serializer.endTag(null, "global");

			serializer.startTag(null, "android");
			for(int s=0; s<existingPrefs.size(); s++){
				serializer.startTag(null, "server");
				serializer.attribute(null, "host", existingPrefs.get(s).getAttribute().get(ClientUtils.XML_HOST_KEY));
				serializer.attribute(null, "port", existingPrefs.get(s).getAttribute().get(ClientUtils.XML_PORT_KEY));
				serializer.endTag(null, "server");
			}

			serializer.startTag(null, "server");

			Set<String> keys = map.keySet();
			for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();){
				String key = iterator.next();
				String value = map.get(key);
				serializer.attribute(null, key, value);
			}
			serializer.endTag(null, "server");

			serializer.endTag(null, "android");
			serializer.endTag(null,"saProperties");
			serializer.endDocument();
			serializer.flush();
			fileos.close();

		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/** Reads the list of servers and populates the list. */
	private void populateServersList(){
		final ListPreference serverSelector = (ListPreference) findPreference("selected_server_option");
		CharSequence entries[] = null;
		CharSequence entryValues[] = null;

		List<String> list = new ArrayList<String>();

		ArrayList<XMLElementModel> servers = getClientPreference("android", "server");
		if(servers.size() != 0){
			for(int i=0; i < servers.size(); i++){
				String URL = servers.get(i).getAttribute().get(ClientUtils.XML_HOST_KEY) + ":" + servers.get(i).getAttribute().get(ClientUtils.XML_PORT_KEY);
				list.add(URL);
			}

			entries = list.toArray(new CharSequence[list.size()]);
			entryValues = list.toArray(new CharSequence[list.size()]);
			serverSelector.setEntries(entries);
			serverSelector.setEntryValues(entryValues);
		}else{
			Log.i(TAG, "Available servers " + servers.size());
		}
	}
}
