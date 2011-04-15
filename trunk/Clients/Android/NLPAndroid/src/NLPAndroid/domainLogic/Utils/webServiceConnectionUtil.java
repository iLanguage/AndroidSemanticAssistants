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
/**
 * 
 * 
 * @author Chadi Cortbaoui - Concordia University Software Engineering
 * 
 * 	This Class is for server configuration related tasks.
 */

package NLPAndroid.domainLogic.Utils;

import java.util.Properties;

public class webServiceConnectionUtil {
	
	// Ksoap2 Envelope Required Attributes
	private String methodname ;
	private String url ;
	private String namespace; 
	private String action; 

	public webServiceConnectionUtil(){}
	
	public webServiceConnectionUtil(Properties properties)
	{
		loadDefaultwebServicePreferences(properties);
	}

	public webServiceConnectionUtil(String methodname, String url,
			String namespace, String action) {
		this.methodname = methodname;
		this.url = url;
		this.namespace = namespace;
		this.action = action;
	}
	
	
	//Hudson's Default
	//assign of change any of the ws properties
	public void assignDefaultwebServicePreferences()
	{
		setMethodname("getAvailableServices");
		setUrl("http://assistant.cs.concordia.ca:8879/SemAssist?wsdl");
		setNamespace("http://server.semassist.semanticsoftware.info/");
		setAction("SemanticServiceBroker");
		
	}
	
	//function to load the default in the application
	public void loadDefaultwebServicePreferences(Properties properties)
	{
		setMethodname(properties.getProperty("methodname"));
		setUrl(properties.getProperty("url"));
		setNamespace(properties.getProperty("namespace"));
		setAction(properties.getProperty("action"));
		
	}
	
	public String getMethodname() {
		return methodname;
	}

	public void setMethodname(String methodname) {
		this.methodname = methodname;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

}
