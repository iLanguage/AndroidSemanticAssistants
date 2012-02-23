package info.semanticsoftware.semassist.android.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import info.semanticsoftware.semassist.csal.result.Annotation;
import info.semanticsoftware.semassist.csal.result.AnnotationVector;
import info.semanticsoftware.semassist.csal.result.SemanticServiceResult;

public class ServerResponseHandler {
	static int annID = 0;
	
	public static List<AnnotationInstance> createAnnotation(SemanticServiceResult current){
		List<AnnotationInstance> results = new ArrayList<AnnotationInstance>();
		/** List of annotations that maps document IDs to annotation instances */
		HashMap<String, AnnotationVector> annotationsVector = current.mAnnotations;
		Set<String> keys = annotationsVector.keySet();

		for( Iterator<String> it2 = keys.iterator(); it2.hasNext(); ){
			String docID = it2.next();
	            AnnotationVector annotsVector = annotationsVector.get(docID);
	            Vector<Annotation> annots = annotsVector.mAnnotationVector;
	            for(int i=0; i < annots.size(); i++){
	            	AnnotationInstance annotation = new AnnotationInstance(Integer.toString(annID), annots.get(i).mContent, annotsVector.mType, String.valueOf(annots.get(i).mStart), String.valueOf(annots.get(i).mEnd));
	            	Set<String> featureNames = annots.get(i).mFeatures.keySet();
	            	            	
	            	for(Iterator<String> it3 = featureNames.iterator(); it3.hasNext();){
	            		String name = it3.next();
	            		String value = annots.get(i).mFeatures.get(name);
	            		if(value.equals("")){
	            			continue;
	            		}else{
	            			annotation.addFeatureMap(name, value);
	            		}
	            	}
	            	results.add(annotation);
	            	annID++;
	            }
	            
	            /*if(annotsVector.mType.equalsIgnoreCase("person")){
	            	Vector<Annotation> annots = annotsVector.mAnnotationVector;
		            for(int i=0; i < annots.size(); i++){
		            		persons.getItem().add(annots.get(i).mContent);
	            	}	
	            }*/
	             
		}
		
		return results;
	}
}
