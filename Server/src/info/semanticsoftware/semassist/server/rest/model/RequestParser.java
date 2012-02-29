/*
Semantic Assistants -- http://www.semanticsoftware.info/semantic-assistants

This file is part of the Semantic Assistants architecture.

Copyright (C) 2011, 2012 Semantic Software Lab, http://www.semanticsoftware.info
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
