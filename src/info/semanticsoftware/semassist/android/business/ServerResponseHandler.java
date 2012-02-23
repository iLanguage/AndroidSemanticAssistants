package info.semanticsoftware.semassist.android.business;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import net.java.dev.jaxb.array.StringArray;

import info.semanticsoftware.semassist.csal.result.Annotation;
import info.semanticsoftware.semassist.csal.result.AnnotationVector;
import info.semanticsoftware.semassist.csal.result.SemanticServiceResult;

public class ServerResponseHandler {
	public static StringArray createAnnotation(SemanticServiceResult current){
		StringArray persons = new StringArray();
		/** List of annotations that maps document IDs to annotation instances */
		HashMap<String, AnnotationVector> annotationsVector = current.mAnnotations;
		Set<String> keys = annotationsVector.keySet();

		for( Iterator<String> it2 = keys.iterator(); it2.hasNext(); ){
			String docID = it2.next();
	            AnnotationVector annotsVector = annotationsVector.get(docID);
	            if(annotsVector.mType.equalsIgnoreCase("person")){
	            	Vector<Annotation> annots = annotsVector.mAnnotationVector;
		            for(int i=0; i < annots.size(); i++){
		            		persons.getItem().add(annots.get(i).mContent);
	            	}	
	            }
	     }
		
		return persons;
	}
}
