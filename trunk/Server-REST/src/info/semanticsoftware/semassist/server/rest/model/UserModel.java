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

import java.util.Iterator;

import info.semanticsoftware.semassist.server.ServiceInfoForClient;
import info.semanticsoftware.semassist.server.core.security.authentication.AuthenticationUtils;

/** Provides a representation of a user in the system.
 * @author Bahar Sateli
 */
public class UserModel {

	/** Services iterator object.*/
	Iterator<ServiceInfoForClient> iterator = null;

	public UserModel(){
	}

	/**
	 * Returns XML representation of a user.
	 * @param userName username
	 * @return XML representation of the specified user with public key value or null
	 */
	public String getXML(String userName) {
		StringBuilder xml = new StringBuilder();
		xml.append("<user>");
		xml.append("<userName>").append(userName).append("</userName>");
		xml.append("<userKey>").append(AuthenticationUtils.getInstance().getModulusString(userName)).append("</userKey>");
		xml.append("<link>").append(getLink(userName)).append("</link>");
		xml.append("</user>");
		return xml.toString();
	}

	/**
	 * Returns partial link URI of the specified user.
	 * @param userName username
	 * @return partial URI for the user
	 */
	private String getLink(String userName){
		return "/users/" + userName;
	}

}
