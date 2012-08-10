/*
Semantic Assistants -- http://www.semanticsoftware.info/semantic-assistants

This file is part of the Semantic Assistants architecture.

Copyright (C) 2012, 2013 Semantic Software Lab, http://www.semanticsoftware.info
Rene Witte
Bahar Sateli

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
package info.semanticsoftware.semassist.client.wiki.broker;

import info.semanticsoftware.semassist.csal.ClientUtils;
import info.semanticsoftware.semassist.server.SemanticServiceBroker;
import info.semanticsoftware.semassist.server.SemanticServiceBrokerService;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

public class ServiceAgentSingleton
{
	/** Semantic Service Broker Service static instance. */
	private static SemanticServiceBrokerService service = null;

	/** Semantic Service Broker static instance. */
	private static SemanticServiceBroker broker = null;

	/** Host name value. */
	//private static String serverHost ="";

	/** Port number value. */
	//private static String serverPort = "";

	/** Semantic Assistants server address defined by user. */
	private static String serverAddress = "";

	public static boolean defaultSettings = true;

	/**
	 * This method is the global access point to the singleton instance of this class.
	 * 
	 * @return SemanticServiceBroker The broker instance
	 */
	public static SemanticServiceBroker getInstance()
	{
		try{
/*			if( broker == null ){
				if( service == null ){
					if( !serverHost.isEmpty() && !serverPort.isEmpty() ){*/
						System.out.println( "Creating broker using params");
						service = new SemanticServiceBrokerService( new URL( createURL() ),
								new QName( "http://server.semassist.semanticsoftware.info/","SemanticServiceBrokerService" ) );
/*					}else{
						System.out.println( "Creating broker not using params");
						service = new SemanticServiceBrokerService();
					}
				}*/
				broker = service.getSemanticServiceBrokerPort();
			//}
		}catch(WebServiceException e1){
			System.err.println("Server not found. \nPlease check the Server Host and Port and if Server is Online");
		}
		catch( MalformedURLException e2)
		{
			e2.printStackTrace();
		}
		return broker;
	}

	/** 
	 * Creates and returns a URL for service wsdl file.
	 * 
	 * @return String URL for service WSDL file
	 * */
	private static String createURL()
	{
		System.out.println("Connecting to http://" + serverAddress+ "/SemAssist?wsdl");
		return "http://" + serverAddress + "/SemAssist?wsdl";
	}

	/** 
	 * Adds a new server element to the wiki's preferences section.
	 * 
	 * @param String server's host name
	 * @param String server's port number
	 * */
	public static void addNewServer(final String host, final String port){
		Map<String,String> map = new HashMap<String, String>();
		map.put(ClientUtils.XML_HOST_KEY, host);
		map.put(ClientUtils.XML_PORT_KEY, port);
		ClientUtils.addNewServer(map);
		ClientUtils.setClientPreference("wiki", "server", map);
	}

	/** 
	 * Sets the server host and port number to be used by broker.
	 * @param String address of the server in form of HOSTNAME:PORTNUMBER
	 * */
	public static void setServer(final String address){
		try {
			serverAddress = URLDecoder.decode(address,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
