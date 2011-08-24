/*
    Semantic Assistants -- http://www.semanticsoftware.info/semantic-assistants

    This file is part of the Semantic Assistants architecture.

    Copyright (C) 2011 Semantic Software Lab, http://www.semanticsoftware.info

    The Semantic Assistants CSAL is free software: you can
    redistribute and/or modify it under the terms of the GNU Lesser General
    Public License as published by the Free Software Foundation, either
    version 3 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package info.semanticsoftware.semassist.csal;

import java.util.HashMap;
import java.util.Map;

public class XMLElementModel {
	
	private String name; // XML tag name.
	private Map<String, String> attributes = new HashMap<String, String>(); // XML tag attribute/value map.
	
	public void setName(String _name){
		this.name = _name;
	}
	
	public String getName(){
		return this.name;
	}
	
	
	public void setAttribute(String _key, String _value){
		this.attributes.put(_key, _value);
	}
	
	public Map<String, String> getAttribute(){
		return this.attributes;
	}
}