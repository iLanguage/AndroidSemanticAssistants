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

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class XMLParser 
{

	HashMap<String, Annotation> annotation ;
	ArrayList<String> outputDocument ;

	public boolean parse(String document)
	{
		try {
			
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();

			ByteArrayInputStream bais = new ByteArrayInputStream(document.getBytes());
			
			XMLHandler myXMLHandler = new XMLHandler();
			xr.setContentHandler(myXMLHandler);
			xr.parse(new InputSource(bais));
			
			annotation = XMLHandler.annotation;
			outputDocument = XMLHandler.outputDocument ;
			
			return true ;
			
		} catch (Exception e) {
			System.out.println("XML Parsing Exception = " + e);
			return false ;
		}

	}

	public ArrayList<String> getOutputDocument() {
		return outputDocument;
	}

	public void setOutputDocument(ArrayList<String> outputDocument) {
		this.outputDocument = outputDocument;
	}


	public HashMap<String, Annotation> getAnnotation() {
		return annotation;
	}

	public void setAnnotation(HashMap<String, Annotation> annotation) {
		this.annotation = annotation;
	}
}