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

import info.semanticsoftware.semassist.server.GateRuntimeParameterArray;
import info.semanticsoftware.semassist.server.UriList;
import info.semanticsoftware.semassist.server.UserContext;
import info.semanticsoftware.semassist.server.core.security.authentication.AuthenticationUtils;
import info.semanticsoftware.semassist.server.core.security.encryption.EncryptionUtils;
import info.semanticsoftware.semassist.server.rest.business.ServiceAgentSingleton;

import java.io.StringReader;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.RSAPrivateKeySpec;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.java.dev.jaxb.array.StringArray;

import org.apache.commons.codec.binary.Base64;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class RequestParser {
	private String requestRepresentation = null;

	public RequestParser(String representation){
		requestRepresentation = representation;
	}

	public String executeRequest(){
		try { 
			SAXParserFactory spf = SAXParserFactory.newInstance(); 
			SAXParser sp = spf.newSAXParser(); 
			XMLReader xr = sp.getXMLReader(); 

			/* Create a new ContentHandler and apply it to the XML-Reader*/
			RequestHandler handler = new RequestHandler();
			xr.setContentHandler(handler);

			/* Parse the XML data from the request string */
			xr.parse(new InputSource(new StringReader(requestRepresentation)));

			/* Get the parsed data to invoke the service */
			StringArray content = new StringArray();
			UriList urilist = new UriList();

			System.out.println("Invocation asked for " + handler.getServiceName());

			String encryptedText = handler.getInput();
			System.out.println("Ecnrypted input is: " + encryptedText);

			// STEP 1: Decrypt the session key using the user's private key
			//String sessionKeyString = handler.getSessionKeyString();
			//byte[] sessionKeyByte = Base64.decodeBase64(sessionKeyString);

			/********** DBG *********/

			String modulus = AuthenticationUtils.getInstance().getModulusString(handler.getUsername());
			String priPart = AuthenticationUtils.getInstance().getPrivateKeyString(handler.getUsername());

			RSAPrivateKeySpec newSpec = new RSAPrivateKeySpec(new BigInteger(modulus), new BigInteger(priPart));
			KeyFactory fact = KeyFactory.getInstance("RSA");
			PrivateKey priKey = fact.generatePrivate(newSpec);

			String test = requestRepresentation.substring(requestRepresentation.indexOf("<sessionKey>")+"<sessionKey>".length());
			test = test.substring(0, test.indexOf("</sessionKey>")).trim();

			System.out.println("Encrypted sessionKey: *"+test+"*");

			byte[] decryptedSessionKey = EncryptionUtils.getInstance().decryptSessionKey(test,priKey);
			System.out.println("Decrypted sessionKey: " + decryptedSessionKey);

			String sessionIV = handler.getSessionIVString();
			byte[] ivByte = Base64.decodeBase64(sessionIV);

			String plainText = new String(EncryptionUtils.getInstance().decryptInputData(encryptedText, decryptedSessionKey, ivByte));

			System.out.println("Decrypted input is: " + plainText);
			urilist.getUriList().add("#literal");
			content.getItem().add(plainText.trim());

			String results = ServiceAgentSingleton.getInstance().invokeService(handler.getServiceName(), urilist, content, 0L, new GateRuntimeParameterArray(), new UserContext());
			System.out.println("results" + results); 
			return results;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
