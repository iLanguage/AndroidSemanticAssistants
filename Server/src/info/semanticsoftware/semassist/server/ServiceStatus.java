package info.semanticsoftware.semassist.server;

import gate.creole.SerialController;
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
	private Object gatePipeline; //the gate service 
	private String pipelineName; //the name of the service
	private String gateResourceName;//name of the gateResource (pipeline name)
	private PipelineThreadInfo pti;
	private boolean toBeTerminated;
	
	public boolean isToBeTerminated() {
		return toBeTerminated;
	}

	public void setToBeTerminated(boolean toBeTerminated) {
		this.toBeTerminated = toBeTerminated;
	}
	public static final int STATUS_INACTIVE = 0;  //constants to be used by the class as 
	public static final int STATUS_ACTIVE = 1;    //to not confuse active and inactive		
	
	public ServiceStatus(String _serviceName, Object _gateService, int _status, PipelineThreadInfo _pti, boolean terminate){
		setServiceName(_serviceName); //set the service name
		setGatePipeline(_gateService); //set the actual service object
		setStatus(_status); //set the initial state of the service when created
		setGateResourceName(((SerialController)_gateService).getName());
		setPipelineThreadInfo(_pti);
		setToBeTerminated(terminate);
	}
	
	protected String getGateResourceName() {
		return gateResourceName;
	}
	protected void setGateResourceName(String gateResourceName) {
		this.gateResourceName = gateResourceName;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getStatus() {
		return status;
	}
	public String getStatusDescription() {
		String statusDesc="Active";
		if(status==STATUS_INACTIVE){
			statusDesc="Inactive";
		}
		return statusDesc;
	}	

	public void setGatePipeline(Object gatePipeline) {
		this.gatePipeline = gatePipeline;
	}
	public Object getGatePipeline() {
		return gatePipeline;
	}
	public void setServiceName(String pipelineName) {
		this.pipelineName = pipelineName;
	}
	public String getServiceName() {
		return pipelineName;
	}
	public String toString(){
		return getGateResourceName() + getServiceName() + getStatusDescription();
	}

	public void setPipelineThreadInfo(PipelineThreadInfo pti) {
		this.pti = pti;
	}

	public PipelineThreadInfo getPipelineThreadInfo() {
		return pti;
	}
	
}
