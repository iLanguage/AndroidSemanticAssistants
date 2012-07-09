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

package info.semanticsoftware.semassist.server.rest.model;

import info.semanticsoftware.semassist.server.GateRuntimeParameter;
import info.semanticsoftware.semassist.server.GateRuntimeParameterArray;
import info.semanticsoftware.semassist.server.ServiceInfoForClient;
import info.semanticsoftware.semassist.server.UriList;
import info.semanticsoftware.semassist.server.UserContext;
import info.semanticsoftware.semassist.server.core.security.authentication.AuthenticationUtils;
import info.semanticsoftware.semassist.server.rest.business.ServiceAgentSingleton;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.java.dev.jaxb.array.StringArray;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * Parses a request XML message and executes an invocation call
 * to the Semantic Assistants server.
 * @author Bahar Sateli 
 * */
public class RequestParser {
	private String requestRepresentation = null;

	/** Public constructor.
	 * @param representation user request's XML representation*/
	public RequestParser(final String representation){
		requestRepresentation = representation;
	}

	/**
	 * Parses a request and executes an invocation call
	 * to the Semantic Assistants server.
	 * @return server's XML response
	 * */
	public String executeRequest(){
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser parser = spf.newSAXParser();
			XMLReader reader = parser.getXMLReader();

			/* Create a new RequestHandler and apply it to the XML-Reader*/
			RequestHandler handler = new RequestHandler();
			reader.setContentHandler(handler);

			/* Parse the XML data from the request string */
			reader.parse(new InputSource(new StringReader(requestRepresentation)));

			/* Do we need user authentication? */
			if(handler.needsAuthentication()){
				System.out.println("Request asks for authentication mode. Looking up credentials in the database...");
				// do the DB look up
				if(!AuthenticationUtils.getInstance().authenticateUser(handler.getUsername(), handler.getPassword())){
					System.err.println("Invalid user credentials. Aborting the request...");
					return "Authentication failed.";
				}else{
					System.out.println("Credentials OK. Executing service invocation request...");
				}
			}

			/* Get the parsed data and the service name to invoke */
			StringArray content = new StringArray();
			UriList urilist = new UriList();
			String selectedServiceName = handler.getServiceName();
			String encryptedText = handler.getInput();
			urilist.getUriList().add("#literal");
			content.getItem().add(encryptedText.trim());
			System.out.println("Service invocation asked for: " + selectedServiceName);

			// Take care of the runtime parameters
			List<GateRuntimeParameter> serviceParams = new ArrayList<GateRuntimeParameter>();
			for(ServiceInfoForClient service : ServiceAgentSingleton.getInstance().getAvailableServices().getItem()){
				if(service.getServiceName().equals(selectedServiceName)){
					serviceParams = service.getParams();
				}
			}
			Map<String, String> parsedParamsList = handler.getParams();
			GateRuntimeParameterArray arrayToGo = new GateRuntimeParameterArray();
			for(Iterator<GateRuntimeParameter> itr = serviceParams.iterator(); itr.hasNext();){
				GateRuntimeParameter param = itr.next();
				String paramName = param.getParamName();
				String type = param.getType();
				if(type.equals("int")){
					param.setIntValue(Integer.parseInt(parsedParamsList.get(paramName)));
				}else if(type.equals("string")){
					param.setStringValue(parsedParamsList.get(paramName));
				}else if(type.equals("double")){
					param.setDoubleValue(Double.parseDouble(parsedParamsList.get(paramName)));
				}else if(type.equals("boolean")){
					param.setBooleanValue(Boolean.parseBoolean(parsedParamsList.get(paramName)));
				}else if(type.equals("url")){
					param.setUrlValue(parsedParamsList.get(paramName));
				}
				System.out.println(paramName + "=" + param.getStringValue());
				arrayToGo.getItem().add(param);
			}

			// Execute the request
			String results = ServiceAgentSingleton.getInstance().invokeService(selectedServiceName, urilist, content, 0L, arrayToGo, new UserContext());
			System.out.println("Server response: " + results);
			return results;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
