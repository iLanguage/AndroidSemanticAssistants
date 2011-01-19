package info.semanticsoftware.semassist.server;

import info.semanticsoftware.semassist.server.util.Logging;



/**
 * Class is used to extend the information of the GateServiceProcess
 * We store 
 * 		status (active, inactive)
 * 		the actual service object (to be reused at a later time)
 * 		the type of service (string referencing the file .gapp)
 *
 */
public class ServiceStatus {
	
	private int status; //ACTIVE(1) or INACTIVE(0)
	private Object gateService; //the gate service 
	private String serviceName; //the name of the service
	
	public static final int STATUS_INACTIVE = 0;  //constants to be used by the class as 
	public static final int STATUS_ACTIVE = 1;    //to not confuse active and inactive		
	
	public ServiceStatus(String _serviceName, Object _gateService, int _status){
		setServiceName(_serviceName); //set the service name
		setGateService(_gateService); //set the actual service object
		setStatus(_status); //set the initial state of the service when created
		Logging.log("Register Setting: " + getServiceName() + " - " + getStatus());
			//TODO: simple tracking message can be removed in the future
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	public int getStatus() {
		return status;
	}
	public void setGateService(Object gateService) {
		this.gateService = gateService;
	}
	public Object getGateService() {
		return gateService;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getServiceName() {
		return serviceName;
	}
	
	
}
