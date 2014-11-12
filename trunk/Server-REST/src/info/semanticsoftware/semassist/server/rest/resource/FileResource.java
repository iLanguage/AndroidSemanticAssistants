/*
Semantic Assistants -- http://www.semanticsoftware.info/semantic-assistants

This file is part of the Semantic Assistants architecture.

Copyright (C) 2014 Semantic Software Lab, http://www.semanticsoftware.info
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

import info.semanticsoftware.semassist.server.rest.business.ServiceAgentSingleton;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
/**
 * Router class to return file results.
 * @author Bahar Sateli
 * */
public class FileResource extends ServerResource{

	/** Handles HTTP GET requests. Returns the specified fileName 
	 * content as a string representation.
	 * @return String representation of the file content */
	@Get
	public Representation getFileContent() {
		String fileName = (String) getRequest().getAttributes().get("fileName");
		String fileString = ServiceAgentSingleton.getInstance().getResultFile("file:/tmp/"+fileName);
		StringRepresentation fileContent = new StringRepresentation(fileString, MediaType.TEXT_PLAIN);
		return fileContent;
	}
}
