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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class RequestHandler extends DefaultHandler{
   
    private boolean serviceNameTag = false;
    private boolean inputTag = false;
    private String serviceName;
    private String input;
   
    public String getServiceName(){
    	return this.serviceName;
    }
    
    public String getInput(){
    	return this.input;
    }

    @Override
    public void startDocument() throws SAXException {
    	System.out.println("Started parsing service request");

    }

    @Override
    public void endDocument() throws SAXException {
        // Nothing to do
    	System.out.println("Finished parsing service request");
    }

    @Override
    public void startElement(String namespaceURI, String localName,String qName, Attributes atts) throws SAXException {
            if (qName.equals("serviceName")) {
                 this.serviceNameTag = true;
            }else if (qName.equals("input")) {
                this.inputTag = true;
	        }
    }
   
    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
    	if (qName.equals("serviceName")) {
            this.serviceNameTag = false;
    	}else if (qName.equals("input")) {
        this.inputTag = false;
    	}
    }
   
    @Override
    public void characters(char ch[], int start, int length) {
            if(this.serviceNameTag){
            	serviceName = new String(ch, start, length);
            }else if(this.inputTag){
            	input = new String(ch, start, length);
            }
    }
}
