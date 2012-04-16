package info.semanticsoftware.semassist.server.rest.resource;

import info.semanticsoftware.semassist.server.rest.model.ServiceModel;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class ServicesResource extends ServerResource{

	@Get("xml")
	public Representation getXML() {
		String xml = new ServiceModel().getAllXML();
		Representation representation = new StringRepresentation(xml, MediaType.APPLICATION_XML);
		return representation;
	}
}
