package info.semanticsoftware.semassist.csal.test;

import static org.junit.Assert.*;

import java.util.*;
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

   /**
    * This test ensures all fields of the resulting data model memory
    * structure have been initialized properly.
    */
   @Test
   public void testDatamodelFieldIntegrity() {
      final String DOC_ID = "";
      final String ANNOT_TYPE = "Person";
      final String ANNOT_CONTENT = "Steven Harper";
      final long ANNOT_START = 13;
      final long ANNOT_END = ANNOT_START + ANNOT_CONTENT.length();
      final String[] ANNOT_FEATURE = {"gender"};
      final String[] ANNOT_VALUE = {"male"};

      final String response = 
         "<?xml version=\"1.0\"?>"+
         "<saResponse>"+
            "<annotation type=\""+ ANNOT_TYPE +"\" annotationSet=\"Annotation\">"+
               "<document url=\""+ DOC_ID +"\">"+
                  "<annotationInstance content=\""+ ANNOT_CONTENT +"\" start=\""+ ANNOT_START +"\" end=\""+ ANNOT_END +"\">"+
                     "<feature name=\""+ ANNOT_FEATURE[0] +"\" value=\""+ ANNOT_VALUE[0] +"\"/>"+
                  "</annotationInstance>"+
               "</document>"+
            "</annotation>"+
         "</saResponse>";

      try {
         // Invoke CSAl with the raw server XML response & analyze the
         // resulting memory structure.
         final Vector<SemanticServiceResult> results = ClientUtils.getServiceResults(response);
         for (final SemanticServiceResult res : results) {

            assertNotNull("Empty annotVtr map", res.mAnnotations);               
            assertEquals("Wrong # of annotVtr mappings", 1, res.mAnnotations.size());
            for (final String docID : res.mAnnotations.keySet()) {
				   final AnnotationVector annotVtr = res.mAnnotations.get(docID);

               // Annotations bundled by the same type
               assertNotNull("Empty annotVtr["+ docID +"].mType", annotVtr.mType);
               assertEquals("Wrong annotVtr["+ docID +"].mType", ANNOT_TYPE, annotVtr.mType);

               // What does this offset represent? why is it needed?
               //assertEquals("Wrong annotVtr["+ docID +"].mStart", -1, annotVtr.mStart);

               // Annotation bundle
               assertNotNull("Empty annotVtr["+ docID +"].mAnnotationVector", annotVtr.mAnnotationVector);               
               assertEquals("Wrong # of ann(s)", 1, annotVtr.mAnnotationVector.size());
               for (final Annotation ann : annotVtr.mAnnotationVector) {

                  // Annotation Type
                  assertNotNull("Empty ann.mType", ann.mType);
                  assertEquals("Wrong ann.mType", ANNOT_TYPE, ann.mType);

                  // Annotation Content
                  assertNotNull("Empty ann.mContent", ann.mContent);
                  assertEquals("Wrong ann.mContent", ANNOT_CONTENT, ann.mContent);

                  // Annotation Features
                  assertNotNull("Empty ann.features", ann.mFeatures);               
                  assertEquals("Wrong # of feature(s)", 1, ann.mFeatures.size());
                  for (int i = 0; i < Math.min(ANNOT_FEATURE.length, ANNOT_VALUE.length); ++i) {
                     assertTrue("Missing ann.feature", ann.mFeatures.containsKey(ANNOT_FEATURE[i]));
                     assertEquals("Wrong feature.value", ANNOT_VALUE[i], ann.mFeatures.get(ANNOT_FEATURE[i]));
                  }

                  // Annotation Offset
                  assertEquals("Wrong ann.mStart", ANNOT_START, ann.mStart);
                  assertEquals("Wrong ann.mEnd", ANNOT_END, ann.mEnd);
               }
            }
         }
      } catch (final Exception ex) {
			fail("Failed to parse annotation from XML.");
      }
   }
}
