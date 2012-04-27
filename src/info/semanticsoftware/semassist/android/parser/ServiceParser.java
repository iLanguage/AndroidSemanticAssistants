package info.semanticsoftware.semassist.android.parser;

import java.io.StringReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import info.semanticsoftware.semassist.server.ServiceInfoForClientArray;

public class ServiceParser {

	private String serviceRepresentation = null;

	public ServiceParser(String representation){
		serviceRepresentation = representation;
	}

	public ServiceInfoForClientArray parseToObject(){
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance(); 
			SAXParser sp = spf.newSAXParser(); 
			XMLReader xr = sp.getXMLReader(); 

			/* Create a new ContentHandler and apply it to the XML-Reader*/
			ServiceHandler handler = new ServiceHandler();
			xr.setContentHandler(handler);

			/* Parse the XML data from our string */
			xr.parse(new InputSource(new StringReader(serviceRepresentation)));
			/* Parsing has finished. */

			/* Our ServiceHandler now provides the parsed data */
			ServiceInfoForClientArray servicesList = handler.getParsedData();

			return servicesList;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
