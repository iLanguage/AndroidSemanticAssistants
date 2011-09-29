package info.semanticsoftware.semassist.client.eclipse.model;

import info.semanticsoftware.semassist.csal.result.Annotation;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class defines the structure of Semantic Annotations produced from parsing the serves's XML response.
 * See the Semantic Assistants documentation.
 *  
 * @author Bahar Sateli
 * */
public class AnnotationInstance {

   /** Monotonically increasing count used for Annotation IDs. */
   private static int instanceCounter = 0;
 
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

   /** Flag indicating if this annotation presents the user with choices. */
   private boolean interactive;


	/** Constructor. Initializing private variables. */
	private AnnotationInstance(final String content, final String type, final String start, final String end) {
		super();
		this.id = Integer.toString(instanceCounter++); // set unique key for next instance.
		this.content = content;
		this.type = type;
		this.start = start;
		this.end = end;
	}

   /** Constructor. Transformation from Annotation types. */
   public AnnotationInstance(final Annotation annot, final boolean interactive) {
      this(annot.mContent, annot.mType, String.valueOf(annot.mStart), String.valueOf(annot.mEnd));
		final Set<String> featureNames = annot.mFeatures.keySet();
		            	            	
      for (final String name : featureNames) {
		   final String value = annot.mFeatures.get(name);
		   this.addFeatureMap(name, value);
		}
      this.interactive = interactive;
   }

   //TODO: The existence of this method indicates that this class
   // should be merged with that of CSAL's Annotation!!
   public final Annotation toAnnotation() {
      final Annotation annot = new Annotation();
      annot.mType = this.getType();
      annot.mContent = this.getContent();
      annot.mFeatures = new HashMap<String,String>(this.getFeatureMap().getFeaturesMap());
      annot.mStart = Long.valueOf(this.getStart());
      annot.mEnd = Long.valueOf(this.getEnd());  
      return annot;
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

   /** 
    * @return The interactive status of the annotation.
    */
   public boolean isInteractive() {
      return interactive;
   }
}	
