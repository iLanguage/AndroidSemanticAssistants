package info.semanticsoftware.semassist.android.restlet;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RequestRepresentation {
	
	private String serviceName;
	private String input;
	private Map<String, String> params = new HashMap<String, String>();
	
	public RequestRepresentation(String iServiceName, Map<String,String> iParams, String input){
		this.serviceName = iServiceName;
		this.params = iParams;
		this.input = input;
	}
	
	public String getXML(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		buffer.append("<invocation>");
		buffer.append("<serviceName>").append(serviceName).append("</serviceName>");	
		if(params !=null){
			Set<String> paramNames = params.keySet();
			for(String name:paramNames){
				buffer.append("<param>");
					buffer.append("<name>").append(name).append("</name>");
					buffer.append("<value>");
						params.get(name);
					buffer.append("</value>");
				buffer.append("</param>");
			}
		}
		buffer.append("<input><![CDATA[");
			buffer.append(input);
		buffer.append("]]></input>");
        buffer.append("</invocation>");
        return buffer.toString();
	}

}
