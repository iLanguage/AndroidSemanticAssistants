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

package info.semanticsoftware.semassist.server.rest.model;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

	public class RequestHandler extends DefaultHandler{

	/** The <serviceName> XML element representing an NLP service name. */
	private boolean serviceNameTag = false;

	/** The <input> XML element representing representing the input text. */
	private boolean inputTag = false;

	/** The <userName> XML element representing a user. */
	private boolean usernameTag = false;

	/** The <password> XML element representing a user's password. */
	private boolean passwordTag = false;

	/** The <sessionKey> XML element representing a shared secret key. */
	private boolean sessionKeyTag = false;

	/** The <serviceIV> XML element representing the session key initialization vector. */
	private boolean sessionIVTag = false;

	/** Contains the <serviceName> node value. */
	private String serviceName;

	/** Contains the <input> node value. */
	private String input;

	/** Contains the <userName> node value. */
	private String username;

	/** Contains the <password> node value. */
	private String password;

	/** Contains the <sessionKey> node value. */
	private String sessionKey;

	/** Contains the <sessionIV> node value. */
	private String sessionIV;

	/** Returns the service name specified in the request. 
	 * @return service name*/
	public String getServiceName(){
		return this.serviceName;
	}

	/** Returns the input specified in the request. 
	 * @return input string */
	public String getInput(){
		return this.input;
	}

	/** Returns the username specified in the request. 
	 * @return username */
	public String getUsername(){
		return this.username;
	}

	/** Returns the password specified in the request. 
	 * @return password */
	public String getPassword(){
		return this.password;
	}

	/** Returns the session key specified in the request. 
	 * @return session key string representation */
	public String getSessionKeyString(){
		return this.sessionKey;
	}

	/** Returns the session IV specified in the request. 
	 * @return session key IV string representation */
	public String getSessionIVString(){
		return this.sessionIV;
	}

	/** Gets invoked when parsing a request is initiated. */
	@Override
	public void startDocument() throws SAXException {
		System.out.println("Started parsing service request");
	}

	/** Gets invoked when parsing a request is finished. */
	@Override
	public void endDocument() throws SAXException {
		System.out.println("Finished parsing service request");
	}

	/** Gets called on opening tags like <tag>.
	 * It can also provide attribute(s), e.g., <tag attribute="attributeValue">
	 * @param namespaceURI namespace used for the node
	 * @param localName node's local name in the document
	 * @param qName nodes' qualified name
	 * @param atts node's attributes */
	@Override
	public void startElement(String namespaceURI, String localName,String qName, Attributes atts) throws SAXException {
		if (qName.equals("serviceName")) {
			this.serviceNameTag = true;
		}else if (qName.equals("input")) {
			this.inputTag = true;
		}else if (qName.equals("username")) {
			this.usernameTag = true;
		}else if (qName.equals("password")) {
			this.passwordTag = true;
		}else if (qName.equals("sessionKey")) {
			this.sessionKeyTag = true;
		}else if (qName.equals("sessionIV")) {
			this.sessionIVTag = true;
		}
	}

	/** Gets called on closing tags like </tag>.
	 * @param namespaceURI namespace used for the node
	 * @param localName node's local name in the document
	 * @param qName node's qualifed name */
	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		if (qName.equals("serviceName")) {
			this.serviceNameTag = false;
		}else if (qName.equals("input")) {
			this.inputTag = false;
		}else if (qName.equals("username")) {
			this.usernameTag = false;
		}else if (qName.equals("password")) {
			this.passwordTag = false;
		}else if (qName.equals("sessionKey")) {
			this.sessionKeyTag = false;
		}else if (qName.equals("sessionIV")) {
			this.sessionIVTag = false;
		}
	}

	/** Gets called on the following structure: <tag>characters</tag>.
	 * @param ch char array representing the node content
	 * @param start start offset in the array
	 * @param length length of the content in the array */
	@Override
	public void characters(char[] ch, int start, int length) {
		if(this.serviceNameTag){
			serviceName = new String(ch, start, length);
		}else if(this.inputTag){
			input = new String(ch, start, length).trim();
		}else if(this.usernameTag){
			username = new String(ch, start, length);
		}else if(this.passwordTag){
			password = new String(ch, start, length).trim();
		}else if(this.sessionKeyTag){
			sessionKey = new String(ch, start, length).trim();
		}else if(this.sessionIVTag){
			sessionIV = new String(ch, start, length).trim();
		}
	}
}
