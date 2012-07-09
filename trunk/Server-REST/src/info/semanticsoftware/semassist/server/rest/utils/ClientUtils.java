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
package info.semanticsoftware.semassist.server.rest.utils;

import java.io.StringWriter;
import java.util.Map;
import java.util.Set;

import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;

/** Semantic Assistants RESTful interface Client-Side Utils.
 *  @author Bahar Sateli
 *  */
public class ClientUtils {

	/** class singleton object. */
	private static ClientUtils instance = null;

	/** Protected constructor. **/
	protected ClientUtils(){
		// defeat instantiation
	}

	/**
	 * Returns the class singleton object.
	 * @return singleton object
	 */
	public static ClientUtils getInstance(){
		if(instance == null){
			instance = new ClientUtils();
		}
		return instance;
	}

	/**
	* Creates an XML representation for a service invocation request.
	* @param serviceName NLP service name to invoke
	* @param params list of runtime parameters
	* @param inputString input string for analysis
	* @param authenticationNeeded authentication mode
	* @param username username (only needed in authentication mode)
	* @param password password (only needed in authentication mode)
	* @throws Exception if user credentials are invalid
	* @return XML representation of the request
	* */
	public static String createXMLServiceRequest(final String serviceName, final Map<String, String> params,
			final String inputString, final boolean authenticationNeeded, final String username, final String password) throws Exception{
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		buffer.append("<invocation>");
		buffer.append("<authenticationNeeded>");
		if(authenticationNeeded){
			if(username != null && username.length() > 0 && password != null && password.length() > 0){
				buffer.append("yes");
				buffer.append("</authenticationNeeded>");
				String normUsername = username;
				if(username.indexOf("@") > -1){
					normUsername = username.substring(0, username.indexOf("@"));
				}
				buffer.append("<username>").append(normUsername).append("</username>");
				buffer.append("<password>").append(password).append("</password>");
			}else{
				throw new Exception("User credentials cannot be empty or null in authentication mode. Provide valid credentials or set the \"authenticationNeeded\" argument to false.");
			}
		}else{
			buffer.append("no");
			buffer.append("</authenticationNeeded>");
		}
		buffer.append("<serviceName>").append(serviceName).append("</serviceName>");
		if(params !=null){
			Set<String> paramNames = params.keySet();
			for(String name:paramNames){
				buffer.append("<param>");
					buffer.append("<name>").append(name).append("</name>");
					buffer.append("<value>");
					buffer.append(params.get(name));
					buffer.append("</value>");
				buffer.append("</param>");
			}
		}

		buffer.append("<input><![CDATA[");
		buffer.append(inputString);
		buffer.append("]]></input>");

		buffer.append("</invocation>");
		return buffer.toString();
	}

	/**
	 * Test cases for server authentication modes.
	 * @param args runtime arguments
	 */
	public static void main(final String[] args){
		try{
			// authentication mode needed
			//String request = createXMLServiceRequest("Person and Location Extractor", null, "Hello John", true, "admin" , "admin");

			// without authentication
			String request = createXMLServiceRequest("Person and Location Extractor", null, "Hello John", false, null , null);

			/* faulty requests for testing */
			//String request = createXMLServiceRequest("Person and Location Extractor", null, "Hello John", true, "admin" , "");
			//String request = createXMLServiceRequest("Person and Location Extractor", null, "Hello John", true, "admin" , "wrongpassword");
			//String request = createXMLServiceRequest("Person and Location Extractor", null, "Hello John", false, "admin" , "admin");

			Representation representation = new StringRepresentation(request);
			String uri = "http://localhost:8182/service";
			Representation response = new ClientResource(uri).post(representation);
			StringWriter writer = new StringWriter();
			response.write(writer);
			System.out.println(writer.toString());
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
