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
 * 	This Class is the local representation of the gateRuntimeParameter complex type
 *	Required in the WSDL. It implement KvmSerializable (a ksoap2 serialization interface)
 *	It has the same parameters of the complex type's
 */

package NLPAndroid.domainLogic.WSMatchingClasses;

import java.util.Hashtable;
import java.util.List;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import java.lang.reflect.Field;

public class gateRuntimeParameter implements KvmSerializable {
	
	public String defaultValueString;//For example: defaultValueString=10;
	public String label;//For example:  label=Number of search results;
	public boolean optional;//For example: optional=true; 
	public String PRName;//For example: PRName=Yahoo PR; 
	public String paramName;//For example: paramName=limit; 
	public String pipelineName;//For example: pipelineName=Yahoo Search;
	public String type;//For example: type=int; 
	public Integer intValue;//For example:  how many results we want for yahoo PR

	
    public gateRuntimeParameter(){}

    
    //--------------------------------------------------------------------------
	//		Function for Modifying class attributes Using reflection!
    //--------------------------------------------------------------------------
	public void setAttr(Object tag,String value) throws IllegalArgumentException, IllegalAccessException//to dynamically set parameters values
, SecurityException, NoSuchFieldException
	{
		Field dynamicSet = gateRuntimeParameter.class.getField((String)tag);
	   
	    if (boolean.class.equals(dynamicSet.getType())) 
	    {
	    	dynamicSet.set(this,  Boolean.valueOf(value)); 
    	}
    	else if (Integer.class.equals(dynamicSet.getType())) 
    	{
    		dynamicSet.set(this,  Integer.valueOf(value));
    	}
    	else if(List.class.equals(dynamicSet.getType()))
	    {
    		dynamicSet.set(this,  List.class.cast(value));	    	
	    }
    	else if(Double.class.equals(dynamicSet.getType()))
	    {
    		dynamicSet.set(this,  Double.valueOf(value));
	    }
    	else if(String.class.equals(dynamicSet.getType()))
	    {
    		dynamicSet.set(this,  String.valueOf(value));
	    }
	   
	}

	
	@Override
	public Object getProperty(int arg0) {
		
		switch (arg0){

		case 0:
			return defaultValueString;
		case 1:
			return intValue;
		case 2:
			return label;
		case 3:
			return optional;
		case 4:
			return PRName;
		case 5:
			return paramName;
		case 6:
			return pipelineName;
		case 7:
			return type;
		default:
		    return null;
		    
		}
	}

	@Override
	public int getPropertyCount() {
		return 8;//8 parameters
	}

	@Override
	public void getPropertyInfo(int arg0, Hashtable arg1, PropertyInfo arg2) {
		switch(arg0)
        {
	        case 0:
	        	arg2.type = PropertyInfo.STRING_CLASS;
	        	arg2.name = "defaultValueString";
	            break;
	        case 1:
	        	arg2.type = PropertyInfo.INTEGER_CLASS;
	        	arg2.name = "intValue";
	            break;
	        case 2:
	        	arg2.type = PropertyInfo.STRING_CLASS;
	        	arg2.name = "label";
	            break;
	        case 3:
	        	arg2.type = PropertyInfo.BOOLEAN_CLASS;
	        	arg2.name = "optional";
	            break;
	        case 4:
	        	arg2.type = PropertyInfo.STRING_CLASS;
	        	arg2.name = "PRName";
	            break;
	        case 5:
	        	arg2.type = PropertyInfo.STRING_CLASS;
	        	arg2.name = "paramName";
	            break;
	        case 6:
	        	arg2.type = PropertyInfo.STRING_CLASS;
	        	arg2.name = "pipelineName";
	            break;
	        case 7:
	        	arg2.type = PropertyInfo.STRING_CLASS;
	        	arg2.name = "type";
	            break;
	        default:break;
        }
	}

	@Override
	public void setProperty(int arg0, Object arg1) {
		
		switch(arg0)
        {
        case 0:
        	defaultValueString =  (String)arg1;
        	
            break;
        case 1:
        	intValue =  (Integer)arg1;
            break;
        case 2:
        	label =  (String)arg1;
            break;
        case 3:
        	optional =  (Boolean)arg1;
            break;
        case 4:
        	PRName =  (String)arg1;
            break;
        case 5:
        	paramName =  (String)arg1;
            break;
        case 6:
        	pipelineName =  (String)arg1;
            break;
        case 7:
        	type =  (String)arg1;
            break;
        default:
            break;
        }
	}
}

