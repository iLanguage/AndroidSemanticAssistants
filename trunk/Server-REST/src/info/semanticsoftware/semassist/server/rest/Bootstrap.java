package info.semanticsoftware.semassist.server.rest;

import org.restlet.Component;
import org.restlet.data.Protocol;

/**
 * Starts the Restlet as a standalone application.
 * @author Bahar Sateli
 * */
public class Bootstrap {

	/** Main method.
	 * @param args runtime arguments */
	public static void main(String[] args) {
		try {
			// Create a new Component.
			Component component = new Component();

			// Add a new HTTP server listening on port 8182.
			component.getServers().add(Protocol.HTTP, 8182);

			// Attach the SA application.
			component.getDefaultHost().attach(new SemAssist());

			// Start the component.
			component.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}