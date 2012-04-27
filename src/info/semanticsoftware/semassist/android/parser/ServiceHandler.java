package info.semanticsoftware.semassist.android.parser;

import info.semanticsoftware.semassist.server.GateRuntimeParameter;
import info.semanticsoftware.semassist.server.ServiceInfoForClient;
import info.semanticsoftware.semassist.server.ServiceInfoForClientArray;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ServiceHandler extends DefaultHandler{
	
	/* Extract an Attribute
	String attrValue = atts.getValue("thenumber");
	int i = Integer.parseInt(attrValue);
	myParsedExampleDataSet.setExtractedInt(i);*/

	@SuppressWarnings("unused")
	private boolean serviceTag = false;
	private boolean serviceNameTag = false;
	private boolean serviceDescTag = false;
	private ServiceInfoForClientArray servicesList;
	private ServiceInfoForClient parsedServiceObject;
	private boolean paramTag = false;
	private boolean paramNameTag = false;
	private boolean paramTypeTag = false;
	private boolean paramIsOptional = false;
	private boolean paramDefaultValueTag = false;
	private boolean paramPipelineTag = false;
	private boolean paramPRTag = false;
	private GateRuntimeParameter parsedParamObject;


	public ServiceInfoForClientArray getParsedData() {
		return this.servicesList;
	}

	@Override
	public void startDocument() throws SAXException {
		servicesList = new ServiceInfoForClientArray();
	}

	@Override
	public void endDocument() throws SAXException {
		// Nothing to do
		System.out.println("Finished parsing " + servicesList.getItem().size() + " services representation");
	}

	/** Gets called on opening tags like:
	 * <tag>
	 * Can provide attribute(s), when xml was like:
	 * <tag attribute="attributeValue">*/
	@Override
	public void startElement(String namespaceURI, String localName,String qName, Attributes atts) throws SAXException {
		if(qName.equals("service")){
			this.serviceTag = true;
		}else if (qName.equals("serviceName")) {
			this.serviceNameTag = true;
		}else if (qName.equals("serviceDescription")) {
			this.serviceDescTag = true;
		}else if (qName.equals("RTParam")) {
			//this.paramTag = true;
		}else if (qName.equals("paramName")) {
			this.paramNameTag = true;
		}else if (qName.equals("paramType")) {
			this.paramTypeTag = true;
		}else if (qName.equals("defaultValue")) {
			this.paramDefaultValueTag = true;
		}else if (qName.equals("pipelineName")) {
			this.paramPipelineTag = true;
		}else if (qName.equals("PRName")) {
			this.paramPRTag = true;
		}else if (qName.equals("isOptional")) {
			this.paramIsOptional = true;
		}
	}

	/** Gets called on closing tags like:
	 * </tag> */
	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		if (qName.equals("service")){
			this.serviceTag = false;
			servicesList.getItem().add(parsedServiceObject);
		}else if (qName.equals("serviceName")) {
			this.serviceNameTag = false;
		}else if (qName.equals("serviceDescription")) {
			this.serviceDescTag = false;
		}else if (qName.equals("RTParam")) {
			this.paramTag = false;
			parsedServiceObject.getParams().add(parsedParamObject);
		}else if (qName.equals("paramName")) {
			this.paramNameTag = false;
		}else if (qName.equals("paramType")) {
			this.paramTypeTag = false;
		}else if (qName.equals("defaultValue")) {
			this.paramDefaultValueTag = false;
		}else if (qName.equals("pipelineName")) {
			this.paramPipelineTag = false;
		}else if (qName.equals("PRName")) {
			this.paramPRTag = false;
		}else if (qName.equals("isOptional")) {
			this.paramIsOptional = false;
		}
	}

	/** Gets called on the following structure:
	 * <tag>characters</tag> */
	@Override
	public void characters(char ch[], int start, int length) {
		if(this.serviceNameTag){
			parsedServiceObject = new ServiceInfoForClient();
			parsedServiceObject.setServiceName(new String(ch, start, length));
		}else if(this.serviceDescTag){
			parsedServiceObject.setServiceDescription(new String(ch, start, length));
		}else if(this.paramTag){
		}else if(this.paramNameTag){
			parsedParamObject = new GateRuntimeParameter();
			parsedParamObject.setParamName(new String(ch, start, length));
		}else if(this.paramTypeTag){
			parsedParamObject.setType(new String(ch, start, length));
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
