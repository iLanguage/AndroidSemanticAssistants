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

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class parses a request representation XML message.
 * @author Bahar Sateli
 * */
public class RequestHandler extends DefaultHandler{

	/** The <code><serviceName></code> XML element representing an NLP service name. */
	private boolean serviceNameTag = false;

	/** The <code><input></code> XML element representing representing the input text. */
	private boolean inputTag = false;

	/** The <code><userName></code> XML element representing a user. */
	private boolean usernameTag = false;

	/** The <code><password></code> XML element representing a user's password. */
	private boolean passwordTag = false;

	/** The <code><sessionKey></code> XML element representing a shared secret key. */
	private boolean sessionKeyTag = false;

	/** The <code><serviceIV></code> XML element representing the session key initialization vector. */
	private boolean sessionIVTag = false;

	/** Contains the <code><serviceName></code> node value. */
	private String serviceName;

	/** Contains the <code><input></code> node value. */
	private String input;

	/** Contains the <code><userName></code> node value. */
	private String username;

	/** Contains the <code><password></code> node value. */
	private String password;

	/** Contains the <code><sessionKey></code> node value. */
	private String sessionKey;

	/** Contains the <code><sessionIV></code> node value. */
	private String sessionIV;

	/** Boolean value representing whether the request needs user authentication */
	private boolean needsAuthentication = false;

	/** The <map between parameter's name and values. */
	private Map<String, String> params = new HashMap<String, String>();

	/** The <code><name></code> XML element representing a runtime parameter's name. */
	private boolean paramNameTag = false;

	/** The <code><value></code> XML element representing a runtime parameter's value. */
	private boolean paramValueTag = false;

	/** A temporary object to contain the parameter <code><name></code> node value. */
	private String tempParamName = "";

	/** The <code><authenticationNeeded></code> XML element representing user authentication */
	private boolean authenticationTag = false;

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

	/** Returns the runtime parameters map.
	 * @return runtime parameters map
	 */
	public Map<String, String> getParams(){
		return this.params;
	}

	/** Returns the user authentication value
	 * @return true if request needs user authentication, false otherwise
	 */
	public boolean needsAuthentication(){
		return this.needsAuthentication;
	}

	/** Gets invoked when parsing a request is initiated. 
	 * @throws SAXException if the request cannot be parsed */
	@Override
	public void startDocument() throws SAXException {
		System.out.println("Started parsing service request.");
	}

	/** Gets invoked when parsing a request is finished. 
	 * @throws SAXException if the request cannot be parsed */
	@Override
	public void endDocument() throws SAXException {
		System.out.println("Finished parsing service request.");
	}

	/** Gets called on opening tags like <code><tag></code>.
	 * It can also provide attribute(s), e.g., <tag attribute="attributeValue">
	 * @param namespaceURI namespace used for the node
	 * @param localName node's local name in the document
	 * @param qName nodes' qualified name
	 * @param atts node's attributes 
	 * @throws SAXException if the request cannot be parsed */
	@Override
	public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) throws SAXException {
		if (qName.equals("serviceName")) {
			this.serviceNameTag = true;
		}else if (qName.equals("input")) {
			this.inputTag = true;
		}else if (qName.equals("username")) {
			this.usernameTag = true;
		}else if (qName.equals("password")) {
			this.passwordTag = true;
		}else if(qName.equals("name")){
			this.paramNameTag = true;
		}else if(qName.equals("value")){
			this.paramValueTag = true;
		}else if (qName.equals("sessionKey")) {
			this.sessionKeyTag = true;
		}else if (qName.equals("sessionIV")) {
			this.sessionIVTag = true;
		}else if(qName.equals("authenticationNeeded")){
			this.authenticationTag = true;
		}
	}

	/** Gets called on closing tags like <code></tag></code>.
	 * @param namespaceURI namespace used for the node
	 * @param localName node's local name in the document
	 * @param qName node's qualifed name 
	 * @throws SAXException if the request cannot be parsed */
	@Override
	public void endElement(final String namespaceURI, final String localName, final String qName) throws SAXException {
		if (qName.equals("serviceName")) {
			this.serviceNameTag = false;
		}else if (qName.equals("input")) {
			this.inputTag = false;
		}else if (qName.equals("username")) {
			this.usernameTag = false;
		}else if (qName.equals("password")) {
			this.passwordTag = false;
		}else if(qName.equals("name")){
			this.paramNameTag = false;
		}else if(qName.equals("value")){
			this.paramValueTag = true;
		}else if (qName.equals("sessionKey")) {
			this.sessionKeyTag = false;
		}else if (qName.equals("sessionIV")) {
			this.sessionIVTag = false;
		}else if(qName.equals("authenticationNeeded")){
			this.authenticationTag = false;
		}
	}

	/** Gets called on the following structure: <code><tag>characters</tag></code>.
	 * @param ch char array representing the node content
	 * @param start start offset in the array
	 * @param length length of the content in the array */
	@Override
	public void characters(final char[] ch, final int start, final int length) {
		if(this.serviceNameTag){
			serviceName = new String(ch, start, length);
		}else if(this.inputTag){
			input = new String(ch, start, length).trim();
		}else if(this.usernameTag){
			username = new String(ch, start, length);
		}else if(this.passwordTag){
			password = new String(ch, start, length).trim();
		}else if(this.paramNameTag){
			tempParamName = new String(ch, start, length).trim();
		}else if(this.paramValueTag){
			String tempParamValue = new String(ch, start, length).trim();
			params.put(tempParamName, tempParamValue);
		}else if(this.sessionKeyTag){
			sessionKey = new String(ch, start, length).trim();
		}else if(this.sessionIVTag){
			sessionIV = new String(ch, start, length).trim();
		}else if(this.authenticationTag){
			String value = new String(ch, start, length).toLowerCase().trim();
			if(value.equals("yes")){
				needsAuthentication = true;
			}else{
				needsAuthentication = false;
			}
		}
	}
}
