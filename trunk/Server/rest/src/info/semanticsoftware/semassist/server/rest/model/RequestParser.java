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

public class RequestParser {
	private String requestRepresentation = null;
	
	public RequestParser(String representation){
		requestRepresentation = representation;
	}
	
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
                urilist.getUriList().add("#literal");
                content.getItem().add(handler.getInput());
                System.out.println("Invocation asked for " + handler.getServiceName());
                System.out.println("Input is" + content.getItem().get(0));
                String results = ServiceAgentSingleton.getInstance().invokeService(handler.getServiceName(), urilist, content, 0L, new GateRuntimeParameterArray(), new UserContext());
                System.out.println(results); 
                return results;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
	}
}
