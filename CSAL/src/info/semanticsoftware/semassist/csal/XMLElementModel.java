package info.semanticsoftware.semassist.csal;

import java.util.HashMap;
import java.util.Map;

public class XMLElementModel {
	
	private String name;
	private Map<String, String> attributes = new HashMap<String, String>();
	
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
