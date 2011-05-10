package info.semanticsoftware.semassist.server;

import info.semanticsoftware.semassist.server.util.ServiceInfo;
import java.util.concurrent.Callable;

/**
 * This class allows the SemanticServiceBroker method "runOneService" 
 * to be run as an independent thread.  Allowing for Thread Pooling
 * 
 * */
public class CallableRunOneServiceThread implements Callable<ServiceExecutionStatus>{
	//basically the method is transformed into a Class to implement the Callable class
	//this class allows the thread, once completed to return the value.
	
	//DB: Creating the class was the only way I though of making the method into a thread call 
	private ServiceInfo currentService; //declare the necessary variables needed for the runOneService method
	private ServiceExecutionStatus status;
	private SemanticServiceBroker SSB;
	
	/**
	 * Constructor that receives
	 * @param currentService [ServiceInfo] : Represents the information the service about to be run contains (ie. Name, Description, location on the file system)
	 * @param status  [ServiceExecutionStatus] : Needed for passing the information on to the next service, if any.
	 * @param _SSB [SemanticServiceBroker] : Actual class that defines the runOneService method
	 * */
	
	public CallableRunOneServiceThread(ServiceInfo currentService, ServiceExecutionStatus status, SemanticServiceBroker _SSB){
		//assign parameters to call instance variables
		this.currentService = currentService; 
		this.status = status;
		this.SSB=_SSB;
	}
	
	/**
	 * @return [ServiceExecutionStatus] : Once the function has completed execution it returns the information needed to continue on to the next service call
	 */
	public ServiceExecutionStatus call(){
		return SSB.runOneService(currentService, status);
	}

}
