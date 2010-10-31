/*
    Semantic Assistants -- http://www.semanticsoftware.info/semantic-assistants

    This file is part of the Semantic Assistants architecture.

    Copyright (C) 2009, 2010 Semantic Software Lab, http://www.semanticsoftware.info
        Nikolaos Papadakis
        Tom Gitzinger

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

package info.semanticsoftware.semassist.server.util;

import java.util.*;
//import serverside.ipd.semassist.util.GATERuntimeParameter;




/**
 * Read-only version of ServiceInfo. Holding less
 * information also.
 */
public class ServiceInfoForClient
{

     public String serviceName = "";
     public String serviceDescription = "";
     public Vector<GATERuntimeParameter> params = new Vector<GATERuntimeParameter>();
     
     

     public ServiceInfoForClient() 
	  {
	       
	  }

     public ServiceInfoForClient(String name, String desc) 
	  {
	       serviceName = name;
	       serviceDescription = desc;
	  }

     public ServiceInfoForClient(ServiceInfo other) 
	  {
	       serviceName        = other.getServiceName();
	       serviceDescription = other.getServiceDescription();
	       params             = other.mParams;
	  }
     

     public boolean hasMandatoryRuntimeParams() 
	  {       
	       Vector<GATERuntimeParameter> p = this.params;
	       Iterator<GATERuntimeParameter> it = p.iterator();
	       while (it.hasNext()) {
		    GATERuntimeParameter param = it.next();
		    if (!param.getOptional()) return true;
	       }	       
	       return false;
	  }
     

     public String getServiceName() 
	  {
	       return serviceName;
	  }


     public String getServiceDescription() 
	  {
	       return serviceDescription;
	  }
     
     
     
     
}
