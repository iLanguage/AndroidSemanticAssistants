package info.semanticsoftware.semassist.client.eclipse.handlers;

/*
Semantic Assistants -- http://www.semanticsoftware.info/semantic-assistants

This file is part of the Semantic Assistants architecture.

Copyright (C) 2009 Semantic Software Lab, http://www.semanticsoftware.info
    Nikolaos Papadakis
    Tom Gitzinger

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

import info.semanticsoftware.semassist.client.eclipse.dialogs.FileSelectionDialog;
//import info.semanticsoftware.semassist.client.eclipse.utils.Utils;
import info.semanticsoftware.semassist.server.SemanticServiceBroker;
import info.semanticsoftware.semassist.server.SemanticServiceBrokerService;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

public class ServiceAgentSingleton
{

	/** Semantic Service Broker Service static instance */
	private static SemanticServiceBrokerService service = null;
	
	/** Semantic Service Broker static instance */
	private static SemanticServiceBroker broker = null;
	
    /** Host name value */
	private static String serverHost ="";
	
    /** Port number value */
	private static String serverPort = "";

	/**
	 * This method is the global access point to the singleton instance of this class
	 * 
	 * @return SemanticServiceBroker The broker instance
	 */
	public static SemanticServiceBroker getInstance()
	{

		//Utils.propertiesReader();

		try
	    {
//	        if( broker == null )
//	        {
//	            if( service == null )
//	            {
	                if( !serverHost.isEmpty() && !serverPort.isEmpty() )
	                {
	                    System.out.println("Creating broker using hostname " + serverHost + " and port "+ serverPort );
	                    service = new SemanticServiceBrokerService( new URL( createURL() ),
	                            new QName( "http://server.semassist.semanticsoftware.info/",
	                            "SemanticServiceBrokerService" ) );
	                    
	                }
	                else
	                {
	                    System.out.println( "Creating broker using default values");
	              	  	service = new SemanticServiceBrokerService();                      
	                }
	
	            //}
	            broker = service.getSemanticServiceBrokerPort();
	        //}
	    }catch(WebServiceException e1){
	    	System.err.println("Server not found. \nPlease check the Server Host and Port and if Server is Online");
	    	FileSelectionDialog.CONNECTION_IS_FINE = false;
	    	//e1.printStackTrace();
	    }
	    catch( MalformedURLException e2)
	    {
	        e2.printStackTrace();
	    }
	
	    return broker;
	}
	
	/** 
	 * Creates and returns a URL for service wsdl file 
	 * 
	 * @return String URL for service WSDL file
	 * */
	private static String createURL()
	{
		return "http://" + serverHost + ":" + serverPort + "/SemAssist?wsdl";
	}
	
	/** 
	 * Sets the host name with the value provided 
	 * 
	 * @param value The value provided as host name
	 * */
	public static void setServerHost(String value)
	{
	   serverHost = value;
	}
	
	/** 
	 * Sets the port number with the value provided 
	 * 
	 * @param value The value provided as port number
	 * */
	public static void setServerPort(String value )
	{
	    serverPort = value;
	}
	
	/** Return the host name as String 
	 * 
	 * @return String Server host name
	 * */
	public static String getServerHost()
	{
	   return serverHost;
	}
	
	/** Return the port number as String 
	 * 
	 * @return String Server port number
	 * */
	public static String getServerPort()
	{
	    return serverPort;
	}
}