package info.semanticsoftware.semassist.android.activity;

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
import android.util.Xml;

public class GlobalSettingsActivity extends PreferenceActivity{
	
	public static String SERVERS_XML_FILE_PATH = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.semassist_settings); 
		SERVERS_XML_FILE_PATH = getFilesDir().getAbsolutePath()+ File.separator + "servers.xml";
		
		populateServersList();
		
		EditTextPreference serverCreator = (EditTextPreference) findPreference("new_server_info");

		serverCreator.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference arg0, Object arg1) {
				final EditTextPreference serverCreator = (EditTextPreference) findPreference("new_server_info");
				System.out.println(serverCreator.getEditText().getText());
				
				String address = serverCreator.getEditText().getText().toString();
        		String[] tokens = address.split(":");
        		Map<String, String> map = new HashMap<String, String>();
        		System.out.println("input host is " + tokens[0]);
        		System.out.println("input port is " + tokens[1]);
        		map.put(ClientUtils.XML_HOST_KEY, tokens[0]);
        		map.put(ClientUtils.XML_PORT_KEY, tokens[1]);
        		setClientPreference("android", "server", map);
        		
        		try {
        			FileInputStream fstream;
        			fstream = new FileInputStream(new File(SERVERS_XML_FILE_PATH));
        			DataInputStream in = new DataInputStream(fstream);
        			  BufferedReader br = new BufferedReader(new InputStreamReader(in));
        			  String strLine;
        			  //Read File Line By Line
        			  while ((strLine = br.readLine()) != null)   {
        			  // Print the content on the console
        			  System.out.println (strLine);
        			  }
        			  //Close the input stream
        			  in.close();
        			  
        			  populateServersList();
        		} catch (FileNotFoundException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        		// Get the object of DataInputStream
         catch (IOException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
				return true;
			}
		});
		
		/* This is how you restrict the input type
		 * serverCreator.getEditText().setKeyListener(DigitsKeyListener.getInstance());
		 * serverCreator.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER); */
		
	}
	
	
	/**
     * Returns a list of elements in the client scope in form of XMLElementModel objects.
     * If the client or element does not exist, it returns an empty list.
     * @see XMLElementModel
     * @param client the target client scope
     * @param element the specific target element to retrieve or null to retrieve all
     *                elements at the @a client scope.
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
					System.out.println("WARNING: Cannot resolve client: \"" + client + "\"");
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
						System.out.println("WARNING: No \"" + element + "\" element found for client \"" + client + "\"");
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
	        //serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
	        serializer.startTag(null, "saProperties");
	        serializer.startTag(null, "global");
	        
	        serializer.startTag(null, "lastCalledServer");
	        serializer.attribute(null, "host", "semassist.ilanguage.ca");
	        serializer.attribute(null, "port", "8182");
	        serializer.endTag(null, "lastCalledServer");
	        
	        serializer.startTag(null, "server");
	        serializer.attribute(null, "host", "semassist.ilanguage.ca");
	        serializer.attribute(null, "port", "8182");
	        serializer.endTag(null, "server");
	        
	        serializer.endTag(null, "global");
	        
	        serializer.startTag(null, "android");
	        
	        serializer.startTag(null, "server");
	        serializer.attribute(null, "host", "semassist.ilanguage.ca");
	        serializer.attribute(null, "port", "8182");
	        serializer.endTag(null, "server");
	        
	        serializer.endTag(null, "android");
	        
	        serializer.endTag(null,"saProperties");
	        serializer.endDocument();
	        serializer.flush();
	        fileos.close();
System.out.println("file created");
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
			  //Read File Line By Line
			  while ((strLine = br.readLine()) != null)   {
			  // Print the content on the console
			  System.out.println (strLine);
			  }
			  //Close the input stream
			  in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
	        serializer.attribute(null, "host", "semassist.ilanguage.ca");
	        serializer.attribute(null, "port", "8182");
	        serializer.endTag(null, "lastCalledServer");
	        
	        serializer.startTag(null, "server");
	        serializer.attribute(null, "host", "semassist.ilanguage.ca");
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
	        
	        
	        /*serializer.startTag(null, "Child2");
	        serializer.attribute(null, "attribute", "value");
	        serializer.endTag(null, "Child2");
	        serializer.startTag(null, "Child3");
	        serializer.text("Some text inside child 3");
	        serializer.endTag(null,"Child3");*/
	        serializer.endTag(null,"saProperties");
	        serializer.endDocument();
	        serializer.flush();
	        fileos.close();

        }catch(Exception e)
        {
            e.printStackTrace();
        }
		/*try {		
			File propertiesFile = new File(SERVERS_XML_FILE_PATH);
			if (propertiesFile.exists()) {
				FileInputStream fstream = new FileInputStream(propertiesFile);
				// Get the object of DataInputStream
				
				DataInputStream in = new DataInputStream(fstream);
				  BufferedReader br = new BufferedReader(new InputStreamReader(in));
				  String strLine;
				  //Read File Line By Line
				  while ((strLine = br.readLine()) != null)   {
				  // Print the content on the console
				  System.out.println (strLine);
				  }
				  //Close the input stream
				  in.close();
				DocumentBuilderFactory builder = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = builder.newDocumentBuilder();
				Document doc = docBuilder.parse(propertiesFile);
				doc.getDocumentElement().normalize();

				Element root = doc.getDocumentElement();
				Element clientTag;
				NodeList clientElement = doc.getElementsByTagName(client);
				Element elementNode = null;
				if(clientElement.getLength() != 0){
					System.out.println("already there");
    				clientTag = (Element) clientElement.item(0);
    				NodeList children = clientTag.getChildNodes();
					for(int i=0; i < children.getLength(); i++){
						if(children.item(i).getNodeName().equals(element)){
							elementNode = (Element) children.item(i);
						}
					}
					
					// if the client is there, but there is no element tag, let's create it
					if(elementNode == null){
						elementNode = doc.createElement(element);
					}
    				
  				}else{
  					clientTag = doc.createElement(client);
  					elementNode = doc.createElement(element);
  				}
				
				//System.out.println("children no " + clientTag.getChildNodes().getLength());
				//for(int b=0; b < clientTag.getChildNodes().getLength(); b++){
				//	System.out.println(clientTag.getChildNodes().item(b).getNodeName());
				//	System.out.println(clientTag.getChildNodes().item(b).getAttributes().getLength());
				//}				
				
				Set<String> keys = map.keySet();
    			for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();){
    	    		String key = iterator.next();
    	    		String value = map.get(key);
    	    		elementNode.setAttribute(key, value);
    	    	}
    			
    			//clientTag.appendChild(elementNode);
				//root.appendChild(clientTag);

				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new FileOutputStream(propertiesFile));

				TransformerFactory transFactory = TransformerFactory.newInstance();
				Transformer transformer = transFactory.newTransformer();
				//transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.transform(source, result);
				
			} else {
		    	createPropertiesFile();
		    	setClientPreference(client,element,map);
			}
		} catch (IOException e1){
			e1.printStackTrace();
		} catch (SAXException e2) {
			e2.printStackTrace();
		} catch (ParserConfigurationException e3) {
			e3.printStackTrace();
		} catch (TransformerConfigurationException e4) {
			e4.printStackTrace();
		} catch (TransformerException e5) {
			e5.printStackTrace();
		} catch (Exception e){
			System.out.println(e.getMessage());
		}*/
		
    }
    
    private void populateServersList(){
		final ListPreference serverSelector = (ListPreference) findPreference("selected_server_option");

    	CharSequence entries[] = null;
	    CharSequence entryValues[] = null;
	    
	    List<String> list = new ArrayList<String>();
	    
		ArrayList<XMLElementModel> servers = getClientPreference("android", "server");
		if(servers.size() != 0){
			for(int i=0; i < servers.size(); i++){			
				String URL = "http://" + servers.get(i).getAttribute().get(ClientUtils.XML_HOST_KEY) + ":" + servers.get(i).getAttribute().get(ClientUtils.XML_PORT_KEY);
				list.add(URL);		
			}
			
			entries = list.toArray(new CharSequence[list.size()]);
			entryValues = list.toArray(new CharSequence[list.size()]);
		    serverSelector.setEntries(entries);
		    serverSelector.setEntryValues(entryValues);
		}else{
			System.out.println("servers " + servers.size());
		}
    }
}
