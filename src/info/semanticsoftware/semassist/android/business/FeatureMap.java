package info.semanticsoftware.semassist.android.business;

import java.util.HashMap;
import java.util.Map;

/** 
 * This class define a structure to store annotation features.
 * 
 * @author Bahar Sateli
 * */
public class FeatureMap {

	/** This map stores the annotation features in form of (key.value) pairs */ 
	Map<String,String> featuresMap = new HashMap<String,String>();

	public FeatureMap(){
	}

	/** This method puts the input arguments into the map */
	public void put(String key, String value){
		featuresMap.put(key, value);
	}

	/** This method returns the map containing the annotation features
	 * 
	 * @return featuresMap the map containing the annotation features
	 * */
	public Map<String,String> getFeaturesMap(){
		return featuresMap;
	}
}
