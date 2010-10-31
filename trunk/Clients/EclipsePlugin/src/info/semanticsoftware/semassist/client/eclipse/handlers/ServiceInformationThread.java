package info.semanticsoftware.semassist.client.eclipse.handlers;

import java.util.ArrayList;
import java.util.Iterator;


import info.semanticsoftware.semassist.client.eclipse.dialogs.FileSelectionDialog;
import info.semanticsoftware.semassist.server.SemanticServiceBroker;
import info.semanticsoftware.semassist.server.ServiceInfoForClient;
import info.semanticsoftware.semassist.server.ServiceInfoForClientArray;

/**
 * This class represent a thread that asynchronously from the main thread, fetches all available service names in an array list.
 * @author Bahar Sateli
 * */

public class ServiceInformationThread extends Thread {
	
	/** An array list to store the service names  */
	public ArrayList<String> services = new ArrayList<String>();
	
	public void run(){
		fetchAvailableServices();
	}
	
	/** This method brings back the service names using the singleton objects of ServiceAgentSingleton class.
	 * 
	 *  @see ServiceAgentSingletion.java
	 *  
	*/
	private void fetchAvailableServices(){
		SemanticServiceBroker agent = ServiceAgentSingleton.getInstance();
		try{
	        ServiceInfoForClientArray sia = agent.getAvailableServices();

	        java.util.List<ServiceInfoForClient> results = sia.getItem();
	        Iterator<ServiceInfoForClient> it = results.iterator();

	        while( it.hasNext() )
	        {
	            ServiceInfoForClient info = it.next();
	            services.add(info.getServiceName());
	        }
		}catch(Exception e){
			FileSelectionDialog.CONNECTION_IS_FINE = false;
			System.err.println("Can not read list of available services. Server is not found.");
		}   	
   	
   }

}
