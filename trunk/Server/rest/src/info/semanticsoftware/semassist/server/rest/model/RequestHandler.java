package info.semanticsoftware.semassist.server.rest.model;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class RequestHandler extends DefaultHandler{
	// ===========================================================
    // Fields
    // ===========================================================
   
    private boolean serviceNameTag = false;
    private boolean inputTag = false;
    private String serviceName;
    private String input;
   
    // ===========================================================
    // Getter & Setter
    // ===========================================================

    public String getServiceName(){
    	return this.serviceName;
    }
    
    public String getInput(){
    	return this.input;
    }

    // ===========================================================
    // Methods
    // ===========================================================
    @Override
    public void startDocument() throws SAXException {
    	System.out.println("Started parsing service request");

    }

    @Override
    public void endDocument() throws SAXException {
        // Nothing to do
    	System.out.println("Finished parsing service request");
    }

    /** Gets called on opening tags like:
     * <tag>
     * Can provide attribute(s), when xml was like:
     * <tag attribute="attributeValue">*/
    @Override
    public void startElement(String namespaceURI, String localName,String qName, Attributes atts) throws SAXException {
            if (qName.equals("serviceName")) {
                 this.serviceNameTag = true;
            }else if (qName.equals("input")) {
                this.inputTag = true;
	        }
    }
   
    /** Gets called on closing tags like:
     * </tag> */
    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
    	if (qName.equals("serviceName")) {
            this.serviceNameTag = false;
    	}else if (qName.equals("input")) {
        this.inputTag = false;
    	}
    }
   
    /** Gets called on the following structure:
     * <tag>characters</tag> */
    @Override
    public void characters(char ch[], int start, int length) {
            if(this.serviceNameTag){
            	serviceName = new String(ch, start, length);
            }else if(this.inputTag){
            	input = new String(ch, start, length);
            }
    }
}
