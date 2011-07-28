/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is the Semantic Assistants Mozilla Extension.
 *
 * The Initial Developer of the Original Code is
 * Semantic Software Lab (http://www.semanticsoftware.info).
 * Portions created by the Initial Developer are Copyright (C) 2011
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Nikolaos Papadakis
 *   Tom Gitzinger
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */

package info.semanticsoftware.semassist.client.mozilla.services;

import info.semanticsoftware.semassist.client.mozilla.domain.Results;
import info.semanticsoftware.semassist.csal.ClientUtils;
import info.semanticsoftware.semassist.csal.result.Annotation;
import info.semanticsoftware.semassist.csal.result.AnnotationVector;
import info.semanticsoftware.semassist.csal.result.AnnotationVectorArray;
import info.semanticsoftware.semassist.csal.result.RetrievedDocument;
import info.semanticsoftware.semassist.csal.result.SemanticServiceResult;
import info.semanticsoftware.semassist.server.GateRuntimeParameterArray;
import info.semanticsoftware.semassist.server.SemanticServiceBroker;
import info.semanticsoftware.semassist.server.UriList;
import info.semanticsoftware.semassist.server.UserContext;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import net.java.dev.jaxb.array.StringArray;

public class ServiceInvocationHandler implements Runnable {
	
	/** The text to be analyzed */
	private String argumentText = null;
	
	/** The service name */
	private String serviceName = null;
	
	/** The thread */
	private Thread thread;
	
	/** The array of GateRuntimeParameter */
	private GateRuntimeParameterArray rtpArray = new GateRuntimeParameterArray();

	/**
	 * Invokes the service, obtains and processes the results, and returns a list of results 
	 * 
	 * @param serviceCalled The name of the service called
	 * @return ArrayList<Results> The list of results
	 */
	public ArrayList<Results> getResults(String serviceCalled) {
		ArrayList<Results> resultsList = new ArrayList<Results>();
		if ( argumentText == null || serviceName == null ) {
			return resultsList;
		}

		System.out.println("Start of argument text");
		System.out.println();
		System.out.println(argumentText);
		System.out.println();
		System.out.println("End of argument text");
		System.out.println();

		SemanticServiceBroker broker = ServiceAgentSingleton.getInstance();
		UriList uriList = new UriList();
		uriList.getUriList().add( new String( "#literal" ) );
		StringArray stringArray = new StringArray();
		stringArray.getItem().add( new String( argumentText ) );

		String serviceResponse = null;

		try {
			serviceResponse = broker.invokeService( serviceName, uriList, stringArray, 0L,
					rtpArray, new UserContext() );
			System.out.println("Start of serviceResponse");
			System.out.println();
			System.out.println(serviceResponse);
			System.out.println();
			System.out.println("End of serviceResponse");
			System.out.println();
		}
		catch (Exception connEx) {
			// Server not found. Please check the Server Host and Port and if Server is Online
			return resultsList;
		}

		// returns result in sorted by type
		Vector<SemanticServiceResult> results = ClientUtils.getServiceResults( serviceResponse );

		// used for document case
		ArrayList<String> docString = new ArrayList<String>();
		//boolean docCase = false;
		String resultTypeCase = new String("");

		if ( results == null ) {
			System.out.println( "---------- No results retrieved in response message" );
			return resultsList;
		}

		// Key is annotation document URL or ID
		HashMap<String, AnnotationVectorArray> annotationsPerDocument =
			new HashMap<String, AnnotationVectorArray>();

		for ( Iterator<SemanticServiceResult> it = results.iterator(); it.hasNext(); ) {
			SemanticServiceResult current = it.next();

			if ( current.mResultType.equals( SemanticServiceResult.FILE ) ) {                	
				// File case
				System.out.println( "------------ Result type: " + SemanticServiceResult.FILE );
				String fileString = broker.getResultFile( current.mFileUrl );
				String fileExt = ClientUtils.getFileNameExt( current.mFileUrl );

				if ( fileExt == null ) {
					fileExt = ".txt";
				}

				System.out.println( "------------ fileExt: " + fileExt );
				System.out.println();
				File f = ClientUtils.writeStringToFile( fileString, fileExt + ".html" );

				// Try to open HTML file in browser

				System.out.println("Using primary method to open the file in browser");
				System.out.println();
				try {
					System.out.println( "java.awt.Desktop.isDesktopSupported() == " + java.awt.Desktop.isDesktopSupported() );
					if (java.awt.Desktop.isDesktopSupported()) {
						System.out.println( "canonical path of file: <" +  f.getCanonicalPath() + ">" );
						String url = new String(f.getCanonicalPath().replace('\\', '/'));
						System.out.println( "url: <" + url + ">" );
						System.out.println();
						
						System.out.println( "Opening file in browser" );
						System.out.println();
						java.awt.Desktop.getDesktop().browse(java.net.URI.create(url)); 
						System.out.println( "File opened in browser" );
						System.out.println();
					}
				}
				catch( java.io.IOException e ) {
					e.printStackTrace();
					System.out.println();
					System.out.println("Primary method to open the file in browser failed");
					System.out.println();
					System.out.println("Using secondary method which attempts to open the file specifically in Firefox");
					System.out.println();

					if ( fileString.startsWith( "<!DOCTYPE HTML" ) ) {
						try {
							String command = "firefox " + f.getCanonicalPath();
							System.out.println( "---------------- Executing " + command );
							Process p = Runtime.getRuntime().exec( command );
							System.out.println( "---------------- Command executed" );
							System.out.println();
						}
						catch ( java.io.IOException ex ) {
							System.out.println( "Secondary method to open the file in browser failed" ); 
							System.out.println();
						}
					}
				}

				resultTypeCase = "FILE";
			}                
			else if ( current.mResultType.equals( SemanticServiceResult.ANNOTATION_IN_WHOLE ) ) {
				// Annotation case => append to data structure
				System.out.println( "---------------- Annotation case..." );

				// Keys are document IDs or URLs
				HashMap<String, AnnotationVector> map = current.mAnnotations;
				Set<String> keys = map.keySet();

				for( Iterator<String> it2 = keys.iterator(); it2.hasNext(); ) {
					String docID = it2.next();

					if( annotationsPerDocument.get( docID ) == null ) {
						annotationsPerDocument.put( docID, new AnnotationVectorArray() );
					}

					AnnotationVectorArray v = annotationsPerDocument.get( docID );
					v.mAnnotVectorArray.add( map.get( docID ) );
				}

				// Assemble annotations string
				if( annotationsPerDocument.size() > 0 ) {
					System.out.println( "---------------- Creating document with annotation information..." );

					//create the document from the response
					System.out.println( saveAnnotationsString( annotationsPerDocument ) );   
				}
			}
			else if ( current.mResultType.equals( SemanticServiceResult.ANNOTATION ) ) {
				// Sidenote case => append to data structure
				System.out.println( "---------------- Annotation case..." );

				// Keys are document IDs or URLs
				HashMap<String, AnnotationVector> map = current.mAnnotations;
				Set<String> keys = map.keySet();

				for( Iterator<String> it2 = keys.iterator(); it2.hasNext(); ) {
					String docID = it2.next();

					if( annotationsPerDocument.get( docID ) == null ) {
						annotationsPerDocument.put( docID, new AnnotationVectorArray() );
					}

					AnnotationVectorArray v = annotationsPerDocument.get( docID );
					v.mAnnotVectorArray.add( map.get( docID ) );
				}

				// Assemble annotations string
				if( annotationsPerDocument.size() > 0 ) {
					System.out.println( "---------------- Creating side-notes with annotation information..." );
					saveAnnotationsString( annotationsPerDocument );
				}
			}
			else if ( current.mResultType.equals( SemanticServiceResult.DOCUMENT ) ) {
				// Corpus case
				System.out.println( "---------------- Document case... URL:" + current.mFileUrl );
				//docCase = true;
				resultTypeCase = "DOCUMENT";
				docString.add(current.mFileUrl);
			}
			// Everything else
			else {
				System.out.println( "---------------- Do not recognize kind of output: " + current.mResultType );
			}

		} // end while (for each result)

		System.out.println("Service called: " + serviceCalled);
		System.out.println();
		
		//save the results
		
		if (ClientUtils.mAnnotArray != null ) { 
			for ( Iterator<Annotation> it2 = ClientUtils.mAnnotArray.iterator() ; it2.hasNext() ; ) {
				Annotation annotation = it2.next();
				Results result = new Results("ANNOTATION", serviceCalled);
				result.setAnnotation(annotation);
				resultsList.add(result);
			}
		}

		//if (docCase && docString.size() > 0) {
		if (resultTypeCase == "DOCUMENT" && docString.size() > 0) {
			//create the document from the response
			for (String s: docString) {
				Results result = new Results("DOCUMENT", serviceCalled);
				result.setUrl(s);
				resultsList.add(result);
			}
		} 
		else if (resultTypeCase == "FILE") {
			Results result = new Results("FILE", serviceCalled);
			resultsList.add(result);
		}
		
		return resultsList;
	}

	/**
	 * Returns the annotation in string form
	 * 
	 * @param map The map of annotations 
	 * @param results 
	 * @return String
	 */
	private String saveAnnotationsString( HashMap<String, AnnotationVectorArray> map ) {
		if ( map == null ) {
			return "";
		}

		StringBuffer stringBuffer = new StringBuffer();

		// The key is annotation document ID (URL or number), the values are
		// annotation instances, basically
		Set<String> keys = map.keySet();

		for ( Iterator<String> it = keys.iterator(); it.hasNext(); ) {
			String docID = it.next();
			stringBuffer.append( "Annotations for document " + docID + ":\n\n" );
			AnnotationVectorArray va = map.get( docID );

			ClientUtils.SortAnnotations( va );
		}
		
		return stringBuffer.toString();
	}

	public void setServiceName( String serviceName ) {
		this.serviceName = serviceName;
	}

	public void setArgumentText( String argumentText ) {
		this.argumentText = argumentText;
	}

	public void setRuntimeParameters( GateRuntimeParameterArray rtpArray ) {
		this.rtpArray = rtpArray;
	}
	
	/*
	public void start() {
		run();
	}

	public void join() {
		try {
			thread.join();
		}
		catch( InterruptedException e ) {
			e.printStackTrace();
		}
	}
	
	*/
	
	@Override
	public void run() {
		
	}

}
