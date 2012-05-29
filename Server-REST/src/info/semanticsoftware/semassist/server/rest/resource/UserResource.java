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

package info.semanticsoftware.semassist.server.rest.resource;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import info.semanticsoftware.semassist.server.core.security.authentication.AuthenticationUtils;
import info.semanticsoftware.semassist.server.rest.model.RequestHandler;
import info.semanticsoftware.semassist.server.rest.model.UserModel;
import info.semanticsoftware.semassist.server.rest.utils.Constants;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/** Router class to execute user requests.
 * @author Bahar Sateli
 */
public class UserResource extends ServerResource{

	/** Handles HTTP GET requests. Returns an XML
	 * representation of an authenticated user.
	 * @return XML representation of a user
	 */
	@Get("xml")
	public Representation getXML() {
		String userName = (String) getRequest().getAttributes().get("userName");
		StringRepresentation representation = null;
		String xml = new UserModel().getXML(userName);
		representation = new StringRepresentation(xml, MediaType.APPLICATION_XML);

		if (xml != null) {
			return representation;
		} else {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			return null;
		}
	}

	/** Handles HTTP POST requests. Returns the results
	 * of a user authentication process.
	 * @param representation user representation 
	 * @return string representation of authentication result
	 */
	@Post
	public StringRepresentation authenticate(final Representation representation){
		StringRepresentation result = null;
		try {
			String authRequest = representation.getText();
			System.out.println(authRequest);

			SAXParserFactory spf = SAXParserFactory.newInstance(); 
			SAXParser sp = spf.newSAXParser(); 
			XMLReader xr = sp.getXMLReader(); 

			RequestHandler handler = new RequestHandler();
			xr.setContentHandler(handler);

			/* Parse the XML data from the request string */
			xr.parse(new InputSource(new StringReader(authRequest)));

			String username = handler.getUsername();
			String password = handler.getPassword();

			// database lookup
			AuthenticationUtils authUtil = AuthenticationUtils.getInstance();
			// client response
			if(authUtil.authenticateUser(username, password)){
				String xml = new UserModel().getXML(username);
				result = new StringRepresentation(xml, MediaType.APPLICATION_XML);
				if (xml != null) {
					return result;
				}
			}else {
				//setStatus(Status.CLIENT_ERROR_NOT_FOUND);
				return new StringRepresentation(Constants.AUHTENTICATION_FAIL);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return result;
	}

}
