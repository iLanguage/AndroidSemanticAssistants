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
