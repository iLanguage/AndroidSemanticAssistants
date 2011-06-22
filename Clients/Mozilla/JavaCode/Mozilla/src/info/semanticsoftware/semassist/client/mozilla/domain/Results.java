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
 *   Jason Tan
 *   Kevin Tung
 *   Paola Jimenez
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

package info.semanticsoftware.semassist.client.mozilla.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import info.semanticsoftware.semassist.csal.result.*;

public class Results {
	
	private AnnotationVectorArray annotationVector;
	private ArrayList<RetrievedDocument> retrievedDoc;
	private Annotation annotation;
	private String url;
	private String selectedText;
	private String docString;
	private String serviceCalled;
	private String type;

	/** 
	 * The default constructor 
	 */
	public Results() {
		
	}
	
	/** 
	 * A non-default constructor 
	 * 
	 * @param text The text to be analyzed
	 */
	public Results(String text) {
		this.selectedText = text;
	}
	
	/** 
	 * A non-default constructor 
	 * 
	 * @param type The result type 
	 * @param serviceCalled The service called
	 */
	public Results(String type, String serviceCalled) {
		this.type = type;
		this.serviceCalled = serviceCalled;
	}

	/** 
	 * Gets the service called 
	 * 
	 * @return String The service called
	 */
	public String getServiceCalled() {
		return this.serviceCalled;
	}

	/** 
	 * Sets the service called 
	 * 
	 * @param serviceCalled The service called
	 */
	public void setServiceCalled(String serviceCalled) {
		this.serviceCalled = serviceCalled;
	}

	/** 
	 * Gets the result 
	 * 
	 * @return String The result
	 */
	public String getResult() {
		String result = "";
		
		if ( type.equalsIgnoreCase("ANNOTATION") ) {
			result = getAnnotationToString(annotation).toString();
		}
		else if ( type.equalsIgnoreCase("FILE") ) {
			
		}
		else if ( type.equalsIgnoreCase("DOCUMENT") ) {
			result = this.url;
		}
		else if ( type.equalsIgnoreCase("CORPUS") ) {
	
		}
		else if ( type.equalsIgnoreCase("ANNOTATION_IN_WHOLE") ) {
	
		}
		return result;
	}

	/** 
	 * Sets the URL
	 * 
	 * @param url The URL
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/** 
	 * Gets the selected text
	 * 
	 * @return String The selected text
	 */
	public String getSelectedText() {
		return this.selectedText;
	}

	/** 
	 * Sets the selected text
	 * 
	 * @param selectedText The selected text
	 */
	public void setSelectedText(String selectedText) {
		this.selectedText = selectedText;
	}

	/** 
	 * Gets the type
	 * 
	 * @return String The type
	 */
	public String getType() {
		return this.type;
	}

	/** 
	 * Sets the annotation vector array
	 * 
	 * @param annotationVector The annotation vector array
	 */
	public void setAnnotationVector(AnnotationVectorArray annotationVector) {
		this.annotationVector = annotationVector;
	}

	/** 
	 * Sets the retrieved document
	 * 
	 * @param retrievedDoc The retrieved document
	 */
	public void setRetrievedDoc(ArrayList<RetrievedDocument> retrievedDoc) {
		this.retrievedDoc = retrievedDoc;
	}

	/** 
	 * Sets the annotation
	 * 
	 * @param annotation The annotation
	 */
	public void setAnnotation(Annotation annotation) {
		this.annotation = annotation;
	}

	/** 
	 * Sets the type of the result
	 * 
	 * @param type The type of the result
	 */
	public void setType(String type) {
		this.type = type;
	}

	/** 
	 * Gets the annotation
	 * 
	 * @return StringBuffer The annotation
	 */
	private StringBuffer getAnnotationToString(Annotation annotation) {
		StringBuffer results = new StringBuffer();
		
		if (annotation == null) {
			return results;
		}
		if( annotation.mType != null && !annotation.mType.equals( "" ) ) {
			results.append( "type: " + annotation.mType  + " \n" );
		}
		if( annotation.mContent != null && !annotation.mContent.equals( "" ) ) {
			results.append( "content: " + annotation.mContent + " \n" );
		}
		if( annotation.mFeatures == null || annotation.mFeatures.size() == 0 ) {
			results.append( " \n" );
			return results;
		}
		if( annotation.mFeatures.size() > 1 ) {
			results.append( "Features: \n" );
		}
		Set<String> keys = annotation.mFeatures.keySet();

		for( Iterator<String> it2 = keys.iterator(); it2.hasNext(); ) {
			String currentKey = it2.next();
			results.append( currentKey + ": " + annotation.mFeatures.get( currentKey ) + " \n" );
		}

		return results;
	}

