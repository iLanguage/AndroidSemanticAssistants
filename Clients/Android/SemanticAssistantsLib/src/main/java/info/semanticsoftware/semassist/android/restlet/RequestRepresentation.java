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
package info.semanticsoftware.semassist.android.restlet;

import info.semanticsoftware.semassist.android.prefs.PrefUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.app.Application;

public class RequestRepresentation {

	private String serviceName;
	private String input;
	private Map<String, String> params = new HashMap<String, String>();
	private Application application = null;

	public RequestRepresentation(Application app, String iServiceName, Map<String,String> iParams, String input){
		this.serviceName = iServiceName;
		this.params = iParams;
		this.input = input;
		this.application = app;
	}

	public String getXML(){
		PrefUtils prefUtil = PrefUtils.getInstance(application);

		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		buffer.append("<invocation>");
		String username = prefUtil.getUsername();
		String sessionId = prefUtil.getSessionId();
		buffer.append("<authenticationNeeded>");
		if(username != null){
			buffer.append("yes");
			if(username.indexOf("@") > -1){
				username = username.substring(0, username.indexOf("@"));
			}
			buffer.append("<username>").append(username).append("</username>");
			buffer.append("<sessionId>").append(sessionId).append("</sessionId>");
		}else{
			buffer.append("no");
		}
		buffer.append("</authenticationNeeded>");
		buffer.append("<serviceName>").append(serviceName).append("</serviceName>");
		if(params !=null){
			Set<String> paramNames = params.keySet();
			for(String name:paramNames){
				buffer.append("<param>");
					buffer.append("<name>").append(name).append("</name>");
					buffer.append("<value>");
					buffer.append(params.get(name));
					buffer.append("</value>");
				buffer.append("</param>");
			}
		}

		buffer.append("<input><![CDATA[");
		buffer.append(input);
		buffer.append("]]></input>");

		buffer.append("</invocation>");
		return buffer.toString();
	}
}
