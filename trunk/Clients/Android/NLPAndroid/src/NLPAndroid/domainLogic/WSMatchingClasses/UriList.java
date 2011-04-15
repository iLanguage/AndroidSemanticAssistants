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
 * 	This Class is the local representation of the UriList complex type
 *	Required in the WSDL. It implement KvmSerializable (a ksoap2 serialization interface)
 *	It has the same parameters of the complex type's
 */
package NLPAndroid.domainLogic.WSMatchingClasses;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class UriList implements KvmSerializable {

		public String uriList; 
		public List<String> listOfuriList ;
		
		public UriList()
		{
			listOfuriList = new ArrayList<String>();
		}
		@Override
		public Object getProperty(int arg0) {
			for(int i=0;i<listOfuriList.size();i++)
			{
				if (arg0 == i)
					return listOfuriList.get(i);
			}

			    return null;
		}

		@Override
		public int getPropertyCount() {
			return listOfuriList.size();
		}

		@Override
		public void getPropertyInfo(int arg0, Hashtable arg1, PropertyInfo arg2) {
			for(int i=0;i<listOfuriList.size();i++)
			{
				if(arg0 == i)
				{
					arg2.type = PropertyInfo.STRING_CLASS;
		        	
		        	arg2.name = "uriList";
				}
			}
			
		}

		@Override
		public void setProperty(int arg0, Object arg1) {

			String var =null;

			for(int i=0;i<listOfuriList.size();i++)
			{
				if(arg0 == i)
				{
					var = listOfuriList.get(i);
		        	var = (String)arg1;
				}
			}

		}
	}
