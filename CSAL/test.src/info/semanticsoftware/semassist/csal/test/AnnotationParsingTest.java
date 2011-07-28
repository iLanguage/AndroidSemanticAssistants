package info.semanticsoftware.semassist.csal.test;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import org.junit.Test;

import info.semanticsoftware.semassist.csal.*;
import info.semanticsoftware.semassist.csal.result.Annotation;
import info.semanticsoftware.semassist.csal.result.AnnotationVector;
import info.semanticsoftware.semassist.csal.result.SemanticServiceResult;


public class AnnotationParsingTest {

	String featurlessAnnotation = "<?xml version=\"1.0\"?><saResponse><annotation type=\"Sentence\" annotationSet=\"Annotation\"><document url=\"\"><annotationInstance content=\"This is it.\" start=\"0\" end=\"11\"></annotationInstance></document></annotation></saResponse>";

	@Test
	public void testGetAnnotationsForOneDocument() {
		try{
			Vector<SemanticServiceResult> results = ClientUtils.getServiceResults(featurlessAnnotation);
			Iterator<SemanticServiceResult> it = results.iterator();
			while(it.hasNext()){
				SemanticServiceResult ssr = it.next();
				if (ssr.mAnnotations.size() == 0){
					fail("Failed to initialize annotation instances.");
				}else{
					HashMap<String, AnnotationVector> annotationsVector = ssr.mAnnotations;
					Set<String> keys = annotationsVector.keySet();
		
					for( Iterator<String> it2 = keys.iterator(); it2.hasNext(); ){
				            String docID = it2.next();
				            AnnotationVector annotsVector = annotationsVector.get(docID);
				            Vector<Annotation> annots = annotsVector.mAnnotationVector;
				            for(int i=0; i < annots.size(); i++){
				            	if(annots.get(i).mFeatures.size() != 0){
				        			fail("Faux feature created from the result annotation.");
				            	}
				            }
				     }
				}
			}
		}catch(Exception e){
			fail("Failed to parse annotation from XML.");
		}
		
	}

}
