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

package info.semanticsoftware.semassist.server.rest.model;

import java.util.List;
import info.semanticsoftware.semassist.server.GateRuntimeParameter;
import info.semanticsoftware.semassist.server.ServiceInfoForClient;

public class RTParamModel {

	/** Returns the XML representation of all RTParams of a specific services. 
	 * @param service the NLP service instance
	 * @return XML representation of all of the runtime parameters
	 */
	public String getAllXML(final ServiceInfoForClient service){
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
	 * Returns XML representation of the input service.
	 * @param service NLP service object retrieved from SA server
	 * @return XML representation of the specified service
	 */
	public String getXML(final GateRuntimeParameter param) {
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
