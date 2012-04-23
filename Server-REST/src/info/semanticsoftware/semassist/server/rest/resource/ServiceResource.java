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

import info.semanticsoftware.semassist.server.rest.model.RequestParser;
import info.semanticsoftware.semassist.server.rest.model.ServiceModel;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

/** Router class to execute service resource requests.
 * @author Bahar Sateli
 */
public class ServiceResource extends ServerResource{

	/**
	 * Handles HTTP GET requests. Each request is passed
	 * to the ServiceModel class to find the corresponding
	 * service object. The results is an XML representation
	 * of the service.
	 * @return XML representation of the service object
	 * */
	@Get("xml")
	public Representation getXML() {
		String serviceName = (String) getRequest().getAttributes().get("serviceName");
		StringRepresentation representation = null;	
		String xml = new ServiceModel().getXML(serviceName);
		representation = new StringRepresentation(xml, MediaType.APPLICATION_XML);

		if (xml != null) {
			return representation;
		} else {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			return null;
		}
	}

	/** Handles HTTP POST requests. Each requests is passed
	 * to the handler class, which in eventually executes
	 * an invocation call to the SA server. 
	 * @param representation representation of the request
	 * @return string representation of the server response
	 * */
	@Post
	public StringRepresentation invoke(Representation representation){
		try {
			System.out.println("Request Content-Length size: " + representation.getSize() + " bytes.");
			String request = representation.getText();
			System.out.println("REQUEST: " + request);
			RequestParser parser = new RequestParser(request);
			StringRepresentation response = new StringRepresentation(parser.executeRequest());
			return response;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
