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

public class ServiceResource extends ServerResource{
	
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
	
	@Post
	public StringRepresentation invoke(Representation representation){
		try {
			System.out.println("Request Content-Length size: " + representation.getSize() + " bytes.");
			String request = representation.getText();
			RequestParser parser = new RequestParser(request);
			StringRepresentation response = new StringRepresentation(parser.executeRequest());
			return response;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
