/**
   Semantic Assistants - http://www.semanticsoftware.info/semantic-assistants

   This file is part of the Semantic Assistants architecture.

   Copyright (C) 2009, 2010, 2011 Semantic Software Lab, http://www.semanticsoftware.info

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
/**
 * 
 * 
 * @author Chadi Cortbaoui - Concordia University Software Engineering
 * 
 * 	This Class Is for creating Semantic Assistant Services Objects
 *
 */
package NLPAndroid.domainLogic;

import java.util.List;
import NLPAndroid.domainLogic.*;
import NLPAndroid.domainLogic.WSMatchingClasses.gateRuntimeParameter;

//--------------------
// NLP Service Object
//--------------------
public class Service implements NLPService{
	
	private String serviceName;
	private String serviceDescription;
	private List<gateRuntimeParameter> gateParams;//Gate Parameters Array(s) returned from the web service
	
	public Service(){}
	
	//Constructor
	public Service(String serviceName,String serviceDescription,List<gateRuntimeParameter> gateParams)
	{
		this.serviceName = serviceName;
		this.serviceDescription= serviceDescription;
		this.gateParams = gateParams;	
	}

	//--------------------
	// Getters/Setters
	//--------------------
	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceDescription() {
		return serviceDescription;
	}

	public void setServiceDescription(String serviceDescription) {
		this.serviceDescription = serviceDescription;
	}

	public void setGateParams(List<gateRuntimeParameter> gateParams) {
		this.gateParams = gateParams;
	}

	public List<gateRuntimeParameter> getGateParams() {
		return gateParams;
	}

	
	
}
