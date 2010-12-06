package info.semanticsoftware.semassist.client.eclipse.handlers;

import info.semanticsoftware.semassist.client.eclipse.model.AnnotationInstance;
import info.semanticsoftware.semassist.client.eclipse.model.SemanticAssistantsStatusViewModel;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * This class handles the various types of responses received from the server creates annotation instances.
 * @author Bahar Sateli
 *
 */
public class ServerResponseHandler {
	
	public static void annotationCase(String response){
		int annotationID = 1;
		/** XML parser */
			try{
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = factory.newDocumentBuilder();
				InputSource inStream = new InputSource();
				inStream.setCharacterStream(new StringReader(response));
				Document doc = db.parse(inStream);
				
				doc.getDocumentElement().normalize();
				
				NodeList annotationsLst = doc.getElementsByTagName("annotation");
				
				for(int i=0; i < annotationsLst.getLength(); i++){
					NodeList annotationInstanceList = null;
					System.out.println("---------------\nAnnotation Type: " + annotationsLst.item(i).getAttributes().getNamedItem("type").getNodeValue());
					String type = annotationsLst.item(i).getAttributes().getNamedItem("type").getNodeValue();
					System.out.println("Annotation Set: " + annotationsLst.item(i).getAttributes().getNamedItem("annotationSet").getNodeValue());
		
					
					if (annotationsLst.item(i).getNodeType() == Node.ELEMENT_NODE) {
						
						Element annotation = (Element) annotationsLst.item(i);
						NodeList documentList = annotation.getElementsByTagName("document");
						
						for(int iterator=0; iterator < documentList.getLength(); iterator++){
							Element document = (Element) documentList.item(iterator);
							System.out.println("\nDocument URL: " + document.getAttributes().getNamedItem("url").getNodeValue());
							annotationInstanceList = document.getElementsByTagName("annotationInstance");
											
							if(annotationInstanceList.getLength() == 0){
								AnnotationInstance annot = new AnnotationInstance("-","-",type,"-","-");
								EvaluationSession.getResources().get(iterator).getAnnotations().add(annot);
							}else{
								for(int k=0; k < annotationInstanceList.getLength(); k++){
								System.out.println("\tAnnotation Instance #" + (k+1));
							
								Element annotationInstance = (Element) annotationInstanceList.item(k);
								
								System.out.println("\tContent: " + annotationInstance.getAttributes().getNamedItem("content").getNodeValue());
								String content = annotationInstance.getAttributes().getNamedItem("content").getNodeValue();
								
								System.out.println("\tStart: " + annotationInstance.getAttributes().getNamedItem("start").getNodeValue());
								String start = annotationInstance.getAttributes().getNamedItem("start").getNodeValue();
								
								System.out.println("\tEnd: " + annotationInstance.getAttributes().getNamedItem("end").getNodeValue()+"\n");
								String end = annotationInstance.getAttributes().getNamedItem("end").getNodeValue();
								
								AnnotationInstance annot = new AnnotationInstance(Integer.toString(annotationID),content,type,start,end);
								annotationID++;
								NodeList featureLst = annotationInstance.getElementsByTagName("feature");
								for(int z=0; z < featureLst.getLength(); z++){
									System.out.print("\t\tFeature #" + (z+1));
									
									Element feature = (Element) featureLst.item(z);
									String name =  feature.getAttributes().getNamedItem("name").getNodeValue();
									String value =  feature.getAttributes().getNamedItem("value").getNodeValue();
									
									System.out.println(" " + name + " = " + value);
									
									annot.addFeatureMap(name, value);
								}
								
								System.err.println("\tAdded to " + EvaluationSession.getResources().get(iterator).getFile().getFullPath().toString()+"\n");
								EvaluationSession.getResources().get(iterator).getAnnotations().add(annot);
							}
						}
					}
					}	
				}
			}catch(Exception e){
				System.err.println("Exception is parsing the result XML. Content is not allowed.");
				SemanticAssistantsStatusViewModel.addLog("Could not parse sever's response. Aborting...");
			}
		}
	

		

}
