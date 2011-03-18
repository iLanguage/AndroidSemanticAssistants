package info.semanticsoftware.semassist.server;

import info.semanticsoftware.semassist.server.util.ServiceInfo;
import java.util.concurrent.Callable;

public class CallableService implements Callable<ServiceExecutionStatus> {
	
	private ServiceInfo latestService;
	private ServiceExecutionStatus status;
	private SemanticServiceBroker SBB;
	
	public CallableService(ServiceInfo _latestService, ServiceExecutionStatus _status, SemanticServiceBroker _SBB){
		this.latestService = _latestService;
		this.status = _status;
		this.SBB = _SBB;
	}
	
	
    public ServiceExecutionStatus call() {
    	return SBB.runOneService(latestService, status, latestService.isConcatenation());
    }
}
