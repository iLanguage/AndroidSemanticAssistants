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

import info.semanticsoftware.semassist.server.GateRuntimeParameterArray;
import info.semanticsoftware.semassist.server.UriList;
import info.semanticsoftware.semassist.server.UserContext;
import info.semanticsoftware.semassist.server.rest.business.ServiceAgentSingleton;

import java.io.StringReader;
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
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();

			/* Create a new ContentHandler and apply it to the XML-Reader*/
			RequestHandler handler = new RequestHandler();
			xr.setContentHandler(handler);

			/* Parse the XML data from the request string */
			xr.parse(new InputSource(new StringReader(requestRepresentation)));

			/* Get the parsed data to invoke the service */
			StringArray content = new StringArray();
			UriList urilist = new UriList();

			System.out.println("Invocation asked for " + handler.getServiceName());

			String encryptedText = handler.getInput();
			
			//STEP 3: Execute the request
			urilist.getUriList().add("#literal");
			content.getItem().add(encryptedText.trim());

			String results = ServiceAgentSingleton.getInstance().invokeService(handler.getServiceName(), urilist, content, 0L, new GateRuntimeParameterArray(), new UserContext());
			System.out.println("results" + results);
			return results;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
