package info.semanticsoftware.semassist.client.eclipse.handlers;

import info.semanticsoftware.semassist.client.eclipse.model.AnnotationInstance;
import info.semanticsoftware.semassist.client.eclipse.model.SemanticAssistantsStatusViewModel;
import info.semanticsoftware.semassist.csal.ClientUtils;
import info.semanticsoftware.semassist.csal.result.Annotation;
import info.semanticsoftware.semassist.csal.result.AnnotationVector;
import info.semanticsoftware.semassist.csal.result.AnnotationVectorArray;
import info.semanticsoftware.semassist.csal.result.SemanticServiceResult;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * This class handles the various types of responses received from the server and reacts in a proper manner.
 * @author Bahar Sateli
 *
 */
public class ServerResponseHandler {
		static int annID = 0;
		public static void createAnnotation(SemanticServiceResult current){
			
			int counter = 0;
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
		            		annotation.addFeatureMap(name, value);
		            	}
		
		            	EvaluationSession.getResources().get(counter).getAnnotations().add(annotation);
		            	annID++;
		            }
		            counter++;
		     }
		}
		
		public static void createFile(String fileContent, String fileExt){
			String outputFilePath = "";
        	File outputFile = ClientUtils.writeStringToFile(fileContent, fileExt);
        	try {
        		outputFilePath = "file://" + outputFile.getCanonicalPath();
				PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(new URL(outputFilePath));
				SemanticAssistantsStatusViewModel.addLog("The results are retrieved from the server. Please check your browser to see the output.");
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
		
		public static void createDocument(String result){
			
			final File outputFile = ClientUtils.writeStringToFile(result,".txt");
			 // Open the output document in a new editor
	        Display.getDefault().asyncExec(new Runnable() {
							@Override
							public void run() {
								FileSelectionHandler.openEditor(outputFile);
							}
						}); 
		}
		
	   public static String getAnnotationsString(SemanticServiceResult current){
	       
		   HashMap<String, AnnotationVectorArray> annotationsPerDocument = new HashMap<String, AnnotationVectorArray>();

           // Keys are document IDs or URLs
           HashMap<String, AnnotationVector> map = current.mAnnotations;
           Set<String> keys = map.keySet();

           for( Iterator<String> it2 = keys.iterator(); it2.hasNext(); )
           {
               String docID = it2.next();

               if( annotationsPerDocument.get( docID ) == null )
               {
                   annotationsPerDocument.put( docID, new AnnotationVectorArray() );
               }

               AnnotationVectorArray v = annotationsPerDocument.get( docID );
               v.mAnnotVectorArray.add( map.get( docID ) );
           }

           // Assemble annotations string
           if( annotationsPerDocument.size() > 0 )
           {
               return getAnnotationsString(annotationsPerDocument);
           }
           
           return "Could not read the annotation content!";
	       
	     }
	   
	   static String getAnnotationsString( HashMap<String, AnnotationVectorArray> map )
	    {
	        if( map == null )
	        {
	            return "";
	        }

	        StringBuffer sb = new StringBuffer();

	        // The key is annotation document ID (URL or number), the values are
	        // annotation instances, basically
	        Set<String> keys = map.keySet();


	        for( Iterator<String> it = keys.iterator(); it.hasNext(); )
	        {
	            String docID = it.next();
	            sb.append( "Annotations for document " + docID + ":\n\n" );
	            AnnotationVectorArray va = map.get( docID );
	            sb.append( getAnnotationsString( va ) );
	        }


	        return sb.toString();
	    }
	   
	   static String getAnnotationsString( AnnotationVectorArray annotVectorArr )
	    {

	        StringBuffer strBuffer = new StringBuffer();


	        if( annotVectorArr == null )
	        {
	            return "";
	        }


	        for( Iterator<AnnotationVector> it = annotVectorArr.mAnnotVectorArray.iterator(); it.hasNext(); )
	        {
	            AnnotationVector annotVector = it.next();

	            strBuffer.append( "Type: " + annotVector.mType + "\n" );

	            System.out.println( "Type: " + annotVector.mType + "\n" );

	            strBuffer.append( listAnnotations( annotVector ) );

	        }

	        // sort annotations by start
	        ClientUtils.SortAnnotations( annotVectorArr );
	        //

	        for ( Iterator<Annotation> it2 = ClientUtils.mAnnotArray.iterator(); it2.hasNext(); )
	        {
	            // Create Side Notes
	        }

	        return strBuffer.toString();

	    }

	    static String listAnnotations( AnnotationVector as )
	    {
	        if( as == null )
	        {
	            return "";
	        }

	        StringBuffer sb = new StringBuffer();


	        for( Iterator<Annotation> it = as.mAnnotationVector.iterator(); it.hasNext(); )
	        {
	            Annotation annotation = it.next();

	            if( annotation.mContent != null && !annotation.mContent.equals( "" ) )
	            {
	                sb.append( "Start: " + annotation.mStart + ", end: " + annotation.mEnd + ", content: " + annotation.mContent + "\n" );
	            }

	            if( annotation.mFeatures == null || annotation.mFeatures.size() == 0 )
	            {
	                sb.append( "\n" );
	                continue;
	            }

	            if( annotation.mFeatures.size() > 1 )
	            {
	                sb.append( "Features:\n" );
	            }

	            Set<String> keys = annotation.mFeatures.keySet();


	            for( Iterator<String> it2 = keys.iterator(); it2.hasNext(); )
	            {
	                String currentKey = it2.next();
	                sb.append( currentKey + ": " + annotation.mFeatures.get( currentKey ) + "\n" );
	            }

	            sb.append( "\n" );
	        }

	        return sb.toString();
	    }

}
