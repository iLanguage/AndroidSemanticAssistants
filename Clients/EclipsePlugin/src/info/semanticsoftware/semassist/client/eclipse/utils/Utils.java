package info.semanticsoftware.semassist.client.eclipse.utils;

import info.semanticsoftware.semassist.client.eclipse.handlers.ServiceAgentSingleton;
import info.semanticsoftware.semassist.csal.ClientUtils;
import info.semanticsoftware.semassist.csal.XMLElementModel;

import java.util.ArrayList;

public class Utils {
	
   public static final String INTERACTIVE_CONTEXT_FEATURE = "problem";

	public static boolean defaultSettings = true;

	public static void propertiesReader(){
		// Should return only one item in the list
		ArrayList<XMLElementModel> server = ClientUtils.getClientPreference("eclipse", "server");
		
		// if there are no server defined for this client. then look for the last called one in the global scope
		if(server.size() == 0){
			server = ClientUtils.getClientPreference(ClientUtils.XML_CLIENT_GLOBAL, "lastCalledServer");
		}
		
		// Note that if the former case, if by mistake there are more than one server defined, we pick the first one
		ServiceAgentSingleton.setServerHost(server.get(0).getAttribute().get(ClientUtils.XML_HOST_KEY));
		ServiceAgentSingleton.setServerPort(server.get(0).getAttribute().get(ClientUtils.XML_PORT_KEY));
	}

}
