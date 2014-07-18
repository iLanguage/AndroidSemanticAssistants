/*
Semantic Assistants -- http://www.semanticsoftware.info/semantic-assistants

This file is part of the Semantic Assistants architecture.

Copyright (C) 2013, 2014 Semantic Software Lab, http://www.semanticsoftware.info
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
import info.semanticsoftware.semassist.server.rest.utils.Constants;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

/**
 * Router class to execute service resource requests.
 * 
 * @author Bahar Sateli
 */
public class ServiceResource extends ServerResource {

	/**
	 * Handles HTTP GET requests. Each request is passed to the ServiceModel
	 * class to find the corresponding service object. The results is an XML or
	 * JSON representation of the service.
	 * 
	 * @return XML or JSON representation of the service object
	 * */
	@Get
	public Representation getRepresentation() {
		String serviceName = (String) getRequest().getAttributes().get(
				"serviceName");
		// FIXME make proper URL encoding
		serviceName = serviceName.replace("%20", " ");

		StringRepresentation representation = null;

		// What MIME types does the client accept?
		Form headers = (Form) getRequestAttributes().get(
				"org.restlet.http.headers");
		String accepts = headers.getFirstValue("Accept");
		// if no MIME provided, fall back to XML (for backward compatibility
		// with other SA clients
		accepts = (accepts == null) ? "xml" : accepts;
		System.out.println("Request Accepts: " + accepts);

		switch (Constants.MIME_TYPES.valueOf(accepts.toUpperCase())) {
		case JSON:
			String json = new ServiceModel().getJSON(serviceName);
			representation = new StringRepresentation(json,
					MediaType.APPLICATION_JSON);
			break;

		case XML:
		case APPXHTML:
		case APPXML:
			String xml = new ServiceModel().getXML(serviceName);
			if (xml == null) {
				setStatus(Status.CLIENT_ERROR_NOT_FOUND);
				return null;
			}
			representation = new StringRepresentation(xml,
					MediaType.APPLICATION_XML);
			break;
		case TEXT:
			// TODO send a plain text representation
			break;
		default:
			break;
		}
		return representation;

	}

	/**
	 * Handles HTTP POST requests. Each requests is passed to the handler class,
	 * which in eventually executes an invocation call to the SA server.
	 * 
	 * @param representation
	 *            representation of the request
	 * @return string representation of the server response
	 * */
	@Post
	public StringRepresentation invoke(final Representation representation) {
		try {
			System.out.println("Request Content-Length size: "
					+ representation.getSize() + " bytes.");
			String request = representation.getText();
			System.out.println("REQUEST: " + request);
			RequestParser parser = new RequestParser(request);
			StringRepresentation response = new StringRepresentation(
					parser.executeRequest());
			return response;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