	/** 
	 * Gets the annotation start offset
	 * 
	 * @return long The annotation start offset
	 */
	public long getAnnotationmStart() {
		long returnValue = -1;
		if (this.annotation != null) {
			returnValue = this.annotation.mStart;
		}
		return returnValue;
	}

	/** 
	 * Gets the annotation end offset
	 * 
	 * @return long The annotation end offset
	 */
	public long getAnnotationmEnd() {
		long returnValue = -1;
		if (this.annotation != null) {
			returnValue = this.annotation.mEnd;
		}
		return returnValue;
	}

	/** 
	 * Gets the annotation type
	 * 
	 * @return String The annotation type
	 */
	public String getAnnotationType() {
		String annotationType = "";
		if (this.annotation != null) {
			annotationType = this.annotation.mType;
		}
		return annotationType;
	}

	/** 
	 * Gets the annotation content
	 * 
	 * @return String The annotation content
	 */
	public String getAnnotationContent() {
		String annotationContent = "";
		if (this.annotation != null) {
			annotationContent = this.annotation.mContent;
		}
		return annotationContent;
	}
	
	/** 
	 * Gets all the annotation features, with both empty and non-empty values
	 * 
	 * @return String All the annotation features 
	 */
	public String getAllAnnotationFeatures() {
		String features = "  ";
		if (this.annotation != null) {
			Set<String> keys = this.annotation.mFeatures.keySet();

			for( Iterator<String> it2 = keys.iterator(); it2.hasNext(); ) {
				String currentKey = it2.next();
				features +=( currentKey + ": " + this.annotation.mFeatures.get( currentKey ) + "\n" + "   ");
			}
		}
		return features;
	}
	
	/** 
	 * Gets the annotation features that have non-empty values
	 * 
	 * @return String The annotation features that have non-empty values
	 */
	public String getFilledAnnotationFeatures() {
		String features = "  ";
		if (this.annotation != null) {
			Set<String> keys = this.annotation.mFeatures.keySet();

			for( Iterator<String> it2 = keys.iterator(); it2.hasNext(); ) {
				String currentKey = it2.next();
				if ( !(this.annotation.mFeatures.get( currentKey )).equals("") ){
					features +=( currentKey + ": " + this.annotation.mFeatures.get( currentKey ) + "\n" + "   " );
				}
			}
		}
		return features;		
	}
	
	/** 
	 * Gets the scientific name of the result 
	 * (used for displaying the grouping the results of the Organism Tagger service)
	 * 
	 * @return String The grouping name
	 */
	public String getGroupingName() {
		String groupingName = "";
		
		if (this.annotation != null) {
			Set<String> keys = this.annotation.mFeatures.keySet();

			for( Iterator<String> it2 = keys.iterator(); it2.hasNext(); ) {
				String currentKey = it2.next();
				if (currentKey.equalsIgnoreCase("ScientificName")) {
					groupingName = this.annotation.mFeatures.get( currentKey );
					break;
				}				
			}
			
			// if the "scientific name" feature has an empty value, use the annotation content instead
			if (groupingName.equals("")) {
				groupingName = this.annotation.mContent;
			}
		}
		
		return groupingName;
	}
	
	/** 
	 * Gets the ncbi id of the result 
	 * (used for grouping the results of the Organism Tagger service)
	 * 
	 * @return String The grouping id
	 */
	public String getGroupingId() {
		String groupingId = "";
		
		if (this.annotation != null) {
			Set<String> keys = this.annotation.mFeatures.keySet();

			for( Iterator<String> it2 = keys.iterator(); it2.hasNext(); ) {
				String currentKey = it2.next();
				if (currentKey.equalsIgnoreCase("ncbiId")) {
					groupingId = this.annotation.mFeatures.get( currentKey );
					break;
				}				
			}
			
			// if the "ncbi id" feature has an empty value, use the annotation content instead
			if (groupingId.equals("")) {
				groupingId = this.annotation.mContent;
			}
		}
		
		return groupingId;
	}

}
