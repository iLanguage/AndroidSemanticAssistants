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
 * 	This Class is the local representation of the UserContext complex type
 *	Required in the WSDL. It implement KvmSerializable (a ksoap2 serialization interface)
 *	It has the same parameters of the complex type's
 */
package NLPAndroid.domainLogic.WSMatchingClasses;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class UserContext implements KvmSerializable{
	public String mUserLanguages;
    public String mDocLang;

    public UserContext(){}

	@Override
	public Object getProperty(int arg0) {
		switch (arg0){
	case 0:
		return mUserLanguages;
	case 1:
		return mDocLang;
	default:
	    return null;
		}
	}
	@Override
	public int getPropertyCount() {
		return 2;
	}
	@Override
	public void getPropertyInfo(int arg0, Hashtable arg1, PropertyInfo arg2) {
		switch(arg0)
        {
        
        case 0:
        	arg2.type = PropertyInfo.STRING_CLASS;
        	arg2.name = "mUserLanguages";
            break;
        case 1:
        	arg2.type = PropertyInfo.STRING_CLASS;
        	arg2.name = "mDocLang";
            break;
        default:break;
        }
		
	}
	@Override
	public void setProperty(int arg0, Object arg1) {
		switch(arg0)
        {
	        case 0:
	        	mUserLanguages =  (String)arg1;
	            break;
	        case 1:
	        	mDocLang =  (String)arg1;
	        	
	            break;
	        default:
	            break;
        }
		
	}
}
