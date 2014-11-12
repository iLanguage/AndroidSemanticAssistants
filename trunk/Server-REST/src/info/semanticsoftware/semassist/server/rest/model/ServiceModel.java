/*
Semantic Assistants -- http://www.semanticsoftware.info/semantic-assistants

This file is part of the Semantic Assistants architecture.

Copyright (C) 2014 Semantic Software Lab, http://www.semanticsoftware.info
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

import com.google.gson.Gson;

import info.semanticsoftware.semassist.server.SemanticServiceBroker;
import info.semanticsoftware.semassist.server.ServiceInfoForClient;
import info.semanticsoftware.semassist.server.ServiceInfoForClientArray;
import info.semanticsoftware.semassist.server.rest.business.ServiceAgentSingleton;

/** Represents an NLP pipeline.
 * @author Bahar Sateli */
public class ServiceModel {

	/** Services iterator object.*/
	private Iterator<ServiceInfoForClient> iterator = null;

	/** Class constructor that creates a broker agent to the Semantic Assistants server.*/
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

	/** Returns the XML representation of all the available services.
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
	
	public String getAllJSON(){
		Gson gson = new Gson();
		StringBuffer buffer = new StringBuffer();
		while (iterator.hasNext()) {
			buffer.append(gson.toJson(iterator.next()));
			buffer.append(",");
		}
		
		String output = buffer.toString();
		String representation = output.substring(0, output.lastIndexOf(","));
		//String jsonp = callback + "([" + representation + "]);";
		return representation;
	}

	/**
	 * Returns XML representation of the input service.
	 * @param service NLP service object retrieved from SA server
	 * @return XML representation of the specified service
	 */
	public String getXML(final ServiceInfoForClient service) {
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
	 * Returns XML representation of the input service.
	 * @param serviceName name of the NLP service
	 * @return XML representation of the specified service
	 */
	public String getXML(final String serviceName) {
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
	
	public String getJSON(final String serviceName) {
		ServiceInfoForClient serviceObject = null;
		String json = null;

		while(iterator.hasNext()){
			ServiceInfoForClient service = iterator.next();
			System.out.println(service.getServiceName());
			if(service.getServiceName().equals(serviceName)){
				serviceObject = service;
				break;
			}
		}

		if(serviceObject != null){
			Gson gson = new Gson();
			json = gson.toJson(serviceObject);
		}else{
			System.err.println("Service not found");
		}

		return json;
	}

	/**
	 * Returns partial link URI of the specified service.
	 * @param service NLP service object
	 * @return partial URI for the service
	 */
	private String getLink(final ServiceInfoForClient service){
		return "/services/" + replace(service.getServiceName());
	}

	/**
	 * Replaces space characters with underscore.
	 * @param service service name string
	 * @return replaced service name
	 */
	private String replace(String input){
		return input.replaceAll(" ", "_");
	}

}
