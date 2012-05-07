package info.semanticsoftware.semassist.server.rest;

import org.restlet.Component;
import org.restlet.data.Protocol;

/**
 * Starts the Restlet as a standalone application.
 * @author Bahar Sateli
 * */
public class Bootstrap {

	/** Main method.
	 * @param args runtime arguments. args[0] is a port number. */
	public static void main(String[] args) {
		try {
			// Create a new Component.
			Component component = new Component();
			
			if(args.length > 0){
                                 // Add a new HTTP server listening on the user provided port.
                                component.getServers().add(Protocol.HTTP, Integer.parseInt(args[0]));
                        }else{
                                // Add a new HTTP server listening on the default port.
                                component.getServers().add(Protocol.HTTP, 8182);
                        }
                        
			// Attach the SA application.
                        component.getDefaultHost().attach(new SemAssist());

			// Start the component.
			component.start();
		}catch (NumberFormatException e){
			e.printStackTrace();
			System.err.println("Port number format exception: " + args[0]); 
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
