/*
Semantic Assistants -- http://www.semanticsoftware.info/semantic-assistants

This file is part of the Semantic Assistants architecture.

Copyright (C) 2012, 2013 Semantic Software Lab, http://www.semanticsoftware.info
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
package info.semanticsoftware.semassist.client.wiki.html;

import java.util.ArrayList;
import java.util.List;

import info.semanticsoftware.semassist.csal.ClientUtils;
import info.semanticsoftware.semassist.csal.XMLElementModel;
import info.semanticsoftware.semassist.server.GateRuntimeParameter;
import info.semanticsoftware.semassist.server.ServiceInfoForClient;
import info.semanticsoftware.semassist.server.ServiceInfoForClientArray;
import info.semanticsoftware.semassist.client.wiki.servlets.BaseOntologyKeeper;
import info.semanticsoftware.semassist.client.wiki.servlets.SemAssistServlet;
import info.semanticsoftware.semassist.client.wiki.utils.Wiki;

/**
 * This class generates HTML representation of the Semantic Assistants user interface.
 * @author Bahar Sateli
 * */
public class HTMLGenerator {

	/**
	 * Transforms the array of service information objects to the HTML code that presents a combobox listing the available services.
	 * @param services The array of ServiceInfoForClient objects
	 * @return String The HTML code
	 * */
	public static String servicesCombobox(final ServiceInfoForClientArray services){

		StringBuffer result = new StringBuffer();
		result.append("<select id=\"semAssistServices\" name=\"serviceName\" size=1>");
		result.append("<option value=\"dummy\" selected=\"selected\">Select a service</option>");
		for(ServiceInfoForClient service: services.getItem()){
			result.append("<option title=\"" + service.getServiceDescription() + "\"" + " value=\"" + service.getServiceName() + "\">" + service.getServiceName() + "</option>");
			result.append(System.getProperty("line.separator"));
		}
		result.append("</select>");
		return result.toString();
	}

	/**
	 * Inquires about a selected service runtime parameters and returns HTML codes representing the runtime parameters frame in the UI.
	 * @param services array of the available services
	 * @param name name of the selected service in the UI 
	 * @return String The HTML code
	 * */
	public static String serviceRTP(final ServiceInfoForClientArray services, final String name){
		StringBuffer result = new StringBuffer();
		boolean serviceFound = false;

		for(ServiceInfoForClient service : services.getItem()){
			if(service.getServiceName().equals(name)){
				// Get all the runtime parameters needed
				List<GateRuntimeParameter> runtimeParams = service.getParams();
				if(runtimeParams.size() == 0){
					return "This service has no runtime parameter.";
				}
				for(GateRuntimeParameter param : runtimeParams){
					result.append(param.getLabel() + "&nbsp");
					result.append("<input type=\"text\" name=\"" + param.getParamName() + "\" value=\"" + param.getDefaultValueString() + "\"></input>");
					result.append("<br>");
				}
				serviceFound = true;
				break;
			}
		}

		if(serviceFound == false){
			result.append("Invalid service name. " + name);
		}
		return result.toString();
	}

	/**
	 * Reads the list of a wiki's namespaces from its ontology and returns a combobox.
	 * @return String HTML code of the comobox
	 * */
	public static String namespacesCombobox(){
		//MWOntology mw = new MWOntology();
		//List<String> namespaces = mw.getNamespaces();
		List<String> namespaces = SemAssistServlet.getWiki().getOntologyKeeper().getNamespaces();
		StringBuffer result = new StringBuffer();
		result.append("<select id=\"wikiNamespaces\" name=\"wikiNamespaces\" size=1>");
		result.append("<option value=\"dummy\" selected=\"selected\">Select a namespace</option>");
		for(int i=0; i < namespaces.size(); i++){
			result.append("<option value=\"" + namespaces.get(i) + "\">" + namespaces.get(i) + "</option>");
			result.append(System.getProperty("line.separator"));
		}
		result.append("</select>");
		return result.toString();
	}

	/**
	* Returns the list of known wikis engines, read from the ontology repository, represented as an HTML combobox.
	* @return String HTML code containing list of known wiki engines
	*/
	public static String supportedWikisCombobox(){
		List<Wiki> supportedWikis = BaseOntologyKeeper.getSupportedWikis();
		System.out.println(supportedWikis.size());
		StringBuffer result = new StringBuffer();
		result.append("<select id=\"supportedWikiEngines\" name=\"supportedWikiEngines\" size=1>");
		result.append("<option value=\"dummy\" selected=\"selected\">Select a wiki engine</option>");
		for(int i=0; i < supportedWikis.size(); i++){
			String wikiInfo = supportedWikis.get(i).getEngine() + "-" + supportedWikis.get(i).getVersion();
			result.append("<option value=\"" + wikiInfo+ "\">" + wikiInfo + "</option>");
			result.append(System.getProperty("line.separator"));
		}
		result.append("</select>");
		return result.toString();
	}

	/**
	* Returns the list of available servers, read from the Semantic Assistants' settings file represented as an HTML combobox.
	* @return String HTML code containing list of available servers
	*/
	public static String preDefinedServers(){
		ArrayList<XMLElementModel> servers = ClientUtils.getClientPreference(ClientUtils.XML_CLIENT_GLOBAL, "server");
		StringBuffer resultBuffer = new StringBuffer();
		resultBuffer.append("<select id=\"preDefinedServers\" name=\"preDefinedServers\" size=1>");
		for(int i=0; i < servers.size(); i++){

			String server = servers.get(i).getAttribute().get(ClientUtils.XML_HOST_KEY);
			server = server.concat(":").concat(servers.get(i).getAttribute().get(ClientUtils.XML_PORT_KEY));
			resultBuffer.append("<option value=\"" + server+ "\">" + server + "</option>");
			resultBuffer.append(System.getProperty("line.separator"));
		}
		resultBuffer.append("</select>");
		return resultBuffer.toString();
	}
}
