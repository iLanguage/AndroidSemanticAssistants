package info.semanticsoftware.semassist.server.rest.model;

import java.util.List;
import info.semanticsoftware.semassist.server.GateRuntimeParameter;
import info.semanticsoftware.semassist.server.ServiceInfoForClient;

public class RTParamModel {
	
	/** Returns the XML representation of all RTParams of a specific services 
	 * @param service the NLP service instance
	 * @return XML representation of all of the runtime parameters
	 */
	public String getAllXML(ServiceInfoForClient service){
		List<GateRuntimeParameter> paramsList = service.getParams();
		System.out.println(service.getServiceName() + " has " + paramsList.size() + " params.");
		StringBuffer buffer = new StringBuffer();
		buffer.append("<RTParams>");
			
		for(GateRuntimeParameter param: paramsList){
			buffer.append(getXML(param));	
		}
		
		buffer.append("</RTParams>");
        return buffer.toString();
	}
	
	/**
	 * Returns XML representation of the input service
	 * @param service NLP service object retrieved from SA server
	 * @return XML representation of the specified service
	 */
	public String getXML(GateRuntimeParameter param) {
		StringBuilder xml = new StringBuilder();
		xml.append("<RTParam>");
		xml.append("<paramName>").append(param.getParamName()).append("</paramName>");
		xml.append("<paramType>").append(param.getType()).append("</paramType>");
		xml.append("<pipelineName>").append(param.getPipelineName()).append("</pipelineName>");
		xml.append("<PRName>").append(param.getPRName()).append("</PRName>");
		xml.append("<defaultValue>").append(param.getDefaultValueString()).append("</defaultValue>");
		xml.append("<isOptional>").append(param.isOptional()).append("</isOptional>");
		xml.append("</RTParam>");
		return xml.toString();
	}

}
