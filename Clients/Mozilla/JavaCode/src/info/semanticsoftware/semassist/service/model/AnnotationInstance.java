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

package info.semanticsoftware.semassist.service.model;

import java.util.Iterator;
import java.util.Map;

/**
 * This class defines the structure of Semantic Annotations produced from parsing the serves's XML response.
 * See the Semantic Assistants documentation.
 *  
 * @author Bahar Sateli
 * */
public class AnnotationInstance {
	
	/** Annotation ID */
	private String id;
	
	/** Content of the annotation */
	private String content;

	/** Type of the annotation */
	private String type;
	
	/** Starting point of the annotation in the file */
	private String start;
	
	/** End offset of the annotation in the file */
	private String end;
	
	/** This string is the concatenation of all the features is featuresMap */
	private String features ="";
	
	/** This variable stores all the features associated with each annotation in form of (key,value) pairs in a Java map. */
	private FeatureMap featuresMap = new FeatureMap();

	/** Constructor. Initializing private variables. */
	public AnnotationInstance(String identifier, String content, String type, String start, String end) {
		super();
		this.id = identifier;
		this.content = content;
		this.type = type;
		this.start = start;
		this.end = end;
	}

	/** Getter method for annotation's identifier 
	 * @return id Identifier of the annotation 
	 * */
	public String getID(){
		return id;
	}
	
	/** Getter method for annotation's content 
	 * @return content The content of annotation 
	 * */
	public String getContent(){
		return content;
	}
	
	/** Getter method for annotation's type 
	 * @return type The type of annotation 
	 * */
	public String getType() {
		return type;
	}
	
	/** Getter method for annotation's start point 
	 * @return start The starting point of annotation in the file 
	 * */
	public String getStart(){
		return start;
	}
	
	/** Getter method for annotation's end point 
	 * @return start The ending offset of annotation in the file 
	 * */
	public String getEnd(){
		return end;
	}
	
	/** Getter method for annotation's features 
	 * @return features A string containing the concatenation of all the features in featuresMap 
	 * */
	public String getFeatures(){
		features ="";
		 Iterator<Map.Entry<String, String>> it = featuresMap.getFeaturesMap().entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry<String,String> pairs = it.next();
		        features = features + (pairs.getKey() + "=" + pairs.getValue() + " | ");
		    }
		return features;
	}
	
	/** Getter method for annotation feature map 
	 * @return featuresMap the map containing the annotation features 
	 * */
	public FeatureMap getFeatureMap(){
		return featuresMap;
	}
	
	
	/** Setter method for annotation identifier 
	 * @param input annotation identifier */
	public void setID(String input){
		this.id = input;
	}
	
	/** Setter method for annotation content */
	public void setContent(String content){
		this.content = content;
	}
	
	/** Setter method for annotation type */
	public void setType(String type) {
		this.type = type;
	}
	
	/** Setter method for annotation start point in the file */
	public void setStart(String start){
		this.start =start;
	}
	
	/** Setter method for annotation end offset in the file */
	public void setEnd(String end){
		this.end = end;
	}
	
	/** This method puts the input arguments as the key and value (representing a feature) into the features map 
	 * @param key feature's name
	 * @param valye features' value
	 * */
	public void addFeatureMap(String key, String value){
		featuresMap.put(key, value);
	}

}	
