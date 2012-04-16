package info.semanticsoftware.semassist.server.rest.model;

import java.util.Iterator;

import info.semanticsoftware.semassist.server.ServiceInfoForClient;
import info.semanticsoftware.semassist.server.core.security.authentication.AuthenticationUtils;

public class UserModel {

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
