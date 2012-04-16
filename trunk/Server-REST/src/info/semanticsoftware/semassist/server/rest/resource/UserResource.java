package info.semanticsoftware.semassist.server.rest.resource;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import info.semanticsoftware.semassist.server.core.security.authentication.AuthenticationUtils;
import info.semanticsoftware.semassist.server.rest.model.RequestHandler;
import info.semanticsoftware.semassist.server.rest.model.UserModel;

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

public class UserResource extends ServerResource{
	
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
	
	@Post
	public StringRepresentation authenticate(Representation representation){
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
            
			AuthenticationUtils authUtil = AuthenticationUtils.getInstance();
			if(authUtil.authenticateUser(username, password)){
				String xml = new UserModel().getXML(username);
				result = new StringRepresentation(xml, MediaType.APPLICATION_XML);
			
				if (xml != null) {
					return result;
				} else {
					setStatus(Status.CLIENT_ERROR_NOT_FOUND);
					return new StringRepresentation("No such user");
				}
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
