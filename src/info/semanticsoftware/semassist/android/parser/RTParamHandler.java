package info.semanticsoftware.semassist.android.parser;

import info.semanticsoftware.semassist.server.GateRuntimeParameter;
import info.semanticsoftware.semassist.server.GateRuntimeParameterArray;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class RTParamHandler extends DefaultHandler{
	// ===========================================================
    // Fields
    // ===========================================================
   
    private boolean paramTag = false;
    private boolean paramNameTag = false;
    private boolean paramTypeTag = false;
    private boolean paramIsOptional = false;
    private boolean paramDefaultValueTag = false;
    private boolean paramPipelineTag = false;
    private boolean paramPRTag = false;
    private GateRuntimeParameterArray paramsList;
    private GateRuntimeParameter parsedParamObject;

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    public GateRuntimeParameterArray getParsedData() {
            //return this.parsedServiceObject;
            return this.paramsList;
    }

    // ===========================================================
    // Methods
    // ===========================================================
    @Override
    public void startDocument() throws SAXException {
            //this.parsedServiceObject = new ServiceInfoForClient();
    		paramsList = new GateRuntimeParameterArray();
    }

    @Override
    public void endDocument() throws SAXException {
            // Nothing to do
    }

    /** Gets called on opening tags like:
     * <tag>
     * Can provide attribute(s), when xml was like:
     * <tag attribute="attributeValue">*/
    @Override
    public void startElement(String namespaceURI, String localName,String qName, Attributes atts) throws SAXException {
           	if (localName.equals("RTParam")) {
                    this.paramTag = true;
            }else if (localName.equals("paramName")) {
                    this.paramNameTag = true;
            }else if (localName.equals("paramType")) {
                	this.paramTypeTag = true;
            }else if (localName.equals("defaultValue")) {
            		this.paramDefaultValueTag = true;
            }else if (localName.equals("pipelineName")) {
        		this.paramPipelineTag = true;
            }else if (localName.equals("PRName")) {
        		this.paramPRTag = true;
            }else if (localName.equals("isOptional")) {
            		this.paramIsOptional = true;
                    // Extract an Attribute
                    //String attrValue = atts.getValue("thenumber");
                    //int i = Integer.parseInt(attrValue);
                    //myParsedExampleDataSet.setExtractedInt(i);
            }/*else{
            	throw new SAXException("An unknown tag was found in XML: " + localName);
            }*/
    }
   
    /** Gets called on closing tags like:
     * </tag> */
    @Override
    public void endElement(String namespaceURI, String localName, String qName)
                    throws SAXException {
    	if (localName.equals("RTParam")) {
	            this.paramTag = false;
	            paramsList.getItem().add(parsedParamObject);
	    }else if (localName.equals("paramName")) {
	            this.paramNameTag = false;
	    }else if (localName.equals("paramType")) {
	        	this.paramTypeTag = false;
	    }else if (localName.equals("defaultValue")) {
	    		this.paramDefaultValueTag = false;
	    }else if (localName.equals("pipelineName")) {
			this.paramPipelineTag = false;
	    }else if (localName.equals("PRName")) {
			this.paramPRTag = false;
	    }else if (localName.equals("isOptional")) {
	    		this.paramIsOptional = false;
	            // Extract an Attribute
	            //String attrValue = atts.getValue("thenumber");
	            //int i = Integer.parseInt(attrValue);
	            //myParsedExampleDataSet.setExtractedInt(i);
	    }/*else{
	    	throw new SAXException("An unknown tag was found in XML: " + localName);
	    }*/
    }
   
    /** Gets called on the following structure:
     * <tag>characters</tag> */
    @Override
    public void characters(char ch[], int start, int length) {
            if(this.paramTag){
            	parsedParamObject = new GateRuntimeParameter();
            }else if(this.paramNameTag){
            	parsedParamObject.setParamName(new String(ch, start, length));
            }else if(this.paramTypeTag){
            	parsedParamObject.setParamName(new String(ch, start, length));
            }else if(this.paramDefaultValueTag){
            	parsedParamObject.setDefaultValueString(new String(ch, start, length));
            }else if(this.paramPipelineTag){
            	parsedParamObject.setPipelineName(new String(ch, start, length));
            }else if(this.paramPRTag){
            	parsedParamObject.setPRName(new String(ch, start, length));
            }else if(this.paramIsOptional){
            	parsedParamObject.setOptional(Boolean.getBoolean(new String(ch, start, length)));
            }
    }

}
