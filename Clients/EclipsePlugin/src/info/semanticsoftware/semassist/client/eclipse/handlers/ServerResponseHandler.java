package info.semanticsoftware.semassist.client.eclipse.handlers;

import info.semanticsoftware.semassist.client.eclipse.model.AnnotationInstance;
import info.semanticsoftware.semassist.csal.result.Annotation;
import info.semanticsoftware.semassist.csal.result.AnnotationVector;
import info.semanticsoftware.semassist.csal.result.SemanticServiceResult;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

/**
 * This class handles the various types of responses received from the server and reacts in a proper manner.
 * @author Bahar Sateli
 *
 */
public class ServerResponseHandler {
	
		public static void createAnnotation(SemanticServiceResult current){
		HashMap<String, AnnotationVector> annotationsVector = current.mAnnotations;
		Set<String> keys = annotationsVector.keySet();
		for( Iterator<String> it2 = keys.iterator(); it2.hasNext(); )
        {
            String docID = it2.next();
            
            AnnotationVector annotsVector = annotationsVector.get(docID);
            Vector<Annotation> annots = annotsVector.mAnnotationVector;
            for(int i=0; i < annots.size(); i++){
            	AnnotationInstance annotation = new AnnotationInstance("0", annots.get(i).mContent, annotsVector.mType, String.valueOf(annots.get(i).mStart), String.valueOf(annots.get(i).mEnd));
            	Set<String> featureNames = annots.get(i).mFeatures.keySet();
            	            	
            	for(Iterator<String> it3 = featureNames.iterator(); it3.hasNext();){
            		String name = it3.next();
            		String value = annots.get(i).mFeatures.get(name);
            		annotation.addFeatureMap(name, value);
            	}

            	EvaluationSession.getResources().get(Integer.parseInt(docID)).getAnnotations().add(annotation);
            }
        }
	}

}
