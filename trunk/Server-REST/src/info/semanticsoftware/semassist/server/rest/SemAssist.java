package info.semanticsoftware.semassist.server.rest;

import info.semanticsoftware.semassist.server.rest.resource.ServiceResource;
import info.semanticsoftware.semassist.server.rest.resource.ServicesResource;
import info.semanticsoftware.semassist.server.rest.resource.UserResource;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;
import org.restlet.Context;

public class SemAssist extends Application {
    
	public SemAssist(Context parentContext) {
        super(parentContext);
    }
    
    @Override
    public synchronized Restlet createInboundRoot() {
        Router router = new Router(getContext());
        // Define routers for NLP services
        router.attach("/services", ServicesResource.class);
        router.attach("/services/{serviceName}", ServiceResource.class);
        // Define routers for user actions
        router.attach("/user", UserResource.class);
        router.attach("/users/{userName}", UserResource.class);
		return router;
    }
    
}