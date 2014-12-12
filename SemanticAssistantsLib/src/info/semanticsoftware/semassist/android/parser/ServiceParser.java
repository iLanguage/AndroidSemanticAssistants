/*
* Semantic Assistants -- http://www.semanticsoftware.info/semantic-assistants
* 
* Copyright (C) 2014 Semantic Software Lab, http://www.semanticsoftware.info
* Rene Witte
* Bahar Sateli
* 
* This file is part of the Semantic Assistants architecture, and is 
* free software, licensed under the GNU Lesser General Public License 
* as published by the Free Software Foundation, either version 3 of 
* the License, or (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
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
