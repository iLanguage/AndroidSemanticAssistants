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
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLHandler extends DefaultHandler {

	Boolean currentElement = false;
	String currentValue = null;
	private String lastAttr = null ;
	private String content ;
	public static HashMap<String, Annotation> annotation = null;
	public static ArrayList<String> outputDocument = null ;

	public static HashMap<String, Annotation> getAnnotation() {
		return annotation;
	}

	public static void setAnnotation(HashMap<String, Annotation> annotations) {
		XMLHandler.annotation = annotations;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		currentElement = true;

		if (localName.equalsIgnoreCase("saResponse"))
		{
			annotation = new HashMap<String,Annotation>();
			outputDocument = new ArrayList<String>() ;
		} 
		else if (localName.equalsIgnoreCase("annotation")) 
		{
			String attr = attributes.getValue("type");
			System.out.println("got anno: "+attr+"\n");
			annotation.put(attr, new Annotation(attr)) ;
			lastAttr = attr ;
		}
		else if (localName.equalsIgnoreCase("document")) 
		{
			String attr = attributes.getValue("url");
			System.out.println("got docurl: "+attr+"\n");
			annotation.get(lastAttr).setDocumentUrl(attr) ;
		}
		else if (localName.equalsIgnoreCase("feature")) 
		{
			String name = attributes.getValue("name");
			String value = attributes.getValue("value");
			System.out.println("got feature: "+name+"\n");
			annotation.get(lastAttr).setDocumentAnnotationInstance(name, value, this.content) ;
//			System.out.println("my internal content is: "+ this.content);
		}
		else if (localName.equalsIgnoreCase("annotationInstance")) 
		{
			String content = attributes.getValue("content");
//			System.out.println("got content: "+content+"\n");
			this.content = content ;
//			annotation.get(lastAttr).getDocumentAnnotationInstance().
		}
		else if (localName.equalsIgnoreCase("outputDocument")) 
		{
			String attr = attributes.getValue("url");
			System.out.println("got outdoc: "+attr+"\n");
			outputDocument.add(attr) ;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		currentElement = false;

	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {

		if (currentElement) {
			currentValue = new String(ch, start, length);
			currentElement = false;
		}

	}

}
