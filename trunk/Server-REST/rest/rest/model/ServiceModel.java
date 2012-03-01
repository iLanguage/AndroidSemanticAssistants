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

import java.util.Iterator;

import info.semanticsoftware.semassist.server.SemanticServiceBroker;
import info.semanticsoftware.semassist.server.ServiceInfoForClient;
import info.semanticsoftware.semassist.server.ServiceInfoForClientArray;
import info.semanticsoftware.semassist.server.rest.business.ServiceAgentSingleton;

public class ServiceModel {
	
	Iterator<ServiceInfoForClient> iterator = null;
    
	public ServiceModel(){
			try{
				SemanticServiceBroker broker = ServiceAgentSingleton.getInstance();
				ServiceInfoForClientArray services = broker.getAvailableServices();
		        iterator = services.getItem().iterator();
			}catch(Exception e){
				System.err.println("Can not read list of available services.");
				e.printStackTrace();
			}
	}
	
	/** Returns the XML representation of all the available services 
	 * @return XML representation of services
	 */
	public String getAllXML(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("<services>");
			while(iterator.hasNext()){
	        	buffer.append(getXML(iterator.next()));
	        }
        buffer.append("</services>");
        return buffer.toString();
	}
	
	/**
	 * Returns XML representation of the input service
	 * @param service NLP service object retrieved from SA server
	 * @return XML representation of the specified service
	 */
	public String getXML(ServiceInfoForClient service) {
		StringBuilder xml = new StringBuilder();
		xml.append("<service>");
		xml.append("<serviceName>").append(service.getServiceName()).append("</serviceName>");
		xml.append("<serviceDescription>").append(service.getServiceDescription()).append("</serviceDescription>");
		xml.append("<link>").append(getLink(service)).append("</link>");
		xml.append(new RTParamModel().getAllXML(service));
		xml.append("</service>");
		return xml.toString();
	}
	
	
	/**
	 * Returns XML representation of the input service
	 * @param serviceName name of the NLP service
	 * @return XML representation of the specified service
	 */
	public String getXML(String serviceName) {
		ServiceInfoForClient serviceObject = null;
		StringBuilder xml = new StringBuilder();

		while(iterator.hasNext()){
			ServiceInfoForClient service = iterator.next();
			System.out.println(service.getServiceName());
			if(service.getServiceName().equals(serviceName)){
				serviceObject = service;
				break;
			}
		}
		
		if(serviceObject != null){
			xml.append("<service>");
			xml.append("<serviceName>").append(serviceObject.getServiceName()).append("</serviceName>");
			xml.append("<serviceDescription>").append(serviceObject.getServiceDescription()).append("</serviceDescription>");
			xml.append(new RTParamModel().getAllXML(serviceObject));
			xml.append("<link>").append(getLink(serviceObject)).append("</link>");
			xml.append("</service>");
		}else{
			System.err.println("Service not found");
		}
		
		return xml.toString();
	}
	
	/**
	 * Returns partial link URI of the specified service
	 * @param service NLP service object
	 * @return partial URI for the service
	 */
	private String getLink(ServiceInfoForClient service){
		return "/services/" + replace(service.getServiceName());
	}
	
	/**
	 * Replaces space characters with underscore
	 * @param service service name string
	 * @return replaced service name
	 */
	private String replace(String input){
		return input.replaceAll(" ", "_");
	}

}
