/*
Semantic Assistants -- http://www.semanticsoftware.info/semantic-assistants

This file is part of the Semantic Assistants architecture.

Copyright (C) 2012, 2013 Semantic Software Lab, http://www.semanticsoftware.info
Rene Witte
Bahar Sateli

The Semantic Assistants architecture is free software: you can
redistribute and/or modify it under the terms of the GNU Affero General
Public License as published by the Free Software Foundation, either
version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package info.semanticsoftware.semassist.service.HTMLTagger;

import net.java.dev.jaxb.array.StringArray;

import org.restlet.data.Form;
import org.restlet.data.Status;

import info.semanticsoftware.semassist.csal.ClientUtils;
import info.semanticsoftware.semassist.csal.result.Annotation;
import info.semanticsoftware.semassist.csal.result.AnnotationVector;
import info.semanticsoftware.semassist.csal.result.SemanticServiceResult;
import info.semanticsoftware.semassist.server.GateRuntimeParameterArray;
import info.semanticsoftware.semassist.server.SemanticServiceBroker;
import info.semanticsoftware.semassist.server.UriList;
import info.semanticsoftware.semassist.server.UserContext;
import info.semanticsoftware.semassist.service.model.AnnotationInstance;

//import java.text.SimpleDateFormat;
//import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

/**
 * Processes the incoming service requests.
 * @author Bahar Sateli
 * */
public class HTMLTagger extends ServerResource{
	
	public static String serviceName = "";

	/**
	 * Receives a POST request and processes the request for service invocation and annotation.
	 * @param entity request entity
	 * @return representation of the response (for now it's the body inner HTML)
	 * */
	@Post
	public synchronized Representation processRequest(final Representation entity) {
		try{
			Form form = new Form(entity);
			serviceName = form.getFirstValue("serviceName");
			String serverHost = form.getFirstValue("serverHost");
			String serverPort = form.getFirstValue("serverPort");
			String docURL = form.getFirstValue("doc");
			String rawHTML = form.getFirstValue("content");
			ServiceAgentSingleton.setServerHost(serverHost);
			ServiceAgentSingleton.setServerPort(serverPort);
			SemanticServiceBroker broker = ServiceAgentSingleton.getInstance();
			UriList docs = new UriList();
			docs.getUriList().add(docURL);
			
			/* ************************************* */
			/* DEBUGGING INFO -- uncomment if needed */
			/* ************************************* */
			/* System.out.println("Incoming Request Information \n\tReceived on " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) + "\n\tURL: " + docURL + "\n\tSA Server: " + serverHost + ":" + serverPort + "\n\tService Name: " + serviceName);*/
			/* ************************************* */
			
			//TODO look for runtime params
			long startTime = System.currentTimeMillis();
			String result = broker.invokeService(serviceName, docs, new StringArray(), 0L, new GateRuntimeParameterArray(), new UserContext());
			long endTime = System.currentTimeMillis();
			System.out.println("Service execution finished in " + ((endTime - startTime)/1000) + " seconds!");
			if(result.length() == 0){
				System.err.println("[WARNING] Service result is empty!");
				return null;
			}
			/* ************************************* */
			/* DEBUGGING INFO -- uncomment if needed */
			/* ************************************* */
			/* System.out.println("Service execution finished successfully."); */
			/* ************************************* */
			return processResults(rawHTML, result);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Processes the Semantic Assistants server XML response and delegates the
	 * annotation task to the MultiNodeAnnotation class.
	 * @param rawHTML HTML code processed from the POST request body
	 * @param XMLResponse the Semantic Assistants server XML response 
	 * @return the annotated body inner HTML
	 * */
	private Representation processResults(final String rawHTML, final String XMLResponse){
		String result = null;
		StringRepresentation representation = null;
		try {
			// parsed list of annotations sorted by their offset
			Vector<SemanticServiceResult> parsedResults = ClientUtils.getServiceResults(XMLResponse);
			List<AnnotationInstance> annotsArray = new ArrayList<AnnotationInstance>();
			for ( Iterator<SemanticServiceResult> it = parsedResults.iterator(); it.hasNext(); ) {
				SemanticServiceResult current = it.next();
				if ( current.mResultType.equals( SemanticServiceResult.ANNOTATION ) ) {
					
					/** List of annotations that maps document IDs to annotation instances */
					HashMap<String, AnnotationVector> annotationsVector = current.mAnnotations;
					Set<String> keys = annotationsVector.keySet();

					for(Iterator<String> it2 = keys.iterator(); it2.hasNext();){
						String docID = it2.next();
						AnnotationVector annotsVector = annotationsVector.get(docID);
						Vector<Annotation> annots = annotsVector.mAnnotationVector;
						for(int i=0; i < annots.size(); i++){
							AnnotationInstance annotation = new AnnotationInstance(Integer.toString(i), annots.get(i).mContent, annotsVector.mType, String.valueOf(annots.get(i).mStart), String.valueOf(annots.get(i).mEnd));
							Set<String> featureNames = annots.get(i).mFeatures.keySet();
							for(Iterator<String> it3 = featureNames.iterator(); it3.hasNext();){
								String name = it3.next();
								String value = annots.get(i).mFeatures.get(name);
								// filter empty features
								if(value.equals("")){
									continue;
								}else{
									annotation.addFeatureMap(name, value);
								}
							}
							annotsArray.add(annotation);
						}
					}
				}//FIXME handle other service output types: boundless annotation, file, document
			}
			// pass the HTML and annotation instances to the annotator class
			result = NodeAnnotator.getAnnotatedHTML(rawHTML, annotsArray);
			//result = NodeAnnotator.myText(rawHTML, annotsArray);
		}catch (Exception e1) {
			e1.printStackTrace();
		}
		
		if (result != null) {
			System.out.println("Done!");
			// send back the response to client
			setStatus(Status.SUCCESS_OK);
			representation = new StringRepresentation(result, MediaType.TEXT_PLAIN);
			return representation;
		} else {
			// send HTTP RFC-10.5.1 500 Internal Server Error
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return null;
		}
	}
}
