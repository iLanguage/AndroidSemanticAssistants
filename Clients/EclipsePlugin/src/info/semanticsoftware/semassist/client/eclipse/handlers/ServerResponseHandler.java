package info.semanticsoftware.semassist.client.eclipse.handlers;

import info.semanticsoftware.semassist.client.eclipse.model.AnnotationInstance;
import info.semanticsoftware.semassist.client.eclipse.model.SemanticAssistantsStatusViewModel;
import info.semanticsoftware.semassist.csal.ClientUtils;
import info.semanticsoftware.semassist.csal.result.Annotation;
import info.semanticsoftware.semassist.csal.result.AnnotationVector;
import info.semanticsoftware.semassist.csal.result.SemanticServiceResult;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

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
		
		public static void createFile(String fileContent, String fileExt){
			String outputFilePath = "";
        	File outputFile = ClientUtils.writeStringToFile(fileContent, fileExt);
        	try {
        		outputFilePath = "file://" + outputFile.getCanonicalPath();
				PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(new URL(outputFilePath));
        	}catch (MalformedURLException e) {
				System.err.println("A malfored URL has been retrieved from the server. Semantic Assistants cannot open the results.");
				SemanticAssistantsStatusViewModel.addLog("A malfored URL has been retrieved from the server. Semantic Assistants cannot open the results.");
				e.printStackTrace();
        	} catch (PartInitException e2) {
				System.err.println("No browser has been configured. Semantic Assistants cannot find the browser to open the results.");
				SemanticAssistantsStatusViewModel.addLog("No browser has been configured. Semantic Assistants cannot find the browser to open the results.");
				e2.printStackTrace();
			}  catch (IOException e1) {
				System.err.println("Semantic Assistants cannot create the output file.");
				SemanticAssistantsStatusViewModel.addLog("Semantic Assistants cannot create the output file.");
				e1.printStackTrace();
			}
		}
}
