/**
   Semantic Assistants - http://www.semanticsoftware.info/semantic-assistants

   This file is part of the Semantic Assistants architecture.

   Copyright (C) 2009, 2010, 2011 Semantic Software Lab, http://www.semanticsoftware.info

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
package NLPAndroid.domainLogic.Utils;

import java.util.ArrayList;

//this models the first type of server response
public class Annotation {

	//variables
	private Document document ;
	private String type ;
//	private String content ;
	
	//ctor
	Annotation()
	{
		this.document = new Document() ;
	}
	Annotation(String type)
	{
		this.document = new Document();
		this.type = type ;
	}
	
	//get, set
	public Document getDocument() {
		return document;
	}
	public void setDocument(Document document) {
		this.document = document;
	}
	
	public String getDocumentUrl() {
		return this.document.url;
	}
	public void setDocumentUrl(String url) {
		this.document.url = url;
	}

	public void setDocumentAnnotationInstance(String featureValue, String featureName, String content) {
		AnnotationInstance annoInstance = new AnnotationInstance() ;
		annoInstance.setFeatureValue(featureValue) ;
		annoInstance.setFeatureName(featureName) ;
		annoInstance.setContent(content) ;
		this.document.annotationInstance.add(annoInstance) ;
	}
	
	public ArrayList<AnnotationInstance> getDocumentAnnotationInstance()
	{
		return this.document.annotationInstance ;
	}
	
	// Inner classes
	private class Document
	{
		private String url ;
		private ArrayList<AnnotationInstance> annotationInstance ;
		
		Document()
		{
			this.url = "" ;
			this.annotationInstance = new ArrayList<AnnotationInstance>() ;
		}

		public ArrayList<AnnotationInstance> getAnnotationInstance() {
			return annotationInstance;
		}

		public void setAnnotationInstance(
				ArrayList<AnnotationInstance> annotationInstance) {
			this.annotationInstance = annotationInstance;
		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
//	public void setDocumentAnnotationInstanceContent(String content) {
//		this.content = content ;
//	}
//	public String getContent() {
//		return content;
//	}

}
